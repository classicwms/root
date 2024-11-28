package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.transfertypeid.AddTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.FindTransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.TransferTypeId;
import com.tekclover.wms.api.idmaster.model.transfertypeid.UpdateTransferTypeId;
import com.tekclover.wms.api.idmaster.service.TransferTypeIdService;
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
@Api(tags = {"TransferTypeId"}, value = "TransferTypeId  Operations related to TransferTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "TransferTypeId ",description = "Operations related to TransferTypeId ")})
@RequestMapping("/transfertypeid")
@RestController
public class TransferTypeIdController {
	
	@Autowired
	TransferTypeIdService transfertypeidService;
	
    @ApiOperation(response = TransferTypeId.class, value = "Get all TransferTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<TransferTypeId> transfertypeidList = transfertypeidService.getTransferTypeIds();
		return new ResponseEntity<>(transfertypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = TransferTypeId.class, value = "Get a TransferTypeId") // label for swagger 
	@GetMapping("/{transferTypeId}")
	public ResponseEntity<?> getTransferTypeId(@RequestParam String warehouseId,@PathVariable String transferTypeId,
											   @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	TransferTypeId transfertypeid = 
    			transfertypeidService.getTransferTypeId(warehouseId,transferTypeId,companyCodeId,languageId,plantId);
    	log.info("TransferTypeId : " + transfertypeid);
		return new ResponseEntity<>(transfertypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = TransferTypeId.class, value = "Create TransferTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postTransferTypeId(@Valid @RequestBody AddTransferTypeId newTransferTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		TransferTypeId createdTransferTypeId = transfertypeidService.createTransferTypeId(newTransferTypeId, loginUserID);
		return new ResponseEntity<>(createdTransferTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = TransferTypeId.class, value = "Update TransferTypeId") // label for swagger
    @PatchMapping("/{transferTypeId}")
	public ResponseEntity<?> patchTransferTypeId(@RequestParam String warehouseId,@PathVariable String transferTypeId,
			 @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateTransferTypeId updateTransferTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		TransferTypeId createdTransferTypeId = 
				transfertypeidService.updateTransferTypeId(warehouseId, transferTypeId,companyCodeId,languageId,plantId,loginUserID, updateTransferTypeId);
		return new ResponseEntity<>(createdTransferTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = TransferTypeId.class, value = "Delete TransferTypeId") // label for swagger
	@DeleteMapping("/{transferTypeId}")
	public ResponseEntity<?> deleteTransferTypeId(@RequestParam String warehouseId,@PathVariable String transferTypeId,
			@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId, @RequestParam String loginUserID) {
    	transfertypeidService.deleteTransferTypeId(warehouseId, transferTypeId,companyCodeId,languageId,plantId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = TransferTypeId.class, value = "Find TransferTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findTransferTypeId(@Valid @RequestBody FindTransferTypeId findTransferTypeId) throws Exception {
		List<TransferTypeId> createdTransferTypeId = transfertypeidService.findTransferTypeId(findTransferTypeId);
		return new ResponseEntity<>(createdTransferTypeId, HttpStatus.OK);
	}
}