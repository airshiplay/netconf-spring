package com.airlenet.network;

public interface NetworkDataSource {

    NetworkConnection getConnection() throws NetworkException;

    NetworkConnection getConnection(String username,String password) throws NetworkException;
}
