package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkDataSource;

import java.util.Date;

public abstract class NetconfAbstractDataSource implements NetworkDataSource {
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22

    protected final Date createdTime = new Date();
    protected Date initedTime;
    protected long id;

    protected String name;

    public long getID() {
        return this.id;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "DataSource-" + System.identityHashCode(this);
    }

    public String getUsername() {
        return username;
    }

    public Date getCreatedTime() {
        return createdTime;
    }
}
