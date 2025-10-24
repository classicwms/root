package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.impalletization.AddImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.ImPalletization;
import com.tekclover.wms.api.masters.model.impalletization.SearchImPalletization;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.ImPalletizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.Data;
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
@Api(tags = {"ImPalletization"}, value = "ImPalletization  Operations related to ImPalletizationController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "ImPalletization ",description = "Operations related to ImPalletization ")})
@RequestMapping("/impalletization")
@RestController
public class ImPalletizationController {
    @Autowired
    ImPalletizationService imPalletizationService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = ImPalletization.class, value = "Get a ImPalletization") // label for swagger
    @GetMapping("/{itemCode}")
    public ResponseEntity<?> getImPacking(@PathVariable String itemCode,@RequestParam String companyCodeId,
                                          @RequestParam String plantId, @RequestParam String languageId,
                                          @RequestParam String warehouseId ) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<ImPalletization> imPalletization = imPalletizationService.getImPalletization(warehouseId,companyCodeId,languageId,plantId,itemCode);
        log.info("ImPalletization : " + imPalletization);
        return new ResponseEntity<>(imPalletization, HttpStatus.OK);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImPalletization.class, value = "Search ImPalletization") // label for swagger
    @PostMapping("/findImPalletization")
    public List<ImPalletization> findImPalletization(@RequestBody SearchImPalletization searchImPalletization)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbNameList(searchImPalletization.getCompanyCodeId(), searchImPalletization.getPlantId(), searchImPalletization.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        return imPalletizationService.findImPalletization(searchImPalletization);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImPalletization.class, value = "Create ImPalletization") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postImPalletization(@Valid @RequestBody List<AddImPalletization> newImPalletization,
                                                 @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            for (AddImPalletization newImpalletization : newImPalletization) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(newImpalletization.getCompanyCodeId(), newImpalletization.getPlantId(), newImpalletization.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
        List<ImPalletization> createdPalletization = imPalletizationService.createImPalletization(newImPalletization, loginUserID);
        return new ResponseEntity<>(createdPalletization , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImPalletization.class, value = "Update ImPalletization") // label for swagger
    @PatchMapping("/{itemCode}")
    public ResponseEntity<?> patchPalletization(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                                @RequestParam String plantId,@RequestParam String languageId,
                                                @RequestParam String warehouseId,@Valid @RequestBody List<AddImPalletization> updateImPalletization,
                                                @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<ImPalletization> createdImPalletization =
                imPalletizationService.updateImPalletization(companyCodeId,plantId,warehouseId,languageId,itemCode,
                        updateImPalletization,loginUserID);
        return new ResponseEntity<>(createdImPalletization , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = ImPalletization.class, value = "Delete ImPalletization") // label for swagger
    @DeleteMapping("/{itemCode}")
    public ResponseEntity<?> deleteImPalletization(@PathVariable String itemCode, @RequestParam String companyCodeId,
                                                   @RequestParam String plantId,@RequestParam String languageId,
                                                   @RequestParam String warehouseId, @RequestParam String loginUserID) throws ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        imPalletizationService.deleteImPalletization(companyCodeId,languageId,plantId,warehouseId,itemCode,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
