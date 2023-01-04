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

import com.ustorage.api.master.model.documentstatus.AddDocumentStatus;
import com.ustorage.api.master.model.documentstatus.DocumentStatus;
import com.ustorage.api.master.model.documentstatus.UpdateDocumentStatus;
import com.ustorage.api.master.service.DocumentStatusService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "DocumentStatus" }, value = "DocumentStatus Operations related to DocumentStatusController") 
@SwaggerDefinition(tags = { @Tag(name = "DocumentStatus", description = "Operations related to DocumentStatus") })
@RequestMapping("/documentStatus")
@RestController
public class DocumentStatusController {

	@Autowired
	DocumentStatusService documentStatusService;

	@ApiOperation(response = DocumentStatus.class, value = "Get all DocumentStatus details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<DocumentStatus> documentStatusList = documentStatusService.getDocumentStatus();
		return new ResponseEntity<>(documentStatusList, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Get a DocumentStatus") // label for swagger
	@GetMapping("/{documentStatusId}")
	public ResponseEntity<?> getDocumentStatus(@PathVariable String documentStatusId) {
		DocumentStatus dbDocumentStatus = documentStatusService.getDocumentStatus(documentStatusId);
		log.info("DocumentStatus : " + dbDocumentStatus);
		return new ResponseEntity<>(dbDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Create DocumentStatus") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postDocumentStatus(@Valid @RequestBody AddDocumentStatus newDocumentStatus,
			@RequestParam String loginUserID) throws Exception {
		DocumentStatus createdDocumentStatus = documentStatusService.createDocumentStatus(newDocumentStatus, loginUserID);
		return new ResponseEntity<>(createdDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Update DocumentStatus") // label for swagger
	@PatchMapping("/{documentStatusId}")
	public ResponseEntity<?> patchDocumentStatus(@PathVariable String documentStatusId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateDocumentStatus updateDocumentStatus)
			throws IllegalAccessException, InvocationTargetException {
		DocumentStatus updatedDocumentStatus = documentStatusService.updateDocumentStatus(documentStatusId, loginUserID,
				updateDocumentStatus);
		return new ResponseEntity<>(updatedDocumentStatus, HttpStatus.OK);
	}

	@ApiOperation(response = DocumentStatus.class, value = "Delete DocumentStatus") // label for swagger
	@DeleteMapping("/{documentStatusId}")
	public ResponseEntity<?> deleteDocumentStatus(@PathVariable String documentStatusId, @RequestParam String loginUserID) {
		documentStatusService.deleteDocumentStatus(documentStatusId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
