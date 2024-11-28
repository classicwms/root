package com.tekclover.wms.api.enterprise.controller;

import com.tekclover.wms.api.enterprise.model.warehouse.AddWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.SearchWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.UpdateWarehouse;
import com.tekclover.wms.api.enterprise.model.warehouse.Warehouse;
import com.tekclover.wms.api.enterprise.service.WarehouseService;
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
import java.text.ParseException;
import java.util.List;

@Slf4j
@Validated
@Api(tags = {"Warehouse"}, value = "Warehouse  Operations related to WarehouseController")
@SwaggerDefinition(tags = {@Tag(name = "Warehouse ", description = "Operations related to Warehouse")})
@RequestMapping("/warehouse")
@RestController
public class WarehouseController {

    @Autowired
    WarehouseService warehouseService;

    @ApiOperation(response = Warehouse.class, value = "Get all Warehouse details")
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<Warehouse> plantList = warehouseService.getWarehouses();
        return new ResponseEntity<>(plantList, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Get a Warehouse")
    @GetMapping("/{warehouseId}")
    public ResponseEntity<?> getWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation, @RequestParam String companyId,
                                          @RequestParam String languageId, @RequestParam String plantId, Long warehouseTypeId) {
        Warehouse warehouse = warehouseService.getWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId);
        log.info("Warehouse : " + warehouse);
        return new ResponseEntity<>(warehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Search Warehouse") // label for swagger
    @PostMapping("/findWarehouse")
    public List<Warehouse> findWarehouse(@RequestBody SearchWarehouse searchWarehouse) {
        return warehouseService.findWarehouse(searchWarehouse);
    }

    @ApiOperation(response = Warehouse.class, value = "Create Warehouse")
    @PostMapping("")
    public ResponseEntity<?> postWarehouse(@Valid @RequestBody AddWarehouse newWarehouse, @RequestParam String loginUserID) {
        Warehouse createdWarehouse = warehouseService.createWarehouse(newWarehouse, loginUserID);
        return new ResponseEntity<>(createdWarehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Update Warehouse")
    @PatchMapping("/{warehouseId}")
    public ResponseEntity<?> patchWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation, @RequestParam Long warehouseTypeId,
                                            @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId,
                                            @Valid @RequestBody UpdateWarehouse updateWarehouse, @RequestParam String loginUserID) {
        Warehouse updatedWarehouse = warehouseService.updateWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId, updateWarehouse, loginUserID);
        return new ResponseEntity<>(updatedWarehouse, HttpStatus.OK);
    }

    @ApiOperation(response = Warehouse.class, value = "Delete Warehouse")
    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable String warehouseId, @RequestParam String modeOfImplementation, @RequestParam Long warehouseTypeId,
                                             @RequestParam String companyId, @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID) throws ParseException {
        warehouseService.deleteWarehouse(warehouseId, modeOfImplementation, warehouseTypeId, companyId, plantId, languageId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}