package com.mnrclara.spark.core.config;

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

//	private ApiKey apiKey() {
//	    return new ApiKey("JWT", "Authorization", "header");
//	}
//
//	private SecurityContext securityContext() {
//	    return SecurityContext.builder().securityReferences(defaultAuth()).build();
//	}
//
//	private List<SecurityReference> defaultAuth() {
//	    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//	    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//	    authorizationScopes[0] = authorizationScope;
//	    return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
//	}
//
//	@Bean
//	public Docket api() {
//	    return new Docket(DocumentationType.SWAGGER_2)
//	      .apiInfo(apiInfo())
////	      .securityContexts(Arrays.asList(securityContext()))
////	      .securitySchemes(Arrays.asList(apiKey()))
//	      .select()
////	      .apis(RequestHandlerSelectors.any())
////	      .paths(PathSelectors.any())
//	      .apis(RequestHandlerSelectors.basePackage("com.tekclover.wms.core.controller"))
////	      .paths(PathSelectors.ant("/user/*"))
//	      .paths(PathSelectors.any())
//	      .build();
//
//	}
//
//	private ApiInfo apiInfo() {
//	    return new ApiInfo(
//	      "My REST API",
//	      "Some custom description of API.",
//	      "1.0",
//	      "Terms of service",
//	      new Contact("Sallo Szrajbman", "www.baeldung.com", "salloszraj@gmail.com"),
//	      "License of API",
//	      "API license URL",
//	      Collections.emptyList());
//	}

	/**
	 * apiKey()
	 * @return
	 */
//	private ApiKey apiKey() {
//		return new ApiKey("JWT", "Authorization", "header");
//	}

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
//				.securitySchemes(Arrays.asList(apiKey()))
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.mnrclara.spark.core.controller"))
				.paths(PathSelectors.any())
				.build();

	}

//		@Bean
//		public Docket api() {
//			return new Docket(DocumentationType.SWAGGER_2)
//					.select()
//					.apis(RequestHandlerSelectors.basePackage("com.mnrclara.spark.core.controller"))
//					.build();
//		}


	/**
	 * apiInfo()
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfo (
				"M&R Clara Services - Spark Service",
				"Spark Service API",
				"1.0",
				"Terms of service",
				new Contact("M&R Clara", "www.mnrclara.com", "admin@mnrclara.com"),
				"License of API",
				"API license URL",
				Collections.emptyList());
	}
}