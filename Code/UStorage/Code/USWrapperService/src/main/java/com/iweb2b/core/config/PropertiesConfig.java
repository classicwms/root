package com.iweb2b.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource("classpath:application-messages.properties")
public class PropertiesConfig {

	@Value("${b2bmaster.oauth.credentials.client_id}")
	private String clientId;
	
	@Value("${b2bmaster.oauth.credentials.client_secret_key}")
	private String clientSecretKey;
	
	@Value("${b2bmaster.oauth.grant_type}")
	private String grantType;
	
	@Value("${b2bmaster.oauth.grant_type.username}")
	private String username;
	
	@Value("${b2bmaster.oauth.grant_type.password}")
	private String password;
	
	//-----------------------------------------------------------------------------------
	@Value("${b2bmaster.oauth.access_token_url}")
	private String b2bmasterAccessTokenUrl;
	
	@Value("${b2bintegration.oauth.access_token_url}")
	private String b2bintegrationAccessTokenUrl;
	
	//-----------------------------------------------------------------------------------
	@Value("${api.b2bmaster.service.url}")
	private String b2bmasterServiceUrl;
	
	@Value("${api.b2bintegration.service.url}")
	private String b2bintegrationServiceUrl;

	//-----------------------------------------------------------------------------------
	// file.upload.location
	@Value("${file.upload.location}")
	private String fileUploadLocation;

	//-----------------------------------------------------------------------------------
}
