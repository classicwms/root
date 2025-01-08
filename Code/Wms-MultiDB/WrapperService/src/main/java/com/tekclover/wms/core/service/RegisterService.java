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
		return commonService.generateOAuthToken(authTokenRequest.getApiName(),
				authTokenRequest.getClientId(), 
				authTokenRequest.getClientSecretKey(),
				authTokenRequest.getGrantType(),
				authTokenRequest.getOauthUserName(),
				authTokenRequest.getOauthPassword());	
	}
}