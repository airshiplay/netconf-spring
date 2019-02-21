package com.airlenet.network;

public interface MultiNetworkDataSource extends NetworkDataSource {

    public NetworkDataSource getDataSource(String url, String username, String password) throws NetworkException;

    public NetworkConnection getConnection(String url, String username, String password) throws NetworkException;

    public void removeDataSource(String url) throws NetworkException;

    public void addDataSource(String url, String username, String password) throws NetworkException;

    public void removeDataSource(NetworkDataSource dataSource) throws NetworkException;

    public void addDataSource(NetworkDataSource dataSource) throws NetworkException;
}
