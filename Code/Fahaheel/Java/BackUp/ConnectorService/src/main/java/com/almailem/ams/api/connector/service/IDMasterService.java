package com.almailem.ams.api.connector.service;

import java.time.Year;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.almailem.ams.api.connector.config.PropertiesConfig;
import com.almailem.ams.api.connector.controller.exception.BadRequestException;
import com.almailem.ams.api.connector.model.auth.AuthToken;
import com.almailem.ams.api.connector.model.dto.AuditLog;

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
	
	/**
	 * createAuditLog
	 * 
	 * @param auditLog
	 * @param authToken
	 * @param authToken2
	 */
	private void createAuditLog(AuditLog auditLog, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(auditLog, headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "auditLog")
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<AuditLog> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity,
					AuditLog.class);
			log.info("result : " + result.getStatusCode());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * createAuditLogRecord
	 * @param warehouseId
	 * @param tableName
	 * @param screenNo
	 * @param subScreenNo
	 * @param modifiedField
	 * @param oldValue
	 * @param newValue
	 * @param loginUserID
	 */
	public void createAuditLogRecord(String warehouseId, String tableName, Integer screenNo, Integer subScreenNo, 
			String modifiedField, String oldValue, String newValue, String loginUserID) {
		AuditLog auditLog = new AuditLog();
		
		AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();
//		Warehouse warehouse = getWarehouse (warehouseId, authTokenForIdmasterService.getAccess_token());
//
//		// C_ID
//		auditLog.setCompanyCode(warehouse.getCompanyCode());
//		
//		// PLANT_ID
//		auditLog.setPlantID(warehouse.getPlantId());
		
		// WH_ID
		auditLog.setWarehouseId(warehouseId);

		// AUD_LOG_NO
		Long NUM_RAN_CODE = 999L;
		int fiscalYear = Year.now().getValue();
		String newAuditLogNumber = getNextNumberRange(NUM_RAN_CODE, fiscalYear, warehouseId, authTokenForIdmasterService.getAccess_token());
		log.info("nextVal for AuditLogNumber : " + newAuditLogNumber);
		auditLog.setAuditLogNumber(newAuditLogNumber);
		
		// FISCAL YEAR
		auditLog.setFiscalYear(Integer.valueOf(fiscalYear));
		
		// OBJ_NM
		auditLog.setObjectName(tableName);
		
		// SCREEN_NO
		auditLog.setScreenNo(screenNo);
		
		// SUB_SCREEN_NO
		auditLog.setSubScreenNo(subScreenNo);
		
		// TABLE_NM
		auditLog.setTableName(tableName);
		
		// MOD_FIELD
		auditLog.setModifiedField(modifiedField);
		
		// OLD_VL
		auditLog.setOldValue(oldValue);

		// NEW_VL
		auditLog.setNewValue(newValue);

		// CTD_BY
		auditLog.setCreatedBy(loginUserID);

		// UTD_BY
		auditLog.setUpdatedBy(loginUserID);

		// UTD_ON
		auditLog.setUpdatedOn(new Date());
		createAuditLog(auditLog, loginUserID, authTokenForIdmasterService.getAccess_token());
	}

	//----------------------------------------------------V2--------------------------------------------------------------
	// GET
	public String getNextNumberRange(Long numberRangeCode, String warehouseId,
									 String companyCodeId, String plantId,
									 String languageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() +
									"numberrange/nextNumberRange/" + numberRangeCode + "/v2")
							.queryParam("warehouseId", warehouseId)
							.queryParam("companyCodeId", companyCodeId)
							.queryParam("plantId", plantId)
							.queryParam("languageId", languageId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<String> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
}