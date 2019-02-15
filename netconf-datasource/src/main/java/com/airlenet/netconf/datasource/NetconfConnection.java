package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;

import java.io.IOException;

public class NetconfConnection implements NetworkConnection {

    protected final NetconfSession netconfSession;
    private final long sessionId;
    protected boolean transaction;

    public NetconfConnection(NetconfSession netconfSession) {
        this.netconfSession = netconfSession;
        this.sessionId = netconfSession.sessionId;
    }

    @Override
    public void close() throws NetworkException {
        try {
            netconfSession.closeSession();
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    @Override
    public boolean isClosed() throws NetworkException {
        return false;
    }

    @Override
    public void rollback() throws NetworkException {

    }

    public boolean isCandidate() {
        return netconfSession.hasCapability(Capabilities.CANDIDATE_CAPABILITY);
    }

    public boolean isConfirmedCommit() {
        return this.netconfSession.hasCapability(Capabilities.CONFIRMED_COMMIT_CAPABILITY);
    }

    public boolean isWritableRunning() {
        return this.netconfSession.hasCapability(Capabilities.WRITABLE_RUNNING_CAPABILITY);
    }

    @Override
    public void commit() throws NetworkException {
        try {
            netconfSession.commit();//now commit them 确认提交
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    @Override
    public NodeSet executeQuery(String req) throws NetworkException {
        try {
            Element readXml = Element.readXml(req);
            NodeSet nodeSet = netconfSession.get(readXml);
            return nodeSet;
        } catch (JNCException e) {
            throw new NetworkException(e);
        } catch (IOException e) {
            throw new NetworkException(e);
        }
    }

    @Override
    public Object executeConfig(String req) throws NetworkException {
        try {
            Element readXml = Element.readXml(req);
            NodeSet nodeSet = netconfSession.getConfig(readXml);
            return nodeSet;
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public NodeSet get(Element subtreeFilter) throws NetworkException {
        try {
            return netconfSession.get(subtreeFilter);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public NodeSet get(String xpath) throws NetworkException {
        try {
            return netconfSession.get(xpath);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public NodeSet getConfig(String xpath) throws NetworkException {
        try {
            return netconfSession.getConfig(xpath);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public NodeSet getConfig(Element subtreeFilter) throws NetworkException {
        try {
            return netconfSession.getConfig(subtreeFilter);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public void editConfig(Element configTree) throws NetworkException {
        if (this.transaction && isCandidate()) {
            try {
                netconfSession.discardChanges();//现将 上次没有提交的配置 还原
                netconfSession.lock(NetconfSession.CANDIDATE);
                netconfSession.copyConfig(NetconfSession.RUNNING, NetconfSession.CANDIDATE);
                this.netconfSession.editConfig(NetconfSession.CANDIDATE, configTree);
                if (isConfirmedCommit()) {
                    netconfSession.confirmedCommit(60);// candidates are now updated 1分钟内没有确认 则还原配置
                }
                netconfSession.commit();//now commit them 确认提交
            } catch (JNCException e) {
                throw new NetconfException(e);
            } catch (IOException e) {
                throw new NetconfException(e);
            } finally {
                try {
                    netconfSession.unlock(NetconfSession.CANDIDATE);
                } catch (JNCException e) {
                    throw new NetconfException(e);
                } catch (IOException e) {
                    throw new NetconfException(e);
                }
            }
        } else {
            try {
                netconfSession.editConfig(configTree);
            } catch (JNCException e) {
                throw new NetconfException(e);
            } catch (IOException e) {
                throw new NetconfException(e);
            }
        }

    }

    public void subscription(String stream) throws NetconfException {
        try {
            netconfSession.createSubscription(stream);
        } catch (IOException e) {
            throw new NetconfException(e);
        } catch (JNCException e) {
            throw new NetconfException(e);
        }
    }

}
