package com.airlenet.netconf.datasource.util;

public enum NetconfExceptionType {
    NONE, NetconfException, SocketTimeout,
    SessionClosed, IOException,
    ConnectRefused, JNCException, JNC_Timeout, JNC_AUTH_FAILED
}
