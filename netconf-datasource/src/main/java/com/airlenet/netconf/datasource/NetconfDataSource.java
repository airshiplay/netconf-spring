package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkDataSource;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;
import com.airlenet.netconf.datasource.util.Utils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfDataSource implements NetworkDataSource {
    public final static int DEFAULT_MAX_POOL_SIZE = 8;
    public final static int DEFAULT_CONNECTION_TIMEOUT = 0;
    private String initStackTrace;
    protected ReentrantLock activeConnectionLock = new ReentrantLock();
    private long connectCount = 0L;
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22
    protected volatile long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile long readTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile long kexTimeout = DEFAULT_CONNECTION_TIMEOUT;
    protected volatile int maxPoolSize = DEFAULT_MAX_POOL_SIZE;
    protected volatile boolean inited;

    private volatile boolean closing = false;
    private volatile boolean closed = false;
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
            device.close();

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

            connectionQueue = new ArrayBlockingQueue<>(maxPoolSize);
            device = new Device("", url.substring(10, url.lastIndexOf(":")), Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.length())));
            device.setDefaultReadTimeout((int) readTimeout);
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

    public NetconfPooledConnection getConnectionDirect(long connectionTimeoutMillis, NetconfSubscriber subscriber) throws NetworkException {
        NetconfPooledConnection pooledConnection = getConnectionInternal(connectionTimeoutMillis, subscriber);
        activeConnectionLock.lock();
        try {
        } finally {
            activeConnectionLock.unlock();
        }
        return pooledConnection;
    }


    private NetconfPooledConnection getConnectionInternal(long connectionTimeout, NetconfSubscriber subscriber) throws NetworkException {
        NetconfConnectionHolder holder = null;

        lock.lock();
        try {
            if (connectionQueue.isEmpty() && connectCount < maxPoolSize) {
                //创建连接
                if (!device.isConnect()) {
                    device.connect(username, null, Math.toIntExact(connectionTimeout), Math.toIntExact(kexTimeout));
                }
                long connectionId = connectCount + 1;
                String sessionName = "datasource-" + connectionId + ":" + Math.random();
                NetconfConnection netconfConnection = null;

                JNCSubscriber jncSubscriber = new JNCSubscriber(url, sessionName, subscriber);
                try {
                    device.newSession(jncSubscriber, sessionName);
                } catch (Exception e) {//断链，重新连接 TODO 需将所有连接重置，并重新建联

                    device.connect(username, (int) connectionTimeout);
                    device.newSession(jncSubscriber, sessionName);
                }
                SSHSession sshSession = device.getSSHSession(sessionName);
                NetconfSession netconfSession = device.getSession(sessionName);
                netconfConnection = new NetconfConnection(sessionName, sshSession, netconfSession, jncSubscriber);

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
                if (connectionTimeout > 0) {
                    holder = connectionQueue.poll(connectionTimeout, TimeUnit.MILLISECONDS);
                } else {
                    holder = connectionQueue.take();
                }
            }
            if (holder == null) {
                throw new NetconfTimeoutException(this.url + " getConnection Timeout:" + connectionTimeout);
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
        try {
            connectionQueue.put(pooledConnection.holder);
        } catch (InterruptedException e) {
            throw new NetconfException(e);
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
}
