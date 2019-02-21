package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkDataSource;
import com.airlenet.network.NetworkException;
import com.airlenet.network.MultiNetworkDataSource;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class MultiNetconfDataSource extends NetconfDataSource implements MultiNetworkDataSource {

    protected final Map<Object, NetconfDataSource> dataSourceObjectMap = new HashMap<>();
    public ReentrantLock lock = new ReentrantLock();

    public MultiNetconfDataSource() {
        this("", "", "");
    }

    private MultiNetconfDataSource(String url, String username, String password) {
        super(url, username, password);
    }

    @Override
    public final NetconfPooledConnection getConnection() throws NetworkException {
        throw new IllegalArgumentException("");
    }

    @Override
    public final NetconfPooledConnection getConnection(String username, String password) throws NetworkException {
        throw new IllegalArgumentException("");
    }

    //    @Override
    public final NetconfPooledConnection getConnectionDirect(long maxWaitMillis) throws NetworkException {
        throw new IllegalArgumentException("");
    }

    @Override
    public NetconfDataSource getDataSource(String url, String username, String password) throws NetworkException {
        synchronized (dataSourceObjectMap) {
            NetconfDataSource netconfDataSource = dataSourceObjectMap.get(url);
            if (netconfDataSource == null) {
                netconfDataSource = new NetconfDataSource(url, username, password);
                netconfDataSource.setReadTimeout(this.getReadTimeout());
                netconfDataSource.setConnectionTimeout(this.getConnectionTimeout());
                netconfDataSource.setMaxPoolSize(this.getMaxPoolSize());
                dataSourceObjectMap.put(url, netconfDataSource);
            }
            return netconfDataSource;
        }
    }

    @Override
    public NetconfPooledConnection getConnection(String url, String username, String password) throws NetworkException {
        NetconfDataSource dataSource = getDataSource(url, username, password);
        return dataSource.getConnection();
    }

    @Override
    public void removeDataSource(String url) throws NetworkException {
        synchronized (dataSourceObjectMap) {
            NetconfDataSource netconfDataSource = dataSourceObjectMap.get(url);
            if (netconfDataSource != null) {
                netconfDataSource.close();
            }
        }
    }

    @Override
    public void addDataSource(String url, String username, String password) throws NetworkException {
        getDataSource(url, username, password);
    }

    @Override
    public void removeDataSource(NetworkDataSource dataSource) throws NetworkException {
        if (dataSource instanceof NetconfDataSource) {
            NetconfDataSource originalNetconfDataSource = (NetconfDataSource) dataSource;
            synchronized (dataSourceObjectMap) {
                NetconfDataSource netconfDataSource = dataSourceObjectMap.get(originalNetconfDataSource.getUrl());
                if (netconfDataSource != null) {
                    netconfDataSource.close();
                }
            }
        }
    }

    @Override
    public void addDataSource(NetworkDataSource dataSource) throws NetworkException {
        if (dataSource instanceof NetconfDataSource) {
            NetconfDataSource originalNetconfDataSource = (NetconfDataSource) dataSource;
            synchronized (dataSourceObjectMap) {
                NetconfDataSource netconfDataSource = dataSourceObjectMap.get(originalNetconfDataSource.getUrl());
                if (netconfDataSource == null) {
                    dataSourceObjectMap.put(originalNetconfDataSource.getUrl(), originalNetconfDataSource);
                }
            }
        }
    }
}
