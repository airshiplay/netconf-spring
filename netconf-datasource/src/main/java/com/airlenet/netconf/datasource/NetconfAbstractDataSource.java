package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.util.StringUtils;
import com.airlenet.netconf.datasource.util.Utils;
import com.airlenet.network.NetworkDataSource;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public abstract class NetconfAbstractDataSource implements NetworkDataSource {
    private static final Logger LOG = Logger.getLogger("NetconfAbstractDataSource");
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22

    protected final Date createdTime = new Date();
    protected Date initedTime;
    protected long id;
    protected volatile boolean inited;
    protected TimeZone timeZone;
    protected String name;
    protected ReentrantLock activeConnectionLock = new ReentrantLock();
    protected final Map<NetconfPooledConnection, Object> activeConnections = new IdentityHashMap<NetconfPooledConnection, Object>();
    protected final static Object PRESENT = new Object();

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

    public void setUsername(String username) {
        if (!StringUtils.equals(this.username, username)) {
            if (this.inited) {
                throw new UnsupportedOperationException();
            } else {
                this.username = username;
            }
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        if (!StringUtils.equals(this.password, password)) {
            if (this.inited) {
                LOG.info("password changed");
            }
            this.password = password;
        }
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
