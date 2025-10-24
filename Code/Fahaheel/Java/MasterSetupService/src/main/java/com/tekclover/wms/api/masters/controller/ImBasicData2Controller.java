package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.imbasicdata2.SearchImBasicData2;
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

import com.tekclover.wms.api.masters.model.imbasicdata2.AddImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.ImBasicData2;
import com.tekclover.wms.api.masters.model.imbasicdata2.UpdateImBasicData2;
import com.tekclover.wms.api.masters.service.ImBasicData2Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImBasicData2"}, value = "ImBasicData2  Operations related to ImBasicData2Controller") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImBasicData2 ",description = "Operations related to ImBasicData2")})
@RequestMapping("/imbasicdata2")
@RestController
public class ImBasicData2Controller {
	
	@Autowired
	ImBasicData2Service imbasicdata2Service;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = ImBasicData2.class, value = "Get all ImBasicData2 details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImBasicData2> imbasicdata2List = imbasicdata2Service.getImBasicData2s();
		return new ResponseEntity<>(imbasicdata2List, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImBasicData2.class, value = "Get a ImBasicData2") // label for swagger 
	@GetMapping("/{itemCode}")
	public ResponseEntity<?> getImBasicData2(@PathVariable String itemCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ImBasicData2 imbasicdata2 = imbasicdata2Service.getImBasicData2(itemCode,companyCodeId,plantId,warehouseId,languageId);
//    	log.info("ImBasicData2 : " + imbasicdata2);
		return new ResponseEntity<>(imbasicdata2, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
	@ApiOperation(response = ImBasicData2.class, value = "Search ImBasicData2") // label for swagger
	@PostMapping("/findImBasicData2")
	public List<ImBasicData2> findImBasicData2(@RequestBody SearchImBasicData2 searchImBasicData2)
			throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbNameList(searchImBasicData2.getCompanyCodeId(), searchImBasicData2.getPlantId(), searchImBasicData2.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return imbasicdata2Service.findImBasicData2(searchImBasicData2);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	
    @ApiOperation(response = ImBasicData2.class, value = "Create ImBasicData2") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImBasicData2(@Valid @RequestBody AddImBasicData2 newImBasicData2, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newImBasicData2.getCompanyCodeId(), newImBasicData2.getPlantId(), newImBasicData2.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ImBasicData2 createdImBasicData2 = imbasicdata2Service.createImBasicData2(newImBasicData2, loginUserID);
		return new ResponseEntity<>(createdImBasicData2 , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ImBasicData2.class, value = "Update ImBasicData2") // label for swagger
    @PatchMapping("/{itemCode}")
	public ResponseEntity<?> patchImBasicData2(@PathVariable String itemCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,
			@Valid @RequestBody UpdateImBasicData2 updateImBasicData2, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ImBasicData2 createdImBasicData2 = imbasicdata2Service.updateImBasicData2(itemCode,companyCodeId,plantId,warehouseId,languageId,updateImBasicData2, loginUserID);
		return new ResponseEntity<>(createdImBasicData2 , HttpStatus.OK);
	}

		finally {
			DataBaseContextHolder.clear();
		}
		}
    @ApiOperation(response = ImBasicData2.class, value = "Delete ImBasicData2") // label for swagger
	@DeleteMapping("/{itemCode}")
	public ResponseEntity<?> deleteImBasicData2(@PathVariable String itemCode,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String warehouseId,@RequestParam String languageId,@RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		imbasicdata2Service.deleteImBasicData2(itemCode,companyCodeId,plantId,warehouseId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}