package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.NetconfException;

public interface NetconfSession {
    public void close() throws NetconfException;
}
