package com.airlenet.netconf.common;

import com.tailf.jnc.IOSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by airlenet on 17/8/24.
 */
public class PlayNotification extends IOSubscriber {

    private static Logger logger = LoggerFactory.getLogger(PlayNotification.class);
    private PlayNetconfDevice playNetconfDevice;
    private List<PlayNetconfListener> listenerList;
    private String stream;
    /**
     * Empty constructor. The rawmode, inb and outb fields will be unassigned.
     */
    public PlayNotification(PlayNetconfDevice playNetconfDevice) {
        super(false);
        this.playNetconfDevice = playNetconfDevice;
    }

    public PlayNotification(PlayNetconfDevice playNetconfDevice,String stream) {
        super(false);
        this.playNetconfDevice = playNetconfDevice;
        this.stream = stream;
    }

    public void addListenerList(PlayNetconfListener listener) {
        if (null == listenerList) {
            listenerList = new ArrayList<>();
        }
        listenerList.add(listener);
    }

    public void removeListenerList(PlayNetconfListener listener) {
        this.listenerList.remove(listener);
    }

    /**
     * Will get called as soon as we have input (data which is received).
     *
     * @param s Text being received
     */
    @Override
    public void input(String s) {
        if (listenerList != null) {
            for (PlayNetconfListener listener : listenerList) {
                listener.receive(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
            }
        }

        logger.info("receive from ip:"+ this.playNetconfDevice.getMgmt_ip()+" message:"+s);
    }

    /**
     * Will get called as soon as we have output (data which is being sent).
     *
     * @param s Text being sent
     */
    @Override
    public void output(String s) {
        if (listenerList != null) {
            for (PlayNetconfListener listener : listenerList) {
                listener.send(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
            }
        }

        logger.info("send to ip:"+ this.playNetconfDevice.getMgmt_ip()+" message:"+s);
    }


    public String getStream() {
        return stream;
    }
}
