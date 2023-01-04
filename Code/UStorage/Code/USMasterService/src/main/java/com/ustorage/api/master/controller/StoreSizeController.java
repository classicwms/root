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

import com.ustorage.api.master.model.storesize.AddStoreSize;
import com.ustorage.api.master.model.storesize.StoreSize;
import com.ustorage.api.master.model.storesize.UpdateStoreSize;
import com.ustorage.api.master.service.StoreSizeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "StoreSize" }, value = "StoreSize Operations related to StoreSizeController") 
@SwaggerDefinition(tags = { @Tag(name = "StoreSize", description = "Operations related to StoreSize") })
@RequestMapping("/storeSize")
@RestController
public class StoreSizeController {

	@Autowired
	StoreSizeService storeSizeService;

	@ApiOperation(response = StoreSize.class, value = "Get all StoreSize details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<StoreSize> storeSizeList = storeSizeService.getStoreSize();
		return new ResponseEntity<>(storeSizeList, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Get a StoreSize") // label for swagger
	@GetMapping("/{storeSizeId}")
	public ResponseEntity<?> getStoreSize(@PathVariable String storeSizeId) {
		StoreSize dbStoreSize = storeSizeService.getStoreSize(storeSizeId);
		log.info("StoreSize : " + dbStoreSize);
		return new ResponseEntity<>(dbStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Create StoreSize") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postStoreSize(@Valid @RequestBody AddStoreSize newStoreSize,
			@RequestParam String loginUserID) throws Exception {
		StoreSize createdStoreSize = storeSizeService.createStoreSize(newStoreSize, loginUserID);
		return new ResponseEntity<>(createdStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Update StoreSize") // label for swagger
	@PatchMapping("/{storeSizeId}")
	public ResponseEntity<?> patchStoreSize(@PathVariable String storeSizeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateStoreSize updateStoreSize)
			throws IllegalAccessException, InvocationTargetException {
		StoreSize updatedStoreSize = storeSizeService.updateStoreSize(storeSizeId, loginUserID,
				updateStoreSize);
		return new ResponseEntity<>(updatedStoreSize, HttpStatus.OK);
	}

	@ApiOperation(response = StoreSize.class, value = "Delete StoreSize") // label for swagger
	@DeleteMapping("/{storeSizeId}")
	public ResponseEntity<?> deleteStoreSize(@PathVariable String storeSizeId, @RequestParam String loginUserID) {
		storeSizeService.deleteStoreSize(storeSizeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
