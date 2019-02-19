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

    private final NetconfSubscriber netconfSubscriber;
    private final String url;

    public JNCSubscriber(boolean rawmode, String url,
                         String stream, NetconfSubscriber netconfSubscriber) {
        super(rawmode);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    public JNCSubscriber(String url, NetconfSubscriber netconfSubscriber) {
        super(false);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    @Override
    public void input(String msg) {
        logger.debug("url={},msg={}", this.url, msg);
        if (netconfSubscriber != null)
            netconfSubscriber.input(this.url, msg);
    }

    @Override
    public void output(String msg) {
        logger.debug("url={},msg={}", this.url, msg);
        if (netconfSubscriber != null)
            netconfSubscriber.output(this.url, msg);
    }

    public void offline() {

    }

    public void online() {

    }

    public void timeout() {

    }

    public void subscriberException(Exception e) {
    }
}
