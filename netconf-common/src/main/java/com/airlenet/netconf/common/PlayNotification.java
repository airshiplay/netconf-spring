package com.airlenet.netconf.common;

import com.tailf.jnc.IOSubscriber;
import com.tailf.jnc.JNCException;
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
    private static final String OnlineNotification="<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><connect></connect></notification>";
    private static final String OfflineNotification="<notification xmlns=\"urn:ietf:params:xml:ns:netconf:notification:1.0\"><eventTime>%s</eventTime><disconnect></disconnect></notification>";
    private String stream;
    private static Timer timer = new Timer();
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
        if(listenerList.indexOf(listener)==-1){
            listenerList.add(listener);
        }
    }


    public void resume(){
        input(String.format(OfflineNotification,simpleDateFormat.format(new Date())));
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    playNetconfDevice.resumSubscription(PlayNotification.this.getStream());
                    input(String.format(OnlineNotification,simpleDateFormat.format(new Date())));
                } catch (IOException e) {
                    logger.error("",e);
                    resume();
                } catch (JNCException e) {
                    logger.error("",e);
                    resume();
                } catch (Exception e) {
                    logger.error("",e);
                    resume();
                }
            }
        },2*1000);
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
            Iterator<PlayNetconfListener> iterator = listenerList.iterator();
            while (iterator.hasNext()){
                PlayNetconfListener listener=iterator.next();
                if(listener.isRemove()){
                    iterator.remove();
                }else {
                    listener.receive(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
                }
            }
        }

        logger.debug("receive from ip:"+ this.playNetconfDevice.getMgmt_ip()+" message:"+s);
    }

    /**
     * Will get called as soon as we have output (data which is being sent).
     *
     * @param s Text being sent
     */
    @Override
    public void output(String s) {
        if (listenerList != null) {
            Iterator<PlayNetconfListener> iterator = listenerList.iterator();
            while (iterator.hasNext()){
                PlayNetconfListener listener=iterator.next();
                if(listener.isRemove()){
                    iterator.remove();
                }else {
                    listener.send(this.playNetconfDevice.getId(), this.playNetconfDevice.getMgmt_ip(), s);
                }
            }
        }
        logger.debug("send to ip:"+ this.playNetconfDevice.getMgmt_ip()+" message:"+s);
    }


    public String getStream() {
        return stream;
    }
}
