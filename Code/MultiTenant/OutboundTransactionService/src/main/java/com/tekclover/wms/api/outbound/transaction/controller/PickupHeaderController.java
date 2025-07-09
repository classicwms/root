package com.tekclover.wms.api.outbound.transaction.controller;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.model.cyclecount.perpetual.UpdatePerpetualLine;
import com.tekclover.wms.api.outbound.transaction.model.dto.PickListTransaction;
import com.tekclover.wms.api.outbound.transaction.model.dto.PickupHeaderGroupByDto;
import com.tekclover.wms.api.outbound.transaction.model.inventory.Inventory;
import com.tekclover.wms.api.outbound.transaction.model.mnc.InhouseTransferHeaderEntity;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.SearchPickupHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.*;
import com.tekclover.wms.api.outbound.transaction.model.warehouse.Warehouse;
import com.tekclover.wms.api.outbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.outbound.transaction.repository.WarehouseRepository;
import com.tekclover.wms.api.outbound.transaction.service.AsyncService;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import lombok.Data;
import org.springframework.beans.BeanUtils;
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

import com.tekclover.wms.api.outbound.transaction.service.PickupHeaderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Api(tags = {"PickupHeader"}, value = "PickupHeader  Operations related to PickupHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PickupHeader ", description = "Operations related to PickupHeader ")})
@RequestMapping("/pickupheader")
@RestController
public class PickupHeaderController {

    @Autowired
    PickupHeaderService pickupheaderService;
    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    AsyncService asyncService;

    @ApiOperation(response = PickupHeader.class, value = "Get all PickupHeader details") // label for swagger
    @GetMapping("")
    public ResponseEntity<?> getAll() {
        List<PickupHeader> pickupheaderList = pickupheaderService.getPickupHeaders();
        return new ResponseEntity<>(pickupheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Get a PickupHeader") // label for swagger 
    @GetMapping("/{pickupNumber}")
    public ResponseEntity<?> getPickupHeader(@PathVariable String pickupNumber,
                                             @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                             @RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String itemCode) {
        PickupHeader pickupheader =
                pickupheaderService.getPickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                        lineNumber, itemCode);
        log.info("PickupHeader : " + pickupheader);
        return new ResponseEntity<>(pickupheader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
    @PostMapping("/findPickupHeader")
    public List<PickupHeader> findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPickupHeader.getCompanyCodeId(), searchPickupHeader.getPlantId(), searchPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickupheaderService.findPickupHeader(searchPickupHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
//    @PostMapping("/groupby/findPickupHeader")
//    public List<PickupHeaderGroupByDto> findPickupHeaderNamratha(@RequestBody FindPickupHeaderNamratha searchPickupHeader)
//            throws Exception {
//        try {
//            DataBaseContextHolder.setCurrentDb("MT");
//            String routingDb = dbConfigRepository.getDbList(searchPickupHeader.getCompanyCodeId(), searchPickupHeader.getPlantId(), searchPickupHeader.getWarehouseId());
//            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb(routingDb);
//            return pickupheaderService.getPickupHeaderGroupByNamratha(searchPickupHeader);
//        } finally {
//            DataBaseContextHolder.clear();
//        }
//    }

    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
    @PostMapping("/groupby/findPickupHeader")
    public PickListTransaction findPickupHeaderNamratha(@RequestBody FindPickupHeaderNamratha searchPickupHeader)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPickupHeader.getCompanyCodeId(), searchPickupHeader.getPlantId(), searchPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickupheaderService.getPickListCancellation(searchPickupHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/findPickupHeaderNew")
    public Stream<PickupHeader> findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader)
            throws Exception {

        return pickupheaderService.findPickupHeaderNew(searchPickupHeader);
    }

    @ApiOperation(response = PickupHeader.class, value = "Create PickupHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPickupHeader(@Valid @RequestBody AddPickupHeader newPickupHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPickupHeader.getCompanyCodeId(), newPickupHeader.getPlantId(), newPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupHeader createdPickupHeader = pickupheaderService.createPickupHeader(newPickupHeader, loginUserID);
            return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody UpdatePickupHeader updatePickupHeader,
                                               @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        try {
            log.info("UpdatePickupHeader -----> {}", updatePickupHeader);
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(updatePickupHeader.getCompanyCodeId(), updatePickupHeader.getPlantId(), warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupHeader createdPickupHeader =
                    pickupheaderService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader);
            return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader")
    // label for swagger
    @PatchMapping("/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
                                                                 @RequestParam("loginUserID") String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            for (UpdatePickupHeader updatePickupHeaderlist : updatePickupHeaderList) {
                DataBaseContextHolder.setCurrentDb("MT");
                String routingDb = dbConfigRepository.getDbName(updatePickupHeaderlist.getCompanyCodeId(), updatePickupHeaderlist.getPlantId(), updatePickupHeaderlist.getWarehouseId());
                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb(routingDb);
            }
            List<PickupHeader> createdPickupHeader =
                    pickupheaderService.patchAssignedPickerIdInPickupHeader(loginUserID, updatePickupHeaderList);
            return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
        } finally {

        }
    }

    @ApiOperation(response = PickupHeader.class, value = "Delete PickupHeader") // label for swagger
    @DeleteMapping("/{pickupNumber}")
    public ResponseEntity<?> deletePickupHeader(@PathVariable String pickupNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                @RequestParam String proposedPackCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {

        pickupheaderService.deletePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=======================================================V2============================================================

    @ApiOperation(response = PickupHeaderV2.class, value = "Get all PickupHeader details") // label for swagger
    @GetMapping("/v2")
    public ResponseEntity<?> getAllPickupHeader() {
        List<PickupHeaderV2> pickupheaderList = pickupheaderService.getPickupHeadersV2();
        return new ResponseEntity<>(pickupheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Get a PickupHeader") // label for swagger
    @GetMapping("/v2/{pickupNumber}")
    public ResponseEntity<?> getPickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                               @RequestParam String partnerCode, @RequestParam Long lineNumber, @RequestParam String itemCode) {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupHeaderV2 pickupheader =
                    pickupheaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                            lineNumber, itemCode);
            log.info("PickupHeader : " + pickupheader);
            return new ResponseEntity<>(pickupheader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/v2/findPickupHeaderStream")
    public Stream<PickupHeaderV2> findPickupHeaderV2(@RequestBody SearchPickupHeaderV2 searchPickupHeader)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPickupHeader.getCompanyCodeId(), searchPickupHeader.getPlantId(), searchPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickupheaderService.findPickupHeaderV2(searchPickupHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/v2/findPickupHeader")
    public List<PickupHeaderV2> findPickupHeaderV2New(@RequestBody SearchPickupHeaderV2 searchPickupHeader)
            throws Exception {
        try {
            log.info("SearchPickupHeaderV2 ------> {}", searchPickupHeader);
            String routingDb = null;
            DataBaseContextHolder.setCurrentDb("MT");
            Warehouse warehouseName = warehouseRepository.findTop1ByWarehouseIdAndDeletionIndicator(searchPickupHeader.getWarehouseId().get(0), 0L);
            routingDb = dbConfigRepository.getDbName(warehouseName.getCompanyCodeId(), warehouseName.getPlantId(), warehouseName.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickupheaderService.findPickupHeaderNewV2(searchPickupHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Create PickupHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPickupHeaderV2(@Valid @RequestBody PickupHeaderV2 newPickupHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException, FirebaseMessagingException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(newPickupHeader.getCompanyCodeId(), newPickupHeader.getPlantId(), newPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupHeaderV2 createdPickupHeader = pickupheaderService.createPickupHeaderV2(newPickupHeader, loginUserID);
            return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/v2/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeaderV2 updatePickupHeader,
                                                 @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException, FirebaseMessagingException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            PickupHeaderV2 createdPickupHeader =
                    pickupheaderService.updatePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                            pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader);
            return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

//    @ApiOperation(response = PickupHeaderV2.class, value = "Update Assigned PickerId in PickupHeader")    // label for swagger
//    @PatchMapping("/v2/update-assigned-picker")
//    public ResponseEntity<?> patchAssignedPickerIdInPickupHeaderV2(@RequestParam String companyCodeId, @RequestParam String warehouseId,
//                                                                   @RequestParam String plantId, @RequestParam String languageId,
//                                                                   @Valid @RequestBody List<PickupHeaderV2> updatePickupHeaderList,
//                                                                   @RequestParam("loginUserID") String loginUserID)
//            throws IllegalAccessException, InvocationTargetException {
//        List<PickupHeaderV2> createdPickupHeader =
//                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, loginUserID, updatePickupHeaderList);
//        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
//    }

    //API changed without parameters - only request body is required to update picker
    //11-03-2024 Ticket No. ALM/2024/002
    @ApiOperation(response = PickupHeaderV2.class, value = "Update Assigned PickerId in PickupHeader")
    // label for swagger
    @PatchMapping("/v2/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeaderV2(@Valid @RequestBody List<PickupHeaderV2> updatePickupHeaderList) {
//        try {
//            log.info("updatePickupHeaderList ------> {}", updatePickupHeaderList);
//            String routingDb = null;
//            for (PickupHeaderV2 updatepickupHeaderlist : updatePickupHeaderList) {
//                DataBaseContextHolder.setCurrentDb("MT");
//                routingDb = dbConfigRepository.getDbName(updatepickupHeaderlist.getCompanyCodeId(), updatepickupHeaderlist.getPlantId(), updatepickupHeaderlist.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(routingDb);
//            }
//
//            List<PickupHeaderV2> updatedPickupHeader = new ArrayList<>();
//            if (routingDb != null) {
//                switch (routingDb) {
//                    case "NAMRATHA":
//                         updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV6(updatePickupHeaderList);
//                        break;
//                    case "KNOWELL":
//                        updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList);
//                        break;
//                    case "FAHAHEEL":
//                        updatedPickupHeader =
//                                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList);
//                        break;
//                }
//            }
//            return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
//        } finally {
//            DataBaseContextHolder.clear();
//        }
        try {
            log.info("PickupHeaderV2 List -----> {}", updatePickupHeaderList);

            List<PickupHeaderV2> createdPickupHeader = updatePickupHeaderList.stream()
                    .map(item -> {
                        PickupHeaderV2 copy = new PickupHeaderV2();
                        BeanUtils.copyProperties(item, copy, CommonUtils.getNullPropertyNames(item));
                        return copy;
                    })
                    .collect(Collectors.toList());

            // Return early response
            ResponseEntity<?> response = new ResponseEntity<>(createdPickupHeader, HttpStatus.ACCEPTED);

            // Fire async processing
            asyncService.processPatchAssignedPickerIdInPickupHeaderAsync(updatePickupHeaderList);

            return response;
        } catch (Exception e) {
            log.error("Error processing PutAwayLine line async", e);
            return new ResponseEntity<>("Failed to start PutAwayLine Line process", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Delete PickupHeader") // label for swagger
    @DeleteMapping("/v2/{pickupNumber}")
    public ResponseEntity<?> deletePickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String companyCodeId,
                                                  @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                  @RequestParam String proposedPackCode, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            pickupheaderService.deletePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                    lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader with status") // label for swagger
    @PostMapping("/findPickupHeader/v2/status")
    public PickUpHeaderReport findPickupHeaderWithStatus(@RequestBody FindPickUpHeader searchPickupHeader)
            throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbList(searchPickupHeader.getCompanyCodeId(), searchPickupHeader.getPlantId(), searchPickupHeader.getWarehouseId());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            return pickupheaderService.findPickUpHeaderWithStatusId(searchPickupHeader);
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}