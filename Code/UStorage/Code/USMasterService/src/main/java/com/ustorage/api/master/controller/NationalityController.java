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

import com.ustorage.api.master.model.nationality.AddNationality;
import com.ustorage.api.master.model.nationality.Nationality;
import com.ustorage.api.master.model.nationality.UpdateNationality;
import com.ustorage.api.master.service.NationalityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Nationality" }, value = "Nationality Operations related to NationalityController") 
@SwaggerDefinition(tags = { @Tag(name = "Nationality", description = "Operations related to Nationality") })
@RequestMapping("/nationality")
@RestController
public class NationalityController {

	@Autowired
	NationalityService nationalityService;

	@ApiOperation(response = Nationality.class, value = "Get all Nationality details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Nationality> nationalityList = nationalityService.getNationality();
		return new ResponseEntity<>(nationalityList, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Get a Nationality") // label for swagger
	@GetMapping("/{nationalityId}")
	public ResponseEntity<?> getNationality(@PathVariable String nationalityId) {
		Nationality dbNationality = nationalityService.getNationality(nationalityId);
		log.info("Nationality : " + dbNationality);
		return new ResponseEntity<>(dbNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Create Nationality") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postNationality(@Valid @RequestBody AddNationality newNationality,
			@RequestParam String loginUserID) throws Exception {
		Nationality createdNationality = nationalityService.createNationality(newNationality, loginUserID);
		return new ResponseEntity<>(createdNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Update Nationality") // label for swagger
	@PatchMapping("/{nationalityId}")
	public ResponseEntity<?> patchNationality(@PathVariable String nationalityId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateNationality updateNationality)
			throws IllegalAccessException, InvocationTargetException {
		Nationality updatedNationality = nationalityService.updateNationality(nationalityId, loginUserID,
				updateNationality);
		return new ResponseEntity<>(updatedNationality, HttpStatus.OK);
	}

	@ApiOperation(response = Nationality.class, value = "Delete Nationality") // label for swagger
	@DeleteMapping("/{nationalityId}")
	public ResponseEntity<?> deleteNationality(@PathVariable String nationalityId, @RequestParam String loginUserID) {
		nationalityService.deleteNationality(nationalityId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
