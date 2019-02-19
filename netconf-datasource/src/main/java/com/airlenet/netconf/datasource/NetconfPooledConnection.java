package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkPooledConnection;
import com.tailf.jnc.NetconfSession;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfPooledConnection extends NetconfConnection implements NetworkPooledConnection, NetworkConnection {
    private volatile boolean running = false;
    private volatile boolean abandoned = false;
    public ReentrantLock lock = new ReentrantLock();
    protected final Thread ownerThread;
    private long connectedTimeMillis;
    protected NetconfConnection conn;
    protected volatile NetconfConnectionHolder holder;
    private volatile boolean disable = false;
    protected volatile boolean closed = false;

    public NetconfPooledConnection(NetconfConnectionHolder holder) {
        super(holder.conn.sessionName,holder.conn.sshSession,holder.conn.netconfSession, holder.conn.jncSubscriber);
        this.conn = holder.conn;
        this.holder = holder;
        ownerThread = Thread.currentThread();
        connectedTimeMillis = System.currentTimeMillis();
    }

    public Thread getOwnerThread() {
        return ownerThread;
    }

    public long getConnectedTimeMillis() {
        return connectedTimeMillis;
    }

    @Override
    public NetconfConnection getConnection() throws NetconfException {
        return conn;
    }

    @Override
    public void close() throws NetconfException {
        if (this.disable) {
            return;
        }

        NetconfConnectionHolder holder = this.holder;
        if (holder == null) {
            return;
        }
        recycle();
    }

    @Override
    public boolean isClosed() throws NetconfException {
        if (holder == null) {
            return true;
        }

        return closed || disable;
    }

    public void recycle() throws NetconfException {
        if (this.disable) {
            return;
        }
        if (abandoned) {//废弃此链接
            holder.dataSource.discardConnection(this);
        } else {//回收链接
            holder.dataSource.recycle(this);
        }

        this.holder = null;
        closed = true;
    }

    @Override
    public void rollback() throws NetconfException {

    }

    @Override
    public void commit() throws NetconfException {

    }

    boolean isRunning() {
        return running;
    }

    public void abandond() {
        this.abandoned = true;
    }

    final void beforeExecute() {
        running = true;
    }

    final void afterExecute() {
        running = false;
    }

    public String toString() {
        if (conn != null) {
            return conn.toString();
        } else {
            return "closed-conn-" + System.identityHashCode(this);
        }
    }
}
