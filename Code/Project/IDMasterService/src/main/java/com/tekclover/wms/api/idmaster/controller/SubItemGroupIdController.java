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

import com.tekclover.wms.api.idmaster.model.subitemgroupid.AddSubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.SubItemGroupId;
import com.tekclover.wms.api.idmaster.model.subitemgroupid.UpdateSubItemGroupId;
import com.tekclover.wms.api.idmaster.service.SubItemGroupIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"SubItemGroupId"}, value = "SubItemGroupId  Operations related to SubItemGroupIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SubItemGroupId ",description = "Operations related to SubItemGroupId ")})
@RequestMapping("/subitemgroupid")
@RestController
public class SubItemGroupIdController {
	
	@Autowired
	SubItemGroupIdService subitemgroupidService;
	
    @ApiOperation(response = SubItemGroupId.class, value = "Get all SubItemGroupId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<SubItemGroupId> subitemgroupidList = subitemgroupidService.getSubItemGroupIds();
		return new ResponseEntity<>(subitemgroupidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = SubItemGroupId.class, value = "Get a SubItemGroupId") // label for swagger 
	@GetMapping("/{subItemGroupId}")
	public ResponseEntity<?> getSubItemGroupId(@PathVariable Long subItemGroupId, @RequestParam String warehouseId, 
			@RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup) {
    	SubItemGroupId subitemgroupid = 
    			subitemgroupidService.getSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup);
    	log.info("SubItemGroupId : " + subitemgroupid);
		return new ResponseEntity<>(subitemgroupid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = SubItemGroupId.class, value = "Search SubItemGroupId") // label for swagger
//	@PostMapping("/findSubItemGroupId")
//	public List<SubItemGroupId> findSubItemGroupId(@RequestBody SearchSubItemGroupId searchSubItemGroupId)
//			throws Exception {
//		return subitemgroupidService.findSubItemGroupId(searchSubItemGroupId);
//	}
    
    @ApiOperation(response = SubItemGroupId.class, value = "Create SubItemGroupId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSubItemGroupId(@Valid @RequestBody AddSubItemGroupId newSubItemGroupId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId createdSubItemGroupId = subitemgroupidService.createSubItemGroupId(newSubItemGroupId, loginUserID);
		return new ResponseEntity<>(createdSubItemGroupId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubItemGroupId.class, value = "Update SubItemGroupId") // label for swagger
    @PatchMapping("/{subItemGroupId}")
	public ResponseEntity<?> patchSubItemGroupId(@PathVariable Long subItemGroupId, 
			@RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup, 
			@Valid @RequestBody UpdateSubItemGroupId updateSubItemGroupId, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		SubItemGroupId createdSubItemGroupId = 
				subitemgroupidService.updateSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup, loginUserID, updateSubItemGroupId);
		return new ResponseEntity<>(createdSubItemGroupId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubItemGroupId.class, value = "Delete SubItemGroupId") // label for swagger
	@DeleteMapping("/{subItemGroupId}")
	public ResponseEntity<?> deleteSubItemGroupId(@PathVariable Long subItemGroupId, 
			@RequestParam String warehouseId, @RequestParam Long itemTypeId, @RequestParam Long itemGroupId, @RequestParam String subItemGroup, @RequestParam String loginUserID) {
    	subitemgroupidService.deleteSubItemGroupId(warehouseId, itemTypeId, itemGroupId, subItemGroupId, subItemGroup, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}