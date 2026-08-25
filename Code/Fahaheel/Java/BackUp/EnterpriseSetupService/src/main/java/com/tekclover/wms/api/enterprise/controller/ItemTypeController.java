package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.enterprise.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.enterprise.repository.DbConfigRepository;
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

import com.tekclover.wms.api.enterprise.model.itemtype.AddItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.ItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.SearchItemType;
import com.tekclover.wms.api.enterprise.model.itemtype.UpdateItemType;
import com.tekclover.wms.api.enterprise.service.ItemTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ItemType "}, value = "ItemType  Operations related to ItemTypeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemType ",description = "Operations related to ItemType ")})
@RequestMapping("/itemtype")
@RestController
public class ItemTypeController {
	
	@Autowired
	ItemTypeService itemtypeService;

	@Autowired
	DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ItemType.class, value = "Get all ItemType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ItemType> itemtypeList = itemtypeService.getItemTypes();
		return new ResponseEntity<>(itemtypeList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = ItemType.class, value = "Get a ItemType") 
	@GetMapping("/{itemTypeId}")
	public ResponseEntity<?> getItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId,@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemType itemtype = itemtypeService.getItemType(warehouseId, itemTypeId,companyId,languageId,plantId);
	   	log.info("ItemType : " + itemtype);
		return new ResponseEntity<>(itemtype, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemType.class, value = "Search ItemType") // label for swagger
	@PostMapping("/findItemType")
	public List<ItemType> findItemType(@RequestBody SearchItemType searchItemType)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(searchItemType.getCompanyId(), searchItemType.getPlantId(), searchItemType.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return itemtypeService.findItemType(searchItemType);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemType.class, value = "Create ItemType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postItemType(@Valid @RequestBody AddItemType newItemType, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(newItemType.getCompanyId(), newItemType.getPlantId(), newItemType.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemType createdItemType = itemtypeService.createItemType(newItemType, loginUserID);
		return new ResponseEntity<>(createdItemType , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemType.class, value = "Update ItemType") // label for swagger
    @PatchMapping("/{itemTypeId}")
	public ResponseEntity<?> patchItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId,@RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateItemType updateItemType, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemType createdItemType = itemtypeService.updateItemType(warehouseId, itemTypeId,companyId,languageId,plantId,updateItemType, loginUserID);
		return new ResponseEntity<>(createdItemType , HttpStatus.OK);
	}
	finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemType.class, value = "Delete ItemType") // label for swagger
	@DeleteMapping("/{itemTypeId}")
	public ResponseEntity<?> deleteItemType(@PathVariable Long itemTypeId, @RequestParam String warehouseId, @RequestParam String companyId,@RequestParam String plantId,@RequestParam String languageId,
			@RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName(companyId,plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
    	itemtypeService.deleteItemType(warehouseId, itemTypeId,companyId,languageId,plantId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}