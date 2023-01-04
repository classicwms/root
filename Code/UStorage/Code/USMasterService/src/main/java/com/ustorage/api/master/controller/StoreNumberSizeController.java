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

import com.ustorage.api.master.model.storenumbersize.AddStoreNumberSize;
import com.ustorage.api.master.model.storenumbersize.StoreNumberSize;
import com.ustorage.api.master.model.storenumbersize.UpdateStoreNumberSize;
import com.ustorage.api.master.service.StoreNumberSizeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "StoreNumberSize" }, value = "StoreNumberSize Operations related to StoreNumberSizeController") 
@SwaggerDefinition(tags = { @Tag(name = "StoreNumberSize", description = "Operations related to StoreNumberSize") })
@RequestMapping("/storeNumberSize")
@RestController
public class StoreNumberSizeController {

	@Autowired
	StoreNumberSizeService storeNumberSizeService;

	@ApiOperation(response = StoreNumberSize.class, value = "Get all StoreNumberSize details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StoreNumberSize> storeNumberSizeList = storeNumberSizeService.getStoreNumberSize();
		return new ResponseEntity<>(storeNumberSizeList, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Get a StoreNumberSize") // label for swagger
	@GetMapping("/{storeNumberSizeId}")
	public ResponseEntity<?> getStoreNumberSize(@PathVariable String storeNumberSizeId) {
		StoreNumberSize dbStoreNumberSize = storeNumberSizeService.getStoreNumberSize(storeNumberSizeId);
		log.info("StoreNumberSize : " + dbStoreNumberSize);
		return new ResponseEntity<>(dbStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Create StoreNumberSize") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStoreNumberSize(@Valid @RequestBody AddStoreNumberSize newStoreNumberSize,
			@RequestParam String loginUserID) throws Exception {
		StoreNumberSize createdStoreNumberSize = storeNumberSizeService.createStoreNumberSize(newStoreNumberSize, loginUserID);
		return new ResponseEntity<>(createdStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Update StoreNumberSize") // label for swagger
	@PatchMapping("/{storeNumberSizeId}")
	public ResponseEntity<?> patchStoreNumberSize(@PathVariable String storeNumberSizeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateStoreNumberSize updateStoreNumberSize)
			throws IllegalAccessException, InvocationTargetException {
		StoreNumberSize updatedStoreNumberSize = storeNumberSizeService.updateStoreNumberSize(storeNumberSizeId, loginUserID,
				updateStoreNumberSize);
		return new ResponseEntity<>(updatedStoreNumberSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreNumberSize.class, value = "Delete StoreNumberSize") // label for swagger
	@DeleteMapping("/{storeNumberSizeId}")
	public ResponseEntity<?> deleteStoreNumberSize(@PathVariable String storeNumberSizeId, @RequestParam String loginUserID) {
		storeNumberSizeService.deleteStoreNumberSize(storeNumberSizeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
