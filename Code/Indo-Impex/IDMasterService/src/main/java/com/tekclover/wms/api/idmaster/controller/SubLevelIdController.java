package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.sublevelid.AddSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.FindSubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.SubLevelId;
import com.tekclover.wms.api.idmaster.model.sublevelid.UpdateSubLevelId;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
import com.tekclover.wms.api.idmaster.service.SubLevelIdService;
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
@Api(tags = {"SubLevelId"}, value = "SubLevelId  Operations related to SubLevelIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "SubLevelId ",description = "Operations related to SubLevelId ")})
@RequestMapping("/sublevelid")
@RestController
public class SubLevelIdController {
	@Autowired
	private PlantIdRepository plantIdRepository;

	@Autowired
	SubLevelIdService sublevelidService;
	
    @ApiOperation(response = SubLevelId.class, value = "Get all SubLevelId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<SubLevelId> sublevelidList = sublevelidService.getSubLevelIds();
		return new ResponseEntity<>(sublevelidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = SubLevelId.class, value = "Get a SubLevelId") // label for swagger 
	@GetMapping("/{subLevelId}")
	public ResponseEntity<?> getSubLevelId(@PathVariable String subLevelId,
			@RequestParam String warehouseId,@RequestParam Long levelId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	SubLevelId sublevelid = 
    			sublevelidService.getSubLevelId(warehouseId, subLevelId, levelId,companyCodeId,languageId,plantId);
    	log.info("SubLevelId : " + sublevelid);
		return new ResponseEntity<>(sublevelid, HttpStatus.OK);
	}
    
    @ApiOperation(response = SubLevelId.class, value = "Create SubLevelId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postSubLevelId(@Valid @RequestBody AddSubLevelId newSubLevelId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		SubLevelId createdSubLevelId = sublevelidService.createSubLevelId(newSubLevelId, loginUserID);
		return new ResponseEntity<>(createdSubLevelId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubLevelId.class, value = "Update SubLevelId") // label for swagger
    @PatchMapping("/{subLevelId}")
	public ResponseEntity<?> patchSubLevelId(@PathVariable String subLevelId,
			@RequestParam String warehouseId,@RequestParam Long levelId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateSubLevelId updateSubLevelId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		SubLevelId createdSubLevelId = 
				sublevelidService.updateSubLevelId(warehouseId, subLevelId,levelId,companyCodeId,languageId,plantId,loginUserID, updateSubLevelId);
		return new ResponseEntity<>(createdSubLevelId , HttpStatus.OK);
	}
    
    @ApiOperation(response = SubLevelId.class, value = "Delete SubLevelId") // label for swagger
	@DeleteMapping("/{subLevelId}")
	public ResponseEntity<?> deleteSubLevelId(@PathVariable String subLevelId,
			@RequestParam String warehouseId,@RequestParam Long levelId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
    	sublevelidService.deleteSubLevelId(warehouseId, subLevelId, levelId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = SubLevelId.class, value = "Find SubLevelId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findSubLevelId(@Valid @RequestBody FindSubLevelId findSubLevelId) throws Exception {
		List<SubLevelId> createdSubLevelId= sublevelidService.findSubLevelId(findSubLevelId);
		return new ResponseEntity<>(createdSubLevelId, HttpStatus.OK);
	}
}