package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.imvariant.AddImVariant;
import com.tekclover.wms.api.masters.model.imvariant.ImVariant;
import com.tekclover.wms.api.masters.model.imvariant.SearchImVariant;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.BaseService;
import com.tekclover.wms.api.masters.service.ImVariantService;
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
@Api(tags = {"ImVariant"}, value = "ImVariant  Operations related to ImVariantController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImVariant ", description = "Operations related to ImVariant ")})
@RequestMapping("/imvariant")
@RestController
public class ImVariantController {
    @Autowired
    ImVariantService imVariantService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = ImVariant.class, value = "Get all ImVariant details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<ImVariant> imVariantList = imVariantService.getAllImVariant();
        return new ResponseEntity<>(imVariantList, HttpStatus.OK);
    }

    @ApiOperation(response = ImVariant.class, value = "Get a ImVariant") // label for swagger
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getImVariant(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                          @RequestParam String plantId, @RequestParam String warehouseId,
                                          @RequestParam String languageId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<ImVariant> imVariant = imVariantService.getImVariant(warehouseId, companyCodeId, languageId, plantId, itemCode);
            log.info("ImVariant : " + imVariant);
            return new ResponseEntity<>(imVariant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImVariant.class, value = "Search ImVariant") // label for swagger
    @PostMapping("/findImVariant")
    public List<ImVariant> findImVariant(@RequestBody SearchImVariant searchImVariant)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchImVariant.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return imVariantService.findImVariant(searchImVariant);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImVariant.class, value = "Create ImVariant") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImVariant(@Valid @RequestBody List<AddImVariant> newImVariant, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newImVariant.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<ImVariant> createdImVariant = imVariantService.createImvariant(newImVariant, loginUserID);
            return new ResponseEntity<>(createdImVariant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImVariant.class, value = "Update ImVariant") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchImVariant(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                            @RequestParam String plantId, @RequestParam String warehouseId,
                                            @RequestParam String languageId, @Valid @RequestBody List<AddImVariant> updateImVariant,
                                            @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<ImVariant> createdImVariant = imVariantService.updateImVariant(companyCodeId, plantId, warehouseId, languageId,
                    itemCode, updateImVariant, loginUserID);
            return new ResponseEntity<>(createdImVariant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = ImVariant.class, value = "Delete ImVariant") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteImVariant(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                             @RequestParam String plantId, @RequestParam String warehouseId,
                                             @RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            imVariantService.deleteImVariant(companyCodeId, languageId, plantId, warehouseId, itemCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
