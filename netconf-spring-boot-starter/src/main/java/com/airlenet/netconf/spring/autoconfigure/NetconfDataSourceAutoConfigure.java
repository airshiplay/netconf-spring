package com.airlenet.netconf.spring.autoconfigure;

import com.airlenet.netconf.datasource.MultiNetconfDataSource;
import com.airlenet.netconf.datasource.NetconfDataSource;
import com.airlenet.netconf.spring.NetconfClient;
import com.airlenet.netconf.spring.NetconfClientInvocationHandler;
import com.airlenet.netconf.spring.autoconfigure.stat.NetconfSpringAopConfiguration;
import com.airlenet.netconf.spring.autoconfigure.stat.NetconfStatViewServletConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.reflect.Proxy;

@Configuration
@ConditionalOnClass({MultiNetconfDataSource.class, NetconfDataSource.class})
@EnableConfigurationProperties(NetconfProperties.class)
@Import({NetconfSpringAopConfiguration.class,NetconfStatViewServletConfiguration.class})
public class NetconfDataSourceAutoConfigure {
    @Autowired
    private NetconfProperties netconfProperties;

    @Bean
    @ConditionalOnMissingBean
    public MultiNetconfDataSource multiNetconfDataSource() {
        MultiNetconfDataSource multiDataSource = new MultiNetconfDataSource();
        multiDataSource.setReadTimeout(netconfProperties.getReadTimeout());
        multiDataSource.setConnectionTimeout(netconfProperties.getConnectionTimeout());
        multiDataSource.setKexTimeout(netconfProperties.getKexTimeout());
        multiDataSource.setMaxPoolSize(netconfProperties.getMaxPoolSize());
        return multiDataSource;
    }

    @Bean
    public NetconfClientInvocationHandler netconfClientInvocationHandler() {
        return new NetconfClientInvocationHandler(multiNetconfDataSource());
    }

    @Bean
    @ConditionalOnMissingBean
    public NetconfClient netconfClient() {
        return (NetconfClient) Proxy.newProxyInstance(NetconfClient.class.getClassLoader(), new Class[]{NetconfClient.class}, netconfClientInvocationHandler());
    }
}
