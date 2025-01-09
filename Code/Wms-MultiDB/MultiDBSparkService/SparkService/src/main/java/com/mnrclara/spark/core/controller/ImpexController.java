package com.mnrclara.spark.core.controller;

import com.mnrclara.spark.core.model.Almailem.BusinessPartnerV2;
import com.mnrclara.spark.core.model.Almailem.FindBusinessPartner;
import com.mnrclara.spark.core.model.Almailem.FindOutboundLineV2;
import com.mnrclara.spark.core.model.Almailem.joinspark.GrLineV3;
import com.mnrclara.spark.core.model.Almailem.joinspark.OutboundHeaderV3;
import com.mnrclara.spark.core.model.impex.*;
import com.mnrclara.spark.core.model.wmscorev2.*;
import com.mnrclara.spark.core.service.ImpexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Api(tags = {"Impex"}, value = "Impex Operations related to ImpexController")
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Impex")})
@RequestMapping("/impex")
public class ImpexController {

    @Autowired
    ImpexService impexService;

    //=======================================================================================================================

    //Find BusinessPartnerService
    @ApiOperation(response = BusinessPartnerV2.class, value = "Spark BusinessPartner")
    @PostMapping("/businessPartner")
    public ResponseEntity<?> businessPartner(@RequestBody FindBusinessPartner findBusinessPartner) throws Exception {
        List<BusinessPartnerV2> businessPartnerV2s = impexService.findBusinessPartner(findBusinessPartner);
        return new ResponseEntity<>(businessPartnerV2s, HttpStatus.OK);
    }

    //Find StorageBin
    @ApiOperation(response = StorageBin.class, value = "Spark Find StorageBin")
    @PostMapping("/storagebin")
    public ResponseEntity<?> searchStorageBin(@RequestBody SearchStorageBin searchStorageBin) throws Exception {
        List<StorageBin> storageBinList = impexService.findStorageBin(searchStorageBin);
        return new ResponseEntity<>(storageBinList, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1NewV4.class, value = "Spark ImBasicData1")
    @PostMapping("/imbasicdata1")
    public ResponseEntity<?> findImBasicData1(SearchImBasicData1 findImBasicData1) throws Exception {
        List<ImBasicData1NewV4> imBasicData1List = impexService.searchImBasicData1(findImBasicData1);
        return new ResponseEntity<>(imBasicData1List, HttpStatus.OK);
    }

    //Find ImBasicData1
    @ApiOperation(response = ImBasicData1V4.class, value = "Spark ImBasicData1V3")
    @PostMapping("/imbasicdata1v3")
    public ResponseEntity<?> findImBasicData1V3(@RequestBody SearchImBasicData1 searchImBasicData1) throws Exception {
        List<ImBasicData1V4> imBasicData1V3s = impexService.searchImBasicData1V3(searchImBasicData1);
        return new ResponseEntity<>(imBasicData1V3s, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Spark FindPreInboundHeader")
    @PostMapping("/preinboundHeader")
    public ResponseEntity<?> findPreinboundHeaderv2(@RequestBody FindPreInboundHeaderV2 findPreInboundHeaderV2) throws Exception {
        List<PreInboundHeaderV2> preInboundHeaderv2 = impexService.findPreInboundHeaderv2(findPreInboundHeaderV2);
        return new ResponseEntity<>(preInboundHeaderv2, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Spark FindContainerReceipt")
    @PostMapping("/containerReceipt")
    public ResponseEntity<?> findContainerReceiptV2(@RequestBody FindContainerReceiptV2 findContainerReceiptV2) throws Exception {
        List<ContainerReceiptV2> containerReceiptV2 = impexService.findContainerReceipt(findContainerReceiptV2);
        return new ResponseEntity<>(containerReceiptV2, HttpStatus.OK);
    }
    
    @ApiOperation(response = Optional.class, value = "Spark Find GrHeader")
    @PostMapping("/grheader")
    public ResponseEntity<?> findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeaderV2) throws Exception {
        List<GrHeaderV2> findGrHeaderV2 = impexService.findGrHeaderV2(searchGrHeaderV2);
        return new ResponseEntity<>(findGrHeaderV2, HttpStatus.OK);
    }

    //Find PutAwayHeader
    @ApiOperation(response = PutAwayHeaderV4.class, value = "Spark Find PutAwayHeader")
    @PostMapping("/putawayheader")
    public ResponseEntity<?> findPutawayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeaderV2) throws Exception {
        List<PutAwayHeaderV4> findPutAwayHeaderV2 = impexService.findPutAwayHeaderV2(searchPutAwayHeaderV2);
        return new ResponseEntity<>(findPutAwayHeaderV2, HttpStatus.OK);
    }

    //Find StagingHeader
    @ApiOperation(response = StagingHeaderV2.class, value = "Spark Find StagingHeader")
    @PostMapping("/stagingheader")
    public ResponseEntity<?> searchStagingHeader(@RequestBody FindStagingHeaderV2 findStagingHeader) throws Exception {
        List<StagingHeaderV2> stList = impexService.findStagingHeader(findStagingHeader);
        return new ResponseEntity<>(stList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Spark InboundHeader")
    @PostMapping("/inboundHeader")
    public ResponseEntity<?> findInboundHeader(@RequestBody FindInboundHeaderV2 findInboundHeader) throws Exception {
        List<InboundHeaderV2> inboundHeaderList = impexService.findInboundHeader(findInboundHeader);
        return new ResponseEntity<>(inboundHeaderList, HttpStatus.OK);
    }

    // Find InboundLine
    @ApiOperation(response = InboundLineNewV4.class, value = "Spark Find InboundLine")
    @PostMapping("/inboundline")
    public ResponseEntity<?> searchInboundLine(@RequestBody FindInboundLineV2 findInboundLineV2) throws Exception {
        List<InboundLineNewV4> inboundLineList = impexService.findInboundLineV2(findInboundLineV2);
        return new ResponseEntity<>(inboundLineList, HttpStatus.OK);
    }

    //Get StagingLine
    @ApiOperation(response = StagingLineV4.class, value = "Spark FindStagingLine")
    @PostMapping("/stagingLine")
    public ResponseEntity<?> findStagingLineV2(@RequestBody FindStagingLineV2 findStagingLineV2) throws Exception {
        List<StagingLineV4> stagingLineV2 = impexService.findStagingLineV2(findStagingLineV2);
        return new ResponseEntity<>(stagingLineV2, HttpStatus.OK);
    }

    //Get GrLine
    @ApiOperation(response = GrLineV4.class, value = "Spark FindGrLine")
    @PostMapping("/grline")
    public ResponseEntity<?> findGrLineV2(@RequestBody FindGrLineV2 findGrLineV2) throws Exception {
        List<GrLineV4> grLineV2 = impexService.findGrLineV2(findGrLineV2);
        return new ResponseEntity<>(grLineV2, HttpStatus.OK);
    }

    // PutAwayLine
    @ApiOperation(response = PutAwayLineCoreV4.class, value = "Spark PutAwayLine")
    @PostMapping("/putawayline/new")
    public ResponseEntity<?> putAwayLineSpark(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        List<PutAwayLineCoreV4> putAwayLineCoreList = impexService.getPutAwayLine(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineCoreList, HttpStatus.OK);
    }

    // PutAwayLineV2
    @ApiOperation(response = PutAwayLineV4.class, value = "Spark PutAwayLineV2")
    @PostMapping("/putawayline")
    public ResponseEntity<?> putAwayLineSparkV2(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        List<PutAwayLineV4> putAwayLineCoreList = impexService.findPutAwayLineV2(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineCoreList, HttpStatus.OK);
    }

    /*================================================OUTBOUND=======================================================*/

    //Find PreOutboundHeader
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Spark Find PreOutboundHeader")
    @PostMapping("/preoutboundheader")
    public ResponseEntity<?> searchPreOutboundHeader(@RequestBody FindPreOutboundHeaderV2 findPreObHeader) throws Exception {
        List<PreOutboundHeaderV2> preObList = impexService.findPreOutboundHeader(findPreObHeader);
        return new ResponseEntity<>(preObList, HttpStatus.OK);
    }

    //Find OrderManagementLineV2
    @ApiOperation(response = OrderManagementLineV4.class, value = "Spark Find OrderManagementLine")
    @PostMapping("/ordermanagementline")
    public ResponseEntity<?> searchOrderManagementLineV2(@RequestBody FindOrderManagementLineV2 findOrderManagementLine)
            throws Exception {
        List<OrderManagementLineV4> omlList = impexService.findOrderManagementLine(findOrderManagementLine);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    // PickupHeaderV2
    @ApiOperation(response = PickupHeaderV4.class, value = "Spark Find PickupHeader")
    @PostMapping("/pickupheader")
    public ResponseEntity<?> findPickupHeaderV2(@RequestBody FindPickupHeaderV2 findPickupHeaderV2) throws Exception {
        List<PickupHeaderV4> pickupHeaderV2List = impexService.findPickupHeaderV2(findPickupHeaderV2);
        return new ResponseEntity<>(pickupHeaderV2List, HttpStatus.OK);
    }

    // QualityHeaderV2
    @ApiOperation(response = QualityHeaderV4.class, value = "Spark Find QualityHeader")
    @PostMapping("/qualityheader")
    public ResponseEntity<?> findQualityHeaderV2(@RequestBody FindQualityHeaderV2 findQualityHeaderV2) throws Exception {
        List<QualityHeaderV4> qualityHeaderV2List = impexService.findQualityHeaderV2(findQualityHeaderV2);
        return new ResponseEntity<>(qualityHeaderV2List, HttpStatus.OK);
    }

    // QualityHeaderV2
    @ApiOperation(response = OutBoundHeaderV2.class, value = "Spark Find OutBoundHeader")
    @PostMapping("/outboundheader")
    public ResponseEntity<?> findOutboundHeaderV2(@RequestBody FindOutBoundHeaderV2 findOutBoundHeaderV2) throws Exception {
        List<OutBoundHeaderV2> outBoundHeaderV2List = impexService.findOutBoundHeaderV2(findOutBoundHeaderV2);
        return new ResponseEntity<>(outBoundHeaderV2List, HttpStatus.OK);
    }

    //Find OutBoundReversal
    @ApiOperation(response = OutBoundReversalV2.class, value = "Spark Find OutBoundReversal")
    @PostMapping("/outboundreversal")
    public ResponseEntity<?> searchOutboundReversal(@RequestBody FindOutBoundReversalV2 findOutBoundReversalV2)
            throws Exception {
        List<OutBoundReversalV2> omlList = impexService.findOutBoundReversal(findOutBoundReversalV2);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find PickupLine
    @ApiOperation(response = PickupLineNewV4.class, value = "Spark Find PickUpLine")
    @PostMapping("/pickupline")
    public ResponseEntity<?> searchPickUpLine(@RequestBody SearchPickupLineV2 searchStorageBin)
            throws Exception {
        List<PickupLineNewV4> omlList = impexService.findPickupLines(searchStorageBin);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find InventoryMovement
    @ApiOperation(response = InventoryMovementV2.class, value = "Spark Find InventoryMovement")
    @PostMapping("/inventorymovement")
    public ResponseEntity<?> searchInventoryMovement(@RequestBody SearchInventoryMovementV2 searchInventoryMovementV2)
            throws Exception {
        List<InventoryMovementV2> omlList = impexService.findInventoryMovement(searchInventoryMovementV2);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find InhouseTransferLine
    @ApiOperation(response = InhouseTransferLine.class, value = "Spark Find InhouseTransferLine")
    @PostMapping("/inhousetransferline")
    public ResponseEntity<?> searchInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine)
            throws Exception {
        List<InhouseTransferLine> omlList = impexService.findInhouseTransferLines(searchInhouseTransferLine);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    // Find InventoryV2
    @ApiOperation(response = InventoryV4.class, value = "Spark Find Inventory")
    @PostMapping("/inventory/new")
    public ResponseEntity<?> searchInventoryV2(@RequestBody FindInventoryV2 findInventoryV2) throws Exception {
        List<InventoryV4> inventoryList = impexService.findInventoryV2(findInventoryV2);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    // Find PeriodicHeader
    @ApiOperation(response = PeriodicHeaderV2.class, value = "Spark Find PeriodicHeader")
    @PostMapping("/periodicheader")
    public ResponseEntity<?> searchPeriodicHeaderV2(@RequestBody FindPeriodicHeaderV2 findPeriodicHeaderV2) throws Exception {
        List<PeriodicHeaderV2> periodicHeaderList = impexService.findPeriodicHeaderV2(findPeriodicHeaderV2);
        return new ResponseEntity<>(periodicHeaderList, HttpStatus.OK);
    }

    // Find PerpetualHeader
    @ApiOperation(response = PerpetualHeader.class, value = "Spark Find PerpetualHeader")
    @PostMapping("/perpetualheader")
    public ResponseEntity<?> searchPerpetualHeader(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeaderV2) throws Exception {
        List<PerpetualHeader> perpetualHeaderList = impexService.findPerpetualHeader(searchPerpetualHeaderV2);
        return new ResponseEntity<>(perpetualHeaderList, HttpStatus.OK);
    }

    // Get InhouseTransferHeader
    @ApiOperation(response = InhouseTransferHeaderV4.class, value = "Spark Find InhouseTransferHeader")
    @PostMapping("/inhousetransferheader")
    public ResponseEntity<?> searchInhouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInhouseTransferHeader) throws Exception {
        List<InhouseTransferHeaderV4> inboundLineList = impexService.findInhouseTransferHeader(searchInhouseTransferHeader);
        return new ResponseEntity<>(inboundLineList, HttpStatus.OK);
    }

    // Get StockReport
    @ApiOperation(response = StockReport.class, value = "Spark Get StockReport")
    @PostMapping("/stockreport")
    public ResponseEntity<?> getStockReport(FindStockReport findStockReport) throws Exception {
        List<StockReport> stockReportList = impexService.getStockReport(findStockReport);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get OrderStatusReport
    @ApiOperation(response = Optional.class, value = "Spark Get Order Status Report")
    @PostMapping("/orderstatusreport")
    public ResponseEntity<?> findOrderStatus(@RequestBody SearchOrderStatusReport searchOrderStatusReport) throws Exception {
        List<OrderStatusReport> findOrderStatus = impexService.findOrderStatusReport(searchOrderStatusReport);
        return new ResponseEntity<>(findOrderStatus, HttpStatus.OK);
    }

    // Get PerpetualLine
    @ApiOperation(response = Optional.class, value = "Spark Find PerpetualLine ")
    @PostMapping("/perpetualline")
    public ResponseEntity<?> findPerpetualLine(@RequestBody SearchPerpetualLineV2 searchPerpetualLineV2) throws Exception {
        List<PerpetualLineV2> findPerpetualLine = impexService.findPerpetualLine(searchPerpetualLineV2);
        return new ResponseEntity<>(findPerpetualLine, HttpStatus.OK);
    }

    // Get PeriodicLine
    @ApiOperation(response = Optional.class, value = "Spark Find PeriodicLine ")
    @PostMapping("/periodicline")
    public ResponseEntity<?> findPeriodicLine(@RequestBody FindPeriodicLineV2 findPeriodicLineV2) throws Exception {
        List<PeriodicLineV2> findPeriodicLine = impexService.findPeriodicLine(findPeriodicLineV2);
        return new ResponseEntity<>(findPeriodicLine, HttpStatus.OK);
    }

    // Get PreOutBoundLine
    @ApiOperation(response = PreOutBoundLineV4.class, value = "Spark Find PreOutBoundLine ")
    @PostMapping("/preoutboundline")
    public ResponseEntity<?> findPreOutBoundLine(@RequestBody FindPreOutBoundLineV2 findPeriodicLineV2) throws Exception {
        List<PreOutBoundLineV4> findPeriodicLine = impexService.findPreOutBoundLine(findPeriodicLineV2);
        return new ResponseEntity<>(findPeriodicLine, HttpStatus.OK);
    }

    //Find QualityLine
    @ApiOperation(response = QualityLineV4.class, value = "Spark FindQualityLine")
    @PostMapping("/qualityline")
    public ResponseEntity<?> qualityLineV2(@RequestBody FindQualityLineV2 findQualityLineV2) throws Exception {
        List<QualityLineV4> qualityLineV2 = impexService.findQualityLineV2(findQualityLineV2);
        return new ResponseEntity<>(qualityLineV2, HttpStatus.OK);
    }

    //InboundLine
    @ApiOperation(response = InboundLineV4.class, value = "SparkInboundLine")
    @PostMapping("/inboundLine/new")
    public ResponseEntity<?> getInboundLineV3(@RequestBody com.mnrclara.spark.core.model.Almailem.FindInboundLineV2 findInboundLineV2) {
        List<InboundLineV4> putAwayLines =  impexService.getInboundLine(findInboundLineV2);
        return new ResponseEntity<>(putAwayLines, HttpStatus.OK);
    }

    // PickupLine
    @ApiOperation(response = PickUpLineV4.class, value = "SparkPickUpLineV3Service")
    @PostMapping("/pickuplinev3/new")
    public ResponseEntity<?> getPickupLineV3(@RequestBody com.mnrclara.spark.core.model.Almailem.SearchPickupLineV2 searchPickupLineV2) {
        List<PickUpLineV4> pickUpLineV3s = impexService.getPickupLine(searchPickupLineV2);
        return new ResponseEntity<>(pickUpLineV3s, HttpStatus.OK);
    }

    @ApiOperation(response = com.mnrclara.spark.core.model.Almailem.OutboundLineV2.class, value = "SparkOutbound")
    @PostMapping("/outboundv3/new")
    public ResponseEntity<?> getOutboundLine(@RequestBody FindOutboundLineV2 findOutBoundHeader) {
        List<com.mnrclara.spark.core.model.Almailem.OutboundLineV2> outboundLineV2s = impexService.getOutBoundLineV3(findOutBoundHeader);
        return new ResponseEntity<>(outboundLineV2s, HttpStatus.OK);
    }

    @ApiOperation(response = GrLineV3.class, value = "SparkGrLineV3Service")
    @PostMapping("/grline/v3")
    public ResponseEntity<?> getGrLine(@RequestBody com.mnrclara.spark.core.model.Almailem.FindGrLineV2 findGrLineV2) {
        List<GrLineV3> findGrLine = impexService.findGrLine(findGrLineV2);
        return new ResponseEntity<>(findGrLine, HttpStatus.OK);
    }

    // Outbound Order Summary Report
    @ApiOperation(response = OutboundHeaderV3.class, value = "Spark Outbound Order Summary Report")
    @PostMapping("/obHeader/obOrderSummaryReport")
    public ResponseEntity<?> obOrderSummaryReport(@RequestBody com.mnrclara.spark.core.model.Almailem.FindOutBoundHeaderV2 findOutBoundHeaderV2) {
        List<OutboundHeaderV3> outboundHeaderV3List = impexService.findOutboundOrderSummaryReport(findOutBoundHeaderV2);
        return new ResponseEntity<>(outboundHeaderV3List, HttpStatus.OK);
    }
}