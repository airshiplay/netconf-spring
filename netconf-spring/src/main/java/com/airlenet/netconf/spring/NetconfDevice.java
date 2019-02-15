package com.airlenet.netconf.spring;

public class NetconfDevice {
    private String url;
    private String host;
    private int port;
    private String username;
    private String password;

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
}
