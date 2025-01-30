package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;
import com.tekclover.wms.api.idmaster.model.itemgroupid.FindItemGroupId;
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

import com.tekclover.wms.api.idmaster.model.itemgroupid.AddItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.ItemGroupId;
import com.tekclover.wms.api.idmaster.model.itemgroupid.UpdateItemGroupId;
import com.tekclover.wms.api.idmaster.service.ItemGroupIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"ItemGroupId"}, value = "ItemGroupId  Operations related to ItemGroupIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ItemGroupId ",description = "Operations related to ItemGroupId ")})
@RequestMapping("/itemgroupid")
@RestController
public class ItemGroupIdController {

	@Autowired
	ItemGroupIdService itemgroupidService;
	
    @ApiOperation(response = ItemGroupId.class, value = "Get all ItemGroupId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ItemGroupId> itemgroupidList = itemgroupidService.getItemGroupIds();
		return new ResponseEntity<>(itemgroupidList, HttpStatus.OK); 
	}
    
    @ApiOperation(response = ItemGroupId.class, value = "Get a ItemGroupId") // label for swagger 
	@GetMapping("/{itemGroupId}")
	public ResponseEntity<?> getItemGroupId(@PathVariable Long itemGroupId, 
			@RequestParam String warehouseId, @RequestParam Long itemTypeId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId) {
    	ItemGroupId itemgroupid = 
    			itemgroupidService.getItemGroupId(warehouseId, itemTypeId, itemGroupId,companyCodeId,plantId,languageId);
    	log.info("ItemGroupId : " + itemgroupid);
		return new ResponseEntity<>(itemgroupid, HttpStatus.OK);
	}
    
//	@ApiOperation(response = ItemGroupId.class, value = "Search ItemGroupId") // label for swagger
//	@PostMapping("/findItemGroupId")
//	public List<ItemGroupId> findItemGroupId(@RequestBody SearchItemGroupId searchItemGroupId)
//			throws Exception {
//		return itemgroupidService.findItemGroupId(searchItemGroupId);
//	}
    
    @ApiOperation(response = ItemGroupId.class, value = "Create ItemGroupId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postItemGroupId(@Valid @RequestBody AddItemGroupId newItemGroupId, 
			@RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemGroupId createdItemGroupId = itemgroupidService.createItemGroupId(newItemGroupId, loginUserID);
		return new ResponseEntity<>(createdItemGroupId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ItemGroupId.class, value = "Update ItemGroupId") // label for swagger
    @PatchMapping("/{itemGroupId}")
	public ResponseEntity<?> patchItemGroupId(@PathVariable Long itemGroupId, 
			@RequestParam String warehouseId, @RequestParam Long itemTypeId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,
			@Valid @RequestBody UpdateItemGroupId updateItemGroupId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		ItemGroupId createdItemGroupId = 
				itemgroupidService.updateItemGroupId(warehouseId, itemTypeId, itemGroupId,companyCodeId,plantId,languageId,loginUserID, updateItemGroupId);
		return new ResponseEntity<>(createdItemGroupId , HttpStatus.OK);
	}
    
    @ApiOperation(response = ItemGroupId.class, value = "Delete ItemGroupId") // label for swagger
	@DeleteMapping("/{itemGroupId}")
	public ResponseEntity<?> deleteItemGroupId(@PathVariable Long itemGroupId, 
			@RequestParam String warehouseId, @RequestParam Long itemTypeId,@RequestParam String companyCodeId,@RequestParam String plantId,@RequestParam String languageId,@RequestParam String loginUserID) {
    	itemgroupidService.deleteItemGroupId(warehouseId, itemTypeId, itemGroupId, companyCodeId,plantId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = ItemGroupId.class, value = "Find ItemGroupId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findItemGroupId(@Valid @RequestBody FindItemGroupId findItemGroupId) throws Exception {
		List<ItemGroupId> createdItemGroupId = itemgroupidService.findItemGroupId(findItemGroupId);
		return new ResponseEntity<>(createdItemGroupId, HttpStatus.OK);
	}
}