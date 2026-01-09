package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.enterprise.service.BatchSerialService;
import com.tekclover.wms.api.masters.model.batchserial.AddBatchSerial;
import com.tekclover.wms.api.masters.model.batchserial.BatchSerial;
import com.tekclover.wms.api.masters.model.batchserial.SearchBatchSerial;
import com.tekclover.wms.api.masters.model.batchserial.UpdateBatchSerial;
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
@Api(tags = {"BatchSerial "}, value = "BatchSerial  Operations related to BatchSerialController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "BatchSerial ", description = "Operations related to BatchSerial ")})
@RequestMapping("/batchserial")
@RestController
public class BatchSerialController {

    @Autowired
    BatchSerialService batchserialService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = BatchSerial.class, value = "Get all BatchSerial details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<BatchSerial> batchserialList = batchserialService.getBatchSerials();
        return new ResponseEntity<>(batchserialList, HttpStatus.OK);
    }

    @ApiOperation(response = BatchSerial.class, value = "Get a BatchSerial") // label for swagger
    @GetMapping("/{storageMethod}")
    public ResponseEntity<?> getBatchSerial(@PathVariable String storageMethod, @RequestParam String plantId,
                                            @RequestParam String companyId, @RequestParam String languageId,
                                            @RequestParam String warehouseId, @RequestParam Long levelId, @RequestParam String maintenance) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<BatchSerial> batchserial = batchserialService.getBatchSerialOutput(storageMethod, plantId, companyId, languageId, warehouseId, levelId, maintenance);
            log.info("BatchSerial : " + batchserial);
            return new ResponseEntity<>(batchserial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BatchSerial.class, value = "Search BatchSerial") // label for swagger
    @PostMapping("/findBatchSerial")
    public List<BatchSerial> findBatchSerial(@RequestBody SearchBatchSerial searchBatchSerial)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchBatchSerial.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return batchserialService.findBatchSerial(searchBatchSerial);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BatchSerial.class, value = "Create BatchSerial") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postBatchSerial(@Valid @RequestBody List<AddBatchSerial> newBatchSerial, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newBatchSerial.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<BatchSerial> createdBatchSerial = batchserialService.createBatchSerial(newBatchSerial, loginUserID);
            return new ResponseEntity<>(createdBatchSerial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BatchSerial.class, value = "Update BatchSerial") // label for swagger
    @PatchMapping("/{storageMethod}")
    public ResponseEntity<?> patchBatchSerial(@PathVariable String storageMethod, @RequestParam String companyId,
                                              @RequestParam String plantId, @RequestParam String languageId,
                                              @RequestParam String warehouseId, @RequestParam Long levelId,
                                              @Valid @RequestBody List<UpdateBatchSerial> updateBatchSerial, @RequestParam String maintenance,
                                              @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<BatchSerial> createdBatchSerial = batchserialService.updateBatchSerial(storageMethod, companyId, plantId,
                    languageId, maintenance, warehouseId, levelId, updateBatchSerial, loginUserID);
            return new ResponseEntity<>(createdBatchSerial, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = BatchSerial.class, value = "Delete BatchSerial") // label for swagger
    @DeleteMapping("/{storageMethod}")
    public ResponseEntity<?> deleteBatchSerial(@PathVariable String storageMethod, @RequestParam String companyId,
                                               @RequestParam String languageId, @RequestParam String plantId,
                                               @RequestParam Long levelId, @RequestParam String warehouseId, @RequestParam String maintenance,
                                               @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            batchserialService.deleteBatchSerial(storageMethod, companyId, languageId, plantId, warehouseId, levelId, maintenance, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}