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
        Throwable cause = e.getCause();
        if (cause == null) {
            return e;
        }
        if (cause instanceof SocketTimeoutException) {
            return (SocketTimeoutException) cause;
        }
        if (cause instanceof SessionClosedException) {
            return (SessionClosedException) cause;
        }
        if (cause instanceof IOException) {
            Throwable cause2 = cause.getCause();
            if (cause2 != null && cause2 instanceof ConnectException) {
                return (ConnectException) cause2;
            } else if (cause2.getCause() != null && cause2.getCause() instanceof ConnectException) {
                return (ConnectException) cause2.getCause();
            }
            return (IOException) cause;
        }
        if (cause instanceof JNCException) {
            return (JNCException) cause;
        }
        return e;
    }
}
