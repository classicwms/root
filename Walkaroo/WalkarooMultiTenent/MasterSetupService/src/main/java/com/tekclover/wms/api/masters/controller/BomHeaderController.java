package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.bom.AddBomHeader;
import com.tekclover.wms.api.masters.model.bom.BomHeader;
import com.tekclover.wms.api.masters.model.bom.SearchBomHeader;
import com.tekclover.wms.api.masters.model.bom.UpdateBomHeader;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.BomHeaderService;
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
@Api(tags = {"BomHeader"}, value = "BomHeader  Operations related to BomHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BomHeader ", description = "Operations related to BomHeader ")})
@RequestMapping("/bomheader")
@RestController
public class BomHeaderController {

    @Autowired
    BomHeaderService bomheaderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = BomHeader.class, value = "Get all BomHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<AddBomHeader> bomheaderList = bomheaderService.getBomHeaders();
        return new ResponseEntity<>(bomheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = BomHeader.class, value = "Get a BomHeader") // label for swagger 
    @GetMapping("/{parentItemCode}")
    public ResponseEntity<?> getBomHeader(@PathVariable String parentItemCode, @RequestParam String warehouseId, @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            AddBomHeader bomheader = bomheaderService.getBomHeader(warehouseId, parentItemCode, languageId, companyCode, plantId);
            log.info("BomHeader : " + bomheader);
            return new ResponseEntity<>(bomheader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BomHeader.class, value = "Search BomHeader") // label for swagger
    @PostMapping("/findBomHeader")
    public List<AddBomHeader> findBomHeader(@RequestBody SearchBomHeader searchBomHeader)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchBomHeader.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return bomheaderService.findBomHeader(searchBomHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BomHeader.class, value = "Create BomHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBomHeader(@Valid @RequestBody AddBomHeader newBomHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newBomHeader.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            AddBomHeader createdBomHeader = bomheaderService.createBomHeader(newBomHeader, loginUserID);
            return new ResponseEntity<>(createdBomHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BomHeader.class, value = "Update BomHeader") // label for swagger
    @PatchMapping("/{parentItemCode}")
    public ResponseEntity<?> patchBomHeader(@PathVariable String parentItemCode, @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam String plantId, @RequestParam String companyCode,
                                            @Valid @RequestBody UpdateBomHeader updateBomHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            UpdateBomHeader createdBomHeader =
                    bomheaderService.updateBomHeader(warehouseId, parentItemCode, languageId, companyCode, plantId, loginUserID, updateBomHeader);
            return new ResponseEntity<>(createdBomHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }

    }

    @ApiOperation(response = BomHeader.class, value = "Delete BomHeader") // label for swagger
    @DeleteMapping("/{parentItemCode}")
    public ResponseEntity<?> deleteBomHeader(@PathVariable String parentItemCode, @RequestParam String warehouseId, @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String loginUserID) {

        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            bomheaderService.deleteBomHeader(warehouseId, parentItemCode, companyCode, languageId, plantId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}