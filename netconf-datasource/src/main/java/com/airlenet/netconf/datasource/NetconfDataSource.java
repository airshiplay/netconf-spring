package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.exception.*;
import com.airlenet.netconf.datasource.stat.NetconfDataSourceStatManager;
import com.airlenet.netconf.datasource.util.Utils;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfDataSource extends NetconfAbstractDataSource implements MBeanRegistration, NetconfDataSourceMBean {
    private static final Logger logger = LoggerFactory.getLogger(NetconfDataSource.class);
    public final static int DEFAULT_MAX_POOL_SIZE = 8;
    public final static int DEFAULT_CONNECTION_TIMEOUT = 0;
    private String initStackTrace;
    private String lockStackTrace;
    private long connectCount = 0L;
    private long subscriberConnectCount = 0L;
    private long discardConnectCount = 0L;
    private long lockConnectTimeoutCount = 0L;
    private Map<String, Long> statSubscriberMap = new LinkedHashMap<>();
    private long statReconnectionCount = 0l;
    private long closeTimeMillis = -1L;
    private long connectTimeMillis = -1L;
    protected long outputDataInteractionTimeMillis = -1L;
    protected long inputDataInteractionTimeMillis = -1L;
    protected volatile long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile long readTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile long kexTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    protected volatile boolean autoReconnection = true;
    protected volatile boolean inited;

    private volatile boolean closing = false;
    private volatile boolean closed = false;
    public ReentrantLock lock = new ReentrantLock();
    private Device device;
    private DeviceUser deviceUser;

    protected BlockingQueue<NetconfConnectionHolder> connectionQueue;
    protected ArrayList<NetconfConnectionHolder> holders = new ArrayList<>();
    private boolean mbeanRegistered = false;

    private ObjectName objectName;
    private final static AtomicInteger dataSourceIdSeed = new AtomicInteger(0);

    public NetconfDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public boolean isInited() {
        return this.inited;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public NetconfPooledConnection getConnection() throws NetworkException {
        return this.getConnection(this.connectionTimeout, null);
    }

    public NetconfPooledConnection getConnection(NetconfSubscriber subscriber) throws NetworkException {
        return this.getConnection(this.connectionTimeout, subscriber);
    }

    @Override
    public NetconfPooledConnection getConnection(String username, String password) throws NetworkException {
        return getConnection();
    }

    @Override
    public void close() {

        lock.lock();
        try {
            if (this.closed) {
                return;
            }

            if (!this.inited) {
                return;
            }

            this.closing = true;
            connectionQueue.clear();
            connectCount = 0;
            discardConnectCount = 0L;
            subscriberConnectCount = 0L;
            statReconnectionCount = 0;
            lockConnectTimeoutCount = 0L;
            outputDataInteractionTimeMillis = -1;
            inputDataInteractionTimeMillis = -1;
            connectTimeMillis = -1;
            holders.clear();
            device.close();
            this.closeTimeMillis = System.currentTimeMillis();
            unregisterMbean();
            this.closed = true;
        } finally {
            lock.unlock();
        }


    }

    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void restart() {
        lock.lock();
        try {
            this.close();
            this.inited = false;
            this.closed = false;
            logger.debug("Restart DataSource {}", this.url);
        } finally {
            lock.unlock();
        }
    }

    private NetconfPooledConnection getConnection(long connectionTimeout, NetconfSubscriber subscriber) throws NetworkException {
        this.init();
        return this.getConnectionDirect(connectionTimeout, subscriber);
    }

    public void init() throws NetworkException {
        if (inited) {
            return;
        }

        final ReentrantLock lock = this.lock;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new NetworkException("interrupt", e);
        }

        try {
            if (inited) {
                return;
            }
            initStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            if (maxPoolSize <= 0) {
                throw new IllegalArgumentException("illegal maxPoolSize " + maxPoolSize);
            }

            this.id = dataSourceIdSeed.incrementAndGet();
            connectionQueue = new ArrayBlockingQueue<>(maxPoolSize);
            device = new Device("", url.substring(10, url.lastIndexOf(":")), Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.length())));
            device.setDefaultReadTimeout((int) readTimeout);
            deviceUser = new DeviceUser(username, username, password);
            device.addUser(deviceUser);

            initedTime = new Date();
            registerMbean();
        } catch (RuntimeException e) {
            throw e;
        } catch (Error e) {
            throw e;

        } catch (Exception e) {
            throw e;
        } finally {
            inited = true;
            lock.unlock();
        }
    }

    public NetconfPooledConnection getConnectionDirect(long connectionTimeoutMillis, NetconfSubscriber subscriber) throws NetworkException {
        NetconfPooledConnection pooledConnection = getConnectionInternal(connectionTimeoutMillis, subscriber);
        return pooledConnection;
    }


    private NetconfPooledConnection getConnectionInternal(long connectionTimeout, NetconfSubscriber subscriber) throws NetworkException {
        if (closed) {
            throw new NetconfDataSourceClosedException("dataSource already closed at " + new Date(closeTimeMillis));
        }

        NetconfConnectionHolder holder = null;
        logger.debug("Fetching Netconf Connection from DataSource {}", this.url);
        try {
            if (lock.isLocked() && logger.isDebugEnabled()) {
                //lock.lockInterruptibly();
                logger.debug("Lock Netconf Connection from DataSource {} by {}", this.url, lock.toString());
            }
            boolean tryLock = lock.tryLock(connectionTimeout, TimeUnit.MILLISECONDS);
            if (tryLock) {
                try {
                    lockStackTrace = Utils.toString(Thread.currentThread().getStackTrace());

                    if (connectionQueue.isEmpty() && connectCount < maxPoolSize) {

                        for (int i = 0; ; i++) {
                            //创建连接
                            if (!device.isConnect()) {
                                logger.debug("Connect Netconf Device {},connectionTimeout={},kexTimeout={}", this.url, connectionTimeout, kexTimeout);
                                device.connect(username, null, Math.toIntExact(connectionTimeout), Math.toIntExact(kexTimeout));
                                connectTimeMillis = System.currentTimeMillis();
                            }
                            long connectionId = connectCount + 1;
                            String sessionName = "datasource-" + connectionId + ":" + Math.random();
                            NetconfConnection netconfConnection = null;

                            JNCSubscriber jncSubscriber = new JNCSubscriber(this, url, sessionName, subscriber);
                            try {
                                device.newSession(jncSubscriber, sessionName);
                            } catch (Exception e) {//断链，重新连接 TODO 需将所有连接重置，并重新建联
                                connectCount = 0;
                                device.close();
                                connectionQueue.clear();
                                holders.clear();
                                logger.error("Fetching Netconf Connection newSession from DataSource " + this.url, e);
                                if (!autoReconnection && i > 0) {
                                    throw e;
                                }
                                this.statReconnectionCount++;

                                logger.debug("Again Connect Netconf Device {},connectionTimeout={},kexTimeout={}", this.url, connectionTimeout, kexTimeout);
                                continue;
                            }
                            SSHSession sshSession = device.getSSHSession(sessionName);
                            NetconfSession netconfSession = device.getSession(sessionName);
                            netconfConnection = new NetconfConnection(sessionName, sshSession, netconfSession, jncSubscriber);

                            holder = new NetconfConnectionHolder(this, netconfConnection, connectionId);
                            holders.add(holder);
                            connectCount++;
                            break;
                        }

                    }
                } catch (YangException e) {
                    throw new NetconfException(e);
                } catch (SocketTimeoutException e) {
                    throw new NetconfSocketTimeoutException(e);
                } catch (ConnectException e) {
                    throw new NetconfConnectException(e);
                } catch (IOException e) {
                    throw new NetconfIOException(e);
                } catch (JNCException e) {
                    throw new NetconfJNCException(e);
                } finally {
                    lockStackTrace = "";
                    lock.unlock();
                }
                //获取连接
                try {
                    if (holder == null) {
                        if (connectionTimeout > 0) {
                            holder = connectionQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
                        } else {
                            holder = connectionQueue.take();
                        }
                    }
                    if (holder == null) {
                        throw new IllegalStateException("NetconfDataSource returned null from getConnection(): " + this);
                    }
                } catch (InterruptedException e) {
                    throw new NetworkException(e);
                }
                holder.incrementUseCount();
                logger.debug("Fetched Netconf Connection {} from DataSource {}", holder.conn.getSessionName(), this.url);
                return new NetconfPooledConnection(holder);
            } else {
                lockConnectTimeoutCount++;
                throw new GetNetconfConnectionTimeoutException("Fetching Netconf Connection from DataSource " + this.url + ", Timeout:" + connectionTimeout + ", Lock " + this.lock.toString());
            }

        } catch (InterruptedException e) {
            throw new NetconfException(e);
        }


    }

    /**
     * 废弃链接
     *
     * @param realConnection
     */
    public void discardConnection(NetconfPooledConnection realConnection) {
        lock.lock();
        connectCount--;
        discardConnectCount++;
        if (realConnection.getStream() != null) {
            subscriberConnectCount--;
        }
        holders.remove(realConnection.holder);
        logger.debug("Discard Netconf Connection {} to DataSource {}", realConnection.getSessionName(), this.url);
        device.closeSession(realConnection.sessionName);
        lock.unlock();
    }

    /**
     * 回收链接
     *
     * @param pooledConnection
     * @throws NetconfException
     */
    protected void recycle(NetconfPooledConnection pooledConnection) throws NetconfException {
        lock.lock();
        try {
            logger.debug("Recycling Netconf Connection {} to DataSource {}", pooledConnection.getSessionName(), this.url);
            if (holders.indexOf(pooledConnection.holder) != -1) {
                connectionQueue.put(pooledConnection.holder);
                logger.debug("Recycled Netconf Connection {} to DataSource {}", pooledConnection.getSessionName(), this.url);
            } else {
                logger.debug("Discarded Netconf Connection {} to DataSource {}", pooledConnection.getSessionName(), this.url);
            }
        } catch (InterruptedException e) {
            connectCount--;
            discardConnectCount++;
            throw new NetconfException(e);
        } finally {
            lock.unlock();
        }
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getKexTimeout() {
        return kexTimeout;
    }

    public void setKexTimeout(long kexTimeout) {
        this.kexTimeout = kexTimeout;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public boolean isAutoReconnection() {
        return autoReconnection;
    }

    public void setAutoReconnection(boolean autoReconnection) {
        this.autoReconnection = autoReconnection;
    }

    public void registerMbean() {
        if (!mbeanRegistered) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    ObjectName objectName = NetconfDataSourceStatManager.addDataSource(NetconfDataSource.this,
                            NetconfDataSource.this.name);

                    NetconfDataSource.this.setObjectName(objectName);
                    NetconfDataSource.this.mbeanRegistered = true;

                    return null;
                }
            });
        }
    }

    public void unregisterMbean() {
        if (mbeanRegistered) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {

                @Override
                public Object run() {
                    NetconfDataSourceStatManager.removeDataSource(NetconfDataSource.this);
                    NetconfDataSource.this.mbeanRegistered = false;
                    return null;
                }
            });
        }
    }

    public boolean isMbeanRegistered() {
        return mbeanRegistered;
    }


    public Map<String, Object> getStatData() {
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();
        dataMap.put("Identity", System.identityHashCode(this));
        dataMap.put("Name", this.getName());
        dataMap.put("URL", this.getUrl());
        dataMap.put("UserName", this.getUsername());
        if (this.getTimeZone() != null) {
            dataMap.put("TimeZone", this.getTimeZone());
        }
        dataMap.put("InitedTime", initedTime);
        dataMap.put("MaxPoolSize", this.getMaxPoolSize());
        dataMap.put("Status", this.closed ? "Closed" : (device != null && device.isConnect() ? "Connect" : "DisConnect"));
        dataMap.put("ConnectTime", new Date(this.connectTimeMillis));
        dataMap.put("InputDataInteractionTime", new Date(this.inputDataInteractionTimeMillis));
        dataMap.put("OutputDataInteractionTime", new Date(this.outputDataInteractionTimeMillis));

        dataMap.put("ConnectCount", this.connectCount);
        dataMap.put("SubscriberConnectCount", this.subscriberConnectCount);
        dataMap.put("IdleConnectionCount", connectionQueue.size());
        dataMap.put("DiscardConnectionCount", this.discardConnectCount);
        dataMap.put("Subscriber", statSubscriberMap);

        dataMap.put("LockConnectTimeoutCount", lockConnectTimeoutCount);

        dataMap.put("AutoReconnection", autoReconnection);
        dataMap.put("ReconnectionCount", this.statReconnectionCount);
        return dataMap;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        if (server != null) {
            try {
                if (server.isRegistered(name)) {
                    server.unregisterMBean(name);
                }
            } catch (Exception ex) {
                logger.warn("DruidDataSource preRegister error", ex);
            }
        }
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {

    }

    @Override
    public void preDeregister() throws Exception {

    }

    @Override
    public void postDeregister() {

    }

    protected void updateSubscriberCount(String stream) {
        this.subscriberConnectCount++;
    }

    protected void updateNotificationCount(String stream, long receiveSubscriberCount) {
        statSubscriberMap.put(stream, receiveSubscriberCount);
    }

    public List<String> getActiveConnectionStackTrace() {
        List<String> list = new ArrayList<String>();

//        for (NetconfPooledConnection conn : this.getActiveConnections()) {
//            list.add(Utils.toString(conn.getConnectStackTrace()));
//        }

        return list;
    }

    public List<Map<String, Object>> getPoolingConnectionInfo() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        lock.lock();
        try {
            for (int i = 0; i < holders.size(); ++i) {
                NetconfConnectionHolder connHolder = holders.get(i);
                NetconfConnection conn = connHolder.conn;
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", System.identityHashCode(conn));
                map.put("connectionId", connHolder.getConnectionId());
                map.put("useCount", connHolder.getUseCount());
                list.add(map);
            }
        } finally {
            lock.unlock();
        }
        return list;
    }
}
