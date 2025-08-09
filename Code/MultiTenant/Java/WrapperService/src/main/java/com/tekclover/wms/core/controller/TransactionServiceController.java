package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.model.auth.AuthToken;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.service.AuthTokenService;
import com.tekclover.wms.core.service.InboundTransactionService;
import com.tekclover.wms.core.service.OutboundTransactionService;
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
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-transaction-service")
@Api(tags = {"Transaction Service"}, value = "Transaction Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Transaction Modules")})
public class TransactionServiceController {

    @Autowired
    InboundTransactionService transactionService;

    @Autowired
    OutboundTransactionService outboundTransactionService;

    @Autowired
    AuthTokenService authTokenService;

    //Find

    @ApiOperation(response = PutAwayHeaderV2.class, value = "Search PutAwayHeader V2") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeader/v2")
    public PutAwayHeaderV2[] findPutAwayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        AuthToken authTokenForInboundTransaction = authTokenService.getInboundTransactionServiceAuthToken();
        return transactionService.findPutAwayHeaderV2(searchPutAwayHeader, authTokenForInboundTransaction.getAccess_token());
    }


    //Create
    @ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
    @PostMapping("/putawayline/v2")
    public ResponseEntity<?> postPutAwayLineV2(@Valid @RequestBody List<PutAwayLineV2> newPutAwayLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        AuthToken authTokenForInboundTransaction = authTokenService.getInboundTransactionServiceAuthToken();
        PutAwayLineV2[] createdPutAwayLine = transactionService.createPutAwayLineV2(newPutAwayLine, loginUserID, authTokenForInboundTransaction.getAccess_token());
        return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader V2") // label for swagger
    @PostMapping("/pickupheader/v2/findPickupHeader")
    public PickupHeaderV2[] findPickupHeaderV2(@RequestBody SearchPickupHeaderV2 searchPickupHeader,
                                               @RequestParam String authToken) throws Exception {
        AuthToken authTokenForOutboundTransaction = authTokenService.getOutboundTransactionServiceAuthToken();
        return outboundTransactionService.findPickupHeaderV2(searchPickupHeader, authTokenForOutboundTransaction.getAccess_token());
    }

    //V2
    @ApiOperation(response = InventoryV2.class, value = "Search Inventory V2") // label for swagger
    @PostMapping("/inventory/findInventory/v2")
    public InventoryV2[] findInventoryV2(@RequestBody SearchInventoryV2 searchInventory,
                                         @RequestParam String authToken) throws Exception {
        AuthToken authTokenForInboundTransaction = authTokenService.getInboundTransactionServiceAuthToken();
        return transactionService.findInventoryV2(searchInventory, authTokenForInboundTransaction.getAccess_token());
    }

    @ApiOperation(response = PickupLineV2.class, value = "Create PickupLine V2") // label for swagger
    @PostMapping("/v2/pickupline")
    public ResponseEntity<?> postPickupLineV2(@Valid @RequestBody List<AddPickupLine> newPickupLine,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        log.info("Api Called -----> transaction-service//v2/pickupline");
        AuthToken authTokenForOutboundTransaction = authTokenService.getOutboundTransactionServiceAuthToken();
        PickupLineV2[] createdPickupLine = outboundTransactionService.createPickupLineV2(newPickupLine, loginUserID, authTokenForOutboundTransaction.getAccess_token());
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
    @GetMapping("/reports/dashboard/get-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception {
      log.info("API Name -------> /reports/dashboard/get-count ");
        AuthToken authTokenForInboundTransaction = authTokenService.getInboundTransactionServiceAuthToken();
        Dashboard dashboard = transactionService.getDashboardCount(warehouseId, authTokenForInboundTransaction.getAccess_token());
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    //FIND
    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard")//label for swagger
    @PostMapping("/reports/dashboard/mobile/find")
    public MobileDashboard findMobileDashBoard(@RequestBody FindMobileDashBoard findMobileDashBoard,
                                               @RequestParam String authToken) throws Exception {
        log.info("Api Name -------> /reports/dashboard/mobile/find ");
        AuthToken authTokenForInboundTransaction = authTokenService.getOutboundTransactionServiceAuthToken();
        return outboundTransactionService.findMobileDashBoard(findMobileDashBoard, authTokenForInboundTransaction.getAccess_token());
    }

    //====================================================InhouseTransferHeader============================================
    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader V2")
    // label for swagger
    @PostMapping("/inhousetransferheader/v2")
    public ResponseEntity<?> postInHouseTransferHeaderV2(@Valid @RequestBody InhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        log.info("Api Called -----> transaction-service/inhousetransferheader/v2");
        log.info("InhouseTransferHeader ------> {}", newInHouseTransferHeader);
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        InhouseTransferHeader createdInHouseTransferHeader = outboundTransactionService.createInhouseTransferHeaderV2(newInHouseTransferHeader, loginUserID, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderEntityV2[].class, value = "Search PeriodicHeader V2") // label for swagger
    @PostMapping("/periodicheader/v2/findPeriodicHeader")
    public PeriodicHeaderEntityV2[] findPeriodicHeaderV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                         @RequestParam String authToken) throws Exception {
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        return outboundTransactionService.findPeriodicHeaderV2(searchPeriodicHeader, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
    }

    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLine V2") // label for swagger
    @PostMapping("/periodicline/v2/findPeriodicLine")
    public PeriodicLineV2[] findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLine,
                                               @RequestParam String authToken) throws Exception {
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        return outboundTransactionService.findPeriodicLineV2(searchPeriodicLine, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
    }
    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine V2") // label for swagger
    @PatchMapping("/periodicline/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
                                                 @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicUpdateResponseV2 updatedPeriodicLine =
                outboundTransactionService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader V2") // label for swagger
    @PatchMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                   @RequestParam Long cycleCountTypeId, @RequestBody PeriodicHeaderEntityV2 updatePeriodicHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        PeriodicHeaderV2 createdPeriodicHeader =
                outboundTransactionService.updatePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        loginUserID, updatePeriodicHeader, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = StagingLineEntityV2.class, value = "Search StagingLine V2") // label for swagger
    @PostMapping("/stagingline/findStagingLine/v2")
    public StagingLineEntityV2[] findStagingLineV2(@RequestBody SearchStagingLineV2 searchStagingLine,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findStagingLineV2(searchStagingLine, authToken);
    }

    //Find
    @ApiOperation(response = GrHeaderV2.class, value = "Search GrHeader V2") // label for swagger
    @PostMapping("/grheader/findGrHeader/v2")
    public GrHeaderV2[] findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeaderV2(searchGrHeader, authToken);
    }

    //Create
    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/grline/v2")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine, @RequestParam String loginUserID,
                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrLineV2[] createdGrLine = transactionService.createGrLineV2(newGrLine, loginUserID, authToken);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine V2") // label for swagger
    @PostMapping("/grline/findGrLine/v2")
    public GrLineV2[] findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLineV2(searchGrLine, authToken);
    }

    //
    @ApiOperation(response = ImPartner.class, value = "Update BarcodeId") // label for swagger
    @PatchMapping("/pickupline/v2/barcodeId")
    public ResponseEntity<?> patchPickupLineBarcodeIdV2(@Valid @RequestBody UpdateBarcodeInput updateBarcodeInput, @RequestParam String authToken) {
        log.info("Api Called -----> transaction-service/pickupline/v2/barcodeId");
        log.info("UpdateBarcodeInput ------> {}", updateBarcodeInput);
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        ImPartner results = outboundTransactionService.updatePickupLineForBarcodeId(updateBarcodeInput, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader V2") // label for swagger
    @PostMapping("/qualityheader/v2/findQualityHeader")
    public QualityHeaderV2[] findQualityHeaderV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader,
                                                 @RequestParam String authToken) throws Exception {
        log.info("Api Called -----> transaction-service/qualityheader/v2/findQualityHeader");
        log.info("SearchQualityHeaderV2 ------> {}", searchQualityHeader);
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        return outboundTransactionService.findQualityHeaderV2(searchQualityHeader, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
    }

    //Update Stagingline
    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine V2") // label for swagger
    @RequestMapping(value = "/stagingline/{lineNo}/v2", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchStagingLineV2(@PathVariable Long lineNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId, @RequestParam String refDocNumber,
                                                @RequestParam String stagingNo, @RequestParam String palletCode, @RequestParam String caseCode,
                                                @RequestParam String preInboundNo, @RequestParam String itemCode,
                                                @Valid @RequestBody StagingLineEntityV2 updateStagingLine, @RequestParam String loginUserID,
                                                @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingLineEntityV2 updatedStagingLine =
                transactionService.updateStagingLineV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, stagingNo, palletCode, caseCode,
                        lineNo, itemCode, loginUserID, updateStagingLine, authToken);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }

    //-----------------PACK_BARCODE-GENERATION-V2--------------------------------------------------------------------------
    @ApiOperation(response = GrLineV2.class, value = "Get PackBarcodes V2") // label for swagger
    @GetMapping("/grline/packBarcode/v2")
    public ResponseEntity<?> getPackBarcodeV2(@RequestParam Long acceptQty, @RequestParam Long damageQty,
                                              @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                              @RequestParam String plantId, @RequestParam String languageId,
                                              @RequestParam String loginUserID, @RequestParam String authToken) {
        PackBarcode[] packBarcodes =
                transactionService.generatePackBarcodeV2(companyCodeId, plantId, languageId, acceptQty,
                        damageQty, warehouseId, loginUserID, authToken);
        log.info("packBarcodes : " + packBarcodes);
        return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
    }

    @ApiOperation(response = QualityLineV2.class, value = "Create QualityLine V2") // label for swagger
    @PostMapping("/v2/qualityline")
    public ResponseEntity<?> postQualityLineV2(@Valid @RequestBody List<AddQualityLine> newQualityLine,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        QualityLineV2[] createdQualityLine = outboundTransactionService.createQualityLineV2(newQualityLine, loginUserID, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
    }

    /*
     * -----------------------------Perpetual Count----------------------------------------------------
     */
    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get all PerpetualHeader details V2")
    // label for swagger
    @GetMapping("/v2/perpetualheader")
    public ResponseEntity<?> getPerpetualHeadersV2(@RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2[] perpetualheaderList = outboundTransactionService.getPerpetualHeadersV2(authToken);
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get a PerpetualHeader V2") // label for swagger
    @GetMapping("/perpetualheader/v2/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                  @RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2 perpetualheader =
                outboundTransactionService.getPerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId, authToken);
        log.info("PerpetualHeader : " + perpetualheader);
        return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2[].class, value = "Search PerpetualHeader V2") // label for swagger
    @PostMapping("/perpetualheader/v2/findPerpetualHeader")
    public PerpetualHeaderEntityV2[] findPerpetualHeaderV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                           @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualHeaderV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderV2[].class, value = "Search PerpetualHeader New V2") // label for swagger
    @PostMapping("/perpetualheader/v2/findPerpetualHeaderNew")
    public PerpetualHeaderV2[] findPerpetualHeaderNewV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                        @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualHeaderEntityV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Create PerpetualHeader V2") // label for swagger
    @PostMapping("/v2/perpetualheader")
    public ResponseEntity<?> postPerpetualHeaderV2(@Valid @RequestBody PerpetualHeaderEntityV2 newPerpetualHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeaderEntityV2 createdPerpetualHeader =
                outboundTransactionService.createPerpetualHeaderV2(newPerpetualHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineV2[].class, value = "Create PerpetualHeader V2") // label for swagger
    @PostMapping("/perpetualheader/v2/run")
    public ResponseEntity<?> postRunPerpetualHeaderV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                      @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] perpetualLineEntity =
                outboundTransactionService.runPerpetualHeaderV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Create PerpetualHeader Stream V2") // label for swagger
    @PostMapping("/perpetualheader/v2/runNew")
    public ResponseEntity<?> postRunPerpetualHeaderStreamV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                            @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] perpetualLineEntity =
                outboundTransactionService.runPerpetualHeaderNewV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderV2.class, value = "Update PerpetualHeader V2") // label for swagger
    @PatchMapping("/perpetualheader/v2/{cycleCountNo}")
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
    @DeleteMapping("/perpetualheader/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                     @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
        outboundTransactionService.deletePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                subMovementTypeId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PerpetualLine---------------------------------------------------------------------
    @ApiOperation(response = PerpetualLineV2.class, value = "SearchPerpetualLine V2") // label for swagger
    @PostMapping("/v2/findPerpetualLine")
    public PerpetualLineV2[] findPerpetualLineV2(@RequestBody SearchPerpetualLineV2 searchPerpetualLine,
                                                 @RequestParam String authToken) throws Exception {
        return outboundTransactionService.findPerpetualLineV2(searchPerpetualLine, authToken);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Update AssignHHTUser V2") // label for swagger
    @PatchMapping("/perpetualline/v2/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] createdPerpetualLine = outboundTransactionService.updateAssingHHTUserV2(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualUpdateResponseV2.class, value = "Update PerpetualLine V2") // label for swagger
    @PatchMapping("/perpetualline/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineV2(@PathVariable String cycleCountNo,
                                                  @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualUpdateResponseV2 createdPerpetualLine =
                outboundTransactionService.updatePerpetualLineV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PerpetualLine confirm V2") // label for swagger
    @PatchMapping("/perpetualline/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineConfirmV2(@PathVariable String cycleCountNo,
                                                         @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPerpetualLine =
                outboundTransactionService.updatePerpetualLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    //=================================PeriodicLineConfirm=======================================//
    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine confirm V2") // label for swagger
    @PatchMapping("/periodicline/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineConfirmV4(@PathVariable String cycleCountNo,
                                                        @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        WarehouseApiResponse createPeriodicLine =
                outboundTransactionService.updatePeriodicLineConfirmV4(cycleCountNo, updatePerpetualLine, loginUserID, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        return new ResponseEntity<>(createPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2.class, value = "Create PerpetualLine From ZeroStock V2") // label for swagger
    @PostMapping("/perpetualline/v2/createPerpetualFromZeroStk")
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

    @ApiOperation(response = InventoryV2[].class, value = "Get AdditionalBins V2") // label for swagger
    @GetMapping("/pickupline/v2/additionalBins")
    public ResponseEntity<?> getAdditionalBinsV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String warehouseId, @RequestParam String itemCode, @RequestParam String manufacturerName, @RequestParam Long obOrdertypeId,
                                                 @RequestParam String proposedPackBarCode, @RequestParam String proposedStorageBin, @RequestParam String authToken) {
        log.info("Add Bins inputs : companyCodeId ----> " + companyCodeId + ", plantId ----> " + plantId + ", languageId ----> " + languageId +
                ", warehouseId ----> " + warehouseId + ", itemCode ----> " + itemCode + ", manufacturerName ---> " + manufacturerName + ", obOrderTypeId ----> " +
                obOrdertypeId + ", proposedPackBarCode ----> " + proposedPackBarCode + ", proposedStorageBin ----> " + proposedStorageBin);
        AuthToken authTokenServiceOutboundTransactionServiceAuthToken = authTokenService.getOutboundTransactionServiceAuthToken();
        InventoryV2[] additionalBins = outboundTransactionService.getAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode, obOrdertypeId,
                proposedPackBarCode, manufacturerName, proposedStorageBin, authTokenServiceOutboundTransactionServiceAuthToken.getAccess_token());
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

}




