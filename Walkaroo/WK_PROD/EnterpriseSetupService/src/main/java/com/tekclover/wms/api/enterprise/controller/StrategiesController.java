package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.enterprise.model.strategies.AddStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.SearchStrategies;
import com.tekclover.wms.api.enterprise.model.strategies.Strategies;
import com.tekclover.wms.api.enterprise.repository.DbConfigRepository;
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
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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

    @Autowired
    DbConfigRepository dbConfigRepository;

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
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<Strategies> strategies = strategiesService.getStrategies(companyId, languageId, plantId, warehouseId);
            log.info("Strategies : " + strategies);
            return new ResponseEntity<>(strategies, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Strategies.class, value = "Search Strategies") // label for swagger
    @PostMapping("/findStrategies")
    public List<Strategies> findStrategies(@RequestBody SearchStrategies searchStrategies)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(searchStrategies.getCompanyId(), searchStrategies.getPlantId(), searchStrategies.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return strategiesService.findStrategies(searchStrategies);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Strategies.class, value = "Create Strategies") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStrategies(@Valid @RequestBody List<AddStrategies> newStrategies, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (AddStrategies addStrategies : newStrategies) {
                DataBaseContextHolder.setCurrentDb("WK");
                String routingDb = dbConfigRepository.getDbName(addStrategies.getCompanyId(), addStrategies.getPlantId(), addStrategies.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<Strategies> createdStrategies = strategiesService.createStrategies(newStrategies, loginUserID);
            return new ResponseEntity<>(createdStrategies, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Strategies.class, value = "Update Strategies") // label for swagger
    @PatchMapping("/strategies")
    public ResponseEntity<?> patchStrategies(@RequestParam String warehouseId, @RequestParam String companyId,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @Valid @RequestBody List<AddStrategies> updateStrategies,
                                             @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<Strategies> createdStrategies = strategiesService.updateStrategies(companyId, languageId, plantId, warehouseId, updateStrategies, loginUserID);
            return new ResponseEntity<>(createdStrategies, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Strategies.class, value = "Delete Strategies") // label for swagger
    @DeleteMapping("/strategies")
    public ResponseEntity<?> deleteStrategies(@RequestParam String companyId,
                                              @RequestParam String languageId, @RequestParam String plantId,
                                              @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            strategiesService.deleteStrategies(companyId, languageId, plantId, warehouseId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}