package com.almailem.ams.api.connector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource("classpath:application-messages.properties")
//@PropertySource("classpath:application-messages.properties")
public class PropertiesConfig {

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

	//---------------------------almailem--------------------------------------------------------
	@Value("${almailem.company.code.primary}")
	private String almailemPrimaryCompanyCode;

	@Value("${api.enterprise.service.url}")
	private String almailemPrimaryPlant;

	@Value("${api.masters.service.url}")
	private String almailemSecondaryCompanyCode;

	@Value("${api.idmaster.service.url}")
	private String almailemSecondaryPlant;

	//----------------------------amghara-------------------------------------------------------
	@Value("${api.transaction.service.url}")
	private String amgharaPrimaryComanyCode;

	@Value("${api.enterprise.service.url}")
	private String amgharaPrimaryPlant;

	@Value("${api.masters.service.url}")
	private String amgharaSecondaryCompanyCode;

	@Value("${api.idmaster.service.url}")
	private String amgharaSecondaryPlant;

	//-----------------------------------------------------------------------------------
	@Value("${api.amghara.transaction.service.url}")
	private String amgharaTransactionServiceUrl;

	@Value("${api.amghara.masters.service.url}")
	private String amgharaMastersServiceUrl;

	//-----------------------------------------------------------------------------------
	@Value("${amghara.transaction.oauth.access_token_url}")
	private String amgharaTransactionAccessTokenUrl;

	@Value("${amghara.masters.oauth.access_token_url}")
	private String amgharaMastersAccessTokenUrl;

	//--------------------------------------------------------------------------------------

	@Value("${fahaheel.masters.oauth.access_token_url}")
	private String fhMastersAccessTokenUrl;

	@Value("${fahaheel.inboundorder.oauth.access_token_url}")
	private String fhInboundOrderAccessTokenUrl;

	@Value("${fahaheel.outboundorder.oauth.access_token_url}")
	private String fhOutboundOrderAccessTokenUrl;

	@Value("${api.fahaheel.inboundorder.service.url}")
	private String fhInboundOrderServiceUrl;

	@Value("${api.fahaheel.outboundorder.service.url}")
	private String fhOutboundOrderServiceUrl;

	@Value("${api.fahaheel.masters.service.url}")
	private String fhMastersServiceUrl;
}
