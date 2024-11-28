package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.model.mfg.Process;
import com.tekclover.wms.core.model.mfg.*;
import com.tekclover.wms.core.service.MfgService;
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
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/wms-mfg-service")
@Api(tags = {"Mfg Service"}, value = "Mfg Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Mfg Modules")})
public class MfgServiceController {

    @Autowired
    MfgService mfgService;

    // --------------------------------MasterOperation---------------------------------------------------------------
    @ApiOperation(response = MasterOperation.class, value = "Get a MasterOperation") // label for swagger
    @GetMapping("/masterOperation/{operationNumber}")
    public ResponseEntity<?> getMasterOperation(@PathVariable String operationNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String phaseNumber, @RequestParam String authToken) {
        MasterOperation masteroperation = mfgService.getMasterOperation(companyCodeId, plantId, languageId, warehouseId, operationNumber, phaseNumber, authToken);
        return new ResponseEntity<>(masteroperation, HttpStatus.OK);
    }

    @ApiOperation(response = MasterOperation.class, value = "Get a MasterOperation") // label for swagger
    @GetMapping("/masterOperation/v2/{operationNumber}")
    public ResponseEntity<?> getMasterOperation(@PathVariable String operationNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        MasterOperation[] masteroperation = mfgService.getMasterOperation(companyCodeId, plantId, languageId, warehouseId, operationNumber, authToken);
        return new ResponseEntity<>(masteroperation, HttpStatus.OK);
    }

    @ApiOperation(response = MasterOperation.class, value = "Search MasterOperation") // label for swagger
    @PostMapping("/masterOperation/findMasterOperation")
    public MasterOperation[] findMasterOperation(@RequestBody SearchMasterOperation searchMasterOperation, @RequestParam String authToken)
            throws Exception {
        return mfgService.findMasterOperation(searchMasterOperation, authToken);
    }

    @ApiOperation(response = MasterOperation.class, value = "Create MasterOperation") // label for swagger
    @PostMapping("masterOperation/create")
    public ResponseEntity<?> postMasterOperation(@Valid @RequestBody List<MasterOperation> newMasterOperation, @RequestParam String loginUserID, @RequestParam String authToken) {
        MasterOperation[] createdMasterOperation = mfgService.createMasterOperation(newMasterOperation, loginUserID, authToken);
        return new ResponseEntity<>(createdMasterOperation, HttpStatus.OK);
    }

    @ApiOperation(response = MasterOperation.class, value = "Patch MasterOperation") // label for swagger
    @PatchMapping("/masterOperation/update")
    public ResponseEntity<?> patchMasterOperation(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken,
                                                  @Valid @RequestBody List<MasterOperation> modifyMasterOperation) {
        MasterOperation[] createdMasterOperation = mfgService.updateMasterOperation(companyCodeId, plantId, languageId, warehouseId, loginUserID, modifyMasterOperation, authToken);
        return new ResponseEntity<>(createdMasterOperation, HttpStatus.OK);
    }

    @ApiOperation(response = MasterOperation.class, value = "Delete MasterOperation") // label for swagger
    @DeleteMapping("/masterOperation/{operationNumber}")
    public ResponseEntity<?> deleteMasterOperation(@PathVariable String operationNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam(required = false) String phaseNumber,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteMasterOperation(companyCodeId, plantId, languageId, warehouseId, operationNumber, phaseNumber, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------MasterReceipe---------------------------------------------------------------
    @ApiOperation(response = MasterReceipe.class, value = "Get a MasterReceipe") // label for swagger
    @GetMapping("/masterReceipe/{receipeId}")
    public ResponseEntity<?> getMasterReceipe(@PathVariable String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                              @RequestParam String itemCode, @RequestParam String bomNumber, @RequestParam String childItemCode,
                                              @RequestParam String phaseNumber, @RequestParam String authToken) {
        MasterReceipe masterreceipe = mfgService.getMasterReceipe(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, itemCode,
                bomNumber, phaseNumber, childItemCode, authToken);
        return new ResponseEntity<>(masterreceipe, HttpStatus.OK);
    }

    @ApiOperation(response = MasterReceipe.class, value = "Get a MasterReceipe") // label for swagger
    @GetMapping("/masterReceipe/v2/{receipeId}")
    public ResponseEntity<?> getMasterReceipe(@PathVariable String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam String operationNumber, @RequestParam String authToken) {
        MasterReceipe[] masterreceipe = mfgService.getMasterReceipe(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, authToken);
        return new ResponseEntity<>(masterreceipe, HttpStatus.OK);
    }

    @ApiOperation(response = MasterReceipe.class, value = "Search MasterReceipe") // label for swagger
    @PostMapping("/masterReceipe/findMasterReceipe")
    public MasterReceipe[] findMasterReceipe(@RequestBody SearchMasterReceipe searchMasterReceipe, @RequestParam String authToken)
            throws Exception {
        return mfgService.findMasterReceipe(searchMasterReceipe, authToken);
    }

    @ApiOperation(response = MasterReceipe.class, value = "Create MasterReceipe") // label for swagger
    @PostMapping("masterReceipe/create")
    public ResponseEntity<?> postMasterReceipe(@Valid @RequestBody List<MasterReceipe> newMasterReceipe, @RequestParam String loginUserID, @RequestParam String authToken) {
        MasterReceipe[] createdMasterReceipe = mfgService.createMasterReceipe(newMasterReceipe, loginUserID, authToken);
        return new ResponseEntity<>(createdMasterReceipe, HttpStatus.OK);
    }

    @ApiOperation(response = MasterReceipe.class, value = "Patch MasterReceipe") // label for swagger
    @PatchMapping("/masterReceipe/update")
    public ResponseEntity<?> patchMasterReceipe(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken,
                                                @Valid @RequestBody List<MasterReceipe> modifyMasterReceipe) {
        MasterReceipe[] createdMasterReceipe = mfgService.updateMasterReceipe(companyCodeId, plantId, languageId, warehouseId,
                loginUserID, modifyMasterReceipe, authToken);
        return new ResponseEntity<>(createdMasterReceipe, HttpStatus.OK);
    }

    @ApiOperation(response = MasterReceipe.class, value = "Delete MasterReceipe") // label for swagger
    @DeleteMapping("/masterReceipe/{receipeId}")
    public ResponseEntity<?> deleteMasterReceipe(@PathVariable String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                                 @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteMasterReceipe(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // --------------------------------InboundQualityHeader---------------------------------------------------------------
    @ApiOperation(response = InboundQualityHeader.class, value = "Get a InboundQualityHeader") // label for swagger
    @GetMapping("/inboundQualityHeader/{inboundQualityNumber}")
    public ResponseEntity<?> getInboundQualityHeader(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                     @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                     @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String authToken) {
        InboundQualityHeader inboundqualityheader = mfgService.getInboundQualityHeader(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, itemCode, authToken);
        return new ResponseEntity<>(inboundqualityheader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityHeader.class, value = "Search InboundQualityHeader") // label for swagger
    @PostMapping("/inboundQualityHeader/findInboundQualityHeader")
    public InboundQualityHeader[] findInboundQualityHeader(@RequestBody SearchInboundQualityHeader searchInboundQualityHeader, @RequestParam String authToken)
            throws Exception {
        return mfgService.findInboundQualityHeader(searchInboundQualityHeader, authToken);
    }

    @ApiOperation(response = InboundQualityHeader.class, value = "Create InboundQualityHeader") // label for swagger
    @PostMapping("inboundQualityHeader/create")
    public ResponseEntity<?> postInboundQualityHeader(@Valid @RequestBody List<InboundQualityHeader> newInboundQualityHeader, @RequestParam String loginUserID, @RequestParam String authToken) {
        InboundQualityHeader[] createdInboundQualityHeader = mfgService.createInboundQualityHeader(newInboundQualityHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityHeader.class, value = "Patch InboundQualityHeader") // label for swagger
    @PatchMapping("/inboundQualityHeader/update")
    public ResponseEntity<?> patchInboundQualityHeader(@RequestParam String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                       @RequestParam String preInboundNo, @RequestParam String loginUserID,
                                                       @Valid @RequestBody List<InboundQualityHeader> modifyInboundQualityHeader, @RequestParam String authToken) {
        InboundQualityHeader[] createdInboundQualityHeader = mfgService.updateInboundQualityHeader(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo,
                inboundQualityNumber, loginUserID, modifyInboundQualityHeader, authToken);
        return new ResponseEntity<>(createdInboundQualityHeader, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityHeader.class, value = "Delete InboundQualityHeader") // label for swagger
    @DeleteMapping("/inboundQualityHeader/{inboundQualityNumber}")
    public ResponseEntity<?> deleteInboundQualityHeader(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                        @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteInboundQualityHeader(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                preInboundNo, inboundQualityNumber, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------InboundQualityLine---------------------------------------------------------------

    @ApiOperation(response = InboundQualityLine.class, value = "Get a InboundQualityLine") // label for swagger 
    @GetMapping("/inboundQualityLine/{inboundQualityNumber}")
    public ResponseEntity<?> getInboundQualityLine(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                   @RequestParam String preInboundNo, @RequestParam Long lineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        InboundQualityLine inboundqualityline = mfgService.getInboundQualityLine(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, lineNo, itemCode, authToken);
        return new ResponseEntity<>(inboundqualityline, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityLine.class, value = "Get all InboundQualityLines") // label for swagger
    @GetMapping("/inboundQualityLine/v2/{inboundQualityNumber}")
    public ResponseEntity<?> getInboundQualityLine(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                   @RequestParam String preInboundNo, @RequestParam String authToken) {
        InboundQualityLine[] inboundqualityline = mfgService.getInboundQualityLines(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, authToken);
        return new ResponseEntity<>(inboundqualityline, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityLine.class, value = "Search InboundQualityLine") // label for swagger
    @PostMapping("/inboundQualityLine/findInboundQualityLine")
    public InboundQualityLine[] findInboundQualityLine(@RequestBody SearchInboundQualityLine searchInboundQualityLine, @RequestParam String authToken)
            throws Exception {
        return mfgService.findInboundQualityLine(searchInboundQualityLine, authToken);
    }

    @ApiOperation(response = InboundQualityLine.class, value = "Create InboundQualityLine") // label for swagger
    @PostMapping("inboundQualityLine/create")
    public ResponseEntity<?> postInboundQualityLine(@Valid @RequestBody List<InboundQualityLine> newInboundQualityLine, @RequestParam String loginUserID, @RequestParam String authToken) {
        InboundQualityLine[] createdInboundQualityLine = mfgService.createInboundQualityLine(newInboundQualityLine, loginUserID, authToken);
        return new ResponseEntity<>(createdInboundQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityLine.class, value = "Patch InboundQualityLine") // label for swagger
    @PatchMapping("/inboundQualityLine/{inboundQualityNumber}")
    public ResponseEntity<?> patchInboundQualityLine(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                     @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                     @RequestParam String preInboundNo, @RequestParam String itemCode, @RequestParam String loginUserID,
                                                     @Valid @RequestBody List<InboundQualityLine> modifyInboundQualityLine, @RequestParam String authToken) {
        InboundQualityLine[] createdInboundQualityLine = mfgService.updateInboundQualityLine(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, loginUserID, modifyInboundQualityLine, authToken);
        return new ResponseEntity<>(createdInboundQualityLine, HttpStatus.OK);
    }

    @ApiOperation(response = InboundQualityLine.class, value = "Delete InboundQualityLine") // label for swagger
    @DeleteMapping("/inboundQualityLine/{inboundQualityNumber}")
    public ResponseEntity<?> deleteInboundQualityLine(@PathVariable String inboundQualityNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                      @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String refDocNumber,
                                                      @RequestParam String preInboundNo, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteInboundQualityLine(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundQualityNumber, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    // --------------------------------OperationHeader---------------------------------------------------------------
    @ApiOperation(response = OperationHeader.class, value = "Get a OperationHeader") // label for swagger
    @GetMapping("/operationHeader/{productionOrderNo}")
    public ResponseEntity<?> getOperationHeader(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        OperationHeader operationheader = mfgService.getOperationHeader(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(operationheader, HttpStatus.OK);
    }

    @ApiOperation(response = OperationHeader.class, value = "Search OperationHeader") // label for swagger
    @PostMapping("/operationHeader/findOperationHeader")
    public OperationHeader[] findOperationHeader(@RequestBody SearchOperationHeader searchOperationHeader, @RequestParam String authToken)
            throws Exception {
        return mfgService.findOperationHeader(searchOperationHeader, authToken);
    }

    @ApiOperation(response = OperationHeader.class, value = "Create OperationHeader") // label for swagger
    @PostMapping("operationHeader/create")
    public ResponseEntity<?> postOperationHeader(@Valid @RequestBody List<OperationHeader> newOperationHeader, @RequestParam String loginUserID, @RequestParam String authToken) {
        OperationHeader[] createdOperationHeader = mfgService.createOperationHeader(newOperationHeader, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OperationHeader.class, value = "Patch OperationHeader") // label for swagger
    @PatchMapping("/operationHeader/update")
    public ResponseEntity<?> patchOperationHeader(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                                  @Valid @RequestBody List<OperationHeader> modifyOperationHeader, @RequestParam String authToken) {
        OperationHeader[] createdOperationHeader = mfgService.updateOperationHeader(companyCodeId, plantId, languageId, warehouseId, loginUserID, modifyOperationHeader, authToken);
        return new ResponseEntity<>(createdOperationHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OperationHeader.class, value = "Delete OperationHeader") // label for swagger
    @DeleteMapping("/operationHeader/{productionOrderNo}")
    public ResponseEntity<?> deleteOperationHeader(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteOperationHeader(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------ProductionOrder---------------------------------------------------------------
    @ApiOperation(response = ProductionOrderOutput.class, value = "Get a ProductionOrder") // label for swagger
    @GetMapping("/productionOrder/{productionOrderNo}")
    public ResponseEntity<?> getProductionOrder(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        ProductionOrderOutput operationheader = mfgService.getProductionOrder(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(operationheader, HttpStatus.OK);
    }

    @ApiOperation(response = ProductionOrder.class, value = "Get a ProductionOrder") // label for swagger
    @GetMapping("/productionOrder/v2/{productionOrderNo}")
    public ResponseEntity<?> getProductionOrder(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String batchNumber, @RequestParam String authToken) {
        ProductionOrder operationheader = mfgService.getProductionOrder(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, authToken);
        return new ResponseEntity<>(operationheader, HttpStatus.OK);
    }

    @ApiOperation(response = ProductionOrderOutput.class, value = "Search ProductionOrder") // label for swagger
    @PostMapping("/productionOrder/findOperationHeader")
    public ProductionOrderOutput[] findProductionOrder(@RequestBody SearchOperationHeader searchOperationHeader, @RequestParam String authToken)
            throws Exception {
        return mfgService.findProductionOrder(searchOperationHeader, authToken);
    }

    @ApiOperation(response = ProductionOrderOutput.class, value = "Create ProductionOrder") // label for swagger
    @PostMapping("productionOrder/create")
    public ResponseEntity<?> postProductionOrder(@Valid @RequestBody List<ProductionOrder> newProductionOrder, @RequestParam String loginUserID, @RequestParam String authToken) {
        ProductionOrderOutput[] createdOperationHeader = mfgService.createProductionOrder(newProductionOrder, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProductionOrderOutput.class, value = "Create SFG ProductionOrder") // label for swagger
    @PostMapping("productionOrder/sfgCreate")
    public ResponseEntity<?> postSFGProductionOrder(@Valid @RequestBody List<ProductionOrder> newProductionOrder, @RequestParam String loginUserID, @RequestParam String authToken) {
        Boolean createdOperationHeader = mfgService.createSFGProductionOrder(newProductionOrder, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationHeader, HttpStatus.OK);
    }

    @ApiOperation(response = ProductionOrderOutput.class, value = "Update ProductionOrder") // label for swagger
    @PatchMapping("productionOrder/update")
    public ResponseEntity<?> patchProductionOrder(@Valid @RequestBody List<ProductionOrder> newProductionOrder, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        ProductionOrderOutput[] createdOperationHeader = mfgService.updateProductionOrder(newProductionOrder, companyCodeId, plantId, languageId, warehouseId, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationHeader, HttpStatus.OK);
    }

    @ApiOperation(response = OperationHeader.class, value = "Delete ProductionOrder") // label for swagger
    @DeleteMapping("/productionOrder/{productionOrderNo}")
    public ResponseEntity<?> deleteProductionOrder(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                   @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteProductionOrder(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //====================================================================================================================================

    //Create OrderConfirmation
    @ApiOperation(response = OrderConfirmation.class, value = "Create OrderConfirmation")
    @PostMapping("productionOrder/orderConfirmationConfirm")
    public ResponseEntity<?> postOrderConfirmation(@Valid @RequestBody OrderConfirmation orderConfirmation,
                                                   @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.createOrderConfirmation(loginUserID, orderConfirmation, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //Get OrderConfirmation
    @ApiOperation(response = OrderConfirmation.class, value = "Get OrderConfirmation")
    @GetMapping("productionOrder/orderConfirmationGet")
    public ResponseEntity<?> getOrderConfirmation(@RequestParam String companyCodeId, @RequestParam String plantId,
                                                  @RequestParam String languageId, @RequestParam String warehouseId,
                                                  @RequestParam String productionOrderNo, @RequestParam String batchNumber,
                                                  @RequestParam String loginUserID, @RequestParam String authToken) {
        OrderConfirmation orderConfirmation = mfgService.getOrderConfirmation(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
        return new ResponseEntity<>(orderConfirmation, HttpStatus.OK);
    }

    // --------------------------------OperationLine---------------------------------------------------------------
    @ApiOperation(response = OperationLine.class, value = "Get a OperationLine") // label for swagger
    @GetMapping("/operationLine/{productionOrderNo}")
    public ResponseEntity<?> getOperationLine(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId,
                                              @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        OperationLine operationline = mfgService.getOperationLine(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(operationline, HttpStatus.OK);
    }

    @ApiOperation(response = OperationLine.class, value = "Get a OperationLines") // label for swagger
    @GetMapping("/operationLine/v2/{productionOrderNo}")
    public ResponseEntity<?> getOperationLine(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        OperationLine[] operationline = mfgService.getOperationLines(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(operationline, HttpStatus.OK);
    }

    @ApiOperation(response = OperationLine.class, value = "Search OperationLine") // label for swagger
    @PostMapping("/operationLine/findOperationLine")
    public OperationLine[] findOperationLine(@RequestBody SearchOperationLine searchOperationLine, @RequestParam String authToken)
            throws Exception {
        return mfgService.findOperationLine(searchOperationLine, authToken);
    }

    @ApiOperation(response = OperationLine.class, value = "Create OperationLine") // label for swagger
    @PostMapping("operationLine/create")
    public ResponseEntity<?> postOperationLine(@Valid @RequestBody List<OperationLine> newOperationLine, @RequestParam String loginUserID, @RequestParam String authToken) {
        OperationLine[] createdOperationLine = mfgService.createOperationLine(newOperationLine, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationLine, HttpStatus.OK);
    }

    @ApiOperation(response = OperationLine.class, value = "Patch OperationLine") // label for swagger
    @PatchMapping("/operationLine/{productionOrderNo}")
    public ResponseEntity<?> patchOperationLine(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                                @Valid @RequestBody List<OperationLine> modifyOperationLine, @RequestParam String authToken) {
        OperationLine[] createdOperationLine = mfgService.updateOperationLine(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, modifyOperationLine, authToken);
        return new ResponseEntity<>(createdOperationLine, HttpStatus.OK);
    }

    @ApiOperation(response = OperationLine.class, value = "Delete OperationLine") // label for swagger
    @DeleteMapping("/operationLine/{productionOrderNo}")
    public ResponseEntity<?> deleteOperationLine(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteOperationLine(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------OperationConsumption---------------------------------------------------------------
    @ApiOperation(response = OperationConsumption.class, value = "Get a OperationConsumption") // label for swagger 
    @GetMapping("/operationConsumption/{productionOrderNo}")
    public ResponseEntity<?> getOperationConsumption(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                     @RequestParam String languageId, @RequestParam String warehouseId,
                                                     @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        OperationConsumption operationconsumption = mfgService.getOperationConsumption(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(operationconsumption, HttpStatus.OK);
    }

    @ApiOperation(response = OperationConsumption.class, value = "Get a OperationConsumptions") // label for swagger 
    @GetMapping("/operationConsumption/v2/{productionOrderNo}")
    public ResponseEntity<?> getOperationConsumption(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                     @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        OperationConsumption[] operationconsumption = mfgService.getOperationConsumptions(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(operationconsumption, HttpStatus.OK);
    }

    @ApiOperation(response = OperationConsumption.class, value = "Search OperationConsumption") // label for swagger
    @PostMapping("/operationConsumption/findOperationConsumption")
    public OperationConsumption[] findOperationConsumption(@RequestBody SearchOperationConsumption searchOperationConsumption, @RequestParam String authToken)
            throws Exception {
        return mfgService.findOperationConsumption(searchOperationConsumption, authToken);
    }

    @ApiOperation(response = OperationConsumption.class, value = "Create OperationConsumption") // label for swagger
    @PostMapping("operationConsumption/create")
    public ResponseEntity<?> postOperationConsumption(@Valid @RequestBody List<OperationConsumption> newOperationConsumption, @RequestParam String loginUserID, @RequestParam String authToken) {
        OperationConsumption[] createdOperationConsumption = mfgService.createOperationConsumption(newOperationConsumption, loginUserID, authToken);
        return new ResponseEntity<>(createdOperationConsumption, HttpStatus.OK);
    }

    @ApiOperation(response = OperationConsumption.class, value = "Patch OperationConsumption") // label for swagger
    @PatchMapping("/operationConsumption/{productionOrderNo}")
    public ResponseEntity<?> patchOperationConsumption(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                       @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                                       @Valid @RequestBody List<OperationConsumption> modifyOperationConsumption, @RequestParam String authToken) {
        OperationConsumption[] createdOperationConsumption = mfgService.updateOperationConsumption(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyOperationConsumption, authToken);
        return new ResponseEntity<>(createdOperationConsumption, HttpStatus.OK);
    }

    @ApiOperation(response = OperationConsumption.class, value = "Delete OperationConsumption") // label for swagger
    @DeleteMapping("/operationConsumption/{productionOrderNo}")
    public ResponseEntity<?> deleteOperationConsumption(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteOperationConsumption(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------Sorting---------------------------------------------------------------
    @ApiOperation(response = Sorting.class, value = "Get a Sorting") // label for swagger
    @GetMapping("/sorting/{productionOrderNo}")
    public ResponseEntity<?> getSorting(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId,
                                        @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                        @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        Sorting sorting = mfgService.getSorting(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(sorting, HttpStatus.OK);
    }


    @ApiOperation(response = Sorting.class, value = "Get a Sorting") // label for swagger
    @GetMapping("/sorting/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkSorting(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        Sorting[] sorting = mfgService.getBulkSorting(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(sorting, HttpStatus.OK);
    }

    @ApiOperation(response = Sorting.class, value = "Search Sorting") // label for swagger
    @PostMapping("/sorting/findSorting")
    public Sorting[] findSorting(@RequestBody SearchSorting searchSorting, @RequestParam String authToken)
            throws Exception {
        return mfgService.findSorting(searchSorting, authToken);
    }

    @ApiOperation(response = Sorting.class, value = "Create Sorting") // label for swagger
    @PostMapping("sorting/create")
    public ResponseEntity<?> postSorting(@Valid @RequestBody List<Sorting> newSorting, @RequestParam String loginUserID, @RequestParam String authToken) {
        Sorting[] createdSorting = mfgService.createSorting(newSorting, loginUserID, authToken);
        return new ResponseEntity<>(createdSorting, HttpStatus.OK);
    }

    @ApiOperation(response = Sorting.class, value = "Patch Sorting") // label for swagger
    @PatchMapping("/sorting/{productionOrderNo}")
    public ResponseEntity<?> patchSorting(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                          @Valid @RequestBody List<Sorting> modifySorting, @RequestParam String authToken) {
        Sorting[] createdSorting = mfgService.updateSorting(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifySorting, authToken);
        return new ResponseEntity<>(createdSorting, HttpStatus.OK);
    }
    @ApiOperation(response = Sorting.class, value = "Delete Sorting") // label for swagger
    @DeleteMapping("/sorting/{productionOrderNo}")
    public ResponseEntity<?> deleteSorting(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteSorting(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------------------------------------Soaking-------------------------------------------------------------
    @ApiOperation(response = Soaking.class, value = "Get a Soaking") // label for swagger
    @GetMapping("/soaking/{productionOrderNo}")
    public ResponseEntity<?> getSoaking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId,
                                        @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                        @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        Soaking soaking = mfgService.getSoaking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(soaking, HttpStatus.OK);
    }

    @ApiOperation(response = Soaking.class, value = "Get a Soaking") // label for swagger
    @GetMapping("/soaking/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkSoaking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        Soaking[] soaking = mfgService.getBulkSoaking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(soaking, HttpStatus.OK);
    }

    @ApiOperation(response = Soaking.class, value = "Search Soaking") // label for swagger
    @PostMapping("/soaking/findSoaking")
    public Soaking[] findSoaking(@RequestBody SearchSoaking searchSoaking, @RequestParam String authToken)
            throws Exception {
        return mfgService.findSoaking(searchSoaking, authToken);
    }

    @ApiOperation(response = Soaking.class, value = "Create Soaking") // label for swagger
    @PostMapping("soaking/create")
    public ResponseEntity<?> postSoaking(@Valid @RequestBody List<Soaking> newSoaking, @RequestParam String loginUserID, @RequestParam String authToken) {
        Soaking[] createdSoaking = mfgService.createSoaking(newSoaking, loginUserID, authToken);
        return new ResponseEntity<>(createdSoaking, HttpStatus.OK);
    }

    @ApiOperation(response = Soaking.class, value = "Patch Soaking") // label for swagger
    @PatchMapping("/soaking/{productionOrderNo}")
    public ResponseEntity<?> patchSoaking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                          @Valid @RequestBody List<Soaking> modifySoaking, @RequestParam String authToken) {
        Soaking[] createdSoaking = mfgService.updateSoaking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifySoaking, authToken);
        return new ResponseEntity<>(createdSoaking, HttpStatus.OK);
    }
    @ApiOperation(response = Soaking.class, value = "Delete Soaking") // label for swagger
    @DeleteMapping("/soaking/{productionOrderNo}")
    public ResponseEntity<?> deleteSoaking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteSoaking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------------------------------------Peeling-------------------------------------------------------------
    @ApiOperation(response = Peeling.class, value = "Get a Peeling") // label for swagger
    @GetMapping("/peeling/{productionOrderNo}")
    public ResponseEntity<?> getPeeling(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId,
                                        @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                        @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        Peeling peeling = mfgService.getPeeling(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(peeling, HttpStatus.OK);
    }


    @ApiOperation(response = Peeling.class, value = "Get a Peeling") // label for swagger
    @GetMapping("/peeling/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkPeeling(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        Peeling[] peeling = mfgService.getBulkPeeling(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(peeling, HttpStatus.OK);
    }

    @ApiOperation(response = Peeling.class, value = "Search Peeling") // label for swagger
    @PostMapping("/peeling/findPeeling")
    public Peeling[] findPeeling(@RequestBody SearchPeeling searchPeeling, @RequestParam String authToken)
            throws Exception {
        return mfgService.findPeeling(searchPeeling, authToken);
    }

    @ApiOperation(response = Peeling.class, value = "Create Peeling") // label for swagger
    @PostMapping("peeling/create")
    public ResponseEntity<?> postPeeling(@Valid @RequestBody List<Peeling> newPeeling, @RequestParam String loginUserID, @RequestParam String authToken) {
        Peeling[] createdPeeling = mfgService.createPeeling(newPeeling, loginUserID, authToken);
        return new ResponseEntity<>(createdPeeling, HttpStatus.OK);
    }

    @ApiOperation(response = Peeling.class, value = "Patch Peeling") // label for swagger
    @PatchMapping("/peeling/{productionOrderNo}")
    public ResponseEntity<?> patchPeeling(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                          @Valid @RequestBody List<Peeling> modifyPeeling, @RequestParam String authToken) {
        Peeling[] createdPeeling = mfgService.updatePeeling(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyPeeling, authToken);
        return new ResponseEntity<>(createdPeeling, HttpStatus.OK);
    }

    @ApiOperation(response = Peeling.class, value = "Delete Peeling") // label for swagger
    @DeleteMapping("/peeling/{productionOrderNo}")
    public ResponseEntity<?> deletePeeling(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deletePeeling(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------------------------------------GingerPaste-------------------------------------------------------------
    @ApiOperation(response = GingerPaste.class, value = "Get a GingerPaste") // label for swagger
    @GetMapping("/gingerPaste/{productionOrderNo}")
    public ResponseEntity<?> getGingerPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId,
                                            @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                            @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        GingerPaste gingerPaste = mfgService.getGingerPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(gingerPaste, HttpStatus.OK);
    }


    @ApiOperation(response = GingerPaste.class, value = "Get a GingerPaste") // label for swagger
    @GetMapping("/gingerPaste/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkGingerPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        GingerPaste[] gingerPaste = mfgService.getBulkGingerPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(gingerPaste, HttpStatus.OK);
    }

    @ApiOperation(response = GingerPaste.class, value = "Search GingerPaste") // label for swagger
    @PostMapping("/gingerPaste/findGingerPaste")
    public GingerPaste[] findGingerPaste(@RequestBody SearchGingerPaste searchGingerPaste, @RequestParam String authToken)
            throws Exception {
        return mfgService.findGingerPaste(searchGingerPaste, authToken);
    }

    @ApiOperation(response = GingerPaste.class, value = "Create GingerPaste") // label for swagger
    @PostMapping("gingerPaste/create")
    public ResponseEntity<?> postGingerPaste(@Valid @RequestBody List<GingerPaste> newGingerPaste, @RequestParam String loginUserID, @RequestParam String authToken) {
        GingerPaste[] createdGingerPaste = mfgService.createGingerPaste(newGingerPaste, loginUserID, authToken);
        return new ResponseEntity<>(createdGingerPaste, HttpStatus.OK);
    }

    @ApiOperation(response = GingerPaste.class, value = "Patch GingerPaste") // label for swagger
    @PatchMapping("/gingerPaste/{productionOrderNo}")
    public ResponseEntity<?> patchGingerPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                              @Valid @RequestBody List<GingerPaste> modifyGingerPaste, @RequestParam String authToken) {
        GingerPaste[] createdGingerPaste = mfgService.updateGingerPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyGingerPaste, authToken);
        return new ResponseEntity<>(createdGingerPaste, HttpStatus.OK);
    }

    @ApiOperation(response = GingerPaste.class, value = "Delete GingerPaste") // label for swagger
    @DeleteMapping("/gingerPaste/{productionOrderNo}")
    public ResponseEntity<?> deleteGingerPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteGingerPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    //--------------------------------------------------Paste-------------------------------------------------------------
    @ApiOperation(response = Paste.class, value = "Get a Paste") // label for swagger
    @GetMapping("/paste/{productionOrderNo}")
    public ResponseEntity<?> getPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                      @RequestParam String languageId, @RequestParam String warehouseId,
                                      @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                      @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        Paste paste = mfgService.getPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(paste, HttpStatus.OK);
    }


    @ApiOperation(response = Paste.class, value = "Get a Paste") // label for swagger
    @GetMapping("/paste/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        Paste[] paste = mfgService.getBulkPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(paste, HttpStatus.OK);
    }

    @ApiOperation(response = Paste.class, value = "Search Paste") // label for swagger
    @PostMapping("/paste/findPaste")
    public Paste[] findPaste(@RequestBody SearchPaste searchPaste, @RequestParam String authToken)
            throws Exception {
        return mfgService.findPaste(searchPaste, authToken);
    }

    @ApiOperation(response = Paste.class, value = "Create Paste") // label for swagger
    @PostMapping("paste/create")
    public ResponseEntity<?> postPaste(@Valid @RequestBody List<Paste> newPaste, @RequestParam String loginUserID, @RequestParam String authToken) {
        Paste[] createdPaste = mfgService.createPaste(newPaste, loginUserID, authToken);
        return new ResponseEntity<>(createdPaste, HttpStatus.OK);
    }

    @ApiOperation(response = Paste.class, value = "Patch Paste") // label for swagger
    @PatchMapping("/paste/{productionOrderNo}")
    public ResponseEntity<?> patchPaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                        @Valid @RequestBody List<Paste> modifyPaste, @RequestParam String authToken) {
        Paste[] createdPaste = mfgService.updatePaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyPaste, authToken);
        return new ResponseEntity<>(createdPaste, HttpStatus.OK);
    }

    @ApiOperation(response = Paste.class, value = "Delete Paste") // label for swagger
    @DeleteMapping("/paste/{productionOrderNo}")
    public ResponseEntity<?> deletePaste(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deletePaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------------------------------------FgReceiving-------------------------------------------------------------
    @ApiOperation(response = FgReceiving.class, value = "Get a FgReceiving") // label for swagger
    @GetMapping("/fgReceiving/{productionOrderNo}")
    public ResponseEntity<?> getFgReceiving(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId,
                                            @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                            @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        FgReceiving fgReceiving = mfgService.getFgReceiving(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(fgReceiving, HttpStatus.OK);
    }


    @ApiOperation(response = FgReceiving.class, value = "Get a FgReceiving") // label for swagger
    @GetMapping("/fgReceiving/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkFgReceiving(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        FgReceiving[] fgReceivings = mfgService.getBulkFgReceiving(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(fgReceivings, HttpStatus.OK);
    }

    @ApiOperation(response = FgReceiving.class, value = "Search FgReceiving") // label for swagger
    @PostMapping("/fgReceiving/findFgReceiving")
    public FgReceiving[] findFgReceiving(@RequestBody SearchFgReceiving searchFgReceiving, @RequestParam String authToken)
            throws Exception {
        return mfgService.findFgReceiving(searchFgReceiving, authToken);
    }

    @ApiOperation(response = FgReceiving.class, value = "Create FgReceiving") // label for swagger
    @PostMapping("fgReceiving/create")
    public ResponseEntity<?> postFgReceiving(@Valid @RequestBody List<FgReceiving> newFgReceiving, @RequestParam String loginUserID, @RequestParam String authToken) {
        FgReceiving[] createdFgReceiving = mfgService.createFgReceiving(newFgReceiving, loginUserID, authToken);
        return new ResponseEntity<>(createdFgReceiving, HttpStatus.OK);
    }

    @ApiOperation(response = FgReceiving.class, value = "Patch FgReceiving") // label for swagger
    @PatchMapping("/fgReceiving/{productionOrderNo}")
    public ResponseEntity<?> patchFgReceiving(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                              @Valid @RequestBody List<FgReceiving> modifyFgReceiving, @RequestParam String authToken) {
        FgReceiving[] createdFgReceiving = mfgService.updateFgReceiving(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyFgReceiving, authToken);
        return new ResponseEntity<>(createdFgReceiving, HttpStatus.OK);
    }

    @ApiOperation(response = FgReceiving.class, value = "Delete FgReceiving") // label for swagger
    @DeleteMapping("/fgReceiving/{productionOrderNo}")
    public ResponseEntity<?> deleteFgReceiving(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteFgReceiving(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //--------------------------------------------------FgPacking-------------------------------------------------------------
    @ApiOperation(response = FgPacking.class, value = "Get a FgPacking") // label for swagger
    @GetMapping("/fgPacking/{productionOrderNo}")
    public ResponseEntity<?> getFgPacking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId,
                                          @RequestParam Long productionOrderLineNo, @RequestParam String receipeId,
                                          @RequestParam String operationNumber, @RequestParam String itemCode, @RequestParam String authToken) {
        FgPacking fgPacking = mfgService.getFgPacking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, productionOrderLineNo, receipeId, operationNumber, itemCode, authToken);
        return new ResponseEntity<>(fgPacking, HttpStatus.OK);
    }


    @ApiOperation(response = FgPacking.class, value = "Get a FgPacking") // label for swagger
    @GetMapping("/fgPacking/v2/{productionOrderNo}")
    public ResponseEntity<?> getBulkFgPacking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        FgPacking[] fgPacking = mfgService.getBulkFgPacking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(fgPacking, HttpStatus.OK);
    }

    @ApiOperation(response = FgPacking.class, value = "Search FgPacking") // label for swagger
    @PostMapping("/fgPacking/findFgPacking")
    public FgPacking[] findFgPacking(@RequestBody SearchFgPacking searchFgPacking, @RequestParam String authToken)
            throws Exception {
        return mfgService.findFgPacking(searchFgPacking, authToken);
    }

    @ApiOperation(response = FgPacking.class, value = "Create FgPacking") // label for swagger
    @PostMapping("fgPacking/create")
    public ResponseEntity<?> postFgPacking(@Valid @RequestBody List<FgPacking> newFgPacking, @RequestParam String loginUserID, @RequestParam String authToken) {
        FgPacking[] createdFgPacking = mfgService.createFgPacking(newFgPacking, loginUserID, authToken);
        return new ResponseEntity<>(createdFgPacking, HttpStatus.OK);
    }

    @ApiOperation(response = FgPacking.class, value = "Patch FgPacking") // label for swagger
    @PatchMapping("/fgPacking/{productionOrderNo}")
    public ResponseEntity<?> patchFgPacking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID,
                                            @Valid @RequestBody List<FgPacking> modifyFgPacking, @RequestParam String authToken) {
        FgPacking[] createdFgPacking = mfgService.updateFgPacking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo,
                loginUserID, modifyFgPacking, authToken);
        return new ResponseEntity<>(createdFgPacking, HttpStatus.OK);
    }

    @ApiOperation(response = FgPacking.class, value = "Delete FgPacking") // label for swagger
    @DeleteMapping("/fgPacking/{productionOrderNo}")
    public ResponseEntity<?> deleteFgPacking(@PathVariable String productionOrderNo, @RequestParam String companyCodeId, @RequestParam String plantId,
                                             @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteFgPacking(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------Powder---------------------------------------------------------------
    @ApiOperation(response = Powder.class, value = "Get a Powder") // label for swagger
    @GetMapping("/powder/")
    public ResponseEntity<?> getPowder(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                       @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                       @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        Powder powder = mfgService.getPowder(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(powder, HttpStatus.OK);
    }

    @ApiOperation(response = Powder.class, value = "Search Powder") // label for swagger
    @PostMapping("/powder/")
    public Powder[] findPowder(@RequestBody SearchPowder searchPowder, @RequestParam String authToken)
            throws Exception {
        return mfgService.findPowder(searchPowder, authToken);
    }

    @ApiOperation(response = Powder.class, value = "Create Powder") // label for swagger
    @PostMapping("powder/create")
    public ResponseEntity<?> postPowder(@Valid @RequestBody List<Powder> newPowder, @RequestParam String loginUserID, @RequestParam String authToken) {
        Powder[] createdPowder = mfgService.createPowder(newPowder, loginUserID, authToken);
        return new ResponseEntity<>(createdPowder, HttpStatus.OK);
    }

    @ApiOperation(response = Powder.class, value = "Patch Powder") // label for swagger
    @PatchMapping("/powder/update")
    public ResponseEntity<?> patchPowder(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                         @Valid @RequestBody List<Powder> modifyPowder) {
        Powder[] updatedPowder = mfgService.updatePowder(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifyPowder, authToken);
        return new ResponseEntity<>(updatedPowder, HttpStatus.OK);
    }

    @ApiOperation(response = Powder.class, value = "Delete Powder") // label for swagger
    @DeleteMapping("/powder/")
    public ResponseEntity<?> deletePowder(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                          @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deletePowder(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------DiceSliceChop---------------------------------------------------------------
    @ApiOperation(response = DiceSliceChop.class, value = "Get a DiceSliceChop") // label for swagger
    @GetMapping("/diceSliceChop/")
    public ResponseEntity<?> getDiceSliceChop(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                              @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        DiceSliceChop diceSliceChop = mfgService.getDiceSliceChop(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(diceSliceChop, HttpStatus.OK);
    }

    @ApiOperation(response = DiceSliceChop.class, value = "Search DiceSliceChop") // label for swagger
    @PostMapping("/diceSliceChop/")
    public DiceSliceChop[] findDiceSliceChop(@RequestBody SearchDiceSliceChop searchDiceSliceChop, @RequestParam String authToken)
            throws Exception {
        return mfgService.findDiceSliceChop(searchDiceSliceChop, authToken);
    }

    @ApiOperation(response = DiceSliceChop.class, value = "Create DiceSliceChop") // label for swagger
    @PostMapping("diceSliceChop/create")
    public ResponseEntity<?> postDiceSliceChop(@Valid @RequestBody List<DiceSliceChop> newDiceSliceChop, @RequestParam String loginUserID, @RequestParam String authToken) {
        DiceSliceChop[] createdDiceSliceChop = mfgService.createDiceSliceChop(newDiceSliceChop, loginUserID, authToken);
        return new ResponseEntity<>(createdDiceSliceChop, HttpStatus.OK);
    }

    @ApiOperation(response = DiceSliceChop.class, value = "Patch DiceSliceChop") // label for swagger
    @PatchMapping("/diceSliceChop/update")
    public ResponseEntity<?> patchDiceSliceChop(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                                @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                                @Valid @RequestBody List<DiceSliceChop> modifyDiceSliceChop) {
        DiceSliceChop[] updateDiceSliceChop = mfgService.updateDiceSliceChop(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifyDiceSliceChop, authToken);
        return new ResponseEntity<>(updateDiceSliceChop, HttpStatus.OK);
    }

    @ApiOperation(response = DiceSliceChop.class, value = "Delete DiceSliceChop") // label for swagger
    @DeleteMapping("/diceSliceChop/")
    public ResponseEntity<?> deleteDiceSliceChop(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                                 @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                                 @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteDiceSliceChop(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------SFGQI---------------------------------------------------------------
    @ApiOperation(response = SFGQI.class, value = "Get a SFGQI") // label for swagger
    @GetMapping("/sfgqi/")
    public ResponseEntity<?> getSFGQI(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                      @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                      @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        SFGQI sfgqi = mfgService.getSFGQI(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(sfgqi, HttpStatus.OK);
    }

    @ApiOperation(response = SFGQI.class, value = "Search SFGQI") // label for swagger
    @PostMapping("/sfgqi/")
    public SFGQI[] findSFGQI(@RequestBody SearchSFGQI searchSFGQI, @RequestParam String authToken)
            throws Exception {
        return mfgService.findSFGQI(searchSFGQI, authToken);
    }

    @ApiOperation(response = SFGQI.class, value = "Create SFGQI") // label for swagger
    @PostMapping("sfgqi/create")
    public ResponseEntity<?> postSFGQI(@Valid @RequestBody List<SFGQI> newSFGQI, @RequestParam String loginUserID, @RequestParam String authToken) {
        SFGQI[] createdSFGQI = mfgService.createSFGQI(newSFGQI, loginUserID, authToken);
        return new ResponseEntity<>(createdSFGQI, HttpStatus.OK);
    }

    @ApiOperation(response = SFGQI.class, value = "Patch SFGQI") // label for swagger
    @PatchMapping("/sfgqi/update")
    public ResponseEntity<?> patchSFGQi(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                        @Valid @RequestBody List<SFGQI> modifySFGQI) {
        SFGQI[] updatedSFGQI = mfgService.updateSFGQI(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifySFGQI, authToken);
        return new ResponseEntity<>(updatedSFGQI, HttpStatus.OK);
    }

    @ApiOperation(response = SFGQI.class, value = "Delete SFGQI") // label for swagger
    @DeleteMapping("/sfgqi/")
    public ResponseEntity<?> deleteSFGQI(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                         @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                         @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteSFGQI(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------Cooking---------------------------------------------------------------
    @ApiOperation(response = Cooking.class, value = "Get a Cooking") // label for swagger
    @GetMapping("/cooking/")
    public ResponseEntity<?> getCooking(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                        @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        Cooking cooking = mfgService.getCooking(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(cooking, HttpStatus.OK);
    }

    @ApiOperation(response = Cooking.class, value = "Search Cooking") // label for swagger
    @PostMapping("/cooking/")
    public Cooking[] findCooking(@RequestBody SearchCooking searchCooking, @RequestParam String authToken)
            throws Exception {
        return mfgService.findCooking(searchCooking, authToken);
    }

    @ApiOperation(response = Cooking.class, value = "Create Cooking") // label for swagger
    @PostMapping("cooking/create")
    public ResponseEntity<?> postCooking(@Valid @RequestBody List<Cooking> newCooking, @RequestParam String loginUserID, @RequestParam String authToken) {
        Cooking[] createdCooking = mfgService.createCooking(newCooking, loginUserID, authToken);
        return new ResponseEntity<>(createdCooking, HttpStatus.OK);
    }

    @ApiOperation(response = Cooking.class, value = "Patch Cooking") // label for swagger
    @PatchMapping("/cooking/update")
    public ResponseEntity<?> patchCooking(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                          @Valid @RequestBody List<Cooking> modifyCooking) {
        Cooking[] updatedCooking = mfgService.updateCooking(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifyCooking, authToken);
        return new ResponseEntity<>(updatedCooking, HttpStatus.OK);
    }

    @ApiOperation(response = Cooking.class, value = "Delete Cooking") // label for swagger
    @DeleteMapping("/cooking/")
    public ResponseEntity<?> deleteCooking(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                           @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteCooking(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------PackingQc---------------------------------------------------------------
    @ApiOperation(response = PackingQc.class, value = "Get a PackingQc") // label for swagger
    @GetMapping("/packingQc/")
    public ResponseEntity<?> getPackingQc(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                          @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                          @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        PackingQc packingQc = mfgService.getPackingQc(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(packingQc, HttpStatus.OK);
    }

    @ApiOperation(response = PackingQc.class, value = "Search PackingQc") // label for swagger
    @PostMapping("/packingQc/")
    public PackingQc[] findPackingQc(@RequestBody SearchPackingQc searchPackingQc, @RequestParam String authToken)
            throws Exception {
        return mfgService.findPackingQc(searchPackingQc, authToken);
    }

    @ApiOperation(response = PackingQc.class, value = "Create PackingQc") // label for swagger
    @PostMapping("packingQc/create")
    public ResponseEntity<?> postPackingQc(@Valid @RequestBody List<PackingQc> newPackingQc, @RequestParam String loginUserID, @RequestParam String authToken) {
        PackingQc[] createdPackingQc = mfgService.createPackingQc(newPackingQc, loginUserID, authToken);
        return new ResponseEntity<>(createdPackingQc, HttpStatus.OK);
    }

    @ApiOperation(response = PackingQc.class, value = "Patch PackingQc") // label for swagger
    @PatchMapping("/packingQc/update")
    public ResponseEntity<?> patchPackingQc(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                            @Valid @RequestBody List<PackingQc> modifyPackingQc) {
        PackingQc[] updatedPackingQc = mfgService.updatePackingQc(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifyPackingQc, authToken);
        return new ResponseEntity<>(updatedPackingQc, HttpStatus.OK);
    }

    @ApiOperation(response = PackingQc.class, value = "Delete PackingQc") // label for swagger
    @DeleteMapping("/packingQc/")
    public ResponseEntity<?> deletePackingQc(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                             @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                             @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deletePackingQc(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------FgDelivery---------------------------------------------------------------
    @ApiOperation(response = FgDelivery.class, value = "Get a FgDelivery") // label for swagger
    @GetMapping("/fgDelivery/")
    public ResponseEntity<?> getFgDelivery(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                           @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        FgDelivery fgDelivery = mfgService.getFgDelivery(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, authToken);
        return new ResponseEntity<>(fgDelivery, HttpStatus.OK);
    }

    @ApiOperation(response = FgDelivery.class, value = "Search FgDelivery") // label for swagger
    @PostMapping("/fgDelivery/")
    public FgDelivery[] findFgDelivery(@RequestBody SearchFgDelivery searchFgDelivery, @RequestParam String authToken)
            throws Exception {
        return mfgService.findFgDelivery(searchFgDelivery, authToken);
    }

    @ApiOperation(response = FgDelivery.class, value = "Create FgDelivery") // label for swagger
    @PostMapping("fgDelivery/create")
    public ResponseEntity<?> postFgDelivery(@Valid @RequestBody List<FgDelivery> newFgDelivery, @RequestParam String loginUserID, @RequestParam String authToken) {
        FgDelivery[] createdFgDelivery = mfgService.createFgDelivery(newFgDelivery, loginUserID, authToken);
        return new ResponseEntity<>(createdFgDelivery, HttpStatus.OK);
    }

    @ApiOperation(response = FgDelivery.class, value = "Patch FgDelivery") // label for swagger
    @PatchMapping("/fgDelivery/update")
    public ResponseEntity<?> patchFgDelivery(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                             @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String receiptId, @RequestParam String operationNumber, @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken,
                                             @Valid @RequestBody List<FgDelivery> modifyFgDelivery) {
        FgDelivery[] updatedFgDelivery = mfgService.updateFgDelivery(companyCodeId, plantId, languageId, warehouseId, operationNumber, receiptId, productionOrderNo, productionOrderLineNo, itemCode,
                loginUserID, modifyFgDelivery, authToken);
        return new ResponseEntity<>(updatedFgDelivery, HttpStatus.OK);
    }

    @ApiOperation(response = FgDelivery.class, value = "Delete FgDelivery") // label for swagger
    @DeleteMapping("/fgDelivery/")
    public ResponseEntity<?> deleteFgDelivery(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                              @RequestParam String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteFgDelivery(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo, itemCode, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------OrderConfirmation---------------------------------------------------------------

    //Update BlackDal
//    @ApiOperation(response = BlackDal.class, value = "Patch BlackDal")
//    @PatchMapping("/productionOrder/blackDalConfirm")
//    public ResponseEntity<?> patchBlackDal(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
//                                           @RequestParam String warehouseId, @RequestParam String productionOrderNo, @RequestParam String loginUserID,
//                                           @Valid @RequestBody BlackDal blackDal, @RequestParam String authToken) {
//        mfgService.updateBlackDal(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, blackDal, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Create BlackDal
//    @ApiOperation(response = BlackDal.class, value = "Patch BlackDal")
//    @PostMapping("productionOrder/blackDalConfirm")
//    public ResponseEntity<?> postBlackDal(@Valid @RequestBody BlackDal blackDal, @RequestParam String loginUserID,
//                                          @RequestParam String authToken) {
//        mfgService.createBlackDal(loginUserID, blackDal, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get BlackDal
//    @ApiOperation(response = BlackDal.class, value = "Get BlackDal")
//    @GetMapping("productionOrder/blackDalGet")
//    public ResponseEntity<?> getBlackDal(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                         @RequestParam String languageId, @RequestParam String warehouseId,
//                                         @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                         @RequestParam String loginUserID, @RequestParam String authToken) {
//        BlackDal blackDal = mfgService.getBlackDal(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(blackDal, HttpStatus.OK);
//
//    }
//
//    //---------------------------------------------------------------------------
//
//    // Create ChanaDal
//    @ApiOperation(response = ChanaDal.class, value = "Patch ChanaDal")
//    @PostMapping("productionOrder/chanaDalConfirm")
//    public ResponseEntity<?> postChanaDal(@Valid @RequestBody ChanaDal chanaDal, @RequestParam String loginUserID, @RequestParam String authToken) {
//
//        mfgService.createChanaDal(loginUserID, chanaDal, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get ChanaDal
//    @ApiOperation(response = ChanaDal.class, value = "Get ChanaDal")
//    @GetMapping("productionOrder/chanaDalGet")
//    public ResponseEntity<?> getChanaDal(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                         @RequestParam String languageId, @RequestParam String warehouseId,
//                                         @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                         @RequestParam String loginUserID, @RequestParam String authToken) {
//        ChanaDal chanaDal = mfgService.getChanaDal(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(chanaDal, HttpStatus.OK);
//    }
//
//    //----------------------------------------------------------------------------
//
//    // Create ChitraRajma
//    @ApiOperation(response = ChitraRajma.class, value = "Patch ChitraRajma")
//    @PostMapping("productionOrder/chitraRajmaConfirm")
//    public ResponseEntity<?> postChitraRajma(@Valid @RequestBody ChitraRajma chitraRajma,
//                                             @RequestParam String loginUserID,
//                                             @RequestParam String authToken) {
//
//        mfgService.createChitraRajma(loginUserID, chitraRajma, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get ChitraRajma
//    @ApiOperation(response = ChitraRajma.class, value = "Get ChitraRajma")
//    @GetMapping("productionOrder/chitraRajmaGet")
//    public ResponseEntity<?> getChitraRajma(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                         @RequestParam String languageId, @RequestParam String warehouseId,
//                                         @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                         @RequestParam String loginUserID, @RequestParam String authToken) {
//        ChitraRajma chitraRajma = mfgService.getChitraRajma(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(chitraRajma, HttpStatus.OK);
//    }
//
//    //------------------------------------------------------------------------------------
//
//    //Create TomatoPuree
//    @ApiOperation(response = TomatoPuree.class, value = "Create TomatoPuree")
//    @PostMapping("productionOrder/tomatoPureeConfirm")
//    public ResponseEntity<?> postTomatoPuree(@Valid @RequestBody TomatoPuree tomatoPuree,
//                                             @RequestParam String loginUserID,
//                                             @RequestParam String authToken) {
//        mfgService.createTomatoPuree(loginUserID, tomatoPuree, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get TomatoPuree
//    @ApiOperation(response = TomatoPuree.class, value = "Get TomatoPuree")
//    @GetMapping("productionOrder/tomatoPureeGet")
//    public ResponseEntity<?> getTomatoPuree(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                            @RequestParam String languageId, @RequestParam String warehouseId,
//                                            @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                            @RequestParam String loginUserID, @RequestParam String authToken) {
//        TomatoPuree tomatoPuree = mfgService.getTomatoPuree(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(tomatoPuree, HttpStatus.OK);
//    }
//
//    //----------------------------------------------------------------------------------------
//
//    //Create GingerPaste
//    @ApiOperation(response = GingerPaste.class, value = "Create GingerPaste")
//    @PostMapping("productionOrder/gingerPasteConfirm")
//    public ResponseEntity<?> postGingerPaste(@Valid @RequestBody SfgGingerPaste sfgGingerPaste,
//                                             @RequestParam String loginUserID,
//                                             @RequestParam String authToken) {
//        mfgService.createSfgGingerPaste(loginUserID, sfgGingerPaste, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get GingerPaste
//    @ApiOperation(response = GingerPaste.class, value = "Get GingerPaste")
//    @GetMapping("productionOrder/gingerPasteGet")
//    public ResponseEntity<?> getGingerPaste(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                            @RequestParam String languageId, @RequestParam String warehouseId,
//                                            @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                            @RequestParam String loginUserID, @RequestParam String authToken) {
//        SfgGingerPaste sfgGingerPaste = mfgService.getSfgGingerPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(sfgGingerPaste, HttpStatus.OK);
//    }
//
//    //----------------------------------------------------------------------------------------
//
//    //Create GarlicPaste
//    @ApiOperation(response = GarlicPaste.class, value = "Create GarlicPaste")
//    @PostMapping("productionOrder/garlicPasteConfirm")
//    public ResponseEntity<?> postGarlicPaste(@Valid @RequestBody GarlicPaste garlicPaste,
//                                             @RequestParam String loginUserID,
//                                             @RequestParam String authToken) {
//        mfgService.createGarlicPaste(loginUserID, garlicPaste, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get GarlicPaste
//    @ApiOperation(response = GarlicPaste.class, value = "Get GarlicPaste")
//    @GetMapping("productionOrder/garlicPasteGet")
//    public ResponseEntity<?> getGarlicPaste(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                            @RequestParam String languageId, @RequestParam String warehouseId,
//                                            @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                            @RequestParam String loginUserID, @RequestParam String authToken) {
//        GarlicPaste garlicPaste = mfgService.getGarlicPaste(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(garlicPaste, HttpStatus.OK);
//    }
//
//    //----------------------------------------------------------------------------------------
//
//    //Create GarlicChop
//    @ApiOperation(response = GarlicChop.class, value = "Create GarlicChop")
//    @PostMapping("productionOrder/garlicChopConfirm")
//    public ResponseEntity<?> postGarlicChop(@Valid @RequestBody GarlicChop garlicChop,
//                                             @RequestParam String loginUserID,
//                                             @RequestParam String authToken) {
//        mfgService.createGarlicChop(loginUserID, garlicChop, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get GarlicChop
//    @ApiOperation(response = GarlicChop.class, value = "Get GarlicChop")
//    @GetMapping("productionOrder/garlicChopGet")
//    public ResponseEntity<?> getGarlicChop(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                            @RequestParam String languageId, @RequestParam String warehouseId,
//                                            @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                            @RequestParam String loginUserID, @RequestParam String authToken) {
//        GarlicChop garlicChop = mfgService.getGarlicChop(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(garlicChop, HttpStatus.OK);
//    }
//
//    //----------------------------------------------------------------------------------
//
//    //Create RoastedKasturiMethi
//    @ApiOperation(response = RoastedKasturiMethi.class, value = "Create RoastedKasturiMethi")
//    @PostMapping("productionOrder/roastedKasturiMethiConfirm")
//    public ResponseEntity<?> postRoastedKasturiMethi(@Valid @RequestBody RoastedKasturiMethi roastedKasturiMethi,
//                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
//        mfgService.createRoastedKasturiMethi(loginUserID, roastedKasturiMethi, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get RoastedKasturiMethi
//    @ApiOperation(response = RoastedKasturiMethi.class, value = "Get RoastedKasturiMethi")
//    @GetMapping("productionOrder/roastedKasturiMethiGet")
//    public ResponseEntity<?> getRoastedKasturiMethi(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                                    @RequestParam String languageId, @RequestParam String warehouseId,
//                                                    @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                                    @RequestParam String loginUserID, @RequestParam String authToken) {
//        RoastedKasturiMethi roastedKasturiMethi = mfgService.getRoastedKasturiMethi(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(roastedKasturiMethi, HttpStatus.OK);
//    }
//
//    //------------------------------------------------------------------------------------------
//
//    //Create RoastedCuminPowder
//    @ApiOperation(response = RoastedCuminPowder.class, value = "Create RoastedCuminPowder")
//    @PostMapping("productionOrder/roastedCuminPowderConfirm")
//    public ResponseEntity<?> postRoastedCuminPowder(@Valid @RequestBody RoastedCuminPowder roastedCuminPowder,
//                                                     @RequestParam String loginUserID, @RequestParam String authToken) {
//        mfgService.createRoastedCuminPowder(loginUserID, roastedCuminPowder, authToken);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    //Get RoastedCuminPowder
//    @ApiOperation(response = RoastedCuminPowder.class, value = "Get RoastedCuminPowder")
//    @GetMapping("productionOrder/roastedCuminPowderGet")
//    public ResponseEntity<?> getRoastedCuminPowder(@RequestParam String companyCodeId, @RequestParam String plantId,
//                                                    @RequestParam String languageId, @RequestParam String warehouseId,
//                                                    @RequestParam String productionOrderNo, @RequestParam String batchNumber,
//                                                    @RequestParam String loginUserID, @RequestParam String authToken) {
//        RoastedCuminPowder roastedCuminPowder = mfgService.getRoastedCuminPowder(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
//        return new ResponseEntity<>(roastedCuminPowder, HttpStatus.OK);
//    }


    //-----------------------------------------OperationLineReport---------------------------------

    @ApiOperation(response = OperationLineReport.class, value = "Search OperationLineReport") // label for swagger
    @PostMapping("/operationLineReport")
    public OperationLineReport[] findOperationLineReport(@RequestBody SearchOperationLineReport searchOperationLineReport, @RequestParam String authToken)
            throws Exception {
        return mfgService.findOperationLineReport(searchOperationLineReport, authToken);
    }

//    @ApiOperation(response = OperationLineReport.class, value = "Search OperationLineV2Report") // label for swagger
//    @PostMapping("/operationLineV2Report")
//    public OperationLineReport[] findOperationLineV2Report(@RequestBody SearchOperationLineReport searchOperationLineReport, @RequestParam String authToken)
//            throws Exception {
//        return mfgService.findOperationLineV2Report(searchOperationLineReport, authToken);
//    }

    @ApiOperation(response = LineReportProcess.class, value = "Search OperationLineV2Report-Process") // label for swagger
    @PostMapping("/findOperationLineProcessReport")
    public LineReportProcess findOperationLineProcessReport(@RequestBody SearchOperationLineReportProcess searchOperationLineReport, @RequestParam String authToken)
            throws Exception {
        return mfgService.findOperationLineProcessReport(searchOperationLineReport, authToken);
    }
    // --------------------------------Process---------------------------------------------------------------
    @ApiOperation(response = Process.class, value = "Get a Process") // label for swagger
    @GetMapping("/process/{productionOrderNo}")
    public ResponseEntity<?> getProcess(@RequestParam String receipeId, @RequestParam String companyCodeId, @RequestParam String plantId,
                                        @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String operationNumber,
                                        @RequestParam String phaseNumber, @RequestParam String bomItem, @RequestParam String batchNumber,
                                        @PathVariable String productionOrderNo, @RequestParam Long productionOrderLineNo, @RequestParam String itemCode, @RequestParam String authToken) {
        Process process = mfgService.getProcess(companyCodeId, plantId, languageId, warehouseId, operationNumber, receipeId, productionOrderNo, productionOrderLineNo,
                itemCode,phaseNumber, bomItem, batchNumber, authToken);
        return new ResponseEntity<>(process, HttpStatus.OK);
    }

    @ApiOperation(response = Process.class, value = "Get a Process by OrderNo")
    @GetMapping("/process/v2/{productionOrderNo}")
    public ResponseEntity<?> getProcess(@RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String warehouseId,
                                        @RequestParam String plantId, @PathVariable String productionOrderNo, @RequestParam String authToken) {
        Process[] process = mfgService.getProcess(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, authToken);
        return new ResponseEntity<>(process, HttpStatus.OK);
    }

    @ApiOperation(response = Process.class, value = "Get a Process by Batch")
    @GetMapping("/process/batch/{productionOrderNo}")
    public ResponseEntity<?> getProcess(@RequestParam String companyCodeId, @RequestParam String languageId, @RequestParam String warehouseId,
                                        @RequestParam String plantId, @PathVariable String productionOrderNo, @RequestParam String batchNumber, @RequestParam String authToken) {
        Process[] process = mfgService.getProcess(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, authToken);
        return new ResponseEntity<>(process, HttpStatus.OK);
    }

    @ApiOperation(response = Process.class, value = "Search Process") // label for swagger
    @PostMapping("/process/findProcess")
    public Process[] findProcess(@RequestBody SearchProcess searchProcess, @RequestParam String authToken) {
        return mfgService.findProcess(searchProcess, authToken);
    }

    @ApiOperation(response = Process.class, value = "Create Process") // label for swagger
    @PostMapping("process/create")
    public ResponseEntity<?> postProcess(@Valid @RequestBody List<Process> newProcess, @RequestParam String loginUserID, @RequestParam String authToken) {
        Process[] createdProcess = mfgService.createProcess(newProcess, loginUserID, authToken);
        return new ResponseEntity<>(createdProcess, HttpStatus.OK);
    }

    @ApiOperation(response = Process.class, value = "Patch Process") // label for swagger
    @PatchMapping("/process/update/{productionOrderNo}")
    public ResponseEntity<?> patchProcess(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String languageId,
                                          @RequestParam String warehouseId, @PathVariable String productionOrderNo,
                                          @Valid @RequestBody List<Process> modifyProcess, @RequestParam String loginUserID, @RequestParam String authToken) {
        Process[] updatedProcess = mfgService.updateProcess(modifyProcess, companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(updatedProcess, HttpStatus.OK);
    }

    @ApiOperation(response = Process.class, value = "Delete Process") // label for swagger
    @DeleteMapping("/process/{productionOrderNo}")
    public ResponseEntity<?> deleteProcess(@RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @PathVariable String productionOrderNo,
                                           @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteProcess(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(response = Process.class, value = "Delete Process by Batch") // label for swagger
    @DeleteMapping("/process/batch/{productionOrderNo}")
    public ResponseEntity<?> deleteProcess(@RequestParam String companyCodeId, @RequestParam String plantId,
                                           @RequestParam String languageId, @RequestParam String warehouseId, @PathVariable String productionOrderNo,
                                           @RequestParam String loginUserID, @RequestParam String batchNumber, @RequestParam String authToken) {
        mfgService.deleteProcess(companyCodeId, plantId, languageId, warehouseId, productionOrderNo, batchNumber, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --------------------------------MasterPhase---------------------------------------------------------------
    @ApiOperation(response = MasterPhase.class, value = "Get a MasterPhase") // label for swagger
    @GetMapping("/masterPhase/{phaseNumber}")
    public ResponseEntity<?> getMasterPhase(@PathVariable String phaseNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                            @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken) {
        MasterPhase masterphase = mfgService.getMasterPhase(companyCodeId, plantId, languageId, warehouseId, phaseNumber, authToken);
        return new ResponseEntity<>(masterphase, HttpStatus.OK);
    }

    @ApiOperation(response = MasterPhase.class, value = "Search MasterPhase") // label for swagger
    @PostMapping("/masterPhase/findMasterPhase")
    public MasterPhase[] findMasterPhase(@RequestBody SearchMasterPhase searchMasterPhase, @RequestParam String authToken) {
        return mfgService.findMasterPhase(searchMasterPhase, authToken);
    }

    @ApiOperation(response = MasterPhase.class, value = "Create MasterPhase") // label for swagger
    @PostMapping("masterPhase/create")
    public ResponseEntity<?> postMasterPhase(@Valid @RequestBody List<MasterPhase> newMasterPhase, @RequestParam String loginUserID, @RequestParam String authToken) {
        MasterPhase[] createdMasterPhase = mfgService.createMasterPhase(newMasterPhase, loginUserID, authToken);
        return new ResponseEntity<>(createdMasterPhase, HttpStatus.OK);
    }

    @ApiOperation(response = MasterPhase.class, value = "Patch MasterPhase") // label for swagger
    @PatchMapping("/masterPhase/update")
    public ResponseEntity<?> patchMasterPhase(@RequestParam String companyCodeId, @RequestParam String plantId, @RequestParam String loginUserID,
                                              @RequestParam String languageId, @RequestParam String warehouseId, @RequestParam String authToken,
                                              @Valid @RequestBody List<MasterPhase> modifyMasterPhase) {
        MasterPhase[] createdMasterPhase = mfgService.updateMasterPhase(companyCodeId, plantId, languageId, warehouseId, loginUserID, modifyMasterPhase, authToken);
        return new ResponseEntity<>(createdMasterPhase, HttpStatus.OK);
    }

    @ApiOperation(response = MasterPhase.class, value = "Delete MasterPhase") // label for swagger
    @DeleteMapping("/masterPhase/{phaseNumber}")
    public ResponseEntity<?> deleteMasterPhase(@PathVariable String phaseNumber, @RequestParam String companyCodeId, @RequestParam String plantId,
                                               @RequestParam String languageId, @RequestParam String warehouseId,
                                               @RequestParam String loginUserID, @RequestParam String authToken) {
        mfgService.deleteMasterPhase(companyCodeId, plantId, languageId, warehouseId, phaseNumber, loginUserID, authToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}