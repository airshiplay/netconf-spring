package com.airlenet.network;

public interface NetworkConnection {

    void close() throws NetworkException;

    boolean isClosed() throws NetworkException;

    void rollback() throws NetworkException;

    void commit() throws NetworkException;

    Object executeQuery(String req) throws NetworkException;

    Object executeConfig(String req) throws NetworkException;


}
