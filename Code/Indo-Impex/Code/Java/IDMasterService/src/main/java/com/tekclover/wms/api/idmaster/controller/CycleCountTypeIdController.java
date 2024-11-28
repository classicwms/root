package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.AddCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.CycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.FindCycleCountTypeId;
import com.tekclover.wms.api.idmaster.model.cyclecounttypeid.UpdateCycleCountTypeId;
import com.tekclover.wms.api.idmaster.service.CycleCountTypeIdService;
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
@Api(tags = {"CycleCountTypeId"}, value = "CycleCountTypeId  Operations related to CycleCountTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CycleCountTypeId ",description = "Operations related to CycleCountTypeId ")})
@RequestMapping("/cyclecounttypeid")
@RestController
public class CycleCountTypeIdController {
	
	@Autowired
	CycleCountTypeIdService cyclecounttypeidService;
	
    @ApiOperation(response = CycleCountTypeId.class, value = "Get all CycleCountTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<CycleCountTypeId> cyclecounttypeidList = cyclecounttypeidService.getCycleCountTypeIds();
		return new ResponseEntity<>(cyclecounttypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = CycleCountTypeId.class, value = "Get a CycleCountTypeId") // label for swagger 
	@GetMapping("/{cycleCountTypeId}")
	public ResponseEntity<?> getCycleCountTypeId(@PathVariable String cycleCountTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId) {
    	CycleCountTypeId cyclecounttypeid = 
    			cyclecounttypeidService.getCycleCountTypeId(warehouseId,cycleCountTypeId,companyCodeId,languageId,plantId);
    	log.info("CycleCountTypeId : " + cyclecounttypeid);
		return new ResponseEntity<>(cyclecounttypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = CycleCountTypeId.class, value = "Create CycleCountTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postCycleCountTypeId(@Valid @RequestBody AddCycleCountTypeId newCycleCountTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		CycleCountTypeId createdCycleCountTypeId = cyclecounttypeidService.createCycleCountTypeId(newCycleCountTypeId, loginUserID);
		return new ResponseEntity<>(createdCycleCountTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = CycleCountTypeId.class, value = "Update CycleCountTypeId") // label for swagger
    @PatchMapping("/{cycleCountTypeId}")
	public ResponseEntity<?> patchCycleCountTypeId(@PathVariable String cycleCountTypeId,
			@RequestParam String warehouseId, @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateCycleCountTypeId updateCycleCountTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		CycleCountTypeId createdCycleCountTypeId = 
				cyclecounttypeidService.updateCycleCountTypeId(warehouseId, cycleCountTypeId,companyCodeId,languageId,plantId,loginUserID,updateCycleCountTypeId);
		return new ResponseEntity<>(createdCycleCountTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = CycleCountTypeId.class, value = "Delete CycleCountTypeId") // label for swagger
	@DeleteMapping("/{cycleCountTypeId}")
	public ResponseEntity<?> deleteCycleCountTypeId(@PathVariable String cycleCountTypeId,
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
    	cyclecounttypeidService.deleteCycleCountTypeId(warehouseId, cycleCountTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = CycleCountTypeId.class, value = "Find CycleCountTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findCycleCountTypeId(@Valid @RequestBody FindCycleCountTypeId findCycleCountTypeId) throws Exception {
		List<CycleCountTypeId> createdCycleCountTypeId = cyclecounttypeidService.findCycleCountTypeId(findCycleCountTypeId);
		return new ResponseEntity<>(createdCycleCountTypeId, HttpStatus.OK);
	}
}