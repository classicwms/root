package com.tekclover.wms.api.masters.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.tekclover.wms.api.masters.model.imbasicdata1.AddImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.ImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.SearchImBasicData1;
import com.tekclover.wms.api.masters.model.imbasicdata1.UpdateImBasicData1;
import com.tekclover.wms.api.masters.service.ImBasicData1Service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImBasicData1"}, value = "ImBasicData1 Operations related to ImBasicData1Controller") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImBasicData1 ",description = "Operations related to ImBasicData1")})
@RequestMapping("/imbasicdata1")
@RestController
public class ImBasicData1Controller {
	
	@Autowired
	ImBasicData1Service imbasicdata1Service;
	
    @ApiOperation(response = ImBasicData1.class, value = "Get all ImBasicData1 details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		Iterable<ImBasicData1> imbasicdata1List = imbasicdata1Service.getImBasicData1s();
		return new ResponseEntity<>(imbasicdata1List, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImBasicData1.class, value = "Get a ImBasicData1") // label for swagger 
	@GetMapping("/{itemCode}")
	public ResponseEntity<?> getImBasicData1ByItemCode(@PathVariable String itemCode,
			@RequestParam String warehouseId) {
    	ImBasicData1 imbasicdata1 = imbasicdata1Service.getImBasicData1 (itemCode, warehouseId);
    	log.info("ImBasicData1 : " + imbasicdata1);
		return new ResponseEntity<>(imbasicdata1, HttpStatus.OK);
	}
    
	@ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
	@PostMapping("/findImBasicData1/pagination")
	public Page<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy)
			throws Exception {
		return imbasicdata1Service.findImBasicData1(searchImBasicData1, pageNo, pageSize, sortBy);
	}
	
	@ApiOperation(response = ImBasicData1.class, value = "Search ImBasicData1") // label for swagger
	@PostMapping("/findImBasicData1")
	public List<ImBasicData1> findImBasicData1(@RequestBody SearchImBasicData1 searchImBasicData1)
			throws Exception {
		return imbasicdata1Service.findImBasicData1(searchImBasicData1);
	}
	
    @ApiOperation(response = ImBasicData1.class, value = "Create ImBasicData1") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImBasicData1(@Valid @RequestBody AddImBasicData1 newImBasicData1, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData1 createdImBasicData1 = imbasicdata1Service.createImBasicData1(newImBasicData1, loginUserID);
		return new ResponseEntity<>(createdImBasicData1 , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImBasicData1.class, value = "Update ImBasicData1") // label for swagger
    @PatchMapping("/{itemCode}")
	public ResponseEntity<?> patchImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId,
			@Valid @RequestBody UpdateImBasicData1 updateImBasicData1, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImBasicData1 createdImBasicData1 = imbasicdata1Service.updateImBasicData1(itemCode, warehouseId, 
				updateImBasicData1, loginUserID);
		return new ResponseEntity<>(createdImBasicData1 , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImBasicData1.class, value = "Delete ImBasicData1") // label for swagger
	@DeleteMapping("/{itemCode}")
	public ResponseEntity<?> deleteImBasicData1(@PathVariable String itemCode, @RequestParam String warehouseId, 
			@RequestParam String loginUserID) {
    	imbasicdata1Service.deleteImBasicData1(itemCode, warehouseId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}