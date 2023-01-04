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

import com.ustorage.api.master.model.bin.AddBin;
import com.ustorage.api.master.model.bin.Bin;
import com.ustorage.api.master.model.bin.UpdateBin;
import com.ustorage.api.master.service.BinService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Bin" }, value = "Bin Operations related to BinController") 
@SwaggerDefinition(tags = { @Tag(name = "Bin", description = "Operations related to Bin") })
@RequestMapping("/bin")
@RestController
public class BinController {

	@Autowired
	BinService binService;

	@ApiOperation(response = Bin.class, value = "Get all Bin details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Bin> binList = binService.getBin();
		return new ResponseEntity<>(binList, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Get a Bin") // label for swagger
	@GetMapping("/{binId}")
	public ResponseEntity<?> getBin(@PathVariable String binId) {
		Bin dbBin = binService.getBin(binId);
		log.info("Bin : " + dbBin);
		return new ResponseEntity<>(dbBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Create Bin") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postBin(@Valid @RequestBody AddBin newBin,
			@RequestParam String loginUserID) throws Exception {
		Bin createdBin = binService.createBin(newBin, loginUserID);
		return new ResponseEntity<>(createdBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Update Bin") // label for swagger
	@PatchMapping("/{binId}")
	public ResponseEntity<?> patchBin(@PathVariable String binId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateBin updateBin)
			throws IllegalAccessException, InvocationTargetException {
		Bin updatedBin = binService.updateBin(binId, loginUserID,
				updateBin);
		return new ResponseEntity<>(updatedBin, HttpStatus.OK);
	}

	@ApiOperation(response = Bin.class, value = "Delete Bin") // label for swagger
	@DeleteMapping("/{binId}")
	public ResponseEntity<?> deleteBin(@PathVariable String binId, @RequestParam String loginUserID) {
		binService.deleteBin(binId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
