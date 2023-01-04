package com.ustorage.api.master.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ustorage.api.master.model.itemtype.AddItemType;
import com.ustorage.api.master.model.itemtype.ItemType;
import com.ustorage.api.master.model.itemtype.UpdateItemType;
import com.ustorage.api.master.service.ItemTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "ItemType" }, value = "ItemType Operations related to ItemTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "ItemType", description = "Operations related to ItemType") })
@RequestMapping("/itemType")
@RestController
public class ItemTypeController {

	@Autowired
	ItemTypeService itemTypeService;

	@ApiOperation(response = ItemType.class, value = "Get all ItemType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ItemType> itemTypeList = itemTypeService.getItemType();
		return new ResponseEntity<>(itemTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Get a ItemType") // label for swagger
	@GetMapping("/{itemTypeId}")
	public ResponseEntity<?> getItemType(@PathVariable String itemTypeId) {
		ItemType dbItemType = itemTypeService.getItemType(itemTypeId);
		log.info("ItemType : " + dbItemType);
		return new ResponseEntity<>(dbItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Create ItemType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postItemType(@Valid @RequestBody AddItemType newItemType,
			@RequestParam String loginUserID) throws Exception {
		ItemType createdItemType = itemTypeService.createItemType(newItemType, loginUserID);
		return new ResponseEntity<>(createdItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Update ItemType") // label for swagger
	@PatchMapping("/{itemTypeId}")
	public ResponseEntity<?> patchItemType(@PathVariable String itemTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateItemType updateItemType)
			throws IllegalAccessException, InvocationTargetException {
		ItemType updatedItemType = itemTypeService.updateItemType(itemTypeId, loginUserID,
				updateItemType);
		return new ResponseEntity<>(updatedItemType, HttpStatus.OK);
	}

	@ApiOperation(response = ItemType.class, value = "Delete ItemType") // label for swagger
	@DeleteMapping("/{itemTypeId}")
	public ResponseEntity<?> deleteItemType(@PathVariable String itemTypeId, @RequestParam String loginUserID) {
		itemTypeService.deleteItemType(itemTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
