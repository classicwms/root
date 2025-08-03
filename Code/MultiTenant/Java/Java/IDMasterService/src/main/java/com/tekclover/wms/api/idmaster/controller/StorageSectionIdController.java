package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.storagesectionid.FindStorageSectionId;
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

import com.tekclover.wms.api.idmaster.model.storagesectionid.AddStorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.StorageSectionId;
import com.tekclover.wms.api.idmaster.model.storagesectionid.UpdateStorageSectionId;
import com.tekclover.wms.api.idmaster.service.StorageSectionIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageSectionId"}, value = "StorageSectionId  Operations related to StorageSectionIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageSectionId ",description = "Operations related to StorageSectionId ")})
@RequestMapping("/storagesectionid")
@RestController
public class StorageSectionIdController {

	@Autowired
	StorageSectionIdService storagesectionidService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@ApiOperation(response = StorageSectionId.class, value = "Get all StorageSectionId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageSectionId> storagesectionidList = storagesectionidService.getStorageSectionIds();
		return new ResponseEntity<>(storagesectionidList, HttpStatus.OK);
	}

	@ApiOperation(response = StorageSectionId.class, value = "Get a StorageSectionId") // label for swagger
	@GetMapping("/{storageSectionId}")
	public ResponseEntity<?> getStorageSectionId(@RequestParam String warehouseId,@RequestParam Long floorId,@PathVariable String storageSectionId,
												 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		StorageSectionId storagesectionid =
				storagesectionidService.getStorageSectionId(warehouseId,floorId,storageSectionId,companyCodeId,languageId,plantId);
		log.info("StorageSectionId : " + storagesectionid);
		return new ResponseEntity<>(storagesectionid, HttpStatus.OK);
	}
finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = StorageSectionId.class, value = "Create StorageSectionId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageSectionId(@Valid @RequestBody AddStorageSectionId newStorageSectionId,
												  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newStorageSectionId.getCompanyCodeId(), newStorageSectionId.getPlantId(), newStorageSectionId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		StorageSectionId createdStorageSectionId = storagesectionidService.createStorageSectionId(newStorageSectionId, loginUserID);
		return new ResponseEntity<>(createdStorageSectionId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = StorageSectionId.class, value = "Update StorageSectionId") // label for swagger
	@PatchMapping("/{storageSectionId}")
	public ResponseEntity<?> patchStorageSectionId(@RequestParam String warehouseId, @RequestParam Long floorId,@PathVariable String storageSectionId,
												   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID,
												   @Valid @RequestBody UpdateStorageSectionId updateStorageSectionId)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		StorageSectionId createdStorageSectionId =
				storagesectionidService.updateStorageSectionId(warehouseId,floorId,storageSectionId,companyCodeId,languageId,plantId,loginUserID, updateStorageSectionId);
		return new ResponseEntity<>(createdStorageSectionId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = StorageSectionId.class, value = "Delete StorageSectionId") // label for swagger
	@DeleteMapping("/{storageSectionId}")
	public ResponseEntity<?> deleteStorageSectionId(@RequestParam String warehouseId, @RequestParam Long floorId,@PathVariable String storageSectionId,@RequestParam String companyCodeId,@RequestParam String languageId,
													@RequestParam String plantId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		storagesectionidService.deleteStorageSectionId(warehouseId, floorId, storageSectionId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = StorageSectionId.class, value = "Find StorageSectionId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStorageSectionId(@Valid @RequestBody FindStorageSectionId findStorageSectionId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbNameList(findStorageSectionId.getCompanyCodeId(), findStorageSectionId.getPlantId(), findStorageSectionId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<StorageSectionId> createdStorageSectionId = storagesectionidService.findStorageSectionId(findStorageSectionId);
		return new ResponseEntity<>(createdStorageSectionId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}