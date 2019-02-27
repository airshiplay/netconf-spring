package com.airlenet.netconf.datasource;

import java.sql.Connection;

public class NetconfConnectionHolder {
    protected long connectionId;
    protected boolean transaction;
    protected final NetconfConnection conn;
    private long useCount = 0;
    protected final NetconfDataSource dataSource;
    protected String stream;
    public NetconfConnectionHolder(NetconfDataSource dataSource, NetconfConnection conn, long connectionId) {
        this.dataSource = dataSource;
        this.conn = conn;
        this.connectionId = connectionId;
    }

    public long getUseCount() {
        return useCount;
    }

    public long getConnectionId() {
        return connectionId;
    }

    public void incrementUseCount() {
        useCount++;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public void recycle() {

    }
}
