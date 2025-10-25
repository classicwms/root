package com.almailem.ams.api.connector.service;

import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.auth.AuthTokenRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthTokenService {

	@Autowired
	PropertiesConfig propertiesConfig;

	/**
	 * Returns RestTemplate Object
	 * @return
	 */
	public RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();

		// Object Convertor
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter
				.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
		restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);
		return restTemplate;
	}

	/**
	 * 
	 * @param apiUrl
	 * @param clientId
	 * @param clientSecretKey
	 * @param grantType
	 * @param oauthUserName
	 * @param oauthPassword
	 * @return
	 */
	private AuthToken generateOAuthToken(String apiUrl, String clientId, String clientSecretKey, String grantType,
			String oauthUserName, String oauthPassword) {
		// Client Id and Client Secret Key to be sent as part of header for
		// authentication
		String credentials = clientId + ":" + clientSecretKey;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);
		String accessTokenUrl = apiUrl;

		// AuthToken URL dynamically
		if (apiUrl.equalsIgnoreCase("wms-masters-service")) {
			accessTokenUrl = propertiesConfig.getMastersAccessTokenUrl();
		} else if (apiUrl.equalsIgnoreCase("wms-idmaster-service")) {
			accessTokenUrl = propertiesConfig.getIdmasterAccessTokenUrl();
		} else if (apiUrl.equalsIgnoreCase("wms-transaction-service")) {
			accessTokenUrl = propertiesConfig.getTransactionAccessTokenUrl();
		} else if(apiUrl.equalsIgnoreCase("wms-fh-masters-service")) {
			accessTokenUrl = propertiesConfig.getFhMastersAccessTokenUrl();
		} else if (apiUrl.equalsIgnoreCase("wms-fh-inboundorder-service")) {
			accessTokenUrl = propertiesConfig.getFhInboundOrderAccessTokenUrl();
		} else if(apiUrl.equalsIgnoreCase("wms-fh-outboundorder-service")) {
			accessTokenUrl = propertiesConfig.getFhOutboundOrderAccessTokenUrl();
		} else {
				log.info("The given URL is not available. Quiting.");
				throw new BadRequestException("The given URL is not available. Quiting");
		}

		log.info("Access token url: " + accessTokenUrl);
		accessTokenUrl += "?grant_type=" + grantType + "&username=" + oauthUserName + "&password=" + oauthPassword;
		log.info("accessTokenUrl : " + accessTokenUrl);

		ResponseEntity<AuthToken> response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request,
				AuthToken.class);
		log.info("Access Token Response ---------" + response.getBody());
		return response.getBody();
	}

	/**
	 * 
	 * @param authTokenRequest
	 * @return
	 */
	public AuthToken getAuthToken(AuthTokenRequest authTokenRequest) {
		return generateOAuthToken(authTokenRequest.getApiName(), authTokenRequest.getClientId(),
				authTokenRequest.getClientSecretKey(), authTokenRequest.getGrantType(),
				authTokenRequest.getOauthUserName(), authTokenRequest.getOauthPassword());
	}

	/**
	 * getSetupServiceAuthToken
	 * 
	 * @return
	 */
	public AuthToken getIDMasterServiceAuthToken() {
		// Generate AuthToken for IDMasterService
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-idmaster-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}

	/**
	 * getCommonServiceAuthToken
	 * 
	 * @return
	 */
	public AuthToken getMastersServiceAuthToken() {
		// Generate AuthToken for MastersService
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-masters-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}

	public AuthToken getTransactionServiceAuthToken() {
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-transaction-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}

//	/*------------------AX-API----AUTH-TOKEN---------------------------*/
//	/**
//	 * 
//	 * @return
//	 */
//	public AXAuthToken generateAXOAuthToken() {
//		RestTemplate restTemplate = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		
//		AXUserAuth axapiUserAuth = new AXUserAuth();
//		axapiUserAuth.setUsername(propertiesConfig.getAxapiAccessTokenUsername());
//		axapiUserAuth.setPassword(propertiesConfig.getAxapiAccessTokenPassword());
//		
//		HttpEntity<?> entity = new HttpEntity<>(axapiUserAuth, headers);
//		String accessTokenUrl = propertiesConfig.getAxapiAccessTokenUrl();
//		log.info("Access token url: " + accessTokenUrl);
//		
//		ResponseEntity<AXAuthToken> response = 
//				restTemplate.exchange(accessTokenUrl, HttpMethod.POST, entity, AXAuthToken.class);
//		log.info("Access Token Response ---------" + response.getBody());
//		return response.getBody();
//	}


	/**
	 *
	 * @param apiUrl
	 * @param clientId
	 * @param clientSecretKey
	 * @param grantType
	 * @param oauthUserName
	 * @param oauthPassword
	 * @return
	 */
	private AuthToken generateAmgharaOAuthToken(String apiUrl, String clientId, String clientSecretKey, String grantType,
												String oauthUserName, String oauthPassword) {
		// Client Id and Client Secret Key to be sent as part of header for
		// authentication
		String credentials = clientId + ":" + clientSecretKey;
		String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", "Basic " + encodedCredentials);

		HttpEntity<String> request = new HttpEntity<String>(headers);
		String accessTokenUrl = apiUrl;

		// AuthToken URL dynamically
		if (apiUrl.equalsIgnoreCase("wms-masters-service")) {
			accessTokenUrl = propertiesConfig.getAmgharaMastersAccessTokenUrl();
		} else if (apiUrl.equalsIgnoreCase("wms-transaction-service")) {
			accessTokenUrl = propertiesConfig.getAmgharaTransactionAccessTokenUrl();
		} else {
			log.info("The given URL is not available. Quiting.");
			throw new BadRequestException("The given URL is not available. Quiting");
		}

		log.info("Access token url: " + accessTokenUrl);
		accessTokenUrl += "?grant_type=" + grantType + "&username=" + oauthUserName + "&password=" + oauthPassword;
		log.info("accessTokenUrl : " + accessTokenUrl);

		ResponseEntity<AuthToken> response = restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request,
				AuthToken.class);
		log.info("Access Token Response ---------" + response.getBody());
		return response.getBody();
	}

	/**
	 *
	 * @param authTokenRequest
	 * @return
	 */
	public AuthToken getAmgharaAuthToken(AuthTokenRequest authTokenRequest) {
		return generateAmgharaOAuthToken(authTokenRequest.getApiName(), authTokenRequest.getClientId(),
				authTokenRequest.getClientSecretKey(), authTokenRequest.getGrantType(),
				authTokenRequest.getOauthUserName(), authTokenRequest.getOauthPassword());
	}

	/**
	 *
	 * @return
	 */
	public AuthToken getAmgharaMastersServiceAuthToken() {
		// Generate AuthToken for MastersService
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-masters-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAmgharaAuthToken(authTokenRequest);
	}

	/**
	 *
	 * @return
	 */
	public AuthToken getAmgharaTransactionServiceAuthToken() {
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-transaction-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAmgharaAuthToken(authTokenRequest);
	}

	/**
	 * getCommonServiceAuthToken
	 *
	 * @return
	 */
	public AuthToken getFHMastersServiceAuthToken() {
		// Generate AuthToken for MastersService
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-fh-masters-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}

	public AuthToken getFhInboundOrderServiceAuthToken() {
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-fh-inboundorder-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}

	public AuthToken getFhOutboundOrderServiceAuthToken() {
		AuthTokenRequest authTokenRequest = new AuthTokenRequest();
		authTokenRequest.setApiName("wms-fh-outboundorder-service");
		authTokenRequest.setClientId(propertiesConfig.getClientId());
		authTokenRequest.setClientSecretKey(propertiesConfig.getClientSecretKey());
		authTokenRequest.setGrantType(propertiesConfig.getGrantType());
		authTokenRequest.setOauthUserName(propertiesConfig.getUsername());
		authTokenRequest.setOauthPassword(propertiesConfig.getPassword());
		return getAuthToken(authTokenRequest);
	}
}
