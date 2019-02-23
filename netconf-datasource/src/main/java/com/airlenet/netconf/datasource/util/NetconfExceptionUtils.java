package com.airlenet.netconf.datasource.util;

import com.airlenet.netconf.datasource.NetconfException;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.SessionClosedException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class NetconfExceptionUtils {

    public static NetconfExceptionType getCauseType(NetconfException e) {
        if (e == null) {
            return NetconfExceptionType.NONE;
        }
        if (e.getCause() == null) {
            return NetconfExceptionType.NetconfException;
        }
        if (e.getCause() instanceof SocketTimeoutException) {
            return NetconfExceptionType.SocketTimeout;
        }
        if (e.getCause() instanceof SessionClosedException) {
            return NetconfExceptionType.SessionClosed;
        }
        if (e.getCause() instanceof IOException) {
            if (e.getCause().getCause() != null && e.getCause().getCause() instanceof ConnectException) {
                return NetconfExceptionType.ConnectRefused;
            } else if (e.getCause().getCause().getCause() != null && e.getCause().getCause().getCause() instanceof ConnectException) {
                return NetconfExceptionType.ConnectRefused;
            }
            return NetconfExceptionType.IOException;
        }
        if (e.getCause() instanceof JNCException) {
            if (e.getCause().getMessage().startsWith("Timeout error:")) {
                return NetconfExceptionType.JNC_Timeout;
            } else if (e.getCause().getMessage().startsWith("Authentication failed")) {
                return NetconfExceptionType.JNC_AUTH_FAILED;
            }
            return NetconfExceptionType.JNCException;
        }
        return NetconfExceptionType.NetconfException;
    }

    public static Exception getCauseException(Exception e) {
        if (e == null) {
            return null;
        }
        if (e.getCause() == null) {
            return e;
        }
        if (e.getCause() instanceof SocketTimeoutException) {
            return (SocketTimeoutException) e.getCause();
        }
        if (e.getCause() instanceof SessionClosedException) {
            return (SessionClosedException) e.getCause();
        }
        if (e.getCause() instanceof IOException) {
            if (e.getCause().getCause() != null && e.getCause().getCause() instanceof ConnectException) {
                return (ConnectException) e.getCause();
            } else if (e.getCause().getCause().getCause() != null && e.getCause().getCause().getCause() instanceof ConnectException) {
                return (ConnectException) e.getCause();
            }
            return (IOException) e.getCause();
        }
        if (e.getCause() instanceof JNCException) {
            return (JNCException) e.getCause();
        }
        return e;
    }
}
