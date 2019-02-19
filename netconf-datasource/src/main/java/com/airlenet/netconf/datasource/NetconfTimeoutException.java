package com.airlenet.netconf.datasource;

public class NetconfTimeoutException extends NetconfException {
    public NetconfTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetconfTimeoutException(Throwable cause) {
        super(cause);
    }

    public NetconfTimeoutException(String message) {
        super(message);
    }
}
