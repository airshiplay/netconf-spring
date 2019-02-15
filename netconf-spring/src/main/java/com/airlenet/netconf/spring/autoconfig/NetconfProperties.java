package com.airlenet.netconf.spring.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.netconf")
public class NetconfProperties {

    private static final int ReadTimeOut = 0;
    private int readTimeout = ReadTimeOut;


    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
}
