package com.airlenet.network;

public interface MultiNetworkDataSource extends NetworkDataSource {

    public NetworkDataSource getDataSource(String url, String username, String password) throws NetworkException;

    public NetworkConnection getConnection(String url, String username, String password) throws NetworkException;
}
