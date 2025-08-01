package com.tekclover.wms.api.idmaster.controller;

import com.tekclover.wms.api.idmaster.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.idmaster.model.interimbarcode.AddInterimBarcode;
import com.tekclover.wms.api.idmaster.model.interimbarcode.FindInterimBarcode;
import com.tekclover.wms.api.idmaster.model.interimbarcode.InterimBarcode;
import com.tekclover.wms.api.idmaster.service.InterimBarcodeService;
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
@Api(tags = {"InterimBarcode"}, value = "InterimBarcode Operations related to InterimBarcodeController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InterimBarcode", description = "Operations related to InterimBarcode")})
@RequestMapping("/interimbarcode")
@RestController
public class InterimBarcodeController {

    @Autowired
    InterimBarcodeService interimBarcodeService;


    @ApiOperation(response = InterimBarcode.class, value = "Get all InterimBarcode details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InterimBarcode> interimBarcodeList = interimBarcodeService.getAll();
        return new ResponseEntity<>(interimBarcodeList, HttpStatus.OK);
    }


    @ApiOperation(response = InterimBarcode.class, value = "Get a InterimBarcode") // label for swagger 
    @GetMapping("/{storageBin}")
    public ResponseEntity<?> getInterimBarcode(@PathVariable String storageBin, @RequestParam String itemCode, @RequestParam String barcode) {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            InterimBarcode interimBarcode = interimBarcodeService.getInterimBarcode(storageBin, itemCode, barcode);
            log.info("InterimBarcode : " + interimBarcode);
            return new ResponseEntity<>(interimBarcode, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InterimBarcode.class, value = "Create InterimBarcode") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> addInterimBarcode(@Valid @RequestBody AddInterimBarcode newInterimBarcode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            InterimBarcode createdInterimBarcode = interimBarcodeService.createInterimBarcode(newInterimBarcode, loginUserID);
            return new ResponseEntity<>(createdInterimBarcode, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    //Search
    @ApiOperation(response = InterimBarcode.class, value = "Find InterimBarcode") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findInterimBarcode(@Valid @RequestBody FindInterimBarcode findInterimBarcode) throws
            Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("FAHAHEEL");
            List<InterimBarcode> dbInterimBarcode = interimBarcodeService.findInterimBarcode(findInterimBarcode);
            return new ResponseEntity<>(dbInterimBarcode, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}