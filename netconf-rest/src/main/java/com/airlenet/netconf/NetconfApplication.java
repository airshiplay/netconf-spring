package com.airlenet.netconf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.airlenet")
public class NetconfApplication {
    public static void main(String args[]) {
        SpringApplication.run(NetconfApplication.class, args);
    }
}
