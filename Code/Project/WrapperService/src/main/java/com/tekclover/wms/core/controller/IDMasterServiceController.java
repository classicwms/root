package com.tekclover.wms.core.controller;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.core.exception.BadRequestException;
import com.tekclover.wms.core.exception.CustomErrorResponse;
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
import com.tekclover.wms.core.model.idmaster.User;
import com.tekclover.wms.core.model.idmaster.UserTypeId;
import com.tekclover.wms.core.model.idmaster.VariantId;
import com.tekclover.wms.core.model.idmaster.Vertical;
import com.tekclover.wms.core.model.idmaster.WarehouseId;
import com.tekclover.wms.core.model.idmaster.WarehouseTypeId;
import com.tekclover.wms.core.model.user.AddUserManagement;
import com.tekclover.wms.core.model.user.UpdateUserManagement;
import com.tekclover.wms.core.model.user.UserManagement;
import com.tekclover.wms.core.service.IDMasterService;
import com.tekclover.wms.core.service.RegisterService;
import com.tekclover.wms.core.util.CommonUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

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
	public ResponseEntity<?> getUserManagement(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
		UserManagement dbUserManagement = idmasterService.getUserManagement(userId, warehouseId, authToken);
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
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody UpdateUserManagement updateUserManagement)
			throws IllegalAccessException, InvocationTargetException {
		UserManagement updatedUserManagement = 
				idmasterService.updateUserManagement(userId, warehouseId, loginUserID, updateUserManagement, authToken);
		return new ResponseEntity<>(updatedUserManagement, HttpStatus.OK);
	}

	@ApiOperation(response = UserManagement.class, value = "Delete UserManagement") // label for swagger
	@DeleteMapping("/usermanagement/{userId}")
	public ResponseEntity<?> deleteUserManagement(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteUserManagement(userId, warehouseId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
	@RequestMapping(value = "/companyid", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchCompanyId(@PathVariable String companyCodeId,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody CompanyId updateCompanyId)
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
	public ResponseEntity<?> getItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String itemGroup, @RequestParam String authToken) {
		ItemGroupId dbItemGroupId = idmasterService.getItemGroupId(warehouseId, itemTypeId, itemGroupId, itemGroup, authToken);
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
	public ResponseEntity<?> patchItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String itemGroup, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody ItemGroupId updateItemGroupId)
			throws IllegalAccessException, InvocationTargetException {
		ItemGroupId updatedItemGroupId = 
				idmasterService.updateItemGroupId(warehouseId, itemTypeId, itemGroupId, itemGroup, loginUserID, updateItemGroupId, authToken);
		return new ResponseEntity<>(updatedItemGroupId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroupId.class, value = "Delete ItemGroupId") // label for swagger
	@DeleteMapping("/itemgroupid/{itemGroupId}")
	public ResponseEntity<?> deleteItemGroupId(@PathVariable Long itemGroupId, @RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam String itemGroup, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteItemGroupId(warehouseId, itemTypeId, itemGroupId, itemGroup, loginUserID, authToken);
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
			@RequestParam String itemType, @RequestParam String authToken) {
		ItemTypeId dbItemTypeId = idmasterService.getItemTypeId(warehouseId, itemTypeId, itemType, authToken);
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
	public ResponseEntity<?> patchItemTypeId(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String itemType, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody ItemTypeId updateItemTypeId)
			throws IllegalAccessException, InvocationTargetException {
		ItemTypeId updatedItemTypeId = 
				idmasterService.updateItemTypeId(warehouseId, itemTypeId, itemType, loginUserID, updateItemTypeId, authToken);
		return new ResponseEntity<>(updatedItemTypeId, HttpStatus.OK);
	}

	@ApiOperation(response = ItemTypeId.class, value = "Delete ItemTypeId") // label for swagger
	@DeleteMapping("/itemtypeid/{itemTypeId}")
	public ResponseEntity<?> deleteItemTypeId(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String itemType, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteItemTypeId(warehouseId, itemTypeId, itemType, loginUserID, authToken);
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
	public ResponseEntity<?> getMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, @RequestParam Long subMenuId, 
			@RequestParam Long authorizationObjectId, @RequestParam String authorizationObjectValue,  @RequestParam String authToken) {
		MenuId dbMenuId = idmasterService.getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue, authToken);
		log.info("MenuId : " + dbMenuId);
		return new ResponseEntity<>(dbMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Create MenuId") // label for swagger
	@PostMapping("/menuid")
	public ResponseEntity<?> postMenuId(@Valid @RequestBody MenuId newMenuId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		MenuId createdMenuId = idmasterService.createMenuId(newMenuId, loginUserID, authToken);
		return new ResponseEntity<>(createdMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Update MenuId") // label for swagger
	@RequestMapping(value = "/menuid/{menuId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String menuName,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody MenuId updateMenuId)
			throws IllegalAccessException, InvocationTargetException {
		MenuId updatedMenuId = 
				idmasterService.updateMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, menuName, loginUserID, updateMenuId, authToken);
		return new ResponseEntity<>(updatedMenuId, HttpStatus.OK);
	}

	@ApiOperation(response = MenuId.class, value = "Delete MenuId") // label for swagger
	@DeleteMapping("/menuid/{menuId}")
	public ResponseEntity<?> deleteMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, @RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String menuName,  
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, menuName, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
			@RequestParam Long subLevelId, @RequestParam String processDescription, @RequestParam String subProcessDescription, 
			@RequestParam String authToken) {
		ProcessSequenceId dbProcessSequenceId = 
				idmasterService.getProcessSequenceId(warehouseId, processId, subLevelId, processDescription, 
						subProcessDescription, authToken);
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
			@RequestParam String processDescription, @RequestParam String subProcessDescription, @RequestParam String loginUserID, 
			@RequestParam String authToken, @Valid @RequestBody ProcessSequenceId updateProcessSequenceId)
			throws IllegalAccessException, InvocationTargetException {
		ProcessSequenceId updatedProcessSequenceId = 
				idmasterService.updateProcessSequenceId(warehouseId, processId, subLevelId, processDescription, 
						subProcessDescription, loginUserID, updateProcessSequenceId, authToken);
		return new ResponseEntity<>(updatedProcessSequenceId, HttpStatus.OK);
	}

	@ApiOperation(response = ProcessSequenceId.class, value = "Delete ProcessSequenceId") // label for swagger
	@DeleteMapping("/processsequenceid/{processId}")
	public ResponseEntity<?> deleteProcessSequenceId(@PathVariable Long processId, @RequestParam String warehouseId, @RequestParam Long subLevelId,  @RequestParam String processDescription, @RequestParam String subProcessDescription, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteProcessSequenceId(warehouseId, processId, subLevelId, processDescription, subProcessDescription, loginUserID, authToken);
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
	public ResponseEntity<?> postStatusId(@Valid @RequestBody StatusId newStatusId, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		StatusId createdStatusId = idmasterService.createStatusId(newStatusId, loginUserID, authToken);
		return new ResponseEntity<>(createdStatusId, HttpStatus.OK);
	}

	@ApiOperation(response = StatusId.class, value = "Update StatusId") // label for swagger
	@RequestMapping(value = "/statusid/{statusId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchStatusId(@PathVariable Long statusId,  @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StatusId updateStatusId)
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
	public ResponseEntity<?> getStorageSectionId(@PathVariable Long storageSectionId, @RequestParam String warehouseId, 
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
	public ResponseEntity<?> patchStorageSectionId(@PathVariable Long storageSectionId, @RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSection,  
			@RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StorageSectionId updateStorageSectionId)
			throws IllegalAccessException, InvocationTargetException {
		StorageSectionId updatedStorageSectionId = 
				idmasterService.updateStorageSectionId(warehouseId, floorId, storageSectionId, storageSection, loginUserID, 
						updateStorageSectionId, authToken);
		return new ResponseEntity<>(updatedStorageSectionId, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Delete StorageSectionId") // label for swagger
	@DeleteMapping("/storagesectionid/{storageSectionId}")
	public ResponseEntity<?> deleteStorageSectionId(@PathVariable Long storageSectionId, @RequestParam String warehouseId, 
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
	@GetMapping("/subitemgroupid/{subitemgroupid}")
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
	@RequestMapping(value = "/subitemgroupid/{subitemgroupid}", method = RequestMethod.PATCH)
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
	@DeleteMapping("/subitemgroupid/{subitemgroupid}")
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
	@ApiOperation(response = HhtUser.class, value = "Get all HhtUser details") // label for swagger
	@GetMapping("/hhtuser")
	public ResponseEntity<?> getHhtUsers(@RequestParam String authToken) {
		HhtUser[] userIdList = idmasterService.getHhtUsers(authToken);
		return new ResponseEntity<>(userIdList, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Get a HhtUser") // label for swagger
	@GetMapping("/hhtuser/{userId}")
	public ResponseEntity<?> getHhtUser(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String authToken) {
		HhtUser dbHhtUser = idmasterService.getHhtUser(userId, warehouseId, authToken);
		log.info("HhtUser : " + dbHhtUser);
		return new ResponseEntity<>(dbHhtUser, HttpStatus.OK);
	}
	
	@ApiOperation(response = HhtUser.class, value = "Get HhtUsers") // label for swagger 
   	@GetMapping("/hhtuser/{warehouseId}/hhtUser")
   	public ResponseEntity<?> getHhtUser(@PathVariable String warehouseId, @RequestParam String authToken) {
       	HhtUser[] hhtuser = idmasterService.getHhtUserByWarehouseId(warehouseId, authToken);
       	log.info("HhtUser : " + hhtuser);
   		return new ResponseEntity<>(hhtuser, HttpStatus.OK);
   	}

	@ApiOperation(response = HhtUser.class, value = "Create HhtUser") // label for swagger
	@PostMapping("/hhtuser")
	public ResponseEntity<?> postHhtUser(@Valid @RequestBody HhtUser newHhtUser, @RequestParam String loginUserID,
			@RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
		HhtUser createdHhtUser = idmasterService.createHhtUser(newHhtUser, loginUserID, authToken);
		return new ResponseEntity<>(createdHhtUser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Update HhtUser") // label for swagger
	@RequestMapping(value = "/hhtuser/{userId}", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchHhtUser(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody HhtUser updateHhtUser) throws IllegalAccessException, InvocationTargetException {
		HhtUser updatedHhtUser = 
				idmasterService.updateHhtUser(userId, warehouseId, updateHhtUser, loginUserID, authToken);
		return new ResponseEntity<>(updatedHhtUser, HttpStatus.OK);
	}

	@ApiOperation(response = HhtUser.class, value = "Delete HhtUser") // label for swagger
	@DeleteMapping("/hhtuser/{userId}")
	public ResponseEntity<?> deleteHhtUser(@PathVariable String userId, @RequestParam String warehouseId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteHhtUser(warehouseId, userId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	/*
	 * --------------------------------RoleAccess---------------------------------
	 */
	@ApiOperation(response = RoleAccess.class, value = "Get all RoleAccess details") // label for swagger
	@GetMapping("/roleaccess/userRoleId")
	public ResponseEntity<?> getRoleAccesss(@RequestParam String authToken) {
		RoleAccess[] userRoleIdList = idmasterService.getRoleAccesss(authToken);
		return new ResponseEntity<>(userRoleIdList, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess[].class, value = "Get a RoleAccess") // label for swagger
	@GetMapping("/roleaccess/{userRoleId}")
	public ResponseEntity<?> getRoleAccess(@PathVariable Long userRoleId, 
			@RequestParam String warehouseId, @RequestParam String authToken) {
		RoleAccess[] dbRoleAccess = idmasterService.getRoleAccess(warehouseId, userRoleId, authToken);
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
	@RequestMapping(value = "/roleaccess", method = RequestMethod.PATCH)
	public ResponseEntity<?> patchRoleAccess(@PathVariable Long userRoleId, 
			@RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken, 
			@Valid @RequestBody List<RoleAccess> updateRoleAccess) throws IllegalAccessException, InvocationTargetException {
		RoleAccess[] updatedRoleAccess = 
				idmasterService.updateRoleAccess(warehouseId, userRoleId, loginUserID, updateRoleAccess, authToken);
		return new ResponseEntity<>(updatedRoleAccess, HttpStatus.OK);
	}

	@ApiOperation(response = RoleAccess.class, value = "Delete RoleAccess") // label for swagger
	@DeleteMapping("/roleaccess/{userRoleId}")
	public ResponseEntity<?> deleteRoleAccess(@PathVariable Long userRoleId, 
			@RequestParam String warehouseId, @RequestParam Long menuId, @RequestParam Long subMenuId, 
			@RequestParam String loginUserID, @RequestParam String authToken) {
		idmasterService.deleteRoleAccess(warehouseId, userRoleId, menuId, subMenuId, loginUserID, authToken);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}