package com.tekclover.wms.core.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
	    return new Docket(DocumentationType.SWAGGER_2)
	      .apiInfo(apiInfo())
//	      .securityContexts(Arrays.asList(securityContext()))
//	      .securitySchemes(Arrays.asList(apiKey()))
	      .select()
//	      .apis(RequestHandlerSelectors.any())
//	      .paths(PathSelectors.any())
	      .apis(RequestHandlerSelectors.basePackage("com.tekclover.wms.core.controller"))
//	      .paths(PathSelectors.ant("/user/*"))
	      .paths(PathSelectors.any())
	      .build();
	    
	}

	private ApiInfo apiInfo() {
	    return new ApiInfo (
	      "Classic WMS Services - Walkaroo PRODUCTION",
	      "Classic WMS Service API - Walkaroo Production",
	      "1.0",
	      "Terms of service",
	      new Contact("WMS", "www.tekclover.com", "admin@tekclover.com"),
	      "License of API",
	      "API license URL",
	      Collections.emptyList());
	}
}