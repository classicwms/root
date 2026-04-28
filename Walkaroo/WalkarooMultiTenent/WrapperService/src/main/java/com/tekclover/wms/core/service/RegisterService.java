package com.tekclover.wms.core.service;

import java.util.Collections;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.auth.AuthTokenRequest;
import com.tekclover.wms.core.model.user.NewUser;
import com.tekclover.wms.core.model.user.RegisteredUser;
import com.tekclover.wms.core.repository.RegisterRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RegisterService {
	
	@Autowired
	private RegisterRepository registerRepository;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	private String getIDMasterServiceApiUrl () {
		return propertiesConfig.getIdmasterServiceUrl();
	}
	
	public String getClientSecretID () {
		return propertiesConfig.getClientId();
	}
	
	public String getRandomUUID () {
		return commonService.generateUUID();
	}
	
	public String validateRegisteredUser(@Valid RegisteredUser registeredUser) {
		Optional<NewUser> newUserOpt = registerRepository.findByRegisterId(registeredUser.getRegisterId());
		if (newUserOpt.isEmpty()) {
			throw new BadRequestException("Given Client Register ID: " + registeredUser.getRegisterId() + " does not exist.");
		}
		
		String clientSecretIDFromDB = newUserOpt.get().getClientSecretId();
		
		/*
		 * Client ID validation check
		 */
		if (registeredUser.getClientSecretId().equalsIgnoreCase(clientSecretIDFromDB)) {
			return propertiesConfig.getClientSecretKey();
		} else {
			throw new BadRequestException("Client Secret ID doesn't match. Please provide valid Client ID.");
		}
	}

	public NewUser registerNewUser (String clientName) {
		NewUser newUser = new NewUser();
		
		Optional<NewUser> newUserOpt = registerRepository.findByClientName(clientName);
		if (!newUserOpt.isEmpty()) {
			throw new BadRequestException("Already " + clientName + " exists.");
		}
		newUser.setClientName(clientName);
		newUser.setRegisterId(getRandomUUID ());
		newUser.setClientSecretId(getClientSecretID());
		newUser = registerRepository.save(newUser);
		return newUser;
	}

	public AuthToken getAuthToken(@Valid AuthTokenRequest authTokenRequest) {
		if(authTokenRequest.getApiName().equalsIgnoreCase("wms-transaction-service")) {
			AuthToken newAuth = new AuthToken();
			newAuth.setAccess_token("eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3NzczNjM1NDEsInVzZXJfbmFtZSI6IndtcyIsImF1dGhvcml0aWVzIjpbIlJPTEVfU1lTVEVNQURNSU4iXSwianRpIjoiVDNMQ002cDk1aWRvMzlxeW9ldnBZU01rY1BVIiwiY2xpZW50X2lkIjoicGl4ZWx0cmljZSIsInNjb3BlIjpbInJlYWQiLCJ3cml0ZSJdfQ.OYtvBCHePEX6r1An7O03Bsf35x0huGhWPTSxvJSlx48");
			newAuth.setToken_type("bearer");
			newAuth.setRefresh_token("eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX25hbWUiOiJ3bXMiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiYXRpIjoiVDNMQ002cDk1aWRvMzlxeW9ldnBZU01rY1BVIiwiZXhwIjoxNzc3MzYzNTQxLCJhdXRob3JpdGllcyI6WyJST0xFX1NZU1RFTUFETUlOIl0sImp0aSI6ImdpaU5xQXZ3YmVKSUtJWnNEODhzOVUyR09EYyIsImNsaWVudF9pZCI6InBpeGVsdHJpY2UifQ.3ny_VsxCLOaKX1K3PEvCrTDTDZBubstmZQ4i2v_cLOo");
			newAuth.setExpires_in("19999");
			newAuth.setScope("read write");
			newAuth.setJti("T3LCM6p95ido39qyoevpYSMkcPU");
			return newAuth;
		}
		return commonService.generateOAuthToken(authTokenRequest.getApiName(),
				authTokenRequest.getClientId(), 
				authTokenRequest.getClientSecretKey(),
				authTokenRequest.getGrantType(),
				authTokenRequest.getOauthUserName(),
				authTokenRequest.getOauthPassword());	
	}
}