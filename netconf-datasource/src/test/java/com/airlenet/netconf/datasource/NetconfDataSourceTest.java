package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkException;
import com.tailf.jnc.NodeSet;
import org.junit.Test;

public class NetconfDataSourceTest {

    NetconfDataSource dataSource = new NetconfDataSource("netconf://172.19.102.122:2022", "admin", "admin");

    @Test
    public void testGetConnection() throws NetworkException {
        NetconfPooledConnection connection = dataSource.getConnection();
        System.out.println(connection);
        NodeSet nodeSet = connection.get("sys-info");
        System.out.println(nodeSet.toXMLString());


        NetconfPooledConnection connection1 = dataSource.getConnection();
        System.out.println(connection1);
        NodeSet nodeSet1 = connection1.get("sys-info");
        System.out.println(nodeSet1.toXMLString());


        connection.close();

        connection = dataSource.getConnection();
        System.out.println(connection);
        nodeSet = connection.get("sys-info");
        System.out.println(nodeSet.toXMLString());
    }
}
