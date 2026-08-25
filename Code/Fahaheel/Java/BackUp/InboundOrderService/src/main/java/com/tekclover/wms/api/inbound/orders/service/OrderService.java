package com.tekclover.wms.api.inbound.orders.service;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
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
import com.tekclover.wms.api.inbound.orders.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.orders.repository.GrHeaderV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.ImBasicData1Repository;
import com.tekclover.wms.api.inbound.orders.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.inbound.orders.repository.InboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.InboundIntegrationLogRepository;
import com.tekclover.wms.api.inbound.orders.repository.InboundLineV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.InboundOrderV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.PreInboundHeaderV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.PreInboundLineV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.StagingHeaderV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.StagingLineV2Repository;
import com.tekclover.wms.api.inbound.orders.repository.WarehouseRepository;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
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
    protected String MW_AMS = "MW_AMS";
    @Autowired
    DbConfigRepository dbConfigRepository;
    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;
    String statusDescription = null;


    //------------------------------------------------------------------------------------------------

    /**
     * @param orderId
     * @return
     */
    public InboundOrderV2 getOrderByIdV2(String orderId, Long inboundOrderTypeId) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
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

//    public InboundOrderV2 updateProcessedInboundOrderV3(String orderId, Long inboundOrderTypeId, Long processStatusId) throws ParseException {
////        InboundOrderV2 dbInboundOrder = getOrderByIdV2(orderId, inboundOrderTypeId);
////        log.info("orderId : " + orderId);
////        log.info("dbInboundOrder : " + dbInboundOrder);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("MT");
//        InboundOrderV2 dbInboundOrder1 = getOrder(orderId);
//        dbInboundOrder1.setProcessedStatusId(processStatusId);
//        dbInboundOrder1.setOrderProcessedOn(new Date());
//        inboundOrderV2Repository.save(dbInboundOrder1);
////        DataBaseContextHolder.clear();
////        try {
////            String routingDb = dbConfigRepository.getDbName(dbInboundOrder.getCompanyCode(), dbInboundOrder.getBranchCode(), dbInboundOrder.getWarehouseID());
////            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
////            DataBaseContextHolder.setCurrentDb(routingDb);
////            inboundOrderV2Repository.updateIbOrderStatus(dbInboundOrder1.getCompanyCode(), dbInboundOrder1.getBranchCode(), dbInboundOrder1.getWarehouseID(),
////                    dbInboundOrder1.getRefDocumentNo(), processStatusId);
////        } finally {
////            DataBaseContextHolder.clear();
////        }
//        return dbInboundOrder1;
//    }

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


    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public InboundHeaderV2 processInboundReceivedV2(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader)
            throws IllegalAccessException, InvocationTargetException, BadRequestException,
            SQLException, SQLServerException, CannotAcquireLockException, LockAcquisitionException, Exception {
        try {
            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundIntegrationHeader.getInboundOrderTypeId());
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
            }

            String warehouseId = inboundIntegrationHeader.getWarehouseID();
            log.info("warehouseId : " + warehouseId);

            // validate the ITM_CODE result is Not Null
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            log.info("authTokenForMastersService : " + authTokenForMastersService);
            InboundOrderV2 inboundOrder = inboundOrderV2Repository.findByRefDocumentNoAndInboundOrderTypeId(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId());
            log.info("inboundOrder : " + inboundOrder);

            Warehouse warehouse = null;
            try {
                Optional<Warehouse> optWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                inboundOrder.getCompanyCode(),
                                inboundOrder.getBranchCode(),
                                "EN",
                                0L
                        );
                log.info("dbWarehouse : " + optWarehouse);

                if (optWarehouse != null && optWarehouse.isEmpty()) {
                    log.info("warehouse not found.");
                    throw new BadRequestException("Warehouse cannot be null.");
                }
                warehouse = optWarehouse.get();
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }

            // Getting PreInboundNo from NumberRangeTable
            String preInboundNo = getPreInboundNo(warehouseId, inboundOrder.getCompanyCode(), inboundOrder.getBranchCode(), warehouse.getLanguageId());
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                log.info("inboundIntegrationLine : " + inboundIntegrationLine);
                ImBasicData1V2 imBasicData1 =
                        imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                                warehouse.getLanguageId(),
                                warehouse.getCompanyCodeId(),
                                warehouse.getPlantId(),
                                warehouse.getWarehouseId(),
                                inboundIntegrationLine.getItemCode(),
                                inboundIntegrationLine.getManufacturerName(),
                                0L);
                log.info("imBasicData1 exists: " + imBasicData1);

                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
                if (imBasicData1 == null) {
                    imBasicData1 = new ImBasicData1V2();
                    imBasicData1.setLanguageId("EN");                                            // LANG_ID
                    imBasicData1.setWarehouseId(warehouseId);                                    // WH_ID
                    imBasicData1.setCompanyCodeId(warehouse.getCompanyCodeId());                    // C_ID
                    imBasicData1.setPlantId(warehouse.getPlantId());                            // PLANT_ID
                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                // ITM_CODE
                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                    // UOM_ID
                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());            // ITEM_TEXT
                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
                    imBasicData1.setCapacityCheck(false);
                    imBasicData1.setDeletionIndicator(0L);
                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
                    ImBasicData1 createdImBasicData1 =
                            mastersService.createImBasicData1V2(imBasicData1, "MW_AMS", authTokenForMastersService.getAccess_token());
                    log.info("ImBasicData1 created: " + createdImBasicData1);
                }
            }

            /* -------------------------------PreInboundLine Creation Process-------------------------------------------------*/
            List<PreInboundLineEntityV2> preInboundLineListV2 = new ArrayList<>();
            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
                preInboundLineListV2.add(createPreInboundLineV2(warehouse, preInboundNo, inboundIntegrationHeader, inboundIntegrationLine));
            }
            log.info("PreInboundLine List Size is {} ", preInboundLineListV2.size());

            /*------------------PreInboundHeader Creation Process-----------------------------*/
            PreInboundHeaderEntityV2 preInboundHeaderV2 = createPreInboundHeaderV2(warehouse, preInboundNo, inboundIntegrationHeader);
            log.info("PreInboundHeader Process Completed RefDocNo is -----> " + preInboundHeaderV2.getRefDocNumber());

            /*------------------InboundHeader Creation Process----------------------------*/
            InboundHeaderV2 inboundHeader = createInboundHeaderProcess(preInboundHeaderV2, preInboundLineListV2);
            log.info("InboundHeader Process Completed RefDocNo is -----> " + preInboundHeaderV2.getRefDocNumber());

            /*------------------InboundLine Creation Process----------------------------*/
            List<InboundLineV2> createdInboundLineList = createInboundLineProcess(preInboundLineListV2, preInboundHeaderV2);
            log.info("InboundLine Process Completed RefDocNo is -----> {}, Size is {} ", preInboundHeaderV2.getRefDocNumber(), createdInboundLineList.size());

            /*------------------StagingHeader Creation Process----------------------------*/
            StagingHeaderV2 stagingHeader = createStagingHeader(preInboundHeaderV2, inboundHeader.getCreatedBy());
            log.info("StagingHeader Process Completed RefDocNo is -----> " + stagingHeader.getRefDocNumber());

            /*------------------StagingLine Creation Process----------------------------*/
            List<StagingLineEntityV2> stagingLines =
                    stagingLineService.createStagingLineV2(preInboundLineListV2, stagingHeader.getStagingNo(), stagingHeader.getWarehouseId(),
                            stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getLanguageId(),
                            inboundHeader.getCreatedBy());
            log.info("StagingLine Process Completed RefDocNo is -----> {}, Size is {} ", preInboundHeaderV2.getRefDocNumber(), stagingLines.size());

            /*------------------GrHeader Creation Process----------------------------*/
            GrHeaderV2 grHeader = stagingLineService.createGrHeaderProcess(stagingHeader, stagingLines.get(0));
            log.info("GrHeader Process Completed RefDocNo is -----> " + grHeader.getRefDocNumber());

            log.info("All Order Saved Process Started -----------------> ");
            orderCreationProcess(preInboundHeaderV2, inboundHeader, stagingHeader, grHeader, preInboundLineListV2, createdInboundLineList, stagingLines);
            log.info("All Order Saved Process Completed -----------------> ");

            log.info("Staging Line Inventory Qty Update Started ------------------------------->");
            stagingLineService.updateStagingLineInOrderProcess(grHeader.getCompanyCode(), grHeader.getPlantId(), grHeader.getLanguageId(), warehouseId, refDocNumber, preInboundNo);

            if (grHeader.getInboundOrderTypeId() == 5) {  //Direct Stock Receipt Condition
                stagingLineService.createGrLine(grHeader);
            }
            return inboundHeader;
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
//    public InboundHeaderV2 processInboundReceivedV3(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
//
//        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
//        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
//        String plantId = inboundIntegrationHeader.getBranchCode();
//        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
//        String warehouseId = inboundIntegrationHeader.getWarehouseID();
//        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
//        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);
//
//        try {
//            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
//            if (inboundIntegrationHeader.getLoginUserId() != null) {
//                MW_AMS = inboundIntegrationHeader.getLoginUserId();
//            }
//
//            // save/create process
//            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
//            inboundOrderProcess.setLoginUserId(MW_AMS);
//
//            //Checking whether received refDocNumber processed already.
//            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
//                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }
//
//            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
//
//            String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
//            Long statusId = 13L;
//
//            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
//            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);
//
//            statusDescription = getStatusDescription(statusId, languageId);
//            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//
//            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
//            String partBarCode = generateBarCodeId(refDocNumber);
//
//            log.info("-----inboundIntegrationHeader--------> " + inboundIntegrationHeader.getInboundIntegrationLine());
//            long lineNumber = 1;
//            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
//                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                        languageId, companyCodeId, plantId, warehouseId,
//                        inboundIntegrationLine.getItemCode().trim(), inboundIntegrationLine.getManufacturerName(), 0L);
//                log.info("imBasicData1 exists: " + imBasicData1);
//                if (imBasicData1 != null) {
//                    if (inboundIntegrationLine.getItemText() == null) {
//                        inboundIntegrationLine.setItemText(imBasicData1.getDescription());
//                    }
//                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
//                    inboundIntegrationLine.setSize(imBasicData1.getSize());
//                    if (imBasicData1.getItemType() != null && imBasicData1.getItemTypeDescription() == null) {
//                        inboundIntegrationLine.setItemType(getItemTypeDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemType()));
//                    } else {
//                        inboundIntegrationLine.setItemType(imBasicData1.getItemTypeDescription());
//                    }
//                    if (imBasicData1.getItemGroup() != null && imBasicData1.getItemGroupDescription() == null) {
//                        inboundIntegrationLine.setItemGroup(getItemGroupDesc(companyCodeId, plantId, languageId, warehouseId, imBasicData1.getItemGroup()));
//                    } else {
//                        inboundIntegrationLine.setItemGroup(imBasicData1.getItemGroupDescription());
//                    }
//                }
//
//                // If ITM_CODE value is Null, then insert a record in IMBASICDATA1 table as below
//                if (imBasicData1 == null) {
//                    imBasicData1 = new ImBasicData1V2();
//                    BeanUtils.copyProperties(inboundIntegrationLine, imBasicData1, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//                    imBasicData1.setLanguageId(languageId);                                         // LANG_ID
//                    imBasicData1.setWarehouseId(warehouseId);                                       // WH_ID
//                    imBasicData1.setCompanyCodeId(companyCodeId);                                   // C_ID
//                    imBasicData1.setPlantId(plantId);                                               // PLANT_ID
//                    imBasicData1.setItemCode(inboundIntegrationLine.getItemCode());                 // ITM_CODE
//                    imBasicData1.setUomId(inboundIntegrationLine.getUom());                         // UOM_ID
//                    imBasicData1.setDescription(inboundIntegrationLine.getItemText());              // ITEM_TEXT
//                    imBasicData1.setManufacturerPartNo(inboundIntegrationLine.getManufacturerName());
//                    imBasicData1.setManufacturerName(inboundIntegrationLine.getManufacturerName());
//                    imBasicData1.setManufacturerCode(inboundIntegrationLine.getManufacturerCode());
//                    imBasicData1.setCapacityCheck(false);
//                    imBasicData1.setDeletionIndicator(0L);
//                    imBasicData1.setCompanyDescription(description.getCompanyDesc());
//                    imBasicData1.setPlantDescription(description.getPlantDesc());
//                    imBasicData1.setWarehouseDescription(description.getWarehouseDesc());
//
//                    imBasicData1.setStatusId(1L);                                                // STATUS_ID
//                    ImBasicData1 createdImBasicData1 = imBasicData1Service.createImBasicData1V2(imBasicData1, MW_AMS);
//                    log.info("ImBasicData1 created: " + createdImBasicData1);
//                }
//
//                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
//                /*
//                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
//                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
//                 */
//
//                log.info("noOfBags in inboundIntegrationLine ----------> {}", inboundIntegrationLine.getNoBags());
//                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
//                for (long i = 1; i <= noOfBags; i++) {
//                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
//                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//                    String barcodeId = "";
//                    try {
//                        if (inboundIntegrationLine.getSupplierCode() != null && inboundIntegrationLine.getSupplierCode().equalsIgnoreCase("EVEREST FOOD PRODUCTS PVT LTD")) {
//                            //barcodeId = null;
//                            barcodeId = "10000" + inboundIntegrationLine.getItemCode();
//                        } else {
//                            barcodeId = generateBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
//                        }
//                        barcodeId = barcodeId + lineNumber;
//                        newInboundIntegrationLine.setBarcodeId(barcodeId);
//                    } catch (Exception e) {
//                        throw new RuntimeException("Failed to generate barcode for item code: "
//                                + inboundIntegrationLine.getItemCode(), e);
//                    }
//                    newInboundIntegrationLine.setLineReference(lineNumber);
//                    newInboundIntegrationLine.setNoBags(1D);
//                    newInboundIntegrationLine.setOrderedQty(inboundIntegrationLine.getOrderedQty());
//                    lineNumber++;
//                    inboundIntegrationLines.add(newInboundIntegrationLine);
//                }
//            }
//
//
//            /*------------------Insert into PreInboundHeader table-----------------------------*/
//            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                    inboundIntegrationHeader, MW_AMS, description, statusId, statusDescription);
//            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);
//
//            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
//            inboundOrderProcess.setInboundHeader(createInboundHeader);
//
//            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);
//            inboundOrderProcess.setStagingHeader(stagingHeader);
//
//            //Gr Header Creation
//            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, languageId);
//            inboundOrderProcess.setGrHeader(createGrHeader);
//
//
//
//            /*
//             * Append PREINBOUNDLINE table through below logic
//             */
////            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
////            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
////                try {
////                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
////                            inboundIntegrationHeader, inboundIntegrationLine, MW_AMS,
////                            description, statusId, statusDescription));
////                } catch (Exception e) {
////                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
////                }
////            });
////            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());
//
//            // Batch Insert - PreInboundLines
////            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
////                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
////                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
////            }
////            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
//
//            ExecutorService executor = Executors.newFixedThreadPool(8);
//            List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();
//
//            for (InboundIntegrationLine line : inboundIntegrationLines) {
//                futures.add(executor.submit(() -> {
//                    return createPreInboundLineV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                            inboundIntegrationHeader, line, line.getManufacturerCode(),
//                            description, statusId, statusDescription);
//                }));
//            }
//
//            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
//            for (Future<PreInboundLineEntityV2> future : futures) {
//                try {
//                    finalToBeCreatedPreInboundLineList.add(future.get());
//                } catch (Exception e) {
//                    log.error("Exception in thread: ", e);
//                }
//            }
//            executor.shutdown();
//
//            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
//                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
//                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
//            }
//            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
//
//            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
//                InboundLineV2 inboundLine = new InboundLineV2();
//                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
//                inboundLine.setDescription(preInboundLine.getItemDescription());
//                log.info("Description ------> {}", inboundLine.getDescription());
//                return inboundLine;
//            }).collect(Collectors.toList());
//            inboundOrderProcess.setInboundLines(inboundLines);
//
//            statusDescription = getStatusDescription(14L, languageId);
//            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
//                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
//                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
//                stagingLine.setStagingNo(stagingNo);
//                stagingLine.setCaseCode(caseCode);
//                stagingLine.setPalletCode(caseCode);
//                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
//                stagingLine.setStatusId(14L);
//                stagingLine.setStatusDescription(statusDescription);
//                stagingLine.setGoodsReceiptNo(grNumber);
//
//                // Cross_Dock_logic_started
//                try {
//                    log.info("Cross Dock logic started");
//                    log.info("The stagingLine inputs : companyCode --> " + stagingLine.getCompanyCode() + " and plantId --> " + stagingLine.getPlantId() + " and wareHouseId --> " + stagingLine.getWarehouseId() + " and itemCode --> " + stagingLine.getItemCode());
//                    Optional<OrderManagementLineV2> crossDock = orderManagementLineV2Repository.getOrderManagementLineForCrossDock(
//                            stagingLine.getCompanyCode(), stagingLine.getPlantId(), stagingLine.getLanguageId(), stagingLine.getWarehouseId(), stagingLine.getItemCode());
//                    log.info("Cross Dock Value is " + crossDock);
//                    if (crossDock.isPresent()) {
//                        stagingLine.setCrossDock(true);
//                    } else {
//                        stagingLine.setCrossDock(false);
//                    }
//                } catch (Exception e) {
//                    log.info("Cross Dock Failed " + e);
//                }
//
//                return stagingLine;
//            }).collect(Collectors.toList());
//            inboundOrderProcess.setStagingLines(stagingLines);
//            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);
//
//        } catch (Exception e) {
//            log.error("Inbound Order Processing Exception ----> " + e.toString());
//            throw e;
//        }
//    }


    /**
     * @param refDocNumber
     * @param inboundIntegrationHeader
     * @return
     * @throws Exception
     */
//    public InboundHeaderV2 processInboundReceivedV7(String refDocNumber, InboundIntegrationHeader inboundIntegrationHeader) throws Exception {
//
//        InboundOrderProcess inboundOrderProcess = new InboundOrderProcess();
//        String companyCodeId = inboundIntegrationHeader.getCompanyCode();
//        String plantId = inboundIntegrationHeader.getBranchCode();
//        String languageId = inboundIntegrationHeader.getLanguageId() != null ? inboundIntegrationHeader.getLanguageId() : LANG_ID;
//        String warehouseId = inboundIntegrationHeader.getWarehouseID();
//        Long inboundOrderTypeId = inboundIntegrationHeader.getInboundOrderTypeId();
//        log.info("CompanyCodeId, plantId, languageId, warehouseId : " + companyCodeId + ", " + plantId + ", " + languageId + ", " + warehouseId);
//
//        try {
//            log.info("Inbound Process Initiated ------> " + refDocNumber + ", " + inboundOrderTypeId);
//            if (inboundIntegrationHeader.getLoginUserId() != null) {
//                WMS_KNOWELL = inboundIntegrationHeader.getLoginUserId();
//            }
//
//            // save/create process
//            inboundOrderProcess.setInboundIntegrationHeader(inboundIntegrationHeader);
//            inboundOrderProcess.setLoginUserId(WMS_KNOWELL);
//
//            //Checking whether received refDocNumber processed already.
//            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
//                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, inboundIntegrationHeader.getInboundOrderTypeId(), 0L);
//            if (!orderProcessedStatus.isEmpty()) {
//                throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
//            }
//
//            List<InboundIntegrationLine> inboundIntegrationLines = new ArrayList<>();
//
//            String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
//            Long statusId = 13L;
//
//            log.info("NumberRange input: CompanyCode ---> " + companyCodeId + ", plantId ---> " + plantId + ", languageId ---> " + languageId + ", warehouseId ---> " + warehouseId);
//            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
//            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);
//
//            statusDescription = getStatusDescription(statusId, languageId);
//            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
//
//            List<PreInboundLineEntityV2> overallCreatedPreInboundLineList = new ArrayList<>();
//            long lineNumber = 1;
//            for (InboundIntegrationLine inboundIntegrationLine : inboundIntegrationHeader.getInboundIntegrationLine()) {
//                ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
//                        languageId, companyCodeId, plantId, warehouseId,
//                        inboundIntegrationLine.getItemCode(), inboundIntegrationLine.getManufacturerName(), 0L);
//                log.info("imBasicData1 exists: " + imBasicData1);
//                if (imBasicData1 != null) {
//                    if (inboundIntegrationLine.getItemText() == null) {
//                        inboundIntegrationLine.setItemText(imBasicData1.getDescription());
//                    }
//                    inboundIntegrationLine.setBrand(imBasicData1.getBrand());
//                    inboundIntegrationLine.setSize(imBasicData1.getSize());
//
//                    inboundIntegrationLine.setManufacturerCode(imBasicData1.getManufacturerCode());
//                    inboundIntegrationLine.setManufacturerName(imBasicData1.getManufacturerName());
//                    inboundIntegrationLine.setManufacturerFullName(imBasicData1.getManufacturerFullName());
//                }
//                if (imBasicData1 == null) {
//                    // IB_Order
//                    String errorText = "The given values: manufacturerName:" + inboundIntegrationLine.getManufacturerName() +
//                            ",ItemCode: " + inboundIntegrationLine.getItemCode() + " doesn't exist.";
//                    inboundOrderV2Repository.invalidItemCodeMsgThrow(inboundIntegrationHeader.getRefDocumentNo(), errorText);
//                    log.info("Update Inbound Order Update Successfully");
//
//                    throw new BadRequestException("The given values: manufacturerName:" + inboundIntegrationLine.getManufacturerName() +
//                            ",ItemCode: " + inboundIntegrationLine.getItemCode() + " doesn't exist.");
//                }
//
//                /*-------------Insertion of BOM item in PREINBOUNDLINE table---------------------------------------------------------*/
//                /*
//                 * Before inserting the record into Preinbound table, fetch ITM_CODE from InboundIntegrationHeader (MONGO) table and
//                 * pass into BOMHEADER table as PAR_ITM_CODE and validate record is Not Null
//                 */
//
//                log.info("noOfBags in inboundIntegrationLine ----------> {}", inboundIntegrationLine.getNoBags());
//                double noOfBags = inboundIntegrationLine.getNoBags() != null ? inboundIntegrationLine.getNoBags() : 1L;
//                for (long i = 1; i <= noOfBags; i++) {
//                    InboundIntegrationLine newInboundIntegrationLine = new InboundIntegrationLine();
//                    BeanUtils.copyProperties(inboundIntegrationLine, newInboundIntegrationLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//                    String barcodeId = "";
//                    try {
//                        if (inboundIntegrationLine.getSupplierCode() != null && inboundIntegrationLine.getSupplierCode().equalsIgnoreCase("Everest Pvt Ltd")) {
//                            barcodeId = "10000" + inboundIntegrationLine.getItemCode();
//                        } else {
////                            barcodeId = generateKnowellBarCodeId(newInboundIntegrationLine.getItemCode(), partBarCode, i);
//                            barcodeId = generateKnowellBarCodeId(companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//                        }
//                        newInboundIntegrationLine.setBarcodeId(barcodeId);
//                    } catch (Exception e) {
//                        throw new RuntimeException("Failed to generate barcode for item code: "
//                                + inboundIntegrationLine.getItemCode(), e);
//                    }
//                    newInboundIntegrationLine.setLineReference(lineNumber);
//                    newInboundIntegrationLine.setNoBags(1D);
//                    newInboundIntegrationLine.setOrderedQty(inboundIntegrationLine.getOrderedQty());
//
//                    lineNumber++;
//                    inboundIntegrationLines.add(newInboundIntegrationLine);
//                }
//            }
//
//            /*------------------Insert into PreInboundHeader table-----------------------------*/
//            PreInboundHeaderEntityV2 createPreInboundHeader = createPreInboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                    inboundIntegrationHeader, WMS_KNOWELL, description, statusId, statusDescription);
//            inboundOrderProcess.setPreInboundHeader(createPreInboundHeader);
//
//            /*------------------Insert into Inbound Header----------------------------*/
//            InboundHeaderV2 createInboundHeader = createInboundHeaderV2(createPreInboundHeader, overallCreatedPreInboundLineList);
//            inboundOrderProcess.setInboundHeader(createInboundHeader);
//
//            StagingHeaderV2 stagingHeader = createStagingHeaderV2(createPreInboundHeader, stagingNo);
//            inboundOrderProcess.setStagingHeader(stagingHeader);
//
//            //Gr Header Creation
//            GrHeaderV2 createGrHeader = createGrHeaderV2(stagingHeader, caseCode, grNumber, languageId);
//            inboundOrderProcess.setGrHeader(createGrHeader);
//
//
////            /*
////             * Append PREINBOUNDLINE table through below logic
////             */
////            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
////            inboundIntegrationLines.stream().forEach(inboundIntegrationLine -> {
////                try {
////                    finalToBeCreatedPreInboundLineList.add(createPreInboundLineV3(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
////                            inboundIntegrationHeader, inboundIntegrationLine, inboundIntegrationLine.getManufacturerCode(),
////                            description, statusId, statusDescription));
////                } catch (Exception e) {
////                    throw new BadRequestException("Exception While PreInboundLine Create" + e.toString());
////                }
////            });
////            log.info("toBeCreatedPreInboundLineList [API] : " + finalToBeCreatedPreInboundLineList.size());
////
////            // Batch Insert - PreInboundLines
////            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
////                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList);
////                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
////            }
////            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
////
//            ExecutorService executor = Executors.newFixedThreadPool(8);
//            List<Future<PreInboundLineEntityV2>> futures = new ArrayList<>();
//
//            for (InboundIntegrationLine line : inboundIntegrationLines) {
//                futures.add(executor.submit(() -> {
//                    return createPreInboundLineV3(companyCodeId, plantId, languageId, warehouseId, preInboundNo,
//                            inboundIntegrationHeader, line, line.getManufacturerCode(),
//                            description, statusId, statusDescription);
//                }));
//            }
//
//            List<PreInboundLineEntityV2> finalToBeCreatedPreInboundLineList = new ArrayList<>();
//            for (Future<PreInboundLineEntityV2> future : futures) {
//                try {
//                    finalToBeCreatedPreInboundLineList.add(future.get());
//                } catch (Exception e) {
//                    log.error("Exception in thread: ", e);
//                }
//            }
//            executor.shutdown();
//
//            if (!finalToBeCreatedPreInboundLineList.isEmpty()) {
//                log.info("createdPreInboundLine [API] : " + finalToBeCreatedPreInboundLineList.size());
//                overallCreatedPreInboundLineList.addAll(finalToBeCreatedPreInboundLineList);
//            }
//            inboundOrderProcess.setPreInboundLines(overallCreatedPreInboundLineList);
//
//
//            List<InboundLineV2> inboundLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
//                InboundLineV2 inboundLine = new InboundLineV2();
//                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
//                inboundLine.setDescription(preInboundLine.getItemDescription());
//                return inboundLine;
//            }).collect(Collectors.toList());
//            inboundOrderProcess.setInboundLines(inboundLines);
//
//            statusDescription = getStatusDescription(14L, languageId);
//            List<StagingLineEntityV2> stagingLines = overallCreatedPreInboundLineList.stream().map(preInboundLine -> {
//                StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
//                BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
//                stagingLine.setStagingNo(stagingNo);
//                stagingLine.setCaseCode(caseCode);
//                stagingLine.setPalletCode(caseCode);
//                stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
//                stagingLine.setStatusId(14L);
//                stagingLine.setStatusDescription(statusDescription);
//                stagingLine.setGoodsReceiptNo(grNumber);
//
//                return stagingLine;
//            }).collect(Collectors.toList());
//            inboundOrderProcess.setStagingLines(stagingLines);
//
//            return orderProcessingService.postInboundReceived(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, inboundOrderTypeId, inboundOrderProcess);
//
//        } catch (Exception e) {
//            log.error("Inbound Order Processing Exception ----> " + e.toString());
//            throw e;
//        }
//    }

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
     * @param warehouse
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @param inboundIntegrationLine
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(Warehouse warehouse,
                                                          String preInboundNo,
                                                          InboundIntegrationHeader inboundIntegrationHeader,
                                                          InboundIntegrationLine inboundIntegrationLine) throws ParseException {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();

        preInboundLine.setLanguageId(warehouse.getLanguageId());
        preInboundLine.setCompanyCode(warehouse.getCompanyCodeId());
        preInboundLine.setPlantId(warehouse.getPlantId());
        preInboundLine.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());

        // ITM_CODE
        preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        warehouse.getLanguageId(),
                        warehouse.getCompanyCodeId(),
                        warehouse.getPlantId(),
                        warehouse.getWarehouseId(),
                        inboundIntegrationLine.getItemCode(),
                        inboundIntegrationLine.getManufacturerName(),
                        0L);
        preInboundLine.setItemDescription(imBasicData1.getDescription());
        preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());
        preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
        preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
        preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
        preInboundLine.setStockTypeId(1L);
        preInboundLine.setSpecialStockIndicatorId(1L);
        log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
        preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
        preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());
        preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
        preInboundLine.setStatusId(14L);
        statusDescription = stagingLineV2Repository.getStatusDescription(14L, warehouse.getLanguageId());
        preInboundLine.setStatusDescription(statusDescription);
        IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                warehouse.getLanguageId(),
                warehouse.getPlantId(),
                warehouse.getWarehouseId());

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
//    private PreInboundLineEntityV2 createPreInboundLineV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                          String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
//                                                          InboundIntegrationLine inboundIntegrationLine, String loginUserId,
//                                                          IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
//        try {
//            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
//            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//            preInboundLine.setLanguageId(languageId);
//            preInboundLine.setCompanyCode(companyCodeId);
//            preInboundLine.setPlantId(plantId);
//            preInboundLine.setWarehouseId(warehouseId);
//            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
//            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
//            preInboundLine.setParentProductionOrderNo(inboundIntegrationHeader.getParentProductionOrderNo());
//
//            // PRE_IB_NO
//            preInboundLine.setPreInboundNo(preInboundNo);
//
//            // IB__LINE_NO
//            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
//
//            // ITM_CODE
//            preInboundLine.setItemCode(inboundIntegrationLine.getItemCode());
//
//            // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
//
//            // MFR_PART
//            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());
//
//            // PARTNER_CODE
//            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
//
//            // ORD_QTY
//            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
//
//            // ORD_UOM
//            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
//
//            // STCK_TYP_ID
//            preInboundLine.setStockTypeId(1L);
//
//            // SP_ST_IND_ID
//            preInboundLine.setSpecialStockIndicatorId(1L);
//
//            // EA_DATE
//            log.info("inboundIntegrationLine.getExpectedDate() : {}", inboundIntegrationLine.getExpectedDate());
//            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
//
//            // ITM_CASE_QTY
//            preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());
//
//            // REF_FIELD_4
//            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
//
//            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//            preInboundLine.setStatusId(statusId);
//            preInboundLine.setStatusDescription(statusDesc);
//            preInboundLine.setCompanyDescription(description.getCompanyDesc());
//            preInboundLine.setPlantDescription(description.getPlantDesc());
//            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
//
//            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
//            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
//            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
//            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
//            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
//            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
//            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());
//
//            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
//            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
//            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
//            preInboundLine.setPurchaseOrderNumber(inboundIntegrationLine.getPurchaseOrderNumber());
//            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
//            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());
//
//            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
//            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
//            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());
//
//            if (inboundIntegrationLine.getCustomerId() != null) {
//                preInboundLine.setReferenceField6(inboundIntegrationLine.getCustomerId());
//            }
//            if (inboundIntegrationLine.getCustomerName() != null) {
//                preInboundLine.setReferenceField7(inboundIntegrationLine.getCustomerName());
//            }
//
////            if (preInboundLine.getBarcodeId() == null) {
////                //Barcode
////                List<String> barcode = getBarCodeId(companyCodeId, plantId, languageId, warehouseId,
////                        preInboundLine.getItemCode(), preInboundLine.getManufacturerName());
////                log.info("Barcode : " + barcode);
////                if (barcode != null && !barcode.isEmpty()) {
////                    preInboundLine.setBarcodeId(barcode.get(0));
////                }
////            }
//
//            //-----------------PROP_ST_BIN---------------------------------------------
////            String storageBin = validateStorageBin(companyCodeId, plantId, languageId, warehouseId, inboundIntegrationLine.getBinLocation(), preInboundLine);
////            preInboundLine.setReferenceField5(storageBin);
//            preInboundLine.setReferenceField5(inboundIntegrationLine.getBinLocation());
//            preInboundLine.setManufacturerDate(inboundIntegrationLine.getManufacturerDate());
//            preInboundLine.setVehicleNo(inboundIntegrationLine.getVehicleNo());
//            preInboundLine.setVehicleUnloadingDate(inboundIntegrationLine.getVehicleUnloadingDate());
//            preInboundLine.setVehicleReportingDate(inboundIntegrationLine.getVehicleReportingDate());
//
//            preInboundLine.setDeletionIndicator(0L);
//            preInboundLine.setCreatedBy(loginUserId);
//            preInboundLine.setCreatedOn(new Date());
//
//            if (preInboundLine.getOrderUom().equalsIgnoreCase("Crate") && preInboundLine.getInboundOrderTypeId() == 11L) {
//                preInboundLine.setQtyInCreate(preInboundLine.getOrderQty());
//            } else {
//                log.info("Quantity Logic started-------------->");
//                setAlternateUomQuantities(preInboundLine);
//                log.info("Quantity Logic Completed-------------->");
//            }
//            log.info("preInboundLine : " + preInboundLine);
//            return preInboundLine;
//        } catch (Exception e) {
//            log.error("PreInboundLine Create Exception: " + e);
//            throw e;
//        }
//    }

    /**
     * @param preInboundLineEntityV2
     */
//    private void setAlternateUomQuantities(PreInboundLineEntityV2 preInboundLineEntityV2) {
//        try {
//            Double qtyInPiece = null;
//            Double qtyInCase = null;
//            Double qtyInCreate = null;
//
//            String orderUom = preInboundLineEntityV2.getOrderUom();
//            String companyCodeId = preInboundLineEntityV2.getCompanyCode();
//            String plantId = preInboundLineEntityV2.getPlantId();
//            String warehouseId = preInboundLineEntityV2.getWarehouseId();
//            String itemCode = preInboundLineEntityV2.getItemCode();
//
//            if ("piece".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is PIECE");
//
//                qtyInPiece = preInboundLineEntityV2.getOrderQty();
//                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//
//                log.info("Piece Qty --- {}", preInboundLineEntityV2.getOrderQty());
//                log.info("Case Qty ALT_UOM: {}", caseQty);
//                log.info("Create Qty ALT_UOM: {}", createQty);
//
//                if (preInboundLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
//                    qtyInCase = preInboundLineEntityV2.getOrderQty() / caseQty.getUomQty();
//                }
//
//                if (preInboundLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
//                    qtyInCreate = preInboundLineEntityV2.getOrderQty() / createQty.getUomQty();
//                }
//
//            } else if ("case".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is CASE");
//
//                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//                IKeyValuePair createQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//
//                qtyInCase = preInboundLineEntityV2.getOrderQty();
//
//                log.info("Case Qty --- {}", preInboundLineEntityV2.getOrderQty());
//                log.info("Piece Qty ALT_UOM: {}", pieceQty);
//                log.info("Create Qty ALT_UOM: {}", createQty);
//
//                if (preInboundLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
//                    qtyInPiece = preInboundLineEntityV2.getOrderQty() * pieceQty.getUomQty();
//                }
//
//                if (preInboundLineEntityV2.getOrderQty() != null && createQty != null && createQty.getUomQty() != null) {
//                    qtyInCreate = qtyInPiece / createQty.getUomQty();
//                }
//            } else if ("crate".equalsIgnoreCase(orderUom)) {
//                log.info("OrderUom is CRATE");
//                qtyInCreate = preInboundLineEntityV2.getOrderQty();
//
//                IKeyValuePair pieceQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "3");
//                IKeyValuePair caseQty = stagingLineV2Repository.getAlternateUomQty(companyCodeId, plantId, warehouseId, itemCode, "1", "2");
//
//                log.info("Crate Qty --- {}", preInboundLineEntityV2.getOrderQty());
//                log.info("Piece Qty ALT_UOM: {}", pieceQty);
//                log.info("Case Qty ALT_UOM: {}", caseQty);
//
//                if (preInboundLineEntityV2.getOrderQty() != null && pieceQty != null && pieceQty.getUomQty() != null) {
//                    qtyInPiece = preInboundLineEntityV2.getOrderQty() * pieceQty.getUomQty();
//                }
//
//                if (preInboundLineEntityV2.getOrderQty() != null && caseQty != null && caseQty.getUomQty() != null) {
////                    qtyInCase = preInboundLineEntityV2.getOrderQty() / createQty.getUomQty();
//                    qtyInCase = qtyInPiece / caseQty.getUomQty();
//                    log.info("Case Qty ----->" + qtyInCase);
//                }
//            }
//
//            preInboundLineEntityV2.setQtyInPiece(qtyInPiece);
//            preInboundLineEntityV2.setQtyInCase(qtyInCase);
//            preInboundLineEntityV2.setQtyInCreate(qtyInCreate);
//        } catch (Exception e) {
//            log.error("Error setting UOM quantities: {}", e.getMessage(), e);
//        }
//    }

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
//     */
//    public PreInboundLineEntityV2 createPreInboundLineV3(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                         String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
//                                                         InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
//                                                         IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
//        try {
//            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
//            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//            preInboundLine.setLanguageId(languageId);
//            preInboundLine.setCompanyCode(companyCodeId);
//            preInboundLine.setPlantId(plantId);
//            preInboundLine.setWarehouseId(warehouseId);
//            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
//            preInboundLine.setPreInboundNo(preInboundNo);
//            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
//            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
//            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
//            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
//            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
//            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
//            preInboundLine.setStockTypeId(1L);
//            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));
//            preInboundLine.setSpecialStockIndicatorId(1L);
//            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
//            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
//            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
//            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//            preInboundLine.setStatusId(statusId);
//            preInboundLine.setStatusDescription(statusDesc);
//            preInboundLine.setCompanyDescription(description.getCompanyDesc());
//            preInboundLine.setPlantDescription(description.getPlantDesc());
//            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
//            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
//            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
//            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
//            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
//            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
//
//            preInboundLine.setDeletionIndicator(0L);
//            preInboundLine.setCreatedBy(MW_AMS);
//            preInboundLine.setCreatedOn(new Date());
//
//            log.info("preInboundLine : " + preInboundLine);
//            // IB_Order
//            String orderText = "Inbound Header Created";
//            inboundOrderV2Repository.updateIbHeader(preInboundLine.getInboundOrderTypeId(), preInboundLine.getRefDocNumber(), orderText);
//            log.info("Update Inbound Header Update Successfully");
//
//
//            InboundLineV2 inboundLine = new InboundLineV2();
//            BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
//            inboundLine.setDescription(preInboundLine.getItemDescription());
//            inboundLineV2Repository.save(inboundLine);
//            log.info("InboundLine Saved Successfully");
//
//            return preInboundLine;
//        } catch (Exception e) {
//            log.error("PreInboundLine Create Exception: " + e.toString());
//            throw e;
//        }
//    }

//    public PreInboundLineEntityV2 createPreInboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                         String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
//                                                         InboundIntegrationLine inboundIntegrationLine, String MW_AMS,
//                                                         IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
//        try {
//            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
//            BeanUtils.copyProperties(inboundIntegrationLine, preInboundLine, CommonUtils.getNullPropertyNames(inboundIntegrationLine));
//            preInboundLine.setLanguageId(languageId);
//            preInboundLine.setCompanyCode(companyCodeId);
//            preInboundLine.setPlantId(plantId);
//            preInboundLine.setWarehouseId(warehouseId);
//            preInboundLine.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
//            preInboundLine.setPreInboundNo(preInboundNo);
//            preInboundLine.setLineNo(inboundIntegrationLine.getLineReference());
//            preInboundLine.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());
//            preInboundLine.setItemDescription(inboundIntegrationLine.getItemText());
//            preInboundLine.setBusinessPartnerCode(inboundIntegrationLine.getSupplierCode());
//            preInboundLine.setOrderQty(inboundIntegrationLine.getOrderedQty());
//            preInboundLine.setOrderUom(inboundIntegrationLine.getUom());
//            preInboundLine.setStockTypeId(1L);
//            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCodeId, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));
//            preInboundLine.setSpecialStockIndicatorId(1L);
//            log.info("inboundIntegrationLine.getExpectedDate() : " + inboundIntegrationLine.getExpectedDate());
//            preInboundLine.setExpectedArrivalDate(inboundIntegrationLine.getExpectedDate());
//            preInboundLine.setReferenceField4(inboundIntegrationLine.getSalesOrderReference());
//            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//            preInboundLine.setStatusId(statusId);
//            preInboundLine.setStatusDescription(statusDesc);
//            preInboundLine.setCompanyDescription(description.getCompanyDesc());
//            preInboundLine.setPlantDescription(description.getPlantDesc());
//            preInboundLine.setWarehouseDescription(description.getWarehouseDesc());
//            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
//            preInboundLine.setManufacturerCode(inboundIntegrationLine.getManufacturerName());
//            preInboundLine.setPartnerItemNo(inboundIntegrationLine.getSupplierCode());
//            preInboundLine.setContainerNo(inboundIntegrationLine.getContainerNumber());
//            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
//
//            preInboundLine.setDeletionIndicator(0L);
//            preInboundLine.setCreatedBy(MW_AMS);
//            preInboundLine.setCreatedOn(new Date());
//
//            log.info("preInboundLine : " + preInboundLine);
//            // IB_Order
//            String orderText = "Inbound Header Created";
//            inboundOrderV2Repository.updateIbHeader(preInboundLine.getInboundOrderTypeId(), preInboundLine.getRefDocNumber(), orderText);
//            log.info("Update Inbound Header Update Successfully");
//
//            return preInboundLine;
//        } catch (Exception e) {
//            log.error("PreInboundLine Create Exception: " + e.toString());
//            throw e;
//        }
//    }


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
//    private PreInboundHeaderEntityV2 createPreInboundHeaderV5(String companyCodeId, String plantId, String languageId, String warehouseId,
//                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader,
//                                                              String loginUserId, IKeyValuePair description, Long statusId, String statusDesc) throws Exception {
//        try {
//            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
//            BeanUtils.copyProperties(inboundIntegrationHeader, preInboundHeader, CommonUtils.getNullPropertyNames(inboundIntegrationHeader));
//            preInboundHeader.setCompanyCode(companyCodeId);
//            preInboundHeader.setPlantId(plantId);
//            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
//            preInboundHeader.setWarehouseId(warehouseId);
//            preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
//            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
//            preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
//            preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
//            preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
//            // Status ID - statusId changed to reduce one less step process and avoid deadlock while updating status
//            preInboundHeader.setStatusId(statusId);
//            preInboundHeader.setStatusDescription(statusDesc);
//            preInboundHeader.setCompanyDescription(description.getCompanyDesc());
//            preInboundHeader.setPlantDescription(description.getPlantDesc());
//            preInboundHeader.setWarehouseDescription(description.getWarehouseDesc());
//
//            preInboundHeader.setMiddlewareId(inboundIntegrationHeader.getMiddlewareId());
//            preInboundHeader.setMiddlewareTable(inboundIntegrationHeader.getMiddlewareTable());
//            preInboundHeader.setContainerNo(inboundIntegrationHeader.getContainerNo());
//
//            preInboundHeader.setTransferOrderDate(inboundIntegrationHeader.getTransferOrderDate());
//            preInboundHeader.setSourceBranchCode(inboundIntegrationHeader.getSourceBranchCode());
//            preInboundHeader.setSourceCompanyCode(inboundIntegrationHeader.getSourceCompanyCode());
//            preInboundHeader.setIsCompleted(inboundIntegrationHeader.getIsCompleted());
//            preInboundHeader.setIsCancelled(inboundIntegrationHeader.getIsCancelled());
//            preInboundHeader.setMUpdatedOn(inboundIntegrationHeader.getUpdatedOn());
//            if (inboundIntegrationHeader.getCustomerId() != null) {
//                preInboundHeader.setCustomerId(inboundIntegrationHeader.getCustomerId());
//            }
//            if (inboundIntegrationHeader.getCustomerName() != null) {
//                preInboundHeader.setCustomerName(inboundIntegrationHeader.getCustomerName());
//            }
//
//            preInboundHeader.setDeletionIndicator(0L);
//            preInboundHeader.setCreatedBy(loginUserId);
//            preInboundHeader.setCreatedOn(new Date());
//            log.info("createdPreInboundHeader : " + preInboundHeader);
//
//            // PreInbound_Header
//            String preInbound = "PreInbound Created";
//            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
//            log.info("Update PreInbound Header Update Successfully");
//
//            return preInboundHeader;
//        } catch (Exception e) {
//            log.error("PreInboundHeader Create Exception : " + e);
//            throw e;
//        }
//    }


    /**
     * @param warehouse
     * @param preInboundNo
     * @param inboundIntegrationHeader
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(Warehouse warehouse,
                                                              String preInboundNo, InboundIntegrationHeader inboundIntegrationHeader) throws ParseException {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        preInboundHeader.setLanguageId(warehouse.getLanguageId());                                    // LANG_ID
        preInboundHeader.setWarehouseId(inboundIntegrationHeader.getWarehouseID());
        preInboundHeader.setCompanyCode(warehouse.getCompanyCodeId());
        preInboundHeader.setPlantId(warehouse.getPlantId());
        preInboundHeader.setRefDocNumber(inboundIntegrationHeader.getRefDocumentNo());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(inboundIntegrationHeader.getInboundOrderTypeId());    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(inboundIntegrationHeader.getOrderReceivedOn());                // REF_DOC_DATE
        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, warehouse.getLanguageId());
        preInboundHeader.setStatusDescription(statusDescription);

        IKeyValuePair description = stagingLineV2Repository.getDescription(warehouse.getCompanyCodeId(),
                warehouse.getLanguageId(),
                warehouse.getPlantId(),
                warehouse.getWarehouseId());
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
        return preInboundHeader;
    }


    /**
     * @param preInboundHeader
     * @param preInboundLine
     * @return
     */
    private InboundHeaderV2 createInboundHeaderProcess(PreInboundHeaderEntityV2 preInboundHeader, List<PreInboundLineEntityV2> preInboundLine) {
        InboundHeaderV2 inboundHeader = new InboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
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
        return inboundHeader;
    }

    /*
     * Inbound Line Table Insert
     */
    public List<InboundLineV2> createInboundLineProcess(List<PreInboundLineEntityV2> preInboundLineList, PreInboundHeaderEntityV2 preInboundHeader) {
        List<InboundLineV2> toBeCreatedInboundLineList = new ArrayList<>();
        for (PreInboundLineEntityV2 createdPreInboundLine : preInboundLineList) {
            InboundLineV2 inboundLine = new InboundLineV2();
            BeanUtils.copyProperties(createdPreInboundLine, inboundLine, CommonUtils.getNullPropertyNames(createdPreInboundLine));

            inboundLine.setOrderQty(createdPreInboundLine.getOrderQty());
            inboundLine.setOrderUom(createdPreInboundLine.getOrderUom());
            inboundLine.setDescription(createdPreInboundLine.getItemDescription());
            inboundLine.setVendorCode(createdPreInboundLine.getBusinessPartnerCode());
            inboundLine.setReferenceField4(createdPreInboundLine.getReferenceField4());
            inboundLine.setContainerNo(createdPreInboundLine.getContainerNo());
            inboundLine.setSupplierName(createdPreInboundLine.getSupplierName());
            inboundLine.setMiddlewareId(createdPreInboundLine.getMiddlewareId());
            inboundLine.setMiddlewareHeaderId(createdPreInboundLine.getMiddlewareHeaderId());
            inboundLine.setMiddlewareTable(createdPreInboundLine.getMiddlewareTable());
            inboundLine.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
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
        return toBeCreatedInboundLineList;
    }

    //========================================================================V5====================================================================

    /**
     * To avoid Deadlock
     * @param preInboundHeader
     * @param loginUserID
     * @return
     */
    public StagingHeaderV2 createStagingHeader(PreInboundHeaderEntityV2 preInboundHeader, String loginUserID) {

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
        stagingHeader.setContainerNo(preInboundHeader.getContainerNo());
        stagingHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
        stagingHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
        stagingHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
        stagingHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());

        stagingHeader.setStatusId(14L);
        statusDescription = stagingLineV2Repository.getStatusDescription(14L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
        stagingHeader.setCompanyCode(preInboundHeader.getCompanyCode());
        stagingHeader.setPlantId(preInboundHeader.getPlantId());
        stagingHeader.setWarehouseId(preInboundHeader.getWarehouseId());
        stagingHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
        stagingHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
        stagingHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
        stagingHeader.setIsCompleted(preInboundHeader.getIsCompleted());
        stagingHeader.setIsCancelled(preInboundHeader.getIsCancelled());
        stagingHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());

        stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        return stagingHeader;
    }

    /**
     *
     * @param preInboundHeader preInboundHeader
     * @param inboundHeader InboundHeader
     * @param stagingHeader StagingHeader
     * @param preInboundLine preInboundLine
     * @param inboundLine InboundLine
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    void orderCreationProcess(PreInboundHeaderEntityV2 preInboundHeader, InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                              List<PreInboundLineEntityV2> preInboundLine, List<InboundLineV2> inboundLine, List<StagingLineEntityV2> stagingLine) {

        if (preInboundHeader != null) {
            preInboundHeaderV2Repository.save(preInboundHeader);
            log.info("PreInboundHeader Saved Successfully -------------------->");
        }
        if (inboundHeader != null) {
            inboundHeaderV2Repository.save(inboundHeader);
            log.info("InboundHeader Saved Successfully -------------------->");
        }
        if (stagingHeader != null) {
            stagingHeaderV2Repository.save(stagingHeader);
            log.info("StagingHeader Saved Successfully -------------------->");
        }
        if (grHeaderV2 != null) {
            grHeaderV2Repository.save(grHeaderV2);
            log.info("StagingHeader Saved Successfully -------------------->");
        }
        if (!preInboundLine.isEmpty()) {
            preInboundLineV2Repository.saveAll(preInboundLine);
            log.info("PreInboundLine Saved Successfully Size is -----------> {}", preInboundLine.size());
        }
        if (!inboundLine.isEmpty()) {
            inboundLineV2Repository.saveAll(inboundLine);
            log.info("InboundLine Saved Successfully Size is -----------> {}", inboundLine.size());
        }
        if (!stagingLine.isEmpty()) {
            stagingLineV2Repository.saveAll(stagingLine);
            log.info("StagingLine Saved Successfully Size is -----------> {}", stagingLine.size());
        }
    }

}