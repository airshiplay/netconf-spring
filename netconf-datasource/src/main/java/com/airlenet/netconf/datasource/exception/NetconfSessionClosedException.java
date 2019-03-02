package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfSessionClosedException extends NetconfException {
    public NetconfSessionClosedException(Throwable cause) {
        super(cause);
    }
}
