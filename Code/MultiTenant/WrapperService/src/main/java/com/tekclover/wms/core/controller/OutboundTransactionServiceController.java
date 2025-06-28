package com.tekclover.wms.core.controller;


import com.tekclover.wms.core.exception.CustomErrorResponse;
import com.tekclover.wms.core.model.dto.PickupHeaderGroupByDto;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.core.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.service.CommonService;
import com.tekclover.wms.core.service.FileStorageService;
import com.tekclover.wms.core.service.OutboundTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-outboundtransaction-service")
@Api(tags = {"OutboundTransaction Service"}, value = "OutboundTransaction Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to OutboundTransaction Modules")})
public class OutboundTransactionServiceController {

    @Autowired
    OutboundTransactionService outboundTransactionService;

    @Autowired
    CommonService commonService;

    @Autowired
    FileStorageService fileStorageService;

    /*
     * --------------------------------PreOutboundHeader---------------------------------
     */
    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader") // label for swagger
    @PostMapping("/preoutboundheader/findPreOutboundHeader")
    public PreOutboundHeader[] findPreOutboundHeader(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
                                                     @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPreOutboundHeader(searchPreOutboundHeader, authToken);
    }

    //Stream
    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
    @PostMapping("/preoutboundheader/findPreOutboundHeaderNew")
    public PreOutboundHeader[] findPreOutboundHeaderNew(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
                                                        @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPreOutboundHeaderNew(searchPreOutboundHeader, authToken);
    }

    /*
     * -------------------PreOutboundLine---------------------------------------------------
     */
    @ApiOperation(response = PreOutboundLine.class, value = "Search PreOutboundLine") // label for swagger
    @PostMapping("/preoutboundline/findPreOutboundLine")
    public PreOutboundLine[] findPreOutboundLine(@RequestBody SearchPreOutboundLine searchPreOutboundLine,
                                                 @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPreOutboundLine(searchPreOutboundLine, authToken);
    }

    /*
     * --------------------------------OrderMangementLine---------------------------------
     */
    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine") // label for swagger
    @PostMapping("/ordermanagementline/findOrderManagementLine")
    public OrderManagementLine[] findOrderManagementLine(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
                                                         @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOrderManagementLine(searchOrderMangementLine, authToken);
    }

    //Streaming
    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine New") // label for swagger
    @PostMapping("/ordermanagementline/findOrderManagementLineNew")
    public OrderManagementLine[] findOrderManagementLineNew(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
                                                            @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOrderManagementLineNew(searchOrderMangementLine, authToken);
    }

//    @ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
//    @PatchMapping("/ordermanagementline/unallocate")
//    public ResponseEntity<?> patchUnallocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
//                                             @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
//                                             @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
//                                             @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        OrderManagementLine updatedOrderManagementLine =
//                outboundTransactionService.doUnAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
//                        itemCode, proposedStorageBin, proposedPackBarCode, loginUserID, authToken);
//        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
//    }

//    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
//    @PatchMapping("/ordermanagementline/allocate")
//    public ResponseEntity<?> patchAllocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
//                                           @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
//                                           @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        OrderManagementLine updatedOrderManagementLine =
//                outboundTransactionService.doAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
//                        itemCode, loginUserID, authToken);
//        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
//    }

//    @ApiOperation(response = OrderManagementLine.class, value = "Assign Picker") // label for swagger
//    @PatchMapping("/ordermanagementline/assignPicker")
//    public ResponseEntity<?> assignPicker(@RequestBody List<AssignPicker> assignPicker,
//                                          @RequestParam String assignedPickerId, @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        OrderManagementLine[] updatedOrderManagementLine =
//                outboundTransactionService.doAssignPicker(assignPicker, assignedPickerId, loginUserID, authToken);
//        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
//    }

    @ApiOperation(response = OrderManagementLine.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/ordermanagementline/{refDocNumber}")
    public ResponseEntity<?> patchOrderMangementLine(@PathVariable String refDocNumber,
                                                     @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                     @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                     @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
                                                     @Valid @RequestBody OrderManagementLine updateOrderMangementLine, @RequestParam String loginUserID,
                                                     @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine createdOrderMangementLine =
                outboundTransactionService.updateOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode,
                        loginUserID, updateOrderMangementLine, authToken);
        return new ResponseEntity<>(createdOrderMangementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Delete OrderManagementLine") // label for swagger
    @DeleteMapping("/ordermanagementline/{refDocNumber}")
    public ResponseEntity<?> deleteOrderManagementLine(@PathVariable String refDocNumber,
                                                       @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                       @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                       @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                       @RequestParam String proposedPackCode, @RequestParam String loginUserID,
                                                       @RequestParam String authToken) {
        outboundTransactionService.deleteOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger
    @GetMapping("/ordermanagementline/updateRefFields")
    public ResponseEntity<?> updateRefFields(@RequestParam String authToken) throws Exception {
        outboundTransactionService.updateRef9ANDRef10(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * -------------------------PickupHeader----------------------------------------------------
     */
    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
    @PostMapping("/pickupheader/findPickupHeader")
    public PickupHeader[] findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader,
                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupHeader(searchPickupHeader, authToken);
    }

    @ApiOperation(response = PickupHeaderGroupByDto.class, value = "Search PickupHeader") // label for swagger
    @PostMapping("/pickupheader/groupby/findPickupHeader")
    public PickupHeaderGroupByDto[] findPickupHeaderNamratha(@RequestBody FindPickupHeaderNamratha searchPickupHeader,
                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupHeaderNamratha(searchPickupHeader, authToken);
    }

    /*
     * -------------------------PickupHeader New Stream-----------------------------------------------
     */
    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/pickupheader/findPickupHeaderNew")
    public PickupHeader[] findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader,
                                              @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupHeaderNew(searchPickupHeader, authToken);
    }


    @ApiOperation(response = PickUpHeaderReport.class, value = "Search PickupHeader With StatusId") // label for swagger
    @PostMapping("/pickupheader/findPickupHeader/status")
    public PickUpHeaderReport findPickUpHeaderWithStatus(@RequestBody FindPickUpHeader searchPickupHeader,
                                                         @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickUpHeaderWithStatusId(searchPickupHeader, authToken);
    }

    @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/pickupheader/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeader updatePickupHeader,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeader createdPickupHeader =
                outboundTransactionService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader, authToken);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

//    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader")
//    // label for swagger // label for swagger
//    @PatchMapping("/pickupheader/update-assigned-picker")
//    public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
//                                                                 @RequestParam("loginUserID") String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PickupHeader[] updatedPickupHeader =
//                outboundTransactionService.patchAssignedPickerIdInPickupHeader(loginUserID, updatePickupHeaderList, authToken);
//        return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
//    }

    @ApiOperation(response = PickupHeader.class, value = "Delete PickupHeader") // label for swagger
    @DeleteMapping("/pickupheader/{pickupNumber}")
    public ResponseEntity<?> deletePickupHeader(@PathVariable String pickupNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                @RequestParam String proposedPackCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deletePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -------------------------PickupLine----------------------------------------------------
     */
    @ApiOperation(response = Inventory[].class, value = "Get AdditionalBins") // label for swagger
    @GetMapping("/pickupline/additionalBins")
    public ResponseEntity<?> getAdditionalBins(@RequestParam String warehouseId, @RequestParam String itemCode,
                                               @RequestParam Long obOrdertypeId, @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin,
                                               @RequestParam String authToken) {
        Inventory[] additionalBins = outboundTransactionService.getAdditionalBins(warehouseId, itemCode, obOrdertypeId,
                proposedPackBarCode, proposedStorageBin, authToken);
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

//    @ApiOperation(response = PickupLine.class, value = "Create PickupLine") // label for swagger
//    @PostMapping("/pickupline")
//    public ResponseEntity<?> postPickupLine(@Valid @RequestBody List<AddPickupLine> newPickupLine,
//                                            @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PickupLine[] createdPickupLine = outboundTransactionService.createPickupLine(newPickupLine, loginUserID, authToken);
//        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
//    }

    @ApiOperation(response = PickupLine.class, value = "Search PickupLine") // label for swagger
    @PostMapping("/pickupline/findPickupLine")
    public PickupLine[] findPickupLine(@RequestBody SearchPickupLine searchPickupLine,
                                       @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupLine(searchPickupLine, authToken);
    }

    @ApiOperation(response = PickupLine.class, value = "Update PickupLine") // label for swagger
    @PatchMapping("/pickupline/{actualHeNo}")
    public ResponseEntity<?> patchPickupLine(@PathVariable String actualHeNo, @RequestParam String warehouseId,
                                             @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                             @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                             @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                             @Valid @RequestBody PickupLine updatePickupLine, @RequestParam String loginUserID,
                                             @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine createdPickupLine =
                outboundTransactionService.updatePickupLine(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
                        actualHeNo, loginUserID, updatePickupLine, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Delete PickupLine") // label for swagger
    @DeleteMapping("/pickupline/{actualHeNo}")
    public ResponseEntity<?> deletePickupLine(@PathVariable String actualHeNo,
                                              @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                              @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                              @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deletePickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ------------------------QualityHeader--------------------------------------------------------
     */

    @ApiOperation(response = QualityHeader.class, value = "Create QualityHeader") // label for swagger
    @PostMapping("/qualityheader")
    public ResponseEntity<?> postQualityHeader(@Valid @RequestBody QualityHeader newQualityHeader,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        QualityHeader createdQualityHeader = outboundTransactionService.createQualityHeader(newQualityHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader") // label for swagger
    @PostMapping("/qualityheader/findQualityHeader")
    public QualityHeader[] findQualityHeader(@RequestBody SearchQualityHeader searchQualityHeader,
                                             @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findQualityHeader(searchQualityHeader, authToken);
    }

    //Stream
    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader New") // label for swagger
    @PostMapping("/qualityheader/findQualityHeaderNew")
    public QualityHeader[] findQualityHeaderNew(@RequestBody SearchQualityHeader searchQualityHeader,
                                                @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findQualityHeaderNew(searchQualityHeader, authToken);
    }

    @ApiOperation(response = QualityHeader.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/qualityheader/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeader(@PathVariable String qualityInspectionNo,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                @Valid @RequestBody QualityHeader updateQualityHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityHeader updatedQualityHeader = outboundTransactionService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader, authToken);
        return new ResponseEntity<>(updatedQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Delete QualityHeader") // label for swagger
    @DeleteMapping("/qualityheader/{qualityInspectionNo}")
    public ResponseEntity<?> deleteQualityHeader(@PathVariable String qualityInspectionNo,
                                                 @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deleteQualityHeader(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ------------------------QualityLine-----------------------------------------------------------
     */
    @ApiOperation(response = QualityLine.class, value = "Search QualityLine") // label for swagger
    @PostMapping("/qualityline/findQualityLine")
    public QualityLine[] findQualityLine(@RequestBody SearchQualityLine searchQualityLine,
                                         @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findQualityLine(searchQualityLine, authToken);
    }

//    @ApiOperation(response = QualityLine.class, value = "Create QualityLine") // label for swagger
//    @PostMapping("/qualityline")
//    public ResponseEntity<?> postQualityLine(@Valid @RequestBody List<AddQualityLine> newQualityLine,
//                                             @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        QualityLine[] createdQualityLine = outboundTransactionService.createQualityLine(newQualityLine, loginUserID, authToken);
//        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
//    }

    @ApiOperation(response = QualityLine.class, value = "Update QualityLine") // label for swagger
    @PatchMapping("/qualityline/{partnerCode}")
    public ResponseEntity<?> patchQualityLine(@PathVariable String partnerCode,
                                              @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam Long lineNumber,
                                              @RequestParam String qualityInspectionNo, @RequestParam String itemCode,
                                              @Valid @RequestBody QualityLine updateQualityLine, @RequestParam String loginUserID,
                                              @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityLine createdQualityLine =
                outboundTransactionService.updateQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, qualityInspectionNo, itemCode, loginUserID, updateQualityLine, authToken);
        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = QualityLine.class, value = "Delete QualityLine") // label for swagger
    @DeleteMapping("/qualityline/{partnerCode}")
    public ResponseEntity<?> deleteQualityLine(@PathVariable String partnerCode,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam Long lineNumber,
                                               @RequestParam String qualityInspectionNo, @RequestParam String itemCode,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deleteQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ---------------------------OutboundHeader--------------------------------------------------------
     */
//    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
//    @PostMapping("/outboundheader/findOutboundHeader")
//    public OutboundHeader[] findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader,
//                                               @RequestParam String authToken) throws Exception {
//        return outboundTransactionService.findOutboundHeader(searchOutboundHeader, authToken);
//    }

    //Stream
    @ApiOperation(response = PeriodicHeaderV2[].class, value = "Search PeriodicHeader New V2") // label for swagger
    @PostMapping("/periodicheader/v2/findPeriodicHeaderNew")
    public ResponseEntity<?> findPeriodicHeaderNewV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                     @RequestParam String authToken) throws Exception {
        PeriodicHeaderV2[] results = outboundTransactionService.findPeriodicHeaderStreamV2(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader V2") // label for swagger
    @PostMapping("/periodicheader/v2")
    public ResponseEntity<?> postPeriodicHeaderV2(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity createdPeriodicHeader = outboundTransactionService.createPeriodicHeaderV2(newPeriodicHeader, loginUserID, authToken);
        log.info("createdPeriodicHeader:" + createdPeriodicHeader);

        /* Call Batch */
//		transactionService.createCSV(newPeriodicHeader.getPeriodicLine());
//		batchJobScheduler.runJobPeriodic();
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader V2") // label for swagger
    @PatchMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                   @RequestParam Long cycleCountTypeId, @RequestBody PeriodicHeaderEntityV2 updatePeriodicHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicHeaderV2 createdPeriodicHeader =
                outboundTransactionService.updatePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        loginUserID, updatePeriodicHeader, authToken);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader v2") // label for swagger
    @DeleteMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                    @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deletePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLine V2") // label for swagger
    @PostMapping("/periodicline/v2/findPeriodicLine")
    public PeriodicLineV2[] findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLine,
                                               @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPeriodicLineV2(searchPeriodicLine, authToken);
    }

    @ApiOperation(response = PeriodicLineV2[].class, value = "Update AssignHHTUser V2") // label for swagger
    @PatchMapping("/periodicline/v2/assigingHHTUser")
    public ResponseEntity<?> assigingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicLineV2[] createdPeriodicLine =
                outboundTransactionService.updatePeriodicLineAssingHHTUserV2(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine V2") // label for swagger
    @PatchMapping("/periodicline/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
                                                 @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicUpdateResponseV2 updatedPeriodicLine =
                outboundTransactionService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
    }

//    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine confirm V2") // label for swagger
//    @PatchMapping("/periodicline/v2/confirm/{cycleCountNo}")
//    public ResponseEntity<?> patchPeriodicLineConfirmV2(@PathVariable String cycleCountNo,
//                                                        @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID,
//                                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        WarehouseApiResponse createPeriodicLine =
//                outboundTransactionService.updatePeriodicLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
//        return new ResponseEntity<>(createPeriodicLine, HttpStatus.OK);
//    }

    //-----------------------------------------------------------StockAdjustment---------------------------------------------------------

    @ApiOperation(response = StockAdjustment.class, value = "Get a StockAdjustment") // label for swagger
    @GetMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> getStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                @RequestParam String manufacturerName, @RequestParam String storageBin,
                                                @RequestParam String authToken) throws Exception {
        StockAdjustment[] stockAdjustment =
                outboundTransactionService.getStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey,
                        itemCode, manufacturerName, storageBin, authToken);
        log.info("StockAdjustment : " + stockAdjustment);
        return new ResponseEntity<>(stockAdjustment, HttpStatus.OK);
    }

    @ApiOperation(response = StockAdjustment[].class, value = "Search StockAdjustment V2") // label for swagger
    @PostMapping("/stockAdjustment/findStockAdjustment")
    public StockAdjustment[] findStockAdjustment(@RequestBody SearchStockAdjustment searchInventory,
                                                 @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findStockAdjustment(searchInventory, authToken);
    }

    @ApiOperation(response = StockAdjustment.class, value = "Update StockAdjustment V2") // label for swagger
    @PatchMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> patchStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                  @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                  @RequestParam String manufacturerName, @RequestParam String loginUserID,
                                                  @RequestBody List<StockAdjustment> updateStockAdjustment, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        StockAdjustment[] updatedStockAdjustment =
                outboundTransactionService.updateStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey, itemCode,
                        manufacturerName, loginUserID, updateStockAdjustment, authToken);
        return new ResponseEntity<>(updatedStockAdjustment, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Delete StockAdjustment V2") // label for swagger
    @DeleteMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> deleteStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                   @RequestParam String manufacturerName, @RequestParam String storageBin,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deleteStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey, itemCode, manufacturerName, storageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //findPickListHeader - Pick List Cancellation
    @ApiOperation(response = PickListHeader.class, value = "Search PickListHeader Cancellation") // label for swagger
    @PostMapping("/pickListCancellation/v2/findPickListHeader")
    public PickListHeader[] findPickListHeader(@RequestBody SearchPickListHeader searchPickListHeader,
                                               @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickListHeader(searchPickListHeader, authToken);
    }

    @ApiOperation(response = Optional.class, value = "RollBack") // label for swagger
    @PatchMapping("/outbound/order/v2/rollBack")
    public ResponseEntity<?> rollBackOutbound(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                              @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                              @RequestParam Long outboundOrderTypeId, @RequestParam String authToken) throws Exception {
        outboundTransactionService.rollBackOutboundOrder(companyCodeId, plantId, languageId, warehouseId, refDocNumber, outboundOrderTypeId, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineV2[].class, value = "Create PerpetualHeader V2") // label for swagger
    @PostMapping("/perpetualheader/run")
    public ResponseEntity<?> postRunPerpetualHeaderV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                      @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] perpetualLineEntity =
                outboundTransactionService.runPerpetualHeaderV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Create PerpetualHeader Stream V2") // label for swagger
    @PostMapping("/perpetualheader/runNew")
    public ResponseEntity<?> postRunPerpetualHeaderStreamV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                            @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] perpetualLineEntity =
                outboundTransactionService.runPerpetualHeaderNewV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderV2.class, value = "Update PerpetualHeader V2") // label for swagger
    @PatchMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                    @Valid @RequestBody PerpetualHeaderEntityV2 updatePerpetualHeader, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeaderV2 createdPerpetualHeader =
                outboundTransactionService.updatePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                        subMovementTypeId, loginUserID, updatePerpetualHeader, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Delete PerpetualHeader V2") // label for swagger
    @DeleteMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                     @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deletePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                subMovementTypeId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PerpetualLine---------------------------------------------------------------------
    @ApiOperation(response = PerpetualLineV2.class, value = "SearchPerpetualLine V2") // label for swagger
    @PostMapping("/findPerpetualLine")
    public PerpetualLineV2[] findPerpetualLineV2(@RequestBody SearchPerpetualLineV2 searchPerpetualLine,
                                                 @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualLineV2(searchPerpetualLine, authToken);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Update AssignHHTUser V2") // label for swagger
    @PatchMapping("/perpetualline/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] createdPerpetualLine = outboundTransactionService.updateAssingHHTUserV2(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualUpdateResponseV2.class, value = "Update PerpetualLine V2") // label for swagger
    @PatchMapping("/perpetualline/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineV2(@PathVariable String cycleCountNo,
                                                  @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualUpdateResponseV2 createdPerpetualLine =
                outboundTransactionService.updatePerpetualLineV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PerpetualLine confirm V2") // label for swagger
    @PatchMapping("/perpetualline/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineConfirmV2(@PathVariable String cycleCountNo,
                                                         @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPerpetualLine =
                outboundTransactionService.updatePerpetualLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2.class, value = "Create PerpetualLine From ZeroStock V2") // label for swagger
    @PostMapping("/perpetualline/createPerpetualFromZeroStk")
    public ResponseEntity<?> createPerpetualFromZeroStk(@RequestBody List<PerpetualZeroStockLine> updatePerpetualLine, @RequestParam String loginUserID,
                                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] createdPerpetualLine =
                outboundTransactionService.updatePerpetualZeroStkLine(updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLineV2.class, value = "Create PeriodicLine from ZeroStock V2") // label for swagger
    @PostMapping("/periodicline/v2/createPeriodicFromZeroStk")
    public ResponseEntity<?> createPeriodicFromZeroStk(@RequestBody List<PeriodicZeroStockLine> updatePeriodicLine, @RequestParam String loginUserID,
                                                       @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicLineV2[] createdPeriodicLine =
                outboundTransactionService.updatePeriodicZeroStkLine(updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

    /*
     * -----------------------------Periodic Count----------------------------------------------------
     */
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details V2") // label for swagger
    @GetMapping("/periodicheader/v2")
    public ResponseEntity<?> getPeriodicHeadersV2(@RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] PeriodicheaderList = outboundTransactionService.getPeriodicHeadersV2(authToken);
        return new ResponseEntity<>(PeriodicheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderV2.class, value = "Get a PeriodicHeader v2") // label for swagger
    @GetMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                 @RequestParam String companyCode, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam Long cycleCountTypeId,
                                                 @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntityV2 Periodicheader =
                outboundTransactionService.getPeriodicHeaderV2(warehouseId, companyCode, plantId, languageId, cycleCountTypeId, cycleCountNo, authToken);
        return new ResponseEntity<>(Periodicheader, HttpStatus.OK);
    }

//    @ApiOperation(response = PeriodicHeaderV2[].class, value = "Search PeriodicHeader V2") // label for swagger
//    @PostMapping("/periodicheader/v2/findPeriodicHeader")
//    public PeriodicHeaderV2[] findPeriodicHeaderV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
//                                                   @RequestParam String authToken) throws Exception {
//        return transactionService.findPeriodicHeaderV2(searchPeriodicHeader, authToken);
//    }

    @ApiOperation(response = PeriodicHeaderEntityV2[].class, value = "Search PeriodicHeader V2") // label for swagger
    @PostMapping("/periodicheader/v2/findPeriodicHeader")
    public PeriodicHeaderEntityV2[] findPeriodicHeaderV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                         @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPeriodicHeaderV2(searchPeriodicHeader, authToken);
    }

//    @ApiOperation(response = PeriodicHeaderEntityV2[].class, value = "Search PeriodicHeader V6") // label for swagger
//    @PostMapping("/periodicheader/findPeriodicHeader")
//    public PeriodicHeaderEntityV2[] findPeriodicHeaderV6(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
//                                                         @RequestParam String authToken) throws Exception {
//        return outboundTransactionService.findPeriodicHeaderV6(searchPeriodicHeader, authToken);
//    }

    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine V2 stream") // label for swagger
    @PostMapping("/outboundline/v2/findOutboundLine/stream")
    public OutboundLineV2[] findOutboundLineStreamV2(@RequestBody SearchOutboundLineV2 searchOutboundLine,
                                                     @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundLineStreamV2(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine V2") // label for swagger
    @GetMapping("/outboundline/delivery/confirmation")
    public ResponseEntity<?> deliveryConfirmationV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                    @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLineV2[] createdOutboundLine =
                outboundTransactionService.deliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine V2") // label for swagger
    @PatchMapping("/outboundline/v2/{lineNumber}")
    public ResponseEntity<?> patchOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                                 @Valid @RequestBody OutboundLineV2 updateOutboundLine, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLineV2 updatedOutboundLine =
                outboundTransactionService.updateOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, itemCode, loginUserID, updateOutboundLine, authToken);
        return new ResponseEntity<>(updatedOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Delete OutboundLine V2") // label for swagger
    @DeleteMapping("/outboundline/v2/{lineNumber}")
    public ResponseEntity<?> deleteOutboundLineV2(@PathVariable Long lineNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deleteOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------OutboundReversal-----------------------------------------------
     */
    @ApiOperation(response = OutboundReversalV2.class, value = "Search OutboundReversal V2") // label for swagger
    @PostMapping("/outboundreversal/v2/findOutboundReversal")
    public OutboundReversalV2[] findOutboundReversalV2(@RequestBody SearchOutboundReversalV2 searchOutboundReversal,
                                                       @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundReversalV2(searchOutboundReversal, authToken);
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Get Delivery Lines V2") // label for swagger
    @GetMapping("/outboundreversal/v2/reversal/new")
    public ResponseEntity<?> doReversalV2(@RequestParam String refDocNumber, @RequestParam String itemCode, @RequestParam String manufacturerName,
                                          @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversalV2[] deliveryLines = outboundTransactionService.doReversalV2(refDocNumber, itemCode, manufacturerName, loginUserID, authToken);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    /*--------------------Shipping Reversal Batch-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Batch Reversal Delivery Lines V2") // label for swagger
    @PostMapping("/outboundreversal/v2/reversal/batch")
    public ResponseEntity<?> doReversalBatchV2(@RequestBody List<InboundReversalInput> outboundReversalInput,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversalV2[] deliveryLines = outboundTransactionService.doReversalBatchV2(outboundReversalInput, loginUserID, authToken);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    //----------------------Orders-V2-----------------------------------------------------------------

    //ShipmentOrder V2
    @ApiOperation(response = ShipmentOrderV2.class, value = "Create Shipment Order V2") // label for swagger
    @PostMapping("/warehouse/v2/outbound/so")
    public ResponseEntity<?> postShipmenOrderV2(@Valid @RequestBody ShipmentOrderV2 shipmenOrder, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = outboundTransactionService.postSOV2(shipmenOrder, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }


    //SalesOrder V2
    @ApiOperation(response = SalesOrderV2.class, value = "Create SalesOrderV2") // label for swagger
    @PostMapping("/warehouse/outbound/salesorder/v2")
    public ResponseEntity<?> createSalesOrderV2(@Valid @RequestBody SalesOrderV2 salesOrderV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        try {
            WarehouseApiResponse createdSoV2 = outboundTransactionService.postSalesOrderV2(salesOrderV2, authToken);
            return new ResponseEntity<>(createdSoV2, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while SalesOrder Create : " + e.toString());
            CustomErrorResponse customErrorResponse = new CustomErrorResponse();
            String[] err = commonService.removeUnwantedString(e.getLocalizedMessage().toString());
            if(err.length > 1) {
                customErrorResponse.setError(err[0]);
                customErrorResponse.setErrorDescription(err[1]);
            } else {
                customErrorResponse.setError(err[0]);
            }
            customErrorResponse.setStatus(400);
            customErrorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.OK);
        }
    }

    //SalesOrder V2
    @ApiOperation(response = SalesOrderV2.class, value = "Create SalesOrderV2") // label for swagger
    @PostMapping("/warehouse/outbound/salesorder/v4")
    public ResponseEntity<?> createSalesOrderV4(@Valid @RequestBody SalesOrderV2 salesOrderV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        try {
            SalesOrderV2 createdSoV2 = outboundTransactionService.postSalesOrderV4(salesOrderV2);
            return new ResponseEntity<>(createdSoV2, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while SalesOrder Create : " + e.toString());
            CustomErrorResponse customErrorResponse = new CustomErrorResponse();
            String[] err = commonService.removeUnwantedString(e.getLocalizedMessage().toString());
            if(err.length > 1) {
                customErrorResponse.setError(err[0]);
                customErrorResponse.setErrorDescription(err[1]);
            } else {
                customErrorResponse.setError(err[0]);
            }
            customErrorResponse.setStatus(400);
            customErrorResponse.setTimestamp(LocalDateTime.now());
            return new ResponseEntity<>(customErrorResponse, HttpStatus.OK);
        }
    }

    //ReturnPOV2
    @ApiOperation(response = ReturnPOV2.class, value = "Create ReturnPOV2") //label for Swagger
    @PostMapping("/warehouse/outbound/returnpo/v2")
    public ResponseEntity<?> createReturnPoV2(@Valid @RequestBody ReturnPOV2 returnPOV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdRetPoV2 = outboundTransactionService.postReturnPoV2(returnPOV2, authToken);
        return new ResponseEntity<>(createdRetPoV2, HttpStatus.OK);
    }


    //InterWarehouseTransferOutV2
    @ApiOperation(response = InterWarehouseTransferOutV2.class, value = "Create InterWarehouseTransferOutV2")
    @PostMapping("/warehouse/outbound/interwarehousetransferout/v2")
    public ResponseEntity<?> createIWhTransferOutV2(@Valid @RequestBody InterWarehouseTransferOutV2 iWhTransferOutV2,
                                                    @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdIWhTransferOutV2 = outboundTransactionService.postInterWhTransferOutV2(iWhTransferOutV2, authToken);
        return new ResponseEntity<>(createdIWhTransferOutV2, HttpStatus.OK);
    }


    //SalesInvoice
    @ApiOperation(response = SalesInvoice.class, value = "Create SalesInvoice") //label for Swagger
    @PostMapping("/warehouse/outbound/salesinvoice")
    public ResponseEntity<?> createSalesInvoice(@Valid @RequestBody SalesInvoice salesInvoice, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSI = outboundTransactionService.postSalesInvoice(salesInvoice, authToken);
        return new ResponseEntity<>(createdSI, HttpStatus.OK);
    }

    /*
     * -----------------------------Perpetual Count----------------------------------------------------
     */
    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get all PerpetualHeader details V2")
    // label for swagger
    @GetMapping("/perpetualheader")
    public ResponseEntity<?> getPerpetualHeadersV2(@RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2[] perpetualheaderList = outboundTransactionService.getPerpetualHeadersV2(authToken);
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get a PerpetualHeader V2") // label for swagger
    @GetMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                  @RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2 perpetualheader =
                outboundTransactionService.getPerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId, authToken);
        log.info("PerpetualHeader : " + perpetualheader);
        return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
    }

    //    @ApiOperation(response = PerpetualHeaderV2[].class, value = "Search PerpetualHeader V2") // label for swagger
//    @PostMapping("/perpetualheader/v2/findPerpetualHeader")
//    public PerpetualHeaderV2[] findPerpetualHeaderV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
//                                                     @RequestParam String authToken) throws Exception {
//        return transactionService.findPerpetualHeaderV2(searchPerpetualHeader, authToken);
//    }
    @ApiOperation(response = PerpetualHeaderEntityV2[].class, value = "Search PerpetualHeader V2") // label for swagger
    @PostMapping("/perpetualheader/findPerpetualHeader")
    public PerpetualHeaderEntityV2[] findPerpetualHeaderV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualHeaderV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderV2[].class, value = "Search PerpetualHeader New V2") // label for swagger
    @PostMapping("/perpetualheader/findPerpetualHeaderNew")
    public PerpetualHeaderV2[] findPerpetualHeaderNewV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                        @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualHeaderEntityV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Create PerpetualHeader V2") // label for swagger
    @PostMapping("/perpetualheader")
    public ResponseEntity<?> postPerpetualHeaderV2(@Valid @RequestBody PerpetualHeaderEntityV2 newPerpetualHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeaderEntityV2 createdPerpetualHeader =
                outboundTransactionService.createPerpetualHeaderV2(newPerpetualHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    //
    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader New") // label for swagger
    @PostMapping("/outboundheader/findOutboundHeaderNew")
    public OutboundHeader[] findOutboundHeaderNew(@RequestBody SearchOutboundHeader searchOutboundHeader,
                                                  @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundHeaderNew(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/outboundheader/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo,
                                                 @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @Valid @RequestBody OutboundHeader updateOutboundHeader, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundHeader createdOutboundHeader =
                outboundTransactionService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Update PreOutboundHeader") // label for swagger
    @PatchMapping("/preoutboundheader/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String languageId, @RequestParam String companyCodeId,
                                                 @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                 @RequestParam String partnerCode,@Valid @RequestBody PreOutboundHeaderV2 updatePreOutboundHeader,
                                                 @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PreOutboundHeaderV2 createdOutboundHeader =
                outboundTransactionService.updatePreOutboundHeader(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
                        partnerCode, loginUserID, updatePreOutboundHeader, authToken);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }


    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader") // label for swagger
    @DeleteMapping("/outboundheader/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo,
                                                  @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deleteOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ----------------------OutboundLine----------------------------------------------------------
     */
//    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
//    @PostMapping("/outboundline/findOutboundLine")
//    public OutboundLine[] findOutboundLine(@RequestBody SearchOutboundLine searchOutboundLine,
//                                           @RequestParam String authToken) throws Exception {
//        return outboundTransactionService.findOutboundLine(searchOutboundLine, authToken);
//    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/outboundline/findOutboundLine-new")
    public OutboundLine[] findOutboundLineNew(@RequestBody SearchOutboundLine searchOutboundLine,
                                              @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundLineNew(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report")
    // label for swagger
    @PostMapping("/outboundline/stock-movement-report/findOutboundLine")
    public StockMovementReport[] findOutboundLineForStockMovement(@RequestBody SearchOutboundLine searchOutboundLine, @RequestParam String authToken)
            throws Exception {
        return outboundTransactionService.findOutboundLineForStockMovement(searchOutboundLine, authToken);
    }

    @ApiOperation(response = StockMovementReport.class, value = "Search OutboundLine for Stock movement report V2")
    // label for swagger
    @PostMapping("/outboundline/stock-movement-report/v2/findOutboundLine")
    public StockMovementReport[] findOutboundLineForStockMovementV2(@RequestBody SearchOutboundLineV2 searchOutboundLine, @RequestParam String authToken)
            throws Exception {
        return outboundTransactionService.findOutboundLineForStockMovementV2(searchOutboundLine, authToken);
    }

    /*
     * --------------------------------Get all StockMovementReport---------------------------------------------------------------
     */
    @ApiOperation(response = StockMovementReport.class, value = "Get all StockMovementReport details")
    // label for swagger
    @GetMapping("/outboundline/stock-movement-report")
    public ResponseEntity<?> getStockMovementReports(@RequestParam String authToken) throws Exception {
        StockMovementReport[] stockMovementReportList = outboundTransactionService.getStockMovementReports(authToken);
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

    /*
     * Inventory Stock movement report renamed to Transaction History report
     */
    @ApiOperation(response = Optional.class, value = "Get transaction History Report")
    @PostMapping("/reports/transactionHistoryReport")
    public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 findImBasicData1,
                                                         @RequestParam String authToken) throws java.text.ParseException {
        InventoryStockReport[] inventoryReportList = outboundTransactionService.getTransactionHistoryReport(findImBasicData1, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

//    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
//    @GetMapping("/outboundline/delivery/confirmation")
//    public ResponseEntity<?> deliveryConfirmation(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
//                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID,
//                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        OutboundLine[] createdOutboundLine =
//                outboundTransactionService.deliveryConfirmation(warehouseId, preOutboundNo, refDocNumber,
//                        partnerCode, loginUserID, authToken);
//        return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
//    }

    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/outboundline/{lineNumber}")
    public ResponseEntity<?> patchOutboundLine(@PathVariable Long lineNumber,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                               @Valid @RequestBody OutboundLine updateOutboundLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLine updatedOutboundLine =
                outboundTransactionService.updateOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, itemCode, loginUserID, updateOutboundLine, authToken);
        return new ResponseEntity<>(updatedOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Delete OutboundLine") // label for swagger
    @DeleteMapping("/outboundline/{lineNumber}")
    public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID,
                                                @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deleteOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------OutboundReversal-----------------------------------------------
     */
    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal") // label for swagger
    @PostMapping("/outboundreversal/findOutboundReversal")
    public OutboundReversal[] findOutboundReversal(@RequestBody SearchOutboundReversal searchOutboundReversal,
                                                   @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundReversal(searchOutboundReversal, authToken);
    }

    //Stream
    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal New") // label for swagger
    @PostMapping("/outboundreversal/findOutboundReversalNew")
    public OutboundReversal[] findOutboundReversalNew(@RequestBody SearchOutboundReversal searchOutboundReversal,
                                                      @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundReversalNew(searchOutboundReversal, authToken);
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLine.class, value = "Get Delivery Lines") // label for swagger
    @GetMapping("/outboundreversal/reversal/new")
    public ResponseEntity<?> doReversal(@RequestParam String refDocNumber, @RequestParam String itemCode,
                                        @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversal[] deliveryLines = outboundTransactionService.doReversal(refDocNumber, itemCode, loginUserID, authToken);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    /*
     * ----------------------------Reports-----------------------------------------------------------
     */
    @ApiOperation(response = StockReport.class, value = "Get StockReport") // label for swagger
    @GetMapping("/reports/stockReport")
    public ResponseEntity<?> getStockReport(@RequestParam List<String> warehouseId,
                                            @RequestParam(required = false) List<String> itemCode,
                                            @RequestParam(required = false) String itemText,
                                            @RequestParam String stockTypeText,
                                            @RequestParam(defaultValue = "0") Integer pageNo,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @RequestParam(defaultValue = "itemCode") String sortBy,
                                            @RequestParam String authToken) {
        PaginatedResponse<StockReport> stockReport =
                outboundTransactionService.getStockReports(warehouseId, itemCode, itemText, stockTypeText,
                        pageNo, pageSize, sortBy, authToken);
        return new ResponseEntity<>(stockReport, HttpStatus.OK);
    }

    // Get All Stock Reports
    @ApiOperation(response = StockReport.class, value = "Get All Stock Reports") // label for swagger
    @GetMapping("/reports/stockReport-all")
    public ResponseEntity<?> getAllStockReport(@RequestParam List<String> languageId,
                                               @RequestParam List<String> companyCodeId,
                                               @RequestParam List<String> plantId,
                                               @RequestParam List<String> warehouseId,
                                               @RequestParam(required = false) List<String> itemCode,
                                               @RequestParam(required = false) List<String> manufacturerName,
                                               @RequestParam(required = false) String itemText,
                                               @RequestParam(required = true) String stockTypeText, @RequestParam String authToken) {
        StockReport[] stockReportList = outboundTransactionService.getAllStockReports(languageId, companyCodeId, plantId,
                warehouseId, itemCode, manufacturerName, itemText, stockTypeText, authToken);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get All Stock Reports
    @ApiOperation(response = StockReport.class, value = "Get All Stock Reports v2") // label for swagger
    @PostMapping("/reports/v2/stockReport-all")
    public ResponseEntity<?> getAllStockReportV2(@Valid @RequestBody SearchStockReport searchStockReport, @RequestParam String authToken) {
        StockReport[] stockReportList = outboundTransactionService.getAllStockReportsV2(searchStockReport, authToken);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get All Stock Reports - StoredProcedure
    @ApiOperation(response = StockReportOutput.class, value = "Get All Stock Reports v2 Stored Procedure") // label for swagger
    @PostMapping("/reports/v2/stockReportSP")
    public ResponseEntity<?> getAllStockReportV2SP(@Valid @RequestBody SearchStockReportInput searchStockReport, @RequestParam String authToken) {
        StockReportOutput[] stockReportList = outboundTransactionService.getAllStockReportsV2SP(searchStockReport, authToken);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }


    /*
     * Inventory Report
     */
    @ApiOperation(response = InventoryReport.class, value = "Get Inventory Report") // label for swagger
    @GetMapping("/reports/inventoryReport")
    public ResponseEntity<?> getInventoryReport(@RequestParam List<String> warehouseId,
                                                @RequestParam(required = false) List<String> itemCode,
                                                @RequestParam(required = false) String storageBin,
                                                @RequestParam(required = false) String stockTypeText,
                                                @RequestParam(required = false) List<String> stSectionIds,
                                                @RequestParam(defaultValue = "0") Integer pageNo,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(defaultValue = "itemCode") String sortBy,
                                                @RequestParam String authToken) {
        PaginatedResponse<InventoryReport> inventoryReportList =
                outboundTransactionService.getInventoryReport(warehouseId, itemCode, storageBin, stockTypeText, stSectionIds,
                        pageNo, pageSize, sortBy, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    /*
     * Stock movement report
     */
    @ApiOperation(response = StockMovementReport.class, value = "Get StockMovement Report") // label for swagger
    @GetMapping("/reports/stockMovementReport")
    public ResponseEntity<?> getStockMovementReport(@RequestParam String warehouseId,
                                                    @RequestParam String itemCode, @RequestParam String fromCreatedOn,
                                                    @RequestParam String toCreatedOn, @RequestParam String authToken) throws Exception {
        StockMovementReport[] inventoryReportList =
                outboundTransactionService.getStockMovementReport(warehouseId, itemCode, fromCreatedOn, toCreatedOn, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    /*
     * Order status report
     */
    @ApiOperation(response = OrderStatusReport.class, value = "Get OrderStatus Report") // label for swagger
    @PostMapping("/reports/orderStatusReport")
    public ResponseEntity<?> getOrderStatusReport(@RequestBody @Valid SearchOrderStatusReport request,
                                                  @RequestParam String authToken) throws ParseException, Exception {
        OrderStatusReport[] orderStatusReportList = outboundTransactionService.getOrderStatusReport(request, authToken);
        return new ResponseEntity<>(orderStatusReportList, HttpStatus.OK);
    }

    /*
     * Shipment Delivery
     */
    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report") // label for swagger
    @GetMapping("/reports/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String companyCodeId, @RequestParam String plantId,@RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber,
                                                       @RequestParam String authToken)
            throws ParseException, Exception {
        ShipmentDeliveryReport[] shipmentDeliveryList = outboundTransactionService.getShipmentDeliveryReport(companyCodeId, plantId,warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, authToken);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2")    // label for swagger
    @GetMapping("/reports/v2/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber,
                                                       @RequestParam String authToken)
            throws ParseException, Exception {
        ShipmentDeliveryReport[] shipmentDeliveryList = outboundTransactionService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, authToken);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2 New")    // label for swagger
    @GetMapping("/reports/shipmentDelivery/new")
    public ResponseEntity<?> getShipmentDeliveryReportV2(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String warehouseId,
                                                         @RequestParam(required = false) String fromDeliveryDate,
                                                         @RequestParam(required = false) String toDeliveryDate,
                                                         @RequestParam(required = false) String storeCode,
                                                         @RequestParam(required = false) List<String> soType,
                                                         @RequestParam String orderNumber, @RequestParam String preOutboundNo,
                                                         @RequestParam String authToken)
            throws ParseException, Exception {
        ShipmentDeliveryReport[] shipmentDeliveryList = outboundTransactionService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, preOutboundNo, authToken);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    @ApiOperation(response = ShipmentDeliverySummaryReport.class, value = "Get ShipmentDeliverySummary Report")    // label for swagger
    @GetMapping("/reports/shipmentDeliverySummary1")
    public ResponseEntity<?> getShipmentDeliveryReport1(@RequestParam String fromDeliveryDate,
                                                        @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode,
                                                        @RequestParam String authToken, @RequestParam(required = true) String warehouseId,
                                                        @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId)
            throws ParseException, Exception {
        ShipmentDeliverySummaryReport shipmentDeliverySummaryReport =
                outboundTransactionService.getShipmentDeliverySummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, companyCodeId, plantId, languageId, authToken);
        return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
    }

    /*
     * Inventory Stock movement report
     */
    @ApiOperation(response = Optional.class, value = "Get Opening Stock Report")
    @PostMapping("/reports/openingStockReport")
    public ResponseEntity<?> getInventoryStockReport(@RequestBody FindImBasicData1 findImBasicData1,
                                                     @RequestParam String authToken) throws java.text.ParseException {
        InventoryStockReport[] inventoryReportList = outboundTransactionService.getInventoryStockReport(findImBasicData1, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /*
     * Shipment Dispatch Summary
     */
    @ApiOperation(response = ShipmentDispatchSummaryReport.class, value = "Get ShipmentDispatchSummary Report")
    // label for swagger
    @GetMapping("/shipmentDispatchSummary")
    public ResponseEntity<?> getShipmentDispatchSummaryReport(@RequestParam String fromDeliveryDate,
                                                              @RequestParam String toDeliveryDate, @RequestParam(required = false) List<String> customerCode,
                                                              @RequestParam String authToken, @RequestParam(required = true) String warehouseId) throws ParseException, Exception {
        ShipmentDispatchSummaryReport shipmentDispatchSummary =
                outboundTransactionService.getShipmentDispatchSummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, authToken);
        return new ResponseEntity<>(shipmentDispatchSummary, HttpStatus.OK);
    }

    /*
     * Receipt Confirmation
     */
    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")
    // label for swagger
    @GetMapping("/reports/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReport(@RequestParam String asnNumber,
                                                         @RequestParam String authToken) throws Exception {
        ReceiptConfimationReport receiptConfimationReport = outboundTransactionService.getReceiptConfimationReport(asnNumber, authToken);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/reports/v2/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReportNew(@RequestParam String asnNumber, @RequestParam String preInboundNo, @RequestParam String companyCodeId,
                                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                            @RequestParam String authToken) throws Exception {
        ReceiptConfimationReport receiptConfimationReport = outboundTransactionService.getReceiptConfimationReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId, authToken);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    /*
     * MobileDashboard
     */
    @ApiOperation(response = MobileDashboard.class, value = "Get MobileDashboard Report") // label for swagger
    @GetMapping("/reports/dashboard/mobile")
    public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId, @RequestParam String loginUserID,
                                                @RequestParam String authToken) throws Exception {
        MobileDashboard dashboard = outboundTransactionService.getMobileDashboard(companyCode, plantId, languageId, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }



    ///*
    //     * MobileDashboard
    //     */
    //    @ApiOperation(response = MobileDashboard.class, value = "Get MobileDashboard Report") // label for swagger
    //    @GetMapping("/reports/dashboard/mobile")
    //    public ResponseEntity<?> getMobileDashboard(@RequestParam String warehouseId,
    //                                                @RequestParam String authToken) throws Exception {
    //        MobileDashboard dashboard = transactionService.getMobileDashboard(warehouseId, authToken);
    //        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    //    }
    /*
     * -----------------------------Perpetual Count----------------------------------------------------
     */
//    @ApiOperation(response = PerpetualHeader.class, value = "Get all PerpetualHeader details") // label for swagger
//    @GetMapping("/perpetualheader")
//    public ResponseEntity<?> getPerpetualHeaders(@RequestParam String authToken) throws Exception {
//        PerpetualHeader[] perpetualheaderList = outboundTransactionService.getPerpetualHeaders(authToken);
//        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualHeader.class, value = "Get a PerpetualHeader") // label for swagger
//    @GetMapping("/perpetualheader/{cycleCountNo}")
//    public ResponseEntity<?> getPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
//                                                @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
//                                                @RequestParam String authToken) throws Exception {
//        PerpetualHeader perpetualheader =
//                outboundTransactionService.getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo,
//                        movementTypeId, subMovementTypeId, authToken);
//        log.info("PerpetualHeader : " + perpetualheader);
//        return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualHeaderEntity[].class, value = "Search PerpetualHeader") // label for swagger
//    @PostMapping("/perpetualheader/findPerpetualHeader")
//    public PerpetualHeaderEntity[] findPerpetualHeader(@RequestBody SearchPerpetualHeader searchPerpetualHeader,
//                                                       @RequestParam String authToken) throws Exception {
//        return outboundTransactionService.findPerpetualHeader(searchPerpetualHeader, authToken);
//    }

//    @ApiOperation(response = PerpetualHeader.class, value = "Create PerpetualHeader") // label for swagger
//    @PostMapping("/perpetualheader")
//    public ResponseEntity<?> postPerpetualHeader(@Valid @RequestBody AddPerpetualHeader newPerpetualHeader,
//                                                 @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PerpetualHeader createdPerpetualHeader =
//                outboundTransactionService.createPerpetualHeader(newPerpetualHeader, loginUserID, authToken);
//        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
//    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
//    @ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader") // label for swagger
//    @PostMapping("/perpetualheader/run")
//    public ResponseEntity<?> postRunPerpetualHeader(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
//                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PerpetualLineEntity[] perpetualLineEntity =
//                outboundTransactionService.runPerpetualHeader(runPerpetualHeader, authToken);
//        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader Stream") // label for swagger
//    @PostMapping("/perpetualheader/runNew")
//    public ResponseEntity<?> postRunPerpetualHeaderStream(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
//                                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PerpetualLineEntity[] perpetualLineEntity =
//                outboundTransactionService.runPerpetualHeaderNew(runPerpetualHeader, authToken);
//        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader") // label for swagger
//    @PatchMapping("/perpetualheader/{cycleCountNo}")
//    public ResponseEntity<?> patchPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
//                                                  @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
//                                                  @Valid @RequestBody UpdatePerpetualHeader updatePerpetualHeader, @RequestParam String loginUserID,
//                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PerpetualHeader createdPerpetualHeader =
//                outboundTransactionService.updatePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
//                        subMovementTypeId, loginUserID, updatePerpetualHeader, authToken);
//        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
//    @DeleteMapping("/perpetualheader/{cycleCountNo}")
//    public ResponseEntity<?> deletePerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
//                                                   @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
//                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
//        outboundTransactionService.deletePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
//                subMovementTypeId, loginUserID, authToken);
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }

    //-------------------------------PerpetualLine---------------------------------------------------------------------

    //-------------------------------PerpetualLine---------------------------------------------------------------------
//    @ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine") // label for swagger
//    @PostMapping("/findPerpetualLine")
//    public PerpetualLine[] findPerpetualHeader(@RequestBody SearchPerpetualLine searchPerpetualLine,
//                                               @RequestParam String authToken) throws Exception {
//        return outboundTransactionService.findPerpetualLine(searchPerpetualLine, authToken);
//    }

    @ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine Stream") // label for swagger
    @PostMapping("/findPerpetualLineNew")
    public PerpetualLine[] findPerpetualHeaderStream(@RequestBody SearchPerpetualLine searchPerpetualLine,
                                                     @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualLineStream(searchPerpetualLine, authToken);
    }

//    @ApiOperation(response = PerpetualLine[].class, value = "Update AssignHHTUser") // label for swagger
//    @PatchMapping("/perpetualline/assigingHHTUser")
//    public ResponseEntity<?> patchAssingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
//                                                @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PerpetualLine[] createdPerpetualLine = outboundTransactionService.updateAssingHHTUser(assignHHTUser, loginUserID, authToken);
//        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
//    }

//    @ApiOperation(response = PerpetualLine.class, value = "Update PerpetualLine") // label for swagger
//    @PatchMapping("/perpetualline/{cycleCountNo}")
//    public ResponseEntity<?> patchAssingHHTUser(@PathVariable String cycleCountNo,
//                                                @RequestBody List<UpdatePerpetualLine> updatePerpetualLine, @RequestParam String loginUserID,
//                                                @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PerpetualUpdateResponse createdPerpetualLine =
//                outboundTransactionService.updatePerpetualLine(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
//        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
//    }

    /*
     * -----------------------------Periodic Count----------------------------------------------------
     */
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
    @GetMapping("/periodicheader")
    public ResponseEntity<?> getPeriodicHeaders(@RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] PeriodicheaderList = outboundTransactionService.getPeriodicHeaders(authToken);
        return new ResponseEntity<>(PeriodicheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger
    @GetMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                               @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                               @RequestParam String authToken) throws Exception {
        PeriodicHeader[] Periodicheader =
                outboundTransactionService.getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId, authToken);
        return new ResponseEntity<>(Periodicheader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader") // label for swagger
    @PostMapping("/periodicheader/findPeriodicHeader")
    public ResponseEntity<?> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] results = outboundTransactionService.findPeriodicHeader(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader Stream") // label for swagger
    @PostMapping("/periodicheader/findPeriodicHeaderNew")
    public ResponseEntity<?> findPeriodicHeaderNew(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                   @RequestParam String authToken) throws Exception {
        PeriodicHeader[] results = outboundTransactionService.findPeriodicHeaderStream(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
    @PostMapping("/periodicheader")
    public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity createdPeriodicHeader = outboundTransactionService.createPeriodicHeader(newPeriodicHeader, loginUserID, authToken);
        log.info("createdPeriodicHeader:" + createdPeriodicHeader);

        /* Call Batch */
//		transactionService.createCSV(newPeriodicHeader.getPeriodicLine());
//		batchJobScheduler.runJobPeriodic();
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLineEntity.class, value = "Search Inventory") // label for swagger
    @PostMapping("/periodicheader/run/pagination")
    public ResponseEntity<?> findInventory(@RequestParam String warehouseId,
                                           @RequestParam(defaultValue = "0") Integer pageNo,
                                           @RequestParam(defaultValue = "100") Integer pageSize,
                                           @RequestParam(defaultValue = "itemCode") String sortBy,
                                           @RequestParam String authToken)
            throws Exception {
        Page<?> periodicLineEntity = outboundTransactionService.runPeriodicHeader(warehouseId, pageNo, pageSize, sortBy, authToken);
        return new ResponseEntity<>(periodicLineEntity, HttpStatus.OK);
    }

//    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
//    @PatchMapping("/periodicheader/{cycleCountNo}")
//    public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
//                                                 @RequestParam Long cycleCountTypeId, @RequestBody UpdatePeriodicHeader updatePeriodicHeader,
//                                                 @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PeriodicHeader createdPeriodicHeader =
//                outboundTransactionService.updatePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo,
//                        loginUserID, updatePeriodicHeader, authToken);
//        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
//    }

    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
    @DeleteMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deletePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Namratha
    @ApiOperation(response = PeriodicHeaderEntityV2.class, value = "Namratha Post PeriodicHeader and Line V2") // label for swagger
    @PostMapping("/periodicheader/create")
    public ResponseEntity<?> createPeriodicHeaderV4(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                    @RequestParam String languageId, @RequestParam String warehouseId,
                                                    @Valid @RequestBody List<PeriodicLineV2> periodicLines,
                                                    @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntityV2 createdPeriodicHeader = outboundTransactionService.postPeriodicHeaderV4(
                companyCodeId, plantId, languageId, warehouseId, loginUserID, periodicLines, authToken);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
    @PostMapping("/periodicline/findPeriodicLine")
    public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLine(@RequestBody SearchPeriodicLine searchPeriodicLine,
                                                                                    @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPeriodicLine(searchPeriodicLine, authToken);
    }

    //Stream
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
    @PostMapping("/periodicline/findPeriodicLineNew")
    public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLineNew(@RequestBody SearchPeriodicLine searchPeriodicLine,
                                                                                       @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPeriodicLineNew(searchPeriodicLine, authToken);
    }

//    @ApiOperation(response = PeriodicLine[].class, value = "Update AssignHHTUser") // label for swagger
//    @PatchMapping("/periodicline/assigingHHTUser")
//    public ResponseEntity<?> assigingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
//                                             @RequestParam String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PeriodicLine[] createdPeriodicLine =
//                outboundTransactionService.updatePeriodicLineAssingHHTUser(assignHHTUser, loginUserID, authToken);
//        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
//    }

    @ApiOperation(response = PeriodicLineV2[].class, value = "Update AssignHHTUser V6") // label for swagger
    @PatchMapping("/periodicline/assigingHHTUser")
    public ResponseEntity<?> assigingHHTUserV6(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicLineV2[] createdPeriodicLine =
                outboundTransactionService.updatePeriodicLineAssingHHTUserV6(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

//    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
//    @PatchMapping("/periodicline/{cycleCountNo}")
//    public ResponseEntity<?> patchPeriodicLine(@PathVariable String cycleCountNo,
//                                               @RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID,
//                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
//        PeriodicUpdateResponse updatedPeriodicLine =
//                outboundTransactionService.updatePeriodicLine(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
//        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
//    }

    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine V2") // label for swagger
    @PatchMapping("/periodicline/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineV6(@PathVariable String cycleCountNo,
                                                 @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicUpdateResponseV2 updatedPeriodicLine =
                outboundTransactionService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLineV2.class, value = "ExecuteStockCount") // label for swagger
    @PostMapping("/periodicline/executeStockCount")
    public ResponseEntity<?> executeStockCount(@RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId,
                                               @RequestBody ExecuteStockCountInput executeStockCountInput,
                                               @RequestParam String authToken) throws Exception {
        PeriodicLineV2[] stockCountLines = outboundTransactionService.executeStockCount(companyCodeId, plantId, languageId, warehouseId, executeStockCountInput, authToken);
        return new ResponseEntity<>(stockCountLines, HttpStatus.OK);
    }

    //--------------------------Schedule-Report------------------------------------------------------
    @ApiOperation(response = InventoryReport.class, value = "Report Inventory") // label for swagger
    @GetMapping("/reports/inventoryReport/schedule")
    public ResponseEntity<?> scheduleInventoryReport(@RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.getInventoryReport(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
    @GetMapping("/reports/dashboard/get-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception {
        Dashboard dashboard = outboundTransactionService.getDashboardCount(warehouseId, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Fast Slow moving Dashboard") // label for swagger
    @PostMapping("/reports/dashboard/get-fast-slow-moving")
    public ResponseEntity<?> getFastSlowMovingDashboard(@RequestBody FastSlowMovingDashboardRequest fastSlowMovingDashboardRequest,
                                                        @RequestParam String authToken) throws Exception {
        FastSlowMovingDashboard[] dashboard = outboundTransactionService.getFastSlowMovingDashboard(fastSlowMovingDashboardRequest, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    // File Upload - Orders
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/warehouse/outbound/so/upload")
    public ResponseEntity<?> postShipmenOrderUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processSOOrders(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


  // File Upload - Sales Orders - V2
    @ApiOperation(response = SalesOrderV2.class, value = "Create Sales Order V2 ") // label for swagger
    @PostMapping("/warehouse/outbound/salesOrder/upload")
    public ResponseEntity<?> postSalesOrderUploadV3(@RequestParam(required = false) String companyCodeId, @RequestParam(required = false) String plantId,
                                                    @RequestParam(required = false) String languageId, @RequestParam(required = false) String warehouseId,
                                                    @RequestParam(required = false) String loginUserID, @RequestParam(required = false) Long outboundOrderTypeId, @RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = null;
        String profile = companyCodeId;
        log.info("Outbound Upload In {}",profile);
        if(profile != null) {

            if(companyCodeId.equals("21")){
                response = fileStorageService.processSalesOrders(companyCodeId, plantId, languageId, warehouseId, loginUserID, file);
            }
            else if(companyCodeId.equals("1400")){
                response = fileStorageService.processSalesOrders(companyCodeId, plantId, languageId, warehouseId, loginUserID, file);
            }
            else if(companyCodeId.equals("1500")){
                response = fileStorageService.processOutboundOrdersV5(companyCodeId, plantId, languageId, warehouseId, outboundOrderTypeId, loginUserID, file);
            }
            else if(companyCodeId.equals("2000")){
                response = fileStorageService.processOutboundOrdersV7(companyCodeId, plantId, languageId, warehouseId, outboundOrderTypeId, loginUserID, file);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @ApiOperation(response = OutboundOrder.class, value = "Get all Outbound Success Orders") // label for swagger
    @GetMapping("/orders/outbound/success")
    public ResponseEntity<?> getOBAllOrders(@RequestParam String authToken) {
        OutboundOrder[] outboundOrderList = outboundTransactionService.getOBOrders(authToken);
        return new ResponseEntity<>(outboundOrderList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundOrder.class, value = "Get a Orders") // label for swagger
    @GetMapping("/orders/outbound/byDate")
    public ResponseEntity<?> getOBOrdersByDate(@RequestParam String orderStartDate,
                                               @RequestParam String orderEndDate, @RequestParam String authToken) throws Exception {
        OutboundOrder[] orders = outboundTransactionService.getOBOrderByDate(orderStartDate, orderEndDate, authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundOrder.class, value = "Get outbound Order by id ") // label for swagger
    @GetMapping("/orders/outbound/{orderId}")
    public ResponseEntity<?> getOutboundOrdersById(@PathVariable String orderId, @RequestParam String authToken) {
        OutboundOrder orders = outboundTransactionService.getOutboundOrdersById(orderId, authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundIntegrationLog.class, value = "Get Failed OutboundOrders") // label for swagger
    @GetMapping("/orders/outbound/failed")
    public ResponseEntity<?> getFailedOutboundOrders(@RequestParam String authToken) throws Exception {
        OutboundIntegrationLog[] orders = outboundTransactionService.getFailedOutboundOrders(authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /*-----------------------------CycleCountOrder------------------------------------------------*/

    //Perpetual
    @ApiOperation(response = Perpetual.class, value = "Create Perpetual Order") // label for swagger
    @PostMapping("/warehouse/stockcount/perpetual")
    public ResponseEntity<?> postPerpetual(@Valid @RequestBody Perpetual perpetual, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPerpetual = outboundTransactionService.postPerpetual(perpetual, authToken);
        return new ResponseEntity<>(createdPerpetual, HttpStatus.OK);
    }

    //Periodic
    @ApiOperation(response = Periodic.class, value = "Create Periodic Order") // label for swagger
    @PostMapping("/warehouse/stockcount/periodic")
    public ResponseEntity<?> postPeriodic(@Valid @RequestBody Periodic periodic, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPeriodic = outboundTransactionService.postPeriodic(periodic, authToken);
        return new ResponseEntity<>(createdPeriodic, HttpStatus.OK);
    }

    //=============================================================OutBound V2=================================================================

    /*
     * --------------------------------PreOutboundHeader---------------------------------
     */
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Search PreOutboundHeaderV2") // label for swagger
    @PostMapping("/preoutboundheader/v2/findPreOutboundHeader")
    public PreOutboundHeaderV2[] findPreOutboundHeaderV2(@RequestBody SearchPreOutboundHeaderV2 searchPreOutboundHeader,
                                                         @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPreOutboundHeaderV2(searchPreOutboundHeader, authToken);
    }

    /*
     * -------------------PreOutboundLine---------------------------------------------------
     */
    @ApiOperation(response = PreOutboundLineV2.class, value = "Search PreOutboundLine V2") // label for swagger
    @PostMapping("/preoutboundline/v2/findPreOutboundLine")
    public PreOutboundLineV2[] findPreOutboundLineV2(@RequestBody SearchPreOutboundLineV2 searchPreOutboundLine,
                                                     @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPreOutboundLineV2(searchPreOutboundLine, authToken);
    }

    /*
     * --------------------------------OrderMangementLine---------------------------------
     */
    @ApiOperation(response = OrderManagementLineV2.class, value = "Search OrderMangementLine V2") // label for swagger
    @PostMapping("/ordermanagementline/v2/findOrderManagementLine")
    public OrderManagementLineV2[] findOrderManagementLineV2(@RequestBody SearchOrderManagementLineV2 searchOrderMangementLine,
                                                             @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOrderManagementLineV2(searchOrderMangementLine, authToken);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/unallocate/patch")
    public ResponseEntity<?> patchUnallocateV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                outboundTransactionService.doUnAllocationV2(orderManagementLineV2, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/unallocate")
    public ResponseEntity<?> patchUnallocateV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                               @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2 updatedOrderManagementLine =
                outboundTransactionService.doUnAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, proposedStorageBin, proposedPackBarCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Batch Allocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/allocate/patch")
    public ResponseEntity<?> patchAllocateV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                outboundTransactionService.doAllocationV2(orderManagementLineV2, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Allocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/allocate")
    public ResponseEntity<?> patchAllocateV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                             @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                             @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2 updatedOrderManagementLine =
                outboundTransactionService.doAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Assign Picker V2") // label for swagger
    @PatchMapping("/ordermanagementline/assignPicker")
    public ResponseEntity<?> assignPickerV2(@RequestBody List<AssignPickerV2> assignPicker,
                                            @RequestParam String assignedPickerId, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                outboundTransactionService.doAssignPickerV2(assignPicker, assignedPickerId, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Update OrderMangementLine V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/{refDocNumber}")
    public ResponseEntity<?> patchOrderMangementLineV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                       @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                       @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
                                                       @Valid @RequestBody OrderManagementLineV2 updateOrderMangementLine, @RequestParam String loginUserID,
                                                       @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2 createdOrderMangementLine =
                outboundTransactionService.updateOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, itemCode, proposedStorageBin, proposedPackCode,
                        loginUserID, updateOrderMangementLine, authToken);
        return new ResponseEntity<>(createdOrderMangementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Delete OrderManagementLine V2") // label for swagger
    @DeleteMapping("/ordermanagementline/v2/{refDocNumber}")
    public ResponseEntity<?> deleteOrderManagementLineV2(@PathVariable String refDocNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                         @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                         @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                         @RequestParam String proposedPackCode, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) {
        outboundTransactionService.deleteOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Update Reference Field OrderManagementLine V2")
    // label for swagger
    @GetMapping("/ordermanagementline/v2/updateRefFields")
    public ResponseEntity<?> updateRefFieldsV2(@RequestParam String authToken) throws Exception {
        outboundTransactionService.updateRef9ANDRef10V2(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * -------------------------PickupHeader----------------------------------------------------
     */
    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader V2") // label for swagger
    @PostMapping("/pickupheader/v2/findPickupHeader")
    public PickupHeaderV2[] findPickupHeaderV2(@RequestBody SearchPickupHeaderV2 searchPickupHeader,
                                               @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupHeaderV2(searchPickupHeader, authToken);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Update PickupHeader V2") // label for swagger
    @PatchMapping("/pickupheader/v2/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeaderV2 updatePickupHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeaderV2 createdPickupHeader =
                outboundTransactionService.updatePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader, authToken);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

    //    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader V2")
//    // label for swagger // label for swagger
//    @PatchMapping("/pickupheader/v2/update-assigned-picker")
//    public ResponseEntity<?> patchAssignedPickerIdInPickupHeaderV2(@Valid @RequestBody List<PickupHeaderV2> updatePickupHeaderList, @RequestParam String companyCodeId, @RequestParam String plantId,
//                                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam("loginUserID") String loginUserID, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        PickupHeaderV2[] updatedPickupHeader =
//                transactionService.patchAssignedPickerIdInPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, loginUserID, updatePickupHeaderList, authToken);
//        return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
//    }
    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader V2")    // label for swagger // label for swagger
    @PatchMapping("/pickupheader/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeaderV2(@Valid @RequestBody List<PickupHeaderV2> updatePickupHeaderList, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeaderV2[] updatedPickupHeader =
                outboundTransactionService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList, authToken);
        return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Delete PickupHeader V2") // label for swagger
    @DeleteMapping("/pickupheader/v2/{pickupNumber}")
    public ResponseEntity<?> deletePickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                  @RequestParam String proposedPackCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deletePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * -------------------------PickupLine----------------------------------------------------
     */
    @ApiOperation(response = InventoryV2[].class, value = "Get AdditionalBins V2") // label for swagger
    @GetMapping("/pickupline/v2/additionalBins")
    public ResponseEntity<?> getAdditionalBinsV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String warehouseId, @RequestParam String itemCode, @RequestParam String manufacturerName, @RequestParam Long obOrdertypeId,
                                                 @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin, @RequestParam String authToken) {
        InventoryV2[] additionalBins = outboundTransactionService.getAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode, obOrdertypeId,
                proposedPackBarCode, manufacturerName, proposedStorageBin, authToken);
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Create PickupLine V2") // label for swagger
    @PostMapping("/pickupline")
    public ResponseEntity<?> postPickupLineV2(@Valid @RequestBody List<AddPickupLine> newPickupLine,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupLineV2[] createdPickupLine = outboundTransactionService.createPickupLineV2(newPickupLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Search PickupLine V2") // label for swagger
    @PostMapping("/pickupline/v2/findPickupLine")
    public PickupLineV2[] findPickupLineV2(@RequestBody SearchPickupLineV2 searchPickupLine,
                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPickupLineV2(searchPickupLine, authToken);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Update PickupLine V2") // label for swagger
    @PatchMapping("/pickupline/v2/{actualHeNo}")
    public ResponseEntity<?> patchPickupLineV2(@PathVariable String actualHeNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                               @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                               @Valid @RequestBody PickupLineV2 updatePickupLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupLineV2 createdPickupLine =
                outboundTransactionService.updatePickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
                        actualHeNo, loginUserID, updatePickupLine, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = ImPartner.class, value = "Update BarcodeId") // label for swagger
    @PatchMapping("/pickupline/v2/barcodeId")
    public ResponseEntity<?> patchPickupLineBarcodeIdV2(@Valid @RequestBody UpdateBarcodeInput updateBarcodeInput, @RequestParam String authToken) {
        ImPartner results = outboundTransactionService.updatePickupLineForBarcodeId(updateBarcodeInput, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Delete PickupLine V2") // label for swagger
    @DeleteMapping("/pickupline/v2/{actualHeNo}")
    public ResponseEntity<?> deletePickupLineV2(@PathVariable String actualHeNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam Long lineNumber, @RequestParam String pickupNumber, @RequestParam String itemCode,
                                                @RequestParam String pickedStorageBin, @RequestParam String pickedPackCode,
                                                @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deletePickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                pickupNumber, itemCode, actualHeNo, pickedStorageBin, pickedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ------------------------QualityHeader--------------------------------------------------------
     */

    @ApiOperation(response = QualityHeaderV2.class, value = "Create QualityHeader V2") // label for swagger
    @PostMapping("/v2/qualityheader")
    public ResponseEntity<?> postQualityHeaderV2(@Valid @RequestBody QualityHeaderV2 newQualityHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        QualityHeaderV2 createdQualityHeader = outboundTransactionService.createQualityHeaderV2(newQualityHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader V2") // label for swagger
    @PostMapping("/qualityheader/v2/findQualityHeader")
    public QualityHeaderV2[] findQualityHeaderV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader,
                                                 @RequestParam String authToken) throws Exception {
        log.info("Api Called -----> outboundtransaction-service/qualityheader/v2/findQualityHeader");
        return outboundTransactionService.findQualityHeaderV2(searchQualityHeader, authToken);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Update QualityHeader V2") // label for swagger
    @PatchMapping("/qualityheader/v2/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeaderV2(@PathVariable String qualityInspectionNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                  @Valid @RequestBody QualityHeaderV2 updateQualityHeader,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityHeaderV2 updatedQualityHeader = outboundTransactionService.updateQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, updateQualityHeader, authToken);
        return new ResponseEntity<>(updatedQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Delete QualityHeader") // label for swagger
    @DeleteMapping("/qualityheader/v2/{qualityInspectionNo}")
    public ResponseEntity<?> deleteQualityHeaderV2(@PathVariable String qualityInspectionNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                   @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                   @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        outboundTransactionService.deleteQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, pickupNumber, qualityInspectionNo, actualHeNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ------------------------QualityLine-----------------------------------------------------------
     */
    @ApiOperation(response = QualityLineV2.class, value = "Search QualityLine V2") // label for swagger
    @PostMapping("/qualityline/v2/findQualityLine")
    public QualityLineV2[] findQualityLineV2(@RequestBody SearchQualityLineV2 searchQualityLine,
                                             @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findQualityLineV2(searchQualityLine, authToken);
    }

    @ApiOperation(response = QualityLineV2.class, value = "Create QualityLine V2") // label for swagger
    @PostMapping("/qualityline")
    public ResponseEntity<?> postQualityLineV2(@Valid @RequestBody List<AddQualityLine> newQualityLine,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        QualityLineV2[] createdQualityLine = outboundTransactionService.createQualityLineV2(newQualityLine, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = QualityLineV2.class, value = "Update QualityLine V2") // label for swagger
    @PatchMapping("/qualityline/v2/{partnerCode}")
    public ResponseEntity<?> patchQualityLineV2(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam Long lineNumber,
                                                @RequestParam String qualityInspectionNo, @RequestParam String itemCode,
                                                @Valid @RequestBody QualityLineV2 updateQualityLine, @RequestParam String loginUserID,
                                                @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityLineV2 createdQualityLine =
                outboundTransactionService.updateQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, qualityInspectionNo, itemCode, loginUserID, updateQualityLine, authToken);
        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = QualityLineV2.class, value = "Delete QualityLine V2") // label for swagger
    @DeleteMapping("/qualityline/v2/{partnerCode}")
    public ResponseEntity<?> deleteQualityLineV2(@PathVariable String partnerCode, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam Long lineNumber,
                                                 @RequestParam String qualityInspectionNo, @RequestParam String itemCode,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deleteQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ---------------------------OutboundHeader--------------------------------------------------------
     */
    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2") // label for swagger
    @PostMapping("/outboundheader/findOutboundHeader")
    public OutboundHeaderV2[] findOutboundHeaderV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                   @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundHeaderV2(searchOutboundHeader, authToken);
    }

    //Find OutBoundOrder
    @ApiOperation(response = OutboundOrderV2[].class, value = "Find OutboundOrderV2")//label for swagger
    @PostMapping("/findOutboundOrder")
    public ResponseEntity<?> findOutboundOrderV2(@RequestBody FindOutboundOrderV2 findOutboundOrderV2,
                                                 @RequestParam String authToken) {
        OutboundOrderV2[] outboundOrderV2 = outboundTransactionService.findOutboundOrderV2(findOutboundOrderV2, authToken);
        return new ResponseEntity<>(outboundOrderV2, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2 for Delivery")    // label for swagger
    @PostMapping("/outboundheader/v2/findOutboundHeader/delivery")
    public OutboundHeaderV2[] findOutboundHeaderDeliveryV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundHeaderDeliveryV2(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2") // label for swagger
    @PostMapping("/outboundheader/findOutboundHeader/rfd")
    public OutboundHeaderV2[] findOutboundHeaderV2Rfd(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                      @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundHeaderRfd(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Update OutboundHeader V2") // label for swagger
    @PatchMapping("/outboundheader/v2/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                   @RequestParam String partnerCode, @Valid @RequestBody OutboundHeaderV2 updateOutboundHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundHeaderV2 createdOutboundHeader =
                outboundTransactionService.updateOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader V2") // label for swagger
    @DeleteMapping("/outboundheader/v2/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                  @RequestParam String partnerCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deleteOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ----------------------OutboundLine----------------------------------------------------------
     */
    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine V2") // label for swagger
    @PostMapping("/outboundline/findOutboundLine")
    public OutboundLineV2[] findOutboundLineV2(@RequestBody SearchOutboundLineV2 searchOutboundLine,
                                               @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findOutboundLineV2(searchOutboundLine, authToken);
    }
    //--------------------------------------DriverRemark------------------------------//

    @ApiOperation(response = DriverRemark.class, value = "Create DriverRemark") // label for swagger
    @PostMapping("/driverRemark/create")
    public ResponseEntity<?> postDriverRemark(@Valid @RequestBody DriverRemark newDriverRemark,
                                              @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        DriverRemark createdDriverRemark = outboundTransactionService.createDriverRemark(authToken, loginUserID, newDriverRemark);
        return new ResponseEntity<>(createdDriverRemark , HttpStatus.OK);
    }

    @ApiOperation(response = DriverRemark.class, value = "Update DriverRemark") // label for swagger
    @PostMapping("/driverRemark/update")
    public ResponseEntity<?> patchDriverRemark(@Valid @RequestBody DriverRemark newDriverRemark,
                                               @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        DriverRemark updatedDriverRemark = outboundTransactionService.updateDriverRemark(authToken, loginUserID, newDriverRemark);
        return new ResponseEntity<>(updatedDriverRemark , HttpStatus.OK);
    }

    @ApiOperation(response = DriverRemark.class, value = "Find DriverRemark") // label for swagger
    @PostMapping("/driverRemark/find")
    public ResponseEntity<?> findDriverRemark(@Valid @RequestBody SearchDriverRemark searchDriverRemark, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        DriverRemark[] findDriverRemark = outboundTransactionService.findDriverRemark(authToken, searchDriverRemark);
        return new ResponseEntity<>(findDriverRemark , HttpStatus.OK);
    }

    //====================================================InhouseTransferHeader============================================
    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader V2")
    // label for swagger
    @PostMapping("/inhousetransferheader")
    public ResponseEntity<?> postInHouseTransferHeaderV2(@RequestBody InhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InhouseTransferHeader createdInHouseTransferHeader = outboundTransactionService.createInhouseTransferHeaderV2(newInHouseTransferHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }

    //=================================PeriodicLineConfirm=======================================//
    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine confirm V2") // label for swagger
    @PatchMapping("/periodicline/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineConfirmV4(@PathVariable String cycleCountNo,
                                                        @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createPeriodicLine =
                outboundTransactionService.updatePeriodicLineConfirmV4(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createPeriodicLine, HttpStatus.OK);
    }
    //=================================InHouseTransferHeader=======================================//
    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
    @PostMapping("/inhousetransferheader/findInHouseTransferHeader")
    public InhouseTransferHeader[] findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                             @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findInHouseTransferHeader(searchInHouseTransferHeader, authToken);
    }

    //=========================================Update=====================================================================

    @ApiOperation(response = OutboundOrder.class, value = "Update OutboundOrder details")
    @PatchMapping("/orders/update/outboundOrder")
    public ResponseEntity<?> updateOutboundOrder(@RequestParam String orderId, @RequestBody UpdateOutboundOrder updateOutboundOrder, @RequestParam String authToken) throws Exception {
        OutboundOrder outboundOrder = outboundTransactionService.updateOutboundOrder(orderId, updateOutboundOrder, authToken);
        return new ResponseEntity<>(outboundOrder, HttpStatus.OK);
    }

    //==================================================Delete=========================================================

    @ApiOperation(response = OutboundOrder.class, value = " Delete OutboundOrder details")
    @DeleteMapping("/orders/delete/outboundOrder")
    public ResponseEntity<?> deleteOutboundOrder(@RequestParam String orderId, @RequestParam String authToken) throws Exception {
        outboundTransactionService.deleteOutboundOrder(orderId, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
