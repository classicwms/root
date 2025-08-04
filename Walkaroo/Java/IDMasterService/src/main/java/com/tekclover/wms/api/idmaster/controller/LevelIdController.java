package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.levelid.FindLevelId;
import com.tekclover.wms.api.idmaster.model.websocketnotification.NotificationSave;
import com.tekclover.wms.api.idmaster.service.NotificationService;
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

import com.tekclover.wms.api.idmaster.model.levelid.AddLevelId;
import com.tekclover.wms.api.idmaster.model.levelid.LevelId;
import com.tekclover.wms.api.idmaster.model.levelid.UpdateLevelId;
import com.tekclover.wms.api.idmaster.service.LevelIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"LevelId"}, value = "LevelId  Operations related to LevelIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "LevelId ",description = "Operations related to LevelId ")})
@RequestMapping("/levelid")
@RestController
public class LevelIdController {
	
	@Autowired
	LevelIdService levelIdService;

    @ApiOperation(response = LevelId.class, value = "Get all LevelId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<LevelId> levelidList = levelIdService.getLevelIds();
		return new ResponseEntity<>(levelidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = LevelId.class, value = "Get a LevelId") // label for swagger 
	@GetMapping("/{levelId}")
	public ResponseEntity<?> getLevelId(@PathVariable Long levelId, @RequestParam String warehouseId, 
			@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	LevelId levelid = levelIdService.getLevelId(warehouseId, levelId,companyCodeId,languageId,plantId);
    	log.info("LevelId : " + levelid);
		return new ResponseEntity<>(levelid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = LevelId.class, value = "Search LevelId") // label for swagger
//	@PostMapping("/findLevelId")
//	public List<LevelId> findLevelId(@RequestBody SearchLevelId searchLevelId)
//			throws Exception {
//		return levelidService.findLevelId(searchLevelId);
//	}
    
    @ApiOperation(response = LevelId.class, value = "Create LevelId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postLevelId(@Valid @RequestBody AddLevelId newLevelId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		LevelId createdLevelId = levelIdService.createLevelId(newLevelId, loginUserID);
		return new ResponseEntity<>(createdLevelId , HttpStatus.OK);
	}
    
    @ApiOperation(response = LevelId.class, value = "Update LevelId") // label for swagger
    @PatchMapping("/{levelId}")
	public ResponseEntity<?> patchLevelId(@PathVariable Long levelId, 
			@RequestParam String warehouseId, @RequestParam String companyCodeId,
										  @RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateLevelId updateLevelId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		LevelId createdLevelId = 
				levelIdService.updateLevelId(warehouseId, levelId,companyCodeId,languageId,plantId,loginUserID, updateLevelId);
		return new ResponseEntity<>(createdLevelId , HttpStatus.OK);
	}
    
    @ApiOperation(response = LevelId.class, value = "Delete LevelId") // label for swagger
	@DeleteMapping("/{levelId}")
	public ResponseEntity<?> deleteLevelId(@PathVariable Long levelId, 
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,
										   @RequestParam String plantId, @RequestParam String loginUserID) {
    	levelIdService.deleteLevelId(warehouseId, levelId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = LevelId.class, value = "Find LevelId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findLevelId(@Valid @RequestBody FindLevelId findLevelId) throws Exception {
		List<LevelId> createdLevelId = levelIdService.findLevelId(findLevelId);
		return new ResponseEntity<>(createdLevelId, HttpStatus.OK);
	}
}