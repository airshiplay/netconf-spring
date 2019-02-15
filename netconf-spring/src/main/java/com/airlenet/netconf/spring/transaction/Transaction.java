package com.airlenet.netconf.spring.transaction;

import com.airlenet.netconf.datasource.NetconfConnection;
import com.airlenet.netconf.datasource.NetconfException;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {
    NetconfConnection getConnection() throws NetconfException;

    void commit() throws NetconfException;

    void rollback() throws NetconfException;

    void close() throws NetconfException;

    Integer getTimeout() throws NetconfException;
}
