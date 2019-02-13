package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;
import com.airlenet.network.NetworkPooledConnection;

import java.sql.Connection;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfPooledConnection implements NetworkPooledConnection, NetworkConnection {
    private volatile boolean running = false;
    private volatile boolean abandoned = false;
    public ReentrantLock lock = new ReentrantLock();
    protected NetconfConnection conn;
    protected volatile NetconfConnectionHolder holder;
    private volatile boolean disable = false;
    protected volatile boolean closed = false;

    public NetconfPooledConnection(NetconfConnectionHolder holder) {
        this.conn = holder.conn;
        this.holder = holder;
    }

    @Override
    public NetworkConnection getConnection() throws NetworkException {
        return conn;
    }

    @Override
    public void close() throws NetworkException {
        if (this.disable) {
            return;
        }

        NetconfConnectionHolder holder = this.holder;
        if (holder == null) {
            return;
        }
    }

    @Override
    public boolean isClosed() throws NetworkException {
        return false;
    }

    @Override
    public void rollback() throws NetworkException {

    }

    @Override
    public void commit() throws NetworkException {

    }

    @Override
    public Object executeQuery(String req) throws NetworkException {
        return this.conn.executeQuery(req);
    }

    @Override
    public Object executeConfig(String req) throws NetworkException {
        return this.conn.executeConfig(req);
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
