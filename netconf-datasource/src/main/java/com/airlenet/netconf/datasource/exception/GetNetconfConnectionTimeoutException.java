package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class GetNetconfConnectionTimeoutException extends NetconfException {
    public GetNetconfConnectionTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public GetNetconfConnectionTimeoutException(Throwable cause) {
        super(cause);
    }

    public GetNetconfConnectionTimeoutException(String message) {
        super(message);
    }
}
