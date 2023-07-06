package com.tekclover.wms.core.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.tekclover.wms.core.exception.RestResponseEntityExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.core.exception.*;
import com.tekclover.wms.core.exception.CustomErrorResponse;
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
import com.tekclover.wms.core.service.IDMasterService;
import com.tekclover.wms.core.service.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-idmaster-service")
@Api(tags = {"IDMaster Service"}, value = "IDMaster Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User",description = "Operations related to IDMaster Service")})
public class IDMasterServiceController { 
	
	@Autowired
	IDMasterService idmasterService;
	
	@Autowired
	RegisterService registerService;

	@Autowired
	FileStorageService fileStorageService;

	@Autowired
	DocStorageService docStorageService;
	
	/* --------------------------------LOGIN-------------------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Login User") // label for swagger
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> loginUser(@RequestParam String name, @RequestParam String password, 
			@RequestParam String authToken, @RequestParam(required = false) String version) {
		try {
			UserManagement loggedUser = idmasterService.validateUserID(name, password, authToken, version);
			log.info("LoginUser::: " + loggedUser);
			log.info("version::: " + version);
			return new ResponseEntity<>(loggedUser, HttpStatus.OK);
		} catch (BadRequestException e) {
			log.error("Invalid user");
			String str = "Either UserId is invalid or Password does not match.";
			CustomErrorResponse error = new CustomErrorResponse();
	        error.setTimestamp(LocalDateTime.now());
	        error.setError(str);
	        error.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
		}
	}

	@ApiOperation(response = UserManagement.class, value = "Get all UserManagement details") // label for swagger
	@GetMapping("/usermanagement")
	public ResponseEntity<?> getAll(@RequestParam String authToken) {
		UserManagement[] userManagementList = idmasterService.getUserManagements(authToken);
		return new ResponseEntity<>(userManagementList, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Get a UserManagement") // label for swagger
	@GetMapping("/usermanagement/{userId}")
	public ResponseEntity<?> getUserManagement(@PathVariable String userId,@RequestParam String companyCode,
											   @RequestParam String plantId,@RequestParam String languageId,
											   @RequestParam Long userRoleId,@RequestParam String warehouseId,
											   @RequestParam String authToken) {
		UserManagement dbUserManagement = idmasterService.getUserManagement(userId,companyCode,plantId,languageId,
																			userRoleId,warehouseId, authToken);
		log.info("UserManagement : " + dbUserManagement);
		return new ResponseEntity<>(dbUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Create UserManagement") // label for swagger
	@PostMapping("/usermanagement")
	public ResponseEntity<?> postUserManagement(@Valid @RequestBody AddUserManagement newUserManagement,
												@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {

		UserManagement createdUserManagement = idmasterService.createUserManagement(newUserManagement, loginUserID, authToken);

		return new ResponseEntity<>(createdUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Update UserManagement") // label for swagger
	@RequestMapping(value = "/usermanagement/{userId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchUserManagement(@PathVariable String userId, @RequestParam String warehouseId,
												 @RequestParam String companyCode,@RequestParam String plantId,
												 @RequestParam String languageId,@RequestParam Long userRoleId,
												 @RequestParam String loginUserID, @RequestParam String authToken,
												 @Valid @RequestBody UpdateUserManagement updateUserManagement)
			throws IllegalAccessException, InvocationTargetException {

		UserManagement updatedUserManagement =
				idmasterService.updateUserManagement(userId,warehouseId,loginUserID,companyCode,
													plantId,languageId,userRoleId,updateUserManagement, authToken);

		return new ResponseEntity<>(updatedUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Delete UserManagement") // label for swagger
	@DeleteMapping("/usermanagement/{userId}")
	public ResponseEntity<?> deleteUserManagement(@PathVariable String userId, @RequestParam String warehouseId,
												  @RequestParam String companyCode,@RequestParam String plantId,
												  @RequestParam String languageId,@RequestParam Long userRoleId,
												  @RequestParam String loginUserID, @RequestParam String authToken) {

		idmasterService.deleteUserManagement(userId,warehouseId,companyCode,languageId,plantId,userRoleId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//FIND
	@ApiOperation(response = UserManagement[].class, value = "Find UserManagement")//label for swagger
	@PostMapping("/usermanagement/findUserManagement")
	public UserManagement[] findUserManagement(@RequestBody FindUserManagement findUserManagement,
											   @RequestParam String authToken)throws Exception{
		return idmasterService.findUserManagement(findUserManagement,authToken);
	}
	
    /* --------------------------------City-------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Get All Cities") // label for swagger
    @RequestMapping(value = "/city", method = RequestMethod.GET)
	public ResponseEntity<?> getCities(@RequestParam String authToken) {
		City[] city = idmasterService.getCities(authToken);
		log.info("getCities::: " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get City") // label for swagger
    @RequestMapping(value = "/city/{cityId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCity(@RequestParam String cityId, @RequestParam String authToken) {
		City city = idmasterService.getCity(cityId, authToken);
		log.info("getCity::: " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create new city") // label for swagger
    @RequestMapping(value = "/city", method = RequestMethod.POST)
	public ResponseEntity<?> createCity(@RequestParam String authToken, @RequestBody City newCity) {
		City city = idmasterService.addCity(newCity, authToken);
		log.info("createCity::: " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update City") // label for swagger
    @RequestMapping(value = "/city", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateCity(@RequestParam String authToken, @RequestParam String cityId, @RequestBody City updatedCity) {
		City city = idmasterService.updateCity(cityId, updatedCity, authToken);
		log.info("updateCity::: " + city);
		return new ResponseEntity<>(city, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get City") // label for swagger
    @RequestMapping(value = "/city/{cityId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCity(@RequestParam String cityId, @RequestParam String authToken) {
		boolean isCityDeleted = idmasterService.deleteCity(cityId, authToken);
		log.info("isCityDeleted::: " + isCityDeleted);
		return new ResponseEntity<>(isCityDeleted, HttpStatus.OK);
	}
    
    /* --------------------------------Country-------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Get All Country") // label for swagger
    @RequestMapping(value = "/country", method = RequestMethod.GET)
	public ResponseEntity<?> getCountries(@RequestParam String authToken) {
		Country[] country = idmasterService.getCountries(authToken);
		log.info("getCities::: " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Country") // label for swagger
    @RequestMapping(value = "/country/{countryId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCountry(@RequestParam String countryId, @RequestParam String authToken) {
		Country country = idmasterService.getCountry(countryId, authToken);
		log.info("getCountry::: " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Create new Country") // label for swagger
    @RequestMapping(value = "/country", method = RequestMethod.POST)
	public ResponseEntity<?> createCountry(@RequestParam String authToken, @RequestBody Country newCountry) {
		Country country = idmasterService.addCountry(newCountry, authToken);
		log.info("createCountry::: " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update Country") // label for swagger
    @RequestMapping(value = "/country", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateCountry(@RequestParam String authToken, @RequestParam String countryId, @RequestBody Country updatedCountry) {
		Country country = idmasterService.updateCountry(countryId, updatedCountry, authToken);
		log.info("updateCountry::: " + country);
		return new ResponseEntity<>(country, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Country") // label for swagger
    @RequestMapping(value = "/country/{countryId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCountry(@RequestParam String countryId, @RequestParam String authToken) {
		boolean isCountryDeleted = idmasterService.deleteCountry(countryId, authToken);
		log.info("isCountryDeleted::: " + isCountryDeleted);
		return new ResponseEntity<>(isCountryDeleted, HttpStatus.OK);
	}
    
    /* --------------------------------State-------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Get All States") // label for swagger
    @RequestMapping(value = "/state", method = RequestMethod.GET)
	public ResponseEntity<?> getStates(@RequestParam String authToken) {
		State[] state = idmasterService.getStates(authToken);
		log.info("getStates::: " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get State") // label for swagger
    @RequestMapping(value = "/state/{stateId}", method = RequestMethod.GET)
	public ResponseEntity<?> getState(@RequestParam String stateId, @RequestParam String authToken) {
		State state = idmasterService.getState(stateId, authToken);
		log.info("getState::: " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
	@ApiOperation(response = Optional.class, value = "Create new State") // label for swagger
    @RequestMapping(value = "/state", method = RequestMethod.POST)
	public ResponseEntity<?> createState(@RequestParam String authToken, @RequestBody State newState) {
		State state = idmasterService.addState(newState, authToken);
		log.info("createState::: " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update State") // label for swagger
    @RequestMapping(value = "/state", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateState(@RequestParam String authToken, @RequestParam String stateId, @RequestBody State updatedState) {
		State state = idmasterService.updateState(stateId, updatedState, authToken);
		log.info("updateState::: " + state);
		return new ResponseEntity<>(state, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get State") // label for swagger
    @RequestMapping(value = "/state/{stateId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteState(@RequestParam String stateId, @RequestParam String authToken) {
		boolean isStateDeleted = idmasterService.deleteState(stateId, authToken);
		log.info("isStateDeleted::: " + isStateDeleted);
		return new ResponseEntity<>(isStateDeleted, HttpStatus.OK);
	}
    
    /* --------------------------------Currency-------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Get All Currency") // label for swagger
    @RequestMapping(value = "/currency", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrencies(@RequestParam String authToken) {
		Currency[] currency = idmasterService.getCurrencies(authToken);
		log.info("getCities::: " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Currency") // label for swagger
    @RequestMapping(value = "/currency/{currencyId}", method = RequestMethod.GET)
	public ResponseEntity<?> getCurrency(@RequestParam String currencyId, @RequestParam String authToken) {
		Currency currency = idmasterService.getCurrency(currencyId, authToken);
		log.info("getCurrency::: " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
	@ApiOperation(response = Optional.class, value = "Create new Currency") // label for swagger
    @RequestMapping(value = "/currency", method = RequestMethod.POST)
	public ResponseEntity<?> createCurrency(@RequestParam String authToken, @RequestBody Currency newCurrency) {
		Currency currency = idmasterService.addCurrency(newCurrency, authToken);
		log.info("createCurrency::: " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update Currency") // label for swagger
    @RequestMapping(value = "/currency", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateCurrency(@RequestParam String authToken, @RequestParam String currencyId, @RequestBody Currency updatedCurrency) {
		Currency currency = idmasterService.updateCurrency(currencyId, updatedCurrency, authToken);
		log.info("updateCurrency::: " + currency);
		return new ResponseEntity<>(currency, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Currency") // label for swagger
    @RequestMapping(value = "/currency/{currencyId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteCurrency(@RequestParam String currencyId, @RequestParam String authToken) {
		boolean isCurrencyDeleted = idmasterService.deleteCurrency(currencyId, authToken);
		log.info("isCurrencyDeleted::: " + isCurrencyDeleted);
		return new ResponseEntity<>(isCurrencyDeleted, HttpStatus.OK);
	}
    
    /* --------------------------------Vertical-------------------------------------------------------------------------*/
	
    @ApiOperation(response = Optional.class, value = "Get All Verticals") // label for swagger
    @RequestMapping(value = "/vetical", method = RequestMethod.GET)
	public ResponseEntity<?> getVeticals(@RequestParam String authToken) {
		Vertical[] veticals = idmasterService.getVerticals(authToken);
		log.info("getVeticals::: " + veticals);
		return new ResponseEntity<>(veticals, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Vertical") // label for swagger
    @RequestMapping(value = "/vetical/{verticalId}", method = RequestMethod.GET)
	public ResponseEntity<?> getVetical(@RequestParam String verticalId, @RequestParam String authToken) {
		Vertical vertical = idmasterService.getVertical(verticalId, authToken);
		log.info("getVetical::: " + vertical);
		return new ResponseEntity<>(vertical, HttpStatus.OK);
	}
    
	@ApiOperation(response = Optional.class, value = "Create new Vertical") // label for swagger
    @RequestMapping(value = "/vertical", method = RequestMethod.POST)
	public ResponseEntity<?> createVertical(@RequestParam String authToken, @RequestBody Vertical newVertical) {
		Vertical vertical = idmasterService.addVertical(newVertical, authToken);
		log.info("createVertical::: " + vertical);
		return new ResponseEntity<>(vertical, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Update Vertical") // label for swagger
    @RequestMapping(value = "/vertical", method = RequestMethod.PATCH)
	public ResponseEntity<?> updateVertical(@RequestParam String authToken, @RequestParam String verticalId, @RequestBody Vertical updatedVertical) {
		Vertical vertical = idmasterService.updateVertical(verticalId, updatedVertical, authToken);
		log.info("updateVertical::: " + vertical);
		return new ResponseEntity<>(vertical, HttpStatus.OK);
	}
    
    @ApiOperation(response = Optional.class, value = "Get Vertical") // label for swagger
    @RequestMapping(value = "/vertical/{verticalId}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVertical(@RequestParam String verticalId, @RequestParam String authToken) {
		boolean isVerticalDeleted = idmasterService.deleteVertical(verticalId, authToken);
		log.info("isVerticalDeleted::: " + isVerticalDeleted);
		return new ResponseEntity<>(isVerticalDeleted, HttpStatus.OK);
	}
    
    /*
	 * --------------------------------BarcodeSubTypeId---------------------------------
	 */
	@ApiOperation(response = BarcodeSubTypeId.class, value = "Get all BarcodeSubTypeId details") // label for swagger
	@GetMapping("/barcodesubtypeid")
	public ResponseEntity<?> getBarcodeSubTypeIds(@RequestParam String authToken) {
		BarcodeSubTypeId[] barcodeSubTypeIdList = idmasterService.getBarcodeSubTypeIds(authToken);
		return new ResponseEntity<>(barcodeSubTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeSubTypeId.class, value = "Get a BarcodeSubTypeId") // label for swagger
	@GetMapping("/barcodesubtypeid/{barcodeSubTypeId}")
	public ResponseEntity<?> getBarcodeSubTypeId(@PathVariable Long barcodeSubTypeId, 
			@RequestParam String warehouseId, @RequestParam Long barcodeTypeId,  @RequestParam String authToken) {
		BarcodeSubTypeId dbBarcodeSubTypeId = 
				idmasterService.getBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, authToken);
		log.info("BarcodeSubTypeId : " + dbBarcodeSubTypeId);
		return new ResponseEntity<>(dbBarcodeSubTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeSubTypeId.class, value = "Create BarcodeSubTypeId") // label for swagger
	@PostMapping("/barcodesubtypeid")
	public ResponseEntity<?> postBarcodeSubTypeId(@Valid @RequestBody BarcodeSubTypeId newBarcodeSubTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		BarcodeSubTypeId createdBarcodeSubTypeId = idmasterService.createBarcodeSubTypeId(newBarcodeSubTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdBarcodeSubTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeSubTypeId.class, value = "Update BarcodeSubTypeId") // label for swagger
	@RequestMapping(value = "/barcodesubtypeid/{barcodeSubTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchBarcodeSubTypeId(@PathVariable Long barcodeSubTypeId, @RequestParam String warehouseId, 
			@RequestParam Long barcodeTypeId, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody BarcodeSubTypeId updateBarcodeSubTypeId) throws IllegalAccessException, InvocationTargetException {
		BarcodeSubTypeId updatedBarcodeSubTypeId = 
				idmasterService.updateBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, loginUserID, updateBarcodeSubTypeId, authToken);
		return new ResponseEntity<>(updatedBarcodeSubTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeSubTypeId.class, value = "Delete BarcodeSubTypeId") // label for swagger
	@DeleteMapping("/barcodesubtypeid/{barcodeSubTypeId}")
	public ResponseEntity<?> deleteBarcodeSubTypeId(@PathVariable Long barcodeSubTypeId, @RequestParam String warehouseId, @RequestParam Long barcodeTypeId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteBarcodeSubTypeId(warehouseId, barcodeTypeId, barcodeSubTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------BarcodeTypeId---------------------------------
	 */
	@ApiOperation(response = BarcodeTypeId.class, value = "Get all BarcodeTypeId details") // label for swagger
	@GetMapping("/barcodetypeid")
	public ResponseEntity<?> getBarcodeTypeIds(@RequestParam String authToken) {
		BarcodeTypeId[] barcodeTypeIdList = idmasterService.getBarcodeTypeIds(authToken);
		return new ResponseEntity<>(barcodeTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeTypeId.class, value = "Get a BarcodeTypeId") // label for swagger
	@GetMapping("/barcodetypeid/{barcodeTypeId}")
	public ResponseEntity<?> getBarcodeTypeId(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,  @RequestParam String authToken) {
		BarcodeTypeId dbBarcodeTypeId = idmasterService.getBarcodeTypeId(warehouseId, barcodeTypeId, authToken);
		log.info("BarcodeTypeId : " + dbBarcodeTypeId);
		return new ResponseEntity<>(dbBarcodeTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeTypeId.class, value = "Create BarcodeTypeId") // label for swagger
	@PostMapping("/barcodetypeid")
	public ResponseEntity<?> postBarcodeTypeId(@Valid @RequestBody BarcodeTypeId newBarcodeTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId createdBarcodeTypeId = idmasterService.createBarcodeTypeId(newBarcodeTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdBarcodeTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeTypeId.class, value = "Update BarcodeTypeId") // label for swagger
	@RequestMapping(value = "/barcodetypeid/{barcodeTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchBarcodeTypeId(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody BarcodeTypeId updateBarcodeTypeId)
			throws IllegalAccessException, InvocationTargetException {
		BarcodeTypeId updatedBarcodeTypeId = 
				idmasterService.updateBarcodeTypeId(warehouseId, barcodeTypeId, loginUserID, updateBarcodeTypeId, authToken);
		return new ResponseEntity<>(updatedBarcodeTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = BarcodeTypeId.class, value = "Delete BarcodeTypeId") // label for swagger
	@DeleteMapping("/barcodetypeid/{barcodeTypeId}")
	public ResponseEntity<?> deleteBarcodeTypeId(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteBarcodeTypeId(warehouseId, barcodeTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------CompanyId---------------------------------
	 */
	@ApiOperation(response = CompanyId.class, value = "Get all CompanyId details") // label for swagger
	@GetMapping("/companyid")
	public ResponseEntity<?> getCompanyIds(@RequestParam String authToken) {
		CompanyId[] companyCodeIdList = idmasterService.getCompanyIds(authToken);
		return new ResponseEntity<>(companyCodeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Get a CompanyId") // label for swagger
	@GetMapping("/companyid/{companyCodeId}")
	public ResponseEntity<?> getCompanyId(@PathVariable String companyCodeId,  @RequestParam String authToken) {
		CompanyId dbCompanyId = idmasterService.getCompanyId(companyCodeId, authToken);
		log.info("CompanyId : " + dbCompanyId);
		return new ResponseEntity<>(dbCompanyId, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Create CompanyId") // label for swagger
	@PostMapping("/companyid")
	public ResponseEntity<?> postCompanyId(@Valid @RequestBody CompanyId newCompanyId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		CompanyId createdCompanyId = idmasterService.createCompanyId(newCompanyId, loginUserID, authToken);
		return new ResponseEntity<>(createdCompanyId, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Update CompanyId") // label for swagger
	@RequestMapping(value = "/companyid/{companyCodeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchCompanyId(@PathVariable String companyCodeId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateCompanyId updateCompanyId)
			throws IllegalAccessException, InvocationTargetException {
		CompanyId updatedCompanyId = 
				idmasterService.updateCompanyId(companyCodeId, loginUserID, updateCompanyId, authToken);
		return new ResponseEntity<>(updatedCompanyId, HttpStatus.OK);
	}

	@ApiOperation(response = CompanyId.class, value = "Delete CompanyId") // label for swagger
	@DeleteMapping("/companyid/{companyCodeId}")
	public ResponseEntity<?> deleteCompanyId(@PathVariable String companyCodeId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteCompanyId(companyCodeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------FloorId---------------------------------
	 */
	@ApiOperation(response = FloorId.class, value = "Get all FloorId details") // label for swagger
	@GetMapping("/floorid")
	public ResponseEntity<?> getFloorIds(@RequestParam String authToken) {
		FloorId[] floorIdList = idmasterService.getFloorIds(authToken);
		return new ResponseEntity<>(floorIdList, HttpStatus.OK);
	}

	@ApiOperation(response = FloorId.class, value = "Get a FloorId") // label for swagger
	@GetMapping("/floorid/{floorId}")
	public ResponseEntity<?> getFloorId(@PathVariable Long floorId, @RequestParam String warehouseId,  @RequestParam String authToken) {
		FloorId dbFloorId = idmasterService.getFloorId(warehouseId, floorId, authToken);
		log.info("FloorId : " + dbFloorId);
		return new ResponseEntity<>(dbFloorId, HttpStatus.OK);
	}

	@ApiOperation(response = FloorId.class, value = "Create FloorId") // label for swagger
	@PostMapping("/floorid")
	public ResponseEntity<?> postFloorId(@Valid @RequestBody FloorId newFloorId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		FloorId createdFloorId = idmasterService.createFloorId(newFloorId, loginUserID, authToken);
		return new ResponseEntity<>(createdFloorId, HttpStatus.OK);
	}

	@ApiOperation(response = FloorId.class, value = "Update FloorId") // label for swagger
	@RequestMapping(value = "/floorid/{floorId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchFloorId(@PathVariable Long floorId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody FloorId updateFloorId)
			throws IllegalAccessException, InvocationTargetException {
		FloorId updatedFloorId = 
				idmasterService.updateFloorId(warehouseId, floorId, loginUserID, updateFloorId, authToken);
		return new ResponseEntity<>(updatedFloorId, HttpStatus.OK);
	}

	@ApiOperation(response = FloorId.class, value = "Delete FloorId") // label for swagger
	@DeleteMapping("/floorid/{floorId}")
	public ResponseEntity<?> deleteFloorId(@PathVariable Long floorId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteFloorId(warehouseId, floorId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------ItemGroupId---------------------------------
	 */
	@ApiOperation(response = ItemGroupId.class, value = "Get all ItemGroupId details") // label for swagger
	@GetMapping("/itemgroupid")
	public ResponseEntity<?> getItemGroupIds(@RequestParam String authToken) {
		ItemGroupId[] itemGroupIdList = idmasterService.getItemGroupIds(authToken);
		return new ResponseEntity<>(itemGroupIdList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroupId.class, value = "Get a ItemGroupId") // label for swagger
	@GetMapping("/itemgroupid/{itemGroupId}")
	public ResponseEntity<?> getItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String authToken) {
		ItemGroupId dbItemGroupId = idmasterService.getItemGroupId(warehouseId, itemTypeId, itemGroupId, authToken);
		log.info("ItemGroupId : " + dbItemGroupId);
		return new ResponseEntity<>(dbItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroupId.class, value = "Create ItemGroupId") // label for swagger
	@PostMapping("/itemgroupid")
	public ResponseEntity<?> postItemGroupId(@Valid @RequestBody ItemGroupId newItemGroupId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ItemGroupId createdItemGroupId = idmasterService.createItemGroupId(newItemGroupId, loginUserID, authToken);
		return new ResponseEntity<>(createdItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroupId.class, value = "Update ItemGroupId") // label for swagger
	@RequestMapping(value = "/itemgroupid/{itemGroupId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId,
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody ItemGroupId updateItemGroupId)
			throws IllegalAccessException, InvocationTargetException {
		ItemGroupId updatedItemGroupId = 
				idmasterService.updateItemGroupId(warehouseId, itemTypeId, itemGroupId, loginUserID, updateItemGroupId, authToken);
		return new ResponseEntity<>(updatedItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroupId.class, value = "Delete ItemGroupId") // label for swagger
	@DeleteMapping("/itemgroupid/{itemGroupId}")
	public ResponseEntity<?> deleteItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId,
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteItemGroupId(warehouseId, itemTypeId, itemGroupId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------ItemTypeId---------------------------------
	 */
	@ApiOperation(response = ItemTypeId.class, value = "Get all ItemTypeId details") // label for swagger
	@GetMapping("/itemtypeid")
	public ResponseEntity<?> getItemTypeIds(@RequestParam String authToken) {
		ItemTypeId[] itemTypeIdList = idmasterService.getItemTypeIds(authToken);
		return new ResponseEntity<>(itemTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemTypeId.class, value = "Get a ItemTypeId") // label for swagger
	@GetMapping("/itemtypeid/{itemTypeId}")
	public ResponseEntity<?> getItemTypeId(@PathVariable Long itemTypeId, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
		ItemTypeId dbItemTypeId = idmasterService.getItemTypeId(warehouseId, itemTypeId, authToken);
		log.info("ItemTypeId : " + dbItemTypeId);
		return new ResponseEntity<>(dbItemTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemTypeId.class, value = "Create ItemTypeId") // label for swagger
	@PostMapping("/itemtypeid")
	public ResponseEntity<?> postItemTypeId(@Valid @RequestBody ItemTypeId newItemTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ItemTypeId createdItemTypeId = idmasterService.createItemTypeId(newItemTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdItemTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemTypeId.class, value = "Update ItemTypeId") // label for swagger
	@RequestMapping(value = "/itemtypeid/{itemTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchItemTypeId(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody ItemTypeId updateItemTypeId)
			throws IllegalAccessException, InvocationTargetException {
		ItemTypeId updatedItemTypeId = 
				idmasterService.updateItemTypeId(warehouseId, itemTypeId, loginUserID, updateItemTypeId, authToken);
		return new ResponseEntity<>(updatedItemTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemTypeId.class, value = "Delete ItemTypeId") // label for swagger
	@DeleteMapping("/itemtypeid/{itemTypeId}")
	public ResponseEntity<?> deleteItemTypeId(@PathVariable Long itemTypeId, @RequestParam String warehouseId,
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteItemTypeId(warehouseId, itemTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------LevelId---------------------------------
	 */
	@ApiOperation(response = LevelId.class, value = "Get all LevelId details") // label for swagger
	@GetMapping("/levelid")
	public ResponseEntity<?> getLevelIds(@RequestParam String authToken) {
		LevelId[] levelIdList = idmasterService.getLevelIds(authToken);
		return new ResponseEntity<>(levelIdList, HttpStatus.OK);
	}

	@ApiOperation(response = LevelId.class, value = "Get a LevelId") // label for swagger
	@GetMapping("/levelid/{levelId}")
	public ResponseEntity<?> getLevelId(@PathVariable Long levelId, @RequestParam String warehouseId,  @RequestParam String level, 
			@RequestParam String authToken) {
		LevelId dbLevelId = idmasterService.getLevelId(warehouseId, levelId, level, authToken);
		log.info("LevelId : " + dbLevelId);
		return new ResponseEntity<>(dbLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = LevelId.class, value = "Create LevelId") // label for swagger
	@PostMapping("/levelid")
	public ResponseEntity<?> postLevelId(@Valid @RequestBody LevelId newLevelId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		LevelId createdLevelId = idmasterService.createLevelId(newLevelId, loginUserID, authToken);
		return new ResponseEntity<>(createdLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = LevelId.class, value = "Update LevelId") // label for swagger
	@RequestMapping(value = "/levelid/{levelId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchLevelId(@PathVariable Long levelId, @RequestParam String warehouseId,  @RequestParam String level, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody LevelId updateLevelId)
			throws IllegalAccessException, InvocationTargetException {
		LevelId updatedLevelId = 
				idmasterService.updateLevelId(warehouseId, levelId, level, loginUserID, updateLevelId, authToken);
		return new ResponseEntity<>(updatedLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = LevelId.class, value = "Delete LevelId") // label for swagger
	@DeleteMapping("/levelid/{levelId}")
	public ResponseEntity<?> deleteLevelId(@PathVariable Long levelId, @RequestParam String warehouseId,  @RequestParam String level, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteLevelId(warehouseId, levelId, level, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------MenuId-----------------------------------------------------------------------------------
	 */
	@ApiOperation(response = MenuId.class, value = "Get all MenuId details") // label for swagger
	@GetMapping("/menuid")
	public ResponseEntity<?> getMenuIds(@RequestParam String authToken) {
		MenuId[] menuIdList = idmasterService.getMenuIds(authToken);
		return new ResponseEntity<>(menuIdList, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Get a MenuId") // label for swagger
	@GetMapping("/menuid/{menuId}")
	public ResponseEntity<?> getMenuId(@RequestParam String warehouseId,@PathVariable Long menuId,
									   @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId,
									   @RequestParam String companyCodeId,@RequestParam String languageId,
									   @RequestParam String plantId,@RequestParam String authToken) {

		MenuId dbMenuId = idmasterService.getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId,
													companyCodeId,languageId,plantId,authToken);
		log.info("MenuId : " + dbMenuId);
		return new ResponseEntity<>(dbMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Create MenuId") // label for swagger
	@PostMapping("/menuid")
	public ResponseEntity<?> postMenuId(@Valid @RequestBody AddMenuId newMenuId, @RequestParam String loginUserID,
										@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		MenuId createdMenuId = idmasterService.createMenuId(newMenuId, loginUserID, authToken);
		return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Create MenuId Bulk") // label for swagger
	@PostMapping("/menuid/bulk")
	public ResponseEntity<?> postMenuIdBulk(@Valid @RequestBody List<AddMenuId> newMenuId, @RequestParam String loginUserID,
										@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		MenuId[] createdMenuId = idmasterService.createMenuIdBulk(newMenuId, loginUserID, authToken);
		return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Update MenuId") // label for swagger
	@RequestMapping(value = "/menuid/{menuId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchMenuId( @RequestParam String warehouseId,@PathVariable Long menuId,
										  @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId,
										  @RequestParam String companyCodeId,@RequestParam String languageId,
										  @RequestParam String plantId,@RequestParam String loginUserID,
										  @Valid @RequestBody UpdateMenuId updateMenuId, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {

		MenuId updatedMenuId =
				idmasterService.updateMenuId(warehouseId, menuId, subMenuId,
											authorizationObjectId,companyCodeId,languageId,plantId,
											loginUserID, updateMenuId, authToken);
		return new ResponseEntity<>(updatedMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Delete MenuId") // label for swagger
	@DeleteMapping("/menuid/{menuId}")
	public ResponseEntity<?> deleteMenuId(@RequestParam String warehouseId,@PathVariable Long menuId,
										  @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId,
										  @RequestParam String companyCodeId,@RequestParam String languageId,
										  @RequestParam String plantId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteMenuId(warehouseId, menuId, subMenuId, authorizationObjectId,
									companyCodeId,languageId,plantId,loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//FIND
	@ApiOperation(response = MenuId[].class, value = "Find MenuId")//label for swagger
	@PostMapping("/menuid/findMenuId")
	public MenuId[] findMenuId(@RequestBody FindMenuId findMenuId,
							   @RequestParam String authToken)throws Exception{
		return idmasterService.findMenuId(findMenuId,authToken);
	}

	/*
	 * --------------------------------PlantId---------------------------------
	 */
	@ApiOperation(response = PlantId.class, value = "Get all PlantId details") // label for swagger
	@GetMapping("/plantid")
	public ResponseEntity<?> getPlantIds(@RequestParam String authToken) {
		PlantId[] plantIdList = idmasterService.getPlantIds(authToken);
		return new ResponseEntity<>(plantIdList, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Get a PlantId") // label for swagger
	@GetMapping("/plantid/{plantId}")
	public ResponseEntity<?> getPlantId(@PathVariable String plantId, @RequestParam String authToken) {
		PlantId dbPlantId = idmasterService.getPlantId(plantId, authToken);
		log.info("PlantId : " + dbPlantId);
		return new ResponseEntity<>(dbPlantId, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Create PlantId") // label for swagger
	@PostMapping("/plantid")
	public ResponseEntity<?> postPlantId(@Valid @RequestBody PlantId newPlantId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PlantId createdPlantId = idmasterService.createPlantId(newPlantId, loginUserID, authToken);
		return new ResponseEntity<>(createdPlantId, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Update PlantId") // label for swagger
	@RequestMapping(value = "/plantid/{plantId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchPlantId(@PathVariable String plantId, @RequestParam String companyCodeId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody PlantId updatePlantId)
			throws IllegalAccessException, InvocationTargetException {
		PlantId updatedPlantId = idmasterService.updatePlantId(plantId, loginUserID, updatePlantId, authToken);
		return new ResponseEntity<>(updatedPlantId, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Delete PlantId") // label for swagger
	@DeleteMapping("/plantid/{plantId}")
	public ResponseEntity<?> deletePlantId(@PathVariable String plantId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deletePlantId(plantId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------ProcessSequenceId---------------------------------
	 */
	@ApiOperation(response = ProcessSequenceId.class, value = "Get all ProcessSequenceId details") // label for swagger
	@GetMapping("/processsequenceid")
	public ResponseEntity<?> getProcessSequenceIds(@RequestParam String authToken) {
		ProcessSequenceId[] processIdList = idmasterService.getProcessSequenceIds(authToken);
		return new ResponseEntity<>(processIdList, HttpStatus.OK);
	}

	@ApiOperation(response = ProcessSequenceId.class, value = "Get a ProcessSequenceId") // label for swagger
	@GetMapping("/processsequenceid/{processId}")
	public ResponseEntity<?> getProcessSequenceId(@PathVariable Long processId, @RequestParam String warehouseId, 
			@RequestParam Long subLevelId, @RequestParam String authToken) {
		ProcessSequenceId dbProcessSequenceId = 
				idmasterService.getProcessSequenceId(warehouseId, processId, subLevelId, authToken);
		log.info("ProcessSequenceId : " + dbProcessSequenceId);
		return new ResponseEntity<>(dbProcessSequenceId, HttpStatus.OK);
	}

	@ApiOperation(response = ProcessSequenceId.class, value = "Create ProcessSequenceId") // label for swagger
	@PostMapping("/processsequenceid")
	public ResponseEntity<?> postProcessSequenceId(@Valid @RequestBody ProcessSequenceId newProcessSequenceId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId createdProcessSequenceId = idmasterService.createProcessSequenceId(newProcessSequenceId, loginUserID, authToken);
		return new ResponseEntity<>(createdProcessSequenceId, HttpStatus.OK);
	}

	@ApiOperation(response = ProcessSequenceId.class, value = "Update ProcessSequenceId") // label for swagger
	@RequestMapping(value = "/processsequenceid/{processId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchProcessSequenceId(@PathVariable Long processId, @RequestParam String warehouseId, @RequestParam Long subLevelId,  
			@RequestParam String loginUserID,@RequestParam String authToken, @Valid @RequestBody ProcessSequenceId updateProcessSequenceId)
			throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId updatedProcessSequenceId = 
				idmasterService.updateProcessSequenceId(warehouseId, processId, subLevelId,
						loginUserID, updateProcessSequenceId, authToken);
		return new ResponseEntity<>(updatedProcessSequenceId, HttpStatus.OK);
	}

	@ApiOperation(response = ProcessSequenceId.class, value = "Delete ProcessSequenceId") // label for swagger
	@DeleteMapping("/processsequenceid/{processId}")
	public ResponseEntity<?> deleteProcessSequenceId(@PathVariable Long processId, @RequestParam String warehouseId, @RequestParam Long subLevelId,
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteProcessSequenceId(warehouseId, processId, subLevelId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------RowId---------------------------------
	 */
	@ApiOperation(response = RowId.class, value = "Get all RowId details") // label for swagger
	@GetMapping("/rowid")
	public ResponseEntity<?> getRowIds(@RequestParam String authToken) {
		RowId[] rowIdList = idmasterService.getRowIds(authToken);
		return new ResponseEntity<>(rowIdList, HttpStatus.OK);
	}

	@ApiOperation(response = RowId.class, value = "Get a RowId") // label for swagger
	@GetMapping("/rowid/{rowId}")
	public ResponseEntity<?> getRowId(@PathVariable String rowId, @RequestParam String warehouseId, @RequestParam Long floorId, 
			@RequestParam Long storageSectionId,  @RequestParam String authToken) {
		RowId dbRowId = idmasterService.getRowId(warehouseId, floorId, storageSectionId, rowId, authToken);
		log.info("RowId : " + dbRowId);
		return new ResponseEntity<>(dbRowId, HttpStatus.OK);
	}

	@ApiOperation(response = RowId.class, value = "Create RowId") // label for swagger
	@PostMapping("/rowid")
	public ResponseEntity<?> postRowId(@Valid @RequestBody RowId newRowId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		RowId createdRowId = idmasterService.createRowId(newRowId, loginUserID, authToken);
		return new ResponseEntity<>(createdRowId, HttpStatus.OK);
	}

	@ApiOperation(response = RowId.class, value = "Update RowId") // label for swagger
	@RequestMapping(value = "/rowid/{rowId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchRowId(@PathVariable String rowId, @RequestParam String warehouseId, @RequestParam Long floorId, 
			@RequestParam Long storageSectionId, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody RowId updateRowId)
			throws IllegalAccessException, InvocationTargetException {
		RowId updatedRowId = 
				idmasterService.updateRowId(warehouseId, floorId, storageSectionId, rowId, loginUserID, updateRowId, authToken);
		return new ResponseEntity<>(updatedRowId, HttpStatus.OK);
	}

	@ApiOperation(response = RowId.class, value = "Delete RowId") // label for swagger
	@DeleteMapping("/rowid/{rowId}")
	public ResponseEntity<?> deleteRowId(@PathVariable String rowId, @RequestParam String warehouseId, @RequestParam Long floorId, 
			@RequestParam Long storageSectionId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteRowId(warehouseId, floorId, storageSectionId, rowId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StatusId----------------------------------------------------------------------------
	 */
	@ApiOperation(response = StatusId.class, value = "Get all StatusId details") // label for swagger
	@GetMapping("/statusid")
	public ResponseEntity<?> getStatusIds(@RequestParam String authToken) {
		StatusId[] statusIdList = idmasterService.getStatusIds(authToken);
		return new ResponseEntity<>(statusIdList, HttpStatus.OK);
	}

	@ApiOperation(response = StatusId.class, value = "Get a StatusId") // label for swagger
	@GetMapping("/statusid/{statusId}")
	public ResponseEntity<?> getStatusId(@PathVariable Long statusId, @RequestParam String warehouseId, @RequestParam String authToken) {
		StatusId dbStatusId = idmasterService.getStatusId(warehouseId, statusId, authToken);
		log.info("StatusId : " + dbStatusId);
		return new ResponseEntity<>(dbStatusId, HttpStatus.OK);
	}

	@ApiOperation(response = StatusId.class, value = "Create StatusId") // label for swagger
	@PostMapping("/statusid")
	public ResponseEntity<?> postStatusId(@Valid @RequestBody AddStatusId newStatusId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StatusId createdStatusId = idmasterService.createStatusId(newStatusId, loginUserID, authToken);
		return new ResponseEntity<>(createdStatusId, HttpStatus.OK);
	}

	@ApiOperation(response = StatusId.class, value = "Update StatusId") // label for swagger
	@RequestMapping(value = "/statusid/{statusId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStatusId(@PathVariable Long statusId,  @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateStatusId updateStatusId)
			throws IllegalAccessException, InvocationTargetException {
		StatusId updatedStatusId = 
				idmasterService.updateStatusId(warehouseId, statusId, loginUserID, updateStatusId, authToken);
		return new ResponseEntity<>(updatedStatusId, HttpStatus.OK);
	}

	@ApiOperation(response = StatusId.class, value = "Delete StatusId") // label for swagger
	@DeleteMapping("/statusid/{statusId}")
	public ResponseEntity<?> deleteStatusId(@PathVariable Long statusId,  @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStatusId(warehouseId, statusId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StorageBinTypeId---------------------------------
	 */
	@ApiOperation(response = StorageBinTypeId.class, value = "Get all StorageBinTypeId details") // label for swagger
	@GetMapping("/storagebintypeid")
	public ResponseEntity<?> getStorageBinTypeIds(@RequestParam String authToken) {
		StorageBinTypeId[] storageBinTypeIdList = idmasterService.getStorageBinTypeIds(authToken);
		return new ResponseEntity<>(storageBinTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageBinTypeId.class, value = "Get a StorageBinTypeId") // label for swagger
	@GetMapping("/storagebintypeid/{storageBinTypeId}")
	public ResponseEntity<?> getStorageBinTypeId(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, 
			@RequestParam Long storageClassId, @RequestParam Long storageTypeId,  @RequestParam String authToken) {
		StorageBinTypeId dbStorageBinTypeId = 
				idmasterService.getStorageBinTypeId(warehouseId, storageClassId, storageTypeId, storageBinTypeId, authToken);
		log.info("StorageBinTypeId : " + dbStorageBinTypeId);
		return new ResponseEntity<>(dbStorageBinTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageBinTypeId.class, value = "Create StorageBinTypeId") // label for swagger
	@PostMapping("/storagebintypeid")
	public ResponseEntity<?> postStorageBinTypeId(@Valid @RequestBody StorageBinTypeId newStorageBinTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StorageBinTypeId createdStorageBinTypeId = idmasterService.createStorageBinTypeId(newStorageBinTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageBinTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageBinTypeId.class, value = "Update StorageBinTypeId") // label for swagger
	@RequestMapping(value = "/storagebintypeid/{storageBinTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStorageBinTypeId(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId, @RequestParam Long storageTypeId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StorageBinTypeId updateStorageBinTypeId)
			throws IllegalAccessException, InvocationTargetException {
		StorageBinTypeId updatedStorageBinTypeId = 
				idmasterService.updateStorageBinTypeId(warehouseId, storageClassId, storageTypeId, storageBinTypeId, loginUserID, updateStorageBinTypeId, authToken);
		return new ResponseEntity<>(updatedStorageBinTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageBinTypeId.class, value = "Delete StorageBinTypeId") // label for swagger
	@DeleteMapping("/storagebintypeid/{storageBinTypeId}")
	public ResponseEntity<?> deleteStorageBinTypeId(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId, @RequestParam Long storageTypeId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStorageBinTypeId(warehouseId, storageClassId, storageTypeId, storageBinTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StorageClassId---------------------------------
	 */
	@ApiOperation(response = StorageClassId.class, value = "Get all StorageClassId details") // label for swagger
	@GetMapping("/storageclassid")
	public ResponseEntity<?> getStorageClassIds(@RequestParam String authToken) {
		StorageClassId[] storageClassIdList = idmasterService.getStorageClassIds(authToken);
		return new ResponseEntity<>(storageClassIdList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageClassId.class, value = "Get a StorageClassId") // label for swagger
	@GetMapping("/storageclassid/{storageClassId}")
	public ResponseEntity<?> getStorageClassId(@PathVariable Long storageClassId, @RequestParam String warehouseId,  
			@RequestParam String authToken) {
		StorageClassId dbStorageClassId = idmasterService.getStorageClassId(warehouseId, storageClassId, authToken);
		log.info("StorageClassId : " + dbStorageClassId);
		return new ResponseEntity<>(dbStorageClassId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageClassId.class, value = "Create StorageClassId") // label for swagger
	@PostMapping("/storageclassid")
	public ResponseEntity<?> postStorageClassId(@Valid @RequestBody StorageClassId newStorageClassId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StorageClassId createdStorageClassId = idmasterService.createStorageClassId(newStorageClassId, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageClassId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageClassId.class, value = "Update StorageClassId") // label for swagger
	@RequestMapping(value = "/storageclassid/{storageClassId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStorageClassId(@PathVariable Long storageClassId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StorageClassId updateStorageClassId)
			throws IllegalAccessException, InvocationTargetException {
		StorageClassId updatedStorageClassId = 
				idmasterService.updateStorageClassId(warehouseId, storageClassId, loginUserID, updateStorageClassId, authToken);
		return new ResponseEntity<>(updatedStorageClassId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageClassId.class, value = "Delete StorageClassId") // label for swagger
	@DeleteMapping("/storageclassid/{storageClassId}")
	public ResponseEntity<?> deleteStorageClassId(@PathVariable Long storageClassId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStorageClassId(warehouseId, storageClassId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StorageSectionId----------------------------------------------------------------
	 */
	@ApiOperation(response = StorageSectionId.class, value = "Get all StorageSectionId details") // label for swagger
	@GetMapping("/storagesectionid")
	public ResponseEntity<?> getStorageSectionIds(@RequestParam String authToken) {
		StorageSectionId[] storageSectionIdList = idmasterService.getStorageSectionIds(authToken);
		return new ResponseEntity<>(storageSectionIdList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Get a StorageSectionId") // label for swagger
	@GetMapping("/storagesectionid/{storageSectionId}")
	public ResponseEntity<?> getStorageSectionId(@PathVariable String storageSectionId, @RequestParam String warehouseId,
			@RequestParam Long floorId, @RequestParam String storageSection,  @RequestParam String authToken) {
		StorageSectionId dbStorageSectionId = idmasterService.getStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, authToken);
		log.info("StorageSectionId : " + dbStorageSectionId);
		return new ResponseEntity<>(dbStorageSectionId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Create StorageSectionId") // label for swagger
	@PostMapping("/storagesectionid")
	public ResponseEntity<?> postStorageSectionId(@Valid @RequestBody StorageSectionId newStorageSectionId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StorageSectionId createdStorageSectionId = idmasterService.createStorageSectionId(newStorageSectionId, loginUserID, authToken);
		return new ResponseEntity<>(createdStorageSectionId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Update StorageSectionId") // label for swagger
	@RequestMapping(value = "/storagesectionid/{storageSectionId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStorageSectionId(@PathVariable String storageSectionId, @RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSection,
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StorageSectionId updateStorageSectionId)
			throws IllegalAccessException, InvocationTargetException {
		StorageSectionId updatedStorageSectionId = 
				idmasterService.updateStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, loginUserID, 
						updateStorageSectionId, authToken);
		return new ResponseEntity<>(updatedStorageSectionId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Delete StorageSectionId") // label for swagger
	@DeleteMapping("/storagesectionid/{storageSectionId}")
	public ResponseEntity<?> deleteStorageSectionId(@PathVariable String storageSectionId, @RequestParam String warehouseId,
			@RequestParam Long floorId, @RequestParam String storageSection, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StrategyId---------------------------------------------------------------------------
	 */
	@ApiOperation(response = StrategyId.class, value = "Get all StrategyId details") // label for swagger
	@GetMapping("/strategyid")
	public ResponseEntity<?> getStrategyIds(@RequestParam String authToken) {
		StrategyId[] strategyNoList = idmasterService.getStrategyIds(authToken);
		return new ResponseEntity<>(strategyNoList, HttpStatus.OK);
	}

	@ApiOperation(response = StrategyId.class, value = "Get a StrategyId") // label for swagger
	@GetMapping("/strategyid/{strategyNo}")
	public ResponseEntity<?> getStrategyId(@PathVariable String strategyNo, @RequestParam String warehouseId, 
			@RequestParam Long strategyTypeId,  @RequestParam String authToken) {
		StrategyId dbStrategyId = idmasterService.getStrategyId(warehouseId, strategyTypeId, strategyNo, authToken);
		log.info("StrategyId : " + dbStrategyId);
		return new ResponseEntity<>(dbStrategyId, HttpStatus.OK);
	}

	@ApiOperation(response = StrategyId.class, value = "Create StrategyId") // label for swagger
	@PostMapping("/strategyid")
	public ResponseEntity<?> postStrategyId(@Valid @RequestBody StrategyId newStrategyId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StrategyId createdStrategyId = idmasterService.createStrategyId(newStrategyId, loginUserID, authToken);
		return new ResponseEntity<>(createdStrategyId, HttpStatus.OK);
	}

	@ApiOperation(response = StrategyId.class, value = "Update StrategyId") // label for swagger
	@RequestMapping(value = "/strategyid/{strategyNo}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStrategyId(@PathVariable String strategyNo, @RequestParam String warehouseId, 
			@RequestParam Long strategyTypeId, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody StrategyId updateStrategyId)
			throws IllegalAccessException, InvocationTargetException {
		StrategyId updatedStrategyId = 
				idmasterService.updateStrategyId(warehouseId, strategyTypeId, strategyNo, loginUserID, updateStrategyId, authToken);
		return new ResponseEntity<>(updatedStrategyId, HttpStatus.OK);
	}

	@ApiOperation(response = StrategyId.class, value = "Delete StrategyId") // label for swagger
	@DeleteMapping("/strategyid/{strategyNo}")
	public ResponseEntity<?> deleteStrategyId(@PathVariable String strategyNo, @RequestParam String warehouseId, @RequestParam Long strategyTypeId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStrategyId(warehouseId, strategyTypeId, strategyNo, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------StroageTypeId---------------------------------
	 */
	@ApiOperation(response = StroageTypeId.class, value = "Get all StroageTypeId details") // label for swagger
	@GetMapping("/stroagetypeid")
	public ResponseEntity<?> getStroageTypeIds(@RequestParam String authToken) {
		StroageTypeId[] storageTypeIdList = idmasterService.getStroageTypeIds(authToken);
		return new ResponseEntity<>(storageTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = StroageTypeId.class, value = "Get a StroageTypeId") // label for swagger
	@GetMapping("/stroagetypeid/{storageTypeId}")
	public ResponseEntity<?> getStroageTypeId(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId,  @RequestParam String authToken) {
		StroageTypeId dbStroageTypeId = idmasterService.getStroageTypeId(warehouseId, storageClassId, storageTypeId, authToken);
		log.info("StroageTypeId : " + dbStroageTypeId);
		return new ResponseEntity<>(dbStroageTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StroageTypeId.class, value = "Create StroageTypeId") // label for swagger
	@PostMapping("/stroagetypeid")
	public ResponseEntity<?> postStroageTypeId(@Valid @RequestBody StroageTypeId newStroageTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StroageTypeId createdStroageTypeId = idmasterService.createStroageTypeId(newStroageTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdStroageTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StroageTypeId.class, value = "Update StroageTypeId") // label for swagger
	@RequestMapping(value = "/stroagetypeid/{storageTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStroageTypeId(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StroageTypeId updateStroageTypeId)
			throws IllegalAccessException, InvocationTargetException {
		StroageTypeId updatedStroageTypeId = 
				idmasterService.updateStroageTypeId(warehouseId, storageClassId, storageTypeId, loginUserID, updateStroageTypeId, authToken);
		return new ResponseEntity<>(updatedStroageTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = StroageTypeId.class, value = "Delete StroageTypeId") // label for swagger
	@DeleteMapping("/stroagetypeid/{storageTypeId}")
	public ResponseEntity<?> deleteStroageTypeId(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteStroageTypeId(warehouseId, storageClassId, storageTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------SubItemGroupId---------------------------------
	 */
	@ApiOperation(response = SubItemGroupId.class, value = "Get all SubItemGroupId details") // label for swagger
	@GetMapping("/subitemgroupid")
	public ResponseEntity<?> getSubItemGroupIds(@RequestParam String authToken) {
		SubItemGroupId[] subitemgroupidList = idmasterService.getSubItemGroupIds(authToken);
		return new ResponseEntity<>(subitemgroupidList, HttpStatus.OK);
	}

	@ApiOperation(response = SubItemGroupId.class, value = "Get a SubItemGroupId") // label for swagger
	@GetMapping("/subitemgroupid/{subItemGroupId}")
	public ResponseEntity<?> getSubItemGroupId(@PathVariable Long subItemGroupId, @RequestParam String warehouseId, 
			@RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup, 
			@RequestParam String authToken) {
		SubItemGroupId dbSubItemGroupId = 
				idmasterService.getSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup, authToken);
		log.info("SubItemGroupId : " + dbSubItemGroupId);
		return new ResponseEntity<>(dbSubItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = SubItemGroupId.class, value = "Create SubItemGroupId") // label for swagger
	@PostMapping("/subitemgroupid")
	public ResponseEntity<?> postSubItemGroupId(@Valid @RequestBody SubItemGroupId newSubItemGroupId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId createdSubItemGroupId = idmasterService.createSubItemGroupId(newSubItemGroupId, loginUserID, authToken);
		return new ResponseEntity<>(createdSubItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = SubItemGroupId.class, value = "Update SubItemGroupId") // label for swagger
	@RequestMapping(value = "/subitemgroupid/{subItemGroupId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchSubItemGroupId(@PathVariable Long subItemGroupId, @RequestParam String warehouseId, 
			@RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody SubItemGroupId updateSubItemGroupId)
			throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId updatedSubItemGroupId = 
				idmasterService.updateSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup, loginUserID,
						updateSubItemGroupId, authToken);
		return new ResponseEntity<>(updatedSubItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = SubItemGroupId.class, value = "Delete SubItemGroupId") // label for swagger
	@DeleteMapping("/subitemgroupid/{subItemGroupId}")
	public ResponseEntity<?> deleteSubItemGroupId(@PathVariable Long subItemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------UomId---------------------------------
	 */
	@ApiOperation(response = UomId.class, value = "Get all UomId details") // label for swagger
	@GetMapping("/uomid")
	public ResponseEntity<?> getUomIds(@RequestParam String authToken) {
		UomId[] uomIdList = idmasterService.getUomIds(authToken);
		return new ResponseEntity<>(uomIdList, HttpStatus.OK);
	}

	@ApiOperation(response = UomId.class, value = "Get a UomId") // label for swagger
	@GetMapping("/uomid/{uomId}")
	public ResponseEntity<?> getUomId(@PathVariable String uomId, @RequestParam String authToken) {
		UomId dbUomId = idmasterService.getUomId(uomId, authToken);
		log.info("UomId : " + dbUomId);
		return new ResponseEntity<>(dbUomId, HttpStatus.OK);
	}

	@ApiOperation(response = UomId.class, value = "Create UomId") // label for swagger
	@PostMapping("/uomid")
	public ResponseEntity<?> postUomId(@Valid @RequestBody UomId newUomId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		UomId createdUomId = idmasterService.createUomId(newUomId, loginUserID, authToken);
		return new ResponseEntity<>(createdUomId, HttpStatus.OK);
	}

	@ApiOperation(response = UomId.class, value = "Update UomId") // label for swagger
	@RequestMapping(value = "/uomid/{uomId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchUomId(@PathVariable String uomId, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody UomId updateUomId)
			throws IllegalAccessException, InvocationTargetException {
		UomId updatedUomId = idmasterService.updateUomId(uomId, loginUserID, updateUomId, authToken);
		return new ResponseEntity<>(updatedUomId, HttpStatus.OK);
	}

	@ApiOperation(response = UomId.class, value = "Delete UomId") // label for swagger
	@DeleteMapping("/uomid/{uomId}")
	public ResponseEntity<?> deleteUomId(@PathVariable String uomId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteUomId(uomId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------UserTypeId-----------------------------------------------------------------------
	 */
	@ApiOperation(response = UserTypeId.class, value = "Get all UserTypeId details") // label for swagger
	@GetMapping("/usertypeid")
	public ResponseEntity<?> getUserTypeIds(@RequestParam String authToken) {
		UserTypeId[] userTypeIdList = idmasterService.getUserTypeIds(authToken);
		return new ResponseEntity<>(userTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = UserTypeId.class, value = "Get a UserTypeId") // label for swagger
	@GetMapping("/usertypeid/{userTypeId}")
	public ResponseEntity<?> getUserTypeId(@PathVariable Long userTypeId, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
		UserTypeId dbUserTypeId = idmasterService.getUserTypeId(warehouseId, userTypeId, authToken);
		log.info("UserTypeId : " + dbUserTypeId);
		return new ResponseEntity<>(dbUserTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = UserTypeId.class, value = "Create UserTypeId") // label for swagger
	@PostMapping("/usertypeid")
	public ResponseEntity<?> postUserTypeId(@Valid @RequestBody UserTypeId newUserTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		UserTypeId createdUserTypeId = idmasterService.createUserTypeId(newUserTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdUserTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = UserTypeId.class, value = "Update UserTypeId") // label for swagger
	@RequestMapping(value = "/usertypeid/{userTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchUserTypeId(@PathVariable Long userTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UserTypeId updateUserTypeId)
			throws IllegalAccessException, InvocationTargetException {
		UserTypeId updatedUserTypeId = 
				idmasterService.updateUserTypeId(warehouseId, userTypeId, loginUserID, updateUserTypeId, authToken);
		return new ResponseEntity<>(updatedUserTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = UserTypeId.class, value = "Delete UserTypeId") // label for swagger
	@DeleteMapping("/usertypeid/{userTypeId}")
	public ResponseEntity<?> deleteUserTypeId(@PathVariable Long userTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteUserTypeId(warehouseId, userTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------VariantId---------------------------------
	 */
	@ApiOperation(response = VariantId.class, value = "Get all VariantId details") // label for swagger
	@GetMapping("/variantid")
	public ResponseEntity<?> getVariantIds(@RequestParam String authToken) {
		VariantId[] variantCodeList = idmasterService.getVariantIds(authToken);
		return new ResponseEntity<>(variantCodeList, HttpStatus.OK);
	}

	@ApiOperation(response = VariantId.class, value = "Get a VariantId") // label for swagger
	@GetMapping("/variantid/{variantCode}")
	public ResponseEntity<?> getVariantId(@PathVariable String variantCode, @RequestParam String warehouseId, 
			@RequestParam String variantType, @RequestParam String variantSubCode, @RequestParam String authToken) {
		VariantId dbVariantId = idmasterService.getVariantId(warehouseId, variantCode, variantType, variantSubCode, authToken);
		log.info("VariantId : " + dbVariantId);
		return new ResponseEntity<>(dbVariantId, HttpStatus.OK);
	}

	@ApiOperation(response = VariantId.class, value = "Create VariantId") // label for swagger
	@PostMapping("/variantid")
	public ResponseEntity<?> postVariantId(@Valid @RequestBody VariantId newVariantId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		VariantId createdVariantId = idmasterService.createVariantId(newVariantId, loginUserID, authToken);
		return new ResponseEntity<>(createdVariantId, HttpStatus.OK);
	}

	@ApiOperation(response = VariantId.class, value = "Update VariantId") // label for swagger
	@RequestMapping(value = "/variantid/{variantCode}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchVariantId(@PathVariable String variantCode, @RequestParam String warehouseId, @RequestParam String variantType, @RequestParam String variantSubCode, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody VariantId updateVariantId)
			throws IllegalAccessException, InvocationTargetException {
		VariantId updatedVariantId = 
				idmasterService.updateVariantId(warehouseId, variantCode, variantType, variantSubCode, loginUserID, updateVariantId, authToken);
		return new ResponseEntity<>(updatedVariantId, HttpStatus.OK);
	}

	@ApiOperation(response = VariantId.class, value = "Delete VariantId") // label for swagger
	@DeleteMapping("/variantid/{variantCode}")
	public ResponseEntity<?> deleteVariantId(@PathVariable String variantCode, @RequestParam String warehouseId, @RequestParam String variantType, @RequestParam String variantSubCode, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteVariantId(warehouseId, variantCode, variantType, variantSubCode, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------WarehouseId---------------------------------
	 */
	@ApiOperation(response = WarehouseId.class, value = "Get all WarehouseId details") // label for swagger
	@GetMapping("/warehouseid")
	public ResponseEntity<?> getWarehouseIds(@RequestParam String authToken) {
		WarehouseId[] warehouseIdList = idmasterService.getWarehouseIds(authToken);
		return new ResponseEntity<>(warehouseIdList, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseId.class, value = "Get a WarehouseId") // label for swagger
	@GetMapping("/warehouseid/{warehouseId}")
	public ResponseEntity<?> getWarehouseId(@PathVariable String warehouseId, @RequestParam String plantId, 
			@RequestParam String authToken) {
		WarehouseId dbWarehouseId = idmasterService.getWarehouseId(warehouseId, authToken);
		log.info("WarehouseId : " + dbWarehouseId);
		return new ResponseEntity<>(dbWarehouseId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseId.class, value = "Create WarehouseId") // label for swagger
	@PostMapping("/warehouseid")
	public ResponseEntity<?> postWarehouseId(@Valid @RequestBody WarehouseId newWarehouseId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		WarehouseId createdWarehouseId = idmasterService.createWarehouseId(newWarehouseId, loginUserID, authToken);
		return new ResponseEntity<>(createdWarehouseId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseId.class, value = "Update WarehouseId") // label for swagger
	@RequestMapping(value = "/warehouseid/{warehouseId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchWarehouseId(@PathVariable String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody WarehouseId updateWarehouseId)
			throws IllegalAccessException, InvocationTargetException {
		WarehouseId updatedWarehouseId = 
				idmasterService.updateWarehouseId(warehouseId, loginUserID, updateWarehouseId, authToken);
		return new ResponseEntity<>(updatedWarehouseId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseId.class, value = "Delete WarehouseId") // label for swagger
	@DeleteMapping("/warehouseid/{warehouseId}")
	public ResponseEntity<?> deleteWarehouseId(@PathVariable String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteWarehouseId(warehouseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------WarehouseTypeId---------------------------------
	 */
	@ApiOperation(response = WarehouseTypeId.class, value = "Get all WarehouseTypeId details") // label for swagger
	@GetMapping("/warehousetypeid")
	public ResponseEntity<?> getWarehouseTypeIds(@RequestParam String authToken) {
		WarehouseTypeId[] warehouseTypeIdList = idmasterService.getWarehouseTypeIds(authToken);
		return new ResponseEntity<>(warehouseTypeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseTypeId.class, value = "Get a WarehouseTypeId") // label for swagger
	@GetMapping("/warehousetypeid/{warehouseTypeId}")
	public ResponseEntity<?> getWarehouseTypeId(@PathVariable Long warehouseTypeId, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
		WarehouseTypeId dbWarehouseTypeId = idmasterService.getWarehouseTypeId(warehouseId, warehouseTypeId, authToken);
		log.info("WarehouseTypeId : " + dbWarehouseTypeId);
		return new ResponseEntity<>(dbWarehouseTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseTypeId.class, value = "Create WarehouseTypeId") // label for swagger
	@PostMapping("/warehousetypeid")
	public ResponseEntity<?> postWarehouseTypeId(@Valid @RequestBody WarehouseTypeId newWarehouseTypeId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		WarehouseTypeId createdWarehouseTypeId = idmasterService.createWarehouseTypeId(newWarehouseTypeId, loginUserID, authToken);
		return new ResponseEntity<>(createdWarehouseTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseTypeId.class, value = "Update WarehouseTypeId") // label for swagger
	@RequestMapping(value = "/warehousetypeid/{warehouseTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchWarehouseTypeId(@PathVariable Long warehouseTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody WarehouseTypeId updateWarehouseTypeId)
			throws IllegalAccessException, InvocationTargetException {
		WarehouseTypeId updatedWarehouseTypeId = 
				idmasterService.updateWarehouseTypeId(warehouseId, warehouseTypeId, loginUserID, updateWarehouseTypeId, authToken);
		return new ResponseEntity<>(updatedWarehouseTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = WarehouseTypeId.class, value = "Delete WarehouseTypeId") // label for swagger
	@DeleteMapping("/warehousetypeid/{warehouseTypeId}")
	public ResponseEntity<?> deleteWarehouseTypeId(@PathVariable Long warehouseTypeId, @RequestParam String warehouseId,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteWarehouseTypeId(warehouseId, warehouseTypeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------HhtUser---------------------------------
	 */
	@ApiOperation(response = HhtUserOutput.class, value = "Get all HhtUser details") // label for swagger
	@GetMapping("/hhtuser")
	public ResponseEntity<?> getHhtUsers(@RequestParam String authToken) {
		HhtUserOutput[] userIdList = idmasterService.getHhtUsers(authToken);
		return new ResponseEntity<>(userIdList, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUserOutput.class, value = "Get a HhtUser") // label for swagger
	@GetMapping("/hhtuser/{userId}")
	public ResponseEntity<?> getHhtUser(@PathVariable String userId,@RequestParam String companyCodeId,
										@RequestParam String languageId,@RequestParam String plantId, @RequestParam String warehouseId,
										@RequestParam Long levelId,@RequestParam String authToken) {

		HhtUserOutput dbHhtUser =
				idmasterService.getHhtUser(userId,warehouseId, companyCodeId,levelId,plantId,languageId, authToken);
		log.info("HhtUser : " + dbHhtUser);
		return new ResponseEntity<>(dbHhtUser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUserOutput.class, value = "Get HhtUsers") // label for swagger
	@GetMapping("/hhtuser/{warehouseId}/hhtUser")
	public ResponseEntity<?> getHhtUser(@PathVariable String warehouseId, @RequestParam String authToken) {
		HhtUserOutput[] hhtuser = idmasterService.getHhtUserByWarehouseId(warehouseId, authToken);
		log.info("HhtUser : " + hhtuser);
		return new ResponseEntity<>(hhtuser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Create HhtUser") // label for swagger
	@PostMapping("/hhtuser")
	public ResponseEntity<?> postHhtUser(@Valid @RequestBody AddHhtUser newHhtUser, @RequestParam String loginUserID,
										 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		HhtUser createdHhtUser = idmasterService.createHhtUser(newHhtUser, loginUserID, authToken);
		return new ResponseEntity<>(createdHhtUser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Update HhtUser") // label for swagger
	@RequestMapping(value = "/hhtuser/{userId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchHhtUser(@PathVariable String userId, @RequestParam String warehouseId,@RequestParam String companyCodeId,
										  @RequestParam String languageId,@RequestParam String plantId,@RequestParam Long levelId,
										  @RequestParam String loginUserID, @RequestParam String authToken,
										  @Valid @RequestBody UpdateHhtUser updateHhtUser) throws IllegalAccessException, InvocationTargetException {

		HhtUser updatedHhtUser = idmasterService.updateHhtUser(userId, warehouseId,companyCodeId,languageId,plantId,
				updateHhtUser,loginUserID,levelId,authToken);
		return new ResponseEntity<>(updatedHhtUser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Delete HhtUser") // label for swagger
	@DeleteMapping("/hhtuser/{userId}")
	public ResponseEntity<?> deleteHhtUser(@PathVariable String userId,@RequestParam String warehouseId,@RequestParam String companyCodeId,
										   @RequestParam String plantId,@RequestParam String languageId,@RequestParam Long levelId,
										   @RequestParam String loginUserID, @RequestParam String authToken) {

		idmasterService.deleteHhtUser(warehouseId, userId,companyCodeId,languageId,plantId,loginUserID,levelId,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//FIND
	@ApiOperation(response = HhtUserOutput[].class, value = "Find HhtUser")//label for swagger
	@PostMapping("/hhtuser/findHhtUser")
	public HhtUserOutput[] findHhtUser(@RequestBody FindHhtUser findHhtUser,
									   @RequestParam String authToken)throws Exception{
		return idmasterService.findHhtUser(findHhtUser,authToken);
	}
	
	/*
	 * --------------------------------RoleAccess---------------------------------
	 */
	@ApiOperation(response = RoleAccess.class, value = "Get all RoleAccess details") // label for swagger
	@GetMapping("/roleaccess")
	public ResponseEntity<?> getRoleAccesss(@RequestParam String authToken) {
		RoleAccess[] userRoleIdList = idmasterService.getRoleAccesss(authToken);
		return new ResponseEntity<>(userRoleIdList, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess.class, value = "Get a RoleAccess") // label for swagger
	@GetMapping("/roleaccess/{roleId}")
	public ResponseEntity<?> getRoleAccess(@PathVariable Long roleId, @RequestParam String companyCodeId,
										   @RequestParam String languageId, @RequestParam String plantId,
										   @RequestParam String warehouseId, @RequestParam String authToken) {
		RoleAccess[] dbRoleAccess = idmasterService.getRoleAccess(warehouseId, roleId,companyCodeId,plantId,languageId,authToken);
		log.info("RoleAccess : " + dbRoleAccess);
		return new ResponseEntity<>(dbRoleAccess, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess[].class, value = "Create RoleAccess") // label for swagger
	@PostMapping("/roleaccess")
	public ResponseEntity<?> postRoleAccess(@Valid @RequestBody List<AddRoleAccess> newRoleAccess,
											@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		RoleAccess[] createdRoleAccess = idmasterService.createRoleAccess(newRoleAccess, loginUserID, authToken);
		return new ResponseEntity<>(createdRoleAccess, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess[].class, value = "Update RoleAccess") // label for swagger
	@RequestMapping(value = "/roleaccess/{roleId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchRoleAccess(@PathVariable Long roleId, @RequestParam String companyCodeId,
											 @RequestParam String languageId, @RequestParam String plantId,
											 @RequestParam String warehouseId, @RequestParam String loginUserID,
											 @RequestParam String authToken, @Valid @RequestBody List<AddRoleAccess> updateRoleAccess)
			throws IllegalAccessException, InvocationTargetException {
		RoleAccess[] updatedRoleAccess =
				idmasterService.updateRoleAccess(warehouseId, roleId,companyCodeId,languageId,
						plantId,loginUserID,updateRoleAccess, authToken);
		return new ResponseEntity<>(updatedRoleAccess, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess.class, value = "Delete RoleAccess") // label for swagger
	@DeleteMapping("/roleaccess/{roleId}")
	public ResponseEntity<?> deleteRoleAccess(@PathVariable Long roleId, @RequestParam String warehouseId,
											  @RequestParam String companyCodeId, @RequestParam String languageId,
											  @RequestParam String plantId, @RequestParam String loginUserID,
											  @RequestParam String authToken) {
		idmasterService.deleteRoleAccess(warehouseId, roleId,companyCodeId,languageId,plantId,loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//FIND
	@ApiOperation(response = RoleAccess[].class, value = "Find RoleAccess")//label for swagger
	@PostMapping("/roleaccess/findRoleAccess")
	public RoleAccess[] findRoleAccess(@RequestBody FindRoleId findRoleId,
									   @RequestParam String authToken)throws Exception{
		return idmasterService.findRoleAccess(findRoleId,authToken);
	}
	// --------------------------------DoorId---------------------------------

	@ApiOperation(response = DoorId.class, value = "Get all DoorId details") // label for swagger
	@GetMapping("/doorid")
	public ResponseEntity<?> getDoorIds(@RequestParam String authToken) {
		DoorId[] doorIdList = idmasterService.getDoorIds(authToken);
		return new ResponseEntity<>(doorIdList, HttpStatus.OK);
	}

	@ApiOperation(response = DoorId.class, value = "Get a DoorId") // label for swagger
	@GetMapping("/doorid/{doorId}")
	public ResponseEntity<?> getDoorId(@PathVariable String doorId, @RequestParam String warehouseId,
									   @RequestParam String authToken) {
		DoorId dbDoorId = idmasterService.getDoorId(warehouseId, doorId, authToken);
		log.info("DoorId : " + dbDoorId);
		return new ResponseEntity<>(dbDoorId, HttpStatus.OK);
	}

	@ApiOperation(response = DoorId.class, value = "Create DoorId") // label for swagger
	@PostMapping("/doorid")
	public ResponseEntity<?> postDoorId(@Valid @RequestBody AddDoorId newDoorId, @RequestParam String loginUserID,
										@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		DoorId createdDoorId = idmasterService.createDoorId(newDoorId, loginUserID, authToken);
		return new ResponseEntity<>(createdDoorId, HttpStatus.OK);
	}

	@ApiOperation(response = DoorId.class, value = "Update DoorId") // label for swagger
	@RequestMapping(value = "/doorid/{doorId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchDoorId(@PathVariable String doorId, @RequestParam String warehouseId,
										 @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateDoorId updateDoorId)
			throws IllegalAccessException, InvocationTargetException {
		DoorId updatedDoorId =
				idmasterService.updateDoorId(warehouseId, doorId, loginUserID, updateDoorId, authToken);
		return new ResponseEntity<>(updatedDoorId, HttpStatus.OK);
	}

	@ApiOperation(response = DoorId.class, value = "Delete DoorId") // label for swagger
	@DeleteMapping("/doorid/{doorId}")
	public ResponseEntity<?> deleteDoorId(@PathVariable String doorId, @RequestParam String warehouseId,
										  @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteDoorId(warehouseId, doorId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// --------------------------------ModuleId---------------------------------

	@ApiOperation(response = ModuleId.class, value = "Get all ModuleId details") // label for swagger
	@GetMapping("/moduleid")
	public ResponseEntity<?> getModuleIds(@RequestParam String authToken) {
		ModuleId[] moduleIdList = idmasterService.getModuleIds(authToken);
		return new ResponseEntity<>(moduleIdList, HttpStatus.OK);
	}

	@ApiOperation(response = ModuleId.class, value = "Get a ModuleId") // label for swagger
	@GetMapping("/moduleid/{moduleId}")
	public ResponseEntity<?> getModuleId( @RequestParam String warehouseId, @PathVariable String moduleId,
										  @RequestParam String companyCodeId, @RequestParam String languageId,
										  @RequestParam String plantId, @RequestParam String authToken) {
		ModuleId[] dbModuleId = idmasterService.getModuleId(warehouseId, moduleId,companyCodeId,languageId,plantId, authToken);
		log.info("ModuleId : " + dbModuleId);
		return new ResponseEntity<>(dbModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = ModuleId.class, value = "Create ModuleId") // label for swagger
	@PostMapping("/moduleid")
	public ResponseEntity<?> postModuleId(@Valid @RequestBody List<AddModuleId> newModuleId, @RequestParam String loginUserID,
										  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ModuleId[] createdModuleId = idmasterService.createModuleId(newModuleId, loginUserID, authToken);
		return new ResponseEntity<>(createdModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = ModuleId.class, value = "Update ModuleId") // label for swagger
	@RequestMapping(value = "/moduleid/{moduleId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchModuleId(@RequestParam String warehouseId,@PathVariable String moduleId,
										   @RequestParam String companyCodeId,@RequestParam String languageId,
										   @RequestParam String plantId, @RequestParam String loginUserID,
										   @Valid @RequestBody List<UpdateModuleId> updateModuleId, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ModuleId[] updatedModuleId =
				idmasterService.updateModuleId(warehouseId, moduleId,companyCodeId,languageId,
												plantId,loginUserID,updateModuleId,authToken);
		return new ResponseEntity<>(updatedModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = ModuleId.class, value = "Delete ModuleId") // label for swagger
	@DeleteMapping("/moduleid/{moduleId}")
	public ResponseEntity<?> deleteModuleId( @RequestParam String warehouseId,@PathVariable String moduleId,
											 @RequestParam String companyCodeId,@RequestParam String languageId,
											 @RequestParam String plantId, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteModuleId(warehouseId, moduleId,companyCodeId,languageId,plantId,loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//FIND
	@ApiOperation(response = ModuleId[].class, value = "Find ModuleId")//label for swagger
	@PostMapping("/moduleid/findModuleId")
	public ModuleId[] findModuleId(@RequestBody FindModuleId findModuleId,
								   @RequestParam String authToken)throws Exception{
		return idmasterService.findModuleId(findModuleId,authToken);
	}

	// --------------------------------AdhocModuleId---------------------------------

	@ApiOperation(response = AdhocModuleId.class, value = "Get all AdhocModuleId details") // label for swagger
	@GetMapping("/adhocmoduleid")
	public ResponseEntity<?> getAdhocModuleIds(@RequestParam String authToken) {
		AdhocModuleId[] adhocModuleIdList = idmasterService.getAdhocModuleIds(authToken);
		return new ResponseEntity<>(adhocModuleIdList, HttpStatus.OK);
	}

	@ApiOperation(response = AdhocModuleId.class, value = "Get a AdhocModuleId") // label for swagger
	@GetMapping("/adhocmoduleid/{adhocModuleId}")
	public ResponseEntity<?> getAdhocModuleId(@PathVariable String adhocModuleId, @RequestParam String moduleId,@RequestParam String warehouseId,
											  @RequestParam String authToken) {
		AdhocModuleId dbAdhocModuleId = idmasterService.getAdhocModuleId(warehouseId, adhocModuleId, moduleId, authToken);
		log.info("AdhocModuleId : " + dbAdhocModuleId);
		return new ResponseEntity<>(dbAdhocModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = AdhocModuleId.class, value = "Create AdhocModuleId") // label for swagger
	@PostMapping("/adhocmoduleid")
	public ResponseEntity<?> postAdhocModuleId(@Valid @RequestBody AddAdhocModuleId newAdhocModuleId, @RequestParam String loginUserID,
											   @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId createdAdhocModuleId = idmasterService.createAdhocModuleId(newAdhocModuleId, loginUserID, authToken);
		return new ResponseEntity<>(createdAdhocModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = AdhocModuleId.class, value = "Update AdhocModuleId") // label for swagger
	@RequestMapping(value = "/adhocmoduleid/{adhocModuleId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchAdhocModuleId(@PathVariable String adhocModuleId, @RequestParam String moduleId,@RequestParam String warehouseId,
												@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateAdhocModuleId updateAdhocModuleId)
			throws IllegalAccessException, InvocationTargetException {
		AdhocModuleId updatedAdhocModuleId =
				idmasterService.updateAdhocModuleId(warehouseId, adhocModuleId, moduleId, loginUserID, updateAdhocModuleId, authToken);
		return new ResponseEntity<>(updatedAdhocModuleId, HttpStatus.OK);
	}

	@ApiOperation(response = AdhocModuleId.class, value = "Delete AdhocModuleId") // label for swagger
	@DeleteMapping("/adhocmoduleid/{adhocModuleId}")
	public ResponseEntity<?> deleteAdhocModuleId(@PathVariable String adhocModuleId,@RequestParam String moduleId, @RequestParam String warehouseId,
												 @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteAdhocModuleId(warehouseId, adhocModuleId, moduleId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// --------------------------------PalletizationLevelId---------------------------------

	@ApiOperation(response = PalletizationLevelId.class, value = "Get all PalletizationLevelId details") // label for swagger
	@GetMapping("/palletizationlevelid")
	public ResponseEntity<?> getPalletizationLevelIds(@RequestParam String authToken) {
		PalletizationLevelId[] palletizationLevelIdList = idmasterService.getPalletizationLevelIds(authToken);
		return new ResponseEntity<>(palletizationLevelIdList, HttpStatus.OK);
	}

	@ApiOperation(response = PalletizationLevelId.class, value = "Get a PalletizationLevelId") // label for swagger
	@GetMapping("/palletizationlevelid/{palletizationLevelId}")
	public ResponseEntity<?> getPalletizationLevelId(@PathVariable String palletizationLevelId,@RequestParam String palletizationLevel, @RequestParam String warehouseId,
													 @RequestParam String authToken) {
		PalletizationLevelId dbPalletizationLevelId = idmasterService.getPalletizationLevelId(warehouseId, palletizationLevelId,palletizationLevel, authToken);
		log.info("PalletizationLevelId : " + dbPalletizationLevelId);
		return new ResponseEntity<>(dbPalletizationLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = PalletizationLevelId.class, value = "Create PalletizationLevelId") // label for swagger
	@PostMapping("/palletizationlevelid")
	public ResponseEntity<?> postPalletizationLevelId(@Valid @RequestBody AddPalletizationLevelId newPalletizationLevelId, @RequestParam String loginUserID,
													  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		PalletizationLevelId createdPalletizationLevelId = idmasterService.createPalletizationLevelId(newPalletizationLevelId, loginUserID, authToken);
		return new ResponseEntity<>(createdPalletizationLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = PalletizationLevelId.class, value = "Update PalletizationLevelId") // label for swagger
	@RequestMapping(value = "/palletizationlevelid/{palletizationLevelId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchPalletizationLevelId(@PathVariable String palletizationLevelId, @RequestParam String palletizationLevel,@RequestParam String warehouseId,
													   @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdatePalletizationLevelId updatePalletizationLevelId)
			throws IllegalAccessException, InvocationTargetException {
		PalletizationLevelId updatedPalletizationLevelId =
				idmasterService.updatePalletizationLevelId(warehouseId, palletizationLevelId, palletizationLevel,loginUserID, updatePalletizationLevelId, authToken);
		return new ResponseEntity<>(updatedPalletizationLevelId, HttpStatus.OK);
	}

	@ApiOperation(response = PalletizationLevelId.class, value = "Delete PalletizationLevelId") // label for swagger
	@DeleteMapping("/palletizationlevelid/{palletizationLevelId}")
	public ResponseEntity<?> deletePalletizationLevelId(@PathVariable String palletizationLevelId,@RequestParam String palletizationLevel, @RequestParam String warehouseId,
														@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deletePalletizationLevelId(warehouseId, palletizationLevelId, palletizationLevel, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// --------------------------------EmployeeId---------------------------------

	@ApiOperation(response = EmployeeId.class, value = "Get all EmployeeId details") // label for swagger
	@GetMapping("/employeeid")
	public ResponseEntity<?> getEmployeeIds(@RequestParam String authToken) {
		EmployeeId[] employeeIdList = idmasterService.getEmployeeIds(authToken);
		return new ResponseEntity<>(employeeIdList, HttpStatus.OK);
	}

	@ApiOperation(response = EmployeeId.class, value = "Get a EmployeeId") // label for swagger
	@GetMapping("/employeeid/{employeeId}")
	public ResponseEntity<?> getEmployeeId(@PathVariable String employeeId, @RequestParam String warehouseId,
										   @RequestParam String authToken) {
		EmployeeId dbEmployeeId = idmasterService.getEmployeeId(warehouseId, employeeId, authToken);
		log.info("EmployeeId : " + dbEmployeeId);
		return new ResponseEntity<>(dbEmployeeId, HttpStatus.OK);
	}

	@ApiOperation(response = EmployeeId.class, value = "Create EmployeeId") // label for swagger
	@PostMapping("/employeeid")
	public ResponseEntity<?> postEmployeeId(@Valid @RequestBody AddEmployeeId newEmployeeId, @RequestParam String loginUserID,
											@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		EmployeeId createdEmployeeId = idmasterService.createEmployeeId(newEmployeeId, loginUserID, authToken);
		return new ResponseEntity<>(createdEmployeeId, HttpStatus.OK);
	}

	@ApiOperation(response = EmployeeId.class, value = "Update EmployeeId") // label for swagger
	@RequestMapping(value = "/employeeid/{employeeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchEmployeeId(@PathVariable String employeeId, @RequestParam String warehouseId,
											 @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateEmployeeId updateEmployeeId)
			throws IllegalAccessException, InvocationTargetException {
		EmployeeId updatedEmployeeId =
				idmasterService.updateEmployeeId(warehouseId, employeeId, loginUserID, updateEmployeeId, authToken);
		return new ResponseEntity<>(updatedEmployeeId, HttpStatus.OK);
	}

	@ApiOperation(response = EmployeeId.class, value = "Delete EmployeeId") // label for swagger
	@DeleteMapping("/employeeid/{employeeId}")
	public ResponseEntity<?> deleteEmployeeId(@PathVariable String employeeId, @RequestParam String warehouseId,
											  @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteEmployeeId(warehouseId, employeeId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------BinClassId---------------------------------
	 */
	@ApiOperation(response = BinClassId[].class, value = "Get all BinClassId details") // label for swagger
	@GetMapping("/binclassid")
	public ResponseEntity<?> getBinClassIds(@RequestParam String authToken) {
		BinClassId[] userBinClassId = idmasterService.getBinClassIds(authToken);
		return new ResponseEntity<>(userBinClassId, HttpStatus.OK);
	}
	@ApiOperation(response = BinClassId.class, value = "Get a BinClassId") // label for swagger
	@GetMapping("/binclassid/{binClassId}")
	public ResponseEntity<?>  getBinClassId(@RequestParam String warehouseId,
											@PathVariable Long binClassId,@RequestParam String authToken) {
		BinClassId dbBinClassId = idmasterService.getBinClassId(warehouseId,binClassId,authToken);
		//log.info("BinClassId : " +dbBinClassId);
		return new ResponseEntity<>(dbBinClassId, HttpStatus.OK);
	}
	@ApiOperation(response = BinClassId.class, value = "Create BinClassId") // label for swagger
	@PostMapping("/binclassid")
	public ResponseEntity<?> PostBinClassId(@Valid @RequestBody AddBinClassId newBinClassId,
											@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		BinClassId createdBinClassId = idmasterService.createBinClassId(newBinClassId,loginUserID, authToken);
		return new ResponseEntity<>(createdBinClassId, HttpStatus.OK);
	}
	@ApiOperation(response = BinClassId.class, value = "Update BinClassId") // label for swagger
	@RequestMapping(value = "/binclassid/{binClassId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateBinClassId(@PathVariable Long binClassId,
											 @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
											 @Valid @RequestBody UpdateBinClassId updateBinClassId) throws IllegalAccessException, InvocationTargetException {
		BinClassId updatedBinClassId =
				idmasterService.updateBinclassId(warehouseId ,binClassId,loginUserID,updateBinClassId,authToken);
		return new ResponseEntity<>(updatedBinClassId, HttpStatus.OK);
	}
	@ApiOperation(response = BinClassId.class, value = "Delete BinClassId") // label for swagger
	@DeleteMapping("/binclassid/{binClassId}")
	public ResponseEntity<?> deleteBinClassId(@PathVariable Long binClassId, @RequestParam String warehouseId,
											  @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteBinClassId(warehouseId, binClassId,
				loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------DockId---------------------------------
	 */
	@ApiOperation(response = DockId[].class, value = "Get all DockId details") // label for swagger
	@GetMapping("/dockid")
	public ResponseEntity<?> getDockIds(@RequestParam String authToken) {
		DockId[] userDockId = idmasterService.getDockIds(authToken);
		return new ResponseEntity<>(userDockId, HttpStatus.OK);
	}
	@ApiOperation(response = DockId.class, value = "Get a DockId") // label for swagger
	@GetMapping("/dockid/{dockId}")
	public ResponseEntity<?>  getDockId(@RequestParam String warehouseId,
										@PathVariable String dockId,@RequestParam String authToken) {
		DockId dbDockId = idmasterService.getDockId(warehouseId,dockId,authToken);
		return new ResponseEntity<>(dbDockId, HttpStatus.OK);
	}
	@ApiOperation(response = DockId.class, value = "Create DockId") // label for swagger
	@PostMapping("/dockid")
	public ResponseEntity<?> PostDockId(@Valid @RequestBody AddDockId newDockId,
										@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DockId createDockId = idmasterService.createDockId(newDockId,loginUserID,authToken);
		return new ResponseEntity<>(createDockId, HttpStatus.OK);
	}
	@ApiOperation(response = DockId.class, value = "Update DockId") // label for swagger
	@RequestMapping(value = "/dockid/{dockId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateDockId(@PathVariable String  dockId,
										 @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
										 @Valid @RequestBody UpdateDockId updateDockIdId) throws IllegalAccessException, InvocationTargetException {
		DockId updateDockId =
				idmasterService.updateDockId(warehouseId,dockId,loginUserID,updateDockIdId,authToken);
		return new ResponseEntity<>(updateDockId, HttpStatus.OK);
	}

	@ApiOperation(response = DockId.class, value = "Delete DockId") // label for swagger
	@DeleteMapping("/dockid/{dockId}")
	public ResponseEntity<?> deleteDockId(@PathVariable String  dockId, @RequestParam String warehouseId,
										  @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteDockId(warehouseId,dockId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------WorkCenterId---------------------------------
	 */
	@ApiOperation(response = WorkCenterId[].class, value = "Get all WorkCenterId details") // label for swagger
	@GetMapping("/workcenterid")
	public ResponseEntity<?> getworkCenterIds(@RequestParam String authToken) {
		WorkCenterId[] userWorkCenterId = idmasterService.getworkCenterIds(authToken);
		return new ResponseEntity<>(userWorkCenterId, HttpStatus.OK);
	}
	@ApiOperation(response = WorkCenterId.class, value = "Get a WorkCenterId") // label for swagger
	@GetMapping("/workcenterid/{workCenterId}")
	public ResponseEntity<?>  getWorkCenterId(@RequestParam String warehouseId,
											  @PathVariable String workCenterId,@RequestParam String authToken) {
		WorkCenterId dbworkCenterId = idmasterService.getworkCenterId(warehouseId,workCenterId,authToken);
		return new ResponseEntity<>(dbworkCenterId, HttpStatus.OK);
	}
	@ApiOperation(response = WorkCenterId.class, value = "Create WorkCenterId") // label for swagger
	@PostMapping("/workcenterid")
	public ResponseEntity<?> PostWorkCenter(@Valid @RequestBody AddWorkCenterId newWorkCenterId,
											@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		WorkCenterId createWorkCenterId = idmasterService.createWorkCenterId(newWorkCenterId,loginUserID,authToken);
		return new ResponseEntity<>(createWorkCenterId, HttpStatus.OK);
	}
	@ApiOperation(response = WorkCenterId.class, value = "Update WorkCenterId") // label for swagger
	@RequestMapping(value = "/workcenter/{workCenterId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateWorkCenterId(@RequestParam String warehouseId,
											   @PathVariable String  workCenterId,
											   @RequestParam String loginUserID, @RequestParam String authToken,
											   @Valid @RequestBody UpdateWorkCenterId updateWorkCenterId) throws IllegalAccessException, InvocationTargetException {
		WorkCenterId updateworkCenterId =
				idmasterService.updateWorkCenterId(warehouseId,workCenterId,loginUserID,updateWorkCenterId,authToken);
		return new ResponseEntity<>(updateworkCenterId, HttpStatus.OK);
	}
	@ApiOperation(response = WorkCenterId.class, value = "Delete WorkCenterId") // label for swagger
	@DeleteMapping("/workcenterid/{workCenterId}")
	public ResponseEntity<?> workCenterId( @RequestParam String warehouseId,@PathVariable String workCenterId,
										   @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteWorkCenterId(warehouseId,workCenterId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------OutboundOrderStatusId---------------------------------
	 */

	@ApiOperation(response = OutboundOrderStatusId[].class, value = "Get all OutboundOrderStatusId details") // label for swagger
	@GetMapping("/outboundorderstatusid")
	public ResponseEntity<?> getoutboundOrderStatusIds(@RequestParam String authToken) {
		OutboundOrderStatusId[] userOutboundorderstatusId = idmasterService.getoutBoundOrderStatusIds(authToken);
		return new ResponseEntity<>(userOutboundorderstatusId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderStatusId.class, value = "Get a OutboundOrderStatusId") // label for swagger
	@GetMapping("/outboundorderstatusid/{outboundOrderStatusId}")
	public ResponseEntity<?>  getOutboundOrderStatusId(@RequestParam String warehouseId,
													   @PathVariable String outboundOrderStatusId,@RequestParam String authToken) {
		OutboundOrderStatusId dbOutboundOrderStatusId = idmasterService.getoutBoundOrderStatusId(warehouseId,outboundOrderStatusId,authToken);
		return new ResponseEntity<>(dbOutboundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderStatusId.class, value = "Create OutboundsOrderStatusId") // label for swagger
	@PostMapping("/outboundorderstatusid")
	public ResponseEntity<?> PostoutbountOrderStatusId(@Valid @RequestBody AddOutboundOrderStatusId newoutboundOrderStatusId,
													   @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		OutboundOrderStatusId createOutboundOrderStatusId = idmasterService.createoutBoundOrderStatusId(newoutboundOrderStatusId,loginUserID,authToken);
		return new ResponseEntity<>(createOutboundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderStatusId.class, value = "Update OutboundOrderStatusId") // label for swagger
	@RequestMapping(value = "/outboundorderstatusid/{outboundOrderStatusId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateoutboundOrderStatusId(@PathVariable String  outboundOrderStatusId,
														@RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
														@Valid @RequestBody UpdateOutboundOrderStatusId updateOutboundOrderStatusId) throws IllegalAccessException, InvocationTargetException {
		OutboundOrderStatusId updateoutboundOrderStatusId =
				idmasterService.updateOutboundOrderStatusId(warehouseId,outboundOrderStatusId,loginUserID,updateOutboundOrderStatusId,authToken);
		return new ResponseEntity<>(updateoutboundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderStatusId.class, value = "Delete OutBoundOrderStatusId") // label for swagger
	@DeleteMapping("/outboundorderstatusid/{outBoundOrderStatusId}")
	public ResponseEntity<?> deleteoutBoundOrderStatusId(@PathVariable String  outBoundOrderStatusId, @RequestParam String warehouseId,
														 @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteoutBoundOrderStatusId(warehouseId,outBoundOrderStatusId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------InboundOrderStatusId---------------------------------
	 */

	@ApiOperation(response = InboundOrderStatusId[].class, value = "Get all InboundOrderStatusId details") // label for swagger
	@GetMapping("/inboundorderstatusid")
	public ResponseEntity<?> getinboundOrderStatusIds(@RequestParam String authToken) {
		InboundOrderStatusId[] userinboundorderstatusId = idmasterService.getInboundOrderStatusIds(authToken);
		return new ResponseEntity<>(userinboundorderstatusId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderStatusId.class, value = "Get a inboundOrderStatusId") // label for swagger
	@GetMapping("/inboundorderstatusid/{inboundOrderStatusId}")
	public ResponseEntity<?>  getinboundOrderStatusId(@RequestParam String warehouseId,
													  @PathVariable String inboundOrderStatusId,@RequestParam String authToken) {
		InboundOrderStatusId dbinboundOrderStatusId = idmasterService.getInboundOrderStatusID(warehouseId,inboundOrderStatusId,authToken);
		return new ResponseEntity<>(dbinboundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderStatusId.class, value = "Create InboundsOrderStatusId") // label for swagger
	@PostMapping("/inboundorderstatusid")
	public ResponseEntity<?> PostinbountOrderStatusId(@Valid @RequestBody AddInboundOrderStatusId newinboundOrderStatusId,
													  @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		InboundOrderStatusId createinboundOrderStatusId = idmasterService.createinboundOrderStatusId(newinboundOrderStatusId,loginUserID,authToken);
		return new ResponseEntity<>(createinboundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderStatusId.class, value = "Update InboundOrderStatusId") // label for swagger
	@RequestMapping(value = "/inboundorderstatusid/{inboundOrderStatusId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateinboundOrderStatusId(@PathVariable String  inboundOrderStatusId,
													   @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
													   @Valid @RequestBody UpdateInboundOrderStatusId updateinboundOrderStatusId) throws IllegalAccessException, InvocationTargetException {
		InboundOrderStatusId updateinBoundOrderStatusId =
				idmasterService.updateinboundOrderStatusId(warehouseId,inboundOrderStatusId,loginUserID,updateinboundOrderStatusId,authToken);
		return new ResponseEntity<>(updateinBoundOrderStatusId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderStatusId.class, value = "Delete InboundOrderStatusId") // label for swagger
	@DeleteMapping("/inboundorderstatusid/{inboundOrderStatusId}")
	public ResponseEntity<?> deleteinBoundOrderStatusId(@PathVariable String  inboundOrderStatusId, @RequestParam String warehouseId,
														@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteInboundOrderStatusId(warehouseId,inboundOrderStatusId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------ControlTypeId---------------------------------
	 */

	@ApiOperation(response = ControlTypeId[].class, value = "Get all ControlTypeId details") // label for swagger
	@GetMapping("/controltypeid")
	public ResponseEntity<?> getcontrolTypeIds(@RequestParam String authToken) {
		ControlTypeId[] usercontrolTypeId = idmasterService.getcontrolTypeIds(authToken);
		return new ResponseEntity<>(usercontrolTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlTypeId.class, value = "Get a ControlTypeId") // label for swagger
	@GetMapping("/controltypeid/{controlTypeId}")
	public ResponseEntity<?>  getcontrolTypeId(@RequestParam String warehouseId,
											   @PathVariable String controlTypeId,@RequestParam String authToken) {
		ControlTypeId dbControlTypeId = idmasterService.getcontrolTypeId(warehouseId,controlTypeId,authToken);
		return new ResponseEntity<>(dbControlTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlTypeId.class, value = "Create a ControlTypeId") // label for swagger
	@PostMapping("/controltypeid")
	public ResponseEntity<?> PostcontrolTypeId(@Valid @RequestBody AddControlTypeId newControllTypeID,
											   @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ControlTypeId createcontrolTypeId = idmasterService.createcontrolTypeId(newControllTypeID,loginUserID,authToken);
		return new ResponseEntity<>(createcontrolTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlTypeId.class, value = "Update ControlTypeID") // label for swagger
	@RequestMapping(value = "/controltypeid/{controlTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateControlId(@PathVariable String  controlTypeId,
											@RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
											@Valid @RequestBody UpdateControlTypeId updateControlTypeId) throws IllegalAccessException, InvocationTargetException {
		ControlTypeId updatecontrolTypeId =
				idmasterService.updatecontrolTypeId(warehouseId,controlTypeId,loginUserID,updateControlTypeId,authToken);
		return new ResponseEntity<>(updatecontrolTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlTypeId.class, value = "Delete ControlTypeId") // label for swagger
	@DeleteMapping("/controltypeid/{controlTypeId}")
	public ResponseEntity<?> deleteControlTypeId(@PathVariable String  controlTypeId, @RequestParam String warehouseId,
												 @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deletecontrolTypeId(warehouseId,controlTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------ApprovalId---------------------------------
	 */
	@ApiOperation(response = ApprovalId[].class, value = "Get all ApprovalId details") // label for swagger
	@GetMapping("/approvalid")
	public ResponseEntity<?> getapprovalIds(@RequestParam String authToken) {
		ApprovalId[] userapprovalId = idmasterService.getapprovalIds(authToken);
		return new ResponseEntity<>(userapprovalId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalId.class, value = "Get a ApprovalId") // label for swagger
	@GetMapping("/approvalid/{approvalId}")
	public ResponseEntity<?>  getapprovalId(@PathVariable String approvalId,
											@RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String approverCode,@RequestParam String authToken) {
		ApprovalId dbapprovalId = idmasterService.getapprovalId(warehouseId,approvalId,approvalLevel,approverCode,authToken);
		return new ResponseEntity<>(dbapprovalId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalId.class, value = "Create a ApprovalId") // label for swagger
	@PostMapping("/approvalid")
	public ResponseEntity<?> postapprovalId(@Valid @RequestBody AddApprovalId newApprovalId,
											@RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ApprovalId createApprovalId = idmasterService.createapprovalId(newApprovalId,loginUserID,authToken);
		return new ResponseEntity<>(createApprovalId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalId.class, value = "Update ApprovalID") // label for swagger
	@RequestMapping(value = "/approvalid/{approvalId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateApprovalId(@PathVariable String approvalId,
											 @RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String approverCode,
											 @Valid @RequestBody UpdateApprovalId updateApprovalId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ApprovalId updateapprovalId =
				idmasterService.updateApprovalId(approvalId,warehouseId,approvalLevel,approverCode,updateApprovalId,loginUserID,authToken);
		return new ResponseEntity<>(updateapprovalId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalId.class, value = "Delete ApprovalId") // label for swagger
	@DeleteMapping("/approvalid/{approvalId}")
	public ResponseEntity<?> deleteApprovalId(@PathVariable String approvalId,
											  @RequestParam String warehouseId,@RequestParam String approvalLevel,@RequestParam String approverCode, @RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteapprovalId(warehouseId,approvalId,approvalLevel,approverCode,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------RefDocTypeId---------------------------------
	 */

	@ApiOperation(response = RefDocTypeId[].class, value = "Get all RefDocTypeIdTypeId details") // label for swagger
	@GetMapping("/refdoctypeid")
	public ResponseEntity<?> getRefDocTypeIds(@RequestParam String authToken) {
		RefDocTypeId[] userRefDocTypeId = idmasterService.getrefDocTypeIds(authToken);
		return new ResponseEntity<>(userRefDocTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = RefDocTypeId.class, value = "Get a RefDocTypeId") // label for swagger
	@GetMapping("/refdoctypeid/{referenceDocumentTypeId}")
	public ResponseEntity<?>  getRefDocTypeId(@RequestParam String warehouseId,
											  @PathVariable String referenceDocumentTypeId,@RequestParam String authToken) {
		RefDocTypeId dbRefDocTypeId = idmasterService.getrefDocTypeId(warehouseId,referenceDocumentTypeId,authToken);
		return new ResponseEntity<>(dbRefDocTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = RefDocTypeId.class, value = "Create a RefDocTypeId") // label for swagger
	@PostMapping("/refdoctypeid")
	public ResponseEntity<?> PostRefDocTypeId(@Valid @RequestBody AddRefDocTypeId newRefDocTypeId,
											  @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId createRefDocTypeId = idmasterService.createrefDocTypeId(newRefDocTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createRefDocTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = RefDocTypeId.class, value = "Update RefDocTypeId") // label for swagger
	@RequestMapping(value = "/refdoctypeid/{referenceDocumentTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateRefDocTypeId(@PathVariable String  referenceDocumentTypeId,
											   @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
											   @Valid @RequestBody UpdateRefDocTypeId updateRefDocTypeId) throws IllegalAccessException, InvocationTargetException {
		RefDocTypeId updaterefdoctypeid =
				idmasterService.updateRefDocTypeId(warehouseId,referenceDocumentTypeId,loginUserID,updateRefDocTypeId,authToken);
		return new ResponseEntity<>(updaterefdoctypeid, HttpStatus.OK);
	}
	@ApiOperation(response = RefDocTypeId.class, value = "Delete RefDocTypeId") // label for swagger
	@DeleteMapping("/refdoctypeid/{referenceDocumentTypeId}")
	public ResponseEntity<?> deleteRefDocTypeId(@PathVariable String  referenceDocumentTypeId, @RequestParam String warehouseId,
												@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteRefDocTypeId(warehouseId,referenceDocumentTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------ControlProcessId---------------------------------
	 */

	@ApiOperation(response = ControlProcessId[].class, value = "Get all ControlProcessId details") // label for swagger
	@GetMapping("/controlprocessid")
	public ResponseEntity<?> getControlProcessIds(@RequestParam String authToken) {
		ControlProcessId[] userControlProcessId = idmasterService.getControlProcessIds(authToken);
		return new ResponseEntity<>(userControlProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlProcessId.class, value = "Get a ControlProcessId") // label for swagger
	@GetMapping("/controlprocessid/{controlProcessId}")
	public ResponseEntity<?>  getControlProcessId(@RequestParam String warehouseId,
												  @PathVariable String controlProcessId,@RequestParam String authToken) {
		ControlProcessId dbControlProcessId = idmasterService.getControlProcessId(warehouseId,controlProcessId,authToken);
		return new ResponseEntity<>(dbControlProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlProcessId.class, value = "Create a ControlProcessId") // label for swagger
	@PostMapping("/controlprocessid")
	public ResponseEntity<?> PostControlProcessId(@Valid @RequestBody AddControlProcessId newControlProcessId,
												  @RequestParam String loginUserID, @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ControlProcessId createControlProcessId = idmasterService.createControlProcessId(newControlProcessId,loginUserID,authToken);
		return new ResponseEntity<>(createControlProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlProcessId.class, value = "Update ControlProcessId") // label for swagger
	@RequestMapping(value = "/controlprocessid/{controlProcessId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateControlProcessId(@PathVariable String  controlProcessId,
												   @RequestParam String loginUserID, @RequestParam String warehouseId, @RequestParam String authToken,
												   @Valid @RequestBody UpdateControlProcessId updateControlProcessId) throws IllegalAccessException, InvocationTargetException {
		ControlProcessId updatecontrolProcessId =
				idmasterService.updateControlProcessId(warehouseId,controlProcessId,loginUserID,updateControlProcessId,authToken);
		return new ResponseEntity<>(updatecontrolProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ControlProcessId.class, value = "Delete ControlProcessId") // label for swagger
	@DeleteMapping("/controlprocessid/{controlProcessId}")
	public ResponseEntity<?> deleteControlProcessId(@PathVariable String  controlProcessId, @RequestParam String warehouseId,
													@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deletecontrolProcessId(warehouseId,controlProcessId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------AisleId---------------------------------
	 */

	@ApiOperation(response = AisleId[].class, value = "Get all AisleId details") // label for swagger
	@GetMapping("/aisleid")
	public ResponseEntity<?> getAisleIds(@RequestParam String authToken) {
		AisleId[] userAisleId = idmasterService.getAisleIds(authToken);
		return new ResponseEntity<>(userAisleId, HttpStatus.OK);
	}
	@ApiOperation(response = AisleId.class, value = "Get a AisleId") // label for swagger
	@GetMapping("/aisleid/{aisleId}")
	public ResponseEntity<?>  getAisleId(@PathVariable String aisleId,
										 @RequestParam String warehouseId, @RequestParam String floorId, @RequestParam String storageSectionId,@RequestParam String authToken) {
		AisleId dbAisleId = idmasterService.getAisleId(warehouseId,aisleId, floorId,storageSectionId,authToken);
		return new ResponseEntity<>(dbAisleId, HttpStatus.OK);
	}
	@ApiOperation(response = AisleId.class, value = "Create a AisleId") // label for swagger
	@PostMapping("/aisleid")
	public ResponseEntity<?> PostAisleId(@Valid @RequestBody AddAisleId newAisleId,
										 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		AisleId createAisleId = idmasterService.createAisleId(newAisleId,loginUserID,authToken);
		return new ResponseEntity<>(createAisleId, HttpStatus.OK);
	}
	@ApiOperation(response = AisleId.class, value = "Update AisleId") // label for swagger
	@RequestMapping(value = "/aisleid/{aisleId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateAisleId(@PathVariable String aisleId,
										  @RequestParam String warehouseId, @RequestParam String floorId, @RequestParam String storageSectionId,@RequestParam String authToken,
										  @Valid @RequestBody UpdateAisleId updateaisleId, @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		AisleId updateAisleId =
				idmasterService.updateAisleId(warehouseId,aisleId,floorId,storageSectionId,loginUserID,updateaisleId,authToken);
		return new ResponseEntity<>(updateAisleId, HttpStatus.OK);
	}
	@ApiOperation(response = AisleId.class, value = "Delete AisleId") // label for swagger
	@DeleteMapping("/aisleid/{aisleId}")
	public ResponseEntity<?> deleteAisleId(@PathVariable String aisleId,@RequestParam String floorId, @RequestParam String storageSectionId,
										   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteAisleId(warehouseId,aisleId,floorId,storageSectionId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------SpanId---------------------------------
	 */
	@ApiOperation(response = SpanId[].class, value = "Get all SpanId details") // label for swagger
	@GetMapping("/spanid")
	public ResponseEntity<?> getSpanIds(@RequestParam String authToken) {
		SpanId[] userSpanId = idmasterService.getSpanIds(authToken);
		return new ResponseEntity<>(userSpanId, HttpStatus.OK);
	}
	@ApiOperation(response = SpanId.class, value = "Get a SpanId") // label for swagger
	@GetMapping("/spanid/{spanId}")
	public ResponseEntity<?>  getSpanId(@PathVariable String spanId,
										@RequestParam String warehouseId,@RequestParam String aisleId, @RequestParam String floorId, @RequestParam String storageSectionId,@RequestParam String authToken) {
		SpanId dbSpanId = idmasterService.getSpanId(warehouseId,aisleId,spanId, floorId,storageSectionId,authToken);
		return new ResponseEntity<>(dbSpanId, HttpStatus.OK);
	}
	@ApiOperation(response = SpanId.class, value = "Create a SpanId") // label for swagger
	@PostMapping("/spanid")
	public ResponseEntity<?> PostSpanId(@Valid @RequestBody AddSpanId newSpanId,
										@RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SpanId createSpanId = idmasterService.createSpanId(newSpanId,loginUserID,authToken);
		return new ResponseEntity<>(createSpanId, HttpStatus.OK);
	}
	@ApiOperation(response = SpanId.class, value = "Update SpanId") // label for swagger
	@RequestMapping(value = "/spanid/{spanId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateSpanId(@PathVariable String spanId,
										 @RequestParam String warehouseId, @RequestParam String aisleId, @RequestParam String floorId, @RequestParam String storageSectionId,
										 @Valid @RequestBody UpdateSpanId updateSpanId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		SpanId UpdatespanId =
				idmasterService.updateSpanId(warehouseId,aisleId,spanId,floorId,storageSectionId,loginUserID,updateSpanId,authToken);
		return new ResponseEntity<>(UpdatespanId, HttpStatus.OK);
	}
	@ApiOperation(response = SpanId.class, value = "Delete SpanId") // label for swagger
	@DeleteMapping("/spanid/{spanId}")
	public ResponseEntity<?> deleteSpanId(@PathVariable String spanId,@RequestParam String aisleId,@RequestParam String floorId, @RequestParam String storageSectionId,
										  @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteSpanId(warehouseId,aisleId,spanId,floorId,storageSectionId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------ShelfId---------------------------------
	 */
	@ApiOperation(response = ShelfId[].class, value = "Get all ShelfId details") // label for swagger
	@GetMapping("/shelfid")
	public ResponseEntity<?> getShelfIds(@RequestParam String authToken) {
		ShelfId[] userShelfId = idmasterService.getShelfIds(authToken);
		return new ResponseEntity<>(userShelfId, HttpStatus.OK);
	}
	@ApiOperation(response = ShelfId.class, value = "Get a ShelfId") // label for swagger
	@GetMapping("/shelfid/{shelfId}")
	public ResponseEntity<?>  getShelfId(@PathVariable String shelfId,
										 @RequestParam String warehouseId,@RequestParam String aisleId, @RequestParam String floorId,@RequestParam String spanId, @RequestParam String storageSectionId,@RequestParam String authToken) {
		ShelfId dbShelfId = idmasterService.getShelfId(warehouseId,aisleId,shelfId,spanId, floorId,storageSectionId,authToken);
		return new ResponseEntity<>(dbShelfId, HttpStatus.OK);
	}
	@ApiOperation(response = ShelfId.class, value = "Create a ShelfId") // label for swagger
	@PostMapping("/shelfid")
	public ResponseEntity<?> PostShelfId(@Valid @RequestBody AddShelfId newShelfId,
										 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ShelfId createShelfId = idmasterService.createShelfId(newShelfId,loginUserID,authToken);
		return new ResponseEntity<>(createShelfId, HttpStatus.OK);
	}
	@ApiOperation(response = ShelfId.class, value = "Update ShelfId") // label for swagger
	@RequestMapping(value = "/shelfid/{shelfId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateShelfId(@PathVariable String shelfId,
										  @RequestParam String warehouseId, @RequestParam String aisleId, @RequestParam String floorId, @RequestParam String storageSectionId,
										  @Valid @RequestBody UpdateShelfId updateShelfId,@RequestParam String spanId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ShelfId UpdateShelfId =
				idmasterService.updateShelfId(warehouseId,aisleId,shelfId,spanId,floorId,storageSectionId,loginUserID,updateShelfId,authToken);
		return new ResponseEntity<>(UpdateShelfId, HttpStatus.OK);
	}
	@ApiOperation(response = ShelfId.class, value = "Delete ShelfId") // label for swagger
	@DeleteMapping("/shelfid/{shelfId}")
	public ResponseEntity<?> deleteShelfId(@PathVariable String shelfId,@RequestParam String aisleId,@RequestParam String floorId, @RequestParam String storageSectionId,
										   @RequestParam String warehouseId,@RequestParam String spanId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteShelfId(warehouseId,aisleId,shelfId,spanId,floorId,storageSectionId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------BinSectionId---------------------------------
	 */
	@ApiOperation(response = BinSectionId[].class, value = "Get all BinSectionId details") // label for swagger
	@GetMapping("/binsectionid")
	public ResponseEntity<?> getBinSectionIds(@RequestParam String authToken) {
		BinSectionId[] userbinsectionid = idmasterService.getBinSectionIds(authToken);
		return new ResponseEntity<>(userbinsectionid, HttpStatus.OK);
	}
	@ApiOperation(response = BinSectionId.class, value = "Get a BinSectionId") // label for swagger
	@GetMapping("/binsectionid/{binSectionId}")
	public ResponseEntity<?>  getBinSectionId(@PathVariable String binSectionId,
											  @RequestParam String warehouseId,@RequestParam String authToken) {
		BinSectionId dbbinSectionId = idmasterService.getBinSectionId(warehouseId,binSectionId,authToken);
		return new ResponseEntity<>(dbbinSectionId, HttpStatus.OK);
	}
	@ApiOperation(response = BinSectionId.class, value = "Create a BinSectionId") // label for swagger
	@PostMapping("/binsectionid")
	public ResponseEntity<?> PostBinSectionId(@Valid @RequestBody AddBinSectionId newBinSectionId,
											  @RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		BinSectionId createbinSectionId = idmasterService.createBinSectionId(newBinSectionId,loginUserID,authToken);
		return new ResponseEntity<>(createbinSectionId, HttpStatus.OK);
	}
	@ApiOperation(response = BinSectionId.class, value = "Update BinSectionId") // label for swagger
	@RequestMapping(value = "/binsectionid/{binSectionId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateBinSectionId(@PathVariable String binSectionId,
											   @RequestParam String warehouseId,
											   @Valid @RequestBody UpdateBinSectionId updateBinSectionId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		BinSectionId Updatebinsectionid =
				idmasterService.updateBinSectionId(warehouseId,binSectionId,loginUserID,updateBinSectionId,authToken);
		return new ResponseEntity<>(Updatebinsectionid, HttpStatus.OK);
	}
	@ApiOperation(response = BinSectionId.class, value = "Delete BinSectionId") // label for swagger
	@DeleteMapping("/binsectionid/{binSectionId}")
	public ResponseEntity<?> deleteShelfId(@PathVariable String binSectionId,
										   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteBinSectionId(warehouseId,binSectionId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------DateFormatId---------------------------------
	 */
	@ApiOperation(response = DateFormatId[].class, value = "Get all DateFormatId details") // label for swagger
	@GetMapping("/dateformatid")
	public ResponseEntity<?> getDateFormatIds(@RequestParam String authToken) {
		DateFormatId[] userdateformatid = idmasterService.getDateFormatIds(authToken);
		return new ResponseEntity<>(userdateformatid, HttpStatus.OK);
	}
	@ApiOperation(response = DateFormatId.class, value = "Get a DateFormatId") // label for swagger
	@GetMapping("/dateformatid/{dateFormatId}")
	public ResponseEntity<?>  getDateFormatId(@PathVariable String dateFormatId,
											  @RequestParam String warehouseId,@RequestParam String authToken) {
		DateFormatId dbdateformatid= idmasterService.getDateFormatId(warehouseId,dateFormatId,authToken);
		return new ResponseEntity<>(dbdateformatid, HttpStatus.OK);
	}
	@ApiOperation(response = DateFormatId.class, value = "Create a DateFormatId") // label for swagger
	@PostMapping("/dateformatid")
	public ResponseEntity<?> PostDateFormatId(@Valid @RequestBody AddDateFormatId newDateFormatId,
											  @RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DateFormatId createdateformatid = idmasterService.createDateFormatId(newDateFormatId,loginUserID,authToken);
		return new ResponseEntity<>(createdateformatid, HttpStatus.OK);
	}
	@ApiOperation(response = DateFormatId.class, value = "Update DateFormatId") // label for swagger
	@RequestMapping(value = "/dateformatid/{dateFormatId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateDateFormatId(@PathVariable String dateFormatId,
											   @RequestParam String warehouseId,
											   @Valid @RequestBody UpdateDateFormatId updateDateFormatId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		DateFormatId Updatedateformatid =
				idmasterService.updateDateFormatId(warehouseId,dateFormatId,loginUserID,updateDateFormatId,authToken);
		return new ResponseEntity<>(Updatedateformatid, HttpStatus.OK);
	}
	@ApiOperation(response = DateFormatId.class, value = "Delete DateFormatId") // label for swagger
	@DeleteMapping("/dateformatid/{dateFormatId}")
	public ResponseEntity<?> deletedateformatId(@PathVariable String dateFormatId,
												@RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteDateFormatId(warehouseId,dateFormatId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------DecimalNotationId---------------------------------
	 */
	@ApiOperation(response = DecimalNotationId[].class, value = "Get all DecimalNotationId details") // label for swagger
	@GetMapping("/decimalnotationid")
	public ResponseEntity<?> getDecimalNotationIds(@RequestParam String authToken) {
		DecimalNotationId[] userDecimalNotationId = idmasterService.getDecimalNotationIds(authToken);
		return new ResponseEntity<>(userDecimalNotationId, HttpStatus.OK);
	}
	@ApiOperation(response = DecimalNotationId.class, value = "Get a DecimalNotationId") // label for swagger
	@GetMapping("/decimalnotationid/{decimalNotationId}")
	public ResponseEntity<?>  getDecimalNotationId(@PathVariable String decimalNotationId,
												   @RequestParam String warehouseId,@RequestParam String authToken) {
		DecimalNotationId dbDecimalNotationId= idmasterService.getDecimalNotationId(warehouseId,decimalNotationId,authToken);
		return new ResponseEntity<>(dbDecimalNotationId, HttpStatus.OK);
	}
	@ApiOperation(response = DecimalNotationId.class, value = "Create a DecimalNotationId") // label for swagger
	@PostMapping("/decimalnotationid")
	public ResponseEntity<?> PostDecimalNotationId(@Valid @RequestBody AddDecimalNotationId newDecimalNotationId,
												   @RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId createDecimalNotationId = idmasterService.createDecimalNotationId(newDecimalNotationId,loginUserID,authToken);
		return new ResponseEntity<>(createDecimalNotationId, HttpStatus.OK);
	}
	@ApiOperation(response = DecimalNotationId.class, value = "Update DecimalNotationId") // label for swagger
	@RequestMapping(value = "/decimalnotationid/{decimalNotationId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateDecimalNotationId(@PathVariable String decimalNotationId,
													@RequestParam String warehouseId,
													@Valid @RequestBody UpdateDecimalNotationId updateDecimalNotationId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		DecimalNotationId UpdateDecimalNotationId =
				idmasterService.updateDecimalNotationId(warehouseId,decimalNotationId,loginUserID,updateDecimalNotationId,authToken);
		return new ResponseEntity<>(UpdateDecimalNotationId, HttpStatus.OK);
	}
	@ApiOperation(response = DecimalNotationId.class, value = "Delete DecimalNotationId") // label for swagger
	@DeleteMapping("/decimalnotationid/{decimalNotationId}")
	public ResponseEntity<?> deleteDecimalNotationId(@PathVariable String decimalNotationId,
													 @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteDecimalNotationId(warehouseId,decimalNotationId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------LanguageId---------------------------------
	 */
	@ApiOperation(response = LanguageId[].class, value = "Get all LanguageId details") // label for swagger
	@GetMapping("/languageid")
	public ResponseEntity<?> getLanguageIds(@RequestParam String authToken) {
		LanguageId[] userLanguageId = idmasterService.getLanguageIds(authToken);
		return new ResponseEntity<>(userLanguageId, HttpStatus.OK);
	}
	@ApiOperation(response = LanguageId.class, value = "Get a LanguageId") // label for swagger
	@GetMapping("/languageid/{languageId}")
	public ResponseEntity<?>  getLanguageId(@PathVariable String languageId
			,@RequestParam String authToken) {
		LanguageId dbLanguageId= idmasterService.getLanguageId(languageId,authToken);
		return new ResponseEntity<>(dbLanguageId, HttpStatus.OK);
	}
	@ApiOperation(response = LanguageId.class, value = "Create a LanguageId") // label for swagger
	@PostMapping("/languageid")
	public ResponseEntity<?> PostLanguageId(@Valid @RequestBody AddLanguageId newLanguageId,
											@RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		LanguageId createLanguageId = idmasterService.createLanguageId(newLanguageId,loginUserID,authToken);
		return new ResponseEntity<>(createLanguageId, HttpStatus.OK);
	}
	@ApiOperation(response = LanguageId.class, value = "Update LanguageId") // label for swagger
	@RequestMapping(value = "/languageid/{languageId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateLanguageId(@PathVariable String languageId,
											 @Valid @RequestBody UpdateLanguageId updateLanguageId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		LanguageId UpdateLanguageId =
				idmasterService.updateLanguageId(languageId,loginUserID,updateLanguageId,authToken);
		return new ResponseEntity<>(UpdateLanguageId, HttpStatus.OK);
	}
	@ApiOperation(response = LanguageId.class, value = "Delete LanguageId") // label for swagger
	@DeleteMapping("/languageid/{languageId}")
	public ResponseEntity<?> deleteLanguageId(@PathVariable String languageId,
											  @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteLanguageId(languageId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------StatusMessagesId---------------------------------
	 */
	@ApiOperation(response = StatusMessagesId[].class, value = "Get all StatusMessagesId details") // label for swagger
	@GetMapping("/statusmessagesid")
	public ResponseEntity<?> getStatusMessagesIds(@RequestParam String authToken) {
		StatusMessagesId[] userStatusMessagesId = idmasterService.getStatusMessagesIds(authToken);
		return new ResponseEntity<>(userStatusMessagesId, HttpStatus.OK);
	}
	@ApiOperation(response = StatusMessagesId.class, value = "Get a StatusMessagesId") // label for swagger
	@GetMapping("/statusmessagesid/{messagesId}")
	public ResponseEntity<?>  getStatusMessagesId(@PathVariable String messagesId,
												  @RequestParam String languageId, @RequestParam String messageType,@RequestParam String authToken) {
		StatusMessagesId dbStatusMessagesId= idmasterService.getStatusMessagesId(messagesId,languageId,messageType,authToken);
		return new ResponseEntity<>(dbStatusMessagesId, HttpStatus.OK);
	}
	@ApiOperation(response = StatusMessagesId.class, value = "Create a StatusMessagesId") // label for swagger
	@PostMapping("/statusmessagesid")
	public ResponseEntity<?> PostStatusMessagesId(@Valid @RequestBody AddStatusMessagesId newStatusMessagesId,
												  @RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StatusMessagesId createStatusMessagesId = idmasterService.createStatusMessagesId(newStatusMessagesId,loginUserID,authToken);
		return new ResponseEntity<>(createStatusMessagesId, HttpStatus.OK);
	}
	@ApiOperation(response = StatusMessagesId.class, value = "Update StatusMessagesId") // label for swagger
	@RequestMapping(value = "/statusmessagesid/{messagesId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateStatusMessagesId(@PathVariable String messagesId,
												   @RequestParam String languageId,
												   @RequestParam String messageType,
												   @Valid @RequestBody UpdateStatusMessagesId updateStatusMessagesId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StatusMessagesId UpdateStatusMessagesId =
				idmasterService.updateStatusMessagesId(messagesId,languageId,messageType,loginUserID,updateStatusMessagesId,authToken);
		return new ResponseEntity<>(UpdateStatusMessagesId, HttpStatus.OK);
	}
	@ApiOperation(response = StatusMessagesId.class, value = "Delete StatusMessagesId") // label for swagger
	@DeleteMapping("/statusmessagesid/{messagesId}")
	public ResponseEntity<?> deleteStatusMessagesId(@PathVariable String messagesId,
													@RequestParam String languageId,
													@RequestParam String messageType,
													@RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteStatusMessagesId(messagesId,languageId,messageType,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------MovementTypeId---------------------------------
	 */
	@ApiOperation(response = MovementTypeId[].class, value = "Get all MovementTypeId details") // label for swagger
	@GetMapping("/movementtypeid")
	public ResponseEntity<?> getMovementTypeIds(@RequestParam String authToken) {
		MovementTypeId[] userMovementTypeId = idmasterService.getMovementTypeIds(authToken);
		return new ResponseEntity<>(userMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = MovementTypeId.class, value = "Get a MovementTypeId") // label for swagger
	@GetMapping("/movementtypeid/{movementTypeId}")
	public ResponseEntity<?>  getMovementTypeId(@PathVariable String movementTypeId,
												@RequestParam String warehouseId,@RequestParam String authToken) {
		MovementTypeId dbMovementTypeId= idmasterService.getMovementTypeId(warehouseId,movementTypeId,authToken);
		return new ResponseEntity<>(dbMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = MovementTypeId.class, value = "Create a MovementTypeId") // label for swagger
	@PostMapping("/movementtypeid")
	public ResponseEntity<?> PostMovementTypeId(@Valid @RequestBody AddMovementTypeId newMovementTypeId,
												@RequestParam String loginUserID,String authToken)
			throws IllegalAccessException, InvocationTargetException {
		MovementTypeId createMovementTypeId = idmasterService.createMovementTypeId(newMovementTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = MovementTypeId.class, value = "Update MovementTypeId") // label for swagger
	@RequestMapping(value = "/movementtypeid/{movementTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateMovementTypeId(@PathVariable String movementTypeId,
												 @RequestParam String warehouseId,
												 @Valid @RequestBody UpdateMovementTypeId updateMovementTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		MovementTypeId UpdateMovementTypeId =
				idmasterService.updateMovementTypeId(warehouseId,movementTypeId,loginUserID,updateMovementTypeId,authToken);
		return new ResponseEntity<>(UpdateMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = MovementTypeId.class, value = "Delete MovementTypeId") // label for swagger
	@DeleteMapping("/movementtypeid/{movementTypeId}")
	public ResponseEntity<?> deleteMovementTypeId(@PathVariable String movementTypeId,
												  @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteMovementTypeId(warehouseId,movementTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------SubMovementTypeId---------------------------------
	 */
	@ApiOperation(response = SubMovementTypeId[].class, value = "Get all SubMovementTypeId details") // label for swagger
	@GetMapping("/submovementtypeid")
	public ResponseEntity<?> getSubMovementTypeIds(@RequestParam String authToken) {
		SubMovementTypeId[] userSubMovementTypeId = idmasterService.getSubMovementTypeIds(authToken);
		return new ResponseEntity<>(userSubMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = SubMovementTypeId.class, value = "Get a SubMovementTypeId") // label for swagger
	@GetMapping("/submovementtypeid/{subMovementTypeId}")
	public ResponseEntity<?>  getSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
												   @RequestParam String warehouseId,@RequestParam String authToken) {
		SubMovementTypeId dbSubMovementTypeId= idmasterService.getSubMovementTypeId(warehouseId,movementTypeId,subMovementTypeId,authToken);
		return new ResponseEntity<>(dbSubMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = SubMovementTypeId.class, value = "Create a SubMovementTypeId") // label for swagger
	@PostMapping("/submovementtypeid")
	public ResponseEntity<?> PostSubMovementTypeId(@Valid @RequestBody AddSubMovementTypeId newSubMovementTypeId,
												   @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId createSubMovementTypeId = idmasterService.createSubMovementTypeId(newSubMovementTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createSubMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = SubMovementTypeId.class, value = "Update SubMovementTypeId") // label for swagger
	@RequestMapping(value = "/submovementtypeid/{subMovementTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
													@RequestParam String warehouseId,
													@Valid @RequestBody UpdateSubMovementTypeId updateSubMovementTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		SubMovementTypeId UpdateSubMovementTypeId =
				idmasterService.updateSubMovementTypeId(warehouseId,movementTypeId,subMovementTypeId,loginUserID,updateSubMovementTypeId,authToken);
		return new ResponseEntity<>(UpdateSubMovementTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = SubMovementTypeId.class, value = "Delete SubMovementTypeId") // label for swagger
	@DeleteMapping("/submovementtypeid/{subMovementTypeId}")
	public ResponseEntity<?> deleteSubMovementTypeId(@PathVariable String subMovementTypeId,@RequestParam String movementTypeId,
													 @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteSubMovementTypeId(warehouseId,movementTypeId,subMovementTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	/*
	 * --------------------------------OutboundOrderTypeId---------------------------------
	 */
	@ApiOperation(response = OutboundOrderTypeId[].class, value = "Get all OutboundOrderTypeId details") // label for swagger
	@GetMapping("/outboundordertypeid")
	public ResponseEntity<?> getOutboundOrderTypeIds(@RequestParam String authToken) {
		OutboundOrderTypeId[] userOutboundOrderTypeId = idmasterService.getOutboundOrderTypeIds(authToken);
		return new ResponseEntity<>(userOutboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderTypeId.class, value = "Get a OutboundOrderTypeId") // label for swagger
	@GetMapping("/outboundordertypeid/{outboundOrderTypeId}")
	public ResponseEntity<?>  getOutboundOrderTypeId(@PathVariable String outboundOrderTypeId,
													 @RequestParam String warehouseId,@RequestParam String authToken) {
		OutboundOrderTypeId dbOutboundOrderTypeId= idmasterService.getOutboundOrderTypeId(warehouseId,outboundOrderTypeId,authToken);
		return new ResponseEntity<>(dbOutboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderTypeId.class, value = "Create a OutboundOrderTypeId") // label for swagger
	@PostMapping("/outboundordertypeid")
	public ResponseEntity<?> PostOutboundOrderTypeId(@Valid @RequestBody AddOutboundOrderTypeId newOutboundOrderTypeId,
													 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		OutboundOrderTypeId createOutboundOrderTypeId = idmasterService.createOutboundOrderTypeId(newOutboundOrderTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createOutboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderTypeId.class, value = "Update OutboundOrderTypeId") // label for swagger
	@RequestMapping(value = "/outboundordertypeid/{outboundOrderTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateOutboundOrderTypeId(@PathVariable String outboundOrderTypeId,
													  @RequestParam String warehouseId,
													  @Valid @RequestBody UpdateOutboundOrderTypeId  updateOutboundOrderTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		OutboundOrderTypeId UpdateOutboundOrderTypeId =
				idmasterService.updateOutboundOrderTypeId(warehouseId,outboundOrderTypeId,loginUserID,updateOutboundOrderTypeId,authToken);
		return new ResponseEntity<>(UpdateOutboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = OutboundOrderTypeId.class, value = "Delete OutboundOrderTypeId") // label for swagger
	@DeleteMapping("/outboundordertypeid/{outboundOrderTypeId}")
	public ResponseEntity<?> deleteOutboundOrderTypeId(@PathVariable String outboundOrderTypeId,
													   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteOutboundOrderTypeId(warehouseId,outboundOrderTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------InboundOrderTypeId---------------------------------
	 */
	@ApiOperation(response = InboundOrderTypeId[].class, value = "Get all InboundOrderTypeId details") // label for swagger
	@GetMapping("/inboundordertypeid")
	public ResponseEntity<?> getInboundOrderTypeIds(@RequestParam String authToken) {
		InboundOrderTypeId[] userInboundOrderTypeId = idmasterService.getInboundOrderTypeIds(authToken);
		return new ResponseEntity<>(userInboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderTypeId.class, value = "Get a InboundOrderTypeId") // label for swagger
	@GetMapping("/inboundordertypeid/{inboundOrderTypeId}")
	public ResponseEntity<?>  getInboundOrderTypeId(@PathVariable String inboundOrderTypeId,
													@RequestParam String warehouseId,@RequestParam String authToken) {
		InboundOrderTypeId dbInboundOrderTypeId= idmasterService.getInboundOrderTypeId(warehouseId,inboundOrderTypeId,authToken);
		return new ResponseEntity<>(dbInboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderTypeId.class, value = "Create a InboundOrderTypeId") // label for swagger
	@PostMapping("/inboundordertypeid")
	public ResponseEntity<?> PostInboundOrderTypeId(@Valid @RequestBody AddInboundOrderTypeId newInboundOrderTypeId,
													@RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		InboundOrderTypeId createInboundOrderTypeId = idmasterService.createInboundOrderTypeId(newInboundOrderTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createInboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderTypeId.class, value = "Update InboundOrderTypeId") // label for swagger
	@RequestMapping(value = "/inboundordertypeid/{inboundOrderTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateInboundOrderTypeId(@PathVariable String inboundOrderTypeId,
													 @RequestParam String warehouseId,
													 @Valid @RequestBody UpdateInboundOrderTypeId updateInboundOrderTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		InboundOrderTypeId UpdateInboundOrderTypeId =
				idmasterService.updateInboundOrderTypeId(warehouseId,inboundOrderTypeId,loginUserID,updateInboundOrderTypeId,authToken);
		return new ResponseEntity<>(UpdateInboundOrderTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = InboundOrderTypeId.class, value = "Delete InboundOrderTypeId") // label for swagger
	@DeleteMapping("/inboundordertypeid/{inboundOrderTypeId}")
	public ResponseEntity<?> deleteInboundOrderTypeId(@PathVariable String inboundOrderTypeId,
													  @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteInboundOrderTypeId(warehouseId,inboundOrderTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------TransferTypeId---------------------------------
	 */
	@ApiOperation(response = TransferTypeId[].class, value = "Get all TransferTypeId details") // label for swagger
	@GetMapping("/transfertypeid")
	public ResponseEntity<?> getTransferTypeIds(@RequestParam String authToken) {
		TransferTypeId[] userTransferTypeId = idmasterService.getTransferTypeIds(authToken);
		return new ResponseEntity<>(userTransferTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = TransferTypeId.class, value = "Get a TransferTypeId") // label for swagger
	@GetMapping("/transfertypeid/{transferTypeId}")
	public ResponseEntity<?>  getTransferTypeId(@PathVariable String transferTypeId,
												@RequestParam String warehouseId,@RequestParam String authToken) {
		TransferTypeId dbTransferTypeId= idmasterService.getTransferTypeId(warehouseId,transferTypeId,authToken);
		return new ResponseEntity<>(dbTransferTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = TransferTypeId.class, value = "Create a TransferTypeId") // label for swagger
	@PostMapping("/transfertypeid")
	public ResponseEntity<?> PostTransferTypeId(@Valid @RequestBody AddTransferTypeId newTransferTypeId,
												@RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		TransferTypeId createTransferTypeId = idmasterService.createTransferTypeId(newTransferTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createTransferTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = TransferTypeId.class, value = "Update TransferTypeId") // label for swagger
	@RequestMapping(value = "/transfertypeid/{transferTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateTransferTypeId(@PathVariable String transferTypeId,
												 @RequestParam String warehouseId,
												 @Valid @RequestBody UpdateTransferTypeId updateTransferTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		TransferTypeId UpdateTransferTypeId =
				idmasterService.updateTransferTypeId(warehouseId,transferTypeId,loginUserID,updateTransferTypeId,authToken);
		return new ResponseEntity<>(UpdateTransferTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = TransferTypeId.class, value = "Delete TransferTypeId") // label for swagger
	@DeleteMapping("/transfertypeid/{transferTypeId}")
	public ResponseEntity<?> deleteTransferTypeId(@PathVariable String transferTypeId,
												  @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteTransferTypeId(warehouseId,transferTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------StockTypeId---------------------------------
	 */
	@ApiOperation(response = StockTypeId[].class, value = "Get all StockTypeId details") // label for swagger
	@GetMapping("/stocktypeid")
	public ResponseEntity<?> getStockTypeIds(@RequestParam String authToken) {
		StockTypeId[] userStockTypeId = idmasterService.getStockTypeIds(authToken);
		return new ResponseEntity<>(userStockTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = StockTypeId.class, value = "Get a StockTypeId") // label for swagger
	@GetMapping("/stocktypeid/{stockTypeId}")
	public ResponseEntity<?>  getStockTypeId(@PathVariable String stockTypeId,
											 @RequestParam String warehouseId,@RequestParam String authToken) {
		StockTypeId dbStockTypeId= idmasterService.getStockTypeId(warehouseId,stockTypeId,authToken);
		return new ResponseEntity<>(dbStockTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = StockTypeId.class, value = "Create a StockTypeId") // label for swagger
	@PostMapping("/stocktypeid")
	public ResponseEntity<?> PostStockTypeId(@Valid @RequestBody AddStockTypeId newStockTypeId,
											 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		StockTypeId createStockTypeId = idmasterService.createStockTypeId(newStockTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createStockTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = StockTypeId.class, value = "Update StockTypeId") // label for swagger
	@RequestMapping(value = "/stocktypeid/{stockTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateStockTypeId(@PathVariable String stockTypeId,
											  @RequestParam String warehouseId,
											  @Valid @RequestBody UpdateStockTypeId updateStockTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StockTypeId UpdateStockTypeId =
				idmasterService.updateStockTypeId(warehouseId,stockTypeId,loginUserID,updateStockTypeId,authToken);
		return new ResponseEntity<>(UpdateStockTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = StockTypeId.class, value = "Delete StockTypeId") // label for swagger
	@DeleteMapping("/stocktypeid/{stockTypeId}")
	public ResponseEntity<?> deleteStockTypeId(@PathVariable String stockTypeId,
											   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteStockTypeId(warehouseId,stockTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------SpecialStockIndicatorId---------------------------------
	 */
	@ApiOperation(response = SpecialStockIndicatorId[].class, value = "Get all SpecialStockIndicatorId details") // label for swagger
	@GetMapping("/specialstockindicatorid")
	public ResponseEntity<?> getSpecialStockIndicatorIds(@RequestParam String authToken) {
		SpecialStockIndicatorId[] userSpecialStockIndicatorId= idmasterService.getSpecialStockIndicatorIds(authToken);
		return new ResponseEntity<>(userSpecialStockIndicatorId, HttpStatus.OK);
	}
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Get a SpecialStockIndicatorId") // label for swagger
	@GetMapping("/specialstockindicatorid/{specialStockIndicatorId}")
	public ResponseEntity<?>  getSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId, @RequestParam String stockTypeId,
														 @RequestParam String warehouseId,@RequestParam String authToken) {
		SpecialStockIndicatorId dbSpecialStockIndicatorId= idmasterService.getSpecialStockIndicatorId(warehouseId,stockTypeId,specialStockIndicatorId,authToken);
		return new ResponseEntity<>(dbSpecialStockIndicatorId, HttpStatus.OK);
	}
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Create a SpecialStockIndicatorId") // label for swagger
	@PostMapping("/specialstockindicatorid")
	public ResponseEntity<?> PostSpecialStockIndicatorId(@Valid @RequestBody AddSpecialStockIndicatorId newSpecialStockIndicatorId,
														 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SpecialStockIndicatorId createSpecialStockIndicatorId = idmasterService.createSpecialStockIndicatorId(newSpecialStockIndicatorId,loginUserID,authToken);
		return new ResponseEntity<>(createSpecialStockIndicatorId, HttpStatus.OK);
	}
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Update SpecialStockIndicatorId") // label for swagger
	@RequestMapping(value = "/specialstockindicatorid/{specialStockIndicatorId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId,@RequestParam String stockTypeId,
														  @RequestParam String warehouseId,
														  @Valid @RequestBody UpdateSpecialStockIndicatorId updateSpecialStockIndicatorId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		SpecialStockIndicatorId UpdateSpecialStockIndicatorId =
				idmasterService.updateSpecialStockIndicatorId(warehouseId,stockTypeId,specialStockIndicatorId,loginUserID,updateSpecialStockIndicatorId,authToken);
		return new ResponseEntity<>(UpdateSpecialStockIndicatorId, HttpStatus.OK);
	}
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Delete SpecialStockIndicatorId") // label for swagger
	@DeleteMapping("/specialstockindicatorid/{specialStockIndicatorId}")
	public ResponseEntity<?> deleteSpecialStockIndicatorId(@PathVariable String specialStockIndicatorId,@RequestParam String stockTypeId,
														   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteSpecialStockIndicatorId(warehouseId,stockTypeId,specialStockIndicatorId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------HandlingEquipmentId---------------------------------
	 */
	@ApiOperation(response = HandlingEquipmentId[].class, value = "Get all HandlingEquipmentId details") // label for swagger
	@GetMapping("/handlingequipmentid")
	public ResponseEntity<?> getHandlingEquipmentIds(@RequestParam String authToken) {
		HandlingEquipmentId[] userHandlingEquipmentId= idmasterService.getHandlingEquipmentIds(authToken);
		return new ResponseEntity<>(userHandlingEquipmentId, HttpStatus.OK);
	}
	@ApiOperation(response = SpecialStockIndicatorId.class, value = "Get a HandlingEquipmentId") // label for swagger
	@GetMapping("/handlingequipmentid/{handlingEquipmentId}")
	public ResponseEntity<?>  getHandlingEquipmentId(@PathVariable String handlingEquipmentId, @RequestParam String handlingEquipmentNumber,
													 @RequestParam String warehouseId,@RequestParam String authToken) {
		HandlingEquipmentId dbHandlingEquipmentId= idmasterService.getHandlingEquipmentId(warehouseId,handlingEquipmentId,handlingEquipmentNumber,authToken);
		return new ResponseEntity<>(dbHandlingEquipmentId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingEquipmentId.class, value = "Create a HandlingEquipmentId") // label for swagger
	@PostMapping("/handlingequipmentid")
	public ResponseEntity<?> PostHandlingEquipmentId(@Valid @RequestBody AddHandlingEquipmentId newHandlingEquipmentId,
													 @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId createHandlingEquipmentId = idmasterService.createHandlingEquipmentId(newHandlingEquipmentId,loginUserID,authToken);
		return new ResponseEntity<>(createHandlingEquipmentId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingEquipmentId.class, value = "Update HandlingEquipmentId") // label for swagger
	@RequestMapping(value = "/handlingequipmentid/{handlingEquipmentId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateHandlingEquipmentId(@PathVariable String handlingEquipmentId,@RequestParam String handlingEquipmentNumber,
													  @RequestParam String warehouseId,
													  @Valid @RequestBody UpdateHandlingEquipmentId updateHandlingEquipmentId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		HandlingEquipmentId UpdateHandlingEquipmentId =
				idmasterService.updateHandlingEquipmentId(warehouseId,handlingEquipmentId,handlingEquipmentNumber,loginUserID,updateHandlingEquipmentId,authToken);
		return new ResponseEntity<>(UpdateHandlingEquipmentId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingEquipmentId.class, value = "Delete HandlingEquipmentId") // label for swagger
	@DeleteMapping("/handlingequipmentid/{handlingEquipmentId}")
	public ResponseEntity<?> deleteHandlingEquipmentId(@PathVariable String handlingEquipmentId,@RequestParam String handlingEquipmentNumber,
													   @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteHandlingEquipmentId(warehouseId,handlingEquipmentId,handlingEquipmentNumber,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------HandlingUnitId---------------------------------
	 */
	@ApiOperation(response = HandlingUnitId[].class, value = "Get all HandlingUnitId details") // label for swagger
	@GetMapping("/handlingunitid")
	public ResponseEntity<?> getHandlingUnitIds(@RequestParam String authToken) {
		HandlingUnitId[] userHandlingUnitId= idmasterService.getHandlingUnitIds(authToken);
		return new ResponseEntity<>(userHandlingUnitId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingUnitId.class, value = "Get a HandlingUnitId") // label for swagger
	@GetMapping("/handlingunitid/{handlingUnitId}")
	public ResponseEntity<?>  getHandlingUnitId(@PathVariable String handlingUnitId, @RequestParam String handlingUnitNumber,
												@RequestParam String warehouseId,@RequestParam String authToken) {
		HandlingUnitId dbHandlingUnitId= idmasterService.getHandlingUnitId(warehouseId,handlingUnitId,handlingUnitNumber,authToken);
		return new ResponseEntity<>(dbHandlingUnitId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingUnitId.class, value = "Create a HandlingUnitId") // label for swagger
	@PostMapping("/handlingunitid")
	public ResponseEntity<?> PostHandlingUnitId(@Valid @RequestBody AddHandlingUnitId newHandlingUnitId,
												@RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId createHandlingUnitId = idmasterService.createHandlingUnitId(newHandlingUnitId,loginUserID,authToken);
		return new ResponseEntity<>(createHandlingUnitId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingUnitId.class, value = "Update HandlingUnitId") // label for swagger
	@RequestMapping(value = "/handlingunitid/{handlingUnitId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateHandlingUnitId(@PathVariable String handlingUnitId,@RequestParam String handlingUnitNumber,
												 @RequestParam String warehouseId,
												 @Valid @RequestBody UpdateHandlingUnitId updateHandlingUnitId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		HandlingUnitId UpdateHandlingUnitId =
				idmasterService.updateHandlingUnitId(warehouseId,handlingUnitId,handlingUnitNumber,loginUserID,updateHandlingUnitId,authToken);
		return new ResponseEntity<>(UpdateHandlingUnitId, HttpStatus.OK);
	}
	@ApiOperation(response = HandlingUnitId.class, value = "Delete HandlingUnitId") // label for swagger
	@DeleteMapping("/handlingunitid/{handlingUnitId}")
	public ResponseEntity<?> deleteHandlingUnitId(@PathVariable String handlingUnitId,@RequestParam String handlingUnitNumber,
												  @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteHandlingUnitId(warehouseId,handlingUnitId,handlingUnitNumber,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------CycleCountTypeId---------------------------------
	 */
	@ApiOperation(response = CycleCountTypeId[].class, value = "Get all CycleCountTypeId details") // label for swagger
	@GetMapping("/cyclecounttypeid")
	public ResponseEntity<?> getCycleCountTypeIds(@RequestParam String authToken) {
		CycleCountTypeId[] userCycleCountTypeId= idmasterService.getCycleCountTypeIds(authToken);
		return new ResponseEntity<>(userCycleCountTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = CycleCountTypeId.class, value = "Get a CycleCountTypeId") // label for swagger
	@GetMapping("/cyclecounttypeid/{cycleCountTypeId}")
	public ResponseEntity<?>  getCycleCountTypeId(@PathVariable String cycleCountTypeId,
												  @RequestParam String warehouseId,@RequestParam String authToken) {
		CycleCountTypeId dbCycleCountTypeId= idmasterService.getCycleCountTypeId(warehouseId,cycleCountTypeId,authToken);
		return new ResponseEntity<>(dbCycleCountTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = CycleCountTypeId.class, value = "Create a CycleCountTypeId") // label for swagger
	@PostMapping("/cyclecounttypeid")
	public ResponseEntity<?> PostCycleCountTypeId(@Valid @RequestBody AddCycleCountTypeId newCycleCountTypeId,
												  @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		CycleCountTypeId createCycleCountTypeId = idmasterService.createCycleCountTypeId(newCycleCountTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createCycleCountTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = CycleCountTypeId.class, value = "Update CycleCountTypeId") // label for swagger
	@RequestMapping(value = "/cyclecounttypeid/{cycleCountTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateCycleCountTypeId(@PathVariable String cycleCountTypeId,
												   @RequestParam String warehouseId,
												   @Valid @RequestBody UpdateCycleCountTypeId updateCycleCountTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		CycleCountTypeId UpdateCycleCountTypeId =
				idmasterService.updateCycleCountTypeId(warehouseId,cycleCountTypeId,loginUserID,updateCycleCountTypeId,authToken);
		return new ResponseEntity<>(UpdateCycleCountTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = CycleCountTypeId.class, value = "Delete CycleCountTypeId") // label for swagger
	@DeleteMapping("/cyclecounttypeid/{cycleCountTypeId}")
	public ResponseEntity<?> deleteCycleCountTypeId(@PathVariable String cycleCountTypeId,
													@RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteCycleCountTypeId(warehouseId,cycleCountTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}



	/*
	 * --------------------------------ReturnTypeId---------------------------------
	 */
	@ApiOperation(response = ReturnTypeId[].class, value = "Get all ReturnTypeId details") // label for swagger
	@GetMapping("/returntypeid")
	public ResponseEntity<?> getReturnTypeIds(@RequestParam String authToken) {
		ReturnTypeId[] userReturnTypeId= idmasterService.getReturnTypeIds(authToken);
		return new ResponseEntity<>(userReturnTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ReturnTypeId.class, value = "Get a ReturnTypeId") // label for swagger
	@GetMapping("/returntypeid/{returnTypeId}")
	public ResponseEntity<?>  getReturnTypeId(@PathVariable String returnTypeId,
											  @RequestParam String warehouseId,@RequestParam String authToken) {
		ReturnTypeId dbReturnTypeId= idmasterService.getReturnTypeId(warehouseId,returnTypeId,authToken);
		return new ResponseEntity<>(dbReturnTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ReturnTypeId.class, value = "Create a ReturnTypeId") // label for swagger
	@PostMapping("/returntypeid")
	public ResponseEntity<?> PostReturnTypeId(@Valid @RequestBody AddReturnTypeId newReturnTypeId,
											  @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ReturnTypeId createReturnTypeId = idmasterService.createReturnTypeId(newReturnTypeId,loginUserID,authToken);
		return new ResponseEntity<>(createReturnTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ReturnTypeId.class, value = "Update ReturnTypeId") // label for swagger
	@RequestMapping(value = "/returntypeid/{returnTypeId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateReturnTypeId(@PathVariable String returnTypeId,
											   @RequestParam String warehouseId,
											   @Valid @RequestBody UpdateReturnTypeId updateReturnTypeId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ReturnTypeId UpdateReturnTypeId =
				idmasterService.updateReturnTypeId(warehouseId,returnTypeId,loginUserID,updateReturnTypeId,authToken);
		return new ResponseEntity<>(UpdateReturnTypeId, HttpStatus.OK);
	}
	@ApiOperation(response = ReturnTypeId.class, value = "Delete ReturnTypeId") // label for swagger
	@DeleteMapping("/returntypeid/{returnTypeId}")
	public ResponseEntity<?> deleteReturnTypeId(@PathVariable String returnTypeId,
												@RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteReturnTypeId(warehouseId,returnTypeId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/*
	 * --------------------------------ApprovalProcessId---------------------------------
	 */
	@ApiOperation(response = ApprovalProcessId[].class, value = "Get all ApprovalProcessId details") // label for swagger
	@GetMapping("/approvalprocessid")
	public ResponseEntity<?> getApprovalProcessIds(@RequestParam String authToken) {
		ApprovalProcessId[] userApprovalProcessId= idmasterService.getApprovalProcessIds(authToken);
		return new ResponseEntity<>(userApprovalProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalProcessId.class, value = "Get a ApprovalProcessId") // label for swagger
	@GetMapping("/approvalprocessid/{approvalProcessId}")
	public ResponseEntity<?>  getApprovalProcessId(@PathVariable String approvalProcessId,
												   @RequestParam String warehouseId,@RequestParam String authToken) {
		ApprovalProcessId dbApprovalProcessId= idmasterService.getApprovalProcessId(warehouseId,approvalProcessId,authToken);
		return new ResponseEntity<>(dbApprovalProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalProcessId.class, value = "Create a ApprovalProcessId") // label for swagger
	@PostMapping("/approvalprocessid")
	public ResponseEntity<?> PostApprovalProcessId(@Valid @RequestBody AddApprovalProcessId newApprovalProcessId,
												   @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ApprovalProcessId createApprovalProcessId = idmasterService.createApprovalProcessId(newApprovalProcessId,loginUserID,authToken);
		return new ResponseEntity<>(createApprovalProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalProcessId.class, value = "Update ApprovalProcessId") // label for swagger
	@RequestMapping(value = "/approvalprocessid/{approvalProcessId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateApprovalProcessId(@PathVariable String approvalProcessId,
													@RequestParam String warehouseId,
													@Valid @RequestBody UpdateApprovalProcessId updateApprovalProcessId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ApprovalProcessId UpdateApprovalProcessId =
				idmasterService.updateApprovalProcessId(warehouseId,approvalProcessId,loginUserID,updateApprovalProcessId,authToken);
		return new ResponseEntity<>(UpdateApprovalProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ApprovalProcessId.class, value = "Delete ApprovalProcessId") // label for swagger
	@DeleteMapping("/approvalprocessid/{approvalProcessId}")
	public ResponseEntity<?> deleteApprovalProcessId(@PathVariable String approvalProcessId,
													 @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteApprovalProcessId(warehouseId,approvalProcessId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------ProcessId---------------------------------
	 */
	@ApiOperation(response = ProcessId[].class, value = "Get all ProcessId details") // label for swagger
	@GetMapping("/processid")
	public ResponseEntity<?> getProcessIds(@RequestParam String authToken) {
		ProcessId[] userProcessId= idmasterService.getProcessIds(authToken);
		return new ResponseEntity<>(userProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ProcessId.class, value = "Get a ProcessId") // label for swagger
	@GetMapping("/processid/{processId}")
	public ResponseEntity<?>  getProcessId(@PathVariable String processId,
										   @RequestParam String warehouseId,@RequestParam String authToken) {
		ProcessId dbProcessId= idmasterService.getProcessId(warehouseId,processId,authToken);
		return new ResponseEntity<>(dbProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ProcessId.class, value = "Create a ProcessId") // label for swagger
	@PostMapping("/processid")
	public ResponseEntity<?> PostprocessId(@Valid @RequestBody AddProcessId newProcessId,
										   @RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		ProcessId createProcessId = idmasterService.createProcessId(newProcessId,loginUserID,authToken);
		return new ResponseEntity<>(createProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ProcessId.class, value = "Update ProcessId") // label for swagger
	@RequestMapping(value = "/processid/{processId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateProcessId(@PathVariable String processId,
											@RequestParam String warehouseId,
											@Valid @RequestBody UpdateProcessId updateProcessId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		ProcessId UpdateProcessId =
				idmasterService.updateProcessId(warehouseId,processId,loginUserID,updateProcessId,authToken);
		return new ResponseEntity<>(UpdateProcessId, HttpStatus.OK);
	}
	@ApiOperation(response = ProcessId.class, value = "Delete ProcessId") // label for swagger
	@DeleteMapping("/processid/{processId}")
	public ResponseEntity<?> deleteProcessId(@PathVariable String processId,
											 @RequestParam String warehouseId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteProcessId(warehouseId,processId,loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}


	/*
	 * --------------------------------SubLevelId---------------------------------
	 */
	@ApiOperation(response = SubLevelId[].class, value = "Get all SubLevelId details") // label for swagger
	@GetMapping("/sublevelid")
	public ResponseEntity<?> getSubLevelIds(@RequestParam String authToken) {
		SubLevelId[] userSubLevelId= idmasterService.getSubLevelIds(authToken);
		return new ResponseEntity<>(userSubLevelId, HttpStatus.OK);
	}
	@ApiOperation(response = SubLevelId.class, value = "Get a SubLevelId") // label for swagger
	@GetMapping("/sublevelid/{subLevelId}")
	public ResponseEntity<?>  getSubLevelId(@PathVariable String subLevelId,
											@RequestParam String warehouseId,@RequestParam String levelId,@RequestParam String authToken) {
		SubLevelId dbSubLevelId= idmasterService.getSubLevelId(warehouseId, subLevelId, levelId,authToken);
		return new ResponseEntity<>(dbSubLevelId, HttpStatus.OK);
	}
	@ApiOperation(response = SubLevelId.class, value = "Create a SubLevelId") // label for swagger
	@PostMapping("/sublevelid")
	public ResponseEntity<?> PostSubLevelId(@Valid @RequestBody AddSubLevelId newSubLevelId,
											@RequestParam String loginUserID,@RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		SubLevelId CreateSubLevelId = idmasterService.createSubLevelId(newSubLevelId, loginUserID,authToken);
		return new ResponseEntity<>(CreateSubLevelId, HttpStatus.OK);
	}
	@ApiOperation(response = SubLevelId.class, value = "Update SubLevelId") // label for swagger
	@RequestMapping(value = "/sublevelid/{subLevelId}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateSubLevelId(@PathVariable String subLevelId,
											 @RequestParam String warehouseId,@RequestParam String levelId,
											 @Valid @RequestBody UpdateSubLevelId updateSubLevelId, @RequestParam String loginUserID,@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		SubLevelId UpdateSubLevelId =
				idmasterService.updateSubLevelId(warehouseId, subLevelId,levelId, loginUserID, updateSubLevelId,authToken);
		return new ResponseEntity<>(UpdateSubLevelId, HttpStatus.OK);
	}
	@ApiOperation(response = SubLevelId.class, value = "Delete SubLevelId") // label for swagger
	@DeleteMapping("/sublevelid/{subLevelId}")
	public ResponseEntity<?> deleteSubLevelId(@PathVariable String subLevelId,
											  @RequestParam String warehouseId,@RequestParam String levelId, @RequestParam String loginUserID,@RequestParam String authToken) {
		idmasterService.deleteSubLevelId(warehouseId, subLevelId, levelId, loginUserID,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//-----------------------------------Document Upload------------------------------------------------------------
	@ApiOperation(response = UploadFileResponse.class, value = "Document Storage Upload") // label for swagger
	@PostMapping("/doc-storage/upload")
	public ResponseEntity<?> docStorageUpload(@RequestParam String location, @RequestParam("file") MultipartFile file)
			throws Exception {
		if (location == null) {
			throw new BadRequestException("Location can't be blank. Please provide 'document' as Location");
		}
		Map<String, String> response = fileStorageService.storingFile(location, file);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	//Upload - Report Single Upload per Day per Warehouse
//	@ApiOperation(response = UploadFileResponse.class, value = "Document Storage Upload") // label for swagger
//	@PostMapping("/doc-storage/upload")
//	public ResponseEntity<?> docStorageUpload(@RequestParam String location, @RequestParam("file") MultipartFile file)
//			throws Exception {
//		if (location == null) {
//			throw new BadRequestException("Location can't be blank. Please provide 'document' as Location");
//		}
//		Map<String, String> response = fileStorageService.storingFileMailingReport(location, file);
//		return new ResponseEntity<>(response,HttpStatus.OK);
//	}

	//-----------------------------------Document Download------------------------------------------------------------
	@ApiOperation(response = Optional.class, value = "Document Storage Download") // label for swagger
	@GetMapping("/doc-storage/download")
	public ResponseEntity<?> docStorageDownload(@RequestParam String location, @RequestParam String fileName)
			throws Exception {
		String filePath = docStorageService.getQualifiedFilePath (location, fileName);
		File file = new File (filePath);
		Path path = Paths.get(file.getAbsolutePath());
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");
		return ResponseEntity.ok()
				.headers(header)
				.contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}
	/*
	 * --------------------------------EMail---------------------------------
	 */
	@ApiOperation(response = EMailDetails[].class, value = "Get all EMail details") // label for swagger
	@GetMapping("/email")
	public ResponseEntity<?> getEMails(@RequestParam String authToken) {
		EMailDetails[] userEMail= idmasterService.getEMailDetailsList(authToken);
		return new ResponseEntity<>(userEMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Get a EMail") // label for swagger
	@GetMapping("/email/{id}")
	public ResponseEntity<?>  getEMail(@PathVariable Long id,
									   @RequestParam String authToken) {
		EMailDetails dbEMail= idmasterService.getEMail(id,authToken);
		return new ResponseEntity<>(dbEMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Create a EMail") // label for swagger
	@PostMapping("/email")
	public ResponseEntity<?> PostEMail(@Valid @RequestBody AddEMailDetails newEMail,
									   @RequestParam String authToken)
			throws IllegalAccessException, InvocationTargetException {
		EMailDetails CreateEMail = idmasterService.createEMail(newEMail, authToken);
		return new ResponseEntity<>(CreateEMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Update EMail") // label for swagger
	@RequestMapping(value = "/email/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<?>updateEMail(@PathVariable Long id,
										@Valid @RequestBody AddEMailDetails updateEMail, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		EMailDetails UpdateEMail =
				idmasterService.updateEMail(id, updateEMail,authToken);
		return new ResponseEntity<>(UpdateEMail, HttpStatus.OK);
	}
	@ApiOperation(response = EMailDetails.class, value = "Delete EMail") // label for swagger
	@DeleteMapping("/email/{id}")
	public ResponseEntity<?> deleteEMail(@PathVariable Long id,
										 @RequestParam String authToken) {
		idmasterService.deleteEMail(id,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	@ApiOperation(response = EMailDetails.class, value = "Un_Delete_EMail") // label for swagger
	@DeleteMapping("/email/undelete/{id}")
	public ResponseEntity<?> undeleteEMail(@PathVariable Long id,
										 @RequestParam String authToken) {
		EMailDetails dbEMail = idmasterService.undeleteEMail(id,authToken);
		return new ResponseEntity<>(dbEMail, HttpStatus.OK);
	}
	/*
	 * --------------------------------FileNameForEmail---------------------------------
	 */
	@ApiOperation(response = FileNameForEmail[].class, value = "Get all FileNameForEmail details") // label for swagger
	@GetMapping("/filenameforemail")
	public ResponseEntity<?> getFileNameForEmailList(@RequestParam String authToken) {
		FileNameForEmail[] userFileNameForEmail= idmasterService.getFileNameForEmailList(authToken);
		return new ResponseEntity<>(userFileNameForEmail, HttpStatus.OK);
	}
	@ApiOperation(response = FileNameForEmail.class, value = "Get a FileNameForEmail") // label for swagger
	@GetMapping("/filenameforemail/{fileNameId}")
	public ResponseEntity<?>  getFileNameForEmail(@PathVariable Long fileNameId,
									   @RequestParam String authToken) {
		FileNameForEmail dbFileNameForEmail = idmasterService.getFileNameForEmail(fileNameId,authToken);
		return new ResponseEntity<>(dbFileNameForEmail, HttpStatus.OK);
	}

	@ApiOperation(response = Optional.class, value = "Get DateTime in desired Format") // label for swagger
	@GetMapping("/filenameforemail/dateTimeNew")
	public ResponseEntity<?>  getDateTimeNew() throws ParseException {
		String dateTime = idmasterService.getDateTime();
		return new ResponseEntity<>(dateTime, HttpStatus.OK);
	}
//	@ApiOperation(response = FileNameForEmail.class, value = "Create a FileNameForEmail") // label for swagger
//	@PostMapping("/filenameforemail")
//	public ResponseEntity<?> PostFileNameForEmail(@Valid @RequestBody FileNameForEmail newFileNameForEmail,
//									   @RequestParam String authToken)
//			throws IllegalAccessException, InvocationTargetException {
//		FileNameForEmail CreateFileNameForEmail = idmasterService.createFileNameForEmail(newFileNameForEmail, authToken);
//		return new ResponseEntity<>(CreateFileNameForEmail, HttpStatus.OK);
//	}
	@ApiOperation(response = FileNameForEmail.class, value = "Delete FileNameForEmail") // label for swagger
	@DeleteMapping("/filenameforemail/{fileNameId}")
	public ResponseEntity<?> deleteFileNameForEmail(@PathVariable Long fileNameId,
										 @RequestParam String authToken) {
		idmasterService.deleteFileNameForEmail(fileNameId,authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	@ApiOperation(response = Optional.class, value = "Send Email") // label for swagger
	@GetMapping("/email/sendmail")
	public ResponseEntity<?> sendEmail(@RequestParam String authToken)
						throws MessagingException, IOException {
		idmasterService.sendMail(authToken);
		return new ResponseEntity<>("Mail Sent Successfully",HttpStatus.OK);
	}
}