package com.ustorage.core.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
	    return new Docket(DocumentationType.SWAGGER_2)
	      .apiInfo(apiInfo())
	      .select()
	      .apis(RequestHandlerSelectors.basePackage("com.ustorage.core.controller"))
	      .paths(PathSelectors.any())
	      .build();
	}

	private ApiInfo apiInfo() {
	    return new ApiInfo (
	      "UStorage Wrapper Services",
	      "UStorage Service API",
	      "1.0",
	      "Terms of service",
	      new Contact("UStorage", "www.ustorage.com", "admin@ustorage.com"),
	      "License of API",
	      "API license URL",
	      Collections.emptyList());
	}
}
