package com.tekclover.wms.api.transaction.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.FindPickUpHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickUpHeaderReport;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.SearchPickupHeaderV2;
import com.tekclover.wms.api.transaction.service.PickupHeaderService;
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
import java.util.stream.Stream;

@Slf4j
@Validated
@Api(tags = {"PickupHeader"}, value = "PickupHeader  Operations related to PickupHeaderController") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "PickupHeader ", description = "Operations related to PickupHeader ")})
@RequestMapping("/pickupheader")
@RestController
public class PickupHeaderController {

    @Autowired
    PickupHeaderService pickupheaderService;

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
    public List<PickupHeader> findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader) throws Exception {
        return pickupheaderService.findPickupHeader(searchPickupHeader);
    }

    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/findPickupHeaderNew")
    public Stream<PickupHeader> findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader) throws Exception {
        return pickupheaderService.findPickupHeaderNew(searchPickupHeader);
    }

    @ApiOperation(response = PickupHeader.class, value = "Create PickupHeader") // label for swagger
    @PostMapping("")
    public ResponseEntity<?> postPickupHeader(@Valid @RequestBody AddPickupHeader newPickupHeader, @RequestParam String loginUserID) throws Exception {
        PickupHeader createdPickupHeader = pickupheaderService.createPickupHeader(newPickupHeader, loginUserID);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody UpdatePickupHeader updatePickupHeader,
                                               @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeader createdPickupHeader =
                pickupheaderService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                                                       pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader")
    // label for swagger
    @PatchMapping("/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
                                                                 @RequestParam("loginUserID") String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PickupHeader> createdPickupHeader =
                pickupheaderService.patchAssignedPickerIdInPickupHeader(loginUserID, updatePickupHeaderList);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
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
        PickupHeaderV2 pickupheader =
                pickupheaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                                                      lineNumber, itemCode);
        log.info("PickupHeader : " + pickupheader);
        return new ResponseEntity<>(pickupheader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/v2/findPickupHeaderStream")
    public Stream<PickupHeaderV2> findPickupHeaderV2(@RequestBody SearchPickupHeaderV2 searchPickupHeader)
            throws Exception {
        return pickupheaderService.findPickupHeaderV2(searchPickupHeader);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/v2/findPickupHeader")
    public List<PickupHeaderV2> findPickupHeaderV2New(@RequestBody SearchPickupHeaderV2 searchPickupHeader)
            throws Exception {
        return pickupheaderService.findPickupHeaderNewV2(searchPickupHeader);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Create PickupHeader") // label for swagger
    @PostMapping("/v2")
    public ResponseEntity<?> postPickupHeaderV2(@Valid @RequestBody PickupHeaderV2 newPickupHeader, @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException, FirebaseMessagingException {
        PickupHeaderV2 createdPickupHeader = pickupheaderService.createPickupHeaderV2(newPickupHeader, loginUserID);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/v2/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeaderV2 updatePickupHeader,
                                                 @RequestParam String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException, FirebaseMessagingException {
        PickupHeaderV2 createdPickupHeader =
                pickupheaderService.updatePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                                                         pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
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
        List<PickupHeaderV2> updatedPickupHeader =
                pickupheaderService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList);
        return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
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
        pickupheaderService.deletePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                                                 lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader with status") // label for swagger
    @PostMapping("/findPickupHeader/v2/status")
    public PickUpHeaderReport findPickupHeaderWithStatus(@RequestBody FindPickUpHeader searchPickupHeader)
            throws Exception {
        return pickupheaderService.findPickUpHeaderWithStatusId(searchPickupHeader);
    }
}