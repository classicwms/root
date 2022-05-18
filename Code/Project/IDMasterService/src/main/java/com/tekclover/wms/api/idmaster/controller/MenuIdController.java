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

import com.tekclover.wms.api.idmaster.model.menuid.AddMenuId;
import com.tekclover.wms.api.idmaster.model.menuid.MenuId;
import com.tekclover.wms.api.idmaster.model.menuid.UpdateMenuId;
import com.tekclover.wms.api.idmaster.service.MenuIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"MenuId"}, value = "MenuId  Operations related to MenuIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "MenuId ",description = "Operations related to MenuId ")})
@RequestMapping("/menuid")
@RestController
public class MenuIdController {
	
	@Autowired
	MenuIdService menuidService;
	
    @ApiOperation(response = MenuId.class, value = "Get all MenuId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<MenuId> menuidList = menuidService.getMenuIds();
		return new ResponseEntity<>(menuidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = MenuId.class, value = "Get a MenuId") // label for swagger 
	@GetMapping("/{menuId}")
	public ResponseEntity<?> getMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, 
			@RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String authorizationObjectValue) {
    	MenuId menuid = menuidService.getMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue);
    	log.info("MenuId : " + menuid);
		return new ResponseEntity<>(menuid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = MenuId.class, value = "Search MenuId") // label for swagger
//	@PostMapping("/findMenuId")
//	public List<MenuId> findMenuId(@RequestBody SearchMenuId searchMenuId)
//			throws Exception {
//		return menuidService.findMenuId(searchMenuId);
//	}
    
    @ApiOperation(response = MenuId.class, value = "Create MenuId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postMenuId(@Valid @RequestBody AddMenuId newMenuId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		MenuId createdMenuId = menuidService.createMenuId(newMenuId, loginUserID);
		return new ResponseEntity<>(createdMenuId , HttpStatus.OK);
	}
    
    @ApiOperation(response = MenuId.class, value = "Update MenuId") // label for swagger
    @PatchMapping("/{menuId}")
	public ResponseEntity<?> patchMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, 
			@RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, 
			@RequestParam String authorizationObjectValue, @Valid @RequestBody UpdateMenuId updateMenuId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		MenuId createdMenuId = 
				menuidService.updateMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue, loginUserID, updateMenuId);
		return new ResponseEntity<>(createdMenuId , HttpStatus.OK);
	}
    
    @ApiOperation(response = MenuId.class, value = "Delete MenuId") // label for swagger
	@DeleteMapping("/{menuId}")
	public ResponseEntity<?> deleteMenuId(@PathVariable Long menuId, @RequestParam String warehouseId, 
			@RequestParam Long subMenuId, @RequestParam Long authorizationObjectId, @RequestParam String authorizationObjectValue, 
			@RequestParam String loginUserID) {
    	menuidService.deleteMenuId(warehouseId, menuId, subMenuId, authorizationObjectId, authorizationObjectValue, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}