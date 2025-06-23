package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.itemtypeid.FindItemTypeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.PalletizationLevelIdRepository;
import com.tekclover.wms.api.idmaster.repository.PlantIdRepository;
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

import com.tekclover.wms.api.idmaster.model.itemtypeid.AddItemTypeId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.ItemTypeId;
import com.tekclover.wms.api.idmaster.model.itemtypeid.UpdateItemTypeId;
import com.tekclover.wms.api.idmaster.service.ItemTypeIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ItemTypeId"}, value = "ItemTypeId  Operations related to ItemTypeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemTypeId ",description = "Operations related to ItemTypeId ")})
@RequestMapping("/itemtypeid")
@RestController
public class ItemTypeIdController {
	@Autowired
	private PalletizationLevelIdRepository palletizationLevelIdRepository;
	@Autowired
	private PlantIdRepository plantIdRepository;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@Autowired
	ItemTypeIdService itemtypeidService;
	
    @ApiOperation(response = ItemTypeId.class, value = "Get all ItemTypeId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ItemTypeId> itemtypeidList = itemtypeidService.getItemTypeIds();
		return new ResponseEntity<>(itemtypeidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ItemTypeId.class, value = "Get a ItemTypeId") // label for swagger 
	@GetMapping("/{itemTypeId}")
	public ResponseEntity<?> getItemTypeId(@PathVariable Long itemTypeId, 
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemTypeId itemtypeid =
    			itemtypeidService.getItemTypeId(warehouseId, itemTypeId,companyCodeId,plantId,languageId);
    	log.info("ItemTypeId : " + itemtypeid);
		return new ResponseEntity<>(itemtypeid, HttpStatus.OK);
	}
    finally {
			DataBaseContextHolder.clear();
		}
		}
//	@ApiOperation(response = ItemTypeId.class, value = "Search ItemTypeId") // label for swagger
//	@PostMapping("/findItemTypeId")
//	public List<ItemTypeId> findItemTypeId(@RequestBody SearchItemTypeId searchItemTypeId)
//			throws Exception {
//		return itemtypeidService.findItemTypeId(searchItemTypeId);
//	}
    
    @ApiOperation(response = ItemTypeId.class, value = "Create ItemTypeId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postItemTypeId(@Valid @RequestBody AddItemTypeId newItemTypeId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(newItemTypeId.getCompanyCodeId(), newItemTypeId.getPlantId(), newItemTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemTypeId createdItemTypeId = itemtypeidService.createItemTypeId(newItemTypeId, loginUserID);
		return new ResponseEntity<>(createdItemTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemTypeId.class, value = "Update ItemTypeId") // label for swagger
    @PatchMapping("/{itemTypeId}")
	public ResponseEntity<?> patchItemTypeId(@PathVariable Long itemTypeId, 
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateItemTypeId updateItemTypeId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		ItemTypeId createdItemTypeId =
				itemtypeidService.updateItemTypeId(warehouseId, itemTypeId,companyCodeId,plantId,languageId,loginUserID, updateItemTypeId);
		return new ResponseEntity<>(createdItemTypeId , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = ItemTypeId.class, value = "Delete ItemTypeId") // label for swagger
	@DeleteMapping("/{itemTypeId}")
	public ResponseEntity<?> deleteItemTypeId(@PathVariable Long itemTypeId, 
			@RequestParam String warehouseId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String loginUserID) {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		itemtypeidService.deleteItemTypeId(warehouseId, itemTypeId,companyCodeId,plantId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
	//Search
	@ApiOperation(response = ItemTypeId.class, value = "Find ItemTypeId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findItemTypeId(@Valid @RequestBody FindItemTypeId findItemTypeId) throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("IMF");
			String routingDb = dbConfigRepository.getDbName(findItemTypeId.getCompanyCodeId(), findItemTypeId.getPlantId(), findItemTypeId.getWarehouseId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		List<ItemTypeId> createdItemTypeId = itemtypeidService.findItemTypeId(findItemTypeId);
		return new ResponseEntity<>(createdItemTypeId, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}