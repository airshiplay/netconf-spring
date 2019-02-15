package com.airlenet.netconf.common;

import com.tailf.jnc.Device;
import com.tailf.jnc.DeviceUser;
import com.tailf.jnc.JNCException;
import com.tailf.jnc.NetconfSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by airlenet on 17/8/24.
 */
public class PlayNetconfDevice {
    private static final Logger logger = LoggerFactory.getLogger(PlayNetconfDevice.class);
    private Long id;
    private String remoteUser;
    private String password;
    private String mgmt_ip;
    private String serialNumber;
    private int mgmt_port;
    private Device device;
    //  the timeout value to be used in milliseconds.
    private int connectTimeout = 0;
    //
    private int defaultReadTimeout = 0;
    private int subscriptionReadTimeout = 0;
    private Socket socket;
    /**
     * true service拦截，开启事物处理；false 自己开启事物处理
     */
    private boolean openTransaction;

    protected transient HashMap<String, PlayNetconfSession> connSessionMap = new HashMap<>();

    public PlayNetconfDevice(Long id, String mgmt_ip, int mgmt_port, String remoteUser, String password) {
        this.id = id;
        this.remoteUser = remoteUser;
        this.password = password;
        this.mgmt_ip = mgmt_ip;
        this.mgmt_port = mgmt_port;
    }

    public PlayNetconfDevice(Long id, String remoteUser, String password, String mgmt_ip, int mgmt_port) {
        this.id = id;
        this.remoteUser = remoteUser;
        this.password = password;
        this.mgmt_ip = mgmt_ip;
        this.mgmt_port = mgmt_port;
    }

    public PlayNetconfDevice(Long id, String serialNumber, Device device) {
        this.id = id;
        this.device = device;
        this.serialNumber = serialNumber;
    }

    public Device getDevice() {
        return this.device;
    }

    public void updateUsernamePassword(String username, String password) {
        if (username.equals(this.remoteUser) && password.equals(this.password)) {

        } else if (device != null) {
            device.close();
            this.remoteUser = username;
            this.password = password;
            device = null;
        }
    }

    /**
     * @param connectTimeout milliseconds.
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @param defaultReadTimeout milliseconds.
     */
    public void setDefaultReadTimeout(int defaultReadTimeout) {
        this.defaultReadTimeout = defaultReadTimeout;
    }

    public void setSubscriptionReadTimeout(int subscriptionReadTimeout) {
        this.subscriptionReadTimeout = subscriptionReadTimeout;
    }

    public synchronized PlayNetconfSession getDefaultNetconfSession() throws IOException, JNCException {
        PlayNotification notification = null;

        if (device == null) {
            DeviceUser duser = new DeviceUser(this.remoteUser, this.remoteUser, this.password);
            device = new Device(this.mgmt_ip, duser, this.mgmt_ip, this.mgmt_port);
            device.setDefaultReadTimeout(defaultReadTimeout);
            logger.debug("connect " + mgmt_ip);
            device.connect(this.remoteUser, null, connectTimeout);
            notification = new PlayNotification(this);
            logger.debug("new Session defaultPlaySession " + mgmt_ip);
            device.newSession(notification, "defaultPlaySession");
            device.getSession("defaultPlaySession");

        } else {
            NetconfSession netconfSession = device.getSession("defaultPlaySession");
            if (netconfSession == null) {
                try {
                    logger.debug("new Session defaultPlaySession device " + mgmt_ip);
                    device.newSession(notification, "defaultPlaySession");
                    device.getSession("defaultPlaySession");
                } catch (Exception e) {
                    try {
                        logger.debug("close for connect device " + mgmt_ip);
                        device.close();
                    } catch (Exception e1) {
                    }
                    logger.debug("connect device " + mgmt_ip);
                    device.connect(this.remoteUser, null, connectTimeout);
                    notification = new PlayNotification(this);
                    logger.debug("new Session defaultPlaySession device " + mgmt_ip);
                    device.newSession(notification, "defaultPlaySession");
                    device.getSession("defaultPlaySession");
                }
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
    public synchronized void createSubscription(String stream, String eventFilter,
                                                String startTime, String stopTime, PlayNetconfListener listener, boolean resume) throws IOException, JNCException {
        PlayNetconfSession streamSession = connSessionMap.get(stream);
        if (streamSession == null) {
            PlayNotification notification = new PlayNotification(this, stream);
            notification.addListenerList(listener);
            try {
                if (device == null) {
                    DeviceUser duser = new DeviceUser(this.remoteUser, this.remoteUser, this.password);
                    device = new Device(this.mgmt_ip, duser, this.mgmt_ip, this.mgmt_port);
                    device.setDefaultReadTimeout(defaultReadTimeout);
                    logger.debug("subscription connect device " + mgmt_ip);
                    device.connect(this.remoteUser, null, connectTimeout);
                }
                logger.debug("subscription new session " + stream + " device " + mgmt_ip);
                device.newSession(notification, stream);
                device.setReadTimeout(stream, subscriptionReadTimeout);
            } catch (Exception e) {//device 断链，重新连接
                try {
                    logger.debug("subscription close device " + mgmt_ip + e.getMessage());
                    device.close();
                } catch (Exception e1) {
                }
                logger.debug("subscription connect device " + mgmt_ip);
                device.connect(this.remoteUser, null, connectTimeout);
                logger.debug("subscription new session " + stream + " device " + mgmt_ip);
                device.newSession(notification, stream);
                device.setReadTimeout(stream, subscriptionReadTimeout);
            }
            NetconfSession netconfSession = device.getSession(stream);
            netconfSession.createSubscription(stream, eventFilter, startTime, stopTime);
            logger.debug("subscription createSubscription " + stream + " device " + mgmt_ip);
            connSessionMap.put(stream, new PlayNetconfSession(this, netconfSession, notification, resume));
        } else {
            streamSession.addNetconfSessionListenerList(listener);
        }
    }

    public synchronized void createSubscription(String stream, PlayNetconfListener listener, boolean resume) throws IOException, JNCException {
        createSubscription(stream, null, null, null, listener, resume);
    }

    public synchronized void createSubscription(String stream, PlayNetconfListener listener) throws IOException, JNCException {
        createSubscription(stream, listener, true);
    }

    protected synchronized int resumSubscription(String stream) throws IOException, JNCException {
        PlayNetconfSession streamSession = connSessionMap.get(stream);
        if (streamSession != null) {
            PlayNotification notification = streamSession.getNotification();
            try {
                NetconfSession netconfSession = device.getSession(stream);
                if (netconfSession == null) {
                    device.newSession(notification, stream);
                }
            } catch (Exception e) {//device 断链，重新连接
                try {
                    logger.debug("resumSubscription close device " + mgmt_ip);
                    device.close();
                } catch (Exception e1) {
                }
                logger.debug("resumSubscription connect device " + mgmt_ip);
                device.connect(this.remoteUser, null, connectTimeout);
                logger.debug("resumSubscription new session " + stream + " device " + mgmt_ip);
                device.newSession(notification, stream);
//                device.setReadTimeout(stream, subscriptionReadTimeout);
            }
            NetconfSession netconfSession = device.getSession(stream);
            netconfSession.createSubscription(stream);
            streamSession.receiveNotification(netconfSession);
            logger.debug("resumSubscription receiveNotification " + stream + " device " + mgmt_ip);
            return 0;
        } else {
            logger.debug("resumSubscription not exists " + stream + " device " + mgmt_ip);
            return -1;
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

    public void closeSession(String stream) {
        device.closeSession(stream);
    }

    public void close() {
        logger.debug("close device" + mgmt_ip);
        connSessionMap.clear();
        device.close();
    }

    public void closeConnection() {
        logger.debug("closeConnection device" + mgmt_ip);
        connSessionMap.clear();
        device.closeConnection();
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
