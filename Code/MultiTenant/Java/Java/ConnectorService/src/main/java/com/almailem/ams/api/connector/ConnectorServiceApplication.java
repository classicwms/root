package com.almailem.ams.api.connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableResourceServer
@EnableAuthorizationServer
@EnableSwagger2
@EnableScheduling
public class ConnectorServiceApplication {

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("GMT+3"));
        SpringApplication.run(ConnectorServiceApplication.class, args);
    }
}