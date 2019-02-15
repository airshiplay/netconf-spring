package com.airlenet.netconf.spring.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.netconf")
public class NetconfProperties {

    private static final int ReadTimeOut = 0;
    private int readTimeout = ReadTimeOut;
    private int connectTimeout = 0;
    private int subscriptionReadTimeout = 0;

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSubscriptionReadTimeout() {
        return subscriptionReadTimeout;
    }

    public void setSubscriptionReadTimeout(int subscriptionReadTimeout) {
        this.subscriptionReadTimeout = subscriptionReadTimeout;
    }
}
