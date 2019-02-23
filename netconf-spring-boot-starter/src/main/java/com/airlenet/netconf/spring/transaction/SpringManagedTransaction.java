package com.airlenet.netconf.spring.transaction;

import com.airlenet.netconf.datasource.NetconfConnection;
import com.airlenet.netconf.datasource.NetconfException;

public class SpringManagedTransaction  implements Transaction {
    @Override
    public NetconfConnection getConnection() throws NetconfException {
        return null;
    }

    @Override
    public void commit() throws NetconfException {

    }

    @Override
    public void rollback() throws NetconfException {

    }

    @Override
    public void close() throws NetconfException {

    }

    @Override
    public Integer getTimeout() throws NetconfException {
        return null;
    }
}
