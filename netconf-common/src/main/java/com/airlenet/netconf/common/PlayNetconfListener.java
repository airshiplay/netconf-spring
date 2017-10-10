package com.airlenet.netconf.common;

/**
 * @author airlenet
 * @version 2017-10-10
 */
public interface PlayNetconfListener {
    public void receive(Long id,String ip,String msg);
    public void send(Long id,String ip,String msg);
}
