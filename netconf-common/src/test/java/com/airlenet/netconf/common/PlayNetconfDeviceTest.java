package com.airlenet.netconf.common;

import com.tailf.jnc.JNCException;
import com.tailf.jnc.NodeSet;

import java.io.IOException;

/**
 * @author airlenet
 * @version 2017-10-10
 */
public class PlayNetconfDeviceTest {
    public static void main(String args[]) throws IOException, JNCException {
        PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin", "172.19.105.115", 2022);
        long sessionId = netconfDevice.getDefaultNetconfSession().getSessionId();
        System.out.println(sessionId);
//        sessionId = netconfDevice.getDefaultNetconfSession().getSessionId();
//        System.out.println(sessionId);
//        netconfDevice.closeDefaultNetconfSession();
//        sessionId = netconfDevice.getDefaultNetconfSession().getSessionId();
//        System.out.println(sessionId);
//        netconfDevice.close();
//        sessionId = netconfDevice.getDefaultNetconfSession().getSessionId();
//        System.out.println(sessionId);
        NodeSet nodeSet = netconfDevice.getDefaultNetconfSession().get("sys-info");
        System.out.println(nodeSet);


        nodeSet = netconfDevice.getDefaultNetconfSession().get("sys-info");
        System.out.println(nodeSet);


        nodeSet = netconfDevice.getDefaultNetconfSession().get("sys-info");
        System.out.println(nodeSet);

        nodeSet = netconfDevice.getDefaultNetconfSession().get("sys-info");
        System.out.println(nodeSet);

        nodeSet = netconfDevice.getDefaultNetconfSession().get("sys-info");
        System.out.println(nodeSet);
    }
}
