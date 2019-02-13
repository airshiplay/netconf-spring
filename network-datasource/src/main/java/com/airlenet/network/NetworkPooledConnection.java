package com.airlenet.network;

public interface NetworkPooledConnection {

    NetworkConnection  getConnection() throws NetworkException;

    void close() throws NetworkException;
}
