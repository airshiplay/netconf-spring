package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNCSubscriber extends IOSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(JNCSubscriber.class);
    private NetconfSubscriber netconfSubscriber;
    private final String url;
    private String sessionName;
    private String stream;
    NetconfDataSource dataSource;

    public JNCSubscriber(NetconfDataSource dataSource, String url, NetconfSubscriber netconfSubscriber) {
        super(false);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
        this.dataSource = dataSource;
    }

    public JNCSubscriber(NetconfDataSource dataSource, String url, String sessionName, NetconfSubscriber subscriber) {
        super(false);
        this.dataSource = dataSource;
        this.netconfSubscriber = subscriber;
        this.url = url;
        this.sessionName = sessionName;
    }

    @Override
    public void input(String msg) {
        dataSource.inputDataInteractionTimeMillis = System.currentTimeMillis();
        logger.debug("url={},sessionName={},stream={},msg={}", this.url, sessionName, stream, msg);
        if (netconfSubscriber != null)
            netconfSubscriber.input(this.url, msg);
    }

    @Override
    public void output(String msg) {
        dataSource.outputDataInteractionTimeMillis = System.currentTimeMillis();
        logger.debug("url={},sessionName={},stream={},msg={}", this.url, sessionName, stream, msg);
        if (netconfSubscriber != null)
            netconfSubscriber.output(this.url, msg);
    }

    public void offline() {

    }

    public void online() {

    }

    public void timeout() {

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
}
