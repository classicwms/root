package com.tekclover.wms.api.masters.controller;


import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.AddNumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.NumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.SearchNumberRangeStorageBin;
import com.tekclover.wms.api.masters.model.numberrangestoragebin.UpdateNumberRangeStorageBin;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.NumberRangeStorageBinService;
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
@Api(tags = {"NumberRangeStorageBin"}, value = "NumberRangeStorageBin  Operations related to NumberRangeStorageBinController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "NumberRangeStorageBin ", description = "Operations related to NumberRangeStorageBin ")})
@RequestMapping("/numberrangestoragebin")
@RestController
public class NumberRangeStorageBinController {

    @Autowired
    NumberRangeStorageBinService numberRangeStorageBinService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = NumberRangeStorageBin.class, value = "Get all NumberRangeStorageBin details")
    // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<NumberRangeStorageBin> numberRangeStorageBinList = numberRangeStorageBinService.getAllNumberRangeStorageBin();
        return new ResponseEntity<>(numberRangeStorageBinList, HttpStatus.OK);
    }

    @ApiOperation(response = NumberRangeStorageBin.class, value = "Get a NumberRangeStorageBin") // label for swagger
    @GetMapping("/{storageSectionId}")
    public ResponseEntity<?> getNumberRangeStorageBin(@RequestParam String warehouseId, @PathVariable String storageSectionId, @RequestParam Long floorId,
                                                      @RequestParam String rowId, @RequestParam String aisleNumber, @RequestParam String companyCodeId,
                                                      @RequestParam String languageId, @RequestParam String plantId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            NumberRangeStorageBin numberRangeStorageBin =
                    numberRangeStorageBinService.getNumberRangeStorageBin(warehouseId, companyCodeId, languageId,
                            plantId, floorId, rowId, storageSectionId, aisleNumber);
            log.info("NumberRangeStorageBin : " + numberRangeStorageBin);
            return new ResponseEntity<>(numberRangeStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = NumberRangeStorageBin.class, value = "Create NumberRangeStorageBin") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postNumberRangeStorageBin(@Valid @RequestBody AddNumberRangeStorageBin newNumberRangeStorageBin,
                                                       @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newNumberRangeStorageBin.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            NumberRangeStorageBin createdNumberRangeStorageBin = numberRangeStorageBinService.createNumberRangeStorageBin(newNumberRangeStorageBin, loginUserID);
            return new ResponseEntity<>(createdNumberRangeStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = NumberRangeStorageBin.class, value = "Update NumberRangeStorageBin") // label for swagger
    @PatchMapping("/{storageSectionId}")
    public ResponseEntity<?> patchCbmInbound(@RequestParam String warehouseId, @PathVariable String storageSectionId, @RequestParam Long floorId,
                                             @RequestParam String rowId, @RequestParam String aisleNumber, @RequestParam String companyCodeId,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @Valid @RequestBody UpdateNumberRangeStorageBin updateNumberRangeStorageBin, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            NumberRangeStorageBin createdNumberRangeStorageBin =
                    numberRangeStorageBinService.updateNumberRangeItem(companyCodeId, plantId, warehouseId, languageId, floorId,
                            storageSectionId, rowId, aisleNumber, updateNumberRangeStorageBin, loginUserID);
            return new ResponseEntity<>(createdNumberRangeStorageBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = NumberRangeStorageBin.class, value = "Delete NumberRangeStorageBin") // label for swagger
    @DeleteMapping("/{storageSectionId}")
    public ResponseEntity<?> deleteNumberRangeStorageBin(@RequestParam String warehouseId, @PathVariable String storageSectionId,
                                                         @RequestParam Long floorId, @RequestParam String rowId, @RequestParam String aisleNumber,
                                                         @RequestParam String companyCodeId, @RequestParam String languageId,
                                                         @RequestParam String plantId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            numberRangeStorageBinService.deleteNumberRangeStorageBin(companyCodeId, languageId,
                    plantId, warehouseId, floorId, storageSectionId, rowId, aisleNumber, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = NumberRangeStorageBin.class, value = "Find NumberRangeStorageBin") // label for swagger
    @PostMapping("/findNumberRangeStorageBin")
    public ResponseEntity<?> findNumberRangeStorageSection(@Valid @RequestBody SearchNumberRangeStorageBin searchNumberRangeStorageBin) throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchNumberRangeStorageBin.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<NumberRangeStorageBin> createdNumberRangeBin = numberRangeStorageBinService.findNumberRangeStorageBin(searchNumberRangeStorageBin);
            return new ResponseEntity<>(createdNumberRangeBin, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
