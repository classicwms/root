package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.driver.AddDriver;
import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import com.tekclover.wms.api.masters.model.driver.UpdateDriver;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.DriverService;
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
@Api(tags = {"Driver"}, value = "Driver  Operations related to DriverController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Driver ",description = "Operations related to Driver ")})
@RequestMapping("/driver")
@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = Driver.class, value = "Get a Driver") // label for swagger
    @GetMapping("/{driverId}")
    public ResponseEntity<?> getDriver(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                       @RequestParam String languageId,@RequestParam String plantId,
                                       @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

        Driver dbDriver = driverService.getDriver(driverId,companyCodeId,plantId,languageId,warehouseId);
        log.info("driver : " + dbDriver);
        return new ResponseEntity<>(dbDriver, HttpStatus.OK);
    }

finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = Driver.class, value = "Create Driver") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDriver(@Valid @RequestBody AddDriver newDriver, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newDriver.getCompanyCodeId(), newDriver.getPlantId(), newDriver.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        Driver createdDriver= driverService.createDriver(newDriver, loginUserID);
        return new ResponseEntity<>(createdDriver , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = Driver.class, value = "Update Driver") // label for swagger
    @PatchMapping("/{driverId}")
    public ResponseEntity<?> patchDock(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                       @RequestParam String languageId, @RequestParam String plantId,
                                       @RequestParam String warehouseId,@Valid @RequestBody UpdateDriver updateDriver,
                                       @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

        Driver createDriver = driverService.updateDriver(companyCodeId,plantId,warehouseId,
                languageId,driverId,updateDriver,loginUserID);
        return new ResponseEntity<>(createDriver , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = Driver.class, value = "Delete Driver") // label for swagger
    @DeleteMapping("/{driverId}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                          @RequestParam String languageId, @RequestParam String plantId,
                                          @RequestParam String warehouseId,@RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        driverService.deleteDriver(companyCodeId,languageId,plantId,warehouseId,driverId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = Driver.class, value = "Find Driver") // label for swagger
    @PostMapping("/findDriver")
    public ResponseEntity<?> findDriver(@Valid @RequestBody SearchDriver searchDriver) throws Exception {

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchDriver.getCompanyCodeId(), searchDriver.getPlantId(), searchDriver.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);List<Driver> createDriver = driverService.findDriver(searchDriver);
        return new ResponseEntity<>(createDriver, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
