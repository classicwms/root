package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.storagetype.AddStorageType;
import com.tekclover.wms.api.masters.model.storagetype.SearchStorageType;
import com.tekclover.wms.api.masters.model.storagetype.StorageType;
import com.tekclover.wms.api.masters.model.storagetype.UpdateStorageType;
import com.tekclover.wms.api.masters.enterprise.service.StorageTypeService;
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
@Api(tags = {"StorageType "}, value = "StorageType  Operations related to StorageTypeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageType ", description = "Operations related to StorageType ")})
@RequestMapping("/storagetype")
@RestController
public class StorageTypeController {

    @Autowired
    StorageTypeService storagetypeService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = StorageType.class, value = "Get all StorageType details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageType> storagetypeList = storagetypeService.getStorageTypes();
        return new ResponseEntity<>(storagetypeList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageType.class, value = "Get a StorageType")
    @GetMapping("/{storageTypeId}")
    public ResponseEntity<?> getStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam Long storageClassId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageType storagetype = storagetypeService.getStorageType(warehouseId, storageClassId, storageTypeId, companyId, languageId, plantId);
            log.info("StorageType : " + storagetype);
            return new ResponseEntity<>(storagetype, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageType.class, value = "Search StorageType") // label for swagger
    @PostMapping("/findStorageType")
    public List<StorageType> findStorageType(@RequestBody SearchStorageType searchStorageType)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchStorageType.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return storagetypeService.findStorageType(searchStorageType);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageType.class, value = "Create StorageType") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageType(@Valid @RequestBody AddStorageType newStorageType, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newStorageType.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageType createdStorageType = storagetypeService.createStorageType(newStorageType, loginUserID);
            return new ResponseEntity<>(createdStorageType, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageType.class, value = "Update StorageType") // label for swagger
    @PatchMapping("/{storageTypeId}")
    public ResponseEntity<?> patchStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId, @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                              @Valid @RequestBody UpdateStorageType updateStorageType, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageType createdStorageType = storagetypeService.updateStorageType(warehouseId, storageClassId, storageTypeId, companyId, languageId, plantId, updateStorageType, loginUserID);
            return new ResponseEntity<>(createdStorageType, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageType.class, value = "Delete StorageType") // label for swagger
    @DeleteMapping("/{storageTypeId}")
    public ResponseEntity<?> deleteStorageType(@PathVariable Long storageTypeId, @RequestParam String warehouseId, @RequestParam Long storageClassId, @RequestParam String companyId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            storagetypeService.deleteStorageType(warehouseId, storageClassId, storageTypeId, companyId, plantId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}