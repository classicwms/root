package com.tekclover.wms.api.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.dto.ExcessConfirmation;
import com.tekclover.wms.api.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.SearchInventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.transaction.service.BaseService;
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

import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.service.InventoryService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Inventory"}, value = "Inventory  Operations related to InventoryController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Inventory ",description = "Operations related to Inventory ")})
@RequestMapping("/inventory")
@RestController
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	BaseService baseService;

    @ApiOperation(response = Inventory.class, value = "Get all Inventory details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Inventory> inventoryList = inventoryService.getInventorys();
		return new ResponseEntity<>(inventoryList, HttpStatus.OK);
	}
    
    @ApiOperation(response = Inventory.class, value = "Get a Inventory") // label for swagger 
	@GetMapping("/transfer")
	public ResponseEntity<?> getInventory(@RequestParam String warehouseId, @RequestParam String packBarcodes, 
			@RequestParam String itemCode, @RequestParam String storageBin) {
    	Inventory inventory = 
    			inventoryService.getInventory(warehouseId, packBarcodes, itemCode, storageBin);
    	log.info("Inventory : " + inventory);
		return new ResponseEntity<>(inventory, HttpStatus.OK);
	}
    
    @ApiOperation(response = Inventory.class, value = "Get a Inventory") // label for swagger 
	@GetMapping("/{stockTypeId}")
	public ResponseEntity<?> getInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId) {
    	Inventory inventory = 
    			inventoryService.getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
    	log.info("Inventory : " + inventory);
		return new ResponseEntity<>(inventory, HttpStatus.OK);
	}
    
    @ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
	@PostMapping("/findInventory/pagination")
	public Page<Inventory> findInventory(@RequestBody SearchInventory searchInventory,
			@RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "10") Integer pageSize,
			@RequestParam(defaultValue = "itemCode") String sortBy) 
			throws Exception {
		return inventoryService.findInventory(searchInventory, pageNo, pageSize, sortBy);
	}
    
    @ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
   	@PostMapping("/findInventory")
   	public List<Inventory> findInventory(@RequestBody SearchInventory searchInventory) 
   			throws Exception {
   		return inventoryService.findInventory(searchInventory);
   	}
	@ApiOperation(response = InventoryImpl.class, value = "Search Inventory New") // label for swagger
   	@PostMapping("/findInventoryNew")
   	public List<InventoryImpl> findInventoryNew(@RequestBody SearchInventory searchInventory)
   			throws Exception {
   		return inventoryService.findInventoryNew(searchInventory);
   	}

	@ApiOperation(response = Inventory.class, value = "Search Inventory by quantity validation") // label for swagger
	@PostMapping("/get-all-validated-inventory")
	public List<Inventory> getQuantityValidatedInventory(@RequestBody SearchInventory searchInventory)
			throws Exception {
		return inventoryService.getQuantityValidatedInventory(searchInventory);
	}
    
    @ApiOperation(response = Inventory.class, value = "Create Inventory") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInventory(@Valid @RequestBody AddInventory newInventory, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(newInventory.getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
		return new ResponseEntity<>(createdInventory , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Inventory.class, value = "Update Inventory") // label for swagger
    @PatchMapping("/{stockTypeId}")
	public ResponseEntity<?> patchInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId, @Valid @RequestBody UpdateInventory updateInventory, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(updateInventory.getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		Inventory createdInventory = 
				inventoryService.updateInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, 
						specialStockIndicatorId, updateInventory, loginUserID);
		return new ResponseEntity<>(createdInventory , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Inventory.class, value = "Delete Inventory") // label for swagger
	@DeleteMapping("/{stockTypeId}")
	public ResponseEntity<?> deleteInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId, @RequestParam String loginUserID) {
    	inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	//V2
	@ApiOperation(response = InventoryV2.class, value = "Search Inventory V2") // label for swagger
	@PostMapping("/findInventory/v2")
	public List<InventoryV2> findInventoryV2(@RequestBody SearchInventoryV2 searchInventory)
			throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchInventory.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		return inventoryService.findInventoryV2(searchInventory);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	@ApiOperation(response = IInventoryImpl.class, value = "Search Inventory V2") // label for swagger
	@PostMapping("/findInventoryNew/v2")
	public List<IInventoryImpl> findInventoryNewV2(@RequestBody SearchInventoryV2 searchInventory)
			throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchInventory.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		return inventoryService.findInventoryNewV2(searchInventory);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = InventoryV2.class, value = "Create Inventory V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postInventoryV2(@Valid @RequestBody InventoryV2 newInventory, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(newInventory.getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		InventoryV2 createdInventory = inventoryService.createInventoryV2(newInventory, loginUserID);
		return new ResponseEntity<>(createdInventory , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = InventoryV2.class, value = "Update Inventory V2") // label for swagger
	@PatchMapping("/v2/{stockTypeId}")
	public ResponseEntity<?> patchInventoryV2(@PathVariable Long stockTypeId, @RequestParam String companyCodeId, @RequestParam String plantId,
											  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String manufacturerName,
											  @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin,
											  @RequestParam Long specialStockIndicatorId, @Valid @RequestBody InventoryV2 updateInventory,
											  @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		InventoryV2 updatedInventory =
				inventoryService.updateInventoryV2(companyCodeId, plantId, languageId, warehouseId,
						packBarcodes, itemCode, manufacturerName, storageBin,  stockTypeId,
						specialStockIndicatorId, updateInventory, loginUserID);
		return new ResponseEntity<>(updatedInventory , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = InventoryV2.class, value = "Delete Inventory V2") // label for swagger
	@DeleteMapping("/v2/{stockTypeId}")
	public ResponseEntity<?> deleteInventoryV2(@PathVariable Long stockTypeId, @RequestParam String companyCodeId, @RequestParam String plantId,
											   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String manufacturerName,
											   @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin,
											   @RequestParam Long specialStockIndicatorId, @RequestParam String loginUserID) {
		try {
			String currentDB = baseService.getDataBase(plantId);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		inventoryService.deleteInventoryV2(companyCodeId, plantId, languageId, warehouseId, stockTypeId, specialStockIndicatorId, packBarcodes, itemCode, manufacturerName, storageBin, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	/**
	 * For Stock Movement Report
	 * @param searchInventory
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(response = IInventoryImpl.class, value = "Search Inventory V3") // label for swagger
	@PostMapping("/findInventory/v3")
	public List<IInventoryImpl> findInventoryV3(@RequestBody SearchInventoryV2 searchInventory) throws Exception {
		try {
			String currentDB = baseService.getDataBase(searchInventory.getPlantId().get(0));
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		return inventoryService.findInventoryV3(searchInventory);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}

	@ApiOperation(response = WarehouseApiResponse.class, value = "Excess Confirmation") // label for swagger
	@PostMapping("/update/excessConfirmation")
	public ResponseEntity<?> inventoryCreate(@Valid @RequestBody List<ExcessConfirmation> excessConfirmations, @RequestParam String loginUserID) throws Exception {
		try {
			String currentDB = baseService.getDataBase(excessConfirmations.get(0).getPlantId());
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(currentDB);
			log.info("Current DB " + currentDB);
		List<InventoryV2> createdInventory = inventoryService.createInventoryv3(excessConfirmations, loginUserID);
		WarehouseApiResponse response = new WarehouseApiResponse();
		response.setStatusCode("200");
		response.setMessage("Success");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}
