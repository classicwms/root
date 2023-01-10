package com.ustorage.api.trans.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.validation.Valid;

import com.ustorage.api.trans.model.storenumber.FindStoreNumber;
import com.ustorage.api.trans.service.StoreNumberService;
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

import com.ustorage.api.trans.model.agreement.*;

import com.ustorage.api.trans.service.AgreementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "Agreement" }, value = "Agreement Operations related to AgreementController") 
@SwaggerDefinition(tags = { @Tag(name = "Agreement", description = "Operations related to Agreement") })
@RequestMapping("/agreement")
@RestController
public class AgreementController {

	@Autowired
	AgreementService agreementService;

	@Autowired
	StoreNumberService storeNumberService;

	@ApiOperation(response = Agreement.class, value = "Get all Agreement details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Agreement> agreementList = agreementService.getAgreement();
		return new ResponseEntity<>(agreementList, HttpStatus.OK);
	}

	@ApiOperation(response = GAgreement.class, value = "Get a Agreement") // label for swagger
	@GetMapping("/{agreementNumber}")
	public ResponseEntity<?> getAgreement(@PathVariable String agreementNumber) {
		GAgreement dbAgreement = agreementService.getAgreement(agreementNumber);
		log.info("Agreement : " + dbAgreement);
		return new ResponseEntity<>(dbAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Create Agreement") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postAgreement(@Valid @RequestBody AddAgreement newAgreement,
			@RequestParam String loginUserID) throws Exception {
		Agreement createdAgreement = agreementService.createAgreement(newAgreement, loginUserID);
		return new ResponseEntity<>(createdAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Update Agreement") // label for swagger
	@PatchMapping("/{agreement}")
	public ResponseEntity<?> patchAgreement(@PathVariable String agreement,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateAgreement updateAgreement)
			throws IllegalAccessException, InvocationTargetException {
		Agreement updatedAgreement = agreementService.updateAgreement(agreement, loginUserID,
				updateAgreement);
		return new ResponseEntity<>(updatedAgreement, HttpStatus.OK);
	}

	@ApiOperation(response = Agreement.class, value = "Delete Agreement") // label for swagger
	@DeleteMapping("/{agreement}")
	public ResponseEntity<?> deleteAgreement(@PathVariable String agreement, @RequestParam String loginUserID) {
		agreementService.deleteAgreement(agreement, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = GAgreement.class, value = "Find Agreement") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findAgreement(@Valid @RequestBody FindAgreement findAgreement) throws Exception {
		List<GAgreement> createdAgreement = agreementService.findAgreement(findAgreement);
		return new ResponseEntity<>(createdAgreement, HttpStatus.OK);
	}
	//Search
	@ApiOperation(response = GAgreement.class, value = "Find StoreNumber") // label for swagger
	@PostMapping("/findStoreNumber")
	public ResponseEntity<?> findStoreNumber(@Valid @RequestBody FindStoreNumber findStoreNumber) throws Exception {
		List<GAgreement> dbAgreement = storeNumberService.findStoreNumber(findStoreNumber);
		return new ResponseEntity<>(dbAgreement, HttpStatus.OK);
	}
}
