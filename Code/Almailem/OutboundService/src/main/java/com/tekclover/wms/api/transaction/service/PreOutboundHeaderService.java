package com.tekclover.wms.api.transaction.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.*;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLineInterim;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.OrderManagementLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.*;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.*;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.*;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundHeaderSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PreOutboundHeaderV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PreOutboundHeaderService extends BaseService {
    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private PreOutboundHeaderRepository preOutboundHeaderRepository;

    @Autowired
    private PreOutboundLineRepository preOutboundLineRepository;

    @Autowired
    private OrderManagementHeaderRepository orderManagementHeaderRepository;

    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    private OutboundLineRepository outboundLineRepository;

    @Autowired
    private OrderManagementLineRepository orderManagementLineRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OutboundLineService outboundLineService;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @Autowired
    private OrderManagementHeaderService orderManagementHeaderService;

    @Autowired
    private OrderManagementLineService orderManagementLineService;

    @Autowired
    private OutboundIntegrationLogRepository outboundIntegrationLogRepository;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    //------------------------------------------------------------------------------------------------------

    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    private OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    private PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    private PickupHeaderService pickupHeaderService;
    @Autowired
    private PickupLineService pickupLineService;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private QualityHeaderService qualityHeaderService;

    @Autowired
    private QualityLineService qualityLineService;

    @Autowired
    private PreOutboundLineService preOutboundLineService;

    @Autowired
    private OutboundHeaderService outboundHeaderService;

    @Autowired
    private PickListHeaderService pickListHeaderService;

    @Autowired
    private PickListHeaderRepository pickListHeaderRepository;

    String statusDescription = null;

    //------------------------------------------------------------------------------------------------------

    /**
     * getPreOutboundHeaders
     * @return
     */
    public List<PreOutboundHeader> getPreOutboundHeaders() {
        List<PreOutboundHeader> preOutboundHeaderList = preOutboundHeaderRepository.findAll();
        preOutboundHeaderList = preOutboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preOutboundHeaderList;
    }

    /**
     * getPreOutboundHeader
     * @param preOutboundNo
     * @return
     */
    public PreOutboundHeader getPreOutboundHeader(String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode) {
        Optional<PreOutboundHeader> preOutboundHeader =
                preOutboundHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
                        getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, refDocNumber, preOutboundNo, partnerCode, 0L);
        if (!preOutboundHeader.isEmpty()) {
            return preOutboundHeader.get();
        }
        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    public PreOutboundHeader getPreOutboundHeader(String preOutboundNo) {
        PreOutboundHeader preOutboundHeader = preOutboundHeaderRepository.findByPreOutboundNo(preOutboundNo);
        if (preOutboundHeader != null && preOutboundHeader.getDeletionIndicator() == 0) {
            return preOutboundHeader;
        } else {
            throw new BadRequestException("The given PreOutboundHeader ID : " + preOutboundNo + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public PreOutboundHeader getPreOutboundHeader(String warehouseId, String refDocNumber) {
        Optional<PreOutboundHeader> preOutboundHeader =
                preOutboundHeaderRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(), getCompanyCode(), getPlantId(), warehouseId, refDocNumber, 0L);
        if (!preOutboundHeader.isEmpty()) {
            return preOutboundHeader.get();
        }
        return null;
    }

    /**
     * @param searchPreOutboundHeader
     * @return
     * @throws Exception
     */
    public List<PreOutboundHeader> findPreOutboundHeader(SearchPreOutboundHeader searchPreOutboundHeader)
            throws Exception {

        if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null && searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartRequiredDeliveryDate(), searchPreOutboundHeader.getEndRequiredDeliveryDate());
            searchPreOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
            searchPreOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartOrderDate(), searchPreOutboundHeader.getEndOrderDate());
            searchPreOutboundHeader.setStartOrderDate(dates[0]);
            searchPreOutboundHeader.setEndOrderDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartCreatedOn(), searchPreOutboundHeader.getEndCreatedOn());
            searchPreOutboundHeader.setStartCreatedOn(dates[0]);
            searchPreOutboundHeader.setEndCreatedOn(dates[1]);
        }

        PreOutboundHeaderSpecification spec = new PreOutboundHeaderSpecification(searchPreOutboundHeader);
        List<PreOutboundHeader> results = preOutboundHeaderRepository.findAll(spec);
        return results;
    }

    /**
     * @param searchPreOutboundHeader
     * @return
     * @throws Exception
     */
    public Stream<PreOutboundHeader> findPreOutboundHeaderNew(SearchPreOutboundHeader searchPreOutboundHeader)
            throws Exception {

        if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null && searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartRequiredDeliveryDate(), searchPreOutboundHeader.getEndRequiredDeliveryDate());
            searchPreOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
            searchPreOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartOrderDate(), searchPreOutboundHeader.getEndOrderDate());
            searchPreOutboundHeader.setStartOrderDate(dates[0]);
            searchPreOutboundHeader.setEndOrderDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartCreatedOn(), searchPreOutboundHeader.getEndCreatedOn());
            searchPreOutboundHeader.setStartCreatedOn(dates[0]);
            searchPreOutboundHeader.setEndCreatedOn(dates[1]);
        }

        PreOutboundHeaderSpecification spec = new PreOutboundHeaderSpecification(searchPreOutboundHeader);
        Stream<PreOutboundHeader> preOutboundHeaderList = preOutboundHeaderRepository.stream(spec, PreOutboundHeader.class);
        return preOutboundHeaderList;
    }

    /**
     * createPreOutboundHeader
     * @param newPreOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundHeader createPreOutboundHeader(AddPreOutboundHeader newPreOutboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PreOutboundHeader dbPreOutboundHeader = new PreOutboundHeader();
        log.info("newPreOutboundHeader : " + newPreOutboundHeader);
        BeanUtils.copyProperties(newPreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(newPreOutboundHeader));

        dbPreOutboundHeader.setDeletionIndicator(0L);
        dbPreOutboundHeader.setCreatedBy(loginUserID);
        dbPreOutboundHeader.setUpdatedBy(loginUserID);
        dbPreOutboundHeader.setCreatedOn(new Date());
        dbPreOutboundHeader.setUpdatedOn(new Date());
        return preOutboundHeaderRepository.save(dbPreOutboundHeader);
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param loginUserID
     * @param updatePreOutboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundHeader updatePreOutboundHeader(String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode,
                                                     String loginUserID, UpdatePreOutboundHeader updatePreOutboundHeader)
            throws IllegalAccessException, InvocationTargetException {
        PreOutboundHeader dbPreOutboundHeader = getPreOutboundHeader(warehouseId, refDocNumber, preOutboundNo, partnerCode);
        BeanUtils.copyProperties(updatePreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(updatePreOutboundHeader));
        dbPreOutboundHeader.setUpdatedBy(loginUserID);
        dbPreOutboundHeader.setUpdatedOn(new Date());
        return preOutboundHeaderRepository.save(dbPreOutboundHeader);
    }

    /**
     * deletePreOutboundHeader
     * @param loginUserID
     * @param preOutboundNo
     */
    public void deletePreOutboundHeader(String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, String loginUserID) {
        PreOutboundHeader preOutboundHeader = getPreOutboundHeader(preOutboundNo);
        if (preOutboundHeader != null) {
            preOutboundHeader.setDeletionIndicator(1L);
            preOutboundHeader.setUpdatedBy(loginUserID);
            preOutboundHeaderRepository.save(preOutboundHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preOutboundNo);
        }
    }

    /*----------------------PROCESS-OUTBOUND-RECEIVED----------------------------------------------*/
    /*
     * Process the PreoutboundIntegraion data
     */
    @Transactional
    public OutboundHeader processOutboundReceived(OutboundIntegrationHeader outboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException, Exception {
        /*
         * Checking whether received refDocNumber processed already.
         */
        Optional<PreOutboundHeader> orderProcessedStatus =
                preOutboundHeaderRepository.findByRefDocNumberAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), 0);
        if (!orderProcessedStatus.isEmpty()) {
            orderService.updateProcessedOrder(outboundIntegrationHeader.getRefDocumentNo());
            throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() +
                    " already processed. Reprocessing can't be allowed.");
        }

        String warehouseId = outboundIntegrationHeader.getWarehouseID();
        log.info("warehouseId : " + warehouseId);

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Warehouse warehouse = getWarehouse(warehouseId);
        log.info("warehouse : " + warehouse);
        if (warehouse == null) {
            log.info("warehouse not found.");
            throw new BadRequestException("Warehouse cannot be null.");
        }

        // Getting PreOutboundNo from NumberRangeTable
        String preOutboundNo = getPreOutboundNo(warehouseId);
        String refField1ForOrderType = null;

        List<PreOutboundLine> overallCreatedPreoutboundLineList = new ArrayList<>();
        for (OutboundIntegrationLine outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLine()) {
            log.info("outboundIntegrationLine : " + outboundIntegrationLine);

            /*-------------Insertion of BOM item in preOutboundLine table---------------------------------------------------------*/
            /*
             * Before Insertion into PREOUTBOUNDLINE table , validate the Below
             * Pass the WH_ID/ITM_CODE as PAR_ITM_CODE into BOMHEADER table and validate the records,
             * If record is not Null then fetch BOM_NO Pass BOM_NO in BOMITEM table and fetch CHL_ITM_CODE and
             * CHL_ITM_QTY values and insert along with PAR_ITM_CODE in PREOUTBOUNDLINE table as below
             * Insertion of CHL_ITM_CODE values
             */
            BomHeader bomHeader = mastersService.getBomHeader(outboundIntegrationLine.getItemCode(),
                    warehouseId, authTokenForMastersService.getAccess_token());
            if (bomHeader != null) {
                BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(),
                        authTokenForMastersService.getAccess_token());
                List<PreOutboundLine> toBeCreatedpreOutboundLineList = new ArrayList<>();
                for (BomLine dbBomLine : bomLine) {
                    toBeCreatedpreOutboundLineList.add(createPreoutboundLineBOMBased(warehouse.getCompanyCode(),
                            warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, dbBomLine, outboundIntegrationLine));
                }

                // Batch Insert - preOutboundLines
                if (!toBeCreatedpreOutboundLineList.isEmpty()) {
                    List<PreOutboundLine> createdpreOutboundLine = preOutboundLineRepository.saveAll(toBeCreatedpreOutboundLineList);
                    log.info("createdpreOutboundLine [BOM] : " + createdpreOutboundLine);
                    overallCreatedPreoutboundLineList.addAll(createdpreOutboundLine);
                }
            }
            refField1ForOrderType = outboundIntegrationLine.getRefField1ForOrderType();
        }

        /*
         * Inserting BOM Line records in OutboundLine and OrderManagementLine
         */
        if (!overallCreatedPreoutboundLineList.isEmpty()) {
            for (PreOutboundLine preOutboundLine : overallCreatedPreoutboundLineList) {
                // OrderManagementLine
                OrderManagementLine orderManagementLine = createOrderManagementLine(warehouse.getCompanyCode(),
                        warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, preOutboundLine);
                log.info("orderManagementLine created---BOM---> : " + orderManagementLine);
            }

            /*------------------Record Insertion in OUTBOUNDLINE table--for BOM---------*/
            List<OutboundLine> createOutboundLineListForBOM = createOutboundLine(overallCreatedPreoutboundLineList);
            log.info("createOutboundLine created : " + createOutboundLineListForBOM);
        }

        /*
         * Append PREOUTBOUNDLINE table through below logic
         */
        List<PreOutboundLine> createdPreOutboundLineList = new ArrayList<>();
        for (OutboundIntegrationLine outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLine()) {
            // PreOutboundLine
            try {
                PreOutboundLine preOutboundLine = createPreOutboundLine(warehouse.getCompanyCode(),
                        warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine);
                PreOutboundLine createdPreOutboundLine = preOutboundLineRepository.save(preOutboundLine);
                log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
                createdPreOutboundLineList.add(createdPreOutboundLine);

                // OrderManagementLine
                OrderManagementLine orderManagementLine = createOrderManagementLine(warehouse.getCompanyCode(),
                        warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, preOutboundLine);
                log.info("orderManagementLine created---1---> : " + orderManagementLine);
            } catch (Exception e) {
                log.error("Error on processing Preoutboudline : " + e.toString());
                e.printStackTrace();
            }
        }

        /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
        List<OutboundLine> createOutboundLineList = createOutboundLine(createdPreOutboundLineList);
        log.info("createOutboundLine created : " + createOutboundLineList);

        /*------------------Insert into PreOutboundHeader table-----------------------------*/
        PreOutboundHeader createdPreOutboundHeader = createPreOutboundHeader(warehouse.getCompanyCode(),
                warehouse.getPlantId(), preOutboundNo, outboundIntegrationHeader, refField1ForOrderType);
        log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

        /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
        OrderManagementHeader createdOrderManagementHeader = createOrderManagementHeader(createdPreOutboundHeader);
        log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

        /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
        OutboundHeader outboundHeader = createOutboundHeader(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId());

        /*------------------------------------------------------------------------------------*/
        updateStatusAs47ForOBHeader(warehouseId, preOutboundNo, outboundHeader.getRefDocNumber());
        return outboundHeader;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     */
    private void updateStatusAs47ForOBHeader(String warehouseId, String preOutboundNo, String refDocNumber) {
        List<OutboundLine> outboundLineList = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
        long matchedCount = outboundLineList.stream().filter(a -> a.getStatusId() == 47L).count();
        boolean isConditionMet = (matchedCount == outboundLineList.size());
        Long STATUS_ID_47 = 47L;
        if (isConditionMet) {
            //----------------Outbound Header update----------------------------------------------------------------------------------------
            outboundHeaderRepository.updateOutboundHeaderStatusAs47(warehouseId, refDocNumber, STATUS_ID_47);
            log.info("OutboundHeader status updated as 47. ");

            //----------------Preoutbound Header--------------------------------------------------------------------------------------------
            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
            StatusId idStatus = idmasterService.getStatus(STATUS_ID_47, warehouseId, authTokenForIDService.getAccess_token());
            preOutboundHeaderRepository.updatePreOutboundHeaderStatus(warehouseId, refDocNumber, STATUS_ID_47, idStatus.getStatus());
            log.info("PreOutbound Header status updated as 47.");
        }
    }

    /**
     * @param createdPreOutboundLine
     * @return
     */
    private List<OutboundLine> createOutboundLine(List<PreOutboundLine> createdPreOutboundLine) {
        List<OutboundLine> outboundLines = new ArrayList<>();
        for (PreOutboundLine preOutboundLine : createdPreOutboundLine) {
            List<OrderManagementLine> orderManagementLine = orderManagementLineService.getOrderManagementLine(preOutboundLine.getPreOutboundNo(),
                    preOutboundLine.getLineNumber(), preOutboundLine.getItemCode());
            for (OrderManagementLine dbOrderManagementLine : orderManagementLine) {
                OutboundLine outboundLine = new OutboundLine();
                BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
                outboundLine.setDeliveryQty(0D);
                outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                outboundLine.setCreatedBy(preOutboundLine.getCreatedBy());
                outboundLine.setCreatedOn(preOutboundLine.getCreatedOn());
                outboundLines.add(outboundLine);
            }
        }

        outboundLines = outboundLineRepository.saveAll(outboundLines);
        log.info("outboundLines created -----2------>: " + outboundLines);
        return outboundLines;
    }

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @return
     * @throws ParseException
     */
    private OutboundHeader createOutboundHeader(PreOutboundHeader createdPreOutboundHeader, Long statusId) throws ParseException {
        OutboundHeader outboundHeader = new OutboundHeader();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader,
                CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
        OrderManagementHeader dbOrderManagementHeader = orderManagementHeaderService.getOrderManagementHeader(createdPreOutboundHeader.getPreOutboundNo());

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(dbOrderManagementHeader.getStatusId());
        outboundHeader.setCreatedBy(createdPreOutboundHeader.getCreatedBy());
        outboundHeader.setCreatedOn(createdPreOutboundHeader.getCreatedOn());
        outboundHeader = outboundHeaderRepository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader);
        return outboundHeader;
    }

    /**
     * @param createdPreOutboundHeader
     * @return
     */
    private OrderManagementHeader createOrderManagementHeader(PreOutboundHeader createdPreOutboundHeader) {
        OrderManagementHeader newOrderManagementHeader = new OrderManagementHeader();
        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));

        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(41L, createdPreOutboundHeader.getWarehouseId(), authTokenForIDService.getAccess_token());
        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"
        newOrderManagementHeader.setReferenceField7(idStatus.getStatus());
        OrderManagementHeader createdOrderMangementHeader = orderManagementHeaderRepository.save(newOrderManagementHeader);
        return createdOrderMangementHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param refField1ForOrderType
     * @return
     * @throws ParseException
     */
    private PreOutboundHeader createPreOutboundHeader(String companyCodeId, String plantId, String preOutboundNo,
                                                      OutboundIntegrationHeader outboundIntegrationHeader, String refField1ForOrderType) throws ParseException {
        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        PreOutboundHeader preOutboundHeader = new PreOutboundHeader();
        preOutboundHeader.setLanguageId("EN");
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
        preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
        preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());    // Hardcoded value "0"
        preOutboundHeader.setReferenceDocumentType("SO");                                                // Hardcoded value "SO"

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        preOutboundHeader.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);

        // Status Description
        StatusId idStatus = idmasterService.getStatus(39L, outboundIntegrationHeader.getWarehouseID(), authTokenForIDService.getAccess_token());

        // REF_FIELD_10
        preOutboundHeader.setReferenceField10(idStatus.getStatus());

        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy("MW_AMS");
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeader createdPreOutboundHeader = preOutboundHeaderRepository.save(preOutboundHeader);
        log.info("createdPreOutboundHeader : " + createdPreOutboundHeader);
        return createdPreOutboundHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param dbBomLine
     * @param outboundIntegrationLine
     * @return
     */
    private PreOutboundLine createPreoutboundLineBOMBased(String companyCodeId, String plantId, String preOutboundNo,
                                                          OutboundIntegrationHeader outboundIntegrationHeader, BomLine dbBomLine,
                                                          OutboundIntegrationLine outboundIntegrationLine) {
        Warehouse warehouse = getWarehouse(outboundIntegrationHeader.getWarehouseID());

        PreOutboundLine preOutboundLine = new PreOutboundLine();
        preOutboundLine.setLanguageId(warehouse.getLanguageId());
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);

        // WH_ID
        preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());

        // PRE_IB_NO
        preOutboundLine.setPreOutboundNo(preOutboundNo);

        // REF_DOC_NO
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

        // OB_ORD_TYP_ID
        preOutboundLine.setOutboundOrderTypeId(Long.valueOf(outboundIntegrationHeader.getOutboundOrderTypeID()));

        // IB__LINE_NO
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

        // ITM_CODE
        preOutboundLine.setItemCode(dbBomLine.getChildItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        //HAREESH 27-08-2022 - bom line creation get item description based on child item code change
        ImBasicData1 imBasicData1 =
                mastersService.getImBasicData1ByItemCode(dbBomLine.getChildItemCode(),
                        outboundIntegrationHeader.getWarehouseID(), authTokenForMastersService.getAccess_token());
        if (imBasicData1 != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
            // MFR
            preOutboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
        }

        // PARTNER_CODE
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

        // ORD_QTY
        double orderQuantity = outboundIntegrationLine.getOrderedQty() * dbBomLine.getChildItemQuantity();
        preOutboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

        // REQ_DEL_DATE
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // STCK_TYP_ID
        preOutboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preOutboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preOutboundLine.setStatusId(39L);

        // REF_FIELD_1
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

        // REF_FIELD_2
        preOutboundLine.setReferenceField2("BOM");

        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());
        return preOutboundLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param outboundIntegrationLine
     * @return
     */
    private PreOutboundLine createPreOutboundLine(String companyCodeId, String plantId, String preOutboundNo,
                                                  OutboundIntegrationHeader outboundIntegrationHeader, OutboundIntegrationLine outboundIntegrationLine) {
        PreOutboundLine preOutboundLine = new PreOutboundLine();

        preOutboundLine.setLanguageId("EN");
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);

        // WH_ID
        preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());

        // REF DOC Number
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

        // PRE_IB_NO
        preOutboundLine.setPreOutboundNo(preOutboundNo);

        // PARTNER_CODE
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

        // IB__LINE_NO
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

        // ITM_CODE
        preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());

        // OB_ORD_TYP_ID
        preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());

        // STATUS_ID
        preOutboundLine.setStatusId(39L);

        // STCK_TYP_ID
        preOutboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preOutboundLine.setSpecialStockIndicatorId(1L);

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                mastersService.getImBasicData1ByItemCode(outboundIntegrationLine.getItemCode(),
                        outboundIntegrationHeader.getWarehouseID(),
                        authTokenForMastersService.getAccess_token());
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null && imBasicData1.getDescription() != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
        } else {
            preOutboundLine.setDescription(outboundIntegrationLine.getItemText());
        }

        // ORD_QTY
        preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());

        // ORD_UOM
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

        // REQ_DEL_DATE
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());

        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
    }

    /**
     * ORDERMANAGEMENTLINE TABLE
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @return
     */
    private OrderManagementLine createOrderManagementLine(String companyCodeId, String plantId, String preOutboundNo,
                                                          OutboundIntegrationHeader outboundIntegrationHeader, PreOutboundLine preOutboundLine) {
        OrderManagementLine orderManagementLine = new OrderManagementLine();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));

        // INV_QTY
        /*
         * 1. If OB_ORD_TYP_ID = 0,1,3
         * Pass WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
         * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZB,ZC,ZG,ZT and
         * PUTAWAY_BLOCK and PICK_BLOCK are false(Null).
         *
         * 2. If OB_ORD_TYP_ID = 2
         * Pass  WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
         * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZD and PUTAWAY_BLOCK
         * and PICK_BLOCK are false(Null).
         *
         * Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch Sum(INV_QTY)"
         */
        log.info("orderManagementLine : " + orderManagementLine);

        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); //ZB,ZC,ZG,ZT
            orderManagementLine = createOrderManagement(storageSectionIds, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
        }

        if (OB_ORD_TYP_ID == 2L) {
            List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
            orderManagementLine = createOrderManagement(storageSectionIds, orderManagementLine, preOutboundLine.getWarehouseId(),
                    preOutboundLine.getItemCode(), preOutboundLine.getOrderQty());
        }

        // PROP_ST_BIN
        return orderManagementLine;
    }

    /**
     * @param warehouseId
     * @return
     */
    private String getPreOutboundNo(String warehouseId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE=9  in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1and then update in PREOUTBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            return getNextRangeNumber(9, warehouseId, authTokenForIDMasterService.getAccess_token());
        } catch (Exception e) {
            throw new BadRequestException("Error on Number Range");
        }
    }


    /**
     * @param storageSectionIds
     * @param orderManagementLine
     * @param warehouseId
     * @param itemCode
     * @param ORD_QTY
     * @return
     */
    private OrderManagementLine createOrderManagement(List<String> storageSectionIds, OrderManagementLine orderManagementLine,
                                                      String warehouseId, String itemCode, Double ORD_QTY) {
        List<Inventory> stockType1InventoryList = inventoryService.getInventoryForOrderManagement(warehouseId, itemCode, 1L, 1L);
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList);
        if (stockType1InventoryList.isEmpty()) {
            return createEMPTYOrderManagementLine(orderManagementLine);
        }
        return orderManagementLineService.updateAllocation(orderManagementLine, storageSectionIds, ORD_QTY, warehouseId, itemCode, "ORDER_PLACED");
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLine createEMPTYOrderManagementLine(OrderManagementLine orderManagementLine) {
        AuthToken idmasterAuthToken = authTokenService.getIDMasterServiceAuthToken();
        StatusId idStatus = idmasterService.getStatus(47L, orderManagementLine.getWarehouseId(), idmasterAuthToken.getAccess_token());

        orderManagementLine.setStatusId(47L);
        orderManagementLine.setReferenceField7(idStatus.getStatus());
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineRepository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param outbound
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public OutboundIntegrationLog createOutboundIntegrationLog(OutboundIntegrationHeader outbound)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        Warehouse warehouse = getWarehouse(outbound.getWarehouseID());
        OutboundIntegrationLog dbOutboundIntegrationLog = new OutboundIntegrationLog();
        dbOutboundIntegrationLog.setLanguageId("EN");
        dbOutboundIntegrationLog.setCompanyCodeId(warehouse.getCompanyCode());
        dbOutboundIntegrationLog.setPlantId(warehouse.getPlantId());
        dbOutboundIntegrationLog.setWarehouseId(warehouse.getWarehouseId());
        dbOutboundIntegrationLog.setIntegrationLogNumber(outbound.getRefDocumentNo());
        dbOutboundIntegrationLog.setRefDocNumber(outbound.getRefDocumentNo());
        dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
        dbOutboundIntegrationLog.setIntegrationStatus("FAILED");
        dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
        dbOutboundIntegrationLog.setDeletionIndicator(0L);
        dbOutboundIntegrationLog.setCreatedBy("MSD_API");
        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        dbOutboundIntegrationLog.setCreatedOn(new Date());
        dbOutboundIntegrationLog = outboundIntegrationLogRepository.save(dbOutboundIntegrationLog);
        log.info("dbOutboundIntegrationLog : " + dbOutboundIntegrationLog);
        return dbOutboundIntegrationLog;
    }

    /**
     * getPreOutboundHeaders
     * @return
     */
    public List<PreOutboundHeaderV2> getPreOutboundHeadersV2() {
        List<PreOutboundHeaderV2> preOutboundHeaderList = preOutboundHeaderV2Repository.findAll();
        preOutboundHeaderList = preOutboundHeaderList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return preOutboundHeaderList;
    }

    /**
     * getPreOutboundHeader
     * @param preOutboundNo
     * @return
     */
    public PreOutboundHeaderV2 getPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode) {
        Optional<PreOutboundHeaderV2> preOutboundHeader =
                preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndPartnerCodeAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo, partnerCode, 0L);
        if (!preOutboundHeader.isEmpty()) {
            return preOutboundHeader.get();
        }
        return null;
    }

    /**
     * @param preOutboundNo
     * @return
     */
    public PreOutboundHeaderV2 getPreOutboundHeaderV2(String preOutboundNo) {
        PreOutboundHeaderV2 preOutboundHeader = preOutboundHeaderV2Repository.findByPreOutboundNoAndDeletionIndicator(preOutboundNo, 0L);
        if (preOutboundHeader != null) {
            return preOutboundHeader;
        } else {
            throw new BadRequestException("The given PreOutboundHeader ID : " + preOutboundNo + " doesn't exist.");
        }
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public PreOutboundHeaderV2 getPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber) {
        Optional<PreOutboundHeaderV2> preOutboundHeader =
                preOutboundHeaderV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId, refDocNumber, 0L);
        if (!preOutboundHeader.isEmpty()) {
            return preOutboundHeader.get();
        }
        return null;
    }

    /**
     * @param searchPreOutboundHeader
     * @return
     * @throws Exception
     */
    public Stream<PreOutboundHeaderV2> findPreOutboundHeaderV2(SearchPreOutboundHeaderV2 searchPreOutboundHeader)
            throws Exception {

        if (searchPreOutboundHeader.getStartRequiredDeliveryDate() != null && searchPreOutboundHeader.getEndRequiredDeliveryDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartRequiredDeliveryDate(), searchPreOutboundHeader.getEndRequiredDeliveryDate());
            searchPreOutboundHeader.setStartRequiredDeliveryDate(dates[0]);
            searchPreOutboundHeader.setEndRequiredDeliveryDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartOrderDate() != null && searchPreOutboundHeader.getEndOrderDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartOrderDate(), searchPreOutboundHeader.getEndOrderDate());
            searchPreOutboundHeader.setStartOrderDate(dates[0]);
            searchPreOutboundHeader.setEndOrderDate(dates[1]);
        }

        if (searchPreOutboundHeader.getStartCreatedOn() != null && searchPreOutboundHeader.getEndCreatedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPreOutboundHeader.getStartCreatedOn(), searchPreOutboundHeader.getEndCreatedOn());
            searchPreOutboundHeader.setStartCreatedOn(dates[0]);
            searchPreOutboundHeader.setEndCreatedOn(dates[1]);
        }

        PreOutboundHeaderV2Specification spec = new PreOutboundHeaderV2Specification(searchPreOutboundHeader);
        Stream<PreOutboundHeaderV2> preOutboundHeaderList = preOutboundHeaderV2Repository.stream(spec, PreOutboundHeaderV2.class);
        return preOutboundHeaderList;
    }

    /**
     * createPreOutboundHeader
     * @param newPreOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundHeaderV2 createPreOutboundHeaderV2(PreOutboundHeaderV2 newPreOutboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreOutboundHeaderV2 dbPreOutboundHeader = new PreOutboundHeaderV2();
        log.info("newPreOutboundHeader : " + newPreOutboundHeader);
        BeanUtils.copyProperties(newPreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(newPreOutboundHeader));

        dbPreOutboundHeader.setDeletionIndicator(0L);
        dbPreOutboundHeader.setCreatedBy(loginUserID);
        dbPreOutboundHeader.setUpdatedBy(loginUserID);
        dbPreOutboundHeader.setCreatedOn(new Date());
        dbPreOutboundHeader.setUpdatedOn(new Date());
        return preOutboundHeaderV2Repository.save(dbPreOutboundHeader);
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param partnerCode
     * @param loginUserID
     * @param updatePreOutboundHeader
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PreOutboundHeaderV2 updatePreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String refDocNumber, String preOutboundNo, String partnerCode,
                                                         String loginUserID, PreOutboundHeaderV2 updatePreOutboundHeader)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PreOutboundHeaderV2 dbPreOutboundHeader = getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, partnerCode);
        BeanUtils.copyProperties(updatePreOutboundHeader, dbPreOutboundHeader, CommonUtils.getNullPropertyNames(updatePreOutboundHeader));
        dbPreOutboundHeader.setUpdatedBy(loginUserID);
        dbPreOutboundHeader.setUpdatedOn(new Date());
        return preOutboundHeaderV2Repository.save(dbPreOutboundHeader);
    }

    /**
     * deletePreOutboundHeader
     * @param loginUserID
     * @param preOutboundNo
     */
    public void deletePreOutboundHeaderV2(String languageId, String companyCodeId, String plantId, String warehouseId, String refDocNumber, String preOutboundNo, String partnerCode, String loginUserID) {
        PreOutboundHeaderV2 preOutboundHeader = getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, partnerCode);
        if (preOutboundHeader != null) {
            preOutboundHeader.setDeletionIndicator(1L);
            preOutboundHeader.setUpdatedBy(loginUserID);
            preOutboundHeaderV2Repository.save(preOutboundHeader);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + preOutboundNo);
        }
    }

    /*----------------------PROCESS-OUTBOUND-RECEIVED----------------------------------------------*/

    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public OutboundHeaderV2 processOutboundReceivedV2(OutboundIntegrationHeaderV2 outboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException,
            SQLException, SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
        /*
         * Checking whether received refDocNumber processed already.
         */
            log.info("Outbound Process Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
//        Optional<PreOutboundHeaderV2> orderProcessedStatus =
//                preOutboundHeaderV2Repository.findByRefDocNumberAndDeletionIndicator(outboundIntegrationHeader.getRefDocumentNo(), 0L);
        Optional<PreOutboundHeaderV2> orderProcessedStatus =
                preOutboundHeaderV2Repository.findByRefDocNumberAndOutboundOrderTypeIdAndDeletionIndicator(
                        outboundIntegrationHeader.getRefDocumentNo(), outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);

        if (!orderProcessedStatus.isEmpty()) {
//            orderService.updateProcessedOrderV2(outboundIntegrationHeader.getRefDocumentNo(), 100L);
            throw new BadRequestException("Order :" + outboundIntegrationHeader.getRefDocumentNo() +
                    " already processed. Reprocessing can't be allowed.");
        }
            List<OutboundIntegrationLineV2> outboundIntegrationLines = outboundIntegrationHeader.getOutboundIntegrationLines();

        PickListCancellation createPickListCancellation = null;
//            Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> warehouse = null;
            String warehouseId = outboundIntegrationHeader.getWarehouseID();
            String companyCodeId = outboundIntegrationHeader.getCompanyCode();
            String plantId = outboundIntegrationHeader.getBranchCode();
            String languageId = outboundIntegrationHeader.getLanguageId() != null ? outboundIntegrationHeader.getLanguageId() : "EN";

            String finalCompanyCodeId = companyCodeId;
            String finalPlantId = plantId;
            String finalLanguageId = languageId;
            String finalWarehouseId = warehouseId;

            String masterServiceAuthToken = getMasterAuthToken();

//            try {
//                warehouse = warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
//                        outboundIntegrationHeader.getCompanyCode(), outboundIntegrationHeader.getBranchCode(), "EN", 0L);
//                log.info("warehouse : " + warehouse.get().getWarehouseId());
//
//                warehouseId = warehouse.get().getWarehouseId();
//                companyCodeId = outboundIntegrationHeader.getCompanyCode();
//                plantId = outboundIntegrationHeader.getBranchCode();
//                languageId = warehouse.get().getLanguageId();
//                log.info("warehouseId : " + warehouseId);
//            } catch (Exception e) {
//                e.printStackTrace();
//                throw e;
//            }

//            if (warehouse == null) {
//                log.info("warehouse not found.");
//                throw new BadRequestException("Warehouse cannot be null.");
//            }

        if (outboundIntegrationHeader.getOutboundOrderTypeID() == 4 || outboundIntegrationHeader.getReferenceDocumentType().equalsIgnoreCase("Sales Invoice")) {
            OutboundHeaderV2 updateOutboundHeaderAndLine = outboundHeaderService.updateOutboundHeaderForSalesInvoice(outboundIntegrationHeader, warehouseId);
            log.info("SalesInvoice Updated in OutboundHeader and Line");
            if (updateOutboundHeaderAndLine == null) {
                updateOutboundHeaderAndLine = new OutboundHeaderV2();
            }
            return updateOutboundHeaderAndLine;
        }

        //PickList Cancellation
        if (outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
            log.info("Executing PickList cancellation scenario pre - checkup process");
            String salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();

            //Check WMS order table
//            List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndDeletionIndicator(salesOrderNumber, 0L);
            List<OutboundHeaderV2> outbound = outboundHeaderV2Repository.findBySalesOrderNumberAndOutboundOrderTypeIdAndDeletionIndicator(salesOrderNumber, outboundIntegrationHeader.getOutboundOrderTypeID(), 0L);
            log.info("SalesOrderNumber already Exist: ---> PickList Cancellation to be executed " + salesOrderNumber);
            String newPickListNo = outboundIntegrationHeader.getPickListNumber();
            if (outbound != null && !outbound.isEmpty()) {
//                List<String> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).map(OutboundHeaderV2::getPickListNumber).collect(Collectors.toList());
                List<OutboundHeaderV2> oldPickListNo = outbound.stream().filter(n -> !n.getPickListNumber().equalsIgnoreCase(newPickListNo)).collect(Collectors.toList());
                log.info("Old PickList Number, New PickList Number: " + oldPickListNo + ", " + newPickListNo);

                if (oldPickListNo != null && !oldPickListNo.isEmpty()) {
                    for (OutboundHeaderV2 oldPickListNumber : oldPickListNo) {
                        OutboundHeaderV2 outboundOrderV2 =
                                outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                                        companyCodeId, languageId, plantId, warehouseId, oldPickListNumber.getPickListNumber(), oldPickListNumber.getPreOutboundNo(), 0L);
                        log.info("Outbound Order status ---> Delivered for old Picklist Number: " + outboundOrderV2 + ", " + oldPickListNumber);

                        if (outboundOrderV2 != null && outboundOrderV2.getInvoiceNumber() != null) {
                            // Update error message for the new PicklistNo
                            throw new BadRequestException("Picklist cannot be cancelled as Sales order associated with picklist - Invoice has been raised");
                        }

                        log.info("Old PickList Number: " + oldPickListNumber.getPickListNumber() +", " +
                                oldPickListNumber.getPreOutboundNo()  + " Cancellation Initiated and followed by New PickList " + newPickListNo + " creation started");

                        //Delete old PickListData
                        createPickListCancellation = pickListCancellation(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber.getPickListNumber(), newPickListNo, oldPickListNumber.getPreOutboundNo(), "MW_AMS");
                    }
                }
            }
        }

        // Getting PreOutboundNo from NumberRangeTable
        String preOutboundNo = getPreOutboundNo(warehouseId, companyCodeId, plantId, languageId);
            AtomicReference<String> refField1ForOrderType = new AtomicReference<>();

        List<PreOutboundLineV2> overallCreatedPreoutboundLineList = new ArrayList<>();

            outboundIntegrationLines.forEach(outboundIntegrationLine -> {
                try {
            log.info("outboundIntegrationLine : " + outboundIntegrationLine);

            /*-------------Insertion of BOM item in preOutboundLine table---------------------------------------------------------*/
            /*
             * Before Insertion into PREOUTBOUNDLINE table , validate the Below
             * Pass the WH_ID/ITM_CODE as PAR_ITM_CODE into BOMHEADER table and validate the records,
             * If record is not Null then fetch BOM_NO Pass BOM_NO in BOMITEM table and fetch CHL_ITM_CODE and
             * CHL_ITM_QTY values and insert along with PAR_ITM_CODE in PREOUTBOUNDLINE table as below
             * Insertion of CHL_ITM_CODE values
             */
            BomHeader bomHeader = mastersService.getBomHeader(outboundIntegrationLine.getItemCode(),
                                                                      finalCompanyCodeId, finalPlantId, finalLanguageId, finalWarehouseId, masterServiceAuthToken);
            if (bomHeader != null) {
                        BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), finalCompanyCodeId, finalPlantId, finalLanguageId, finalWarehouseId, masterServiceAuthToken);
                List<PreOutboundLineV2> toBeCreatedpreOutboundLineList = new ArrayList<>();
                        List<CompletableFuture<Void>> futures = new ArrayList<>();
                        // Create a thread pool with a fixed number of threads
                        ExecutorService executor = Executors.newFixedThreadPool(5);
                        // Batch processing of preAlerts using streams and CompletableFuture

                        Arrays.stream(bomLine).forEach(dbBomLine -> {
                            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                                toBeCreatedpreOutboundLineList.add(createPreOutboundLineBOMBasedV2(finalCompanyCodeId, finalPlantId, finalLanguageId,
                                                                                                   preOutboundNo, outboundIntegrationHeader, dbBomLine, outboundIntegrationLine));
                            }, executor);
                            futures.add(future);
                        });

                        // Wait for all the futures to complete
                        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                        // Shut down the executor service
                        executor.shutdown();

                // Batch Insert - preOutboundLines
                if (!toBeCreatedpreOutboundLineList.isEmpty()) {
                    List<PreOutboundLineV2> createdpreOutboundLine = preOutboundLineV2Repository.saveAll(toBeCreatedpreOutboundLineList);
                    log.info("createdpreOutboundLine [BOM] : " + createdpreOutboundLine);
                            log.info("Outbound Process BOM PreOutboundLine Finished----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
                    overallCreatedPreoutboundLineList.addAll(createdpreOutboundLine);
                }
            }
                    refField1ForOrderType.set(outboundIntegrationLine.getRefField1ForOrderType());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Error on processing OrderManagementLine " + e);
        }
            });

        /*
         * Inserting BOM Line records in OutboundLine and OrderManagementLine
         */
        if (!overallCreatedPreoutboundLineList.isEmpty()) {
                try {
                    List<CompletableFuture<Void>> futures = new ArrayList<>();
                    // Create a thread pool with a fixed number of threads
                    ExecutorService executor = Executors.newFixedThreadPool(10);
                    // Batch processing of preAlerts using streams and CompletableFuture
                    overallCreatedPreoutboundLineList.forEach(preOutboundLine -> {
                        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                            OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(finalCompanyCodeId, finalPlantId, finalLanguageId,
                                                                                                    preOutboundNo, outboundIntegrationHeader, preOutboundLine, masterServiceAuthToken);
                log.info("orderManagementLine created---BOM---> : " + orderManagementLine);
                            log.info("Outbound Process BOM OrderManagementLine Finished----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
                        }, executor);
                        futures.add(future);
                    });

                    // Wait for all the futures to complete
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    // Shut down the executor service
                    executor.shutdown();
//            for (PreOutboundLineV2 preOutboundLine : overallCreatedPreoutboundLineList) {
////                 OrderManagementLine
//                OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(companyCodeId,
//                        plantId, languageId, preOutboundNo, outboundIntegrationHeader, preOutboundLine);
//                log.info("orderManagementLine created---BOM---> : " + orderManagementLine);
//            }

            /*------------------Record Insertion in OUTBOUNDLINE table--for BOM---------*/
                    createOutboundLineV2(overallCreatedPreoutboundLineList, outboundIntegrationHeader);
//            List<OutboundLineV2> createOutboundLineListForBOM = createOutboundLineV2(overallCreatedPreoutboundLineList, outboundIntegrationHeader);
//            log.info("createOutboundLine created : " + createOutboundLineListForBOM);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Error on processing OrderManagementLine " + e);
                }
        }

        /*
         * Append PREOUTBOUNDLINE table through below logic
         */
            List<PreOutboundLineV2> createPreOutboundLineList = new ArrayList<>();
        List<PreOutboundLineV2> createdPreOutboundLineList = new ArrayList<>();
            log.info("Outbound Process PreOutboundLine Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
            try {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                // Create a thread pool with a fixed number of threads
                ExecutorService executor = Executors.newFixedThreadPool(10);
                // Batch processing of preAlerts using streams and CompletableFuture
                outboundIntegrationLines.forEach(outboundIntegrationLine -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(finalCompanyCodeId, finalPlantId, finalLanguageId, finalWarehouseId,
                                                                                    preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine, masterServiceAuthToken);
                        log.info("preOutboundLine create---1---> : " + preOutboundLine + " -------> " + new Date());
                        createPreOutboundLineList.add(preOutboundLine);
                    }, executor);
                    futures.add(future);
                });

                // Wait for all the futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                // Shut down the executor service
                executor.shutdown();
                createdPreOutboundLineList = preOutboundLineV2Repository.saveAll(createPreOutboundLineList);
                log.info("Outbound Process PreOutboundLine Fininshed----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Error on processing OrderManagementLine " + e);
            }

            log.info("Outbound Process OML Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
            try {
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                // Create a thread pool with a fixed number of threads
                ExecutorService executor = Executors.newFixedThreadPool(10);
                // Batch processing of preAlerts using streams and CompletableFuture
                createdPreOutboundLineList.forEach(preOutboundLine -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                // OrderManagementLine
                        OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(finalCompanyCodeId, finalPlantId, finalLanguageId,
                                                                                                preOutboundNo, outboundIntegrationHeader, preOutboundLine, masterServiceAuthToken);
                        log.info("orderManagementLine created---1---> : " + orderManagementLine + " -------> " + new Date());
                    }, executor);
                    futures.add(future);
                });

                // Wait for all the futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                // Shut down the executor service
                executor.shutdown();
                log.info("Outbound Process PreOutboundLine & OML Fininshed----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("Error on processing OrderManagementLine " + e);
            }
//            for (OutboundIntegrationLineV2 outboundIntegrationLine : outboundIntegrationHeader.getOutboundIntegrationLines()) {
//            // PreOutboundLine
//            try {
//                PreOutboundLineV2 preOutboundLine = createPreOutboundLineV2(companyCodeId,
//                        plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, outboundIntegrationLine);
//                PreOutboundLineV2 createdPreOutboundLine = preOutboundLineV2Repository.save(preOutboundLine);
//                log.info("preOutboundLine created---1---> : " + createdPreOutboundLine);
//                createdPreOutboundLineList.add(createdPreOutboundLine);
//
//                // OrderManagementLine
//                OrderManagementLineV2 orderManagementLine = createOrderManagementLineV2(companyCodeId,
//                        plantId, languageId, preOutboundNo, outboundIntegrationHeader, preOutboundLine);
//                log.info("orderManagementLine created---1---> : " + orderManagementLine);
//
//            } catch (Exception e) {
//                log.error("Error on processing PreOutboundLine : " + e.toString());
//                e.printStackTrace();
//            }
//        }

        /*------------------Record Insertion in OUTBOUNDLINE tables-----------*/
            createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
//        List<OutboundLineV2> createOutboundLineList = createOutboundLineV2(createdPreOutboundLineList, outboundIntegrationHeader);
//            log.info("createOutboundLine created : " + createOutboundLineList.size());

        /*------------------Insert into PreOutboundHeader table-----------------------------*/
        PreOutboundHeaderV2 createdPreOutboundHeader = createPreOutboundHeaderV2(companyCodeId,
                                                                                     plantId, languageId, warehouseId, preOutboundNo, outboundIntegrationHeader, refField1ForOrderType.get());
        log.info("preOutboundHeader Created : " + createdPreOutboundHeader);

        /*------------------ORDERMANAGEMENTHEADER TABLE-------------------------------------*/
        OrderManagementHeaderV2 createdOrderManagementHeader = createOrderManagementHeaderV2(createdPreOutboundHeader);
        log.info("OrderMangementHeader Created : " + createdOrderManagementHeader);

        /*------------------Record Insertion in OUTBOUNDHEADER/OUTBOUNDLINE tables-----------*/
//        OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId());
        OutboundHeaderV2 outboundHeader = createOutboundHeaderV2(createdPreOutboundHeader, createdOrderManagementHeader.getStatusId(), outboundIntegrationHeader);

        //check the status of OrderManagementLine for NoStock update status of outbound header, preoutbound header, preoutboundline
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, languageId);
        orderManagementLineV2Repository.updateNostockStatusUpdateProc(companyCodeId, plantId, languageId, warehouseId, outboundHeader.getRefDocNumber(), outboundHeader.getPreOutboundNo(), 47L, statusDescription);
        log.info("No stock status updated in preinbound header and line, outbound header using stored procedure when condition is satisfied");

        /*------------------------------------------------------------------------------------*/
//        updateStatusAs47ForOBHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, outboundHeader.getRefDocNumber());

        /*------------------------------------------------------------------------------------*/
        //Pickup Header Automation only for Picklist Header - OutboundOrderTypeId - 3L --> wh_id = 200 referenceDocumentType - Pick List
        //for wh_id = 200 ---> ob_type_id=3 && wh_id = 100 ---> ob_type_id=0,1,3
        if ((warehouseId.equalsIgnoreCase("200") && outboundIntegrationHeader.getOutboundOrderTypeID() == 3L) ||
                (warehouseId.equalsIgnoreCase("100") && (outboundIntegrationHeader.getOutboundOrderTypeID() == 0L || outboundIntegrationHeader.getOutboundOrderTypeID() == 1L || outboundIntegrationHeader.getOutboundOrderTypeID() == 3L))) {
//            updateStatusAs48ForPickupHeaderCreateSuccess(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
//            updateStatusAs48ForPickupHeader(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
//            createPickUpHeaderAssignPicker(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader, preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
            createPickUpHeaderAssignPickerModified(companyCodeId, plantId, languageId, warehouseId, outboundIntegrationHeader,
                    preOutboundNo, outboundHeader.getRefDocNumber(), outboundHeader.getPartnerCode());
        }

        //PickList Cancellation
        if (outboundIntegrationHeader.getOutboundOrderTypeID() == 3) {
            if(createPickListCancellation != null){
            log.info("Executing Post PickList cancellation scenario");
            String salesOrderNumber = outboundIntegrationHeader.getSalesOrderNumber();
            companyCodeId = outboundIntegrationHeader.getCompanyCode();
            plantId = outboundIntegrationHeader.getBranchCode();
            warehouseId = outboundIntegrationHeader.getWarehouseID();
            languageId = outboundIntegrationHeader.getLanguageId();
            //String loginUserID = "MW_AMS";                                     //Hard Coded

            //Create PickListData
            createPickListCancellation(companyCodeId, plantId, languageId, warehouseId,
                    createPickListCancellation.getOldPickListNumber(), createPickListCancellation.getNewPickListNumber(),
                    preOutboundNo, createPickListCancellation, "MW_AMS");
            }
        }
            log.info("Finished Outbound Processing----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        return outboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Outbound Order Processing Bad Request Exception : " + e);
        }
    }

    /**
     * New Logic - same picker for single order - differ only by level (i.e., single picker assigned for n number of items in same level/floor )
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void createPickUpHeaderAssignPickerModified(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                   String preOutboundNo, String refDocNumber, String partnerCode) throws InvocationTargetException, IllegalAccessException, ParseException, FirebaseMessagingException {
        log.info("Outbound Process pickup Header Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

        //pickup header create
        Long assignPickerListCount = 0L;
        Long hhtUserCount = 0L;
        Long lineCount = 0L;

        lineCount = orderManagementLineV2List.stream().count();
        log.info("total Order Lines Count: " + lineCount);

        List<String> assignPickerList = new ArrayList<>();
        List<OrderManagementLineV2> orderManagementLineOrderedList = new ArrayList<>();
        List<OrderManagementLineV2> orderManagementLineRemainingList = new ArrayList<>();
        List<OrderManagementLineV2> orderManagementLineFilterList = new ArrayList<>();
        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());

            List<String> assignPickerPickUpHeaderList = pickupHeaderV2Repository.getPickupHeaderAssignPickerList(
                    companyCodeId, plantId, languageId, warehouseId, 48L, dates[0], dates[1]);
            log.info("assignPickerPickUpHeaderList : " + assignPickerPickUpHeaderList.size());
            if (assignPickerPickUpHeaderList != null && !assignPickerPickUpHeaderList.isEmpty()) {
                for(OrderManagementLineV2 dbOrderManagementLine : orderManagementLineV2List){
                    String itmMfrName = dbOrderManagementLine.getItemCode()+dbOrderManagementLine.getManufacturerName();
                    boolean itemPresent = assignPickerPickUpHeaderList.stream().allMatch(a -> a.equalsIgnoreCase(itmMfrName));
                    if(itemPresent) {
                        orderManagementLineFilterList.add(dbOrderManagementLine);
                    }
                    if(!itemPresent) {
                        orderManagementLineRemainingList.add(dbOrderManagementLine);
                    }
                }
                log.info("OrderManagementLine sameItem Present : " + orderManagementLineFilterList.size());
                log.info("OrderManagementLine No sameItem Present : " + orderManagementLineRemainingList.size());
            }
            if(orderManagementLineFilterList != null && !orderManagementLineFilterList.isEmpty()){
                orderManagementLineOrderedList.addAll(orderManagementLineFilterList);
            }
            if(orderManagementLineRemainingList != null && !orderManagementLineRemainingList.isEmpty()) {
                orderManagementLineOrderedList.addAll(orderManagementLineRemainingList);
            }
            if((orderManagementLineFilterList == null || orderManagementLineFilterList.isEmpty())
                    && (orderManagementLineRemainingList == null || orderManagementLineRemainingList.isEmpty())){
                orderManagementLineOrderedList = orderManagementLineV2List;
            }

        for (OrderManagementLineV2 orderManagementLine : orderManagementLineOrderedList) {
            String assignPickerId = null;

            if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                log.info("orderManagementLine: " + orderManagementLine);

                int currentTime = DateUtils.getCurrentTime();
                log.info("CurrentTime: " + currentTime);

//                if (currentTime < 15) {

                if (warehouseId.equalsIgnoreCase("100")) {
                    log.info("warehouseId: " + warehouseId);

                    Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
                    log.info("OutboundOrderTypeId: " + OB_ORD_TYP_ID);

                    List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListNew(OB_ORD_TYP_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserIdList);

                    List<String> hhtUserList = new ArrayList<>();
                    List<String> absentHhtUserList = new ArrayList<>();
                    if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                        for (HHTUser dbHhtUser : hhtUserIdList) {
                            if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                        dbHhtUser.getCompanyCodeId(),
                                        dbHhtUser.getLanguageId(),
                                        dbHhtUser.getPlantId(),
                                        dbHhtUser.getWarehouseId(),
                                        dbHhtUser.getUserId(),
                                        dbHhtUser.getStartDate(),
                                        dbHhtUser.getEndDate());
                                log.info("HHt User Absent: " + userPresent);
                                if (userPresent != null && !userPresent.isEmpty()) {
                                    absentHhtUserList.add(dbHhtUser.getUserId());
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        }
                    }
                    log.info("Present HHtUser List: " + hhtUserList);
                    log.info("Absent HHtUser List: " + absentHhtUserList);

                    if (hhtUserList != null && !hhtUserList.isEmpty()) {

                            hhtUserCount = hhtUserList.stream().count();
                            log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomationByOutboundOrderTypeId(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName(), OB_ORD_TYP_ID);
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem ---> same level: " + assignPickerPickUpHeader);
                            if (assignPickerPickUpHeader != null) {
                            List<String> userPresentInSelectedLevel = hhtUserList.stream().filter(n -> n.equalsIgnoreCase(assignPickerPickUpHeader.getAssignedPickerId())).collect(Collectors.toList());
                            log.info("userPresentInSelectedLevel: " + userPresentInSelectedLevel);
                            if(userPresentInSelectedLevel != null && !userPresentInSelectedLevel.isEmpty()) {
                                log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                                assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                            }
                        }

                            if (assignPickerId == null) {
                                log.info("assignPickerId: " + assignPickerId);
                                assignPickerList = new ArrayList<>();
                                outerLoop2:
                                for (String hhtUser : hhtUserList) {
                                    PickupHeaderV2 pickerPickupHeaderSameList = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrderNew(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderSameList);
                                    if (pickerPickupHeaderSameList != null) {
                                        assignPickerList.add(hhtUser);
                                        if (assignPickerList.size() > 0) {
                                            break outerLoop2;
                                        }
                                    }
                                }
                                outerLoop1:
                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
                                outerLoop3:
                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 50L);
                                        List<PickupHeaderV2> pickupHeader48List = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 48L);
                                        log.info("pickUpHeader: " + pickupHeaderList.size());
                                    if ((pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
                                            (pickupHeader48List == null || pickupHeader48List.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop3;
                                            }
                                        }
                                    }
                                }
                                outerLoop:
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {

                                List<String> pickerCountList_50 = pickupHeaderV2Repository
                                        .getPickUpheader50AssignPickerObTypeList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 50L, dates[0], dates[1]);
                                Set<String> pickerCountList_50_filtered = pickerCountList_50.stream().collect(Collectors.toSet());
                                log.info("assigned Picker status_50L: " + pickerCountList_50);
                                log.info("assigned Picker status_50L_filtered: " + pickerCountList_50_filtered);
                                if(pickerCountList_50_filtered != null && !pickerCountList_50_filtered.isEmpty()){
                                    for(String dbAssignpickerId : pickerCountList_50_filtered) {
                                        List<String> pickerCountList_48 = pickupHeaderV2Repository
                                                .getPickUpheaderAssignPickerAmgharaListNew(companyCodeId, plantId, languageId, warehouseId, dbAssignpickerId, OB_ORD_TYP_ID, 48L, dates[0], dates[1]);
                                        if (pickerCountList_48 == null || pickerCountList_48.isEmpty()) {
                                            IKeyValuePair pickupHeaderPickerByCount = pickupHeaderV2Repository.getAssignPickerNew(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 50L, dates[0], dates[1]);
                                            if (pickupHeaderPickerByCount != null) {
                                                boolean isPickerSame = dbAssignpickerId.equalsIgnoreCase(pickupHeaderPickerByCount.getAssignPicker());
                                                if(isPickerSame) {
                                                    assignPickerList.add(dbAssignpickerId);
                                                    log.info("assigned Picker: " + assignPickerList.get(0));
                                            if (assignPickerList.size() > 0) {
                                                break outerLoop;
                                            }
                                        }
                                        }
                                    }
                                            }
                                        }

                                List<String> pickerCountList = pickupHeaderV2Repository
                                        .getPickUpheaderAssignPickerAmgharaList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, OB_ORD_TYP_ID, 48L, dates[0], dates[1]);
                                log.info("assigned Picker status_48L: " + pickerCountList);
                                        if(pickerCountList != null && !pickerCountList.isEmpty()) {
                                    assignPickerList.add(pickerCountList.get(0));
                                                            log.info("assigned Picker: " + assignPickerList.get(0));
                                                            if (assignPickerList.size() > 0) {
                                                                break outerLoop;
                                                            }
                                                        }
                                                    }
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                                if(assignPickerList != null && !assignPickerList.isEmpty()) {
                                    assignPickerId = assignPickerList.get(0);
                                }
                                log.info("assignPickerId: " + assignPickerId);
                            }
                        }
                }
//            }
            if (warehouseId.equalsIgnoreCase("200")) {
                log.info("warehouseId: " + warehouseId);

                Long LEVEL_ID = Long.valueOf(orderManagementLine.getLevelId());
                log.info("levelId: " + LEVEL_ID);

                List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListByLevelIdNew(LEVEL_ID, companyCodeId, plantId, languageId, warehouseId);
                log.info("hhtUserList: " + hhtUserIdList);

                List<String> hhtUserList = new ArrayList<>();
                List<String> absentHhtUserList = new ArrayList<>();
                if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                    for (HHTUser dbHhtUser : hhtUserIdList) {
                        if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                            List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                    dbHhtUser.getCompanyCodeId(),
                                    dbHhtUser.getLanguageId(),
                                    dbHhtUser.getPlantId(),
                                    dbHhtUser.getWarehouseId(),
                                    dbHhtUser.getUserId(),
                                    dbHhtUser.getStartDate(),
                                    dbHhtUser.getEndDate());
                            log.info("HHt User Absent: " + userPresent);
                            if (userPresent != null && !userPresent.isEmpty()) {
                                absentHhtUserList.add(dbHhtUser.getUserId());
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        } else {
                            hhtUserList.add(dbHhtUser.getUserId());
                        }
                    }
                }
                log.info("Present HHtUser List: " + hhtUserList);
                log.info("Absent HHtUser List: " + absentHhtUserList);

                if (hhtUserList != null && !hhtUserList.isEmpty()) {

                    hhtUserCount = hhtUserList.stream().count();
                    log.info("hhtUserList count: " + hhtUserCount);

                    PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomationByLevelId(companyCodeId, plantId, languageId, warehouseId,
                            orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName(), String.valueOf(LEVEL_ID));
                    log.info("pickupHeader--> Status48---> assignPicker---> SameItem ---> same level: " + assignPickerPickUpHeader);
                    if (assignPickerPickUpHeader != null) {
                        List<String> userPresentInSelectedLevel = hhtUserList.stream().filter(n -> n.equalsIgnoreCase(assignPickerPickUpHeader.getAssignedPickerId())).collect(Collectors.toList());
                        log.info("userPresentInSelectedLevel: " + userPresentInSelectedLevel);
                        if(userPresentInSelectedLevel != null && !userPresentInSelectedLevel.isEmpty()) {
                        log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                        assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }
                    }

                    if (assignPickerId == null) {
                        log.info("assignPickerId: " + assignPickerId);
                        assignPickerList = new ArrayList<>();
                        outerLoop2:
                        for (String hhtUser : hhtUserList) {
                            PickupHeaderV2 pickerPickupHeaderSameList = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrderNew(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                            log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderSameList);
                            if (pickerPickupHeaderSameList != null) {
                                assignPickerList.add(hhtUser);
                                if (assignPickerList.size() > 0) {
                                    break outerLoop2;
                                }
                            }
                        }
                        outerLoop1:
                        if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                log.info("pickUpHeader: " + pickupHeaderList.size());
                                if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUser);
                                    if (assignPickerList.size() > 0) {
                                        break outerLoop1;
                                    }
                                }
                            }
                        }
                        outerLoop3:
                        if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 50L);
                                List<PickupHeaderV2> pickupHeader48List = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUser, 48L);
                                log.info("pickUpHeader: " + pickupHeaderList.size());
                                if ((pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
                                        (pickupHeader48List == null || pickupHeader48List.isEmpty())) {
                                    assignPickerList.add(hhtUser);
                                    if (assignPickerList.size() > 0) {
                                        break outerLoop3;
                                    }
                                }
                            }
                        }
                        outerLoop:
                        if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {

//                                    String assignPickerPickupHeader50 = pickupHeaderService.getPickupHeaderAutomateCurrentDateHhtListCount(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 50L);

//                                    if(assignPickerPickupHeader50 != null) {
//                                        assignPickerList.add(assignPickerPickupHeader50);
//                                        if (assignPickerList.size() > 0) {
//                                            break outerLoop;
//                                        }
//                                    }

                            List<String> pickerCountList_50 = pickupHeaderV2Repository
                                            .getPickUpheader50AssignPickerList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 50L, dates[0], dates[1]);
                            Set<String> pickerCountList_50_filtered = pickerCountList_50.stream().collect(Collectors.toSet());
                            log.info("assigned Picker status_50L: " + pickerCountList_50);
                            log.info("assigned Picker status_50L_filtered: " + pickerCountList_50_filtered);
                            if(pickerCountList_50_filtered != null && !pickerCountList_50_filtered.isEmpty()){
                                for(String dbAssignpickerId : pickerCountList_50_filtered) {
                                    List<String> pickerCountList_48 = pickupHeaderV2Repository
                                            .getPickUpheaderAssignPickerListNew(companyCodeId, plantId, languageId, warehouseId, dbAssignpickerId, LEVEL_ID, 48L, dates[0], dates[1]);
                                    if (pickerCountList_48 == null || pickerCountList_48.isEmpty()) {
                                        IKeyValuePair pickupHeaderPickerByCount = pickupHeaderV2Repository.getAssignPickerNew(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 50L, dates[0], dates[1]);
                                        if (pickupHeaderPickerByCount != null) {
                                            boolean isPickerSame = dbAssignpickerId.equalsIgnoreCase(pickupHeaderPickerByCount.getAssignPicker());
                                            if(isPickerSame) {
                                                assignPickerList.add(dbAssignpickerId);
                                                log.info("assigned Picker: " + assignPickerList.get(0));
                                                if (assignPickerList.size() > 0) {
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            List<String> pickerCountList = pickupHeaderV2Repository
                                            .getPickUpheaderAssignPickerList(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 48L, dates[0], dates[1]);
                            log.info("assigned Picker status_48L: " + pickerCountList);
                            if(pickerCountList != null && !pickerCountList.isEmpty()){
                                assignPickerList.add(pickerCountList.get(0));
                                log.info("assigned Picker: " + assignPickerList.get(0));
                                if (assignPickerList.size() > 0) {
                                    break outerLoop;
                                }
                            }

//                                    List<IKeyValuePair> pickerCountList = pickupHeaderV2Repository
//                                            .getAssignPicker(companyCodeId, plantId, languageId, warehouseId, hhtUserList, LEVEL_ID, 48L, dates[0], dates[1]);
//                                    if(pickerCountList != null && !pickerCountList.isEmpty()) {
//                                        for(IKeyValuePair iKeyValuePair : pickerCountList){             //for log purpose only will comment later
//                                            log.info("Picker Count - Picker : " + iKeyValuePair.getPickerCount() + " - " + iKeyValuePair.getAssignPicker());
//                                        }
//                                        List<String> hhtUser48List = pickerCountList.stream().map(IKeyValuePair::getAssignPicker).collect(Collectors.toList());
//                                        log.info("Filter Picker List based on Count : " + hhtUser48List);
//                                        List<IKeyValuePair> pickerCountListCreatedOn = pickupHeaderV2Repository
//                                                .getAssignPickerList(companyCodeId, plantId, languageId, warehouseId, hhtUser48List, LEVEL_ID, 48L, dates[0], dates[1]);
//                                        if(pickerCountListCreatedOn != null && !pickerCountListCreatedOn.isEmpty()) {
//                                            for(IKeyValuePair pickerCheck : pickerCountList ){
//                                                for(IKeyValuePair pickerCheckByCreatedOn : pickerCountListCreatedOn) {
//                                                    log.info("Picker_Count, Picker Sortby Created On : "
//                                                            + pickerCheck.getAssignPicker() + ", "
//                                                            + pickerCheck.getPickerCount() + ", " + pickerCheckByCreatedOn.getAssignPicker());
//                                                    if(pickerCheck.getAssignPicker().equalsIgnoreCase(pickerCheckByCreatedOn.getAssignPicker())) {
//                                                        log.info("picker, count, ctdOn Picker : " + pickerCheck.getAssignPicker()
//                                                                + "," + pickerCheck.getPickerCount() + ", " +pickerCheckByCreatedOn.getAssignPicker());
//                                                        assignPickerList.add(pickerCheck.getAssignPicker());
//                                                        log.info("assigned Picker: " + assignPickerList.get(0));
//                                                        if (assignPickerList.size() > 0) {
//                                                            break outerLoop;
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }

//                                }
                        }
                        if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                            assignPickerList.add(hhtUserList.get(0));
                        }
                        if(assignPickerList != null && !assignPickerList.isEmpty()) {
                                assignPickerId = assignPickerList.get(0);
                        }
                        log.info("assignPickerId: " + assignPickerId);
                    }
                }
            }

                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                long NUM_RAN_CODE = 10;
                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                log.info("PU_NO : " + PU_NO);

                newPickupHeader.setAssignedPickerId(assignPickerId);
                newPickupHeader.setPickupNumber(PU_NO);

                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
                // PICK_UOM
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                // REF_FIELD_1
                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());

                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                newPickupHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                newPickupHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                newPickupHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());

                newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
                newPickupHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
                newPickupHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
                newPickupHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

                PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLine.setPickupNumber(PU_NO);
                orderManagementLine.setAssignedPickerId(assignPickerId);
                orderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                orderManagementLine.setStatusDescription(statusDescription);
//                    orderManagementLine.setPickupUpdatedBy("Automate");            // Ref_field_7
                orderManagementLine.setPickupUpdatedOn(new Date());
                OrderManagementLineV2 orderManagementLineV2 = orderManagementLineV2Repository.save(orderManagementLine);
                log.info("OrderManagementLine updated : " + orderManagementLineV2);
//                    }

                /*
                 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                 * OUTBOUNDLINE table and update SATATU_ID as 48
                 */
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode,
                        orderManagementLine.getLineNumber(),
                        orderManagementLine.getItemCode());
                outboundLine.setAssignedPickerId(assignPickerId);
                outboundLine.setStatusId(48L);
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);
            }
        }

        //push notification separated from pickup header and consolidated notification sent
        orderManagementLineService.sendPushNotification(preOutboundNo, warehouseId);

        // OutboundHeader Update
        OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode);
        outboundHeader.setStatusId(48L);
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeaderV2Repository.save(outboundHeader);
        log.info("outboundHeader updated : " + outboundHeader);

        // ORDERMANAGEMENTHEADER Update
        OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                .getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        orderManagementHeader.setStatusId(48L);
        orderManagementHeader.setStatusDescription(statusDescription);
        orderManagementHeaderV2Repository.save(orderManagementHeader);
        log.info("orderManagementHeader updated : " + orderManagementHeader);
        log.info("Outbound Process pickup Header finished----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
    }

    private void createPickUpHeaderAssignPicker(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                String preOutboundNo, String refDocNumber, String partnerCode) throws InvocationTargetException, IllegalAccessException, ParseException, FirebaseMessagingException {

        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

        //pickup header create
        Long assignPickerListCount = 0L;
        Long hhtUserCount = 0L;
        Long lineCount = 0L;

        lineCount = orderManagementLineV2List.stream().count();
        log.info("total Order Lines Count: " + lineCount);

        List<String> assignPickerList = new ArrayList<>();

        for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {
            String assignPickerId = null;

            if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                log.info("orderManagementLine: " + orderManagementLine);

                int currentTime = DateUtils.getCurrentTime();
                log.info("CurrentTime: " + currentTime);

//                if (currentTime < 15) {

                if (warehouseId.equalsIgnoreCase("100")) {
                    log.info("warehouseId: " + warehouseId);

                    Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
                    log.info("OutboundOrderTypeId: " + OB_ORD_TYP_ID);

                    List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListNew(OB_ORD_TYP_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserIdList);

                    List<String> hhtUserList = new ArrayList<>();
                    List<String> absentHhtUserList = new ArrayList<>();
                    if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                        for (HHTUser dbHhtUser : hhtUserIdList) {
                            if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                        dbHhtUser.getCompanyCodeId(),
                                        dbHhtUser.getLanguageId(),
                                        dbHhtUser.getPlantId(),
                                        dbHhtUser.getWarehouseId(),
                                        dbHhtUser.getUserId(),
                                        dbHhtUser.getStartDate(),
                                        dbHhtUser.getEndDate());
                                log.info("HHt User Absent: " + userPresent);
                                if (userPresent != null && !userPresent.isEmpty()) {
                                    absentHhtUserList.add(dbHhtUser.getUserId());
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        }
                    }
                    log.info("Present HHtUser List: " + hhtUserList);
                    log.info("Absent HHtUser List: " + absentHhtUserList);

                    if (hhtUserList != null && !hhtUserList.isEmpty()) {
                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);
                    }
                    if (hhtUserList != null && !hhtUserList.isEmpty()) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);

                            assignPickerList = new ArrayList<>();

                            outerLoop1:
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                log.info("pickUpHeader: " + pickupHeaderList.size());
                                if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUser);
                                    assignPickerListCount = assignPickerList.stream().count();
                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                    if (assignPickerListCount > 0) {
                                        break outerLoop1;
                                    }
                                }
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    List<PickupLineV2> pickerPickupLineList = pickupLineService.getPickupLineAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("Pickupline for Current Date: " + pickerPickupLineList);
                                    if (pickerPickupLineList != null && !pickerPickupLineList.isEmpty()) {
                                        List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList);
                                        List<PickupHeaderV2> pickerPickupHeaderSameOrderList = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderSameOrderList);
                                        if (pickerPickupHeaderSameOrderList != null && !pickerPickupHeaderSameOrderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if ((pickerPickupHeaderSameOrderList == null || pickerPickupHeaderSameOrderList.isEmpty()) &&
                                                (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                    if (pickerPickupLineList == null || pickerPickupLineList.isEmpty()) {
                                        List<PickupHeaderV2> pickerPickupHeaderList2 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList2);
                                        List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                        if (pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if ((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                                (pickerPickupHeaderList2 == null || pickerPickupHeaderList2.isEmpty() || pickerPickupHeaderList2.size() == 0)) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
                            }
                            if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.
                                        getPickupHeaderAutomateCurrentDateHhtList(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                                if (pickerPickupHeaderList != null && !pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(pickerPickupHeaderList.get(0).getAssignedPickerId());
                                }
                                if (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                            }
                            if (assignPickerList != null && !assignPickerList.isEmpty()){
                                assignPickerId = assignPickerList.get(0);
                            }
                            log.info("assignPickerId: " + assignPickerId);
                        }
                    }
                }
//            }
                if (warehouseId.equalsIgnoreCase("200")) {
                    log.info("warehouseId: " + warehouseId);

                    if (orderManagementLine.getLevelId() == null) {
                        orderManagementLine.setLevelId("1");                //HardCode default Level
                    }
                    Long LEVEL_ID = Long.valueOf(orderManagementLine.getLevelId());
                    log.info("levelId: " + LEVEL_ID);

                    List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListByLevelIdNew(LEVEL_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserIdList);

                    List<String> hhtUserList = new ArrayList<>();
                    List<String> absentHhtUserList = new ArrayList<>();
                    if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                        for (HHTUser dbHhtUser : hhtUserIdList) {
                            if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                        dbHhtUser.getCompanyCodeId(),
                                        dbHhtUser.getLanguageId(),
                                        dbHhtUser.getPlantId(),
                                        dbHhtUser.getWarehouseId(),
                                        dbHhtUser.getUserId(),
                                        dbHhtUser.getStartDate(),
                                        dbHhtUser.getEndDate());
                                log.info("HHt User Absent: " + userPresent);
                                if (userPresent != null && !userPresent.isEmpty()) {
                                    absentHhtUserList.add(dbHhtUser.getUserId());
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        }
                    }
                    log.info("Present HHtUser List: " + hhtUserList);
                    log.info("Absent HHtUser List: " + absentHhtUserList);

                    if (hhtUserList != null) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);
                            assignPickerList = new ArrayList<>();

                            outerLoop1:
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                log.info("pickUpHeader: " + pickupHeaderList);
                                if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUser);
                                    assignPickerListCount = assignPickerList.stream().count();
                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                    if(assignPickerListCount > 0) {
                                        break outerLoop1;
                                    }
                                }
                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    List<PickupLineV2> pickerPickupLineList = pickupLineService.getPickupLineAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("Pickupline for Current Date: " + pickerPickupLineList);
                                    if(pickerPickupLineList != null && !pickerPickupLineList.isEmpty()) {
                                        List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList);
                                        List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                        if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if(assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                                (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if(assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                    if(pickerPickupLineList == null || pickerPickupLineList.isEmpty()){
                                        List<PickupHeaderV2> pickerPickupHeaderList2 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList2);
                                        List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                        if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if(assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                                (pickerPickupHeaderList2 == null || pickerPickupHeaderList2.isEmpty() || pickerPickupHeaderList2.size() == 0)){
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if(assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
                            }
                            if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.
                                        getPickupHeaderAutomateCurrentDateHhtList(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                                if(pickerPickupHeaderList != null && !pickerPickupHeaderList.isEmpty()){
                                    assignPickerList.add(pickerPickupHeaderList.get(0).getAssignedPickerId());
                                }
                                if (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                            }

                            if(assignPickerList != null && !assignPickerList.isEmpty()) {
                                assignPickerId = assignPickerList.get(0);
                            }
                            log.info("assignPickerId: " + assignPickerId);
                        }
                    }
                }

                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                long NUM_RAN_CODE = 10;
                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                log.info("PU_NO : " + PU_NO);

                newPickupHeader.setAssignedPickerId(assignPickerId);
                newPickupHeader.setPickupNumber(PU_NO);

                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());

                // PICK_UOM
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                // REF_FIELD_1
                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());

                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                newPickupHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                newPickupHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                newPickupHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());

                newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
                newPickupHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
                newPickupHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
                newPickupHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

                PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLine.setPickupNumber(PU_NO);
                orderManagementLine.setAssignedPickerId(assignPickerId);
                orderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                orderManagementLine.setStatusDescription(statusDescription);
//                    orderManagementLine.setPickupUpdatedBy("Automate");            // Ref_field_7
                orderManagementLine.setPickupUpdatedOn(new Date());
                OrderManagementLineV2 orderManagementLineV2 = orderManagementLineV2Repository.save(orderManagementLine);
                log.info("OrderManagementLine updated : " + orderManagementLineV2);
//                    }

                /*
                 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                 * OUTBOUNDLINE table and update SATATU_ID as 48
                 */
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode,
                        orderManagementLine.getLineNumber(),
                        orderManagementLine.getItemCode());
                outboundLine.setStatusId(48L);
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);
            }
        }

        //push notification separated from pickup header and consolidated notification sent
        orderManagementLineService.sendPushNotification(preOutboundNo, warehouseId);

        // OutboundHeader Update
        OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode);
        outboundHeader.setStatusId(48L);
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeaderV2Repository.save(outboundHeader);
        log.info("outboundHeader updated : " + outboundHeader);

        // ORDERMANAGEMENTHEADER Update
        OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                .getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        orderManagementHeader.setStatusId(48L);
        orderManagementHeader.setStatusDescription(statusDescription);
        orderManagementHeaderV2Repository.save(orderManagementHeader);
        log.info("orderManagementHeader updated : " + orderManagementHeader);
    }

    private void updateStatusAs48ForPickupHeader(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                              String preOutboundNo, String refDocNumber, String partnerCode) throws InvocationTargetException, IllegalAccessException, ParseException, FirebaseMessagingException {

        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

        //pickup header create
        Long assignPickerIdLineCount = 0L;
        Long assignPickerListCount = 0L;
//        String assignPickerId = null;
        Long hhtUserCount = 0L;
        Long hhtUserCountInPickupHeader = 0L;
        Long lineCount = 0L;
        Integer pickerPosition = 0;
        Integer picker0Position = 0;
        Integer picker1Position = 0;
        Integer picker2Position = 0;
        Integer picker3Position = 0;
        Integer picker4Position = 0;

        Integer obPicker0Position = 0;
        Integer obPicker1Position = 0;
        Integer obPicker2Position = 0;
        Integer obPicker3Position = 0;


        lineCount = orderManagementLineV2List.stream().count();
        log.info("total Order Lines Count: " + lineCount);

        List<String> assignPickerList = new ArrayList<>();
        List<String> pickUpheaderUserList = new ArrayList<>();

        List<String> assignedPickerIdList = new ArrayList<>();
        List<String> linePickerIdAssignedList = new ArrayList<>();
        List<Long> levelId0List = new ArrayList<>();
        List<Long> levelId1List = new ArrayList<>();
        List<Long> levelId2List = new ArrayList<>();
        List<Long> levelId3List = new ArrayList<>();
        List<Long> levelId4List = new ArrayList<>();

        List<Long> obType0List = new ArrayList<>();
        List<Long> obType1List = new ArrayList<>();
        List<Long> obType2List = new ArrayList<>();
        List<Long> obType3List = new ArrayList<>();
        for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {
            String assignPickerId = null;

            if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                log.info("orderManagementLine: " + orderManagementLine);

                int currentTime = DateUtils.getCurrentTime();
                log.info("CurrentTime: " + currentTime);

//                if (currentTime < 15) {

                if (warehouseId.equalsIgnoreCase("100")) {
                    log.info("warehouseId: " + warehouseId);

                    Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
                    log.info("OutboundOrderTypeId: " + OB_ORD_TYP_ID);

                    if (OB_ORD_TYP_ID == 0) {
                        if (obType0List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker0Position = obPicker0Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 1) {
                        if (obType1List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker1Position = obPicker1Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 2) {
                        if (obType2List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker2Position = obPicker2Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 3) {
                        if (obType3List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker3Position = obPicker3Position + 1;
                        }
                    }


                    List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListNew(OB_ORD_TYP_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserIdList);

                    List<String> hhtUserList = new ArrayList<>();
                    List<String> absentHhtUserList = new ArrayList<>();
                    if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                        for (HHTUser dbHhtUser : hhtUserIdList) {
                            if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                        dbHhtUser.getCompanyCodeId(),
                                        dbHhtUser.getLanguageId(),
                                        dbHhtUser.getPlantId(),
                                        dbHhtUser.getWarehouseId(),
                                        dbHhtUser.getUserId(),
                                        dbHhtUser.getStartDate(),
                                        dbHhtUser.getEndDate());
                                log.info("HHt User Absent: " + userPresent);
                                if (userPresent != null && !userPresent.isEmpty()) {
                                    absentHhtUserList.add(dbHhtUser.getUserId());
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        }
                    }
                    log.info("Present HHtUser List: " + hhtUserList);
                    log.info("Absent HHtUser List: " + absentHhtUserList);

                    if (hhtUserList != null && !hhtUserList.isEmpty()) {
                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);
                    }
                    if (hhtUserList != null && !hhtUserList.isEmpty()) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);
                            List<PickupHeaderV2> pickupHeaderAssignedPickerIdList = pickupHeaderService.getPickupHeaderAutomationWithoutStatusId(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if (pickupHeaderAssignedPickerIdList != null && !pickupHeaderAssignedPickerIdList.isEmpty()) {
                                log.info("PickUpHeaderList Count: " + pickupHeaderAssignedPickerIdList.stream().count());
                            }

                            if (pickupHeaderAssignedPickerIdList != null) {
                                pickUpheaderUserList = pickupHeaderAssignedPickerIdList.stream().map(PickupHeaderV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                hhtUserCountInPickupHeader = pickupHeaderAssignedPickerIdList.stream().count();
                                log.info("hhtUser PickupHeader count: " + hhtUserCountInPickupHeader);
                            }

                            List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if (pickupLineList != null) {
                                log.info("PickupLineV2List count: " + pickupLineList.stream().count());
                            }
                            if (pickupLineList != null) {
                                linePickerIdAssignedList = pickupLineList.stream().map(PickupLineV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                assignPickerIdLineCount = linePickerIdAssignedList.stream().count();
                                log.info("assignPickerIdLineCount: " + assignPickerIdLineCount);
                            }

                            assignPickerList = new ArrayList<>();

                            assignPickerListCount = assignPickerList.stream().count();

//                            outerLoop:
//                            if (lineCount != assignPickerListCount) {
//                                log.info("lineCount != assignPickerListCount :" + lineCount + ", " + assignPickerListCount);
//                                for (int i = 1; i <= lineCount; ) {
//                                    log.info("i, lineCount: " + i + ", " + lineCount);
//                                    for (String hhtUser : hhtUserList) {
//                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
//                                        log.info("pickUpHeader: " + pickupHeaderList);
//                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//                                    if (pickupLineList != null) {
//                                        log.info("pickupLineList: " + linePickerIdAssignedList);
//                                        for (String hhtUser : linePickerIdAssignedList) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//                                    if (pickupHeaderAssignedPickerIdList != null) {
//                                        log.info("pickupHeaderAssignedPickerIdList: " + pickUpheaderUserList);
//                                        for (String hhtUser : pickUpheaderUserList) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//
//                                    if (hhtUserList != null) {
//                                        log.info("hhtUserList: " + hhtUserList);
//                                        while (true) {
//                                            for (String hhtUser : hhtUserList) {
//                                                assignPickerList.add(hhtUser);
//                                                i++;
//
//                                                if (i > lineCount) {
//                                                    assignPickerListCount = assignPickerList.stream().count();
//                                                    log.info("assignPickerListCount: " + assignPickerListCount);
//                                                    break outerLoop;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }

                            outerLoop1:
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                log.info("pickUpHeader: " + pickupHeaderList);
                                if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUser);
                                    assignPickerListCount = assignPickerList.stream().count();
                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                    if (assignPickerListCount > 0) {
                                        break outerLoop1;
                                    }
                                }
                                if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    List<PickupLineV2> pickerPickupLineList = pickupLineService.getPickupLineAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("Pickupline for Current Date: " + pickerPickupLineList);
                                    if (pickerPickupLineList != null && !pickerPickupLineList.isEmpty()) {
                                        List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList);
                                        List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                        if (pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if ((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                                (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty())) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                    if (pickerPickupLineList == null || pickerPickupLineList.isEmpty()) {
                                        List<PickupHeaderV2> pickerPickupHeaderList2 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("PickupHeader for Current Date: " + pickerPickupHeaderList2);
                                        List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                        log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                        if (pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                        if ((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                                (pickerPickupHeaderList2 == null || pickerPickupHeaderList2.isEmpty() || pickerPickupHeaderList2.size() == 0)) {
                                            assignPickerList.add(hhtUser);
                                            assignPickerListCount = assignPickerList.stream().count();
                                            if (assignPickerListCount > 0) {
                                                break outerLoop1;
                                            }
                                        }
                                    }
                                }
//                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
//                                    List<PickupHeaderV2> pickerPickupHeaderList3 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
//                                    log.info("PickupHeader for Current Date: " + pickerPickupHeaderList3);
//                                    List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
//                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
//                                    if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
//                                        assignPickerList.add(hhtUser);
//                                        assignPickerListCount = assignPickerList.stream().count();
//                                        if(assignPickerListCount > 0) {
//                                            break outerLoop1;
//                                        }
//                                    }
//                                    if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty())
////                                            && (pickerPickupHeaderList3 == null || pickerPickupHeaderList3.isEmpty() || pickerPickupHeaderList3.size() == 0)
//                                    ) {
//                                        assignPickerList.add(hhtUser);
//                                        assignPickerListCount = assignPickerList.stream().count();
//                                        if (assignPickerListCount > 0) {
//                                            break outerLoop1;
//                                        }
//                                    }
//                                }
                            }
                            if (assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.
                                        getPickupHeaderAutomateCurrentDateHhtList(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                                if (pickerPickupHeaderList != null && !pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(pickerPickupHeaderList.get(0).getAssignedPickerId());
                                }
                                if (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                            }
                            if (assignPickerList != null && !assignPickerList.isEmpty()){
                                if (OB_ORD_TYP_ID == 0) {
                                    assignPickerId = assignPickerList.get(0);
                                    log.info("OB_ORD_TYP_ID 0 picker");
//                                log.info("obPicker0Position: " + obPicker0Position);
                                    obType0List.add(OB_ORD_TYP_ID);
                                }
                            if (OB_ORD_TYP_ID == 1) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("OB_ORD_TYP_ID 1 picker");
//                                log.info("obPicker1Position: " + obPicker1Position);
                                obType1List.add(OB_ORD_TYP_ID);
                            }
                            if (OB_ORD_TYP_ID == 2) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("OB_ORD_TYP_ID 2 picker");
//                                log.info("obPicker2Position: " + obPicker2Position);
                                obType2List.add(OB_ORD_TYP_ID);
                            }
                            if (OB_ORD_TYP_ID == 3) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("OB_ORD_TYP_ID 3 picker");
//                                log.info("obPicker3Position: " + obPicker3Position);
                                obType3List.add(OB_ORD_TYP_ID);
                            }
                        }
                            log.info("assignPickerId: " + assignPickerId);
                        }
                    }
                }
//            }
                if (warehouseId.equalsIgnoreCase("200")) {
                    log.info("warehouseId: " + warehouseId);

                    if (orderManagementLine.getLevelId() == null) {
                        orderManagementLine.setLevelId("1");                //HardCode default Level
                    }
                    Long LEVEL_ID = Long.valueOf(orderManagementLine.getLevelId());
                    log.info("levelId: " + LEVEL_ID);

                    if (LEVEL_ID == 0) {
                        if (levelId0List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker0Position = picker0Position + 1;
                        }
                    }
                    if (LEVEL_ID == 1) {
                        if (levelId1List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker1Position = picker1Position + 1;
                        }
                    }
                    if (LEVEL_ID == 2) {
                        if (levelId2List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker2Position = picker2Position + 1;
                        }
                    }
                    if (LEVEL_ID == 3) {
                        if (levelId3List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker3Position = picker3Position + 1;
                        }
                    }
                    if (LEVEL_ID == 4) {
                        if (levelId4List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker4Position = picker4Position + 1;
                        }
                    }

                    List<HHTUser> hhtUserIdList = preOutboundHeaderV2Repository.getHHTUserListByLevelIdNew(LEVEL_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserIdList);

                    List<String> hhtUserList = new ArrayList<>();
                    List<String> absentHhtUserList = new ArrayList<>();
                    if(hhtUserIdList != null && !hhtUserIdList.isEmpty()) {
                        for (HHTUser dbHhtUser : hhtUserIdList) {
                            if (dbHhtUser.getStartDate() != null && dbHhtUser.getEndDate() != null) {
                                List<String> userPresent = preOutboundHeaderV2Repository.getHhtUserAttendance(
                                        dbHhtUser.getCompanyCodeId(),
                                        dbHhtUser.getLanguageId(),
                                        dbHhtUser.getPlantId(),
                                        dbHhtUser.getWarehouseId(),
                                        dbHhtUser.getUserId(),
                                        dbHhtUser.getStartDate(),
                                        dbHhtUser.getEndDate());
                                log.info("HHt User Absent: " + userPresent);
                                if (userPresent != null && !userPresent.isEmpty()) {
                                    absentHhtUserList.add(dbHhtUser.getUserId());
                                } else {
                                    hhtUserList.add(dbHhtUser.getUserId());
                                }
                            } else {
                                hhtUserList.add(dbHhtUser.getUserId());
                            }
                        }
                    }
                    log.info("Present HHtUser List: " + hhtUserList);
                    log.info("Absent HHtUser List: " + absentHhtUserList);

                    if (hhtUserList != null) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);

                            List<PickupHeaderV2> pickupHeaderAssignedPickerIdList = pickupHeaderService.getPickupHeaderAutomationWithoutStatusId(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupHeaderAssignedPickerIdList != null && !pickupHeaderAssignedPickerIdList.isEmpty()) {
                                log.info("PickUpHeader Count: " + pickupHeaderAssignedPickerIdList.stream().count());
                            }

                            if (pickupHeaderAssignedPickerIdList != null) {
                                pickUpheaderUserList = pickupHeaderAssignedPickerIdList.stream().map(PickupHeaderV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                hhtUserCountInPickupHeader = pickupHeaderAssignedPickerIdList.stream().count();
                                log.info("hhtUser PickupHeader count: " + hhtUserCountInPickupHeader);
                            }

                            List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupLineList != null) {
                                log.info("PickupLineV2List Count: " + pickupLineList.stream().count());
                            }
                            if (pickupLineList != null) {
                                linePickerIdAssignedList = pickupLineList.stream().map(PickupLineV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                assignPickerIdLineCount = linePickerIdAssignedList.stream().count();
                                log.info("assignPickerIdLineCount: " + assignPickerIdLineCount);
                            }

                            assignPickerList = new ArrayList<>();

                            assignPickerListCount = assignPickerList.stream().count();

//                            outerLoop:
//                            if (lineCount != assignPickerListCount) {
//                                log.info("lineCount != assignPickerListCount :" + lineCount + ", " + assignPickerListCount);
//                                for (int i = 1; i <= lineCount; ) {
//                                    log.info("i, lineCount: " + i + ", " + lineCount);
//                                    for (String hhtUser : hhtUserList) {
//                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
//                                        log.info("pickUpHeader: " + pickupHeaderList);
//                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//                                    if (pickupLineList != null) {
//                                        log.info("pickupLineList: " + linePickerIdAssignedList);
//                                        for (String hhtUser : linePickerIdAssignedList) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//                                    if (pickupHeaderAssignedPickerIdList != null) {
//                                        log.info("pickupHeaderAssignedPickerIdList: " + pickUpheaderUserList);
//                                        for (String hhtUser : pickUpheaderUserList) {
//                                            assignPickerList.add(hhtUser);
//                                            i++;
//                                            if (i > lineCount) {
//                                                assignPickerListCount = assignPickerList.stream().count();
//                                                log.info("assignPickerListCount: " + assignPickerListCount);
//                                                break outerLoop;
//                                            }
//                                        }
//                                    }
//
//                                    if (hhtUserList != null) {
//                                        log.info("hhtUserList: " + hhtUserList);
//                                        while (true) {
//                                            for (String hhtUser : hhtUserList) {
//                                                assignPickerList.add(hhtUser);
//                                                i++;
//
//                                                if (i > lineCount) {
//                                                    assignPickerListCount = assignPickerList.stream().count();
//                                                    log.info("assignPickerListCount: " + assignPickerListCount);
//                                                    break outerLoop;
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }

                            outerLoop1:
                            for (String hhtUser : hhtUserList) {
                                List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                log.info("pickUpHeader: " + pickupHeaderList);
                                if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUser);
                                    assignPickerListCount = assignPickerList.stream().count();
                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                    if(assignPickerListCount > 0) {
                                        break outerLoop1;
                                    }
                                }
                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                    List<PickupLineV2> pickerPickupLineList = pickupLineService.getPickupLineAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("Pickupline for Current Date: " + pickerPickupLineList);
                                if(pickerPickupLineList != null && !pickerPickupLineList.isEmpty()) {
                                    List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("PickupHeader for Current Date: " + pickerPickupHeaderList);
                                    List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                    if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                        assignPickerList.add(hhtUser);
                                        assignPickerListCount = assignPickerList.stream().count();
                                        if(assignPickerListCount > 0) {
                                            break outerLoop1;
                                        }
                                    }
                                    if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                            (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty())) {
                                        assignPickerList.add(hhtUser);
                                        assignPickerListCount = assignPickerList.stream().count();
                                        if(assignPickerListCount > 0) {
                                            break outerLoop1;
                                        }
                                    }
                                  }
                                if(pickerPickupLineList == null || pickerPickupLineList.isEmpty()){
                                    List<PickupHeaderV2> pickerPickupHeaderList2 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                    log.info("PickupHeader for Current Date: " + pickerPickupHeaderList2);
                                    List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
                                    if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
                                        assignPickerList.add(hhtUser);
                                        assignPickerListCount = assignPickerList.stream().count();
                                        if(assignPickerListCount > 0) {
                                            break outerLoop1;
                                        }
                                    }
                                    if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty()) &&
                                            (pickerPickupHeaderList2 == null || pickerPickupHeaderList2.isEmpty() || pickerPickupHeaderList2.size() == 0)){
                                        assignPickerList.add(hhtUser);
                                        assignPickerListCount = assignPickerList.stream().count();
                                        if(assignPickerListCount > 0) {
                                            break outerLoop1;
                                        }
                                    }
                                }
                                }
//                                if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
//                                    List<PickupHeaderV2> pickerPickupHeaderList3 = pickupHeaderService.getPickupHeaderAutomateCurrentDate(companyCodeId, plantId, languageId, warehouseId, hhtUser);
//                                    log.info("PickupHeader for Current Date: " + pickerPickupHeaderList3);
//                                    List<PickupHeaderV2> pickerPickupHeaderList1 = pickupHeaderService.getPickupHeaderAutomateCurrentDateSameOrder(companyCodeId, plantId, languageId, warehouseId, hhtUser, refDocNumber);
//                                    log.info("PickupHeader for Current Date same Order: " + pickerPickupHeaderList1);
//                                    if(pickerPickupHeaderList1 != null && !pickerPickupHeaderList1.isEmpty()) {
//                                        assignPickerList.add(hhtUser);
//                                        assignPickerListCount = assignPickerList.stream().count();
//                                        if(assignPickerListCount > 0) {
//                                            break outerLoop1;
//                                        }
//                                    }
//                                    if((pickerPickupHeaderList1 == null || pickerPickupHeaderList1.isEmpty())
////                                            && (pickerPickupHeaderList3 == null || pickerPickupHeaderList3.isEmpty() || pickerPickupHeaderList3.size() == 0)
//                                    ) {
//                                        assignPickerList.add(hhtUser);
//                                        assignPickerListCount = assignPickerList.stream().count();
//                                        if (assignPickerListCount > 0) {
//                                            break outerLoop1;
//                                        }
//                                    }
//                                }
                            }
                            if(assignPickerList == null || assignPickerList.isEmpty() || assignPickerList.size() == 0) {
                                List<PickupHeaderV2> pickerPickupHeaderList = pickupHeaderService.
                                        getPickupHeaderAutomateCurrentDateHhtList(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                                if(pickerPickupHeaderList != null && !pickerPickupHeaderList.isEmpty()){
                                    assignPickerList.add(pickerPickupHeaderList.get(0).getAssignedPickerId());
                                }
                                if (pickerPickupHeaderList == null || pickerPickupHeaderList.isEmpty()) {
                                    assignPickerList.add(hhtUserList.get(0));
                                }
                            }

                        if(assignPickerList != null && !assignPickerList.isEmpty()) {
                            if (LEVEL_ID == 0) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("Level 0 picker");
//                                log.info("picker0Position: " + picker0Position);
                                levelId0List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 1) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("Level 1 picker");
//                                log.info("picker1Position: " + picker1Position);
                                levelId1List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 2) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("Level 2 picker");
//                                log.info("picker2Position: " + picker2Position);
                                levelId2List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 3) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("Level 3 picker");
//                                log.info("picker3Position: " + picker3Position);
                                levelId3List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 4) {
                                assignPickerId = assignPickerList.get(0);
                                log.info("Level 4 picker");
//                                log.info("picker4Position: " + picker4Position);
                                levelId4List.add(LEVEL_ID);
                            }
                        }
                            log.info("assignPickerId: " + assignPickerId);

                        }
                    }
                }

                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                long NUM_RAN_CODE = 10;
                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                log.info("PU_NO : " + PU_NO);

                newPickupHeader.setAssignedPickerId(assignPickerId);
                newPickupHeader.setPickupNumber(PU_NO);

                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());

                // PICK_UOM
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                // REF_FIELD_1
                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());

                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                newPickupHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                newPickupHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                newPickupHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());

                newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
                newPickupHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
                newPickupHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
                newPickupHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

                PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLine.setPickupNumber(PU_NO);
                orderManagementLine.setAssignedPickerId(assignPickerId);
                orderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                orderManagementLine.setStatusDescription(statusDescription);
//                    orderManagementLine.setPickupUpdatedBy("Automate");            // Ref_field_7
                orderManagementLine.setPickupUpdatedOn(new Date());
                OrderManagementLineV2 orderManagementLineV2 = orderManagementLineV2Repository.save(orderManagementLine);
                log.info("OrderManagementLine updated : " + orderManagementLineV2);
//                    }

                /*
                 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                 * OUTBOUNDLINE table and update SATATU_ID as 48
                 */
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode,
                        orderManagementLine.getLineNumber(),
                        orderManagementLine.getItemCode());
                outboundLine.setStatusId(48L);
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);
            }
        }

        //push notification separated from pickup header and consolidated notification sent
        orderManagementLineService.sendPushNotification(preOutboundNo, warehouseId);

        // OutboundHeader Update
        OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode);
        outboundHeader.setStatusId(48L);
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeaderV2Repository.save(outboundHeader);
        log.info("outboundHeader updated : " + outboundHeader);

        // ORDERMANAGEMENTHEADER Update
        OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                .getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        orderManagementHeader.setStatusId(48L);
        orderManagementHeader.setStatusDescription(statusDescription);
        orderManagementHeaderV2Repository.save(orderManagementHeader);
        log.info("orderManagementHeader updated : " + orderManagementHeader);
    }

    /**
     * old Logic - each picker for each item
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param outboundIntegrationHeader
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    private void updateStatusAs48ForPickupHeaderCreateSuccess(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              OutboundIntegrationHeaderV2 outboundIntegrationHeader,
                                                              String preOutboundNo, String refDocNumber, String partnerCode) throws InvocationTargetException, IllegalAccessException, ParseException, FirebaseMessagingException {

        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineService.
                getOrderManagementLineForPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

        //pickup header create
        Long assignPickerIdLineCount = 0L;
        Long assignPickerListCount = 0L;
//        String assignPickerId = null;
        Long hhtUserCount = 0L;
        Long hhtUserCountInPickupHeader = 0L;
        Long lineCount = 0L;
        Integer pickerPosition = 0;
        Integer picker0Position = 0;
        Integer picker1Position = 0;
        Integer picker2Position = 0;
        Integer picker3Position = 0;
        Integer picker4Position = 0;

        Integer obPicker0Position = 0;
        Integer obPicker1Position = 0;
        Integer obPicker2Position = 0;
        Integer obPicker3Position = 0;


        lineCount = orderManagementLineV2List.stream().count();
        log.info("total Order Lines Count: " + lineCount);

        List<String> assignPickerList = new ArrayList<>();
        List<String> pickUpheaderUserList = new ArrayList<>();

        List<String> assignedPickerIdList = new ArrayList<>();
        List<String> linePickerIdAssignedList = new ArrayList<>();
        List<Long> levelId0List = new ArrayList<>();
        List<Long> levelId1List = new ArrayList<>();
        List<Long> levelId2List = new ArrayList<>();
        List<Long> levelId3List = new ArrayList<>();
        List<Long> levelId4List = new ArrayList<>();

        List<Long> obType0List = new ArrayList<>();
        List<Long> obType1List = new ArrayList<>();
        List<Long> obType2List = new ArrayList<>();
        List<Long> obType3List = new ArrayList<>();
        for (OrderManagementLineV2 orderManagementLine : orderManagementLineV2List) {
            String assignPickerId = null;

            if (orderManagementLine != null && orderManagementLine.getStatusId() != 47L) {
                log.info("orderManagementLine: " + orderManagementLine);

                int currentTime = DateUtils.getCurrentTime();
                log.info("CurrentTime: " + currentTime);

//                if (currentTime < 15) {

                if (warehouseId.equalsIgnoreCase("100")) {
                    log.info("warehouseId: " + warehouseId);

                    Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
                    log.info("OutboundOrderTypeId: " + OB_ORD_TYP_ID);

                    if (OB_ORD_TYP_ID == 0) {
                        if (obType0List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker0Position = obPicker0Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 1) {
                        if (obType1List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker1Position = obPicker1Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 2) {
                        if (obType2List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker2Position = obPicker2Position + 1;
                        }
                    }
                    if (OB_ORD_TYP_ID == 3) {
                        if (obType3List.stream().anyMatch(a -> a.equals(OB_ORD_TYP_ID))) {
                            obPicker3Position = obPicker3Position + 1;
                        }
                    }


                    List<String> hhtUserList = preOutboundHeaderV2Repository.getHHTUserPresentList(OB_ORD_TYP_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserList);

                    if (hhtUserList != null) {
                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);
                    }
                    if (hhtUserList != null) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);
                            List<PickupHeaderV2> pickupHeaderAssignedPickerIdList = pickupHeaderService.getPickupHeaderAutomationWithoutStatusId(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupHeaderAssignedPickerIdList != null && !pickupHeaderAssignedPickerIdList.isEmpty()) {
                                log.info("PickUpHeaderList Count: " + pickupHeaderAssignedPickerIdList.stream().count());
                            }

                            if (pickupHeaderAssignedPickerIdList != null) {
                                pickUpheaderUserList = pickupHeaderAssignedPickerIdList.stream().map(PickupHeaderV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                hhtUserCountInPickupHeader = pickupHeaderAssignedPickerIdList.stream().count();
                                log.info("hhtUser PickupHeader count: " + hhtUserCountInPickupHeader);
                            }

                            List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupLineList != null) {
                                log.info("PickupLineV2List count: " + pickupLineList.stream().count());
                            }
                            if (pickupLineList != null) {
                                linePickerIdAssignedList = pickupLineList.stream().map(PickupLineV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                assignPickerIdLineCount = linePickerIdAssignedList.stream().count();
                                log.info("assignPickerIdLineCount: " + assignPickerIdLineCount);
                            }

                            assignPickerList = new ArrayList<>();

                            assignPickerListCount = assignPickerList.stream().count();

                            outerLoop:
                            if (!lineCount.equals(assignPickerListCount)) {
                                log.info("lineCount != assignPickerListCount :" + lineCount + ", " + assignPickerListCount);
                                for (int i = 1; i <= lineCount; ) {
                                    log.info("i, lineCount: " + i + ", " + lineCount);
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("pickUpHeader: " + pickupHeaderList);
                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }
                                    if (pickupLineList != null) {
                                        log.info("pickupLineList: " + linePickerIdAssignedList);
                                        for (String hhtUser : linePickerIdAssignedList) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }
                                    if (pickupHeaderAssignedPickerIdList != null) {
                                        log.info("pickupHeaderAssignedPickerIdList: " + pickUpheaderUserList);
                                        for (String hhtUser : pickUpheaderUserList) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }

                                    if (hhtUserList != null) {
                                        log.info("hhtUserList: " + hhtUserList);
                                        while (true) {
                                            for (String hhtUser : hhtUserList) {
                                                assignPickerList.add(hhtUser);
                                                i++;

                                                if (i > lineCount) {
                                                    assignPickerListCount = assignPickerList.stream().count();
                                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (OB_ORD_TYP_ID == 0) {
                                assignPickerId = assignPickerList.get(obPicker0Position);
                                log.info("OB_ORD_TYP_ID 0 picker");
                                log.info("obPicker0Position: " + obPicker0Position);
                                obType0List.add(OB_ORD_TYP_ID);
                            }
                            if (OB_ORD_TYP_ID == 1) {
                                assignPickerId = assignPickerList.get(obPicker1Position);
                                log.info("OB_ORD_TYP_ID 1 picker");
                                log.info("obPicker1Position: " + obPicker1Position);
                                obType1List.add(OB_ORD_TYP_ID);
                            }
                            if (OB_ORD_TYP_ID == 2) {
                                assignPickerId = assignPickerList.get(obPicker2Position);
                                log.info("OB_ORD_TYP_ID 2 picker");
                                log.info("obPicker2Position: " + obPicker2Position);
                                obType2List.add(OB_ORD_TYP_ID);
                            }
                            if (OB_ORD_TYP_ID == 3) {
                                assignPickerId = assignPickerList.get(obPicker3Position);
                                log.info("OB_ORD_TYP_ID 3 picker");
                                log.info("obPicker3Position: " + obPicker3Position);
                                obType3List.add(OB_ORD_TYP_ID);
                            }

                            log.info("assignPickerId: " + assignPickerId);
                        }
                    }
                }
//            }
                if (warehouseId.equalsIgnoreCase("200")) {
                    log.info("warehouseId: " + warehouseId);

                    if (orderManagementLine.getLevelId() == null) {
                        orderManagementLine.setLevelId("1");                //HardCode default Level
                    }
                    Long LEVEL_ID = Long.valueOf(orderManagementLine.getLevelId());
                    log.info("levelId: " + LEVEL_ID);

                    if (LEVEL_ID == 0) {
                        if (levelId0List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker0Position = picker0Position + 1;
                        }
                    }
                    if (LEVEL_ID == 1) {
                        if (levelId1List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker1Position = picker1Position + 1;
                        }
                    }
                    if (LEVEL_ID == 2) {
                        if (levelId2List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker2Position = picker2Position + 1;
                        }
                    }
                    if (LEVEL_ID == 3) {
                        if (levelId3List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker3Position = picker3Position + 1;
                        }
                    }
                    if (LEVEL_ID == 4) {
                        if (levelId4List.stream().anyMatch(a -> a.equals(LEVEL_ID))) {
                            picker4Position = picker4Position + 1;
                        }
                    }

                    List<String> hhtUserList = preOutboundHeaderV2Repository.getHHTUserByLevelIdPresentList(LEVEL_ID, companyCodeId, plantId, languageId, warehouseId);
                    log.info("hhtUserList: " + hhtUserList);

                    if (hhtUserList != null) {

                        hhtUserCount = hhtUserList.stream().count();
                        log.info("hhtUserList count: " + hhtUserCount);

                        PickupHeaderV2 assignPickerPickUpHeader = pickupHeaderService.getPickupHeaderAutomation(companyCodeId, plantId, languageId, warehouseId,
                                orderManagementLine.getItemCode(), orderManagementLine.getManufacturerName());
                        log.info("pickupHeader--> Status48---> assignPicker---> SameItem: " + assignPickerPickUpHeader);
                        if (assignPickerPickUpHeader != null) {
                            log.info("Picker Assigned: " + assignPickerPickUpHeader.getAssignedPickerId());
                            assignPickerId = assignPickerPickUpHeader.getAssignedPickerId();
                        }

                        if (assignPickerId == null) {
                            log.info("assignPickerId: " + assignPickerId);

                            List<PickupHeaderV2> pickupHeaderAssignedPickerIdList = pickupHeaderService.getPickupHeaderAutomationWithoutStatusId(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupHeaderAssignedPickerIdList != null && !pickupHeaderAssignedPickerIdList.isEmpty()) {
                                log.info("PickUpHeader Count: " + pickupHeaderAssignedPickerIdList.stream().count());
                            }

                            if (pickupHeaderAssignedPickerIdList != null) {
                                pickUpheaderUserList = pickupHeaderAssignedPickerIdList.stream().map(PickupHeaderV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                hhtUserCountInPickupHeader = pickupHeaderAssignedPickerIdList.stream().count();
                                log.info("hhtUser PickupHeader count: " + hhtUserCountInPickupHeader);
                            }

                            List<PickupLineV2> pickupLineList = pickupLineService.getPickupLineAutomation(companyCodeId, plantId, languageId, warehouseId, hhtUserList);
                            if(pickupLineList != null) {
                                log.info("PickupLineV2List Count: " + pickupLineList.stream().count());
                            }
                            if (pickupLineList != null) {
                                linePickerIdAssignedList = pickupLineList.stream().map(PickupLineV2::getAssignedPickerId).distinct().collect(Collectors.toList());
                                assignPickerIdLineCount = linePickerIdAssignedList.stream().count();
                                log.info("assignPickerIdLineCount: " + assignPickerIdLineCount);
                            }

                            assignPickerList = new ArrayList<>();

                            assignPickerListCount = assignPickerList.stream().count();

                            outerLoop:
                            if (!lineCount.equals(assignPickerListCount)) {
                                log.info("lineCount != assignPickerListCount :" + lineCount + ", " + assignPickerListCount);
                                for (int i = 1; i <= lineCount; ) {
                                    log.info("i, lineCount: " + i + ", " + lineCount);
                                    for (String hhtUser : hhtUserList) {
                                        List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.getPickupHeaderAutomationWithOutStatusIdV2(companyCodeId, plantId, languageId, warehouseId, hhtUser);
                                        log.info("pickUpHeader: " + pickupHeaderList);
                                        if (pickupHeaderList == null || pickupHeaderList.isEmpty()) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }
                                    if (pickupLineList != null) {
                                        log.info("pickupLineList: " + linePickerIdAssignedList);
                                        for (String hhtUser : linePickerIdAssignedList) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }
                                    if (pickupHeaderAssignedPickerIdList != null) {
                                        log.info("pickupHeaderAssignedPickerIdList: " + pickUpheaderUserList);
                                        for (String hhtUser : pickUpheaderUserList) {
                                            assignPickerList.add(hhtUser);
                                            i++;
                                            if (i > lineCount) {
                                                assignPickerListCount = assignPickerList.stream().count();
                                                log.info("assignPickerListCount: " + assignPickerListCount);
                                                break outerLoop;
                                            }
                                        }
                                    }

                                    if (hhtUserList != null) {
                                        log.info("hhtUserList: " + hhtUserList);
                                        while (true) {
                                            for (String hhtUser : hhtUserList) {
                                                assignPickerList.add(hhtUser);
                                                i++;

                                                if (i > lineCount) {
                                                    assignPickerListCount = assignPickerList.stream().count();
                                                    log.info("assignPickerListCount: " + assignPickerListCount);
                                                    break outerLoop;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (LEVEL_ID == 0) {
                                assignPickerId = assignPickerList.get(picker0Position);
                                log.info("Level 0 picker");
                                log.info("picker0Position: " + picker0Position);
                                levelId0List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 1) {
                                assignPickerId = assignPickerList.get(picker1Position);
                                log.info("Level 1 picker");
                                log.info("picker1Position: " + picker1Position);
                                levelId1List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 2) {
                                assignPickerId = assignPickerList.get(picker2Position);
                                log.info("Level 2 picker");
                                log.info("picker2Position: " + picker2Position);
                                levelId2List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 3) {
                                assignPickerId = assignPickerList.get(picker3Position);
                                log.info("Level 3 picker");
                                log.info("picker3Position: " + picker3Position);
                                levelId3List.add(LEVEL_ID);
                            }
                            if (LEVEL_ID == 4) {
                                assignPickerId = assignPickerList.get(picker4Position);
                                log.info("Level 4 picker");
                                log.info("picker4Position: " + picker4Position);
                                levelId4List.add(LEVEL_ID);
                            }

                            log.info("assignPickerId: " + assignPickerId);

                        }
                    }
                }

                PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));
                long NUM_RAN_CODE = 10;
                String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                log.info("PU_NO : " + PU_NO);

                newPickupHeader.setAssignedPickerId(assignPickerId);
                newPickupHeader.setPickupNumber(PU_NO);

                newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());

                // PICK_UOM
                newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

                // STATUS_ID
                newPickupHeader.setStatusId(48L);
                statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                newPickupHeader.setStatusDescription(statusDescription);

                // ProposedPackbarcode
                newPickupHeader.setProposedPackBarCode(orderManagementLine.getProposedPackBarCode());

                // REF_FIELD_1
                newPickupHeader.setReferenceField1(orderManagementLine.getReferenceField1());

                newPickupHeader.setManufacturerCode(orderManagementLine.getManufacturerCode());
                newPickupHeader.setManufacturerName(orderManagementLine.getManufacturerName());
                newPickupHeader.setManufacturerPartNo(orderManagementLine.getManufacturerPartNo());
                newPickupHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                newPickupHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                newPickupHeader.setSalesInvoiceNumber(orderManagementLine.getSalesInvoiceNumber());
                newPickupHeader.setOutboundOrderTypeId(orderManagementLine.getOutboundOrderTypeId());
                newPickupHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
                newPickupHeader.setSupplierInvoiceNo(orderManagementLine.getSupplierInvoiceNo());
                newPickupHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
                newPickupHeader.setLevelId(orderManagementLine.getLevelId());
                newPickupHeader.setTargetBranchCode(orderManagementLine.getTargetBranchCode());
                newPickupHeader.setLineNumber(orderManagementLine.getLineNumber());

                newPickupHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
                newPickupHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
                newPickupHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
                newPickupHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

                PickupHeaderV2 createdPickupHeader = pickupHeaderService.createOutboundOrderProcessingPickupHeaderV2(newPickupHeader, orderManagementLine.getPickupCreatedBy());
                log.info("pickupHeader created: " + createdPickupHeader);

                orderManagementLine.setPickupNumber(PU_NO);
                orderManagementLine.setAssignedPickerId(assignPickerId);
                orderManagementLine.setStatusId(48L);                        // 2. Update STATUS_ID = 48
                orderManagementLine.setStatusDescription(statusDescription);
//                    orderManagementLine.setPickupUpdatedBy("Automate");            // Ref_field_7
                orderManagementLine.setPickupUpdatedOn(new Date());
                OrderManagementLineV2 orderManagementLineV2 = orderManagementLineV2Repository.save(orderManagementLine);
                log.info("OrderManagementLine updated : " + orderManagementLineV2);
//                    }

                /*
                 * Update ORDERMANAGEMENTHEADER --------------------------------- Pass the
                 * Selected WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE in
                 * OUTBOUNDLINE table and update SATATU_ID as 48
                 */
                OutboundLineV2 outboundLine = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode,
                        orderManagementLine.getLineNumber(),
                        orderManagementLine.getItemCode());
                outboundLine.setStatusId(48L);
                outboundLine.setStatusDescription(statusDescription);
                outboundLine = outboundLineV2Repository.save(outboundLine);
                log.info("outboundLine updated : " + outboundLine);
            }
        }

        //push notification separated from pickup header and consolidated notification sent
        orderManagementLineService.sendPushNotification(preOutboundNo, warehouseId);

        // OutboundHeader Update
        OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode);
        outboundHeader.setStatusId(48L);
        outboundHeader.setStatusDescription(statusDescription);
        outboundHeaderV2Repository.save(outboundHeader);
        log.info("outboundHeader updated : " + outboundHeader);

        // ORDERMANAGEMENTHEADER Update
        OrderManagementHeaderV2 orderManagementHeader = orderManagementHeaderService
                .getOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        orderManagementHeader.setStatusId(48L);
        orderManagementHeader.setStatusDescription(statusDescription);
        orderManagementHeaderV2Repository.save(orderManagementHeader);
        log.info("orderManagementHeader updated : " + orderManagementHeader);
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     */
    private void updateStatusAs47ForOBHeaderV2(String companyCodeId, String plantId,
                                               String languageId, String warehouseId, String preOutboundNo, String refDocNumber) {
        List<OutboundLineV2> outboundLineList = outboundLineService.getOutboundLineV2(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);

        long matchedCount = 0L;
        boolean isConditionMet = false;
        if (outboundLineList != null) {
            matchedCount = outboundLineList.stream().filter(a -> a.getStatusId() == 47L).count();
            isConditionMet = (matchedCount == outboundLineList.size());
        }
        Long STATUS_ID_47 = 47L;
        if (isConditionMet) {
            //----------------Outbound Header update----------------------------------------------------------------------------------------
            outboundHeaderV2Repository.updateOutboundHeaderStatusAs47(companyCodeId, plantId, languageId, warehouseId, refDocNumber, STATUS_ID_47);
            log.info("OutboundHeader status updated as 47. ");

            //----------------Preoutbound Header--------------------------------------------------------------------------------------------
//            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
//            StatusId idStatus = idmasterService.getStatus(STATUS_ID_47, warehouseId, authTokenForIDService.getAccess_token());
            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID_47, languageId);
            preOutboundHeaderV2Repository.updatePreOutboundHeaderStatus(languageId, companyCodeId, plantId, warehouseId, refDocNumber, STATUS_ID_47, statusDescription);
            log.info("PreOutbound Header status updated as 47.");
        }
    }


    /**
     * @return
     */
    private String getPreOutboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = getNextRangeNumber(9L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param createdPreOutboundLine
     * @return
     */
    private void createOutboundLineV2(List<PreOutboundLineV2> createdPreOutboundLine, OutboundIntegrationHeaderV2 outboundIntegrationHeader) {
        log.info("Outbound Process OutboundLine Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // Create a thread pool with a fixed number of threads
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<OutboundLineV2> outboundLines = new ArrayList<>();
        createdPreOutboundLine.stream().forEach(preOutboundLine -> {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

//        for (PreOutboundLineV2 preOutboundLine : createdPreOutboundLine) {
            List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.getOrderManagementLineV2(
                    preOutboundLine.getCompanyCodeId(),
                    preOutboundLine.getPlantId(),
                    preOutboundLine.getLanguageId(),
                    preOutboundLine.getWarehouseId(),
                    preOutboundLine.getPreOutboundNo(),
                    preOutboundLine.getLineNumber(),
                    preOutboundLine.getItemCode());
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                OutboundLineV2 outboundLine = new OutboundLineV2();
                BeanUtils.copyProperties(preOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(preOutboundLine));
                outboundLine.setDeliveryQty(0D);
                outboundLine.setStatusId(dbOrderManagementLine.getStatusId());
                outboundLine.setCreatedBy(preOutboundLine.getCreatedBy());
                outboundLine.setCreatedOn(preOutboundLine.getCreatedOn());

                IKeyValuePair description = stagingLineV2Repository.getDescription(preOutboundLine.getCompanyCodeId(),
                        preOutboundLine.getLanguageId(),
                        preOutboundLine.getPlantId(),
                        preOutboundLine.getWarehouseId());

                if (preOutboundLine.getStatusId() != null) {
                    statusDescription = stagingLineV2Repository.getStatusDescription(dbOrderManagementLine.getStatusId(), dbOrderManagementLine.getLanguageId());
                    outboundLine.setStatusDescription(statusDescription);
                }
                outboundLine.setCompanyDescription(description.getCompanyDesc());
                outboundLine.setPlantDescription(description.getPlantDesc());
                outboundLine.setWarehouseDescription(description.getWarehouseDesc());

                outboundLine.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
                outboundLine.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
                outboundLine.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
                outboundLine.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
                outboundLine.setSupplierInvoiceNo(preOutboundLine.getSupplierInvoiceNo());
                outboundLine.setTokenNumber(preOutboundLine.getTokenNumber());

                outboundLine.setMiddlewareId(preOutboundLine.getMiddlewareId());
                outboundLine.setMiddlewareHeaderId(preOutboundLine.getMiddlewareHeaderId());
                outboundLine.setMiddlewareTable(preOutboundLine.getMiddlewareTable());
                outboundLine.setSalesInvoiceNumber(preOutboundLine.getSalesInvoiceNumber());
                outboundLine.setManufacturerFullName(preOutboundLine.getManufacturerFullName());
                outboundLine.setManufacturerName(preOutboundLine.getManufacturerName());
                outboundLine.setOutboundOrderTypeId(preOutboundLine.getOutboundOrderTypeId());
                outboundLine.setReferenceDocumentType(preOutboundLine.getReferenceDocumentType());
                outboundLine.setTargetBranchCode(preOutboundLine.getTargetBranchCode());
                outboundLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                if(outboundLine.getOutboundOrderTypeId() == 3L) {
                    outboundLine.setCustomerType("INVOICE");
                }
                if(outboundLine.getOutboundOrderTypeId() == 0L || outboundLine.getOutboundOrderTypeId() == 1L){
                    outboundLine.setCustomerType("TRANSVERSE");
                }

                outboundLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
                outboundLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
                outboundLine.setIsCompleted(preOutboundLine.getIsCompleted());
                outboundLine.setIsCancelled(preOutboundLine.getIsCancelled());
                outboundLines.add(outboundLine);
//                OutboundLineV2 createdOutboundLine = outboundLineV2Repository.save(outboundLine);
//                log.info("outboundLines created -----2------>: " + createdOutboundLine);
            }
//        }
            }, executor);
            futures.add(future);
        });

        // Wait for all the futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Shut down the executor service
        executor.shutdown();

        outboundLineV2Repository.saveAll(outboundLines);
        log.info("Outbound Process OutboundLine Finished----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        log.info("outboundLines created -----2------>: " + outboundLines);
//        return outboundLines;
    }

    /**
     * @param createdPreOutboundHeader
     * @param statusId
     * @return
     * @throws ParseException
     */
    private OutboundHeaderV2 createOutboundHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader, Long statusId, OutboundIntegrationHeaderV2 outboundIntegrationHeader) throws ParseException {
        log.info("Outbound Process Outbound Header Initiated----> " + createdPreOutboundHeader.getRefDocNumber() + " - " + createdPreOutboundHeader.getOutboundOrderTypeId() + " - " + new Date());
        OutboundHeaderV2 outboundHeader = new OutboundHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, outboundHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));
//        OrderManagementHeaderV2 dbOrderManagementHeader = orderManagementHeaderService.getOrderManagementHeaderV2(createdPreOutboundHeader.getPreOutboundNo());

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        outboundHeader.setRefDocDate(new Date());
        outboundHeader.setStatusId(statusId);

        statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdPreOutboundHeader.getLanguageId());

        outboundHeader.setStatusDescription(statusDescription);

        outboundHeader.setCompanyDescription(createdPreOutboundHeader.getCompanyDescription());
        outboundHeader.setPlantDescription(createdPreOutboundHeader.getPlantDescription());
        outboundHeader.setWarehouseDescription(createdPreOutboundHeader.getWarehouseDescription());
        outboundHeader.setMiddlewareId(createdPreOutboundHeader.getMiddlewareId());
        outboundHeader.setMiddlewareTable(createdPreOutboundHeader.getMiddlewareTable());
        outboundHeader.setReferenceDocumentType(createdPreOutboundHeader.getReferenceDocumentType());

        outboundHeader.setInvoiceDate(outboundIntegrationHeader.getRequiredDeliveryDate());
        outboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
        outboundHeader.setSalesInvoiceNumber(outboundIntegrationHeader.getSalesInvoiceNumber());
        outboundHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
        outboundHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        outboundHeader.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        outboundHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
        outboundHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
        outboundHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
        outboundHeader.setMUpdatedOn(outboundHeader.getMUpdatedOn());

        if(outboundHeader.getOutboundOrderTypeId() == 3L) {
            outboundHeader.setCustomerType("INVOICE");
        }
        if(outboundHeader.getOutboundOrderTypeId() == 0L || outboundHeader.getOutboundOrderTypeId() == 1L){
            outboundHeader.setCustomerType("TRANSVERSE");
        }

        outboundHeader.setCreatedBy(createdPreOutboundHeader.getCreatedBy());
        outboundHeader.setCreatedOn(createdPreOutboundHeader.getCreatedOn());
        outboundHeader = outboundHeaderV2Repository.save(outboundHeader);
        log.info("Created outboundHeader : " + outboundHeader + "---------------> " + new Date());
        return outboundHeader;
    }


    /**
     * @param createdPreOutboundHeader
     * @return
     */
    private OrderManagementHeaderV2 createOrderManagementHeaderV2(PreOutboundHeaderV2 createdPreOutboundHeader) {
        log.info("Outbound Process OrderManagement Header Initiated----> " + createdPreOutboundHeader.getRefDocNumber() + " - " + createdPreOutboundHeader.getOutboundOrderTypeId() + " - " + new Date());
        OrderManagementHeaderV2 newOrderManagementHeader = new OrderManagementHeaderV2();
        BeanUtils.copyProperties(createdPreOutboundHeader, newOrderManagementHeader, CommonUtils.getNullPropertyNames(createdPreOutboundHeader));

//        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
//        StatusId idStatus = idmasterService.getStatus(41L, createdPreOutboundHeader.getWarehouseId(), authTokenForIDService.getAccess_token());
        newOrderManagementHeader.setStatusId(41L);    // Hard Coded Value "41"

        statusDescription = stagingLineV2Repository.getStatusDescription(41L, createdPreOutboundHeader.getLanguageId());

        newOrderManagementHeader.setStatusDescription(statusDescription);
        newOrderManagementHeader.setCompanyDescription(createdPreOutboundHeader.getCompanyDescription());
        newOrderManagementHeader.setPlantDescription(createdPreOutboundHeader.getPlantDescription());
        newOrderManagementHeader.setWarehouseDescription(createdPreOutboundHeader.getWarehouseDescription());
        newOrderManagementHeader.setMiddlewareId(createdPreOutboundHeader.getMiddlewareId());
        newOrderManagementHeader.setMiddlewareTable(createdPreOutboundHeader.getMiddlewareTable());
        newOrderManagementHeader.setReferenceDocumentType(createdPreOutboundHeader.getReferenceDocumentType());
        newOrderManagementHeader.setSalesOrderNumber(createdPreOutboundHeader.getSalesOrderNumber());
        newOrderManagementHeader.setPickListNumber(createdPreOutboundHeader.getPickListNumber());
        newOrderManagementHeader.setTokenNumber(createdPreOutboundHeader.getTokenNumber());
        newOrderManagementHeader.setTargetBranchCode(createdPreOutboundHeader.getTargetBranchCode());


        newOrderManagementHeader.setFromBranchCode(createdPreOutboundHeader.getFromBranchCode());
        newOrderManagementHeader.setIsCompleted(createdPreOutboundHeader.getIsCompleted());
        newOrderManagementHeader.setIsCancelled(createdPreOutboundHeader.getIsCancelled());
        newOrderManagementHeader.setMUpdatedOn(createdPreOutboundHeader.getMUpdatedOn());

        newOrderManagementHeader.setReferenceField7(statusDescription);
        OrderManagementHeaderV2 createdOrderMangementHeader = orderManagementHeaderV2Repository.save(newOrderManagementHeader);
        log.info("Outbound Process OrderManagement Header Finished----> " + createdOrderMangementHeader.getRefDocNumber() + " - " + createdOrderMangementHeader.getOutboundOrderTypeId() + " - " + new Date());
        return createdOrderMangementHeader;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param refField1ForOrderType
     * @return
     * @throws ParseException
     */
    private PreOutboundHeaderV2 createPreOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                          OutboundIntegrationHeaderV2 outboundIntegrationHeader, String refField1ForOrderType) throws ParseException {
        log.info("Outbound Process PreOutboundHeader Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
//        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        PreOutboundHeaderV2 preOutboundHeader = new PreOutboundHeaderV2();
        preOutboundHeader.setLanguageId(languageId);
        preOutboundHeader.setCompanyCodeId(companyCodeId);
        preOutboundHeader.setPlantId(plantId);
        preOutboundHeader.setWarehouseId(warehouseId);
        preOutboundHeader.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());
        preOutboundHeader.setPreOutboundNo(preOutboundNo);                                                // PRE_OB_NO
        preOutboundHeader.setPartnerCode(outboundIntegrationHeader.getPartnerCode());
        preOutboundHeader.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());    // Hardcoded value "0"
//        preOutboundHeader.setReferenceDocumentType("SO");                                                // Hardcoded value "SO"

        /*
         * Setting up KuwaitTime
         */
//        Date kwtDate = DateUtils.getCurrentKWTDateTime();
        preOutboundHeader.setRefDocDate(new Date());
        preOutboundHeader.setStatusId(39L);
        preOutboundHeader.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundHeader.setReferenceField1(refField1ForOrderType);

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCodeId,
                languageId,
                plantId,
                warehouseId);

        preOutboundHeader.setCompanyDescription(description.getCompanyDesc());
        preOutboundHeader.setPlantDescription(description.getPlantDesc());
        preOutboundHeader.setWarehouseDescription(description.getWarehouseDesc());

        preOutboundHeader.setMiddlewareId(outboundIntegrationHeader.getMiddlewareId());
        preOutboundHeader.setMiddlewareTable(outboundIntegrationHeader.getMiddlewareTable());
        preOutboundHeader.setReferenceDocumentType(outboundIntegrationHeader.getReferenceDocumentType());
        preOutboundHeader.setSalesOrderNumber(outboundIntegrationHeader.getSalesOrderNumber());
        preOutboundHeader.setPickListNumber(outboundIntegrationHeader.getPickListNumber());
        preOutboundHeader.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        preOutboundHeader.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        preOutboundHeader.setFromBranchCode(outboundIntegrationHeader.getFromBranchCode());
        preOutboundHeader.setIsCompleted(outboundIntegrationHeader.getIsCompleted());
        preOutboundHeader.setIsCancelled(outboundIntegrationHeader.getIsCancelled());
        preOutboundHeader.setMUpdatedOn(outboundIntegrationHeader.getMUpdatedOn());

        // Status Description
//		StatusId idStatus = idmasterService.getStatus(39L, outboundIntegrationHeader.getWarehouseID(), authTokenForIDService.getAccess_token());
        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        log.info("PreOutboundHeader StatusDescription: " + statusDescription);
        // REF_FIELD_10
        preOutboundHeader.setReferenceField10(statusDescription);

        preOutboundHeader.setStatusDescription(statusDescription);

        preOutboundHeader.setDeletionIndicator(0L);
        preOutboundHeader.setCreatedBy("MW_AMS");
        preOutboundHeader.setCreatedOn(new Date());
        PreOutboundHeaderV2 createdPreOutboundHeader = preOutboundHeaderV2Repository.save(preOutboundHeader);
        log.info("Finished createdPreOutboundHeader : " + createdPreOutboundHeader + " ---------> " + new Date());
        return createdPreOutboundHeader;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param dbBomLine
     * @param outboundIntegrationLine
     * @return
     */
    private PreOutboundLineV2 createPreOutboundLineBOMBasedV2(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                              OutboundIntegrationHeaderV2 outboundIntegrationHeader, BomLine dbBomLine,
                                                              OutboundIntegrationLineV2 outboundIntegrationLine) {
//        Warehouse warehouse = getWarehouse(outboundIntegrationHeader.getWarehouseID());
        log.info("Outbound Process BOM PreOutboundLine Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();
        preOutboundLine.setLanguageId(languageId);
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);

        // WH_ID
        preOutboundLine.setWarehouseId(outboundIntegrationHeader.getWarehouseID());

        // PRE_IB_NO
        preOutboundLine.setPreOutboundNo(preOutboundNo);

        // REF_DOC_NO
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

        // OB_ORD_TYP_ID
        preOutboundLine.setOutboundOrderTypeId(Long.valueOf(outboundIntegrationHeader.getOutboundOrderTypeID()));

        // IB__LINE_NO
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

        // ITM_CODE
        preOutboundLine.setItemCode(dbBomLine.getChildItemCode());
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        //HAREESH 27-08-2022 - bom line creation get item description based on child item code change

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(companyCodeId);
        imBasicData.setPlantId(plantId);
        imBasicData.setLanguageId(languageId);
        imBasicData.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
        imBasicData.setItemCode(dbBomLine.getChildItemCode());
        imBasicData.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());

//        ImBasicData1 imBasicData1 =
//                mastersService.getImBasicData1ByItemCodeV2(dbBomLine.getChildItemCode(),
//                        languageId,
//                        companyCodeId,
//                        plantId,
//                        outboundIntegrationHeader.getWarehouseID(),
//                        outboundIntegrationLine.getManufacturerName(),
//                        authTokenForMastersService.getAccess_token());
        if (imBasicData1 != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
            // MFR
            preOutboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());
        }

        // PARTNER_CODE
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

        // ORD_QTY
        double orderQuantity = outboundIntegrationLine.getOrderedQty() * dbBomLine.getChildItemQuantity();
        preOutboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

        // REQ_DEL_DATE
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // STCK_TYP_ID
        preOutboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preOutboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preOutboundLine.setStatusId(39L);

        IKeyValuePair description = stagingLineV2Repository.getDescription(preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getLanguageId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId());

        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        preOutboundLine.setStatusDescription(statusDescription);

        preOutboundLine.setCompanyDescription(description.getCompanyDesc());
        preOutboundLine.setPlantDescription(description.getPlantDesc());
        preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());

        preOutboundLine.setMiddlewareId(outboundIntegrationLine.getMiddlewareId());
        preOutboundLine.setMiddlewareHeaderId(outboundIntegrationLine.getMiddlewareHeaderId());
        preOutboundLine.setMiddlewareTable(outboundIntegrationLine.getMiddlewareTable());
        preOutboundLine.setReferenceDocumentType(outboundIntegrationLine.getReferenceDocumentType());
        preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
        preOutboundLine.setManufacturerFullName(outboundIntegrationLine.getManufacturerFullName());
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        preOutboundLine.setManufacturerCode(outboundIntegrationLine.getManufacturerCode());
        preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
        preOutboundLine.setSalesOrderNumber(outboundIntegrationLine.getSalesOrderNumber());
        preOutboundLine.setSupplierInvoiceNo(outboundIntegrationLine.getSupplierInvoiceNo());
        preOutboundLine.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        preOutboundLine.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        preOutboundLine.setTransferOrderNo(outboundIntegrationLine.getTransferOrderNo());
        preOutboundLine.setReturnOrderNo(outboundIntegrationLine.getReturnOrderNo());
        preOutboundLine.setIsCompleted(outboundIntegrationLine.getIsCompleted());
        preOutboundLine.setIsCancelled(outboundIntegrationLine.getIsCancelled());

        // REF_FIELD_1
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

        // REF_FIELD_2
        preOutboundLine.setReferenceField2("BOM");

        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());
        return preOutboundLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param outboundIntegrationLine
     * @return
     */
    private PreOutboundLineV2 createPreOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                      OutboundIntegrationHeaderV2 outboundIntegrationHeader, OutboundIntegrationLineV2 outboundIntegrationLine,
                                                      String authToken) {
        try {
            log.info("Outbound Process PreOutboundLine Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        PreOutboundLineV2 preOutboundLine = new PreOutboundLineV2();

        preOutboundLine.setLanguageId(languageId);
        preOutboundLine.setCompanyCodeId(companyCodeId);
        preOutboundLine.setPlantId(plantId);

        // WH_ID
        preOutboundLine.setWarehouseId(warehouseId);

        // REF DOC Number
        preOutboundLine.setRefDocNumber(outboundIntegrationHeader.getRefDocumentNo());

        // PRE_IB_NO
        preOutboundLine.setPreOutboundNo(preOutboundNo);

        // PARTNER_CODE
        preOutboundLine.setPartnerCode(outboundIntegrationHeader.getPartnerCode());

        // IB__LINE_NO
        preOutboundLine.setLineNumber(outboundIntegrationLine.getLineReference());

        // ITM_CODE
        preOutboundLine.setItemCode(outboundIntegrationLine.getItemCode());

        // OB_ORD_TYP_ID
        preOutboundLine.setOutboundOrderTypeId(outboundIntegrationHeader.getOutboundOrderTypeID());

        // STATUS_ID
        preOutboundLine.setStatusId(39L);

        // STCK_TYP_ID
        preOutboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preOutboundLine.setSpecialStockIndicatorId(1L);

        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());

        IKeyValuePair description = stagingLineV2Repository.getDescription(preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getLanguageId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId());

        statusDescription = stagingLineV2Repository.getStatusDescription(39L, languageId);
        preOutboundLine.setStatusDescription(statusDescription);

        preOutboundLine.setCompanyDescription(description.getCompanyDesc());
        preOutboundLine.setPlantDescription(description.getPlantDesc());
        preOutboundLine.setWarehouseDescription(description.getWarehouseDesc());

        preOutboundLine.setMiddlewareId(outboundIntegrationLine.getMiddlewareId());
        preOutboundLine.setMiddlewareHeaderId(outboundIntegrationLine.getMiddlewareHeaderId());
        preOutboundLine.setMiddlewareTable(outboundIntegrationLine.getMiddlewareTable());
        preOutboundLine.setReferenceDocumentType(outboundIntegrationLine.getReferenceDocumentType());
        preOutboundLine.setSalesInvoiceNumber(outboundIntegrationLine.getSalesInvoiceNo());
        preOutboundLine.setManufacturerFullName(outboundIntegrationLine.getManufacturerFullName());
        preOutboundLine.setManufacturerCode(outboundIntegrationLine.getManufacturerCode());
        preOutboundLine.setManufacturerName(outboundIntegrationLine.getManufacturerName());
        preOutboundLine.setPickListNumber(outboundIntegrationLine.getPickListNo());
        preOutboundLine.setSalesOrderNumber(outboundIntegrationLine.getSalesOrderNumber());
        preOutboundLine.setSupplierInvoiceNo(outboundIntegrationLine.getSupplierInvoiceNo());
        preOutboundLine.setTokenNumber(outboundIntegrationHeader.getTokenNumber());
        preOutboundLine.setTargetBranchCode(outboundIntegrationHeader.getTargetBranchCode());

        preOutboundLine.setTransferOrderNo(outboundIntegrationLine.getTransferOrderNo());
        preOutboundLine.setReturnOrderNo(outboundIntegrationLine.getReturnOrderNo());
        preOutboundLine.setIsCompleted(outboundIntegrationLine.getIsCompleted());
        preOutboundLine.setIsCancelled(outboundIntegrationLine.getIsCancelled());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(companyCodeId);
        imBasicData.setPlantId(plantId);
        imBasicData.setLanguageId(languageId);
        imBasicData.setWarehouseId(outboundIntegrationHeader.getWarehouseID());
        imBasicData.setItemCode(outboundIntegrationLine.getItemCode());
        imBasicData.setManufacturerName(outboundIntegrationLine.getManufacturerName());
            ImBasicData1 imBasicData1 = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authToken);
//        ImBasicData1 imBasicData1 =
//                mastersService.getImBasicData1ByItemCodeV2(outboundIntegrationLine.getItemCode(),
//                        languageId,
//                        companyCodeId,
//                        plantId,
//                        outboundIntegrationHeader.getWarehouseID(),
//                        outboundIntegrationLine.getManufacturerName(),
//                        authTokenForMastersService.getAccess_token());
        log.info("imBasicData1 : " + imBasicData1);
        if (imBasicData1 != null && imBasicData1.getDescription() != null) {
            preOutboundLine.setDescription(imBasicData1.getDescription());
        } else {
            preOutboundLine.setDescription(outboundIntegrationLine.getItemText());
        }

        // ORD_QTY
        preOutboundLine.setOrderQty(outboundIntegrationLine.getOrderedQty());

        // ORD_UOM
        preOutboundLine.setOrderUom(outboundIntegrationLine.getUom());

        // REQ_DEL_DATE
        preOutboundLine.setRequiredDeliveryDate(outboundIntegrationHeader.getRequiredDeliveryDate());

        // REF_FIELD_1
        preOutboundLine.setReferenceField1(outboundIntegrationLine.getRefField1ForOrderType());

        preOutboundLine.setDeletionIndicator(0L);
        preOutboundLine.setCreatedBy("MW_AMS");
        preOutboundLine.setCreatedOn(new Date());

        log.info("preOutboundLine : " + preOutboundLine);
        return preOutboundLine;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error on processing PreoutboundLine " + e);
        }
    }

    /**
     * ORDERMANAGEMENTLINE TABLE
     * @param companyCodeId
     * @param plantId
     * @param preOutboundNo
     * @param outboundIntegrationHeader
     * @param preOutboundLine
     * @return
     */
    private OrderManagementLineV2 createOrderManagementLineV2(String companyCodeId, String plantId, String languageId, String preOutboundNo,
                                                              OutboundIntegrationHeaderV2 outboundIntegrationHeader, PreOutboundLineV2 preOutboundLine, String authToken) {
        log.info("Outbound Process OrderManagementLine Initiated----> " + outboundIntegrationHeader.getRefDocumentNo() + " - " + outboundIntegrationHeader.getOutboundOrderTypeID() + " - " + new Date());
        try {
        OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
        BeanUtils.copyProperties(preOutboundLine, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLine));

        orderManagementLine.setMiddlewareId(preOutboundLine.getMiddlewareId());
        orderManagementLine.setMiddlewareHeaderId(preOutboundLine.getMiddlewareHeaderId());
        orderManagementLine.setMiddlewareTable(preOutboundLine.getMiddlewareTable());
            orderManagementLine.setPreOutboundNo(preOutboundNo);
        orderManagementLine.setReferenceDocumentType(preOutboundLine.getReferenceDocumentType());
        orderManagementLine.setSalesInvoiceNumber(preOutboundLine.getSalesInvoiceNumber());
        orderManagementLine.setManufacturerName(preOutboundLine.getManufacturerName());
        orderManagementLine.setItemCode(preOutboundLine.getItemCode());
        orderManagementLine.setReferenceField1(preOutboundLine.getReferenceField1());
        orderManagementLine.setManufacturerFullName(preOutboundLine.getManufacturerFullName());
        orderManagementLine.setPickListNumber(preOutboundLine.getPickListNumber());
        orderManagementLine.setSupplierInvoiceNo(preOutboundLine.getSupplierInvoiceNo());
        orderManagementLine.setTokenNumber(preOutboundLine.getTokenNumber());
        orderManagementLine.setTargetBranchCode(preOutboundLine.getTargetBranchCode());
        orderManagementLine.setPickupCreatedBy(preOutboundLine.getCreatedBy());
        orderManagementLine.setPickupCreatedOn(new Date());

        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(preOutboundLine.getItemCode(),
                preOutboundLine.getCompanyCodeId(),
                preOutboundLine.getPlantId(),
                preOutboundLine.getWarehouseId(),
                preOutboundLine.getManufacturerName(),
                preOutboundLine.getLanguageId());
        log.info("Barcode : " + barcode);

        if (barcode != null && !barcode.isEmpty()) {
            orderManagementLine.setBarcodeId(barcode.get(0));
            orderManagementLine.setItemBarcode(barcode.get(0));
        }

        orderManagementLine.setTransferOrderNo(preOutboundLine.getTransferOrderNo());
        orderManagementLine.setReturnOrderNo(preOutboundLine.getReturnOrderNo());
        orderManagementLine.setIsCompleted(preOutboundLine.getIsCompleted());
        orderManagementLine.setIsCancelled(preOutboundLine.getIsCancelled());

//        List<InventoryV2> inventoryV2 = inventoryService.getInventoryForInhouseTransferV2(companyCodeId, plantId, languageId, preOutboundLine.getWarehouseId(),
//                preOutboundLine.getItemCode(), preOutboundLine.getManufacturerName());
//
//        if (inventoryV2 != null && !inventoryV2.isEmpty()) {
//            orderManagementLine.setBarcodeId(inventoryV2.get(0).getBarcodeId());
//            orderManagementLine.setLevelId(inventoryV2.get(0).getLevelId());
//            log.info("PartnerItemBarCode: " + inventoryV2.get(0).getBarcodeId());
//        }

        // INV_QTY
        /*
         * 1. If OB_ORD_TYP_ID = 0,1,3
         * Pass WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
         * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZB,ZC,ZG,ZT and
         * PUTAWAY_BLOCK and PICK_BLOCK are false(Null).
         *
         * 2. If OB_ORD_TYP_ID = 2
         * Pass  WH_ID/ITM_CODE in INVENTORY table and fetch ST_BIN.
         * Pass ST_BIN into STORAGEBIN table and filter ST_BIN values by ST_SEC_ID values of ZD and PUTAWAY_BLOCK
         * and PICK_BLOCK are false(Null).
         *
         * Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and fetch Sum(INV_QTY)"
         */
        log.info("orderManagementLine : " + orderManagementLine);

        Long OB_ORD_TYP_ID = outboundIntegrationHeader.getOutboundOrderTypeID();
        Long BIN_CLASS_ID;

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); //ZB,ZC,ZG,ZT
            BIN_CLASS_ID = 1L;
            orderManagementLine = createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                                                              preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), authToken);
        }

        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); //ZD
            BIN_CLASS_ID = 7L;
            orderManagementLine = createOrderManagementV2(companyCodeId, plantId, languageId, BIN_CLASS_ID, orderManagementLine, preOutboundLine.getWarehouseId(),
                                                              preOutboundLine.getItemCode(), preOutboundLine.getOrderQty(), authToken);
        }

        // PROP_ST_BIN
        return orderManagementLine;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error on processing OrderManagementLine " + e);
        }
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
     * @return
     */
    private OrderManagementLineV2 createOrderManagementV2(String companyCodeId, String plantId, String languageId,
                                                          Long binClassId, OrderManagementLineV2 orderManagementLine,
                                                          String warehouseId, String itemCode, Double ORD_QTY, String authToken) throws ParseException {
        List<IInventoryImpl> stockType1InventoryList = inventoryService.
                getInventoryForOrderManagementV2(companyCodeId, plantId, languageId, warehouseId, itemCode, 1L, binClassId, orderManagementLine.getManufacturerName());
        log.info("---Global---stockType1InventoryList-------> : " + stockType1InventoryList.size());
        if (stockType1InventoryList.isEmpty()) {
            return createEMPTYOrderManagementLineV2(orderManagementLine);
        }
        return orderManagementLineService.updateAllocationV2(orderManagementLine, binClassId, ORD_QTY, warehouseId, itemCode, "ORDER_PLACED", authToken);
    }

    /**
     * @param orderManagementLine
     * @return
     */
    private OrderManagementLineV2 createEMPTYOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {

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
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    public OutboundIntegrationLog createOutboundIntegrationLogV2(OutboundIntegrationHeaderV2 outbound, String errorDesc)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        try {
            OutboundIntegrationLog dbOutboundIntegrationLog = new OutboundIntegrationLog();

            try {
                Optional<com.tekclover.wms.api.transaction.model.warehouse.Warehouse> warehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                outbound.getCompanyCode(), outbound.getBranchCode(), "EN", 0L);
                dbOutboundIntegrationLog.setWarehouseId(warehouse.get().getWarehouseId());
            } catch (Exception e) {
                throw e;
            }

            dbOutboundIntegrationLog.setLanguageId("EN");
            dbOutboundIntegrationLog.setCompanyCodeId(outbound.getCompanyCode());
            dbOutboundIntegrationLog.setPlantId(outbound.getBranchCode());
            dbOutboundIntegrationLog.setIntegrationLogNumber(outbound.getRefDocumentNo());
            dbOutboundIntegrationLog.setRefDocNumber(outbound.getRefDocumentNo());
            dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
            dbOutboundIntegrationLog.setIntegrationStatus("FAILED");
            dbOutboundIntegrationLog.setOrderReceiptDate(outbound.getOrderProcessedOn());
            dbOutboundIntegrationLog.setDeletionIndicator(0L);
            dbOutboundIntegrationLog.setCreatedBy("MSD_API");

//            dbOutboundIntegrationLog.setReferenceField1(errorDesc);
            /*
             * Setting up KuwaitTime
             */
//            Date kwtDate = DateUtils.getCurrentKWTDateTime();
            dbOutboundIntegrationLog.setCreatedOn(new Date());
            dbOutboundIntegrationLog = outboundIntegrationLogRepository.save(dbOutboundIntegrationLog);
            log.info("dbOutboundIntegrationLog : " + dbOutboundIntegrationLog);
            return dbOutboundIntegrationLog;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //cancel order PreOutBoundHeader
    public PreOutboundHeaderV2 cancelPreOutboundHeader(String companyCodeId, String languageId, String plantId, String warehouseId,
                                                       String refDocNumber, String  preOutboundNo, String loginUserID, String remarks) throws Exception {
        PreOutboundHeaderV2 preOutboundHeaderV2 =
                preOutboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber,  preOutboundNo,0L);
        log.info("PickList Cancellation - preOutboundHeaderV2: " + preOutboundHeaderV2);
        if (preOutboundHeaderV2 != null) {
            preOutboundHeaderV2.setUpdatedBy(loginUserID);
            preOutboundHeaderV2.setUpdatedOn(new Date());
            preOutboundHeaderV2.setStatusId(96L);           //Order Cancelled
            statusDescription = stagingLineV2Repository.getStatusDescription(96L, languageId);
            if(remarks != null) {
                preOutboundHeaderV2.setReferenceField1(remarks);
            }
            preOutboundHeaderV2.setStatusDescription(statusDescription);
            preOutboundHeaderV2Repository.save(preOutboundHeaderV2);
        }
        return preOutboundHeaderV2;
    }

    //Delete PreOutBoundHeader
    public PreOutboundHeaderV2 deletePreOutboundHeader(String companyCodeId, String languageId, String plantId,
                                                       String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) throws Exception {
        PreOutboundHeaderV2 preOutboundHeaderV2 =
                preOutboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - preOutboundHeaderV2: " + preOutboundHeaderV2);
        if (preOutboundHeaderV2 != null) {
            preOutboundHeaderV2.setUpdatedBy(loginUserID);
            preOutboundHeaderV2.setUpdatedOn(new Date());
            preOutboundHeaderV2.setDeletionIndicator(1L);
            preOutboundHeaderV2Repository.save(preOutboundHeaderV2);
        }
        return preOutboundHeaderV2;
    }


    //Delete Old PickList records and insert new picklist data
    private PickListCancellation pickListCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      String oldPickListNumber, String newPickListNumber, String oldPreOutboundNo, String loginUserID) throws Exception {

        log.info("PickList Cancellation Initiated---> " + companyCodeId + "," + plantId + "," + languageId + "," + warehouseId + "," + oldPickListNumber + "," + oldPreOutboundNo);
        //Delete OutBoundHeader
        OutboundHeaderV2 outboundHeaderV2 = outboundHeaderService.deleteOutBoundHeader(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("OutboundHeaderV2 Deleted SuccessFully" + outboundHeaderV2);

        //Delete OutBoundLine
        List<OutboundLineV2> outboundLineV2 = outboundLineService.deleteOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("OutboundLineV2 Deleted SuccessFully" + outboundLineV2);

        //Delete OutBoundLine Interim
        List<OutboundLineInterim> outboundLineInterimList = outboundLineService.deleteOutboundLineInterimForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("OutboundLine Interim deleted Successfully " + outboundLineInterimList);

        //Delete PreOutBoundHeaderV2
        PreOutboundHeaderV2 preOutboundHeader = deletePreOutboundHeader(companyCodeId, languageId, plantId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("PreOutBoundHeader Deleted SuccessFully" + preOutboundHeader);

        //Delete PreOutboundLine
        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineService.deletePreOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("PreOutBoundLine Deleted SuccessFully" + preOutboundLineV2List);

        //DeleteOrderManagementLine
        OrderManagementHeader orderManagementHeader = orderManagementHeaderService.deleteOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("OrderManagementHeader Deleted SuccessFully" + orderManagementHeader);

        //DeleteOrderManagementLine
        List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.deleteOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("OrderManagementLine Deleted SuccessFully" + orderManagementLine);

        //Delete PickupHeader
        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickUpHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("PickupHeader Deleted SuccessFully" + pickupHeader);

        //Delete PickUpLine
        List<PickupLineV2> pickupLineV2 = pickupLineService.deletePickUpLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("PickupLineV2 Deleted SuccessFully" + pickupLineV2);
        List<String> pickuplineItemCodeMfrNameList = new ArrayList<>();
        List<String> pickuplineLineNoItemCodeMfrNameList = new ArrayList<>();
        if(pickupLineV2 != null && !pickupLineV2.isEmpty()){
            for(PickupLineV2 pickupLine : pickupLineV2) {
                pickuplineItemCodeMfrNameList.add(pickupLine.getItemCode()+pickupLine.getManufacturerName());
                InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                        pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                List<PickupLineV2> filteredList = pickupLineV2.stream().filter(a -> a.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                        a.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                        a.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                List<PreOutboundLineV2> filteredPreOutboundLineList = preOutboundLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                        n.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                        n.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                Double PICK_CNF_QTY = 0D;
                Double ORD_QTY = 0D;
                if(filteredList != null && !filteredList.isEmpty()){
                    PICK_CNF_QTY = filteredList.stream().mapToDouble(a->a.getPickConfirmQty()).sum();
                }
                if(filteredPreOutboundLineList != null && !filteredPreOutboundLineList.isEmpty()){
                    ORD_QTY = filteredPreOutboundLineList.stream().mapToDouble(n->n.getOrderQty()).sum();
                }
                boolean equalQty = PICK_CNF_QTY.equals(ORD_QTY);
                log.info("PICK_CNF_QTY, ORD_TY, EqualQty Condition: " + PICK_CNF_QTY + ", " + ORD_QTY + ", " + equalQty);
                if(equalQty) {
                    if (inventory != null) {
                        Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                        if (INV_QTY < 0) {
                            log.info("inventory qty calculated is less than 0: " + INV_QTY);
                            INV_QTY = 0D;
                        }
                        inventory.setInventoryQuantity(INV_QTY);
                        Double ALLOC_QTY = 0D;
                        if (inventory.getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventory.getAllocatedQuantity();
                        }
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        inventory.setReferenceField4(TOT_QTY);              //Total Qty

                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
                        newInventoryV2.setInventoryId(System.currentTimeMillis());
                        InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + updateInventoryV2);
                    }
                }
                if(!equalQty) {
                    String itmMfrNameLineNo = pickupLine.getItemCode()+pickupLine.getManufacturerName()+pickupLine.getLineNumber();
                    List<String> filterList = pickuplineLineNoItemCodeMfrNameList.stream().filter(a->a.equalsIgnoreCase(itmMfrNameLineNo)).collect(Collectors.toList());
                    if(filterList.size() == 0) {
                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + ORD_QTY;
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if (inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity() - (ORD_QTY - PICK_CNF_QTY);
                            }
                            if (ALLOC_QTY < 0) {
                                log.info("allocated qty calculated is less than 0: " + ALLOC_QTY);
                                ALLOC_QTY = 0D;
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                        pickuplineLineNoItemCodeMfrNameList.add(itmMfrNameLineNo);
                    }
                }
            }
        }

        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            log.info("Inventory update for OrderManagementLine");
            log.info("Pickupline ItmCodeMfrName: " + pickuplineItemCodeMfrNameList);
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                if (dbOrderManagementLine.getStatusId() != 47L) {
                    if (dbOrderManagementLine.getProposedStorageBin() == null || dbOrderManagementLine.getProposedStorageBin().equalsIgnoreCase("")) {
                        throw new BadRequestException("OrderManagementLine ProposedStorageBin is Empty, hence inventory cannot be reversed - PickList Cancellation Aborting");
                    }
                    String itmMfrName = dbOrderManagementLine.getItemCode() + dbOrderManagementLine.getManufacturerName();
                    log.info("ItmMfrName: " + itmMfrName);
                    List<String> itmPresent = pickuplineItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrName)).collect(Collectors.toList());
                    log.info("itmPresent: " + itmPresent);
                    if(itmPresent.size() == 0){
                        InventoryV2 inventory = inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getProposedPackBarCode(), dbOrderManagementLine.getItemCode(),
                                dbOrderManagementLine.getProposedStorageBin(), dbOrderManagementLine.getManufacturerName());

                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) - (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                            if (ALLOC_QTY < 0) {
                                ALLOC_QTY = 0D;
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setAllocatedQuantity(ALLOC_QTY);
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                    }
                }
            }
        }
        //Quality Header
        List<QualityHeaderV2> qualityHeaderV2 = qualityHeaderService.deleteQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("QualityHeader Deleted SuccessFully" + qualityHeaderV2);

        //Quality Line
        List<QualityLineV2> qualityLineV2 = qualityLineService.deleteQualityLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("QualityLine Deleted SuccessFully" + qualityLineV2);

        //Delete InventoryMovement
        List<InventoryMovement> inventoryMovement = inventoryMovementService.deleteInventoryMovement(warehouseId, companyCodeId, plantId, languageId, oldPickListNumber, oldPreOutboundNo, loginUserID);
        log.info("InventoryMovement Deleted SuccessFully" + inventoryMovement);

        PickListCancellation pickListCancellation = new PickListCancellation();
        if(pickupLineV2 != null && !pickupLineV2.isEmpty()){
            pickListCancellation.setOldPickupLineList(pickupLineV2);
        }
        if(qualityLineV2 != null && !qualityLineV2.isEmpty()) {
            pickListCancellation.setOldQualityLineList(qualityLineV2);
        }
        if(outboundHeaderV2 != null) {
            pickListCancellation.setOldOutboundHeader(outboundHeaderV2);
        }
        if(outboundLineV2 != null && !outboundLineV2.isEmpty()){
            pickListCancellation.setOldOutboundLineList(outboundLineV2);
        }
        if(orderManagementLine != null && !orderManagementLine.isEmpty()){
            pickListCancellation.setOldOrderManagementLineList(orderManagementLine);
        }
        pickListCancellation.setOldPickListNumber(oldPickListNumber);
        pickListCancellation.setNewPickListNumber(newPickListNumber);
        return pickListCancellation;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     * @param pickListCancellation
     * @param loginUserID
     * @throws Exception
     */
    private void createPickListCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String oldPickListNumber, String newPickListNumber, String newPreOutboundNo,
                                            PickListCancellation pickListCancellation,
                                            String loginUserID) throws Exception {
    List<PickupLineV2> pickupLineV2 = pickListCancellation.getOldPickupLineList();
    List<QualityLineV2> qualityLineV2 = pickListCancellation.getOldQualityLineList();
    OutboundHeaderV2 outboundHeaderV2 = pickListCancellation.getOldOutboundHeader();
    List<OutboundLineV2> outboundLineV2 = pickListCancellation.getOldOutboundLineList();
    List<OrderManagementLineV2> orderManagementLine = pickListCancellation.getOldOrderManagementLineList();
        //PickUpLine Creation
        List<PickupLineV2> createNewPickUpLineList = null;
        if(pickupLineV2 != null && !pickupLineV2.isEmpty()) {
            List<PickupHeaderV2> newPickupHeaderList = pickupHeaderService.getPickupHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("newPickupHeaderList: " + newPickupHeaderList);

            List<AddPickupLine> newPickupLineList = new ArrayList<>();
            if(newPickupHeaderList != null && !newPickupHeaderList.isEmpty()){
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                // Create a thread pool with a fixed number of threads
                ExecutorService executor = Executors.newFixedThreadPool(10);
                // Batch processing of preAlerts using streams and CompletableFuture
                newPickupHeaderList.forEach(pickupHeaderV2 -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                for (PickupHeaderV2 pickupHeaderV2 : newPickupHeaderList) {
                    List<PickupLineV2> pickupLinePresent =
                            pickupLineV2
                                    .stream()
                                    .filter(n->n.getItemCode().equalsIgnoreCase(pickupHeaderV2.getItemCode()) && n.getManufacturerName().equalsIgnoreCase(pickupHeaderV2.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Pickup Line present for that itemCode & MFR_Name: " + pickupLinePresent);

                    if(pickupLinePresent != null && !pickupLinePresent.isEmpty()){
                    AddPickupLine newPickupLine = new AddPickupLine();
                    BeanUtils.copyProperties(pickupHeaderV2, newPickupLine, CommonUtils.getNullPropertyNames(pickupHeaderV2));
                    Double oldPickConfirmQty = pickupLinePresent.get(0).getPickConfirmQty();
                    Double newPickConfirmQty = pickupHeaderV2.getPickToQty();
                    if(oldPickConfirmQty < newPickConfirmQty){
//                        Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
                        newPickupLine.setPickConfirmQty(oldPickConfirmQty);
                        newPickupLine.setAllocatedQty(oldPickConfirmQty);
                        newPickupLine.setPickQty(String.valueOf(oldPickConfirmQty));
                    }
                    if(oldPickConfirmQty >= newPickConfirmQty){
                        newPickupLine.setPickConfirmQty(newPickConfirmQty);
                        newPickupLine.setAllocatedQty(newPickConfirmQty);
                        newPickupLine.setPickQty(String.valueOf(newPickConfirmQty));
                    }
                    newPickupLine.setCompanyCodeId(Long.valueOf(pickupHeaderV2.getCompanyCodeId()));
                    newPickupLine.setPickedStorageBin(pickupHeaderV2.getProposedStorageBin());
                    newPickupLine.setPickupNumber(pickupHeaderV2.getPickupNumber());
                    newPickupLine.setActualHeNo(pickupLinePresent.get(0).getActualHeNo());
                    newPickupLine.setPickedPackCode(pickupHeaderV2.getProposedPackBarCode());
                    newPickupLineList.add(newPickupLine);
                    }
                    }, executor);
                    futures.add(future);
                });

                // Wait for all the futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                // Shut down the executor service
                executor.shutdown();
            }
            if(newPickupLineList != null && !newPickupLineList.isEmpty()){
//                createNewPickUpLineList = pickupLineService.createPickupLineV2(newPickupLineList, loginUserID);
                createNewPickUpLineList = pickupLineService.createPickupLineNonCBMPickListCancellationV2(newPickupLineList, loginUserID);
                log.info("CreatedPickUpLine List : " + createNewPickUpLineList);
            }
        }

        //Quality Line Creation
        if(qualityLineV2 != null && !qualityLineV2.isEmpty()) {
            List<QualityHeaderV2> newQualityHeaderList = qualityHeaderService.getQualityHeaderForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber, newPreOutboundNo);
            log.info("NewQualityHeaderList: " + newQualityHeaderList);

            List<AddQualityLineV2> newQualityLineList = new ArrayList<>();
            if(newQualityHeaderList != null && !newQualityHeaderList.isEmpty()){
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                // Create a thread pool with a fixed number of threads
                ExecutorService executor = Executors.newFixedThreadPool(10);
                // Batch processing of preAlerts using streams and CompletableFuture
                newQualityHeaderList.forEach(qualityHeader -> {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                for (QualityHeaderV2 qualityHeader : newQualityHeaderList) {
                    List<QualityLineV2> qualityLinePresent =
                            qualityLineV2
                                    .stream()
                                    .filter(n->n.getItemCode().equalsIgnoreCase(qualityHeader.getReferenceField4()) && n.getManufacturerName().equalsIgnoreCase(qualityHeader.getManufacturerName()))
                                    .collect(Collectors.toList());
                    log.info("Quality Line Present for that itemCode and MFR_Name: " + qualityLinePresent);

                    if(qualityLinePresent != null && !qualityLinePresent.isEmpty()){
                        AddQualityLineV2 newQualityLine = new AddQualityLineV2();
                        BeanUtils.copyProperties(qualityHeader, newQualityLine, CommonUtils.getNullPropertyNames(qualityHeader));

                        Double oldPickConfirmQty = qualityLinePresent.get(0).getPickConfirmQty();
                        Double newPickConfirmQty = Double.valueOf(qualityHeader.getQcToQty());
                        if(oldPickConfirmQty < newPickConfirmQty){
//                            Double pickConfirmQty = newPickConfirmQty - oldPickConfirmQty;
                            newQualityLine.setPickConfirmQty(oldPickConfirmQty);
                            newQualityLine.setQualityQty(Double.valueOf(oldPickConfirmQty));
                        }
                        if(oldPickConfirmQty >= newPickConfirmQty){
                            newQualityLine.setPickConfirmQty(Double.valueOf(newPickConfirmQty));
                            newQualityLine.setQualityQty(Double.valueOf(newPickConfirmQty));
                        }

                        newQualityLine.setDescription(qualityHeader.getReferenceField3());
                        newQualityLine.setItemCode(qualityHeader.getReferenceField4());
                        newQualityLine.setManufacturerName(qualityHeader.getManufacturerName());
                        newQualityLine.setPickPackBarCode(qualityHeader.getReferenceField2());
                        newQualityLine.setQualityInspectionNo(qualityHeader.getQualityInspectionNo());
                        if(qualityHeader.getReferenceField5() != null) {
                            newQualityLine.setLineNumber(Long.valueOf(qualityHeader.getReferenceField5()));
                        }
                        newQualityLineList.add(newQualityLine);
                    }
                    }, executor);
                    futures.add(future);
                });

                // Wait for all the futures to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                // Shut down the executor service
                executor.shutdown();
            }
            if(newQualityLineList != null && !newQualityLineList.isEmpty()) {
                List<QualityLineV2> createNewQualityLineList = qualityLineService.createQualityLineV2(newQualityLineList, loginUserID);
                log.info("Created Quality Line List : " + createNewQualityLineList);
            }
        }
        log.info("Pick List Cancellation Completed");
        log.info("Stored procedure call to update cnf_by in pickup line, qc header and line : " + oldPickListNumber + ", " + outboundHeaderV2.getPreOutboundNo() + ", " + newPickListNumber + ", " + newPreOutboundNo + ", " + outboundHeaderV2.getSalesOrderNumber());
        pickListHeaderRepository.updatePickupLineQualityHeaderLineCnfByUpdateProc(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, outboundHeaderV2.getPreOutboundNo(), newPickListNumber, newPreOutboundNo, outboundHeaderV2.getSalesOrderNumber());
        log.info("SP update done");
        insertNewPickListCancelRecord(outboundHeaderV2, outboundLineV2, pickupLineV2, createNewPickUpLineList, orderManagementLine, companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, newPickListNumber);
    }

    /**
     * @param outboundHeaderV2
     * @param outboundLineV2List
     * @param oldPickupLineList
     * @param newPickupLineList
     * @param orderManagementLineList
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     */
    private void insertNewPickListCancelRecord(OutboundHeaderV2 outboundHeaderV2, List<OutboundLineV2> outboundLineV2List, List<PickupLineV2> oldPickupLineList,
                                               List<PickupLineV2> newPickupLineList, List<OrderManagementLineV2> orderManagementLineList,
                                               String companyCodeId, String plantId, String languageId, String warehouseId,
                                               String oldPickListNumber, String newPickListNumber){
        log.info("Insert Record for PickList Cancellation Started");
        String loginUserID = null;
        Long oldObLineCount = 0L;
        Long newObLineCount = 0L;
        Long oldPickLineCount = 0L;
        Long newPickLineCount = 0L;
        if(outboundLineV2List != null && !outboundLineV2List.isEmpty()){            //old OrderLines Count
            oldObLineCount = outboundLineV2List.stream().count();
        }
        if(oldPickupLineList != null && !oldPickupLineList.isEmpty()) {             //old PickedLines Count
            oldPickLineCount = oldPickupLineList.stream().count();
        }
        List<OutboundLineV2> newOutboundLineList = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber);               //Fetch New OrderLines
        if(newOutboundLineList != null && !newOutboundLineList.isEmpty()) {         //new OrderLines Count
            newObLineCount = newOutboundLineList.stream().count();
        }
        if(newPickupLineList != null && !newPickupLineList.isEmpty()) {             //new PickedLines Count
            newPickLineCount = newPickupLineList.stream().count();
        }

        if(outboundHeaderV2 != null) {
            loginUserID = outboundHeaderV2.getCreatedBy();
            PickListHeader pickListHeader = new PickListHeader();
            BeanUtils.copyProperties(outboundHeaderV2, pickListHeader, CommonUtils.getNullPropertyNames(outboundHeaderV2));
            OutboundHeaderV2 newOutboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, newPickListNumber, warehouseId);         //Fetch New Order Details - Header
            if(newOutboundHeader != null) {
                loginUserID = newOutboundHeader.getCreatedBy();
                pickListHeader.setOldPreOutboundNo(outboundHeaderV2.getPreOutboundNo());
                pickListHeader.setOldRefDocNumber(outboundHeaderV2.getRefDocNumber());
                pickListHeader.setOldPickListNumber(oldPickListNumber);
                pickListHeader.setOldCustomerId(outboundHeaderV2.getCustomerId());
                pickListHeader.setOldCustomerName(outboundHeaderV2.getCustomerName());
                pickListHeader.setOldInvoiceDate(outboundHeaderV2.getInvoiceDate());
                pickListHeader.setOldInvoiceNumber(outboundHeaderV2.getInvoiceNumber());
                pickListHeader.setOldSalesInvoiceNumber(outboundHeaderV2.getSalesInvoiceNumber());
                pickListHeader.setOldSupplierInvoiceNo(outboundHeaderV2.getSupplierInvoiceNo());
                pickListHeader.setOldSalesOrderNumber(outboundHeaderV2.getSalesOrderNumber());
                pickListHeader.setOldTokenNumber(outboundHeaderV2.getTokenNumber());
                pickListHeader.setOldCountOfOrderedLine(oldObLineCount);
                pickListHeader.setOldCountOfPickedLine(oldPickLineCount);
                pickListHeader.setOldStatusId(outboundHeaderV2.getStatusId());
                pickListHeader.setOldStatusDescription(outboundHeaderV2.getStatusDescription());

                pickListHeader.setNewPreOutboundNo(newOutboundHeader.getPreOutboundNo());
                pickListHeader.setNewRefDocNumber(newOutboundHeader.getRefDocNumber());
                pickListHeader.setNewPickListNumber(newOutboundHeader.getPickListNumber());
                pickListHeader.setNewCustomerId(newOutboundHeader.getCustomerId());
                pickListHeader.setNewCustomerName(newOutboundHeader.getCustomerName());
                pickListHeader.setNewInvoiceDate(newOutboundHeader.getInvoiceDate());
                pickListHeader.setNewInvoiceNumber(newOutboundHeader.getInvoiceNumber());
                pickListHeader.setNewSalesInvoiceNumber(newOutboundHeader.getSalesInvoiceNumber());
                pickListHeader.setNewSupplierInvoiceNo(newOutboundHeader.getSupplierInvoiceNo());
                pickListHeader.setNewSalesOrderNumber(newOutboundHeader.getSalesOrderNumber());
                pickListHeader.setNewTokenNumber(newOutboundHeader.getTokenNumber());
                pickListHeader.setNewCountOfOrderedLine(newObLineCount);
                pickListHeader.setNewCountOfPickedLine(newPickLineCount);
                pickListHeader.setNewStatusId(newOutboundHeader.getStatusId());
                pickListHeader.setNewStatusDescription(newOutboundHeader.getStatusDescription());
                pickListHeader.setPickListCancelHeaderId(System.currentTimeMillis());
            }
            List<PickListLine> createPickListLineList = new ArrayList<>();
            List<String> createdItmMfrNameList = new ArrayList<>();
            List<String> oldItmMfrNameList = new ArrayList<>();
            List<String> newItmMfrNameList = new ArrayList<>();
            List<String> filterOldList = new ArrayList<>();
            List<String> filterNewList = new ArrayList<>();
            List<String> createdItmMfrNameList2 = new ArrayList<>();
            List<String> oldItmMfrNameList2 = new ArrayList<>();
            List<String> newItmMfrNameList2 = new ArrayList<>();
            List<String> filterOldList2 = new ArrayList<>();
            List<String> filterNewList2 = new ArrayList<>();
            if(oldPickupLineList != null && !oldPickupLineList.isEmpty()) {
                if (newPickupLineList != null && !newPickupLineList.isEmpty()) {
                    for (PickupLineV2 pickupLineV2 : oldPickupLineList) {
                        for (PickupLineV2 newPickupLineV2 : newPickupLineList) {
                            if (pickupLineV2.getItemCode().equalsIgnoreCase(newPickupLineV2.getItemCode()) &&
                                    pickupLineV2.getManufacturerName().equalsIgnoreCase(newPickupLineV2.getManufacturerName())) {

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(pickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(pickupLineV2));

                                dbPickListLine.setOldPreOutboundNo(pickupLineV2.getPreOutboundNo());
                                dbPickListLine.setOldRefDocNumber(pickupLineV2.getRefDocNumber());
                                dbPickListLine.setOldLineNo(pickupLineV2.getLineNumber());
                                dbPickListLine.setOldPickConfirmQty(pickupLineV2.getPickConfirmQty());
                                dbPickListLine.setOldPickedStorageBin(pickupLineV2.getPickedStorageBin());
                                dbPickListLine.setOldOrderQty(pickupLineV2.getPickConfirmQty());
                                dbPickListLine.setOldStatusId(pickupLineV2.getStatusId());
                                dbPickListLine.setOldStatusDescription(pickupLineV2.getStatusDescription());

                                dbPickListLine.setNewPreOutboundNo(newPickupLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newPickupLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newPickupLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewPickedStorageBin(newPickupLineV2.getPickedStorageBin());
                                dbPickListLine.setNewOrderQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewStatusId(newPickupLineV2.getStatusId());
                                dbPickListLine.setNewStatusDescription(newPickupLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());

                                createPickListLineList.add(dbPickListLine);
                                createdItmMfrNameList.add(newPickupLineV2.getItemCode()+newPickupLineV2.getManufacturerName());
                            }
                        }
                    }
                    for (PickupLineV2 newPickupLine : newPickupLineList) {
                        newItmMfrNameList.add(newPickupLine.getItemCode() + newPickupLine.getManufacturerName());
                    }
                    log.info("NewPickList : " + newItmMfrNameList);
                    log.info("CreatedPickList : " + createdItmMfrNameList);
                    if(createdItmMfrNameList != null && !createdItmMfrNameList.isEmpty()){
                        for(String itmMfrName : createdItmMfrNameList){
                            boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                            if(!newItmPresent){
                                filterNewList.add(itmMfrName);
                            }
                        }
                    }
                    if(createdItmMfrNameList == null || createdItmMfrNameList.isEmpty()){
                        filterNewList = newItmMfrNameList;
                    }
                    log.info("Filtered NewPickList : " + filterNewList);
                    if (filterNewList != null && !filterNewList.isEmpty()) {
                        for (PickupLineV2 newPickupLineV2 : newPickupLineList) {
                            String itmMfrName = newPickupLineV2.getItemCode() + newPickupLineV2.getManufacturerName();
                            boolean itmPresent = filterNewList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if(!itmPresent){
                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(newPickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(newPickupLineV2));
                                dbPickListLine.setNewPreOutboundNo(newPickupLineV2.getPreOutboundNo());
                                dbPickListLine.setNewRefDocNumber(newPickupLineV2.getRefDocNumber());
                                dbPickListLine.setNewLineNo(newPickupLineV2.getLineNumber());
                                dbPickListLine.setNewPickConfirmQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewPickedStorageBin(newPickupLineV2.getPickedStorageBin());
                                dbPickListLine.setNewOrderQty(newPickupLineV2.getPickConfirmQty());
                                dbPickListLine.setNewStatusId(newPickupLineV2.getStatusId());
                                dbPickListLine.setNewStatusDescription(newPickupLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                                createPickListLineList.add(dbPickListLine);
                            }
                        }
                    }
                }
                for (PickupLineV2 oldPickupLine : oldPickupLineList) {
                    oldItmMfrNameList.add(oldPickupLine.getItemCode() + oldPickupLine.getManufacturerName());
                }
                log.info("OldPickList : " + oldItmMfrNameList);
                if (createdItmMfrNameList != null && !createdItmMfrNameList.isEmpty()) {
                    for (String itmMfrName : createdItmMfrNameList) {
                        boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if (!oldItmPresent) {
                            filterOldList.add(itmMfrName);
                        }
                    }
                }
                if (createdItmMfrNameList == null || createdItmMfrNameList.isEmpty()) {
                    filterOldList = oldItmMfrNameList;
                }
                log.info("Filtered OldPickList : " + filterOldList);
                if(filterOldList != null && !filterOldList.isEmpty()){
                    for (PickupLineV2 oldPickupLineV2 : oldPickupLineList) {
                        String itmMfrName = oldPickupLineV2.getItemCode() + oldPickupLineV2.getManufacturerName() + oldPickupLineV2.getLineNumber();
                        boolean itmPresent = filterOldList.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                        if(!itmPresent){
                            PickListLine dbPickListLine = new PickListLine();
                            BeanUtils.copyProperties(oldPickupLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(oldPickupLineV2));
                            dbPickListLine.setOldPreOutboundNo(oldPickupLineV2.getPreOutboundNo());
                            dbPickListLine.setOldRefDocNumber(oldPickupLineV2.getRefDocNumber());
                            dbPickListLine.setOldLineNo(oldPickupLineV2.getLineNumber());
                            dbPickListLine.setOldPickConfirmQty(oldPickupLineV2.getPickConfirmQty());
                            dbPickListLine.setOldPickedStorageBin(oldPickupLineV2.getPickedStorageBin());
                            dbPickListLine.setOldOrderQty(oldPickupLineV2.getPickConfirmQty());
                            dbPickListLine.setOldStatusId(oldPickupLineV2.getStatusId());
                            dbPickListLine.setOldStatusDescription(oldPickupLineV2.getStatusDescription());

                            dbPickListLine.setDeletionIndicator(0L);
                            dbPickListLine.setCreatedBy(loginUserID);
                            dbPickListLine.setUpdatedBy(loginUserID);
                            dbPickListLine.setCreatedOn(new Date());
                            dbPickListLine.setUpdatedOn(new Date());
                            dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                            dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                            createPickListLineList.add(dbPickListLine);
                        }
                    }
                }
            }
            //If PickupLine is Empty fill the line details from outboundLine and bin from OrderManagementLine
//            if(oldPickupLineList == null || oldPickupLineList.isEmpty()) {

                List<OrderManagementLineV2> newOrderManagementLineList = orderManagementLineService
                        .getOrderManagementLineForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, newPickListNumber);
                List<OrderManagementLineV2> oldOrderManagementLineFilteredList = null;
                List<OrderManagementLineV2> newOrderManagementLineFilteredList = null;

                if (outboundLineV2List != null && !outboundLineV2List.isEmpty()) {
                    if (newOutboundLineList != null && !newOutboundLineList.isEmpty()) {
                        for (OutboundLineV2 outboundLineV2 : outboundLineV2List) {
                            String oldItmMfrName = outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName();
                            log.info("oldItmMfrName: " + oldItmMfrName);
                            boolean oldItmPresent = oldItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(oldItmMfrName));
                            if(!oldItmPresent){
                                oldItmMfrNameList2.add(outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName());
                            for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                                String newItmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                                log.info("ItmMfrName: " + newItmMfrName);
                                boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                                if(!newItmPresent){
                                if (outboundLineV2.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                        outboundLineV2.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName())) {

                                    //Filter ItemCode, MFR Name and LineNumber - OldPickList
                                    oldOrderManagementLineFilteredList = orderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode()) &&
                                            a.getManufacturerName().equalsIgnoreCase(outboundLineV2.getManufacturerName()) &&
                                            a.getLineNumber().equals(outboundLineV2.getLineNumber())).collect(Collectors.toList());
                                    //Filter ItemCode, MFR Name and LineNumber - NewPickList
                                    newOrderManagementLineFilteredList = newOrderManagementLineList.stream().filter(a -> a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                            a.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName()) &&
                                            a.getLineNumber().equals(newOutboundLineV2.getLineNumber())).collect(Collectors.toList());

                                    PickListLine dbPickListLine = new PickListLine();
                                    BeanUtils.copyProperties(outboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(outboundLineV2));

                                    dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                                    dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                                    dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                                    dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                                    if (oldOrderManagementLineFilteredList != null && !oldOrderManagementLineFilteredList.isEmpty()) {
                                        dbPickListLine.setOldPickedStorageBin(oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                    }
                                    dbPickListLine.setOldOrderQty(outboundLineV2.getOrderQty());
                                    dbPickListLine.setOldStatusId(outboundLineV2.getStatusId());
                                    dbPickListLine.setOldStatusDescription(outboundLineV2.getStatusDescription());

                                    dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                    dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                    dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                    dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                    if (newOrderManagementLineFilteredList != null && !newOrderManagementLineFilteredList.isEmpty()) {
                                        dbPickListLine.setNewPickedStorageBin(newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                    }
                                    dbPickListLine.setNewOrderQty(newOutboundLineV2.getOrderQty());
                                    dbPickListLine.setNewStatusId(newOutboundLineV2.getStatusId());
                                    dbPickListLine.setNewStatusDescription(newOutboundLineV2.getStatusDescription());

                                    dbPickListLine.setDeletionIndicator(0L);
                                    dbPickListLine.setCreatedBy(loginUserID);
                                    dbPickListLine.setUpdatedBy(loginUserID);
                                    dbPickListLine.setCreatedOn(new Date());
                                    dbPickListLine.setUpdatedOn(new Date());
                                    dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                    dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());

                                    createPickListLineList.add(dbPickListLine);
                                    createdItmMfrNameList2.add(newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName());
                                }
                            }
                            }
                        }
                        }
                        for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                            String newItmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                            log.info("ItmMfrName: " + newItmMfrName);
                            boolean newItmPresent = newItmMfrNameList.stream().anyMatch(a -> a.equalsIgnoreCase(newItmMfrName));
                            if(!newItmPresent) {
                                newItmMfrNameList2.add(newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName());
                            }
                        }
                        log.info("OldPickList2 : " + oldItmMfrNameList2);
                        log.info("NewPickList2 : " + newItmMfrNameList2);
                        log.info("CreatedPickList2 : " + createdItmMfrNameList2);
                        if(createdItmMfrNameList2 != null && !createdItmMfrNameList2.isEmpty()){
                            for(String itmMfrName : createdItmMfrNameList2){
                                boolean oldItmPresent2 = oldItmMfrNameList2.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                                boolean newItmPresent2 = newItmMfrNameList2.stream().anyMatch(a->a.equalsIgnoreCase(itmMfrName));
                            if (!oldItmPresent2) {
                                    filterOldList2.add(itmMfrName);
                                }
                            if (!newItmPresent2) {
                                    filterNewList2.add(itmMfrName);
                                }
                            }
                        }
                        if(createdItmMfrNameList2 == null || createdItmMfrNameList2.isEmpty()){
                            filterOldList2 = oldItmMfrNameList2;
                            filterNewList2 = newItmMfrNameList2;
                        }
//                        }
                        log.info("Filtered OldPickList2 : " + filterOldList2);
                        log.info("Filtered NewPickList2 : " + filterNewList2);
                        if (filterNewList2 != null && !filterNewList2.isEmpty()) {
                            for (OutboundLineV2 newOutboundLineV2 : newOutboundLineList) {
                                String itmMfrName = newOutboundLineV2.getItemCode() + newOutboundLineV2.getManufacturerName();
                                boolean itmPresent = filterNewList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                                if (!itmPresent) {

                                    //Filter ItemCode, MFR Name and LineNumber - NewPickList
                                    newOrderManagementLineFilteredList = newOrderManagementLineList.stream().filter(a->a.getItemCode().equalsIgnoreCase(newOutboundLineV2.getItemCode()) &&
                                            a.getManufacturerName().equalsIgnoreCase(newOutboundLineV2.getManufacturerName()) &&
                                            a.getLineNumber().equals(newOutboundLineV2.getLineNumber())).collect(Collectors.toList());

                                    PickListLine dbPickListLine = new PickListLine();
                                    BeanUtils.copyProperties(newOutboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(newOutboundLineV2));
                                    dbPickListLine.setNewPreOutboundNo(newOutboundLineV2.getPreOutboundNo());
                                    dbPickListLine.setNewRefDocNumber(newOutboundLineV2.getRefDocNumber());
                                    dbPickListLine.setNewLineNo(newOutboundLineV2.getLineNumber());
                                    dbPickListLine.setNewPickConfirmQty(newOutboundLineV2.getDeliveryQty());
                                    if(newOrderManagementLineFilteredList != null && !newOrderManagementLineFilteredList.isEmpty()) {
                                        dbPickListLine.setNewPickedStorageBin(newOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                    }
                                    dbPickListLine.setNewOrderQty(newOutboundLineV2.getOrderQty());
                                    dbPickListLine.setNewStatusId(newOutboundLineV2.getStatusId());
                                    dbPickListLine.setNewStatusDescription(newOutboundLineV2.getStatusDescription());

                                    dbPickListLine.setDeletionIndicator(0L);
                                    dbPickListLine.setCreatedBy(loginUserID);
                                    dbPickListLine.setUpdatedBy(loginUserID);
                                    dbPickListLine.setCreatedOn(new Date());
                                    dbPickListLine.setUpdatedOn(new Date());
                                    dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                    dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                                    createPickListLineList.add(dbPickListLine);
                                }
                            }
                        }
                    }
                    if (oldItmMfrNameList2 != null && !oldItmMfrNameList2.isEmpty()) {
                        for (OutboundLineV2 outboundLineV2 : outboundLineV2List) {
                            String itmMfrName = outboundLineV2.getItemCode() + outboundLineV2.getManufacturerName();
                            boolean itmPresent = filterOldList2.stream().anyMatch(a -> a.equalsIgnoreCase(itmMfrName));
                            if (!itmPresent) {
                                //Filter ItemCode, MFR Name and LineNumber - OldPickList
                                oldOrderManagementLineFilteredList = orderManagementLineList.stream().filter(a->a.getItemCode().equalsIgnoreCase(outboundLineV2.getItemCode()) &&
                                        a.getManufacturerName().equalsIgnoreCase(outboundLineV2.getManufacturerName()) &&
                                        a.getLineNumber().equals(outboundLineV2.getLineNumber())).collect(Collectors.toList());

                                PickListLine dbPickListLine = new PickListLine();
                                BeanUtils.copyProperties(outboundLineV2, dbPickListLine, CommonUtils.getNullPropertyNames(outboundLineV2));
                                dbPickListLine.setOldPreOutboundNo(outboundLineV2.getPreOutboundNo());
                                dbPickListLine.setOldRefDocNumber(outboundLineV2.getRefDocNumber());
                                dbPickListLine.setOldLineNo(outboundLineV2.getLineNumber());
                                dbPickListLine.setOldPickConfirmQty(outboundLineV2.getDeliveryQty());
                                if(oldOrderManagementLineFilteredList != null && !oldOrderManagementLineFilteredList.isEmpty()) {
                                    dbPickListLine.setOldPickedStorageBin(oldOrderManagementLineFilteredList.get(0).getProposedStorageBin());
                                }
                                dbPickListLine.setOldOrderQty(outboundLineV2.getOrderQty());
                                dbPickListLine.setOldStatusId(outboundLineV2.getStatusId());
                                dbPickListLine.setOldStatusDescription(outboundLineV2.getStatusDescription());

                                dbPickListLine.setDeletionIndicator(0L);
                                dbPickListLine.setCreatedBy(loginUserID);
                                dbPickListLine.setUpdatedBy(loginUserID);
                                dbPickListLine.setCreatedOn(new Date());
                                dbPickListLine.setUpdatedOn(new Date());
                                dbPickListLine.setPickListCancelHeaderId(pickListHeader.getPickListCancelHeaderId());
                                dbPickListLine.setPickListCancelLineId(System.currentTimeMillis());
                                createPickListLineList.add(dbPickListLine);
                            }
                        }
                    }
                }
//            }
            log.info("PickList Line: " + createPickListLineList);
            pickListHeader.setLine(createPickListLineList);
            PickListHeader createdPickListHeader = pickListHeaderService.createPickListHeader(pickListHeader, loginUserID);
            log.info("Created PicklistHeader : " + createdPickListHeader);
        }
    }

    //Order Cancellation
    public PreOutboundHeaderV2 orderCancellation(OutboundOrderCancelInput outboundOrderCancelInput, String loginUserID) throws Exception {
        PreOutboundHeaderV2 preOutboundHeaderV2 = orderCancellation(
                outboundOrderCancelInput.getCompanyCodeId(),
                outboundOrderCancelInput.getPlantId(),
                outboundOrderCancelInput.getLanguageId(),
                outboundOrderCancelInput.getWarehouseId(),
                outboundOrderCancelInput.getRefDocNumber(),
                outboundOrderCancelInput.getPreOutboundNo(),
                loginUserID,
                outboundOrderCancelInput.getRemarks()
        );
        return preOutboundHeaderV2;
    }

    //Order Cancellation
    public PreOutboundHeaderV2 orderCancellation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String oldPickListNumber, String preOutboundNo, String loginUserID, String remarks) throws Exception {

        OutboundHeaderV2 outboundOrderV2 =
                outboundHeaderV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, languageId, plantId, warehouseId, oldPickListNumber, preOutboundNo, 0L);
        log.info("Outbound Order status ---> Delivered for Picklist Number: " + outboundOrderV2 + ", " + oldPickListNumber);

        if (outboundOrderV2 == null) {
            throw new BadRequestException("Order not found");
        }

        //Delete OutBoundHeader
        OutboundHeaderV2 outboundHeaderV2 = outboundHeaderService.deleteOutBoundHeader(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("OutboundHeaderV2 Deleted SuccessFully" + outboundHeaderV2);

        //Delete OutBoundLine
        List<OutboundLineV2> outboundLineV2 = outboundLineService.deleteOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("OutboundLineV2 Deleted SuccessFully" + outboundLineV2);

        //Delete OutBoundLine Interim
        List<OutboundLineInterim> outboundLineInterimList = outboundLineService.deleteOutboundLineInterimForPickListCancellationV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("OutboundLine Interim deleted Successfully " + outboundLineInterimList);

        //Delete PreOutBoundHeaderV2
        PreOutboundHeaderV2 preOutboundHeader = cancelPreOutboundHeader(companyCodeId, languageId, plantId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID, remarks);
        log.info("PreOutBoundHeader Cancelled SuccessFully" + preOutboundHeader);

        //Delete PreOutboundLine
        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineService.cancelPreOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("PreOutBoundLine Cancelled SuccessFully" + preOutboundLineV2List);

        //DeleteOrderManagementLine
        OrderManagementHeader orderManagementHeader = orderManagementHeaderService.deleteOrderManagementHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("OrderManagementHeader Deleted SuccessFully" + orderManagementHeader);

        //DeleteOrderManagementLine
        List<OrderManagementLineV2> orderManagementLine = orderManagementLineService.deleteOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("OrderManagementLine Deleted SuccessFully" + orderManagementLine);

        //Delete PickupHeader
        List<PickupHeaderV2> pickupHeader = pickupHeaderService.deletePickUpHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("PickupHeader Deleted SuccessFully" + pickupHeader);

        //Delete PickUpLine
        List<PickupLineV2> pickupLineV2 = pickupLineService.deletePickUpLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("PickupLineV2 Deleted SuccessFully" + pickupLineV2);
        List<String> pickuplineItemCodeMfrNameList = new ArrayList<>();
        List<String> pickuplineLineNoItemCodeMfrNameList = new ArrayList<>();
        if(pickupLineV2 != null && !pickupLineV2.isEmpty()){
            for(PickupLineV2 pickupLine : pickupLineV2) {
                pickuplineItemCodeMfrNameList.add(pickupLine.getItemCode()+pickupLine.getManufacturerName());
                InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                        pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                List<PickupLineV2> filteredList = pickupLineV2.stream().filter(a -> a.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                        a.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                        a.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                List<PreOutboundLineV2> filteredPreOutboundLineList = preOutboundLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                        n.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                        n.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                Double PICK_CNF_QTY = 0D;
                Double ORD_QTY = 0D;
                if(filteredList != null && !filteredList.isEmpty()){
                    PICK_CNF_QTY = filteredList.stream().mapToDouble(a->a.getPickConfirmQty()).sum();
                }
                if(filteredPreOutboundLineList != null && !filteredPreOutboundLineList.isEmpty()){
                    ORD_QTY = filteredPreOutboundLineList.stream().mapToDouble(n->n.getOrderQty()).sum();
                }
                boolean equalQty = PICK_CNF_QTY.equals(ORD_QTY);
                log.info("PICK_CNF_QTY, ORD_TY, EqualQty Condition: " + PICK_CNF_QTY + ", " + ORD_QTY + ", " + equalQty);
                if(equalQty) {
                    if (inventory != null) {
                        Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                        if (INV_QTY < 0) {
                            log.info("inventory qty calculated is less than 0: " + INV_QTY);
                            INV_QTY = 0D;
                        }
                        inventory.setInventoryQuantity(INV_QTY);
                        Double ALLOC_QTY = 0D;
                        if (inventory.getAllocatedQuantity() != null) {
                            ALLOC_QTY = inventory.getAllocatedQuantity();
                        }
                        Double TOT_QTY = INV_QTY + ALLOC_QTY;
                        inventory.setReferenceField4(TOT_QTY);              //Total Qty

                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
                        newInventoryV2.setInventoryId(System.currentTimeMillis());
                        InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + updateInventoryV2);
                    }
                }
                if(!equalQty) {
                    String itmMfrNameLineNo = pickupLine.getItemCode()+pickupLine.getManufacturerName()+pickupLine.getLineNumber();
                    List<String> filterList = pickuplineLineNoItemCodeMfrNameList.stream().filter(a->a.equalsIgnoreCase(itmMfrNameLineNo)).collect(Collectors.toList());
                    if(filterList.size() == 0) {
                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + ORD_QTY;
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if (inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity() - (ORD_QTY - PICK_CNF_QTY);
                            }
                            if (ALLOC_QTY < 0) {
                                log.info("allocated qty calculated is less than 0: " + ALLOC_QTY);
                                ALLOC_QTY = 0D;
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                        pickuplineLineNoItemCodeMfrNameList.add(itmMfrNameLineNo);
                    }
                }
            }
        }

        if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
            log.info("Inventory update for OrderManagementLine");
            log.info("Pickupline ItmCodeMfrName: " + pickuplineItemCodeMfrNameList);
            for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                if (dbOrderManagementLine.getStatusId() != 47L) {
                    if (dbOrderManagementLine.getProposedStorageBin() == null || dbOrderManagementLine.getProposedStorageBin().equalsIgnoreCase("")) {
                        throw new BadRequestException("OrderManagementLine ProposedStorageBin is Empty, hence inventory cannot be reversed - PickList Cancellation Aborting");
                    }
                    String itmMfrName = dbOrderManagementLine.getItemCode() + dbOrderManagementLine.getManufacturerName();
                    log.info("ItmMfrName: " + itmMfrName);
                    List<String> itmPresent = pickuplineItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrName)).collect(Collectors.toList());
                    log.info("itmPresent: " + itmPresent);
                    if(itmPresent.size() == 0){
                        InventoryV2 inventory = inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getProposedPackBarCode(), dbOrderManagementLine.getItemCode(),
                                dbOrderManagementLine.getProposedStorageBin(), dbOrderManagementLine.getManufacturerName());

                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) - (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                            if (ALLOC_QTY < 0) {
                                ALLOC_QTY = 0D;
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setAllocatedQuantity(ALLOC_QTY);
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            newInventoryV2.setInventoryId(System.currentTimeMillis());
                            InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                            log.info("InventoryV2 created : " + updateInventoryV2);
                        }
                    }
                }
            }
        }
        //Quality Header
        List<QualityHeaderV2> qualityHeaderV2 = qualityHeaderService.deleteQualityHeaderV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("QualityHeader Deleted SuccessFully" + qualityHeaderV2);

        //Quality Line
        List<QualityLineV2> qualityLineV2 = qualityLineService.deleteQualityLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("QualityLine Deleted SuccessFully" + qualityLineV2);

        //Delete InventoryMovement
        List<InventoryMovement> inventoryMovement = inventoryMovementService.deleteInventoryMovement(warehouseId, companyCodeId, plantId, languageId, oldPickListNumber, preOutboundNo, loginUserID);
        log.info("InventoryMovement Deleted SuccessFully" + inventoryMovement);

        return preOutboundHeader;
    }
}