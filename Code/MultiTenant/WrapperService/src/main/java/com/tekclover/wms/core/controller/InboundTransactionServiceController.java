package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.batch.scheduler.BatchJobScheduler;
import com.tekclover.wms.core.exception.CustomErrorResponse;
import com.tekclover.wms.core.model.dto.MultiDbInput;
import com.tekclover.wms.core.model.dto.StagingLineUpdate;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.core.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.core.model.warehouse.inbound.ASN;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.service.CommonService;
import com.tekclover.wms.core.service.FileStorageService;
import com.tekclover.wms.core.service.InboundTransactionService;
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
@RequestMapping("/wms-inboundtransaction-service")
@Api(tags = {"InboundTransaction Service"}, value = "InboundTransaction Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to InboundTransaction Modules")})
public class InboundTransactionServiceController {

    @Autowired
    InboundTransactionService transactionService;

    @Autowired
    CommonService commonService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    BatchJobScheduler batchJobScheduler;


    /*
     * --------------------------------ContainerReceipt---------------------------------------------------------------
     */
    @ApiOperation(response = ContainerReceipt.class, value = "Get all ContainerReceipt details") // label for swagger
    @GetMapping("/containerreceipt")
    public ResponseEntity<?> getContainerReceipts(@RequestParam String authToken) throws Exception {
        ContainerReceipt[] containerReceiptNoList = transactionService.getContainerReceipts(authToken);
        return new ResponseEntity<>(containerReceiptNoList, HttpStatus.OK);
    }

    //Streaming
    @ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt New") // label for swagger
    @PostMapping("/containerreceipt/findContainerReceiptNew")
    public ContainerReceipt[] findContainerReceiptNew(@RequestBody SearchContainerReceipt searchContainerReceipt,
                                                      @RequestParam String authToken) throws Exception {
        return transactionService.findContainerReceiptNew(searchContainerReceipt, authToken);
    }

    /*
     * --------------------------------Pre-InboundHeader---------------------------------------------------------------
     */
    @ApiOperation(response = PreInboundHeader.class, value = "Get all PreInboundHeader details") // label for swagger
    @GetMapping("/preinboundheader")
    public ResponseEntity<?> getPreInboundHeaders(@RequestParam String authToken) throws Exception {
        PreInboundHeader[] preinboundheaderList = transactionService.getPreInboundHeaders(authToken);
        return new ResponseEntity<>(preinboundheaderList, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = PreInboundHeader.class, value = "Search PreInboundHeader New") // label for swagger
    @PostMapping("/preinboundheader/findPreInboundHeaderNew")
    public PreInboundHeader[] findPreInboundHeaderNew(@RequestBody SearchPreInboundHeader searchPreInboundHeader,
                                                      @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundHeaderNew(searchPreInboundHeader, authToken);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Get a PreInboundHeader With Status=24")
    // label for swagger
    @GetMapping("/preinboundheader/{warehouseId}/inboundconfirm")
    public ResponseEntity<?> getPreInboundHeader(@PathVariable String warehouseId,
                                                 @RequestParam String authToken) throws Exception {
        PreInboundHeader[] preinboundheader =
                transactionService.getPreInboundHeaderWithStatusId(warehouseId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Process ASN") // label for swagger
    @PostMapping("/preinboundheader/processASN")
    public ResponseEntity<?> processASN(@RequestBody List<PreInboundLine> newPreInboundLine, @RequestParam String loginUserID,
                                        @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeader createdStagingHeader = transactionService.processASN(newPreInboundLine, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    /*
     * --------------------------------InboundHeader---------------------------------
     */
    @ApiOperation(response = InboundHeader.class, value = "Get all InboundHeader details") // label for swagger
    @GetMapping("/inboundheader")
    public ResponseEntity<?> getInboundHeaders(@RequestParam String authToken) throws Exception {
        InboundHeader[] refDocNumberList = transactionService.getInboundHeaders(authToken);
        return new ResponseEntity<>(refDocNumberList, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = InboundHeader.class, value = "Search InboundHeader New") // label for swagger
    @PostMapping("/inboundheader/findInboundHeaderNew")
    public InboundHeader[] findInboundHeaderNew(@RequestBody SearchInboundHeader searchInboundHeader,
                                                @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderNew(searchInboundHeader, authToken);
    }

    @ApiOperation(response = InboundHeader.class, value = "Get a InboundHeader") // label for swagger
    @GetMapping("/inboundheader/inboundconfirm")
    public ResponseEntity<?> getInboundHeader(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception {
        InboundHeaderEntity[] inboundheaderEntity = transactionService.getInboundHeaderWithStatusId(warehouseId, authToken);
        log.info("PreInboundHeader : " + inboundheaderEntity);
        return new ResponseEntity<>(inboundheaderEntity, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Create InboundHeader") // label for swagger
    @PostMapping("/inboundheader")
    public ResponseEntity<?> postInboundHeader(@Valid @RequestBody InboundHeader newInboundHeader, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InboundHeader createdInboundHeader = transactionService.createInboundHeader(newInboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeader, HttpStatus.OK);
    }

    // -------------------StagingHeader----------------------------------------------------------------------------------------------
    @ApiOperation(response = StagingHeader.class, value = "Get all StagingHeader details") // label for swagger
    @GetMapping("/stagingheader")
    public ResponseEntity<?> getStagingHeaders(@RequestParam String authToken) throws Exception {
        StagingHeader[] stagingheaderList = transactionService.getStagingHeaders(authToken);
        return new ResponseEntity<>(stagingheaderList, HttpStatus.OK);
    }

//    @ApiOperation(response = StagingHeader.class, value = "Get a StagingHeader") // label for swagger
//    @GetMapping("/stagingheader/{stagingNo}")
//    public ResponseEntity<?> getStagingHeader(@PathVariable String stagingNo, @RequestParam String languageId,
//                                              @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId,
//                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String authToken) throws Exception {
//        StagingHeader stagingheader = transactionService.getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, authToken);
//        log.info("StagingHeader : " + stagingheader);
//        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
//    }

    //Stream
    @ApiOperation(response = StagingHeader.class, value = "Search StagingHeader New") // label for swagger
    @PostMapping("/stagingheader/findStagingHeaderNew")
    public StagingHeader[] findStagingHeaderNew(@RequestBody SearchStagingHeader searchStagingHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findStagingHeaderNew(searchStagingHeader, authToken);
    }

    /*
     * Barccode Generation
     * ----------------------
     */
    @ApiOperation(response = StagingHeader.class, value = "Get Barcodes") // label for swagger 
    @GetMapping("/stagingheader/{numberOfCases}/barcode")
    public ResponseEntity<?> getStagingHeader(@PathVariable Long numberOfCases, @RequestParam String warehouseId,
                                              @RequestParam String authToken) {
        String[] stagingheader = transactionService.generateNumberRanges(numberOfCases, warehouseId, authToken);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    /*
     * --------------------------------StagingLine-----------------------------------------------------------------------
     */
    @ApiOperation(response = StagingLine.class, value = "Get all StagingLine details") // label for swagger
    @GetMapping("/stagingline")
    public ResponseEntity<?> getStagingLines(@RequestParam String authToken) throws Exception {
        StagingLineEntity[] palletCodeList = transactionService.getStagingLines(authToken);
        return new ResponseEntity<>(palletCodeList, HttpStatus.OK);
    }

    /*
     * --------------------------------GrHeader---------------------------------------------------------------------
     */
    @ApiOperation(response = GrHeader.class, value = "Get all GrHeader details") // label for swagger
    @GetMapping("/grheader")
    public ResponseEntity<?> getGrHeaders(@RequestParam String authToken) throws Exception {
        GrHeader[] goodsReceiptNoList = transactionService.getGrHeaders(authToken);
        return new ResponseEntity<>(goodsReceiptNoList, HttpStatus.OK);
    }

    //Stream - JPA
    @ApiOperation(response = GrHeader.class, value = "Search GrHeader New") // label for swagger
    @PostMapping("/grheader/findGrHeaderNew")
    public GrHeader[] findGrHeaderNew(@RequestBody SearchGrHeader searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeaderNew(searchGrHeader, authToken);
    }

    /*
     * --------------------------------GrLine------------------------------------------------------------------------------
     */
    @ApiOperation(response = GrLine.class, value = "Get all GrLine details") // label for swagger
    @GetMapping("/grline")
    public ResponseEntity<?> getGrLines(@RequestParam String authToken) throws Exception {
        GrLine[] itemCodeList = transactionService.getGrLines(authToken);
        return new ResponseEntity<>(itemCodeList, HttpStatus.OK);
    }

//    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger
//    @GetMapping("/grline/{lineNo}")
//    public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
//                                       @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
//                                       @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
//                                       @RequestParam String itemCode, @RequestParam String authToken) throws Exception {
//        GrLine dbGrLine =
//                transactionService.getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
//                        packBarcodes, lineNo, itemCode, authToken);
//        log.info("GrLine : " + dbGrLine);
//        return new ResponseEntity<>(dbGrLine, HttpStatus.OK);
//    }

    @ApiOperation(response = GrLine.class, value = "Search GrLine") // label for swagger
    @PostMapping("/findGrLine")
    public GrLine[] findGrLine(@RequestBody SearchGrLine searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLine(searchGrLine, authToken);
    }

    @ApiOperation(response = GrLine.class, value = "Update GrLine") // label for swagger
    @RequestMapping(value = "/grline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchGrLine(@RequestParam Long lineNo, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                         @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode,
                                         @RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode,
                                         @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody GrLine updateGrLine)
            throws IllegalAccessException, InvocationTargetException {
        GrLine updatedGrLine =
                transactionService.updateGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                        packBarcodes, lineNo, itemCode, loginUserID, updateGrLine, authToken);
        return new ResponseEntity<>(updatedGrLine, HttpStatus.OK);
    }


    /*
     * --------------------------------PutAwayHeader---------------------------------
     */
    @ApiOperation(response = PutAwayHeader.class, value = "Get all PutAwayHeader details") // label for swagger
    @GetMapping("/putawayheader")
    public ResponseEntity<?> getPutAwayHeaders(@RequestParam String authToken) throws Exception {
        PutAwayHeader[] putAwayNumberList = transactionService.getPutAwayHeaders(authToken);
        return new ResponseEntity<>(putAwayNumberList, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader New") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeaderNew")
    public PutAwayHeader[] findPutAwayHeaderNew(@RequestBody SearchPutAwayHeader searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findPutAwayHeaderNew(searchPutAwayHeader, authToken);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
    @RequestMapping(value = "/putawayheader", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayHeader updatePutAwayHeader, @RequestParam String loginUserID,
                                                @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayHeader updatedPutAwayHeader =
                transactionService.updatePutAwayHeader(warehouseId, preInboundNo,
                        refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin, updatePutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(updatedPutAwayHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader Bulk") // label for swagger
    @RequestMapping(value = "/putawayheader/bulkupdate", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPutAwayHeader(@Valid @RequestBody List<PutAwayHeaderV2> updatePutAwayHeader, @RequestParam String loginUserID,
                                                @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayHeader[] updatedPutAwayHeader =
                transactionService.updatePutAwayHeaderV2(updatePutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(updatedPutAwayHeader, HttpStatus.OK);
    }

    /*
     * --------------------------------PutAwayLine---------------------------------
     */
    @ApiOperation(response = PutAwayLine.class, value = "Get all PutAwayLine details") // label for swagger
    @GetMapping("/putawayline/confirmedStorageBin")
    public ResponseEntity<?> getPutAwayLines(@RequestParam String authToken) throws Exception {
        PutAwayLine[] confirmedStorageBinList = transactionService.getPutAwayLines(authToken);
        return new ResponseEntity<>(confirmedStorageBinList, HttpStatus.OK);
    }


    /*
     * --------------------------------InventoryMovement---------------------------------
     */
    @ApiOperation(response = InventoryMovement.class, value = "Get all InventoryMovement details") // label for swagger
    @GetMapping("/inventorymovement")
    public ResponseEntity<?> getInventoryMovements(@RequestParam String authToken) throws Exception {
        InventoryMovement[] movementTypeList = transactionService.getInventoryMovements(authToken);
        return new ResponseEntity<>(movementTypeList, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Get a InventoryMovement") // label for swagger
    @GetMapping("/inventorymovement/{movementType}")
    public ResponseEntity<?> getInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId, @RequestParam Long submovementType,
                                                  @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo,
                                                  @RequestParam String authToken) throws Exception {
        InventoryMovement dbInventoryMovement = transactionService.getInventoryMovement(warehouseId, movementType, submovementType,
                packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, authToken);
        log.info("InventoryMovement : " + dbInventoryMovement);
        return new ResponseEntity<>(dbInventoryMovement, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Search InventoryMovement") // label for swagger
    @PostMapping("/inventorymovement/findInventoryMovement")
    public InventoryMovement[] findInventoryMovement(@RequestBody SearchInventoryMovement searchInventoryMovement,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.findInventoryMovement(searchInventoryMovement, authToken);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Create InventoryMovement") // label for swagger
    @PostMapping("/inventorymovement")
    public ResponseEntity<?> postInventoryMovement(@Valid @RequestBody InventoryMovement newInventoryMovement, @RequestParam String loginUserID,
                                                   @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InventoryMovement createdInventoryMovement = transactionService.createInventoryMovement(newInventoryMovement, loginUserID, authToken);
        return new ResponseEntity<>(createdInventoryMovement, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Update InventoryMovement") // label for swagger
    @RequestMapping(value = "/inventorymovement/{movementType}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInventoryMovement(@PathVariable Long movementType, @RequestParam String languageId,
                                                    @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam Long submovementType,
                                                    @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo,
                                                    @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody InventoryMovement updateInventoryMovement)
            throws IllegalAccessException, InvocationTargetException {
        InventoryMovement updatedInventoryMovement =
                transactionService.updateInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, updateInventoryMovement,
                        loginUserID, authToken);
        return new ResponseEntity<>(updatedInventoryMovement, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryMovement.class, value = "Delete InventoryMovement") // label for swagger
    @DeleteMapping("/inventorymovement/{movementType}")
    public ResponseEntity<?> deleteInventoryMovement(@PathVariable Long movementType, @RequestParam String warehouseId, @RequestParam Long submovementType,
                                                     @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String batchSerialNumber, @RequestParam String movementDocumentNo,
                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInventoryMovement(warehouseId, movementType, submovementType, packBarcodes, itemCode, batchSerialNumber, movementDocumentNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------Inventory---------------------------------
     */
    @ApiOperation(response = Inventory.class, value = "Get all Inventory details") // label for swagger
    @GetMapping("/inventory")
    public ResponseEntity<?> getInventorys(@RequestParam String authToken) throws Exception {
        Inventory[] stockTypeIdList = transactionService.getInventorys(authToken);
        return new ResponseEntity<>(stockTypeIdList, HttpStatus.OK);
    }

    @ApiOperation(response = Inventory.class, value = "Get a Inventory") // label for swagger
    @GetMapping("/inventory/{stockTypeId}")
    public ResponseEntity<?> getInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId,
                                          @RequestParam String packBarcodes, @RequestParam String itemCode, @RequestParam String storageBin,
                                          @RequestParam Long specialStockIndicatorId, @RequestParam String authToken) throws Exception {
        Inventory dbInventory =
                transactionService.getInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, authToken);
        log.info("Inventory : " + dbInventory);
        return new ResponseEntity<>(dbInventory, HttpStatus.OK);
    }

    @ApiOperation(response = Inventory.class, value = "Get a Inventory For Transfer") // label for swagger
    @GetMapping("/inventory/transfer")
    public ResponseEntity<?> getInventory(@RequestParam String warehouseId, @RequestParam String packBarcodes,
                                          @RequestParam String itemCode, @RequestParam String storageBin, @RequestParam String authToken) throws Exception {
        Inventory inventory =
                transactionService.getInventory(warehouseId, packBarcodes, itemCode, storageBin, authToken);
        log.info("Inventory : " + inventory);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }


    //SQL Query - New
    @ApiOperation(response = Inventory.class, value = "Search Inventory New") // label for swagger
    @PostMapping("/inventory/findInventoryNew")
    public Inventory[] findInventoryNew(@RequestBody SearchInventory searchInventory,
                                        @RequestParam String authToken) throws Exception {
        return transactionService.findInventoryNew(searchInventory, authToken);
    }

    @ApiOperation(response = Inventory.class, value = "Search Inventory by quantity validation") // label for swagger
    @PostMapping("/get-all-validated-inventory")
    public Inventory[] getQuantityValidatedInventory(@RequestBody SearchInventory searchInventory,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.getQuantityValidatedInventory(searchInventory, authToken);
    }


    @ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
    @PostMapping("/inventory/findInventory/pagination")
    public Page<Inventory> findInventory(@RequestBody SearchInventory searchInventory,
                                         @RequestParam(defaultValue = "0") Integer pageNo,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(defaultValue = "itemCode") String sortBy,
                                         @RequestParam String authToken) throws Exception {
        return transactionService.findInventory(searchInventory, pageNo, pageSize, sortBy, authToken);
    }



    /*
     * --------------------------------InHouseTransferHeader---------------------------------
     */
    @ApiOperation(response = InhouseTransferHeader.class, value = "Get all InHouseTransferHeader details")
    // label for swagger
    @GetMapping("/inhousetransferheader")
    public ResponseEntity<?> getInHouseTransferHeaders(@RequestParam String authToken) throws Exception {
        InhouseTransferHeader[] transferNumberList = transactionService.getInhouseTransferHeaders(authToken);
        return new ResponseEntity<>(transferNumberList, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Get a InHouseTransferHeader") // label for swagger
    @GetMapping("/inhousetransferheader/{transferNumber}")
    public ResponseEntity<?> getInHouseTransferHeader(@PathVariable String transferNumber, @RequestParam String warehouseId,
                                                      @RequestParam Long transferTypeId, @RequestParam String authToken) throws Exception {
        InhouseTransferHeader dbInHouseTransferHeader =
                transactionService.getInhouseTransferHeader(warehouseId, transferNumber, transferTypeId, authToken);
        log.info("InHouseTransferHeader : " + dbInHouseTransferHeader);
        return new ResponseEntity<>(dbInHouseTransferHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader") // label for swagger
    @PostMapping("/inhousetransferheader/findInHouseTransferHeader")
    public InhouseTransferHeader[] findInHouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInHouseTransferHeader(searchInHouseTransferHeader, authToken);
    }

    //Stream
    @ApiOperation(response = InhouseTransferHeader.class, value = "Search InHouseTransferHeader New")
    // label for swagger
    @PostMapping("/inhousetransferheader/findInHouseTransferHeaderNew")
    public InhouseTransferHeader[] findInHouseTransferHeaderNew(@RequestBody SearchInhouseTransferHeader searchInHouseTransferHeader,
                                                                @RequestParam String authToken) throws Exception {
        return transactionService.findInHouseTransferHeaderNew(searchInHouseTransferHeader, authToken);
    }

    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader") // label for swagger
    @PostMapping("/inhousetransferheader")
    public ResponseEntity<?> postInHouseTransferHeader(@Valid @RequestBody InhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID,
                                                       @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InhouseTransferHeader createdInHouseTransferHeader = transactionService.createInhouseTransferHeader(newInHouseTransferHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }

    /*
     * --------------------------------InHouseTransferLine---------------------------------
     */
    @ApiOperation(response = InhouseTransferLine.class, value = "Get all InHouseTransferLine details")
    // label for swagger
    @GetMapping("/inhousetransferline")
    public ResponseEntity<?> getInHouseTransferLines(@RequestParam String authToken) throws Exception {
        InhouseTransferLine[] transferNumberList = transactionService.getInhouseTransferLines(authToken);
        return new ResponseEntity<>(transferNumberList, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Get a InHouseTransferLine") // label for swagger
    @GetMapping("/inhousetransferline/{transferNumber}")
    public ResponseEntity<?> getInHouseTransferLine(@PathVariable String transferNumber, @RequestParam String warehouseId,
                                                    @RequestParam String sourceItemCode, @RequestParam String authToken) throws Exception {
        InhouseTransferLine dbInHouseTransferLine =
                transactionService.getInhouseTransferLine(warehouseId, transferNumber, sourceItemCode, authToken);
        log.info("InHouseTransferLine : " + dbInHouseTransferLine);
        return new ResponseEntity<>(dbInHouseTransferLine, HttpStatus.OK);
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Search InhouseTransferLine") // label for swagger
    @PostMapping("/inhousetransferline/findInhouseTransferLine")
    public InhouseTransferLine[] findInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine,
                                                         @RequestParam String authToken) throws Exception {
        return transactionService.findInhouseTransferLine(searchInhouseTransferLine, authToken);
    }

    @ApiOperation(response = InhouseTransferLine.class, value = "Create InHouseTransferLine") // label for swagger
    @PostMapping("/inhousetransferline")
    public ResponseEntity<?> postInHouseTransferLine(@Valid @RequestBody InhouseTransferLine newInHouseTransferLine, @RequestParam String loginUserID,
                                                     @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InhouseTransferLine createdInHouseTransferLine = transactionService.createInhouseTransferLine(newInHouseTransferLine, loginUserID, authToken);
        return new ResponseEntity<>(createdInHouseTransferLine, HttpStatus.OK);
    }


    //-----------------------------------------ORDER APIs---------------------------------------------------------
    @ApiOperation(response = InboundOrder.class, value = "Get all InboundOrder Sucess Orders") // label for swagger
    @GetMapping("/orders/inbound/success")
    public ResponseEntity<?> getAll(@RequestParam String authToken) {
        InboundOrder[] inboundOrderList = transactionService.getInboundOrders(authToken);
        return new ResponseEntity<>(inboundOrderList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundOrder.class, value = "Get inbound Order by id ") // label for swagger
    @GetMapping("/orders/inbound/{orderId}")
    public ResponseEntity<?> getInboundOrdersById(@PathVariable String orderId, @RequestParam String authToken) {
        InboundOrder orders = transactionService.getInboundOrderById(orderId, authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = InboundIntegrationLog.class, value = "Get Failed Orders") // label for swagger
    @GetMapping("/orders/inbound/failed")
    public ResponseEntity<?> getFailedInbounOrders(@RequestParam String authToken) throws Exception {
        InboundIntegrationLog[] orders = transactionService.getFailedInboundOrders(authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //Find InBoundOrder
    @ApiOperation(response = InboundOrderV2[].class, value = "Find InboundOrderV2")//label for swagger
    @PostMapping("/findInboundOrder")
    public ResponseEntity<?> findInboundOrderV2(@RequestBody FindInboundOrderV2 findInboundOrderV2,
                                                @RequestParam String authToken) {
        InboundOrderV2[] inboundOrderV2 = transactionService.findInboundOrderV2(findInboundOrderV2, authToken);
        return new ResponseEntity<>(inboundOrderV2, HttpStatus.OK);
    }

    /*
     * --------------------------------ContainerReceipt-V2-------------------------------------------------------------
     */

//    //Get
//    @ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
//    @GetMapping("/containerreceipt/{containerReceiptNo}")
//    public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
//                                                 @RequestParam String plantId, @RequestParam String languageId,
//                                                 @RequestParam String warehouseId, @RequestParam String authToken) throws java.text.ParseException {
//        ContainerReceiptV2 containerreceipt =
//                transactionService.getContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo, authToken);
//        log.info("ContainerReceipt : " + containerreceipt);
//        return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
//    }

    //Get
    @ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
    @GetMapping("/containerreceipt/{containerReceiptNo}")
    public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                 @RequestParam String loginUserID,
                                                 @RequestParam String warehouseId, @RequestParam String authToken) throws java.text.ParseException {
        ContainerReceiptV2 containerreceipt =
                transactionService.getContainerReceiptV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, containerReceiptNo, loginUserID, authToken);
        log.info("ContainerReceipt : " + containerreceipt);
        return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = ContainerReceiptV2.class, value = "Search ContainerReceipt V2") // label for swagger
    @PostMapping("/containerreceipt/findContainerReceipt")
    public ContainerReceiptV2[] findContainerReceiptV2(@RequestBody SearchContainerReceiptV2 searchContainerReceipt,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findContainerReceiptV2(searchContainerReceipt, authToken);
    }

    //Create
    @ApiOperation(response = ContainerReceiptV2.class, value = "Create ContainerReceipt V2") // label for swagger
    @PostMapping("/containerreceipt")
    public ResponseEntity<?> postContainerReceiptV2(@Valid @RequestBody ContainerReceiptV2 newContainerReceipt, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        ContainerReceiptV2 createdContainerReceipt = transactionService.createContainerReceiptV2(newContainerReceipt, loginUserID, authToken);
        return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = ContainerReceiptV2.class, value = "Update ContainerReceiptV2") // label for swagger
    @RequestMapping(value = "/containerreceipt/{containerReceiptNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
                                                     @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                     @Valid @RequestBody ContainerReceiptV2 updateContainerReceipt, @RequestParam String loginUserID,
                                                     @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        ContainerReceiptV2 updatedContainerReceipt =
                transactionService.updateContainerReceiptV2(companyCode, plantId, languageId, warehouseId,
                        containerReceiptNo, loginUserID, updateContainerReceipt, authToken);
        return new ResponseEntity<>(updatedContainerReceipt, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = ContainerReceiptV2.class, value = "Delete ContainerReceiptV2") // label for swagger
    @DeleteMapping("/containerreceipt/{containerReceiptNo}")
    public ResponseEntity<?> deleteContainerReceiptV2(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
                                                      @RequestParam String plantId, @RequestParam String languageId,
                                                      @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                      @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteContainerReceiptV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, containerReceiptNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------Pre-InboundHeader-V2-------------------------------------------------------------
     */

    //Get
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Get a PreInboundHeader V2") // label for swagger
    @GetMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> getPreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                   @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundHeaderV2 preinboundheader = transactionService.getPreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Search PreInboundHeader v2") // label for swagger
    @PostMapping("/preinboundheader/findPreInboundHeader")
    public PreInboundHeaderV2[] findPreInboundHeaderV2(@RequestBody SearchPreInboundHeaderV2 searchPreInboundHeader,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundHeaderV2(searchPreInboundHeader, authToken);
    }

    //Get
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Get a PreInboundHeader With Status=24 V2")
    // label for swagger
    @GetMapping("/preinboundheader/inboundconfirm")
    public ResponseEntity<?> getPreInboundHeaderV2(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundHeaderV2[] preinboundheader =
                transactionService.getPreInboundHeaderWithStatusIdV2(warehouseId, companyCode, plantId, languageId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    //Create
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Create PreInboundHeaderV2") // label for swagger
    @PostMapping("/preinboundheader")
    public ResponseEntity<?> postPreInboundHeaderV2(@Valid @RequestBody PreInboundHeaderV2 newPreInboundHeader, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PreInboundHeaderV2 createdPreInboundHeader = transactionService.createPreInboundHeaderV2(newPreInboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Update PreInboundHeader V2") // label for swagger
    @PatchMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                     @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                     @Valid @RequestBody PreInboundHeaderV2 updatePreInboundHeader,
                                                     @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundHeaderV2 createdPreInboundHeader =
                transactionService.updatePreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId,
                        loginUserID, updatePreInboundHeader, authToken);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Delete PreInboundHeader V2") // label for swagger
    @DeleteMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                      @RequestParam String companyCode, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String loginUserID,
                                                      @RequestParam String authToken) {
        transactionService.deletePreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------Preinbound-Line-V2-------------------------------------------------------------
     */

    //Create
    @ApiOperation(response = PreInboundLineV2.class, value = "Insertion of BOM item V2") // label for swagger
    @PostMapping("/preinboundline/bom")
    public ResponseEntity<?> postPreInboundLineBOMV2(@RequestParam String preInboundNo, @RequestParam String warehouseId,
                                                     @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                     @RequestParam String refDocNumber, @RequestParam String itemCode,
                                                     @RequestParam Long lineNo, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundLineV2[] createdPreInboundLine =
                transactionService.createPreInboundLineBOMV2(preInboundNo, warehouseId, refDocNumber,
                        companyCode, plantId, languageId, itemCode,
                        lineNo, loginUserID, authToken);
        return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
    }

    //Get
    @ApiOperation(response = PreInboundLineV2.class, value = "Get a PreInboundLine V2") // label for swagger
    @GetMapping("/preinboundline/{preInboundNo}")
    public ResponseEntity<?> getPreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundLine[] preinboundline = transactionService.getPreInboundLineV2(preInboundNo, authToken);
        log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
    }

    //Patch
    @ApiOperation(response = PreInboundLineV2.class, value = "Patch PreInboundLine V2") // label for swagger
    @PatchMapping("/preinboundline/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String refDocNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
                                                   @Valid @RequestBody PreInboundLineV2 updatePreInboundLine, @RequestParam String loginUserID,
                                                   @RequestParam String authToken) throws java.text.ParseException {
        PreInboundLine preinboundline = transactionService.patchPreInboundLineV2(
                companyCode, plantId, warehouseId, languageId, preInboundNo, refDocNumber, lineNo, itemCode, loginUserID, updatePreInboundLine, authToken);
        log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
    }

//==============================================InboundHeader=V2==================================================

    //Find
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Search InboundHeader V2") // label for swagger
    @PostMapping("/inboundheader/findInboundHeader")
    public InboundHeaderEntityV2[] findInboundHeaderV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderV2(searchInboundHeader, authToken);
    }

    //Find-Stream
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Search InboundHeader Stream V2") // label for swagger
    @PostMapping("/inboundheader/findInboundHeader/stream")
    public InboundHeaderEntityV2[] findInboundHeaderstreamV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader,
                                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderStreamV2(searchInboundHeader, authToken);
    }

    //replaceASN
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Replace ASN V2") // label for swagger
    @GetMapping("/inboundheader/replaceASN")
    public ResponseEntity<?> replaceASNV2(@RequestParam String refDocNumber, @RequestParam String preInboundNo,
                                          @RequestParam String asnNumber, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.replaceASNV2(refDocNumber, preInboundNo, asnNumber, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Get a InboundHeader") // label for swagger
    @GetMapping("/inboundheader/{refDocNumber}")
    public ResponseEntity<?> getInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String authToken) throws Exception {
        InboundHeaderEntityV2 dbInboundHeader = transactionService.getInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, authToken);
        log.info("InboundHeader : " + dbInboundHeader);
        return new ResponseEntity<>(dbInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Confirm") // label for swagger
    @PatchMapping("/inboundheader/confirmIndividual")
    public ResponseEntity<?> patchInboundHeaderConfirmV2(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                         @RequestParam String companyCode, @RequestParam String plantId,
                                                         @RequestParam String languageId, @RequestParam String refDocNumber,
                                                         @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderConfirmV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Partial Confirm") // label for swagger
    @PatchMapping("/inboundheader/partialConfirmIndividual")
    public ResponseEntity<?> patchInboundHeaderPartialConfirmV2(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                                @RequestParam String companyCode, @RequestParam String plantId,
                                                                @RequestParam String languageId, @RequestParam String refDocNumber,
                                                                @RequestParam String loginUserID, @RequestParam String authToken) {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderPartialConfirmV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Partial Confirm with InboundLines Input") // label for swagger
    @PostMapping("/inboundheader/confirmIndividual/partial")
    public ResponseEntity<?> patchInboundHeaderPartialWithInboundLinesConfirmV2(@RequestBody List<InboundLineV2> inboundLineList, @RequestParam String warehouseId,
                                                                                @RequestParam String preInboundNo, @RequestParam String companyCode, @RequestParam String plantId,
                                                                                @RequestParam String languageId, @RequestParam String refDocNumber,
                                                                                @RequestParam String loginUserID, @RequestParam String authToken) {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderWithIbLinePartialConfirmV2(inboundLineList, companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Update InboundHeader V2") // label for swagger
    @RequestMapping(value = "/inboundheader/{refDocNumber}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCode, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken,
                                                  @Valid @RequestBody InboundHeaderV2 updateInboundHeader)
            throws IllegalAccessException, InvocationTargetException {
        InboundHeaderEntityV2 updatedInboundHeader =
                transactionService.updateInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader, authToken);
        return new ResponseEntity<>(updatedInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Delete InboundHeader V2") // label for swagger
    @DeleteMapping("/inboundheader/{refDocNumber}")
    public ResponseEntity<?> deleteInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId,
                                                   @RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------InboundLine-V2--------------------------------
     */
    @ApiOperation(response = InboundLineV2.class, value = "Get all InboundLine details V2") // label for swagger
    @GetMapping("/inboundline")
    public ResponseEntity<?> getInboundLinesV2(@RequestParam String authToken) throws Exception {
        InboundLineV2[] lineNoList = transactionService.getInboundLinesV2(authToken);
        return new ResponseEntity<>(lineNoList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Get a InboundLine V2") // label for swagger
    @GetMapping("/inboundline/{lineNo}")
    public ResponseEntity<?> getInboundLineV2(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                              @RequestParam String preInboundNo, @RequestParam String itemCode,
                                              @RequestParam String authToken) throws Exception {
        InboundLineV2 dbInboundLine =
                transactionService.getInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, authToken);
        log.info("InboundLine : " + dbInboundLine);
        return new ResponseEntity<>(dbInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Search InboundLine V2") // label for swagger
    @PostMapping("/inboundline/findInboundLine")
    public InboundLineV2[] findInboundLineV2(@RequestBody SearchInboundLineV2 searchInboundLine,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInboundLineV2(searchInboundLine, authToken);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Create InboundLine V2") // label for swagger
    @PostMapping("/inboundline")
    public ResponseEntity<?> postInboundLineV2(@Valid @RequestBody InboundLineV2 newInboundLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InboundLineV2 createdInboundLine = transactionService.createInboundLineV2(newInboundLine, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Update InboundLine V2") // label for swagger
    @RequestMapping(value = "/inboundline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInboundLineV2(@RequestParam Long lineNo, @RequestParam String languageId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID,
                                                @RequestParam String authToken, @Valid @RequestBody InboundLineV2 updateInboundLine)
            throws IllegalAccessException, InvocationTargetException {
        InboundLineV2 updatedInboundLine =
                transactionService.updateInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber,
                        preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine, authToken);
        return new ResponseEntity<>(updatedInboundLine, HttpStatus.OK);
    }
    //Batch Update Process
    @ApiOperation(response = InboundLineV2.class, value = "Batch Update InboundLines V2") // label for swagger
    @RequestMapping(value = "/inboundline/batchUpdateInboundLines", method = RequestMethod.PATCH)
    public ResponseEntity<?> batchUpdateInboundLineV2(@RequestParam String loginUserID, @RequestParam String authToken,
                                                      @Valid @RequestBody List<InboundLineV2> updateInboundLines) {
        InboundLineV2[] updatedInboundLines = transactionService.batchUpdateInboundLineV2(updateInboundLines, loginUserID, authToken);
        return new ResponseEntity<>(updatedInboundLines, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Delete InboundLine V2") // label for swagger
    @DeleteMapping("/inboundline/{lineNo}")
    public ResponseEntity<?> deleteInboundLineV2(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String warehouseId,
                                                 @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInboundLineV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


// -------------------------------------StagingHeader-V2--------------------------------------------------------------------------------------------

    //Get
    @ApiOperation(response = StagingHeaderV2.class, value = "Get a StagingHeader V2") // label for swagger
    @GetMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> getStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String authToken) throws java.text.ParseException {
        StagingHeaderV2 stagingheader = transactionService.getStagingHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, stagingNo, authToken);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = StagingHeaderV2.class, value = "Search StagingHeader V2") // label for swagger
    @PostMapping("/stagingheader/findStagingHeader")
    public StagingHeaderV2[] findStagingHeaderV2(@RequestBody SearchStagingHeaderV2 searchStagingHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findStagingHeaderV2(searchStagingHeader, authToken);
    }

    //Create
    @ApiOperation(response = StagingHeaderV2.class, value = "Create StagingHeader V2") // label for swagger
    @PostMapping("/stagingheader")
    public ResponseEntity<?> postStagingHeaderV2(@Valid @RequestBody StagingHeaderV2 newStagingHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeaderV2 createdStagingHeader = transactionService.createStagingHeaderV2(newStagingHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = StagingHeaderV2.class, value = "Update StagingHeader V2") // label for swagger
    @PatchMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> patchStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId,
                                                  @RequestParam String companyCode, @RequestParam String plantId,
                                                  @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                  @RequestParam String refDocNumber, @Valid @RequestBody StagingHeaderV2 updateStagingHeader,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeaderV2 createdStagingHeader =
                transactionService.updateStagingHeaderV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, stagingNo, loginUserID,
                        updateStagingHeader, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = StagingHeaderV2.class, value = "Delete StagingHeader V2") // label for swagger
    @DeleteMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> deleteStagingHeaderV2(@PathVariable String stagingNo, @RequestParam String languageId,
                                                   @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                   @RequestParam String refDocNumber, @RequestParam String loginUserID,
                                                   @RequestParam String authToken) {
        transactionService.deleteStagingHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, stagingNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------StagingLine-V2---------------------------------------------------------------------
     */

    //Get
    @ApiOperation(response = StagingLineEntityV2.class, value = "Get a StagingLine V2") // label for swagger
    @GetMapping("/stagingline/{lineNo}")
    public ResponseEntity<?> getStagingLine(@PathVariable Long lineNo, @RequestParam String companyCode,
                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                            @RequestParam String refDocNumber, @RequestParam String stagingNo, @RequestParam String palletCode,
                                            @RequestParam String caseCode, @RequestParam String preInboundNo,
                                            @RequestParam String itemCode, @RequestParam String authToken) throws java.text.ParseException {
        StagingLineEntityV2 dbStagingLine =
                transactionService.getStagingLineV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, stagingNo, palletCode,
                        caseCode, lineNo, itemCode, authToken);
        log.info("StagingLine : " + dbStagingLine);
        return new ResponseEntity<>(dbStagingLine, HttpStatus.OK);
    }

//    Find
    @ApiOperation(response = StagingLineEntityV2.class, value = "Search StagingLine V2") // label for swagger
    @PostMapping("/stagingline/findStagingLine")
    public StagingLineEntityV2[] findStagingLineV2(@RequestBody SearchStagingLineV2 searchStagingLine,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findStagingLineV2(searchStagingLine, authToken);
    }

    //Create
    @ApiOperation(response = StagingLineEntityV2.class, value = "Create StagingLine V2") // label for swagger
    @PostMapping("/stagingline")
    public ResponseEntity<?> postStagingLineV2(@Valid @RequestBody List<PreInboundLineV2> newStagingLine,
                                               @RequestParam String warehouseId, @RequestParam String companyCodeId,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        StagingLineEntityV2[] createdStagingLine =
                transactionService.createStagingLineV2(newStagingLine, warehouseId, companyCodeId, plantId, languageId, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine V2") // label for swagger
    @RequestMapping(value = "/stagingline/{lineNo}", method = RequestMethod.PATCH)
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

    //Update
    @ApiOperation(response = StagingLineUpdate.class, value = "Update ExpiryMfr")
    @PostMapping(value = "/stagingline/updateExpiryMfr")
    public ResponseEntity<?>updateExpiryMfr(@RequestBody List<StagingLineUpdate> input, @RequestParam String authToken){
      StagingLineUpdate[] updatedStagingLine=
              transactionService.updateExpiryMfr(input, authToken);
      return  new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }


    //Update
    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine V2") // label for swagger
    @PatchMapping("/stagingline/caseConfirmation")
    public ResponseEntity<?> patchStagingLineForCaseConfirmationV2(@RequestBody List<CaseConfirmation> caseConfirmations, @RequestParam String companyCode,
                                                                   @RequestParam String plantId, @RequestParam String languageId,
                                                                   @RequestParam String caseCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingLineEntityV2[] createdStagingLine =
                transactionService.caseConfirmationV2(companyCode, plantId, languageId, caseConfirmations, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = StagingLineEntityV2.class, value = "Delete StagingLine V2") // label for swagger
    @DeleteMapping("/stagingline/{lineNo}")
    public ResponseEntity<?> deleteStagingLine(@PathVariable Long lineNo, @RequestParam String companyCode,
                                               @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                               @RequestParam String stagingNo, @RequestParam String palletCode,
                                               @RequestParam String caseCode, @RequestParam String preInboundNo,
                                               @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteStagingLineV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo,
                itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Delete
    @ApiOperation(response = StagingLineEntityV2.class, value = "Delete StagingLine V2") // label for swagger
    @DeleteMapping("/stagingline/{lineNo}/cases")
    public ResponseEntity<?> deleteCasesV2(@PathVariable Long lineNo, @RequestParam String preInboundNo,
                                           @RequestParam String caseCode, @RequestParam String itemCode,
                                           @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteCasesV2(preInboundNo, lineNo, itemCode, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //AssignHHT
    @ApiOperation(response = StagingLineEntityV2.class, value = "AssignHHTUser StagingLine V2") // label for swagger
    @PatchMapping("/stagingline/assignHHTUser")
    public ResponseEntity<?> assignHHTUserV2(@RequestBody List<AssignHHTUser> assignHHTUsers, @RequestParam String companyCode,
                                             @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String assignedUserId, @RequestParam String loginUserID,
                                             @RequestParam String authToken) throws IllegalAccessException,
            InvocationTargetException {
        StagingLineEntityV2[] updatedStagingLine =
                transactionService.assignHHTUserV2(companyCode, plantId, languageId, assignHHTUsers, assignedUserId, loginUserID, authToken);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }

    /*
     * --------------------------------GrHeader-V2-------------------------------------------------------------------
     */

    //Get
    @ApiOperation(response = GrHeaderV2.class, value = "Get a GrHeader V2") // label for swagger
    @GetMapping("/grheader/{goodsReceiptNo}")
    public ResponseEntity<?> getGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                           @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                           @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                           @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String authToken) throws java.text.ParseException {
        GrHeaderV2 dbGrHeader =
                transactionService.getGrHeaderV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode,
                        caseCode, authToken);
        log.info("GrHeader : " + dbGrHeader);
        return new ResponseEntity<>(dbGrHeader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = GrHeaderV2.class, value = "Search GrHeader V2") // label for swagger
    @PostMapping("/grheader/findGrHeader")
    public GrHeaderV2[] findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeaderV2(searchGrHeader, authToken);
    }

    //Create
    @ApiOperation(response = GrHeaderV2.class, value = "Create GrHeader V2") // label for swagger
    @PostMapping("/grheader")
    public ResponseEntity<?> postGrHeaderV2(@Valid @RequestBody GrHeaderV2 newGrHeader, @RequestParam String loginUserID,
                                            @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrHeaderV2 createdGrHeader = transactionService.createGrHeaderV2(newGrHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = GrHeaderV2.class, value = "Update GrHeader V2") // label for swagger
    @RequestMapping(value = "/grheader/{goodsReceiptNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                             @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                             @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                             @RequestParam String palletCode, @RequestParam String caseCode,
                                             @Valid @RequestBody GrHeaderV2 updateGrHeader,
                                             @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        GrHeaderV2 updatedGrHeader =
                transactionService.updateGrHeaderV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                        palletCode, caseCode, loginUserID, updateGrHeader, authToken);
        return new ResponseEntity<>(updatedGrHeader, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = GrHeaderV2.class, value = "Delete GrHeader V2") // label for swagger
    @DeleteMapping("/grheader/{goodsReceiptNo}")
    public ResponseEntity<?> deleteGrHeaderV2(@PathVariable String goodsReceiptNo, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                              @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID,
                                              @RequestParam String authToken) {
        transactionService.deleteGrHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                palletCode, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------GrLine-V2----------------------------------------------------------------------------
     */

    //Get
    @ApiOperation(response = GrLineV2.class, value = "Get a GrLine V2") // label for swagger
    @GetMapping("/grline/{lineNo}")
    public ResponseEntity<?> getGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                         @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                         @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                         @RequestParam String itemCode, @RequestParam String authToken) throws java.text.ParseException {
        GrLineV2 dbGrLine =
                transactionService.getGrLineV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                        packBarcodes, lineNo, itemCode, authToken);
        log.info("GrLine : " + dbGrLine);
        return new ResponseEntity<>(dbGrLine, HttpStatus.OK);
    }

    //Get
    // PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
    @ApiOperation(response = GrLineV2.class, value = "Get a GrLine V2") // label for swagger
    @GetMapping("/grline/{lineNo}/putawayline")
    public ResponseEntity<?> getGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                         @RequestParam String plantId, @RequestParam String languageId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                         @RequestParam String packBarcodes, @RequestParam String itemCode,
                                         @RequestParam String authToken) throws java.text.ParseException {
        GrLineV2[] grline = transactionService.getGrLineV2(companyCode, plantId, languageId, preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode, authToken);
        log.info("GrLine : " + grline);
        return new ResponseEntity<>(grline, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine V2") // label for swagger
    @PostMapping("/grline/findGrLine")
    public GrLineV2[] findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLineV2(searchGrLine, authToken);
    }
    //Find
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine - SQL for report V2") // label for swagger
    @PostMapping("/grline/findGrLineNew")
    public GrLineV2[] findGrLineNewV2(@RequestBody SearchGrLineV2 searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLineSQLV2(searchGrLine, authToken);
    }

    //Create
    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/grline")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine, @RequestParam String loginUserID,
                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrLineV2[] createdGrLine = transactionService.createGrLineV2(newGrLine, loginUserID, authToken);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = GrLineV2.class, value = "Update GrLine V2") // label for swagger
    @RequestMapping(value = "/grline/{lineNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode,
                                           @RequestParam String plantId, @RequestParam String languageId,
                                           @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                           @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                           @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                           @RequestParam String itemCode, @Valid @RequestBody GrLineV2 updateGrLine,
                                           @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        GrLineV2 updatedGrLine =
                transactionService.updateGrLineV2(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                        packBarcodes, lineNo, itemCode, loginUserID, updateGrLine, authToken);
        return new ResponseEntity<>(updatedGrLine, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = GrLineV2.class, value = "Delete GrLine V2") // label for swagger
    @DeleteMapping("/grline/{lineNo}")
    public ResponseEntity<?> deleteGrLineV2(@PathVariable Long lineNo, @RequestParam String companyCode, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                            @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode,
                                            @RequestParam String caseCode, @RequestParam String packBarcodes, @RequestParam String itemCode,
                                            @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteGrLineV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode,
                caseCode, packBarcodes, lineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-----------------PACK_BARCODE-GENERATION-V2--------------------------------------------------------------------------
    @ApiOperation(response = GrLineV2.class, value = "Get PackBarcodes V2") // label for swagger
    @GetMapping("/grline/packBarcode")
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

    /*
     * --------------------------------PutAwayHeader-V2-------------------------------
     */

    //Get
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/putawayheader/{putAwayNumber}")
    public ResponseEntity<?> getPutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                @RequestParam String proposedStorageBin, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayHeaderV2 dbPutAwayHeader = transactionService.getPutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin, authToken);
        log.info("PutAwayHeader : " + dbPutAwayHeader);
        return new ResponseEntity<>(dbPutAwayHeader, HttpStatus.OK);
    }

    //Get
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/putawayheader/{refDocNumber}/inboundreversal/asn")
    public ResponseEntity<?> getPutAwayHeaderForASNV2(@RequestParam String companyCode, @RequestParam String plantId,
                                                      @RequestParam String languageId, @PathVariable String refDocNumber, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayHeaderV2[] putawayheader = transactionService.getPutAwayHeaderV2(companyCode, plantId, languageId, refDocNumber, authToken);
        log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Search PutAwayHeader V2") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeader")
    public PutAwayHeaderV2[] findPutAwayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findPutAwayHeaderV2(searchPutAwayHeader, authToken);
    }

    //Create
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Create PutAwayHeader V2") // label for swagger
    @PostMapping("/putawayheader")
    public ResponseEntity<?> postPutAwayHeaderV2(@Valid @RequestBody PutAwayHeaderV2 newPutAwayHeader, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayHeaderV2 createdPutAwayHeader = transactionService.createPutAwayHeaderV2(newPutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
    }

    //Upadte
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
    @RequestMapping(value = "/putawayheader/{putAwayNumber}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId,
                                                @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayHeaderV2 updatePutAwayHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayHeaderV2 updatedPutAwayHeader =
                transactionService.updatePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, preInboundNo,
                        refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes,
                        putAwayNumber, proposedStorageBin, updatePutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(updatedPutAwayHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
    @PatchMapping("/putawayheader/{refDocNumber}/reverse")
    public ResponseEntity<?> patchPutAwayHeaderV2(@PathVariable String refDocNumber, @RequestParam String packBarcodes, @RequestParam String warehouseId,
                                                  @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.updatePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, packBarcodes, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PutAwayHeaderV2.class, value = "batch PutAwayHeaderV2 Reversal") // label for swagger
    @PatchMapping("/putawayheader/reverse/batch")
    public ResponseEntity<?> batchPutAwayHeaderReversalV2(@RequestBody List<InboundReversalInput> inboundReversalInput,
                                                          @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.batchPutAwayHeaderReversalV2(inboundReversalInput, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Delete PutAwayHeader V2") // label for swagger
    @DeleteMapping("/putawayheader/{putAwayNumber}")
    public ResponseEntity<?> deletePutAwayHeaderV2(@PathVariable String putAwayNumber, @RequestParam String warehouseId,
                                                   @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                   @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                                   @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                                   @RequestParam String proposedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId,
                preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber,
                proposedStorageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------PutAwayLine-V2-------------------------------
     */

    //Get
    @ApiOperation(response = PutAwayLineV2.class, value = "Get a PutAwayLine V2") // label for swagger
    @GetMapping("/putawayline/{confirmedStorageBin}")
    public ResponseEntity<?> getPutAwayLineV2(@PathVariable List<String> confirmedStorageBin, @RequestParam String companyCode,
                                              @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                              @RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
                                              @RequestParam String proposedStorageBin, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayLineV2 dbPutAwayLine = transactionService.getPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode,
                proposedStorageBin, confirmedStorageBin, authToken);
        log.info("PutAwayLine : " + dbPutAwayLine);
        return new ResponseEntity<>(dbPutAwayLine, HttpStatus.OK);
    }

    //Get
    @ApiOperation(response = PutAwayLineV2.class, value = "Get a PutAwayLine V2") // label for swagger
    @GetMapping("/putawayline/{refDocNumber}/inboundreversal/palletId")
    public ResponseEntity<?> getPutAwayLineForInboundLineV2(@PathVariable String refDocNumber, @RequestParam String companyCode,
                                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayLineV2[] putawayline = transactionService.getPutAwayLineV2(companyCode, plantId, languageId, refDocNumber, authToken);
        log.info("PutAwayLine : " + putawayline);
        return new ResponseEntity<>(putawayline, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PutAwayLineV2.class, value = "Search PutAwayLine V2") // label for swagger
    @PostMapping("/putawayline/findPutAwayLine")
    public PutAwayLineV2[] findPutAwayLineV2(@RequestBody SearchPutAwayLineV2 searchPutAwayLine,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findPutAwayLineV2(searchPutAwayLine, authToken);
    }

    //Create
    @ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
    @PostMapping("/putawayline")
    public ResponseEntity<?> postPutAwayLineV2(@Valid @RequestBody List<PutAwayLineV2> newPutAwayLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayLineV2[] createdPutAwayLine = transactionService.createPutAwayLineV2(newPutAwayLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PutAwayLineV2.class, value = "Update PutAwayLine V2") // label for swagger
    @RequestMapping(value = "/putawayline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPutAwayLineV2(@PathVariable String confirmedStorageBin, @RequestParam String languageId,
                                                @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String warehouseId,
                                                @RequestParam String goodsReceiptNo, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String putAwayNumber, @RequestParam Long lineNo, @RequestParam String itemCode,
                                                @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayLineV2 updatePutAwayLine,
                                                @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLineV2 updatedPutAwayLine =
                transactionService.updatePutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                        goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode,
                        proposedStorageBin, confirmedStorageBin, updatePutAwayLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPutAwayLine, HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = PutAwayLine.class, value = "Delete PutAwayLine") // label for swagger
    @DeleteMapping("/putawayline/{confirmedStorageBin}")
    public ResponseEntity<?> deletePutAwayLineV2(@PathVariable String confirmedStorageBin, @RequestParam String languageId,
                                                 @RequestParam String companyCode, @RequestParam String plantId,
                                                 @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
                                                 @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                 @RequestParam String putAwayNumber, @RequestParam Long lineNo,
                                                 @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, proposedStorageBin,
                confirmedStorageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //V2
    @ApiOperation(response = InventoryV2.class, value = "Search Inventory V2") // label for swagger
    @PostMapping("/inventory/findInventory")
    public InventoryV2[] findInventoryV2(@RequestBody SearchInventoryV2 searchInventory,
                                         @RequestParam String authToken) throws Exception {
        return transactionService.findInventoryV2(searchInventory, authToken);
    }

    @ApiOperation(response = InventoryV2.class, value = "Create Inventory V2") // label for swagger
    @PostMapping("/inventory")
    public ResponseEntity<?> postInventoryV2(@Valid @RequestBody InventoryV2 newInventory, @RequestParam String loginUserID,
                                           @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InventoryV2 createdInventory = transactionService.createInventoryV2(newInventory, loginUserID, authToken);
        return new ResponseEntity<>(createdInventory, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryV2.class, value = "Update Inventory V2") // label for swagger
    @RequestMapping(value = "/inventory/{stockTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInventoryV2(@PathVariable Long stockTypeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String packBarcodes,
                                              @RequestParam String itemCode, @RequestParam String manufacturerName, @RequestParam String storageBin,
                                              @RequestParam Long specialStockIndicatorId, @RequestParam String loginUserID,
                                              @RequestParam String authToken, @Valid @RequestBody InventoryV2 updateInventory)
            throws IllegalAccessException, InvocationTargetException {
        InventoryV2 updatedInventory =
                transactionService.updateInventoryV2(companyCodeId, plantId, languageId, warehouseId, packBarcodes, itemCode,
                        manufacturerName, storageBin, stockTypeId, specialStockIndicatorId, updateInventory, loginUserID, authToken);
        return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryV2.class, value = "Delete Inventory V2") // label for swagger
    @DeleteMapping("/inventory/{stockTypeId}")
    public ResponseEntity<?> deleteInventoryV2(@PathVariable Long stockTypeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String packBarcodes,
                                               @RequestParam String itemCode, @RequestParam String manufacturerName, @RequestParam String storageBin,
                                               @RequestParam Long specialStockIndicatorId, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInventoryV2(companyCodeId, plantId, languageId, warehouseId, manufacturerName,
                packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------Almailem--Orders------------------------------------------------------------------

    @ApiOperation(response = ASNV2.class, value = "Create ASNV2 Order") // label for swagger
    @PostMapping("/warehouse/inbound/asn/v4")
    public ResponseEntity<?> postASN(@Valid @RequestBody ASNV2 asnv2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        try {
            WarehouseApiResponse createdSO = transactionService.postASNV2(asnv2, authToken);
            return new ResponseEntity<>(createdSO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while Inbound Order Post : " + e.toString());
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

    @ApiOperation(response = StockReceiptHeader.class, value = "Create StockReceipt Order") // label for swagger
    @PostMapping("/warehouse/inbound/stockReceipt")
    public ResponseEntity<?> postStockReceipt(@Valid @RequestBody StockReceiptHeader stockReceipt, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdStockReceipt = transactionService.postStockReceipt(stockReceipt, authToken);
        return new ResponseEntity<>(createdStockReceipt, HttpStatus.OK);
    }

    // File Upload - Orders
    @ApiOperation(response = ASNV2.class, value = "ASN V2") // label for swagger
    @PostMapping("/warehouse/inbound/asn/upload")
    public ResponseEntity<?> postWarehouseInboundAsnUploadV2(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                             @RequestParam String languageId, @RequestParam String warehouseId,
                                                             @RequestParam String loginUserID, @RequestParam Long inboundOrderTypeId,
                                                             @RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response=null;
        String profile = companyCodeId;
        log.info("Inbound Upload In {}",profile);
        if(profile != null){
            if(companyCodeId.equals("21")){
                response = fileStorageService.processAsnOrders(companyCodeId, plantId, languageId, warehouseId, inboundOrderTypeId, loginUserID,file);
            }
            else if(companyCodeId.equals("1400")){
                 response = fileStorageService.processAsnOrdersV6(companyCodeId, plantId, languageId, warehouseId, loginUserID, file);
            }
            else if(companyCodeId.equals("1500")){
                response = fileStorageService.processInboundOrdersV5(companyCodeId, plantId, languageId, warehouseId, inboundOrderTypeId, loginUserID, file);
            } else if (companyCodeId.equals("2000")) {
                response = fileStorageService.processInboundOrdersV7(companyCodeId, plantId, languageId, warehouseId, inboundOrderTypeId,loginUserID, file);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // File Upload - Orders
    @ApiOperation(response = ShipmentOrder.class, value = "Inter Warehouse Transfer V2") // label for swagger
    @PostMapping("/warehouse/inbound/interWarehouseTransferIn/upload/v2")
    public ResponseEntity<?> postinterWarehouseTransferInUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processInterWarehouseTransferInOrders(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // SO ReturnV2
    @ApiOperation(response = SaleOrderReturnV2.class, value = "Sales Order Return V2") // label for swagger
    @PostMapping("/warehouse/inbound/soreturn/v2")
    public ResponseEntity<?> postSoReturnV2(@RequestBody SaleOrderReturnV2 saleOrderReturnV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        log.info("saleOrderReturnV2 input ------> {}", saleOrderReturnV2);
        try {
            WarehouseApiResponse createdSO = transactionService.postSOReturnV2(saleOrderReturnV2, authToken);
            return new ResponseEntity<>(createdSO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while Inbound Return Order Post : " + e.toString());
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

    // B2b Transfer IN
    @ApiOperation(response = B2bTransferIn.class, value = "B2b Transfer In") // label for swagger
    @PostMapping("/warehouse/inbound/b2bTransferIn")
    public ResponseEntity<?> postB2bTransferIn(@Valid @RequestBody B2bTransferIn b2bTransferIn, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postB2bTransferIn(b2bTransferIn, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }

    // B2b Transfer IN
    @ApiOperation(response = InterWarehouseTransferInV2.class, value = "InterWarehouse TransferIn V2")
    // label for swagger
    @PostMapping("/warehouse/inbound/interWarehouseTransferIn/v2")
    public ResponseEntity<?> postInterWarehouseTransferInV2(@Valid @RequestBody InterWarehouseTransferInV2 interWarehouseTransferInV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postInterWarehouseTransferInV2(interWarehouseTransferInV2, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }

    //====================================================InhouseTransferHeader============================================
    @ApiOperation(response = InhouseTransferHeader.class, value = "Create InHouseTransferHeader V2")
    // label for swagger
    @PostMapping("/inhousetransferheader/v2")
    public ResponseEntity<?> postInHouseTransferHeaderV2(@Valid @RequestBody InhouseTransferHeader newInHouseTransferHeader, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InhouseTransferHeader createdInHouseTransferHeader = transactionService.createInhouseTransferHeaderV2(newInHouseTransferHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdInHouseTransferHeader, HttpStatus.OK);
    }
    /*
     * --------------------------------DeliveryHeader---------------------------------
     */

    // GET ALL
    @ApiOperation(response = DeliveryHeader.class, value = "Get all DeliveryHeader details") // label for swagger
    @GetMapping("/deliveryheader")
    public ResponseEntity<?> getAllDeliveryHeader(@RequestParam String authToken) {
        DeliveryHeader[] deliveryHeaders = transactionService.getAllDeliveryHeader(authToken);
        return new ResponseEntity<>(deliveryHeaders, HttpStatus.OK);
    }

    // GET
    @ApiOperation(response = DeliveryHeader.class, value = "Get a DeliveryHeader") // label for swagger
    @GetMapping("/deliveryheader/{deliveryNo}")
    public ResponseEntity<?> getHhtUser(@PathVariable Long deliveryNo, @RequestParam String companyCodeId,
                                        @RequestParam String languageId, @RequestParam String plantId,
                                        @RequestParam String warehouseId, @RequestParam String authToken) {
        DeliveryHeader dbDeliveryHeader
                = transactionService.getDeliveryHeader(warehouseId, deliveryNo, companyCodeId, languageId, plantId, authToken);

        log.info("DeliveryHeader : " + dbDeliveryHeader);
        return new ResponseEntity<>(dbDeliveryHeader, HttpStatus.OK);
    }

    // CREATE
    @ApiOperation(response = DeliveryHeader.class, value = "Create DeliveryHeader") // label for swagger
    @PostMapping("/deliveryheader")
    public ResponseEntity<?> postDeliveryHeader(@Valid @RequestBody AddDeliveryHeader addDeliveryHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {

        DeliveryHeader createdDeliveryHeader
                = transactionService.createDeliveryHeader(addDeliveryHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdDeliveryHeader, HttpStatus.OK);
    }

    // UPDATE
    @ApiOperation(response = DeliveryHeader.class, value = "Update DeliveryHeader") // label for swagger
    @RequestMapping(value = "/deliveryheader/{deliveryNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchDeliveryHeader(@PathVariable Long deliveryNo, @RequestParam String warehouseId,
                                                 @RequestParam String companyCodeId, @RequestParam String languageId,
                                                 @RequestParam String plantId, @RequestParam String loginUserID,
                                                 @Valid @RequestBody UpdateDeliveryHeader updateDeliveryHeader,
                                                 @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {

        DeliveryHeader dbDeliveryHeader = transactionService.updateDeliveryHeader(warehouseId, deliveryNo, companyCodeId,
                languageId, plantId, loginUserID, updateDeliveryHeader, authToken);
        return new ResponseEntity<>(dbDeliveryHeader, HttpStatus.OK);
    }

    // DELETE
    @ApiOperation(response = DeliveryHeader.class, value = "Delete DeliveryHeader") // label for swagger
    @DeleteMapping("/deliveryheader/{deliveryNo}")
    public ResponseEntity<?> deleteHhtUser(@PathVariable Long deliveryNo, @RequestParam String warehouseId,
                                           @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String loginUserID,
                                           @RequestParam String authToken) {

        transactionService.deleteDeliveryHeader(warehouseId, deliveryNo, companyCodeId,
                languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //FIND
    @ApiOperation(response = DeliveryHeader[].class, value = "Find DeliveryHeader")//label for swagger
    @PostMapping("/deliveryheader/findDeliveryHeader")
    public DeliveryHeader[] findDeliveryHeader(@RequestBody SearchDeliveryHeader searchDeliveryHeader,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findDeliveryHeader(searchDeliveryHeader, authToken);
    }


    /*
     * --------------------------------DeliveryLine---------------------------------
     */

    // GET ALL
    @ApiOperation(response = DeliveryLine.class, value = "Get all DeliveryLine details") // label for swagger
    @GetMapping("/deliveryline")
    public ResponseEntity<?> getAllDeliveryLine(@RequestParam String authToken) {
        DeliveryLine[] deliveryLines = transactionService.getAllDeliveryLine(authToken);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    // CREATE DeliveryLine
    @ApiOperation(response = DeliveryLine.class, value = "Create DeliveryLine") // label for swagger
    @PostMapping("/deliveryline")
    public ResponseEntity<?> postDeliveryLine(@Valid @RequestBody List<AddDeliveryLine> addDeliveryLine,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {

        DeliveryLine[] createDeliveryLine = transactionService.createDeliveryLine(addDeliveryLine, loginUserID, authToken);
        return new ResponseEntity<>(createDeliveryLine, HttpStatus.OK);
    }


    // GET
    @ApiOperation(response = DeliveryLine.class, value = "Get a DeliveryLine") // label for swagger
    @GetMapping("/deliveryline/{deliveryNo}")
    public ResponseEntity<?> getHhtUser(@PathVariable Long deliveryNo, @RequestParam String companyCodeId,
                                        @RequestParam String invoiceNumber, @RequestParam String refDocNumber,
                                        @RequestParam String languageId, @RequestParam String plantId,
                                        @RequestParam String itemCode, @RequestParam Long lineNumber,
                                        @RequestParam String warehouseId, @RequestParam String authToken) {

        DeliveryLine dbDeliveryLine = transactionService.getDeliveryLine(warehouseId, deliveryNo, itemCode, lineNumber,
                companyCodeId, languageId, plantId, invoiceNumber, refDocNumber, authToken);

        log.info("DeliveryNo : " + dbDeliveryLine);
        return new ResponseEntity<>(dbDeliveryLine, HttpStatus.OK);
    }

    //PATCH
    @ApiOperation(response = DeliveryLine.class, value = "Update DeliveryLine") // label for swagger
    @RequestMapping(value = "/deliveryline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchDeliveryLine(@RequestParam String loginUserID, @RequestParam String authToken,
                                               @Valid @RequestBody List<UpdateDeliveryLine> updateDeliveryLine)
            throws IllegalAccessException, InvocationTargetException {

        DeliveryLine[] dbDeliveryLine = transactionService.updateDeliveryLine(loginUserID, updateDeliveryLine, authToken);
        return new ResponseEntity<>(dbDeliveryLine, HttpStatus.OK);
    }

    // DELETE
    @ApiOperation(response = DeliveryLine.class, value = "Delete DeliveryLine") // label for swagger
    @DeleteMapping("/deliveryline/{deliveryNo}")
    public ResponseEntity<?> deleteDeliveryLine(@PathVariable Long deliveryNo, @RequestParam String warehouseId,
                                                @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String itemCode,
                                                @RequestParam Long lineNumber, @RequestParam String invoiceNumber,
                                                @RequestParam String refDocNumber, @RequestParam String loginUserID,
                                                @RequestParam String authToken) {

        transactionService.deleteDeliveryLine(warehouseId, deliveryNo, itemCode, lineNumber, refDocNumber, invoiceNumber,
                companyCodeId, languageId, plantId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //FIND
    @ApiOperation(response = DeliveryLine[].class, value = "Find DeliveryLine")//label for swagger
    @PostMapping("/deliveryline/findDeliveryLine")
    public DeliveryLine[] findDeliveryLine(@RequestBody SearchDeliveryLine searchDeliveryLine,
                                           @RequestParam String authToken) throws Exception {
        return transactionService.findDeliveryLine(searchDeliveryLine, authToken);
    }

    // GET
    @ApiOperation(response = DeliveryLineCount.class, value = "Get a DeliveryLine Count") // label for swagger
    @GetMapping("/deliveryline/count")
    public ResponseEntity<?> getDeliveryLineCount( @RequestParam String companyCodeId, @RequestParam String languageId,
                                                   @RequestParam String plantId, @RequestParam String warehouseId,
                                                   @RequestParam String driverId, @RequestParam String authToken) {

        DeliveryLineCount dbDeliveryLine = transactionService.getDeliveryLineCount(companyCodeId, plantId, languageId, warehouseId, driverId, authToken);
        return new ResponseEntity<>(dbDeliveryLine, HttpStatus.OK);
    }

    //FIND
    @ApiOperation(response = DeliveryLineCount.class, value = "Find DeliveryLineCount")//label for swagger
    @PostMapping("/deliveryline/findDeliveryLineCount")
    public DeliveryLineCount findDeliveryLineCount(@RequestBody FindDeliveryLineCount findDeliveryLineCount,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findDeliveryLineCount(findDeliveryLineCount, authToken);
    }

    @ApiOperation(response = ASN.class, value = "Create ASN Order") // label for swagger
    @PostMapping("/warehouse/inbound/asn")
    public ResponseEntity<?> postASN(@Valid @RequestBody ASN asn, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postASN(asn, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }

    //----------------------Orders------------------------------------------------------------------
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/warehouse/outbound/so")
    public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody ShipmentOrder shipmenOrder, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postSO(shipmenOrder, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }



    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/reports/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReportNew(@RequestParam String asnNumber, @RequestParam String preInboundNo, @RequestParam String companyCodeId,
                                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                            @RequestParam String authToken) throws Exception {
        ReceiptConfimationReport receiptConfimationReport = transactionService.getReceiptConfimationReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId, authToken);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PreInboundLineOutputV2.class, value = "Search PreInboundLine v2") // label for swagger
    @PostMapping("/preinboundline/findPreInboundLine")
    public PreInboundLineOutputV2[] findPreInboundLineV2(@RequestBody SearchPreInboundLineV2 searchPreInboundLine,
                                                         @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundLineV2(searchPreInboundLine, authToken);
    }

    //--------------------------------CrossDock--------------------------------------------//

    //Create
    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/crossdock/grline")
    public ResponseEntity<?> postCrossDockGrLineV2(@Valid @RequestBody CrossDockGrLineOrderManagementLine crossDockGrLineOrderManagementLine, @RequestParam String loginUserID,
                                                   @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        CrossDockEntity crossDockEntity = transactionService.crossDockGrLineConfirm(crossDockGrLineOrderManagementLine, authToken, loginUserID);
        return new ResponseEntity<>(crossDockEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayLineV2.class, value = "Find Cross-Dock")
    @PostMapping("/find/cross-dock")
    public ResponseEntity<?> findCrossDock(@RequestBody MultiDbInput input, @RequestParam String authToken) {
        CrossDock crossDock = transactionService.findCrossDock(input, authToken);
        return new ResponseEntity<>(crossDock, HttpStatus.OK);
    }

//    @ApiOperation(response = PeriodicHeaderEntityV2[].class, value = "Search PeriodicHeader V2") // label for swagger
//    @PostMapping("/periodicheader/v2/findPeriodicHeaderWithLines")
//    public PeriodicHeaderEntityV2[] findPeriodicHeaderEntityV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
//                                                               @RequestParam String authToken) throws Exception {
//        return transactionService.findPeriodicHeaderEntityV2(searchPeriodicHeader, authToken);
//    }

//    @ApiOperation(response = ASNV2.class, value = "Create ASNV2 Order") // label for swagger
//    @PostMapping("/warehouse/inbound/asn/v4")
//    public ResponseEntity<?> postASN(@Valid @RequestBody ASNV2 asnv2, @RequestParam String authToken)
//            throws IllegalAccessException, InvocationTargetException {
//        ASNV2 createdSO = transactionService.postASNV2(asnv2, authToken);
//        return new ResponseEntity<>(createdSO, HttpStatus.OK);
//    }

    @ApiOperation(response = ASNV2.class, value = "Create ASNV2 Order") // label for swagger
    @PostMapping("/warehouse/inbound/asn/v2")
    public ResponseEntity<?> postASN_V4(@Valid @RequestBody ASNV2 asnv2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        try {
            WarehouseApiResponse createdSO  = transactionService.postASNV4(asnv2, authToken);
            return new ResponseEntity<>(createdSO, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception while Inbound Order Post : " + e.toString());
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

    //----------------------------------------Notification---------------------------------------------------------//

    //Find
    @ApiOperation(response = StorageBinDashBoardImpl.class, value = "Get Storage Bin Dashboard count - walkaroo V3") // label for swagger
    @PostMapping("/dashBoard/storageBinDashboard")
    public StorageBinDashBoardImpl[] getStorageBinDashBoard(@RequestBody StorageBinDashBoardInput storageBinDashBoardInput,
                                                            @RequestParam String authToken) throws Exception {
        return transactionService.getStorageBinDashBoard(storageBinDashBoardInput, authToken);
    }

    //----------------------------------------InboundManualConfirmationV7---------------------------------------

    @ApiOperation(response = PutAwayLine.class, value = "InboundConfirmValidation") // label for swagger
    @GetMapping("/inboundManualConfirmation")
    public ResponseEntity<?> getInboundConfirmationV7(@RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,@RequestParam String warehouseId,
                                                      @RequestParam String refDocNumber,@RequestParam String preInboundNo,@RequestParam String loginUserID) throws Exception {
        PutAwayLine putAwayLine =
                transactionService.getinboundConfirmValidationV7(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID);
        log.info("PutAwayline : " + putAwayLine);
        return new ResponseEntity<>(putAwayLine, HttpStatus.OK);
    }

    //Find InboundHeader with Line
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Search InboundHeader And InboundLine V2") // label for swagger
    @PostMapping("/inboundheader/findInboundHeaderWithLines/v2")
    public InboundHeaderEntityV2[] findInboundHeaderWithLinesV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader,
                                                                @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderWithLinesV2(searchInboundHeader, authToken);
    }
    
        //---------------------------------------------Inbound Reversal------------------------------------------------------------

    //Update
    @ApiOperation(response = PutAwayLineV2.class, value = "Inbound Reversal") // label for swagger
    @PatchMapping("/reports/inboundReversal")
    public ResponseEntity<?> inboundReversal(@RequestParam String companyCodeId,@RequestParam String plantId,
                                             @RequestParam String warehouseId,@RequestParam String refDocNumber,@RequestParam String preInboundNo, @RequestParam String authToken) {
        transactionService.inboundReversal(companyCodeId, plantId, warehouseId, refDocNumber, preInboundNo, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //=========================================Update=====================================================================
    @ApiOperation(response = InboundOrder.class, value = "Update InboundOrder details")
    @PatchMapping("/orders/update/inboundOrder")
    public ResponseEntity<?> updateInboundOrder(@RequestParam String orderId, @RequestBody UpdateInboundOrder updateInboundOrder, @RequestParam String authToken) throws Exception {
        InboundOrder inboundOrder = transactionService.updateInboundOrder(orderId, updateInboundOrder, authToken);
        return new ResponseEntity<>(inboundOrder, HttpStatus.OK);
    }

    //==================================================Delete=========================================================

    @ApiOperation(response = InboundOrder.class, value = " Delete InboundOrder details")
    @DeleteMapping("/orders/delete/inboundOrder")
    public ResponseEntity<?> deleteInboundOrder(@RequestParam String orderId, @RequestParam String authToken) throws Exception {
        transactionService.deleteInboundOrder(orderId, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
