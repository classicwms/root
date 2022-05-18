package com.tekclover.wms.api.masters.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.api.masters.config.PropertiesConfig;
import com.tekclover.wms.api.masters.exception.BadRequestException;
import com.tekclover.wms.api.masters.model.dto.UserManagement;
import com.tekclover.wms.api.masters.model.dto.Warehouse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IDMasterService {
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	@Autowired
	AuthTokenService authTokenService;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	private String getIDMasterServiceApiUrl () {
		return propertiesConfig.getIdmasterServiceUrl();
	}
	
	
	//--------------------------------------------------------------------------------------------------------------------
	// GET
	public Warehouse getWarehouse (String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "ClassicWMS RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<Warehouse> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Warehouse.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			return null;
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	// GET - /usermanagement/?
	public UserManagement getUserManagement(String userId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement/" + userId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	// GET - /login/userManagement
	public String getNextNumberRange(Long numberRangeCode, int fiscalYear, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + 
							"numberRange/nextNumberRange/" + numberRangeCode)
					.queryParam("fiscalYear", fiscalYear)
					.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
}