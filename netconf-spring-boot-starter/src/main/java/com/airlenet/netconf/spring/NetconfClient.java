package com.airlenet.netconf.spring;

import com.airlenet.netconf.datasource.NetconfConnection;
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

    public NodeSet editConfig(String url, String username, String password, Element configTree) throws NetconfException;

    public NodeSet editConfig(NetconfDevice netconfDevice, Element configTree) throws NetconfException;

//    public NetconfPooledConnection getConnection(String url, String username, String password) throws NetconfException;
//
//    public NetconfPooledConnection getConnection(NetconfDevice netconfDevice) throws NetconfException;

}
