package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkException;
import org.junit.Test;

public class NetconfDataSourceTest {

    NetconfDataSource dataSource = new NetconfDataSource("netconf://172.19.102.122:2022", "admin", "admin");

    @Test
    public void testGetConnection() throws NetworkException {
        NetconfPooledConnection connection = dataSource.getConnection();



        connection.executeQuery("");
    }
}
