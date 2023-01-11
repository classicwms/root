package com.ustorage.api.master.controller;

import com.ustorage.api.master.model.documentstorage.*;
import com.ustorage.api.master.service.DocumentStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "DocumentStorage" }, value = "DocumentStorage Operations related to DocumentStorageController") 
@SwaggerDefinition(tags = { @Tag(name = "DocumentStorage", description = "Operations related to DocumentStorage") })
@RequestMapping("/documentStorage")
@RestController
public class DocumentStorageController {

	@Autowired
	DocumentStorageService documentStorageService;

	@ApiOperation(response = DocumentStorage.class, value = "Get a DocumentStorage") // label for swagger
	@GetMapping("/{documentNumber}")
	public ResponseEntity<?> getDocumentStorage(@PathVariable String documentNumber) {
		DocumentStorage dbDocumentStorage = documentStorageService.getDocumentStorage(documentNumber);
		return new ResponseEntity<>(dbDocumentStorage, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStorage.class, value = "Create DocumentStorage") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDocumentStorage(@Valid @RequestBody AddDocumentStorage newDocumentStorage,
			@RequestParam String loginUserID) throws Exception {
		DocumentStorage createdDocumentStorage = documentStorageService.createDocumentStorage(newDocumentStorage, loginUserID);
		return new ResponseEntity<>(createdDocumentStorage, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStorage.class, value = "Delete DocumentStorage") // label for swagger
	@DeleteMapping("/{documentNumber}")
	public ResponseEntity<?> deleteDocumentStorage(@PathVariable String documentNumber, @RequestParam String loginUserID) {
		documentStorageService.deleteDocumentStorage(documentNumber, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = DocumentStorage.class, value = "Find DocumentStorage") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findDocumentStorage(@Valid @RequestBody FindDocumentStorage findDocumentStorage) throws Exception {
		List<DocumentStorage> createdDocumentStorage = documentStorageService.findDocumentStorage(findDocumentStorage);
		return new ResponseEntity<>(createdDocumentStorage, HttpStatus.OK);
	}

}
