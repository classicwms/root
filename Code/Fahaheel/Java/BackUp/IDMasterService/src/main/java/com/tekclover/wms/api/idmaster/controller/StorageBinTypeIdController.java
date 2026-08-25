package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.FindStorageBinTypeId;
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

import com.tekclover.wms.api.idmaster.model.storagebintypeid.AddStorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.StorageBinTypeId;
import com.tekclover.wms.api.idmaster.model.storagebintypeid.UpdateStorageBinTypeId;
import com.tekclover.wms.api.idmaster.service.StorageBinTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageBinTypeId"}, value = "StorageBinTypeId  Operations related to StorageBinTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageBinTypeId ",description = "Operations related to StorageBinTypeId ")})
@RequestMapping("/storagebintypeid")
@RestController
public class StorageBinTypeIdController {
	
	@Autowired
	StorageBinTypeIdService storagebintypeidService;

	@Autowired
	DbConfigRepository dbConfigRepository;
	
    @ApiOperation(response = StorageBinTypeId.class, value = "Get all StorageBinTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageBinTypeId> storagebintypeidList = storagebintypeidService.getStorageBinTypeIds();
		return new ResponseEntity<>(storagebintypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageBinTypeId.class, value = "Get a StorageBinTypeId") // label for swagger 
	@GetMapping("/{storageBinTypeId}")
	public ResponseEntity<?> getStorageBinTypeId(@RequestParam String warehouseId,@PathVariable Long storageBinTypeId,
			 @RequestParam Long storageClassId,@RequestParam Long storageTypeId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			StorageBinTypeId storagebintypeid =
					storagebintypeidService.getStorageBinTypeId(warehouseId, storageBinTypeId, storageClassId, storageTypeId, companyCodeId, languageId, plantId);
			log.info("StorageBinTypeId : " + storagebintypeid);
			return new ResponseEntity<>(storagebintypeid, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
//	@ApiOperation(response = StorageBinTypeId.class, value = "Search StorageBinTypeId") // label for swagger
//	@PostMapping("/findStorageBinTypeId")
//	public List<StorageBinTypeId> findStorageBinTypeId(@RequestBody SearchStorageBinTypeId searchStorageBinTypeId)
//			throws Exception {
//		return storagebintypeidService.findStorageBinTypeId(searchStorageBinTypeId);
//	}
    
    @ApiOperation(response = StorageBinTypeId.class, value = "Create StorageBinTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageBinTypeId(@Valid @RequestBody AddStorageBinTypeId newStorageBinTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newStorageBinTypeId.getCompanyCodeId(), newStorageBinTypeId.getPlantId(), newStorageBinTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			StorageBinTypeId createdStorageBinTypeId = storagebintypeidService.createStorageBinTypeId(newStorageBinTypeId, loginUserID);
			return new ResponseEntity<>(createdStorageBinTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = StorageBinTypeId.class, value = "Update StorageBinTypeId") // label for swagger
    @PatchMapping("/{storageBinTypeId}")
	public ResponseEntity<?> patchStorageBinTypeId(@RequestParam String warehouseId,@PathVariable Long storageBinTypeId, @RequestParam Long storageClassId,
												   @RequestParam Long storageTypeId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID,
													@Valid @RequestBody UpdateStorageBinTypeId updateStorageBinTypeId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			StorageBinTypeId createdStorageBinTypeId =
					storagebintypeidService.updateStorageBinTypeId(warehouseId, storageBinTypeId, storageClassId, storageTypeId, companyCodeId, languageId, plantId,
							loginUserID, updateStorageBinTypeId);
			return new ResponseEntity<>(createdStorageBinTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
    
    @ApiOperation(response = StorageBinTypeId.class, value = "Delete StorageBinTypeId") // label for swagger
	@DeleteMapping("/{storageBinTypeId}")
	public ResponseEntity<?> deleteStorageBinTypeId(@RequestParam String warehouseId,@PathVariable Long storageBinTypeId, @RequestParam Long storageClassId,
													@RequestParam Long storageTypeId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			storagebintypeidService.deleteStorageBinTypeId(warehouseId, storageBinTypeId, storageClassId, storageTypeId, companyCodeId, languageId, plantId, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
	//Search
	@ApiOperation(response = StorageBinTypeId.class, value = "Find StorageBinTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStorageBinTypeId(@Valid @RequestBody FindStorageBinTypeId findStorageBinTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(findStorageBinTypeId.getCompanyCodeId(), findStorageBinTypeId.getPlantId(), findStorageBinTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<StorageBinTypeId> createdStorageBinTypeId = storagebintypeidService.findStorageBinTypeId(findStorageBinTypeId);
			return new ResponseEntity<>(createdStorageBinTypeId, HttpStatus.OK);
		}
		finally {
			DataBaseContextHolder.clear();
		}
	}
}