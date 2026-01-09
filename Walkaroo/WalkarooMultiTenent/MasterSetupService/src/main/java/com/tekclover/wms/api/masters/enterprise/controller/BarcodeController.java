package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.enterprise.service.BarcodeService;
import com.tekclover.wms.api.masters.model.barcode.Barcode;
import com.tekclover.wms.api.masters.model.barcode.AddBarcode;
import com.tekclover.wms.api.masters.model.barcode.SearchBarcode;
import com.tekclover.wms.api.masters.model.barcode.UpdateBarcode;
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
@Api(tags = {"Barcode "}, value = "Barcode  Operations related to BarcodeController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Barcode ", description = "Operations related to Barcode ")})
@RequestMapping("/barcode")
@RestController
public class BarcodeController {

    @Autowired
    BarcodeService barcodeService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Barcode.class, value = "Get all Barcode details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Barcode> barcodeList = barcodeService.getBarcodes();
        return new ResponseEntity<>(barcodeList, HttpStatus.OK);
    }

    @ApiOperation(response = Barcode.class, value = "Get a Barcode")
    @GetMapping("/{barcodeTypeId}")
    public ResponseEntity<?> getBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                        @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                        @RequestParam String levelReference, @RequestParam Long processId) {
        Barcode barcode = barcodeService.getBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId,
                levelReference, processId);
        log.info("Barcode : " + barcode);
        return new ResponseEntity<>(barcode, HttpStatus.OK);
    }

    @ApiOperation(response = Barcode.class, value = "Search Barcode") // label for swagger
    @PostMapping("/findBarcode")
    public List<Barcode> findBarcode(@RequestBody SearchBarcode searchBarcode)
            throws Exception {
        return barcodeService.findBarcode(searchBarcode);
    }

    @ApiOperation(response = Barcode.class, value = "Create Barcode") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBarcode(@Valid @RequestBody AddBarcode newBarcode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newBarcode.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Barcode createdBarcode = barcodeService.createBarcode(newBarcode, loginUserID);
            return new ResponseEntity<>(createdBarcode, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Barcode.class, value = "Update Barcode") // label for swagger
    @PatchMapping("/{barcodeTypeId}")
    public ResponseEntity<?> patchBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                          @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                          @RequestParam String levelReference, @RequestParam Long processId,
                                          @Valid @RequestBody UpdateBarcode updateBarcode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateBarcode.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            Barcode createdBarcode = barcodeService.updateBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId,
                    levelReference, processId, updateBarcode, loginUserID);
            return new ResponseEntity<>(createdBarcode, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Barcode.class, value = "Delete Barcode") // label for swagger
    @DeleteMapping("/{barcodeTypeId}")
    public ResponseEntity<?> deleteBarcode(@PathVariable Long barcodeTypeId, @RequestParam String warehouseId,
                                           @RequestParam String method, @RequestParam Long barcodeSubTypeId, @RequestParam Long levelId,
                                           @RequestParam String levelReference, @RequestParam Long processId, @RequestParam String loginUserID) {
        barcodeService.deleteBarcode(warehouseId, method, barcodeTypeId, barcodeSubTypeId, levelId,
                levelReference, processId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}