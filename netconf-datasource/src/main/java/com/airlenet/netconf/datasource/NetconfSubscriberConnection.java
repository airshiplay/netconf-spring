package com.airlenet.netconf.datasource;

import com.tailf.jnc.NetconfSession;

public class NetconfSubscriberConnection extends NetconfConnection {
    public NetconfSubscriberConnection(NetconfSession netconfSession,JNCSubscriber jncSubscriber) {
        super(netconfSession);
    }
}
