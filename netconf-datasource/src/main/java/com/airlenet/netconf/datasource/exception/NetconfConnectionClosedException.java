package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfConnectionClosedException extends NetconfException {
    public NetconfConnectionClosedException(Throwable cause) {
        super(cause);
    }
}
