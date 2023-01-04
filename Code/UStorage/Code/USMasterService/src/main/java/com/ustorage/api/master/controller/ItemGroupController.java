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

import com.ustorage.api.master.model.itemgroup.AddItemGroup;
import com.ustorage.api.master.model.itemgroup.ItemGroup;
import com.ustorage.api.master.model.itemgroup.UpdateItemGroup;
import com.ustorage.api.master.service.ItemGroupService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "ItemGroup" }, value = "ItemGroup Operations related to ItemGroupController") 
@SwaggerDefinition(tags = { @Tag(name = "ItemGroup", description = "Operations related to ItemGroup") })
@RequestMapping("/itemGroup")
@RestController
public class ItemGroupController {

	@Autowired
	ItemGroupService itemGroupService;

	@ApiOperation(response = ItemGroup.class, value = "Get all ItemGroup details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<ItemGroup> itemGroupList = itemGroupService.getItemGroup();
		return new ResponseEntity<>(itemGroupList, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Get a ItemGroup") // label for swagger
	@GetMapping("/{itemGroupId}")
	public ResponseEntity<?> getItemGroup(@PathVariable String itemGroupId) {
		ItemGroup dbItemGroup = itemGroupService.getItemGroup(itemGroupId);
		log.info("ItemGroup : " + dbItemGroup);
		return new ResponseEntity<>(dbItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Create ItemGroup") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postItemGroup(@Valid @RequestBody AddItemGroup newItemGroup,
			@RequestParam String loginUserID) throws Exception {
		ItemGroup createdItemGroup = itemGroupService.createItemGroup(newItemGroup, loginUserID);
		return new ResponseEntity<>(createdItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Update ItemGroup") // label for swagger
	@PatchMapping("/{itemGroupId}")
	public ResponseEntity<?> patchItemGroup(@PathVariable String itemGroupId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateItemGroup updateItemGroup)
			throws IllegalAccessException, InvocationTargetException {
		ItemGroup updatedItemGroup = itemGroupService.updateItemGroup(itemGroupId, loginUserID,
				updateItemGroup);
		return new ResponseEntity<>(updatedItemGroup, HttpStatus.OK);
	}

	@ApiOperation(response = ItemGroup.class, value = "Delete ItemGroup") // label for swagger
	@DeleteMapping("/{itemGroupId}")
	public ResponseEntity<?> deleteItemGroup(@PathVariable String itemGroupId, @RequestParam String loginUserID) {
		itemGroupService.deleteItemGroup(itemGroupId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
