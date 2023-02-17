package com.iweb2b.core.config;

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
	      .apis(RequestHandlerSelectors.basePackage("com.iweb2b.core.controller"))
	      .paths(PathSelectors.any())
	      .build();
	}

	private ApiInfo apiInfo() {
	    return new ApiInfo (
	      "IWEB2B Gateway Services",
	      "IWEB2B Gateway Service API",
	      "1.0",
	      "Terms of service",
	      new Contact("WMS", "www.tekclover.com", "admin@tekclover.com"),
	      "License of API",
	      "API license URL",
	      Collections.emptyList());
	}
}
