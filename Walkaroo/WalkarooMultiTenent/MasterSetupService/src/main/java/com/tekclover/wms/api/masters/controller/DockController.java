package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.dock.AddDock;
import com.tekclover.wms.api.masters.model.dock.Dock;
import com.tekclover.wms.api.masters.model.dock.SearchDock;
import com.tekclover.wms.api.masters.model.dock.UpdateDock;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.DockService;
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
@Api(tags = {"Dock"}, value = "Dock  Operations related to DockController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Dock ", description = "Operations related to Dock ")})
@RequestMapping("/dock")
@RestController
public class DockController {
    @Autowired
    private DockService dockService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Dock.class, value = "Get all Dock details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Dock> dockList = dockService.getAllDock();
        return new ResponseEntity<>(dockList, HttpStatus.OK);
    }

    @ApiOperation(response = Dock.class, value = "Get a Dock") // label for swagger
    @GetMapping("/{dockId}")
    public ResponseEntity<?> getDock(@PathVariable String dockId, @RequestParam String companyCodeId, @RequestParam String languageId,
                                     @RequestParam String plantId, @RequestParam String dockType, @RequestParam String warehouseId) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Dock dbDock = dockService.getDock(dockId, companyCodeId, plantId, languageId, warehouseId, dockType);
            return new ResponseEntity<>(dbDock, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    @ApiOperation(response = Dock.class, value = "Create Dock") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postDock(@Valid @RequestBody AddDock newDock, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newDock.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Dock createdDock = dockService.createDock(newDock, loginUserID);
            return new ResponseEntity<>(createdDock, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Dock.class, value = "Update Dock") // label for swagger
    @PatchMapping("/{dockId}")
    public ResponseEntity<?> patchDock(@PathVariable String dockId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String dockType, @RequestParam String warehouseId,
                                       @Valid @RequestBody UpdateDock updateDock, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Dock createdDock = dockService.updateDock(companyCodeId, plantId, warehouseId, languageId, dockType, dockId, updateDock, loginUserID);
            return new ResponseEntity<>(createdDock, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Dock.class, value = "Delete Dock") // label for swagger
    @DeleteMapping("/{dockId}")
    public ResponseEntity<?> deleteDock(@PathVariable String dockId, @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String dockType, @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            dockService.deleteDock(companyCodeId, languageId, plantId, warehouseId, dockId, dockType, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Dock.class, value = "Find Dock") // label for swagger
    @PostMapping("/findDock")
    public ResponseEntity<?> findDock(@Valid @RequestBody SearchDock searchDock) throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchDock.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Dock> createdDock = dockService.findDock(searchDock);
            return new ResponseEntity<>(createdDock, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
