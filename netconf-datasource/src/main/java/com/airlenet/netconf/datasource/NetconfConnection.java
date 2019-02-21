package com.airlenet.netconf.datasource;

import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;

import java.io.IOException;

public class NetconfConnection implements NetworkConnection {

    protected final NetconfSession netconfSession;
    private final long sessionId;
    protected final String sessionName;
    protected final SSHSession sshSession;
    protected volatile boolean abandoned = false;
    protected boolean transaction;
    protected JNCSubscriber jncSubscriber;

    public NetconfConnection(String sessionName, SSHSession sshSession, NetconfSession netconfSession, JNCSubscriber jncSubscriber) {
        this.netconfSession = netconfSession;
        this.sessionId = netconfSession.sessionId;
        this.jncSubscriber = jncSubscriber;
        this.sessionName = sessionName;
        this.sshSession = sshSession;
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

    public String getSessionName() {
        return sessionName;
    }

    public boolean isAbandonded() {
        return this.abandoned;
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
        } catch (SessionClosedException e) {
            abandoned = true;
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
        } catch (SessionClosedException e) {
            abandoned = true;
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
        } catch (SessionClosedException e) {
            abandoned = true;
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
        } catch (SessionClosedException e) {
            abandoned = true;
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
        } catch (SessionClosedException e) {
            abandoned = true;
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
            } catch (SessionClosedException e) {
                abandoned = true;
                throw new NetconfException(e);
            } catch (IOException e) {
                throw new NetconfException(e);
            } finally {
                try {
                    netconfSession.unlock(NetconfSession.CANDIDATE);
                } catch (JNCException e) {
                    throw new NetconfException(e);
                } catch (SessionClosedException e) {
                    abandoned = true;
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
            } catch (SessionClosedException e) {
                abandoned = true;
                throw new NetconfException(e);
            } catch (IOException e) {
                throw new NetconfException(e);
            }
        }

    }

    public void setReadTimeout(long readTimeout) {
        this.sshSession.setReadTimeout(Math.toIntExact(readTimeout));
    }

    public void subscription(String stream) throws NetconfException {
        try {
            netconfSession.createSubscription(stream);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (SessionClosedException e) {
            abandoned = true;
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        }
    }

    public Capabilities getCapabilities() {
        return netconfSession.getCapabilities();
    }

    public boolean hasNotification() {
        return netconfSession.getCapabilities().hasNotification();
    }

    public Element receiveNotification() throws NetconfException {
        try {
            return this.netconfSession.receiveNotification();
        } catch (SessionClosedException e) {
            abandoned = true;
            throw new NetconfException(e);
        } catch (IOException e) {
            throw new NetconfException(e);
        } catch (JNCException e) {
            throw new NetconfException(e);
        } catch (Exception e) {
            throw new NetconfException(e);
        }
    }

}
