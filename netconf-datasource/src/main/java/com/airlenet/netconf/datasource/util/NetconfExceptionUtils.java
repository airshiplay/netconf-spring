package com.airlenet.netconf.datasource.util;

import com.airlenet.netconf.datasource.NetconfException;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.SessionClosedException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class NetconfExceptionUtils {

    public NetconfExceptionType getCauseType(NetconfException e) {
        if (e == null) {
            return NetconfExceptionType.NONE;
        }
        if (e.getCause() == null) {
            return NetconfExceptionType.NetconfException;
        }
        if (e.getCause() instanceof SocketTimeoutException) {
            return NetconfExceptionType.SocketTimeoutException;
        }
        if (e.getCause() instanceof SessionClosedException) {
            return NetconfExceptionType.SessionClosedException;
        }
        if (e.getCause() instanceof IOException) {
            if (e.getCause().getCause() instanceof ConnectException) {
                return NetconfExceptionType.ConnectException;
            }
            return NetconfExceptionType.IOException;
        }
        if (e.getCause() instanceof JNCException) {
            return NetconfExceptionType.JNCException;
        }

        return NetconfExceptionType.NetconfException;
    }
}
