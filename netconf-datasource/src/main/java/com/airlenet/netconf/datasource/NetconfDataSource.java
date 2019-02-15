package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkDataSource;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.Device;
import com.tailf.jnc.DeviceUser;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.YangException;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfDataSource implements NetworkDataSource {
    protected final static Object PRESENT = new Object();
    public final static int DEFAULT_MAX_ACTIVE_SIZE = 8;
    public final static int DEFAULT_MAX_WAIT = -1;
    private String initStackTrace;
    protected ReentrantLock activeConnectionLock = new ReentrantLock();
    private long connectCount = 0L;
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22
    protected volatile long maxWait = DEFAULT_MAX_WAIT;
    protected volatile int maxActive = DEFAULT_MAX_ACTIVE_SIZE;
    protected volatile boolean inited;
    public ReentrantLock lock = new ReentrantLock();
    private Device device;
    private DeviceUser deviceUser;
    protected BlockingQueue<NetconfConnectionHolder> connectionQueue;

    public NetconfDataSource(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public boolean isInited() {
        return this.inited;
    }

    @Override
    public NetconfPooledConnection getConnection() throws NetworkException {
        return this.getConnection(this.maxWait, null, null);
    }

    public NetconfPooledConnection getSubscriberConnection(String stream, NetconfSubscriber subscriber) throws NetworkException {
        return this.getConnection(this.maxWait, stream, subscriber);
    }

    @Override
    public NetconfPooledConnection getConnection(String username, String password) throws NetworkException {
        return getConnection();
    }

    private NetconfPooledConnection getConnection(long maxWait, String stream, NetconfSubscriber subscriber) throws NetworkException {
        this.init();
        return this.getConnectionDirect(maxWait, stream, subscriber);
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
            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            connectionQueue = new ArrayBlockingQueue<>(maxActive);
            device = new Device("", url.substring(10, url.lastIndexOf(":")), Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.length())));
            deviceUser = new DeviceUser(username, username, password);
            device.addUser(deviceUser);

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

    public NetconfPooledConnection getConnectionDirect(long maxWaitMillis, String stream, NetconfSubscriber subscriber) throws NetworkException {
        NetconfPooledConnection pooledConnection = getConnectionInternal(maxWaitMillis, stream, subscriber);
        activeConnectionLock.lock();
        try {
        } finally {
            activeConnectionLock.unlock();
        }
        return pooledConnection;
    }


    private NetconfPooledConnection getConnectionInternal(long maxWait, String stream, NetconfSubscriber subscriber) throws NetworkException {
        NetconfConnectionHolder holder = null;

        lock.lock();
        try {
            if (connectionQueue.isEmpty() && connectCount < maxActive) {
                //创建连接
                if (!device.isConnect()) {
                    device.connect(username);
                }
                long connectionId = connectCount + 1;
                String sessionName = connectionId + "";
                NetconfConnection netconfConnection = null;

                JNCSubscriber jncSubscriber = new JNCSubscriber(url, stream, subscriber);
                try {
                    device.newSession(jncSubscriber, sessionName);
                } catch (Exception e) {//断链，重新连接 TODO 需将所有连接重置，并重新建联
                    device.connect(username);
                    device.newSession(jncSubscriber, sessionName);
                }
                netconfConnection = new NetconfSubscriberConnection(device.getSession(sessionName), jncSubscriber);
                if (stream != null && !stream.equals("")) {//不为空时，订阅消息
                    netconfConnection.subscription(stream);
                }
                holder = new NetconfConnectionHolder(this, netconfConnection, connectionId);
                connectCount++;
            }
        } catch (YangException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } finally {
            lock.unlock();
        }

        //获取连接
        try {
            if (holder == null) {
                if (maxWait > 0) {
                    holder = connectionQueue.poll(maxWait, TimeUnit.MILLISECONDS);
                } else {
                    holder = connectionQueue.take();
                }
            }
        } catch (InterruptedException e) {
            throw new NetworkException(e);
        }

        return new NetconfPooledConnection(holder);
    }

    /**
     * 废弃链接
     *
     * @param realConnection
     */
    public void discardConnection(NetconfPooledConnection realConnection) {
        lock.lock();
        connectCount--;
        lock.unlock();
    }

    /**
     * 回收链接
     *
     * @param pooledConnection
     * @throws NetconfException
     */
    protected void recycle(NetconfPooledConnection pooledConnection) throws NetconfException {
        try {
            connectionQueue.put(pooledConnection.holder);
        } catch (InterruptedException e) {
            throw new NetconfException(e);
        }
    }

}
