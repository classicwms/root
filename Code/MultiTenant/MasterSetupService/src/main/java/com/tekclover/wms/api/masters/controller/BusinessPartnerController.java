package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.businesspartner.v2.BusinessPartnerV2;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.masters.model.businesspartner.AddBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.BusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.SearchBusinessPartner;
import com.tekclover.wms.api.masters.model.businesspartner.UpdateBusinessPartner;
import com.tekclover.wms.api.masters.service.BusinessPartnerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BusinessPartner"}, value = "BusinessPartner  Operations related to BusinessPartnerController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BusinessPartner ",description = "Operations related to BusinessPartner ")})
@RequestMapping("/businesspartner")
@RestController
public class BusinessPartnerController {
	
	@Autowired
	BusinessPartnerService businesspartnerService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = BusinessPartner.class, value = "Get all BusinessPartner details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BusinessPartner> businesspartnerList = businesspartnerService.getBusinessPartners();
		return new ResponseEntity<>(businesspartnerList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BusinessPartner.class, value = "Get a BusinessPartner") // label for swagger 
	@GetMapping("/{partnerCode}")
	public ResponseEntity<?> getBusinessPartner(@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam Long businessPartnerType) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartner businesspartner = businesspartnerService.getBusinessPartner(partnerCode,companyCodeId,plantId,warehouseId,languageId,businessPartnerType);
    	log.info("BusinessPartner : " + businesspartner);
		return new ResponseEntity<>(businesspartner, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
	@ApiOperation(response = BusinessPartner.class, value = "Search BusinessPartner") // label for swagger
	@PostMapping("/findBusinessPartner")
	public List<BusinessPartner> findBusinessPartner(@RequestBody SearchBusinessPartner searchBusinessPartner)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbNameList(searchBusinessPartner.getCompanyCodeId(), searchBusinessPartner.getPlantId(), searchBusinessPartner.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return businesspartnerService.findBusinessPartner(searchBusinessPartner);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	
    @ApiOperation(response = BusinessPartner.class, value = "Create BusinessPartner") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBusinessPartner(@Valid @RequestBody AddBusinessPartner newBusinessPartner, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newBusinessPartner.getCompanyCodeId(), newBusinessPartner.getPlantId(), newBusinessPartner.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartner createdBusinessPartner = businesspartnerService.createBusinessPartner(newBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = BusinessPartner.class, value = "Update BusinessPartner") // label for swagger
    @PatchMapping("/{partnerCode}")
	public ResponseEntity<?> patchBusinessPartner(@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam Long businessPartnerType,
			@Valid @RequestBody UpdateBusinessPartner updateBusinessPartner, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartner createdBusinessPartner = businesspartnerService.updateBusinessPartner(partnerCode,companyCodeId,plantId,warehouseId,languageId,businessPartnerType,updateBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = BusinessPartnerV2.class, value = "Get a BusinessPartner") // label for swagger
	@GetMapping("/v2/{partnerCode}")
	public ResponseEntity<?> getBusinessPartnerV2(@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam Long businessPartnerType) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartnerV2 businesspartner = businesspartnerService.getBusinessPartnerV2(partnerCode,companyCodeId,plantId,warehouseId,languageId,businessPartnerType);
		log.info("BusinessPartner : " + businesspartner);
		return new ResponseEntity<>(businesspartner, HttpStatus.OK);
	}

		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = BusinessPartner.class, value = "Create BusinessPartner") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postBusinessPartnerV2(@Valid @RequestBody BusinessPartnerV2 newBusinessPartner, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newBusinessPartner.getCompanyCodeId(), newBusinessPartner.getPlantId(), newBusinessPartner.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartnerV2 createdBusinessPartner = businesspartnerService.createBusinessPartnerV2(newBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = BusinessPartnerV2.class, value = "Update BusinessPartner V2") // label for swagger
    @PatchMapping("/v2/{partnerCode}")
	public ResponseEntity<?> patchBusinessPartnerV2(@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam Long businessPartnerType,
			@Valid @RequestBody BusinessPartnerV2 updateBusinessPartner, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		BusinessPartnerV2 createdBusinessPartner = businesspartnerService.updateBusinessPartnerV2(partnerCode,companyCodeId,plantId,warehouseId,languageId,businessPartnerType,updateBusinessPartner, loginUserID);
		return new ResponseEntity<>(createdBusinessPartner , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = BusinessPartner.class, value = "Delete BusinessPartner") // label for swagger
	@DeleteMapping("/{partnerCode}")
	public ResponseEntity<?> deleteBusinessPartner(@PathVariable String partnerCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam Long businessPartnerType, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		businesspartnerService.deleteBusinessPartner(partnerCode,companyCodeId,plantId,warehouseId,languageId,businessPartnerType,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}