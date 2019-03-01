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


        new Thread(){
            @Override
            public void run() {
                try {
                    NodeSet nodeSet = netconfClient.get("netconf://172.19.102.122:2022", "admin", "admin", "sys-info");
                } catch (NetconfException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    NodeSet nodeSet = netconfClient.get("netconf://1.1.1.1:2022", "admin", "admin", "sys-info");
                } catch (NetconfException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    NodeSet nodeSet = netconfClient.get("netconf://4.4.4.4:2022", "admin", "admin", "sys-info");
                } catch (NetconfException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                try {
                    NodeSet nodeSet = netconfClient.get("netconf://2.2.2.2:2022", "admin", "admin", "sys-info");
                } catch (NetconfException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        new Thread(){
            @Override
            public void run() {
                try {
                    NodeSet nodeSet = netconfClient.get("netconf://3.3.3.3:2022", "admin", "admin", "sys-info");
                } catch (NetconfException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }
}
