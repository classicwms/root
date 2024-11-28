package com.tekclover.wms.api.enterprise.controller;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Plant "}, value = "Plant  Operations related to PlantController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Plant ", description = "Operations related to Plant ")})
@RequestMapping("/plant")
@RestController
public class PlantController {

    @Autowired
    PlantService plantService;

    @ApiOperation(response = Plant.class, value = "Get all Plant details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Plant> plantList = plantService.getPlants();
        return new ResponseEntity<>(plantList, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Get a Plant")
    @GetMapping("/{plantId}")
    public ResponseEntity<?> getPlant(@PathVariable String plantId, @RequestParam String companyId, @RequestParam String languageId) {
        Plant plant = plantService.getPlant(plantId, companyId, languageId);
        log.info("Plant : " + plant);
        return new ResponseEntity<>(plant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Search Plant") // label for swagger
    @PostMapping("/findPlant")
    public List<Plant> findPlant(@RequestBody SearchPlant searchPlant) {
        return plantService.findPlant(searchPlant);
    }

    @ApiOperation(response = Plant.class, value = "Create Plant") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPlant(@Valid @RequestBody AddPlant newPlant, @RequestParam String loginUserID) {
        Plant createdPlant = plantService.createPlant(newPlant, loginUserID);
        return new ResponseEntity<>(createdPlant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Update Plant") // label for swagger
    @PatchMapping("/{plantId}")
    public ResponseEntity<?> patchPlant(@PathVariable String plantId, @RequestParam String companyId, @RequestParam String languageId,
                                        @Valid @RequestBody UpdatePlant updatePlant, @RequestParam String loginUserID) {
        Plant createdPlant = plantService.updatePlant(plantId, companyId, languageId, updatePlant, loginUserID);
        return new ResponseEntity<>(createdPlant, HttpStatus.OK);
    }

    @ApiOperation(response = Plant.class, value = "Delete Plant") // label for swagger
    @DeleteMapping("/{plantId}")
    public ResponseEntity<?> deletePlant(@PathVariable String plantId, @RequestParam String companyId,
                                         @RequestParam String languageId, @RequestParam String loginUserID) {
        plantService.deletePlant(plantId, companyId, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}