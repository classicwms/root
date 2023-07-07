package com.tekclover.wms.core.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.tekclover.wms.core.util.DateUtils;
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
import com.tekclover.wms.core.model.idmaster.*;
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
	@Autowired
	FileStorageService fileStorageService;
	
	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
	
	private String getIDMasterServiceApiUrl () {
		return propertiesConfig.getIdmasterServiceUrl();
	}

	/*--------------------------------------------UserManagement--------------------------------------*/

	// GET - /login/validate
	public UserManagement validateUserID(String userID, String password, String authToken, String version) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "Classic WMS's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "login")
					.queryParam("userID", userID)
					.queryParam("password", password)
					.queryParam("version", version);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<UserManagement> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, UserManagement.class);
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "companyid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newCompanyId, headers);
		ResponseEntity<CompanyId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CompanyId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public CompanyId updateCompanyId (String companyCodeId, String loginUserID, UpdateCompanyId modifiedCompanyId, String authToken) {
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
	public ItemGroupId getItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid/" + itemGroupId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("itemTypeId", itemTypeId);
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
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemgroupid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newItemGroupId, headers);
		ResponseEntity<ItemGroupId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemGroupId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ItemGroupId updateItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId,
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
	public boolean deleteItemGroupId (String warehouseId, Long itemTypeId, Long itemGroupId, String loginUserID, String authToken) {
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
			headers.add("User-Agent", "RestTemplate");
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
	public ItemTypeId getItemTypeId ( String warehouseId, Long itemTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid/" + itemTypeId)
					.queryParam("warehouseId", warehouseId);
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
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newItemTypeId, headers);
		ResponseEntity<ItemTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ItemTypeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public ItemTypeId updateItemTypeId ( String warehouseId, Long itemTypeId,
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
	public boolean deleteItemTypeId ( String warehouseId, Long itemTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "itemtypeid/" + itemTypeId)
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

	//--------------------------------------------LevelId------------------------------------------------------------------------
	// GET ALL
	public LevelId[] getLevelIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processsequenceid/" + processId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("processId", processId)
					.queryParam("subLevelId", subLevelId);
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
		headers.add("User-Agent", "RestTemplate");
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
			 String loginUserID, ProcessSequenceId modifiedProcessSequenceId,
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
	public boolean deleteProcessSequenceId (String warehouseId, Long processId, Long subLevelId,  String loginUserID, String authToken) {
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
	public StatusId createStatusId (AddStatusId newStatusId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStatusId, headers);
		ResponseEntity<StatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StatusId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StatusId updateStatusId (String warehouseId, Long statusId, String loginUserID, UpdateStatusId modifiedStatusId, String authToken) {
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
	public StorageSectionId getStorageSectionId (String warehouseId, Long floorId, String storageSectionId,
			String storageSection, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "storagesectionid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStorageSectionId, headers);
		ResponseEntity<StorageSectionId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StorageSectionId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	
	// PATCH
	public StorageSectionId updateStorageSectionId (String warehouseId, Long floorId, String storageSectionId,
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
	public boolean deleteStorageSectionId (String warehouseId, Long floorId, String storageSectionId,
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			
			UriComponentsBuilder builder = 
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "warehousetypeid/" + warehouseTypeId)
					.queryParam("warehouseId", warehouseId);
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
		headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
			headers.add("User-Agent", "RestTemplate");
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
		headers.add("User-Agent", "RestTemplate");
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
	public boolean deleteHhtUser ( String warehouseId, String userId, String loginUserID, String authToken) {
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
			headers.add("User-Agent", "RestTemplate");
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
	public RoleAccess[] getRoleAccess (String warehouseId, Long roleId,
									   String companyCodeId, String plantId,
									   String languageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + roleId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("languageId",languageId)
							.queryParam("plantId",plantId);
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
		headers.add("User-Agent", "RestTemplate");
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
	public RoleAccess[] updateRoleAccess (String warehouseId, Long roleId,String companyCodeId,
										  String languageId,String plantId,String loginUserID,
										  List<AddRoleAccess> modifiedRoleAccess, String authToken) {
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
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + roleId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("plantId",plantId)
							.queryParam("languageId",languageId)
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
	public boolean deleteRoleAccess (String warehouseId, Long roleId,
									 String companyCodeId, String languageId,
									 String plantId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "roleaccess/" + roleId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("companyCodeId",companyCodeId)
							.queryParam("languageId",languageId)
							.queryParam("plantId",plantId)
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
	//SEARCH
	public RoleAccess[] findRoleAccess(FindRoleId findRoleId,String authToken){
		try{
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder=UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() +"roleaccess/find");
			HttpEntity<?>entity = new HttpEntity<>(findRoleId,headers);
			ResponseEntity<RoleAccess[]> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RoleAccess[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------DoorId------------------------------------------------------------------------
	// GET ALL
	public DoorId[] getDoorIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "doorid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DoorId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DoorId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public DoorId getDoorId (String warehouseId, String doorId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "doorid/" + doorId)
							.queryParam("warehouseId",warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DoorId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DoorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public DoorId createDoorId (AddDoorId newDoorId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", " RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "doorid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newDoorId, headers);
		ResponseEntity<DoorId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DoorId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public DoorId updateDoorId (String warehouseId, String doorId,
								String loginUserID, UpdateDoorId modifiedDoorId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedDoorId, headers);

			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "doorid/" + doorId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);

			ResponseEntity<DoorId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DoorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteDoorId (String warehouseId, String doorId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "doorid/" + doorId)
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

	//--------------------------------------------ModuleId------------------------------------------------------------------------
	// GET ALL
	public ModuleId[] getModuleIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ModuleId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ModuleId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public ModuleId getModuleId (String warehouseId, String moduleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid/" + moduleId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ModuleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ModuleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public ModuleId createModuleId (AddModuleId newModuleId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", " RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newModuleId, headers);
		ResponseEntity<ModuleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ModuleId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public ModuleId updateModuleId (String warehouseId, String moduleId,
									String loginUserID, UpdateModuleId modifiedModuleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedModuleId, headers);

			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid/" + moduleId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);

			ResponseEntity<ModuleId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ModuleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteModuleId (String warehouseId, String moduleId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "moduleid/" + moduleId)
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

	//--------------------------------------------AdhocModuleId------------------------------------------------------------------------
	// GET ALL
	public AdhocModuleId[] getAdhocModuleIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "adhocmoduleid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AdhocModuleId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AdhocModuleId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public AdhocModuleId getAdhocModuleId (String warehouseId, String adhocModuleId,String moduleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "adhocmoduleid/" + adhocModuleId)
							.queryParam("moduleId", moduleId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AdhocModuleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AdhocModuleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public AdhocModuleId createAdhocModuleId (AddAdhocModuleId newAdhocModuleId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", " RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "adhocmoduleid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newAdhocModuleId, headers);
		ResponseEntity<AdhocModuleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AdhocModuleId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public AdhocModuleId updateAdhocModuleId (String warehouseId, String adhocModuleId,String moduleId,
											  String loginUserID, UpdateAdhocModuleId modifiedAdhocModuleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedAdhocModuleId, headers);

			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "adhocmoduleid/" + adhocModuleId)
					.queryParam("moduleId", moduleId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);

			ResponseEntity<AdhocModuleId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, AdhocModuleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteAdhocModuleId (String warehouseId, String adhocModuleId, String moduleId,String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "adhocmoduleid/" + adhocModuleId)
							.queryParam("moduleId", moduleId)
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

	//--------------------------------------------PalletizationLevelId------------------------------------------------------------------------
	// GET ALL
	public PalletizationLevelId[] getPalletizationLevelIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "palletizationlevelid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PalletizationLevelId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PalletizationLevelId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public PalletizationLevelId getPalletizationLevelId (String warehouseId, String palletizationLevelId, String palletizationLevel, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "palletizationlevelid/" + palletizationLevelId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("palletizationLevel", palletizationLevel);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<PalletizationLevelId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, PalletizationLevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public PalletizationLevelId createPalletizationLevelId (AddPalletizationLevelId newPalletizationLevelId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", " RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "palletizationlevelid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newPalletizationLevelId, headers);
		ResponseEntity<PalletizationLevelId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, PalletizationLevelId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public PalletizationLevelId updatePalletizationLevelId (String warehouseId, String palletizationLevelId,String palletizationLevel,
															String loginUserID, UpdatePalletizationLevelId modifiedPalletizationLevelId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedPalletizationLevelId, headers);

			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "palletizationlevelid/" + palletizationLevelId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("palletizationLevel", palletizationLevel)
					.queryParam("loginUserID", loginUserID);

			ResponseEntity<PalletizationLevelId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, PalletizationLevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deletePalletizationLevelId (String warehouseId, String palletizationLevelId,String palletizationLevel, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "palletizationlevelid/" + palletizationLevelId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("palletizationLevel", palletizationLevel)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------EmployeeId------------------------------------------------------------------------
	// GET ALL
	public EmployeeId[] getEmployeeIds (String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "employeeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EmployeeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EmployeeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// GET
	public EmployeeId getEmployeeId (String warehouseId, String employeeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "employeeid/" + employeeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("employeeId", employeeId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EmployeeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EmployeeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// POST
	public EmployeeId createEmployeeId (AddEmployeeId newEmployeeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", " RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "employeeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newEmployeeId, headers);
		ResponseEntity<EmployeeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EmployeeId.class);
		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}

	// PATCH
	public EmployeeId updateEmployeeId (String warehouseId, String employeeId,
										String loginUserID, UpdateEmployeeId modifiedEmployeeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", " RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(modifiedEmployeeId, headers);

			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "employeeid/" + employeeId)
					.queryParam("warehouseId", warehouseId)
					.queryParam("loginUserID", loginUserID);

			ResponseEntity<EmployeeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, EmployeeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// DELETE
	public boolean deleteEmployeeId (String warehouseId, String employeeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "employeeid/" + employeeId)
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

	//--------------------------------------------BinClassId------------------------------------------------------------------------
	//GET ALL
	public BinClassId[] getBinClassIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BinClassId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BinClassId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public BinClassId getBinClassId(String warehouseId, Long binClassId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid/" + binClassId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BinClassId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BinClassId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public BinClassId createBinClassId(AddBinClassId newBinClassId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newBinClassId, headers);
		ResponseEntity<BinClassId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BinClassId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public BinClassId updateBinclassId(String warehouseId, Long binClassId, String loginUserID,
									   UpdateBinClassId modifiedBinClassId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(modifiedBinClassId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid/" + binClassId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<BinClassId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BinClassId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteBinClassId(String warehouseId, Long binClassId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binclassid/" + binClassId)
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
	//--------------------------------------------DockId ------------------------------------------------------------------------
	//GET ALL
	public DockId[] getDockIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dockid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DockId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DockId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public DockId getDockId(String warehouseId, String dockId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dockid/" + dockId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DockId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DockId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public DockId createDockId(AddDockId newDockId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dockid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newDockId, headers);
		ResponseEntity<DockId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DockId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public DockId updateDockId(String warehouseId, String dockId, String loginUserID,
							   UpdateDockId updateDockId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDockId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dockid/" + dockId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<DockId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DockId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteDockId(String warehouseId, String dockId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dockid/" + dockId)
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
	//--------------------------------------------WorkCenterId ------------------------------------------------------------------------
	//GET ALL
	public WorkCenterId[] getworkCenterIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "workcenterid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkCenterId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkCenterId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public WorkCenterId getworkCenterId( String warehouseId, String workCenterId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "workcenterid/" + workCenterId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<WorkCenterId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, WorkCenterId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public WorkCenterId createWorkCenterId(AddWorkCenterId newWorkCenterId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "workcenterid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newWorkCenterId, headers);
		ResponseEntity<WorkCenterId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, WorkCenterId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public WorkCenterId updateWorkCenterId(String warehouseId, String workCenterId, String loginUserID,
										   UpdateWorkCenterId updateWorkCenterId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateWorkCenterId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "workcenterid/" + workCenterId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<WorkCenterId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, WorkCenterId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteWorkCenterId(String warehouseId, String workCenterId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "workcenterid/" + workCenterId)
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
	//--------------------------------------------outboundOrderStatusId------------------------------------------------------------------------
	//GET ALL
	public OutboundOrderStatusId[] getoutBoundOrderStatusIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundorderstatusid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundOrderStatusId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundOrderStatusId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public OutboundOrderStatusId getoutBoundOrderStatusId(String warehouseId, String outboundOrderStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundorderstatusid/" + outboundOrderStatusId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundOrderStatusId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundOrderStatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public OutboundOrderStatusId createoutBoundOrderStatusId(AddOutboundOrderStatusId newOutboundOrderStatusId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundorderstatusid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newOutboundOrderStatusId, headers);
		ResponseEntity<OutboundOrderStatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderStatusId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public OutboundOrderStatusId updateOutboundOrderStatusId(String warehouseId, String outboundOrderStatusId, String loginUserID,
															 UpdateOutboundOrderStatusId updateOutboundOrderStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOutboundOrderStatusId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundorderstatusid/" + outboundOrderStatusId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<OutboundOrderStatusId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OutboundOrderStatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteoutBoundOrderStatusId(String warehouseId, String outBoundOrderStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundorderstatusid/" + outBoundOrderStatusId)
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
	//--------------------------------------------InboundOrderStatusId ------------------------------------------------------------------------
	//GET ALL
	public InboundOrderStatusId[] getInboundOrderStatusIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundorderstatusid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundOrderStatusId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundOrderStatusId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public InboundOrderStatusId getInboundOrderStatusID(String warehouseId, String inboundOrderStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundorderstatusid/" + inboundOrderStatusId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundOrderStatusId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundOrderStatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public InboundOrderStatusId createinboundOrderStatusId(AddInboundOrderStatusId newinboundOrderStatusId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundorderstatusid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newinboundOrderStatusId, headers);
		ResponseEntity<InboundOrderStatusId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundOrderStatusId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public InboundOrderStatusId updateinboundOrderStatusId(String warehouseId, String inboundOrderStatusId, String loginUserID,
														   UpdateInboundOrderStatusId updateInboundOrderStatusId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateInboundOrderStatusId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundorderstatusid/" + inboundOrderStatusId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<InboundOrderStatusId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundOrderStatusId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteInboundOrderStatusId(String warehouseId, String inboundOrderStatusId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundorderstatusid/" + inboundOrderStatusId)
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
	//--------------------------------------------ControlTypeId ------------------------------------------------------------------------
	//GET ALL
	public ControlTypeId[] getcontrolTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controltypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ControlTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ControlTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ControlTypeId getcontrolTypeId(String warehouseId, String controlTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controltypeid/" + controlTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ControlTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ControlTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ControlTypeId createcontrolTypeId(AddControlTypeId newControlTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controltypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newControlTypeId, headers);
		ResponseEntity<ControlTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ControlTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ControlTypeId updatecontrolTypeId(String warehouseId, String controlTypeId, String loginUserID,
											 UpdateControlTypeId updateControlTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateControlTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controltypeid/" + controlTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ControlTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ControlTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deletecontrolTypeId(String warehouseId, String controlTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controltypeid/" + controlTypeId)
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
	//--------------------------------------------ApprovalId ------------------------------------------------------------------------
	//GET ALL
	public ApprovalId[] getapprovalIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ApprovalId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ApprovalId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ApprovalId getapprovalId(String warehouseId, String approvalId, String approvalLevel, String approverCode, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalid/" + approvalId)
							.queryParam("approvalLevel",approvalLevel)
							.queryParam("approverCode",approverCode)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ApprovalId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ApprovalId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ApprovalId createapprovalId(AddApprovalId newApprovalId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newApprovalId, headers);
		ResponseEntity<ApprovalId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ApprovalId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ApprovalId updateApprovalId(String approvalId,
									   String warehouseId,String approvalLevel, String approverCode,
									   UpdateApprovalId updateApprovalId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateApprovalId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalid/" + approvalId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("approvalLevel",approvalLevel)
							.queryParam("approverCode",approverCode)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ApprovalId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ApprovalId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteapprovalId(String warehouseId, String approvalId,String approvalLevel, String approverCode, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalid/" + approvalId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("approvalLevel",approvalLevel)
							.queryParam("approverCode",approverCode)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------RefDocTypeId ------------------------------------------------------------------------
	//GET ALL
	public RefDocTypeId[] getrefDocTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);

			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "refdoctypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RefDocTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RefDocTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public RefDocTypeId getrefDocTypeId(String warehouseId, String referenceDocumentTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "refdoctypeid/" + referenceDocumentTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<RefDocTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, RefDocTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public RefDocTypeId createrefDocTypeId(AddRefDocTypeId newRefDocTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "refdoctypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newRefDocTypeId, headers);
		ResponseEntity<RefDocTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, RefDocTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public RefDocTypeId updateRefDocTypeId(String warehouseId, String referenceDocumentTypeId, String loginUserID,
										   UpdateRefDocTypeId updateRefDocTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateRefDocTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "refdoctypeid/" + referenceDocumentTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<RefDocTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, RefDocTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteRefDocTypeId(String warehouseId, String referenceDocumentTypeId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "refdoctypeid/" + referenceDocumentTypeId)
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
	//--------------------------------------------ControlProcessId ------------------------------------------------------------------------
	//GET ALL
	public ControlProcessId[] getControlProcessIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controlprocessid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ControlProcessId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ControlProcessId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ControlProcessId getControlProcessId(String warehouseId, String controlProcessId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controlprocessid/" + controlProcessId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ControlProcessId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ControlProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ControlProcessId createControlProcessId(AddControlProcessId newControlProcessId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controlprocessid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newControlProcessId, headers);
		ResponseEntity<ControlProcessId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ControlProcessId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ControlProcessId updateControlProcessId(String warehouseId, String controlProcessId, String loginUserID,
												   UpdateControlProcessId updateControlProcessId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateControlProcessId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controlprocessid/" + controlProcessId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ControlProcessId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ControlProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deletecontrolProcessId(String warehouseId, String controlProcessId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "controlprocessid/" + controlProcessId)
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

	//--------------------------------------------AisleId ------------------------------------------------------------------------
	//GET ALL
	public AisleId[] getAisleIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "aisleid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AisleId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AisleId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public AisleId getAisleId(String warehouseId, String aisleId, String floorId, String storageSectionId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "aisleid/" + aisleId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<AisleId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, AisleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public AisleId createAisleId(AddAisleId newAisled, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "aisleid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newAisled, headers);
		ResponseEntity<AisleId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, AisleId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public AisleId updateAisleId(String warehouseId,String aisleId,String floorId,String storageSectionId,String loginUserID,
								 UpdateAisleId updateAisleId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateAisleId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "aisleid/" + aisleId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<AisleId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, AisleId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteAisleId(String warehouseId, String aisleId,String floorId, String storageSectionId, String loginUserID, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "aisleid/" + aisleId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------SpanId ------------------------------------------------------------------------
	//GET ALL
	public SpanId[] getSpanIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "spanid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SpanId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SpanId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public SpanId getSpanId(String warehouseId, String aisleId, String spanId, String floorId, String storageSectionId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "spanid/" + spanId)
							.queryParam("floorId",floorId)
							.queryParam("aisleId",aisleId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SpanId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SpanId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public SpanId createSpanId(AddSpanId newSpanId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "spanid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newSpanId, headers);
		ResponseEntity<SpanId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SpanId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public SpanId updateSpanId(String warehouseId, String aisleId,String spanId,String floorId, String storageSectionId, String loginUserID,
							   UpdateSpanId updateSpanId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSpanId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "spanid/" + spanId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("aisleId",aisleId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<SpanId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SpanId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteSpanId(String warehouseId,String aisleId, String spanId,String floorId, String storageSectionId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "spanid/" + spanId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("aisleId",aisleId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------ShelfId ------------------------------------------------------------------------
	//GET ALL
	public ShelfId[] getShelfIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "shelfid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ShelfId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ShelfId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ShelfId getShelfId(String warehouseId, String aisleId, String shelfId,String spanId, String floorId, String storageSectionId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "shelfid/" + shelfId)
							.queryParam("floorId",floorId)
							.queryParam("spanId",spanId)
							.queryParam("aisleId",aisleId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ShelfId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ShelfId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ShelfId createShelfId(AddShelfId newShelfId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "shelfid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newShelfId, headers);
		ResponseEntity<ShelfId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ShelfId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ShelfId updateShelfId(String warehouseId, String aisleId,String shelfId,String spanId,String floorId, String storageSectionId, String loginUserID,
								 UpdateShelfId updateShelfId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateShelfId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "shelfid/" + shelfId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("spanId",spanId)
							.queryParam("aisleId",aisleId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ShelfId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ShelfId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteShelfId(String warehouseId,String aisleId, String shelfId,String spanId,String floorId, String storageSectionId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "shelfid/" + shelfId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("spanId",spanId)
							.queryParam("aisleId",aisleId)
							.queryParam("floorId",floorId)
							.queryParam("storageSectionId",storageSectionId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------BinSectionId ------------------------------------------------------------------------
	//GET ALL
	public BinSectionId[] getBinSectionIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binsectionid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BinSectionId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BinSectionId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public BinSectionId getBinSectionId(String warehouseId, String binSectionId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binsectionid/" + binSectionId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<BinSectionId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, BinSectionId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public BinSectionId createBinSectionId(AddBinSectionId newBinSectionId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binsectionid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newBinSectionId, headers);
		ResponseEntity<BinSectionId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, BinSectionId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public BinSectionId updateBinSectionId(String warehouseId, String binSectionId, String loginUserID,
										   UpdateBinSectionId updateBinSectionId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateBinSectionId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binsectionid/" + binSectionId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<BinSectionId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, BinSectionId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteBinSectionId(String warehouseId, String binSectionId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "binsectionid/" + binSectionId)
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
	//--------------------------------------------DateFormatId ------------------------------------------------------------------------
	//GET ALL
	public DateFormatId[] getDateFormatIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dateformatid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DateFormatId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DateFormatId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public DateFormatId getDateFormatId(String warehouseId, String dateFormatId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dateformatid/" + dateFormatId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DateFormatId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DateFormatId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public DateFormatId createDateFormatId(AddDateFormatId newDateFormatId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dateformatid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newDateFormatId, headers);
		ResponseEntity<DateFormatId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DateFormatId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public DateFormatId updateDateFormatId(String warehouseId, String dateFormatId, String loginUserID,
										   UpdateDateFormatId updateDateFormatId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDateFormatId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dateformatid/" + dateFormatId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<DateFormatId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DateFormatId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteDateFormatId(String warehouseId, String dateFormatId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "dateformatid/" + dateFormatId)
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
	//--------------------------------------------DecimalNotationId ------------------------------------------------------------------------
	//GET ALL
	public DecimalNotationId[] getDecimalNotationIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "decimalnotationid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DecimalNotationId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DecimalNotationId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public DecimalNotationId getDecimalNotationId(String warehouseId, String decimalNotationId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "decimalnotationid/" + decimalNotationId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<DecimalNotationId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, DecimalNotationId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public DecimalNotationId createDecimalNotationId(AddDecimalNotationId newDecimalNotationId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "decimalnotationid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newDecimalNotationId, headers);
		ResponseEntity<DecimalNotationId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, DecimalNotationId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public DecimalNotationId updateDecimalNotationId(String warehouseId, String decimalNotationId, String loginUserID,
													 UpdateDecimalNotationId updateDecimalNotationId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateDecimalNotationId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "decimalnotationid/" + decimalNotationId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<DecimalNotationId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, DecimalNotationId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteDecimalNotationId(String warehouseId, String decimalNotationId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "decimalnotationid/" + decimalNotationId)
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

	//--------------------------------------------LanguageId ------------------------------------------------------------------------
	//GET ALL
	public LanguageId[] getLanguageIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "languageid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LanguageId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LanguageId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public LanguageId getLanguageId(String languageId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "languageid/" + languageId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<LanguageId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, LanguageId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public LanguageId createLanguageId(AddLanguageId newLanguageId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "languageid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newLanguageId, headers);
		ResponseEntity<LanguageId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, LanguageId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public LanguageId updateLanguageId(String languageId, String loginUserID,
									   UpdateLanguageId updateLanguageId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateLanguageId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "languageid/" + languageId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<LanguageId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, LanguageId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteLanguageId(String languageId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "languageid/" + languageId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------StatusMessagesId ------------------------------------------------------------------------
	public StatusMessagesId[] getStatusMessagesIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusmessagesid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StatusMessagesId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusMessagesId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public StatusMessagesId getStatusMessagesId(String messagesId, String languageId, String messageType,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusmessagesid/" + messagesId)
							.queryParam("languageId",languageId)
							.queryParam("messageType",messageType);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StatusMessagesId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StatusMessagesId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public StatusMessagesId createStatusMessagesId(AddStatusMessagesId newStatusMessagesId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusmessagesid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStatusMessagesId, headers);
		ResponseEntity<StatusMessagesId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StatusMessagesId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public StatusMessagesId updateStatusMessagesId(String messagesId, String languageId, String messageType, String loginUserID,
												   UpdateStatusMessagesId updateStatusMessagesId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStatusMessagesId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusmessagesid/" + messagesId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("languageId",languageId)
							.queryParam("messageType",messageType);
			ResponseEntity<StatusMessagesId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StatusMessagesId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteStatusMessagesId(String messageId, String languageId, String messageType, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "statusmessagesid/" + messageId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("languageId",languageId)
							.queryParam("messageType",messageType);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------MovementTypeId ------------------------------------------------------------------------
	public MovementTypeId[] getMovementTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "movementtypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MovementTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MovementTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public MovementTypeId getMovementTypeId(String warehouseId, String movementTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "movementtypeid/" + movementTypeId)
							.queryParam("warehouseId",warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<MovementTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, MovementTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public MovementTypeId createMovementTypeId(AddMovementTypeId newMovementTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "movementtypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newMovementTypeId, headers);
		ResponseEntity<MovementTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, MovementTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public MovementTypeId updateMovementTypeId(String warehouseId, String movementTypeId, String loginUserID,
											   UpdateMovementTypeId updateMovementTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateMovementTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "movementtypeid/" + movementTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<MovementTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, MovementTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteMovementTypeId(String warehouseId, String movementTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "movementtypeid/" + movementTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------SubMovementTypeId ------------------------------------------------------------------------
	public SubMovementTypeId[] getSubMovementTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "submovementtypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubMovementTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubMovementTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public SubMovementTypeId getSubMovementTypeId(String warehouseId, String movementTypeId, String subMovementTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "submovementtypeid/" + subMovementTypeId)
							.queryParam("movementTypeId",movementTypeId)
							.queryParam("warehouseId",warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubMovementTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubMovementTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public SubMovementTypeId createSubMovementTypeId(AddSubMovementTypeId newSubMovementTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "submovementtypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newSubMovementTypeId, headers);
		ResponseEntity<SubMovementTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SubMovementTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public SubMovementTypeId updateSubMovementTypeId(String warehouseId,String movementTypeId, String subMovementTypeId, String loginUserID,
													 UpdateSubMovementTypeId updateSubMovementTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSubMovementTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "submovementtypeid/" + subMovementTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("movementTypeId",movementTypeId)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<SubMovementTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SubMovementTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteSubMovementTypeId(String warehouseId,String movementTypeId, String subMovementTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "submovementtypeid/" + subMovementTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("movementTypeId",movementTypeId)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	//--------------------------------------------OutboundOrderTypeId ------------------------------------------------------------------------
	public OutboundOrderTypeId[] getOutboundOrderTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundordertypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundOrderTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundOrderTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public OutboundOrderTypeId getOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundordertypeid/" + outboundOrderTypeId)
							.queryParam("warehouseId",warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<OutboundOrderTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, OutboundOrderTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public OutboundOrderTypeId createOutboundOrderTypeId(AddOutboundOrderTypeId newOutboundOrderTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundordertypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newOutboundOrderTypeId, headers);
		ResponseEntity<OutboundOrderTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, OutboundOrderTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public OutboundOrderTypeId updateOutboundOrderTypeId(String warehouseId, String outboundOrderTypeId, String loginUserID,
														 UpdateOutboundOrderTypeId updateOutboundOrderTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateOutboundOrderTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundordertypeid/" + outboundOrderTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<OutboundOrderTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, OutboundOrderTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteOutboundOrderTypeId(String warehouseId,String outboundOrderTypeId,String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "outboundordertypeid/" + outboundOrderTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}



	//--------------------------------------------InboundOrderTypeId ------------------------------------------------------------------------
	public InboundOrderTypeId[] getInboundOrderTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundordertypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundOrderTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundOrderTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public InboundOrderTypeId getInboundOrderTypeId(String warehouseId, String inboundOrderTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundordertypeid/" + inboundOrderTypeId)
							.queryParam("warehouseId",warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<InboundOrderTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, InboundOrderTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public InboundOrderTypeId createInboundOrderTypeId(AddInboundOrderTypeId newInboundOrderTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundordertypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newInboundOrderTypeId, headers);
		ResponseEntity<InboundOrderTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, InboundOrderTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public InboundOrderTypeId updateInboundOrderTypeId(String warehouseId, String inboundOrderTypeId, String loginUserID,
													   UpdateInboundOrderTypeId updateInboundOrderTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateInboundOrderTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundordertypeid/" + inboundOrderTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<InboundOrderTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, InboundOrderTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteInboundOrderTypeId(String warehouseId, String inboundOrderTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "MNRClara's RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "inboundordertypeid/" + inboundOrderTypeId)
							.queryParam("loginUserID", loginUserID)
							.queryParam("warehouseId",warehouseId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------TransferTypeId ------------------------------------------------------------------------
	//GET ALL
	public TransferTypeId[] getTransferTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "transfertypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<TransferTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, TransferTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public TransferTypeId getTransferTypeId(String warehouseId, String transferTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "transfertypeid/" + transferTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<TransferTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, TransferTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public TransferTypeId createTransferTypeId(AddTransferTypeId newTransferTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "transfertypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newTransferTypeId, headers);
		ResponseEntity<TransferTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, TransferTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public TransferTypeId updateTransferTypeId(String warehouseId, String transferTypeId, String loginUserID,
											   UpdateTransferTypeId updateTransferTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateTransferTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "transfertypeid/" + transferTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<TransferTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, TransferTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteTransferTypeId(String warehouseId, String transferTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "transfertypeid/" + transferTypeId)
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

	//--------------------------------------------StockTypeId ------------------------------------------------------------------------
	//GET ALL
	public StockTypeId[] getStockTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "stocktypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StockTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public StockTypeId getStockTypeId(String warehouseId, String stockTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "stocktypeid/" + stockTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<StockTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, StockTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public StockTypeId createStockTypeId(AddStockTypeId newStockTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "stocktypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newStockTypeId, headers);
		ResponseEntity<StockTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, StockTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public StockTypeId updateStockTypeId(String warehouseId, String stockTypeId, String loginUserID,
										 UpdateStockTypeId updateStockTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateStockTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "stocktypeid/" + stockTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<StockTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, StockTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteStockTypeId(String warehouseId, String stockTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "stocktypeid/" + stockTypeId)
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

	//--------------------------------------------SpecialStockIndicatorId ------------------------------------------------------------------------
	//GET ALL
	public SpecialStockIndicatorId[] getSpecialStockIndicatorIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "specialstockindicatorid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SpecialStockIndicatorId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SpecialStockIndicatorId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public SpecialStockIndicatorId getSpecialStockIndicatorId(String warehouseId,String stockTypeId, String specialStockIndicatorId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "specialstockindicatorid/" + specialStockIndicatorId)
							.queryParam("stockTypeId",stockTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SpecialStockIndicatorId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SpecialStockIndicatorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public SpecialStockIndicatorId createSpecialStockIndicatorId(AddSpecialStockIndicatorId newSpecialStockIndicatorId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "specialstockindicatorid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newSpecialStockIndicatorId, headers);
		ResponseEntity<SpecialStockIndicatorId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SpecialStockIndicatorId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public SpecialStockIndicatorId updateSpecialStockIndicatorId(String warehouseId, String stockTypeId, String specialStockIndicatorId, String loginUserID,
																 UpdateSpecialStockIndicatorId updateSpecialStockIndicatorId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSpecialStockIndicatorId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "specialstockindicatorid/" + specialStockIndicatorId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("stockTypeId",stockTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<SpecialStockIndicatorId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SpecialStockIndicatorId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteSpecialStockIndicatorId(String warehouseId, String stockTypeId, String specialStockIndicatorId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "specialstockindicatorid/" + specialStockIndicatorId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("stockTypeId",stockTypeId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------HandlingEquipmentId ------------------------------------------------------------------------
	//GET ALL
	public HandlingEquipmentId[] getHandlingEquipmentIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingequipmentid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingEquipmentId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipmentId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public HandlingEquipmentId getHandlingEquipmentId(String warehouseId,String handlingEquipmentId, String handlingEquipmentNumber,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingequipmentid/" + handlingEquipmentId)
							.queryParam("handlingEquipmentNumber",handlingEquipmentNumber)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingEquipmentId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingEquipmentId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public HandlingEquipmentId createHandlingEquipmentId(AddHandlingEquipmentId newHandlingEquipmentId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingequipmentid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newHandlingEquipmentId, headers);
		ResponseEntity<HandlingEquipmentId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingEquipmentId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public HandlingEquipmentId updateHandlingEquipmentId(String warehouseId, String handlingEquipmentId,String handlingEquipmentNumber, String loginUserID,
														 UpdateHandlingEquipmentId updateHandlingEquipmentId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateHandlingEquipmentId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingequipmentid/" + handlingEquipmentId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("handlingEquipmentNumber",handlingEquipmentNumber)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<HandlingEquipmentId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingEquipmentId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteHandlingEquipmentId(String warehouseId, String handlingEquipmentId,String handlingEquipmentNumber, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingequipmentid/" + handlingEquipmentId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("handlingEquipmentNumber",handlingEquipmentNumber)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------HandlingUnitId ------------------------------------------------------------------------
	//GET ALL
	public HandlingUnitId[] getHandlingUnitIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingunitid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingUnitId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnitId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public HandlingUnitId getHandlingUnitId(String warehouseId,String handlingUnitId, String handlingUnitNumber,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingunitid/" + handlingUnitId)
							.queryParam("handlingUnitNumber",handlingUnitNumber)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<HandlingUnitId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, HandlingUnitId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public HandlingUnitId createHandlingUnitId(AddHandlingUnitId newHandlingUnitId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingunitid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newHandlingUnitId, headers);
		ResponseEntity<HandlingUnitId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, HandlingUnitId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public HandlingUnitId updateHandlingUnitId(String warehouseId, String handlingUnitId,String handlingUnitNumber, String loginUserID,
											   UpdateHandlingUnitId updateHandlingUnitId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateHandlingUnitId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingunitid/" + handlingUnitId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("handlingUnitNumber",handlingUnitNumber)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<HandlingUnitId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, HandlingUnitId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteHandlingUnitId(String warehouseId, String handlingUnitId,String handlingUnitNumber, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "handlingunitid/" + handlingUnitId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("handlingUnitNumber",handlingUnitNumber)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}


	//--------------------------------------------CycleCountTypeId ------------------------------------------------------------------------
	//GET ALL
	public CycleCountTypeId[] getCycleCountTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "cyclecounttypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CycleCountTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CycleCountTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public CycleCountTypeId getCycleCountTypeId(String warehouseId, String cycleCountTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "cyclecounttypeid/" + cycleCountTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<CycleCountTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, CycleCountTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public CycleCountTypeId createCycleCountTypeId(AddCycleCountTypeId newCycleCountTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "cyclecounttypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newCycleCountTypeId, headers);
		ResponseEntity<CycleCountTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, CycleCountTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public CycleCountTypeId updateCycleCountTypeId(String warehouseId, String cycleCountTypeId, String loginUserID,
												   UpdateCycleCountTypeId updateCycleCountTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateCycleCountTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "cyclecounttypeid/" + cycleCountTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<CycleCountTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, CycleCountTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteCycleCountTypeId(String warehouseId, String cycleCountTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "cyclecounttypeid/" + cycleCountTypeId)
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


	//--------------------------------------------ReturnTypeId ------------------------------------------------------------------------
	//GET ALL
	public ReturnTypeId[] getReturnTypeIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "returntypeid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ReturnTypeId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ReturnTypeId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ReturnTypeId getReturnTypeId(String warehouseId, String returnTypeId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "returntypeid/" + returnTypeId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ReturnTypeId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ReturnTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ReturnTypeId createReturnTypeId(AddReturnTypeId newReturnTypeId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "returntypeid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newReturnTypeId, headers);
		ResponseEntity<ReturnTypeId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ReturnTypeId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ReturnTypeId updateReturnTypeId(String warehouseId, String returnTypeId, String loginUserID,
										   UpdateReturnTypeId updateReturnTypeId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateReturnTypeId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "returntypeid/" + returnTypeId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ReturnTypeId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ReturnTypeId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteReturnTypeId(String warehouseId, String returnTypeId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "returntypeid/" + returnTypeId)
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

	//--------------------------------------------ApprovalProcessId ------------------------------------------------------------------------
	//GET ALL
	public ApprovalProcessId[] getApprovalProcessIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalprocessid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ApprovalProcessId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ApprovalProcessId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ApprovalProcessId getApprovalProcessId(String warehouseId, String approvalProcessId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalprocessid/" + approvalProcessId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ApprovalProcessId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ApprovalProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ApprovalProcessId createApprovalProcessId(AddApprovalProcessId newApprovalProcessId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalprocessid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newApprovalProcessId, headers);
		ResponseEntity<ApprovalProcessId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ApprovalProcessId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ApprovalProcessId updateApprovalProcessId(String warehouseId, String approvalProcessId, String loginUserID,
													 UpdateApprovalProcessId updateApprovalProcessId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateApprovalProcessId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalprocessid/" + approvalProcessId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ApprovalProcessId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ApprovalProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteApprovalProcessId(String warehouseId, String approvalProcessId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "approvalprocessid/" + approvalProcessId)
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

	//--------------------------------------------ProcessId ------------------------------------------------------------------------
	//GET ALL
	public ProcessId[] getProcessIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ProcessId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProcessId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public ProcessId getProcessId(String warehouseId, String processId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processid/" + processId)
							.queryParam("warehouseId", warehouseId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<ProcessId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, ProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public ProcessId createProcessId(AddProcessId newProcessId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newProcessId, headers);
		ResponseEntity<ProcessId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, ProcessId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public ProcessId updateProcessId(String warehouseId, String processId, String loginUserID,
									 UpdateProcessId updateProcessId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateProcessId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processid/" + processId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<ProcessId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, ProcessId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteProcessId(String warehouseId, String processId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "processid/" + processId)
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


	//--------------------------------------------SubLevelId ------------------------------------------------------------------------
	//GET ALL
	public SubLevelId[] getSubLevelIds(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "sublevelid");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubLevelId[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubLevelId[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public SubLevelId getSubLevelId(String warehouseId, String subLevelId, String levelId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "sublevelid/" + subLevelId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("levelId",levelId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<SubLevelId> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, SubLevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public SubLevelId createSubLevelId(AddSubLevelId newSubLevelId, String loginUserID, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "sublevelid")
				.queryParam("loginUserID", loginUserID);
		HttpEntity<?> entity = new HttpEntity<>(newSubLevelId, headers);
		ResponseEntity<SubLevelId> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, SubLevelId.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public SubLevelId updateSubLevelId(String warehouseId, String subLevelId,String levelId, String loginUserID,
									   UpdateSubLevelId updateSubLevelId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateSubLevelId, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "sublevelid/" + subLevelId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("levelId",levelId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<SubLevelId> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, SubLevelId.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteSubLevelId(String warehouseId, String subLevelId,String levelId, String loginUserID,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "sublevelid/" + subLevelId)
							.queryParam("warehouseId", warehouseId)
							.queryParam("levelId",levelId)
							.queryParam("loginUserID", loginUserID);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//--------------------------------------------EMail ------------------------------------------------------------------------
	//GET ALL
	public EMailDetails[] getEMailDetailsList(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EMailDetails[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EMailDetails[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public EMailDetails getEMail(Long id, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email/" + id);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EMailDetails> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EMailDetails.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//CREATE
	public EMailDetails createEMail(AddEMailDetails newEMail, String authToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("User-Agent", "RestTemplate");
		headers.add("Authorization", "Bearer " + authToken);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email");
		HttpEntity<?> entity = new HttpEntity<>(newEMail, headers);
		ResponseEntity<EMailDetails> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, EMailDetails.class);
		//		log.info("result : " + result.getStatusCode());
		return result.getBody();
	}
	//PATCH
	public EMailDetails updateEMail(Long id,
							 AddEMailDetails updateEMail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateEMail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email/" + id);
			ResponseEntity<EMailDetails> result = restTemplate.exchange(builder.toUriString(), HttpMethod.PATCH, entity, EMailDetails.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteEMail(Long id,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email/" + id);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//UN DELETE
	public EMailDetails undeleteEMail(Long id,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email/undelete/" + id);
			ResponseEntity<EMailDetails> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EMailDetails.class);
			log.info("result : " + result);
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//--------------------------------------------Report File Name ------------------------------------------------------------------------

	//CREATE - result
	public boolean createMailingReport(MailingReport newMailingReport, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(newMailingReport, headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail/createMailingReport" );
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//PATCH
	public FileNameForEmail updateFileNameForEmail(FileNameForEmail updateFileNameForEmail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateFileNameForEmail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail" );
			ResponseEntity<FileNameForEmail> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, FileNameForEmail.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	//result
	public boolean updateReportFileName(FileNameForEmail updateFileNameForEmail, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(updateFileNameForEmail, headers);
			HttpClient client = HttpClients.createDefault();
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(client));
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail" );
			ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, entity, String.class);
			log.info("result : " + result.getStatusCode());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//GET ALL
	public FileNameForEmail[] getFileNameForEmailList(String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail");
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FileNameForEmail[]> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FileNameForEmail[].class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET
	public FileNameForEmail getFileNameForEmail(Long fileNameId, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail/" + fileNameId);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FileNameForEmail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FileNameForEmail.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// GET DateTime format dd-MM-yyyy HH:mm:ss
	public String getDateTime() throws ParseException {

		return String.valueOf(DateUtils.addTimeToDate2(new Date(),3));

	}
	// GET by Date
	public FileNameForEmail getFileNameForEmailByDate(String fileNameDate, String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail/fileNameByDate/" + fileNameDate);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<FileNameForEmail> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, FileNameForEmail.class);
			log.info("result : " + result.getStatusCode());
			return result.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	//DELETE
	public boolean deleteFileNameForEmail(Long fileNameId,String authToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			HttpEntity<?> entity = new HttpEntity<>(headers);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "filenameforemail/" + fileNameId);
			ResponseEntity<String> result = getRestTemplate().exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
			log.info("result : " + result);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	// Send Mail
	public void sendMail(String authToken) throws MessagingException, IOException {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.add("User-Agent", "RestTemplate");
			headers.add("Authorization", "Bearer " + authToken);
			UriComponentsBuilder builder =
					UriComponentsBuilder.fromHttpUrl(getIDMasterServiceApiUrl() + "email/sendMail" );
			HttpEntity<?> entity = new HttpEntity<>(headers);
			ResponseEntity<EMailDetails> result =
					getRestTemplate().exchange(builder.toUriString(), HttpMethod.GET, entity, EMailDetails.class);
			log.info("result : " + result.getStatusCode());
//			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}