package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.model.stocktypeid.AddStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.FindStockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.StockTypeId;
import com.tekclover.wms.api.idmaster.model.stocktypeid.UpdateStockTypeId;
import com.tekclover.wms.api.idmaster.service.StockTypeIdService;
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
@Api(tags = {"StockTypeId"}, value = "StockTypeId  Operations related to StockTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StockTypeId ",description = "Operations related to StockTypeId ")})
@RequestMapping("/stocktypeid")
@RestController
public class StockTypeIdController {
	
	@Autowired
	StockTypeIdService stocktypeidService;
	
    @ApiOperation(response = StockTypeId.class, value = "Get all StockTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StockTypeId> stocktypeidList = stocktypeidService.getStockTypeIds();
		return new ResponseEntity<>(stocktypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = StockTypeId.class, value = "Get a StockTypeId") // label for swagger 
	@GetMapping("/{stockTypeId}")
	public ResponseEntity<?> getStockTypeId(@RequestParam String warehouseId,@PathVariable String stockTypeId,@RequestParam String companyCodeId,
											@RequestParam String languageId,@RequestParam String plantId) {
    	StockTypeId stocktypeid = 
    			stocktypeidService.getStockTypeId(warehouseId,stockTypeId,companyCodeId,languageId,plantId);
    	log.info("StockTypeId : " + stocktypeid);
		return new ResponseEntity<>(stocktypeid, HttpStatus.OK);
	}
    
    @ApiOperation(response = StockTypeId.class, value = "Create StockTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStockTypeId(@Valid @RequestBody AddStockTypeId newStockTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		StockTypeId createdStockTypeId = stocktypeidService.createStockTypeId(newStockTypeId, loginUserID);
		return new ResponseEntity<>(createdStockTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StockTypeId.class, value = "Update StockTypeId") // label for swagger
    @PatchMapping("/{stockTypeId}")
	public ResponseEntity<?> patchStockTypeId(@RequestParam String warehouseId,@PathVariable String stockTypeId,
											  @RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
			@Valid @RequestBody UpdateStockTypeId updateStockTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		StockTypeId createdStockTypeId = 
				stocktypeidService.updateStockTypeId(warehouseId, stockTypeId,companyCodeId,languageId,plantId,loginUserID, updateStockTypeId);
		return new ResponseEntity<>(createdStockTypeId , HttpStatus.OK);
	}
    
    @ApiOperation(response = StockTypeId.class, value = "Delete StockTypeId") // label for swagger
	@DeleteMapping("/{stockTypeId}")
	public ResponseEntity<?> deleteStockTypeId(@RequestParam String warehouseId,@PathVariable String stockTypeId, @RequestParam String companyCodeId,
											   @RequestParam String languageId,@RequestParam String plantId,@RequestParam String loginUserID) {
    	stocktypeidService.deleteStockTypeId(warehouseId, stockTypeId,companyCodeId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = StockTypeId.class, value = "Find StockTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findStockTypeId(@Valid @RequestBody FindStockTypeId findStockTypeId) throws Exception {
		List<StockTypeId> createdStockTypeId = stocktypeidService.findStockTypeId(findStockTypeId);
		return new ResponseEntity<>(createdStockTypeId, HttpStatus.OK);
	}
}