package com.tekclover.wms.api.outbound.transaction.controller;


import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.deliveryheader.AddDeliveryHeader;
import com.tekclover.wms.api.outbound.transaction.model.deliveryheader.DeliveryHeader;
import com.tekclover.wms.api.outbound.transaction.model.deliveryheader.SearchDeliveryHeader;
import com.tekclover.wms.api.outbound.transaction.model.deliveryheader.UpdateDeliveryHeader;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.DeliveryHeaderRepository;
import com.tekclover.wms.api.outbound.transaction.service.DeliveryHeaderService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"DeliveryHeader"}, value = "DeliveryHeader  Operations related to DeliveryHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CountBar ",description = "Operations related to CountBar ")})
@RequestMapping("/deliveryheader")
@RestController
public class DeliveryHeaderController {

    @Autowired
    private DeliveryHeaderRepository deliveryHeaderRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    private DeliveryHeaderService deliveryHeaderService;


    // Get All
    @ApiOperation(response = DeliveryHeader.class, value = "Get all DeliveryHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<DeliveryHeader> deliveryHeaderList = deliveryHeaderService.getAllDeliveryHeader();
        return new ResponseEntity<>(deliveryHeaderList, HttpStatus.OK);
    }


    // Get DeliveryHeader
    @ApiOperation(response = DeliveryHeader.class, value = "Get a DeliveryHeader") // label for swagger
    @GetMapping("/{deliveryNo}")
    public ResponseEntity<?> getDeliveryHeader(@PathVariable Long deliveryNo,@RequestParam String companyCodeId,
                                               @RequestParam String languageId,@RequestParam String plantId,
                                               @RequestParam String warehouseId){
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

        DeliveryHeader deliveryHeader=
                deliveryHeaderService.getDeliveryHeader(companyCodeId,plantId,warehouseId,languageId,deliveryNo);
        log.info("DeliveryHeader");
        return new ResponseEntity<>(deliveryHeader,HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Create DeliveryHeader
    @ApiOperation(response = DeliveryHeader.class, value = "Create DeliveryHeader")
    @PostMapping("")
    public ResponseEntity<?> postDeliveryHeader(@Valid @RequestBody AddDeliveryHeader newDeliveryHeader,
                                                @RequestParam String loginUserID) throws
            IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newDeliveryHeader.getCompanyCodeId(), newDeliveryHeader.getPlantId(), newDeliveryHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

        DeliveryHeader createdDeliveryHeader =
                deliveryHeaderService.createDeliveryHeader(newDeliveryHeader, loginUserID);
        return new ResponseEntity<>(createdDeliveryHeader , HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    //Update DeliveryHeader
    @ApiOperation(response = DeliveryHeader.class, value = "Update DeliveryHeader")// label for swagger
    @PatchMapping("/{deliveryNo}")
    public ResponseEntity<?> patchDeliveryHeader(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String warehouseId,@RequestParam String loginUserID,
                                                 @RequestParam String languageId,@PathVariable Long deliveryNo,
                                                 @Valid @RequestBody UpdateDeliveryHeader updateDeliveryHeader)
            throws IllegalAccessException,InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);

        DeliveryHeader deliveryHeader = deliveryHeaderService.UpdateDeliveryHeader(companyCodeId,plantId,
                warehouseId, deliveryNo,languageId,updateDeliveryHeader,loginUserID);
        return new ResponseEntity<>(deliveryHeader , HttpStatus.OK);

    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Delete DeliveryHeader
    @ApiOperation(response = DeliveryHeader.class, value = "Delete DeliveryHeader") // label for swagger
    @DeleteMapping("/{deliveryNo}")
    public ResponseEntity<?> deleteAisleId(@PathVariable Long deliveryNo,@RequestParam String companyCodeId,
                                           @RequestParam String plantId,@RequestParam String warehouseId,
                                           @RequestParam String languageId,@RequestParam String loginUserID){

        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        deliveryHeaderService.deleteDeliveryHeader(companyCodeId,plantId,warehouseId,
                deliveryNo,languageId,loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {

            DataBaseContextHolder.clear();
        }
        }

// Search DeliveryHeader
    @ApiOperation(response = DeliveryHeader.class, value = "Find DeliveryHeader") // label for swagger
    @PostMapping("/findDeliveryHeader")
    public ResponseEntity<?> findDeliveryHeader(@Valid @RequestBody SearchDeliveryHeader searchDeliveryHeader) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchDeliveryHeader.getCompanyCodeId(), searchDeliveryHeader.getPlantId(), searchDeliveryHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<DeliveryHeader> createdDeliveryHeader =
                deliveryHeaderService.findDeliveryHeader(searchDeliveryHeader);
        return new ResponseEntity<>(createdDeliveryHeader, HttpStatus.OK);

    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
