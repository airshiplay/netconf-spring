package com.airlenet.netconf.datasource.util;

import com.airlenet.netconf.datasource.NetconfException;
import com.airlenet.netconf.datasource.exception.*;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.SessionClosedException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class NetconfExceptionUtils {
    public static NetconfException getCauseException(Exception e) {
        if (e instanceof SocketTimeoutException) {
            return new NetconfSocketTimeoutException(e);
        }
        if (e instanceof SessionClosedException) {
            return new NetconfSessionClosedException(e);
        }
        if (e instanceof ConnectException) {
            return new NetconfConnectException(e);
        }
        if (e instanceof IOException) {
            Throwable cause2 = e.getCause();
            if (cause2 != null) {
                Throwable cause3 = cause2.getCause();
                if (cause2 instanceof ConnectException) {
                    return new NetconfConnectException(e);
                } else if (cause2 instanceof SocketTimeoutException) {
                    return new NetconfSocketTimeoutException(e);
                } else if (cause3 != null) {
                    if (cause3 instanceof ConnectException) {
                        return new NetconfConnectException(e);
                    }
                }
            }
            return new NetconfIOException(e);
        }
        if (e instanceof JNCException) {
            if (e.toString().startsWith("Timeout error:")) {
                return new NetconfJNCTimeOutException(e);
            } else if (e.toString().startsWith("Authentication failed")) {
                return new NetconfAuthException(e);
            } else if (e.toString().contains("A subscription is already active for this session")) {
                return new NetconfSuscribedException(e);
            } else if (e.toString().contains("Message ID mismatch")) {
                return new NetconfSessionMessageMismatchException(e);
            } else if (e.toString().contains("Element does not exists")) {
                return new NetconfJNCElementMissingException(e);
            } else if (e.toString().startsWith("Parse error")) {
                return new NetconfJNCParseException(e);
            } else if (e.toString().startsWith("Session error:")) {
                return new NetconfJNCSessionException(e);
            }
            return new NetconfJNCException(e);
        }
        return new NetconfException(e);
    }

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
}
