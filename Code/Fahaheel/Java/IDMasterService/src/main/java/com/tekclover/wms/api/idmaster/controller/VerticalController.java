package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.model.vertical.FindVertical;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.tekclover.wms.api.idmaster.model.vertical.AddVertical;
import com.tekclover.wms.api.idmaster.model.vertical.UpdateVertical;
import com.tekclover.wms.api.idmaster.model.vertical.Vertical;
import com.tekclover.wms.api.idmaster.service.VerticalService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@CrossOrigin(origins = "*")
@Api(tags = {"Vertical"}, value = "Vertical Operations related to VerticalController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Vertical",description = "Operations related to Vertical")})
@RequestMapping("/vertical")
@RestController
public class VerticalController {

	@Autowired
	VerticalService verticalService;



	@ApiOperation(response = Vertical.class, value = "Get all Vertical details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Vertical> verticalList = verticalService.getCompanies();
		return new ResponseEntity<>(verticalList, HttpStatus.OK);
	}

	@ApiOperation(response = Vertical.class, value = "Get a Vertical") // label for swagger
	@GetMapping("/{verticalId}")
	public ResponseEntity<?> getVertical(@PathVariable Long verticalId, @RequestParam String languageId) {
		Vertical vertical = verticalService.getVertical(verticalId,languageId);
		log.info("Vertical : " + vertical);
		return new ResponseEntity<>(vertical, HttpStatus.OK);
	}

	@ApiOperation(response = Vertical.class, value = "Create Vertical") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> addVertical(@Valid @RequestBody AddVertical newVertical,@RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {

		Vertical createdVertical = verticalService.createVertical(newVertical,loginUserID);
		return new ResponseEntity<>(createdVertical , HttpStatus.OK);
	}

	@ApiOperation(response = Vertical.class, value = "Update Vertical") // label for swagger
	@PatchMapping("/{verticalId}")
	public ResponseEntity<?> patchVertical(@PathVariable Long verticalId,@RequestParam String languageId,@RequestParam String loginUserID,
										   @RequestBody UpdateVertical updateVertical)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		Vertical updatedVertical = verticalService.updateVertical(verticalId,languageId,loginUserID,updateVertical);
		return new ResponseEntity<>(updatedVertical , HttpStatus.OK);
	}

	@ApiOperation(response = Vertical.class, value = "Delete Vertical") // label for swagger
	@DeleteMapping("/{verticalId}")
	public ResponseEntity<?> deleteVertical(@PathVariable Long verticalId,@RequestParam String languageId) {
		verticalService.deleteVertical(verticalId,languageId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = Vertical.class, value = "Find Vertical") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findVertical(@Valid @RequestBody FindVertical findVertical) throws Exception {
		List<Vertical> createdVertical = verticalService.findVertical(findVertical);
		return new ResponseEntity<>(createdVertical, HttpStatus.OK);
	}
}