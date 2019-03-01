package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNCSubscriber extends IOSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(JNCSubscriber.class);
    protected NetconfConnection netconfConnection;
    private NetconfSubscriber netconfSubscriber;
    private final String url;
    private String sessionName;
    private String stream;
    protected long inputCount;
    protected long inputTimeMillis;
    private String inputMessge;
    protected long outputCount;
    protected long outputTimeMillis;
    private String outputMessage;

    public JNCSubscriber(String url, NetconfSubscriber netconfSubscriber) {
        super(false);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    public JNCSubscriber(String url, String sessionName, NetconfSubscriber subscriber) {
        super(false);
        this.netconfSubscriber = subscriber;
        this.url = url;
        this.sessionName = sessionName;
    }

    @Override
    public void input(String msg) {
        logger.debug("url={},sessionName={},stream={},msg={}", this.url, sessionName, stream, msg);
        this.inputTimeMillis = System.currentTimeMillis();
        this.inputMessge = msg;
        this.inputCount++;
        if (netconfConnection != null) {
            netconfConnection.updateInputDataInteraction(inputMessge,inputCount,inputTimeMillis);
        }
        if (netconfSubscriber != null) {
            netconfSubscriber.input(this.url, msg);
        }
    }

    @Override
    public void output(String msg) {
        logger.debug("url={},sessionName={},stream={},msg={}", this.url, sessionName, stream, msg);
        this.outputTimeMillis = System.currentTimeMillis();
        this.outputMessage = msg;
        this.outputCount++;
        if (netconfConnection != null) {
            netconfConnection.updateOutputDataInteraction(outputMessage,outputCount,outputTimeMillis);
        }
        if (netconfSubscriber != null) {
            netconfSubscriber.output(this.url, msg);
        }
    }

    public void setNetconfSubscriber(NetconfSubscriber netconfSubscriber) {
        this.netconfSubscriber = netconfSubscriber;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public void setNetconfConnection(NetconfConnection connection) {
        this.netconfConnection = connection;
        if (netconfConnection != null) {
            netconfConnection.setInputDataInteraction(inputMessge,inputCount,inputTimeMillis);
            netconfConnection.setOutputDataInteraction(outputMessage,outputCount,outputTimeMillis);
        }
    }
}
