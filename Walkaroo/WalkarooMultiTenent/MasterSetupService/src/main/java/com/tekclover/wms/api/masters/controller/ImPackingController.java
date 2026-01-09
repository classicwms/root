package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.impacking.AddImPacking;
import com.tekclover.wms.api.masters.model.impacking.ImPacking;
import com.tekclover.wms.api.masters.model.impacking.SearchImPacking;
import com.tekclover.wms.api.masters.model.impacking.UpdateImPacking;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.ImPackingService;
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
@Api(tags = {"ImPacking"}, value = "ImPacking  Operations related to ImPackingController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImPacking ", description = "Operations related to ImPacking ")})
@RequestMapping("/impacking")
@RestController
public class ImPackingController {

    @Autowired
    ImPackingService impackingService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = ImPacking.class, value = "Get all ImPacking details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImPacking> impackingList = impackingService.getImPackings();
        return new ResponseEntity<>(impackingList, HttpStatus.OK);
    }

    @ApiOperation(response = ImPacking.class, value = "Get a ImPacking") // label for swagger 
    @GetMapping("/{packingMaterialNo}")
    public ResponseEntity<?> getImPacking(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String itemCode) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImPacking impacking = impackingService.getImPacking(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId, itemCode);
//    	log.info("ImPacking : " + impacking);
            return new ResponseEntity<>(impacking, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImPacking.class, value = "Search ImPacking") // label for swagger
    @PostMapping("/findImPacking")
    public List<ImPacking> findImPacking(@RequestBody SearchImPacking searchImPacking)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchImPacking.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return impackingService.findImPacking(searchImPacking);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImPacking.class, value = "Create ImPacking") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImPacking(@Valid @RequestBody AddImPacking newImPacking, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newImPacking.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImPacking createdImPacking = impackingService.createImPacking(newImPacking, loginUserID);
            return new ResponseEntity<>(createdImPacking, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImPacking.class, value = "Update ImPacking") // label for swagger
    @PatchMapping("/{packingMaterialNo}")
    public ResponseEntity<?> patchImPacking(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                            @Valid @RequestBody UpdateImPacking updateImPacking, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            ImPacking createdImPacking = impackingService.updateImPacking(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId, itemCode, updateImPacking, loginUserID);
            return new ResponseEntity<>(createdImPacking, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImPacking.class, value = "Delete ImPacking") // label for swagger
    @DeleteMapping("/{packingMaterialNo}")
    public ResponseEntity<?> deleteImPacking(@PathVariable String packingMaterialNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String itemCode, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            impackingService.deleteImPacking(packingMaterialNo, companyCodeId, plantId, languageId, warehouseId, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}