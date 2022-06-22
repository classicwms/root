package com.tekclover.wms.core.service;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

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

import com.tekclover.wms.core.config.PropertiesConfig;
import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.model.idmaster.AddRoleAccess;
import com.tekclover.wms.core.model.idmaster.BarcodeSubTypeId;
import com.tekclover.wms.core.model.idmaster.BarcodeTypeId;
import com.tekclover.wms.core.model.idmaster.City;
import com.tekclover.wms.core.model.idmaster.CompanyId;
import com.tekclover.wms.core.model.idmaster.Country;
import com.tekclover.wms.core.model.idmaster.Currency;
import com.tekclover.wms.core.model.idmaster.FloorId;
import com.tekclover.wms.core.model.idmaster.HhtUser;
import com.tekclover.wms.core.model.idmaster.ItemGroupId;
import com.tekclover.wms.core.model.idmaster.ItemTypeId;
import com.tekclover.wms.core.model.idmaster.LevelId;
import com.tekclover.wms.core.model.idmaster.MenuId;
import com.tekclover.wms.core.model.idmaster.PlantId;
import com.tekclover.wms.core.model.idmaster.ProcessSequenceId;
import com.tekclover.wms.core.model.idmaster.RoleAccess;
import com.tekclover.wms.core.model.idmaster.RowId;
import com.tekclover.wms.core.model.idmaster.State;
import com.tekclover.wms.core.model.idmaster.StatusId;
import com.tekclover.wms.core.model.idmaster.StorageBinTypeId;
import com.tekclover.wms.core.model.idmaster.StorageClassId;
import com.tekclover.wms.core.model.idmaster.StorageSectionId;
import com.tekclover.wms.core.model.idmaster.StrategyId;
import com.tekclover.wms.core.model.idmaster.StroageTypeId;
import com.tekclover.wms.core.model.idmaster.SubItemGroupId;
import com.tekclover.wms.core.model.idmaster.UomId;
import com.tekclover.wms.core.model.idmaster.UserTypeId;
import com.tekclover.wms.core.model.idmaster.VariantId;
import com.tekclover.wms.core.model.idmaster.Vertical;
import com.tekclover.wms.core.model.idmaster.WarehouseId;
import com.tekclover.wms.core.model.idmaster.WarehouseTypeId;
import com.tekclover.wms.core.model.user.AddUserManagement;
import com.tekclover.wms.core.model.user.UpdateUserManagement;
import com.tekclover.wms.core.model.user.UserManagement;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IDMasterService {
	
	@Autowired
	PropertiesConfig propertiesConfig;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	private String getIDMasterServiceApiUrl () {
		return propertiesConfig.getIdmasterServiceUrl();
	}
	
	/*--------------------------------------------UserManagement--------------------------------------*/
	
	// GET - /login/validate
	public UserManagement validateUserID(String userID, String password, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "login")
			        .queryParam("userID", userID)
			        .queryParam("password", password);
	
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
//			log.info("result : " + result.getBody());
			return result.getBody();
		} catch (Exception e) {
			throw new BadRequestException(e.getLocalizedMessage());
		}
	}
	
	// GET ALL
	public UserManagement[] getUserManagements(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement");
			ResponseEntity<UserManagement[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public UserManagement getUserManagement (String userId, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement/" + userId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<UserManagement> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public UserManagement createUserManagement (@Valid AddUserManagement newUserManagement, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement")
					.queryParam("loginUserID", loginUserID);
			HttpEntity<?> entity = new HttpEntity<>(newUserManagement, headers);
			ResponseEntity<UserManagement> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UserManagement.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// PATCH
	public UserManagement updateUserManagement (String userId, String warehouseId, String loginUserID, 
			@Valid UpdateUserManagement updateUserManagement, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(updateUserManagement, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement/" + userId)
					.queryParam("loginUserID", loginUserID)
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<UserManagement> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UserManagement.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteUserManagement (String userId, String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usermanagement/" + userId)
					.queryParam("loginUserID", loginUserID)
					.queryParam("warehouseId", warehouseId);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/*--------------------------------------CompanyMaster---------------------------------------------------------------------------------*/
	
	// GET
	public CompanyId getCompany(String companyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyId);
			ResponseEntity<CompanyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CompanyId.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public CompanyId[] getCompanies (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid");
			ResponseEntity<CompanyId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CompanyId[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// CREATE
	public CompanyId addCompany (CompanyId newCompany, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newCompany, headers);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid")
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<CompanyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CompanyId.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public CompanyId updateCompany (String companyCodeId, CompanyId modifiedCompany, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedCompany, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyCodeId)
			        .queryParam("loginUserID", loginUserID);
			
			ResponseEntity<CompanyId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CompanyId.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteCompany (String companyId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* ------------------------Currency-----------------------------------------------------------------------------------------*/
	
	// GET
	public Currency getCurrency(String currencyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "currency/" + currencyId);
			ResponseEntity<Currency> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Currency.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public Currency[] getCurrencies (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "currency");
			ResponseEntity<Currency[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Currency[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// CREATE
	public Currency addCurrency (Currency newCurrency, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newCurrency, headers);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "currency");
			ResponseEntity<Currency> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Currency.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public Currency updateCurrency (String currencyId, Currency modifiedCurrency, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedCurrency, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "currency/" + currencyId);
			
			ResponseEntity<Currency> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Currency.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteCurrency (String currencyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "currency/" + currencyId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/* ------------------------CITY-----------------------------------------------------------------------------------------*/
	
	// GET
	public City getCity(String cityId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "city/" + cityId);
			ResponseEntity<City> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, City.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public City[] getCities (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "city");
			ResponseEntity<City[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, City[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// CREATE
	public City addCity (City newCity, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newCity, headers);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "city");
			ResponseEntity<City> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, City.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public City updateCity (String cityId, City modifiedCity, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedCity, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "city/" + cityId);
			
			ResponseEntity<City> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, City.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteCity (String cityId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "city/" + cityId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
//			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/* ------------------------Country-----------------------------------------------------------------------------------------*/
	
	// GET
	public Country getCountry(String countryId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "country/" + countryId);
			ResponseEntity<Country> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Country.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public Country[] getCountries (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "country");
			ResponseEntity<Country[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Country[].class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// CREATE
	public Country addCountry (Country newCountry, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newCountry, headers);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "country");
			ResponseEntity<Country> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Country.class);
//			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public Country updateCountry (String countryId, Country modifiedCountry, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedCountry, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "country/" + countryId);
			
			ResponseEntity<Country> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Country.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteCountry (String countryId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "country/" + countryId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	/* ------------------------State-----------------------------------------------------------------------------------------*/
	
	// GET
	public State getState(String stateId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "state/" + stateId);
			ResponseEntity<State> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, State.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public State[] getStates (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "state");
			ResponseEntity<State[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, State[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// CREATE
	public State addState (State newState, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newState, headers);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "state");
			ResponseEntity<State> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, State.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public State updateState (String stateId, State modifiedState, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedState, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "state/" + stateId);
			ResponseEntity<State> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, State.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteState (String stateId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "state/" + stateId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/* ------------------------Vertical-----------------------------------------------------------------------------------------*/
	
	// GET
	public Vertical getVertical(String verticalId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "vertical/" + verticalId);
			ResponseEntity<Vertical> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Vertical.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET ALL
	public Vertical[] getVerticals (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "vertical");
			ResponseEntity<Vertical[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, Vertical[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// CREATE
	public Vertical addVertical (Vertical newVertical, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(newVertical, headers);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "vertical");
			ResponseEntity<Vertical> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, Vertical.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// UPDATE
	public Vertical updateVertical (String verticalId, Vertical modifiedVertical, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedVertical, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "vertical/" + verticalId);
			ResponseEntity<Vertical> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, Vertical.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteVertical (String verticalId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "vertical/" + verticalId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	/*-------------------------------------------------------------------------------------------*/
	//--------------------------------------------BarcodeSubTypeId------------------------------------------------------------------------
	// GET ALL
	public BarcodeSubTypeId[] getBarcodeSubTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodesubtypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BarcodeSubTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BarcodeSubTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public BarcodeSubTypeId getBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodesubtypeid/" + barcodeSubTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("barcodeTypeId", barcodeTypeId)
					.queryParam("barcodeSubTypeId", barcodeSubTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<BarcodeSubTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BarcodeSubTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public BarcodeSubTypeId createBarcodeSubTypeId (BarcodeSubTypeId newBarcodeSubTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodesubtypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newBarcodeSubTypeId, headers);
		ResponseEntity<BarcodeSubTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BarcodeSubTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public BarcodeSubTypeId updateBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId,  
			String loginUserID, BarcodeSubTypeId modifiedBarcodeSubTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedBarcodeSubTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodesubtypeid/" + barcodeSubTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("barcodeTypeId", barcodeTypeId)
					.queryParam("loginUserID", loginUserID);			
			ResponseEntity<BarcodeSubTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BarcodeSubTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteBarcodeSubTypeId (String warehouseId, Long barcodeTypeId, Long barcodeSubTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodesubtypeid/" + barcodeSubTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("barcodeTypeId", barcodeTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------BarcodeTypeId------------------------------------------------------------------------
	// GET ALL
	public BarcodeTypeId[] getBarcodeTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodetypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BarcodeTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BarcodeTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public BarcodeTypeId getBarcodeTypeId (String warehouseId, Long barcodeTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodetypeid/" + barcodeTypeId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<BarcodeTypeId> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BarcodeTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public BarcodeTypeId createBarcodeTypeId (BarcodeTypeId newBarcodeTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodetypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newBarcodeTypeId, headers);
		ResponseEntity<BarcodeTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BarcodeTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public BarcodeTypeId updateBarcodeTypeId (String warehouseId, Long barcodeTypeId, String loginUserID, 
			BarcodeTypeId modifiedBarcodeTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedBarcodeTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodetypeid/" + barcodeTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<BarcodeTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BarcodeTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteBarcodeTypeId (String warehouseId, Long barcodeTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "barcodetypeid/" + barcodeTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------CompanyId------------------------------------------------------------------------
	// GET ALL
	public CompanyId[] getCompanyIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CompanyId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CompanyId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public CompanyId getCompanyId (String companyCodeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyCodeId)
					.queryParam("companyCodeId", companyCodeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<CompanyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CompanyId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public CompanyId createCompanyId (CompanyId newCompanyId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newCompanyId, headers);
		ResponseEntity<CompanyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CompanyId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public CompanyId updateCompanyId (String companyCodeId, String loginUserID, CompanyId modifiedCompanyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedCompanyId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyCodeId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<CompanyId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CompanyId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteCompanyId (String companyCodeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid/" + companyCodeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------FloorId------------------------------------------------------------------------
	// GET ALL
	public FloorId[] getFloorIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "floorid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FloorId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FloorId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public FloorId getFloorId (String warehouseId, Long floorId,  String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "floorid/" + floorId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<FloorId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FloorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public FloorId createFloorId (FloorId newFloorId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "floorid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newFloorId, headers);
		ResponseEntity<FloorId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, FloorId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public FloorId updateFloorId (String warehouseId, Long floorId, String loginUserID, FloorId modifiedFloorId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedFloorId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "floorid/" + floorId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<FloorId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, FloorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteFloorId (String warehouseId, Long floorId,  String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "floorid/" + floorId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------ItemGroupId------------------------------------------------------------------------
	// GET ALL
	public ItemGroupId[] getItemGroupIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemGroupId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroupId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public ItemGroupId getItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String itemGroup, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid/" + itemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroup", itemGroup);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<ItemGroupId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemGroupId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public ItemGroupId createItemGroupId (ItemGroupId newItemGroupId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newItemGroupId, headers);
		ResponseEntity<ItemGroupId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroupId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ItemGroupId updateItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String itemGroup, 
			String loginUserID, ItemGroupId modifiedItemGroupId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedItemGroupId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid/" + itemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroup", itemGroup)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ItemGroupId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemGroupId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String itemGroup, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid/" + itemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroup", itemGroup)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

//--------------------------------------------ItemTypeId------------------------------------------------------------------------
	// GET ALL
	public ItemTypeId[] getItemTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ItemTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public ItemTypeId getItemTypeId ( String warehouseId, Long itemTypeId, String itemType, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid/" + itemTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemType", itemType);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<ItemTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ItemTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public ItemTypeId createItemTypeId (ItemTypeId newItemTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newItemTypeId, headers);
		ResponseEntity<ItemTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ItemTypeId updateItemTypeId ( String warehouseId, Long itemTypeId, String itemType, 
			String loginUserID, ItemTypeId modifiedItemTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedItemTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid/" + itemTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemType", itemType)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ItemTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ItemTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteItemTypeId ( String warehouseId, Long itemTypeId, String itemType, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid/" + itemTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemType", itemType)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------LevelId------------------------------------------------------------------------
	// GET ALL
	public LevelId[] getLevelIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "levelid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LevelId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LevelId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public LevelId getLevelId (String warehouseId, Long levelId,  String level, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "levelid/" + levelId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("level", level);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<LevelId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public LevelId createLevelId (LevelId newLevelId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "levelid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newLevelId, headers);
		ResponseEntity<LevelId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, LevelId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public LevelId updateLevelId (String warehouseId, Long levelId,  String level, 
			String loginUserID, LevelId modifiedLevelId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedLevelId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "levelid/" + levelId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("level", level)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<LevelId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, LevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteLevelId (String warehouseId, Long levelId,  String level, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "levelid/" + levelId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("level", level)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------MenuId------------------------------------------------------------------------
	// GET ALL
	public MenuId[] getMenuIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "menuid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MenuId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MenuId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public MenuId getMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId, 
			String authorizationObjectValue,  String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "menuid/" + menuId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("subMenuId", subMenuId)
					.queryParam("authorizationObjectId", authorizationObjectId)
					.queryParam("authorizationObjectValue", authorizationObjectValue);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<MenuId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MenuId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public MenuId createMenuId (MenuId newMenuId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "menuid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newMenuId, headers);
		ResponseEntity<MenuId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MenuId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public MenuId updateMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId, 
			String authorizationObjectValue, String loginUserID, MenuId modifiedMenuId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedMenuId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "menuid/" + menuId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("subMenuId", subMenuId)
					.queryParam("authorizationObjectId", authorizationObjectId)
					.queryParam("authorizationObjectValue", authorizationObjectValue)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<MenuId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MenuId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteMenuId (String warehouseId, Long menuId, Long subMenuId, Long authorizationObjectId, 
			String authorizationObjectValue,  String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "menuid/" + menuId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("subMenuId", subMenuId)
					.queryParam("authorizationObjectId", authorizationObjectId)
					.queryParam("authorizationObjectValue", authorizationObjectValue)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------PlantId------------------------------------------------------------------------
	// GET ALL
	public PlantId[] getPlantIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PlantId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PlantId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public PlantId getPlantId (String plantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid/" + plantId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<PlantId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PlantId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public PlantId createPlantId (PlantId newPlantId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPlantId, headers);
		ResponseEntity<PlantId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PlantId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public PlantId updatePlantId (String plantId, String loginUserID, PlantId modifiedPlantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedPlantId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid/" + plantId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<PlantId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PlantId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deletePlantId (String plantId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "plantid/" + plantId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------ProcessSequenceId------------------------------------------------------------------------
	// GET ALL
	public ProcessSequenceId[] getProcessSequenceIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ProcessSequenceId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProcessSequenceId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public ProcessSequenceId getProcessSequenceId (String warehouseId, Long processId, Long subLevelId, 
			String processDescription, String subProcessDescription, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid/" + processId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("processId", processId)
					.queryParam("subLevelId", subLevelId)
					.queryParam("processDescription", processDescription)
					.queryParam("subProcessDescription", subProcessDescription);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<ProcessSequenceId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProcessSequenceId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public ProcessSequenceId createProcessSequenceId (ProcessSequenceId newProcessSequenceId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newProcessSequenceId, headers);
		ResponseEntity<ProcessSequenceId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProcessSequenceId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ProcessSequenceId updateProcessSequenceId (String warehouseId, Long processId, Long subLevelId, 
			String processDescription, String subProcessDescription, String loginUserID, ProcessSequenceId modifiedProcessSequenceId, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedProcessSequenceId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid/" + processId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("processId", processId)
					.queryParam("subLevelId", subLevelId)
					.queryParam("processDescription", processDescription)
					.queryParam("subProcessDescription", subProcessDescription)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<ProcessSequenceId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ProcessSequenceId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteProcessSequenceId (String warehouseId, Long processId, Long subLevelId,  String processDescription, String subProcessDescription, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid/" + processId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("processId", processId)
					.queryParam("subLevelId", subLevelId)
					.queryParam("processDescription", processDescription)
					.queryParam("subProcessDescription", subProcessDescription)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

///--------------------------------------------RowId------------------------------------------------------------------------
	// GET ALL
	public RowId[] getRowIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "rowid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RowId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RowId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public RowId getRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId,  String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "rowid/" + rowId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSectionId", storageSectionId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<RowId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RowId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public RowId createRowId (RowId newRowId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "rowid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newRowId, headers);
		ResponseEntity<RowId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RowId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public RowId updateRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId,  
			String loginUserID, RowId modifiedRowId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedRowId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "rowid/" + rowId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSectionId", storageSectionId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<RowId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, RowId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteRowId (String warehouseId, Long floorId, Long storageSectionId, String rowId,  String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "rowid/" + rowId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSectionId", storageSectionId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------StatusId------------------------------------------------------------------------
	// GET ALL
	public StatusId[] getStatusIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StatusId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StatusId getStatusId ( String warehouseId, Long statusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid/" + statusId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StatusId createStatusId (StatusId newStatusId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStatusId, headers);
		ResponseEntity<StatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StatusId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StatusId updateStatusId (String warehouseId, Long statusId, String loginUserID, StatusId modifiedStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStatusId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid/" + statusId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StatusId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStatusId (String warehouseId, Long statusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid/" + statusId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

//--------------------------------------------StorageBinTypeId------------------------------------------------------------------------
	// GET ALL
	public StorageBinTypeId[] getStorageBinTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagebintypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageBinTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StorageBinTypeId getStorageBinTypeId (String warehouseId, Long storageClassId, Long storageTypeId, 
			Long storageBinTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagebintypeid/" + storageBinTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId)
					.queryParam("storageTypeId", storageTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StorageBinTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageBinTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StorageBinTypeId createStorageBinTypeId (StorageBinTypeId newStorageBinTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = 
				UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagebintypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStorageBinTypeId, headers);
		ResponseEntity<StorageBinTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageBinTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StorageBinTypeId updateStorageBinTypeId (String warehouseId, Long storageClassId, Long storageTypeId,
			Long storageBinTypeId, String loginUserID, StorageBinTypeId modifiedStorageBinTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStorageBinTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagebintypeid/" + storageBinTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId)
					.queryParam("storageTypeId", storageTypeId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StorageBinTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageBinTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStorageBinTypeId (String warehouseId, Long storageClassId, Long storageTypeId, 
			Long storageBinTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagebintypeid/" + storageBinTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId)
					.queryParam("storageTypeId", storageTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------StorageClassId------------------------------------------------------------------------
	// GET ALL
	public StorageClassId[] getStorageClassIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storageclassid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageClassId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageClassId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StorageClassId getStorageClassId (String warehouseId, Long storageClassId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storageclassid/" + storageClassId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StorageClassId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageClassId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StorageClassId createStorageClassId (StorageClassId newStorageClassId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storageclassid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStorageClassId, headers);
		ResponseEntity<StorageClassId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageClassId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StorageClassId updateStorageClassId (String warehouseId, Long storageClassId, String loginUserID, 
			StorageClassId modifiedStorageClassId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStorageClassId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storageclassid/" + storageClassId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StorageClassId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageClassId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStorageClassId (String warehouseId, Long storageClassId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storageclassid/" + storageClassId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
		//--------------------------------------------StorageSectionId------------------------------------------------------------------------
	// GET ALL
	public StorageSectionId[] getStorageSectionIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StorageSectionId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageSectionId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StorageSectionId getStorageSectionId (String warehouseId, Long floorId, Long storageSectionId, 
			String storageSection, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid/" + storageSectionId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSection", storageSection);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StorageSectionId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StorageSectionId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StorageSectionId createStorageSectionId (StorageSectionId newStorageSectionId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStorageSectionId, headers);
		ResponseEntity<StorageSectionId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageSectionId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StorageSectionId updateStorageSectionId (String warehouseId, Long floorId, Long storageSectionId, 
			String storageSection, String loginUserID, StorageSectionId modifiedStorageSectionId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStorageSectionId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid/" + storageSectionId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSection", storageSection)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StorageSectionId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StorageSectionId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStorageSectionId (String warehouseId, Long floorId, Long storageSectionId, 
			String storageSection, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid/" + storageSectionId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("floorId", floorId)
					.queryParam("storageSection", storageSection)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------StrategyId------------------------------------------------------------------------
	// GET ALL
	public StrategyId[] getStrategyIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "strategyid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StrategyId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StrategyId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StrategyId getStrategyId (String warehouseId, Long strategyTypeId, String strategyNo, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "strategyid/" + strategyNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("strategyTypeId", strategyTypeId)
					.queryParam("strategyNo", strategyNo);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StrategyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StrategyId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StrategyId createStrategyId (StrategyId newStrategyId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "strategyid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStrategyId, headers);
		ResponseEntity<StrategyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StrategyId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StrategyId updateStrategyId (String warehouseId, Long strategyTypeId, String strategyNo,  
			String loginUserID, StrategyId modifiedStrategyId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStrategyId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "strategyid/" + strategyNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("strategyTypeId", strategyTypeId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StrategyId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StrategyId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStrategyId ( String warehouseId, Long strategyTypeId, String strategyNo, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "strategyid/" + strategyNo)
					.queryParam("warehouseId", warehouseId)
					.queryParam("strategyTypeId", strategyTypeId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------StroageTypeId------------------------------------------------------------------------
	// GET ALL
	public StroageTypeId[] getStroageTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagetypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StroageTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StroageTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public StroageTypeId getStroageTypeId (String warehouseId, Long storageClassId, Long storageTypeId, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagetypeid/" + storageTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<StroageTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StroageTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public StroageTypeId createStroageTypeId (StroageTypeId newStroageTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagetypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStroageTypeId, headers);
		ResponseEntity<StroageTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StroageTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StroageTypeId updateStroageTypeId (String warehouseId, Long storageClassId, Long storageTypeId, 
			String loginUserID, StroageTypeId modifiedStroageTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedStroageTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagetypeid/" + storageTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<StroageTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StroageTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteStroageTypeId (String warehouseId, Long storageClassId, Long storageTypeId,  String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagetypeid/" + storageTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("storageClassId", storageClassId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

//--------------------------------------------SubItemGroupId------------------------------------------------------------------------
	// GET ALL
	public SubItemGroupId[] getSubItemGroupIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "subitemgroupid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubItemGroupId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubItemGroupId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public SubItemGroupId getSubItemGroupId ( String warehouseId, Long itemTypeId, Long itemGroupId, 
			Long subItemGroupId, String subItemGroup,  String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "subitemgroupid/" + subItemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroupId", itemGroupId)
					.queryParam("subItemGroup", subItemGroup);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<SubItemGroupId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubItemGroupId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public SubItemGroupId createSubItemGroupId (SubItemGroupId newSubItemGroupId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "subitemgroupid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newSubItemGroupId, headers);
		ResponseEntity<SubItemGroupId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SubItemGroupId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public SubItemGroupId updateSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, 
			Long subItemGroupId, String subItemGroup, String loginUserID, SubItemGroupId modifiedSubItemGroupId, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedSubItemGroupId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "subitemgroupid/" + subItemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroupId", itemGroupId)
					.queryParam("subItemGroup", subItemGroup)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<SubItemGroupId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SubItemGroupId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteSubItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, Long subItemGroupId, 
			String subItemGroup, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "subitemgroupid/" + subItemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId)
					.queryParam("itemGroupId", itemGroupId)
					.queryParam("subItemGroup", subItemGroup)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------UomId------------------------------------------------------------------------
	// GET ALL
	public UomId[] getUomIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "uomid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UomId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UomId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public UomId getUomId (String uomId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "uomid/" + uomId)
					.queryParam("uomId", uomId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<UomId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UomId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public UomId createUomId (UomId newUomId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "uomid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newUomId, headers);
		ResponseEntity<UomId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UomId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public UomId updateUomId (String uomId, String loginUserID, UomId modifiedUomId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedUomId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "uomid/" + uomId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<UomId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UomId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteUomId (String uomId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "uomid/" + uomId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------UserTypeId------------------------------------------------------------------------
	// GET ALL
	public UserTypeId[] getUserTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usertypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public UserTypeId getUserTypeId (String warehouseId, Long userTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usertypeid/" + userTypeId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<UserTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public UserTypeId createUserTypeId (UserTypeId newUserTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usertypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newUserTypeId, headers);
		ResponseEntity<UserTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, UserTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public UserTypeId updateUserTypeId (String warehouseId, Long userTypeId, String loginUserID, 
			UserTypeId modifiedUserTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedUserTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usertypeid/" + userTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<UserTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, UserTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteUserTypeId (String warehouseId, Long userTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "usertypeid/" + userTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------VariantId------------------------------------------------------------------------
	// GET ALL
	public VariantId[] getVariantIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "variantid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<VariantId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, VariantId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public VariantId getVariantId (String warehouseId, String variantCode, String variantType, String variantSubCode, 
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "variantid/" + variantCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("variantCode", variantCode)
					.queryParam("variantType", variantType)
					.queryParam("variantSubCode", variantSubCode);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<VariantId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, VariantId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public VariantId createVariantId (VariantId newVariantId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "variantid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newVariantId, headers);
		ResponseEntity<VariantId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, VariantId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public VariantId updateVariantId (String warehouseId, String variantCode, String variantType, String variantSubCode, 
			String loginUserID, VariantId modifiedVariantId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedVariantId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "variantid/" + variantCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("variantCode", variantCode)
					.queryParam("variantType", variantType)
					.queryParam("variantSubCode", variantSubCode)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<VariantId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, VariantId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteVariantId (String warehouseId, String variantCode, String variantType, String variantSubCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "variantid/" + variantCode)
					.queryParam("warehouseId", warehouseId)
					.queryParam("variantCode", variantCode)
					.queryParam("variantType", variantType)
					.queryParam("variantSubCode", variantSubCode)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------WarehouseId------------------------------------------------------------------------
	// GET ALL
	public WarehouseId[] getWarehouseIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WarehouseId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WarehouseId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public WarehouseId getWarehouseId (String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<WarehouseId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WarehouseId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public WarehouseId createWarehouseId (WarehouseId newWarehouseId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newWarehouseId, headers);
		ResponseEntity<WarehouseId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public WarehouseId updateWarehouseId (String warehouseId, String loginUserID, WarehouseId modifiedWarehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedWarehouseId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<WarehouseId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WarehouseId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteWarehouseId (String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehouseid/" + warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------WarehouseTypeId------------------------------------------------------------------------
	// GET ALL
	public WarehouseTypeId[] getWarehouseTypeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WarehouseTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WarehouseTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public WarehouseTypeId getWarehouseTypeId (String warehouseId, Long warehouseTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid/" + warehouseTypeId)
					.queryParam("warehouseTypeId", warehouseTypeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<WarehouseTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WarehouseTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public WarehouseTypeId createWarehouseTypeId (WarehouseTypeId newWarehouseTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newWarehouseTypeId, headers);
		ResponseEntity<WarehouseTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WarehouseTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public WarehouseTypeId updateWarehouseTypeId (String warehouseId, Long warehouseTypeId, 
			String loginUserID, WarehouseTypeId modifiedWarehouseTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedWarehouseTypeId, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid/" + warehouseTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<WarehouseTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WarehouseTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteWarehouseTypeId (String warehouseId, Long warehouseTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid/" + warehouseTypeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------HhtUser------------------------------------------------------------------------
	// GET ALL
	public HhtUser[] getHhtUsers (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HhtUser[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HhtUser[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public HhtUser getHhtUser (String userId, String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser/" + userId)
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<HhtUser> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HhtUser.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public HhtUser[] getHhtUserByWarehouseId (String warehouseId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser/" + warehouseId + "/hhtUser")
					.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<HhtUser[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HhtUser[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public HhtUser createHhtUser (HhtUser newHhtUser, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newHhtUser, headers);
		ResponseEntity<HhtUser> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HhtUser.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public HhtUser updateHhtUser (String userId, String warehouseId, 
			HhtUser modifiedHhtUser, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedHhtUser, headers);
			
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser/" + userId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<HhtUser> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HhtUser.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteHhtUser (String userId, String warehouseId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "hhtuser/" + userId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	//--------------------------------------------RoleAccess------------------------------------------------------------------------
	// GET ALL
	public RoleAccess[] getRoleAccesss (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RoleAccess[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RoleAccess[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// GET
	public RoleAccess[] getRoleAccess (String warehouseId, Long userRoleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + userRoleId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("userRoleId", userRoleId);
			HttpEntity<?> entity = new HttpEntity<>(headers);	
			ResponseEntity<RoleAccess[]> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RoleAccess[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// POST
	public RoleAccess[] createRoleAccess (List<AddRoleAccess> newRoleAccess, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "MNRClara RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newRoleAccess, headers);
		ResponseEntity<RoleAccess[]> result = 
				getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RoleAccess[].class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public RoleAccess[] updateRoleAccess (String warehouseId, Long userRoleId, String loginUserID, 
			List<RoleAccess> modifiedRoleAccess, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(modifiedRoleAccess, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client)); 
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + userRoleId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("userRoleId", userRoleId)
					.queryParam("loginUserID", loginUserID);
			
			ResponseEntity<RoleAccess[]> result = 
					restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, RoleAccess[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	// DELETE
	public boolean deleteRoleAccess (String warehouseId, Long userRoleId, Long menuId, Long subMenuId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + userRoleId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("userRoleId", userRoleId)
					.queryParam("menuId", menuId)
					.queryParam("subMenuId", subMenuId)
					.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = 
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}