package com.airlenet.snmp.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;

public class SnmpDataSource extends SnmpAbstractDataSource {
    @Override
    public NetworkConnection getConnection() throws NetworkException {
        return null;
    }

    @Override
    public NetworkConnection getConnection(String username, String password) throws NetworkException {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public void restart() {

    }
}
