package com.airlenet.netconf.datasource.support.spring;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetconfStatInterceptor implements MethodInterceptor, InitializingBean, DisposableBean {
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return null;
    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
