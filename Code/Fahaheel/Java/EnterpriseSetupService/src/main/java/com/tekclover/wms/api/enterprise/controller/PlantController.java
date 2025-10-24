package com.tekclover.wms.api.enterprise.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import javax.validation.Valid;

import com.tekclover.wms.api.enterprise.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.enterprise.repository.DbConfigRepository;
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

import com.tekclover.wms.api.enterprise.model.plant.AddPlant;
import com.tekclover.wms.api.enterprise.model.plant.Plant;
import com.tekclover.wms.api.enterprise.model.plant.SearchPlant;
import com.tekclover.wms.api.enterprise.model.plant.UpdatePlant;
import com.tekclover.wms.api.enterprise.service.PlantService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"Plant "}, value = "Plant  Operations related to PlantController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Plant ",description = "Operations related to Plant ")})
@RequestMapping("/plant")
@RestController
public class PlantController {
	
	@Autowired
	PlantService plantService;

	@Autowired
	DbConfigRepository dbConfigRepository;

    @ApiOperation(response = Plant.class, value = "Get all Plant details") // label for swagger
	@GetMapping("")
	public ResponseEntity<?> getAll() {
		List<Plant> plantList = plantService.getPlants();
		return new ResponseEntity<>(plantList, HttpStatus.OK); 
	}
    
	@ApiOperation(response = Plant.class, value = "Get a Plant") 
	@GetMapping("/{plantId}")
	public ResponseEntity<?> getPlant(@PathVariable String plantId,@RequestParam String companyId,@RequestParam String languageId) {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName2(companyId,plantId );
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		Plant plant = plantService.getPlant(plantId,companyId,languageId);
	   	log.info("Plant : " + plant);
		return new ResponseEntity<>(plant, HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Plant.class, value = "Search Plant") // label for swagger
	@PostMapping("/findPlant")
	public List<Plant> findPlant(@RequestBody SearchPlant searchPlant)
			throws Exception {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName2(searchPlant.getCompanyId(), searchPlant.getPlantId() );
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		return plantService.findPlant(searchPlant);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Plant.class, value = "Create Plant") // label for swagger
	@PostMapping("")
	public ResponseEntity<?> postPlant(@Valid @RequestBody AddPlant newPlant, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName2(newPlant.getCompanyId(), newPlant.getPlantId());
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		Plant createdPlant = plantService.createPlant(newPlant, loginUserID);
		return new ResponseEntity<>(createdPlant , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Plant.class, value = "Update Plant") // label for swagger
    @PatchMapping("/{plantId}")
	public ResponseEntity<?> patchPlant(@PathVariable String plantId,@RequestParam String companyId,@RequestParam String languageId,
			@Valid @RequestBody UpdatePlant updatePlant, @RequestParam String loginUserID)
			throws IllegalAccessException, InvocationTargetException, ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName2(companyId,plantId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		Plant createdPlant = plantService.updatePlant (plantId, companyId,languageId,updatePlant, loginUserID);
		return new ResponseEntity<>(createdPlant , HttpStatus.OK);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
    
    @ApiOperation(response = Plant.class, value = "Delete Plant") // label for swagger
	@DeleteMapping("/{plantId}")
	public ResponseEntity<?> deletePlant(@PathVariable String plantId,@RequestParam String companyId,@RequestParam String languageId,@RequestParam String loginUserID) throws ParseException {
		try {
			DataBaseContextHolder.setCurrentDb("MT");
			String routingDb = dbConfigRepository.getDbName2(companyId,plantId);
			log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
			DataBaseContextHolder.clear();
			DataBaseContextHolder.setCurrentDb(routingDb);
		plantService.deletePlant(plantId,companyId,languageId,loginUserID);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
		finally {
			DataBaseContextHolder.clear();
		}
		}
}