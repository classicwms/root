package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.employeeid.AddEmployeeId;
import com.tekclover.wms.api.idmaster.model.employeeid.EmployeeId;
import com.tekclover.wms.api.idmaster.model.employeeid.FindEmployeeId;
import com.tekclover.wms.api.idmaster.model.employeeid.UpdateEmployeeId;
import com.tekclover.wms.api.idmaster.repository.DbConfigRepository;
import com.tekclover.wms.api.idmaster.service.EmployeeIdService;
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
@Api(tags = {"EmployeeId"}, value = "EmployeeId  Operations related to EmployeeIdController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "EmployeeId ", description = "Operations related to EmployeeId ")})
@RequestMapping("/employeeid")
@RestController
public class EmployeeIdController {

    @Autowired
    EmployeeIdService employeeidService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = EmployeeId.class, value = "Get all EmployeeId details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<EmployeeId> employeeidList = employeeidService.getEmployeeIds();
        return new ResponseEntity<>(employeeidList, HttpStatus.OK);
    }

    @ApiOperation(response = EmployeeId.class, value = "Get a EmployeeId") // label for swagger 
    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployeeId(@RequestParam String warehouseId, @PathVariable String employeeId,
                                           @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            EmployeeId employeeid =
                    employeeidService.getEmployeeId(warehouseId, employeeId, companyCodeId, languageId, plantId);
            log.info("EmployeeId : " + employeeid);
            return new ResponseEntity<>(employeeid, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = EmployeeId.class, value = "Create EmployeeId") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postEmployeeId(@Valid @RequestBody AddEmployeeId newEmployeeId,
                                            @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newEmployeeId.getCompanyCodeId(), newEmployeeId.getPlantId(), newEmployeeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            EmployeeId createdEmployeeId = employeeidService.createEmployeeId(newEmployeeId, loginUserID);
            return new ResponseEntity<>(createdEmployeeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = EmployeeId.class, value = "Update EmployeeId") // label for swagger
    @PatchMapping("/{employeeId}")
    public ResponseEntity<?> patchEmployeeId(@RequestParam String warehouseId, @PathVariable String employeeId,
                                             @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId,
                                             @Valid @RequestBody UpdateEmployeeId updateEmployeeId, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            EmployeeId createdEmployeeId =
                    employeeidService.updateEmployeeId(warehouseId, employeeId, companyCodeId, languageId, plantId, loginUserID, updateEmployeeId);
            return new ResponseEntity<>(createdEmployeeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = EmployeeId.class, value = "Delete EmployeeId") // label for swagger
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<?> deleteEmployeeId(@RequestParam String warehouseId, @PathVariable String employeeId,
                                              @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            employeeidService.deleteEmployeeId(warehouseId, employeeId, companyCodeId, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = EmployeeId.class, value = "Find EmployeeId") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findEmployeeId(@Valid @RequestBody FindEmployeeId findEmployeeId) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(findEmployeeId.getCompanyCodeId(), findEmployeeId.getPlantId(), findEmployeeId.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<EmployeeId> createdEmployeeId = employeeidService.findEmployeeId(findEmployeeId);
            return new ResponseEntity<>(createdEmployeeId, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}