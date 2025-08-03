package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.uomid.FindUomId;
import com.tekclover.wms.api.idmaster.repository.CityRepository;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
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

import com.tekclover.wms.api.idmaster.model.uomid.AddUomId;
import com.tekclover.wms.api.idmaster.model.uomid.UomId;
import com.tekclover.wms.api.idmaster.model.uomid.UpdateUomId;
import com.tekclover.wms.api.idmaster.service.UomIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"UomId"}, value = "UomId  Operations related to UomIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "UomId ",description = "Operations related to UomId ")})
@RequestMapping("/uomid")
@RestController
public class UomIdController {
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private CompanyIdRepository companyIdRepository;
	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	UomIdService uomidService;
	
    @ApiOperation(response = UomId.class, value = "Get all UomId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<UomId> uomidList = uomidService.getUomIds();
		return new ResponseEntity<>(uomidList, HttpStatus.OK);
	}
    
    @ApiOperation(response = UomId.class, value = "Get a UomId") // label for swagger 
	@GetMapping("/{uomId}")
	public ResponseEntity<?> getUomId(@PathVariable String uomId,@RequestParam String companyCodeId,@RequestParam String warehouseId,@RequestParam String plantId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			UomId uomid = uomidService.getUomId(uomId, companyCodeId, warehouseId, plantId, languageId);
			log.info("UomId : " + uomid);
			return new ResponseEntity<>(uomid, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
//	@ApiOperation(response = UomId.class, value = "Search UomId") // label for swagger
//	@PostMapping("/findUomId")
//	public List<UomId> findUomId(@RequestBody SearchUomId searchUomId)
//			throws Exception {
//		return uomidService.findUomId(searchUomId);
//	}
    
    @ApiOperation(response = UomId.class, value = "Create UomId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postUomId(@Valid @RequestBody AddUomId newUomId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newUomId.getCompanyCodeId(), newUomId.getPlantId(), newUomId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			UomId createdUomId = uomidService.createUomId(newUomId, loginUserID);
			return new ResponseEntity<>(createdUomId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = UomId.class, value = "Update UomId") // label for swagger
    @PatchMapping("/{uomId}")
	public ResponseEntity<?> patchUomId(@PathVariable String uomId,@RequestParam String companyCodeId,@RequestParam String warehouseId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String loginUserID,@Valid @RequestBody UpdateUomId updateUomId
			) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			UomId createdUomId = uomidService.updateUomId(uomId, companyCodeId, warehouseId, plantId, languageId, loginUserID, updateUomId);
			return new ResponseEntity<>(createdUomId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = UomId.class, value = "Delete UomId") // label for swagger
	@DeleteMapping("/{uomId}")
	public ResponseEntity<?> deleteUomId(@PathVariable String uomId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String warehouseId,@RequestParam String plantId,
			@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			uomidService.deleteUomId(uomId, companyCodeId, warehouseId, plantId, languageId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
	//Search
	@ApiOperation(response = UomId.class, value = "Find UomId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findUomId(@Valid @RequestBody FindUomId findUomId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findUomId.getCompanyCodeId(), findUomId.getPlantId(), findUomId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<UomId> createdUomId = uomidService.findUomId(findUomId);
			return new ResponseEntity<>(createdUomId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
}