package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkDataSource;

import java.util.Date;
import java.util.TimeZone;

public abstract class NetconfAbstractDataSource implements NetworkDataSource {
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;//netconf://172.1.1.1:22

    protected final Date createdTime = new Date();
    protected Date initedTime;
    protected long id;
    protected TimeZone timeZone;
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
}
