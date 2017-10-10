package com.airlenet.netconf.common;

import com.tailf.jnc.JNCException;

import java.io.IOException;

/**
 * @author airlenet
 * @version 2017-10-10
 */
public class PlayNetconfDeviceTest {
    public static void main(String args[]) throws IOException, JNCException {
        PlayNetconfDevice netconfDevice = new PlayNetconfDevice(1L, "admin", "admin", "172.16.129.12", 2022);
        long sessionId =  netconfDevice.getDefaultNetconfSession().getSessionId();
        sessionId= netconfDevice.getDefaultNetconfSession().getSessionId();
        netconfDevice.closeDefaultNetconfSession();
         sessionId = netconfDevice.getDefaultNetconfSession().getSessionId();
        netconfDevice.close();
        sessionId= netconfDevice.getDefaultNetconfSession().getSessionId();
    }
}
