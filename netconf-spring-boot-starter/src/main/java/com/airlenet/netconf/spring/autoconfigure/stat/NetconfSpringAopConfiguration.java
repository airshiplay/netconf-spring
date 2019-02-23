package com.airlenet.netconf.spring.autoconfigure.stat;

import com.airlenet.netconf.datasource.support.spring.NetconfStatInterceptor;
import com.airlenet.netconf.spring.autoconfigure.NetconfProperties;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty({"spring.netconf.aop-patterns"})
public class NetconfSpringAopConfiguration {

    @Bean
    public Advice netconfAdvice() {
        return new NetconfStatInterceptor();
    }

    @Bean
    public Advisor netcofAdvisor(NetconfProperties properties) {
        return new RegexpMethodPointcutAdvisor(properties.getAopPatterns(), this.netconfAdvice());
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator netconfAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }
}
