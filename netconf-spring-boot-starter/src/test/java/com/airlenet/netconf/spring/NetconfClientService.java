package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.NetconfException;
import com.tailf.jnc.NodeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class NetconfClientService {
    @Autowired
    NetconfClient netconfClient;

    @PostConstruct
    private void PostConstruct() {
        try {
            NodeSet nodeSet = netconfClient.get("netconf://172.19.102.122:2022", "admin", "admin", "sys-info");
        } catch (NetconfException e) {
            e.printStackTrace();
        }
    }
}
