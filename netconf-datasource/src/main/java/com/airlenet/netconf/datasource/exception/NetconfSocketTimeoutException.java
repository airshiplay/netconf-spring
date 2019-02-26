package com.airlenet.netconf.datasource.exception;

import com.airlenet.netconf.datasource.NetconfException;

public class NetconfSocketTimeoutException extends NetconfException {
    public NetconfSocketTimeoutException(Throwable cause) {
        super(cause);
    }
}
