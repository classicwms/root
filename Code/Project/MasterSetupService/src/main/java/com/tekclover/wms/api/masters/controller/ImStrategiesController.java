package com.tekclover.wms.api.masters.controller;

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

import com.tekclover.wms.api.masters.model.imstrategies.AddImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.ImStrategies;
import com.tekclover.wms.api.masters.model.imstrategies.UpdateImStrategies;
import com.tekclover.wms.api.masters.service.ImStrategiesService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ImStrategies"}, value = "ImStrategies  Operations related to ImStrategiesController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImStrategies ",description = "Operations related to ImStrategies ")})
@RequestMapping("/imstrategies")
@RestController
public class ImStrategiesController {
	
	@Autowired
	ImStrategiesService imstrategiesService;
	
    @ApiOperation(response = ImStrategies.class, value = "Get all ImStrategies details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ImStrategies> imstrategiesList = imstrategiesService.getImStrategiess();
		return new ResponseEntity<>(imstrategiesList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ImStrategies.class, value = "Get a ImStrategies") // label for swagger 
	@GetMapping("/{strategeyTypeId}")
	public ResponseEntity<?> getImStrategies(@PathVariable String strategeyTypeId) {
    	ImStrategies imstrategies = imstrategiesService.getImStrategies(strategeyTypeId);
    	log.info("ImStrategies : " + imstrategies);
		return new ResponseEntity<>(imstrategies, HttpStatus.OK);
	}
    
//	@ApiOperation(response = ImStrategies.class, value = "Search ImStrategies") // label for swagger
//	@PostMapping("/findImStrategies")
//	public List<ImStrategies> findImStrategies(@RequestBody SearchImStrategies searchImStrategies)
//			throws ParseException {
//		return imstrategiesService.findImStrategies(searchImStrategies);
//	}
	
    @ApiOperation(response = ImStrategies.class, value = "Create ImStrategies") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postImStrategies(@Valid @RequestBody AddImStrategies newImStrategies, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImStrategies createdImStrategies = imstrategiesService.createImStrategies(newImStrategies, loginUserID);
		return new ResponseEntity<>(createdImStrategies , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImStrategies.class, value = "Update ImStrategies") // label for swagger
    @PatchMapping("/{strategeyTypeId}")
	public ResponseEntity<?> patchImStrategies(@PathVariable String strategeyTypeId, 
			@Valid @RequestBody UpdateImStrategies updateImStrategies, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		ImStrategies createdImStrategies = imstrategiesService.updateImStrategies(strategeyTypeId, updateImStrategies, loginUserID);
		return new ResponseEntity<>(createdImStrategies , HttpStatus.OK);
	}
    
    @ApiOperation(response = ImStrategies.class, value = "Delete ImStrategies") // label for swagger
	@DeleteMapping("/{strategeyTypeId}")
	public ResponseEntity<?> deleteImStrategies(@PathVariable String strategeyTypeId, @RequestParam String loginUserID) {
    	imstrategiesService.deleteImStrategies(strategeyTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}