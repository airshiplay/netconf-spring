package com.airlenet.netconf.common;

import com.tailf.jnc.IOSubscriber;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.SessionClosedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by airlenet on 17/8/24.
 */
public class PlayNotification extends IOSubscriber {

    private static Logger logger = LoggerFactory.getLogger(PlayNotification.class);
    private PlayNetconfDevice playNetconfDevice;
    private List<PlayNetconfListener> listenerList;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    private static final String OnlineNotification = "<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><connect></connect></notification>";
    private static final String OfflineNotification = "<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><disconnect></disconnect></notification>";
    private String stream;
    private static Timer timer = new Timer();

    /**
     * Empty constructor. The rawmode, inb and outb fields will be unassigned.
     */
    public PlayNotification(PlayNetconfDevice playNetconfDevice) {
        super(false);
        this.playNetconfDevice = playNetconfDevice;
    }

    public PlayNotification(PlayNetconfDevice playNetconfDevice, String stream) {
        super(false);
        this.playNetconfDevice = playNetconfDevice;
        this.stream = stream;
    }

    public void addListenerList(PlayNetconfListener listener) {
        if (null == listenerList) {
            listenerList = new ArrayList<>();
        }
        if (listenerList.indexOf(listener) == -1) {
            listenerList.add(listener);
        }
    }


    public void resume() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    if(0 == playNetconfDevice.resumSubscription(PlayNotification.this.getStream())){
                        input(String.format(OnlineNotification, simpleDateFormat.format(new Date())));//恢复成功，发送connect
                    }//正常不需要恢复，不发送connect
                    cancel();//恢复成功，取消定时
                } catch (SessionClosedException e) {
                    logger.warn("device "+playNetconfDevice.getMgmt_ip(), e);
                    playNetconfDevice.closeSession(PlayNotification.this.getStream());//删除已关闭的session，等待重建
                    input(String.format(OfflineNotification, simpleDateFormat.format(new Date())));
                } catch (IOException e) {
                    logger.warn("device "+playNetconfDevice.getMgmt_ip(), e);
                    input(String.format(OfflineNotification, simpleDateFormat.format(new Date())));
                } catch (JNCException e) {
                    logger.warn("device "+playNetconfDevice.getMgmt_ip(), e);
                    input(String.format(OfflineNotification, simpleDateFormat.format(new Date())));
                } catch (Exception e) {
                    logger.warn("device "+playNetconfDevice.getMgmt_ip(), e);
                    input(String.format(OfflineNotification, simpleDateFormat.format(new Date())));
                }
            }
        }, 1000, 3 * 1000);
    }

    /**
     * Will get called as soon as we have input (data which is received).
     *
     * @param s Text being received
     */
    @Override
    public void input(String s) {
        logger.debug("receive from ip:" + this.playNetconfDevice.getMgmt_ip() + " stream:" + stream + " message:" + s);
        try {
            if (listenerList != null) {
                PlayNetconfListener[] toArray = listenerList.toArray(new PlayNetconfListener[0]);
                for (PlayNetconfListener listener : toArray) {
                    if (!listener.isRemove()) {
                        listener.receive(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
                    }
                }
            }
        } catch (Exception e) {

        }

    }

    /**
     * Will get called as soon as we have output (data which is being sent).
     *
     * @param s Text being sent
     */
    @Override
    public void output(String s) {
        logger.debug("send to ip:" + this.playNetconfDevice.getMgmt_ip() + " stream:" + stream + " message:" + s);
        try {
            if (listenerList != null) {
                PlayNetconfListener[] toArray = listenerList.toArray(new PlayNetconfListener[0]);
                for (PlayNetconfListener listener : toArray) {
                    if (!listener.isRemove()) {

                        listener.send(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
                    }
                }
            }
        } catch (Exception e) {

        }

    }


    public String getStream() {
        return stream;
    }
}
