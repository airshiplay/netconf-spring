package com.airlenet.netconf.common;

import com.tailf.jnc.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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

    private PlayNetconfSession playNetconfSession;
    protected transient HashMap<String, PlayNetconfSession> connSessionMap = new HashMap<>();

    public PlayNetconfDevice(Long id, String remoteUser, String password, String mgmt_ip, int mgmt_port) {
        this.id = id;
        this.remoteUser = remoteUser;
        this.password = password;
        this.mgmt_ip = mgmt_ip;
        this.mgmt_port = mgmt_port;
    }

    public PlayNetconfSession getDefaultNetconfSession() throws IOException, JNCException {
        PlayNotification notification = null;
        if (device == null) {
            DeviceUser duser = new DeviceUser(this.remoteUser, this.remoteUser, this.password);
            device = new Device(this.mgmt_ip, duser, this.mgmt_ip, this.mgmt_port);
            device.connect(this.remoteUser);
            notification = new PlayNotification(this);
            device.newSession(notification, "defaultPlaySession");
            device.getSession("defaultPlaySession");
        } else {
            NetconfSession netconfSession = device.getSession("defaultPlaySession");
            if (netconfSession == null) {
                device.connect(this.remoteUser);
                notification = new PlayNotification(this);
                device.newSession(notification, "defaultPlaySession");
                device.getSession("defaultPlaySession");
            }
        }
        PlayNetconfSession defaultPlaySession = connSessionMap.get("defaultPlaySession");

        if (defaultPlaySession != null) {
            long newSessionId = device.getSession("defaultPlaySession").sessionId;
            long oldSessionId = defaultPlaySession.getSessionId();
            if (newSessionId != oldSessionId) {
                notification = new PlayNotification(this);
                defaultPlaySession = new PlayNetconfSession(this, device.getSession("defaultPlaySession"), notification, false);
            }
        } else {
            notification = new PlayNotification(this);
            defaultPlaySession = new PlayNetconfSession(this, device.getSession("defaultPlaySession"), notification, false);
        }
        connSessionMap.put("defaultPlaySession", defaultPlaySession);
        return defaultPlaySession;
    }

    /**
     * Netconf协议规定，一个netconf-session有且只有一个订阅，并且订阅消息一旦创建就不允许修改
     *
     * @param stream
     * @param listener
     * @throws IOException
     * @throws JNCException
     */
    public void createSubscription(String stream, PlayNetconfListener listener, boolean resume) throws IOException, JNCException {
        PlayNetconfSession streamSession = connSessionMap.get(stream);
        if (streamSession == null) {
            PlayNotification notification = new PlayNotification(this, stream);
            notification.addListenerList(listener);
            try {
                device.newSession(notification, stream);
            } catch (Exception e) {//device 断链，重新连接
                try {
                    closeNetconfSession(stream);
                } catch (Exception e1) {
                }
                device.connect(this.remoteUser);
                device.newSession(notification, stream);
            }
            NetconfSession netconfSession = device.getSession(stream);
            netconfSession.createSubscription(stream);
            connSessionMap.put(stream, new PlayNetconfSession(this, netconfSession, notification, resume));
        } else {
            streamSession.addNetconfSessionListenerList(listener);
        }
    }

    public void createSubscription(String stream, PlayNetconfListener listener) throws IOException, JNCException {
        createSubscription(stream, listener, true);
    }

    protected void resumSubscription(String stream) throws IOException, JNCException {
        PlayNetconfSession streamSession = connSessionMap.get(stream);
        if (streamSession != null) {
            PlayNotification notification = streamSession.getNotification();
            try {
                device.newSession(notification, stream);
            } catch (Exception e) {//device 断链，重新连接
                try {
                    device.closeSession(stream);
                } catch (Exception e1) {
                }
                device.connect(this.remoteUser);
                device.newSession(notification, stream);
            }
            NetconfSession netconfSession = device.getSession(stream);
            netconfSession.createSubscription(stream);
            streamSession.receiveNotification(netconfSession);
        }
    }

    public void closeDefaultNetconfSession() {
        connSessionMap.remove("defaultPlaySession");
        device.closeSession("defaultPlaySession");
    }

    public void closeNetconfSession(String stream) {
        connSessionMap.remove(stream);
        device.closeSession(stream);
    }

    public void close() {
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


    static private class SessionConnData {
        String sessionName;
        SSHSession sshSession;
        PlayNetconfSession session;
        PlayNotification notification;

        SessionConnData(String n, PlayNetconfSession s, PlayNotification noti) {
            sessionName = n;
            session = s;
            notification = noti;
        }
    }
}
