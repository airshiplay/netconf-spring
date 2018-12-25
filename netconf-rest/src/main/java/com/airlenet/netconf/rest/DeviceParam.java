package com.airlenet.netconf.rest;


import io.swagger.annotations.ApiModelProperty;

public class DeviceParam {

    @ApiModelProperty(value = "Device Id")
    private Long id;

    @ApiModelProperty(value = "Device Netconf IP")
    private String ip;

    @ApiModelProperty(value = "Device Netconf port")
    private int port;

    @ApiModelProperty(value = "Device Netconf Account Username")
    private String user;

    @ApiModelProperty(value = "Device Netconf Account Password")
    private String pass;
    private String sn;

    public DeviceParam() {
    }

    public DeviceParam(Long id, String ip, int port, String user, String pass) {
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
