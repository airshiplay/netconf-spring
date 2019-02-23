package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.NetconfException;
import com.tailf.jnc.NodeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class NetconfClientTest {
    @Autowired
    NetconfClient netconfClient;

    @Test
    public void testMail() throws NetconfException {
        NodeSet nodeSet = netconfClient.get("netconf://172.19.102.122:2022", "admin", "admin", "sys-info");
        System.out.println(nodeSet.toXMLString());

        nodeSet = netconfClient.get("netconf://172.19.102.122:2022", "admin", "admin", "sys-info");
        System.out.println(nodeSet.toXMLString());
    }
}
