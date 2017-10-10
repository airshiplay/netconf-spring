package com.airlenet.netconf.common;

import com.tailf.jnc.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by airlenet on 17/8/24.
 */
public class PlayNetconfDevice {

    private Long id;
    private String remoteUser;
    private String password;
    private String mgmt_ip;
    private int mgmt_port;
    private Device device;
    /**
     * true service拦截，开启事物处理；false 自己开启事物处理
     */
    private boolean openTransaction;

    private  PlayNetconfSession playNetconfSession;
    public PlayNetconfDevice(Long id,String remoteUser, String password, String mgmt_ip, int mgmt_port) {
        this.id = id;
        this.remoteUser = remoteUser;
        this.password = password;
        this.mgmt_ip = mgmt_ip;
        this.mgmt_port = mgmt_port;
    }

    public PlayNetconfSession getDefaultNetconfSession() throws IOException, JNCException {
        PlayNotification notification =null;

        if(device==null){
            DeviceUser duser = new DeviceUser(this.remoteUser, this.remoteUser, this.password);
            device = new Device(this.mgmt_ip, duser, this.mgmt_ip, this.mgmt_port);
            device.connect(this.remoteUser);
            notification = new PlayNotification(this);
            device.newSession(notification,"defaultPlaySession");
            device.getSession("defaultPlaySession").createSubscription("alarm");
        }else{
            NetconfSession netconfSession = device.getSession("defaultPlaySession");
            if(netconfSession==null){
                device.connect(this.remoteUser);
                notification = new PlayNotification(this);
                device.newSession(notification,"defaultPlaySession");
                device.getSession("defaultPlaySession").createSubscription("alarm");
            }
        }
        if(playNetconfSession!=null){
           long newSessionId= device.getSession("defaultPlaySession").sessionId;
            long oldSessionId=  playNetconfSession.getSessionId();
            if(newSessionId!=oldSessionId){
                playNetconfSession = new PlayNetconfSession(this, device.getSession("defaultPlaySession"),notification);
            }
        }else{
            playNetconfSession = new PlayNetconfSession(this, device.getSession("defaultPlaySession"),notification);
        }
       return playNetconfSession;
    }

    public void closeDefaultNetconfSession() {
        device.closeSession("defaultPlaySession");
    }

    public void close(){
        device.close();
    }

    public Long getId() {
        return id;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public String getPassword() {
        return password;
    }

    public String getMgmt_ip() {
        return mgmt_ip;
    }

    public int getMgmt_port() {
        return mgmt_port;
    }

    public boolean isOpenTransaction() {
        return openTransaction;
    }

    public void setOpenTransaction(boolean openTransaction) {
        this.openTransaction = openTransaction;
    }


}
