package com.tekclover.wms.api.inbound.orders.service;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.BomHeader;
import com.tekclover.wms.api.inbound.orders.model.dto.BomLine;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.dto.ImPartner;
import com.tekclover.wms.api.inbound.orders.model.dto.InboundOrderProcess;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationLine;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationLog;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService extends BaseService {

    @Autowired
    private InboundOrderV2Repository inboundOrderV2Repository;
    @Autowired
    private PreInboundHeaderV2Repository preInboundHeaderV2Repository;
    @Autowired
    private AuthTokenService authTokenService;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private MastersService mastersService;
    @Autowired
    private ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    private PreInboundLineV2Repository preInboundLineV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    private InboundHeaderV2Repository inboundHeaderV2Repository;
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    private StagingHeaderV2Repository stagingHeaderV2Repository;
    @Autowired
    private StagingLineService stagingLineService;
    @Autowired
    private ImBasicData1Repository imBasicData1Repository;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;
    protected String MW_AMS = "MW_AMS";
    protected String WMS_KNOWELL = "KNOWELLADMIN";
    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    InboundIntegrationLogRepository inboundIntegrationLogRepository;
    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;
    @Autowired
    GrLineService grLineService;

    private final RepositoryProvider repo;
    @Autowired
    ImBasicData1Service imBasicData1Service;
    @Autowired
    OrderProcessingService orderProcessingService;
    String statusDescription = null;
    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;



    //------------------------------------------------------------------------------------------------

    /**
     * @param orderId
     * @return
     */
    public InboundOrderV2 getOrderByIdV2(String orderId, Long inboundOrderTypeId) {
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(orderId, inboundOrderTypeId);
        if (dbInboundOrder != null) {
            return dbInboundOrder;
        } else {
            return null;
        }
    }

    public InboundOrderV2 getOrder(String orderId) {
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.findByRefDocumentNo(orderId);
        if (dbInboundOrder != null) {
            return dbInboundOrder;
        } else {
            return null;
        }
    }

    public InboundOrderV2 updateProcessedInboundOrderV3(String orderId, Long inboundOrderTypeId, Long processStatusId) throws ParseException {
//        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
//        log.info("orderId : " + orderId);
//        log.info("dbInboundOrder : " + dbInboundOrder);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        InboundOrderV2 dbInboundOrder1 = getOrder(orderId);
        dbInboundOrder1.setProcessedStatusId(processStatusId);
        dbInboundOrder1.setOrderProcessedOn(new Date());
        inboundOrderV2Repository.save(dbInboundOrder1);
//        DataBaseContextHolder.clear();
//        try {
//            String routingDb = dbConfigRepository.getDbName(dbInboundOrder.getCompanyCode(), dbInboundOrder.getBranchCode(), dbInboundOrder.getWarehouseID());
//            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//            DataBaseContextHolder.setCurrentDb(routingDb);
//            inboundOrderV2Repository.updateIbOrderStatus(dbInboundOrder1.getCompanyCode(), dbInboundOrder1.getBranchCode(), dbInboundOrder1.getWarehouseID(),
//                    dbInboundOrder1.getRefDocumentNo(), processStatusId);
//        } finally {
//            DataBaseContextHolder.clear();
//        }
        return dbInboundOrder1;
    }

    /**
     * @param orderId
     * @return
     */
    public InboundOrderV2 updateProcessedInboundOrderV2(String orderId, Long inboundOrderTypeId, Long processStatusId) throws ParseException {
        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
        log.info("orderId : " + orderId);
        log.info("dbInboundOrder : " + dbInboundOrder);
        DataBaseContextHolder.setCurrentDb("MT");
        InboundOrderV2 dbInboundOrder1 = getOrder(orderId);
        dbInboundOrder1.setProcessedStatusId(processStatusId);
        dbInboundOrder1.setOrderProcessedOn(new Date());
        inboundOrderV2Repository.save(dbInboundOrder1);
        DataBaseContextHolder.clear();
        try {
            String routingDb = dbConfigRepository.getDbName(dbInboundOrder.getCompanyCode(), dbInboundOrder.getBranchCode(), dbInboundOrder.getWarehouseID());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.setCurrentDb(routingDb);
            inboundOrderV2Repository.updateIbOrderStatus(dbInboundOrder1.getCompanyCode(), dbInboundOrder1.getBranchCode(), dbInboundOrder1.getWarehouseID(),
                    dbInboundOrder1.getRefDocumentNo(), processStatusId);
        } finally {
            DataBaseContextHolder.clear();
        }
        return dbInboundOrder;
    }

    /**
     *
     * @param orderId
     * @param inboundOrderTypeId
     * @param processStatusId
     * @return
     * @throws ParseException
     */
    public void updateProcessStatus(String orderId, Long inboundOrderTypeId, Long processStatusId) throws ParseException {
        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
        log.info("orderId : " + orderId);
        log.info("dbInboundOrder : " + dbInboundOrder);
        InboundOrderV2 dbInboundOrder1 = getOrder(orderId);
        dbInboundOrder1.setProcessedStatusId(processStatusId);
        dbInboundOrder1.setOrderProcessedOn(new Date());
        inboundOrderV2Repository.save(dbInboundOrder1);
//        return dbInboundOrder;
    }


    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public InboundHeaderV2 processInboundReceivedV2(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException,
            SQLException, SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            // PreInbound_header
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
//            orderService.updateProcessedInboundOrderV2(refDocNumber, 100L);
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

//			String warehouseId = inboundIntegrationHeader.getWarehouseID();
//			log.info("warehouseId : " + warehouseId);

            // Fetch ITM_CODE inserted in INBOUNDINTEGRATION table and pass the ITM_CODE in IMBASICDATA1 table and
            // validate the ITM_CODE result is Not Null
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//        log.info("authTokenForMastersService : " + authTokenForMastersService);
//			InboundOrderV2 inboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
//			log.info("inboundOrder : " + inboundOrder);

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(inboundIntegrationHeader.getCompanyCode(), inboundIntegrationHeader.getBranchCode(), "EN", 0L);
            String warehouseId = dbWarehouse.get().getWarehouseId();
            String languageId = dbWarehouse.get().getLanguageId();
            String companyCode = dbWarehouse.get().getCompanyCodeId();
            String plantId = dbWarehouse.get().getPlantId();
            log.info("Warehouse ID: {}", warehouseId);

            // Description_Set
            IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = getPreInboundNo(warehouseId, companyCode, plantId, languageId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);
                ImBasicData1V2 imBasicData1 =
                        imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                                languageId,
                                companyCode,
                                plantId,
                                warehouseId,
                                inboundIntegrationLine.getItemCode(),
                                inboundIntegrationLine.getManufacturerName(),
                                0L);
                log.info("imBasicData1 exists: " + imBasicData1);

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    imBasicData1.setLanguageId(languageId);                                            // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                    // WH_ID
                    imBasicData1.setCompanyCodeId(companyCode);                    // C_ID
                    imBasicData1.setPlantId(plantId);                            // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                    // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());            // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);

//                } else {
//                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());        // MFR_PART
//                }
                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 =
                            mastersService.createImBasicData1V2(imBasicData1, "MW_AMS", authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */
                BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId,
                        companyCode, plantId, languageId,
                        authTokenForMastersService.getAccess_token());
                log.info("bomHeader [BOM] : " + bomHeader);
                if (bomHeader != null) {
                    BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), bomHeader.getWarehouseId(),
                            authTokenForMastersService.getAccess_token());
                    List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
                    for (BomLine dbBomLine : bomLine) {
                        PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(warehouseId, plantId, warehouseId, languageId,
                                preInboundNo, inboundIntegrationHeader, dbBomLine, inboundIntegrationLine);
                        log.info("preInboundLineEntity [BOM] : " + preInboundLineEntity);
                        toBeCreatedPreInboundLineList.add(preInboundLineEntity);
                    }

                    // Batch Insert - PreInboundLines
                    if (!toBeCreatedPreInboundLineList.isEmpty()) {
                        List<PreInboundLineEntityV2> createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                        log.info("createdPreInboundLine [BOM] : " + createdPreInboundLine);
                        overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
                    }
                }
            }

            /*
             * Append PREINBOUNDLINE table through below logic
             */
            List<PreInboundLineEntityV2> toBeCreatedPreInboundLineList = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                toBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCode, plantId, warehouseId, languageId,
                        preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
            }

            log.info("toBeCreatedPreInboundLineList [API] : " + toBeCreatedPreInboundLineList);

            // Batch Insert - PreInboundLines
            List<PreInboundLineEntityV2> createdPreInboundLine = new ArrayList<>();
            if (!toBeCreatedPreInboundLineList.isEmpty()) {
                createdPreInboundLine = preInboundLineV2Repository.saveAll(toBeCreatedPreInboundLineList);
                log.info("createdPreInboundLine [API] : " + createdPreInboundLine);
                overallCreatedPreInboundLineList.addAll(createdPreInboundLine);
            }

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createdPreInboundHeader = createPreInboundHeaderV2(companyCode, plantId, warehouseId, languageId, preInboundNo, inboundIntegrationHeader);
            log.info("preInboundHeader Created : " + createdPreInboundHeader);

            /*------------------Insert into Inbound Header And Line----------------------------*/
            InboundHeaderV2 createdInboundHeader = createInboundHeaderAndLineV2(createdPreInboundHeader, overallCreatedPreInboundLineList);

            // process ASN
//        StagingHeaderV2 stagingHeader = processASNV2(createdPreInboundLine, createdInboundHeader.getCreatedBy());
            StagingHeaderV2 stagingHeader = processNewASNV2(createdPreInboundHeader, createdInboundHeader.getCreatedBy());
            log.info("StagingHeader Created : " + stagingHeader);

            List<StagingLineEntityV2> stagingLines =
                    stagingLineService.createStagingLineV2(createdPreInboundLine, stagingHeader.getStagingNo(), stagingHeader.getWarehouseId(),
                            stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getLanguageId(),
                            createdInboundHeader.getCreatedBy());
            log.info("StagingLines Created : " + stagingLines);

            return createdInboundHeader;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Inbound Order Processing Bad Request Exception : " + e);
        }
    }


    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            // save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);

            //Checking whether received refDocNumber processed already.
//            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
//                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }


            //----------
            List<PutAwayLineV2> putAwayLine = putAwayLineV2Repository.getPutAwayLinesV4(companyCodeId, plantId, languageId, warehouseId, refDocNumber);

            if (putAwayLine.isEmpty()){
                log.info("Already Exits The Order -------------------------------V4 ----------> {} ", refDocNumber);
                inboundOrderCancellationV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
            }else {
                throw new BadRequestException("Inbound already confirmed for this order : "+refDocNumber);
            }

            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
            String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            String partBarCode = generateBarCodeId(refDocNumber);

            log.info("-----inboundIntegrationHeader--------> " + inboundIntegrationHeader.getInboundIntegrationLine());
            long lineNumber = 1;
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId,
                        inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (imBasicData1 != null) {
                    if (inboundIntegrationLine.getItemText() == null) {
                        inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                    }
                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
                    inboundIntegrationLine.setSize(imBasicData1.getSize());
                    if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                        inboundIntegrationLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemType()));
                    } else {
                        inboundIntegrationLine.setItemType(imBasicData1.getItemTypeDescription());
                    }
                    if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                        inboundIntegrationLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemGroup()));
                    } else {
                        inboundIntegrationLine.setItemGroup(imBasicData1.getItemGroupDescription());
                    }
                }

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    imBasicData1.setLanguageId(languageId);                                         // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
                    imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
                    imBasicData1.setPlantId(plantId);                                               // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                         // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerCode(inboundIntegrationLine.getManufacturerCode());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);
                    imBasicData1.setCompanyDescription(description.getCompanyDesc());
                    imBasicData1.setPlantDescription(description.getPlantDesc());
                    imBasicData1.setWarehouseDescription(description.getWarehouseDesc());

                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 = imBasicData1Service.createImBasicData1V2(imBasicData1, MW_AMS);
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */

                log.info("noOfBags in inboundIntegrationLine ----------> {}", inboundIntegrationLine.getNoBags());
                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
                for (long i = 1; i <= noOfBags; i++) {
                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    String barcodeId = "";
                    try {
                        if (inboundIntegrationLine.getSupplierCode() != null && inboundIntegrationLine.getSupplierCode().equalsIgnoreCase("EVEREST FOOD PRODUCTS PVT LTD")) {
                            //barcodeId = null;
                            barcodeId = "10000" + inboundIntegrationLine.getItemCode();
                        } else {
                            barcodeId = generateBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
                        }
                        barcodeId = barcodeId + lineNumber;
                        newInboundIntegrationLine.setBarcodeId(barcodeId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to generate barcode for item code: "
                                + inboundIntegrationLine.getItemCode(), e);
                    }
                    newInboundIntegrationLine.setLineReference(lineNumber);
                    newInboundIntegrationLine.setNoBags(1D);
                    newInboundIntegrationLine.setOrderedQty(inboundIntegrationLine.getOrderedQty());
                    lineNumber++;
                    inboundIntegrationLines.add(newInboundIntegrationLine);
                }
            }


            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                    inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);

            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundHeader(createInboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);
            inboundOrderProcess.setStagingHeader(stagingHeader);

            //Gr Header Creation
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, languageId);
            inboundOrderProcess.setGrHeader(createGrHeader);



            /*
             * Append PREINBOUNDLINE table through below logic
             */
//            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
//            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
//                try {
//                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
//                            description, statusId, statusDescription));
//                } catch (Exception e) {
//                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
//                }
//            });
//            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
//            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
//                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
//                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
//            }
//            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();

            for (InboundIntegrationLine line : inboundIntegrationLines) {
                futures.add(executor.submit(() -> {
                    return createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, line, line.getManufacturerCode(),
                            description, statusId, statusDescription);
                }));
            }

            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            for (Future<PreInboundLineEntityV2> future : futures) {
                try {
                    finalToBeCreatedPreInboundLineList.add(future.get());
                } catch (Exception e) {
                    log.error("Exception in thread: ", e);
                }
            }
            executor.shutdown();

            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setDescription(preInboundLine.getItemDescription());
                log.info("Description ------> {}", inboundLine.getDescription());
                return inboundLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setInboundLines(inboundLines);

            statusDescription = getStatusDescription(14L, languageId);
            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setStatusId(14L);
                stagingLine.setStatusDescription(statusDescription);
                stagingLine.setGoodsReceiptNo(grNumber);

                // Cross_Dock_logic_started
                try {
                    log.info("Cross Dock logic started");
                    log.info("The stagingLine inputs : companyCode --> " + stagingLine.getCompanyCode() + " and plantId --> " + stagingLine.getPlantId() + " and wareHouseId --> " + stagingLine.getWarehouseId() + " and itemCode --> " + stagingLine.getItemCode());
                    Optional<OrderManagementLineV2> crossDock = orderManagementLineV2Repository.getOrderManagementLineForCrossDock(
                            stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getLanguageId(), stagingLine.getWarehouseId(), stagingLine.getItemCode());
                    log.info("Cross Dock Value is " + crossDock);
                    if (crossDock.isPresent()) {
                        stagingLine.setCrossDock(true);
                    } else {
                        stagingLine.setCrossDock(false);
                    }
                } catch (Exception e) {
                    log.info("Cross Dock Failed " + e);
                }

                return stagingLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setStagingLines(stagingLines);
            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e.toString());
            throw e;
        }
    }


    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV7(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                WMS_KNOWELL = inboundIntegrationHeader.getLoginUserId();
            }

            // save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(WMS_KNOWELL);

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();

            String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            log.info("NumberRange input: CompanyCode ---> " + companyCodeId + ", plantId ---> " + plantId + ", languageId ---> " + languageId + ", warehouseId ---> " + warehouseId);
            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            long lineNumber = 1;
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId,
                        inboundIntegrationLine.getItemCode(), inboundIntegrationLine.getManufacturerName(), 0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (imBasicData1 != null) {
                    if (inboundIntegrationLine.getItemText() == null) {
                        inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                    }
                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
                    inboundIntegrationLine.setSize(imBasicData1.getSize());

                    inboundIntegrationLine.setManufacturerCode(imBasicData1.getManufacturerCode());
                    inboundIntegrationLine.setManufacturerName(imBasicData1.getManufacturerName());
                    inboundIntegrationLine.setManufacturerFullName(imBasicData1.getManufacturerFullName());
                }
                if (imBasicData1 == null) {
                    // IB_Order
                    String errorText = "The given values: manufacturerName:" + inboundIntegrationLine.getManufacturerName() +
                            ",ItemCode: " + inboundIntegrationLine.getItemCode() + " doesn't exist.";
                    inboundOrderV2Repository.invalidItemCodeMsgThrow(inboundIntegrationHeader.getRefDocumentNo(), errorText);
                    log.info("Update Inbound Order Update Successfully");

                    throw new BadRequestException("The given values: manufacturerName:" + inboundIntegrationLine.getManufacturerName() +
                            ",ItemCode: " + inboundIntegrationLine.getItemCode() + " doesn't exist.");
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */

                log.info("noOfBags in inboundIntegrationLine ----------> {}", inboundIntegrationLine.getNoBags());
                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
                for (long i = 1; i <= noOfBags; i++) {
                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    String barcodeId = "";
                    try {
                        if (inboundIntegrationLine.getSupplierCode() != null && inboundIntegrationLine.getSupplierCode().equalsIgnoreCase("Everest Pvt Ltd")) {
                            barcodeId = "10000" + inboundIntegrationLine.getItemCode();
                        } else {
//                            barcodeId = generateKnowellBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
                            barcodeId = generateKnowellBarCodeId(companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
                        }
                        newInboundIntegrationLine.setBarcodeId(barcodeId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to generate barcode for item code: "
                                + inboundIntegrationLine.getItemCode(), e);
                    }
                    newInboundIntegrationLine.setLineReference(lineNumber);
                    newInboundIntegrationLine.setNoBags(1D);
                    newInboundIntegrationLine.setOrderedQty(inboundIntegrationLine.getOrderedQty());

                    lineNumber++;
                    inboundIntegrationLines.add(newInboundIntegrationLine);
                }
            }

            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                    inboundIntegrationHeader, WMS_KNOWELL, description, statusId, statusDescription);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);

            /*------------------Insert into Inbound Header----------------------------*/
            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundHeader(createInboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);
            inboundOrderProcess.setStagingHeader(stagingHeader);

            //Gr Header Creation
            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, languageId);
            inboundOrderProcess.setGrHeader(createGrHeader);


//            /*
//             * Append PREINBOUNDLINE table through below logic
//             */
//            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
//            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
//                try {
//                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV3(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                            inboundIntegrationHeader, inboundIntegrationLine, inboundIntegrationLine.getManufacturerCode(),
//                            description, statusId, statusDescription));
//                } catch (Exception e) {
//                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
//                }
//            });
//            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());
//
//            // Batch Insert - PreInboundLines
//            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
//                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
//                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
//            }
//            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
//
            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();

            for (InboundIntegrationLine line : inboundIntegrationLines) {
                futures.add(executor.submit(() -> {
                    return createPreInboundLineV3(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, line, line.getManufacturerCode(),
                            description, statusId, statusDescription);
                }));
            }

            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            for (Future<PreInboundLineEntityV2> future : futures) {
                try {
                    finalToBeCreatedPreInboundLineList.add(future.get());
                } catch (Exception e) {
                    log.error("Exception in thread: ", e);
                }
            }
            executor.shutdown();

            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList.size());
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);


            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setDescription(preInboundLine.getItemDescription());
                return inboundLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setInboundLines(inboundLines);

            statusDescription = getStatusDescription(14L, languageId);
            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setStatusId(14L);
                stagingLine.setStatusDescription(statusDescription);
                stagingLine.setGoodsReceiptNo(grNumber);

                return stagingLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setStagingLines(stagingLines);

            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e.toString());
            throw e;
        }
    }

    /**
     * @return
     */
    private String getPreInboundNo(String warehouseId, String companyCodeId, String plantId, String languageId) {
        /*
         * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE = 2 values in NUMBERRANGE table and
         * fetch NUM_RAN_CURRENT value of FISCALYEAR = CURRENT YEAR and add +1and then
         * update in PREINBOUNDHEADER table
         */
        try {
            AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
            String nextRangeNumber = mastersService.getNextNumberRange(2L, warehouseId, companyCodeId, plantId, languageId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }


    /**
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode, String plantId, String warehouseId, String languageId, String preInboundNo,
                                                          InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine) throws ParseException {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
        BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
        preInboundLine.setCustomerCode(inboundIntegrationHeader.getCustomerCode());
        preInboundLine.setTransferRequestType(inboundIntegrationHeader.getTransferRequestType());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

        // ITM_CODE
        preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        try {
            ImBasicData1 imBasicData1 =
                    imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                            languageId, companyCode, plantId, warehouseId,
                            inboundIntegrationLine.getItemCode(),
                            inboundIntegrationLine.getManufacturerName(),
                            0L);
            preInboundLine.setItemDescription(imBasicData1.getDescription());
        } catch (Exception e) {
            log.info("Item Description Exception");
        }

        // MFR_PART
        preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());

        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

        // ORD_QTY
        preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());

        // ORD_UOM
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
        log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
        preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());

        // REF_FIELD_4
        preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

        // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
        // STATUS_ID
//        preInboundLine.setStatusId(6L);
        preInboundLine.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
        preInboundLine.setCompanyDescription(description.getCompanyDesc());
        preInboundLine.setPlantDescription(description.getPlantDesc());
        preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
        preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
        preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
        preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
        preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
        preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

        preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
        preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
        preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
        preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
        preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());

        preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
        preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
        preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param loginUserId
     * @param description
     * @param statusDesc
     * @return
     * @throws Exception
     */
    private PreInboundLineEntityV2 createPreInboundLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                          String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine, String loginUserId,
                                                          IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());

            // ITM_CODE
            preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

            // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());

            // MFR_PART
            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());

            // PARTNER_CODE
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // ORD_QTY
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // EA_DATE
            log.info("inboundIntegrationLine.getExpectedDate() : {}", inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // ITM_CASE_QTY
            preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());

            if (inboundIntegrationLine.getCustomerId() != null) {
                preInboundLine.setReferenceField6(inboundIntegrationLine.getCustomerId());
            }
            if (inboundIntegrationLine.getCustomerName() != null) {
                preInboundLine.setReferenceField7(inboundIntegrationLine.getCustomerName());
            }

//            if (preInboundLine.getBarcodeId() == null) {
//                //Barcode
//                List<String> barcode = getBarCodeId(companyCodeId, plantId, languageId, warehouseId,
//                        preInboundLine.getItemCode(), preInboundLine.getManufacturerName());
//                log.info("Barcode : " + barcode);
//                if (barcode != null && !barcode.isEmpty()) {
//                    preInboundLine.setBarcodeId(barcode.get(0));
//                }
//            }

            //-----------------PROP_ST_BIN---------------------------------------------
//            String storageBin = validateStorageBin(companyCodeId, plantId, languageId, warehouseId, inboundIntegrationLine.getBinLocation(), preInboundLine);
//            preInboundLine.setReferenceField5(storageBin);
            preInboundLine.setReferenceField5(inboundIntegrationLine.getBinLocation());
            preInboundLine.setManufacturerDate(inboundIntegrationLine.getManufacturerDate());
            preInboundLine.setVehicleNo(inboundIntegrationLine.getVehicleNo());
            preInboundLine.setVehicleUnloadingDate(inboundIntegrationLine.getVehicleUnloadingDate());
            preInboundLine.setVehicleReportingDate(inboundIntegrationLine.getVehicleReportingDate());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());

            if (preInboundLine.getOrderUom().equalsIgnoreCase("Crate") && preInboundLine.getInboundOrderTypeId() == 11L) {
                preInboundLine.setQtyInCreate(preInboundLine.getOrderQty());
            } else {
                log.info("Quantity Logic started-------------->");
                setAlternateUomQuantities(preInboundLine);
                log.info("Quantity Logic Completed-------------->");
            }
            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }

    /**
     * @param preInboundLineEntityV2
     */
    private void setAlternateUomQuantities(PreInboundLineEntityV2 preInboundLineEntityV2) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = preInboundLineEntityV2.getOrderUom();
            String companyCodeId = preInboundLineEntityV2.getCompanyCode();
            String plantId = preInboundLineEntityV2.getPlantId();
            String warehouseId = preInboundLineEntityV2.getWarehouseId();
            String itemCode = preInboundLineEntityV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = preInboundLineEntityV2.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", preInboundLineEntityV2.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (preInboundLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = preInboundLineEntityV2.getOrderQty() / caseQty.getUomQty();
                }

                if (preInboundLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = preInboundLineEntityV2.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = preInboundLineEntityV2.getOrderQty();

                log.info("Case Qty --- {}", preInboundLineEntityV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (preInboundLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = preInboundLineEntityV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (preInboundLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CRATE");
                qtyInCreate = preInboundLineEntityV2.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("Crate Qty --- {}", preInboundLineEntityV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Case Qty ALT_UOM: {}", caseQty);

                if (preInboundLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = preInboundLineEntityV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (preInboundLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
//                    qtyInCase = preInboundLineEntityV2.getOrderQty() / createQty.getUomQty();
                    qtyInCase = qtyInPiece / caseQty.getUomQty();
                    log.info("Case Qty ----->" + qtyInCase);
                }
            }

            preInboundLineEntityV2.setQtyInPiece(qtyInPiece);
            preInboundLineEntityV2.setQtyInCase(qtyInCase);
            preInboundLineEntityV2.setQtyInCreate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param MW_AMS
     * @param description
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                         InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
                                                         IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));
            preInboundLine.setSpecialStockIndicatorId(1L);
            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            // IB_Order
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundLine.getInboundOrderTypeId(), preInboundLine.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");


            InboundLineV2 inboundLine = new InboundLineV2();
            BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
            inboundLine.setDescription(preInboundLine.getItemDescription());
            inboundLineV2Repository.save(inboundLine);
            log.info("InboundLine Saved Successfully");

            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
            throw e;
        }
    }

    public PreInboundLineEntityV2 createPreInboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                         InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
                                                         IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));
            preInboundLine.setSpecialStockIndicatorId(1L);
            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getBranchCode());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            // IB_Order
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundLine.getInboundOrderTypeId(), preInboundLine.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");

            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
            throw e;
        }
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param loginUserId
     * @return
     * @throws Exception
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV5(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                              String loginUserId, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setCompanyCode(companyCodeId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundHeader.setStatusId(statusId);
            preInboundHeader.setStatusDescription(statusDesc);
            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
            preInboundHeader.setPlantDescription(description.getPlantDesc());
            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());
            if (inboundIntegrationHeader.getCustomerId() != null) {
                preInboundHeader.setCustomerId(inboundIntegrationHeader.getCustomerId());
            }
            if (inboundIntegrationHeader.getCustomerName() != null) {
                preInboundHeader.setCustomerName(inboundIntegrationHeader.getCustomerName());
            }

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy(loginUserId);
            preInboundHeader.setCreatedOn(new Date());
            log.info("createdPreInboundHeader : " + preInboundHeader);

            // PreInbound_Header
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update PreInbound Header Update Successfully");

            return preInboundHeader;
        } catch (Exception e) {
            log.error("PreInboundHeader Create Exception : " + e);
            throw e;
        }
    }


    /**
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCode, String plantId, String warehouseId, String languageId,
                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader) throws ParseException {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
        preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
        preInboundHeader.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundHeader.setCompanyCode(companyCode);
        preInboundHeader.setPlantId(plantId);
        preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
        preInboundHeader.setCustomerCode(inboundIntegrationHeader.getCustomerCode());
        preInboundHeader.setTransferRequestType(inboundIntegrationHeader.getTransferRequestType());
        // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//        preInboundHeader.setStatusId(6L);
        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundHeader.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                languageId, plantId, warehouseId);

        preInboundHeader.setCompanyDescription(description.getCompanyDesc());
        preInboundHeader.setPlantDescription(description.getPlantDesc());
        preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

        preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
        preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
//        preInboundHeader.setManufacturerFullName(inboundIntegrationHeader.getManufacturerFullName());
        preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

        preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
        preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
        preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
        preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
        preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
        preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

        preInboundHeader.setDeletionIndicator(0L);
        preInboundHeader.setCreatedBy("MW_AMS");
        preInboundHeader.setCreatedOn(new Date());
        PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
    }

    public PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                             String MW_AMS, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            log.info("PreInboundHeader Create Started -------------------------> ");
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setCompanyCode(companyCodeId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundHeader.setStatusId(statusId);
            preInboundHeader.setStatusDescription(statusDesc);
            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
            preInboundHeader.setPlantDescription(description.getPlantDesc());
            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy(MW_AMS);
            preInboundHeader.setCreatedOn(new Date());

            log.info("createdPreInboundHeader : " + preInboundHeader);

            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");

            return preInboundHeader;
        } catch (Exception e) {
            log.error("PreInboundHeader Create Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    private InboundHeaderV2 createInboundHeaderAndLineV2(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) {
        InboundHeaderV2 inboundHeader = new InboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//        inboundHeader.setStatusId(6L);
        inboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
        inboundHeader.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(preInboundHeader.getCompanyCode(),
                preInboundHeader.getLanguageId(),
                preInboundHeader.getPlantId(),
                preInboundHeader.getWarehouseId());

        inboundHeader.setCompanyDescription(description.getCompanyDesc());
        inboundHeader.setPlantDescription(description.getPlantDesc());
        inboundHeader.setWarehouseDescription(description.getWarehouseDesc());

        inboundHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
        inboundHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
        inboundHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
        inboundHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());
        inboundHeader.setContainerNo(preInboundHeader.getContainerNo());

        inboundHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
        inboundHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
        inboundHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
        inboundHeader.setIsCompleted(preInboundHeader.getIsCompleted());
        inboundHeader.setIsCancelled(preInboundHeader.getIsCancelled());
        inboundHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());
        inboundHeader.setCountOfOrderLines((long) preInboundLine.size());       //count of lines

        inboundHeader.setDeletionIndicator(0L);
        inboundHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        inboundHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        InboundHeaderV2 createdInboundHeader = inboundHeaderV2Repository.save(inboundHeader);
        log.info("createdInboundHeader : " + createdInboundHeader);

        /*
         * Inbound Line Table Insert
         */
        List<InboundLineV2> toBeCreatedInboundLineList = new ArrayList<>();
        for (PreInboundLineEntityV2 createdPreInboundLine : preInboundLine) {
            InboundLineV2 inboundLine = new InboundLineV2();
            BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

            inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
            inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());
            inboundLine.setDescription(createdPreInboundLine.getItemDescription());
            inboundLine.setVendorCode(createdPreInboundLine.getBusinessPartnerCode());
            inboundLine.setReferenceField4(createdPreInboundLine.getReferenceField4());

            inboundLine.setCompanyDescription(description.getCompanyDesc());
            inboundLine.setPlantDescription(description.getPlantDesc());
            inboundLine.setWarehouseDescription(description.getWarehouseDesc());
            inboundLine.setStatusDescription(statusDescription);
            inboundLine.setContainerNo(createdPreInboundLine.getContainerNo());
            inboundLine.setSupplierName(createdPreInboundLine.getSupplierName());

            inboundLine.setMiddlewareId(createdPreInboundLine.getMiddlewareId());
            inboundLine.setMiddlewareHeaderId(createdPreInboundLine.getMiddlewareHeaderId());
            inboundLine.setMiddlewareTable(createdPreInboundLine.getMiddlewareTable());
            inboundLine.setReferenceDocumentType(createdInboundHeader.getReferenceDocumentType());
            inboundLine.setManufacturerFullName(createdPreInboundLine.getManufacturerFullName());
            inboundLine.setPurchaseOrderNumber(createdPreInboundLine.getPurchaseOrderNumber());

            inboundLine.setManufacturerCode(createdPreInboundLine.getManufacturerName());
            inboundLine.setManufacturerName(createdPreInboundLine.getManufacturerName());
            inboundLine.setExpectedArrivalDate(createdPreInboundLine.getExpectedArrivalDate());
            inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
            inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());

            inboundLine.setVendorCode(createdPreInboundLine.getBusinessPartnerCode());
            inboundLine.setManufacturerPartNo(createdPreInboundLine.getManufacturerPartNo());

            inboundLine.setBranchCode(createdPreInboundLine.getBranchCode());
            inboundLine.setTransferOrderNo(createdPreInboundLine.getTransferOrderNo());
            inboundLine.setIsCompleted(createdPreInboundLine.getIsCompleted());

            inboundLine.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
            inboundLine.setSourceBranchCode(preInboundHeader.getSourceBranchCode());

            inboundLine.setDeletionIndicator(0L);
            inboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
            inboundLine.setCreatedOn(preInboundHeader.getCreatedOn());
            toBeCreatedInboundLineList.add(inboundLine);
        }

        List<InboundLineV2> createdInboundLine = inboundLineV2Repository.saveAll(toBeCreatedInboundLineList);
        log.info("createdInboundLine : " + createdInboundLine);

        return createdInboundHeader;
    }

    /**
     * To avoid Deadlock
     *
     * @param preInboundHeader
     * @param loginUserID
     * @return
     */
    public StagingHeaderV2 processNewASNV2(PreInboundHeaderEntityV2 preInboundHeader, String loginUserID) {

        StagingHeaderV2 stagingHeader = new StagingHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // STG_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        long NUMBER_RANGE_CODE = 3;
        String nextRangeNumber = mastersService.getNextNumberRange(NUMBER_RANGE_CODE,
                preInboundHeader.getWarehouseId(), preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(),
                authTokenForIDMasterService.getAccess_token());
        stagingHeader.setStagingNo(nextRangeNumber);

        // GR_MTD
        stagingHeader.setGrMtd("INTEGRATION");

        // STATUS_ID
        stagingHeader.setStatusId(12L);
        statusDescription = stagingLineV2Repository.getStatusDescription(12L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
        IKeyValuePair description = stagingLineV2Repository.getDescription(preInboundHeader.getCompanyCode(),
                preInboundHeader.getLanguageId(),
                preInboundHeader.getPlantId(),
                preInboundHeader.getWarehouseId());

        stagingHeader.setCompanyDescription(description.getCompanyDesc());
        stagingHeader.setPlantDescription(description.getPlantDesc());
        stagingHeader.setWarehouseDescription(description.getWarehouseDesc());

        stagingHeader.setContainerNo(preInboundHeader.getContainerNo());
        stagingHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
        stagingHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
        stagingHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
        stagingHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());

        stagingHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
        stagingHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
        stagingHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
        stagingHeader.setIsCompleted(preInboundHeader.getIsCompleted());
        stagingHeader.setIsCancelled(preInboundHeader.getIsCancelled());
        stagingHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());

        stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        return stagingHeaderV2Repository.save(stagingHeader);
    }


    /**
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param languageId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param bomLine
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(String companyCode, String plantId, String warehouseId, String languageId, String preInboundNo,
                                                                  InboundIntegrationHeader inboundIntegrationHeader, BomLine bomLine,
                                                                  InboundIntegrationLine inboundIntegrationLine) throws ParseException {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
        BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
        preInboundLine.setCustomerCode(inboundIntegrationHeader.getCustomerCode());
        preInboundLine.setTransferRequestType(inboundIntegrationHeader.getTransferRequestType());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(Long.valueOf(inboundIntegrationLine.getLineReference()));

        // ITM_CODE
        preInboundLine.setItemCode(bomLine.getChildItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                mastersService.getImBasicData1ByItemCode(inboundIntegrationLine.getItemCode(), languageId,
                        companyCode, plantId, warehouseId, inboundIntegrationLine.getUom(),
                        authTokenForMastersService.getAccess_token());
        preInboundLine.setItemDescription(imBasicData1.getDescription());

        // MFR
        preInboundLine.setManufacturerPartNo(imBasicData1.getManufacturerPartNo());

        // ORD_QTY
        double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
        preInboundLine.setOrderQty(orderQuantity);

        // ORD_UOM
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

        // EA_DATE
        preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // STATUS_ID
        preInboundLine.setStatusId(6L);

        statusDescription = stagingLineV2Repository.getStatusDescription(6L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                languageId, plantId, warehouseId);

        preInboundLine.setCompanyDescription(description.getCompanyDesc());
        preInboundLine.setPlantDescription(description.getPlantDesc());
        preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

        preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
        preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
        preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
        preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
        preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

        preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
        preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
        preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
        preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
        preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

        preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
        preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
        preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
        preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

        // REF_FIELD_1
        preInboundLine.setReferenceField1("CHILD ITEM");

        // REF_FIELD_2
        preInboundLine.setReferenceField2("BOM ITEM");

        // REF_FIELD_4
        preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());
        return preInboundLine;
    }


    /**
     * @param newInboundOrderV2
     * @return
     */
    public InboundOrderV2 createInboundOrdersV2(InboundOrderV2 newInboundOrderV2) {
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                findByRefDocumentNoAndInboundOrderTypeId(newInboundOrderV2.getOrderId(), newInboundOrderV2.getInboundOrderTypeId());
        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
        return inboundOrderV2;
    }

    /**
     * @param orderV2
     * @return
     */
    public InboundOrderV2 createInboundOrders(InboundOrderV2 orderV2) {
        InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                findByCompanyCodeAndBranchCodeAndWarehouseIDAndRefDocumentNoAndInboundOrderTypeId(
                        orderV2.getCompanyCode(), orderV2.getBranchCode(), orderV2.getWarehouseID(),
                        orderV2.getOrderId(), orderV2.getInboundOrderTypeId());
        if (dbInboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }
        InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(orderV2);
        String routingDb = dbConfigRepository.getDbName(orderV2.getCompanyCode(), orderV2.getBranchCode(), orderV2.getWarehouseID());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        inboundOrderV2Repository.save(orderV2);
        return inboundOrderV2;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param bomLine
     * @param MW_AMS
     * @param description
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineBOMBasedV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                                 InboundIntegrationLine inboundIntegrationLine, BomLine bomLine, String MW_AMS,
                                                                 IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());

            // PRE_IB_NO
            preInboundLine.setPreInboundNo(preInboundNo);

            // IB__LINE_NO
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());

            // ITM_CODE
            preInboundLine.setItemCode(bomLine.getChildItemCode());

            // ITM_TEXT
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());

            // MFR
            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());

            // ORD_QTY
            double orderQuantity = inboundIntegrationLine.getOrderedQty() * bomLine.getChildItemQuantity();
            preInboundLine.setOrderQty(orderQuantity);

            // ORD_UOM
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());

            // EA_DATE
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());

            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));

            // SP_ST_IND_ID
            preInboundLine.setSpecialStockIndicatorId(1L);

            // STATUS_ID
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());

            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());

            // REF_FIELD_1
            preInboundLine.setReferenceField1("CHILD ITEM");

            // REF_FIELD_2
            preInboundLine.setReferenceField2("BOM ITEM");

            // REF_FIELD_4
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());
            log.info("preInboundLine [BOM] : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("Exception while create preInboundLine - BOM : " + e.toString());
            throw e;
        }
    }

    /**
     * @param l
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param accessToken
     * @return
     */
    private String getNextRangeNumber(long l, String companyCode, String plantId, String languageId, String warehouseId, String accessToken) {
        String nextNumberRange = mastersService.getNextNumberRange(l, warehouseId, companyCode, plantId, languageId, accessToken);
        return nextNumberRange;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param loginUserId
     * @return
     */
    private ImPartner createImpartner(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String itemCode, String manufacturerName, String barcodeId, String loginUserId) {
        ImPartner imPartner = new ImPartner();
        imPartner.setCompanyCodeId(companyCodeId);
        imPartner.setPlantId(plantId);
        imPartner.setLanguageId(languageId);
        imPartner.setWarehouseId(warehouseId);
        imPartner.setItemCode(itemCode);
        imPartner.setManufacturerName(manufacturerName);
        imPartner.setManufacturerCode(manufacturerName);
        imPartner.setBusinessPartnerCode(manufacturerName);
        imPartner.setBusinessPartnerType("1");
        imPartner.setPartnerItemBarcode(barcodeId);
        imPartner.setVendorItemBarcode(barcodeId);
        imPartner.setDeletionIndicator(0L);
        imPartner.setCreatedBy(loginUserId);
        imPartner.setUpdatedBy(loginUserId);
        imPartner.setCreatedOn(new Date());
        imPartner.setUpdatedOn(new Date());
        return imPartner;
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    public InboundHeaderV2 createInboundHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines((long) preInboundLine.size());       //count of lines
//            return inboundHeaderV2Repository.save(inboundHeader);
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            // GR_MTD
            stagingHeader.setGrMtd("INTEGRATION");
//            return stagingHeaderV2Repository.save(stagingHeader);

            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(stagingHeader.getInboundOrderTypeId(), stagingHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");

            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @param languageId
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV2(StagingHeaderV2 stagingHeader, String caseCode, String grNumber, String languageId) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(16L);
            grHeader.setStatusDescription(getStatusDescription(16L, languageId));
//            GrHeaderV2 createdGrHeader = grHeaderV2Repository.save(grHeader);

            // Staging_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");

            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }

    //========================================================================V5====================================================================

    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV5(StagingHeaderV2 stagingHeader, String caseCode, String grNumber, Long statusId, String statusDesc) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(statusId);
            grHeader.setStatusDescription(statusDesc);
            if (stagingHeader.getCustomerId() != null) {
                grHeader.setCustomerId(stagingHeader.getCustomerId());
            }
            if (stagingHeader.getCustomerName() != null) {
                grHeader.setCustomerName(stagingHeader.getCustomerName());
            }
            // Gr_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update Gr_Header Update Successfully");

            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e);
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV5(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            if (preInboundHeader.getCustomerId() != null) {
                stagingHeader.setCustomerId(preInboundHeader.getCustomerId());
            }
            if (preInboundHeader.getCustomerName() != null) {
                stagingHeader.setCustomerName(preInboundHeader.getCustomerName());
            }
            // GR_MTD
            stagingHeader.setGrMtd("INTEGRATION");

            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e);
            throw e;
        }
    }

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public synchronized InboundHeaderV2 processInboundReceivedV5(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("Inbound Process Initiated ------> : {}|{}|{}|{}|{}|{}", companyCodeId, plantId, languageId, warehouseId, refDocNumber, inboundOrderTypeId);

        try {
            MW_AMS = inboundIntegrationHeader.getLoginUserId() != null ? inboundIntegrationHeader.getLoginUserId() : MW_AMS;

            //Checking whether received refDocNumber processed already.
            boolean orderProcessedStatus = preInboundHeaderV2Repository.existsByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            log.error("IsDupicate : " + refDocNumber + " |---> " + orderProcessedStatus);
            if (orderProcessedStatus) {
                return new InboundHeaderV2();
            }

            String idMasterAuthToken = getIDMasterAuthToken();
            String masterAuthToken = getMasterAuthToken();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : {}|{}|{}|{}", preInboundNo, stagingNo, caseCode, grNumber);

            statusDescription = getStatusDescription(statusId, languageId);

            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            log.info("Description" + description);


            inboundOrderProcess = createBOMPreInboundLinesv5(companyCodeId, plantId, languageId, warehouseId, preInboundNo, description, masterAuthToken, statusId, statusDescription, inboundIntegrationHeader, MW_AMS);

            List<InboundIntegrationLine> inboundIntegrationLines = inboundOrderProcess.getInboundIntegrationLines();
            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = inboundOrderProcess.getPreInboundLines();

            //Append PREINBOUNDLINE table through below logic
            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
                try {
                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV5(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
                            description, statusId, statusDescription));
                } catch (Exception e) {
                    throw new BadRequestException("Exception While PreInboundLine Create" + e);
                }
            });
            log.info("toBeCreatedPreInboundLineList [API] : {}", finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }

            //Header
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV5(companyCodeId, plantId, languageId, warehouseId, preInboundNo, inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);

            InboundHeaderV2 createInboundHeader = createInboundHeaderV5(createPreInboundHeader, (long) overallCreatedPreInboundLineList.size());

            StagingHeaderV2 stagingHeader = createStagingHeaderV5(createPreInboundHeader, stagingNo);

            statusDescription = getStatusDescription(16L, languageId);
            GrHeaderV2 createGrHeader = createGrHeaderV5(stagingHeader, caseCode, grNumber, 16L, statusDescription);

//            preInboundHeaderV2Repository.saveAndFlush(createPreInboundHeader);
//            stagingHeaderV2Repository.saveAndFlush(stagingHeader);
//            inboundHeaderV2Repository.saveAndFlush(createInboundHeader);
//            grHeaderV2Repository.saveAndFlush(createGrHeader);

            //Lines
            List<InboundLineV2> inboundLines = createInboundLines(16L, statusDescription, overallCreatedPreInboundLineList);
            statusDescription = getStatusDescription(14L, languageId);

            List<StagingLineEntityV2> stagingLines = createStagingLines(stagingNo, caseCode, 14L, statusDescription, overallCreatedPreInboundLineList);

//            preInboundLineV2Repository.saveAll(overallCreatedPreInboundLineList);
//            inboundLineV2Repository.saveAll(inboundLines);
//            stagingLineV2Repository.saveAll(stagingLines);

            //Log
            InboundIntegrationLog inboundIntegrationLog = createInboundIntegrationLogV5(createPreInboundHeader);
            inboundIntegrationLogRepository.save(inboundIntegrationLog);

            // db save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);
            inboundOrderProcess.setInboundHeader(createInboundHeader);
            inboundOrderProcess.setStagingHeader(stagingHeader);
            inboundOrderProcess.setGrHeader(createGrHeader);
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundLines(inboundLines);
            inboundOrderProcess.setStagingLines(stagingLines);
            inboundOrderProcess.setInboundIntegrationLog(inboundIntegrationLog);
            InboundHeaderV2 createdInboundHeader = orderProcessingService.postInboundReceivedV5(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

//            createGrLineV3(companyCodeId, plantId, languageId, warehouseId, createGrHeader.getParentProductionOrderNo(), createGrHeader, idMasterAuthToken, MW_AMS);

            return createdInboundHeader;

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e);
            throw e;
        }
    }

    /**
     * @param createdPreInboundHeader
     * @return
     * @throws Exception
     */
    public InboundIntegrationLog createInboundIntegrationLogV5(PreInboundHeaderEntityV2 createdPreInboundHeader) throws Exception {
        try {
            InboundIntegrationLog dbInboundIntegrationLog = new InboundIntegrationLog();
            BeanUtils.copyProperties(createdPreInboundHeader, dbInboundIntegrationLog, CommonUtils.getNullPropertyNames(createdPreInboundHeader));
            dbInboundIntegrationLog.setLanguageId(createdPreInboundHeader.getLanguageId());
            dbInboundIntegrationLog.setCompanyCodeId(createdPreInboundHeader.getCompanyCode());
            dbInboundIntegrationLog.setPlantId(createdPreInboundHeader.getPlantId());
            dbInboundIntegrationLog.setWarehouseId(createdPreInboundHeader.getWarehouseId());
            dbInboundIntegrationLog.setIntegrationLogNumber(createdPreInboundHeader.getPreInboundNo());
            dbInboundIntegrationLog.setRefDocNumber(createdPreInboundHeader.getRefDocNumber());
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setIntegrationStatus("SUCCESS");
            dbInboundIntegrationLog.setOrderReceiptDate(createdPreInboundHeader.getCreatedOn());
            dbInboundIntegrationLog.setDeletionIndicator(0L);
            dbInboundIntegrationLog.setCreatedBy(createdPreInboundHeader.getCreatedBy());
            dbInboundIntegrationLog.setCreatedOn(new Date());
            log.info("dbInboundIntegrationLog : {}", dbInboundIntegrationLog);
            return dbInboundIntegrationLog;
        } catch (Exception e) {
            log.error("InboundIntegrationLog Create[Success] Exception : " + e);
            throw e;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param description
     * @param masterAuthToken
     * @param statusId
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundOrderProcess createBOMPreInboundLinesv5(String companyCodeId, String plantId, String languageId, String warehouseId, String preInboundNo,
                                                          IKeyValuePair description, String masterAuthToken, Long statusId, String statusDescription,
                                                          InboundIntegrationHeader inboundIntegrationHeader, String loginUserId) throws Exception {
        List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
        List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        DataBaseContextHolder.setCurrentDb("REEFERON");
        for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
            ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                    languageId, companyCodeId, plantId, warehouseId,
                    inboundIntegrationLine.getItemCode().trim(), MFR_NAME_V5, 0L);
            log.info("imBasicData1 exists: {}", imBasicData1);

            if (inboundIntegrationLine.getItemText() == null && imBasicData1 != null) {
                inboundIntegrationLine.setItemText(imBasicData1.getDescription());
            }

            // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
            if (imBasicData1 == null) {
                imBasicData1 = new ImBasicData1V2();
                BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                imBasicData1.setLanguageId(languageId);                                         // LANG_ID
                imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
                imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
                imBasicData1.setPlantId(plantId);                                               // PLANT_ID
                imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
                String uomId = inboundIntegrationLine.getUom() != null ? inboundIntegrationLine.getUom() : getUomId(companyCodeId, plantId, languageId, warehouseId);
                imBasicData1.setUomId(uomId);                         // UOM_ID
                imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
                imBasicData1.setManufacturerPartNo(MFR_NAME_V5);
                imBasicData1.setManufacturerName(MFR_NAME_V5);
                imBasicData1.setManufacturerCode(MFR_NAME_V5);
                imBasicData1.setCapacityCheck(false);
                imBasicData1.setDeletionIndicator(0L);
                imBasicData1.setCompanyDescription(description.getCompanyDesc());
                imBasicData1.setPlantDescription(description.getPlantDesc());
                imBasicData1.setWarehouseDescription(description.getWarehouseDesc());

                imBasicData1.setStatusId(1L);                                                // STATUS_ID
                ImBasicData1 createdImBasicData1 = mastersService.createImBasicData1V2(imBasicData1, loginUserId, masterAuthToken);
                log.info("ImBasicData1 created: {}", createdImBasicData1);
            }

            /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
            /*
             * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader table and
             * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
             */
//            BomHeader bomHeader = mastersService.getBomHeader(inboundIntegrationLine.getItemCode(), warehouseId, companyCodeId, plantId, languageId, masterAuthToken);
//            log.info("bomHeader [BOM] : {}", bomHeader);
//            if (bomHeader != null) {
//                BomLine[] bomLine = mastersService.getBomLine(bomHeader.getBomNumber(), companyCodeId, plantId, languageId, warehouseId, masterAuthToken);
//                for (BomLine dbBomLine : bomLine) {
//                    PreInboundLineEntityV2 preInboundLineEntity = createPreInboundLineBOMBasedV2(companyCodeId, plantId, languageId, warehouseId,
//                            preInboundNo, inboundIntegrationHeader, inboundIntegrationLine,
//                            dbBomLine, loginUserId, description, statusId, statusDescription);
//                    overallCreatedPreInboundLineList.add(preInboundLineEntity);
//                }
//            }
            inboundIntegrationLines.add(inboundIntegrationLine);
        }
        log.info("preInboundLineEntity [BOM] : {}", overallCreatedPreInboundLineList.size());
        inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
        inboundOrderProcess.setInboundIntegrationLines(inboundIntegrationLines);
        return inboundOrderProcess;
    }

    /**
     * @param preInboundHeader
     * @param orderLinesCount
     * @return
     * @throws Exception
     */
    private InboundHeaderV2 createInboundHeaderV5(PreInboundHeaderEntityV2 preInboundHeader, Long orderLinesCount) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines(orderLinesCount);       //count of lines
            if (preInboundHeader.getCustomerId() != null) {
                inboundHeader.setCustomerId(preInboundHeader.getCustomerId());
            }
            if (preInboundHeader.getCustomerName() != null) {
                inboundHeader.setCustomerName(preInboundHeader.getCustomerName());
            }
            // Inbound_Header
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e);
            throw e;
        }
    }

    /**
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     * @throws Exception
     */
    public List<InboundLineV2> createInboundLines(Long statusId, String statusDesc, List<PreInboundLineEntityV2> preInboundLines) throws Exception {
        try {
            log.info("Inputs------>" + preInboundLines);
            return preInboundLines.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setStatusId(statusId);
                inboundLine.setStatusDescription(statusDesc);
                inboundLine.setQtyInPiece(preInboundLine.getQtyInPiece());
                inboundLine.setQtyInCreate(preInboundLine.getQtyInCreate());
                inboundLine.setQtyInCase(preInboundLine.getQtyInCase());
                inboundLine.setVehicleNo(preInboundLine.getVehicleNo());
                inboundLine.setVehicleReportingDate(preInboundLine.getVehicleReportingDate());
                inboundLine.setVehicleUnloadingDate(preInboundLine.getVehicleUnloadingDate());
                inboundLine.setManufacturerDate(preInboundLine.getManufacturerDate());
                inboundLine.setDescription(preInboundLine.getItemDescription());
                if (preInboundLine.getReferenceField6() != null) {
                    inboundLine.setReferenceField6(preInboundLine.getReferenceField6());
                }
                if (preInboundLine.getReferenceField7() != null) {
                    inboundLine.setReferenceField7(preInboundLine.getReferenceField7());
                }
                log.info("InboundLines ----->" + inboundLine);
                return inboundLine;
            }).collect(toList());
        } catch (Exception e) {
            log.error("Exception while InboundLines Create : " + e);
            throw e;
        }
    }

    /**
     * @param stagingNo
     * @param caseCode
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     */
    public List<StagingLineEntityV2> createStagingLines(String stagingNo, String caseCode, Long statusId, String statusDesc,
                                                        List<PreInboundLineEntityV2> preInboundLines) {
        try {
            return preInboundLines.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setStatusId(statusId);
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setRec_accept_qty(preInboundLine.getOrderQty());
                stagingLine.setRec_damage_qty(0D);
                stagingLine.setManufacturerDate(preInboundLine.getManufacturerDate());
                stagingLine.setVehicleNo(preInboundLine.getVehicleNo());
                stagingLine.setVehicleUnloadingDate(preInboundLine.getVehicleUnloadingDate());
                stagingLine.setVehicleReportingDate(preInboundLine.getVehicleReportingDate());
                if (preInboundLine.getReferenceField6() != null) {
                    stagingLine.setReferenceField6(preInboundLine.getReferenceField6());
                }
                if (preInboundLine.getReferenceField7() != null) {
                    stagingLine.setReferenceField7(preInboundLine.getReferenceField7());
                }

                if (preInboundLine.getOrderUom().equalsIgnoreCase("Crate") && preInboundLine.getInboundOrderTypeId() == 11L) {
                    stagingLine.setQtyInCreate(preInboundLine.getOrderQty());
                } else {
                    log.info("Quantity Logic started-------------->");
                    setAlternateUomQuantities(stagingLine);
                    log.info("Quantity Logic Completed-------------->");
                }
                stagingLine.setStatusDescription(statusDesc);
                return stagingLine;
            }).collect(toList());
        } catch (Exception e) {
            log.error("Exception while StagingLines Create : " + e);
            throw e;
        }
    }

    /**
     * @param stagingLineEntityV2
     */
    private void setAlternateUomQuantities(StagingLineEntityV2 stagingLineEntityV2) {
        try {
            Double qtyInPiece = null;
            Double qtyInCase = null;
            Double qtyInCreate = null;

            String orderUom = stagingLineEntityV2.getOrderUom();
            String companyCodeId = stagingLineEntityV2.getCompanyCode();
            String plantId = stagingLineEntityV2.getPlantId();
            String warehouseId = stagingLineEntityV2.getWarehouseId();
            String itemCode = stagingLineEntityV2.getItemCode();

            if ("piece".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is PIECE");

                qtyInPiece = stagingLineEntityV2.getOrderQty();
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                log.info("Piece Qty --- {}", stagingLineEntityV2.getOrderQty());
                log.info("Case Qty ALT_UOM: {}", caseQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (stagingLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
                    qtyInCase = stagingLineEntityV2.getOrderQty() / caseQty.getUomQty();
                }

                if (stagingLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = stagingLineEntityV2.getOrderQty() / createQty.getUomQty();
                }

            } else if ("case".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CASE");

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");

                qtyInCase = stagingLineEntityV2.getOrderQty();

                log.info("Case Qty --- {}", stagingLineEntityV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("Create Qty ALT_UOM: {}", createQty);

                if (stagingLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = stagingLineEntityV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (stagingLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
                    qtyInCreate = qtyInPiece / createQty.getUomQty();
                }
            } else if ("crate".equalsIgnoreCase(orderUom)) {
                log.info("OrderUom is CRATE");
                qtyInCreate = stagingLineEntityV2.getOrderQty();

                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");

                log.info("CRATE Qty --- {}", stagingLineEntityV2.getOrderQty());
                log.info("Piece Qty ALT_UOM: {}", pieceQty);
                log.info("CASE Qty ALT_UOM: {}", caseQty);

                if (stagingLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
                    qtyInPiece = stagingLineEntityV2.getOrderQty() * pieceQty.getUomQty();
                }

                if (stagingLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
//                    qtyInCase = stagingLineEntityV2.getOrderQty() / caseQty.getUomQty();
                    qtyInCase = qtyInPiece / caseQty.getUomQty();
                    log.info("Case Qty ----->" + qtyInCase);
                }
            }

            stagingLineEntityV2.setQtyInPiece(qtyInPiece);
            stagingLineEntityV2.setQtyInCase(qtyInCase);
            stagingLineEntityV2.setQtyInCreate(qtyInCreate);
        } catch (Exception e) {
            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
        }
    }

//===================================================BP============================================================================

    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV6(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            // save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }

            List<PutAwayLineV2> putAwayLine = putAwayLineV2Repository.getPutAwayLinesV6(companyCodeId, plantId, languageId, warehouseId, refDocNumber);

            if (putAwayLine.isEmpty()){
                log.info("Already Exits The Order -------------------------------V6 ----------> {} ", refDocNumber);
                inboundOrderCancellationV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
            }else {
                throw new BadRequestException("Inbound already confirmed for this order : "+refDocNumber);
            }
            // Description_Set
            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);
            statusDescription = getStatusDescription(statusId, languageId);

            // Step 1: Create headers before line processing
            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV6(
                    companyCodeId, languageId, plantId, preInboundNo, inboundIntegrationHeader, warehouseId, companyText, plantText, warehouseText, MRF_NAME_V6,inboundIntegrationHeader.getAMSSupplierInvoiceNo());
            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());
            inboundOrderProcess.setPreInboundHeader(preInboundHeader);
            InboundHeaderV2 inboundHeader = createInboundHeaderV6(preInboundHeader, inboundIntegrationHeader.getInboundIntegrationLine().size());
            log.info("Inbound Header Created: {}", inboundHeader);
            inboundOrderProcess.setInboundHeader(inboundHeader);

            StagingHeaderV2 stagingHeader = null;
            GrHeaderV2 grHeader = null;

            if (inboundIntegrationHeader.getInboundOrderTypeId().equals(1L)){
                stagingHeader = createStagingHeaderV6(preInboundHeader, stagingNo);
                log.info("StagingHeader Created: {}", stagingHeader);
                inboundOrderProcess.setStagingHeader(stagingHeader);

                grHeader = createGrHeaderV6(stagingHeader, caseCode, grNumber);
                log.info("GrHeader Created: {}", grHeader);
                inboundOrderProcess.setGrHeader(grHeader);

            }

            String partBarCode = generateBarCodeId(preInboundHeader.getRefDocNumber());
            processSingleASNLineV6(inboundIntegrationHeader, inboundIntegrationHeader.getInboundIntegrationLine(), preInboundHeader, stagingHeader, grHeader, partBarCode,inboundOrderProcess);

            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e.toString());
            throw e;
        }
    }


    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
    public InboundHeaderV2 processInboundReceivedV8(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {

        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
        String plantId = inboundIntegrationHeader.getBranchCode();
        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
        String warehouseId = inboundIntegrationHeader.getWarehouseID();
        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);

        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
            if (inboundIntegrationHeader.getLoginUserId() != null) {
                MW_AMS = inboundIntegrationHeader.getLoginUserId();
            }

            // save/create process
            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
            inboundOrderProcess.setLoginUserId(MW_AMS);

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();

            String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);

            statusDescription = getStatusDescription(statusId, languageId);
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);

            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
            String partBarCode = generateBarCodeId(refDocNumber);

            log.info("-----inboundIntegrationHeader--------> " + inboundIntegrationHeader.getInboundIntegrationLine());
            long lineNumber = 1;
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        languageId, companyCodeId, plantId, warehouseId,
                        inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
                log.info("imBasicData1 exists: " + imBasicData1);
                if (imBasicData1 != null) {
                    if (inboundIntegrationLine.getItemText() == null) {
                        inboundIntegrationLine.setItemText(imBasicData1.getDescription());
                    }
                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
                    inboundIntegrationLine.setSize(imBasicData1.getSize());
                    if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
                        inboundIntegrationLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemType()));
                    } else {
                        inboundIntegrationLine.setItemType(imBasicData1.getItemTypeDescription());
                    }
                    if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
                        inboundIntegrationLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemGroup()));
                    } else {
                        inboundIntegrationLine.setItemGroup(imBasicData1.getItemGroupDescription());
                    }
                }

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    imBasicData1.setLanguageId(languageId);                                         // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
                    imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
                    imBasicData1.setPlantId(plantId);                                               // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                         // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerCode(inboundIntegrationLine.getManufacturerCode());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);
                    imBasicData1.setCompanyDescription(description.getCompanyDesc());
                    imBasicData1.setPlantDescription(description.getPlantDesc());
                    imBasicData1.setWarehouseDescription(description.getWarehouseDesc());

                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 = imBasicData1Service.createImBasicData1V2(imBasicData1, MW_AMS);
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }

                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
                /*
                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
                 */

                log.info("noOfBags in inboundIntegrationLine ----------> {}", inboundIntegrationLine.getNoBags());
                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
                for (long i = 1; i <= noOfBags; i++) {
                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
                    String barcodeId = "";
                    try {
                        if (inboundIntegrationLine.getSupplierCode() != null && inboundIntegrationLine.getSupplierCode().equalsIgnoreCase("EVEREST FOOD PRODUCTS PVT LTD")) {
                            //barcodeId = null;
                            barcodeId = "10000" + inboundIntegrationLine.getItemCode();
                        } else {
                            barcodeId = generateBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
                        }
                        barcodeId = barcodeId + lineNumber;
                        newInboundIntegrationLine.setBarcodeId(barcodeId);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to generate barcode for item code: "
                                + inboundIntegrationLine.getItemCode(), e);
                    }
                    newInboundIntegrationLine.setLineReference(lineNumber);
                    newInboundIntegrationLine.setNoBags(1D);
                    newInboundIntegrationLine.setOrderedQty(inboundIntegrationLine.getOrderedQty());
                    lineNumber++;
                    inboundIntegrationLines.add(newInboundIntegrationLine);
                }
            }


            /*------------------Insert into PreInboundHeader table-----------------------------*/
            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV8(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                    inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);
            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);

            InboundHeaderV2 createInboundHeader = createInboundHeaderV8(createPreInboundHeader, overallCreatedPreInboundLineList);
            inboundOrderProcess.setInboundHeader(createInboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV8(createPreInboundHeader, stagingNo);
            inboundOrderProcess.setStagingHeader(stagingHeader);

            //Gr Header Creation
            GrHeaderV2 createGrHeader = createGrHeaderV8(stagingHeader, caseCode, grNumber, languageId);
            inboundOrderProcess.setGrHeader(createGrHeader);



            /*
             * Append PREINBOUNDLINE table through below logic
             */
//            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
//            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
//                try {
//                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
//                            description, statusId, statusDescription));
//                } catch (Exception e) {
//                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
//                }
//            });
//            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());

            // Batch Insert - PreInboundLines
//            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
//                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
//                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
//            }
//            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

            ExecutorService executor = Executors.newFixedThreadPool(8);
            List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();

            for (InboundIntegrationLine line : inboundIntegrationLines) {
                futures.add(executor.submit(() -> {
                    return createPreInboundLineV8(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
                            inboundIntegrationHeader, line, line.getManufacturerCode(),
                            description, statusId, statusDescription);
                }));
            }

            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
            for (Future<PreInboundLineEntityV2> future : futures) {
                try {
                    finalToBeCreatedPreInboundLineList.add(future.get());
                } catch (Exception e) {
                    log.error("Exception in thread: ", e);
                }
            }
            executor.shutdown();

            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            }
            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setDescription(preInboundLine.getItemDescription());
                log.info("Description ------> {}", inboundLine.getDescription());
                return inboundLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setInboundLines(inboundLines);

            statusDescription = getStatusDescription(14L, languageId);
            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingNo);
                stagingLine.setCaseCode(caseCode);
                stagingLine.setPalletCode(caseCode);
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setStatusId(14L);
                stagingLine.setStatusDescription(statusDescription);
                stagingLine.setGoodsReceiptNo(grNumber);

                // Cross_Dock_logic_started
                try {
                    log.info("Cross Dock logic started");
                    log.info("The stagingLine inputs : companyCode --> " + stagingLine.getCompanyCode() + " and plantId --> " + stagingLine.getPlantId() + " and wareHouseId --> " + stagingLine.getWarehouseId() + " and itemCode --> " + stagingLine.getItemCode());
                    Optional<OrderManagementLineV2> crossDock = orderManagementLineV2Repository.getOrderManagementLineForCrossDockV8(
                            stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getLanguageId(), stagingLine.getWarehouseId(), stagingLine.getItemCode());
                    log.info("Cross Dock Value is " + crossDock);
                    if (crossDock.isPresent()) {
                        stagingLine.setCrossDock(true);
                    } else {
                        stagingLine.setCrossDock(false);
                    }
                } catch (Exception e) {
                    log.info("Cross Dock Failed " + e);
                }

                return stagingLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setStagingLines(stagingLines);
            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);

        } catch (Exception e) {
            log.error("Inbound Order Processing Exception ----> " + e.toString());
            throw e;
        }
    }

    /**
     *
     *
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     */
    public PreInboundHeaderEntityV2 createPreInboundHeaderV8(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                             String MW_AMS, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            log.info("PreInboundHeader Create Started -------------------------> ");
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setCompanyCode(companyCodeId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundHeader.setStatusId(statusId);
            preInboundHeader.setStatusDescription(statusDesc);
            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
            preInboundHeader.setPlantDescription(description.getPlantDesc());
            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());

            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());

            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy(MW_AMS);
            preInboundHeader.setCreatedOn(new Date());

            log.info("createdPreInboundHeader : " + preInboundHeader);

            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");

            return preInboundHeader;
        } catch (Exception e) {
            log.error("PreInboundHeader Create Exception : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    public InboundHeaderV2 createInboundHeaderV8(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines((long) preInboundLine.size());       //count of lines
//            return inboundHeaderV2Repository.save(inboundHeader);
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e.toString());
            throw e;
        }
    }
    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV8(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            // GR_MTD
            stagingHeader.setGrMtd("INTEGRATION");
//            return stagingHeaderV2Repository.save(stagingHeader);

            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(stagingHeader.getInboundOrderTypeId(), stagingHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");

            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
        }
    }
    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @param languageId
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV8(StagingHeaderV2 stagingHeader, String caseCode, String grNumber, String languageId) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(16L);
            grHeader.setStatusDescription(getStatusDescription(16L, languageId));
//            GrHeaderV2 createdGrHeader = grHeaderV2Repository.save(grHeader);

            // Staging_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");

            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param MW_AMS
     * @param description
     * @param statusId
     * @param statusDesc
     * @return
     * @throws Exception
     */
    public PreInboundLineEntityV2 createPreInboundLineV8(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                         InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
                                                         IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCodeId);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));
            preInboundLine.setSpecialStockIndicatorId(1L);
            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDesc);
            preInboundLine.setCompanyDescription(description.getCompanyDesc());
            preInboundLine.setPlantDescription(description.getPlantDesc());
            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(MW_AMS);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            // IB_Order
            String orderText = "Inbound Header Created";
            inboundOrderV2Repository.updateIbHeader(preInboundLine.getInboundOrderTypeId(), preInboundLine.getRefDocNumber(), orderText);
            log.info("Update Inbound Header Update Successfully");

            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e.toString());
            throw e;
        }
    }


    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     */
    public void inboundOrderCancellationV4(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber){

        preInboundHeaderV2Repository.deletePreInboundHeaderV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("PreInboundHeader Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deletePreInboundLineV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("PreInboundLine Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteInboundHeaderV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("InboundHeader Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteInboundLineV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("InboundLine Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteStagingHeaderV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("StagingHeader Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteStagingLineV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("StagingLine Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteGrHeaderV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("GrHeader Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deleteGrLineV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("GrLine Deleted Successfully ------> V4 <-----------------");

        preInboundHeaderV2Repository.deletePutawayHeaderV4(companyCodeId,plantId,languageId,warehouseId,refDocNumber);
        log.info("PutAwayHeader Deleted Successfully ------> V4 <-----------------");

    }

    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param mfrName
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV6(String companyId, String languageId, String plantId, String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                            String warehouseId, String companyText, String plantText, String warehouseText, String mfrName, String weight) {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setCompanyCode(companyId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType("Supplier Invoice");    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
            preInboundHeader.setTransferRequestType("Supplier Invoice");
            preInboundHeader.setStatusId(5L);
            statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, languageId);
            preInboundHeader.setStatusDescription(statusDescription);
            preInboundHeader.setCompanyDescription(companyText);
            preInboundHeader.setPlantDescription(plantText);
            preInboundHeader.setWarehouseDescription(warehouseText);
            preInboundHeader.setMiddlewareId(String.valueOf(inboundIntegrationHeader.getMiddlewareId()));
            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
            preInboundHeader.setManufacturerFullName(mfrName);
            preInboundHeader.setReferenceField3(weight);      //weight

            preInboundHeader.setTransferOrderDate(new Date());
            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getBranchCode());
            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getCompanyCode());
            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());

            preInboundHeader.setDeletionIndicator(0L);
            preInboundHeader.setCreatedBy("MW_AMS");
            preInboundHeader.setCreatedOn(new Date());

            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");
            return preInboundHeader;
        } catch (Exception e) {
            log.info("PreInboundHeader Creation Failed -----------> " + e.getMessage());
            throw new BadRequestException("PreInboundHeader Failed -----------------> " + e);
        }
    }

    /**
     * @param preInboundHeader
     * @return
     */
    private InboundHeaderV2 createInboundHeaderV6(PreInboundHeaderEntityV2 preInboundHeader, int lineSize) throws Exception {
        try {
            InboundHeaderV2 inboundHeader = new InboundHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            inboundHeader.setCountOfOrderLines((long) lineSize);       //count of lines
            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e.toString());
            throw e;
        }
    }


    /**
     * @param preInboundHeader
     * @param stagingNo
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV6(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
        try {
            StagingHeaderV2 stagingHeader = new StagingHeaderV2();
            BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
            stagingHeader.setStagingNo(stagingNo);
            stagingHeader.setGrMtd("INTEGRATION");
            // Staging_Header
            String orderText = "StagingHeader Created";
            inboundOrderV2Repository.updateStagingHeader(stagingHeader.getInboundOrderTypeId(), stagingHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param stagingHeader
     * @param caseCode
     * @param grNumber
     * @return
     * @throws Exception
     */
    public GrHeaderV2 createGrHeaderV6(StagingHeaderV2 stagingHeader, String caseCode, String grNumber) throws Exception {
        try {
            GrHeaderV2 grHeader = new GrHeaderV2();
            BeanUtils.copyProperties(stagingHeader, grHeader, CommonUtils.getNullPropertyNames(stagingHeader));
            grHeader.setCaseCode(caseCode);
            grHeader.setPalletCode(caseCode);
            grHeader.setGoodsReceiptNo(grNumber);
            grHeader.setStatusId(16L);
            grHeader.setStatusDescription(getStatusDescription(16L, grHeader.getLanguageId()));

            // Staging_Header
            String orderText = "GrHeader Created";
            inboundOrderV2Repository.updateGrHeader(grHeader.getInboundOrderTypeId(), grHeader.getRefDocNumber(), orderText);
            log.info("Update Staging Header Update Successfully");

            return grHeader;
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLines
     * @param preInboundHeader
     * @param stagingHeader
     * @param grHeaderV2
     * @param partBarCode
     * @param inboundOrderProcess
     * @throws Exception
     */
    private void processSingleASNLineV6(InboundIntegrationHeader inboundIntegrationHeader, List<InboundIntegrationLine> inboundIntegrationLines, PreInboundHeaderEntityV2 preInboundHeader,
                                      StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2, String partBarCode,InboundOrderProcess inboundOrderProcess) throws Exception {

        String companyCode = preInboundHeader.getCompanyCode();
        String plantId = preInboundHeader.getPlantId();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();
        String itemText = null;

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");

        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        List<ImBasicData1V2> imBasicData1List = new ArrayList<>();

        for(InboundIntegrationLine inboundIntegrationLine : inboundIntegrationLines) {
            ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                    languageId, companyCode, plantId, warehouseId, inboundIntegrationLine.getItemCode(), inboundIntegrationLine.getManufacturerName(), 0L);

            if (imBasicData1 == null) {
                imBasicData1 = new ImBasicData1V2();
                imBasicData1.setLanguageId(languageId);
                imBasicData1.setWarehouseId(warehouseId);
                imBasicData1.setCompanyCodeId(companyCode);
                imBasicData1.setPlantId(plantId);
                imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());
                imBasicData1.setUomId(inboundIntegrationLine.getUom());
                imBasicData1.setDescription(inboundIntegrationLine.getItemText());
                imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                imBasicData1.setCapacityCheck(false);
                imBasicData1.setDeletionIndicator(0L);
                imBasicData1.setStatusId(1L);
                imBasicData1.setReferenceField3(inboundIntegrationLine.getMiddlewareTable());        // weight
                imBasicData1.setReferenceField10(inboundIntegrationLine.getAMSSupplierInvoiceNo());  // unit type
                imBasicData1.setCompanyDescription(preInboundHeader.getCompanyDescription());
                imBasicData1.setPlantDescription(preInboundHeader.getPlantDescription());
                imBasicData1.setWarehouseDescription(preInboundHeader.getWarehouseDescription());
                imBasicData1List.add(imBasicData1);
            }
        }

        List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(8);
        List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();

        Long lineNumber = 1L;
        for (InboundIntegrationLine line : inboundIntegrationLines) {
            Long finalLineNumber = lineNumber;
            futures.add(executor.submit(() -> {
                return createPreInboundLineV6(companyCode, plantId, languageId, warehouseId, preInboundHeader.getPreInboundNo(),
                        inboundIntegrationHeader, line,preInboundHeader.getCompanyDescription(),preInboundHeader.getPlantDescription(),preInboundHeader.getWarehouseDescription(),partBarCode,itemText, finalLineNumber);
            }));
            lineNumber++;
        }

        List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
        for (Future<PreInboundLineEntityV2> future : futures) {
            try {
                finalToBeCreatedPreInboundLineList.add(future.get());
            } catch (Exception e) {
                log.error("Exception in thread: ", e);
            }
        }
        executor.shutdown();

        if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
            log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
            overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
            imBasicData1V2Repository.saveAll(imBasicData1List);
        }
        inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);

        if (inboundIntegrationHeader.getInboundOrderTypeId().equals(1L)){
            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setDescription(preInboundLine.getItemDescription());
                inboundLine.setStatusId(17L);
                inboundLine.setBarcodeId(preInboundLine.getBarcodeId());
                log.info("Description ------> {}", inboundLine.getDescription());
                return inboundLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setInboundLines(inboundLines);

            statusDescription = getStatusDescription(14L, languageId);
            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
                stagingLine.setStagingNo(stagingHeader.getStagingNo());
                stagingLine.setCaseCode(grHeaderV2.getCaseCode());
                stagingLine.setPalletCode(grHeaderV2.getCaseCode());
                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
                stagingLine.setBarcodeId(preInboundLine.getBarcodeId());
                stagingLine.setStatusId(14L);
                stagingLine.setStatusDescription(statusDescription);
                stagingLine.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
                return stagingLine;
            }).collect(Collectors.toList());
            inboundOrderProcess.setStagingLines(stagingLines);
        }
        

    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param partBarCode
     * @param itemText
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLineV6(String companyCode, String plantId, String languageId,String warehouseId, String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine,  String companyText, String plantText, String warehouseText,
                                                        String partBarCode, String itemText, Long lineNumber) {

        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();

        String barcodeId = generateBarCodeId(inboundIntegrationLine.getItemCode(), partBarCode);

        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
        preInboundLine.setBarcodeId(barcodeId + lineNumber);
        preInboundLine.setTransferRequestType("Supplier Invoice");
        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);
        // IB__LINE_NO
        preInboundLine.setLineNo(lineNumber);
        // ITM_CODE
        preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

        preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
        // MFR_PART
        preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
        // ORD_QTY
        preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
        // ORD_UOM
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);
        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
        log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
        preInboundLine.setExpectedArrivalDate(getExpectedDate(String.valueOf(inboundIntegrationLine.getExpectedDate())));
        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());
        preInboundLine.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        preInboundLine.setCompanyDescription(companyText);
        preInboundLine.setPlantDescription(plantText);
        preInboundLine.setWarehouseDescription(warehouseText);
        preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
        preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
        preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
        preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
        preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
        preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

        preInboundLine.setMiddlewareId(String.valueOf(inboundIntegrationLine.getMiddlewareId()));
        preInboundLine.setMiddlewareHeaderId(String.valueOf(inboundIntegrationLine.getMiddlewareHeaderId()));
//        preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
        preInboundLine.setReferenceDocumentType("Supplier Invoice");
        preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
        preInboundLine.setReferenceField3(inboundIntegrationLine.getMiddlewareTable());    //weight

        preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
        preInboundLine.setTransferOrderNo(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
        preInboundLine.setReferenceField10(inboundIntegrationLine.getAMSSupplierInvoiceNo()); //unit type

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

}