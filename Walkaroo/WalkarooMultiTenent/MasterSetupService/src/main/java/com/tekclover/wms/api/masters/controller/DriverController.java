package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.driver.AddDriver;
import com.tekclover.wms.api.masters.model.driver.Driver;
import com.tekclover.wms.api.masters.model.driver.SearchDriver;
import com.tekclover.wms.api.masters.model.driver.UpdateDriver;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
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
@SwaggerDefinition(tags = {@Tag(name = "Driver ", description = "Operations related to Driver ")})
@RequestMapping("/driver")
@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Driver.class, value = "Get all Driver details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Driver> driverList = driverService.getAllDriver();
        return new ResponseEntity<>(driverList, HttpStatus.OK);
    }

    @ApiOperation(response = Driver.class, value = "Get a Driver") // label for swagger
    @GetMapping("/{driverId}")
    public ResponseEntity<?> getDriver(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                       @RequestParam String languageId, @RequestParam String plantId,
                                       @RequestParam String warehouseId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Driver dbDriver = driverService.getDriver(driverId, companyCodeId, plantId, languageId, warehouseId);
            log.info("driver : " + dbDriver);
            return new ResponseEntity<>(dbDriver, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = Driver.class, value = "Create Driver") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDriver(@Valid @RequestBody AddDriver newDriver, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newDriver.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Driver createdDriver = driverService.createDriver(newDriver, loginUserID);
            return new ResponseEntity<>(createdDriver, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Driver.class, value = "Update Driver") // label for swagger
    @PatchMapping("/{driverId}")
    public ResponseEntity<?> patchDock(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                       @RequestParam String languageId, @RequestParam String plantId,
                                       @RequestParam String warehouseId, @Valid @RequestBody UpdateDriver updateDriver,
                                       @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Driver createDriver = driverService.updateDriver(companyCodeId, plantId, warehouseId,
                    languageId, driverId, updateDriver, loginUserID);
            return new ResponseEntity<>(createDriver, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Driver.class, value = "Delete Driver") // label for swagger
    @DeleteMapping("/{driverId}")
    public ResponseEntity<?> deleteDriver(@PathVariable Long driverId, @RequestParam String companyCodeId,
                                          @RequestParam String languageId, @RequestParam String plantId,
                                          @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            driverService.deleteDriver(companyCodeId, languageId, plantId, warehouseId, driverId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Driver.class, value = "Find Driver") // label for swagger
    @PostMapping("/findDriver")
    public ResponseEntity<?> findDriver(@Valid @RequestBody SearchDriver searchDriver) throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchDriver.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Driver> createDriver = driverService.findDriver(searchDriver);
            return new ResponseEntity<>(createDriver, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
