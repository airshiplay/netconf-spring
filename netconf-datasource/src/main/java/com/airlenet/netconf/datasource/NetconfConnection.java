package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.exception.NetconfSessionClosedException;
import com.airlenet.netconf.datasource.util.NetconfExceptionUtils;
import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;

public class NetconfConnection implements NetworkConnection {

    protected final NetconfSession netconfSession;
    private final long sessionId;
    protected final String sessionName;
    protected final SSHSession sshSession;
    protected long inputTimeMillis;
    protected long inputCount;
    protected String inputMessage;
    protected long outputTimeMillis;
    protected long outputCount;
    protected String outputMessage;
    protected volatile boolean abandoned = false;
    protected boolean transaction;
    protected JNCSubscriber jncSubscriber;
    protected String stream;
    NetconfDataSource netconfDataSource;

    public NetconfConnection(String sessionName, SSHSession sshSession, NetconfSession netconfSession, JNCSubscriber jncSubscriber) {
        this.netconfSession = netconfSession;
        this.sessionId = netconfSession.sessionId;
        this.jncSubscriber = jncSubscriber;
        this.sessionName = sessionName;
        this.sshSession = sshSession;
        this.jncSubscriber.setNetconfConnection(this);
    }

    public NetconfConnection(NetconfDataSource netconfDataSource, String sessionName, SSHSession sshSession, NetconfSession netconfSession, JNCSubscriber jncSubscriber) {
        this.netconfDataSource = netconfDataSource;
        this.netconfSession = netconfSession;
        this.sessionId = netconfSession.sessionId;
        this.jncSubscriber = jncSubscriber;
        this.sessionName = sessionName;
        this.sshSession = sshSession;
        this.jncSubscriber.setNetconfConnection(this);
    }

    @Override
    public void close() throws NetworkException {
        try {
            netconfSession.closeSession();
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public String getSessionName() {
        return sessionName;
    }

    public long getSessionId() {
        return sessionId;
    }

    public long getInputTimeMillis() {
        return inputTimeMillis;
    }

    public void updateInputDataInteraction(String message, long inputCount, long inputTimeMillis) {
        this.inputTimeMillis = inputTimeMillis;
        this.inputMessage = message;
        this.inputCount = inputCount;
        if (netconfDataSource != null) {
            netconfDataSource.inputDataInteractionTimeMillis = inputTimeMillis;
        }
    }

    public void setInputDataInteraction(String message, long inputCount, long inputTimeMillis) {
        this.inputTimeMillis = inputTimeMillis;
        this.inputMessage = message;
        this.inputCount = inputCount;
        if (netconfDataSource != null) {
            netconfDataSource.inputDataInteractionTimeMillis = inputTimeMillis;
        }
    }

    public long getOutputTimeMillis() {
        return outputTimeMillis;
    }

    public void updateOutputDataInteraction(String message, long outputCount, long outputTimeMillis) {
        this.outputTimeMillis = outputTimeMillis;
        this.outputMessage = message;
        this.outputCount = outputCount;
        if (netconfDataSource != null) {
            netconfDataSource.outputDataInteractionTimeMillis = outputTimeMillis;
        }
    }

    public void setOutputDataInteraction(String message, long outputCount, long outputTimeMillis) {
        this.outputTimeMillis = outputTimeMillis;
        this.outputMessage = message;
        this.outputCount = outputCount;
        if (netconfDataSource != null) {
            netconfDataSource.outputDataInteractionTimeMillis = outputTimeMillis;
        }
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
    public void commit() throws NetconfException {
        try {
            netconfSession.commit();//now commit them 确认提交
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet callRpc(Element data) throws NetconfException {
        try {
            return netconfSession.callRpc(data);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public Element rpc(String request) throws NetconfException {
        try {
            return netconfSession.rpc(request);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public Element rpc(Element request) throws NetconfException {
        try {
            return netconfSession.rpc(request);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public int sendRequest(String request) throws NetconfException {
        try {
            return netconfSession.sendRequest(request);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public int sendRequest(Element request) throws NetconfException {
        try {
            return netconfSession.sendRequest(request);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public int sendRpc(Element data) throws NetconfException {
        try {
            return netconfSession.sendRpc(data);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void validate(Element configTree) throws NetconfException {
        try {
            netconfSession.validate(configTree);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public Element action(Element data) throws NetconfException {
        try {
            return netconfSession.action(data);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet get(String xpath) throws NetconfException {
        try {
            return netconfSession.get(xpath);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet get(Element subtreeFilter) throws NetconfException {
        try {
            return netconfSession.get(subtreeFilter);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }


    public NodeSet getConfig(String xpath) throws NetconfException {
        try {
            return netconfSession.getConfig(xpath);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet getConfig(Element subtreeFilter) throws NetconfException {
        try {
            return netconfSession.getConfig(subtreeFilter);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void editConfig(Element configTree) throws NetconfException {
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
            } catch (Exception e) {
                throw getCauseException(e);
            } finally {
                try {
                    netconfSession.unlock(NetconfSession.CANDIDATE);
                } catch (Exception e) {
                    throw getCauseException(e);
                }
            }
        } else {
            try {
                netconfSession.editConfig(configTree);
            } catch (Exception e) {
                throw getCauseException(e);
            }
        }

    }

    public void setReadTimeout(long readTimeout) {
        this.sshSession.setReadTimeout(Math.toIntExact(readTimeout));
    }

    public void subscription(String stream) throws NetconfException {
        try {
            this.stream = stream;
            jncSubscriber.setStream(stream);
            netconfSession.createSubscription(stream);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void subscription(String stream, String eventFilter, String startTime) throws NetconfException {
        try {
            this.stream = stream;
            jncSubscriber.setStream(stream);
            netconfSession.createSubscription(stream, eventFilter, startTime, null);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void subscription(String stream, String eventFilter, String startTime, String stopTime) throws NetconfException {
        try {
            this.stream = stream;
            jncSubscriber.setStream(stream);
            netconfSession.createSubscription(stream, eventFilter, startTime, stopTime);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public Capabilities getCapabilities() {
        return netconfSession.getCapabilities();
    }

    public boolean hasNotification() {
        return netconfSession.getCapabilities().hasNotification();
    }

    public void setNetconfSubscriber(NetconfSubscriber netconfSubscriber) {
        this.jncSubscriber.setNetconfSubscriber(netconfSubscriber);
    }

    public Element receiveNotification() throws NetconfException {
        try {
            Element receiveNotification = this.netconfSession.receiveNotification();
            return receiveNotification;
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    private NetconfException getCauseException(Exception e) {
        if (e instanceof SessionClosedException) {
            abandoned = true;
            return new NetconfSessionClosedException(e);
        }
        return NetconfExceptionUtils.getCauseException(e);
    }
}
