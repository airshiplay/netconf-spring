package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JNCSubscriber extends IOSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(JNCSubscriber.class);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static final String OnlineNotification = "<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><connect></connect></notification>";
    private static final String OfflineNotification = "<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><disconnect></disconnect></notification>";

    private NetconfSubscriber netconfSubscriber;
    private final String url;
    private String sessionName;
    private String stream;

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
        if (netconfSubscriber != null)
            netconfSubscriber.input(this.url, msg);
    }

    @Override
    public void output(String msg) {
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
