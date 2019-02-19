package com.airlenet.netconf.spring.autoconfig.stat;

import com.airlenet.netconf.datasource.support.spring.NetconfStatInterceptor;
import com.airlenet.netconf.spring.autoconfig.NetconfProperties;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty({"spring.netconf.aop-patterns"})
public class NetconfSpringAopConfiguration {

    @Bean
    public Advice advice() {
        return new NetconfStatInterceptor();
    }

    @Bean
    public Advisor advisor(NetconfProperties properties) {
        return new RegexpMethodPointcutAdvisor(properties.getAopPatterns(), this.advice());
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
}
