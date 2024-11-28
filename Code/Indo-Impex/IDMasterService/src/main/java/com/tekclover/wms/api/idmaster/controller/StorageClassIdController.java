package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.storageclassid.FindStorageClassId;
import com.tekclover.wms.api.idmaster.repository.CompanyIdRepository;
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

import com.tekclover.wms.api.idmaster.model.storageclassid.AddStorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.StorageClassId;
import com.tekclover.wms.api.idmaster.model.storageclassid.UpdateStorageClassId;
import com.tekclover.wms.api.idmaster.service.StorageClassIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"StorageClassId"}, value = "StorageClassId  Operations related to StorageClassIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageClassId ",description = "Operations related to StorageClassId ")})
@RequestMapping("/storageclassid")
@RestController
public class StorageClassIdController {
	@Autowired
	private CompanyIdRepository companyIdRepository;

	@Autowired
	StorageClassIdService storageclassidService;
	
    @ApiOperation(response = StorageClassId.class, value = "Get all StorageClassId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StorageClassId> storageclassidList = storageclassidService.getStorageClassIds();
		return new ResponseEntity<>(storageclassidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StorageClassId.class, value = "Get a StorageClassId") // label for swagger 
	@GetMapping("/{storageClassId}")
	public ResponseEntity<?> getStorageClassId(@RequestParam String warehouseId,@PathVariable Long storageClassId, @RequestParam String companyCodeId,
											   @RequestParam String languageId,@RequestParam String plantId) {
    	StorageClassId storageclassid = 
    			storageclassidService.getStorageClassId(warehouseId, storageClassId,companyCodeId,languageId,plantId);
    	log.info("StorageClassId : " + storageclassid);
		return new ResponseEntity<>(storageclassid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = StorageClassId.class, value = "Search StorageClassId") // label for swagger
//	@PostMapping("/findStorageClassId")
//	public List<StorageClassId> findStorageClassId(@RequestBody SearchStorageClassId searchStorageClassId)
//			throws Exception {
//		return storageclassidService.findStorageClassId(searchStorageClassId);
//	}
    
    @ApiOperation(response = StorageClassId.class, value = "Create StorageClassId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStorageClassId(@Valid @RequestBody AddStorageClassId newStorageClassId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClassId createdStorageClassId = storageclassidService.createStorageClassId(newStorageClassId, loginUserID);
		return new ResponseEntity<>(createdStorageClassId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageClassId.class, value = "Update StorageClassId") // label for swagger
    @PatchMapping("/{storageClassId}")
	public ResponseEntity<?> patchStorageClassId(@RequestParam String warehouseId,@PathVariable Long storageClassId,
			  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateStorageClassId updateStorageClassId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StorageClassId createdStorageClassId = 
				storageclassidService.updateStorageClassId(warehouseId, storageClassId,companyCodeId,languageId,plantId,loginUserID, updateStorageClassId);
		return new ResponseEntity<>(createdStorageClassId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StorageClassId.class, value = "Delete StorageClassId") // label for swagger
	@DeleteMapping("/{storageClassId}")
	public ResponseEntity<?> deleteStorageClassId(@RequestParam String warehouseId,@PathVariable Long storageClassId,
			 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID) {
    	storageclassidService.deleteStorageClassId(warehouseId, storageClassId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = StorageClassId.class, value = "Find StorageClassId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStorageClassId(@Valid @RequestBody FindStorageClassId findStorageClassId) throws Exception {
		List<StorageClassId> createdStorageClassId = storageclassidService.findStorageClassId(findStorageClassId);
		return new ResponseEntity<>(createdStorageClassId, HttpStatus.OK);
	}
}