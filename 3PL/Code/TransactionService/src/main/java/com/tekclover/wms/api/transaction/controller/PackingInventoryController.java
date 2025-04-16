package com.tekclover.wms.api.transaction.controller;

import com.tekclover.wms.api.transaction.model.outbound.packing.FindPackingInventory;
import com.tekclover.wms.api.transaction.model.outbound.packing.PackingInventory;
import com.tekclover.wms.api.transaction.service.PackingInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Slf4j
@Api(tags = {"PackingInventory"}, value = "Packing Inventory Operation to PackingInventory Controller")
@SwaggerDefinition(tags = {@Tag(name = "PackingInventory", description = "Operation Related to PackingInventory")})
@RequestMapping("/packingInventory")
@RestController
public class PackingInventoryController {

    @Autowired
    PackingInventoryService packingInventoryService;

    // GET ALL
    @ApiOperation(response = PackingInventory.class, value = "Get All PackingInventory")
    @GetMapping("")
    public ResponseEntity<?> getAllPacking(){
        List<PackingInventory> packingInventoryList = packingInventoryService.getAllPackingInventory();
        return new ResponseEntity<>(packingInventoryList, HttpStatus.OK);
    }

    //Get PackingInventory
    @ApiOperation(response = PackingInventory.class, value = "Get PackingInventory")
    @GetMapping("/{packingMaterialNo}")
    public ResponseEntity<?> getPackingInventory(@PathVariable String packingMaterialNo, @RequestParam String companyId,
                                                 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId){
        PackingInventory packingInventory =
                packingInventoryService.getPackingInventory(companyId, plantId, languageId, warehouseId, packingMaterialNo);
        return new ResponseEntity<>(packingInventory, HttpStatus.OK);

    }

    // Create PackingInventory
    @ApiOperation(response = PackingInventory.class, value = "Create PackingInventory")
    @PostMapping("")
    public ResponseEntity<?> createPackingInventory(@Valid @RequestBody PackingInventory packingInventory,
                                                    @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException{
        PackingInventory createPackingInventory =
                packingInventoryService.createPackingInventory(packingInventory, loginUserID);
        return new ResponseEntity<>(createPackingInventory, HttpStatus.OK);
    }

    //Update PackingInventory
    @ApiOperation(response = PackingInventory.class, value = "Update PackingInventory")
    @PatchMapping("/{packingMaterialNo}")
    public ResponseEntity<?> updatePackingInventory(@PathVariable String packingMaterialNo, @RequestParam String companyId,
                                                    @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String  warehouseId, @Valid @RequestBody PackingInventory packingInventory,
                                                    @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException{

        PackingInventory updatePackingInventory =
                packingInventoryService.updatePackingInventory(companyId, plantId, languageId, warehouseId,
                        packingMaterialNo, loginUserID, packingInventory);

        return new ResponseEntity<>(updatePackingInventory, HttpStatus.OK);
    }

    // Delete PackingInventory
    @ApiOperation(response = PackingInventory.class, value = "Delete PackingInventory")
    @DeleteMapping("/{packingMaterialNo}")
    public ResponseEntity<?> deletePackingInventory(@PathVariable String packingMaterialNo, @RequestParam String companyId,
                                                    @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String warehouseId, @RequestParam String loginUserID) {
        packingInventoryService.deletePackingInventory(companyId, languageId, plantId, warehouseId, packingMaterialNo, loginUserID );
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // Find PackingInventory
    @ApiOperation(response = PackingInventory.class, value = "Search PackingInventory") // label for swagger
    @PostMapping("/findPackingInventory")
    public ResponseEntity<?> findPackingInventory(@RequestBody FindPackingInventory findPackingInventory)
            throws Exception {
        List<PackingInventory> response = packingInventoryService.findPackingInventory(findPackingInventory);
        return new ResponseEntity<>(response , HttpStatus.OK);
    }
}

