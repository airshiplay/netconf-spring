package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfDataSourceClosedException extends NetconfException {
    public NetconfDataSourceClosedException(String message) {
        super(message);
    }
}
