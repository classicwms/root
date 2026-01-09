package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.enterprise.model.storagebintype.AddStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.SearchStorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.StorageBinType;
import com.tekclover.wms.api.enterprise.model.storagebintype.UpdateStorageBinType;
import com.tekclover.wms.api.enterprise.repository.DbConfigRepository;
import com.tekclover.wms.api.enterprise.service.StorageBinTypeService;
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
@Api(tags = {"StorageBinType "}, value = "StorageBinType  Operations related to StorageBinTypeController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageBinType ", description = "Operations related to StorageBinType ")})
@RequestMapping("/storagebintype")
@RestController
public class StorageBinTypeController {

    @Autowired
    StorageBinTypeService storagebintypeService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = StorageBinType.class, value = "Get all StorageBinType details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageBinType> storagebintypeList = storagebintypeService.getStorageBinTypes();
        return new ResponseEntity<>(storagebintypeList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageBinType.class, value = "Get a StorageBinType")
    @GetMapping("/{storageBinTypeId}")
    public ResponseEntity<?> getStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId,
                                               @RequestParam Long storageTypeId, @RequestParam String companyId, @RequestParam Long storageClassId, @RequestParam String languageId, @RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinType storagebintype =
                    storagebintypeService.getStorageBinType(warehouseId, storageTypeId, storageBinTypeId, storageClassId, companyId, languageId, plantId);
            log.info("StorageBinType : " + storagebintype);
            return new ResponseEntity<>(storagebintype, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinType.class, value = "Search StorageBinType") // label for swagger
    @PostMapping("/findStorageBinType")
    public List<StorageBinType> findStorageBinType(@RequestBody SearchStorageBinType searchStorageBinType)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(searchStorageBinType.getCompanyId(), searchStorageBinType.getPlantId(), searchStorageBinType.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return storagebintypeService.findStorageBinType(searchStorageBinType);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinType.class, value = "Create StorageBinType") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageBinType(@Valid @RequestBody AddStorageBinType newStorageBinType,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(newStorageBinType.getCompanyId(), newStorageBinType.getPlantId(), newStorageBinType.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinType createdStorageBinType =
                    storagebintypeService.createStorageBinType(newStorageBinType, loginUserID);
            return new ResponseEntity<>(createdStorageBinType, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinType.class, value = "Update StorageBinType") // label for swagger
    @PatchMapping("/{storageBinTypeId}")
    public ResponseEntity<?> patchStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam String companyId, @RequestParam Long storageClassId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam Long storageTypeId,
                                                 @Valid @RequestBody UpdateStorageBinType updateStorageBinType, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            StorageBinType createdStorageBinType = storagebintypeService.updateStorageBinType(warehouseId, storageTypeId, storageBinTypeId, companyId, storageClassId, plantId, languageId, updateStorageBinType, loginUserID);
            return new ResponseEntity<>(createdStorageBinType, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageBinType.class, value = "Delete StorageBinType") // label for swagger
    @DeleteMapping("/{storageBinTypeId}")
    public ResponseEntity<?> deleteStorageBinType(@PathVariable Long storageBinTypeId, @RequestParam String warehouseId, @RequestParam String companyId, @RequestParam Long storageClassId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam Long storageTypeId,
                                                  @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            storagebintypeService.deleteStorageBinType(warehouseId, storageTypeId, storageClassId, storageBinTypeId, companyId, plantId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}