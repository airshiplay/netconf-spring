package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNCSubscriber extends IOSubscriber {
    private static final Logger logger = LoggerFactory.getLogger(JNCSubscriber.class);
    private final NetconfSubscriber netconfSubscriber;
    private final String url;

    public JNCSubscriber(boolean rawmode, String url,
                         String stream, NetconfSubscriber netconfSubscriber) {
        super(rawmode);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    public JNCSubscriber(String url, String stream, NetconfSubscriber netconfSubscriber) {
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
}
