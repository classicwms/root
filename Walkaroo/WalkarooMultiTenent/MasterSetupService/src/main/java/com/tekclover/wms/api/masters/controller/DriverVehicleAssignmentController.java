package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.AddDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.DriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.SearchDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.UpdateDriverVehicleAssignment;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.DriverVehicleAssignmentService;
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
@Api(tags = {"DriverVehicleAssignment"}, value = "DriverVehicleAssignment  Operations related to DriverVehicleAssignmentController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DriverVehicleAssignment ", description = "Operations related to DriverVehicleAssignment ")})
@RequestMapping("/drivervehicleassignment")
@RestController
public class DriverVehicleAssignmentController {

    @Autowired
    private DriverVehicleAssignmentService driverVehicleAssignmentService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    // GET ALL
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Get all DriverVehicleAssignment details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<DriverVehicleAssignment> driverVehicleAssignmentList =
                driverVehicleAssignmentService.getAllDriverVehicleAssignment();
        return new ResponseEntity<>(driverVehicleAssignmentList, HttpStatus.OK);
    }

    // GET
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Get a DriverVehicleAssignment")
    // label for swagger
    @GetMapping("/{driverId}")
    public ResponseEntity<?> getDriver(@PathVariable Long driverId, @RequestParam Long routeId, @RequestParam String vehicleNumber,
                                       @RequestParam String companyCodeId, @RequestParam String languageId,
                                       @RequestParam String plantId, @RequestParam String warehouseId) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            DriverVehicleAssignment driverVehicleAssignment =
                    driverVehicleAssignmentService.getDriverVehicleAssignment(driverId, vehicleNumber, routeId,
                            companyCodeId, plantId, languageId, warehouseId);
            return new ResponseEntity<>(driverVehicleAssignment, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // CREATE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Create DriverVehicleAssignment")
    // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDriverVehicleAssignment(@Valid @RequestBody AddDriverVehicleAssignment newDriverVehicleAssignment,
                                                         @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        try {
            String currentDB = baseService.getDataBase(newDriverVehicleAssignment.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            DriverVehicleAssignment createDriverVehicleAssignment =
                    driverVehicleAssignmentService.createDriverVehicleAssignment(newDriverVehicleAssignment, loginUserID);
            return new ResponseEntity<>(createDriverVehicleAssignment, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // UPDATE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Update DriverVehicleAssignment")
    // label for swagger
    @PatchMapping("/{driverId}")
    public ResponseEntity<?> patchDriverVehicleAssignment(@PathVariable Long driverId, @RequestParam Long routeId,
                                                          @RequestParam String vehicleNumber, @RequestParam String companyCodeId,
                                                          @RequestParam String languageId, @RequestParam String plantId,
                                                          @RequestParam String warehouseId, @RequestParam String loginUserID,
                                                          @Valid @RequestBody UpdateDriverVehicleAssignment updateDriverVehicleAssignment)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            DriverVehicleAssignment createDriverVehicleAssignment =
                    driverVehicleAssignmentService.updateDriverVehicleAssignment(companyCodeId, plantId, warehouseId,
                            languageId, driverId, routeId, vehicleNumber, loginUserID, updateDriverVehicleAssignment);
            return new ResponseEntity<>(createDriverVehicleAssignment, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // DELETE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Delete DriverVehicleAssignment")
    // label for swagger
    @DeleteMapping("/{driverId}")
    public ResponseEntity<?> deleteDriverVehicleAssignment(@PathVariable Long driverId, @RequestParam Long routeId,
                                                           @RequestParam String vehicleNumber, @RequestParam String companyCodeId,
                                                           @RequestParam String languageId, @RequestParam String plantId,
                                                           @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            driverVehicleAssignmentService.deleteDriverVehicleAssignment(companyCodeId, languageId, plantId, warehouseId,
                    driverId, vehicleNumber, routeId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // FIND
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Find DriverVehicleAssignment") // label for swagger
    @PostMapping("/findDriverVehicleAssignment")
    public ResponseEntity<?> findDriverVehicleAssignment(@Valid @RequestBody SearchDriverVehicleAssignment
                                                                 searchDriverVehicleAssignment) throws Exception {

        try {
            String currentDB = baseService.getDataBase(searchDriverVehicleAssignment.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<DriverVehicleAssignment> createDriverVehicleAssignment =
                    driverVehicleAssignmentService.findDriverVehicleAssignment(searchDriverVehicleAssignment);
            return new ResponseEntity<>(createDriverVehicleAssignment, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
