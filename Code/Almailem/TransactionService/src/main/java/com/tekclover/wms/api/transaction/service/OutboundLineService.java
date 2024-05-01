package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.config.PropertiesConfig;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AXAuthToken;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.impl.OrderStatusReportImpl;
import com.tekclover.wms.api.transaction.model.impl.OutBoundLineImpl;
import com.tekclover.wms.api.transaction.model.impl.ShipmentDispatchSummaryReportImpl;
import com.tekclover.wms.api.transaction.model.impl.StockMovementReportImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.InboundReversalInput;
import com.tekclover.wms.api.transaction.model.integration.IntegrationApiResponse;
import com.tekclover.wms.api.transaction.model.outbound.*;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.UpdateOrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.AddOutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.OutboundReversal;
import com.tekclover.wms.api.transaction.model.outbound.outboundreversal.v2.OutboundReversalV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineOutput;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.SearchOutboundLineV2;
import com.tekclover.wms.api.transaction.model.report.FindImBasicData1;
import com.tekclover.wms.api.transaction.model.report.SearchOrderStatusReport;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport;
import com.tekclover.wms.api.transaction.model.report.StockMovementReport1;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.confirmation.AXApiResponse;
import com.tekclover.wms.api.transaction.model.warehouse.outbound.confirmation.*;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class OutboundLineService extends BaseService {
    @Autowired
    private OutboundLineInterimRepository outboundLineInterimRepository;
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    private OutboundLineRepository outboundLineRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private OutboundHeaderService outboundHeaderService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private QualityHeaderService qualityHeaderService;

    @Autowired
    private QualityLineService qualityLineService;

    @Autowired
    private QualityLineRepository qualityLineRepository;

    @Autowired
    private PickupLineService pickupLineService;

    @Autowired
    private PickupHeaderService pickupHeaderService;

    @Autowired
    private PickupLineRepository pickupLineRepository;

    @Autowired
    private OrderManagementLineService orderManagementLineService;

    @Autowired
    private OutboundReversalService outboundReversalService;

    @Autowired
    private PreOutboundHeaderRepository preOutboundHeaderRepository;

    @Autowired
    private PreOutboundLineRepository preOutboundLineRepository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private OrderManagementLineRepository orderManagementLineRepository;

    @Autowired
    private IntegrationApiResponseRepository integrationApiResponseRepository;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    private StockMovementReport1Repository stockMovementReport1Repository;

    @Autowired
    private PropertiesConfig propertiesConfig;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    private PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    private PickupLineV2Repository pickupLineV2Repository;
    @Autowired
    private QualityLineV2Repository qualityLineV2Repository;
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;

    String statusDescription = null;
    //------------------------------------------------------------------------------------------------------

    /**
     * getOutboundLines
     *
     * @return
     */
    public List<OutboundLine> getOutboundLines() {
        List<OutboundLine> outboundLineList = outboundLineRepository.findAll();
        outboundLineList = outboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return outboundLineList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public List<OutboundLine> getOutboundLine(String warehouseId, String preOutboundNo,
                                              String refDocNumber, String partnerCode) {
        List<OutboundLine> outboundLine =
                outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + ", partnerCode : " + partnerCode
                    + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param statusIds
     * @return
     */
    public long getOutboundLine(String warehouseId, String preOutboundNo,
                                String refDocNumber, String partnerCode, List<Long> statusIds) {
        long outboundLineCount =
                outboundLineRepository.getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0);
        return outboundLineCount;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param statusIds
     * @return
     */
    public List<OutboundLine> findOutboundLineByStatus(String warehouseId, String preOutboundNo,
                                                       String refDocNumber, String partnerCode, List<Long> statusIds) {
        List<OutboundLine> outboundLine;
        try {
            outboundLine = outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
                    warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0L);
            if (outboundLine == null) {
                throw new BadRequestException("The given OutboundLine ID : "
                        + "warehouseId : " + warehouseId
                        + ", preOutboundNo : " + preOutboundNo
                        + ", refDocNumber : " + refDocNumber
                        + ", partnerCode : " + partnerCode
                        + " doesn't exist.");
            }
            return outboundLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLine> getOutboundLine(String warehouseId, String preOutboundNo,
                                              String refDocNumber) {
        List<OutboundLine> outboundLine =
                outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (!outboundLine.isEmpty()) {
            return outboundLine;
        } else {
            return null;
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLine> getOutboundLineForReports(String warehouseId, String preOutboundNo,
                                                        String refDocNumber) {
        List<OutboundLine> outboundLine =
                outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getCountofOrderedLines(String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> countofOrderedLines = outboundLineRepository.getCountofOrderedLines(warehouseId, preOutboundNo, refDocNumber);
        return countofOrderedLines;
    }

    /**
     * getSumOfOrderedQty
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getSumOfOrderedQty(String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> sumOfOrderedQty = outboundLineRepository.getSumOfOrderedQty(warehouseId, preOutboundNo, refDocNumber);
        return sumOfOrderedQty;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public Long getSumOfOrderedQtyByPartnerCode(String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
        Long sumOfOrderedQty = outboundLineRepository.getSumOfOrderedQtyByPartnerCode(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        return sumOfOrderedQty;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getDeliveryLines(String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> deliveryLines = outboundLineRepository.getDeliveryLines(warehouseId, preOutboundNo, refDocNumber);
        return deliveryLines;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getDeliveryQty(String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> deliveryQtyList = outboundLineRepository.getDeliveryQty(warehouseId, preOutboundNo, refDocNumber);
        return deliveryQtyList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public Long getDeliveryQtyByPartnerCode(String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
        Long deliveryQty = outboundLineRepository.getDeliveryQtyByPartnerCode(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        return deliveryQty;
    }

    /**
     * @param preOBNo
     * @param obLineNo
     * @param itemCode
     * @return
     */
    public List<Long> getLineShipped(String preOBNo, Long obLineNo, String itemCode) {
        List<Long> lineShippedList = outboundLineRepository.findLineShipped(preOBNo, obLineNo, itemCode);
        return lineShippedList;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public List<OutBoundLineImpl> getOutBoundLineDataForOutBoundHeader(List<String> refDocNumber) {
        List<OutBoundLineImpl> outBoundLines = outboundLineRepository.getOutBoundLineDataForOutBoundHeader(refDocNumber);
        return outBoundLines;
    }

    /**
     * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and
     * update SATATU_ID as 48
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     * @return
     */
    public OutboundLine getOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                        Long lineNumber, String itemCode) {
        OutboundLine outboundLine = outboundLineRepository.findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (outboundLine != null) {
            return outboundLine;
        }
        throw new BadRequestException("The given OutboundLine ID : " +
                "warehouseId:" + warehouseId +
                ",preOutboundNo:" + preOutboundNo +
                ",refDocNumber:" + refDocNumber +
                ",partnerCode:" + partnerCode +
                ",lineNumber:" + lineNumber +
                ",itemCode:" + itemCode +
                " doesn't exist.");
    }

    /**
     * @param refDocNo
     * @return
     */
    public List<Long> getLineItem_NByRefDocNoAndRefField2IsNull(List<String> refDocNo, Date startDate, Date endDate) {
        List<Long> lineItems =
                outboundLineRepository.findLineItem_NByRefDocNoAndRefField2IsNull(refDocNo, startDate, endDate);
        return lineItems;
    }

    /**
     * @param refDocNo
     * @return
     */
    public List<Long> getLineItem_NByRefDocNoAndRefField2IsNull(List<String> refDocNo) {
        List<Long> lineItems =
                outboundLineRepository.findLineItem_NByRefDocNoAndRefField2IsNull(refDocNo);
        return lineItems;
    }

    /**
     * @param refDocNo
     * @return
     */
    public List<Long> getShippedLines(List<String> refDocNo, Date startDate, Date endDate) {
        List<Long> lineItems = outboundLineRepository.findShippedLines(refDocNo, startDate, endDate);
        return lineItems;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundLine> findOutboundLine(SearchOutboundLine searchOutboundLine)
            throws ParseException, java.text.ParseException {

        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            OutboundLineSpecification spec = new OutboundLineSpecification(searchOutboundLine);
            List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findAll(spec);

            /*
             * Pass WH-ID/REF_DOC_NO/PRE_OB_NO/OB_LINE_NO/ITM_CODE
             * PickConfirmQty & QCQty - from QualityLine table
             */
            if (outboundLineSearchResults != null) {
                for (OutboundLine outboundLineSearchResult : outboundLineSearchResults) {
                    Double qcQty = qualityLineRepository.getQualityLineCount(outboundLineSearchResult.getWarehouseId(),
                            outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(),
                            outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("qcQty : " + qcQty);
                    if (qcQty != null) {
                        outboundLineSearchResult.setReferenceField10(String.valueOf(qcQty));
                    }

                    Double pickConfirmQty = pickupLineRepository.getPickupLineCount(outboundLineSearchResult.getWarehouseId(),
                            outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(),
                            outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("pickConfirmQty : " + pickConfirmQty);
                    if (pickConfirmQty != null) {
                        outboundLineSearchResult.setReferenceField9(String.valueOf(pickConfirmQty));
                    }
                }
            }
            return outboundLineSearchResults;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundLine> findOutboundLineNew(SearchOutboundLine searchOutboundLine)
            throws ParseException, java.text.ParseException {

        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            if (searchOutboundLine.getWarehouseId() == null || searchOutboundLine.getWarehouseId().isEmpty()) {
                searchOutboundLine.setWarehouseId(null);
            }
            if (searchOutboundLine.getPreOutboundNo() == null || searchOutboundLine.getPreOutboundNo().isEmpty()) {
                searchOutboundLine.setPreOutboundNo(null);
            }
            if (searchOutboundLine.getRefDocNumber() == null || searchOutboundLine.getRefDocNumber().isEmpty()) {
                searchOutboundLine.setRefDocNumber(null);
            }
            if (searchOutboundLine.getLineNumber() == null || searchOutboundLine.getLineNumber().isEmpty()) {
                searchOutboundLine.setLineNumber(null);
            }
            if (searchOutboundLine.getItemCode() == null || searchOutboundLine.getItemCode().isEmpty()) {
                searchOutboundLine.setItemCode(null);
            }
            if (searchOutboundLine.getStatusId() == null || searchOutboundLine.getStatusId().isEmpty()) {
                searchOutboundLine.setStatusId(null);
            }
            if (searchOutboundLine.getOrderType() == null || searchOutboundLine.getOrderType().isEmpty()) {
                searchOutboundLine.setOrderType(null);
            }
            if (searchOutboundLine.getPartnerCode() == null || searchOutboundLine.getPartnerCode().isEmpty()) {
                searchOutboundLine.setPartnerCode(null);
            }

            List<OutboundLine> outboundLineSearchResults = outboundLineRepository.findOutboundLineNew(searchOutboundLine.getWarehouseId(),
                    searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate(), searchOutboundLine.getPreOutboundNo(),
                    searchOutboundLine.getRefDocNumber(), searchOutboundLine.getLineNumber(), searchOutboundLine.getItemCode(),
                    searchOutboundLine.getStatusId(), searchOutboundLine.getOrderType(), searchOutboundLine.getPartnerCode());


            return outboundLineSearchResults;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @throws Exception
     */
//	@Scheduled(cron = "0 0/32 11 * * *")
    public void scheduleStockMovementReport() throws Exception {
        log.info("scheduleStockMovementReport---STARTED-------> : " + new Date());
        FindImBasicData1 searchImBasicData1 = new FindImBasicData1();
        Date dateFrom = DateUtils.convertStringToDateByYYYYMMDD("2022-06-20");
        Date dateTo = new Date();
        Date[] dates = DateUtils.addTimeToDatesForSearch(dateFrom, dateTo);
        searchImBasicData1.setWarehouseId(WAREHOUSE_ID_110);
        searchImBasicData1.setFromCreatedOn(dates[0]);
        searchImBasicData1.setToCreatedOn(dates[1]);

        ImBasicData1Specification spec = new ImBasicData1Specification(searchImBasicData1);
        List<ImBasicData1> resultsImBasicData1 = imbasicdata1Repository.findAll(spec);
        log.info("resultsImBasicData1 : " + resultsImBasicData1.size());

        List<String> itemCodeList = resultsImBasicData1.stream().map(ImBasicData1::getItemCode).collect(Collectors.toList());
        log.info("itemCodeList : " + itemCodeList.size());

        itemCodeList.stream().forEach(i -> {
            try {
                findLinesForStockMovementForScheduler(WAREHOUSE_ID_110, i, dates[0], dates[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        log.info("scheduleStockMovementReport---COMPLETED-------> : " + new Date());
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param fromDate
     * @param toDate
     * @throws ParseException
     */
    public void findLinesForStockMovementForScheduler(String warehouseId, String itemCode, Date fromDate, Date toDate)
            throws ParseException {
        try {
            List<StockMovementReportImpl> allLineData = new ArrayList<>();
            List<StockMovementReportImpl> outboundLineSearchResults =
                    outboundLineRepository.findOutboundLineForStockMovement(Arrays.asList(itemCode), Arrays.asList(warehouseId), 59L, fromDate, toDate);

            List<StockMovementReportImpl> inboundLineSearchResults =
                    inboundLineRepository.findInboundLineForStockMovement(Arrays.asList(itemCode), Arrays.asList(warehouseId), Arrays.asList(14L, 20L, 24L));

            allLineData.addAll(outboundLineSearchResults);
            allLineData.addAll(inboundLineSearchResults);

            List<StockMovementReport1> stockMovementReports = new ArrayList<>();
            allLineData.forEach(data -> {
                StockMovementReport1 stockMovementReport = new StockMovementReport1();
                BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
                if (data.getConfirmedOn() == null) {
                    Date date = inboundLineRepository.findDateFromPutawayLine(stockMovementReport.getDocumentNumber(), stockMovementReport.getItemCode());
                    stockMovementReport.setConfirmedOn(date);
                }
                stockMovementReports.add(stockMovementReport);
            });
            stockMovementReport1Repository.saveAll(stockMovementReports);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<StockMovementReport> findLinesForStockMovement(SearchOutboundLine searchOutboundLine)
            throws ParseException, java.text.ParseException {
        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            log.info("searchOutboundLine.getWarehouseId() :  " + searchOutboundLine.getWarehouseId());
            List<StockMovementReportImpl> allLineData = new ArrayList<>();
            List<StockMovementReportImpl> outboundLineSearchResults =
                    outboundLineRepository.findOutboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
                            59L, searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate());

            List<StockMovementReportImpl> inboundLineSearchResults =
                    inboundLineRepository.findInboundLineForStockMovement(searchOutboundLine.getItemCode(), searchOutboundLine.getWarehouseId(),
                            Arrays.asList(14L, 20L, 24L));

            allLineData.addAll(outboundLineSearchResults);
            allLineData.addAll(inboundLineSearchResults);

            List<StockMovementReport> stockMovementReports = new ArrayList<>();
            allLineData.forEach(data -> {
                StockMovementReport stockMovementReport = new StockMovementReport();
                BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
                if (data.getConfirmedOn() == null) {
                    Date date = inboundLineRepository.findDateFromPutawayLine(stockMovementReport.getDocumentNumber(), stockMovementReport.getItemCode());
                    stockMovementReport.setConfirmedOn(date);
                }
                stockMovementReports.add(stockMovementReport);
            });
            log.info("stockMovementReports :  " + stockMovementReports);
            stockMovementReports.sort(Comparator.comparing(StockMovementReport::getConfirmedOn));
            return stockMovementReports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<ShipmentDispatchSummaryReportImpl> findOutboundLineShipmentReport(SearchOutboundLine searchOutboundLine)
            throws ParseException, java.text.ParseException {

//		if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
//			Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
//					searchOutboundLine.getToDeliveryDate());
//			searchOutboundLine.setFromDeliveryDate(dates[0]);
//			searchOutboundLine.setToDeliveryDate(dates[1]);
//		}

        List<ShipmentDispatchSummaryReportImpl> outboundLineSearchResults =
                outboundLineRepository.getOrderLinesForShipmentDispatchReport(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate(), searchOutboundLine.getWarehouseId().get(0));

        return outboundLineSearchResults;
    }

    /**
     * @param searchOutboundLineReport
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundLine> findOutboundLineReport(SearchOutboundLineReport searchOutboundLineReport)
            throws ParseException, java.text.ParseException {
        if (searchOutboundLineReport.getStartConfirmedOn() != null && searchOutboundLineReport.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLineReport.getStartConfirmedOn(), searchOutboundLineReport.getEndConfirmedOn());
            searchOutboundLineReport.setStartConfirmedOn(dates[0]);
            searchOutboundLineReport.setEndConfirmedOn(dates[1]);
        }

        OutboundLineReportSpecification spec = new OutboundLineReportSpecification(searchOutboundLineReport);
        List<OutboundLine> results = outboundLineRepository.findAll(spec);
        log.info("results: " + results);
        return results;
    }

    /**
     * findOutboundLineOrderStatusReport
     *
     * @param searchOrderStatusReport
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OrderStatusReportImpl> findOutboundLineOrderStatusReport(SearchOrderStatusReport searchOrderStatusReport)
            throws ParseException, java.text.ParseException {
        if (searchOrderStatusReport.getFromDeliveryDate() != null && searchOrderStatusReport.getToDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderStatusReport.getFromDeliveryDate(),
                    searchOrderStatusReport.getToDeliveryDate());
            searchOrderStatusReport.setFromDeliveryDate(dates[0]);
            searchOrderStatusReport.setToDeliveryDate(dates[1]);
        }

        log.info("findOutboundLineOrderStatusReport: " + searchOrderStatusReport);
        List<OrderStatusReportImpl> results = outboundLineRepository.getOrderStatusReportFromOutboundLines(
                searchOrderStatusReport.getLanguageId(),
                searchOrderStatusReport.getCompanyCodeId(),
                searchOrderStatusReport.getPlantId(),
                searchOrderStatusReport.getWarehouseId(),
                searchOrderStatusReport.getItemCode(),
                searchOrderStatusReport.getFromDeliveryDate(),
                searchOrderStatusReport.getToDeliveryDate());
        log.info("OrderStatusReportImpl: " + results.size());
        return results;
    }


    /**
     * createOutboundLine
     *
     * @param newOutboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundLine createOutboundLine(AddOutboundLine newOutboundLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        OutboundLine dbOutboundLine = new OutboundLine();
        log.info("newOutboundLine : " + newOutboundLine);
        BeanUtils.copyProperties(newOutboundLine, dbOutboundLine);
        dbOutboundLine.setDeletionIndicator(0L);
        dbOutboundLine.setCreatedBy(loginUserID);
        dbOutboundLine.setUpdatedBy(loginUserID);
        dbOutboundLine.setCreatedOn(new Date());
        dbOutboundLine.setUpdatedOn(new Date());
        return outboundLineRepository.save(dbOutboundLine);
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLine> deliveryConfirmation(String warehouseId, String preOutboundNo, String refDocNumber,
                                                   String partnerCode, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        /*--------------------OutboundLine-Check---------------------------------------------------------------------------*/
        List<Long> statusIds = Arrays.asList(59L);
        long outboundLineProcessedCount = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds);
        log.info("outboundLineProcessedCount : " + outboundLineProcessedCount);
        boolean isAlreadyProcessed = (outboundLineProcessedCount > 0 ? true : false);
        log.info("outboundLineProcessed Already Processed? : " + isAlreadyProcessed);
        if (isAlreadyProcessed) {
            throw new BadRequestException("Order is already processed.");
        }

        /*--------------------OrderManagementLine-Check---------------------------------------------------------------------*/
        // OrderManagementLine checking for STATUS_ID - 42L, 43L
        long orderManagementLineCount = orderManagementLineService.getOrderManagementLine(warehouseId, refDocNumber, preOutboundNo, Arrays.asList(42L, 43L));
        boolean isConditionMet = (orderManagementLineCount > 0 ? true : false);
        log.info("orderManagementLineCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("OrderManagementLine is not completely Processed.");
        }

        /*--------------------PickupHeader-Check---------------------------------------------------------------------*/
        // PickupHeader checking for STATUS_ID - 48
        long pickupHeaderCount = pickupHeaderService.getPickupHeaderCountForDeliveryConfirmation(warehouseId, refDocNumber, preOutboundNo, 48L);
        isConditionMet = (pickupHeaderCount > 0 ? true : false);
        log.info("pickupHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Pickup is not completely Processed.");
        }

        // QualityHeader checking for STATUS_ID - 54
        long qualityHeaderCount = qualityHeaderService.getQualityHeaderCountForDeliveryConfirmation(warehouseId, refDocNumber, preOutboundNo, 54L);
        isConditionMet = (qualityHeaderCount > 0 ? true : false);
        log.info("qualityHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Quality check is not completely Processed.");
        }

        //----------------------------------------------------------------------------------------------------------
        List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L, 41L);
        long outboundLineListCount = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIdsToBeChecked);
        log.info("outboundLineListCount : " + outboundLineListCount);
        isConditionMet = (outboundLineListCount > 0 ? true : false);
        log.info("isConditionMet : " + isConditionMet);

        AXApiResponse axapiResponse = null;
        if (!isConditionMet) {
            throw new BadRequestException("OutboundLine: Order is not completely Processed.");
        } else {
            log.info("Order can be Processed.");
            /*
             * Call this respective API end points when REF_DOC_NO is confirmed with STATUS_ID = 59 in OUTBOUNDHEADER and
             * OUTBOUNDLINE tables and based on OB_ORD_TYP_ID as per API document
             */
            OutboundHeader confirmedOutboundHeader = outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber);
            List<OutboundLine> confirmedOutboundLines = getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
            log.info("OutboundOrderTypeId : " + confirmedOutboundHeader.getOutboundOrderTypeId());
            log.info("confirmedOutboundLines: " + confirmedOutboundLines);

            /*---------------------AXAPI-integration----------------------------------------------------------*/

            // if OB_ORD_TYP_ID = 0 in OUTBOUNDHEADER table - call Shipment Confirmation
            if (confirmedOutboundHeader.getOutboundOrderTypeId() == 0L && confirmedOutboundLines != null) {
                axapiResponse = postShipment(confirmedOutboundHeader, confirmedOutboundLines);
                log.info("AXApiResponse: " + axapiResponse);
            }

            // if OB_ORD_TYP_ID = 1 in OUTBOUNDHEADER table - Interwarehouse Shipment Confirmation
            if (confirmedOutboundHeader.getOutboundOrderTypeId() == 1L && confirmedOutboundLines != null) {
                axapiResponse = postInterwarehouseShipment(confirmedOutboundHeader, confirmedOutboundLines);
                log.info("AXApiResponse: " + axapiResponse);
            }

            //  if OB_ORD_TYP_ID = 2 in OUTBOUNDHEADER table - Return PO Confirmation
            if (confirmedOutboundHeader.getOutboundOrderTypeId() == 2L && confirmedOutboundLines != null) {
                axapiResponse = postReturnPO(confirmedOutboundHeader, confirmedOutboundLines);
                log.info("AXApiResponse: " + axapiResponse);
            }

            // if OB_ORD_TYP_ID = 3 in OUTBOUNDHEADER table - Sale Order Confirmation - True Express
            if (confirmedOutboundHeader.getOutboundOrderTypeId() == 3L && confirmedOutboundLines != null) {
                axapiResponse = postSalesOrder(confirmedOutboundHeader, confirmedOutboundLines);
                log.info("AXApiResponse: " + axapiResponse);
            }
        }

        if (axapiResponse.getStatusCode() != null && axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
            try {
                Long STATUS_ID_59 = 59L;
                List<Long> statusId57 = Arrays.asList(57L);
                AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
                List<OutboundLine> outboundLineByStatus57List = findOutboundLineByStatus(warehouseId, preOutboundNo, refDocNumber, partnerCode, statusId57);

                // ----------------OoutboundLine update-----------------------------------------------------------------------------------------
                List<Long> lineNumbers = outboundLineByStatus57List.stream().map(OutboundLine::getLineNumber).collect(Collectors.toList());
                List<String> itemCodes = outboundLineByStatus57List.stream().map(OutboundLine::getItemCode).collect(Collectors.toList());

                outboundLineRepository.updateOutboundLineStatus(warehouseId, refDocNumber, STATUS_ID_59, lineNumbers);
                log.info("OutboundLine updated ");

                //----------------Outbound Header update----------------------------------------------------------------------------------------
                outboundHeaderRepository.updateOutboundHeaderStatus(warehouseId, refDocNumber, STATUS_ID_59, new Date());
                OutboundHeader isOrderConfirmedOutboundHeader = outboundHeaderService.getOutboundHeader(warehouseId, preOutboundNo, refDocNumber);
                log.info("OutboundHeader updated----1---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
                if (isOrderConfirmedOutboundHeader.getStatusId() != 59L) {
                    log.info("OutboundHeader is still updated not updated.");
                    log.info("Updating again OutboundHeader.");
                    isOrderConfirmedOutboundHeader.setStatusId(STATUS_ID_59);
                    isOrderConfirmedOutboundHeader.setUpdatedBy(loginUserID);
                    isOrderConfirmedOutboundHeader.setUpdatedOn(new Date());
                    isOrderConfirmedOutboundHeader.setDeliveryConfirmedOn(new Date());
                    outboundHeaderRepository.saveAndFlush(isOrderConfirmedOutboundHeader);
                    log.info("OutboundHeader updated---2---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
                }

                //----------------Preoutbound Line----------------------------------------------------------------------------------------------
                preOutboundLineRepository.updatePreOutboundLineStatus(warehouseId, refDocNumber, STATUS_ID_59);
                log.info("PreOutbound Line updated");

                //----------------Preoutbound Header--------------------------------------------------------------------------------------------
                StatusId idStatus = idmasterService.getStatus(STATUS_ID_59, warehouseId, authTokenForIDService.getAccess_token());
                preOutboundHeaderRepository.updatePreOutboundHeaderStatus(warehouseId, refDocNumber, STATUS_ID_59, idStatus.getStatus());
                log.info("PreOutbound Header updated");

                /*-----------------Inventory Updates---------------------------*/
                List<QualityLine> dbQualityLine = qualityLineService.getQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
                Long BIN_CL_ID = 5L;
                for (QualityLine qualityLine : dbQualityLine) {
                    //------------Update Lock applied---------------------------------------------------------------------------------
                    List<Inventory> inventoryList = inventoryService.getInventoryForDeliveryConfirmtion(qualityLine.getWarehouseId(),
                            qualityLine.getItemCode(), qualityLine.getPickPackBarCode(), BIN_CL_ID);
                    for (Inventory inventory : inventoryList) {
                        Double INV_QTY = inventory.getInventoryQuantity() - qualityLine.getQualityQty();

                        if (INV_QTY < 0) {
                            INV_QTY = 0D;
                        }

                        if (INV_QTY >= 0) {
                            inventory.setInventoryQuantity(INV_QTY);

                            // INV_QTY > 0 then, update Inventory Table
                            inventory = inventoryRepository.save(inventory);
                        }
                    }
                    log.info("Inventory updated");
                }

                /*-------------------Inserting record in InventoryMovement-------------------------------------*/
                Long BIN_CLASS_ID = 5L;
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                StorageBin storageBin = mastersService.getStorageBin(warehouseId, BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
                String movementDocumentNo = refDocNumber;
                String stBin = storageBin.getStorageBin();
                String movementQtyValue = "N";

                List<PickupLine> dbPickupLine = pickupLineService.getPickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
                log.info("dbPickupLine: " + dbPickupLine.size());
                if (dbPickupLine != null) {
                    List<InventoryMovement> newInventoryMovementList = new ArrayList<>();
                    for (PickupLine pickupLine : dbPickupLine) {
                        InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, true);
                        newInventoryMovementList.add(inventoryMovement);
                    }
                    if (newInventoryMovementList.size() > 0) {
                        inventoryMovementRepository.saveAll(newInventoryMovementList);
                        log.info("InventoryMovement list created.");
                    }
                }
                return outboundLineByStatus57List;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String errorFromAXAPI = axapiResponse.getMessage();
            throw new BadRequestException("Error from AX: " + errorFromAXAPI);
        }
        return null;

        ////////////////////////////////////////////OLD_CODE////////////////////////////////////////////////////////////////////////
        /*
         * Pass the selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/ITEM_CODE/OB_LINE_NO values in OUTBOUNDLINE table and
         * Validate STATUS_ID = 55 or 47 or 51, if yes
         */
//		List<OutboundLine> responseOutboundLineList = new ArrayList<>();
//		for (OutboundLine outboundLine : outboundLineList) {
//			if (outboundLine.getStatusId() == 57L || outboundLine.getStatusId() == 47L || outboundLine.getStatusId() == 51L
//					|| outboundLine.getStatusId() == 41L) {
//				/*---------------------AXAPI-integration----------------------------------------------------------*/
//				// Checking the AX-API response
//				if (axapiResponse.getStatusCode() != null && axapiResponse.getStatusCode().equalsIgnoreCase("200")) {
//					if (outboundLine.getStatusId() == 57L) {
//						try {
//							// Pass the above values in OUTBOUNDHEADER and OUTBOUNDLINE tables and update STATUS_ID as "59"
//							try {
//								outboundLine.setStatusId(59L);
//								outboundLine.setUpdatedBy(loginUserID);
//								outboundLine.setUpdatedOn(new Date());
//								outboundLine = outboundLineRepository.save(outboundLine);
//								log.info("outboundLine updated : " + outboundLine);
//								responseOutboundLineList.add(outboundLine);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.error("deliveryConfirmation---OutboundLine update error: " + outboundLine);
//							}
//							
//							// OUTBOUNDHEADER update
//							try {
//								UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
//								updateOutboundHeader.setStatusId(59L);
//								updateOutboundHeader.setDeliveryConfirmedOn(new Date());
//								updateOutboundHeader.setUpdatedOn(new Date());
//								updateOutboundHeader.setUpdatedBy(loginUserID);
//								OutboundHeader updatedOutboundHeader = 
//										outboundHeaderService.updateOutboundHeader(warehouseId, preOutboundNo, refDocNumber, partnerCode, updateOutboundHeader, loginUserID);
//								log.info("updatedOutboundHeader updated : " + updatedOutboundHeader);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.error("deliveryConfirmation---UpdateOutboundHeader update error: [args]" + preOutboundNo + "," + refDocNumber + "," + partnerCode);
//							}
//							
//							//---------------------------------------------------------------------------------------------------------------------------
//							// QUALITYLINE - NOT REQUIRED TO UPDATE THE STATUS
//							try {
//								List<QualityLine> dbQualityLine = 
//										qualityLineService.getQualityLineForUpdateForDeliverConformation(warehouseId, preOutboundNo, refDocNumber, partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode());
//								
//								/*-----------------Inventory Updates---------------------------*/
//								// String warehouseId, String itemCode, Long binClassId
//								Long BIN_CL_ID = 5L;
//								for(QualityLine qualityLine : dbQualityLine) {
//									List<Inventory> inventoryList = inventoryService.getInventoryForDeliveryConfirmtion (outboundLine.getWarehouseId(),
//											outboundLine.getItemCode(), qualityLine.getPickPackBarCode(), BIN_CL_ID); //pack_bar_code
//									for(Inventory inventory : inventoryList) {
//										Double INV_QTY = inventory.getInventoryQuantity() - qualityLine.getQualityQty();
//
//										if (INV_QTY < 0) {
//											INV_QTY = 0D;
//										}
//
//										if (INV_QTY >= 0) {
//											inventory.setInventoryQuantity(INV_QTY);
//
//											// INV_QTY > 0 then, update Inventory Table
//											inventory = inventoryRepository.save(inventory);
//										}
//									}
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("ERROR: Update QualityHeader & line error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode  + "," + 
//										outboundLine.getLineNumber()  + "," +  outboundLine.getItemCode());
//							}
//							
//							try {
//								// PREOUTBOUNDLINE
//								UpdatePreOutboundLine updatePreOutboundLine = new UpdatePreOutboundLine();
//								updatePreOutboundLine.setStatusId(59L);
//								PreOutboundLine updatedPreOutboundLine = preOutboundLineService.updatePreOutboundLine(warehouseId, refDocNumber,
//										preOutboundNo, partnerCode, loginUserID, updatePreOutboundLine);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("Update PreOutboundLine error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode );
//							}
//							
//							try {
//								// PREOUTBOUNDHEADER
//								UpdatePreOutboundHeader updatePreOutboundHeader = new UpdatePreOutboundHeader();
//								updatePreOutboundHeader.setStatusId(59L);
//								PreOutboundHeader updatedPreOutboundHeader = preOutboundHeaderService.updatePreOutboundHeader(warehouseId, refDocNumber, 
//										preOutboundNo, partnerCode, loginUserID, updatePreOutboundHeader);
//								log.info("updatedPreOutboundHeader updated : " + updatedPreOutboundHeader);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log.info("Update PreOutboundHeader error: [args] " + warehouseId  + "," + preOutboundNo  + "," + refDocNumber  + "," + partnerCode );
//							}
//							
//							/*-------------------Inserting record in InventoryMovement-------------------------------------*/
//							Long BIN_CLASS_ID = 5L;
//							AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//							StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, 
//									authTokenForMastersService.getAccess_token());
//							
//							String movementDocumentNo = outboundLine.getRefDocNumber();
//							String stBin = storageBin.getStorageBin();
//							String movementQtyValue = "N";
//							List<PickupLine> dbPickupLine = 
//									pickupLineService.getPickupLineForUpdateConfirmation (warehouseId, preOutboundNo, refDocNumber, partnerCode, outboundLine.getLineNumber(), outboundLine.getItemCode());
//							if(dbPickupLine != null) {
//								for(PickupLine pickupLine : dbPickupLine ){
//									InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
//											movementQtyValue, loginUserID, true);
//									log.info("InventoryMovement created : " + inventoryMovement);
//								}
//							}
//						} catch (Exception e) {
//							log.info("Updating respective tables having Error : " + e.getLocalizedMessage());
//						}
//					}
//				} else {
//					String errorFromAXAPI = axapiResponse.getMessage();
//					throw new BadRequestException("Error from AX: " + errorFromAXAPI);
//				}
//			} else {
//				throw new BadRequestException("Order is not completely Processed.");
//			}
//		}
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @param updateOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundLine updateOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdateOutboundLine updateOutboundLine)
            throws IllegalAccessException, InvocationTargetException {
        OutboundLine outboundLine = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
        BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
        outboundLine.setUpdatedBy(loginUserID);
        outboundLine.setUpdatedOn(new Date());
        outboundLineRepository.save(outboundLine);
        return outboundLine;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @param updateOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLine> updateOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                                 String partnerCode, String loginUserID, UpdateOutboundLine updateOutboundLine)
            throws IllegalAccessException, InvocationTargetException {
        List<OutboundLine> outboundLines = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode);
        for (OutboundLine outboundLine : outboundLines) {
            BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
            outboundLine.setUpdatedBy(loginUserID);
            outboundLine.setUpdatedOn(new Date());
            outboundLineRepository.save(outboundLine);
        }
        return outboundLines;
    }

    /**
     * deleteOutboundLine
     *
     * @param loginUserID
     * @param lineNumber
     */
    public void deleteOutboundLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                   String itemCode, String loginUserID) {
        OutboundLine outboundLine = getOutboundLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
        if (outboundLine != null) {
            outboundLine.setDeletionIndicator(1L);
            outboundLine.setUpdatedBy(loginUserID);
            outboundLineRepository.save(outboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param refDocNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<OutboundReversal> doReversal(String refDocNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<OutboundLine> outboundLineList =
                outboundLineRepository.findByRefDocNumberAndItemCodeAndDeletionIndicator(refDocNumber, itemCode, 0L);
        log.info("outboundLineList---------> : " + outboundLineList);

        List<OutboundReversal> outboundReversalList = new ArrayList<>();
        for (OutboundLine outboundLine : outboundLineList) {
            Warehouse warehouse = getWarehouse(outboundLine.getWarehouseId());
            /*--------------STEP 1-------------------------------------*/
            // If STATUS_ID = 57 - Reversal of QC/Picking confirmation
            if (outboundLine.getStatusId() == 57L) {
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();

                outboundLine.setDeliveryQty(0D);
                outboundLine.setReversedBy(loginUserID);
                outboundLine.setReversedOn(new Date());
                outboundLine.setStatusId(47L);
                outboundLine = outboundLineRepository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                /*--------------STEP 2-------------------------------------
                 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values fetched
                 * from OUTBOUNDHEADER and OUTBOUNDLINE table into QCLINE table  and update STATUS_ID = 56
                 */
                List<QualityLine> qualityLine = qualityLineService.deleteQualityLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), loginUserID);
                log.info("QualityLine----------Deleted-------> : " + qualityLine);

                List<OutboundLineInterim> outboundLineInterim = qualityLineService.deleteOutboundLineInterimForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), loginUserID);
                log.info("OutboundLineInterim----------Deleted-------> : " + outboundLineInterim);

                if (qualityLine != null && qualityLine.size() > 0) {
                    for (QualityLine qualityLineData : qualityLine) {
                        List<QualityHeader> qualityHeader = qualityHeaderService.deleteQualityHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
                                qualityLineData.getQualityInspectionNo(), qualityLineData.getActualHeNo(), loginUserID);
                        log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                    }
                }

                /*---------------STEP 3------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from QCLINE table and
                 * pass the keys in PICKUPLINE table  and update STATUS_ID = 53
                 */
                // HAREESH 06/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLine> pickupLineList = pickupLineService.deletePickupLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), loginUserID);
                log.info("PickupLine----------Deleted-------> : ");

                /*---------------STEP 3.1-----Inventory update-------------------------------
                 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=5/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                 * in INVENTORY table and update INV_QTY as (INV_QTY - DLV_QTY ) and
                 * delete the record if INV_QTY = 0 (Update 1)
                 */
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLine pickupLine : pickupLineList) {
                        Inventory inventory = updateInventory1(pickupLine, outboundLineStatusIdBeforeUpdate);

                        //Get pickupheader for inventory update
                        List<PickupHeader> pickupHeader = pickupHeaderService.getPickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        /*---------------STEP 5-----OrderManagement update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table
                         * and pass the keys in ORDERMANAGEMENTLINE table and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeader pickupHeaderData : pickupHeader) {
                                List<OrderManagementLine> orderManagementLine = updateOrderManagementLineForReversal(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }

                        }

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN /PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                         * in INVENTORY table and update INV_QTY as (INV_QTY + DLV_QTY ) - (Update 2)
                         */
                        if (inventory != null) {
                            inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
                                    pickupLine.getItemCode(), pickupLine.getPickedStorageBin());

                            // HAREESH -28-08-2022 change to update allocated qty
                            if (inventory != null && pickupHeader != null) {
//								for (PickupHeader pickupHeaderData : pickupHeader) {
//									Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) + (pickupHeaderData.getPickToQty() != null ? pickupHeaderData.getPickToQty() : 0);
//									inventory.setAllocatedQuantity(ALLOC_QTY);
                                Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
                                inventory.setInventoryQuantity(INV_QTY);
                                inventory = inventoryRepository.save(inventory);
                                log.info("inventory updated : " + inventory);
//								}
                            }
                        }


                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        for (QualityLine qualityLineData : qualityLine) {
                            String reversalType = "QUALITY";
                            Double reversedQty = qualityLineData.getQualityQty();
                            OutboundReversal createdOutboundReversal = createOutboundReversal(warehouse, reversalType, refDocNumber,
                                    outboundLine.getPartnerCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
                                    60L, loginUserID);
                            outboundReversalList.add(createdOutboundReversal);

                            /////////RECORD-2/////////////////////////////////////////////////////////////////////////////////
                            reversalType = "PICKING";
                            reversedQty = pickupLine.getPickConfirmQty();
                            createdOutboundReversal = createOutboundReversal(warehouse, reversalType, refDocNumber,
                                    outboundLine.getPartnerCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
                                    outboundLine.getStatusId(), loginUserID);
                            outboundReversalList.add(createdOutboundReversal);
                        }

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                        Long BIN_CLASS_ID = 5L;
                        StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
                        String movementDocumentNo = outboundLine.getDeliveryOrderNo();
                        String stBin = storageBin.getStorageBin();
                        String movementQtyValue = "N";
                        InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        movementDocumentNo = pickupLine.getPickupNumber();
                        stBin = pickupLine.getPickedStorageBin();
                        movementQtyValue = "P";
                        inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }

                    //Delete pickupheader after inventory update
                    for (PickupLine pickupLine : pickupLineList) {
                        /*---------------STEP 4-----PickupHeader update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPLINE table
                         * and pass the keys in PICKUPHEADER table and Delete PickUpHeader
                         */
                        List<PickupHeader> pickupHeader = pickupHeaderService.deletePickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }

            /*-----------------------------Next Process----------------------------------------------------------*/
            // If STATUS_ID = 50 - Reversal of Picking Confirmation
            // HAREESH 27-08-2022 added status id 51
            if (outboundLine.getStatusId() == 50L || outboundLine.getStatusId() == 51L) {
                /*----------------------STEP 1------------------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from OUTBOUNDLINE table and
                 * pass the keys in PICKUPLINE table and update STATUS_ID=53 and Delete the record
                 */
                // HAREESH 25/11/2022 update outboundline
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();
                outboundLine.setStatusId(47L);
                outboundLine = outboundLineRepository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                // HAREESH 07/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLine> pickupLineList = pickupLineService.deletePickupLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), loginUserID);
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLine pickupLine : pickupLineList) {

                        //get pickup header
                        List<PickupHeader> pickupHeader = pickupHeaderService.getPickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        List<QualityLine> qualityLine = qualityLineService.deleteQualityLineForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                                outboundLine.getItemCode(), loginUserID);
                        log.info("QualityLine----------Deleted-------> : " + qualityLine);

                        // DELETE QUALITY_HEADER
                        List<QualityHeader> dbQualityHeader = qualityHeaderService.getInitialQualityHeaderForReversal(outboundLine.getWarehouseId(),
                                outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(), pickupLine.getPickupNumber(), outboundLine.getPartnerCode());
                        if (dbQualityHeader != null && dbQualityHeader.size() > 0) {
                            for (QualityHeader qualityHeaderData : dbQualityHeader) {
                                QualityHeader qualityHeader = qualityHeaderService.deleteQualityHeader(outboundLine.getWarehouseId(),
                                        outboundLine.getPreOutboundNo(), refDocNumber, qualityHeaderData.getQualityInspectionNo(),
                                        qualityHeaderData.getActualHeNo(), loginUserID);
                                log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                            }
                        }

                        /*---------------STEP 3-----OrderManagementLine update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table and
                         * pass the keys in ORDERMANAGEMENTLINE table  and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeader pickupHeaderData : pickupHeader) {
                                List<OrderManagementLine> orderManagementLine = updateOrderManagementLineForReversal(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }
                        }

                        /*---------------STEP 3.1-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and
                         * delete the record If INV_QTY = 0 - (Update 1)
                         */
                        updateInventory1(pickupLine, outboundLineStatusIdBeforeUpdate);

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
                         */
                        Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
                                pickupLine.getItemCode(), pickupLine.getPickedStorageBin());

                        if (inventory != null) {
                            // HAREESH -28-08-2022 change to update allocated qty
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = Double.valueOf(0);
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            inventory = inventoryRepository.save(inventory);
                            log.info("inventory updated : " + inventory);
                        }

                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        String reversalType = "PICKING";
                        Double reversedQty = pickupLine.getPickConfirmQty();
                        OutboundReversal createdOutboundReversal = createOutboundReversal(warehouse, reversalType, refDocNumber, outboundLine.getPartnerCode(), itemCode,
                                pickupLine.getPickedPackCode(), reversedQty, outboundLine.getStatusId(), loginUserID);
                        outboundReversalList.add(createdOutboundReversal);
                        /****************************************************************************/

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                        Long BIN_CLASS_ID = 4L;
                        StorageBin storageBin = mastersService.getStorageBin(outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());

                        String movementDocumentNo = pickupLine.getRefDocNumber();
                        String stBin = storageBin.getStorageBin();
                        String movementQtyValue = "N";
                        InventoryMovement inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        movementDocumentNo = pickupLine.getPickupNumber();
                        stBin = pickupLine.getPickedStorageBin();
                        movementQtyValue = "P";
                        inventoryMovement = createInventoryMovement(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }

                    //Delete pickupheader after inventory update
                    for (PickupLine pickupLine : pickupLineList) {
                        List<PickupHeader> pickupHeader = pickupHeaderService.deletePickupHeaderForReversal(outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }
        }

        return outboundReversalList;
    }

    /**
     * @param pickupLine
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @param isFromDelivery
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
//	private InventoryMovement createInventoryMovement (PickupLine pickupLine, 
//			String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID, boolean isFromDelivery ) 
//					throws IllegalAccessException, InvocationTargetException {
//		// Flag "isFromDelivery" is not used anywhere. 
//		AddInventoryMovement inventoryMovement = new AddInventoryMovement();
//		BeanUtils.copyProperties(pickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(pickupLine));
//		
//		// MVT_TYP_ID
//		inventoryMovement.setMovementType(3L);
//		
//		// SUB_MVT_TYP_ID
//		inventoryMovement.setSubmovementType(5L);
//		
//		// PACK_BARCODE
//		inventoryMovement.setPackBarcodes(pickupLine.getPickedPackCode());
//		
//		// VAR_ID
//		inventoryMovement.setVariantCode(1L);
//		
//		// VAR_SUB_ID
//		inventoryMovement.setVariantSubCode("1");
//		
//		// STR_MTD
//		inventoryMovement.setStorageMethod("1");
//		
//		// STR_NO
//		inventoryMovement.setBatchSerialNumber("1");
//		
//		// MVT_DOC_NO
//		inventoryMovement.setMovementDocumentNo(movementDocumentNo);
//		
//		// ST_BIN
//		inventoryMovement.setStorageBin(storageBin);
//		
//		// MVT_QTY_VAL
//		inventoryMovement.setMovementQtyValue(movementQtyValue);
//		
//		// MVT_QTY
//		inventoryMovement.setMovementQty(pickupLine.getPickConfirmQty());
//		
//		// MVT_UOM
//		inventoryMovement.setInventoryUom(pickupLine.getPickUom());
//		
//		// BAL_OH_QTY
//		// PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
//		List<Inventory> inventoryList = 
//				inventoryService.getInventory (pickupLine.getWarehouseId(), pickupLine.getItemCode(), 1L);
//		double sumOfInvQty = inventoryList.stream().mapToDouble(a->a.getInventoryQuantity()).sum();
//		inventoryMovement.setBalanceOHQty(sumOfInvQty);
//	
//		// IM_CTD_BY
//		inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());
//		
//		// IM_CTD_ON
//		inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());
//
//		InventoryMovement createdInventoryMovement = 
//				inventoryMovementService.createInventoryMovement(inventoryMovement, loginUserID);
//		return createdInventoryMovement;
//	}
    private InventoryMovement createInventoryMovement(PickupLine pickupLine,
                                                      String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID, boolean isFromDelivery)
            throws IllegalAccessException, InvocationTargetException {
        // Flag "isFromDelivery" is not used anywhere.
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(pickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(pickupLine));

        // MVT_TYP_ID
        inventoryMovement.setMovementType(3L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(5L);

        // PACK_BARCODE
        inventoryMovement.setPackBarcodes(pickupLine.getPickedPackCode());

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(movementDocumentNo);

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // MVT_QTY
        inventoryMovement.setMovementQty(pickupLine.getPickConfirmQty());

        // MVT_UOM
        inventoryMovement.setInventoryUom(pickupLine.getPickUom());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCount(pickupLine.getWarehouseId(), pickupLine.getItemCode());
        inventoryMovement.setBalanceOHQty(sumOfInvQty);

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());

        return inventoryMovement;
    }

    /**
     * @param warehouse
     * @param reversalType
     * @param refDocNumber
     * @param partnerCode
     * @param itemCode
     * @param packBarcode
     * @param reversedQty
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private OutboundReversal createOutboundReversal(Warehouse warehouse, String reversalType, String refDocNumber,
                                                    String partnerCode, String itemCode, String packBarcode, Double reversedQty, Long statusId,
                                                    String loginUserID) throws IllegalAccessException, InvocationTargetException {
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(47L, warehouse.getWarehouseId(), idmasterAuthToken.getAccess_token());
        AddOutboundReversal newOutboundReversal = new AddOutboundReversal();

        // LANG_ID
        newOutboundReversal.setLanguageId(warehouse.getLanguageId());

        // C_ID - C_ID of the selected REF_DOC_NO
        newOutboundReversal.setCompanyCodeId(warehouse.getCompanyCode());

        // PLANT_ID
        newOutboundReversal.setPlantId(warehouse.getPlantId());

        // WH_ID
        newOutboundReversal.setWarehouseId(warehouse.getWarehouseId());

        // OB_REVERSAL_NO
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 13  in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and insert
         */
        long NUM_RAN_CODE = 13;
        String OB_REVERSAL_NO = getNextRangeNumber(NUM_RAN_CODE, warehouse.getWarehouseId());
        newOutboundReversal.setOutboundReversalNo(OB_REVERSAL_NO);

        // REVERSAL_TYPE
        newOutboundReversal.setReversalType(reversalType);

        // REF_DOC_NO
        newOutboundReversal.setRefDocNumber(refDocNumber);

        // PARTNER_CODE
        newOutboundReversal.setPartnerCode(partnerCode);

        // ITM_CODE
        newOutboundReversal.setItemCode(itemCode);

        // PACK_BARCODE
        newOutboundReversal.setPackBarcode(packBarcode);

        // REV_QTY
        newOutboundReversal.setReversedQty(reversedQty);

        // STATUS_ID
        newOutboundReversal.setStatusId(statusId);
        newOutboundReversal.setReferenceField10(idStatus.getStatus());

        OutboundReversal outboundReversal =
                outboundReversalService.createOutboundReversal(newOutboundReversal, loginUserID);
        log.info("OutboundReversal created : " + outboundReversal);
        return outboundReversal;
    }


    /**
     * @param pickupHeader
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private OrderManagementLine updateOrderManagementLine(PickupHeader pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        UpdateOrderManagementLine updateOrderManagementLine = new UpdateOrderManagementLine();
        updateOrderManagementLine.setPickupNumber(null);
        updateOrderManagementLine.setStatusId(43L);

        //HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null
//		OrderManagementLine orderManagementLine = orderManagementLineService.updateOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
//				pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
//				pickupHeader.getItemCode(), loginUserID, updateOrderManagementLine);

        List<OrderManagementLine> dbOrderManagementLines = orderManagementLineService.getOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
                pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(), pickupHeader.getItemCode());
        OrderManagementLine updatedOrderManagementLine = null;
        for (OrderManagementLine dbOrderManagementLine : dbOrderManagementLines) {
            BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine, CommonUtils.getNullPropertyNames(updateOrderManagementLine));
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupNumber(null);
            dbOrderManagementLine.setStatusId(43L);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated : " + updatedOrderManagementLine);
        }
        return updatedOrderManagementLine;
    }

    /**
     * @param pickupHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private List<OrderManagementLine> updateOrderManagementLineForReversal(PickupHeader pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        //HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null

        List<OrderManagementLine> dbOrderManagementLine = orderManagementLineService.getListOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
                pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
                pickupHeader.getItemCode());
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(47L, pickupHeader.getWarehouseId(), idmasterAuthToken.getAccess_token());
        if (dbOrderManagementLine != null && !dbOrderManagementLine.isEmpty()) {
            List<OrderManagementLine> orderManagementLineList = new ArrayList<>();
            dbOrderManagementLine.forEach(data -> {
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupNumber(null);
                data.setAllocatedQty(0D);                        // HAREESH 25/11/2022
                data.setStatusId(47L);
                data.setReferenceField7(idStatus.getStatus());    // ref_field_7
                data.setPickupUpdatedOn(new Date());
                orderManagementLineList.add(data);
            });

            List<OrderManagementLine> orderManagementLine = orderManagementLineRepository.saveAll(orderManagementLineList);
            log.info("OrderManagementLine updated : " + orderManagementLine);
            if (orderManagementLine.size() > 1) {
                log.info("Delete OrderManagementLines if more that one line : ");
                int i = 0;
                List<OrderManagementLine> deleteOrderManagementLineList = new ArrayList<>();
                for (OrderManagementLine line : orderManagementLine) {
                    if (i != 0) {
                        line.setDeletionIndicator(1L);
                        deleteOrderManagementLineList.add(line);
                    }
                    i++;
                }
                log.info("OrderManagementLines for delete : " + deleteOrderManagementLineList);
                orderManagementLineRepository.saveAll(deleteOrderManagementLineList);
                log.info("OrderManagementLines deleted : ");
            }
            return orderManagementLine;
        } else {
            return null;
        }
    }

    /**
     * @param pickupLine
     * @param loginUserID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private PickupHeader updatePickupHeader(PickupLine pickupLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        UpdatePickupHeader updatePickupHeader = new UpdatePickupHeader();
        updatePickupHeader.setStatusId(53L);
        PickupHeader pickupHeader = pickupHeaderService.updatePickupHeader(pickupLine.getWarehouseId(),
                pickupLine.getPreOutboundNo(), pickupLine.getRefDocNumber(), pickupLine.getPartnerCode(),
                pickupLine.getPickupNumber(), pickupLine.getLineNumber(), pickupLine.getItemCode(),
                loginUserID, updatePickupHeader);
        log.info("pickupHeader updated : " + pickupHeader);
        return pickupHeader;
    }

    /**
     * @param pickupLine
     * @param
     * @return
     */
    private Inventory updateInventory1(PickupLine pickupLine, Long statusId) {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Long BIN_CLASS_ID = 5L;
        if (statusId == 50L) {
            BIN_CLASS_ID = 4L;
        }
        StorageBin storageBin = mastersService.getStorageBin(pickupLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
        Inventory inventory = inventoryService.getInventory(pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
                pickupLine.getItemCode(), storageBin.getStorageBin());
        if (inventory != null) {
            Double INV_QTY = inventory.getInventoryQuantity() - pickupLine.getPickConfirmQty();
            inventory.setInventoryQuantity(INV_QTY);

            // INV_QTY > 0 then, update Inventory Table
            inventory = inventoryRepository.save(inventory);
            log.info("Inventory updated : " + inventory);

            if (INV_QTY == 0) {
//			[Prod Fix: 28-06] - Discussed to comment delete Inventory operation to avoid unwanted delete of Inventory
//			inventoryRepository.delete(inventory);
                log.info("inventory record is deleted...");
            }
        }
        return inventory;
    }

    /*--------------------------------------OUTBOUND---------------------------------------------*/

    /**
     * InterwarehouseShipment API
     *
     * @param confirmedOutboundHeader
     * @param confirmedOutboundLines
     * @return
     */
    private AXApiResponse postInterwarehouseShipment(OutboundHeader confirmedOutboundHeader,
                                                     List<OutboundLine> confirmedOutboundLines) {
        InterWarehouseShipmentHeader toHeader = new InterWarehouseShipmentHeader();
        toHeader.setTransferOrderNumber(confirmedOutboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<InterWarehouseShipmentLine> toLines = new ArrayList<>();
        for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {
            log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
            log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());

            if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
                confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
                InterWarehouseShipmentLine iwhShipmentLine = new InterWarehouseShipmentLine();

                // SKU	<-	ITM_CODE
                iwhShipmentLine.setSku(confirmedOutboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                iwhShipmentLine.setSkuDescription(confirmedOutboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                iwhShipmentLine.setLineReference(confirmedOutboundLine.getLineNumber());

                // Ordered Qty	<- ORD_QTY
                iwhShipmentLine.setOrderedQty(confirmedOutboundLine.getOrderQty());

                // Shipped Qty	<-	DLV_QTY
                iwhShipmentLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());

                // Delivery Date <-	DLV_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
                iwhShipmentLine.setDeliveryDate(date);

                // FromWhsID	<-	WH_ID
                iwhShipmentLine.setFromWhsID(confirmedOutboundLine.getWarehouseId());

                // ToWhsID	<-	PARTNER_CODE
                iwhShipmentLine.setToWhsID(confirmedOutboundLine.getPartnerCode());

                toLines.add(iwhShipmentLine);
            }
        }

        InterWarehouseShipment interWarehouseShipment = new InterWarehouseShipment();
        interWarehouseShipment.setToHeader(toHeader);
        interWarehouseShipment.setToLines(toLines);
        log.info("InterWarehouseShipment : " + interWarehouseShipment);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postInterWarehouseShipmentConfirmation(interWarehouseShipment,
                authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(toHeader.getTransferOrderNumber());
        response.setOrderType("OUTBOUND");
        response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceIWHouseShipmentUrl());
        response.setTransDate(new Date());

        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * Do not send ITM_CODE values where DLV_QTY = 0 or REF_FIELD_2 is Not Null to MS Dynamics system
     *
     * @param confirmedOutboundHeader
     * @param confirmedOutboundLines
     * @return
     */
    private AXApiResponse postShipment(OutboundHeader confirmedOutboundHeader, List<OutboundLine> confirmedOutboundLines) {
        ShipmentHeader toHeader = new ShipmentHeader();
        toHeader.setTransferOrderNumber(confirmedOutboundHeader.getRefDocNumber());    // REF_DOC_NO

        log.info("confirmedOutboundLines--------------> :  " + confirmedOutboundLines);
        List<ShipmentLine> toLines = new ArrayList<>();
        for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {

            log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
            log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());

            if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
                confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
                ShipmentLine shipmentLine = new ShipmentLine();

                // SKU	<-	ITM_CODE
                shipmentLine.setSku(confirmedOutboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                shipmentLine.setSkuDescription(confirmedOutboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                shipmentLine.setLineReference(confirmedOutboundLine.getLineNumber());

                // Ordered Qty	<- ORD_QTY
                shipmentLine.setOrderedQty(confirmedOutboundLine.getOrderQty());

                // Shipped Qty	<-	DLV_QTY
                shipmentLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());

                // Delivery Date <-	DLV_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
                shipmentLine.setDeliveryDate(date);

                // Store ID <-	PARTNER_CODE
                shipmentLine.setStoreId(confirmedOutboundLine.getPartnerCode());

                // Warehouse ID	<-	WH_ID
                shipmentLine.setWareHouseID(confirmedOutboundLine.getWarehouseId());

                toLines.add(shipmentLine);
            }
        }

        Shipment shipment = new Shipment();
        shipment.setToHeader(toHeader);
        shipment.setToLines(toLines);
        log.info("Sending to AX : " + shipment);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postShipmentConfirmation(shipment, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        // Capture the AXResponse in Table
        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(toHeader.getTransferOrderNumber());
        response.setOrderType("OUTBOUND");
        response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceShipmentUrl());
        response.setTransDate(new Date());
        integrationApiResponseRepository.save(response);
        return apiResponse;
    }

    /**
     * ReturnPO
     *
     * @param confirmedOutboundHeader
     * @param confirmedOutboundLines
     * @return
     */
    private AXApiResponse postReturnPO(OutboundHeader confirmedOutboundHeader,
                                       List<OutboundLine> confirmedOutboundLines) {
        ReturnPOHeader poHeader = new ReturnPOHeader();
        poHeader.setPoNumber(confirmedOutboundHeader.getRefDocNumber());    // REF_DOC_NO
        poHeader.setSupplierInvoice(confirmedOutboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<ReturnPOLine> poLines = new ArrayList<>();
        for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {

            log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
            log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());

            if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
                ReturnPOLine returnPOLine = new ReturnPOLine();

                // SKU	<-	ITM_CODE
                returnPOLine.setSku(confirmedOutboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                returnPOLine.setSkuDescription(confirmedOutboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                returnPOLine.setLineReference(confirmedOutboundLine.getLineNumber());

                // Return Qty <-	ORD_QTY
                returnPOLine.setReturnQty(confirmedOutboundLine.getOrderQty());

                // Shipped Qty	<-	DLV_QTY
                returnPOLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());

                // Warehouse ID	<-	WH_ID
                returnPOLine.setWarehouseID(confirmedOutboundLine.getWarehouseId());

                poLines.add(returnPOLine);
            }
        }

        ReturnPO returnPO = new ReturnPO();
        returnPO.setPoHeader(poHeader);
        returnPO.setPoLines(poLines);
        log.info("ReturnPO : " + returnPO);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postReturnPOConfirmation(returnPO, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(poHeader.getPoNumber());
        response.setOrderType("OUTBOUND");
        response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setTransDate(new Date());
        response.setApiUrl(propertiesConfig.getAxapiServiceReturnPOUrl());
        integrationApiResponseRepository.save(response);

        return apiResponse;
    }

    /**
     * @param confirmedOutboundHeader
     * @param confirmedOutboundLines
     * @return
     */
    private AXApiResponse postSalesOrder(OutboundHeader confirmedOutboundHeader,
                                         List<OutboundLine> confirmedOutboundLines) {
        SalesOrderHeader soHeader = new SalesOrderHeader();
        soHeader.setSalesOrderNumber(confirmedOutboundHeader.getRefDocNumber());    // REF_DOC_NO

        List<SalesOrderLine> soLines = new ArrayList<>();
        for (OutboundLine confirmedOutboundLine : confirmedOutboundLines) {

            log.info("DLV_QTY : " + confirmedOutboundLine.getDeliveryQty());
            log.info("ReferenceField2 : " + confirmedOutboundLine.getReferenceField2());

            if (confirmedOutboundLine.getDeliveryQty() != null && confirmedOutboundLine.getDeliveryQty() > 0) {
                confirmedOutboundLine.setDeliveryConfirmedOn(new Date());
                SalesOrderLine salesOrderLine = new SalesOrderLine();

                // SKU	<-	ITM_CODE
                salesOrderLine.setSku(confirmedOutboundLine.getItemCode());

                // SKU description	<- ITEM_TEXT
                salesOrderLine.setSkuDescription(confirmedOutboundLine.getDescription());

                // Line reference	<-	IB_LINE_NO
                salesOrderLine.setLineReference(confirmedOutboundLine.getLineNumber());

                // Ordered Qty	<- ORD_QTY
                salesOrderLine.setOrderedQty(confirmedOutboundLine.getOrderQty());

                // Shipped Qty	<-	DLV_QTY
                salesOrderLine.setShippedQty(confirmedOutboundLine.getDeliveryQty());

                // Delivery Date <-	DLV_CNF_ON
                String date = DateUtils.date2String_MMDDYYYY(confirmedOutboundLine.getDeliveryConfirmedOn());
                salesOrderLine.setDeliveryDate(date);

                // Store ID <-	PARTNER_CODE
                salesOrderLine.setStoreId(confirmedOutboundLine.getPartnerCode());

                // Warehouse ID	<-	WH_ID
                salesOrderLine.setWareHouseID(confirmedOutboundLine.getWarehouseId());

                soLines.add(salesOrderLine);
            }
        }

        SalesOrder salesOrder = new SalesOrder();
        salesOrder.setSoHeader(soHeader);
        salesOrder.setSoLines(soLines);
        log.info("SalesOrder : " + salesOrder);

        /*
         * Posting to AX_API
         */
        AXAuthToken authToken = authTokenService.generateAXOAuthToken();
        AXApiResponse apiResponse = warehouseService.postSaleOrderConfirmation(salesOrder, authToken.getAccess_token());
        log.info("apiResponse : " + apiResponse);

        IntegrationApiResponse response = new IntegrationApiResponse();
        response.setOrderNumber(soHeader.getSalesOrderNumber());
        response.setOrderType("OUTBOUND");
        response.setOrderTypeId(confirmedOutboundHeader.getOutboundOrderTypeId());
        response.setResponseCode(apiResponse.getStatusCode());
        response.setResponseText(apiResponse.getMessage());
        response.setApiUrl(propertiesConfig.getAxapiServiceSalesOrderUrl());
        response.setTransDate(new Date());
        integrationApiResponseRepository.save(response);

        return apiResponse;
    }

    //===================================================================V2==============================================================================

    /**
     * getOutboundLines
     *
     * @return
     */
    public List<OutboundLineV2> getOutboundLinesV2() {
        List<OutboundLineV2> outboundLineList = outboundLineV2Repository.findAll();
        outboundLineList = outboundLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return outboundLineList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String preOutboundNo, String refDocNumber, String partnerCode) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "companyCodeId : " + companyCodeId
                    + "plantId : " + plantId
                    + "languageId : " + languageId
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + ", partnerCode : " + partnerCode
                    + " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(List<String> companyCodeId, List<String> plantId, List<String> languageId,
                                                  List<String> warehouseId, List<String> preOutboundNo, List<String> refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdInAndPlantIdInAndLanguageIdInAndWarehouseIdInAndPreOutboundNoInAndRefDocNumberInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "companyCodeId : " + companyCodeId
                    + "plantId : " + plantId
                    + "languageId : " + languageId
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public Long getOutboundLineCountV2(List<String> companyCodeId, List<String> plantId, List<String> languageId,
                                       List<String> warehouseId, List<String> preOutboundNo, List<String> refDocNumber) {
        Long outboundLineCount =
                outboundLineV2Repository.getOutboundLinesCount(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        log.info("OuboundLine count :----------->" + outboundLineCount);
        return outboundLineCount;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param statusIds
     * @return
     */
    public Long getOutboundLineStatusIdCountV2(List<String> companyCodeId, List<String> plantId, List<String> languageId, List<String> warehouseId,
                                               List<String> preOutboundNo, List<String> refDocNumber, List<Long> statusIds) {
        Long outboundLineCount =
                outboundLineV2Repository.getOutboundLinesStatusIdCount(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, statusIds);
        log.info("OuboundLine status Id 47L,51L,57L :----------->" + outboundLineCount);
        return outboundLineCount;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param statusIds
     * @return
     */
    public long getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String preOutboundNo, String refDocNumber, String partnerCode, List<Long> statusIds) {
        long outboundLineCount =
                outboundLineV2Repository.getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicatorV2(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0);
        log.info("OuboundLine status Id 47L,51L,57L :----------->" + outboundLineCount);
        return outboundLineCount;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param statusIds
     * @return
     */
    public List<OutboundLineV2> findOutboundLineByStatusV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String preOutboundNo, String refDocNumber, String partnerCode, List<Long> statusIds) {
        List<OutboundLineV2> outboundLine;
        try {
            outboundLine = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0L);
            if (outboundLine == null) {
                throw new BadRequestException("The given OutboundLine ID : "
                        + "companyCodeId : " + companyCodeId
                        + "plantId : " + plantId
                        + "languageId : " + languageId
                        + "warehouseId : " + warehouseId
                        + ", preOutboundNo : " + preOutboundNo
                        + ", refDocNumber : " + refDocNumber
                        + ", partnerCode : " + partnerCode
                        + " doesn't exist.");
            }
            return outboundLine;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String preOutboundNo, String refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (!outboundLine.isEmpty()) {
            return outboundLine;
        } else {
            return null;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
            return outboundLine;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineForReportsV2(String companyCodeId, String plantId, String languageId,
                                                            String warehouseId, String preOutboundNo, String refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "companyCodeId : " + companyCodeId
                    + "plantId : " + plantId
                    + "languageId : " + languageId
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getCountofOrderedLinesV2(String companyCodeId, String plantId, String languageId,
                                               String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> countofOrderedLines = outboundLineV2Repository.getCountofOrderedLinesV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        return countofOrderedLines;
    }

    /**
     * getSumOfOrderedQty
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getSumOfOrderedQtyV2(String companyCodeId, String plantId, String languageId,
                                           String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> sumOfOrderedQty = outboundLineV2Repository.getSumOfOrderedQtyV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        return sumOfOrderedQty;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param outboundOrderTypeId
     * @return
     */
    public Long getSumOfOrderedQtyByPartnerCodeV2(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, Long outboundOrderTypeId) {
        Long sumOfOrderedQty = outboundLineV2Repository.getSumOfOrderedQtyByPartnerCodeV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId);
        return sumOfOrderedQty;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getDeliveryLinesV2(String companyCodeId, String plantId, String languageId,
                                         String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> deliveryLines = outboundLineV2Repository.getDeliveryLinesV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        return deliveryLines;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<Long> getDeliveryQtyV2(String companyCodeId, String plantId, String languageId,
                                       String warehouseId, String preOutboundNo, String refDocNumber) {
        List<Long> deliveryQtyList = outboundLineV2Repository.getDeliveryQtyV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        return deliveryQtyList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param outboundOrderTypeId
     * @return
     */
    public Long getDeliveryQtyByPartnerCodeV2(String companyCodeId, String plantId, String languageId,
                                              String warehouseId, List<String> preOutboundNo, List<String> refDocNumber, Long outboundOrderTypeId) {
        Long deliveryQty = outboundLineV2Repository.getDeliveryQtyByPartnerCodeV2(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, outboundOrderTypeId);
        return deliveryQty;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNo
     * @return
     */
    public List<Long> getLineItem_NByRefDocNoAndRefField2IsNullV2(String companyCodeId, String plantId, String languageId,
                                                                  String warehouseId, List<String> refDocNo) {
        List<Long> lineItems =
                outboundLineV2Repository.findLineItem_NByRefDocNoAndRefField2IsNull(companyCodeId, plantId, languageId, warehouseId, refDocNo);
        return lineItems;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNo
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Long> getShippedLinesV2(String companyCodeId, String plantId, String languageId,
                                        String warehouseId, List<String> refDocNo, Date startDate, Date endDate) {
        List<Long> lineItems = outboundLineV2Repository.findShippedLines(companyCodeId, plantId, languageId, warehouseId, refDocNo, startDate, endDate);
        return lineItems;
    }

    /**
     * @param preOBNo
     * @param obLineNo
     * @param itemCode
     * @return
     */
    public List<Long> getLineShippedV2(String companyCodeId, String plantId, String languageId, String preOBNo, Long obLineNo, String itemCode) {
        List<Long> lineShippedList = outboundLineV2Repository.findLineShippedV2(companyCodeId, plantId, languageId, preOBNo, obLineNo, itemCode);
        return lineShippedList;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public List<OutBoundLineImpl> getOutBoundLineDataForOutBoundHeaderV2(String companyCodeId, String plantId, String languageId, List<String> refDocNumber) {
        List<OutBoundLineImpl> outBoundLines = outboundLineV2Repository.getOutBoundLineDataForOutBoundHeaderV2(companyCodeId, plantId, languageId, refDocNumber);
        return outBoundLines;
    }

    /**
     * Pass the Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in OUTBOUNDLINE table and
     * update SATATU_ID as 48
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     * @return
     */
    public OutboundLineV2 getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        OutboundLineV2 outboundLine = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (outboundLine != null) {
            return outboundLine;
        }
        throw new BadRequestException("The given OutboundLine ID : " +
                "companyCodeId:" + companyCodeId +
                "plantId:" + plantId +
                "languageId:" + languageId +
                "warehouseId:" + warehouseId +
                ",preOutboundNo:" + preOutboundNo +
                ",refDocNumber:" + refDocNumber +
                ",partnerCode:" + partnerCode +
                ",lineNumber:" + lineNumber +
                ",itemCode:" + itemCode +
                " doesn't exist.");
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public OutboundLineV2 getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String manufacturerName) {
        OutboundLineV2 outboundLine = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName,  0L);
        if (outboundLine != null) {
            return outboundLine;
        }
        throw new BadRequestException("The given OutboundLine ID : " +
                "companyCodeId:" + companyCodeId +
                "plantId:" + plantId +
                "languageId:" + languageId +
                "warehouseId:" + warehouseId +
                ",preOutboundNo:" + preOutboundNo +
                ",refDocNumber:" + refDocNumber +
                ",partnerCode:" + partnerCode +
                ",lineNumber:" + lineNumber +
                ",itemCode:" + itemCode +
                ",manufacturerName:" + manufacturerName +
                " doesn't exist.");
    }

    /**
     *
     * @param searchOutboundLineReport
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundLineV2> findOutboundLineReportV2(SearchOutboundLineReportV2 searchOutboundLineReport)
            throws ParseException, java.text.ParseException {
        if (searchOutboundLineReport.getStartConfirmedOn() != null && searchOutboundLineReport.getEndConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLineReport.getStartConfirmedOn(), searchOutboundLineReport.getEndConfirmedOn());
            searchOutboundLineReport.setStartConfirmedOn(dates[0]);
            searchOutboundLineReport.setEndConfirmedOn(dates[1]);
        }

        OutboundLineReportV2Specification spec = new OutboundLineReportV2Specification(searchOutboundLineReport);
        List<OutboundLineV2> results = outboundLineV2Repository.findAll(spec);
        log.info("results: " + results);
        return results;
    }


    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public List<OutboundLineV2> findOutboundLineV2(SearchOutboundLineV2 searchOutboundLine)
            throws ParseException, java.text.ParseException {

        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            OutboundLineV2Specification spec = new OutboundLineV2Specification(searchOutboundLine);
            List<OutboundLineV2> outboundLineSearchResults = outboundLineV2Repository.findAll(spec);

            /*
             * Pass WH-ID/REF_DOC_NO/PRE_OB_NO/OB_LINE_NO/ITM_CODE
             * PickConfirmQty & QCQty - from QualityLine table
             */
            if (outboundLineSearchResults != null) {
                for (OutboundLine outboundLineSearchResult : outboundLineSearchResults) {
                    Double qcQty = qualityLineV2Repository.getQualityLineCountV2(
                            outboundLineSearchResult.getCompanyCodeId(), outboundLineSearchResult.getPlantId(),
                            outboundLineSearchResult.getLanguageId(), outboundLineSearchResult.getWarehouseId(),
                            outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(),
                            outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("qcQty : " + qcQty);
                    if (qcQty != null) {
                        outboundLineSearchResult.setReferenceField10(String.valueOf(qcQty));
                    }

                    Double pickConfirmQty = pickupLineV2Repository.getPickupLineCountV2(
                            outboundLineSearchResult.getCompanyCodeId(), outboundLineSearchResult.getPlantId(),
                            outboundLineSearchResult.getLanguageId(), outboundLineSearchResult.getWarehouseId(),
                            outboundLineSearchResult.getRefDocNumber(), outboundLineSearchResult.getPreOutboundNo(),
                            outboundLineSearchResult.getLineNumber(), outboundLineSearchResult.getItemCode());
//					log.info("pickConfirmQty : " + pickConfirmQty);
                    if (pickConfirmQty != null) {
                        outboundLineSearchResult.setReferenceField9(String.valueOf(pickConfirmQty));
                    }
                }
            }
            return outboundLineSearchResults;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    public Stream<OutboundLineV2> findOutboundLineNewStreamV2(SearchOutboundLineV2 searchOutboundLine)
            throws ParseException {

        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            if (searchOutboundLine.getWarehouseId() == null || searchOutboundLine.getWarehouseId().isEmpty()) {
                searchOutboundLine.setWarehouseId(null);
            }
            if (searchOutboundLine.getPreOutboundNo() == null || searchOutboundLine.getPreOutboundNo().isEmpty()) {
                searchOutboundLine.setPreOutboundNo(null);
            }
            if (searchOutboundLine.getRefDocNumber() == null || searchOutboundLine.getRefDocNumber().isEmpty()) {
                searchOutboundLine.setRefDocNumber(null);
            }
            if (searchOutboundLine.getLineNumber() == null || searchOutboundLine.getLineNumber().isEmpty()) {
                searchOutboundLine.setLineNumber(null);
            }
            if (searchOutboundLine.getItemCode() == null || searchOutboundLine.getItemCode().isEmpty()) {
                searchOutboundLine.setItemCode(null);
            }
            if (searchOutboundLine.getStatusId() == null || searchOutboundLine.getStatusId().isEmpty()) {
                searchOutboundLine.setStatusId(null);
            }
            if (searchOutboundLine.getOrderType() == null || searchOutboundLine.getOrderType().isEmpty()) {
                searchOutboundLine.setOrderType(null);
            }
            if (searchOutboundLine.getPartnerCode() == null || searchOutboundLine.getPartnerCode().isEmpty()) {
                searchOutboundLine.setPartnerCode(null);
            }
            log.info("Find OutboundLine Stream Search Input: " + searchOutboundLine);
            OutboundLineV2Specification spec = new OutboundLineV2Specification(searchOutboundLine);
            Stream<OutboundLineV2> outboundLineSearchResults = outboundLineV2Repository.stream(spec, OutboundLineV2.class);

            return outboundLineSearchResults;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     */
    public List<OutboundLineOutput> findOutboundLineNewV2(SearchOutboundLineV2 searchOutboundLine) throws ParseException {

        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }

            if (searchOutboundLine.getWarehouseId() == null || searchOutboundLine.getWarehouseId().isEmpty()) {
                searchOutboundLine.setWarehouseId(null);
            }
            if (searchOutboundLine.getPreOutboundNo() == null || searchOutboundLine.getPreOutboundNo().isEmpty()) {
                searchOutboundLine.setPreOutboundNo(null);
            }
            if (searchOutboundLine.getRefDocNumber() == null || searchOutboundLine.getRefDocNumber().isEmpty()) {
                searchOutboundLine.setRefDocNumber(null);
            }
            if (searchOutboundLine.getLineNumber() == null || searchOutboundLine.getLineNumber().isEmpty()) {
                searchOutboundLine.setLineNumber(null);
            }
            if (searchOutboundLine.getItemCode() == null || searchOutboundLine.getItemCode().isEmpty()) {
                searchOutboundLine.setItemCode(null);
            }
            if (searchOutboundLine.getStatusId() == null || searchOutboundLine.getStatusId().isEmpty()) {
                searchOutboundLine.setStatusId(null);
            }
            if (searchOutboundLine.getOrderType() == null || searchOutboundLine.getOrderType().isEmpty()) {
                searchOutboundLine.setOrderType(null);
            }
            if (searchOutboundLine.getPartnerCode() == null || searchOutboundLine.getPartnerCode().isEmpty()) {
                searchOutboundLine.setPartnerCode(null);
            }
            if (searchOutboundLine.getSalesOrderNumber() == null || searchOutboundLine.getSalesOrderNumber().isEmpty()) {
                searchOutboundLine.setSalesOrderNumber(null);
            }
            if (searchOutboundLine.getTargetBranchCode() == null || searchOutboundLine.getTargetBranchCode().isEmpty()) {
                searchOutboundLine.setTargetBranchCode(null);
            }
            if (searchOutboundLine.getManufacturerName() == null || searchOutboundLine.getManufacturerName().isEmpty()) {
                searchOutboundLine.setManufacturerName(null);
            }

            log.info("Find OutboundLine Search Input: " + searchOutboundLine);
            List<OutboundLineOutput> outboundLineSearchResults = outboundLineV2Repository.findOutboundLine(
                    searchOutboundLine.getCompanyCodeId(), searchOutboundLine.getLanguageId(),
                    searchOutboundLine.getPlantId(), searchOutboundLine.getWarehouseId(),
                    searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate(), searchOutboundLine.getPreOutboundNo(),
                    searchOutboundLine.getRefDocNumber(), searchOutboundLine.getLineNumber(), searchOutboundLine.getItemCode(),
                    searchOutboundLine.getSalesOrderNumber(), searchOutboundLine.getTargetBranchCode(), searchOutboundLine.getManufacturerName(),
                    searchOutboundLine.getStatusId(), searchOutboundLine.getOrderType(), searchOutboundLine.getPartnerCode());
            log.info("OutboundLine search results : " + outboundLineSearchResults.size());
            return outboundLineSearchResults;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * createOutboundLine
     *
     * @param newOutboundLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundLineV2 createOutboundLineV2(OutboundLineV2 newOutboundLine, String loginUserID) throws java.text.ParseException {
        OutboundLineV2 dbOutboundLine = new OutboundLineV2();
        log.info("newOutboundLine : " + newOutboundLine);
        BeanUtils.copyProperties(newOutboundLine, dbOutboundLine, CommonUtils.getNullPropertyNames(newOutboundLine));

        IKeyValuePair description = stagingLineV2Repository.getDescription(dbOutboundLine.getCompanyCodeId(),
                dbOutboundLine.getLanguageId(),
                dbOutboundLine.getPlantId(),
                dbOutboundLine.getWarehouseId());

        if (dbOutboundLine.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbOutboundLine.getStatusId(), dbOutboundLine.getLanguageId());
            dbOutboundLine.setStatusDescription(statusDescription);
        }

        dbOutboundLine.setCompanyDescription(description.getCompanyDesc());
        dbOutboundLine.setPlantDescription(description.getPlantDesc());
        dbOutboundLine.setWarehouseDescription(description.getWarehouseDesc());

        dbOutboundLine.setDeletionIndicator(0L);
        dbOutboundLine.setCreatedBy(loginUserID);
        dbOutboundLine.setUpdatedBy(loginUserID);
        dbOutboundLine.setCreatedOn(new Date());
        dbOutboundLine.setUpdatedOn(new Date());
        return outboundLineV2Repository.save(dbOutboundLine);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updateOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public OutboundLineV2 updateOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode, String loginUserID, OutboundLineV2 updateOutboundLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        OutboundLineV2 outboundLine = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, updateOutboundLine.getManufacturerName());
        BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
        outboundLine.setUpdatedBy(loginUserID);
        outboundLine.setUpdatedOn(new Date());

        if (updateOutboundLine != null && updateOutboundLine.getStatus() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(updateOutboundLine.getStatusId(), languageId);
            outboundLine.setStatusDescription(statusDescription);
        }

        List<OutboundLineV2> outboundLineList = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
        List<OutboundLineV2> outboundLineListStatus90 = null;
        if(outboundLineList != null && !outboundLineList.isEmpty()) {
            log.info("outboundLineList : " + outboundLineList);
            Long outboundLineCount = outboundLineList.stream().count();
            log.info("outboundLine count : " + outboundLineCount);
            outboundLineListStatus90 = outboundLineList.stream().filter(a-> a.getStatusId() == 90L).collect(Collectors.toList());
            Long outboundLineCountStatus90 = outboundLineListStatus90.stream().count();
            log.info("outboundLineListCount : " + outboundLineCountStatus90);
            if(outboundLineCount.equals(outboundLineCountStatus90)){
                OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
                if(outboundHeader != null) {
                    outboundHeader.setStatusId(90L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(90L, languageId);
                    outboundHeader.setStatusDescription(statusDescription);
                    outboundHeaderV2Repository.save(outboundHeader);
                    log.info("Outbound Header Updated to Status 90: " + outboundHeader);
                }
            }
        }

        outboundLineV2Repository.save(outboundLine);
        return outboundLine;
    }

    /**
     * @param loginUserID
     * @param updateOutboundLines
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineV2> updateOutboundLinesV2(String loginUserID, List<OutboundLineOutput> updateOutboundLines)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<OutboundLineV2> updatedOutboundLines = new ArrayList<>();
        List<Long> lineNumbers = new ArrayList<>();
        List<String> itemCodes = new ArrayList<>();
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        for (OutboundLineOutput updateOutboundLine : updateOutboundLines) {
            OutboundLineV2 outboundLine = getOutboundLineV2(
                    updateOutboundLine.getCompanyCodeId(),
                    updateOutboundLine.getPlantId(),
                    updateOutboundLine.getLanguageId(),
                    updateOutboundLine.getWarehouseId(),
                    updateOutboundLine.getPreOutboundNo(),
                    updateOutboundLine.getRefDocNumber(), updateOutboundLine.getPartnerCode(), updateOutboundLine.getLineNumber(),
                    updateOutboundLine.getItemCode());
            BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
            outboundLine.setUpdatedBy(loginUserID);
            outboundLine.setUpdatedOn(new Date());
            statusDescription = stagingLineV2Repository.getStatusDescription(outboundLine.getStatusId(), outboundLine.getLanguageId());
            outboundLine.setStatusDescription(statusDescription);
            outboundLine = outboundLineRepository.save(outboundLine);
            updatedOutboundLines.add(outboundLine);

            warehouseId = updateOutboundLine.getWarehouseId();
            preOutboundNo = updateOutboundLine.getPreOutboundNo();
            refDocNumber = updateOutboundLine.getRefDocNumber();
            partnerCode = updateOutboundLine.getPartnerCode();
            lineNumbers.add(updateOutboundLine.getLineNumber());
            itemCodes.add(updateOutboundLine.getItemCode());
        }
        log.info("-----outboundLines-updated----> : " + updatedOutboundLines);
        return updatedOutboundLines;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @param updateOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineV2> updateOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String preOutboundNo, String refDocNumber, String partnerCode,
                                                     String loginUserID, OutboundLineV2 updateOutboundLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<OutboundLineV2> outboundLines = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        for (OutboundLine outboundLine : outboundLines) {
            BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
            outboundLine.setUpdatedBy(loginUserID);
            outboundLine.setUpdatedOn(new Date());
            outboundLineRepository.save(outboundLine);
        }
        return outboundLines;
    }

    /**
     * deleteOutboundLine
     *
     * @param loginUserID
     * @param lineNumber
     */
    public void deleteOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                     String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                     String itemCode, String loginUserID) throws java.text.ParseException {
        OutboundLineV2 outboundLine = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
        if (outboundLine != null) {
            outboundLine.setDeletionIndicator(1L);
            outboundLine.setUpdatedBy(loginUserID);
            outboundLine.setUpdatedOn(new Date());
            outboundLineV2Repository.save(outboundLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param outboundIntegrationHeader
     * @param preOutboundNo
     * @param refDocNumber
     * @param warehouseId
     * @return
     */
    public List<OutboundLineV2> updateOutboundLineForSalesInvoice(OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                                  String preOutboundNo, String refDocNumber, String warehouseId) throws java.text.ParseException {

        List<OutboundLineV2> dbOutboundLineList = getOutboundLineV2(outboundIntegrationHeader.getCompanyCode(),
                outboundIntegrationHeader.getBranchCode(), "EN", warehouseId, preOutboundNo, refDocNumber);
        if (dbOutboundLineList != null) {
            List<OutboundLineV2> updateOutboundLineList = new ArrayList<>();
            for (OutboundLineV2 dbOutboundLine : dbOutboundLineList) {
                dbOutboundLine.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
                dbOutboundLine.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                dbOutboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
                dbOutboundLine.setDeliveryType(outboundIntegrationHeader.getDeliveryType());
                dbOutboundLine.setCustomerId(outboundIntegrationHeader.getCustomerId());
                dbOutboundLine.setCustomerName(outboundIntegrationHeader.getCustomerName());
                dbOutboundLine.setAddress(outboundIntegrationHeader.getAddress());
                dbOutboundLine.setPhoneNumber(outboundIntegrationHeader.getPhoneNumber());
                dbOutboundLine.setAlternateNo(outboundIntegrationHeader.getAlternateNo());
                dbOutboundLine.setStatus(outboundIntegrationHeader.getStatus());
                dbOutboundLine.setUpdatedOn(new Date());
                updateOutboundLineList.add(dbOutboundLine);
            }
            outboundLineV2Repository.saveAll(updateOutboundLineList);
        }

        return dbOutboundLineList;
    }

    /**
     *
     * @param outboundReversalInputList
     * @param loginUserID
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<OutboundReversalV2> batchOutboundReversal(List<InboundReversalInput> outboundReversalInputList, String loginUserID) throws ParseException, java.text.ParseException, InvocationTargetException, IllegalAccessException {
            log.info("OutboundReversal Input: " + outboundReversalInputList);
            if(outboundReversalInputList != null && !outboundReversalInputList.isEmpty()) {
                for (InboundReversalInput outboundReversalInput : outboundReversalInputList){
                    doReversalBatchV2(
                            outboundReversalInput.getCompanyCodeId(),
                            outboundReversalInput.getPlantId(),
                            outboundReversalInput.getLanguageId(),
                            outboundReversalInput.getWarehouseId(),
                            outboundReversalInput.getRefDocNumber(),
                            outboundReversalInput.getItemCode(),
                            outboundReversalInput.getManufacturerName(),
                            loginUserID);
                }
            }
            return null;
        }
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<OutboundReversalV2> doReversalBatchV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String refDocNumber, String itemCode, String manufacturerName, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<OutboundLineV2> outboundLineList =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, itemCode, manufacturerName, 0L);
        log.info("outboundLineList---------> : " + outboundLineList);

        List<OutboundReversalV2> outboundReversalList = new ArrayList<>();
        for (OutboundLineV2 outboundLine : outboundLineList) {
            Warehouse warehouse = getWarehouse(warehouseId, companyCodeId, plantId, languageId);
            /*--------------STEP 1-------------------------------------*/
            // If STATUS_ID = 57 - Reversal of QC/Picking confirmation
            if (outboundLine.getStatusId() == 57L) {
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();

                outboundLine.setDeliveryQty(0D);
                outboundLine.setReversedBy(loginUserID);
                outboundLine.setReversedOn(new Date());
                outboundLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, outboundLine.getLanguageId());
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                /*--------------STEP 2-------------------------------------
                 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values fetched
                 * from OUTBOUNDHEADER and OUTBOUNDLINE table into QCLINE table  and update STATUS_ID = 56
                 */
                List<QualityLineV2> qualityLine = qualityLineService.deleteQualityLineForReversalV2(outboundLine.getCompanyCodeId(),
                        outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(),
                        outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                        outboundLine.getLineNumber(), outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("QualityLine----------Deleted-------> : " + qualityLine);

                List<OutboundLineInterim> outboundLineInterim = qualityLineService.deleteOutboundLineInterimForReversalV2(
                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("OutboundLineInterim----------Deleted-------> : " + outboundLineInterim);

                if (qualityLine != null && qualityLine.size() > 0) {
                    for (QualityLine qualityLineData : qualityLine) {
                        List<QualityHeaderV2> qualityHeader = qualityHeaderService.deleteQualityHeaderForReversalV2(outboundLine.getCompanyCodeId(),
                                outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
                                qualityLineData.getQualityInspectionNo(), qualityLineData.getActualHeNo(), loginUserID);
                        log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                    }
                }

                /*---------------STEP 3------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from QCLINE table and
                 * pass the keys in PICKUPLINE table  and update STATUS_ID = 53
                 */
                // HAREESH 06/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLineV2> pickupLineList = pickupLineService.deletePickupLineForReversalV2(outboundLine.getCompanyCodeId(),
                        outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("PickupLine----------Deleted-------> : ");

                /*---------------STEP 3.1-----Inventory update-------------------------------
                 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=5/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                 * in INVENTORY table and update INV_QTY as (INV_QTY - DLV_QTY ) and
                 * delete the record if INV_QTY = 0 (Update 1)
                 */
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLine : pickupLineList) {
//                        InventoryV2 inventory = updateInventory1V2(pickupLine, outboundLineStatusIdBeforeUpdate);

                        //Get pickupheader for inventory update
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.getPickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(),
                                outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        /*---------------STEP 5-----OrderManagement update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table
                         * and pass the keys in ORDERMANAGEMENTLINE table and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeaderV2 pickupHeaderData : pickupHeader) {
                                List<OrderManagementLineV2> orderManagementLine = updateOrderManagementLineForReversalV2(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }

                        }

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN /PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                         * in INVENTORY table and update INV_QTY as (INV_QTY + DLV_QTY ) - (Update 2)
                         */
//                        if (inventory != null) {
                        InventoryV2 inventory = inventoryService.getInventoryV2(
                                pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                                pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
                                pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                        // HAREESH -28-08-2022 change to update allocated qty
                        if (inventory != null && pickupHeader != null) {
//								for (PickupHeader pickupHeaderData : pickupHeader) {
//									Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) + (pickupHeaderData.getPickToQty() != null ? pickupHeaderData.getPickToQty() : 0);
//									inventory.setAllocatedQuantity(ALLOC_QTY);
//                            Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            inventory.setReferenceField4(INV_QTY + ALLOC_QTY);
//                                inventory = inventoryV2Repository.save(inventory);
//                                log.info("inventory updated : " + inventory);
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
//								}
                        }
//                        }


                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        for (QualityLineV2 qualityLineData : qualityLine) {
                            String reversalType = "QUALITY";
                            Double reversedQty = qualityLineData.getQualityQty();
                            OutboundReversalV2 createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber,
                                    qualityLineData.getManufacturerName(), outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode,
                                    qualityLineData.getPickPackBarCode(), reversedQty, 60L, loginUserID,
                                    outboundLine.getSalesOrderNumber(), qualityLineData.getBarcodeId());
                            outboundReversalList.add(createdOutboundReversal);

                            /////////RECORD-2/////////////////////////////////////////////////////////////////////////////////
                            reversalType = "PICKING";
                            reversedQty = pickupLine.getPickConfirmQty();
                            createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber, outboundLine.getManufacturerName(),
                                    outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
                                    outboundLine.getStatusId(), loginUserID, outboundLine.getSalesOrderNumber(), qualityLineData.getBarcodeId());
                            outboundReversalList.add(createdOutboundReversal);
                        }

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
//                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                        Long BIN_CLASS_ID = 5L;
//                        StorageBinV2 storageBin = mastersService.getStorageBin(
//                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
//                                outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
//                        String movementDocumentNo = outboundLine.getDeliveryOrderNo();
//                        String stBin = storageBin.getStorageBin();
//                        String movementQtyValue = "N";
//                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
//                                movementQtyValue, loginUserID, false);
//                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        String movementDocumentNo = pickupLine.getPickupNumber();
                        String stBin = pickupLine.getPickedStorageBin();
                        String movementQtyValue = "P";
                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }


                    //Delete pickupheader after inventory update
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        /*---------------STEP 4-----PickupHeader update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPLINE table
                         * and pass the keys in PICKUPHEADER table and Delete PickUpHeader
                         */
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }

            /*-----------------------------Next Process----------------------------------------------------------*/
            // If STATUS_ID = 50 - Reversal of Picking Confirmation
            // HAREESH 27-08-2022 added status id 51
            if (outboundLine.getStatusId() == 50L || outboundLine.getStatusId() == 51L) {
                /*----------------------STEP 1------------------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from OUTBOUNDLINE table and
                 * pass the keys in PICKUPLINE table and update STATUS_ID=53 and Delete the record
                 */
                // HAREESH 25/11/2022 update outboundline
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();
                outboundLine.setStatusId(47L);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                // HAREESH 07/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLineV2> pickupLineList = pickupLineService.deletePickupLineForReversalV2(
                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        //get pickup header
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.getPickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        List<QualityLineV2> qualityLine = qualityLineService.deleteQualityLineForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                                outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                        log.info("QualityLine----------Deleted-------> : " + qualityLine);

                        // DELETE QUALITY_HEADER
                        List<QualityHeaderV2> dbQualityHeader = qualityHeaderService.getInitialQualityHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(),
                                pickupLine.getPickupNumber(), outboundLine.getPartnerCode());
                        if (dbQualityHeader != null && dbQualityHeader.size() > 0) {
                            for (QualityHeaderV2 qualityHeaderData : dbQualityHeader) {
                                QualityHeaderV2 qualityHeader = qualityHeaderService.deleteQualityHeaderV2(
                                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
                                        qualityHeaderData.getQualityInspectionNo(), qualityHeaderData.getActualHeNo(), loginUserID);
                                log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                            }
                        }

                        /*---------------STEP 3-----OrderManagementLine update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table and
                         * pass the keys in ORDERMANAGEMENTLINE table  and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeaderV2 pickupHeaderData : pickupHeader) {
                                List<OrderManagementLineV2> orderManagementLine = updateOrderManagementLineForReversalV2(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }
                        }

                        /*---------------STEP 3.1-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and
                         * delete the record If INV_QTY = 0 - (Update 1)
                         */
//                        updateInventory1V2(pickupLine, outboundLineStatusIdBeforeUpdate);

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
                         */
                        InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                                pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                        if (inventory != null) {
                            // HAREESH -28-08-2022 change to update allocated qty
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = Double.valueOf(0);
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            inventory.setReferenceField4(INV_QTY + ALLOC_QTY);
//                            inventory = inventoryV2Repository.save(inventory);
//                            log.info("inventory updated : " + inventory);
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
                        }

                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        String reversalType = "PICKING";
                        Double reversedQty = pickupLine.getPickConfirmQty();
                        OutboundReversalV2 createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber,
                                outboundLine.getManufacturerName(), outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode,
                                pickupLine.getPickedPackCode(), reversedQty, outboundLine.getStatusId(), loginUserID,
                                outboundLine.getSalesOrderNumber(), pickupLine.getBarcodeId());
                        outboundReversalList.add(createdOutboundReversal);
                        /****************************************************************************/

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
//                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                        Long BIN_CLASS_ID = 4L;
//                        StorageBinV2 storageBin = mastersService.getStorageBin(outboundLine.getCompanyCodeId(), outboundLine.getPlantId(),
//                                outboundLine.getLanguageId(), outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
//
//                        String movementDocumentNo = pickupLine.getRefDocNumber();
//                        String stBin = storageBin.getStorageBin();
//                        String movementQtyValue = "N";
//                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
//                                movementQtyValue, loginUserID, false);
//                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        String movementDocumentNo = pickupLine.getPickupNumber();
                        String stBin = pickupLine.getPickedStorageBin();
                        String movementQtyValue = "P";
                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }

                    //Delete pickupheader after inventory update
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }
        }

        return outboundReversalList;
    }
    /**
     * @param refDocNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<OutboundReversalV2> doReversalV2(String refDocNumber, String itemCode, String manufacturerName, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<OutboundLineV2> outboundLineList =
                outboundLineV2Repository.findByRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(refDocNumber, itemCode, manufacturerName, 0L);
        log.info("outboundLineList---------> : " + outboundLineList);

        List<OutboundReversalV2> outboundReversalList = new ArrayList<>();
        for (OutboundLineV2 outboundLine : outboundLineList) {
            Warehouse warehouse = getWarehouse(outboundLine.getWarehouseId(), outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId());
            /*--------------STEP 1-------------------------------------*/
            // If STATUS_ID = 57 - Reversal of QC/Picking confirmation
            if (outboundLine.getStatusId() == 57L) {
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();

                outboundLine.setDeliveryQty(0D);
                outboundLine.setReversedBy(loginUserID);
                outboundLine.setReversedOn(new Date());
                outboundLine.setStatusId(47L);
                statusDescription = stagingLineV2Repository.getStatusDescription(47L, outboundLine.getLanguageId());
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                /*--------------STEP 2-------------------------------------
                 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values fetched
                 * from OUTBOUNDHEADER and OUTBOUNDLINE table into QCLINE table  and update STATUS_ID = 56
                 */
                List<QualityLineV2> qualityLine = qualityLineService.deleteQualityLineForReversalV2(outboundLine.getCompanyCodeId(),
                        outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(),
                        outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                        outboundLine.getLineNumber(), outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("QualityLine----------Deleted-------> : " + qualityLine);

                List<OutboundLineInterim> outboundLineInterim = qualityLineService.deleteOutboundLineInterimForReversalV2(
                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("OutboundLineInterim----------Deleted-------> : " + outboundLineInterim);

                if (qualityLine != null && qualityLine.size() > 0) {
                    for (QualityLine qualityLineData : qualityLine) {
                        List<QualityHeaderV2> qualityHeader = qualityHeaderService.deleteQualityHeaderForReversalV2(outboundLine.getCompanyCodeId(),
                                outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
                                qualityLineData.getQualityInspectionNo(), qualityLineData.getActualHeNo(), loginUserID);
                        log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                    }
                }

                /*---------------STEP 3------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from QCLINE table and
                 * pass the keys in PICKUPLINE table  and update STATUS_ID = 53
                 */
                // HAREESH 06/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLineV2> pickupLineList = pickupLineService.deletePickupLineForReversalV2(outboundLine.getCompanyCodeId(),
                        outboundLine.getPlantId(), outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                log.info("PickupLine----------Deleted-------> : ");

                /*---------------STEP 3.1-----Inventory update-------------------------------
                 * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=5/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                 * in INVENTORY table and update INV_QTY as (INV_QTY - DLV_QTY ) and
                 * delete the record if INV_QTY = 0 (Update 1)
                 */
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLine : pickupLineList) {
//                        InventoryV2 inventory = updateInventory1V2(pickupLine, outboundLineStatusIdBeforeUpdate);

                        //Get pickupheader for inventory update
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.getPickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(),
                                outboundLine.getLanguageId(), outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        /*---------------STEP 5-----OrderManagement update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table
                         * and pass the keys in ORDERMANAGEMENTLINE table and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeaderV2 pickupHeaderData : pickupHeader) {
                                List<OrderManagementLineV2> orderManagementLine = updateOrderManagementLineForReversalV2(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }

                        }

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN /PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE
                         * in INVENTORY table and update INV_QTY as (INV_QTY + DLV_QTY ) - (Update 2)
                         */
//                        if (inventory != null) {
                        InventoryV2 inventory = inventoryService.getInventoryV2(
                                pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                                pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(),
                                pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                        // HAREESH -28-08-2022 change to update allocated qty
                        if (inventory != null && pickupHeader != null) {
//								for (PickupHeader pickupHeaderData : pickupHeader) {
//									Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) + (pickupHeaderData.getPickToQty() != null ? pickupHeaderData.getPickToQty() : 0);
//									inventory.setAllocatedQuantity(ALLOC_QTY);
//                            Double INV_QTY = inventory.getInventoryQuantity() + pickupLine.getPickConfirmQty();
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            inventory.setReferenceField4(INV_QTY + ALLOC_QTY);
//                                inventory = inventoryV2Repository.save(inventory);
//                                log.info("inventory updated : " + inventory);
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
//								}
                        }
//                        }


                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        for (QualityLineV2 qualityLineData : qualityLine) {
                            String reversalType = "QUALITY";
                            Double reversedQty = qualityLineData.getQualityQty();
                            OutboundReversalV2 createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber,
                                    qualityLineData.getManufacturerName(), outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode,
                                    qualityLineData.getPickPackBarCode(), reversedQty, 60L, loginUserID,
                                    outboundLine.getSalesOrderNumber(), qualityLineData.getBarcodeId());
                            outboundReversalList.add(createdOutboundReversal);

                            /////////RECORD-2/////////////////////////////////////////////////////////////////////////////////
                            reversalType = "PICKING";
                            reversedQty = pickupLine.getPickConfirmQty();
                            createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber, outboundLine.getManufacturerName(),
                                    outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode, qualityLineData.getPickPackBarCode(), reversedQty,
                                    outboundLine.getStatusId(), loginUserID, outboundLine.getSalesOrderNumber(), qualityLineData.getBarcodeId());
                            outboundReversalList.add(createdOutboundReversal);
                        }

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
//                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                        Long BIN_CLASS_ID = 5L;
//                        StorageBinV2 storageBin = mastersService.getStorageBin(
//                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
//                                outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
//                        String movementDocumentNo = outboundLine.getDeliveryOrderNo();
//                        String stBin = storageBin.getStorageBin();
//                        String movementQtyValue = "N";
//                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
//                                movementQtyValue, loginUserID, false);
//                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        String movementDocumentNo = pickupLine.getPickupNumber();
                        String stBin = pickupLine.getPickedStorageBin();
                        String movementQtyValue = "P";
                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }


                    //Delete pickupheader after inventory update
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        /*---------------STEP 4-----PickupHeader update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPLINE table
                         * and pass the keys in PICKUPHEADER table and Delete PickUpHeader
                         */
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }

            /*-----------------------------Next Process----------------------------------------------------------*/
            // If STATUS_ID = 50 - Reversal of Picking Confirmation
            // HAREESH 27-08-2022 added status id 51
            if (outboundLine.getStatusId() == 50L || outboundLine.getStatusId() == 51L) {
                /*----------------------STEP 1------------------------------------------------
                 * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from OUTBOUNDLINE table and
                 * pass the keys in PICKUPLINE table and update STATUS_ID=53 and Delete the record
                 */
                // HAREESH 25/11/2022 update outboundline
                //Get current status id for inventory update
                Long outboundLineStatusIdBeforeUpdate = outboundLine.getStatusId();
                outboundLine.setStatusId(47L);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);

                // HAREESH 07/09/2022 change from single line delete to multiple line delete since there maybe be multiple records for same parameter
                List<PickupLineV2> pickupLineList = pickupLineService.deletePickupLineForReversalV2(
                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                        outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                        outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                if (pickupLineList != null && !pickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        //get pickup header
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.getPickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode());
                        log.info("get pickupHeader : " + pickupHeader);

                        List<QualityLineV2> qualityLine = qualityLineService.deleteQualityLineForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(), outboundLine.getLineNumber(),
                                outboundLine.getItemCode(), outboundLine.getManufacturerName(), loginUserID);
                        log.info("QualityLine----------Deleted-------> : " + qualityLine);

                        // DELETE QUALITY_HEADER
                        List<QualityHeaderV2> dbQualityHeader = qualityHeaderService.getInitialQualityHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), outboundLine.getRefDocNumber(),
                                pickupLine.getPickupNumber(), outboundLine.getPartnerCode());
                        if (dbQualityHeader != null && dbQualityHeader.size() > 0) {
                            for (QualityHeaderV2 qualityHeaderData : dbQualityHeader) {
                                QualityHeaderV2 qualityHeader = qualityHeaderService.deleteQualityHeaderV2(
                                        outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                        outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(), refDocNumber,
                                        qualityHeaderData.getQualityInspectionNo(), qualityHeaderData.getActualHeNo(), loginUserID);
                                log.info("QualityHeader----------Deleted-------> : " + qualityHeader);
                            }
                        }

                        /*---------------STEP 3-----OrderManagementLine update-------------------------------
                         * Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE values from PICKUPHEADER table and
                         * pass the keys in ORDERMANAGEMENTLINE table  and update STATUS_ID as 47
                         */
                        // HAREESH 07/09/2022 change from single line get to multiple line get since there maybe be multiple records for same parameter
                        if (pickupHeader != null) {
                            for (PickupHeaderV2 pickupHeaderData : pickupHeader) {
                                List<OrderManagementLineV2> orderManagementLine = updateOrderManagementLineForReversalV2(pickupHeaderData, loginUserID);
                                log.info("orderManagementLine updated : " + orderManagementLine);
                            }
                        }

                        /*---------------STEP 3.1-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN of BIN_CLASS_ID=4/PACK_BARCODE as PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY - PICK_CNF_QTY ) and
                         * delete the record If INV_QTY = 0 - (Update 1)
                         */
//                        updateInventory1V2(pickupLine, outboundLineStatusIdBeforeUpdate);

                        /*---------------STEP 3.2-----Inventory update-------------------------------
                         * Pass WH_ID/_ITM_CODE/ST_BIN from PICK_ST_BIN/PACK_BARCODE from PICK_PACK_BARCODE of PICKUPLINE in
                         * INVENTORY table and update INV_QTY as (INV_QTY + PICK_CNF_QTY )- (Update 2)
                         */
                        InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                                pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                        if (inventory != null) {
                            // HAREESH -28-08-2022 change to update allocated qty
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = Double.valueOf(0);
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if(inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            inventory.setReferenceField4(INV_QTY + ALLOC_QTY);
//                            inventory = inventoryV2Repository.save(inventory);
//                            log.info("inventory updated : " + inventory);
                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + createdInventoryV2);
                        }

                        /*------------------------Record insertion in Outbound Reversal table----------------------------*/
                        /////////RECORD-1/////////////////////////////////////////////////////////////////////////////////
                        String reversalType = "PICKING";
                        Double reversedQty = pickupLine.getPickConfirmQty();
                        OutboundReversalV2 createdOutboundReversal = createOutboundReversalV2(warehouse, reversalType, refDocNumber,
                                outboundLine.getManufacturerName(), outboundLine.getPartnerCode(), outboundLine.getTargetBranchCode(), itemCode,
                                pickupLine.getPickedPackCode(), reversedQty, outboundLine.getStatusId(), loginUserID,
                                outboundLine.getSalesOrderNumber(), pickupLine.getBarcodeId());
                        outboundReversalList.add(createdOutboundReversal);
                        /****************************************************************************/

                        /*-----------------------InventoryMovement----------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 1-----------------------
//                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                        Long BIN_CLASS_ID = 4L;
//                        StorageBinV2 storageBin = mastersService.getStorageBin(outboundLine.getCompanyCodeId(), outboundLine.getPlantId(),
//                                outboundLine.getLanguageId(), outboundLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
//
//                        String movementDocumentNo = pickupLine.getRefDocNumber();
//                        String stBin = storageBin.getStorageBin();
//                        String movementQtyValue = "N";
//                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
//                                movementQtyValue, loginUserID, false);
//                        log.info("InventoryMovement created for update 1-->: " + inventoryMovement);

                        /*----------------------UPDATE-2------------------------------------------------*/
                        // Inserting record in InventoryMovement------UPDATE 2-----------------------
                        String movementDocumentNo = pickupLine.getPickupNumber();
                        String stBin = pickupLine.getPickedStorageBin();
                        String movementQtyValue = "P";
                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
                                movementQtyValue, loginUserID, false);
                        log.info("InventoryMovement created for update 2-->: " + inventoryMovement);
                    }

                    //Delete pickupheader after inventory update
                    for (PickupLineV2 pickupLine : pickupLineList) {
                        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickupHeaderForReversalV2(
                                outboundLine.getCompanyCodeId(), outboundLine.getPlantId(), outboundLine.getLanguageId(),
                                outboundLine.getWarehouseId(), outboundLine.getPreOutboundNo(),
                                outboundLine.getRefDocNumber(), outboundLine.getPartnerCode(),
                                pickupLine.getPickupNumber(), outboundLine.getLineNumber(), outboundLine.getItemCode(), loginUserID);
                        log.info("pickupHeader deleted : " + pickupHeader);
                    }
                }
            }
        }

        return outboundReversalList;
    }

    /**
     * @param pickupLine
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @param isFromDelivery
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovementV2(PickupLineV2 pickupLine, String movementDocumentNo, String storageBin,
                                                        String movementQtyValue, String loginUserID, boolean isFromDelivery)
            throws IllegalAccessException, InvocationTargetException {
        // Flag "isFromDelivery" is not used anywhere.
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(pickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(pickupLine));

        // MVT_TYP_ID
        inventoryMovement.setMovementType(3L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(2L);

        // PACK_BARCODE
        inventoryMovement.setPackBarcodes(pickupLine.getPickedPackCode());

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(movementDocumentNo);

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // MVT_QTY
        inventoryMovement.setMovementQty(pickupLine.getPickConfirmQty());

        // MVT_UOM
        inventoryMovement.setInventoryUom(pickupLine.getPickUom());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                pickupLine.getCompanyCodeId(),
                pickupLine.getPlantId(),
                pickupLine.getLanguageId(),
                pickupLine.getWarehouseId(),
                pickupLine.getManufacturerName(),
                pickupLine.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if(sumOfInvQty != null) {
        inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = sumOfInvQty - pickupLine.getPickConfirmQty();
            inventoryMovement.setReferenceField2(String.valueOf(openQty));
        }
        if(sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
            inventoryMovement.setReferenceField2("0");
        }

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(pickupLine.getPickupConfirmedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(pickupLine.getPickupCreatedOn());

        inventoryMovement.setManufacturerName(pickupLine.getManufacturerName());
        inventoryMovement.setRefDocNumber(pickupLine.getRefDocNumber());
        inventoryMovement.setCompanyDescription(pickupLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(pickupLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(pickupLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(pickupLine.getBarcodeId());
        inventoryMovement.setDescription(pickupLine.getDescription());

        return inventoryMovement;
    }

    /**
     * @param warehouse
     * @param reversalType
     * @param refDocNumber
     * @param partnerCode
     * @param itemCode
     * @param packBarcode
     * @param reversedQty
     * @param statusId
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private OutboundReversalV2 createOutboundReversalV2(Warehouse warehouse, String reversalType, String refDocNumber, String manufacturerName,
                                                        String partnerCode, String targetBranchCode, String itemCode, String packBarcode,
                                                        Double reversedQty, Long statusId, String loginUserID, String salesOrderNumber, String barcodeId)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
//        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
//        StatusId idStatus = idmasterService.getStatus(47L, warehouse.getWarehouseId(), idmasterAuthToken.getAccess_token());
        OutboundReversalV2 newOutboundReversal = new OutboundReversalV2();

        // LANG_ID
        newOutboundReversal.setLanguageId(warehouse.getLanguageId());

        // C_ID - C_ID of the selected REF_DOC_NO
        newOutboundReversal.setCompanyCodeId(warehouse.getCompanyCodeId());

        // PLANT_ID
        newOutboundReversal.setPlantId(warehouse.getPlantId());

        // WH_ID
        newOutboundReversal.setWarehouseId(warehouse.getWarehouseId());
        newOutboundReversal.setTargetBranchCode(targetBranchCode);

        // OB_REVERSAL_NO
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 13  in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and insert
         */
        long NUM_RAN_CODE = 13;
        String OB_REVERSAL_NO = getNextRangeNumber(NUM_RAN_CODE, warehouse.getCompanyCodeId(), warehouse.getPlantId(), warehouse.getLanguageId(), warehouse.getWarehouseId());
        log.info("OB_REVERSAL_NO :" + OB_REVERSAL_NO);
        newOutboundReversal.setOutboundReversalNo(OB_REVERSAL_NO);

        // REVERSAL_TYPE
        newOutboundReversal.setReversalType(reversalType);

        // REF_DOC_NO
        newOutboundReversal.setRefDocNumber(refDocNumber);

        // PARTNER_CODE
        newOutboundReversal.setPartnerCode(partnerCode);

        // ITM_CODE
        newOutboundReversal.setItemCode(itemCode);
        newOutboundReversal.setManufacturerName(manufacturerName);
        newOutboundReversal.setSalesOrderNumber(salesOrderNumber);
        newOutboundReversal.setBarcodeId(barcodeId);

        // PACK_BARCODE
        newOutboundReversal.setPackBarcode(packBarcode);

        // REV_QTY
        newOutboundReversal.setReversedQty(reversedQty);

        // STATUS_ID
        newOutboundReversal.setStatusId(statusId);
        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, warehouse.getLanguageId());
        newOutboundReversal.setReferenceField10(statusDescription);
        newOutboundReversal.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                warehouse.getLanguageId(),
                warehouse.getPlantId(),
                warehouse.getWarehouseId());

        newOutboundReversal.setCompanyDescription(description.getCompanyDesc());
        newOutboundReversal.setPlantDescription(description.getPlantDesc());
        newOutboundReversal.setWarehouseDescription(description.getWarehouseDesc());

        OutboundReversalV2 outboundReversal =
                outboundReversalService.createOutboundReversalV2(newOutboundReversal, loginUserID);
        log.info("OutboundReversal created : " + outboundReversal);
        return outboundReversal;
    }


    /**
     * @param pickupHeader
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private OrderManagementLine updateOrderManagementLineV2(PickupHeader pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        UpdateOrderManagementLine updateOrderManagementLine = new UpdateOrderManagementLine();
        updateOrderManagementLine.setPickupNumber(null);
        updateOrderManagementLine.setStatusId(43L);

        //HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null
//		OrderManagementLine orderManagementLine = orderManagementLineService.updateOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
//				pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
//				pickupHeader.getItemCode(), loginUserID, updateOrderManagementLine);

        List<OrderManagementLine> dbOrderManagementLines = orderManagementLineService.getOrderManagementLine(pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
                pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(), pickupHeader.getItemCode());
        OrderManagementLine updatedOrderManagementLine = null;
        for (OrderManagementLine dbOrderManagementLine : dbOrderManagementLines) {
            BeanUtils.copyProperties(updateOrderManagementLine, dbOrderManagementLine, CommonUtils.getNullPropertyNames(updateOrderManagementLine));
            dbOrderManagementLine.setPickupUpdatedBy(loginUserID);
            dbOrderManagementLine.setPickupNumber(null);
            dbOrderManagementLine.setStatusId(43L);
            dbOrderManagementLine.setPickupUpdatedOn(new Date());
            updatedOrderManagementLine = orderManagementLineRepository.save(dbOrderManagementLine);
            log.info("OrderManagementLine updated : " + updatedOrderManagementLine);
        }
        return updatedOrderManagementLine;
    }

    /**
     * @param pickupHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private List<OrderManagementLineV2> updateOrderManagementLineForReversalV2(PickupHeaderV2 pickupHeader, String loginUserID) throws IllegalAccessException, InvocationTargetException {
        //HAREESH - 27-08-2022 pickup number null update was reflected due to bean util null property ignore , so wrote the method here and manually set pickupnumber to null

        List<OrderManagementLineV2> dbOrderManagementLine = orderManagementLineService.getListOrderManagementLineV2(
                pickupHeader.getCompanyCodeId(), pickupHeader.getPlantId(), pickupHeader.getLanguageId(),
                pickupHeader.getWarehouseId(), pickupHeader.getPreOutboundNo(),
                pickupHeader.getRefDocNumber(), pickupHeader.getPartnerCode(), pickupHeader.getLineNumber(),
                pickupHeader.getItemCode());
//        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
//        StatusId idStatus = idmasterService.getStatus(47L, pickupHeader.getWarehouseId(), idmasterAuthToken.getAccess_token());
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, pickupHeader.getLanguageId());
        if (dbOrderManagementLine != null && !dbOrderManagementLine.isEmpty()) {
            List<OrderManagementLineV2> orderManagementLineList = new ArrayList<>();
            dbOrderManagementLine.forEach(data -> {
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupNumber(null);
                data.setAllocatedQty(0D);                        // HAREESH 25/11/2022
                data.setStatusId(47L);
                data.setReferenceField7(statusDescription);    // ref_field_7
                data.setStatusDescription(statusDescription);
                data.setPickupUpdatedOn(new Date());
                orderManagementLineList.add(data);
            });

            List<OrderManagementLineV2> orderManagementLine = orderManagementLineV2Repository.saveAll(orderManagementLineList);
            log.info("OrderManagementLine updated : " + orderManagementLine);
            if (orderManagementLine.size() > 1) {
                log.info("Delete OrderManagementLines if more that one line : ");
                int i = 0;
                List<OrderManagementLineV2> deleteOrderManagementLineList = new ArrayList<>();
                for (OrderManagementLineV2 line : orderManagementLine) {
                    if (i != 0) {
                        line.setDeletionIndicator(1L);
                        deleteOrderManagementLineList.add(line);
                    }
                    i++;
                }
                log.info("OrderManagementLines for delete : " + deleteOrderManagementLineList);
                orderManagementLineV2Repository.saveAll(deleteOrderManagementLineList);
                log.info("OrderManagementLines deleted : ");
            }
            return orderManagementLine;
        } else {
            return null;
        }
    }

    /**
     * @param pickupLine
     * @param loginUserID
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private PickupHeader updatePickupHeaderV2(PickupLine pickupLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        UpdatePickupHeader updatePickupHeader = new UpdatePickupHeader();
        updatePickupHeader.setStatusId(53L);
        PickupHeader pickupHeader = pickupHeaderService.updatePickupHeader(pickupLine.getWarehouseId(),
                pickupLine.getPreOutboundNo(), pickupLine.getRefDocNumber(), pickupLine.getPartnerCode(),
                pickupLine.getPickupNumber(), pickupLine.getLineNumber(), pickupLine.getItemCode(),
                loginUserID, updatePickupHeader);
        log.info("pickupHeader updated : " + pickupHeader);
        return pickupHeader;
    }

    /**
     * @param pickupLine
     * @param
     * @return
     */
    private InventoryV2 updateInventory1V2(PickupLineV2 pickupLine, Long statusId) {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Long BIN_CLASS_ID = 5L;
        if (statusId == 50L) {
            BIN_CLASS_ID = 4L;
        }
        StorageBinV2 storageBin = mastersService.getStorageBin(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                pickupLine.getWarehouseId(), BIN_CLASS_ID, authTokenForMastersService.getAccess_token());

        InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), storageBin.getStorageBin());
        if (inventory != null) {
            Double INV_QTY = inventory.getInventoryQuantity() - pickupLine.getPickConfirmQty();
            inventory.setInventoryQuantity(INV_QTY);

            // INV_QTY > 0 then, update Inventory Table
//            inventory = inventoryV2Repository.save(inventory);
//            log.info("Inventory updated : " + inventory);
            InventoryV2 newInventoryV2 = new InventoryV2();
            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
            inventory.setInventoryId(System.currentTimeMillis());
            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
            log.info("InventoryV2 created : " + createdInventoryV2);

            if (INV_QTY == 0) {
                log.info("inventory record is deleted...");
            }
        }
        return inventory;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param deliveryQty
     * @param deliveryOrderNo
     * @param statusId
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public boolean updateOutboundLineByQLCreateProc(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                    String itemCode, Double deliveryQty, String deliveryOrderNo, Long statusId, String statusDescription)
            throws IllegalAccessException, InvocationTargetException {
        outboundLineV2Repository.updateOBlineByQLCreateProcedure(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode, deliveryQty, deliveryOrderNo, statusDescription, statusId);
        log.info("------updateOutboundLineByProc-------> : " + statusId + " updated...");
        return true;
    }

    /**
     * @param searchOutboundLine
     * @return
     * @throws ParseException
     */
    public List<StockMovementReport> findLinesForStockMovementV2(SearchOutboundLineV2 searchOutboundLine)
            throws ParseException {
        try {
            if (searchOutboundLine.getFromDeliveryDate() != null && searchOutboundLine.getToDeliveryDate() != null) {
                Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundLine.getFromDeliveryDate(),
                        searchOutboundLine.getToDeliveryDate());
                searchOutboundLine.setFromDeliveryDate(dates[0]);
                searchOutboundLine.setToDeliveryDate(dates[1]);
            }
            if(searchOutboundLine.getManufacturerName() == null || searchOutboundLine.getManufacturerName().isEmpty()){
                searchOutboundLine.setManufacturerName(null);
            }

            log.info("searchOutboundLine.getWarehouseId() :  " + searchOutboundLine.getWarehouseId());
            List<StockMovementReportImpl> allLineData = new ArrayList<>();
            List<StockMovementReportImpl> outboundLineSearchResults =
                    pickupLineV2Repository.findPickupLineForStockMovement(searchOutboundLine.getItemCode(),
                            searchOutboundLine.getManufacturerName(),
                            searchOutboundLine.getWarehouseId(),
                            searchOutboundLine.getCompanyCodeId(),
                            searchOutboundLine.getPlantId(),
                            searchOutboundLine.getLanguageId(),
                            50L, searchOutboundLine.getFromDeliveryDate(), searchOutboundLine.getToDeliveryDate());

            List<StockMovementReportImpl> inboundLineSearchResults =
                    inboundLineV2Repository.findInboundLineForStockMovement(searchOutboundLine.getItemCode(),
                            searchOutboundLine.getManufacturerName(),
                            searchOutboundLine.getWarehouseId(),
                            searchOutboundLine.getCompanyCodeId(),
                            searchOutboundLine.getPlantId(),
                            searchOutboundLine.getLanguageId(),
                            Arrays.asList(24L));

            allLineData.addAll(outboundLineSearchResults);
            allLineData.addAll(inboundLineSearchResults);

            List<StockMovementReport> stockMovementReports = new ArrayList<>();
            allLineData.forEach(data -> {
                StockMovementReport stockMovementReport = new StockMovementReport();
                BeanUtils.copyProperties(data, stockMovementReport, CommonUtils.getNullPropertyNames(data));
                if (data.getConfirmedOn() == null) {
                    Date date = inboundLineV2Repository.findDateFromPutawayLine(stockMovementReport.getDocumentNumber(),
                            stockMovementReport.getItemCode(), stockMovementReport.getManufacturerSKU(),
                            stockMovementReport.getWarehouseId(),
                            stockMovementReport.getCompanyCodeId(),
                            stockMovementReport.getPlantId(),
                            stockMovementReport.getLanguageId());
                    stockMovementReport.setConfirmedOn(date);
                }
                stockMovementReports.add(stockMovementReport);
            });
            log.info("stockMovementReports :  " + stockMovementReports);
            stockMovementReports.sort(Comparator.comparing(StockMovementReport::getConfirmedOn));
            return stockMovementReports;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineV2> deliveryConfirmationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        /*--------------------OutboundLine-Check---------------------------------------------------------------------------*/
        List<Long> statusIds = Arrays.asList(59L);
        long outboundLineProcessedCount = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds);
        log.info("outboundLineProcessedCount : " + outboundLineProcessedCount);
        boolean isAlreadyProcessed = (outboundLineProcessedCount > 0 ? true : false);
        log.info("outboundLineProcessed Already Processed? : " + isAlreadyProcessed);
        if (isAlreadyProcessed) {
            throw new BadRequestException("Order is already processed.");
        }

        /*--------------------OrderManagementLine-Check---------------------------------------------------------------------*/
        // OrderManagementLine checking for STATUS_ID - 42L, 43L
        long orderManagementLineCount = orderManagementLineService.getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, Arrays.asList(42L, 43L));
        boolean isConditionMet = (orderManagementLineCount > 0 ? true : false);
        log.info("orderManagementLineCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("OrderManagementLine is not completely Processed.");
        }

        /*--------------------PickupHeader-Check---------------------------------------------------------------------*/
        // PickupHeader checking for STATUS_ID - 48
        long pickupHeaderCount = pickupHeaderService.getPickupHeaderCountForDeliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L);
        isConditionMet = (pickupHeaderCount > 0 ? true : false);
        log.info("pickupHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Pickup is not completely Processed.");
        }

        // QualityHeader checking for STATUS_ID - 54
        long qualityHeaderCount = qualityHeaderService.getQualityHeaderCountForDeliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 54L);
        isConditionMet = (qualityHeaderCount > 0 ? true : false);
        log.info("qualityHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Quality check is not completely Processed.");
        }

        //----------------------------------------------------------------------------------------------------------
//        List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L, 41L);
        List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L);
        long outboundLineListCount = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIdsToBeChecked);
        log.info("outboundLineListCount : " + outboundLineListCount);
        isConditionMet = (outboundLineListCount > 0 ? true : false);
        log.info("isConditionMet : " + isConditionMet);

        if (!isConditionMet) {
            throw new BadRequestException("OutboundLine: Order is not completely Processed.");
        } else {
            log.info("Order can be Processed.");
        }

            try {
                Long STATUS_ID_59 = 59L;
                List<Long> statusId57 = Arrays.asList(57L);
                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID_59, languageId);
                List<OutboundLineV2> outboundLineByStatus57List = findOutboundLineByStatusV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusId57);

                // ----------------OutboundLine update-----------------------------------------------------------------------------------------
                List<Long> lineNumbers = outboundLineByStatus57List.stream().map(OutboundLine::getLineNumber).collect(Collectors.toList());
                List<String> itemCodes = outboundLineByStatus57List.stream().map(OutboundLine::getItemCode).collect(Collectors.toList());
                Date deliveryConfirmedOn = new Date();
                outboundLineV2Repository.updateOutboundLineStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, lineNumbers, deliveryConfirmedOn);
                log.info("OutboundLine updated ");

                //----------------Outbound Header update----------------------------------------------------------------------------------------
                log.info("c_id, plant_id, lang_id, wh_id, ref_doc_no, status_id, Status_desc, date:---->OBH Update----> "
                        + companyCodeId + "," + plantId + "," + languageId + "," + warehouseId + "," + refDocNumber + "," + STATUS_ID_59 + "," + statusDescription + "," + new Date());
                outboundHeaderV2Repository.updateOutboundHeaderStatusNewV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, new Date());
                OutboundHeaderV2 isOrderConfirmedOutboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
                log.info("OutboundHeader updated----1---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
                if (isOrderConfirmedOutboundHeader.getStatusId() != 59L) {
                    log.info("OutboundHeader is still updated not updated.");
                    log.info("Updating again OutboundHeader.");
                    isOrderConfirmedOutboundHeader.setStatusId(STATUS_ID_59);
                    isOrderConfirmedOutboundHeader.setStatusDescription(statusDescription);
                    isOrderConfirmedOutboundHeader.setUpdatedBy(loginUserID);
                    isOrderConfirmedOutboundHeader.setUpdatedOn(new Date());
                    isOrderConfirmedOutboundHeader.setDeliveryConfirmedOn(new Date());
                    outboundHeaderV2Repository.saveAndFlush(isOrderConfirmedOutboundHeader);
                    log.info("OutboundHeader updated---2---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
                }

                //----------------Preoutbound Line----------------------------------------------------------------------------------------------
                preOutboundLineV2Repository.updatePreOutboundLineStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
                log.info("PreOutbound Line updated");

                //----------------Preoutbound Header--------------------------------------------------------------------------------------------
                preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
                log.info("PreOutbound Header updated");

                //----------------OrderManagement Line--------------------------------------------------------------------------------------------
                orderManagementLineV2Repository.updateOrderManagementLineStatus(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
                log.info("OrderManagement Line updated");

                /*-----------------Inventory Updates---------------------------*/
//                List<QualityLineV2> dbQualityLine = qualityLineService.getQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
//                Long BIN_CL_ID = 5L;
//                for (QualityLineV2 qualityLine : dbQualityLine) {
//                    //------------Update Lock applied---------------------------------------------------------------------------------
//                    List<InventoryV2> inventoryList = inventoryService.getInventoryForDeliveryConfirmationV2(
//                            qualityLine.getCompanyCodeId(), qualityLine.getPlantId(), qualityLine.getLanguageId(), qualityLine.getWarehouseId(),
//                            qualityLine.getItemCode(), qualityLine.getPickPackBarCode(), BIN_CL_ID);
//                    for (InventoryV2 inventory : inventoryList) {
//                        Double INV_QTY = inventory.getInventoryQuantity() - qualityLine.getQualityQty();
//
//                        if (INV_QTY < 0) {
//                            INV_QTY = 0D;
//                        }
//
//                        if (INV_QTY >= 0) {
//                            inventory.setInventoryQuantity(INV_QTY);
//                            // INV_QTY > 0 then, update Inventory Table
////                            inventory = inventoryV2Repository.save(inventory);
//                            InventoryV2 newInventoryV2 = new InventoryV2();
//                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                            newInventoryV2.setInventoryId(System.currentTimeMillis());
//                            InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
//                            log.info("InventoryV2 created : " + createdInventoryV2);
//                        }
//                    }
//                    log.info("Inventory updated");
//                }

                /*-------------------Inserting record in InventoryMovement-------------------------------------*/
//                Long BIN_CLASS_ID = 5L;
//                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//                StorageBinV2 storageBin = mastersService.getStorageBin(companyCodeId, plantId, languageId, warehouseId, BIN_CLASS_ID, authTokenForMastersService.getAccess_token());
//                String movementDocumentNo = refDocNumber;
//                String stBin = storageBin.getStorageBin();
//                String movementQtyValue = "N";

//                List<PickupLineV2> dbPickupLine = pickupLineService.getPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes);
//                log.info("dbPickupLine: " + dbPickupLine.size());
//                if (dbPickupLine != null) {
//                    List<InventoryMovement> newInventoryMovementList = new ArrayList<>();
//                    for (PickupLineV2 pickupLine : dbPickupLine) {
//                        InventoryMovement inventoryMovement = createInventoryMovementV2(pickupLine, movementDocumentNo, stBin,
//                                movementQtyValue, loginUserID, true);
//                        newInventoryMovementList.add(inventoryMovement);
//                    }
//                    if (newInventoryMovementList.size() > 0) {
//                        inventoryMovementRepository.saveAll(newInventoryMovementList);
//                        log.info("InventoryMovement list created.");
//                    }
//                }
                return outboundLineByStatus57List;
            } catch (Exception e) {
                e.printStackTrace();
            }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<OutboundLineV2> getOutBoundLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                         String warehouseId, String refDocNumber, String itemCode, String manufacturerName) {
        List<OutboundLineV2> outboundLineV2List = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, itemCode, manufacturerName, 0L);
        log.info("PickList Cancellation - OutboundLine : " + outboundLineV2List);
        return outboundLineV2List;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutBoundLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                         String warehouseId, String refDocNumber) {
        List<OutboundLineV2> outboundLineV2List = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("PickList Cancellation - OutboundLine : " + outboundLineV2List);
        return outboundLineV2List;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //Delete OutBoundLine
    public List<OutboundLineV2> deleteOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) throws Exception {

        List<OutboundLineV2> outboundLineV2List = new ArrayList<>();
        List<OutboundLineV2> dbOutBoundLine = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OutboundLine : " + dbOutBoundLine);
        if (dbOutBoundLine != null && !dbOutBoundLine.isEmpty()) {
            for (OutboundLineV2 outboundLineV2 : dbOutBoundLine) {
                outboundLineV2.setDeletionIndicator(1L);
                outboundLineV2.setUpdatedBy(loginUserID);
                outboundLineV2.setUpdatedOn(new Date());
                OutboundLineV2 outboundLine = outboundLineV2Repository.save(outboundLineV2);
                outboundLineV2List.add(outboundLine);
            }
        }
        return outboundLineV2List;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     */
    public List<OutboundLineInterim> deleteOutboundLineInterimForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                                        String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) {
        List<OutboundLineInterim> listOutboundLineInterim = outboundLineInterimRepository.
                findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - OutboundLine Interim: " + listOutboundLineInterim);
        if (listOutboundLineInterim != null && !listOutboundLineInterim.isEmpty()) {
            listOutboundLineInterim.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setUpdatedBy(loginUserID);
                data.setUpdatedOn(new Date());
            });
            return outboundLineInterimRepository.saveAll(listOutboundLineInterim);
        }
        return listOutboundLineInterim;
    }
}