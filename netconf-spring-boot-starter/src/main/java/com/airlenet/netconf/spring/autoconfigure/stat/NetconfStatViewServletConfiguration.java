package com.airlenet.netconf.spring.autoconfigure.stat;

import com.airlenet.netconf.spring.autoconfigure.NetconfProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@ConditionalOnWebApplication
@ConditionalOnProperty(name = "spring.netconf.stat-view-servlet.enabled", havingValue = "true", matchIfMissing = true)
public class NetconfStatViewServletConfiguration {
    @Autowired
    NetconfProperties netconfProperties;

    @Bean
    public ServletRegistrationBean netconfStatViewServletRegistrationBean() {
        NetconfProperties.StatViewServlet config = netconfProperties.getStatViewServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean();
        registrationBean.setName("netconfStatViewServlet");
        registrationBean.setServlet(new com.airlenet.netconf.datasource.support.http.StatViewServlet());
        registrationBean.addUrlMappings(new String[]{config.getUrlPattern() != null ? config.getUrlPattern() : "/netconfStat/*"});
        if (config.getAllow() != null) {
            registrationBean.addInitParameter("allow", config.getAllow());
        }

        if (config.getDeny() != null) {
            registrationBean.addInitParameter("deny", config.getDeny());
        }

        if (config.getLoginUsername() != null) {
            registrationBean.addInitParameter("loginUsername", config.getLoginUsername());
        }

        if (config.getLoginPassword() != null) {
            registrationBean.addInitParameter("loginPassword", config.getLoginPassword());
        }

        return registrationBean;
    }
}
