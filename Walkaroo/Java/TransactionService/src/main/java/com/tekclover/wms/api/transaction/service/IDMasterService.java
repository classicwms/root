package com.tekclover.wms.api.transaction.service;

import java.time.Year;
import java.util.Collections;
import java.util.Date;

import com.tekclover.wms.api.transaction.model.notification.NotificationSave;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.AuditLog;
import com.tekclover.wms.api.transaction.model.dto.BinClassId;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.UserManagement;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;

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
//			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param warehouseId
	 * @param companyCodeId
	 * @param plantId
	 * @param languageId
	 * @param authToken
	 * @return
	 */
	public Warehouse getWarehouse (String warehouseId, String companyCodeId,
								   String plantId, String languageId,
								   String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "ClassicWMS RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId)
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("plantId",plantId)
							.queryParam("languageId", languageId);
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
	public UserManagement getUserManagement(String userId, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement/" + userId)
					.queryParam("warehouseId", warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
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
							"numberrange/nextNumberRange/" + numberRangeCode)
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
	
	//-----------------------------------------------------------------------------------------------------------
	// GET - /binclassid
	public BinClassId getBinClassId(String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid/" + warehouseId);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BinClassId> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BinClassId.class);
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
	 * 
	 * @param loginUserID
	 * @param tableName
	 * @param modifiedField
	 * @param oldValue
	 * @param newValue
     */
	public void createAuditLogRecord(String warehouseId, String tableName, Integer screenNo, Integer subScreenNo, 
			String modifiedField, String oldValue, String newValue, String loginUserID) {
		AuditLog auditLog = new AuditLog();
		
		AuthToken authTokenForIdmasterService = authTokenService.getIDMasterServiceAuthToken();
		Warehouse warehouse = getWarehouse (warehouseId, authTokenForIdmasterService.getAccess_token());

		// C_ID
		auditLog.setCompanyCode(warehouse.getCompanyCode());
		
		// PLANT_ID
		auditLog.setPlantID(warehouse.getPlantId());
		
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

	/**
	 * 
	 * @param statusId
	 * @param warehouseId
     */
	public StatusId getStatus(Long statusId, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid/" + statusId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusId.class);
			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}

	//----------------------------------------------------V2--------------------------------------------------------------

	public StatusId getStatus(Long statusId, String warehouseId, String languageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid/" + statusId)
					.queryParam("languageId", languageId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusId.class);
			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}

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

	/**
	 *
	 * @param notificationSave
	 */
	public void createNotification(NotificationSave notificationSave) {
		try {
			AuthToken authTokenForIDMaster = authTokenService.getIDMasterServiceAuthToken();
			String authToken = authTokenForIDMaster.getAccess_token();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(notificationSave, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "notification-message/create");
			ResponseEntity<String> result =
					restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
//            return result.getBody();
		} catch (Exception e) {
			log.error("webSocketNotification Push failed : " + e.toString());
		}
	}
}