package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkDataSource;
import com.airlenet.network.NetworkException;
import com.airlenet.network.NetworkMultiDataSource;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class NetconfMultiDataSource extends NetconfDataSource implements NetworkMultiDataSource {

    protected final Map<Object, NetconfDataSource> dataSourceObjectMap = new IdentityHashMap<>();
    public ReentrantLock lock = new ReentrantLock();

    public NetconfMultiDataSource() {
        this("", "", "");
    }

    private NetconfMultiDataSource(String url, String username, String password) {
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
    public final Set<NetconfPooledConnection> getActiveConnections() {
        throw new IllegalArgumentException("");
    }

    @Override
    public NetworkDataSource getDataSource(String url, String username, String password) throws NetworkException {
        synchronized (dataSourceObjectMap) {
            NetconfDataSource netconfDataSource = dataSourceObjectMap.get(url);
            if (netconfDataSource == null) {
                netconfDataSource = new NetconfDataSource(url, username, password);
                dataSourceObjectMap.put(url, netconfDataSource);
            }
            return netconfDataSource;
        }
    }

    @Override
    public NetworkConnection getConnection(String url, String username, String password) throws NetworkException {
        NetworkDataSource dataSource = getDataSource(url, username, password);
        return dataSource.getConnection();
    }
}
