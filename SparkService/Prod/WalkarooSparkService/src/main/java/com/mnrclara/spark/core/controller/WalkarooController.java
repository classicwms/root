package com.mnrclara.spark.core.controller;

import com.mnrclara.spark.core.model.Almailem.*;
import com.mnrclara.spark.core.model.Almailem.joinspark.OutboundHeaderV3;
import com.mnrclara.spark.core.model.impex.InhouseTransferHeaderV4;
import com.mnrclara.spark.core.model.impex.SearchInhouseTransferHeader;
import com.mnrclara.spark.core.model.walkaroo.*;
import com.mnrclara.spark.core.model.walkaroo.InboundLineV3;
import com.mnrclara.spark.core.model.walkaroo.InventoryV3;
import com.mnrclara.spark.core.model.wmscorev2.*;
import com.mnrclara.spark.core.model.wmscorev2.ContainerReceiptV2;
import com.mnrclara.spark.core.model.wmscorev2.FindContainerReceiptV2;
import com.mnrclara.spark.core.model.wmscorev2.FindGrLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindInboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindInboundLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindInventoryV2;
import com.mnrclara.spark.core.model.wmscorev2.FindOrderManagementLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindOutBoundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindOutBoundReversalV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPeriodicHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPeriodicLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPickupHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPreInboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPreOutBoundLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPreOutboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindPutAwayLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindQualityHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindQualityLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindStagingHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.FindStagingLineV2;
import com.mnrclara.spark.core.model.wmscorev2.FindStockReport;
import com.mnrclara.spark.core.model.wmscorev2.GrHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.ImBasicData1;
import com.mnrclara.spark.core.model.wmscorev2.InboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.InboundLineV2;
import com.mnrclara.spark.core.model.wmscorev2.InhouseTransferLine;
import com.mnrclara.spark.core.model.wmscorev2.InventoryMovementV2;
import com.mnrclara.spark.core.model.wmscorev2.InventoryV2;
import com.mnrclara.spark.core.model.wmscorev2.OrderManagementLineV2;
import com.mnrclara.spark.core.model.wmscorev2.OrderStatusReport;
import com.mnrclara.spark.core.model.wmscorev2.OutBoundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.OutBoundReversalV2;
import com.mnrclara.spark.core.model.wmscorev2.PeriodicHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PeriodicLineV2;
import com.mnrclara.spark.core.model.wmscorev2.PerpetualHeader;
import com.mnrclara.spark.core.model.wmscorev2.PerpetualLineV2;
import com.mnrclara.spark.core.model.wmscorev2.PickupHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PickupLine;
import com.mnrclara.spark.core.model.wmscorev2.PreInboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PreOutboundHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.PutAwayLineV2;
import com.mnrclara.spark.core.model.wmscorev2.QualityHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.QualityLineV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchGrHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchImBasicData1;
import com.mnrclara.spark.core.model.wmscorev2.SearchInhouseTransferLine;
import com.mnrclara.spark.core.model.wmscorev2.SearchInventoryMovementV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchOrderStatusReport;
import com.mnrclara.spark.core.model.wmscorev2.SearchPerpetualHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchPerpetualLineV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchPickupLineV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchPutAwayHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.SearchStorageBin;
import com.mnrclara.spark.core.model.wmscorev2.StagingHeaderV2;
import com.mnrclara.spark.core.model.wmscorev2.StagingLineV2;
import com.mnrclara.spark.core.model.wmscorev2.StockReport;
import com.mnrclara.spark.core.model.wmscorev2.StorageBin;
import com.mnrclara.spark.core.service.WalkarooService;
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
@Api(tags = {"Walkaroo"}, value = "Walkaroo Operations related to WalkarooController")
@SwaggerDefinition(tags = {@Tag(name = "User", description = "Operations related to Walkaroo")})
@RequestMapping("/walkaroo/prod")
public class WalkarooController {

    @Autowired
    WalkarooService walkarooService;

    //=======================================================================================================================

    //Find BusinessPartnerService
    @ApiOperation(response = BusinessPartnerV2.class, value = "Spark BusinessPartner")
    @PostMapping("/businessPartner")
    public ResponseEntity<?> businessPartner(@RequestBody FindBusinessPartner findBusinessPartner) throws Exception {
        List<BusinessPartnerV2> businessPartnerV2s = walkarooService.findBusinessPartner(findBusinessPartner);
        return new ResponseEntity<>(businessPartnerV2s, HttpStatus.OK);
    }

    //Find StorageBin
    @ApiOperation(response = StorageBin.class, value = "Spark Find StorageBin")
    @PostMapping("/storagebin")
    public ResponseEntity<?> searchStorageBin(@RequestBody SearchStorageBin searchStorageBin) throws Exception {
        List<StorageBin> storageBinList = walkarooService.findStorageBin(searchStorageBin);
        return new ResponseEntity<>(storageBinList, HttpStatus.OK);
    }

    @ApiOperation(response = ImBasicData1.class, value = "Spark ImBasicData1")
    @PostMapping("/imbasicdata1")
    public ResponseEntity<?> findImBasicData1(SearchImBasicData1 findImBasicData1) throws Exception {
        List<ImBasicData1> imBasicData1List = walkarooService.searchImBasicData1(findImBasicData1);
        return new ResponseEntity<>(imBasicData1List, HttpStatus.OK);
    }

    //Find ImBasicData1
    @ApiOperation(response = ImBasicData1V3.class, value = "Spark ImBasicData1V3")
    @PostMapping("/imbasicdata1v3")
    public ResponseEntity<?> findImBasicData1V3(@RequestBody SearchImBasicData1 searchImBasicData1) throws Exception {
        List<ImBasicData1V3> imBasicData1V3s = walkarooService.searchImBasicData1V3(searchImBasicData1);
        return new ResponseEntity<>(imBasicData1V3s, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Spark FindPreInboundHeader")
    @PostMapping("/preinboundHeader")
    public ResponseEntity<?> findPreinboundHeaderv2(@RequestBody FindPreInboundHeaderV2 findPreInboundHeaderV2) throws Exception {
        List<PreInboundHeaderV2> preInboundHeaderv2 = walkarooService.findPreInboundHeaderv2(findPreInboundHeaderV2);
        return new ResponseEntity<>(preInboundHeaderv2, HttpStatus.OK);
    }

    @ApiOperation(response = Optional.class, value = "Spark FindContainerReceipt")
    @PostMapping("/containerReceipt")
    public ResponseEntity<?> findContainerReceiptV2(@RequestBody FindContainerReceiptV2 findContainerReceiptV2) throws Exception {
        List<ContainerReceiptV2> containerReceiptV2 = walkarooService.findContainerReceipt(findContainerReceiptV2);
        return new ResponseEntity<>(containerReceiptV2, HttpStatus.OK);
    }
    
    @ApiOperation(response = Optional.class, value = "Spark Find GrHeader")
    @PostMapping("/grheader")
    public ResponseEntity<?> findGrHeaderV2(@RequestBody SearchGrHeaderV2 searchGrHeaderV2) throws Exception {
        List<GrHeaderV2> findGrHeaderV2 = walkarooService.findGrHeaderV2(searchGrHeaderV2);
        return new ResponseEntity<>(findGrHeaderV2, HttpStatus.OK);
    }

    //Find PutAwayHeader
    @ApiOperation(response = Optional.class, value = "Spark Find PutAwayHeader")
    @PostMapping("/putawayheader")
    public ResponseEntity<?> findPutawayHeaderV2(@RequestBody SearchPutAwayHeaderV2 searchPutAwayHeaderV2) throws Exception {
        List<PutAwayHeaderV3> findPutAwayHeaderV2 = walkarooService.findPutAwayHeaderV2(searchPutAwayHeaderV2);
        return new ResponseEntity<>(findPutAwayHeaderV2, HttpStatus.OK);
    }

    //Find StagingHeader
    @ApiOperation(response = StagingHeaderV2.class, value = "Spark Find StagingHeader")
    @PostMapping("/stagingheader")
    public ResponseEntity<?> searchStagingHeader(@RequestBody FindStagingHeaderV2 findStagingHeader) throws Exception {
        List<StagingHeaderV2> stList = walkarooService.findStagingHeader(findStagingHeader);
        return new ResponseEntity<>(stList, HttpStatus.OK);
    }

    @ApiOperation(response = InboundHeaderV2.class, value = "Spark InboundHeader")
    @PostMapping("/inboundHeader")
    public ResponseEntity<?> findInboundHeader(@RequestBody FindInboundHeaderV2 findInboundHeader) throws Exception {
        List<InboundHeaderV2> inboundHeaderList = walkarooService.findInboundHeader(findInboundHeader);
        return new ResponseEntity<>(inboundHeaderList, HttpStatus.OK);
    }

    // Find InboundLine
    @ApiOperation(response = InboundLineV2.class, value = "Spark Find InboundLine")
    @PostMapping("/inboundline")
    public ResponseEntity<?> searchInboundLine(@RequestBody FindInboundLineV2 findInboundLineV2) throws Exception {
        List<InboundLineV3> inboundLineList = walkarooService.findInboundLineV2(findInboundLineV2);
        return new ResponseEntity<>(inboundLineList, HttpStatus.OK);
    }

    //Get StagingLine
    @ApiOperation(response = StagingLineV2.class, value = "Spark FindStagingLine")
    @PostMapping("/stagingLine")
    public ResponseEntity<?> findStagingLineV2(@RequestBody FindStagingLineV2 findStagingLineV2) throws Exception {
        List<StagingLineV3> stagingLineV2 = walkarooService.findStagingLineV2(findStagingLineV2);
        return new ResponseEntity<>(stagingLineV2, HttpStatus.OK);
    }

    //Get GrLine
    @ApiOperation(response = com.mnrclara.spark.core.model.walkaroo.GrLineV3.class, value = "Spark FindGrLine")
    @PostMapping("/grline")
    public ResponseEntity<?> findGrLineV2(@RequestBody FindGrLineV2 findGrLineV2) throws Exception {
        List<com.mnrclara.spark.core.model.walkaroo.GrLineV3> grLineV2 = walkarooService.findGrLineV2(findGrLineV2);
        return new ResponseEntity<>(grLineV2, HttpStatus.OK);
    }

    // PutAwayLine
    @ApiOperation(response = PutAwayLineCoreV3.class, value = "Spark PutAwayLine")
    @PostMapping("/putawayline/new")
    public ResponseEntity<?> putAwayLineSpark(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        List<PutAwayLineCoreV3> putAwayLineCoreList = walkarooService.getPutAwayLine(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineCoreList, HttpStatus.OK);
    }

    // PutAwayLineV2
    @ApiOperation(response = PutAwayLineV2.class, value = "Spark PutAwayLineV2")
    @PostMapping("/putawayline")
    public ResponseEntity<?> putAwayLineSparkV2(@RequestBody FindPutAwayLineV2 findPutAwayLineV2) throws Exception {
        List<PutAwayLineV3> putAwayLineCoreList = walkarooService.findPutAwayLineV2(findPutAwayLineV2);
        return new ResponseEntity<>(putAwayLineCoreList, HttpStatus.OK);
    }

    /*================================================OUTBOUND=======================================================*/

    //Find PreOutboundHeader
    @ApiOperation(response = PreOutboundHeaderV2.class, value = "Spark Find PreOutboundHeader")
    @PostMapping("/preoutboundheader")
    public ResponseEntity<?> searchPreOutboundHeader(@RequestBody FindPreOutboundHeaderV2 findPreObHeader) throws Exception {
        List<PreOutboundHeaderV2> preObList = walkarooService.findPreOutboundHeader(findPreObHeader);
        return new ResponseEntity<>(preObList, HttpStatus.OK);
    }

    //Find OrderManagementLineV2
    @ApiOperation(response = OrderManagementLineV2.class, value = "Spark Find OrderManagementLine")
    @PostMapping("/ordermanagementline")
    public ResponseEntity<?> searchOrderManagementLineV2(@RequestBody FindOrderManagementLineV2 findOrderManagementLine)
            throws Exception {
        List<OrderManagementLineV3> omlList = walkarooService.findOrderManagementLine(findOrderManagementLine);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    // PickupHeaderV2
    @ApiOperation(response = PickupHeaderV2.class, value = "Spark Find PickupHeader")
    @PostMapping("/pickupheader")
    public ResponseEntity<?> findPickupHeaderV2(@RequestBody FindPickupHeaderV2 findPickupHeaderV2) throws Exception {
        List<PickupHeaderV3> pickupHeaderV2List = walkarooService.findPickupHeaderV2(findPickupHeaderV2);
        return new ResponseEntity<>(pickupHeaderV2List, HttpStatus.OK);
    }

    // QualityHeaderV2
    @ApiOperation(response = QualityHeaderV2.class, value = "Spark Find QualityHeader")
    @PostMapping("/qualityheader")
    public ResponseEntity<?> findQualityHeaderV2(@RequestBody FindQualityHeaderV2 findQualityHeaderV2) throws Exception {
        List<QualityHeaderV3> qualityHeaderV2List = walkarooService.findQualityHeaderV2(findQualityHeaderV2);
        return new ResponseEntity<>(qualityHeaderV2List, HttpStatus.OK);
    }

    // QualityHeaderV2
    @ApiOperation(response = OutBoundHeaderV2.class, value = "Spark Find OutBoundHeader")
    @PostMapping("/outboundheader")
    public ResponseEntity<?> findOutboundHeaderV2(@RequestBody FindOutBoundHeaderV2 findOutBoundHeaderV2) throws Exception {
        List<OutBoundHeaderV2> outBoundHeaderV2List = walkarooService.findOutBoundHeaderV2(findOutBoundHeaderV2);
        return new ResponseEntity<>(outBoundHeaderV2List, HttpStatus.OK);
    }

    //Find OutBoundReversal
    @ApiOperation(response = OutBoundReversalV2.class, value = "Spark Find OutBoundReversal")
    @PostMapping("/outboundreversal")
    public ResponseEntity<?> searchOutboundReversal(@RequestBody FindOutBoundReversalV2 findOutBoundReversalV2)
            throws Exception {
        List<OutBoundReversalV2> omlList = walkarooService.findOutBoundReversal(findOutBoundReversalV2);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find PickupLine
    @ApiOperation(response = PickupLine.class, value = "Spark Find PickUpLine")
    @PostMapping("/pickupline")
    public ResponseEntity<?> searchPickUpLine(@RequestBody SearchPickupLineV2 searchStorageBin)
            throws Exception {
        List<PickupLineV3> omlList = walkarooService.findPickupLines(searchStorageBin);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find InventoryMovement
    @ApiOperation(response = InventoryMovementV2.class, value = "Spark Find InventoryMovement")
    @PostMapping("/inventorymovement")
    public ResponseEntity<?> searchInventoryMovement(@RequestBody SearchInventoryMovementV2 searchInventoryMovementV2)
            throws Exception {
        List<InventoryMovementV2> omlList = walkarooService.findInventoryMovement(searchInventoryMovementV2);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    //Find InhouseTransferLine
    @ApiOperation(response = InhouseTransferLine.class, value = "Spark Find InhouseTransferLine")
    @PostMapping("/inhousetransferline")
    public ResponseEntity<?> searchInhouseTransferLine(@RequestBody SearchInhouseTransferLine searchInhouseTransferLine)
            throws Exception {
        List<InhouseTransferLine> omlList = walkarooService.findInhouseTransferLines(searchInhouseTransferLine);
        return new ResponseEntity<>(omlList, HttpStatus.OK);
    }

    // Find InventoryV2
    @ApiOperation(response = InventoryV3.class, value = "Spark Find Inventory")
    @PostMapping("/inventory/new")
    public ResponseEntity<?> searchInventoryV2(@RequestBody FindInventoryV2 findInventoryV2) throws Exception {
        List<InventoryV3> inventoryList = walkarooService.findInventoryV2(findInventoryV2);
        return new ResponseEntity<>(inventoryList, HttpStatus.OK);
    }

    // Find PeriodicHeader
    @ApiOperation(response = PeriodicHeaderV2.class, value = "Spark Find PeriodicHeader")
    @PostMapping("/periodicheader")
    public ResponseEntity<?> searchPeriodicHeaderV2(@RequestBody FindPeriodicHeaderV2 findPeriodicHeaderV2) throws Exception {
        List<PeriodicHeaderV2> periodicHeaderList = walkarooService.findPeriodicHeaderV2(findPeriodicHeaderV2);
        return new ResponseEntity<>(periodicHeaderList, HttpStatus.OK);
    }

    // Find PerpetualHeader
    @ApiOperation(response = PerpetualHeader.class, value = "Spark Find PerpetualHeader")
    @PostMapping("/perpetualheader")
    public ResponseEntity<?> searchPerpetualHeader(@RequestBody SearchPerpetualHeaderV2 searchPerpetualHeaderV2) throws Exception {
        List<PerpetualHeader> perpetualHeaderList = walkarooService.findPerpetualHeader(searchPerpetualHeaderV2);
        return new ResponseEntity<>(perpetualHeaderList, HttpStatus.OK);
    }

    // Get InhouseTransferHeader
    @ApiOperation(response = InhouseTransferHeaderV4.class, value = "Spark Find InhouseTransferHeader")
    @PostMapping("/inhousetransferheader")
    public ResponseEntity<?> searchInhouseTransferHeader(@RequestBody SearchInhouseTransferHeader searchInhouseTransferHeader) throws Exception {
        List<InhouseTransferHeaderV4> inboundLineList = walkarooService.findInhouseTransferHeader(searchInhouseTransferHeader);
        return new ResponseEntity<>(inboundLineList, HttpStatus.OK);
    }

    // Get StockReport
    @ApiOperation(response = StockReport.class, value = "Spark Get StockReport")
    @PostMapping("/stockreport")
    public ResponseEntity<?> getStockReport(FindStockReport findStockReport) throws Exception {
        List<StockReport> stockReportList = walkarooService.getStockReport(findStockReport);
        return new ResponseEntity<>(stockReportList, HttpStatus.OK);
    }

    // Get OrderStatusReport
    @ApiOperation(response = Optional.class, value = "Spark Get Order Status Report")
    @PostMapping("/orderstatusreport")
    public ResponseEntity<?> findOrderStatus(@RequestBody SearchOrderStatusReport searchOrderStatusReport) throws Exception {
        List<OrderStatusReport> findOrderStatus = walkarooService.findOrderStatusReport(searchOrderStatusReport);
        return new ResponseEntity<>(findOrderStatus, HttpStatus.OK);
    }

    // Get PerpetualLine
    @ApiOperation(response = Optional.class, value = "Spark Find PerpetualLine ")
    @PostMapping("/perpetualline")
    public ResponseEntity<?> findPerpetualLine(@RequestBody SearchPerpetualLineV2 searchPerpetualLineV2) throws Exception {
        List<PerpetualLineV2> findPerpetualLine = walkarooService.findPerpetualLine(searchPerpetualLineV2);
        return new ResponseEntity<>(findPerpetualLine, HttpStatus.OK);
    }

    // Get PeriodicLine
    @ApiOperation(response = Optional.class, value = "Spark Find PeriodicLine ")
    @PostMapping("/periodicline")
    public ResponseEntity<?> findPeriodicLine(@RequestBody FindPeriodicLineV2 findPeriodicLineV2) throws Exception {
        List<PeriodicLineV2> findPeriodicLine = walkarooService.findPeriodicLine(findPeriodicLineV2);
        return new ResponseEntity<>(findPeriodicLine, HttpStatus.OK);
    }

    // Get PreOutBoundLine
    @ApiOperation(response = Optional.class, value = "Spark Find PreOutBoundLine ")
    @PostMapping("/preoutboundline")
    public ResponseEntity<?> findPreOutBoundLine(@RequestBody FindPreOutBoundLineV2 findPeriodicLineV2) throws Exception {
        List<PreOutBoundLineV3> findPeriodicLine = walkarooService.findPreOutBoundLine(findPeriodicLineV2);
        return new ResponseEntity<>(findPeriodicLine, HttpStatus.OK);
    }

    //Find QualityLine
    @ApiOperation(response = QualityLineV2.class, value = "Spark FindQualityLine")
    @PostMapping("/qualityline")
    public ResponseEntity<?> qualityLineV2(@RequestBody FindQualityLineV2 findQualityLineV2) throws Exception {
        List<QualityLineV3> qualityLineV2 = walkarooService.findQualityLineV2(findQualityLineV2);
        return new ResponseEntity<>(qualityLineV2, HttpStatus.OK);
    }

    //InboundLine
    @ApiOperation(response = com.mnrclara.spark.core.model.Almailem.InboundLineV3.class, value = "SparkInboundLine")
    @PostMapping("/inboundLine/new")
    public ResponseEntity<?> getInboundLineV3(@RequestBody com.mnrclara.spark.core.model.Almailem.FindInboundLineV2 findInboundLineV2) {
        List<InboundLineNewV3> putAwayLines =  walkarooService.getInboundLine(findInboundLineV2);
        return new ResponseEntity<>(putAwayLines, HttpStatus.OK);
    }

    // PickupLine
    @ApiOperation(response = PickUpLineV3.class, value = "SparkPickUpLineV3Service")
    @PostMapping("/pickuplinev3/new")
    public ResponseEntity<?> getPickupLineV3(@RequestBody com.mnrclara.spark.core.model.Almailem.SearchPickupLineV2 searchPickupLineV2) {
        List<PickUpLineV3> pickUpLineV3s = walkarooService.getPickupLine(searchPickupLineV2);
        return new ResponseEntity<>(pickUpLineV3s, HttpStatus.OK);
    }

    @ApiOperation(response = com.mnrclara.spark.core.model.Almailem.OutboundLineV2.class, value = "SparkOutbound")
    @PostMapping("/outboundv3/new")
    public ResponseEntity<?> getOutboundLine(@RequestBody FindOutboundLineV2 findOutBoundHeader) {
        List<com.mnrclara.spark.core.model.Almailem.OutboundLineV2> outboundLineV2s = walkarooService.getOutBoundLineV3(findOutBoundHeader);
        return new ResponseEntity<>(outboundLineV2s, HttpStatus.OK);
    }

    @ApiOperation(response = GrLineV3.class, value = "SparkGrLineV3Service")
    @PostMapping("/grline/v3")
    public ResponseEntity<?> getGrLine(@RequestBody com.mnrclara.spark.core.model.Almailem.FindGrLineV2 findGrLineV2) {
        List<GrLineV3New> findGrLine = walkarooService.findGrLine(findGrLineV2);
        return new ResponseEntity<>(findGrLine, HttpStatus.OK);
    }

    // Outbound Order Summary Report
    @ApiOperation(response = OutboundHeaderV3.class, value = "Spark Outbound Order Summary Report")
    @PostMapping("/obHeader/obOrderSummaryReport")
    public ResponseEntity<?> obOrderSummaryReport(@RequestBody com.mnrclara.spark.core.model.Almailem.FindOutBoundHeaderV2 findOutBoundHeaderV2) {
        List<OutboundHeaderV3> outboundHeaderV3List = walkarooService.findOutboundOrderSummaryReport(findOutBoundHeaderV2);
        return new ResponseEntity<>(outboundHeaderV3List, HttpStatus.OK);
    }
}