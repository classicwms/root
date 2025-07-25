package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
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

import com.tekclover.wms.api.masters.model.packingmaterial.AddPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.PackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.SearchPackingMaterial;
import com.tekclover.wms.api.masters.model.packingmaterial.UpdatePackingMaterial;
import com.tekclover.wms.api.masters.service.PackingMaterialService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PackingMaterial"}, value = "PackingMaterial  Operations related to PackingMaterialController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PackingMaterial ",description = "Operations related to PackingMaterial ")})
@RequestMapping("/packingmaterial")
@RestController
public class PackingMaterialController {
	
	@Autowired
	PackingMaterialService packingmaterialService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = PackingMaterial.class, value = "Get all PackingMaterial details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PackingMaterial> packingmaterialList = packingmaterialService.getPackingMaterials();
		return new ResponseEntity<>(packingmaterialList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = PackingMaterial.class, value = "Get a PackingMaterial") // label for swagger 
	@GetMapping("/{packingMaterialNo}")
	public ResponseEntity<?> getPackingMaterial(@PathVariable String packingMaterialNo,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PackingMaterial packingmaterial = packingmaterialService.getPackingMaterial(packingMaterialNo,companyCodeId,plantId,languageId,warehouseId);
    	log.info("PackingMaterial : " + packingmaterial);
		return new ResponseEntity<>(packingmaterial, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
	@ApiOperation(response = PackingMaterial.class, value = "Search PackingMaterial") // label for swagger
	@PostMapping("/findPackingMaterial")
	public List<PackingMaterial> findPackingMaterial(@RequestBody SearchPackingMaterial searchPackingMaterial)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbNameList(searchPackingMaterial.getCompanyCodeId(), searchPackingMaterial.getPlantId(), searchPackingMaterial.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return packingmaterialService.findPackingMaterial(searchPackingMaterial);
	}
	finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = PackingMaterial.class, value = "Create PackingMaterial") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPackingMaterial(@Valid @RequestBody AddPackingMaterial newPackingMaterial, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newPackingMaterial.getCompanyCodeId(), newPackingMaterial.getPlantId(), newPackingMaterial.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PackingMaterial createdPackingMaterial = packingmaterialService.createPackingMaterial(newPackingMaterial, loginUserID);
		return new ResponseEntity<>(createdPackingMaterial , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = PackingMaterial.class, value = "Update PackingMaterial") // label for swagger
    @PatchMapping("/{packingMaterialNo}")
	public ResponseEntity<?> patchPackingMaterial(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,
			@Valid @RequestBody UpdatePackingMaterial updatePackingMaterial, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		PackingMaterial createdPackingMaterial = packingmaterialService.updatePackingMaterial(packingMaterialNo,companyCodeId,plantId,warehouseId,languageId,updatePackingMaterial, loginUserID);
		return new ResponseEntity<>(createdPackingMaterial , HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = PackingMaterial.class, value = "Delete PackingMaterial") // label for swagger
	@DeleteMapping("/{packingMaterialNo}")
	public ResponseEntity<?> deletePackingMaterial(@PathVariable String packingMaterialNo,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		packingmaterialService.deletePackingMaterial(packingMaterialNo,companyCodeId,plantId,warehouseId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}

