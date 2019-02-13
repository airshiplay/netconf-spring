package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkDataSource;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.Device;
import com.tailf.jnc.DeviceUser;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.NetconfSession;
import sun.nio.ch.Net;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfDataSource implements NetworkDataSource {
    protected final static Object PRESENT = new Object();
    public final static int DEFAULT_MAX_ACTIVE_SIZE = 8;
    public final static int DEFAULT_MAX_IDLE = 8;
    public final static int DEFAULT_MIN_IDLE = 0;
    public final static int DEFAULT_MAX_WAIT = -1;
    private String initStackTrace;
    protected ReentrantLock activeConnectionLock = new ReentrantLock();

    private int poolingCount = 0;
    private int activeCount = 0;
    private long discardCount = 0;
    private int keepAliveCheckCount = 0;
    private int activePeak = 0;
    private long activePeakTime = 0;
    private int poolingPeak = 0;
    private long poolingPeakTime = 0;
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22
    protected volatile long maxWait = -1L;
    protected volatile int maxActive = DEFAULT_MAX_ACTIVE_SIZE;
    protected volatile int minIdle = DEFAULT_MIN_IDLE;
    protected volatile boolean inited;
    public ReentrantLock lock = new ReentrantLock();
    private Device device;
    private DeviceUser deviceUser;
    protected NetconfSession[] connections;
    protected final Map<NetconfPooledConnection, Object> activeConnections = new IdentityHashMap<NetconfPooledConnection, Object>();
    protected ScheduledExecutorService destroyScheduler;
    protected ScheduledExecutorService createScheduler;

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

        boolean init = false;
        try {
            if (inited) {
                return;
            }
            initStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            device = new Device("", url.substring(10, url.lastIndexOf(":")), Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.length())));
            deviceUser = new DeviceUser(username, username, password);
            device.addUser(deviceUser);
            init = true;

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
            activeConnections.put(pooledConnection, PRESENT);
        } finally {
            activeConnectionLock.unlock();
        }
        return pooledConnection;
    }


    private NetconfPooledConnection getConnectionInternal(long maxWait, String stream, NetconfSubscriber subscriber) throws NetworkException {
        NetconfConnectionHolder holder;

        lock.lock();
        try {
            if (activeCount < maxActive) {
                activeCount++;
                if (activeCount > activePeak) {
                    activePeak = activeCount;
                    activePeakTime = System.currentTimeMillis();
                }
//                break;
            } else {
//                discard = true;
            }
        } finally {
            lock.unlock();
        }

        try {
            if (!device.isConnect()) {
                device.connect(username);
            }
            long connectionId = 1;
            String sessionName = connectionId + "";
            NetconfConnection netconfConnection = null;
            if (stream == null || subscriber == null) {
                device.newSession(sessionName);
                netconfConnection = new NetconfConnection(device.getSession(sessionName));
            } else {
                JNCSubscriber jncSubscriber = new JNCSubscriber(url, stream, subscriber);
                device.newSession(jncSubscriber, sessionName);

                netconfConnection = new NetconfSubscriberConnection(device.getSession(sessionName), jncSubscriber);
                netconfConnection.subscription(stream);
            }
            holder = new NetconfConnectionHolder(this, netconfConnection, connectionId);
            return new NetconfPooledConnection(holder);
        } catch (IOException e) {
            throw new NetworkException(e);
        } catch (JNCException e) {
            throw new NetworkException(e);
        }
    }

    public Set<NetconfPooledConnection> getActiveConnections() {
        activeConnectionLock.lock();
        try {
            return new HashSet<NetconfPooledConnection>(this.activeConnections.keySet());
        } finally {
            activeConnectionLock.unlock();
        }
    }

    public class CreateConnectionTask implements Runnable {

        @Override
        public void run() {

        }
    }
}
