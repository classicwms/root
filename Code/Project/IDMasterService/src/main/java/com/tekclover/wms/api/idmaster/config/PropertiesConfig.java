package com.tekclover.wms.api.idmaster.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application-messages.properties")
public class PropertiesConfig {

	/*-----------------Document Upload------------------------*/

	@Value("${doc.storage.base.path}")
	private String docStorageBasePath;
	@Value("${doc.storage.document.path}")
	private String docStorageDocumentPath;

//--------------EMAIL-------------------------------------------------------------

	@Value("${email.from.address}")
	private String emailFromAddress;

	@Value("${email.subject}")
	private String emailSubject;

	/*---------------------------IW Logo ------------------------------------*/

	@Value("${logo.path}")
	private String logo;

	/*----------------------------OAuth-----------------------*/

	@Value("${idmaster.oauth.credentials.client_id}")
	private String clientId;

	@Value("${idmaster.oauth.credentials.client_secret_key}")
	private String clientSecretKey;

	@Value("${idmaster.oauth.grant_type}")
	private String grantType;

	@Value("${idmaster.oauth.grant_type.username}")
	private String username;

	@Value("${idmaster.oauth.grant_type.password}")
	private String password;

	//-----------------------------------------------------------------------------------
	@Value("${transaction.oauth.access_token_url}")
	private String transactionAccessTokenUrl;

	@Value("${enterprise.oauth.access_token_url}")
	private String enterpriseAccessTokenUrl;

	@Value("${masters.oauth.access_token_url}")
	private String mastersAccessTokenUrl;

	@Value("${idmaster.oauth.access_token_url}")
	private String idmasterAccessTokenUrl;

	//-----------------------------------------------------------------------------------
	@Value("${api.transaction.service.url}")
	private String transactionServiceUrl;

	@Value("${api.enterprise.service.url}")
	private String enterpriseServiceUrl;

	@Value("${api.masters.service.url}")
	private String mastersServiceUrl;

	@Value("${api.idmaster.service.url}")
	private String idmasterServiceUrl;
	
}
