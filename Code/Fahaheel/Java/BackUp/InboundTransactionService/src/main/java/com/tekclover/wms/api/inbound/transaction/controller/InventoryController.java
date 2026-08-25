package com.tekclover.wms.api.inbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.impl.InventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.SearchInventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.WarehouseRepository;
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

import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.SearchInventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.inbound.transaction.service.InventoryService;

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
	WarehouseRepository warehouseRepository;
	
//    @ApiOperation(response = Inventory.class, value = "Get all Inventory details") // label for swagger
//	@GetMapping("")
//	public ResponseEntity<?> getAll() {
//		List<Inventory> inventoryList = inventoryService.getInventorys();
//		return new ResponseEntity<>(inventoryList, HttpStatus.OK);
//	}
//
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
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newInventory.getCompanyCodeId(), newInventory.getPlantId(), newInventory.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
			return new ResponseEntity<>(createdInventory, HttpStatus.OK);
		} finally {
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
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(updateInventory.getCompanyCodeId(), updateInventory.getPlantId(), warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);

			Inventory createdInventory =
					inventoryService.updateInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId,
							specialStockIndicatorId, updateInventory, loginUserID);
			return new ResponseEntity<>(createdInventory, HttpStatus.OK);
		} finally {
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
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName1(searchInventory.getCompanyCodeId(), searchInventory.getPlantId(), searchInventory.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			return inventoryService.findInventoryV2(searchInventory);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
	@ApiOperation(response = IInventoryImpl.class, value = "Search Inventory V2") // label for swagger
	@PostMapping("/findInventoryNew/v2")
	public List<IInventoryImpl> findInventoryNewV2(@RequestBody SearchInventoryV2 searchInventory)
			throws Exception {
		try {
			log.info("SearchInventoryV2 ------> {}", searchInventory);
			String routingDb = null;
			DataBaseContextHolder.setCurrentDb("MT");
			Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchInventory.getWarehouseId().get(0), 0L);
			routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			return inventoryService.findInventoryV4(searchInventory);
		} finally {
				DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = InventoryV2.class, value = "Create Inventory V2") // label for swagger
	@PostMapping("/v2")
	public ResponseEntity<?> postInventoryV2(@Valid @RequestBody InventoryV2 newInventory, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newInventory.getCompanyCodeId(), newInventory.getPlantId(), newInventory.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			InventoryV2 createdInventory = inventoryService.createInventoryV2(newInventory, loginUserID);
			return new ResponseEntity<>(createdInventory, HttpStatus.OK);
		} finally {
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
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			InventoryV2 updatedInventory =
					inventoryService.updateInventoryV2(companyCodeId, plantId, languageId, warehouseId,
							packBarcodes, itemCode, manufacturerName, storageBin, stockTypeId,
							specialStockIndicatorId, updateInventory, loginUserID);
			return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
		} finally {
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
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			inventoryService.deleteInventoryV2(companyCodeId, plantId, languageId, warehouseId, stockTypeId, specialStockIndicatorId, packBarcodes, itemCode, manufacturerName, storageBin, loginUserID);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
	@ApiOperation(response = Inventory.class, value = "Update Upload Inventory") // label for swagger
	@PostMapping("/update/upload")
	public ResponseEntity<?> postInventoryUpload(@RequestBody List<UpdateInventory> updateInventory,@RequestParam String companyCodeId, @RequestParam String plantId,
												 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<Inventory> createdInventory = inventoryService.updateInventoryUpload(updateInventory,companyCodeId, plantId, languageId, warehouseId, loginUserID);
			return new ResponseEntity<>(createdInventory, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}

	@ApiOperation(response = InventoryV2.class, value = "Upload Inventory") // label for swagger
	@PostMapping("/upload/inventory")
	public ResponseEntity<?> uploadInventory(@Valid @RequestBody List<InventoryV2> inventoryV2){
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(inventoryV2.get(0).getCompanyCodeId(), inventoryV2.get(0).getPlantId(), inventoryV2.get(0).getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
			List<InventoryV2> inventory = inventoryService.inventoryUpload(inventoryV2);
			return new ResponseEntity<>(inventory, HttpStatus.OK);
		} finally {
			DataBaseContextHolder.clear();
		}
	}
}