package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.batch.scheduler.BatchJobScheduler;
import com.tekclover.wms.core.model.masters.ImPartner;
import com.tekclover.wms.core.model.transaction.*;
import com.tekclover.wms.core.model.warehouse.cyclecount.periodic.Periodic;
import com.tekclover.wms.core.model.warehouse.cyclecount.perpetual.Perpetual;
import com.tekclover.wms.core.model.warehouse.inbound.ASN;
import com.tekclover.wms.core.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.core.model.warehouse.inbound.almailem.*;
import com.tekclover.wms.core.model.warehouse.outbound.almailem.*;
import com.tekclover.wms.core.service.FileStorageService;
import com.tekclover.wms.core.service.ReportService;
import com.tekclover.wms.core.service.TransactionService;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-transaction-service")
@Api(tags = {"Transaction Service"}, value = "Transaction Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Transaction Modules")})
public class TransactionServiceController {

    @Autowired
    TransactionService transactionService;

    @Autowired
    ReportService reportService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    BatchJobScheduler batchJobScheduler;

    /*
     * Process the ASN Integraion data
     */
    @ApiOperation(response = PreInboundHeader.class, value = "Create ProcessASN") // label for swagger
    @PostMapping("/preinboundheader/processInboundReceived")
    public ResponseEntity<?> processASN(@RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.processInboundReceived(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * --------------------------------ContainerReceipt---------------------------------------------------------------
     */
    @ApiOperation(response = ContainerReceipt.class, value = "Get all ContainerReceipt details") // label for swagger
    @GetMapping("/containerreceipt")
    public ResponseEntity<?> getContainerReceipts(@RequestParam String authToken) throws Exception {
        ContainerReceipt[] containerReceiptNoList = transactionService.getContainerReceipts(authToken);
        return new ResponseEntity<>(containerReceiptNoList, HttpStatus.OK);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Get a ContainerReceipt") // label for swagger
    @GetMapping("/containerreceipt/{containerReceiptNo}")
    public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo,
                                                 @RequestParam String authToken) throws Exception {
        ContainerReceipt containerreceipt =
                transactionService.getContainerReceipt(containerReceiptNo, authToken);
        log.info("ContainerReceipt : " + containerreceipt);
        return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt") // label for swagger
    @PostMapping("/containerreceipt/findContainerReceipt")
    public ContainerReceipt[] findContainerReceipt(@RequestBody SearchContainerReceipt searchContainerReceipt,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findContainerReceipt(searchContainerReceipt, authToken);
    }

    //Streaming
    @ApiOperation(response = ContainerReceipt.class, value = "Search ContainerReceipt New") // label for swagger
    @PostMapping("/containerreceipt/findContainerReceiptNew")
    public ContainerReceipt[] findContainerReceiptNew(@RequestBody SearchContainerReceipt searchContainerReceipt,
                                                      @RequestParam String authToken) throws Exception {
        return transactionService.findContainerReceiptNew(searchContainerReceipt, authToken);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Create ContainerReceipt") // label for swagger
    @PostMapping("/containerreceipt")
    public ResponseEntity<?> postContainerReceipt(@Valid @RequestBody ContainerReceipt newContainerReceipt, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        ContainerReceipt createdContainerReceipt = transactionService.createContainerReceipt(newContainerReceipt, loginUserID, authToken);
        return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Update ContainerReceipt") // label for swagger
    @RequestMapping(value = "/containerreceipt/{containerReceiptNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchContainerReceipt(@PathVariable String containerReceiptNo,
                                                   @Valid @RequestBody ContainerReceipt updateContainerReceipt, @RequestParam String loginUserID,
                                                   @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        ContainerReceipt updatedContainerReceipt =
                transactionService.updateContainerReceipt(containerReceiptNo, loginUserID, updateContainerReceipt, authToken);
        return new ResponseEntity<>(updatedContainerReceipt, HttpStatus.OK);
    }

    @ApiOperation(response = ContainerReceipt.class, value = "Delete ContainerReceipt") // label for swagger
    @DeleteMapping("/containerreceipt/{containerReceiptNo}")
    public ResponseEntity<?> deleteContainerReceipt(@PathVariable String containerReceiptNo,
                                                    @RequestParam String warehouseId, @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                    @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteContainerReceipt(preInboundNo, refDocNumber, containerReceiptNo, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation(response = PreInboundHeader.class, value = "Get a PreInboundHeader") // label for swagger 
    @GetMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> getPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                 @RequestParam String authToken) throws Exception {
        PreInboundHeader preinboundheader = transactionService.getPreInboundHeader(preInboundNo, warehouseId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Search PreInboundHeader") // label for swagger
    @PostMapping("/preinboundheader/findPreInboundHeader")
    public PreInboundHeader[] findPreInboundHeader(@RequestBody SearchPreInboundHeader searchPreInboundHeader,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundHeader(searchPreInboundHeader, authToken);
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

    @ApiOperation(response = PreInboundHeader.class, value = "Create PreInboundHeader") // label for swagger
    @PostMapping("/preinboundheader")
    public ResponseEntity<?> postPreInboundHeader(@Valid @RequestBody PreInboundHeader newPreInboundHeader, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PreInboundHeader createdPreInboundHeader = transactionService.createPreInboundHeader(newPreInboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Update PreInboundHeader") // label for swagger
    @PatchMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> patchPreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                   @Valid @RequestBody PreInboundHeader updatePreInboundHeader, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundHeader createdPreInboundHeader =
                transactionService.updatePreInboundHeader(preInboundNo, warehouseId, loginUserID, updatePreInboundHeader, authToken);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeader.class, value = "Delete PreInboundHeader") // label for swagger
    @DeleteMapping("/preinboundheader/{preInboundNo}")
    public ResponseEntity<?> deletePreInboundHeader(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                    @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePreInboundHeader(preInboundNo, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
     * --------------------------------Preinbound-Line---------------------------------------------------------------
     */
    @ApiOperation(response = PreInboundLine.class, value = "Insertion of BOM item") // label for swagger
    @PostMapping("/preinboundline/bom")
    public ResponseEntity<?> postPreInboundLineBOM(@RequestParam String preInboundNo,
                                                   @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String itemCode,
                                                   @RequestParam Long lineNo, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PreInboundLine[] createdPreInboundLine =
                transactionService.createPreInboundLineBOM(preInboundNo, warehouseId, refDocNumber, itemCode,
                        lineNo, loginUserID, authToken);
        return new ResponseEntity<>(createdPreInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundLine.class, value = "Get a PreInboundLine") // label for swagger 
    @GetMapping("/preinboundline/{preInboundNo}")
    public ResponseEntity<?> getPreInboundLine(@PathVariable String preInboundNo, @RequestParam String authToken) throws Exception {
        PreInboundLine[] preinboundline = transactionService.getPreInboundLine(preInboundNo, authToken);
        log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
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

    @ApiOperation(response = InboundHeader.class, value = "Get a InboundHeader") // label for swagger
    @GetMapping("/inboundheader/{refDocNumber}")
    public ResponseEntity<?> getInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String authToken) throws Exception {
        InboundHeader dbInboundHeader = transactionService.getInboundHeader(warehouseId, refDocNumber, preInboundNo, authToken);
        log.info("InboundHeader : " + dbInboundHeader);
        return new ResponseEntity<>(dbInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Search InboundHeader") // label for swagger
    @PostMapping("/inboundheader/findInboundHeader")
    public InboundHeader[] findInboundHeader(@RequestBody SearchInboundHeader searchInboundHeader,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeader(searchInboundHeader, authToken);
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

    @ApiOperation(response = InboundHeader.class, value = "Update InboundHeader") // label for swagger
    @RequestMapping(value = "/inboundheader/{refDocNumber}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken,
                                                @Valid @RequestBody InboundHeader updateInboundHeader)
            throws IllegalAccessException, InvocationTargetException {
        InboundHeader updatedInboundHeader =
                transactionService.updateInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID, updateInboundHeader, authToken);
        return new ResponseEntity<>(updatedInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Replace ASN") // label for swagger
    @GetMapping("/inboundheader/replaceASN")
    public ResponseEntity<?> replaceASN(@RequestParam String refDocNumber, @RequestParam String preInboundNo,
                                        @RequestParam String asnNumber, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.replaceASN(refDocNumber, preInboundNo, asnNumber, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Inbound Header & Line Confirm") // label for swagger
    @PatchMapping("/inboundheader/confirmIndividual")
    public ResponseEntity<?> patchInboundHeaderConfirm(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                       @RequestParam String refDocNumber, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderConfirm(warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeader.class, value = "Delete InboundHeader") // label for swagger
    @DeleteMapping("/inboundheader/{refDocNumber}")
    public ResponseEntity<?> deleteInboundHeader(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                 @RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInboundHeader(warehouseId, refDocNumber, preInboundNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * --------------------------------InboundLine---------------------------------
     */
    @ApiOperation(response = InboundLine.class, value = "Get all InboundLine details") // label for swagger
    @GetMapping("/inboundline")
    public ResponseEntity<?> getInboundLines(@RequestParam String authToken) throws Exception {
        InboundLine[] lineNoList = transactionService.getInboundLines(authToken);
        return new ResponseEntity<>(lineNoList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLine.class, value = "Get a InboundLine") // label for swagger
    @GetMapping("/inboundline/{lineNo}")
    public ResponseEntity<?> getInboundLine(@PathVariable Long lineNo, @RequestParam String languageId, @RequestParam String companyCodeId,
                                            @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                            @RequestParam String preInboundNo, @RequestParam String itemCode,
                                            @RequestParam String authToken) throws Exception {
        InboundLine dbInboundLine =
                transactionService.getInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, authToken);
        log.info("InboundLine : " + dbInboundLine);
        return new ResponseEntity<>(dbInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLine.class, value = "Search InboundLine") // label for swagger
    @PostMapping("/inboundline/findInboundLine")
    public InboundLine[] findInboundLine(@RequestBody SearchInboundLine searchInboundLine,
                                         @RequestParam String authToken) throws Exception {
        return transactionService.findInboundLine(searchInboundLine, authToken);
    }

    @ApiOperation(response = InboundLine.class, value = "Create InboundLine") // label for swagger
    @PostMapping("/inboundline")
    public ResponseEntity<?> postInboundLine(@Valid @RequestBody InboundLine newInboundLine, @RequestParam String loginUserID,
                                             @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InboundLine createdInboundLine = transactionService.createInboundLine(newInboundLine, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLine.class, value = "Update InboundLine") // label for swagger
    @RequestMapping(value = "/inboundline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInboundLine(@RequestParam Long lineNo, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                              @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID,
                                              @RequestParam String authToken, @Valid @RequestBody InboundLine updateInboundLine)
            throws IllegalAccessException, InvocationTargetException {
        InboundLine updatedInboundLine =
                transactionService.updateInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, updateInboundLine, authToken);
        return new ResponseEntity<>(updatedInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLine.class, value = "Delete InboundLine") // label for swagger
    @DeleteMapping("/inboundline/{lineNo}")
    public ResponseEntity<?> deleteInboundLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                               @RequestParam String refDocNumber, @RequestParam String preInboundNo, @RequestParam String itemCode,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInboundLine(warehouseId, refDocNumber, preInboundNo, lineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // -------------------StagingHeader----------------------------------------------------------------------------------------------
    @ApiOperation(response = StagingHeader.class, value = "Get all StagingHeader details") // label for swagger
    @GetMapping("/stagingheader")
    public ResponseEntity<?> getStagingHeaders(@RequestParam String authToken) throws Exception {
        StagingHeader[] stagingheaderList = transactionService.getStagingHeaders(authToken);
        return new ResponseEntity<>(stagingheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Get a StagingHeader") // label for swagger 
    @GetMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> getStagingHeader(@PathVariable String stagingNo, @RequestParam String languageId,
                                              @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String authToken) throws Exception {
        StagingHeader stagingheader = transactionService.getStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, authToken);
        log.info("StagingHeader : " + stagingheader);
        return new ResponseEntity<>(stagingheader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Search StagingHeader") // label for swagger
    @PostMapping("/stagingheader/findStagingHeader")
    public StagingHeader[] findStagingHeader(@RequestBody SearchStagingHeader searchStagingHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findStagingHeader(searchStagingHeader, authToken);
    }

    //Stream
    @ApiOperation(response = StagingHeader.class, value = "Search StagingHeader New") // label for swagger
    @PostMapping("/stagingheader/findStagingHeaderNew")
    public StagingHeader[] findStagingHeaderNew(@RequestBody SearchStagingHeader searchStagingHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findStagingHeaderNew(searchStagingHeader, authToken);
    }

    @ApiOperation(response = StagingHeader.class, value = "Create StagingHeader") // label for swagger
    @PostMapping("/stagingheader")
    public ResponseEntity<?> postStagingHeader(@Valid @RequestBody StagingHeader newStagingHeader,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeader createdStagingHeader = transactionService.createStagingHeader(newStagingHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Update StagingHeader") // label for swagger
    @PatchMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> patchStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String refDocNumber,
                                                @Valid @RequestBody StagingHeader updateStagingHeader, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeader createdStagingHeader =
                transactionService.updateStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID,
                        updateStagingHeader, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    @ApiOperation(response = StagingHeader.class, value = "Delete StagingHeader") // label for swagger
    @DeleteMapping("/stagingheader/{stagingNo}")
    public ResponseEntity<?> deleteStagingHeader(@PathVariable String stagingNo, @RequestParam String warehouseId,
                                                 @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) {
        transactionService.deleteStagingHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation(response = StagingLine.class, value = "Get a StagingLine") // label for swagger
    @GetMapping("/stagingline/{lineNo}")
    public ResponseEntity<?> getStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                            @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                            @RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode,
                                            @RequestParam String authToken) throws Exception {
        StagingLineEntity dbStagingLine =
                transactionService.getStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo, itemCode, authToken);
        log.info("StagingLine : " + dbStagingLine);
        return new ResponseEntity<>(dbStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Search StagingLine") // label for swagger
    @PostMapping("/stagingline/findStagingLine")
    public StagingLineEntity[] findStagingLine(@RequestBody SearchStagingLine searchStagingLine,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findStagingLine(searchStagingLine, authToken);
    }

    @ApiOperation(response = StagingLine.class, value = "Create StagingLine") // label for swagger
    @PostMapping("/stagingline")
    public ResponseEntity<?> postStagingLine(@Valid @RequestBody List<StagingLine> newStagingLine,
                                             @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        StagingLineEntity[] createdStagingLine =
                transactionService.createStagingLine(newStagingLine, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
    @RequestMapping(value = "/stagingline/{lineNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                              @RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode,
                                              @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody StagingLineEntity updateStagingLine)
            throws IllegalAccessException, InvocationTargetException {
        StagingLineEntity updatedStagingLine =
                transactionService.updateStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode,
                        lineNo, itemCode, loginUserID, updateStagingLine, authToken);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Update StagingLine") // label for swagger
    @PatchMapping("/stagingline/caseConfirmation")
    public ResponseEntity<?> patchStagingLineForCaseConfirmation(@RequestBody List<CaseConfirmation> caseConfirmations,
                                                                 @RequestParam String caseCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingLineEntity[] createdStagingLine =
                transactionService.caseConfirmation(caseConfirmations, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingLine, HttpStatus.OK);
    }

    @ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
    @DeleteMapping("/stagingline/{lineNo}")
    public ResponseEntity<?> deleteStagingLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                               @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                               @RequestParam String caseCode, @RequestParam String palletCode, @RequestParam String itemCode,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteStagingLine(warehouseId, preInboundNo, refDocNumber, stagingNo, palletCode, caseCode, lineNo,
                itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = StagingLine.class, value = "Delete StagingLine") // label for swagger
    @DeleteMapping("/stagingline/{lineNo}/cases")
    public ResponseEntity<?> deleteCases(@PathVariable Long lineNo, @RequestParam String preInboundNo,
                                         @RequestParam String caseCode, @RequestParam String itemCode, @RequestParam String loginUserID,
                                         @RequestParam String authToken) {
        transactionService.deleteCases(preInboundNo, lineNo, itemCode, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = StagingLine.class, value = "AssignHHTUser StagingLine") // label for swagger
    @PatchMapping("/stagingline/assignHHTUser")
    public ResponseEntity<?> assignHHTUser(@RequestBody List<AssignHHTUser> assignHHTUsers, @RequestParam String assignedUserId,
                                           @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException,
            InvocationTargetException {
        log.info("hello");
        StagingLineEntity[] updatedStagingLine =
                transactionService.assignHHTUser(assignHHTUsers, assignedUserId, loginUserID, authToken);
        return new ResponseEntity<>(updatedStagingLine, HttpStatus.OK);
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

    @ApiOperation(response = GrHeader.class, value = "Get a GrHeader") // label for swagger
    @GetMapping("/grheader/{goodsReceiptNo}")
    public ResponseEntity<?> getGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId,
                                         @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                         @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String authToken) throws Exception {
        GrHeader dbGrHeader =
                transactionService.getGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode,
                        caseCode, authToken);
        log.info("GrHeader : " + dbGrHeader);
        return new ResponseEntity<>(dbGrHeader, HttpStatus.OK);
    }

    @ApiOperation(response = GrHeader.class, value = "Search GrHeader") // label for swagger
    @PostMapping("/grheader/findGrHeader")
    public GrHeader[] findGrHeader(@RequestBody SearchGrHeader searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeader(searchGrHeader, authToken);
    }

    //Stream - JPA
    @ApiOperation(response = GrHeader.class, value = "Search GrHeader New") // label for swagger
    @PostMapping("/grheader/findGrHeaderNew")
    public GrHeader[] findGrHeaderNew(@RequestBody SearchGrHeader searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeaderNew(searchGrHeader, authToken);
    }

    @ApiOperation(response = GrHeader.class, value = "Create GrHeader") // label for swagger
    @PostMapping("/grheader")
    public ResponseEntity<?> postGrHeader(@Valid @RequestBody GrHeader newGrHeader, @RequestParam String loginUserID,
                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrHeader createdGrHeader = transactionService.createGrHeader(newGrHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
    }

    @ApiOperation(response = GrHeader.class, value = "Update GrHeader") // label for swagger
    @RequestMapping(value = "/grheader/{goodsReceiptNo}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchGrHeader(@RequestParam String goodsReceiptNo, @RequestParam String warehouseId,
                                           @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                           @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID,
                                           @RequestParam String authToken, @Valid @RequestBody GrHeader updateGrHeader)
            throws IllegalAccessException, InvocationTargetException {
        GrHeader updatedGrHeader =
                transactionService.updateGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo,
                        palletCode, caseCode, loginUserID, updateGrHeader, authToken);
        return new ResponseEntity<>(updatedGrHeader, HttpStatus.OK);
    }

    @ApiOperation(response = GrHeader.class, value = "Delete GrHeader") // label for swagger
    @DeleteMapping("/grheader/{goodsReceiptNo}")
    public ResponseEntity<?> deleteGrHeader(@PathVariable String goodsReceiptNo, @RequestParam String warehouseId,
                                            @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String stagingNo,
                                            @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String loginUserID,
                                            @RequestParam String authToken) {
        transactionService.deleteGrHeader(warehouseId, preInboundNo, refDocNumber, stagingNo, goodsReceiptNo, palletCode, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger
    @GetMapping("/grline/{lineNo}")
    public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                       @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                       @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                       @RequestParam String itemCode, @RequestParam String authToken) throws Exception {
        GrLine dbGrLine =
                transactionService.getGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode,
                        packBarcodes, lineNo, itemCode, authToken);
        log.info("GrLine : " + dbGrLine);
        return new ResponseEntity<>(dbGrLine, HttpStatus.OK);
    }

    // PRE_IB_NO/REF_DOC_NO/PACK_BARCODE/IB_LINE_NO/ITM_CODE
    @ApiOperation(response = GrLine.class, value = "Get a GrLine") // label for swagger 
    @GetMapping("/grline/{lineNo}/putawayline")
    public ResponseEntity<?> getGrLine(@PathVariable Long lineNo, @RequestParam String preInboundNo,
                                       @RequestParam String refDocNumber, @RequestParam String packBarcodes, @RequestParam String itemCode,
                                       @RequestParam String authToken) throws Exception {
        GrLine[] grline = transactionService.getGrLine(preInboundNo, refDocNumber, packBarcodes, lineNo, itemCode, authToken);
        log.info("GrLine : " + grline);
        return new ResponseEntity<>(grline, HttpStatus.OK);
    }

    @ApiOperation(response = GrLine.class, value = "Search GrLine") // label for swagger
    @PostMapping("/findGrLine")
    public GrLine[] findGrLine(@RequestBody SearchGrLine searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLine(searchGrLine, authToken);
    }

    @ApiOperation(response = GrLine.class, value = "Create GrLine") // label for swagger
    @PostMapping("/grline")
    public ResponseEntity<?> postGrLine(@Valid @RequestBody List<AddGrLine> newGrLine, @RequestParam String loginUserID,
                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrLine[] createdGrLine = transactionService.createGrLine(newGrLine, loginUserID, authToken);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
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

    @ApiOperation(response = GrLine.class, value = "Delete GrLine") // label for swagger
    @DeleteMapping("/grline/{lineNo}")
    public ResponseEntity<?> deleteGrLine(@PathVariable Long lineNo, @RequestParam String warehouseId,
                                          @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo,
                                          @RequestParam String palletCode, @RequestParam String caseCode, @RequestParam String packBarcodes,
                                          @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteGrLine(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, lineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-----------------PACK_BARCODE-GENERATION----------------------------------------------------------------------------
    @ApiOperation(response = GrLine.class, value = "Get PackBarcodes") // label for swagger 
    @GetMapping("/grline/packBarcode")
    public ResponseEntity<?> getPackBarcode(@RequestParam Long acceptQty, @RequestParam Long damageQty,
                                            @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PackBarcode[] packBarcodes =
                transactionService.generatePackBarcode(acceptQty, damageQty, warehouseId, loginUserID, authToken);
        log.info("packBarcodes : " + packBarcodes);
        return new ResponseEntity<>(packBarcodes, HttpStatus.OK);
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

    @ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger
    @GetMapping("/putawayheader/{putAwayNumber}")
    public ResponseEntity<?> getPutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                              @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode,
                                              @RequestParam String packBarcodes, @RequestParam String proposedStorageBin, @RequestParam String authToken) throws Exception {
        PutAwayHeader dbPutAwayHeader = transactionService.getPutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber, proposedStorageBin, authToken);
        log.info("PutAwayHeader : " + dbPutAwayHeader);
        return new ResponseEntity<>(dbPutAwayHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Get a PutAwayHeader") // label for swagger
    @GetMapping("/putawayheader/{refDocNumber}/inboundreversal/asn")
    public ResponseEntity<?> getPutAwayHeaderForASN(@PathVariable String refDocNumber, @RequestParam String authToken) throws Exception {
        PutAwayHeader[] putawayheader = transactionService.getPutAwayHeader(refDocNumber, authToken);
        log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeader")
    public PutAwayHeader[] findPutAwayHeader(@RequestBody SearchPutAwayHeader searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findPutAwayHeader(searchPutAwayHeader, authToken);
    }

    //Stream
    @ApiOperation(response = PutAwayHeader.class, value = "Search PutAwayHeader New") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeaderNew")
    public PutAwayHeader[] findPutAwayHeaderNew(@RequestBody SearchPutAwayHeader searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findPutAwayHeaderNew(searchPutAwayHeader, authToken);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Create PutAwayHeader") // label for swagger
    @PostMapping("/putawayheader")
    public ResponseEntity<?> postPutAwayHeader(@Valid @RequestBody PutAwayHeader newPutAwayHeader, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayHeader createdPutAwayHeader = transactionService.createPutAwayHeader(newPutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
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


    @ApiOperation(response = PutAwayHeader.class, value = "Update PutAwayHeader") // label for swagger
    @PatchMapping("/putawayheader/{refDocNumber}/reverse")
    public ResponseEntity<?> patchPutAwayHeader(@PathVariable String refDocNumber, @RequestParam String packBarcodes,
                                                @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        transactionService.updatePutAwayHeader(refDocNumber, packBarcodes, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayHeader.class, value = "Delete PutAwayHeader") // label for swagger
    @DeleteMapping("/putawayheader/{putAwayNumber}")
    public ResponseEntity<?> deletePutAwayHeader(@PathVariable String putAwayNumber, @RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                 @RequestParam String refDocNumber, @RequestParam String goodsReceiptNo, @RequestParam String palletCode, @RequestParam String caseCode,
                                                 @RequestParam String packBarcodes, @RequestParam String proposedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePutAwayHeader(warehouseId, preInboundNo, refDocNumber, goodsReceiptNo, palletCode, caseCode, packBarcodes, putAwayNumber,
                proposedStorageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger
    @GetMapping("/putawayline/{confirmedStorageBin}")
    public ResponseEntity<?> getPutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
                                            @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo,
                                            @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String authToken) throws Exception {
        PutAwayLine dbPutAwayLine = transactionService.getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode,
                proposedStorageBin, confirmedStorageBin, authToken);
        log.info("PutAwayLine : " + dbPutAwayLine);
        return new ResponseEntity<>(dbPutAwayLine, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayLine.class, value = "Get a PutAwayLine") // label for swagger
    @GetMapping("/putawayline/{refDocNumber}/inboundreversal/palletId")
    public ResponseEntity<?> getPutAwayLineForInboundLine(@PathVariable String refDocNumber,
                                                          @RequestParam String authToken) throws Exception {
        PutAwayLine[] putawayline = transactionService.getPutAwayLine(refDocNumber, authToken);
        log.info("PutAwayLine : " + putawayline);
        return new ResponseEntity<>(putawayline, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayLine.class, value = "Search PutAwayLine") // label for swagger
    @PostMapping("/putawayline/findPutAwayLine")
    public PutAwayLine[] findPutAwayLine(@RequestBody SearchPutAwayLine searchPutAwayLine,
                                         @RequestParam String authToken) throws Exception {
        return transactionService.findPutAwayLine(searchPutAwayLine, authToken);
    }

    @ApiOperation(response = PutAwayLine.class, value = "Create PutAwayLine") // label for swagger
    @PostMapping("/putawayline")
    public ResponseEntity<?> postPutAwayLine(@Valid @RequestBody List<AddPutAwayLine> newPutAwayLine, @RequestParam String loginUserID,
                                             @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayLine[] createdPutAwayLine = transactionService.createPutAwayLine(newPutAwayLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayLine.class, value = "Update PutAwayLine") // label for swagger
    @RequestMapping(value = "/putawayline", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchPutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
                                              @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo,
                                              @RequestParam String itemCode, @RequestParam String proposedStorageBin, @Valid @RequestBody PutAwayLine updatePutAwayLine,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLine updatedPutAwayLine =
                transactionService.updatePutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode,
                        proposedStorageBin, confirmedStorageBin, updatePutAwayLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPutAwayLine, HttpStatus.OK);
    }

    @ApiOperation(response = PutAwayLine.class, value = "Delete PutAwayLine") // label for swagger
    @DeleteMapping("/putawayline/{confirmedStorageBin}")
    public ResponseEntity<?> deletePutAwayLine(@PathVariable String confirmedStorageBin, @RequestParam String languageId, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String goodsReceiptNo,
                                               @RequestParam String preInboundNo, @RequestParam String refDocNumber, @RequestParam String putAwayNumber, @RequestParam Long lineNo,
                                               @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePutAwayLine(languageId, companyCodeId, plantId, warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber, lineNo, itemCode, proposedStorageBin,
                confirmedStorageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    @ApiOperation(response = Inventory.class, value = "Search Inventory") // label for swagger
    @PostMapping("/inventory/findInventory")
    public Inventory[] findInventory(@RequestBody SearchInventory searchInventory,
                                     @RequestParam String authToken) throws Exception {
        return transactionService.findInventory(searchInventory, authToken);
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

    @ApiOperation(response = Inventory.class, value = "Create Inventory") // label for swagger
    @PostMapping("/inventory")
    public ResponseEntity<?> postInventory(@Valid @RequestBody Inventory newInventory, @RequestParam String loginUserID,
                                           @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        Inventory createdInventory = transactionService.createInventory(newInventory, loginUserID, authToken);
        return new ResponseEntity<>(createdInventory, HttpStatus.OK);
    }

    @ApiOperation(response = Inventory.class, value = "Update Inventory") // label for swagger
    @RequestMapping(value = "/inventory/{stockTypeId}", method = RequestMethod.PATCH)
    public ResponseEntity<?> patchInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, @RequestParam String packBarcodes,
                                            @RequestParam String itemCode, @RequestParam String storageBin, @RequestParam Long specialStockIndicatorId,
                                            @RequestParam String loginUserID, @RequestParam String authToken, @Valid @RequestBody Inventory updateInventory)
            throws IllegalAccessException, InvocationTargetException {
        Inventory updatedInventory =
                transactionService.updateInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, updateInventory,
                        loginUserID, authToken);
        return new ResponseEntity<>(updatedInventory, HttpStatus.OK);
    }

    @ApiOperation(response = Inventory.class, value = "Delete Inventory") // label for swagger
    @DeleteMapping("/inventory/{stockTypeId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long stockTypeId, @RequestParam String warehouseId, @RequestParam String packBarcodes,
                                             @RequestParam String itemCode, @RequestParam String storageBin, @RequestParam Long specialStockIndicatorId,
                                             @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteInventory(warehouseId, packBarcodes, itemCode, storageBin, stockTypeId, specialStockIndicatorId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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

    /*
     * --------------------------------PreOutboundHeader---------------------------------
     */
    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader") // label for swagger
    @PostMapping("/preoutboundheader/findPreOutboundHeader")
    public PreOutboundHeader[] findPreOutboundHeader(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.findPreOutboundHeader(searchPreOutboundHeader, authToken);
    }

    //Stream
    @ApiOperation(response = PreOutboundHeader.class, value = "Search PreOutboundHeader New") // label for swagger
    @PostMapping("/preoutboundheader/findPreOutboundHeaderNew")
    public PreOutboundHeader[] findPreOutboundHeaderNew(@RequestBody SearchPreOutboundHeader searchPreOutboundHeader,
                                                        @RequestParam String authToken) throws Exception {
        return transactionService.findPreOutboundHeaderNew(searchPreOutboundHeader, authToken);
    }

    /*
     * -------------------PreOutboundLine---------------------------------------------------
     */
    @ApiOperation(response = PreOutboundLine.class, value = "Search PreOutboundLine") // label for swagger
    @PostMapping("/preoutboundline/findPreOutboundLine")
    public PreOutboundLine[] findPreOutboundLine(@RequestBody SearchPreOutboundLine searchPreOutboundLine,
                                                 @RequestParam String authToken) throws Exception {
        return transactionService.findPreOutboundLine(searchPreOutboundLine, authToken);
    }

    /*
     * --------------------------------OrderMangementLine---------------------------------
     */
    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine") // label for swagger
    @PostMapping("/ordermanagementline/findOrderManagementLine")
    public OrderManagementLine[] findOrderManagementLine(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
                                                         @RequestParam String authToken) throws Exception {
        return transactionService.findOrderManagementLine(searchOrderMangementLine, authToken);
    }

    //Streaming
    @ApiOperation(response = OrderManagementLine.class, value = "Search OrderMangementLine New") // label for swagger
    @PostMapping("/ordermanagementline/findOrderManagementLineNew")
    public OrderManagementLine[] findOrderManagementLineNew(@RequestBody SearchOrderManagementLine searchOrderMangementLine,
                                                            @RequestParam String authToken) throws Exception {
        return transactionService.findOrderManagementLineNew(searchOrderMangementLine, authToken);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "UnAllocate") // label for swagger
    @PatchMapping("/ordermanagementline/unallocate")
    public ResponseEntity<?> patchUnallocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                             @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                             @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
                                             @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine updatedOrderManagementLine =
                transactionService.doUnAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, proposedStorageBin, proposedPackBarCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Allocate") // label for swagger
    @PatchMapping("/ordermanagementline/allocate")
    public ResponseEntity<?> patchAllocate(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                           @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                           @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine updatedOrderManagementLine =
                transactionService.doAllocation(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Assign Picker") // label for swagger
    @PatchMapping("/ordermanagementline/assignPicker")
    public ResponseEntity<?> assignPicker(@RequestBody List<AssignPicker> assignPicker,
                                          @RequestParam String assignedPickerId, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine[] updatedOrderManagementLine =
                transactionService.doAssignPicker(assignPicker, assignedPickerId, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Update OrderMangementLine") // label for swagger
    @PatchMapping("/ordermanagementline/{refDocNumber}")
    public ResponseEntity<?> patchOrderMangementLine(@PathVariable String refDocNumber,
                                                     @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                     @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                                     @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackCode,
                                                     @Valid @RequestBody OrderManagementLine updateOrderMangementLine, @RequestParam String loginUserID,
                                                     @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OrderManagementLine createdOrderMangementLine =
                transactionService.updateOrderManagementLine(warehouseId, preOutboundNo, refDocNumber,
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
        transactionService.deleteOrderManagementLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = OrderManagementLine.class, value = "Get a OrderManagementLine") // label for swagger 
    @GetMapping("/ordermanagementline/updateRefFields")
    public ResponseEntity<?> updateRefFields(@RequestParam String authToken) throws Exception {
        transactionService.updateRef9ANDRef10(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * -------------------------PickupHeader----------------------------------------------------
     */
    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader") // label for swagger
    @PostMapping("/pickupheader/findPickupHeader")
    public PickupHeader[] findPickupHeader(@RequestBody SearchPickupHeader searchPickupHeader,
                                           @RequestParam String authToken) throws Exception {
        return transactionService.findPickupHeader(searchPickupHeader, authToken);
    }

    /*
     * -------------------------PickupHeader New Stream-----------------------------------------------
     */
    @ApiOperation(response = PickupHeader.class, value = "Search PickupHeader New") // label for swagger
    @PostMapping("/pickupheader/findPickupHeaderNew")
    public PickupHeader[] findPickupHeaderNew(@RequestBody SearchPickupHeader searchPickupHeader,
                                              @RequestParam String authToken) throws Exception {
        return transactionService.findPickupHeaderNew(searchPickupHeader, authToken);
    }


    @ApiOperation(response = PickUpHeaderReport.class, value = "Search PickupHeader With StatusId") // label for swagger
    @PostMapping("/pickupheader/findPickupHeader/v2/status")
    public PickUpHeaderReport findPickUpHeaderWithStatus(@RequestBody FindPickUpHeader searchPickupHeader,
                                                         @RequestParam String authToken) throws Exception {
        return transactionService.findPickUpHeaderWithStatusId(searchPickupHeader, authToken);
    }

    @ApiOperation(response = PickupHeader.class, value = "Update PickupHeader") // label for swagger
    @PatchMapping("/pickupheader/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeader(@PathVariable String pickupNumber, @RequestParam String warehouseId,
                                               @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                               @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeader updatePickupHeader,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeader createdPickupHeader =
                transactionService.updatePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        pickupNumber, lineNumber, itemCode, loginUserID, updatePickupHeader, authToken);
        return new ResponseEntity<>(createdPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Update Assigned PickerId in PickupHeader")
    // label for swagger // label for swagger
    @PatchMapping("/pickupheader/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeader(@Valid @RequestBody List<UpdatePickupHeader> updatePickupHeaderList,
                                                                 @RequestParam("loginUserID") String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeader[] updatedPickupHeader =
                transactionService.patchAssignedPickerIdInPickupHeader(loginUserID, updatePickupHeaderList, authToken);
        return new ResponseEntity<>(updatedPickupHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PickupHeader.class, value = "Delete PickupHeader") // label for swagger
    @DeleteMapping("/pickupheader/{pickupNumber}")
    public ResponseEntity<?> deletePickupHeader(@PathVariable String pickupNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam Long lineNumber, @RequestParam String itemCode, @RequestParam String proposedStorageBin,
                                                @RequestParam String proposedPackCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.deletePickupHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
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
        Inventory[] additionalBins = transactionService.getAdditionalBins(warehouseId, itemCode, obOrdertypeId,
                proposedPackBarCode, proposedStorageBin, authToken);
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Create PickupLine") // label for swagger
    @PostMapping("/pickupline")
    public ResponseEntity<?> postPickupLine(@Valid @RequestBody List<AddPickupLine> newPickupLine,
                                            @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine[] createdPickupLine = transactionService.createPickupLine(newPickupLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLine.class, value = "Search PickupLine") // label for swagger
    @PostMapping("/pickupline/findPickupLine")
    public PickupLine[] findPickupLine(@RequestBody SearchPickupLine searchPickupLine,
                                       @RequestParam String authToken) throws Exception {
        return transactionService.findPickupLine(searchPickupLine, authToken);
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
                transactionService.updatePickupLine(warehouseId, preOutboundNo, refDocNumber,
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
        transactionService.deletePickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
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
        QualityHeader createdQualityHeader = transactionService.createQualityHeader(newQualityHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader") // label for swagger
    @PostMapping("/qualityheader/findQualityHeader")
    public QualityHeader[] findQualityHeader(@RequestBody SearchQualityHeader searchQualityHeader,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findQualityHeader(searchQualityHeader, authToken);
    }

    //Stream
    @ApiOperation(response = QualityHeader.class, value = "Search QualityHeader New") // label for swagger
    @PostMapping("/qualityheader/findQualityHeaderNew")
    public QualityHeader[] findQualityHeaderNew(@RequestBody SearchQualityHeader searchQualityHeader,
                                                @RequestParam String authToken) throws Exception {
        return transactionService.findQualityHeaderNew(searchQualityHeader, authToken);
    }

    @ApiOperation(response = QualityHeader.class, value = "Update QualityHeader") // label for swagger
    @PatchMapping("/qualityheader/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeader(@PathVariable String qualityInspectionNo,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                @Valid @RequestBody QualityHeader updateQualityHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityHeader updatedQualityHeader = transactionService.updateQualityHeader(warehouseId, preOutboundNo, refDocNumber,
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
        transactionService.deleteQualityHeader(warehouseId, preOutboundNo, refDocNumber,
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
        return transactionService.findQualityLine(searchQualityLine, authToken);
    }

    @ApiOperation(response = QualityLine.class, value = "Create QualityLine") // label for swagger
    @PostMapping("/qualityline")
    public ResponseEntity<?> postQualityLine(@Valid @RequestBody List<AddQualityLine> newQualityLine,
                                             @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        QualityLine[] createdQualityLine = transactionService.createQualityLine(newQualityLine, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = QualityLine.class, value = "Update QualityLine") // label for swagger
    @PatchMapping("/qualityline/{partnerCode}")
    public ResponseEntity<?> patchQualityLine(@PathVariable String partnerCode,
                                              @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                              @RequestParam String refDocNumber, @RequestParam Long lineNumber,
                                              @RequestParam String qualityInspectionNo, @RequestParam String itemCode,
                                              @Valid @RequestBody QualityLine updateQualityLine, @RequestParam String loginUserID,
                                              @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityLine createdQualityLine =
                transactionService.updateQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
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
        transactionService.deleteQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ---------------------------OutboundHeader--------------------------------------------------------
     */
    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader") // label for swagger
    @PostMapping("/outboundheader/findOutboundHeader")
    public OutboundHeader[] findOutboundHeader(@RequestBody SearchOutboundHeader searchOutboundHeader,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundHeader(searchOutboundHeader, authToken);
    }

    //
    @ApiOperation(response = OutboundHeader.class, value = "Search OutboundHeader New") // label for swagger
    @PostMapping("/outboundheader/findOutboundHeaderNew")
    public OutboundHeader[] findOutboundHeaderNew(@RequestBody SearchOutboundHeader searchOutboundHeader,
                                                  @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundHeaderNew(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Update OutboundHeader") // label for swagger
    @PatchMapping("/outboundheader/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeader(@PathVariable String preOutboundNo,
                                                 @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @Valid @RequestBody OutboundHeader updateOutboundHeader, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundHeader createdOutboundHeader =
                transactionService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader") // label for swagger
    @DeleteMapping("/outboundheader/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo,
                                                  @RequestParam String warehouseId, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ----------------------OutboundLine----------------------------------------------------------
     */
    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/outboundline/findOutboundLine")
    public OutboundLine[] findOutboundLine(@RequestBody SearchOutboundLine searchOutboundLine,
                                           @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundLine(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine") // label for swagger
    @PostMapping("/outboundline/findOutboundLine-new")
    public OutboundLine[] findOutboundLineNew(@RequestBody SearchOutboundLine searchOutboundLine,
                                              @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundLineNew(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLine.class, value = "Search OutboundLine for Stock movement report")
    // label for swagger
    @PostMapping("/outboundline/stock-movement-report/findOutboundLine")
    public StockMovementReport[] findOutboundLineForStockMovement(@RequestBody SearchOutboundLine searchOutboundLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findOutboundLineForStockMovement(searchOutboundLine, authToken);
    }

    @ApiOperation(response = StockMovementReport.class, value = "Search OutboundLine for Stock movement report V2")
    // label for swagger
    @PostMapping("/outboundline/stock-movement-report/v2/findOutboundLine")
    public StockMovementReport[] findOutboundLineForStockMovementV2(@RequestBody SearchOutboundLineV2 searchOutboundLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findOutboundLineForStockMovementV2(searchOutboundLine, authToken);
    }

    /*
     * --------------------------------Get all StockMovementReport---------------------------------------------------------------
     */
    @ApiOperation(response = StockMovementReport.class, value = "Get all StockMovementReport details")
    // label for swagger
    @GetMapping("/outboundline/stock-movement-report")
    public ResponseEntity<?> getStockMovementReports(@RequestParam String authToken) throws Exception {
        StockMovementReport[] stockMovementReportList = transactionService.getStockMovementReports(authToken);
        return new ResponseEntity<>(stockMovementReportList, HttpStatus.OK);
    }

    /*
     * Inventory Stock movement report renamed to Transaction History report
     */
    @ApiOperation(response = Optional.class, value = "Get transaction History Report")
    @PostMapping("/reports/transactionHistoryReport")
    public ResponseEntity<?> getTransactionHistoryReport(@RequestBody FindImBasicData1 findImBasicData1,
                                                         @RequestParam String authToken) throws java.text.ParseException {
        InventoryStockReport[] inventoryReportList = transactionService.getTransactionHistoryReport(findImBasicData1, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @GetMapping("/outboundline/delivery/confirmation")
    public ResponseEntity<?> deliveryConfirmation(@RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLine[] createdOutboundLine =
                transactionService.deliveryConfirmation(warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Update OutboundLine") // label for swagger
    @PatchMapping("/outboundline/{lineNumber}")
    public ResponseEntity<?> patchOutboundLine(@PathVariable Long lineNumber,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String itemCode,
                                               @Valid @RequestBody OutboundLine updateOutboundLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLine updatedOutboundLine =
                transactionService.updateOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        lineNumber, itemCode, loginUserID, updateOutboundLine, authToken);
        return new ResponseEntity<>(updatedOutboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundLine.class, value = "Delete OutboundLine") // label for swagger
    @DeleteMapping("/outboundline/{lineNumber}")
    public ResponseEntity<?> deleteOutboundLine(@PathVariable Long lineNumber,
                                                @RequestParam String warehouseId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber,
                                                @RequestParam String partnerCode, @RequestParam String itemCode, @RequestParam String loginUserID,
                                                @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        transactionService.deleteOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
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
        return transactionService.findOutboundReversal(searchOutboundReversal, authToken);
    }

    //Stream
    @ApiOperation(response = OutboundReversal.class, value = "Search OutboundReversal New") // label for swagger
    @PostMapping("/outboundreversal/findOutboundReversalNew")
    public OutboundReversal[] findOutboundReversalNew(@RequestBody SearchOutboundReversal searchOutboundReversal,
                                                      @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundReversalNew(searchOutboundReversal, authToken);
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLine.class, value = "Get Delivery Lines") // label for swagger 
    @GetMapping("/outboundreversal/reversal/new")
    public ResponseEntity<?> doReversal(@RequestParam String refDocNumber, @RequestParam String itemCode,
                                        @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversal[] deliveryLines = transactionService.doReversal(refDocNumber, itemCode, loginUserID, authToken);
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
                transactionService.getStockReports(warehouseId, itemCode, itemText, stockTypeText,
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
        StockReport[] stockReportList = transactionService.getAllStockReports(languageId, companyCodeId, plantId,
                warehouseId, itemCode, manufacturerName, itemText, stockTypeText, authToken);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get All Stock Reports
    @ApiOperation(response = StockReport.class, value = "Get All Stock Reports v2") // label for swagger
    @PostMapping("/reports/v2/stockReport-all")
    public ResponseEntity<?> getAllStockReportV2(@Valid @RequestBody SearchStockReport searchStockReport, @RequestParam String authToken) {
        StockReport[] stockReportList = transactionService.getAllStockReportsV2(searchStockReport, authToken);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get All Stock Reports - StoredProcedure
    @ApiOperation(response = StockReportOutput.class, value = "Get All Stock Reports v2 Stored Procedure") // label for swagger
    @PostMapping("/reports/v2/stockReportSP")
    public ResponseEntity<?> getAllStockReportV2SP(@Valid @RequestBody SearchStockReportInput searchStockReport, @RequestParam String authToken) {
        StockReportOutput[] stockReportList = transactionService.getAllStockReportsV2SP(searchStockReport, authToken);
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
                transactionService.getInventoryReport(warehouseId, itemCode, storageBin, stockTypeText, stSectionIds,
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
                transactionService.getStockMovementReport(warehouseId, itemCode, fromCreatedOn, toCreatedOn, authToken);
        return new ResponseEntity<>(inventoryReportList, HttpStatus.OK);
    }

    /*
     * Order status report
     */
    @ApiOperation(response = OrderStatusReport.class, value = "Get OrderStatus Report") // label for swagger 
    @PostMapping("/reports/orderStatusReport")
    public ResponseEntity<?> getOrderStatusReport(@RequestBody @Valid SearchOrderStatusReport request,
                                                  @RequestParam String authToken) throws ParseException, Exception {
        OrderStatusReport[] orderStatusReportList = transactionService.getOrderStatusReport(request, authToken);
        return new ResponseEntity<>(orderStatusReportList, HttpStatus.OK);
    }

    /*
     * Shipment Delivery
     */
    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report") // label for swagger 
    @GetMapping("/reports/shipmentDelivery")
    public ResponseEntity<?> getShipmentDeliveryReport(@RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber,
                                                       @RequestParam String authToken)
            throws ParseException, Exception {
        ShipmentDeliveryReport[] shipmentDeliveryList = transactionService.getShipmentDeliveryReport(warehouseId,
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
        ShipmentDeliveryReport[] shipmentDeliveryList = transactionService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
                fromDeliveryDate, toDeliveryDate, storeCode, soType, orderNumber, authToken);
        return new ResponseEntity<>(shipmentDeliveryList, HttpStatus.OK);
    }

    @ApiOperation(response = ShipmentDeliveryReport.class, value = "Get ShipmentDelivery Report v2 New")    // label for swagger
    @GetMapping("/reports/v2/shipmentDelivery/new")
    public ResponseEntity<?> getShipmentDeliveryReportV2(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId,
                                                       @RequestParam(required = false) String fromDeliveryDate,
                                                       @RequestParam(required = false) String toDeliveryDate,
                                                       @RequestParam(required = false) String storeCode,
                                                       @RequestParam(required = false) List<String> soType,
                                                       @RequestParam String orderNumber, @RequestParam String preOutboundNo,
                                                       @RequestParam String authToken)
            throws ParseException, Exception {
        ShipmentDeliveryReport[] shipmentDeliveryList = transactionService.getShipmentDeliveryReportV2(companyCodeId, plantId, languageId, warehouseId,
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
                transactionService.getShipmentDeliverySummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, companyCodeId, plantId, languageId, authToken);
        return new ResponseEntity<>(shipmentDeliverySummaryReport, HttpStatus.OK);
    }

    /*
     * Inventory Stock movement report
     */
    @ApiOperation(response = Optional.class, value = "Get Opening Stock Report")
    @PostMapping("/reports/openingStockReport")
    public ResponseEntity<?> getInventoryStockReport(@RequestBody FindImBasicData1 findImBasicData1,
                                                     @RequestParam String authToken) throws java.text.ParseException {
        InventoryStockReport[] inventoryReportList = transactionService.getInventoryStockReport(findImBasicData1, authToken);
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
                transactionService.getShipmentDispatchSummaryReport(fromDeliveryDate, toDeliveryDate, customerCode, warehouseId, authToken);
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
        ReceiptConfimationReport receiptConfimationReport = transactionService.getReceiptConfimationReport(asnNumber, authToken);
        return new ResponseEntity<>(receiptConfimationReport, HttpStatus.OK);
    }

    @ApiOperation(response = ReceiptConfimationReport.class, value = "Get ReceiptConfimation Report")    // label for swagger
    @GetMapping("/reports/v2/receiptConfirmation")
    public ResponseEntity<?> getReceiptConfimationReportNew(@RequestParam String asnNumber, @RequestParam String preInboundNo, @RequestParam String companyCodeId,
                                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                            @RequestParam String authToken) throws Exception {
        ReceiptConfimationReport receiptConfimationReport = transactionService.getReceiptConfimationReportV2(asnNumber, preInboundNo, companyCodeId, plantId, languageId, warehouseId, authToken);
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
        MobileDashboard dashboard = transactionService.getMobileDashboard(companyCode, plantId, languageId, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }


    //FIND
    @ApiOperation(response = MobileDashboard.class, value = "Find MobileDashBoard")//label for swagger
    @PostMapping("/reports/dashboard/mobile/find")
    public MobileDashboard findMobileDashBoard(@RequestBody FindMobileDashBoard findMobileDashBoard,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findMobileDashBoard(findMobileDashBoard, authToken);
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
    @ApiOperation(response = PerpetualHeader.class, value = "Get all PerpetualHeader details") // label for swagger
    @GetMapping("/perpetualheader")
    public ResponseEntity<?> getPerpetualHeaders(@RequestParam String authToken) throws Exception {
        PerpetualHeader[] perpetualheaderList = transactionService.getPerpetualHeaders(authToken);
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Get a PerpetualHeader") // label for swagger 
    @GetMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                @RequestParam String authToken) throws Exception {
        PerpetualHeader perpetualheader =
                transactionService.getPerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId, authToken);
        log.info("PerpetualHeader : " + perpetualheader);
        return new ResponseEntity<>(perpetualheader, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntity[].class, value = "Search PerpetualHeader") // label for swagger
    @PostMapping("/perpetualheader/findPerpetualHeader")
    public PerpetualHeaderEntity[] findPerpetualHeader(@RequestBody SearchPerpetualHeader searchPerpetualHeader,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualHeader(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("/perpetualheader")
    public ResponseEntity<?> postPerpetualHeader(@Valid @RequestBody AddPerpetualHeader newPerpetualHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeader createdPerpetualHeader =
                transactionService.createPerpetualHeader(newPerpetualHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    /*
     * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field
     * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
     */
    @ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader") // label for swagger
    @PostMapping("/perpetualheader/run")
    public ResponseEntity<?> postRunPerpetualHeader(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineEntity[] perpetualLineEntity =
                transactionService.runPerpetualHeader(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineEntity[].class, value = "Create PerpetualHeader Stream") // label for swagger
    @PostMapping("/perpetualheader/runNew")
    public ResponseEntity<?> postRunPerpetualHeaderStream(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineEntity[] perpetualLineEntity =
                transactionService.runPerpetualHeaderNew(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Update PerpetualHeader") // label for swagger
    @PatchMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                  @Valid @RequestBody UpdatePerpetualHeader updatePerpetualHeader, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeader createdPerpetualHeader =
                transactionService.updatePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                        subMovementTypeId, loginUserID, updatePerpetualHeader, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeader.class, value = "Delete PerpetualHeader") // label for swagger
    @DeleteMapping("/perpetualheader/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                   @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePerpetualHeader(warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                subMovementTypeId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PerpetualLine---------------------------------------------------------------------

    //-------------------------------PerpetualLine---------------------------------------------------------------------
    @ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine") // label for swagger
    @PostMapping("/findPerpetualLine")
    public PerpetualLine[] findPerpetualHeader(@RequestBody SearchPerpetualLine searchPerpetualLine,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualLine(searchPerpetualLine, authToken);
    }

    @ApiOperation(response = PerpetualHeaderEntity.class, value = "SearchPerpetualLine Stream") // label for swagger
    @PostMapping("/findPerpetualLineNew")
    public PerpetualLine[] findPerpetualHeaderStream(@RequestBody SearchPerpetualLine searchPerpetualLine,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualLineStream(searchPerpetualLine, authToken);
    }

    @ApiOperation(response = PerpetualLine[].class, value = "Update AssignHHTUser") // label for swagger
    @PatchMapping("/perpetualline/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PerpetualLine[] createdPerpetualLine = transactionService.updateAssingHHTUser(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLine.class, value = "Update PerpetualLine") // label for swagger
    @PatchMapping("/perpetualline/{cycleCountNo}")
    public ResponseEntity<?> patchAssingHHTUser(@PathVariable String cycleCountNo,
                                                @RequestBody List<UpdatePerpetualLine> updatePerpetualLine, @RequestParam String loginUserID,
                                                @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualUpdateResponse createdPerpetualLine =
                transactionService.updatePerpetualLine(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    /*
     * -----------------------------Periodic Count----------------------------------------------------
     */
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details") // label for swagger
    @GetMapping("/periodicheader")
    public ResponseEntity<?> getPeriodicHeaders(@RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] PeriodicheaderList = transactionService.getPeriodicHeaders(authToken);
        return new ResponseEntity<>(PeriodicheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Get a PeriodicHeader") // label for swagger 
    @GetMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                               @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                               @RequestParam String authToken) throws Exception {
        PeriodicHeader[] Periodicheader =
                transactionService.getPeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo,
                        movementTypeId, subMovementTypeId, authToken);
        return new ResponseEntity<>(Periodicheader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader") // label for swagger
    @PostMapping("/periodicheader/findPeriodicHeader")
    public ResponseEntity<?> findPeriodicHeader(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] results = transactionService.findPeriodicHeader(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    //Stream
    @ApiOperation(response = PeriodicHeader[].class, value = "Search PeriodicHeader Stream") // label for swagger
    @PostMapping("/periodicheader/findPeriodicHeaderNew")
    public ResponseEntity<?> findPeriodicHeaderNew(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                   @RequestParam String authToken) throws Exception {
        PeriodicHeader[] results = transactionService.findPeriodicHeaderStream(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader") // label for swagger
    @PostMapping("/periodicheader")
    public ResponseEntity<?> postPeriodicHeader(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader,
                                                @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity createdPeriodicHeader = transactionService.createPeriodicHeader(newPeriodicHeader, loginUserID, authToken);
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
        Page<?> periodicLineEntity = transactionService.runPeriodicHeader(warehouseId, pageNo, pageSize, sortBy, authToken);
        return new ResponseEntity<>(periodicLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader") // label for swagger
    @PatchMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                 @RequestParam Long cycleCountTypeId, @RequestBody UpdatePeriodicHeader updatePeriodicHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicHeader createdPeriodicHeader =
                transactionService.updatePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo,
                        loginUserID, updatePeriodicHeader, authToken);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader") // label for swagger
    @DeleteMapping("/periodicheader/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeader(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                  @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePeriodicHeader(warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine") // label for swagger
    @PostMapping("/periodicline/findPeriodicLine")
    public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLine(@RequestBody SearchPeriodicLine searchPeriodicLine,
                                                                                    @RequestParam String authToken) throws Exception {
        return transactionService.findPeriodicLine(searchPeriodicLine, authToken);
    }

    //Stream
    @ApiOperation(response = PeriodicLine.class, value = "SearchPeriodicLine Stream") // label for swagger
    @PostMapping("/periodicline/findPeriodicLineNew")
    public com.tekclover.wms.core.model.transaction.PeriodicLine[] findPeriodicLineNew(@RequestBody SearchPeriodicLine searchPeriodicLine,
                                                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findPeriodicLineNew(searchPeriodicLine, authToken);
    }

    @ApiOperation(response = PeriodicLine[].class, value = "Update AssignHHTUser") // label for swagger
    @PatchMapping("/periodicline/assigingHHTUser")
    public ResponseEntity<?> assigingHHTUser(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                             @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicLine[] createdPeriodicLine =
                transactionService.updatePeriodicLineAssingHHTUser(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine") // label for swagger
    @PatchMapping("/periodicline/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLine(@PathVariable String cycleCountNo,
                                               @RequestBody List<UpdatePeriodicLine> updatePeriodicLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicUpdateResponse updatedPeriodicLine =
                transactionService.updatePeriodicLine(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
    }

    //--------------------------Schedule-Report------------------------------------------------------
    @ApiOperation(response = InventoryReport.class, value = "Report Inventory") // label for swagger
    @GetMapping("/reports/inventoryReport/schedule")
    public ResponseEntity<?> scheduleInventoryReport(@RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.getInventoryReport(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Counts") // label for swagger
    @GetMapping("/reports/dashboard/get-count")
    public ResponseEntity<?> getDashboardCount(@RequestParam String warehouseId, @RequestParam String authToken) throws Exception {
        Dashboard dashboard = transactionService.getDashboardCount(warehouseId, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @ApiOperation(response = Dashboard.class, value = "Get Dashboard Fast Slow moving Dashboard") // label for swagger
    @PostMapping("/reports/dashboard/get-fast-slow-moving")
    public ResponseEntity<?> getFastSlowMovingDashboard(@RequestBody FastSlowMovingDashboardRequest fastSlowMovingDashboardRequest,
                                                        @RequestParam String authToken) throws Exception {
        FastSlowMovingDashboard[] dashboard = transactionService.getFastSlowMovingDashboard(fastSlowMovingDashboardRequest, authToken);
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    //----------------------Orders------------------------------------------------------------------
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/warehouse/outbound/so")
    public ResponseEntity<?> postShipmenOrder(@Valid @RequestBody ShipmentOrder shipmenOrder, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postSO(shipmenOrder, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }

    // File Upload - Orders
    @ApiOperation(response = ShipmentOrder.class, value = "Create Shipment Order") // label for swagger
    @PostMapping("/warehouse/outbound/so/upload")
    public ResponseEntity<?> postShipmenOrderUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processSOOrders(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(response = ASN.class, value = "Create ASN Order") // label for swagger
    @PostMapping("/warehouse/inbound/asn")
    public ResponseEntity<?> postASN(@Valid @RequestBody ASN asn, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postASN(asn, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
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

    //-----------------------------------------------------------------------------------------------------------

    @ApiOperation(response = OutboundOrder.class, value = "Get all Outbound Success Orders") // label for swagger
    @GetMapping("/orders/outbound/success")
    public ResponseEntity<?> getOBAllOrders(@RequestParam String authToken) {
        OutboundOrder[] outboundOrderList = transactionService.getOBOrders(authToken);
        return new ResponseEntity<>(outboundOrderList, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundOrder.class, value = "Get a Orders") // label for swagger
    @GetMapping("/orders/outbound/byDate")
    public ResponseEntity<?> getOBOrdersByDate(@RequestParam String orderStartDate,
                                               @RequestParam String orderEndDate, @RequestParam String authToken) throws Exception {
        OutboundOrder[] orders = transactionService.getOBOrderByDate(orderStartDate, orderEndDate, authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundOrder.class, value = "Get outbound Order by id ") // label for swagger
    @GetMapping("/orders/outbound/{orderId}")
    public ResponseEntity<?> getOutboundOrdersById(@PathVariable String orderId, @RequestParam String authToken) {
        OutboundOrder orders = transactionService.getOutboundOrdersById(orderId, authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundIntegrationLog.class, value = "Get Failed OutboundOrders") // label for swagger
    @GetMapping("/orders/outbound/failed")
    public ResponseEntity<?> getFailedOutboundOrders(@RequestParam String authToken) throws Exception {
        OutboundIntegrationLog[] orders = transactionService.getFailedOutboundOrders(authToken);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //============================================STREAMING==========================================================

//    @GetMapping(value = "/streaming/inventoryMovement")
//    public ResponseEntity<StreamingResponseBody> findInventoryMovement() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findInventoryMovementByStreaming();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamOutboundHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamOutboundHeader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamOutboundHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @PostMapping(value = "/streaming/findStreamGrHeader")
//    public ResponseEntity<?> findStreamgrheader() throws ExecutionException, InterruptedException {
////		StreamingResponseBody responseBody = transactionService.findStreamGrHeader();
//        List<GrHeaderStream> responseBody = transactionService.streamGrHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamStagingHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamStagingHeader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamStagingHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamPutAwayHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamPutAwayheader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamPutAwayHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamInboundHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamInboundheader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamInboundHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamPreInboundHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamPreInboundheader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamPreInboundHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamPreOutboundHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamPreOutboundheader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamPreOutboundHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamOrderManagementLine")
//    public ResponseEntity<StreamingResponseBody> findStreamOrderManagementLine() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamOrderManagementLine();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamPickupHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamPickupHeader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamPickupHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamQualityHeader")
//    public ResponseEntity<StreamingResponseBody> findStreamQualityHeader() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamQualityHeader();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamImBasicData1")
//    public ResponseEntity<StreamingResponseBody> findStreamImBasicData1() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamImBasicData1();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    //Filter
//    @PostMapping(value = "/streaming/findStreamImBasicData1New")
//    public ResponseEntity<?> findStreamImBasicData1New(@RequestBody SearchImBasicData1 searchImBasicData1) throws ExecutionException, InterruptedException {
//        List<ImBasicData1> responseBody = transactionService.getAllImBasicData1(searchImBasicData1);
//        return new ResponseEntity<>(responseBody, HttpStatus.OK);
////		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }
//
//    @GetMapping(value = "/streaming/findStreamStorageBin")
//    public ResponseEntity<?> findStreamStorageBin() throws ExecutionException, InterruptedException {
//        StreamingResponseBody responseBody = transactionService.findStreamStorageBin();
//        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(responseBody);
//    }

    /*
     * --------------------------------ContainerReceipt-V2-------------------------------------------------------------
     */

    //Get
    @ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
    @GetMapping("/containerreceipt/{containerReceiptNo}/v2")
    public ResponseEntity<?> getContainerReceipt(@PathVariable String containerReceiptNo, @RequestParam String companyCode,
                                                 @RequestParam String plantId, @RequestParam String languageId,
                                                 @RequestParam String warehouseId, @RequestParam String authToken) throws java.text.ParseException {
        ContainerReceiptV2 containerreceipt =
                transactionService.getContainerReceiptV2(companyCode, plantId, languageId, warehouseId, containerReceiptNo, authToken);
        log.info("ContainerReceipt : " + containerreceipt);
        return new ResponseEntity<>(containerreceipt, HttpStatus.OK);
    }

    //Get
    @ApiOperation(response = ContainerReceiptV2.class, value = "Get a ContainerReceipt V2") // label for swagger
    @GetMapping("/containerreceipt/v2/{containerReceiptNo}")
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
    @PostMapping("/containerreceipt/findContainerReceipt/v2")
    public ContainerReceiptV2[] findContainerReceiptV2(@RequestBody SearchContainerReceiptV2 searchContainerReceipt,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findContainerReceiptV2(searchContainerReceipt, authToken);
    }

    //Create
    @ApiOperation(response = ContainerReceiptV2.class, value = "Create ContainerReceipt V2") // label for swagger
    @PostMapping("/containerreceipt/v2")
    public ResponseEntity<?> postContainerReceiptV2(@Valid @RequestBody ContainerReceiptV2 newContainerReceipt, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        ContainerReceiptV2 createdContainerReceipt = transactionService.createContainerReceiptV2(newContainerReceipt, loginUserID, authToken);
        return new ResponseEntity<>(createdContainerReceipt, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = ContainerReceiptV2.class, value = "Update ContainerReceiptV2") // label for swagger
    @RequestMapping(value = "/containerreceipt/{containerReceiptNo}/v2", method = RequestMethod.PATCH)
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
    @DeleteMapping("/containerreceipt/{containerReceiptNo}/v2")
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
    @GetMapping("/preinboundheader/{preInboundNo}/v2")
    public ResponseEntity<?> getPreInboundHeaderV2(@PathVariable String preInboundNo, @RequestParam String warehouseId,
                                                   @RequestParam String companyCode, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundHeaderV2 preinboundheader = transactionService.getPreInboundHeaderV2(preInboundNo, warehouseId, companyCode, plantId, languageId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Search PreInboundHeader v2") // label for swagger
    @PostMapping("/preinboundheader/findPreInboundHeader/v2")
    public PreInboundHeaderV2[] findPreInboundHeaderV2(@RequestBody SearchPreInboundHeaderV2 searchPreInboundHeader,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundHeaderV2(searchPreInboundHeader, authToken);
    }

    //Get
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Get a PreInboundHeader With Status=24 V2")
    // label for swagger
    @GetMapping("/preinboundheader/inboundconfirm/v2")
    public ResponseEntity<?> getPreInboundHeaderV2(@RequestParam String warehouseId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundHeaderV2[] preinboundheader =
                transactionService.getPreInboundHeaderWithStatusIdV2(warehouseId, companyCode, plantId, languageId, authToken);
        log.info("PreInboundHeader : " + preinboundheader);
        return new ResponseEntity<>(preinboundheader, HttpStatus.OK);
    }

    //Create
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Create PreInboundHeaderV2") // label for swagger
    @PostMapping("/preinboundheader/v2")
    public ResponseEntity<?> postPreInboundHeaderV2(@Valid @RequestBody PreInboundHeaderV2 newPreInboundHeader, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PreInboundHeaderV2 createdPreInboundHeader = transactionService.createPreInboundHeaderV2(newPreInboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPreInboundHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Update PreInboundHeader V2") // label for swagger
    @PatchMapping("/preinboundheader/{preInboundNo}/v2")
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
    @DeleteMapping("/preinboundheader/{preInboundNo}/v2")
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
    @PostMapping("/preinboundline/bom/v2")
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
    @GetMapping("/preinboundline/{preInboundNo}/V2")
    public ResponseEntity<?> getPreInboundLineV2(@PathVariable String preInboundNo, @RequestParam String authToken) throws java.text.ParseException {
        PreInboundLine[] preinboundline = transactionService.getPreInboundLineV2(preInboundNo, authToken);
        log.info("PreInboundLine : " + preinboundline);
        return new ResponseEntity<>(preinboundline, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PreInboundLineOutputV2.class, value = "Search PreInboundLine v2") // label for swagger
    @PostMapping("/preinboundline/v2/findPreInboundLine")
    public PreInboundLineOutputV2[] findPreInboundLineV2(@RequestBody SearchPreInboundLineV2 searchPreInboundLine,
                                                         @RequestParam String authToken) throws Exception {
        return transactionService.findPreInboundLineV2(searchPreInboundLine, authToken);
    }

    //Patch
    @ApiOperation(response = PreInboundLineV2.class, value = "Patch PreInboundLine V2") // label for swagger
    @PatchMapping("/preinboundline/{preInboundNo}/V2")
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
    @PostMapping("/inboundheader/findInboundHeader/v2")
    public InboundHeaderEntityV2[] findInboundHeaderV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader,
                                                       @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderV2(searchInboundHeader, authToken);
    }

    //Find-Stream
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Search InboundHeader Stream V2") // label for swagger
    @PostMapping("/inboundheader/findInboundHeader/v2/stream")
    public InboundHeaderEntityV2[] findInboundHeaderstreamV2(@RequestBody SearchInboundHeaderV2 searchInboundHeader,
                                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInboundHeaderStreamV2(searchInboundHeader, authToken);
    }

    //replaceASN
    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Replace ASN V2") // label for swagger
    @GetMapping("/inboundheader/replaceASN/v2")
    public ResponseEntity<?> replaceASNV2(@RequestParam String refDocNumber, @RequestParam String preInboundNo,
                                          @RequestParam String asnNumber, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.replaceASNV2(refDocNumber, preInboundNo, asnNumber, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderEntityV2.class, value = "Get a InboundHeader") // label for swagger
    @GetMapping("/inboundheader/v2/{refDocNumber}")
    public ResponseEntity<?> getInboundHeaderV2(@PathVariable String refDocNumber, @RequestParam String warehouseId,
                                                @RequestParam String preInboundNo, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String languageId,
                                                @RequestParam String authToken) throws Exception {
        InboundHeaderEntityV2 dbInboundHeader = transactionService.getInboundHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, authToken);
        log.info("InboundHeader : " + dbInboundHeader);
        return new ResponseEntity<>(dbInboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Confirm") // label for swagger
    @PatchMapping("/inboundheader/v2/confirmIndividual")
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
    @PatchMapping("/inboundheader/v2/partialConfirmIndividual")
    public ResponseEntity<?> patchInboundHeaderPartialConfirmV2(@RequestParam String warehouseId, @RequestParam String preInboundNo,
                                                                @RequestParam String companyCode, @RequestParam String plantId,
                                                                @RequestParam String languageId, @RequestParam String refDocNumber,
                                                                @RequestParam String loginUserID, @RequestParam String authToken) {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderPartialConfirmV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Inbound Header & Line Partial Confirm with InboundLines Input") // label for swagger
    @PostMapping("/inboundheader/v2/confirmIndividual/partial")
    public ResponseEntity<?> patchInboundHeaderPartialWithInboundLinesConfirmV2(@RequestBody List<InboundLineV2> inboundLineList, @RequestParam String warehouseId,
                                                                                @RequestParam String preInboundNo, @RequestParam String companyCode, @RequestParam String plantId,
                                                                                @RequestParam String languageId, @RequestParam String refDocNumber,
                                                                                @RequestParam String loginUserID, @RequestParam String authToken) {
        AXApiResponse createdInboundHeaderResponse =
                transactionService.updateInboundHeaderWithIbLinePartialConfirmV2(inboundLineList, companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundHeaderResponse, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Update InboundHeader V2") // label for swagger
    @RequestMapping(value = "/inboundheader/v2/{refDocNumber}", method = RequestMethod.PATCH)
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
    @DeleteMapping("/inboundheader/v2/{refDocNumber}")
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
    @GetMapping("/inboundline/v2")
    public ResponseEntity<?> getInboundLinesV2(@RequestParam String authToken) throws Exception {
        InboundLineV2[] lineNoList = transactionService.getInboundLinesV2(authToken);
        return new ResponseEntity<>(lineNoList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Get a InboundLine V2") // label for swagger
    @GetMapping("/inboundline/v2/{lineNo}")
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
    @PostMapping("/inboundline/v2/findInboundLine")
    public InboundLineV2[] findInboundLineV2(@RequestBody SearchInboundLineV2 searchInboundLine,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findInboundLineV2(searchInboundLine, authToken);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Create InboundLine V2") // label for swagger
    @PostMapping("/inboundline/v2")
    public ResponseEntity<?> postInboundLineV2(@Valid @RequestBody InboundLineV2 newInboundLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InboundLineV2 createdInboundLine = transactionService.createInboundLineV2(newInboundLine, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Update InboundLine V2") // label for swagger
    @RequestMapping(value = "/inboundline/v2", method = RequestMethod.PATCH)
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
    @RequestMapping(value = "/inboundline/v2/batchUpdateInboundLines", method = RequestMethod.PATCH)
    public ResponseEntity<?> batchUpdateInboundLineV2(@RequestParam String loginUserID, @RequestParam String authToken,
                                                      @Valid @RequestBody List<InboundLineV2> updateInboundLines) {
        InboundLineV2[] updatedInboundLines = transactionService.batchUpdateInboundLineV2(updateInboundLines, loginUserID, authToken);
        return new ResponseEntity<>(updatedInboundLines, HttpStatus.OK);
    }

    @ApiOperation(response = InboundLineV2.class, value = "Delete InboundLine V2") // label for swagger
    @DeleteMapping("/inboundline/v2/{lineNo}")
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
    @GetMapping("/stagingheader/{stagingNo}/v2")
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
    @PostMapping("/stagingheader/findStagingHeader/v2")
    public StagingHeaderV2[] findStagingHeaderV2(@RequestBody SearchStagingHeaderV2 searchStagingHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findStagingHeaderV2(searchStagingHeader, authToken);
    }

    //Create
    @ApiOperation(response = StagingHeaderV2.class, value = "Create StagingHeader V2") // label for swagger
    @PostMapping("/stagingheader/v2")
    public ResponseEntity<?> postStagingHeaderV2(@Valid @RequestBody StagingHeaderV2 newStagingHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        StagingHeaderV2 createdStagingHeader = transactionService.createStagingHeaderV2(newStagingHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdStagingHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = StagingHeaderV2.class, value = "Update StagingHeader V2") // label for swagger
    @PatchMapping("/stagingheader/{stagingNo}/v2")
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
    @DeleteMapping("/stagingheader/{stagingNo}/v2")
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
    @GetMapping("/stagingline/{lineNo}/v2")
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

    //Find
    @ApiOperation(response = StagingLineEntityV2.class, value = "Search StagingLine V2") // label for swagger
    @PostMapping("/stagingline/findStagingLine/v2")
    public StagingLineEntityV2[] findStagingLineV2(@RequestBody SearchStagingLineV2 searchStagingLine,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findStagingLineV2(searchStagingLine, authToken);
    }

    //Create
    @ApiOperation(response = StagingLineEntityV2.class, value = "Create StagingLine V2") // label for swagger
    @PostMapping("/stagingline/v2")
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

    //Update
    @ApiOperation(response = StagingLineEntityV2.class, value = "Update StagingLine V2") // label for swagger
    @PatchMapping("/stagingline/caseConfirmation/v2")
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
    @DeleteMapping("/stagingline/{lineNo}/v2")
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
    @DeleteMapping("/stagingline/{lineNo}/cases/v2")
    public ResponseEntity<?> deleteCasesV2(@PathVariable Long lineNo, @RequestParam String preInboundNo,
                                           @RequestParam String caseCode, @RequestParam String itemCode,
                                           @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteCasesV2(preInboundNo, lineNo, itemCode, caseCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //AssignHHT
    @ApiOperation(response = StagingLineEntityV2.class, value = "AssignHHTUser StagingLine V2") // label for swagger
    @PatchMapping("/stagingline/assignHHTUser/v2")
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
    @GetMapping("/grheader/{goodsReceiptNo}/v2")
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
    @PostMapping("/grheader/findGrHeader/v2")
    public GrHeaderV2[] findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrHeaderV2(searchGrHeader, authToken);
    }

    //Create
    @ApiOperation(response = GrHeaderV2.class, value = "Create GrHeader V2") // label for swagger
    @PostMapping("/grheader/v2")
    public ResponseEntity<?> postGrHeaderV2(@Valid @RequestBody GrHeaderV2 newGrHeader, @RequestParam String loginUserID,
                                            @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrHeaderV2 createdGrHeader = transactionService.createGrHeaderV2(newGrHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdGrHeader, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = GrHeaderV2.class, value = "Update GrHeader V2") // label for swagger
    @RequestMapping(value = "/grheader/{goodsReceiptNo}/v2", method = RequestMethod.PATCH)
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
    @DeleteMapping("/grheader/{goodsReceiptNo}/v2")
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
    @GetMapping("/grline/{lineNo}/v2")
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
    @GetMapping("/grline/{lineNo}/putawayline/v2")
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
    @PostMapping("/grline/findGrLine/v2")
    public GrLineV2[] findGrLineV2(@RequestBody SearchGrLineV2 searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLineV2(searchGrLine, authToken);
    }
    //Find
    @ApiOperation(response = GrLineV2.class, value = "Search GrLine - SQL for report V2") // label for swagger
    @PostMapping("/grline/findGrLineNew/v2")
    public GrLineV2[] findGrLineNewV2(@RequestBody SearchGrLineV2 searchGrLine, @RequestParam String authToken)
            throws Exception {
        return transactionService.findGrLineSQLV2(searchGrLine, authToken);
    }

    //Create
    @ApiOperation(response = GrLineV2.class, value = "Create GrLine V2") // label for swagger
    @PostMapping("/grline/v2")
    public ResponseEntity<?> postGrLineV2(@Valid @RequestBody List<AddGrLineV2> newGrLine, @RequestParam String loginUserID,
                                          @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        GrLineV2[] createdGrLine = transactionService.createGrLineV2(newGrLine, loginUserID, authToken);
        return new ResponseEntity<>(createdGrLine, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = GrLineV2.class, value = "Update GrLine V2") // label for swagger
    @RequestMapping(value = "/grline/{lineNo}/v2", method = RequestMethod.PATCH)
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
    @DeleteMapping("/grline/{lineNo}/v2")
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

    /*
     * --------------------------------PutAwayHeader-V2-------------------------------
     */

    //Get
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Get a PutAwayHeader V2") // label for swagger
    @GetMapping("/putawayheader/{putAwayNumber}/v2")
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
    @GetMapping("/putawayheader/{refDocNumber}/inboundreversal/asn/v2")
    public ResponseEntity<?> getPutAwayHeaderForASNV2(@RequestParam String companyCode, @RequestParam String plantId,
                                                      @RequestParam String languageId, @PathVariable String refDocNumber, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayHeaderV2[] putawayheader = transactionService.getPutAwayHeaderV2(companyCode, plantId, languageId, refDocNumber, authToken);
        log.info("PutAwayHeader : " + putawayheader);
        return new ResponseEntity<>(putawayheader, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Search PutAwayHeader V2") // label for swagger
    @PostMapping("/putawayheader/findPutAwayHeader/v2")
    public PutAwayHeaderV2[] findPutAwayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeader, @RequestParam String authToken)
            throws Exception {
        return transactionService.findPutAwayHeaderV2(searchPutAwayHeader, authToken);
    }

    //Create
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Create PutAwayHeader V2") // label for swagger
    @PostMapping("/putawayheader/v2")
    public ResponseEntity<?> postPutAwayHeaderV2(@Valid @RequestBody PutAwayHeaderV2 newPutAwayHeader, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayHeaderV2 createdPutAwayHeader = transactionService.createPutAwayHeaderV2(newPutAwayHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayHeader, HttpStatus.OK);
    }

    //Upadte
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Update PutAwayHeader V2") // label for swagger
    @RequestMapping(value = "/putawayheader/v2/{putAwayNumber}", method = RequestMethod.PATCH)
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
    @PatchMapping("/putawayheader/{refDocNumber}/reverse/v2")
    public ResponseEntity<?> patchPutAwayHeaderV2(@PathVariable String refDocNumber, @RequestParam String packBarcodes, @RequestParam String warehouseId,
                                                  @RequestParam String companyCode, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        transactionService.updatePutAwayHeaderV2(companyCode, plantId, languageId, warehouseId, refDocNumber, packBarcodes, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PutAwayHeaderV2.class, value = "batch PutAwayHeaderV2 Reversal") // label for swagger
    @PatchMapping("/putawayheader/reverse/batch/v2")
    public ResponseEntity<?> batchPutAwayHeaderReversalV2(@RequestBody List<InboundReversalInput> inboundReversalInput,
                                                          @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.batchPutAwayHeaderReversalV2(inboundReversalInput, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Delete
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Delete PutAwayHeader V2") // label for swagger
    @DeleteMapping("/putawayheader/{putAwayNumber}/v2")
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
    @GetMapping("/putawayline/{confirmedStorageBin}/v2")
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
    @GetMapping("/putawayline/{refDocNumber}/inboundreversal/palletId/v2")
    public ResponseEntity<?> getPutAwayLineForInboundLineV2(@PathVariable String refDocNumber, @RequestParam String companyCode,
                                                            @RequestParam String plantId, @RequestParam String languageId, @RequestParam String authToken) throws java.text.ParseException {
        PutAwayLineV2[] putawayline = transactionService.getPutAwayLineV2(companyCode, plantId, languageId, refDocNumber, authToken);
        log.info("PutAwayLine : " + putawayline);
        return new ResponseEntity<>(putawayline, HttpStatus.OK);
    }

    //Find
    @ApiOperation(response = PutAwayLineV2.class, value = "Search PutAwayLine V2") // label for swagger
    @PostMapping("/putawayline/findPutAwayLine/v2")
    public PutAwayLineV2[] findPutAwayLineV2(@RequestBody SearchPutAwayLineV2 searchPutAwayLine,
                                             @RequestParam String authToken) throws Exception {
        return transactionService.findPutAwayLineV2(searchPutAwayLine, authToken);
    }

    //Create
    @ApiOperation(response = PutAwayLineV2.class, value = "Create PutAwayLine V2") // label for swagger
    @PostMapping("/putawayline/v2")
    public ResponseEntity<?> postPutAwayLineV2(@Valid @RequestBody List<PutAwayLineV2> newPutAwayLine, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PutAwayLineV2[] createdPutAwayLine = transactionService.createPutAwayLineV2(newPutAwayLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPutAwayLine, HttpStatus.OK);
    }

    //Update
    @ApiOperation(response = PutAwayLineV2.class, value = "Update PutAwayLine V2") // label for swagger
    @RequestMapping(value = "/putawayline/v2", method = RequestMethod.PATCH)
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
    @DeleteMapping("/putawayline/{confirmedStorageBin}/v2")
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
    @PostMapping("/inventory/findInventory/v2")
    public InventoryV2[] findInventoryV2(@RequestBody SearchInventoryV2 searchInventory,
                                         @RequestParam String authToken) throws Exception {
        return transactionService.findInventoryV2(searchInventory, authToken);
    }

    @ApiOperation(response = InventoryV2.class, value = "Create Inventory V2") // label for swagger
    @PostMapping("/v2/inventory")
    public ResponseEntity<?> postInventoryV2(@Valid @RequestBody InventoryV2 newInventory, @RequestParam String loginUserID,
                                           @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        InventoryV2 createdInventory = transactionService.createInventoryV2(newInventory, loginUserID, authToken);
        return new ResponseEntity<>(createdInventory, HttpStatus.OK);
    }

    @ApiOperation(response = InventoryV2.class, value = "Update Inventory V2") // label for swagger
    @RequestMapping(value = "/inventory/v2/{stockTypeId}", method = RequestMethod.PATCH)
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
    @DeleteMapping("/inventory/v2/{stockTypeId}")
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
    @PostMapping("/warehouse/inbound/asn/v2")
    public ResponseEntity<?> postASN(@Valid @RequestBody ASNV2 asnv2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postASNV2(asnv2, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
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
    @PostMapping("/warehouse/inbound/asn/upload/v2")
    public ResponseEntity<?> postWarehouseInboundAsnUploadV2(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processAsnOrders(file);
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
    public ResponseEntity<?> postSoReturnV2(@Valid @RequestBody SaleOrderReturnV2 saleOrderReturnV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postSOReturnV2(saleOrderReturnV2, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
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

    /*-----------------------------CycleCountOrder------------------------------------------------*/

    //Perpetual
    @ApiOperation(response = Perpetual.class, value = "Create Perpetual Order") // label for swagger
    @PostMapping("/warehouse/stockcount/perpetual")
    public ResponseEntity<?> postPerpetual(@Valid @RequestBody Perpetual perpetual, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPerpetual = transactionService.postPerpetual(perpetual, authToken);
        return new ResponseEntity<>(createdPerpetual, HttpStatus.OK);
    }

    //Periodic
    @ApiOperation(response = Periodic.class, value = "Create Periodic Order") // label for swagger
    @PostMapping("/warehouse/stockcount/periodic")
    public ResponseEntity<?> postPeriodic(@Valid @RequestBody Periodic periodic, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPeriodic = transactionService.postPeriodic(periodic, authToken);
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
        return transactionService.findPreOutboundHeaderV2(searchPreOutboundHeader, authToken);
    }

    /*
     * -------------------PreOutboundLine---------------------------------------------------
     */
    @ApiOperation(response = PreOutboundLineV2.class, value = "Search PreOutboundLine V2") // label for swagger
    @PostMapping("/preoutboundline/v2/findPreOutboundLine")
    public PreOutboundLineV2[] findPreOutboundLineV2(@RequestBody SearchPreOutboundLineV2 searchPreOutboundLine,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.findPreOutboundLineV2(searchPreOutboundLine, authToken);
    }

    /*
     * --------------------------------OrderMangementLine---------------------------------
     */
    @ApiOperation(response = OrderManagementLineV2.class, value = "Search OrderMangementLine V2") // label for swagger
    @PostMapping("/ordermanagementline/v2/findOrderManagementLine")
    public OrderManagementLineV2[] findOrderManagementLineV2(@RequestBody SearchOrderManagementLineV2 searchOrderMangementLine,
                                                             @RequestParam String authToken) throws Exception {
        return transactionService.findOrderManagementLineV2(searchOrderMangementLine, authToken);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/unallocate/patch")
    public ResponseEntity<?> patchUnallocateV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                transactionService.doUnAllocationV2(orderManagementLineV2, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "UnAllocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/unallocate")
    public ResponseEntity<?> patchUnallocateV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                               @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                               @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                               @RequestParam String itemCode, @RequestParam String proposedStorageBin, @RequestParam String proposedPackBarCode,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2 updatedOrderManagementLine =
                transactionService.doUnAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, proposedStorageBin, proposedPackBarCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Batch Allocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/allocate/patch")
    public ResponseEntity<?> patchAllocateV2(@Valid @RequestBody List<OrderManagementLineV2> orderManagementLineV2, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                transactionService.doAllocationV2(orderManagementLineV2, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Allocate V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/allocate")
    public ResponseEntity<?> patchAllocateV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                             @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                             @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam Long lineNumber,
                                             @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2 updatedOrderManagementLine =
                transactionService.doAllocationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                        itemCode, loginUserID, authToken);
        return new ResponseEntity<>(updatedOrderManagementLine, HttpStatus.OK);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Assign Picker V2") // label for swagger
    @PatchMapping("/ordermanagementline/v2/assignPicker")
    public ResponseEntity<?> assignPickerV2(@RequestBody List<AssignPickerV2> assignPicker,
                                            @RequestParam String assignedPickerId, @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OrderManagementLineV2[] updatedOrderManagementLine =
                transactionService.doAssignPickerV2(assignPicker, assignedPickerId, loginUserID, authToken);
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
                transactionService.updateOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
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
        transactionService.deleteOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, proposedStorageBin, proposedPackCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = OrderManagementLineV2.class, value = "Update Reference Field OrderManagementLine V2")
    // label for swagger
    @GetMapping("/ordermanagementline/v2/updateRefFields")
    public ResponseEntity<?> updateRefFieldsV2(@RequestParam String authToken) throws Exception {
        transactionService.updateRef9ANDRef10V2(authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*
     * -------------------------PickupHeader----------------------------------------------------
     */
    @ApiOperation(response = PickupHeaderV2.class, value = "Search PickupHeader V2") // label for swagger
    @PostMapping("/pickupheader/v2/findPickupHeader")
    public PickupHeaderV2[] findPickupHeaderV2(@RequestBody SearchPickupHeaderV2 searchPickupHeader,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findPickupHeaderV2(searchPickupHeader, authToken);
    }

    @ApiOperation(response = PickupHeaderV2.class, value = "Update PickupHeader V2") // label for swagger
    @PatchMapping("/pickupheader/v2/{pickupNumber}")
    public ResponseEntity<?> patchPickupHeaderV2(@PathVariable String pickupNumber, @RequestParam String warehouseId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String preOutboundNo, @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                 @RequestParam Long lineNumber, @RequestParam String itemCode, @Valid @RequestBody PickupHeaderV2 updatePickupHeader,
                                                 @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeaderV2 createdPickupHeader =
                transactionService.updatePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
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
    @PatchMapping("/pickupheader/v2/update-assigned-picker")
    public ResponseEntity<?> patchAssignedPickerIdInPickupHeaderV2(@Valid @RequestBody List<PickupHeaderV2> updatePickupHeaderList, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupHeaderV2[] updatedPickupHeader =
                transactionService.patchAssignedPickerIdInPickupHeaderV2(updatePickupHeaderList, authToken);
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
        transactionService.deletePickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber,
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
        InventoryV2[] additionalBins = transactionService.getAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode, obOrdertypeId,
                proposedPackBarCode, manufacturerName, proposedStorageBin, authToken);
        log.info("additionalBins : " + additionalBins);
        return new ResponseEntity<>(additionalBins, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Create PickupLine V2") // label for swagger
    @PostMapping("/v2/pickupline")
    public ResponseEntity<?> postPickupLineV2(@Valid @RequestBody List<AddPickupLine> newPickupLine,
                                              @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PickupLineV2[] createdPickupLine = transactionService.createPickupLineV2(newPickupLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = PickupLineV2.class, value = "Search PickupLine V2") // label for swagger
    @PostMapping("/pickupline/v2/findPickupLine")
    public PickupLineV2[] findPickupLineV2(@RequestBody SearchPickupLineV2 searchPickupLine,
                                           @RequestParam String authToken) throws Exception {
        return transactionService.findPickupLineV2(searchPickupLine, authToken);
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
                transactionService.updatePickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode,
                        actualHeNo, loginUserID, updatePickupLine, authToken);
        return new ResponseEntity<>(createdPickupLine, HttpStatus.OK);
    }

    @ApiOperation(response = ImPartner.class, value = "Update BarcodeId") // label for swagger
    @PatchMapping("/pickupline/v2/barcodeId")
    public ResponseEntity<?> patchPickupLineBarcodeIdV2(@Valid @RequestBody UpdateBarcodeInput updateBarcodeInput, @RequestParam String authToken) {
        ImPartner results = transactionService.updatePickupLineForBarcodeId(updateBarcodeInput, authToken);
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
        transactionService.deletePickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
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
        QualityHeaderV2 createdQualityHeader = transactionService.createQualityHeaderV2(newQualityHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Search QualityHeader V2") // label for swagger
    @PostMapping("/qualityheader/v2/findQualityHeader")
    public QualityHeaderV2[] findQualityHeaderV2(@RequestBody SearchQualityHeaderV2 searchQualityHeader,
                                                 @RequestParam String authToken) throws Exception {
        return transactionService.findQualityHeaderV2(searchQualityHeader, authToken);
    }

    @ApiOperation(response = QualityHeaderV2.class, value = "Update QualityHeader V2") // label for swagger
    @PatchMapping("/qualityheader/v2/{qualityInspectionNo}")
    public ResponseEntity<?> patchQualityHeaderV2(@PathVariable String qualityInspectionNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                  @RequestParam String refDocNumber, @RequestParam String partnerCode,
                                                  @RequestParam String pickupNumber, @RequestParam String actualHeNo,
                                                  @Valid @RequestBody QualityHeaderV2 updateQualityHeader,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        QualityHeaderV2 updatedQualityHeader = transactionService.updateQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
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
        transactionService.deleteQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
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
        return transactionService.findQualityLineV2(searchQualityLine, authToken);
    }

    @ApiOperation(response = QualityLineV2.class, value = "Create QualityLine V2") // label for swagger
    @PostMapping("/v2/qualityline")
    public ResponseEntity<?> postQualityLineV2(@Valid @RequestBody List<AddQualityLine> newQualityLine,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        QualityLineV2[] createdQualityLine = transactionService.createQualityLineV2(newQualityLine, loginUserID, authToken);
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
                transactionService.updateQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
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
        transactionService.deleteQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ---------------------------OutboundHeader--------------------------------------------------------
     */
    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2") // label for swagger
    @PostMapping("/outboundheader/v2/findOutboundHeader")
    public OutboundHeaderV2[] findOutboundHeaderV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                   @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundHeaderV2(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2 for Delivery")    // label for swagger
    @PostMapping("/outboundheader/v2/findOutboundHeader/delivery")
    public OutboundHeaderV2[] findOutboundHeaderDeliveryV2(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                           @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundHeaderDeliveryV2(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Search OutboundHeader V2") // label for swagger
    @PostMapping("/outboundheader/v2/findOutboundHeader/rfd")
    public OutboundHeaderV2[] findOutboundHeaderV2Rfd(@RequestBody SearchOutboundHeaderV2 searchOutboundHeader,
                                                      @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundHeaderRfd(searchOutboundHeader, authToken);
    }

    @ApiOperation(response = OutboundHeaderV2.class, value = "Update OutboundHeader V2") // label for swagger
    @PatchMapping("/outboundheader/v2/{preOutboundNo}")
    public ResponseEntity<?> patchOutboundHeaderV2(@PathVariable String preOutboundNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                   @RequestParam String partnerCode, @Valid @RequestBody OutboundHeaderV2 updateOutboundHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundHeaderV2 createdOutboundHeader =
                transactionService.updateOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                        updateOutboundHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdOutboundHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OutboundHeader.class, value = "Delete OutboundHeader V2") // label for swagger
    @DeleteMapping("/outboundheader/v2/{preOutboundNo}")
    public ResponseEntity<?> deleteOutboundHeader(@PathVariable String preOutboundNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                  @RequestParam String partnerCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * ----------------------OutboundLine----------------------------------------------------------
     */
    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine V2") // label for swagger
    @PostMapping("/outboundline/v2/findOutboundLine")
    public OutboundLineV2[] findOutboundLineV2(@RequestBody SearchOutboundLineV2 searchOutboundLine,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundLineV2(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Search OutboundLine V2 stream") // label for swagger
    @PostMapping("/outboundline/v2/findOutboundLine/stream")
    public OutboundLineV2[] findOutboundLineStreamV2(@RequestBody SearchOutboundLineV2 searchOutboundLine,
                                                     @RequestParam String authToken) throws Exception {
        return transactionService.findOutboundLineStreamV2(searchOutboundLine, authToken);
    }

    @ApiOperation(response = OutboundLineV2.class, value = "Update OutboundLine V2") // label for swagger
    @GetMapping("/outboundline/v2/delivery/confirmation")
    public ResponseEntity<?> deliveryConfirmationV2(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String warehouseId, @RequestParam String preOutboundNo,
                                                    @RequestParam String refDocNumber, @RequestParam String partnerCode, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        OutboundLineV2[] createdOutboundLine =
                transactionService.deliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
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
                transactionService.updateOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
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
        transactionService.deleteOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
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
        return transactionService.findOutboundReversalV2(searchOutboundReversal, authToken);
    }

    /*--------------------Shipping Reversal-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Get Delivery Lines V2") // label for swagger
    @GetMapping("/outboundreversal/v2/reversal/new")
    public ResponseEntity<?> doReversalV2(@RequestParam String refDocNumber, @RequestParam String itemCode, @RequestParam String manufacturerName,
                                          @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversalV2[] deliveryLines = transactionService.doReversalV2(refDocNumber, itemCode, manufacturerName, loginUserID, authToken);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    /*--------------------Shipping Reversal Batch-----------------------------------------------------------*/
    @ApiOperation(response = OutboundLineV2.class, value = "Batch Reversal Delivery Lines V2") // label for swagger
    @PostMapping("/outboundreversal/v2/reversal/batch")
    public ResponseEntity<?> doReversalBatchV2(@RequestBody List<InboundReversalInput> outboundReversalInput,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        OutboundReversalV2[] deliveryLines = transactionService.doReversalBatchV2(outboundReversalInput, loginUserID, authToken);
        log.info("deliveryLines : " + deliveryLines);
        return new ResponseEntity<>(deliveryLines, HttpStatus.OK);
    }

    //----------------------Orders-V2-----------------------------------------------------------------

    //ShipmentOrder V2
    @ApiOperation(response = ShipmentOrderV2.class, value = "Create Shipment Order V2") // label for swagger
    @PostMapping("/warehouse/v2/outbound/so")
    public ResponseEntity<?> postShipmenOrderV2(@Valid @RequestBody ShipmentOrderV2 shipmenOrder, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSO = transactionService.postSOV2(shipmenOrder, authToken);
        return new ResponseEntity<>(createdSO, HttpStatus.OK);
    }


    //SalesOrder V2
    @ApiOperation(response = SalesOrderV2.class, value = "Create SalesOrderV2") // label for swagger
    @PostMapping("/warehouse/outbound/salesorder/v2")
    public ResponseEntity<?> createSalesOrderV2(@Valid @RequestBody SalesOrderV2 salesOrderV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSoV2 = transactionService.postSalesOrderV2(salesOrderV2, authToken);
        return new ResponseEntity<>(createdSoV2, HttpStatus.OK);
    }


    //ReturnPOV2
    @ApiOperation(response = ReturnPOV2.class, value = "Create ReturnPOV2") //label for Swagger
    @PostMapping("/warehouse/outbound/returnpo/v2")
    public ResponseEntity<?> createReturnPoV2(@Valid @RequestBody ReturnPOV2 returnPOV2, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdRetPoV2 = transactionService.postReturnPoV2(returnPOV2, authToken);
        return new ResponseEntity<>(createdRetPoV2, HttpStatus.OK);
    }


    //InterWarehouseTransferOutV2
    @ApiOperation(response = InterWarehouseTransferOutV2.class, value = "Create InterWarehouseTransferOutV2")
    @PostMapping("/warehouse/outbound/interwarehousetransferout/v2")
    public ResponseEntity<?> createIWhTransferOutV2(@Valid @RequestBody InterWarehouseTransferOutV2 iWhTransferOutV2,
                                                    @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdIWhTransferOutV2 = transactionService.postInterWhTransferOutV2(iWhTransferOutV2, authToken);
        return new ResponseEntity<>(createdIWhTransferOutV2, HttpStatus.OK);
    }


    //SalesInvoice
    @ApiOperation(response = SalesInvoice.class, value = "Create SalesInvoice") //label for Swagger
    @PostMapping("/warehouse/outbound/salesinvoice")
    public ResponseEntity<?> createSalesInvoice(@Valid @RequestBody SalesInvoice salesInvoice, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdSI = transactionService.postSalesInvoice(salesInvoice, authToken);
        return new ResponseEntity<>(createdSI, HttpStatus.OK);
    }

    /*
     * -----------------------------Perpetual Count----------------------------------------------------
     */
    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get all PerpetualHeader details V2")
    // label for swagger
    @GetMapping("/v2/perpetualheader")
    public ResponseEntity<?> getPerpetualHeadersV2(@RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2[] perpetualheaderList = transactionService.getPerpetualHeadersV2(authToken);
        return new ResponseEntity<>(perpetualheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Get a PerpetualHeader V2") // label for swagger
    @GetMapping("/perpetualheader/v2/{cycleCountNo}")
    public ResponseEntity<?> getPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                  @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                  @RequestParam String authToken) throws Exception {
        PerpetualHeaderEntityV2 perpetualheader =
                transactionService.getPerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
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
    @PostMapping("/perpetualheader/v2/findPerpetualHeader")
    public PerpetualHeaderEntityV2[] findPerpetualHeaderV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                           @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualHeaderV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderV2[].class, value = "Search PerpetualHeader New V2") // label for swagger
    @PostMapping("/perpetualheader/v2/findPerpetualHeaderNew")
    public PerpetualHeaderV2[] findPerpetualHeaderNewV2(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeader,
                                                        @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualHeaderEntityV2(searchPerpetualHeader, authToken);
    }

    @ApiOperation(response = PerpetualHeaderEntityV2.class, value = "Create PerpetualHeader V2") // label for swagger
    @PostMapping("/v2/perpetualheader")
    public ResponseEntity<?> postPerpetualHeaderV2(@Valid @RequestBody PerpetualHeaderEntityV2 newPerpetualHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeaderEntityV2 createdPerpetualHeader =
                transactionService.createPerpetualHeaderV2(newPerpetualHeader, loginUserID, authToken);
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
                transactionService.runPerpetualHeaderV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Create PerpetualHeader Stream V2") // label for swagger
    @PostMapping("/perpetualheader/v2/runNew")
    public ResponseEntity<?> postRunPerpetualHeaderStreamV2(@Valid @RequestBody RunPerpetualHeader runPerpetualHeader,
                                                            @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] perpetualLineEntity =
                transactionService.runPerpetualHeaderNewV2(runPerpetualHeader, authToken);
        return new ResponseEntity<>(perpetualLineEntity, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualHeaderV2.class, value = "Update PerpetualHeader V2") // label for swagger
    @PatchMapping("/perpetualheader/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                    @Valid @RequestBody PerpetualHeaderEntityV2 updatePerpetualHeader, @RequestParam String loginUserID,
                                                    @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualHeaderV2 createdPerpetualHeader =
                transactionService.updatePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                        subMovementTypeId, loginUserID, updatePerpetualHeader, authToken);
        return new ResponseEntity<>(createdPerpetualHeader, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Delete PerpetualHeader V2") // label for swagger
    @DeleteMapping("/perpetualheader/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePerpetualHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                                     @RequestParam String warehouseId, @RequestParam Long cycleCountTypeId, @RequestParam Long movementTypeId, @RequestParam Long subMovementTypeId,
                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePerpetualHeaderV2(companyCodeId, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, movementTypeId,
                subMovementTypeId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PerpetualLine---------------------------------------------------------------------
    @ApiOperation(response = PerpetualLineV2.class, value = "SearchPerpetualLine V2") // label for swagger
    @PostMapping("/v2/findPerpetualLine")
    public PerpetualLineV2[] findPerpetualLineV2(@RequestBody SearchPerpetualLineV2 searchPerpetualLine,
                                                 @RequestParam String authToken) throws Exception {
        return transactionService.findPerpetualLineV2(searchPerpetualLine, authToken);
    }

    @ApiOperation(response = PerpetualLineV2[].class, value = "Update AssignHHTUser V2") // label for swagger
    @PatchMapping("/perpetualline/v2/assigingHHTUser")
    public ResponseEntity<?> patchAssingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                                  @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] createdPerpetualLine = transactionService.updateAssingHHTUserV2(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualUpdateResponseV2.class, value = "Update PerpetualLine V2") // label for swagger
    @PatchMapping("/perpetualline/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineV2(@PathVariable String cycleCountNo,
                                                  @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                  @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualUpdateResponseV2 createdPerpetualLine =
                transactionService.updatePerpetualLineV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PerpetualLine confirm V2") // label for swagger
    @PatchMapping("/perpetualline/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPerpetualLineConfirmV2(@PathVariable String cycleCountNo,
                                                         @RequestBody List<PerpetualLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createdPerpetualLine =
                transactionService.updatePerpetualLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PerpetualLineV2.class, value = "Create PerpetualLine From ZeroStock V2") // label for swagger
    @PostMapping("/perpetualline/v2/createPerpetualFromZeroStk")
    public ResponseEntity<?> createPerpetualFromZeroStk(@RequestBody List<PerpetualZeroStockLine> updatePerpetualLine, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PerpetualLineV2[] createdPerpetualLine =
                transactionService.updatePerpetualZeroStkLine(updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPerpetualLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLineV2.class, value = "Create PeriodicLine from ZeroStock V2") // label for swagger
    @PostMapping("/periodicline/v2/createPeriodicFromZeroStk")
    public ResponseEntity<?> createPeriodicFromZeroStk(@RequestBody List<PeriodicZeroStockLine> updatePeriodicLine, @RequestParam String loginUserID,
                                                         @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicLineV2[] createdPeriodicLine =
                transactionService.updatePeriodicZeroStkLine(updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

    /*
     * -----------------------------Periodic Count----------------------------------------------------
     */
    @ApiOperation(response = PeriodicHeader.class, value = "Get all PeriodicHeader details V2") // label for swagger
    @GetMapping("/periodicheader/v2")
    public ResponseEntity<?> getPeriodicHeadersV2(@RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity[] PeriodicheaderList = transactionService.getPeriodicHeadersV2(authToken);
        return new ResponseEntity<>(PeriodicheaderList, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeaderV2.class, value = "Get a PeriodicHeader v2") // label for swagger
    @GetMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> getPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String warehouseId,
                                                 @RequestParam String companyCode, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam Long cycleCountTypeId,
                                                 @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntityV2 Periodicheader =
                transactionService.getPeriodicHeaderV2(warehouseId, companyCode, plantId, languageId, cycleCountTypeId, cycleCountNo, authToken);
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
        return transactionService.findPeriodicHeaderV2(searchPeriodicHeader, authToken);
    }

//    @ApiOperation(response = PeriodicHeaderEntityV2[].class, value = "Search PeriodicHeader V2") // label for swagger
//    @PostMapping("/periodicheader/v2/findPeriodicHeaderWithLines")
//    public PeriodicHeaderEntityV2[] findPeriodicHeaderEntityV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
//                                                               @RequestParam String authToken) throws Exception {
//        return transactionService.findPeriodicHeaderEntityV2(searchPeriodicHeader, authToken);
//    }

    //Stream
    @ApiOperation(response = PeriodicHeaderV2[].class, value = "Search PeriodicHeader New V2") // label for swagger
    @PostMapping("/periodicheader/v2/findPeriodicHeaderNew")
    public ResponseEntity<?> findPeriodicHeaderNewV2(@RequestBody SearchPeriodicHeader searchPeriodicHeader,
                                                     @RequestParam String authToken) throws Exception {
        PeriodicHeaderV2[] results = transactionService.findPeriodicHeaderStreamV2(searchPeriodicHeader, authToken);
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Create PeriodicHeader V2") // label for swagger
    @PostMapping("/periodicheader/v2")
    public ResponseEntity<?> postPeriodicHeaderV2(@Valid @RequestBody AddPeriodicHeader newPeriodicHeader,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) throws Exception {
        PeriodicHeaderEntity createdPeriodicHeader = transactionService.createPeriodicHeaderV2(newPeriodicHeader, loginUserID, authToken);
        log.info("createdPeriodicHeader:" + createdPeriodicHeader);

        /* Call Batch */
//		transactionService.createCSV(newPeriodicHeader.getPeriodicLine());
//		batchJobScheduler.runJobPeriodic();
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Update PeriodicHeader V2") // label for swagger
    @PatchMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String languageId, @RequestParam String warehouseId,
                                                   @RequestParam Long cycleCountTypeId, @RequestBody PeriodicHeaderEntityV2 updatePeriodicHeader,
                                                   @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicHeaderV2 createdPeriodicHeader =
                transactionService.updatePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo,
                        loginUserID, updatePeriodicHeader, authToken);
        return new ResponseEntity<>(createdPeriodicHeader, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicHeader.class, value = "Delete PeriodicHeader v2") // label for swagger
    @DeleteMapping("/periodicheader/v2/{cycleCountNo}")
    public ResponseEntity<?> deletePeriodicHeaderV2(@PathVariable String cycleCountNo, @RequestParam String warehouseId, @RequestParam String companyCode,
                                                    @RequestParam String plantId, @RequestParam String languageId,
                                                    @RequestParam Long cycleCountTypeId, @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deletePeriodicHeaderV2(companyCode, plantId, languageId, warehouseId, cycleCountTypeId, cycleCountNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //-------------------------------PeriodicLine---------------------------------------------------------------------
    @ApiOperation(response = PeriodicLineV2.class, value = "SearchPeriodicLine V2") // label for swagger
    @PostMapping("/periodicline/v2/findPeriodicLine")
    public PeriodicLineV2[] findPeriodicLineV2(@RequestBody SearchPeriodicLineV2 searchPeriodicLine,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findPeriodicLineV2(searchPeriodicLine, authToken);
    }

    @ApiOperation(response = PeriodicLineV2[].class, value = "Update AssignHHTUser V2") // label for swagger
    @PatchMapping("/periodicline/v2/assigingHHTUser")
    public ResponseEntity<?> assigingHHTUserV2(@RequestBody List<AssignHHTUserCC> assignHHTUser,
                                               @RequestParam String loginUserID, @RequestParam String authToken)
            throws IllegalAccessException, InvocationTargetException {
        PeriodicLineV2[] createdPeriodicLine =
                transactionService.updatePeriodicLineAssingHHTUserV2(assignHHTUser, loginUserID, authToken);
        return new ResponseEntity<>(createdPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = PeriodicLine.class, value = "Update PeriodicLine V2") // label for swagger
    @PatchMapping("/periodicline/v2/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineV2(@PathVariable String cycleCountNo,
                                                 @RequestBody List<PeriodicLineV2> updatePeriodicLine, @RequestParam String loginUserID,
                                                 @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        PeriodicUpdateResponseV2 updatedPeriodicLine =
                transactionService.updatePeriodicLineV2(cycleCountNo, updatePeriodicLine, loginUserID, authToken);
        return new ResponseEntity<>(updatedPeriodicLine, HttpStatus.OK);
    }

    @ApiOperation(response = WarehouseApiResponse.class, value = "Update PeriodicLine confirm V2") // label for swagger
    @PatchMapping("/periodicline/v2/confirm/{cycleCountNo}")
    public ResponseEntity<?> patchPeriodicLineConfirmV2(@PathVariable String cycleCountNo,
                                                        @RequestBody List<PeriodicLineV2> updatePerpetualLine, @RequestParam String loginUserID,
                                                        @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        WarehouseApiResponse createPeriodicLine =
                transactionService.updatePeriodicLineConfirmV2(cycleCountNo, updatePerpetualLine, loginUserID, authToken);
        return new ResponseEntity<>(createPeriodicLine, HttpStatus.OK);
    }

    //-----------------------------------------------------------StockAdjustment---------------------------------------------------------

    @ApiOperation(response = StockAdjustment.class, value = "Get a StockAdjustment") // label for swagger
    @GetMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> getStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                @RequestParam String manufacturerName, @RequestParam String storageBin,
                                                @RequestParam String authToken) throws Exception {
        StockAdjustment[] stockAdjustment =
                transactionService.getStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey,
                        itemCode, manufacturerName, storageBin, authToken);
        log.info("StockAdjustment : " + stockAdjustment);
        return new ResponseEntity<>(stockAdjustment, HttpStatus.OK);
    }

    @ApiOperation(response = StockAdjustment[].class, value = "Search StockAdjustment V2") // label for swagger
    @PostMapping("/stockAdjustment/findStockAdjustment")
    public StockAdjustment[] findStockAdjustment(@RequestBody SearchStockAdjustment searchInventory,
                                                 @RequestParam String authToken) throws Exception {
        return transactionService.findStockAdjustment(searchInventory, authToken);
    }

    @ApiOperation(response = StockAdjustment.class, value = "Update StockAdjustment V2") // label for swagger
    @PatchMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> patchStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                  @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                  @RequestParam String manufacturerName, @RequestParam String loginUserID,
                                                  @RequestBody List<StockAdjustment> updateStockAdjustment, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {
        StockAdjustment[] updatedStockAdjustment =
                transactionService.updateStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey, itemCode,
                        manufacturerName, loginUserID, updateStockAdjustment, authToken);
        return new ResponseEntity<>(updatedStockAdjustment, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Delete StockAdjustment V2") // label for swagger
    @DeleteMapping("/stockAdjustment/{stockAdjustmentKey}")
    public ResponseEntity<?> deleteStockAdjustment(@PathVariable Long stockAdjustmentKey, @RequestParam String languageId, @RequestParam String companyCode,
                                                   @RequestParam String plantId, @RequestParam String warehouseId, @RequestParam String itemCode,
                                                   @RequestParam String manufacturerName, @RequestParam String storageBin,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
        transactionService.deleteStockAdjustment(companyCode, plantId, languageId, warehouseId, stockAdjustmentKey, itemCode, manufacturerName, storageBin, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /*------------------------------------------Supplier Invoice Cancellation--------------------------*/

    @ApiOperation(response = InboundHeader.class, value = "SupplierInvoice Replace") // label for swagger
    @GetMapping("/SalesInvoice/replace")
    public ResponseEntity<?> patchSalesInvoiceReplace(@RequestParam String companyCode, @RequestParam String languageId, @RequestParam String plantId,
                                                      @RequestParam String warehouseId, @RequestParam String oldInvoiceNumber, @RequestParam String newInvoiceNumber,
                                                      @RequestParam String oldPreInboundNo, @RequestParam String newPreInboundNo,
                                                      @RequestParam String loginUserID, @RequestParam String authToken) throws IllegalAccessException, InvocationTargetException {

      WarehouseApiResponse result = transactionService.replaceInvoice(companyCode, plantId, languageId, warehouseId, newInvoiceNumber,oldInvoiceNumber,
              newPreInboundNo,oldPreInboundNo, loginUserID, authToken);
      return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation(response = PreInboundHeaderV2.class, value = "Inbound Order Cancellation") // label for swagger
    @PostMapping("/inboundOrder/cancellation")
    public ResponseEntity<?> cancelInboundOrder(@RequestBody InboundOrderCancelInput inboundOrderCancelInput,
                                                @RequestParam String loginUserID, @RequestParam String authToken) {

        PreInboundHeaderV2 result = transactionService.inboundOrderCancellation(inboundOrderCancelInput, loginUserID, authToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //==========================================Get All Exception Log Details==========================================
    @ApiOperation(response = ExceptionLog.class, value = "Get All Exception Log Details")
    @GetMapping("/exceptionlog/all")
    public ResponseEntity<?> getAllExceptionLogDetails(@RequestParam String authToken) {
        ExceptionLog[] exceptionLogs = transactionService.getAllExceptionLogs(authToken);
        return new ResponseEntity<>(exceptionLogs, HttpStatus.OK);
    }

    //===============================================Find Order Details=================================================

    //Find InBoundOrder
    @ApiOperation(response = InboundOrderV2[].class, value = "Find InboundOrderV2")//label for swagger
    @PostMapping("/findInboundOrderV2")
    public ResponseEntity<?> findInboundOrderV2(@RequestBody FindInboundOrderV2 findInboundOrderV2,
                                                @RequestParam String authToken) {
        InboundOrderV2[] inboundOrderV2 = transactionService.findInboundOrderV2(findInboundOrderV2, authToken);
        return new ResponseEntity<>(inboundOrderV2, HttpStatus.OK);
    }

    //Find InBoundOrderLine
    @ApiOperation(response = InboundOrderLinesV2[].class, value = "Find InboundOrderLinesV2")//label for swagger
    @PostMapping("/findInboundOrderLineV2")
    public ResponseEntity<?> findInboundOrderLineV2(@RequestBody FindInboundOrderLineV2 findInboundOrderLineV2,
                                                @RequestParam String authToken) {
        InboundOrderLinesV2[] inboundOrderLinesV2 = transactionService.findInboundOrderLinesV2(findInboundOrderLineV2, authToken);
        return new ResponseEntity<>(inboundOrderLinesV2, HttpStatus.OK);
    }

    //Find OutBoundOrder
    @ApiOperation(response = OutboundOrderV2[].class, value = "Find OutboundOrderV2")//label for swagger
    @PostMapping("/findOutboundOrderV2")
    public ResponseEntity<?> findOutboundOrderV2(@RequestBody FindOutboundOrderV2 findOutboundOrderV2,
                                                    @RequestParam String authToken) {
        OutboundOrderV2[] outboundOrderV2 = transactionService.findOutboundOrderV2(findOutboundOrderV2, authToken);
        return new ResponseEntity<>(outboundOrderV2, HttpStatus.OK);
    }

    //Find OutBoundOrderLine
    @ApiOperation(response = OutboundOrderLineV2[].class, value = "Find OutboundOrderLineV2")//label for swagger
    @PostMapping("/findOutboundOrderLineV2")
    public ResponseEntity<?> findOutboundOrderLineV2(@RequestBody FindOutboundOrderLineV2 findOutboundOrderLineV2,
                                                 @RequestParam String authToken) {
        OutboundOrderLineV2[] outboundOrderLineV2 = transactionService.findOutboundOrderLineV2(findOutboundOrderLineV2, authToken);
        return new ResponseEntity<>(outboundOrderLineV2, HttpStatus.OK);
    }

    //findPickListHeader - Pick List Cancellation
    @ApiOperation(response = PickListHeader.class, value = "Search PickListHeader Cancellation") // label for swagger
    @PostMapping("/pickListCancellation/v2/findPickListHeader")
    public PickListHeader[] findPickListHeader(@RequestBody SearchPickListHeader searchPickListHeader,
                                               @RequestParam String authToken) throws Exception {
        return transactionService.findPickListHeader(searchPickListHeader, authToken);
    }

    //findSupplierInvoiceHeader - Supplier invoice Cancellation
    @ApiOperation(response = SupplierInvoiceHeader.class, value = "Search SupplierInvoiceHeader Cancellation") // label for swagger
    @PostMapping("/supplierInvoiceCancellation/v2/findSupplierInvoiceHeader")
    public SupplierInvoiceHeader[] findSupplierInvoiceHeader(@RequestBody SearchSupplierInvoiceHeader searchSupplierInvoiceHeader,
                                                             @RequestParam String authToken) throws Exception {
        return transactionService.findSupplierInvoiceHeader(searchSupplierInvoiceHeader, authToken);
    }

    @ApiOperation(response = PickListHeader.class, value = "order Cancellation") // label for swagger
    @PostMapping("/outbound/orderCancellation")
    public ResponseEntity<?> orderCancellation(@RequestBody OutboundOrderCancelInput outboundOrderCancelInput, @RequestParam String loginUserID,
                                               @RequestParam String authToken) throws java.text.ParseException {
        PreOutboundHeaderV2 orderCancelled = transactionService.orderCancellation(outboundOrderCancelInput, loginUserID, authToken);
        return new ResponseEntity<>(orderCancelled, HttpStatus.OK);
}

    // File Upload - InhouseTransfer
    @ApiOperation(response = Optional.class, value = "InhouseTransfer Upload V2") // label for swagger
    @PostMapping("/warehouse/makeAndChange/inhouseTransfer/upload/v2")
    public ResponseEntity<?> postInhouseTransferUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processBinToBin(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    // File Upload - StockAdjustment
    @ApiOperation(response = Optional.class, value = "Stock Adjustment Upload V2") // label for swagger
    @PostMapping("/warehouse/makeAndChange/stockAdjustment/upload/v2")
    public ResponseEntity<?> postStockAdjustmentUpload(@RequestParam("file") MultipartFile file) throws Exception {
        Map<String, String> response = fileStorageService.processStockAdjustment(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}