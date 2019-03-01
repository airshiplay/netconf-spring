package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.util.Utils;
import com.airlenet.network.NetworkDataSource;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class NetconfAbstractDataSource implements NetworkDataSource {
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22

    protected final Date createdTime = new Date();
    protected Date initedTime;
    protected long id;
    protected TimeZone timeZone;
    protected String name;
    protected ReentrantLock activeConnectionLock                      = new ReentrantLock();
    protected final Map<NetconfPooledConnection, Object> activeConnections = new IdentityHashMap<NetconfPooledConnection, Object>();
    protected final static Object                      PRESENT                                   = new Object();

    public long getID() {
        return this.id;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "DataSource-" + System.identityHashCode(this);
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Set<NetconfPooledConnection> getActiveConnections() {
        activeConnectionLock.lock();
        try {
            return new HashSet<NetconfPooledConnection>(this.activeConnections.keySet());
        } finally {
            activeConnectionLock.unlock();
        }
    }

    public List<String> getActiveConnectionStackTrace() {
        List<String> list = new ArrayList<String>();

        for (NetconfPooledConnection conn : this.getActiveConnections()) {
            list.add(Utils.toString(conn.getConnectStackTrace()));
        }

        return list;
    }
}
