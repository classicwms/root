package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.AddCycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.CycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.SearchCycleCountScheduler;
import com.tekclover.wms.api.masters.model.cyclecountscheduler.UpdateCycleCountScheduler;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.CycleCountSchedulerService;
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
@Api(tags = {"CycleCountScheduler"}, value = "CycleCountScheduler  Operations related to CycleCountSchedulerController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CycleCountScheduler ",description = "Operations related to CycleCountScheduler ")})
@RequestMapping("/cyclecountscheduler")
@RestController
public class CycleCountSchedulerController {

    @Autowired
    private CycleCountSchedulerService cycleCountSchedulerService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = CycleCountScheduler.class, value = "Get all CycleCountScheduler details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<CycleCountScheduler> cycleCountSchedulerList = cycleCountSchedulerService.getAllCycleCountScheduler();
        return new ResponseEntity<>(cycleCountSchedulerList, HttpStatus.OK);
    }

    @ApiOperation(response = CycleCountScheduler.class, value = "Get a CycleCountScheduler") // label for swagger
    @GetMapping("/{cycleCountTypeId}")
    public ResponseEntity<?> getCycleCountScheduler(@PathVariable Long cycleCountTypeId, @RequestParam String companyCodeId, @RequestParam String languageId,
                                                    @RequestParam String plantId, @RequestParam Long levelId,@RequestParam String schedulerNumber,
                                                    @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        CycleCountScheduler dbCycleCountScheduler = cycleCountSchedulerService.getCycleCountScheduler(companyCodeId,languageId,warehouseId,plantId,levelId,cycleCountTypeId,schedulerNumber);
        log.info("CycleCountScheduler : " + dbCycleCountScheduler);
        return new ResponseEntity<>(dbCycleCountScheduler, HttpStatus.OK);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = CycleCountScheduler.class, value = "Create CycleCountScheduler") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postCycleCountScheduler(@Valid @RequestBody AddCycleCountScheduler addCycleCountScheduler, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(addCycleCountScheduler.getCompanyCodeId(), addCycleCountScheduler.getPlantId(), addCycleCountScheduler.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        CycleCountScheduler createdCycleCountScheduler= cycleCountSchedulerService.createCycleCountScheduler(addCycleCountScheduler, loginUserID);
        return new ResponseEntity<>(createdCycleCountScheduler , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = CycleCountScheduler.class, value = "Update CycleCountScheduler") // label for swagger
    @PatchMapping("/{cycleCountTypeId}")
    public ResponseEntity<?> patchCycleCountScheduler(@PathVariable Long cycleCountTypeId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam Long levelId, @RequestParam String warehouseId,@RequestParam String schedulerNumber,
                                                      @Valid @RequestBody UpdateCycleCountScheduler updateCycleCountScheduler, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        CycleCountScheduler createdCycleCountScheduler = cycleCountSchedulerService.updateCycleCountScheduler(companyCodeId,languageId,plantId,warehouseId,levelId,cycleCountTypeId,schedulerNumber, updateCycleCountScheduler, loginUserID);
        return new ResponseEntity<>(createdCycleCountScheduler , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = CycleCountScheduler.class, value = "Delete CycleCountScheduler") // label for swagger
    @DeleteMapping("/{cycleCountTypeId}")
    public ResponseEntity<?> deleteCycleCountScheduler(@PathVariable Long cycleCountTypeId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,@RequestParam String schedulerNumber,@RequestParam Long levelId, @RequestParam String warehouseId,@RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        cycleCountSchedulerService.deleteCycleCountScheduler(companyCodeId,languageId,plantId,warehouseId,levelId,cycleCountTypeId,schedulerNumber,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = CycleCountScheduler.class, value = "Find CycleCountScheduler") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findCycleCountScheduler(@Valid @RequestBody SearchCycleCountScheduler searchCycleCountScheduler) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("IMF");
            String routingDb = dbConfigRepository.getDbNameList(searchCycleCountScheduler.getCompanyCodeId(), searchCycleCountScheduler.getPlantId(), searchCycleCountScheduler.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<CycleCountScheduler> createdCycleCountScheduler = cycleCountSchedulerService.findCycleCountScheduler(searchCycleCountScheduler);
        return new ResponseEntity<>(createdCycleCountScheduler, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
