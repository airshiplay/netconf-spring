package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.NetconfDevice;
import com.airlenet.netconf.datasource.NetconfException;
import com.airlenet.netconf.datasource.NetconfPooledConnection;
import com.tailf.jnc.Element;
import com.tailf.jnc.NodeSet;

public interface NetconfClient {

    public NodeSet get(String url, String username, String password, String xpath) throws NetconfException;

    public NodeSet get(NetconfDevice netconfDevice, String xpath) throws NetconfException;

    public NodeSet get(String url, String username, String password, Element subtreeFilter) throws NetconfException;

    public NodeSet get(NetconfDevice netconfDevice, Element subtreeFilter) throws NetconfException;

    public NodeSet getConfig(String url, String username, String password, String xpath) throws NetconfException;

    public NodeSet getConfig(NetconfDevice netconfDevice, String xpath) throws NetconfException;

    public NodeSet getConfig(String url, String username, String password, Element subtreeFilter) throws NetconfException;

    public NodeSet getConfig(NetconfDevice netconfDevice, Element subtreeFilter) throws NetconfException;

    public void editConfig(String url, String username, String password, Element configTree) throws NetconfException;

    public void editConfig(NetconfDevice netconfDevice, Element configTree) throws NetconfException;

    public NetconfPooledConnection getConnection(String url, String username, String password) throws NetconfException;

    public NetconfPooledConnection getConnection(NetconfDevice netconfDevice) throws NetconfException;

    public NodeSet callRpc(NetconfDevice netconfDevice, Element data) throws NetconfException;

    public NodeSet callRpc(String url, String username, String password, Element data) throws NetconfException;

    public Element rpc(NetconfDevice netconfDevice, String request) throws NetconfException;

    public Element rpc(String url, String username, String password, String request) throws NetconfException;

    public Element rpc(NetconfDevice netconfDevice, Element request) throws NetconfException;

    public Element rpc(String url, String username, String password, Element request) throws NetconfException;

    public int sendRequest(NetconfDevice netconfDevice, String request) throws NetconfException;

    public int sendRequest(String url, String username, String password, String request) throws NetconfException;

    public int sendRequest(NetconfDevice netconfDevice, Element request) throws NetconfException;

    public int sendRequest(String url, String username, String password, Element request) throws NetconfException;

    public int sendRpc(NetconfDevice netconfDevice, Element data) throws NetconfException;

    public int sendRpc(String url, String username, String password, Element data) throws NetconfException;

    public void validate(NetconfDevice netconfDevice, Element configTree) throws NetconfException;

    public void validate(String url, String username, String password, Element configTree) throws NetconfException;

    public Element action(String url, String username, String password, Element data) throws NetconfException;

    public Element action(NetconfDevice netconfDevice, Element data) throws NetconfException;
}
