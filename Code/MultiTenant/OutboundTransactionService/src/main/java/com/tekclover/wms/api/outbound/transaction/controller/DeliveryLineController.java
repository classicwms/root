package com.tekclover.wms.api.outbound.transaction.controller;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.deliveryline.*;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.DeliveryLineRepository;
import com.tekclover.wms.api.outbound.transaction.service.DeliveryLineService;
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
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"DeliveryLine"}, value = "DeliveryLine  Operations related to DeliveryLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "CountBar ", description = "Operations related to CountBar ")})
@RequestMapping("/deliveryline")
@RestController
public class DeliveryLineController {

    @Autowired
    private DeliveryLineService deliveryLineService;

    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    private DeliveryLineRepository deliveryLineRepository;

    // Get All
    @ApiOperation(response = DeliveryLine.class, value = "Get all DeliveryLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<DeliveryLine> deliveryLineList = deliveryLineService.getAllDeliveryLine();
        return new ResponseEntity<>(deliveryLineList, HttpStatus.OK);
    }


    // Get DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Get a DeliveryLine") // label for swagger
    @GetMapping("/{deliveryNo}")
    public ResponseEntity<?> getDeliveryLine(@PathVariable Long deliveryNo, @RequestParam String companyCodeId,
                                             @RequestParam String invoiceNumber, @RequestParam String refDocNumber,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @RequestParam String itemCode, @RequestParam Long lineNumber,
                                             @RequestParam String warehouseId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        DeliveryLine deliveryLine =
                deliveryLineService.getDeliveryLine(companyCodeId, plantId, warehouseId, invoiceNumber,
                        refDocNumber, languageId, deliveryNo, itemCode, lineNumber);

        log.info("deliveryLine");
        return new ResponseEntity<>(deliveryLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Create DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Create DeliveryLine")
    @PostMapping("")
    public ResponseEntity<?> postDeliveryLine(@Valid @RequestBody List<AddDeliveryLine> newDeliveryLine,
                                              @RequestParam String loginUserID) throws
            IllegalAccessException, InvocationTargetException {
        try {
            for (AddDeliveryLine newDeliveryline : newDeliveryLine) {

                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(newDeliveryline.getCompanyCodeId(), newDeliveryline.getPlantId(), newDeliveryline.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<DeliveryLine> createdDeliveryLine = deliveryLineService.createDeliveryLine(newDeliveryLine, loginUserID);
            return new ResponseEntity<>(createdDeliveryLine, HttpStatus.OK);
        }
        finally {
            DataBaseContextHolder.clear();
        }
    }


    //Update DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Update DeliveryLine")// label for swagger
    @PatchMapping("/deliveryNo")
    public ResponseEntity<?> patchDeliveryLine( @Valid @RequestBody List<UpdateDeliveryLine> updateDeliveryLine,
                                                @RequestParam String loginUserID ) throws IllegalAccessException, InvocationTargetException {
        try {
            for(UpdateDeliveryLine updateDeliveryline :updateDeliveryLine) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updateDeliveryline.getCompanyCodeId(), updateDeliveryline.getPlantId(), updateDeliveryline.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
                List<DeliveryLine> deliveryLine = deliveryLineService.updateDeliveryLine(updateDeliveryLine, loginUserID);
                return new ResponseEntity<>(deliveryLine, HttpStatus.OK);
            }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Delete DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Delete DeliveryLine") // label for swagger
    @DeleteMapping("/{deliveryNo}")
    public ResponseEntity<?> deleteDeliveryLine(@PathVariable Long deliveryNo, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String warehouseId,
                                                @RequestParam String itemCode, @RequestParam Long lineNumber,
                                                @RequestParam String languageId, @RequestParam String loginUserID,
                                                @RequestParam String refDocNumber, @RequestParam String invoiceNumber) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        deliveryLineService.deleteDeliveryLine(companyCodeId, plantId, warehouseId,
                deliveryNo, refDocNumber, invoiceNumber, itemCode, lineNumber, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Search DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Find DeliveryLine") // label for swagger
    @PostMapping("/findDeliveryLine")
    public ResponseEntity<?> findDeliveryLine(@Valid @RequestBody SearchDeliveryLine searchDeliveryLine) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchDeliveryLine.getCompanyCodeId(), searchDeliveryLine.getPlantId(), searchDeliveryLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        List<DeliveryLine> createdDeliveryLine =
                deliveryLineService.findDeliveryLine(searchDeliveryLine);
        return new ResponseEntity<>(createdDeliveryLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }


    // Get DeliveryLine Count
    @ApiOperation(response = DeliveryLineCount.class, value = "Get a DeliveryLine Count") // label for swagger
    @GetMapping("/count")
    public ResponseEntity<?> getDeliveryLineCount(@RequestParam String companyCodeId, @RequestParam String languageId,
                                                  @RequestParam String plantId, @RequestParam String warehouseId,
                                                  @RequestParam String driverId) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        DeliveryLineCount deliveryLine =
                deliveryLineService.getDeliveryLineCount(companyCodeId, languageId, plantId, warehouseId, driverId);
        return new ResponseEntity<>(deliveryLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }

    // Search DeliveryLine
    @ApiOperation(response = DeliveryLineCount.class, value = "Find DeliveryLineCount") // label for swagger
    @PostMapping("/findDeliveryLineCount")
    public ResponseEntity<?> findDeliveryLineCount(@Valid @RequestBody FindDeliveryLineCount findDeliveryLineCount) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(findDeliveryLineCount.getCompanyCodeId(), findDeliveryLineCount.getPlantId(), findDeliveryLineCount.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
        DeliveryLineCount createdDeliveryLine =
                deliveryLineService.findDeliveryLineCount(findDeliveryLineCount);
        return new ResponseEntity<>(createdDeliveryLine, HttpStatus.OK);
    }
        finally {
            DataBaseContextHolder.clear();
        }
        }
}
