package com.ustorage.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@PropertySource("classpath:application-messages.properties")
public class PropertiesConfig {

	@Value("${ustorage.oauth.credentials.client_id}")
	private String clientId;
	
	@Value("${ustorage.oauth.credentials.client_secret_key}")
	private String clientSecretKey;
	
	@Value("${ustorage.oauth.grant_type}")
	private String grantType;
	
	@Value("${ustorage.oauth.grant_type.username}")
	private String username;
	
	@Value("${ustorage.oauth.grant_type.password}")
	private String password;
	
	//-----------------------------------------------------------------------------------

	@Value("${ustorage.oauth.access_token_url}")
	private String ustorageAccessTokenUrl;
	
	//-----------------------------------------------------------------------------------

	@Value("${trans.oauth.access_token_url}")
	private String transAccessTokenUrl;

	//-----------------------------------------------------------------------------------
	
	@Value("${api.trans.service.url}")
	private String transServiceUrl;
	
	//-----------------------------------------------------------------------------------

	@Value("${api.ustorage.service.url}")
	private String ustorageServiceUrl;

	//-----------------------------------------------------------------------------------

	@Value("${doc.storage.base.path}")
	private String docStorageBasePath;

	@Value("${doc.storage.agreement.path}")
	private String docStorageAgreementPath;

	@Value("${doc.storage.document.path}")
	private String docStorageDocumentPath;

	@Value("${doc.storage.template.path}")
	private String docStorageTemplatePath;

	@Value("${file.upload-dir}")
	private String fileUploadDir;

	@Value("${file.moveto-dir}")
	private String fileMoveToDir;

	@Value("${file.agreement}")
	private String agreementFile;

	@Value("${file.storageUnit}")
	private String storageUnitFile;

	@Value("${file.storeNumber}")
	private String storeNumberFile;

	//-----------------------------------------------------------------------------------


}
