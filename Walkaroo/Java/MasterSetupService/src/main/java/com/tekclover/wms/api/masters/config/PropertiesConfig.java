package com.tekclover.wms.api.masters.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource("classpath:application-messages.properties")
public class PropertiesConfig {

	@Value("${common.oauth.credentials.client_id}")
	private String clientId;
	
	@Value("${common.oauth.credentials.client_secret_key}")
	private String clientSecretKey;
	
	@Value("${common.oauth.grant_type}")
	private String grantType;
	
	@Value("${common.oauth.grant_type.username}")
	private String username;
	
	@Value("${common.oauth.grant_type.password}")
	private String password;
	
	//-----------------------------------------------------------------------------------
	
	@Value("${idmaster.oauth.access_token_url}")
	private String idmasterAccessTokenUrl;
	
	//-----------------------------------------------------------------------------------
	
	@Value("${api.idmaster.service.url}")
	private String idmasterServiceUrl;

	//--------------EMAIL-------------------------------------------------------------

	@Value("${email.from.address}")
	private String emailFromAddress;
}
