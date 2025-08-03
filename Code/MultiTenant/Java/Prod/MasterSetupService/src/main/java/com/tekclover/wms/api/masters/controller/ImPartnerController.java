package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.impartner.*;
import com.tekclover.wms.api.masters.model.masters.Warehouse;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.repository.WarehouseRepository;
import lombok.Data;
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

import com.tekclover.wms.api.masters.service.ImPartnerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImPartner"}, value = "ImPartner  Operations related to ImPartnerController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImPartner ",description = "Operations related to ImPartner ")})
@RequestMapping("/impartner")
@RestController
public class ImPartnerController {
	
	@Autowired
	ImPartnerService impartnerService;
	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	WarehouseRepository warehouseRepository;
	
    @ApiOperation(response = ImPartner.class, value = "Get all ImPartner details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImPartner> impartnerList = impartnerService.getImPartners();
		return new ResponseEntity<>(impartnerList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImPartner.class, value = "Get a ImPartner") // label for swagger 
	@GetMapping("/{itemCode}")
	public ResponseEntity<?> getImPartner(@PathVariable String itemCode, @RequestParam String companyCodeId, @RequestParam String manufacturerName,
										  @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String partnerItemBarcode) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImPartner> impartner =
				impartnerService.getImPartner(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, partnerItemBarcode);
		return new ResponseEntity<>(impartner, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
	@ApiOperation(response = ImPartner.class, value = "Search ImPartner") // label for swagger
	@PostMapping("/findImPartner")
	public List<ImPartner> findImPartner(@RequestBody SearchImPartner searchImPartner)
			throws ParseException {
		try {
			log.info("SearchImPartner ------> {}", searchImPartner);
			String routingDb = null;
			DataBaseContextHolder.setCurrentDb("MT");
			Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchImPartner.getWarehouseId().get(0), 0L);
			routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			return impartnerService.findImPartnerV8(searchImPartner);    // For Fahaheel !!!!
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
	
    @ApiOperation(response = ImPartner.class, value = "Create ImPartner") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImPartner(@Valid @RequestBody List<AddImPartner> newImPartner,
										   @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			for (AddImPartner newImpartner : newImPartner) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(newImpartner.getCompanyCodeId(), newImpartner.getPlantId(), newImpartner.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		List<ImPartner> createdImPartner = impartnerService.createImPartner(newImPartner, loginUserID);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ImPartner.class, value = "Update ImPartner") // label for swagger
    @PatchMapping("/{itemCode}")
	public ResponseEntity<?> patchImPartner(@PathVariable String itemCode, @RequestParam String companyCodeId,
											@RequestParam String plantId, @RequestParam String languageId, @RequestParam String manufacturerName,
											@RequestParam String warehouseId, @Valid @RequestBody List<AddImPartner> updateImPartner,
											@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImPartner> createdImPartner =
				impartnerService.updateImPartner(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, updateImPartner, loginUserID);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ImPartner.class, value = "Delete ImPartner") // label for swagger
	@DeleteMapping("/{itemCode}")
	public ResponseEntity<?> deleteImPartner(@PathVariable String itemCode, @RequestParam String manufacturerName,
											 @RequestParam String companyCodeId, @RequestParam String plantId,
											 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String partnerItemBarcode,
											 @RequestParam String loginUserID) throws ParseException, InvocationTargetException, IllegalAccessException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		impartnerService.deleteImPartner(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, partnerItemBarcode, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	@ApiOperation(response = ImPartner.class, value = "Get a ImPartner") // label for swagger
	@PostMapping("/v2/get")
	public ResponseEntity<?> getImPartnerV2(@RequestBody ImPartnerInput imPartnerInput) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(imPartnerInput.getCompanyCodeId(), imPartnerInput.getPlantId(), imPartnerInput.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ImPartner> impartner =
				impartnerService.getImPartnerV2(imPartnerInput);
		return new ResponseEntity<>(impartner, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = ImPartner.class, value = "Update ImPartner V2") // label for swagger
    @PatchMapping("/v2/update")
	public ResponseEntity<?> patchImPartnerV2(@Valid @RequestBody List<AddImPartner> updateImPartner, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			for (AddImPartner updateImpartner : updateImPartner) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(updateImpartner.getCompanyCodeId(), updateImpartner.getPlantId(), updateImpartner.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		List<ImPartner> createdImPartner =
				impartnerService.updateImPartnerV2(updateImPartner, loginUserID);
		return new ResponseEntity<>(createdImPartner , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

    @ApiOperation(response = ImPartner.class, value = "Delete ImPartner") // label for swagger
	@PostMapping("/v2/delete")
	public ResponseEntity<?> deleteImPartnerV2(@RequestBody List<ImPartnerInput> imPartnerInput, @RequestParam String loginUserID)
			throws ParseException, InvocationTargetException, IllegalAccessException {
		try {
			for (ImPartnerInput impartnerInput : imPartnerInput) {
				DataBaseContextHolder.setCurrentDb("MT");
				String routingDb = dbConfigRepository.getDbName(impartnerInput.getCompanyCodeId(), impartnerInput.getPlantId(), impartnerInput.getWarehouseId());
				log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
				DataBaseContextHolder.clear();
				DataBaseContextHolder.setCurrentDb(routingDb);
			}
		impartnerService.deleteImPartnerV2(imPartnerInput, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}