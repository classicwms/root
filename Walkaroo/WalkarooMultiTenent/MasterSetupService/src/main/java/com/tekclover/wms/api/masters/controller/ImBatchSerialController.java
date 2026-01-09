package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.imbatchserial.AddImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.ImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.SearchImBatchSerial;
import com.tekclover.wms.api.masters.model.imbatchserial.UpdateImBatchSerial;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.ImBatchSerialService;
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
@Api(tags = {"ImBatchSerial"}, value = "ImBatchSerial  Operations related to ImBatchSerial") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImBatchSerial ", description = "Operations related to ImBatchSerial")})
@RequestMapping("/imbatchserial")
@RestController
public class ImBatchSerialController {

    @Autowired
    ImBatchSerialService imBatchSerialService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = ImBatchSerial.class, value = "Get all ImBatchSerial details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImBatchSerial> imBatchSerialList = imBatchSerialService.getImBatchSerials();
        return new ResponseEntity<>(imBatchSerialList, HttpStatus.OK);
    }

    @ApiOperation(response = ImBatchSerial.class, value = "Get a ImBatchSerial") // label for swagger
    @GetMapping("/{storageMethod}")
    public ResponseEntity<?> getImBasicData2(@PathVariable String storageMethod, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam String itemCode) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImBatchSerial imBatchSerial = imBatchSerialService.getImBatchSerial(warehouseId, companyCodeId, languageId, plantId, itemCode, storageMethod);
            log.info("imBatchSerial : " + imBatchSerial);
            return new ResponseEntity<>(imBatchSerial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBatchSerial.class, value = "Search ImBatchSerial") // label for swagger
    @PostMapping("/findImBatchSerial")
    public List<ImBatchSerial> findImBatchSerial(@RequestBody SearchImBatchSerial searchImBatchSerial)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchImBatchSerial.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return imBatchSerialService.findImBatchSerial(searchImBatchSerial);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBatchSerial.class, value = "Create ImBatchSerial") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImBatchSerial(@Valid @RequestBody AddImBatchSerial newImBatchSerial, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newImBatchSerial.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImBatchSerial createdImBatchSerial = imBatchSerialService.createImBatchSerial(newImBatchSerial, loginUserID);
            return new ResponseEntity<>(createdImBatchSerial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBatchSerial.class, value = "Update ImBatchSerial") // label for swagger
    @PatchMapping("/{storageMethod}")
    public ResponseEntity<?> patchImBatchSerial(@PathVariable String storageMethod, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam String itemCode,
                                                @Valid @RequestBody UpdateImBatchSerial updateImBatchSerial, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImBatchSerial createdImBatchSerial = imBatchSerialService.updateBatchSerial(companyCodeId, plantId, warehouseId, languageId, itemCode, storageMethod, updateImBatchSerial, loginUserID);
            return new ResponseEntity<>(createdImBatchSerial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImBatchSerial.class, value = "Delete ImBatchSerial") // label for swagger
    @DeleteMapping("/{storageMethod}")
    public ResponseEntity<?> deleteImBatchSerial(@PathVariable String storageMethod, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            imBatchSerialService.deleteImBatchSerial(companyCodeId, languageId, plantId, warehouseId, itemCode, storageMethod, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
