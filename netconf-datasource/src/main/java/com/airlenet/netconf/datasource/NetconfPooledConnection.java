package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.util.Utils;
import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkPooledConnection;
import com.tailf.jnc.Element;
import com.tailf.jnc.NetconfSession;
import com.tailf.jnc.NodeSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfPooledConnection extends NetconfConnection implements NetworkPooledConnection, NetworkConnection {

    public ReentrantLock lock = new ReentrantLock();
    protected final Thread ownerThread;
    private long connectedTimeMillis;
    protected NetconfConnection conn;
    protected volatile NetconfConnectionHolder holder;
    private volatile boolean disable = false;
    protected volatile boolean closed = false;
    protected long receiveSubscriberCount;
    protected String stream;
    protected String runStackTrace;

    public NetconfPooledConnection(NetconfConnectionHolder holder) {
        super(holder.conn.sessionName, holder.conn.sshSession, holder.conn.netconfSession, holder.conn.jncSubscriber);
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
        holder.recycle();

        this.holder = null;
        closed = true;
    }


    public void updateTimeZone(TimeZone timeZone) {
        if (holder != null) {
            holder.dataSource.setTimeZone(timeZone);
        }
    }

    public TimeZone getTimeZone() {
        if (holder != null) {
            return holder.dataSource.getTimeZone();
        }
        return null;
    }

    @Override
    public void rollback() throws NetconfException {

    }

    @Override
    public void commit() throws NetconfException {

    }

    public String toString() {
        if (conn != null) {
            return conn.toString();
        } else {
            return "closed-conn-" + System.identityHashCode(this);
        }
    }

    @Override
    public NodeSet get(String xpath) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        return conn.get(xpath);
    }

    @Override
    public NodeSet get(Element subtreeFilter) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        return conn.get(subtreeFilter);
    }

    @Override
    public NodeSet getConfig(String xpath) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        return conn.getConfig(xpath);
    }

    @Override
    public NodeSet getConfig(Element subtreeFilter) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        return conn.getConfig(subtreeFilter);
    }

    @Override
    public void editConfig(Element configTree) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        conn.editConfig(configTree);
    }

    @Override
    public void subscription(String stream) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        this.stream = stream;
        conn.subscription(stream);
        holder.setStream(stream);
        this.receiveSubscriberCount = 0;
        holder.dataSource.updateSubscriberCount(conn.stream);
        holder.dataSource.updateNotificationCount(conn.stream, this.receiveSubscriberCount);
    }

    @Override
    public void subscription(String stream, String eventFilter, String startTime) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        this.stream = stream;
        conn.subscription(stream, eventFilter, startTime);
        this.receiveSubscriberCount = 0;
        holder.setStream(stream);
        holder.dataSource.updateSubscriberCount(conn.stream);
        holder.dataSource.updateNotificationCount(conn.stream, this.receiveSubscriberCount);
    }

    @Override
    public void subscription(String stream, String eventFilter, String startTime, String stopTime) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        this.stream = stream;
        conn.subscription(stream, eventFilter, startTime, stopTime);
        holder.setStream(stream);
        this.receiveSubscriberCount = 0;
        holder.dataSource.updateSubscriberCount(conn.stream);
        holder.dataSource.updateNotificationCount(conn.stream, this.receiveSubscriberCount);
    }

    @Override
    public Element receiveNotification() throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        Element receiveNotification = conn.receiveNotification();
        if (holder != null) {
            this.receiveSubscriberCount++;
            holder.dataSource.updateNotificationCount(conn.stream, this.receiveSubscriberCount);
        }
        return receiveNotification;
    }

    public String getRunStackTrace() {
        return runStackTrace;
    }

    public String getStream() {
        return holder.getStream();
    }
}
