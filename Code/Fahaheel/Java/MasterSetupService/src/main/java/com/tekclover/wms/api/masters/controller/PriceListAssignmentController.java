package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.masters.model.threepl.pricelist.FindPriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelist.PriceList;
import com.tekclover.wms.api.masters.model.threepl.pricelistassignment.*;
import com.tekclover.wms.api.masters.repository.DbConfigRepository;
import com.tekclover.wms.api.masters.service.PriceListAssignmentService;
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
@Api(tags = {"PriceListAssignment"}, value = "PriceListAssignment  Operations related to PriceListAssignmentController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PriceListAssignment ",description = "Operations related to PriceListAssignment ")})
@RequestMapping("/pricelistassignment")
@RestController
public class PriceListAssignmentController {

    @Autowired
    PriceListAssignmentService priceListAssignmentService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @ApiOperation(response = PriceListAssignment.class, value = "Get a PriceListAssignment") // label for swagger
    @GetMapping("/{priceListId}")
    public ResponseEntity<?> getPriceListAssignment(@RequestParam String partnerCode, @PathVariable Long priceListId,
                                        @RequestParam String warehouseId,@RequestParam String companyCodeId,
                                                    @RequestParam String languageId,@RequestParam String plantId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PriceListAssignment PriceListAssignmentList =
                priceListAssignmentService.getPriceListAssignment(warehouseId,partnerCode,priceListId,companyCodeId,languageId,plantId);
        log.info("PriceListAssignmentList : " + PriceListAssignmentList);
        return new ResponseEntity<>(PriceListAssignmentList, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PriceListAssignment.class, value = "Create PriceListAssignment") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPriceListAssignment(@Valid @RequestBody AddPriceListAssignment newPriceListAssignment,
                                         @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPriceListAssignment.getCompanyCodeId(), newPriceListAssignment.getPlantId(), newPriceListAssignment.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PriceListAssignment createdPriceListAssignment = priceListAssignmentService.createPriceListAssignment(newPriceListAssignment, loginUserID);
        return new ResponseEntity<>(createdPriceListAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    @ApiOperation(response = PriceListAssignment.class, value = "Update PriceListAssignment") // label for swagger
    @PatchMapping("/{priceListId}")
    public ResponseEntity<?> patchPriceListAssignment(@RequestParam String warehouseId, @PathVariable Long priceListId,@RequestParam String partnerCode,@RequestParam String companyCodeId,@RequestParam String languageId,@RequestParam String plantId,
                                                      @Valid @RequestBody UpdatePriceListAssignment updatePriceListAssignment, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        PriceListAssignment createdPriceListAssignment =
                priceListAssignmentService.updatePriceListAssignment(warehouseId, priceListId, partnerCode,companyCodeId,languageId,plantId,loginUserID,updatePriceListAssignment);
        return new ResponseEntity<>(createdPriceListAssignment, HttpStatus.OK);
    }
finally {
            DataBaseContextHolder.clear();
        }
        }
    @ApiOperation(response = PriceListAssignment.class, value = "Delete PriceListAssignment") // label for swagger
    @DeleteMapping("/{priceListId}")
    public ResponseEntity<?> deletePriceListAssignment(@PathVariable Long priceListId,
                                           @RequestParam String warehouseId, @RequestParam String partnerCode,@RequestParam String companyCodeId,@RequestParam String languageId,
                                                       @RequestParam String plantId,@RequestParam String loginUserID) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        priceListAssignmentService.deletePriceListAssignment(warehouseId, priceListId, partnerCode,companyCodeId,languageId,plantId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
    //Search
    @ApiOperation(response = PriceListAssignment.class, value = "Find PriceListAssignment") // label for swagger
    @PostMapping("/find")
    public ResponseEntity<?> findPriceListAssignment(@Valid @RequestBody FindPriceListAssignment findPriceListAssignment) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findPriceListAssignment.getCompanyCodeId(), findPriceListAssignment.getPlantId(), findPriceListAssignment.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<PriceListAssignment> createdPriceListAssignment = priceListAssignmentService.findPriceListAssignment(findPriceListAssignment);
        return new ResponseEntity<>(createdPriceListAssignment, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
