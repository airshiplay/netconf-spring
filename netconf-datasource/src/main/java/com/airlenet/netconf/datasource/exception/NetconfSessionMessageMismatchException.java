package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfSessionMessageMismatchException extends NetconfException {
    public NetconfSessionMessageMismatchException(Throwable cause) {
        super(cause);
    }
}
