package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.handlingunitid.AddHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.FindHandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.HandlingUnitId;
import com.tekclover.wms.api.idmaster.model.handlingunitid.UpdateHandlingUnitId;
import com.tekclover.wms.api.idmaster.service.HandlingUnitIdService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"HandlingUnitId"}, value = "HandlingUnitId  Operations related to HandlingUnitIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingUnitId ",description = "Operations related to HandlingUnitId ")})
@RequestMapping("/handlingunitid")
@RestController
public class HandlingUnitIdController {
	
	@Autowired
	HandlingUnitIdService handlingunitidService;
	
    @ApiOperation(response = HandlingUnitId.class, value = "Get all HandlingUnitId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<HandlingUnitId> handlingunitidList = handlingunitidService.getHandlingUnitIds();
		return new ResponseEntity<>(handlingunitidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Get a HandlingUnitId") // label for swagger 
	@GetMapping("/{handlingUnitNumber}")
	public ResponseEntity<?> getHandlingUnitId(@PathVariable String handlingUnitNumber,@RequestParam String warehouseId,
											   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	HandlingUnitId handlingunitid = 
    			handlingunitidService.getHandlingUnitId(warehouseId,handlingUnitNumber,companyCodeId,languageId,plantId);

		log.info("HandlingUnitId : " + handlingunitid);
		return new ResponseEntity<>(handlingunitid, HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Create HandlingUnitId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postHandlingUnitId(@Valid @RequestBody AddHandlingUnitId newHandlingUnitId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		HandlingUnitId createdHandlingUnitId = handlingunitidService.createHandlingUnitId(newHandlingUnitId, loginUserID);
		return new ResponseEntity<>(createdHandlingUnitId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Update HandlingUnitId") // label for swagger
    @PatchMapping("/{handlingUnitNumber}")
	public ResponseEntity<?> patchHandlingUnitId(@PathVariable String handlingUnitNumber,@RequestParam String warehouseId,
												 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
												 @Valid @RequestBody UpdateHandlingUnitId updateHandlingUnitId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		HandlingUnitId createdHandlingUnitId =
				handlingunitidService.updateHandlingUnitId(warehouseId,handlingUnitNumber,companyCodeId,languageId,
						plantId,loginUserID, updateHandlingUnitId);

		return new ResponseEntity<>(createdHandlingUnitId , HttpStatus.OK);
	}
    
    @ApiOperation(response = HandlingUnitId.class, value = "Delete HandlingUnitId") // label for swagger
	@DeleteMapping("/{handlingUnitNumber}")
	public ResponseEntity<?> deleteHandlingUnitId(@PathVariable String handlingUnitNumber,@RequestParam String warehouseId,
												  @RequestParam String companyCodeId,@RequestParam String languageId,
												  @RequestParam String plantId,@RequestParam String loginUserID) {

		handlingunitidService.deleteHandlingUnitId(warehouseId,handlingUnitNumber,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//Search
	@ApiOperation(response = HandlingUnitId.class, value = "Find HandlingUnitId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findHandlingUnitId(@Valid @RequestBody FindHandlingUnitId findHandlingUnitId) throws Exception {
		List<HandlingUnitId> createdHandlingUnitId = handlingunitidService.findHandlingUnitId(findHandlingUnitId);
		return new ResponseEntity<>(createdHandlingUnitId, HttpStatus.OK);
	}
}