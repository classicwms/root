package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;


import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.FindBarcodeTypeId;
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

import com.tekclover.wms.api.idmaster.model.barcodetypeid.AddBarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.BarcodeTypeId;
import com.tekclover.wms.api.idmaster.model.barcodetypeid.UpdateBarcodeTypeId;
import com.tekclover.wms.api.idmaster.service.BarcodeTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"BarcodeTypeId"}, value = "BarcodeTypeId  Operations related to BarcodeTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BarcodeTypeId ",description = "Operations related to BarcodeTypeId ")})
@RequestMapping("/barcodetypeid")
@RestController
public class BarcodeTypeIdController {
	
	@Autowired
	BarcodeTypeIdService barcodetypeidService;

	@Autowired
	DbConfigRepository dbConfigRepository;

    @ApiOperation(response = BarcodeTypeId.class, value = "Get all BarcodeTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<BarcodeTypeId> barcodetypeidList = barcodetypeidService.getBarcodeTypeIds();
		return new ResponseEntity<>(barcodetypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Get a BarcodeTypeId") // label for swagger 
	@GetMapping("/{barcodeTypeId}")
	public ResponseEntity<?> getBarcodeTypeId(@RequestParam String warehouseId,@PathVariable Long barcodeTypeId,
											  @RequestParam String companyCodeId,@RequestParam String languageId, @RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			BarcodeTypeId barcodetypeid =
					barcodetypeidService.getBarcodeTypeId(warehouseId, barcodeTypeId, companyCodeId, languageId, plantId);
			log.info("BarcodeTypeId : " + barcodetypeid);
			return new ResponseEntity<>(barcodetypeid, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
//	@ApiOperation(response = BarcodeTypeId.class, value = "Search BarcodeTypeId") // label for swagger
//	@PostMapping("/findBarcodeTypeId")
//	public List<BarcodeTypeId> findBarcodeTypeId(@RequestBody SearchBarcodeTypeId searchBarcodeTypeId)
//			throws Exception {
//		return barcodetypeidService.findBarcodeTypeId(searchBarcodeTypeId);
//	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Create BarcodeTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBarcodeTypeId(@Valid @RequestBody AddBarcodeTypeId newBarcodeTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newBarcodeTypeId.getCompanyCodeId(), newBarcodeTypeId.getPlantId(), newBarcodeTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			BarcodeTypeId createdBarcodeTypeId = barcodetypeidService.createBarcodeTypeId(newBarcodeTypeId, loginUserID);
			return new ResponseEntity<>(createdBarcodeTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Update BarcodeTypeId") // label for swagger
    @PatchMapping("/{barcodeTypeId}")
	public ResponseEntity<?> patchBarcodeTypeId(@RequestParam String warehouseId,@PathVariable Long barcodeTypeId,
												@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateBarcodeTypeId updateBarcodeTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);

			BarcodeTypeId createdBarcodeTypeId =
					barcodetypeidService.updateBarcodeTypeId(warehouseId, barcodeTypeId, companyCodeId, languageId, plantId, loginUserID, updateBarcodeTypeId);
			return new ResponseEntity<>(createdBarcodeTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = BarcodeTypeId.class, value = "Delete BarcodeTypeId") // label for swagger
	@DeleteMapping("/{barcodeTypeId}")
	public ResponseEntity<?> deleteBarcodeTypeId(@RequestParam String warehouseId,@PathVariable Long barcodeTypeId,@RequestParam String companyCodeId,
												 @RequestParam String languageId,@RequestParam String plantId,
			 @RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			barcodetypeidService.deleteBarcodeTypeId(warehouseId, barcodeTypeId, companyCodeId, languageId, plantId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
	//Search
	@ApiOperation(response = BarcodeTypeId.class, value = "Find BarcodeTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findBarcodeTypeId(@Valid @RequestBody FindBarcodeTypeId findBarcodeTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findBarcodeTypeId.getCompanyCodeId(), findBarcodeTypeId.getPlantId(), findBarcodeTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<BarcodeTypeId> createdBarcodeTypeId = barcodetypeidService.findBarcodeTypeId(findBarcodeTypeId);
			return new ResponseEntity<>(createdBarcodeTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
}