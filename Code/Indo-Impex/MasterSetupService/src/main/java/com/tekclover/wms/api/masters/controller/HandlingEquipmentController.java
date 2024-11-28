package com.tekclover.wms.api.masters.controller;

import com.tekclover.wms.api.masters.model.handlingequipment.AddHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.HandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.SearchHandlingEquipment;
import com.tekclover.wms.api.masters.model.handlingequipment.UpdateHandlingEquipment;
import com.tekclover.wms.api.masters.service.HandlingEquipmentService;
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
@Api(tags = {"HandlingEquipment"}, value = "HandlingEquipment  Operations related to HandlingEquipmentController")
// label for swagger
@SwaggerDefinition(tags = {@Tag(name = "HandlingEquipment ", description = "Operations related to HandlingEquipment ")})
@RequestMapping("/handlingequipment")
@RestController
public class HandlingEquipmentController {

    @Autowired
    HandlingEquipmentService handlingequipmentService;

    @ApiOperation(response = HandlingEquipment.class, value = "Get all HandlingEquipment details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<HandlingEquipment> handlingequipmentList = handlingequipmentService.getHandlingEquipments();
        return new ResponseEntity<>(handlingequipmentList, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger 
    @GetMapping("/{handlingEquipmentId}")
    public ResponseEntity<?> getHandlingEquipmentByWarehouseId(@PathVariable String handlingEquipmentId, @RequestParam String warehouseId,
                                                               @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
//    	HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipmentByWarehouseId(warehouseId, handlingEquipmentId);
        HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipment(warehouseId, handlingEquipmentId, companyCodeId, languageId, plantId);
        log.info("HandlingEquipment : " + handlingequipment);
        return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger 
    @GetMapping("/{heBarcode}/v2/barCode")
    public ResponseEntity<?> getHandlingEquipment(@PathVariable String heBarcode, @RequestParam String warehouseId,
                                                  @RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String plantId) {
//        HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipment(warehouseId, heBarcode);
        HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipmentV2(companyCodeId, plantId, languageId, warehouseId, heBarcode);
        log.info("HandlingEquipment : " + handlingequipment);
        return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Get a HandlingEquipment") // label for swagger
    @GetMapping("/{heBarcode}/barCode")
    public ResponseEntity<?> getHandlingEquipment(@PathVariable String heBarcode, @RequestParam String warehouseId) {
        HandlingEquipment handlingequipment = handlingequipmentService.getHandlingEquipment(warehouseId, heBarcode);
        log.info("HandlingEquipment : " + handlingequipment);
        return new ResponseEntity<>(handlingequipment, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Search HandlingEquipment") // label for swagger
    @PostMapping("/findHandlingEquipment")
    public List<HandlingEquipment> findHandlingEquipment(@RequestBody SearchHandlingEquipment searchHandlingEquipment) {
        return handlingequipmentService.findHandlingEquipment(searchHandlingEquipment);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Create HandlingEquipment") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postHandlingEquipment(@Valid @RequestBody AddHandlingEquipment newHandlingEquipment,
                                                   @RequestParam String loginUserID) {
        HandlingEquipment createdHandlingEquipment = handlingequipmentService.createHandlingEquipment(newHandlingEquipment, loginUserID);
        return new ResponseEntity<>(createdHandlingEquipment, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Update HandlingEquipment") // label for swagger
    @PatchMapping("/{handlingEquipmentId}")
    public ResponseEntity<?> patchHandlingEquipment(@PathVariable String handlingEquipmentId, @RequestParam String companyCodeId,
                                                    @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                                    @Valid @RequestBody UpdateHandlingEquipment updateHandlingEquipment, @RequestParam String loginUserID) {
        HandlingEquipment createdHandlingEquipment =
                handlingequipmentService.updateHandlingEquipment(handlingEquipmentId, companyCodeId, plantId, warehouseId, languageId,
                        updateHandlingEquipment, loginUserID);
        return new ResponseEntity<>(createdHandlingEquipment, HttpStatus.OK);
    }

    @ApiOperation(response = HandlingEquipment.class, value = "Delete HandlingEquipment") // label for swagger
    @DeleteMapping("/{handlingEquipmentId}")
    public ResponseEntity<?> deleteHandlingEquipment(@PathVariable String handlingEquipmentId, @RequestParam String companyCodeId,
                                                     @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String languageId,
                                                     @RequestParam String loginUserID) {
        handlingequipmentService.deleteHandlingEquipment(handlingEquipmentId, companyCodeId, languageId, plantId, warehouseId, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}