package com.airlenet.netconf.spring.transaction;


import com.airlenet.netconf.datasource.NetconfConnection;
import com.airlenet.netconf.datasource.NetconfDataSource;

import java.util.Properties;

public interface TransactionFactory {
    void setProperties(Properties var1);

    Transaction newTransaction(NetconfConnection connection);

    Transaction newTransaction(NetconfDataSource netconfDataSource, boolean var3);
}
