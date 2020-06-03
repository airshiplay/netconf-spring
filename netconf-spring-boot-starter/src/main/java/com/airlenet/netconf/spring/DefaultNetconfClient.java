package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.MultiNetconfDataSource;
import com.airlenet.netconf.datasource.NetconfDevice;
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
            connection.updateZoneId(netconfDevice.getZoneId());
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
            connection.updateZoneId(netconfDevice.getZoneId());
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
            connection.updateZoneId(netconfDevice.getZoneId());
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
            connection.updateZoneId(netconfDevice.getZoneId());
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
            connection.updateZoneId(netconfDevice.getZoneId());
            connection.editConfig(configTree);
        }
    }


    @Override
    public void editConfig(NetconfDevice netconfDevice, NodeSet nodeSet) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            connection.editConfig(nodeSet);
        }
    }
    @Override
    public NetconfPooledConnection getConnection(String url, String username, String password) throws NetconfException {
        return multiNetconfDataSource.getConnection(url, username, password);
    }

    @Override
    public NetconfPooledConnection getConnection(NetconfDevice netconfDevice) throws NetconfException {
        NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword());
        connection.updateZoneId(netconfDevice.getZoneId());
        return connection;
    }

    @Override
    public NodeSet callRpc(NetconfDevice netconfDevice, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.callRpc(data);
        }
    }

    @Override
    public NodeSet callRpc(String url, String username, String password, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.callRpc(data);
        }
    }

    @Override
    public Element rpc(NetconfDevice netconfDevice, String request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.rpc(request);
        }
    }

    @Override
    public Element rpc(String url, String username, String password, String request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.rpc(request);
        }
    }

    @Override
    public Element rpc(NetconfDevice netconfDevice, Element request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.rpc(request);
        }
    }

    @Override
    public Element rpc(String url, String username, String password, Element request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.rpc(request);
        }
    }

    @Override
    public int sendRequest(NetconfDevice netconfDevice, String request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.sendRequest(request);
        }
    }

    @Override
    public int sendRequest(String url, String username, String password, String request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.sendRequest(request);
        }
    }

    @Override
    public int sendRequest(NetconfDevice netconfDevice, Element request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.sendRequest(request);
        }
    }

    @Override
    public int sendRequest(String url, String username, String password, Element request) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.sendRequest(request);
        }
    }

    @Override
    public int sendRpc(NetconfDevice netconfDevice, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.sendRpc(data);
        }
    }

    @Override
    public int sendRpc(String url, String username, String password, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.sendRpc(data);
        }
    }

    @Override
    public void validate(NetconfDevice netconfDevice, Element configTree) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            connection.validate(configTree);
        }
    }

    @Override
    public void validate(String url, String username, String password, Element configTree) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            connection.validate(configTree);
        }
    }

    @Override
    public Element action(String url, String username, String password, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(url, username, password)) {
            return connection.action(data);
        }
    }

    @Override
    public Element action(NetconfDevice netconfDevice, Element data) throws NetconfException {
        try (NetconfPooledConnection connection = multiNetconfDataSource.getConnection(netconfDevice.getUrl(), netconfDevice.getUsername(), netconfDevice.getPassword())) {
            connection.updateZoneId(netconfDevice.getZoneId());
            return connection.action(data);
        }
    }
}
