package com.airlenet.netconf.datasource;

import java.sql.Connection;

public class NetconfConnectionHolder {
    protected long connectionId;
    protected boolean transaction;
    protected final NetconfConnection conn;

    protected final NetconfDataSource dataSource;

    public NetconfConnectionHolder(NetconfDataSource dataSource, NetconfConnection conn, long connectionId) {
        this.dataSource = dataSource;
        this.conn = conn;
        this.connectionId = connectionId;

    }
}
