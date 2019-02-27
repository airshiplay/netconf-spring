package com.airlenet.netconf.datasource;

import com.airlenet.netconf.datasource.exception.*;
import com.airlenet.netconf.datasource.util.Utils;
import com.airlenet.network.NetworkConnection;
import com.airlenet.network.NetworkException;
import com.tailf.jnc.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

public class NetconfConnection implements NetworkConnection {

    protected final NetconfSession netconfSession;
    private final long sessionId;
    protected final String sessionName;
    protected final SSHSession sshSession;
    protected volatile boolean abandoned = false;
    protected boolean transaction;
    protected JNCSubscriber jncSubscriber;
    protected String stream;
    protected String runStackTrace;

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
        } catch (Exception e) {
            throw getCauseException(e);
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
    public void commit() throws NetconfException {
        try {
            netconfSession.commit();//now commit them 确认提交
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet get(String xpath) throws NetconfException {
        try {
            runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            return netconfSession.get(xpath);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet get(Element subtreeFilter) throws NetconfException {
        try {
            runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            return netconfSession.get(subtreeFilter);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }


    public NodeSet getConfig(String xpath) throws NetconfException {
        try {
            runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            return netconfSession.getConfig(xpath);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public NodeSet getConfig(Element subtreeFilter) throws NetconfException {
        try {
            runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            return netconfSession.getConfig(subtreeFilter);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void editConfig(Element configTree) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
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
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        try {
            this.stream = stream;
            jncSubscriber.setStream(stream);
            netconfSession.createSubscription(stream);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void subscription(String stream, String eventFilter, String startTime) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
        try {
            this.stream = stream;
            jncSubscriber.setStream(stream);
            netconfSession.createSubscription(stream, eventFilter, startTime, null);
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    public void subscription(String stream, String eventFilter, String startTime, String stopTime) throws NetconfException {
        runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
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
            runStackTrace = Utils.toString(Thread.currentThread().getStackTrace());
            Element receiveNotification = this.netconfSession.receiveNotification();
            return receiveNotification;
        } catch (Exception e) {
            throw getCauseException(e);
        }
    }

    private NetconfException getCauseException(Exception e) {
        if (e instanceof SocketTimeoutException) {
            return new NetconfSocketTimeoutException(e);
        }
        if (e instanceof SessionClosedException) {
            abandoned = true;
            return new NetconfConnectClosedException(e);
        }
        if (e instanceof ConnectException) {
            return new NetconfConnectException(e);
        }
        if (e instanceof IOException) {
            Throwable cause2 = e.getCause();
            if (cause2 != null && cause2 instanceof ConnectException) {
                return new NetconfConnectException(e);
            } else if (cause2 != null && cause2 instanceof SocketTimeoutException) {
                return new NetconfSocketTimeoutException(e);
            } else if (cause2.getCause() != null && cause2.getCause() instanceof ConnectException) {
                return new NetconfConnectException(e);
            }
            return new NetconfIOException(e);
        }
        if (e instanceof JNCException) {
            if (e.toString().startsWith("Timeout error:")) {
                return new NetconfJNCTimeOutException(e);
            } else if (e.toString().startsWith("Authentication failed")) {
                return new NetconfAuthException(e);
            }
            return new NetconfJNCException(e);
        }
        return new NetconfException(e);
    }

    public String getRunStackTrace() {
        return runStackTrace;
    }
}
