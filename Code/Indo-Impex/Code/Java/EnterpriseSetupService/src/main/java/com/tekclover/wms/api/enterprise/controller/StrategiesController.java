package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.strategies.AddStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.SearchStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;
import com.tekclover.wms.api.enterprise.service.StrategiesService;
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
@Api(tags = {"Strategies "}, value = "Strategies  Operations related to StrategiesController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Strategies ", description = "Operations related to Strategies ")})
@RequestMapping("/strategies")
@RestController
public class StrategiesController {

    @Autowired
    StrategiesService strategiesService;

    @ApiOperation(response = Strategies.class, value = "Get all Strategies details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Strategies> strategiesList = strategiesService.getStrategiess();
        return new ResponseEntity<>(strategiesList, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Get a Strategies")
    @GetMapping("/strategies")
    public ResponseEntity<?> getStrategies(@RequestParam String warehouseId, @RequestParam String companyId,
                                           @RequestParam String languageId, @RequestParam String plantId) {

        List<Strategies> strategies = strategiesService.getStrategies(companyId, languageId, plantId, warehouseId);
        log.info("Strategies : " + strategies);
        return new ResponseEntity<>(strategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Search Strategies") // label for swagger
    @PostMapping("/findStrategies")
    public List<Strategies> findStrategies(@RequestBody SearchStrategies searchStrategies) {
        return strategiesService.findStrategies(searchStrategies);
    }

    @ApiOperation(response = Strategies.class, value = "Create Strategies") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStrategies(@Valid @RequestBody List<AddStrategies> newStrategies, @RequestParam String loginUserID) {
        List<Strategies> createdStrategies = strategiesService.createStrategies(newStrategies, loginUserID);
        return new ResponseEntity<>(createdStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Update Strategies") // label for swagger
    @PatchMapping("/strategies")
    public ResponseEntity<?> patchStrategies(@RequestParam String warehouseId, @RequestParam String companyId,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @Valid @RequestBody List<AddStrategies> updateStrategies,
                                             @RequestParam String loginUserID) {

        List<Strategies> createdStrategies = strategiesService.updateStrategies(companyId, languageId, plantId, warehouseId, updateStrategies, loginUserID);
        return new ResponseEntity<>(createdStrategies, HttpStatus.OK);
    }

    @ApiOperation(response = Strategies.class, value = "Delete Strategies") // label for swagger
    @DeleteMapping("/strategies")
    public ResponseEntity<?> deleteStrategies(@RequestParam String companyId,
                                              @RequestParam String languageId, @RequestParam String plantId,
                                              @RequestParam String warehouseId, @RequestParam String loginUserID) {

        strategiesService.deleteStrategies(companyId, languageId, plantId, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}