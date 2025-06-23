package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.service.StockAdjustmentService;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.stockadjustment.SearchStockAdjustment;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.stockadjustment.StockAdjustment;
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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"StockAdjustment"}, value = "StockAdjustment  Operations related to StockAdjustmentController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StockAdjustment ",description = "Operations related to StockAdjustment ")})
@RequestMapping("/stockadjustment")
@RestController
public class StockAdjustmentController {
	
	@Autowired
    StockAdjustmentService stockAdjustmentService;
	@Autowired
	DbConfigRepository dbConfigRepository;

	@ApiOperation(response = StockAdjustment.class, value = "Get a StockAdjustment") // label for swagger
	@GetMapping("/{stockAdjustmentKey}")
	public ResponseEntity<?> getStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
												@RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
												@RequestParam String manufacturerName, @RequestParam String storageBin) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<StockAdjustment> stockAdjustment = stockAdjustmentService.getStockAdjustment(
				companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, stockAdjustmentKey, storageBin);
		log.info("stockAdjustment : " + stockAdjustment);
		return new ResponseEntity<>(stockAdjustment, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	//V2
	@ApiOperation(response = StockAdjustment.class, value = "Search StockAdjustment") // label for swagger
	@PostMapping("/findStockAdjustment")
	public Stream<StockAdjustment> findStockAdjustment(@RequestBody SearchStockAdjustment searchInventory)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbList(searchInventory.getCompanyCodeId(), searchInventory.getPlantId(), searchInventory.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return stockAdjustmentService.findStockAdjustment(searchInventory);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = StockAdjustment.class, value = "Update StockAdjustment") // label for swagger
	@PatchMapping("/{stockAdjustmentKey}")
	public ResponseEntity<?> patchStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
												  @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
												  @RequestParam String manufacturerName, @RequestParam String loginUserId,
												  @RequestBody List<StockAdjustment> updateStockAdjustment)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<StockAdjustment> updatedStockAdjustment = stockAdjustmentService.updateStockAdjustment(
				companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, stockAdjustmentKey, updateStockAdjustment, loginUserId);
		return new ResponseEntity<>(updatedStockAdjustment, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = StockAdjustment.class, value = "Delete StockAdjustment") // label for swagger
	@DeleteMapping("/{stockAdjustmentKey}")
	public ResponseEntity<?> deleteStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
												   @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
												   @RequestParam String manufacturerName, @RequestParam String storageBin, @RequestParam String loginUserId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		stockAdjustmentService.deleteStockAdjustment(companyCode, plantId, languageId, warehouseId,
				loginUserId, itemCode, manufacturerName, stockAdjustmentKey, storageBin);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}