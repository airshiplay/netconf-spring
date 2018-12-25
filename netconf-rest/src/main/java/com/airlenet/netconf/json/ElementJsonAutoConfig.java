package com.airlenet.netconf.json;

import com.airlenet.data.jpa.json.JpaModule;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElementJsonAutoConfig implements InitializingBean {

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public ElementModule elementModule() {
        return new ElementModule();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        objectMapper.registerModule(elementModule());
    }

}

