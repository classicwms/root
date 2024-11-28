package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.roleaccess.FindRoleAccess;
import com.tekclover.wms.api.idmaster.repository.RoleAccessRepository;
import com.tekclover.wms.api.idmaster.repository.RowIdRepository;
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

import com.tekclover.wms.api.idmaster.model.roleaccess.AddRoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.RoleAccess;
import com.tekclover.wms.api.idmaster.model.roleaccess.UpdateRoleAccess;
import com.tekclover.wms.api.idmaster.service.RoleAccessService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"RoleAccess"}, value = "RoleAccess  Operations related to RoleAccessController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "RoleAccess ",description = "Operations related to RoleAccess ")})
@RequestMapping("/roleaccess")
@RestController
public class RoleAccessController {
	@Autowired
	private RoleAccessRepository roleAccessRepository;
	@Autowired
	private RowIdRepository rowIdRepository;

	@Autowired
	RoleAccessService roleaccessService;
	
    @ApiOperation(response = RoleAccess.class, value = "Get all RoleAccess details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<RoleAccess> roleaccessList = roleaccessService.getRoleAccesss();
		return new ResponseEntity<>(roleaccessList, HttpStatus.OK); 
	}
    
//    @ApiOperation(response = RoleAccess.class, value = "Get a RoleAccess") // label for swagger 
//	@GetMapping("/{userRoleId}")
//	public ResponseEntity<?> getRoleAccess(@PathVariable Long userRoleId, @RequestParam String warehouseId, 
//			@RequestParam Long menuId, @RequestParam Long subMenuId) {
//    	RoleAccess roleaccess = roleaccessService.getRoleAccess(warehouseId, userRoleId, menuId, subMenuId);
//    	log.info("RoleAccess : " + roleaccess);
//		return new ResponseEntity<>(roleaccess, HttpStatus.OK);
//	}
    
    @ApiOperation(response = RoleAccess.class, value = "Get a RoleAccess") // label for swagger 
   	@GetMapping("/{roleId}")
   	public ResponseEntity<?> getRoleAccess(@PathVariable Long roleId, @RequestParam String warehouseId,
											  @RequestParam String companyCodeId, @RequestParam String languageId,
											  @RequestParam String plantId) {

       	List<RoleAccess> roleaccess = roleaccessService.getRoleAccess(warehouseId, roleId,companyCodeId,languageId,plantId);

       	log.info("RoleAccess : " + roleaccess);

   		return new ResponseEntity<>(roleaccess, HttpStatus.OK);
   	}
    // Long menuId, Long subMenuId,String companyCodeId,String languageId,String plantId
//	@ApiOperation(response = RoleAccess.class, value = "Search RoleAccess") // label for swagger
//	@PostMapping("/findRoleAccess")
//	public List<RoleAccess> findRoleAccess(@RequestBody SearchRoleAccess searchRoleAccess)
//			throws Exception {
//		return roleaccessService.findRoleAccess(searchRoleAccess);
//	}
    
    @ApiOperation(response = RoleAccess.class, value = "Create RoleAccess") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRoleAccess(@Valid @RequestBody List<AddRoleAccess> newRoleAccess, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {

		List<RoleAccess> createdRoleAccess = roleaccessService.createRoleAccess(newRoleAccess, loginUserID);
		return new ResponseEntity<>(createdRoleAccess , HttpStatus.OK);
	}
    
    @ApiOperation(response = RoleAccess.class, value = "Update RoleAccess") // label for swagger
    @PatchMapping("/{roleId}")
	public ResponseEntity<?> patchRoleAccess(@PathVariable Long roleId, @RequestParam String warehouseId,
											 @RequestParam String companyCodeId, @RequestParam String languageId,
											 @RequestParam String plantId, @RequestParam String loginUserID,
											 @Valid @RequestBody List<AddRoleAccess> updateRoleAccess )
			throws IllegalAccessException, InvocationTargetException, ParseException {

		List<RoleAccess> updatedRoleAccess =
				roleaccessService.updateRoleAccess(warehouseId, roleId, companyCodeId, languageId,
													plantId, loginUserID, updateRoleAccess);
		return new ResponseEntity<>(updatedRoleAccess , HttpStatus.OK);
	}
    
    @ApiOperation(response = RoleAccess.class, value = "Delete RoleAccess") // label for swagger
	@DeleteMapping("/{roleId}")
	public ResponseEntity<?> deleteRoleAccess(@PathVariable Long roleId, @RequestParam String warehouseId,
											  @RequestParam String companyCodeId, @RequestParam String languageId,
											  @RequestParam String plantId, @RequestParam String loginUserID) {

    	roleaccessService.deleteRoleAccess(warehouseId, roleId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//Search
	@ApiOperation(response = RoleAccess.class, value = "Find RoleAccess") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findRoleAccess(@Valid @RequestBody FindRoleAccess findRoleAccess) throws Exception {

		List<RoleAccess> createdRoleAccess = roleaccessService.findRoleAccess(findRoleAccess);
		return new ResponseEntity<>(createdRoleAccess, HttpStatus.OK);
	}
}