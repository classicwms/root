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

import com.ustorage.api.master.model.requirementtype.AddRequirementType;
import com.ustorage.api.master.model.requirementtype.RequirementType;
import com.ustorage.api.master.model.requirementtype.UpdateRequirementType;
import com.ustorage.api.master.service.RequirementTypeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = { "RequirementType" }, value = "RequirementType Operations related to RequirementTypeController") 
@SwaggerDefinition(tags = { @Tag(name = "RequirementType", description = "Operations related to RequirementType") })
@RequestMapping("/requirementType")
@RestController
public class RequirementTypeController {

	@Autowired
	RequirementTypeService requirementTypeService;

	@ApiOperation(response = RequirementType.class, value = "Get all RequirementType details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<RequirementType> requirementTypeList = requirementTypeService.getRequirementType();
		return new ResponseEntity<>(requirementTypeList, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Get a RequirementType") // label for swagger
	@GetMapping("/{requirementTypeId}")
	public ResponseEntity<?> getRequirementType(@PathVariable String requirementTypeId) {
		RequirementType dbRequirementType = requirementTypeService.getRequirementType(requirementTypeId);
		log.info("RequirementType : " + dbRequirementType);
		return new ResponseEntity<>(dbRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Create RequirementType") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postRequirementType(@Valid @RequestBody AddRequirementType newRequirementType,
			@RequestParam String loginUserID) throws Exception {
		RequirementType createdRequirementType = requirementTypeService.createRequirementType(newRequirementType, loginUserID);
		return new ResponseEntity<>(createdRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Update RequirementType") // label for swagger
	@PatchMapping("/{requirementTypeId}")
	public ResponseEntity<?> patchRequirementType(@PathVariable String requirementTypeId,
			@RequestParam String loginUserID,
			@Valid @RequestBody UpdateRequirementType updateRequirementType)
			throws IllegalAccessException, InvocationTargetException {
		RequirementType updatedRequirementType = requirementTypeService.updateRequirementType(requirementTypeId, loginUserID,
				updateRequirementType);
		return new ResponseEntity<>(updatedRequirementType, HttpStatus.OK);
	}

	@ApiOperation(response = RequirementType.class, value = "Delete RequirementType") // label for swagger
	@DeleteMapping("/{requirementTypeId}")
	public ResponseEntity<?> deleteRequirementType(@PathVariable String requirementTypeId, @RequestParam String loginUserID) {
		requirementTypeService.deleteRequirementType(requirementTypeId, loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
