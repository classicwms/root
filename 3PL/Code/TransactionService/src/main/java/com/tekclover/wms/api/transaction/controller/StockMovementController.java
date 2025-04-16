package com.tekclover.wms.api.transaction.controller;


import com.tekclover.wms.api.transaction.model.threepl.stockmovement.FindStockMovement;
import com.tekclover.wms.api.transaction.model.threepl.stockmovement.StockMovement;
import com.tekclover.wms.api.transaction.service.StockMovementService;
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
@Api(tags = {"StockMovement"}, value = "StockMovement  Operations related to StockMovementController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "StockMovement ", description = "Operations related to StockMovement ")})
@RequestMapping("/stockmovement")
@RestController
public class StockMovementController {

    @Autowired
    StockMovementService stockMovementService;

    // Get All
    @ApiOperation(response = StockMovement.class, value = "Get all StockMovement details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<StockMovement> stockMovementList = stockMovementService.getAllStockMovement();
        return new ResponseEntity<>(stockMovementList, HttpStatus.OK);
    }


    // Get StockMovement
    @ApiOperation(response = StockMovement.class, value = "Get a StockMovement") // label for swagger
    @GetMapping("/{movementDocNo}")
    public ResponseEntity<?> getStockMovement(@PathVariable Long movementDocNo, @RequestParam String companyCodeId,
                                             @RequestParam String languageId, @RequestParam String plantId,
                                             @RequestParam String itemCode, @RequestParam String warehouseId) {

        List<StockMovement> stockMovement =
                stockMovementService.getStockMovement(movementDocNo, languageId, companyCodeId, plantId, warehouseId, itemCode);

        log.info("stockMovement");
        return new ResponseEntity<>(stockMovement, HttpStatus.OK);
    }

    // Create StockMovement
    @ApiOperation(response = StockMovement.class, value = "Create StockMovement")
    @PostMapping("")
    public ResponseEntity<?> postDeliveryLine(@Valid @RequestBody List<StockMovement> newStockMovement,
                                              @RequestParam String loginUserID) throws
            IllegalAccessException, InvocationTargetException {

        List<StockMovement> createdStockMovement = stockMovementService.createStockMovement(newStockMovement, loginUserID);
        return new ResponseEntity<>(createdStockMovement, HttpStatus.OK);
    }


    //Update StockMovement
    @ApiOperation(response = StockMovement.class, value = "Update StockMovement")// label for swagger
    @PatchMapping("/movementDocNo")
    public ResponseEntity<?> patchMovementNo(@PathVariable Long movementDocNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                              @Valid @RequestBody List<StockMovement> updateStockMovement,
                                              @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {

        List<StockMovement> stockMovementList = stockMovementService.updateStockMovement(movementDocNo, languageId,
                companyCodeId, plantId, warehouseId, itemCode, updateStockMovement, loginUserID);
        return new ResponseEntity<>(stockMovementList, HttpStatus.OK);
    }

    // Delete StockMovement
    @ApiOperation(response = StockMovement.class, value = "Delete StockMovement") // label for swagger
    @DeleteMapping("/{movementDocNo}")
    public ResponseEntity<?> deleteStockMovement(@PathVariable Long movementDocNo, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String warehouseId,
                                                @RequestParam String itemCode, @RequestParam String languageId, @RequestParam String loginUserID) {

        stockMovementService.deleteStockMovement(companyCodeId, plantId, languageId, warehouseId, itemCode, movementDocNo, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Search StockMovement
    @ApiOperation(response = StockMovement.class, value = "Find StockMovement") // label for swagger
    @PostMapping("/findStockMovement")
    public ResponseEntity<?> findStockMovement(@Valid @RequestBody FindStockMovement findStockMovement) throws Exception {

        List<StockMovement> stockMovements =
                stockMovementService.findStockMovement(findStockMovement);
        return new ResponseEntity<>(stockMovements, HttpStatus.OK);
    }



}
