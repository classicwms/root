package com.tekclover.wms.api.transaction.controller;

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
	@PostMapping("/findInventory")
	public List<Inventory> findInventory(@RequestBody SearchInventory searchInventory) 
			throws Exception {
		return inventoryService.findInventory(searchInventory);
	}
    
    @ApiOperation(response = Inventory.class, value = "Create Inventory") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postInventory(@Valid @RequestBody AddInventory newInventory, @RequestParam String loginUserID) 
			throws IllegalAccessException, InvocationTargetException {
		Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
		return new ResponseEntity<>(createdInventory , HttpStatus.OK);
	}
    
    @ApiOperation(response = Inventory.class, value = "Update Inventory") // label for swagger
    @PatchMapping("/{stockTypeId}")
	public ResponseEntity<?> patchInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId, @Valid @RequestBody UpdateInventory updateInventory, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {
		Inventory createdInventory = 
				inventoryService.updateInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, 
						specialStockIndicatorId, updateInventory);
		return new ResponseEntity<>(createdInventory , HttpStatus.OK);
	}
    
    @ApiOperation(response = Inventory.class, value = "Delete Inventory") // label for swagger
	@DeleteMapping("/{stockTypeId}")
	public ResponseEntity<?> deleteInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, 
			@RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin, 
			@RequestParam Long specialStockIndicatorId, @RequestParam String loginUserID) {
    	inventoryService.deleteInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}