package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.imalternateparts.AddImAlternatePart;
import com.tekclover.wms.api.masters.model.imalternateparts.ImAlternatePart;
import com.tekclover.wms.api.masters.model.imalternateparts.SearchImAlternateParts;
import com.tekclover.wms.api.masters.model.imalternateuom.SearchImAlternateUom;
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

import com.tekclover.wms.api.masters.model.imalternateuom.AddImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.ImAlternateUom;
import com.tekclover.wms.api.masters.model.imalternateuom.UpdateImAlternateUom;
import com.tekclover.wms.api.masters.service.ImAlternateUomService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImAlternateUom"}, value = "ImAlternateUom  Operations related to ImAlternateUomController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImAlternateUom ",description = "Operations related to ImAlternateUom ")})
@RequestMapping("/imalternateuom")
@RestController
public class ImAlternateUomController {
	
	@Autowired
	ImAlternateUomService imalternateuomService;
	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ImAlternateUom.class, value = "Get all ImAlternateUom details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImAlternateUom> imalternateuomList = imalternateuomService.getImAlternateUoms();
		return new ResponseEntity<>(imalternateuomList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Get a ImAlternateUom") // label for swagger 
	@GetMapping("/{uomId}")
	public ResponseEntity<?> getImAlternateUom(@PathVariable String uomId,@RequestParam String companyCodeId,
											   @RequestParam String plantId,@RequestParam String warehouseId,
											   @RequestParam String itemCode,@RequestParam String alternateUom,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImAlternateUom> imalternateuom = imalternateuomService.getImAlternateUom(alternateUom,companyCodeId,plantId,warehouseId,itemCode,uomId,languageId);
    	log.info("ImAlternateUom : " + imalternateuom);
		return new ResponseEntity<>(imalternateuom, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ImAlternateUom.class, value = "Create ImAlternateUom") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImAlternateUom(@Valid @RequestBody List<AddImAlternateUom> newImAlternateUom, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			for (AddImAlternateUom newImAlternateuom : newImAlternateUom) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(newImAlternateuom.getCompanyCodeId(), newImAlternateuom.getPlantId(), newImAlternateuom.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		List<ImAlternateUom> createdImAlternateUom = imalternateuomService.createImAlternateUom(newImAlternateUom, loginUserID);
		return new ResponseEntity<>(createdImAlternateUom , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ImAlternateUom.class, value = "Update ImAlternateUom") // label for swagger
    @PatchMapping("/{uomId}")
	public ResponseEntity<?> patchImAlternateUom(@PathVariable String uomId,@RequestParam String companyCodeId,
												 @RequestParam String plantId, @RequestParam String warehouseId,
												 @RequestParam String itemCode,@RequestParam String alternateUom,
												 @RequestParam String languageId, @RequestParam String loginUserID,
												 @Valid @RequestBody List<UpdateImAlternateUom> updateImAlternateUom )
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImAlternateUom> createdImAlternateUom = imalternateuomService.updateImAlternateUom(alternateUom,companyCodeId,plantId,
				warehouseId,itemCode,uomId,languageId,updateImAlternateUom, loginUserID);
		return new ResponseEntity<>(createdImAlternateUom , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ImAlternateUom.class, value = "Delete ImAlternateUom") // label for swagger
	@DeleteMapping("/{uomId}")
	public ResponseEntity<?> deleteImAlternateUom(@PathVariable String uomId,@RequestParam String companyCodeId,
												  @RequestParam String plantId,@RequestParam String warehouseId,
												  @RequestParam String itemCode,@RequestParam String alternateUom,
												  @RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		imalternateuomService.deleteImAlternateUom(alternateUom,companyCodeId,plantId,warehouseId,itemCode,uomId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	@ApiOperation(response = ImAlternateUom.class, value = "Find ImAlternateUom") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findImAlternateUom(@Valid @RequestBody SearchImAlternateUom findImAlternateUom) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbNameList(findImAlternateUom.getCompanyCodeId(), findImAlternateUom.getPlantId(), findImAlternateUom.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImAlternateUom> createdImAlternateUom = imalternateuomService.findImAlternateUom(findImAlternateUom);
		return new ResponseEntity<>(createdImAlternateUom, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}