package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkException;

public class NetconfException extends NetworkException {
    public NetconfException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetconfException(Throwable cause) {
        super(cause);
    }
}
