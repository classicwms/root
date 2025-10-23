package com.tekclover.wms.api.enterprise.transaction.service;

import com.google.api.client.util.Base64;
import com.tekclover.wms.api.enterprise.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.enterprise.controller.exception.BadRequestException;
import com.tekclover.wms.api.enterprise.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.enterprise.transaction.model.dto.*;
import com.tekclover.wms.api.enterprise.transaction.model.tng.ShipmentOrder;
import com.tekclover.wms.api.enterprise.transaction.model.tng.ShipmentOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Year;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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

//	private String getWooCommerceUrl () {
//		return propertiesConfig.getWooCommerceUrl();
//	}

    private String getTNGUrl () {
        return propertiesConfig.getTngUrl();
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

	//==============================================================================================================
//	public String getStatus(Status status,String orderId) {
//		try {
//			String credentials = propertiesConfig.getKey() + ":" + propertiesConfig.getSecret();
//			String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			headers.add("Authorization", "Basic " + encodedCredentials);
//			headers.set("User-Agent", "MySpringApp/1.0");
//
//			UriComponentsBuilder builder =
//					UriComponentsBuilder.fromHttpUrl(getWooCommerceUrl() +"/wp-json/wc/v3/orders/"+orderId);
//
//			HttpEntity<?> entity = new HttpEntity<>(status,headers);
//			ResponseEntity<String> result =
//					getRestTemplate().exchange(builder.toUriString(), HttpMethod.PUT, entity, String.class);
//			log.info("result : " + result.getBody());
//			return result.getBody();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new BadRequestException(e.getLocalizedMessage());
//		}
//	}

    //====================================================TNG=====================================================

    //Create Shipment Order
    public ShipmentOrderResponse shipmentOrder(ShipmentOrder shipmentOrder) {
        try {
			log.info("Shipment Order Process Request -------> " + shipmentOrder);
//			String credentials = propertiesConfig.getTngSecretKey() + ":" + propertiesConfig.getTngSecretValue();
//			String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			headers.add("Authorization", "Basic " + encodedCredentials);
            headers.set(propertiesConfig.getTngSecretKey(), propertiesConfig.getTngSecretValue());
            headers.set("User-Agent", "MySpringApp/1.0");

            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(getTNGUrl() +"CreateShipmentOrder");

            HttpEntity<?> entity = new HttpEntity<>(shipmentOrder,headers);
            ResponseEntity<ShipmentOrderResponse> result =
                    getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ShipmentOrderResponse.class);

            log.info("---post shipmentOrder -----StatusCode----->: " + result.getStatusCodeValue());
            log.info("---post shipmentOrder -----ResponseBody--->: " + result.getBody());

            return result.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

}