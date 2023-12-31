package com.tekclover.wms.api.transaction.service;

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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.auth.AXUserAuth;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.auth.AuthTokenRequest;

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
		} else {
			log.info("The givem URL is not available. Quiting.");
			throw new BadRequestException("The givem URL is not available. Quiting");
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
	
	/*------------------AX-API----AUTH-TOKEN---------------------------*/
	/**
	 * 
	 * @return
	 */
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
	
	/*------------------AX-API----AUTH-TOKEN---------------------------*/
	/**
	 * axapi.service.access_token.url=http://168.187.214.59:10102/api/ax/gettoken
	 * axapi.service.access_token.username=AxUser
	 * axapi.service.access_token.password=Wms-@tv@ndtsc-!nt00
	 * ---------------------------------------------------------
	 * @return
	 */
	public AXAuthToken generateAXOAuthToken() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		headers.add("resource", propertiesConfig.getAxapiServiceAccessTokenResource());
		
		AXUserAuth axapiUserAuth = new AXUserAuth();
//		axapiUserAuth.setClient_Id("a9ef22c7-c41f-4cc5-8d83-4eb222dd7734");
//		axapiUserAuth.setClient_secret("Bx28Q~Sjz45kSetkvgBqZjt2g-fimhs~Cat5taVt");
//		axapiUserAuth.setGrant_type("client_credentials");
//		axapiUserAuth.setResource("https://tvd365-dev81cbb1e734974cffdevaos.axcloud.dynamics.com");
		axapiUserAuth.setClient_Id(propertiesConfig.getAxapiServiceAccessTokenClientId());
		axapiUserAuth.setClient_secret(propertiesConfig.getAxapiServiceAccessTokenClientSecret());
		axapiUserAuth.setGrant_type(propertiesConfig.getAxapiServiceAccessTokenGrantType());
		axapiUserAuth.setResource(propertiesConfig.getAxapiServiceAccessTokenResource());
		
//		HttpEntity<?> entity = new HttpEntity<>(headers);
//		String accessTokenUrl = "https://login.microsoftonline.com/truevalue.com.kw/oauth2/token";
		String accessTokenUrl = propertiesConfig.getAxapiServiceAccessTokenAuthUrl();
		
		log.info("client_Id : " + axapiUserAuth.getClient_Id());
		log.info("grant_type : " + axapiUserAuth.getGrant_type());
		log.info("resource : " + axapiUserAuth.getResource());
		log.info("client_secret : " + axapiUserAuth.getClient_secret());
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("client_Id", axapiUserAuth.getClient_Id());
		map.add("grant_type", axapiUserAuth.getGrant_type());
		map.add("resource", axapiUserAuth.getResource());
		map.add("client_secret", axapiUserAuth.getClient_secret());

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		log.info("Access token url: " + accessTokenUrl);
		
		ResponseEntity<AXAuthToken> response = 
				restTemplate.exchange(accessTokenUrl, HttpMethod.POST, request, AXAuthToken.class);
		log.info("Access Token Response ---------" + response.getBody());
		return response.getBody();
	}
	
	public static void main(String[] args) {
//		generateAXOAuthToken();
	}
}
