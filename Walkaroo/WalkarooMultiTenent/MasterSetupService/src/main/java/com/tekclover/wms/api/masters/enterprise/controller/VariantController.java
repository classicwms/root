package com.tekclover.wms.api.masters.enterprise.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.variant.AddVariant;
import com.tekclover.wms.api.masters.model.variant.SearchVariant;
import com.tekclover.wms.api.masters.model.variant.UpdateVariant;
import com.tekclover.wms.api.masters.model.variant.Variant;
import com.tekclover.wms.api.masters.enterprise.service.VariantService;
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
@Api(tags = {"Variant "}, value = "Variant  Operations related to VariantController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "Variant ", description = "Operations related to Variant ")})
@RequestMapping("/variant")
@RestController
public class VariantController {

    @Autowired
    VariantService variantService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = Variant.class, value = "Get all Variant details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Variant> variantList = variantService.getVariants();
        return new ResponseEntity<>(variantList, HttpStatus.OK);
    }

    @ApiOperation(response = Variant.class, value = "Get a Variant") // label for swagger
    @GetMapping("/{variantId}")
    public ResponseEntity<?> getVariant(@PathVariable String variantId, @RequestParam String companyId,
                                        @RequestParam String languageId, @RequestParam Long levelId,
                                        @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String variantSubId) {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Variant> variant = variantService.getVariantOutput(variantId, companyId, languageId, plantId, warehouseId, levelId, variantSubId);
            log.info("Variant : " + variant);
            return new ResponseEntity<>(variant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Variant.class, value = "Search Variant") // label for swagger
    @PostMapping("/findVariant")
    public List<Variant> findVariant(@RequestBody SearchVariant searchVariant)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchVariant.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return variantService.findVariant(searchVariant);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Variant.class, value = "Create Variant") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postVariant(@Valid @RequestBody List<AddVariant> newVariant, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(newVariant.get(0).getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Variant> createdVariant = variantService.createVariant(newVariant, loginUserID);
            return new ResponseEntity<>(createdVariant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();

        }
    }

    @ApiOperation(response = Variant.class, value = "Update Variant") // label for swagger
    @PatchMapping("/{variantId}")
    public ResponseEntity<?> patchVariant(@PathVariable String variantId, @RequestParam String companyId,
                                          @RequestParam String plantId, @RequestParam String warehouseId,
                                          @RequestParam String languageId, @RequestParam Long levelId, @RequestParam String variantSubId,
                                          @Valid @RequestBody List<UpdateVariant> updateVariant, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            List<Variant> createdVariant = variantService.updateVariant(variantId, companyId, languageId, plantId, variantSubId,
                    warehouseId, levelId, updateVariant, loginUserID);
            return new ResponseEntity<>(createdVariant, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = Variant.class, value = "Delete Variant") // label for swagger
    @DeleteMapping("/{variantId}")
    public ResponseEntity<?> deleteVariant(@PathVariable String variantId, @RequestParam String companyId,
                                           @RequestParam String plantId, @RequestParam String languageId,
                                           @RequestParam String warehouseId, @RequestParam Long levelId,
                                           @RequestParam String variantSubId, @RequestParam String loginUserID) throws ParseException {
        try {
            String currentDB = baseService.getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            variantService.deleteVariant(variantId, companyId, languageId, plantId, variantSubId, warehouseId, levelId, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}