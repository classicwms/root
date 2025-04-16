package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.drivervehicleassignment.AddDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.DriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.SearchDriverVehicleAssignment;
import com.tekclover.wms.api.masters.model.drivervehicleassignment.UpdateDriverVehicleAssignment;
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
@Api(tags = {"DriverVehicleAssignment"}, value = "DriverVehicleAssignment  Operations related to DriverVehicleAssignmentController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "DriverVehicleAssignment ",description = "Operations related to DriverVehicleAssignment ")})
@RequestMapping("/drivervehicleassignment")
@RestController
public class DriverVehicleAssignmentController {

    @Autowired
    private DriverVehicleAssignmentService driverVehicleAssignmentService;

    // GET ALL
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Get all DriverVehicleAssignment details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<DriverVehicleAssignment> driverVehicleAssignmentList =
                driverVehicleAssignmentService.getAllDriverVehicleAssignment();
        return new ResponseEntity<>(driverVehicleAssignmentList, HttpStatus.OK);
    }

    // GET
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Get a DriverVehicleAssignment") // label for swagger
    @GetMapping("/{driverId}")
    public ResponseEntity<?> getDriver(@PathVariable Long driverId,@RequestParam Long routeId,@RequestParam String vehicleNumber,
                                       @RequestParam String companyCodeId, @RequestParam String languageId,
                                       @RequestParam String plantId, @RequestParam String warehouseId) {

        DriverVehicleAssignment driverVehicleAssignment =
                driverVehicleAssignmentService.getDriverVehicleAssignment(driverId,vehicleNumber,routeId,
                        companyCodeId,plantId,languageId,warehouseId);
//        log.info("driverVehicleAssignment : " + driverVehicleAssignment);
        return new ResponseEntity<>(driverVehicleAssignment, HttpStatus.OK);
    }

    // CREATE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Create DriverVehicleAssignment") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDriverVehicleAssignment(@Valid @RequestBody AddDriverVehicleAssignment newDriverVehicleAssignment,
                                                         @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        DriverVehicleAssignment createDriverVehicleAssignment =
                driverVehicleAssignmentService.createDriverVehicleAssignment(newDriverVehicleAssignment, loginUserID);
        return new ResponseEntity<>(createDriverVehicleAssignment , HttpStatus.OK);
    }

    // UPDATE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Update DriverVehicleAssignment") // label for swagger
    @PatchMapping("/{driverId}")
    public ResponseEntity<?> patchDriverVehicleAssignment(@PathVariable Long driverId, @RequestParam Long routeId,
                                                          @RequestParam String vehicleNumber, @RequestParam String companyCodeId,
                                                          @RequestParam String languageId, @RequestParam String plantId,
                                                          @RequestParam String warehouseId, @RequestParam String loginUserID,
                                                          @Valid @RequestBody UpdateDriverVehicleAssignment updateDriverVehicleAssignment)
            throws IllegalAccessException, InvocationTargetException, ParseException {

        DriverVehicleAssignment createDriverVehicleAssignment =
                driverVehicleAssignmentService.updateDriverVehicleAssignment(companyCodeId,plantId,warehouseId,
                        languageId,driverId,routeId,vehicleNumber,loginUserID,updateDriverVehicleAssignment);
        return new ResponseEntity<>(createDriverVehicleAssignment , HttpStatus.OK);
    }

    // DELETE
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Delete DriverVehicleAssignment") // label for swagger
    @DeleteMapping("/{driverId}")
    public ResponseEntity<?> deleteDriverVehicleAssignment(@PathVariable Long driverId,@RequestParam Long routeId,
                                                           @RequestParam String vehicleNumber,@RequestParam String companyCodeId,
                                                           @RequestParam String languageId, @RequestParam String plantId,
                                                           @RequestParam String warehouseId,@RequestParam String loginUserID) throws ParseException {

        driverVehicleAssignmentService.deleteDriverVehicleAssignment(companyCodeId,languageId,plantId,warehouseId,
                driverId,vehicleNumber,routeId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // FIND
    @ApiOperation(response = DriverVehicleAssignment.class, value = "Find DriverVehicleAssignment") // label for swagger
    @PostMapping("/findDriverVehicleAssignment")
    public ResponseEntity<?> findDriverVehicleAssignment(@Valid @RequestBody SearchDriverVehicleAssignment
                                                                     searchDriverVehicleAssignment) throws Exception {

        List<DriverVehicleAssignment> createDriverVehicleAssignment =
                driverVehicleAssignmentService.findDriverVehicleAssignment(searchDriverVehicleAssignment);
        return new ResponseEntity<>(createDriverVehicleAssignment, HttpStatus.OK);
    }
}
