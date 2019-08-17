package com.airlenet.netconf.datasource;

import java.util.HashMap;
import java.util.Map;

public class NetconfDevice {
    private String url;
    private String host;
    private int port;
    private String username;
    private String password;
    private String zoneId;
    private Map<String, Object> extraInfo;

    /**
     * @param url      netconf://127.0.0.1:2022
     * @param username admin
     * @param password admin
     */
    public NetconfDevice(String url, String username, String password) {
        this.url = url;
        this.host = url.substring(10, url.lastIndexOf(":"));
        this.port = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.length()));
        this.username = username;
        this.password = password;
    }

    public NetconfDevice(String url, String username, String password, String zoneId) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.zoneId = zoneId;
    }

    /**
     * @param host     172.0.0.1
     * @param port     2022
     * @param username admin
     * @param password admin
     */
    public NetconfDevice(String host, int port, String username, String password) {
        this.url = "netconf://" + host + ":" + port;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public NetconfDevice(String host, int port, String username, String password, String zoneId) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.zoneId = zoneId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void addExtraInfo(String key, Object o) {
        if (this.extraInfo == null) {
            this.extraInfo = new HashMap<>();
        }
        this.extraInfo.put(key, o);
    }

    public Object getExtraInfo(String key) {
        if (this.extraInfo == null) {
            return null;
        }
        return this.extraInfo.get(key);
    }

    public void removeExtraInfo(String key) {
        if (this.extraInfo == null) {
            this.extraInfo = new HashMap<>();
        }
        this.extraInfo.remove(key);
    }
}
