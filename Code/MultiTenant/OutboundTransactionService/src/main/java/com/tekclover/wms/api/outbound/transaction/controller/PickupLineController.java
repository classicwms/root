package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.dto.ImPartner;
import com.tekclover.wms.api.outbound.transaction.model.dto.UpdateBarcodeInput;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.SearchPickupLine;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.SearchPickupLineV2;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.WarehouseRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.UpdatePickupLine;
import com.tekclover.wms.api.outbound.transaction.service.PickupLineService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PickupLine"}, value = "PickupLine  Operations related to PickupLineController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PickupLine ", description = "Operations related to PickupLine ")})
@RequestMapping("/pickupline")
@RestController
public class PickupLineController {

    @Autowired
    PickupLineService pickuplineService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @ApiOperation(response = PickupLine.class, value = "Get all PickupLine details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PickupLine> pickuplineList = pickuplineService.getPickupLines();
        return new ResponseEntity<>(pickuplineList, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Get a PickupLine") // label for swagger 
    @GetMapping("/{actualHeNo}")
    public ResponseEntity<?> getPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId,
                                           @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                           @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode) {
        PickupLine pickupline = pickuplineService.getPickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode);
        log.info("PickupLine : " + pickupline);
        return new ResponseEntity<>(pickupline, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Get a PickupLine") // label for swagger 
    @GetMapping("/additionalBins")
    public ResponseEntity<?> getAdditionalBins(@RequestParam String warehouseId, @RequestParam String itemCode,
                                               @RequestParam Long obOrdertypeId, @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin) {
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = null;
        Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(warehouseId, 0L);
        routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        List<InventoryV2> additionalBins =
                pickuplineService.getAdditionalBins(warehouseId, itemCode, obOrdertypeId, proposedPackBarCode, proposedStorageBin);
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Search PickupLine") // label for swagger
    @PostMapping("/findPickupLine")
    public List<PickupLine> findPickupLine(@RequestBody SearchPickupLine searchPickupLine)
            throws Exception {

        return pickuplineService.findPickupLine(searchPickupLine);
    }

    @ApiOperation(response = PickupLine.class, value = "Create PickupLine") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPickupLine(@Valid @RequestBody List<AddPickupLine> newPickupLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {


        List<PickupLine> createdPickupLine = pickuplineService.createPickupLine(newPickupLine, loginUserID);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Update PickupLine") // label for swagger
    @PatchMapping("/{actualHeNo}")
    public ResponseEntity<?> patchPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId,
                                             @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                             @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                             @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                             @Valid @RequestBody UpdatePickupLine updatePickupLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        PickupLine createdPickupLine =
                pickuplineService.updatePickupLine(actualHeNo, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
                        loginUserID, updatePickupLine);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Delete PickupLine") // label for swagger
    @DeleteMapping("/{actualHeNo}")
    public ResponseEntity<?> deletePickupLine(@PathVariable String actualHeNo,
                                              @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                              @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                              @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                              @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException {

        pickuplineService.deletePickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    //=======================================================V2============================================================
    @ApiOperation(response = PickupLineV2.class, value = "Get all PickupLine details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPickupLineV2() {
        List<PickupLineV2> pickuplineList = pickuplineService.getPickupLinesV2();
        return new ResponseEntity<>(pickuplineList, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Get a PickupLine") // label for swagger
    @GetMapping("/v2/{actualHeNo}")
    public ResponseEntity<?> getPickupLineV2(@PathVariable String actualHeNo, @RequestParam String companyCodeId,
                                             @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                             @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                             @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupLineV2 pickupline = pickuplineService.getPickupLineV2(companyCodeId, plantId, languageId, warehouseId, actualHeNo, pickupNumber, preOutboundNo, refDocNumber, partnerCode,
                    lineNumber, itemCode);
            log.info("PickupLine : " + pickupline);
            return new ResponseEntity<>(pickupline, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Get a PickupLine") // label for swagger
    @GetMapping("/v2/additionalBins")
    public ResponseEntity<?> getAdditionalBinsV2(@RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                 @RequestParam String itemCode, @RequestParam String manufacturerName,
                                                 @RequestParam Long obOrdertypeId, @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<IInventoryImpl> additionalBins =
                    pickuplineService.getAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode, obOrdertypeId, proposedPackBarCode, proposedStorageBin, manufacturerName);
            log.info("additionalBins : " + additionalBins);
            return new ResponseEntity<>(additionalBins, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Search PickupLine") // label for swagger
    @PostMapping("/v2/findPickupLine")
    public Stream<PickupLineV2> findPickupLineV2(@RequestBody SearchPickupLineV2 searchPickupLine)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPickupLine.getCompanyCodeId(), searchPickupLine.getPlantId(), searchPickupLine.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickuplineService.findPickupLineV2(searchPickupLine);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Create PickupLine") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPickupLineV2(@Valid @RequestBody List<AddPickupLine> newPickupLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            log.info("AddPickupLine -----> {}", newPickupLine);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(String.valueOf(newPickupLine.get(0).getCompanyCodeId()), newPickupLine.get(0).getPlantId(), newPickupLine.get(0).getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            List<PickupLineV2> createdPickupLine = null;
            switch (routingDb) {
                case "FAHAHEEL":
                case "AUTO_LAP":
                    createdPickupLine = pickuplineService.createPickupLineNonCBMV2(newPickupLine, loginUserID);
                    break;
                case "NAMRATHA":
                    createdPickupLine = pickuplineService.createPickupLineNonCBMV4(newPickupLine, loginUserID);
                    break;
                case "REEFERON":
                    createdPickupLine = pickuplineService.createPickupLineV5(newPickupLine, loginUserID);
                    break;
                case "KNOWELL":
                    createdPickupLine = pickuplineService.createPickupLineNonCBMV7(newPickupLine, loginUserID);
                    break;
            }
            return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Update PickupLine") // label for swagger
    @PatchMapping("/v2/{actualHeNo}")
    public ResponseEntity<?> patchPickupLineV2(@PathVariable String actualHeNo, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                               @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                               @Valid @RequestBody PickupLineV2 updatePickupLine, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupLineV2 createdPickupLine =
                    pickuplineService.updatePickupLineV2(companyCodeId, plantId, languageId, actualHeNo, warehouseId, preOutboundNo, refDocNumber,
                            partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
                            loginUserID, updatePickupLine);
            return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Update BarcodeId") // label for swagger
    @PatchMapping("/v2/barcodeId")
    public ResponseEntity<?> patchPickupLineBarcodeIdV2(@Valid @RequestBody UpdateBarcodeInput updateBarcodeInput) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updateBarcodeInput.getCompanyCodeId(), updateBarcodeInput.getPlantId(), updateBarcodeInput.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            ImPartner results = pickuplineService.updatePickupLineForBarcodeV2(updateBarcodeInput);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupLineV2.class, value = "Delete PickupLine") // label for swagger
    @DeleteMapping("/v2/{actualHeNo}")
    public ResponseEntity<?> deletePickupLineV2(@PathVariable String actualHeNo, @RequestParam String companyCodeId,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                                @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                                @RequestParam String loginUserID) throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            pickuplineService.deletePickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                    pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}