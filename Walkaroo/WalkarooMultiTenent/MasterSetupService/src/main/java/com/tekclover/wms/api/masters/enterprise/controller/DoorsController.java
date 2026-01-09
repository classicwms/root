package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.doors.AddDoors;
import com.tekclover.wms.api.masters.model.doors.Doors;
import com.tekclover.wms.api.masters.model.doors.UpdateDoors;
import com.tekclover.wms.api.masters.enterprise.service.DoorsService;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
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
@Api(tags = {"Doors "}, value = "Doors  Operations related to DoorsController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Doors ", description = "Operations related to Doors ")})
@RequestMapping("/doors")
@RestController
public class DoorsController {

    @Autowired
    DoorsService doorsService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Doors.class, value = "Get all Doors details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Doors> doorsList = doorsService.getDoorss();
        return new ResponseEntity<>(doorsList, HttpStatus.OK);
    }

    @ApiOperation(response = Doors.class, value = "Get a Doors") // label for swagger 
    @GetMapping("/{doorNumber}")
    public ResponseEntity<?> getDoors(@PathVariable String doorNumber) {
        Doors doors = doorsService.getDoors(doorNumber);
        log.info("Doors : " + doors);
        return new ResponseEntity<>(doors, HttpStatus.OK);
    }

    @ApiOperation(response = Doors.class, value = "Create Doors") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDoors(@Valid @RequestBody AddDoors newDoors, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newDoors.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Doors createdDoors = doorsService.createDoors(newDoors, loginUserID);
            return new ResponseEntity<>(createdDoors, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Doors.class, value = "Update Doors") // label for swagger
    @PatchMapping("/{doorNumber}")
    public ResponseEntity<?> patchDoors(@PathVariable String doorNumber,
                                        @Valid @RequestBody UpdateDoors updateDoors, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(updateDoors.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Doors createdDoors = doorsService.updateDoors(doorNumber, updateDoors, loginUserID);
            return new ResponseEntity<>(createdDoors, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Doors.class, value = "Delete Doors") // label for swagger
    @DeleteMapping("/{doorNumber}")
    public ResponseEntity<?> deleteDoors(@PathVariable String doorNumber, @RequestParam String loginUserID) throws ParseException {
        doorsService.deleteDoors(doorNumber, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}