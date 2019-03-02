package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkException;
import com.tailf.jnc.Element;
import com.tailf.jnc.NodeSet;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetconfDataSourceTest {
    private static final Logger logger = LoggerFactory.getLogger(NetconfDataSourceTest.class);
    NetconfDataSource dataSource = new NetconfDataSource("netconf://172.19.102.122:2022", "admin", "admin");

    @Test
    public void testGetConnection() throws NetworkException {
        NetconfPooledConnection connection = dataSource.getConnection();
        System.out.println(connection);
        NodeSet nodeSet = connection.get("sys-info");
        System.out.println(nodeSet.toXMLString());

        nodeSet = connection.get("sys-info");
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

    @Test
    public void testGetSubscriptionConnection() throws NetworkException, InterruptedException {
        NetconfPooledConnection connection = dataSource.getConnection(new NetconfSubscriber() {
            @Override
            public void input(String url, String msg) {
                logger.debug(url + msg);
            }

            @Override
            public void output(String url, String msg) {
                logger.debug(url + msg);
            }
        });
        connection.subscription("alarm",null,"2019-02-01T06:23:18.798884+00:00");
        new Thread() {
            @Override
            public void run() {
                if (connection.hasNotification()) {
                    while (true) {
                        try {
                            Element receive = connection.receiveNotification();
                            logger.debug("receive {}", receive.toXMLString());
                        } catch (NetconfException e) {
                            logger.error("", e);
                        }

                    }
                }
            }
        }.start();

        Thread.sleep(60 * 60 * 1000);
    }
}
