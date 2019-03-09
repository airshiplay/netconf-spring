package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.MultiNetconfDataSource;
import com.airlenet.netconf.datasource.NetconfException;
import com.airlenet.netconf.datasource.NetconfPooledConnection;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.Element;
import com.tailf.jnc.NodeSet;

public class DefaultNetconfClient implements NetconfClient {
    private MultiNetconfDataSource multiNetconfDataSource;

    public DefaultNetconfClient(MultiNetconfDataSource multiNetconfDataSource) {
        this.multiNetconfDataSource = multiNetconfDataSource;
    }

    @Override
    public NodeSet get(String url, String username, String password, String xpath) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.get(xpath);
        }
    }

    @Override
    public NodeSet get(NetconfDevice netconfDevice, String xpath) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            return connection.get(xpath);
        }
    }

    @Override
    public NodeSet get(String url, String username, String password, Element subtreeFilter) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.get(subtreeFilter);
        }
    }

    @Override
    public NodeSet get(NetconfDevice netconfDevice, Element subtreeFilter) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {

            return connection.get(subtreeFilter);
        }
    }

    @Override
    public NodeSet getConfig(String url, String username, String password, String xpath) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {

            return connection.getConfig(xpath);
        }
    }

    @Override
    public NodeSet getConfig(NetconfDevice netconfDevice, String xpath) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {

            return connection.getConfig(xpath);
        } catch (NetworkException e) {
            throw new NetconfException(e);
        }
    }

    @Override
    public NodeSet getConfig(String url, String username, String password, Element subtreeFilter) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {

            return connection.getConfig(subtreeFilter);
        }
    }

    @Override
    public NodeSet getConfig(NetconfDevice netconfDevice, Element subtreeFilter) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            return connection.getConfig(subtreeFilter);
        }
    }

    @Override
    public void editConfig(String url, String username, String password, Element configTree) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            connection.editConfig(configTree);
        }
    }

    @Override
    public void editConfig(NetconfDevice netconfDevice, Element configTree) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.editConfig(configTree);
        }
    }

    @Override
    public NetconfPooledConnection getConnection(String url, String username, String password) throws NetconfException {
        return multiNetconfDataSource.getConnection(url, username, password);
    }

    @Override
    public NetconfPooledConnection getConnection(NetconfDevice netconfDevice) throws NetconfException {
        return multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword());
    }
}
