package com.tekclover.wms.api.masters.service;

import java.util.Collections;

import com.tekclover.wms.api.masters.model.dto.PlantId;
import com.tekclover.wms.api.masters.model.warehouse.Warehouse;
import com.tekclover.wms.api.masters.model.idmaster.*;
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
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			return null;
		}
	}

	// GET
	public ServiceTypeId getServiceTypeId(String warehouseId, String moduleId, Long serviceTypeId, String companyCodeId, String languageId, String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Threepl RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "servicetypeid/" + serviceTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("moduleId", moduleId)
					.queryParam("companyCodeId", companyCodeId)
					.queryParam("languageId", languageId)
					.queryParam("plantId", plantId);
			ResponseEntity<ServiceTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ServiceTypeId.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
		// GET
		public ModuleId getModuleId(String warehouseId, String moduleId,String companyCodeId,String languageId,String plantId,String authToken) {
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				headers.add("User-Agent", " RestTemplate");
				headers.add("Authorization", "Bearer " + authToken);

				UriComponentsBuilder builder =
						UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid/" + moduleId)
								.queryParam("warehouseId", warehouseId)
								.queryParam("companyCodeId",companyCodeId)
								.queryParam("languageId",languageId)
								.queryParam("plantId",plantId);
				HttpEntity<?> entity = new HttpEntity<>(headers);
				ResponseEntity<ModuleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ModuleId.class);
//				log.info("result : " + result.getStatusCode());
				return result.getBody();
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	public BillingModeId getBillingModeId(String warehouseId, Long billModeId, String companyCodeId, String languageId, String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Threepl RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "billingmodeid/" + billModeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("companyCodeId",companyCodeId)
					.queryParam("languageId",languageId)
					.queryParam("plantId",plantId);
			ResponseEntity<BillingModeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BillingModeId.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public BillingFrequencyId getBillingFrequencyId(String warehouseId, Long billFrequencyId, String companyCodeId, String languageId, String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Threepl RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "billingfrequencyid/" + billFrequencyId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("companyCodeId",companyCodeId)
					.queryParam("languageId",languageId)
					.queryParam("plantId",plantId);
			ResponseEntity<BillingFrequencyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BillingFrequencyId.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public PaymentTermId getPaymentTermId(String warehouseId, Long paymentTermId, String companyCodeId, String languageId, String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Threepl RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "paymenttermid/" + paymentTermId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("companyCodeId",companyCodeId)
					.queryParam("languageId",languageId)
					.queryParam("plantId",plantId);
			ResponseEntity<PaymentTermId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentTermId.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public PaymentModeId getPaymentModeId(String warehouseId, Long paymentModeId,String companyCodeId,String languageId,String plantId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Threepl RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "paymentmodeid/" + paymentModeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("companyCodeId",companyCodeId)
					.queryParam("languageId",languageId)
					.queryParam("plantId",plantId);
			ResponseEntity<PaymentModeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PaymentModeId.class);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public WarehouseId getWarehouseId (String warehouseId,String plantId,String companyCodeId,String languageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId)
							.queryParam("plantId",plantId)
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("languageId",languageId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WarehouseId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WarehouseId.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//-------------------------------------------------------------------------------------------------------------------
	// GET
	public PlantId[] getPlantId (String companyCodeId, String languageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid/branchCode")
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("languageId",languageId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PlantId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PlantId[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
//			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
	
	//-------------------------------------------------------------------------------------------------------------------
	// GET - /login/userManagement
	public String getNextNumberRange(Long numberRangeCode, int fiscalYear, String warehouseId,
									 String companyCodeId, String plantId,
									 String languageId,	String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + 
							"numberrange/nextNumberRange/" + numberRangeCode)
					.queryParam("fiscalYear", fiscalYear)
					.queryParam("warehouseId", warehouseId)
					.queryParam("companyCodeId", companyCodeId)
					.queryParam("plantId", plantId)
					.queryParam("languageId", languageId);

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