package com.tekclover.wms.api.inbound.transaction.controller;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.SearchInventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.UpdateInventoryMovement;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.InventoryMovementService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"InventoryMovement"}, value = "InventoryMovement  Operations related to InventoryMovementController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "InventoryMovement ", description = "Operations related to InventoryMovement ")})
@RequestMapping("/inventorymovement")
@RestController
public class InventoryMovementController {

    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    InventoryMovementService inventorymovementService;

    @Autowired
    BaseService baseService;

    @ApiOperation(response = InventoryMovement.class, value = "Get all InventoryMovement details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<InventoryMovement> inventorymovementList = inventorymovementService.getInventoryMovements();
        return new ResponseEntity<>(inventorymovementList, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Get a InventoryMovement") // label for swagger 
    @GetMapping("/{movementType}")
    public ResponseEntity<?> getInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId,
                                                  @RequestParam Long submovementType, @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo) {
        InventoryMovement inventorymovement =
                inventorymovementService.getInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode,
                        batchSerialNumber, movementDocumentNo);
        return new ResponseEntity<>(inventorymovement, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Search InventoryMovement") // label for swagger
    @PostMapping("/findInventoryMovement")
    public Stream<InventoryMovement> findInventoryMovement(@RequestBody SearchInventoryMovement searchInventoryMovement)
            throws Exception {
        try {
            String currentDB = baseService.getDataBase(searchInventoryMovement.getPlantId().get(0));
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            return inventorymovementService.findInventoryMovement(searchInventoryMovement);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InventoryMovement.class, value = "Create InventoryMovement") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postInventoryMovement(@Valid @RequestBody AddInventoryMovement newInventoryMovement, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(newInventoryMovement.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InventoryMovement createdInventoryMovement = inventorymovementService.createInventoryMovement(newInventoryMovement, loginUserID);
            return new ResponseEntity<>(createdInventoryMovement, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InventoryMovement.class, value = "Update InventoryMovement") // label for swagger
    @PatchMapping("/{movementType}")
    public ResponseEntity<?> patchInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId,
                                                    @RequestParam Long submovementType, @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo,
                                                    @Valid @RequestBody UpdateInventoryMovement updateInventoryMovement, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            String currentDB = baseService.getDataBase(updateInventoryMovement.getPlantId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            InventoryMovement createdInventoryMovement =
                    inventorymovementService.updateInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode,
                            batchSerialNumber, movementDocumentNo, updateInventoryMovement);
            return new ResponseEntity<>(createdInventoryMovement, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = InventoryMovement.class, value = "Delete InventoryMovement") // label for swagger
    @DeleteMapping("/{movementType}")
    public ResponseEntity<?> deleteInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId,
                                                     @RequestParam Long submovementType, @RequestParam String packBarcodes, @RequestParam String itemCode,
                                                     @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo, @RequestParam String loginUserID) {
        inventorymovementService.deleteInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode,
                batchSerialNumber, movementDocumentNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}