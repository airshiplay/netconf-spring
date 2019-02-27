package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfConnectClosedException extends NetconfConnectException {
    public NetconfConnectClosedException(Throwable cause) {
        super(cause);
    }
}
