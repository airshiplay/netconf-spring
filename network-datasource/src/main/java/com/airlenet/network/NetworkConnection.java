package com.airlenet.network;

public interface NetworkConnection {

    void close() throws NetworkException;

    boolean isClosed() throws NetworkException;

    void rollback() throws NetworkException;

    void commit() throws NetworkException;

    Object get(String req) throws NetworkException;

    Object getConfig(String req) throws NetworkException;

}
