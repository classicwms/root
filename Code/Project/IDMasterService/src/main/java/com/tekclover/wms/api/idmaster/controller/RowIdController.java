package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

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

import com.tekclover.wms.api.idmaster.model.rowid.AddRowId;
import com.tekclover.wms.api.idmaster.model.rowid.RowId;
import com.tekclover.wms.api.idmaster.model.rowid.UpdateRowId;
import com.tekclover.wms.api.idmaster.service.RowIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"RowId"}, value = "RowId  Operations related to RowIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "RowId ",description = "Operations related to RowId ")})
@RequestMapping("/rowid")
@RestController
public class RowIdController {
	
	@Autowired
	RowIdService rowidService;
	
    @ApiOperation(response = RowId.class, value = "Get all RowId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<RowId> rowidList = rowidService.getRowIds();
		return new ResponseEntity<>(rowidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = RowId.class, value = "Get a RowId") // label for swagger 
	@GetMapping("/{rowId}")
	public ResponseEntity<?> getRowId(@PathVariable String rowId, 
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSectionId) {
    	RowId rowid = 
    			rowidService.getRowId(warehouseId, floorId, storageSectionId, rowId);
    	log.info("RowId : " + rowid);
		return new ResponseEntity<>(rowid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = RowId.class, value = "Search RowId") // label for swagger
//	@PostMapping("/findRowId")
//	public List<RowId> findRowId(@RequestBody SearchRowId searchRowId)
//			throws Exception {
//		return rowidService.findRowId(searchRowId);
//	}
    
    @ApiOperation(response = RowId.class, value = "Create RowId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRowId(@Valid @RequestBody AddRowId newRowId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		RowId createdRowId = rowidService.createRowId(newRowId, loginUserID);
		return new ResponseEntity<>(createdRowId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RowId.class, value = "Update RowId") // label for swagger
    @PatchMapping("/{rowId}")
	public ResponseEntity<?> patchRowId(@PathVariable String rowId, 
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSectionId,
			@Valid @RequestBody UpdateRowId updateRowId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		RowId createdRowId = 
				rowidService.updateRowId(warehouseId, floorId, storageSectionId, rowId, loginUserID, updateRowId);
		return new ResponseEntity<>(createdRowId , HttpStatus.OK);
	}
    
    @ApiOperation(response = RowId.class, value = "Delete RowId") // label for swagger
	@DeleteMapping("/{rowId}")
	public ResponseEntity<?> deleteRowId(@PathVariable String rowId, 
			@RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String storageSectionId, @RequestParam String loginUserID) {
    	rowidService.deleteRowId(warehouseId, floorId, storageSectionId, rowId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}