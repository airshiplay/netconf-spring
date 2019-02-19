package com.airlenet.netconf.spring.autoconfig;

import com.airlenet.netconf.datasource.NetconfMultiDataSource;
import com.airlenet.netconf.spring.NetconfClient;
import com.airlenet.netconf.spring.NetconfClientInvocationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;

@Configuration
@EnableConfigurationProperties(NetconfProperties.class)
public class NetconfAutoConfiguration {
    @Autowired
    private NetconfProperties netconfProperties;

    @Bean
    @ConditionalOnMissingBean
    public NetconfMultiDataSource netconfMultiDataSource() {
        NetconfMultiDataSource multiDataSource = new NetconfMultiDataSource();
        multiDataSource.setReadTimeout(netconfProperties.getReadTimeout());
        multiDataSource.setConnectionTimeout(netconfProperties.getConnectionTimeout());
        multiDataSource.setMaxPoolSize(netconfProperties.getMaxPoolSize());
        return multiDataSource;
    }

    @Bean
    public NetconfClientInvocationHandler netconfClientInvocationHandler() {
        return new NetconfClientInvocationHandler(netconfMultiDataSource());
    }

    @Bean
    @ConditionalOnMissingBean
    public NetconfClient netconfClient() {
        return (NetconfClient) Proxy.newProxyInstance(NetconfClient.class.getClassLoader(), new Class[]{NetconfClient.class}, netconfClientInvocationHandler());
    }
}
