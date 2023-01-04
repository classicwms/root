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

import com.ustorage.api.master.model.doortype.AddDoorType;
import com.ustorage.api.master.model.doortype.DoorType;
import com.ustorage.api.master.model.doortype.UpdateDoorType;
import com.ustorage.api.master.service.DoorTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "DoorType" }, value = "DoorType Operations related to DoorTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "DoorType", description = "Operations related to DoorType") })
@RequestMapping("/doorType")
@RestController
public class DoorTypeController {

	@Autowired
	DoorTypeService doorTypeService;

	@ApiOperation(response = DoorType.class, value = "Get all DoorType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DoorType> doorTypeList = doorTypeService.getDoorType();
		return new ResponseEntity<>(doorTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Get a DoorType") // label for swagger
	@GetMapping("/{doorTypeId}")
	public ResponseEntity<?> getDoorType(@PathVariable String doorTypeId) {
		DoorType dbDoorType = doorTypeService.getDoorType(doorTypeId);
		log.info("DoorType : " + dbDoorType);
		return new ResponseEntity<>(dbDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Create DoorType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDoorType(@Valid @RequestBody AddDoorType newDoorType,
			@RequestParam String loginUserID) throws Exception {
		DoorType createdDoorType = doorTypeService.createDoorType(newDoorType, loginUserID);
		return new ResponseEntity<>(createdDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Update DoorType") // label for swagger
	@PatchMapping("/{doorTypeId}")
	public ResponseEntity<?> patchDoorType(@PathVariable String doorTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateDoorType updateDoorType)
			throws IllegalAccessException, InvocationTargetException {
		DoorType updatedDoorType = doorTypeService.updateDoorType(doorTypeId, loginUserID,
				updateDoorType);
		return new ResponseEntity<>(updatedDoorType, HttpStatus.OK);
	}

	@ApiOperation(response = DoorType.class, value = "Delete DoorType") // label for swagger
	@DeleteMapping("/{doorTypeId}")
	public ResponseEntity<?> deleteDoorType(@PathVariable String doorTypeId, @RequestParam String loginUserID) {
		doorTypeService.deleteDoorType(doorTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
