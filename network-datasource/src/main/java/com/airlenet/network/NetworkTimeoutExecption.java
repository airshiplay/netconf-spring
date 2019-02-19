package com.airlenet.network;

public class NetworkTimeoutExecption extends NetworkException {
    public NetworkTimeoutExecption(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkTimeoutExecption(Throwable cause) {
        super(cause);
    }

}
