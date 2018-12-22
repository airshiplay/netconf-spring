package com.airlenet.netconf.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author airlenet
 * @version 2017-10-10
 */
public interface PlayNetconfListener {
    Map<Object,Boolean> thisMoveMap = new HashMap<>();
    public void receive(Long id,String stream,String ip,String msg);
    public void send(Long id,String stream,String ip,String msg);
    public default void removeSelf(){
        thisMoveMap.put(this,true);
    }
    public default boolean isRemove(){
        Boolean remove = thisMoveMap.remove(this);
        return remove ==null?false:remove;
    }
}
