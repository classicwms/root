package com.tekclover.wms.core.controller;

import com.tekclover.wms.core.model.spark.*;
import com.tekclover.wms.core.model.transaction.QualityLineV2;
import com.tekclover.wms.core.model.transaction.SearchInboundHeaderV2;
import com.tekclover.wms.core.service.SparkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/mnr-spark-service")
@Api(tags = {"Spark Service"}, value = "Spark Service Operations") // label for swagger
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Spark Modules")})
public class SparkController {

    @Autowired
    private SparkService sparkService;


    /*===================================================MASTER==========================================================*/
    //master
    @ApiOperation(response = ImBasicData1.class, value = "Get All ImBasicData1 details")
    @PostMapping("/imbasicdata1")
    public ResponseEntity<?> findImBasicData1(@RequestBody SearchImBasicData1 findImBasicData1) throws Exception {
        ImBasicData1[] imBasicData1s = sparkService.findImBasicData1(findImBasicData1);
        return new ResponseEntity<>(imBasicData1s, HttpStatus.OK);
    }

    //StorageBin
    @ApiOperation(response = StorageBin.class, value = "Spark StorageBin details")
    @PostMapping("/storageBin")
    public ResponseEntity<?> findStorageBin(@RequestBody SearchStorageBin searchStorageBin) throws Exception {
        StorageBin[] storageBins = sparkService.findStorageBin(searchStorageBin);
        return new ResponseEntity<>(storageBins, HttpStatus.OK);
    }

    /*=========================================Inbound=====================================================*/

    //ContainerReceipt
    @ApiOperation(response = ContainerReceiptV2.class, value = "Spark ContainerReceipt details")
    @PostMapping("/containerReceipt")
    public ResponseEntity<?> findContainerReceiptV2(@RequestBody FindContainerReceiptV2 findContainerReceiptV2) throws Exception {
        ContainerReceiptV2[] containerReceiptV2 = sparkService.findContainerReceiptV2(findContainerReceiptV2);
        return new ResponseEntity<>(containerReceiptV2, HttpStatus.OK);
    }

    //PreInboundHeader
    @ApiOperation(response = PreInboundHeaderV2.class, value = "Spark PreInboundHeader details")
    @PostMapping("/preinboundheader")
    public ResponseEntity<?> findPreinboundHeaderV2(@RequestBody FindPreInboundHeaderV2 findPreInboundHeaderV2) throws Exception {
        PreInboundHeaderV2[] preInboundHeader = sparkService.findPreinboundHeaderV2(findPreInboundHeaderV2);
        return new ResponseEntity<>(preInboundHeader, HttpStatus.OK);
    }

    //StagingHeader
    @ApiOperation(response = StagingHeaderV2.class, value = "Spark StagingHeader details")
    @PostMapping("/stagingHeader")
    public ResponseEntity<?> findStagingHeader(@RequestBody FindStagingHeaderV2 searchPutAwayHeaderV2) throws Exception {
        StagingHeaderV2[] stagingHeader = sparkService.findStagingHeaderV2(searchPutAwayHeaderV2);
        return new ResponseEntity<>(stagingHeader, HttpStatus.OK);
    }

    //GrHeader
    @ApiOperation(response = GrHeaderV2.class, value = "Spark GrHeader details")
    @PostMapping("/grHeader")
    public ResponseEntity<?> findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeaderV2) throws Exception {
        GrHeaderV2[] grHeadertV2 = sparkService.findGrHeaderV2(searchGrHeaderV2);
        return new ResponseEntity<>(grHeadertV2, HttpStatus.OK);
    }

    //PutAwayHeader
    @ApiOperation(response = PutAwayHeaderV2.class, value = "Spark PutAway Header details")
    @PostMapping("/putAwayHeader")
    public ResponseEntity<?> findPreInboundHeaderv2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeaderV2) throws Exception {
        PutAwayHeaderV2[] putAwayHeaderV2 = sparkService.findPutawayHeaderV2(searchPutAwayHeaderV2);
        return new ResponseEntity<>(putAwayHeaderV2, HttpStatus.OK);
    }

    //InboundHeader
    @ApiOperation(response = InboundHeaderV2.class, value = "Spark InboundHeader details")
    @PostMapping("/inboundHeader")
    public ResponseEntity<?> findInboundHeaderV2(@RequestBody SearchInboundHeaderV2 searchPutAwayHeaderV2) throws Exception {
        InboundHeaderV2[] inboundHeaderV2s = sparkService.findInboundHeaderV2(searchPutAwayHeaderV2);
        return new ResponseEntity<>(inboundHeaderV2s, HttpStatus.OK);
    }

    /*===========================================OUTBOUND=======================================================*/

    //PreoutBoundHeader
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Spark PreOutboundHeader details")
    @PostMapping("/preoutboundheader")
    public ResponseEntity<?> findPreOutboundHeader(@RequestBody FindPreOutboundHeaderV2 findPreOutboundHeaderV2) throws Exception {
        PreOutboundHeaderV2[] PreboundReversal = sparkService.findPreOutboundHeaderV2(findPreOutboundHeaderV2);
        return new ResponseEntity<>(PreboundReversal, HttpStatus.OK);
    }

    //OrderManagementLine
    @ApiOperation(response = OrderManagementLineV2.class, value = "Spark OrderManagementLine details")
    @PostMapping("/ordermanagementline")
    public ResponseEntity<?> findOrderManagementLine(@RequestBody FindOrderManagementLineV2 findPreOutboundHeaderV2) throws Exception {
        OrderManagementLineV2[] orderManagementLineV2s = sparkService.findOrderManagementLineV2(findPreOutboundHeaderV2);
        return new ResponseEntity<>(orderManagementLineV2s, HttpStatus.OK);
    }

    //PickupHeader
    @ApiOperation(response = PickupHeaderV2.class, value = "Spark PickUpHeader details")
    @PostMapping("/pickupheader")
    public ResponseEntity<?> findPickupHeader(@RequestBody FindPickupHeaderV2 findPickupHeaderV2) throws Exception {
        PickupHeaderV2[] pickupHeaderV2s = sparkService.findPickupHeaderV2(findPickupHeaderV2);
        return new ResponseEntity<>(pickupHeaderV2s, HttpStatus.OK);
    }

    //QualityHeader
    @ApiOperation(response = QualityHeaderV2.class, value = "Spark QualityHeader details")
    @PostMapping("/qualityHeader")
    public ResponseEntity<?> findQualityHeader(@RequestBody FindQualityHeaderV2 findQualityHeaderV2) throws Exception {
        QualityHeaderV2[] qualityHeader = sparkService.findQualityHeaderV2(findQualityHeaderV2);
        return new ResponseEntity<>(qualityHeader, HttpStatus.OK);
    }

    //OutboundHeader
    @ApiOperation(response = OutBoundHeaderV2.class, value = "Spark OutboundHeader details")
    @PostMapping("/outboundheader")
    public ResponseEntity<?> findOutboundHeader(@RequestBody FindOutBoundHeaderV2 findOutBoundHeaderV2) throws Exception {
        OutBoundHeaderV2[] outboundHeader = sparkService.findOutboundHeaderV2(findOutBoundHeaderV2);
        return new ResponseEntity<>(outboundHeader, HttpStatus.OK);
    }

    //OutboundReversal
    @ApiOperation(response = OutBoundReversalV2.class, value = "Spark OutboundReversal details")
    @PostMapping("/outboundReversal")
    public ResponseEntity<?> findOutboundReversal(@RequestBody FindOutBoundReversalV2 findOutBoundReversalV2) throws Exception {
        OutBoundReversalV2[] outBoundReversalV2s = sparkService.findOutBoundReversal(findOutBoundReversalV2);
        return new ResponseEntity<>(outBoundReversalV2s, HttpStatus.OK);
    }

    //PickUpLine
    @ApiOperation(response = PickupLine.class, value = "Spark PickUpLine details")
    @PostMapping("/pickUpLine")
    public ResponseEntity<?> findPickUpLine(@RequestBody SearchPickupLineV2 searchPickupLineV2) throws Exception {
        PickupLine[] pickUpLineList = sparkService.findPickUpLine(searchPickupLineV2);
        return new ResponseEntity<>(pickUpLineList, HttpStatus.OK);
    }

    //InventoryMovement
    @ApiOperation(response = InventoryMovementV2.class, value = "Spark InventoryMovement details")
    @PostMapping("/inventorymovement")
    public ResponseEntity<?> findInventoryMovement(@RequestBody SearchInventoryMovementV2 searchInventoryMovementV2) throws Exception {
        InventoryMovementV2[] inventoryMovementV2s = sparkService.findInventoryMovementV2(searchInventoryMovementV2);
        return new ResponseEntity<>(inventoryMovementV2s, HttpStatus.OK);
    }

    // Find InventoryV2
    @ApiOperation(response = InventoryV2.class, value = "Spark Inventory details")
    @PostMapping("inventory/v2")
    public ResponseEntity<?> findInventoryV2(@RequestBody FindInventoryV2 findInventoryV2) throws Exception {
        InventoryV2[] inventoryList = sparkService.findInventoryV2(findInventoryV2);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    // Find PutAwayLineV2
    @ApiOperation(response = PutAwayLineV2.class, value = "Spark PutAwayLine details")
    @PostMapping("putawayline/v2")
    public ResponseEntity<?> findPutAwayLineV2(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        PutAwayLineV2[] putAwayLineList = sparkService.findPutAwayLineV2(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineList, HttpStatus.OK);
    }

    // Find PeriodicHeaderV2
    @ApiOperation(response = PeriodicHeaderV2.class, value = "Spark PeriodicHeader details")
    @PostMapping("/periodicheader")
    public ResponseEntity<?> searchPeriodicHeader(@RequestBody FindPeriodicHeaderV2 findPeriodicHeaderV2) throws Exception {
        PeriodicHeaderV2[] periodicHeaders = sparkService.findPeriodicHeader(findPeriodicHeaderV2);
        return new ResponseEntity<>(periodicHeaders, HttpStatus.OK);
    }

    // Find InhouseTransferLine
    @ApiOperation(response = InhouseTransferLine.class, value = "Spark InhouseTransferLine details")
    @PostMapping("/inhousetransferline")
    public ResponseEntity<?> findInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine) throws Exception {
        InhouseTransferLine[] inhouseTransferLines = sparkService.findInhouseTransferLine(searchInhouseTransferLine);
        return new ResponseEntity<>(inhouseTransferLines, HttpStatus.OK);
    }

    // Find PerpetualHeader
    @ApiOperation(response = PerpetualHeader.class, value = "Spark PerpetualHeader details")
    @PostMapping("/perpetualheader")
    public ResponseEntity<?> searchPerpetualHeader(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeaderV2) throws Exception {
        PerpetualHeader[] perpetualHeaders = sparkService.findPerpetualHeader(searchPerpetualHeaderV2);
        return new ResponseEntity<>(perpetualHeaders, HttpStatus.OK);
    }

    // Find InboundLine
    @ApiOperation(response = InboundLineV2.class, value = "Spark InboundLine details")
    @PostMapping("/inboundline")
    public ResponseEntity<?> searchInboundLine(@RequestBody FindInboundLineV2 findInboundLineV2) throws Exception {
        InboundLineV2[] inboundLines = sparkService.findInboundLine(findInboundLineV2);
        return new ResponseEntity<>(inboundLines, HttpStatus.OK);
    }

    //InHouseTransferHeader
    @ApiOperation(response = InhouseTransferHeader.class, value = "Spark InhouseTransferHeader details")
    @PostMapping("/inhousetransferheader")
    public ResponseEntity<?> getAllInhouseTransferHeaders() {
        InhouseTransferHeader[] inhouseTransferHeaders = sparkService.getAllInhouseTransferHeaders();
        return new ResponseEntity<>(inhouseTransferHeaders, HttpStatus.OK);
    }

    // Get StockReport
    @ApiOperation(response = StockReport.class, value = "Spark StockReport details")
    @PostMapping("/stockreport")
    public ResponseEntity<?> getStockReport(@RequestBody FindStockReport findStockReport) throws Exception {
        StockReport[] stockReports = sparkService.getStockReport(findStockReport);
        return new ResponseEntity<>(stockReports, HttpStatus.OK);
    }

    // Find OrderStatusReport
    @ApiOperation(response = OrderStatusReport.class, value = "Spark Order Status Report")
    @PostMapping("/orderstatusreport")
    public  ResponseEntity<?> searchOrderStatusReport(@RequestBody SearchOrderStatusReport searchOrderStatusReport) throws Exception{
        OrderStatusReport[] orderStatusReport = sparkService.findOrderStatusReport(searchOrderStatusReport);
        return new ResponseEntity<>(orderStatusReport, HttpStatus.OK);
    }

    // Find PeriodicLine
    @ApiOperation(response = PeriodicLineV2.class, value = "Spark PeriodicLine")
    @PostMapping("/periodicline")
    public  ResponseEntity<?> searchPeriodicline(@RequestBody FindPeriodicLineV2 findPeriodicLineV2) throws Exception{
        PeriodicLineV2[] periodicLineV2s = sparkService.findPeriodicLine(findPeriodicLineV2);
        return new ResponseEntity<>(periodicLineV2s, HttpStatus.OK);
    }

    // Find PerpetualLine
    @ApiOperation(response = PerpetualLineV2.class, value = "Spark PerpetualLine")
    @PostMapping("/perpetualline")
    public  ResponseEntity<?> searchPerpetualLine(@RequestBody SearchPerpetualLineV2 searchOrderStatusReport) throws Exception{
        PerpetualLineV2[] perpetualLineV2s = sparkService.findPerpetualLine(searchOrderStatusReport);
        return new ResponseEntity<>(perpetualLineV2s, HttpStatus.OK);
    }

    // Find PreOutBoundLine
    @ApiOperation(response = PreOutBoundLineV2.class, value = "Spark PreOutBoundLine")
    @PostMapping("/preoutboundline")
    public  ResponseEntity<?> searchPreOutBoundLine(@RequestBody FindPreOutBoundLineV2 findPreOutBoundLineV2) throws Exception{
        PreOutBoundLineV2[] preOutBoundLineV2s = sparkService.findPreOutBoundLine(findPreOutBoundLineV2);
        return new ResponseEntity<>(preOutBoundLineV2s, HttpStatus.OK);
    }

    //Find StagingLine
    @ApiOperation(response = StagingLineV2.class, value = "Spark StagingLine details")
    @PostMapping("/stagingline")
    public ResponseEntity<?> findStagingLinetV2(@RequestBody FindStagingLineV2 findStagingLineV2) throws Exception {
        StagingLineV2[] stagingLineV2 = sparkService.findStagingLineV2(findStagingLineV2);
        return new ResponseEntity<>(stagingLineV2, HttpStatus.OK);
    }

    //Find GrLine
    @ApiOperation(response = GrLineV2.class, value = "Spark GrLine details")
    @PostMapping("/grline")
    public ResponseEntity<?> findGrLine(@RequestBody FindGrLineV2 findGrLineV2) throws Exception {
        GrLineV2[] grLineV2 = sparkService.findGrLineV2(findGrLineV2);
        return new ResponseEntity<>(grLineV2, HttpStatus.OK);
    }

    // Find QualityLineV2
    @ApiOperation(response = QualityLineV2.class, value = "Spark QualityLine details")
    @PostMapping("qualityline")
    public ResponseEntity<?> findQualityLineV2(@RequestBody FindQualityLine findQualityLineV2) throws Exception {
        QualityLine[] qualityLineList = sparkService.findQualityLine(findQualityLineV2);
        return new ResponseEntity<>(qualityLineList, HttpStatus.OK);
    }

    // Find ImBasicData1
    @ApiOperation(response = ImBasicData1V3.class, value = "Spark ImBasicData1 details")
    @PostMapping("/imbasicdata1/v2")
    public ResponseEntity<?> findImBasicData1V3(@RequestBody SearchImBasicData1 searchImBasicData1) throws Exception {
        ImBasicData1V3[] imBasicData1V3s = sparkService.findImbasicData1V3(searchImBasicData1);
        return new ResponseEntity<>(imBasicData1V3s, HttpStatus.OK);
    }

    // Find ImBasicData1
    @ApiOperation(response = ImBasicData1V3.class, value = "Spark ImBasicData1 details test")
    @PostMapping("/imbasicdata1/test/v2")
    public ResponseEntity<?> findImBasicData1V5(@RequestBody SearchImBasicData1 searchImBasicData1) throws Exception {
        ImBasicData1V3[] imBasicData1V3s = sparkService.findImbasicData1V5(searchImBasicData1);
        return new ResponseEntity<>(imBasicData1V3s, HttpStatus.OK);
    }

    // Find BusinessPartner
    @ApiOperation(response = BusinessPartnerV2.class, value = "Spark BusinessPartner details test")
    @PostMapping("/findBusinessPartner")
    public ResponseEntity<?> findBusinessPartner(@RequestBody FindBusinessPartner findBusinessPartner) throws Exception {
        BusinessPartnerV2[] businessPartnerV2s = sparkService.findBusinessPartner(findBusinessPartner);
        return new ResponseEntity<>(businessPartnerV2s, HttpStatus.OK);
    }


    // Find InboundLine Join InboundHeader
    @ApiOperation(response = InboundLineV3.class, value = "Spark InboundLine Report")
    @PostMapping("/findInboundLineReport")
    public ResponseEntity<?> findInboundLineReport(@RequestBody FindInboundLineV2 findInboundLineV2) throws Exception {
        InboundLineV3[] inboundLine = sparkService.findInboundLineJoin(findInboundLineV2);
        return new ResponseEntity<>(inboundLine, HttpStatus.OK);
    }

    // Find Inventory
    @ApiOperation(response = InventoryV3.class, value = "Spark Inventory")
    @PostMapping("/inventory/v2/new")
    public ResponseEntity<?> findInventoryV2(@RequestBody FindInventoryV3 findInventoryV3) throws Exception {
        InventoryV3[] inventoryV3s = sparkService.findInventoryV3(findInventoryV3);
        return new ResponseEntity<>(inventoryV3s, HttpStatus.OK);
    }

    // Find PutAwayLine
    @ApiOperation(response = PutAwayLineSpark.class, value = "Spark PutAwayLine Spark")
    @PostMapping("/putawayline/new")
    public ResponseEntity<?> findPutAwayLineSpark(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        PutAwayLineSpark[] putAwayLineSparks = sparkService.findPutAwayLineSpark(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineSparks, HttpStatus.OK);
    }
}
