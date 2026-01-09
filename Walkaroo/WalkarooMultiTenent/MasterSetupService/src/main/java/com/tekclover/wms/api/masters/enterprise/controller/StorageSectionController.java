package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.storagesection.AddStorageSection;
import com.tekclover.wms.api.masters.model.storagesection.SearchStorageSection;
import com.tekclover.wms.api.masters.model.storagesection.StorageSection;
import com.tekclover.wms.api.masters.model.storagesection.UpdateStorageSection;
import com.tekclover.wms.api.masters.enterprise.service.StorageSectionService;
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
@Api(tags = {"StorageSection "}, value = "StorageSection  Operations related to StorageSectionController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StorageSection ", description = "Operations related to StorageSection ")})
@RequestMapping("/storagesection")
@RestController
public class StorageSectionController {

    @Autowired
    StorageSectionService storagesectionService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = StorageSection.class, value = "Get all StorageSection details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StorageSection> storagesectionList = storagesectionService.getStorageSections();
        return new ResponseEntity<>(storagesectionList, HttpStatus.OK);
    }

    @ApiOperation(response = StorageSection.class, value = "Get a StorageSection")
    @GetMapping("/{storageSectionId}")
    public ResponseEntity<?> getStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId,
                                               @RequestParam Long floorId, @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageSection storagesection =
                    storagesectionService.getStorageSection(warehouseId, floorId, storageSectionId, companyId, languageId, plantId);
            log.info("StorageSection : " + storagesection);
            return new ResponseEntity<>(storagesection, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageSection.class, value = "Search StorageSection") // label for swagger
    @PostMapping("/findStorageSection")
    public List<StorageSection> findStorageSection(@RequestBody SearchStorageSection searchStorageSection)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchStorageSection.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return storagesectionService.findStorageSection(searchStorageSection);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageSection.class, value = "Create StorageSection") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postStorageSection(@Valid @RequestBody AddStorageSection newStorageSection,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newStorageSection.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageSection createdStorageSection =
                    storagesectionService.createStorageSection(newStorageSection, loginUserID);
            return new ResponseEntity<>(createdStorageSection, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageSection.class, value = "Update StorageSection") // label for swagger
    @PatchMapping("/{storageSectionId}")
    public ResponseEntity<?> patchStorageSection(@PathVariable String storageSectionId, @RequestParam String warehouseId,
                                                 @RequestParam Long floorId, @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId, @Valid @RequestBody UpdateStorageSection updateStorageSection,
                                                 @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            StorageSection createdStorageSection =
                    storagesectionService.updateStorageSection(warehouseId, floorId, companyId, languageId, plantId, storageSectionId, updateStorageSection, loginUserID);
            return new ResponseEntity<>(createdStorageSection, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = StorageSection.class, value = "Delete StorageSection") // label for swagger
    @DeleteMapping("/{storageSectionId}")
    public ResponseEntity<?> deleteStorageSection(@PathVariable String storageSectionId, @RequestParam String companyId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long floorId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            storagesectionService.deleteStorageSection(warehouseId, floorId, storageSectionId, companyId, plantId, languageId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}