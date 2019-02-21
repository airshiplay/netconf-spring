package com.airlenet.netconf.datasource.util;

public enum NetconfExceptionType {
    NONE, NetconfException, SocketTimeoutException,
    SessionClosedException, IOException,
    ConnectException, JNCException,
}
