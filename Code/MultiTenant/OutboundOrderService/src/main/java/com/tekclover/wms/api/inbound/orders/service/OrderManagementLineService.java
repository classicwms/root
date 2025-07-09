package com.tekclover.wms.api.inbound.orders.service;


import com.tekclover.wms.api.inbound.orders.config.PropertiesConfig;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.*;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.SearchOrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.SearchPickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.SearchQualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.repository.specification.OrderManagementLineV2Specification;
import com.tekclover.wms.api.inbound.orders.repository.specification.PickupHeaderV2Specification;
import com.tekclover.wms.api.inbound.orders.repository.specification.QualityHeaderV2Specification;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class OrderManagementLineService extends BaseService {


    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;


    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    OutboundLineRepository outboundLineRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    String statusDescription = null;
    @Autowired
    PropertiesConfig propertiesConfig;

    @Autowired
    InventoryV2Repository inventoryV2Repository;
    @Autowired
    UnallocatedOrderLineService unallocatedOrderLineService;
    @Autowired
    MastersService mastersService;
    @Autowired
    StrategiesService strategiesService;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    InventoryTransRepository inventoryTransRepository;

    //Streaming
    public Stream<OrderManagementLineV2> findOrderManagementLineV2(SearchOrderManagementLineV2 searchOrderManagementLine)
            throws ParseException, java.text.ParseException {

        if (searchOrderManagementLine.getStartRequiredDeliveryDate() != null
                && searchOrderManagementLine.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartRequiredDeliveryDate(),
                    searchOrderManagementLine.getEndRequiredDeliveryDate());
            searchOrderManagementLine.setStartRequiredDeliveryDate(dates[0]);
            searchOrderManagementLine.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchOrderManagementLine.getStartOrderDate() != null
                && searchOrderManagementLine.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOrderManagementLine.getStartOrderDate(),
                    searchOrderManagementLine.getEndOrderDate());
            searchOrderManagementLine.setStartOrderDate(dates[0]);
            searchOrderManagementLine.setEndOrderDate(dates[1]);
        }
        OrderManagementLineV2Specification spec = new OrderManagementLineV2Specification(searchOrderManagementLine);
        Stream<OrderManagementLineV2> searchResults = orderManagementLineV2Repository.stream(spec, OrderManagementLineV2.class);

        return searchResults;
    }

    //Stream - Find
    public Stream<PickupHeaderV2> findPickupHeaderV2(SearchPickupHeaderV2 searchPickupHeader)
            throws ParseException {
        PickupHeaderV2Specification spec = new PickupHeaderV2Specification(searchPickupHeader);
        Stream<PickupHeaderV2> results = pickupHeaderV2Repository.stream(spec, PickupHeaderV2.class);
        return results;
    }

    //Stream
    public Stream<QualityHeaderV2> findQualityHeaderNewV2(SearchQualityHeaderV2 searchQualityHeader) throws ParseException {
        QualityHeaderV2Specification spec = new QualityHeaderV2Specification(searchQualityHeader);
        Stream<QualityHeaderV2> results = qualityHeaderV2Repository.stream(spec, QualityHeaderV2.class).parallel();
        return results;
    }

    /**
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
     * @param searchOutboundHeader
     * @return
     * @throws ParseException
     * @throws java.text.ParseException
     */
    @Transactional(readOnly = true)
    public List<OutboundHeaderV2Stream> findOutboundHeadernewV2(SearchOutboundHeaderV2 searchOutboundHeader)        //Streaming
            throws ParseException, java.text.ParseException {

        if (searchOutboundHeader.getStartRequiredDeliveryDate() != null && searchOutboundHeader.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate());
            searchOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
            searchOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
        } else {
            searchOutboundHeader.setStartRequiredDeliveryDate(null);
            searchOutboundHeader.setEndRequiredDeliveryDate(null);
        }

        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {

            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
            searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
            searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);

        } else {

            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);

        }
        if (searchOutboundHeader.getStartDeliveryConfirmedOn() != null && searchOutboundHeader.getEndDeliveryConfirmedOn() != null) {
//            if (flag != 1) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn());
            searchOutboundHeader.setStartDeliveryConfirmedOn(dates[0]);
            searchOutboundHeader.setEndDeliveryConfirmedOn(dates[1]);
//            }
        } else {
            searchOutboundHeader.setStartDeliveryConfirmedOn(null);
            searchOutboundHeader.setEndDeliveryConfirmedOn(null);
        }

        if (searchOutboundHeader.getStartOrderDate() != null && searchOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());
            searchOutboundHeader.setStartOrderDate(dates[0]);
            searchOutboundHeader.setEndOrderDate(dates[1]);
        } else {
            searchOutboundHeader.setStartOrderDate(null);
            searchOutboundHeader.setEndOrderDate(null);
        }

        if (searchOutboundHeader.getWarehouseId() == null || searchOutboundHeader.getWarehouseId().isEmpty()) {
            searchOutboundHeader.setWarehouseId(null);
        }
        if (searchOutboundHeader.getRefDocNumber() == null || searchOutboundHeader.getRefDocNumber().isEmpty()) {
            searchOutboundHeader.setRefDocNumber(null);
        }
        if (searchOutboundHeader.getPartnerCode() == null || searchOutboundHeader.getPartnerCode().isEmpty()) {
            searchOutboundHeader.setPartnerCode(null);
        }
        if (searchOutboundHeader.getOutboundOrderTypeId() == null || searchOutboundHeader.getOutboundOrderTypeId().isEmpty()) {
            searchOutboundHeader.setOutboundOrderTypeId(null);
        }
        if (searchOutboundHeader.getSoType() == null || searchOutboundHeader.getSoType().isEmpty()) {
            searchOutboundHeader.setSoType(null);
        }
        if (searchOutboundHeader.getStatusId() == null || searchOutboundHeader.getStatusId().isEmpty()) {
            searchOutboundHeader.setStatusId(null);
        }

        Stream<OutboundHeaderV2Stream> spec = outboundHeaderV2Repository.findAllOutBoundHeader(
                searchOutboundHeader.getCompanyCodeId(),
                searchOutboundHeader.getPlantId(),
                searchOutboundHeader.getLanguageId(),
                searchOutboundHeader.getWarehouseId(),
                searchOutboundHeader.getRefDocNumber(),
                searchOutboundHeader.getPreOutboundNo(),
                searchOutboundHeader.getPartnerCode(),
                searchOutboundHeader.getTargetBranchCode(), searchOutboundHeader.getOutboundOrderTypeId(),
                searchOutboundHeader.getStatusId(), searchOutboundHeader.getSoType(),
                searchOutboundHeader.getStartRequiredDeliveryDate(), searchOutboundHeader.getEndRequiredDeliveryDate(),
                searchOutboundHeader.getStartDeliveryConfirmedOn(), searchOutboundHeader.getEndDeliveryConfirmedOn(),
                searchOutboundHeader.getStartOrderDate(), searchOutboundHeader.getEndOrderDate());

        List<OutboundHeaderV2Stream> outboundHeaderList = spec.collect(Collectors.toList());

        return outboundHeaderList;
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
     * @param loginUserID
     * @param updateOutboundLines
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
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
     * @param lineNumber
     * @param itemCode
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param statusId
     * @return
     */
    public long getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                         String refDocNumber, String preOutboundNo, List<Long> statusId) {
        long orderManagementLineCount = orderManagementLineV2Repository
                .getByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return orderManagementLineCount;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String preOutboundNo, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + "preOutboundNo" + preOutboundNo
                + ",lineNumber" + lineNumber + ",itemCode" + itemCode + " doesn't exist.");
    }

    /**
     * @param preOutboundNo
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                String preOutboundNo, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "companyCodeId:" + companyCodeId + "plantId:" + plantId + "languageId:" + languageId
                + "warehouseId:" + warehouseId + "preOutboundNo" + preOutboundNo
                + ",lineNumber" + lineNumber + ",itemCode" + itemCode + " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param statusId
     * @return
     */
    public long getQualityHeaderCountForDeliveryConfirmationV2(String companyCodeId, String plantId, String languageId,
                                                               String warehouseId, String refDocNumber, String preOutboundNo, Long statusId) {
        long qualityHeaderCount = qualityHeaderV2Repository.getQualityHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicatorV2(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return qualityHeaderCount;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param statusId
     * @return
     */
    public long getPickupHeaderCountForDeliveryConfirmationV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String refDocNumber, String preOutboundNo, Long statusId) {
        long pickupHeaderCount = pickupHeaderV2Repository.getPickupHeaderByWarehouseIdAndRefDocNumberAndPreOutboundNoAndStatusIdInAndDeletionIndicatorV2(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, statusId, 0L);
        return pickupHeaderCount;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
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
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId,
                                                String warehouseId, String preOutboundNo, String refDocNumber) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndReferenceField2AndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, null, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 createOrderManagementV4(String companyCodeId, String plantId, String languageId,
                                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
        log.info("Getting StockType1InventoryList Inpute Values : companyCodeId ------> " + companyCodeId + " plantId -------> " + plantId + " languageId ------> " + languageId + " warehouseId --------> "
                + warehouseId + " itemCode -----> " + itemCode + " binClassId --------> " + binClassId + " manufacturerName ---------> " + orderManagementLine.getManufacturerName());
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        List<IInventoryImpl> stockType1InventoryList = getInventoryForOrderManagementV2(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            return createEMPTYOrderManagementLineV2(orderManagementLine);
        }
        return updateAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                orderManagementLine.getManufacturerName(), binClassId, ORD_QTY,
                orderManagementLine, loginUserId);
    }

    /**
     * For Knowell
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param binClassId
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @param loginUserId
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 createOrderManagementV7(String companyCodeId, String plantId, String languageId,
                                                         Long binClassId, OrderManagementLineV2 orderManagementLine,
                                                         String warehouseId, String itemCode, Double ORD_QTY, String loginUserId) throws Exception {
        log.info("Getting StockType1InventoryList Inpute Values : companyCodeId ------> " + companyCodeId + " plantId -------> " + plantId + " languageId ------> " + languageId + " warehouseId --------> "
                + warehouseId + " itemCode -----> " + itemCode + " binClassId --------> " + binClassId + " manufacturerName ---------> " + orderManagementLine.getManufacturerName());

//        String profile = dbConfigRepository.getDbName(companyCodeId, plantId, warehouseId);
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("KNOWELL");
//        DataBaseContextHolder.setCurrentDb(profile);
        List<IInventoryImpl> stockType1InventoryList = getInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            return createEMPTYOrderManagementLineV7(orderManagementLine);
        }
        return updateAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode,
                orderManagementLine.getManufacturerName(), binClassId, ORD_QTY,
                orderManagementLine, loginUserId);
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 createEMPTYOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {

        log.info("Unallocated Orders creation started ...........");
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);

        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                orderManagementLine.getLanguageId(),
                orderManagementLine.getPlantId(),
                orderManagementLine.getWarehouseId());
        orderManagementLine.setCompanyDescription(description.getCompanyDesc());
        orderManagementLine.setPlantDescription(description.getPlantDesc());
        orderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
//        unallocatedOrderLineService.createUnallocatedOrderLine(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 createEMPTYOrderManagementLineV7(OrderManagementLineV2 orderManagementLine) {

        log.info("Unallocated Orders creation started ...........");
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        log.info("statusDesctiption ----> {}", statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);

        log.info("Input : companyCodeId ---> " + orderManagementLine.getCompanyCodeId() + ", languageId ---> " + orderManagementLine.getLanguageId() +
                ", plantId ---> " + orderManagementLine.getPlantId() + ", warehouseId ---> " + orderManagementLine.getWarehouseId());
//        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
//                orderManagementLine.getLanguageId(),
//                orderManagementLine.getPlantId(),
//                orderManagementLine.getWarehouseId());
        orderManagementLine.setBarcodeId("");
        orderManagementLine.setCompanyDescription("KNOWELL INTERNATIONAL");
        orderManagementLine.setPlantDescription("KNOWELL INTERNATIONAL PLANT");
        orderManagementLine.setWarehouseDescription("KNOWELL INTERNATIONAL WAREHOUSE");
        orderManagementLine.setStatusDescription(statusDescription);
        log.info("Unallocated OrderManagementLine created initiated...");
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }
        //List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId);
        return inventory;
    }

    /**
     * ForKnowell
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                              String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }
        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId);
        return inventory;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {

        log.info("Inventory Update Allocation Started ...........");

        String masterToken = getMasterAuthToken();
        String alternateUom = orderManagementLine.getAlternateUom();
        Long stockTypeId = 1L;
        String orderBy = null;
        String INV_STRATEGY = null;

        log.info("The Alternate UOM ------------------> {}", alternateUom);

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
        imBasicData.setPlantId(orderManagementLine.getPlantId());
        imBasicData.setLanguageId(orderManagementLine.getLanguageId());
        imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
        imBasicData.setItemCode(itemCode);
        ImBatchSerial imBatchSerial = mastersService.getImBatchSerialV2(imBasicData, masterToken);

        if (imBatchSerial != null) {
            Strategies strategies = strategiesService.getStrategies(companyCodeId, languageId, plantId, warehouseId, 2L, imBatchSerial.getSequenceIndicator());           //Outbound - Strategy type - 2; Inbound - Strategy type - 1
            if (strategies != null && strategies.getPriority1() != null) {
                INV_STRATEGY = String.valueOf(strategies.getPriority1());
            }
        }

        // Inventory Strategy Choices
        if (INV_STRATEGY == null) {
            INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        }

        boolean shelfLifeIndicator = false;
        imBasicData.setManufacturerName(manufacturerName);
        ImBasicData1 dbImBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, masterToken);
        log.info("ImBasicData1: " + dbImBasicData1);
        if (dbImBasicData1 != null) {
            if (dbImBasicData1.getShelfLifeIndicator() != null) {
                shelfLifeIndicator = dbImBasicData1.getShelfLifeIndicator();
            }
        }
        log.info("Allocation Strategy: " + INV_STRATEGY);
        log.info("shelfLifeIndicator: " + shelfLifeIndicator);

        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        List<IInventoryImpl> stockType1InventoryList =
                inventoryService.getInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());

        if (stockType1InventoryList == null || stockType1InventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        // Getting Inventory GroupBy ST_BIN wise
        List<IInventoryImpl> finalInventoryList = null;
        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
            log.info("INV_STRATEGY: " + INV_STRATEGY + shelfLifeIndicator);
            if (!shelfLifeIndicator) {
                orderBy = "iv.UTD_ON";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByCreatedOnV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            } else {
                orderBy = "iv.EXP_DATE";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByExpiryDateV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            }
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            orderBy = "iv.LEVEL_ID";
            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, alternateUom);
        }
        if (INV_STRATEGY.equalsIgnoreCase("1")) { // FIFO
            log.info("FIFO");
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, alternateUom);
            log.info("Group By Batch: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
                if (ORD_QTY <= iInventory.getInventoryQty()) {
                    orderBy = "iv.STR_NO";
                    finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom);
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (ORD_QTY > iInventory.getInventoryQty()) {
                    toBeIncluded = false;
                }
                if (!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                orderBy = "iv.LEVEL_ID";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            }
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    stockTypeId, binClassId, manufacturerName, alternateUom);

            log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + companyCodeId + " plantId ----> " + plantId + " languageId ----> " + languageId +
                    ", warehouseId -----> " + warehouseId + "itemCode -----> " + itemCode + " refDocumentNo -----> " + orderManagementLine.getRefDocNumber() + " barcodeId -------> " + orderManagementLine.getBarcodeId());

            Double INV_QTY = inventoryV2Repository.getInvCaseQty2(companyCodeId, plantId, languageId, warehouseId);
            log.info("Queried invQty2 ----------> {}", INV_QTY);
            if (INV_QTY == null) {
                INV_QTY = 0.0;
            }
            log.info("Group By LeveId: " + levelIdList.size());
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY_TOTAL : " + ORD_QTY + ", " + iInventory.getInventoryQty());

                log.info("Order Qty --------> {}", ORD_QTY);
                log.info("BagSize ------------> {}", orderManagementLine.getBagSize());
                log.info("INV_QTY queired 1 -------------> {}", INV_QTY);
                if (Objects.equals(ORD_QTY, INV_QTY)) {
                    log.info("Closed Case Allocation started !!");
                    newOrderManagementLine = fullQtyAllocation(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                } else if (Objects.equals(ORD_QTY, iInventory.getInventoryQty())) {
                    log.info("InventoryQty {}, OrderQty {} is equal ", iInventory.getInventoryQty(), ORD_QTY);
                    newOrderManagementLine = fullQtyAllocation(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                } else {
                    finalInventoryList = inventoryService.getInventoryForOrderManagementLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom,
                            iInventory.getLevelId());
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    if (!finalInventoryList.isEmpty()) {
                        newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                        return newOrderManagementLine;
                    } else {
                        return updateOrderManagementLineV2(orderManagementLine);
                    }
                }
            }
            if (newOrderManagementLine != null) {
                return newOrderManagementLine;
            } else {
                return updateOrderManagementLineV2(orderManagementLine);
            }
        }
        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {

        log.info("Inventory Update Allocation Started ...........");

        String masterToken = getMasterAuthToken();
        String alternateUom = orderManagementLine.getAlternateUom();
        Long stockTypeId = 1L;
        String orderBy = null;
        String INV_STRATEGY = null;

        log.info("The Alternate UOM ------------------> {}", alternateUom);

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(orderManagementLine.getCompanyCodeId());
        imBasicData.setPlantId(orderManagementLine.getPlantId());
        imBasicData.setLanguageId(orderManagementLine.getLanguageId());
        imBasicData.setWarehouseId(orderManagementLine.getWarehouseId());
        imBasicData.setItemCode(itemCode);
        ImBatchSerial imBatchSerial = mastersService.getImBatchSerialV2(imBasicData, masterToken);

        if (imBatchSerial != null) {
            Strategies strategies = strategiesService.getStrategies(companyCodeId, languageId, plantId, warehouseId, 2L, imBatchSerial.getSequenceIndicator());           //Outbound - Strategy type - 2; Inbound - Strategy type - 1
            if (strategies != null && strategies.getPriority1() != null) {
                INV_STRATEGY = String.valueOf(strategies.getPriority1());
            }
        }

        // Inventory Strategy Choices
        if (INV_STRATEGY == null) {
            INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
        }

        boolean shelfLifeIndicator = false;
//        imBasicData.setManufacturerName(manufacturerName);
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndDeletionIndicator(
                languageId, companyCodeId, plantId, warehouseId,
                itemCode, 0L);
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null) {
            if (imBasicData1.getShelfLifeIndicator() != null) {
                shelfLifeIndicator = imBasicData1.getShelfLifeIndicator();
            }
        }

        String MFR_PART = imBasicData1.getManufacturerPartNo();

        log.info("Allocation Strategy: " + INV_STRATEGY);
        log.info("shelfLifeIndicator: " + shelfLifeIndicator);

        OrderManagementLineV2 newOrderManagementLine = null;
        int invQtyByLevelIdCount = 0;
        int invQtyGroupByLevelIdCount = 0;
        List<IInventoryImpl> stockType1InventoryList =
                inventoryService.getInventoryForOrderManagementV7(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        MFR_PART, stockTypeId, binClassId, alternateUom);
        log.info("---updateAllocation---stockType1InventoryList-------> : " + stockType1InventoryList.size());

        if (stockType1InventoryList == null || stockType1InventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }

        // Getting Inventory GroupBy ST_BIN wise
        List<IInventoryImpl> finalInventoryList = null;
        if (INV_STRATEGY.equalsIgnoreCase("SB_CTD_ON")) { // SB_CTD_ON
            log.info("INV_STRATEGY: " + INV_STRATEGY + shelfLifeIndicator);
            if (!shelfLifeIndicator) {
                orderBy = "iv.UTD_ON";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByCreatedOnV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            } else {
                orderBy = "iv.EXP_DATE";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByExpiryDateV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            }
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_LEVEL_ID")) { // SB_LEVEL_ID
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            orderBy = "iv.LEVEL_ID";
            finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, alternateUom);
        }
        if (INV_STRATEGY.equalsIgnoreCase("1")) { // FIFO
            log.info("FIFO");
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, alternateUom);
            log.info("Group By Batch: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
                if (ORD_QTY <= iInventory.getInventoryQty()) {
                    orderBy = "iv.STR_NO";
                    finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByBatchV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId, alternateUom);
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (ORD_QTY > iInventory.getInventoryQty()) {
                    toBeIncluded = false;
                }
                if (!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                orderBy = "iv.LEVEL_ID";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, stockTypeId, binClassId, alternateUom);
            }
        }
        if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
            log.info("INV_STRATEGY: " + INV_STRATEGY);
            List<IInventory> levelIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    stockTypeId, binClassId, MFR_PART, alternateUom);

            log.info("The Given Values for getting InventoryQty : companyCodeId ---> " + companyCodeId + " plantId ----> " + plantId + " languageId ----> " + languageId +
                    ", warehouseId -----> " + warehouseId + "itemCode -----> " + itemCode + " refDocumentNo -----> " + orderManagementLine.getRefDocNumber() + " barcodeId -------> " + orderManagementLine.getBarcodeId());

            Double INV_QTY = inventoryV2Repository.getInvCaseQty2(companyCodeId, plantId, languageId, warehouseId);
            log.info("Queried invQty2 ----------> {}", INV_QTY);
            if (INV_QTY == null) {
                INV_QTY = 0.0;
            }
            log.info("Group By LeveId: " + levelIdList.size());
            List<String> invQtyByLevelIdList = new ArrayList<>();
            boolean toBeIncluded = true;
            for (IInventory iInventory : levelIdList) {
                log.info("ORD_QTY, INV_QTY_TOTAL : " + ORD_QTY + ", " + iInventory.getInventoryQty());

                log.info("Order Qty --------> {}", ORD_QTY);
                log.info("BagSize ------------> {}", orderManagementLine.getBagSize());
                log.info("INV_QTY queired 1 -------------> {}", INV_QTY);
                if (Objects.equals(ORD_QTY, INV_QTY)) {
                    log.info("Closed Case Allocation started !!");
                    newOrderManagementLine = fullQtyAllocationV7(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                    return newOrderManagementLine;
                } else if (Objects.equals(ORD_QTY, iInventory.getInventoryQty())) {
                    log.info("InventoryQty {}, OrderQty {} is equal ", iInventory.getInventoryQty(), ORD_QTY);
                    newOrderManagementLine = fullQtyAllocationV7(iInventory, companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom, loginUserID, ORD_QTY, orderManagementLine);
                    return newOrderManagementLine;
                } else if (ORD_QTY < iInventory.getInventoryQty()) {
                    orderBy = "iv.LEVEL_ID";
                    finalInventoryList = inventoryService.getInventoryForOrderManagementLevelIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            MFR_PART, stockTypeId, binClassId, alternateUom,
                            iInventory.getLevelId());
                    log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                    newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, MFR_PART,
                            binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (ORD_QTY > iInventory.getInventoryQty()) {
                    toBeIncluded = false;
                }
                if (!toBeIncluded) {
                    invQtyByLevelIdList.add("True");
                }
            }
            invQtyByLevelIdCount = levelIdList.size();
            invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
            log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
            if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                return newOrderManagementLine;
            }
            if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                orderBy = "iv.LEVEL_ID";
                finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        MFR_PART, stockTypeId, binClassId, alternateUom);
            }

//            if(newOrderManagementLine != null) {
//                return newOrderManagementLine;
//            } else {
//                return updateOrderManagementLineV2(orderManagementLine);
//            }
        }
        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
        if (finalInventoryList != null && !finalInventoryList.isEmpty()) {
            return updateOrderManagementLineV2(orderManagementLine);
        }
//        log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");
//
//        // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
//        if (finalInventoryList != null && finalInventoryList.isEmpty()) {
//            return updateOrderManagementLineV2(orderManagementLine);
//        }

        newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, MFR_PART,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);

        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }


    /**
     * @param orderManagementLine
     * @return x
     */
    private OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 updateOrderManagementLineV5(OrderManagementLineV2 orderManagementLine) {
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setBarcodeId(UUID.randomUUID().toString());
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) {
        OrderManagementLineV2 newOrderManagementLine = null;
        String alternateUom = orderManagementLine.getAlternateUom();
        outerloop:
        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
            InventoryV2 stBinInventory = inventoryService.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stBinWiseInventory.getBarcodeId(),
                    stBinWiseInventory.getStorageBin(), alternateUom);
            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);

            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
            if (stBinInventory == null) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

            if (stBinInventory != null) {

                Long STATUS_ID = 0L;
                Double ALLOC_QTY = 0D;
                String inventoryAlternateUom = stBinInventory.getAlternateUom();

                /*
                 * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
                 * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                 * ALLOC_QTY=0
                 */
                Double INV_QTY = stBinInventory.getInventoryQuantity();

                // INV_QTY
                orderManagementLine.setInventoryQty(INV_QTY);

                log.info("ORD_QTY -----> {}", ORD_QTY);
                log.info("INV_QTY -----> {}", INV_QTY);

                if (ORD_QTY <= INV_QTY) {
                    ALLOC_QTY = ORD_QTY;
                } else if (ORD_QTY > INV_QTY) {
                    ALLOC_QTY = INV_QTY;
                } else if (INV_QTY == 0) {
                    ALLOC_QTY = 0D;
                }
                log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

                if (orderManagementLine.getStatusId() == 47L) {
                    try {
                        orderManagementLineV2Repository.delete(orderManagementLine);
                        log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                    } catch (Exception e) {
                        log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                        e.printStackTrace();
                    }
                }

                orderManagementLine.setAllocatedQty(ALLOC_QTY);
                orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                // STATUS_ID
                /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                if (ORD_QTY > ALLOC_QTY) {
                    STATUS_ID = 42L;
                }

                /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                if (ORD_QTY == ALLOC_QTY) {
                    STATUS_ID = 43L;
                }

                statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                orderManagementLine.setStatusId(STATUS_ID);
                orderManagementLine.setStatusDescription(statusDescription);
                orderManagementLine.setReferenceField7(statusDescription);
                orderManagementLine.setPickupUpdatedBy(loginUserID);
                orderManagementLine.setPickupUpdatedOn(new Date());

                double allocatedQtyFromOrderMgmt = 0.0;

                /*
                 * Deleting current record and inserting new record (since UK is not allowing to
                 * update prop_st_bin and Pack_bar_codes columns
                 */
                newOrderManagementLine = new OrderManagementLineV2();
                BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

                if (newOrderManagementLine.getCompanyDescription() == null) {
                    description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                    newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                    newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                    newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
                }

                newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                if (stBinInventory.getBarcodeId() != null) {
                    newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
                }
                if (stBinInventory.getLevelId() != null) {
                    newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
                }
                newOrderManagementLine.setNoBags(stBinInventory.getNoBags() != null ? stBinInventory.getNoBags() : 0.0);
                newOrderManagementLine.setBagSize(stBinInventory.getBagSize() != null ? stBinInventory.getBagSize() : 0.0);
                newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
                newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
                newOrderManagementLine.setMrp(stBinInventory.getMrp());
                log.info("LoosePack Inventory is -----------------------> " + stBinInventory.isLoosePack());
                if (stBinInventory.isLoosePack()) {
                    newOrderManagementLine.setLoosePack(1L);
                } else {
                    newOrderManagementLine.setLoosePack(0L);
                }
                OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                BigDecimal ordQty = BigDecimal.valueOf(ORD_QTY);
                BigDecimal allocQty = BigDecimal.valueOf(ALLOC_QTY);

                ordQty = ordQty.setScale(2, RoundingMode.HALF_UP);

                if (ordQty.compareTo(allocQty) > 0) {
//                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                    ORD_QTY = ordQty.doubleValue() - ALLOC_QTY; // convert back if needed
                }

                log.info("ORD_QTY After --else---createdOrderManagementLine newly created------: {}", ORD_QTY);
                log.info("allocatedQtyFromOrderMgmt ----> {}", allocatedQtyFromOrderMgmt);
                if (allocatedQtyFromOrderMgmt > 0) {

                    double[] inventoryQty = allocateInventory(allocatedQtyFromOrderMgmt, orderManagementLine.getNoBags(), stBinInventory.getInventoryQuantity(), stBinInventory.getAllocatedQuantity());

                    // Create new Inventory Record
                    InventoryV2 inventoryV2 = new InventoryV2();
                    BeanUtils.copyProperties(stBinInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinInventory));

                    if (inventoryQty != null && inventoryQty.length > 2) {
                        inventoryV2.setInventoryQuantity(inventoryQty[0]);
                        inventoryV2.setAllocatedQuantity(inventoryQty[1]);
                        inventoryV2.setReferenceField4(inventoryQty[2]);
                    }

                    Double bagSize = inventoryV2.getBagSize() != null ? inventoryV2.getBagSize() : 0.0D;
                    if (bagSize > inventoryV2.getInventoryQuantity()) {
                        log.info("Loose Pack True");
                        inventoryV2.setLoosePack(true);
                    }

                    inventoryV2.setReferenceDocumentNo(orderManagementLine.getRefDocNumber());
                    inventoryV2.setReferenceOrderNo(orderManagementLine.getRefDocNumber());
                    inventoryV2.setUpdatedOn(new Date());
//                    try {
////                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
//                        log.info("-----Inventory2 updated-------: " + inventoryV2);
//                    } catch (Exception e) {
//                        log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
//                        e.printStackTrace();
//                        InventoryTrans newInventoryTrans = new InventoryTrans();
//                        BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
//                        newInventoryTrans.setReRun(0L);
//                        InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
//                        log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
//                    }
                }

                log.info("ORD_QTY == ALLOC_QTY Check for Breaking Loop | " + ORD_QTY + " | " + ALLOC_QTY);
                if (ORD_QTY == ALLOC_QTY) {
                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
                    break outerloop; // If the Inventory satisfied the Ord_qty
                }
            }
        }
        return newOrderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Long binClassId, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) {
        OrderManagementLineV2 newOrderManagementLine = null;
        String alternateUom = orderManagementLine.getAlternateUom();
//        outerloop:
        for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
            InventoryV2 stBinInventory = inventoryService.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stBinWiseInventory.getBarcodeId(),
                    stBinWiseInventory.getStorageBin(), alternateUom);
            log.info("Inventory for Allocation Bin wise ---->: " + stBinInventory);

            // If the queried Inventory is empty then EMPTY orderManagementLine is created.
            if (stBinInventory == null) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

            Long STATUS_ID = 0L;
            Double ALLOC_QTY = 0D;

            /*
             * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
             * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
             * ALLOC_QTY=0
             */
            Double INV_QTY = stBinInventory.getInventoryQuantity();

            // INV_QTY
            orderManagementLine.setInventoryQty(INV_QTY);

            log.info("ORD_QTY -----> {}", ORD_QTY);
            log.info("INV_QTY -----> {}", INV_QTY);

            // Temp variable for setting ORD_QTY
            Double INCOMING_ORD_QTY = ORD_QTY;

            if (ORD_QTY <= INV_QTY) {
                ALLOC_QTY = ORD_QTY;
            } else if (ORD_QTY > INV_QTY) {
                ALLOC_QTY = INV_QTY;
            } else if (INV_QTY == 0) {
                ALLOC_QTY = 0D;
            }
            log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

            if (orderManagementLine.getStatusId() == 47L) {
                try {
                    orderManagementLineV2Repository.delete(orderManagementLine);
                    log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                } catch (Exception e) {
                    log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                    e.printStackTrace();
                }
            }

            orderManagementLine.setNoBags(stBinInventory.getNoBags() != null ? stBinInventory.getNoBags() : 0.0);
            orderManagementLine.setBagSize(stBinInventory.getBagSize() != null ? stBinInventory.getBagSize() : 0.0);
            orderManagementLine.setAllocatedQty(ALLOC_QTY);
            orderManagementLine.setReAllocatedQty(ALLOC_QTY);

            // STATUS_ID
            /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
            if (ORD_QTY > ALLOC_QTY) {
                STATUS_ID = 42L;
            }

            /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
            if (ORD_QTY == ALLOC_QTY) {
                STATUS_ID = 43L;
            }

            statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
            orderManagementLine.setStatusId(STATUS_ID);
            orderManagementLine.setStatusDescription(statusDescription);
            orderManagementLine.setReferenceField7(statusDescription);
            orderManagementLine.setPickupUpdatedBy(loginUserID);
            orderManagementLine.setPickupUpdatedOn(new Date());

            double allocatedQtyFromOrderMgmt = 0.0;

            /*
             * Deleting current record and inserting new record (since UK is not allowing to
             * update prop_st_bin and Pack_bar_codes columns
             */
            newOrderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

            if (newOrderManagementLine.getCompanyDescription() == null) {
                description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
            }

            newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
            if (stBinInventory.getBarcodeId() != null) {
                newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
            }
            if (stBinInventory.getLevelId() != null) {
                newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
            }
            log.info("LoosePack is inventory ---------> " + stBinInventory.isLoosePack());
            if (stBinInventory.isLoosePack()) {
                newOrderManagementLine.setLoosePack(1L);
            } else {
                newOrderManagementLine.setLoosePack(0L);
            }
            newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
            newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
            newOrderManagementLine.setMrp(stBinInventory.getMrp());
            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
            log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
            allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

            BigDecimal ordQty = BigDecimal.valueOf(ORD_QTY);
            BigDecimal allocQty = BigDecimal.valueOf(ALLOC_QTY);

            ordQty = ordQty.setScale(2, RoundingMode.HALF_UP);

            if (ordQty.compareTo(allocQty) > 0) {
//                    ORD_QTY = ORD_QTY - ALLOC_QTY;
                ORD_QTY = ordQty.doubleValue() - ALLOC_QTY; // convert back if needed
            }

            log.info("ORD_QTY After --else---createdOrderManagementLine newly created------: {}", ORD_QTY);
            log.info("allocatedQtyFromOrderMgmt ----> {}", allocatedQtyFromOrderMgmt);
            log.info("INCOMING_ORD_QTY == ALLOC_QTY Check for Breaking Loop | " + INCOMING_ORD_QTY + " | " + ALLOC_QTY);
            if (INCOMING_ORD_QTY.equals(ALLOC_QTY)) {   // Changed coz ord_qty and alloc_qty will always be same if there is excess inv_qty, in that case this condition fails, so instead check ord_qty = inv_qty then the condition is true.
                log.info("ORD_QTY fully allocated: " + ORD_QTY);
                return newOrderManagementLine;
//                    break outerloop; // If the Inventory satisfied the Ord_qty
            }
        }
        return newOrderManagementLine;
    }


    /**
     * @param iInventory
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param loginUserID
     * @param ORD_QTY
     * @param orderManagementLine
     */
    public OrderManagementLineV2 fullQtyAllocation(IInventory iInventory, String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom, String loginUserID,
                                                   Double ORD_QTY, OrderManagementLineV2 orderManagementLine) {

        List<IInventoryImpl> finalInventoryList = null;
        OrderManagementLineV2 newOrderManagementLine = null;

        log.info("Logic according to Closed Case Full ---------------> INV_QTY == ORD_QTY Started");
        finalInventoryList = inventoryService.getInventoryForOrderManagementLevelAsscIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom,
                iInventory.getLevelId());

        log.info("Group By LeveId Inventory Closed Case: " + finalInventoryList.size());
        newOrderManagementLine = orderAllocationV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
        log.info("newOrderManagementLine updated Closed Case ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param iInventory
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param loginUserID
     * @param ORD_QTY
     * @param orderManagementLine
     */
    public OrderManagementLineV2 fullQtyAllocationV7(IInventory iInventory, String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom, String loginUserID,
                                                     Double ORD_QTY, OrderManagementLineV2 orderManagementLine) {

        List<IInventoryImpl> finalInventoryList = null;
        OrderManagementLineV2 newOrderManagementLine = null;

        log.info("Logic according to Closed Case Full ---------------> INV_QTY == ORD_QTY Started");
        finalInventoryList = inventoryService.getInventoryForOrderManagementLevelAsscIdV6(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom,
                iInventory.getLevelId());

        log.info("Group By LeveId Inventory Closed Case: " + finalInventoryList.size());
        newOrderManagementLine = orderAllocationV7(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                binClassId, ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
        log.info("newOrderManagementLine updated Closed Case ---#--->" + newOrderManagementLine);
        return newOrderManagementLine;
    }

    /**
     * @param outboundIntegrationHeader
     * @throws Exception
     */
    public void rollback(OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws Exception {
        try {
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : LANG_ID;
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            Long outboundOrderTypeId = outboundIntegrationHeader.getOutboundOrderTypeID();
            String refDocNo = outboundIntegrationHeader.getRefDocumentNo();
            initiateRollBack(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
        } catch (Exception e) {
            log.error("Exception occurred : " + e.toString());
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNo
     * @param outboundOrderTypeId
     * @throws Exception
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    public void initiateRollBack(String companyCodeId, String plantId, String languageId, String warehouseId,
                                 String refDocNo, Long outboundOrderTypeId) throws Exception {
        try {

            List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                    companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId, 0L);

            log.info("Rollback---> 1. Inventory restore ----> " + refDocNo + ", " + outboundOrderTypeId);
            //if order management line present do un allocation
            if (orderManagementLineV2List != null && !orderManagementLineV2List.isEmpty()) {
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List) {
                    String packBarcodes = dbOrderManagementLine.getProposedPackBarCode();
                    String storageBin = dbOrderManagementLine.getProposedStorageBin();
                    InventoryV2 inventory =
                            inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                    dbOrderManagementLine.getWarehouseId(), packBarcodes, dbOrderManagementLine.getItemCode(), storageBin,
                                    dbOrderManagementLine.getManufacturerName());
                    Double invQty = inventory.getInventoryQuantity() + dbOrderManagementLine.getAllocatedQty();

                    /*
                     * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                     */
                    if (invQty < 0D) {
                        invQty = 0D;
                    }

                    inventory.setInventoryQuantity(invQty);
                    log.info("Inventory invQty: " + invQty);

                    Double allocQty = inventory.getAllocatedQuantity() - dbOrderManagementLine.getAllocatedQty();
                    if (allocQty < 0D) {
                        allocQty = 0D;
                    }
                    inventory.setAllocatedQuantity(allocQty);
                    log.info("Inventory allocQty: " + allocQty);
                    Double totQty = invQty + allocQty;
                    inventory.setReferenceField4(totQty);
                    log.info("Inventory totQty: " + totQty);

                    // Create new Inventory Record
//                    InventoryV2 inventoryV2 = new InventoryV2();
//                    BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
//                    try {
//                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
//                        log.info("-----InventoryV2 created-------: " + inventoryV2);
//                    } catch (Exception e) {
//                        log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
//                        e.printStackTrace();
//                        InventoryTrans newInventoryTrans = new InventoryTrans();
//                        BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
//                        newInventoryTrans.setReRun(0L);
//                        InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
//                        log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
//                    }
                }
                log.info("Rollback---> 1.Inventory restoration Finished ----> " + refDocNo + ", " + outboundOrderTypeId);
            }

            //delete all records from respective tables
            log.info("Rollback---> 2. delete all record initiated ----> " + refDocNo + ", " + outboundOrderTypeId);
            orderManagementLineV2Repository.deleteOutboundProcessingProc(companyCodeId, plantId, languageId, warehouseId, refDocNo, outboundOrderTypeId);
            log.info("Rollback---> 2. delete all record finished ----> " + refDocNo + ", " + outboundOrderTypeId);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param salesOrderNumber
     * @param shipToParty
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLinesShipToPartyV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                            String salesOrderNumber, String shipToParty) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + salesOrderNumber + "|" + shipToParty);
        return orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndSalesOrderNumberAndShipToPartyAndStatusIdNotAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty, 47L, 0L);
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param binClassId
     * @param ORD_QTY
     * @param orderManagementLine
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public OrderManagementLineV2 updateAllocationV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, Long binClassId, Double ORD_QTY,
                                                    OrderManagementLineV2 orderManagementLine, String loginUserID) throws Exception {
        try {
            String manufacturerName = orderManagementLine.getManufacturerName();
            log.info("Quantity Logic started ----------> ");
            setAlternateUomQuantities(orderManagementLine);
            log.info("Quantity Logic started ----------> ");
            ORD_QTY = Optional.ofNullable(orderManagementLine.getQtyInPiece()).orElse(ORD_QTY);
            log.info("ORD_QTY is ------------------> {} ", ORD_QTY);
            Long stockTypeId = 1L;
            String INV_STRATEGY = null;
            String obStrategy = null;
            String fifoMethod = null;
            if (orderManagementLine.getOutboundOrderTypeId() != 11L) {
                if (orderManagementLine.getBarcodeId() == null) {
                    if (orderManagementLine.getPartnerCode() != null) {
                        Long dbBusinessPartner = inventoryV2Repository.findPartnerType(companyCodeId, plantId, languageId, warehouseId, orderManagementLine.getPartnerCode());
                        if (dbBusinessPartner != null && dbBusinessPartner == 3L) {
                            OrderManagementLineV2 newOrderManagementLine = null; // SB_BEST_FIT
                            List<IInventoryImpl> finalInventoryList = null;
                            log.info("Business Partner Type ----->" + dbBusinessPartner);
                            List<IInventory> remainingSelfLife = null;
                            remainingSelfLife = inventoryService.getRemainingSelfLife(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                                    warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
                            log.info("RemainingSelfLife-------->" + remainingSelfLife);
                            log.info("ExpiryDate -------------> " + remainingSelfLife.get(0).getExpiryDate());
                            log.info("Group By ExpiryDate: " + remainingSelfLife.size());
                            log.info("-----------------Outbound Strategies Table Record Creating Started --------------");              // OUTBOUND_STRATEGIES
                            strategiesService.createOutboundStrategies(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                                    orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), remainingSelfLife, loginUserID, orderManagementLine.getRefDocNumber(),
                                    orderManagementLine.getAllocatedQty(), orderManagementLine.getInventoryQty(), "FIFO");
                            assert remainingSelfLife != null;
                            outerloop1:
                            for (IInventory iInventory : remainingSelfLife) {
                                log.info("ORD_QTY, INV_QTY : EX_DATE " + ORD_QTY + ", " + iInventory.getInventoryQty() + "|" + iInventory.getExpiryDate());
//                if (ORD_QTY <= iInventory.getInventoryQty()) {
                                finalInventoryList = inventoryService.getInventoryForOrderManagementGroupByExpiryDate(orderManagementLine.getCompanyCodeId(),
                                        orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                                        warehouseId, itemCode, 1L, binClassId, iInventory.getExpiryDate(), orderManagementLine.getManufacturerName());
                                log.info("Group By BarcodeId Inventory: " + finalInventoryList.size());

                                for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
                                    // Getting PackBarCode by passing ST_BIN to Inventory
                                    List<IInventoryImpl> listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2GroupByBarcodeIdV5(orderManagementLine.getCompanyCodeId(),
                                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
                                            orderManagementLine.getManufacturerName(), binClassId, stBinWiseInventory.getBarcodeId(),
                                            stBinWiseInventory.getStorageBin(), 1L);

                                    log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc.size() + "\n");

                                    // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
                                    // created.
                                    if (listInventoryForAlloc == null && listInventoryForAlloc.isEmpty()) {
                                        return updateOrderManagementLineV5(orderManagementLine);
                                    }

                                    for (IInventoryImpl stBinInventory : listInventoryForAlloc) {
                                        log.info("\nBin-wise Inventory : " + stBinInventory + "\n");

                                        Long STATUS_ID = 0L;
                                        Double ALLOC_QTY = 0D;

                                        /*
                                         * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. Ife
                                         * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                                         * ALLOC_QTY=0
                                         */
                                        Double INV_QTY = stBinInventory.getInventoryQuantity();

                                        // INV_QTY
                                        orderManagementLine.setInventoryQty(INV_QTY);

                                        if (ORD_QTY <= INV_QTY) {
                                            ALLOC_QTY = ORD_QTY;
                                        } else if (ORD_QTY > INV_QTY) {
                                            ALLOC_QTY = INV_QTY;
                                        } else if (INV_QTY == 0) {
                                            ALLOC_QTY = 0D;
                                        }
                                        log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

                                        if (orderManagementLine.getStatusId() == 47L) {
                                            try {
                                                orderManagementLineV2Repository.delete(orderManagementLine);
                                                log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                                            } catch (Exception e) {
                                                log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                                                e.printStackTrace();
                                            }
                                        }

                                        orderManagementLine.setAllocatedQty(ALLOC_QTY);
                                        orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                                        // STATUS_ID
                                        /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                                        if (ORD_QTY > ALLOC_QTY) {
                                            STATUS_ID = 42L;
                                        }

                                        /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                                        if (ORD_QTY == ALLOC_QTY) {
                                            STATUS_ID = 43L;
                                        }

                                        statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                                        orderManagementLine.setStatusId(STATUS_ID);
                                        orderManagementLine.setStatusDescription(statusDescription);
                                        orderManagementLine.setReferenceField7(statusDescription);
                                        orderManagementLine.setPickupUpdatedBy(loginUserID);
                                        orderManagementLine.setPickupUpdatedOn(new Date());
                                        orderManagementLine.setManufacturerDate(stBinInventory.getManufacturerDate());
                                        orderManagementLine.setExpiryDate(stBinInventory.getExpiryDate());
                                        orderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());


                                        double allocatedQtyFromOrderMgmt = 0.0;

                                        /*
                                         * Deleting current record and inserting new record (since UK is not allowing to
                                         * update prop_st_bin and Pack_bar_codes columns
                                         */
                                        newOrderManagementLine = new OrderManagementLineV2();
                                        BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

                                        //V2 Code
                                        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                                                orderManagementLine.getLanguageId(),
                                                orderManagementLine.getPlantId(),
                                                orderManagementLine.getWarehouseId());

                                        newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                                        newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                                        newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());

                                        newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                                        if (stBinInventory.getBarcodeId() != null) {
                                            newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
                                        }
                                        if (stBinInventory.getLevelId() != null) {
                                            newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
                                        }
                                        newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
                                        newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
                                        OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                                        log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                                        allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                                        if (ORD_QTY > ALLOC_QTY) {
                                            ORD_QTY = ORD_QTY - ALLOC_QTY;
                                        }

                                        if (allocatedQtyFromOrderMgmt > 0) {
                                            // Update Inventory table
//                                InventoryV2 inventoryForUpdate = inventoryService.getInventoryForAllocationV2(orderManagementLine.getCompanyCodeId(),
//                                        orderManagementLine.getPlantId(),
//                                        orderManagementLine.getLanguageId(), warehouseId,
//                                        stBinInventory.getPackBarcodes(), itemCode,
//                                        orderManagementLine.getManufacturerName(),
//                                        stBinInventory.getStorageBin());

                                            double dbInventoryQty = 0;
                                            double dbInvAllocatedQty = 0;

                                            if (stBinInventory.getInventoryQuantity() != null) {
                                                dbInventoryQty = stBinInventory.getInventoryQuantity();
                                            }

                                            if (stBinInventory.getAllocatedQuantity() != null) {
                                                dbInvAllocatedQty = stBinInventory.getAllocatedQuantity();
                                            }

                                            double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
                                            double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;

                                            /*
                                             * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                                             */
                                            // Start
                                            if (inventoryQty < 0) {
                                                inventoryQty = 0;
                                            }

                                            // Create new Inventory Record
                                            InventoryV2 inventoryV2 = new InventoryV2();
                                            BeanUtils.copyProperties(stBinInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinInventory));
                                            // End
                                            inventoryV2.setInventoryQuantity(round(inventoryQty));
                                            inventoryV2.setAllocatedQuantity(round(allocatedQty));
                                            inventoryV2.setReferenceField4(round(inventoryQty + allocatedQty));
                                            inventoryV2.setUpdatedOn(new Date());
                                            try {
                                                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                                                log.info("-----Inventory2 updated-------: " + inventoryV2);
                                            } catch (Exception e) {
                                                log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                                e.printStackTrace();
                                                InventoryTrans newInventoryTrans = new InventoryTrans();
                                                BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                                                newInventoryTrans.setReRun(0L);
                                                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                                            }

                                            log.info("-----------------Outbound Strategies Table Record Updated Started --------------");              // OUTBOUND_STRATEGIES
                                            strategiesService.updateStrategies(inventoryV2, loginUserID, stBinInventory.getInventoryId(), orderManagementLine.getRefDocNumber(),
                                                    orderManagementLine.getAllocatedQty(), orderManagementLine.getInventoryQty());
                                        }

                                        if (ORD_QTY == ALLOC_QTY) {
                                            log.info("ORD_QTY fully allocated: " + ORD_QTY);
                                            break outerloop1; // If the Inventory satisfied the Ord_qty
                                        }
                                    }
                                }
                                log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                            }
                            return newOrderManagementLine;
                        }
//                    List<InventoryV2> dbInventory = inventoryService.getInventoryV5(companyCodeId, plantId, languageId, warehouseId, itemCode);
//                    long percentage = Long.parseLong(dbInventory.get(0).getRemainingSelfLifePercentage());
//                    if (percentage >= 70) {

                    }
                }
            }

            if (orderManagementLine.getBarcodeId() != null && !orderManagementLine.getBarcodeId().isEmpty()) {
                log.info("BarcodeId is ------> " + orderManagementLine.getBarcodeId());
                INV_STRATEGY = "SB_BEST_FIT";
            } else {
                log.info("Starting Warehouse table call for Strategies");
                List<Object[]> strategyList = stagingLineV2Repository.getStrategy(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), warehouseId);
                if (strategyList != null && !strategyList.isEmpty()) {
                    Object[] strategy = strategyList.get(0); // GET_FIRST_RECORD
                    if (strategy.length > 0 && strategy[0] != null) {
                        obStrategy = strategy[0].toString();
                    }
                    if (strategy.length > 0 && strategy[1] != null) {
                        fifoMethod = strategy[1].toString();
                    }
                }
                log.info("OB_STRATEGY: {}, FIFO_MD: {}", obStrategy, fifoMethod);
                INV_STRATEGY = obStrategy;
            }

            // Inventory Strategy Choices
            if (INV_STRATEGY == null) {
                INV_STRATEGY = propertiesConfig.getOrderAllocationStrategyCoice();
            }

            log.info("Allocation Strategy: " + INV_STRATEGY);
            OrderManagementLineV2 newOrderManagementLine = null;
            int invQtyByLevelIdCount = 0;
            int invQtyGroupByLevelIdCount = 0;
            // Getting Inventory GroupBy ST_BIN wise
            List<IInventoryImpl> finalInventoryList = null;
            if (INV_STRATEGY.equalsIgnoreCase("SB_BEST_FIT")) { // SB_BEST_FIT
                log.info("INV_STRATEGY: " + INV_STRATEGY);
                List<IInventory> groupByBarCodeIdList = inventoryService.getInventoryForOrderManagementGroupByLevelIdV5(companyCodeId, plantId, languageId, warehouseId, itemCode,
                        stockTypeId, binClassId, manufacturerName, orderManagementLine.getBarcodeId());
                log.info("Group By LeveId: " + groupByBarCodeIdList.size());
                List<String> invQtyByLevelIdList = new ArrayList<>();
                boolean toBeIncluded = true;
                for (IInventory iInventory : groupByBarCodeIdList) {
                    log.info("ORD_QTY, INV_QTY : " + ORD_QTY + ", " + iInventory.getInventoryQty());
                    if (ORD_QTY <= iInventory.getInventoryQty()) {
                        finalInventoryList = inventoryService.getInventoryForOrderManagementLevelIdV5(companyCodeId, plantId, languageId, warehouseId, itemCode,
                                manufacturerName, stockTypeId, binClassId, orderManagementLine.getBarcodeId());
                        log.info("Group By LeveId Inventory: " + finalInventoryList.size());
                        newOrderManagementLine = orderAllocationV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                                ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);
                        log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                        return newOrderManagementLine;
                    }
                    if (ORD_QTY > iInventory.getInventoryQty()) {
                        toBeIncluded = false;
                    }
                    if (!toBeIncluded) {
                        invQtyByLevelIdList.add("True");
                    }
                }
                invQtyByLevelIdCount = groupByBarCodeIdList.size();
                invQtyGroupByLevelIdCount = invQtyByLevelIdList.size();
                log.info("invQtyByLevelIdCount, invQtyGroupByLevelIdCount" + invQtyByLevelIdCount + ", " + invQtyGroupByLevelIdCount);
                if (invQtyByLevelIdCount != invQtyGroupByLevelIdCount) {
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                    return newOrderManagementLine;
                }
                if (invQtyByLevelIdCount == invQtyGroupByLevelIdCount) {
                    finalInventoryList = inventoryService.getInventoryForOrderManagementOrderByLevelIdV5(companyCodeId, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, stockTypeId, binClassId);
                }
            }
            if (INV_STRATEGY.equalsIgnoreCase("FIFO")) { // SB_BEST_FIT
                log.info("FIFO");
                List<IInventory> barcodeIdList = null;
                //FIFO - Method
                if (fifoMethod != null && fifoMethod.equalsIgnoreCase("BARCODE")) {
                    log.info("FIFO METHOD IS BARCODE --------------->");
                    //                    barcodeIdList = inventoryService.getInventoryForOrderManagementGroupByBarcodeIdV5(orderManagementLine.getCompanyCodeId(),
                    //                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                    //                            warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
                    barcodeIdList = inventoryService.getInventoryForOrderManagementGroupByExpiryDate(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
                    log.info("ExpiryDate -------------> " + barcodeIdList.get(0).getExpiryDate());
                    log.info("Group By ExpiryDate: " + barcodeIdList.size());
                    log.info("-----------------Outbound Strategies Table Record Creating Started --------------");              // OUTBOUND_STRATEGIES
                    strategiesService.createOutboundStrategies(orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(),
                            orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), barcodeIdList, loginUserID, orderManagementLine.getRefDocNumber(),
                            orderManagementLine.getAllocatedQty(), orderManagementLine.getInventoryQty(), "FIFO");
                }
                assert barcodeIdList != null;
                outerloop1:
                for (IInventory iInventory : barcodeIdList) {
                    log.info("ORD_QTY, INV_QTY : EX_DATE " + ORD_QTY + ", " + iInventory.getInventoryQty() + "|" + iInventory.getExpiryDate());
//                if (ORD_QTY <= iInventory.getInventoryQty()) {
                    finalInventoryList = inventoryService.getInventoryForOrderManagementGroupByExpiryDate(orderManagementLine.getCompanyCodeId(),
                            orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(),
                            warehouseId, itemCode, 1L, binClassId, iInventory.getExpiryDate(), orderManagementLine.getManufacturerName());
                    log.info("Group By BarcodeId Inventory: " + finalInventoryList.size());
                    if (orderManagementLine.getOutboundOrderTypeId() == 11L) {
                        orderManagementLine.setBarcodeId(orderManagementLine.getBarcodeId());
                    }
                    for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
                        // Getting PackBarCode by passing ST_BIN to Inventory
                        List<IInventoryImpl> listInventoryForAlloc = inventoryService.getInventoryForOrderManagementV2GroupByBarcodeIdV5(orderManagementLine.getCompanyCodeId(),
                                orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), warehouseId, itemCode,
                                orderManagementLine.getManufacturerName(), binClassId, stBinWiseInventory.getBarcodeId(),
                                stBinWiseInventory.getStorageBin(), 1L);

                        log.info("\nlistInventoryForAlloc Inventory ---->: " + listInventoryForAlloc.size() + "\n");

                        // Prod Fix: If the queried Inventory is empty then EMPTY orderManagementLine is
                        // created.
                        if (listInventoryForAlloc == null && listInventoryForAlloc.isEmpty()) {
                            return updateOrderManagementLineV5(orderManagementLine);
                        }

                        for (IInventoryImpl stBinInventory : listInventoryForAlloc) {
                            log.info("\nBin-wise Inventory : " + stBinInventory + "\n");

                            Long STATUS_ID = 0L;
                            Double ALLOC_QTY = 0D;

                            /*
                             * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. Ife
                             * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                             * ALLOC_QTY=0
                             */
                            Double INV_QTY = stBinInventory.getInventoryQuantity();

                            // INV_QTY
                            orderManagementLine.setInventoryQty(INV_QTY);

                            if (ORD_QTY <= INV_QTY) {
                                ALLOC_QTY = ORD_QTY;
                            } else if (ORD_QTY > INV_QTY) {
                                ALLOC_QTY = INV_QTY;
                            } else if (INV_QTY == 0) {
                                ALLOC_QTY = 0D;
                            }
                            log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

                            if (orderManagementLine.getStatusId() == 47L) {
                                try {
                                    orderManagementLineV2Repository.delete(orderManagementLine);
                                    log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                                } catch (Exception e) {
                                    log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                                    e.printStackTrace();
                                }
                            }

                            orderManagementLine.setAllocatedQty(ALLOC_QTY);
                            orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                            // STATUS_ID
                            /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                            if (ORD_QTY > ALLOC_QTY) {
                                STATUS_ID = 42L;
                            }

                            /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                            if (ORD_QTY == ALLOC_QTY) {
                                STATUS_ID = 43L;
                            }

                            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                            orderManagementLine.setStatusId(STATUS_ID);
                            orderManagementLine.setStatusDescription(statusDescription);
                            orderManagementLine.setReferenceField7(statusDescription);
                            orderManagementLine.setPickupUpdatedBy(loginUserID);
                            orderManagementLine.setPickupUpdatedOn(new Date());
                            orderManagementLine.setManufacturerDate(stBinInventory.getManufacturerDate());
                            orderManagementLine.setExpiryDate(stBinInventory.getExpiryDate());
                            orderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());


                            double allocatedQtyFromOrderMgmt = 0.0;

                            /*
                             * Deleting current record and inserting new record (since UK is not allowing to
                             * update prop_st_bin and Pack_bar_codes columns
                             */
                            newOrderManagementLine = new OrderManagementLineV2();
                            BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

                            //V2 Code
                            IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                                    orderManagementLine.getLanguageId(),
                                    orderManagementLine.getPlantId(),
                                    orderManagementLine.getWarehouseId());

                            newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                            newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                            newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());

                            newOrderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
                            if (stBinInventory.getBarcodeId() != null) {
                                newOrderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
                            }
                            if (stBinInventory.getLevelId() != null) {
                                newOrderManagementLine.setLevelId(stBinInventory.getLevelId());
                            }
                            newOrderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
                            newOrderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
                            if (orderManagementLine.getOutboundOrderTypeId() == 11) {
                                newOrderManagementLine.setBarcodeId("Empty Crate");
                            }
                            OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.save(newOrderManagementLine);
                            log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                            allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                            if (ORD_QTY > ALLOC_QTY) {
                                ORD_QTY = ORD_QTY - ALLOC_QTY;
                            }

                            if (allocatedQtyFromOrderMgmt > 0) {
                                // Update Inventory table
//                                InventoryV2 inventoryForUpdate = inventoryService.getInventoryForAllocationV2(orderManagementLine.getCompanyCodeId(),
//                                        orderManagementLine.getPlantId(),
//                                        orderManagementLine.getLanguageId(), warehouseId,
//                                        stBinInventory.getPackBarcodes(), itemCode,
//                                        orderManagementLine.getManufacturerName(),
//                                        stBinInventory.getStorageBin());

                                double dbInventoryQty = 0;
                                double dbInvAllocatedQty = 0;

                                if (stBinInventory.getInventoryQuantity() != null) {
                                    dbInventoryQty = stBinInventory.getInventoryQuantity();
                                }

                                if (stBinInventory.getAllocatedQuantity() != null) {
                                    dbInvAllocatedQty = stBinInventory.getAllocatedQuantity();
                                }

                                double inventoryQty = dbInventoryQty - allocatedQtyFromOrderMgmt;
                                double allocatedQty = dbInvAllocatedQty + allocatedQtyFromOrderMgmt;

                                /*
                                 * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                                 */
                                // Start
                                if (inventoryQty < 0) {
                                    inventoryQty = 0;
                                }

                                // Create new Inventory Record
                                InventoryV2 inventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(stBinInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinInventory));
                                // End
                                inventoryV2.setInventoryQuantity(round(inventoryQty));
                                inventoryV2.setAllocatedQuantity(round(allocatedQty));
                                inventoryV2.setReferenceField4(round(inventoryQty + allocatedQty));
                                inventoryV2.setUpdatedOn(new Date());
                                try {
                                    inventoryV2 = inventoryV2Repository.save(inventoryV2);
                                    log.info("-----Inventory2 updated-------: " + inventoryV2);
                                } catch (Exception e) {
                                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                    e.printStackTrace();
                                    InventoryTrans newInventoryTrans = new InventoryTrans();
                                    BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                                    newInventoryTrans.setReRun(0L);
                                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                                }
                                log.info("-----------------Outbound Strategies Table Record Updated Started --------------");              // OUTBOUND_STRATEGIES
//                                strategiesService.createOutboundStrategies(inventoryV2, loginUserID);
                                strategiesService.updateStrategies(inventoryV2, loginUserID, stBinInventory.getInventoryId(), orderManagementLine.getRefDocNumber(),
                                        orderManagementLine.getAllocatedQty(), orderManagementLine.getInventoryQty());
                            }

                            if (ORD_QTY == ALLOC_QTY) {
                                log.info("ORD_QTY fully allocated: " + ORD_QTY);
                                break outerloop1; // If the Inventory satisfied the Ord_qty
                            }
                        }
                    }
                    log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
                }
                return newOrderManagementLine;
            }
            log.info("finalInventoryList Inventory ---->: " + finalInventoryList.size() + "\n");

            // If the finalInventoryList is EMPTY then we set STATUS_ID as 47 and return from the processing
            if (finalInventoryList == null || (finalInventoryList != null && finalInventoryList.isEmpty())) {
                return updateOrderManagementLineV2(orderManagementLine);
            }

            newOrderManagementLine = orderAllocationV5(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    ORD_QTY, orderManagementLine, finalInventoryList, loginUserID);

            log.info("newOrderManagementLine updated ---#--->" + newOrderManagementLine);
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while updateAllocation V3: " + e);
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param barcodeId
     */
    public void updatePutAwayHeadersV5(String companyCode, String plantId, String languageId,
                                       String warehouseId, String itemCode, String barcodeId) {
        try {
            log.info("putawayHeader Status Update: " + companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + barcodeId);
//        statusDescription = getStatusDescription(20L, languageId);
//        putAwayHeaderV2Repository.updateHeaderStatus(
//                companyCode, plantId, languageId, warehouseId, 20L, statusDescription, itemCode, barcodeId);
            PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndReferenceField5AndBarcodeIdAndStatusIdAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, itemCode, barcodeId, 19L, 0L);
            log.info("PutawayHeader : " + putAwayHeader);
            if (putAwayHeader != null) {
                PutAwayLineV2 putAwayLine = new PutAwayLineV2();
                BeanUtils.copyProperties(putAwayHeader, putAwayLine);
                putAwayLine.setCompanyCode(companyCode);
                putAwayLine.setPutawayConfirmedQty(putAwayHeader.getPutAwayQuantity());
                putAwayLine.setConfirmedStorageBin(putAwayHeader.getProposedStorageBin());
                putAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
                putAwayLine.setItemCode(putAwayHeader.getReferenceField5());
                putAwayLineConfirmNonCBMV5(putAwayLine, putAwayHeader, "STAGING_AREA");
            }
        } catch (Exception e) {
            throw new BadRequestException("Exception while creating ");
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param ORD_QTY
     * @param orderManagementLine
     * @param finalInventoryList
     * @param loginUserID
     * @return
     */
    public OrderManagementLineV2 orderAllocationV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String itemCode, String manufacturerName, Double ORD_QTY,
                                                   OrderManagementLineV2 orderManagementLine, List<IInventoryImpl> finalInventoryList,
                                                   String loginUserID) throws Exception {
        try {
            if (finalInventoryList == null || finalInventoryList.isEmpty()) {
                return updateOrderManagementLineV2(orderManagementLine);
            }
            OrderManagementLineV2 newOrderManagementLine = null;
            outerloop:
            for (IInventoryImpl stBinWiseInventory : finalInventoryList) {
//                InventoryV2 stBinInventory = inventoryService.getInventoryV5(companyCodeId, plantId, languageId, warehouseId, itemCode,
//                        manufacturerName, stBinWiseInventory.getBarcodeId(), stBinWiseInventory.getStorageBin());
//                log.info("Walkaroo Inventory for Allocation Bin wise ---->: " + stBinInventory);

                // If the queried Inventory is empty then EMPTY orderManagementLine is created.
                if (stBinWiseInventory == null) {
                    return updateOrderManagementLineV2(orderManagementLine);
                }

                if (stBinWiseInventory != null) {

                    Long STATUS_ID = 0L;
                    Double ALLOC_QTY = 0D;

                    /*
                     * ALLOC_QTY 1. If ORD_QTY< INV_QTY , then ALLOC_QTY = ORD_QTY. 2. If
                     * ORD_QTY>INV_QTY, then ALLOC_QTY = INV_QTY. If INV_QTY = 0, Auto fill
                     * ALLOC_QTY=0
                     */
                    Double INV_QTY = stBinWiseInventory.getInventoryQuantity();

                    // INV_QTY
                    orderManagementLine.setInventoryQty(INV_QTY);

                    if (ORD_QTY <= INV_QTY) {
                        ALLOC_QTY = ORD_QTY;
                    } else if (ORD_QTY > INV_QTY) {
                        ALLOC_QTY = INV_QTY;
                    } else if (INV_QTY == 0) {
                        ALLOC_QTY = 0D;
                    }

                    log.info("ALLOC_QTY -----1--->: " + ALLOC_QTY);

                    if (orderManagementLine.getStatusId() == 47L) {
                        try {
                            orderManagementLineV2Repository.delete(orderManagementLine);
                            log.info("--#---orderManagementLine--deleted----: " + orderManagementLine);
                        } catch (Exception e) {
                            log.info("--Error---orderManagementLine--deleted----: " + orderManagementLine);
                            e.printStackTrace();
                        }
                    }

                    orderManagementLine.setAllocatedQty(ALLOC_QTY);
                    orderManagementLine.setReAllocatedQty(ALLOC_QTY);

                    // STATUS_ID
                    /* if ORD_QTY> ALLOC_QTY , then STATUS_ID is hardcoded as "42" */
                    if (ORD_QTY > ALLOC_QTY) {
                        STATUS_ID = 42L;
                    }

                    /* if ORD_QTY=ALLOC_QTY, then STATUS_ID is hardcoded as "43" */
                    if (ORD_QTY == ALLOC_QTY) {
                        STATUS_ID = 43L;
                    }

                    statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
                    orderManagementLine.setStatusId(STATUS_ID);
                    orderManagementLine.setStatusDescription(statusDescription);
                    orderManagementLine.setReferenceField7(statusDescription);
                    orderManagementLine.setPickupUpdatedBy(loginUserID);
                    orderManagementLine.setPickupUpdatedOn(new Date());


                    double allocatedQtyFromOrderMgmt = 0.0;

                    /*
                     * Deleting current record and inserting new record (since UK is not allowing to
                     * update prop_st_bin and Pack_bar_codes columns
                     */
                    newOrderManagementLine = new OrderManagementLineV2();
                    BeanUtils.copyProperties(orderManagementLine, newOrderManagementLine, CommonUtils.getNullPropertyNames(orderManagementLine));

                    if (newOrderManagementLine.getCompanyDescription() == null) {
                        description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                        newOrderManagementLine.setCompanyDescription(description.getCompanyDesc());
                        newOrderManagementLine.setPlantDescription(description.getPlantDesc());
                        newOrderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    newOrderManagementLine.setProposedStorageBin(stBinWiseInventory.getStorageBin());
                    newOrderManagementLine.setBarcodeId(stBinWiseInventory.getBarcodeId());
                    newOrderManagementLine.setLevelId(stBinWiseInventory.getLevelId());
                    newOrderManagementLine.setStorageSectionId(stBinWiseInventory.getStorageSectionId());
//                    newOrderManagementLine.setPalletId(stBinWiseInventory.getPalletId());
//                    if (stBinInventory.getBinClassId() == 3L) {
//                        updatePutAwayHeaderV5(companyCodeId, plantId, languageId, warehouseId,
//                                newOrderManagementLine.getItemCode(), newOrderManagementLine.getBarcodeId());
//                        newOrderManagementLine.setStagingArea("SA");
//                        log.info("BarcodeId-------> " + newOrderManagementLine.getBarcodeId());
//                    }

                    newOrderManagementLine.setProposedPackBarCode(stBinWiseInventory.getPackBarcodes());
                    newOrderManagementLine.setProposedBatchSerialNumber(stBinWiseInventory.getBatchSerialNumber());
                    newOrderManagementLine.setArticleNo(stBinWiseInventory.getArticleNo());
                    newOrderManagementLine.setManufacturerDate(stBinWiseInventory.getManufacturerDate());
                    newOrderManagementLine.setExpiryDate(stBinWiseInventory.getExpiryDate());
//                    newOrderManagementLine.setGender(stBinInventory.getGender());
//                    newOrderManagementLine.setMaterialNo(stBinInventory.getMaterialNo());
//                    newOrderManagementLine.setNoPairs(stBinInventory.getNoPairs());
//                    newOrderManagementLine.setSize(stBinInventory.getSize());
//                    newOrderManagementLine.setPriceSegment(stBinInventory.getPriceSegment());
//                    newOrderManagementLine.setColor(stBinInventory.getColor());
//                    if (stBinInventory.getBarcodeId() == null) {
//                        String barcodeId = getNextRangeNumber(25L, companyCodeId, plantId, languageId, warehouseId);
//                        newOrderManagementLine.setBarcodeId(barcodeId);
//                    }
                    OrderManagementLineV2 createdOrderManagementLine = orderManagementLineV2Repository.saveAndFlush(newOrderManagementLine);
                    log.info("--else---createdOrderManagementLine newly created------: " + createdOrderManagementLine);
                    allocatedQtyFromOrderMgmt = createdOrderManagementLine.getAllocatedQty();

                    if (ORD_QTY > ALLOC_QTY) {
                        ORD_QTY = ORD_QTY - ALLOC_QTY;
                    }

                    if (allocatedQtyFromOrderMgmt > 0) {

                        double[] inventoryQty = allocateInventoryV5(allocatedQtyFromOrderMgmt, stBinWiseInventory.getInventoryQuantity(), stBinWiseInventory.getAllocatedQuantity());

                        // Create new Inventory Record
                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(stBinWiseInventory, inventoryV2, CommonUtils.getNullPropertyNames(stBinWiseInventory));

                        if (inventoryQty != null && inventoryQty.length > 2) {
                            inventoryV2.setInventoryQuantity(inventoryQty[0]);
                            inventoryV2.setAllocatedQuantity(inventoryQty[1]);
                            inventoryV2.setReferenceField4(inventoryQty[2]);
                        }

                        inventoryV2.setReferenceDocumentNo(orderManagementLine.getRefDocNumber());
                        inventoryV2.setReferenceOrderNo(orderManagementLine.getRefDocNumber());
                        inventoryV2.setUpdatedOn(new Date());
                        inventoryV2.setManufacturerDate(orderManagementLine.getManufacturerDate());
                        inventoryV2.setExpiryDate(orderManagementLine.getExpiryDate());
                        if (orderManagementLine.getOutboundOrderTypeId() != 11) {
                            inventoryV2.setQtyInPiece(inventoryV2.getInventoryQuantity());
                            log.info("------QtyInPiece-----" + inventoryV2.getQtyInPiece());
                            if (inventoryV2.getQtyInPiece() != null) {
                                setAlternateUomQuantities(inventoryV2);
                            }
                        }
                        try {
                            inventoryV2 = inventoryV2Repository.save(inventoryV2);
                            log.info("-----Inventory2 updated-------: " + inventoryV2);
                        } catch (Exception e) {
                            log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                            e.printStackTrace();
                            InventoryTrans newInventoryTrans = new InventoryTrans();
                            BeanUtils.copyProperties(inventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(inventoryV2));
                            newInventoryTrans.setReRun(0L);
                            InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                            log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                        }
                    }

                    if (ORD_QTY == ALLOC_QTY) {
                        log.info("ORD_QTY fully allocated: " + ORD_QTY);
                        break outerloop; // If the Inventory satisfied the Ord_qty
                    }
                }
            }
            return newOrderManagementLine;
        } catch (Exception e) {
            log.error("Exception while orderAllocation V3: " + e);
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param barcodeId
     */
    public void updatePutAwayHeaderV5(String companyCode, String plantId, String languageId,
                                      String warehouseId, String itemCode, String barcodeId) {
        try {
            log.info("putawayHeader Status Update: " + companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + barcodeId);
//        statusDescription = getStatusDescription(20L, languageId);
//        putAwayHeaderV2Repository.updateHeaderStatus(
//                companyCode, plantId, languageId, warehouseId, 20L, statusDescription, itemCode, barcodeId);
            PutAwayHeaderV2 putAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndReferenceField5AndBarcodeIdAndStatusIdAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, itemCode, barcodeId, 19L, 0L);
            log.info("PutawayHeader : " + putAwayHeader);
            if (putAwayHeader != null) {
                PutAwayLineV2 putAwayLine = new PutAwayLineV2();
                BeanUtils.copyProperties(putAwayHeader, putAwayLine);
                putAwayLine.setCompanyCode(companyCode);
                putAwayLine.setPutawayConfirmedQty(putAwayHeader.getPutAwayQuantity());
                putAwayLine.setConfirmedStorageBin(putAwayHeader.getProposedStorageBin());
                putAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
                putAwayLine.setItemCode(putAwayHeader.getReferenceField5());
                putAwayLineConfirmNonCBMV5(putAwayLine, putAwayHeader, "STAGING_AREA");
            }
        } catch (Exception e) {
            throw new BadRequestException("Exception while creating ");
        }
    }

    /**
     * orderManagementLine order allocate staging area auto putawayline confirm
     *
     * @param newPutAwayLine
     * @param putAwayHeader
     * @param loginUserID
     * @throws Exception
     */
    public void putAwayLineConfirmNonCBMV5(PutAwayLineV2 newPutAwayLine, PutAwayHeaderV2 putAwayHeader, String loginUserID) throws Exception {

        log.info("2.newPutAwayLine to confirm : " + newPutAwayLine);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        try {

            PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
            itemCode = newPutAwayLine.getItemCode();
            companyCode = newPutAwayLine.getCompanyCode();
            plantId = newPutAwayLine.getPlantId();
            languageId = newPutAwayLine.getLanguageId();
            warehouseId = newPutAwayLine.getWarehouseId();
            refDocNumber = newPutAwayLine.getRefDocNumber();
            preInboundNo = newPutAwayLine.getPreInboundNo();

            StagingLineEntityV2 dbStagingLineEntity = getStagingLineForPutAwayLineV5(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                    newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());

            if (dbStagingLineEntity != null) {
                newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                newPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                newPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                newPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                newPutAwayLine.setSize(dbStagingLineEntity.getSize());
                newPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
            }

            BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

            dbPutAwayLine.setStatusId(20L);
            statusDescription = getStatusDescription(20L, languageId);
            dbPutAwayLine.setStatusDescription(statusDescription);
            dbPutAwayLine.setDeletionIndicator(0L);
            dbPutAwayLine.setCreatedBy(loginUserID);
            dbPutAwayLine.setUpdatedBy(loginUserID);
            dbPutAwayLine.setConfirmedBy(loginUserID);

            dbPutAwayLine.setBatchSerialNumber(putAwayHeader.getBatchSerialNumber());
            dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
            dbPutAwayLine.setPutAwayQuantity(putAwayHeader.getPutAwayQuantity());
            dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
            dbPutAwayLine.setStorageSectionId(putAwayHeader.getStorageSectionId());
            if (dbPutAwayLine.getLineNo() == null) {
                dbPutAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
            }

            dbPutAwayLine.setMaterialNo(putAwayHeader.getMaterialNo());
            dbPutAwayLine.setPriceSegment(putAwayHeader.getPriceSegment());
            dbPutAwayLine.setArticleNo(putAwayHeader.getArticleNo());
            dbPutAwayLine.setGender(putAwayHeader.getGender());
            dbPutAwayLine.setColor(putAwayHeader.getColor());
            dbPutAwayLine.setSize(putAwayHeader.getSize());
            dbPutAwayLine.setNoPairs(putAwayHeader.getNoPairs());

            if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                dbPutAwayLine.setParentProductionOrderNo(putAwayHeader.getParentProductionOrderNo());
            }
            dbPutAwayLine.setUpdatedOn(new Date());
            dbPutAwayLine.setConfirmedOn(new Date());

            boolean existingPutAwayLine = putAwayLineV2Repository.existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndRefDocNumberAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, newPutAwayLine.getBarcodeId(), refDocNumber, 0L);
            log.info("2.Existing putawayline already created : " + existingPutAwayLine);

            if (existingPutAwayLine) {
                throw new BadRequestException("2.HU Serial Number already exist..! + " + newPutAwayLine.getBarcodeId());
            }

            if (!existingPutAwayLine) {
                try {
                    String leadTime = putAwayLineV2Repository.getLeadTimeV5(dbPutAwayLine.getAssignedOn(), new Date());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("2.LeadTime: " + leadTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                log.info("---------->2.createdPutAwayLine created: " + createdPutAwayLine);

                if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {

                    putAwayHeaderV2Repository.updateHeaderStatus(companyCode, plantId, languageId, warehouseId, 20L, statusDescription, itemCode, createdPutAwayLine.getBarcodeId());

                    /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                    double addedAcceptQty = 0.0;

                    InboundLineV2 inboundLine = getInboundLineV5(
                            createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                            createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                            createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                            createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());

                    // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                    if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                        if (inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                            addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
                        } else {
                            addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                        }
                        if (addedAcceptQty > inboundLine.getOrderQty()) {
                            throw new BadRequestException("Accept qty cannot be greater than order qty");
                        }
                        inboundLine.setAcceptedQty(addedAcceptQty);
                        inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                    }

                    inboundLine.setReferenceField2("TRUE");
                    inboundLine.setStatusId(20L);
                    statusDescription = getStatusDescription(20L, createdPutAwayLine.getLanguageId());
                    inboundLine.setStatusDescription(statusDescription);
                    inboundLineV2Repository.saveAndFlush(inboundLine);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param lineNo
     * @param itemCode
     * @return
     */
    public InboundLineV2 getInboundLineV5(String companyCode, String plantId,
                                          String languageId, String warehouseId, String refDocNumber,
                                          String preInboundNo, Long lineNo, String itemCode) {
        Optional<InboundLineV2> inboundLine =
                inboundLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        lineNo,
                        itemCode,
                        0L);
        log.info("inboundLine : " + inboundLine);
        if (inboundLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",CompanyCode: " + companyCode + ",plantId: " + plantId + ",languageId: " + languageId +
                    ",refDocNumber: " + refDocNumber + ",preInboundNo: " + preInboundNo + ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode + " doesn't exist.");
        }

        return inboundLine.get();
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public StagingLineEntityV2 getStagingLineForPutAwayLineV5(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preInboundNo,
                                                              String refDocNumber, Long lineNo, String itemCode, String manufacturerName) {
        Optional<StagingLineEntityV2> stagingLineV2 = stagingLineV2Repository
                .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        manufacturerName,
                        0L);
        if (stagingLineV2 == null || stagingLineV2.isEmpty()) {
            return null;
        }
        log.info("dbStagingLine: " + stagingLineV2.get());
        return stagingLineV2.get();
    }


    /**
     * @param orderManagementLine
     */
    private void setAlternateUomQuantities(OrderManagementLineV2 orderManagementLine) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = orderManagementLine.getOrderUom();
            String companyCodeId = orderManagementLine.getCompanyCodeId();
            String plantId = orderManagementLine.getPlantId();
            String warehouseId = orderManagementLine.getWarehouseId();
            String itemCode = orderManagementLine.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = orderManagementLine.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", orderManagementLine.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLine.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = orderManagementLine.getOrderQty() / caseQty.getUomQty();
                }

                if (orderManagementLine.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = orderManagementLine.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = orderManagementLine.getOrderQty();

                log.info("Case Qty --- {}", orderManagementLine.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (orderManagementLine.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLine.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLine.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is Crate");
                qtyInCreate = orderManagementLine.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQy = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("Crate Qty --- {}", orderManagementLine.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", caseQy);

                if (orderManagementLine.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = orderManagementLine.getOrderQty() * pieceQty.getUomQty();
                }

                if (orderManagementLine.getOrderQty() != null && caseQy != null && caseQy.getUomQty() != null) {
                    qtyInCase = qtyInPiece / caseQy.getUomQty();
                }
            }

            orderManagementLine.setQtyInPiece(qtyInPiece);
            orderManagementLine.setQtyInCase(qtyInCase);
            orderManagementLine.setQtyInCrate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }


    /**
     * @param inventoryV2
     */
    private void setAlternateUomQuantities(InventoryV2 inventoryV2) {
        try {
            log.info("----For Inventory Update----");
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = "Piece";
            String companyCodeId = inventoryV2.getCompanyCodeId();
            String plantId = inventoryV2.getPlantId();
            String warehouseId = inventoryV2.getWarehouseId();
            String itemCode = inventoryV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = inventoryV2.getInventoryQuantity();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", inventoryV2.getInventoryQuantity());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (inventoryV2.getInventoryQuantity() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = inventoryV2.getInventoryQuantity() / caseQty.getUomQty();
                }

                if (inventoryV2.getInventoryQuantity() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = inventoryV2.getInventoryQuantity() / createQty.getUomQty();
                }

            }
            inventoryV2.setQtyInPiece(qtyInPiece);
            inventoryV2.setQtyInCreate(qtyInCreate);
            inventoryV2.setQtyInCase(qtyInCase);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }
}

