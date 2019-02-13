package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;

public class JNCSubscriber extends IOSubscriber {
    private final NetconfSubscriber netconfSubscriber;
    private final String url;

    public JNCSubscriber(boolean rawmode, String url,
                         String stream, NetconfSubscriber netconfSubscriber) {
        super(rawmode);
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    public JNCSubscriber(String url, String stream, NetconfSubscriber netconfSubscriber) {
        this.netconfSubscriber = netconfSubscriber;
        this.url = url;
    }

    @Override
    public void input(String s) {
        netconfSubscriber.input(this.url, s);
    }

    @Override
    public void output(String s) {
        netconfSubscriber.output(this.url, s);
    }
}
