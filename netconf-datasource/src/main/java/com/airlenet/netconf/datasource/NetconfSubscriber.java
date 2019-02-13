package com.airlenet.netconf.datasource;

import com.tailf.jnc.IOSubscriber;

public interface NetconfSubscriber {

    public void input(String url, String s);

    public void output(String url, String s);
}
