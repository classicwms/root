package com.ustorage.api.trans.config;

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

	/**
	 * apiKey()
	 * @return
	 */
	private ApiKey apiKey() { 
	    return new ApiKey("JWT", "Authorization", "header"); 	
	}
	
	/**
	 * securityContext()
	 * @return
	 */
	private SecurityContext securityContext() { 
	    return SecurityContext.builder().securityReferences(defaultAuth()).build(); 
	} 

	/**
	 * defaultAuth()
	 * @return
	 */
	private List<SecurityReference> defaultAuth() { 
	    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything"); 
	    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1]; 
	    authorizationScopes[0] = authorizationScope; 
	    return Arrays.asList(new SecurityReference("JWT", authorizationScopes)); 
	}
	
	/**
	 * api()
	 * @return
	 */
	@Bean
	public Docket api() {
	    return new Docket(DocumentationType.SWAGGER_2)
	      .apiInfo(apiInfo())
	      .securityContexts(Arrays.asList(securityContext()))
	      .securitySchemes(Arrays.asList(apiKey()))
	      .select()
	      .apis(RequestHandlerSelectors.basePackage("com.ustorage.api.trans.controller"))
	      .paths(PathSelectors.any())
	      .build();
	    
	}

	/**
	 * apiInfo()
	 * @return
	 */
	private ApiInfo apiInfo() {
	    return new ApiInfo(
	      "UStorage - Transaction Services",
	      "Transaction Service Specifications",
	      "1.0",
	      "Terms of service",
	      new Contact("UStorage Team", "www.tekclover.com", "UStorage@tekclover.com"),
	      "License of API",
	      "API license URL",
	      Collections.emptyList());
	}
}
