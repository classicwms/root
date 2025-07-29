package com.tekclover.wms.api.idmaster.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.plantid.FindPlantId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.repository.LanguageIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.idmaster.model.plantid.AddPlantId;
import com.tekclover.wms.api.idmaster.model.plantid.PlantId;
import com.tekclover.wms.api.idmaster.model.plantid.UpdatePlantId;
import com.tekclover.wms.api.idmaster.service.PlantIdService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PlantId"}, value = "PlantId  Operations related to PlantIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PlantId ",description = "Operations related to PlantId ")})
@RequestMapping("/plantid")
@RestController
public class PlantIdController {
	@Autowired
	private LanguageIdRepository languageIdRepository;

	@Autowired
	PlantIdService plantidService;

	@Autowired
	DbConfigRepository dbConfigRepository;

	@ApiOperation(response = PlantId.class, value = "Get all PlantId details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<PlantId> plantidList = plantidService.getPlantIds();
		return new ResponseEntity<>(plantidList, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Get a PlantId") // label for swagger
	@GetMapping("/{plantId}")
	public ResponseEntity<?> getPlantId(@PathVariable String plantId,@RequestParam String companyCodeId,@RequestParam String languageId) {

		PlantId plantid = plantidService.getPlantId(plantId,companyCodeId,languageId);
		log.info("PlantId : " + plantid);
		return new ResponseEntity<>(plantid, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Get a PlantId") // label for swagger
	@GetMapping("/branchCode")
	public ResponseEntity<?> getPlantIdForAlmailem(@RequestParam String companyCodeId,@RequestParam String languageId) {
		DataBaseContextHolder.setCurrentDb("MT");
		String routingDb = dbConfigRepository.getDbNameByCompany(companyCodeId);
		log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
		DataBaseContextHolder.clear();
		DataBaseContextHolder.setCurrentDb(routingDb);
		List<PlantId> plantid = plantidService.getPlantId(companyCodeId,languageId);
		log.info("PlantId : " + plantid);
		return new ResponseEntity<>(plantid, HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Create PlantId") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPlantId(@Valid @RequestBody AddPlantId newPlantId,
										 @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {

		PlantId createdPlantId = plantidService.createPlantId(newPlantId, loginUserID);
		return new ResponseEntity<>(createdPlantId , HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Update PlantId") // label for swagger
	@PatchMapping("/{plantId}")
	public ResponseEntity<?> patchPlantId(@PathVariable String plantId,@RequestParam String companyCodeId,@RequestParam String languageId,@Valid @RequestBody UpdatePlantId updatePlantId, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		PlantId createdPlantId = plantidService.updatePlantId(plantId,companyCodeId,languageId,loginUserID, updatePlantId);
		return new ResponseEntity<>(createdPlantId , HttpStatus.OK);
	}

	@ApiOperation(response = PlantId.class, value = "Delete PlantId") // label for swagger
	@DeleteMapping("/{plantId}")
	public ResponseEntity<?> deletePlantId(@PathVariable String plantId, @RequestParam String companyCodeId,@RequestParam String languageId,
										   @RequestParam String loginUserID) {
		plantidService.deletePlantId(plantId,companyCodeId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	//Search
	@ApiOperation(response = PlantId.class, value = "Find PlantId") // label for swagger
	@PostMapping("/find")
	public ResponseEntity<?> findPlantId(@Valid @RequestBody FindPlantId findPlantId) throws Exception {
		List<PlantId> createdPlant = plantidService.findPlant(findPlantId);
		return new ResponseEntity<>(createdPlant, HttpStatus.OK);
	}
}