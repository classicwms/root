package com.tekclover.wms.api.inbound.orders.service;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.PackBarcode;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.AddGrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.StockReceipt;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.StockReceiptHeader;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.StockReceiptLine;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StockReceiptService {

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    protected MastersService mastersService;

    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    @Autowired
    AuthTokenService authTokenService;

    @Autowired
    GrHeaderV2Repository grHeaderRepository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    GrLineV2Repository grLineV2Repository;

    @Autowired
    StagingLineService stagingLineService;

    @Autowired
    GrLineService grLineService;

    @Autowired
    StockReceiptServiceV2 stockReceiptServiceV2;

    @Autowired
    DbConfigRepository dbConfigRepository;

    private static String WAREHOUSEID_NUMBERRANGE = "110";

    String statusDescription = null;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<StockReceipt> processInboundReceivedV2(List<StockReceipt> stkList) {

        List<StockReceipt> processSR = Collections.synchronizedList(new ArrayList<>());
        log.info("Inbound Process Started for {} StockReceipt", stkList.size());
        log.info("StockReceipt stkList ------> {}", stkList);

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            for (StockReceipt srV2 : stkList) {
                StockReceiptHeader headerV2 = srV2.getStockReceiptHeader();
                log.info("StockReceiptHeader headerV2 ----> {}", headerV2);
                List<StockReceiptLine> lineV2List = srV2.getStockReceiptLines();
                log.info("StockReceiptLine lineV2List ----> {}", lineV2List);
                String companyCode = headerV2.getCompanyCode();
                String plantId = headerV2.getBranchCode();
                String mfrName = lineV2List.get(0).getManufacturerFullName();

                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                String warehouseId = dbWarehouse.get().getWarehouseId();
                String languageId = dbWarehouse.get().getLanguageId();
                log.info("Warehouse ID: {}", warehouseId);

                // Description_Set
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
                String companyText = description.getCompanyDesc();
                String plantText = description.getPlantDesc();
                String warehouseText = description.getWarehouseDesc();

                // Getting PreInboundNo from NumberRangeTable
                String preInboundNo = getPreInboundNo(warehouseId, companyCode, plantId, languageId);

                // Step 1: Create headers before line processing
                PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
                        companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
                log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

                InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, companyText, plantText, warehouseText, lineV2List.size());
                log.info("Inbound Header Created: {}", inboundHeader);

                StagingHeaderV2 stagingHeader = createStagingHeader(preInboundHeader, companyText, plantText, warehouseText);
                log.info("StagingHeader Created: {}", stagingHeader);

                GrHeaderV2 grHeader = createGrHeader(preInboundHeader, stagingHeader);
                log.info("GrHeader Created: {}", grHeader);

                // Collections for batch saving
                List<ImBasicData1V2> imBasicDataList = Collections.synchronizedList(new ArrayList<>());
                List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
                List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
                List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());
                List<AddGrLineV2> grLineV2List = Collections.synchronizedList(new ArrayList<>());

                // Process lines in parallel
                List<CompletableFuture<Void>> futures = lineV2List.stream()
                        .map(srLineV2 -> CompletableFuture.runAsync(() -> {
                            try {
                                processSingleSRLine(srV2, srLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
                                        imBasicDataList, preInboundLineList, inboundLineList, stagingLineList, grLineV2List);
                            } catch (Exception e) {
                                log.error("Error processing StockReceipt Line for IBSr: {}", headerV2.getReceiptNo(), e);
                                throw new RuntimeException(e);
                            }
                        }, executorService))
                        .collect(Collectors.toList());

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                try {
                    allFutures.join(); // Wait for all tasks to finish
                } catch (CompletionException e) {
                    log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
                }

                // Batch Save All Records
                imBasicData1V2Repository.saveAll(imBasicDataList);
                preInboundLineV2Repository.saveAll(preInboundLineList);
                inboundLineV2Repository.saveAll(inboundLineList);
                stagingLineV2Repository.saveAll(stagingLineList);
                // Direct Stock Receipt Create GrLine and PutAwayHeader and InboundConfirm also
                log.info("Create GrLine Create Started ---------------------------------------> {} ", grLineV2List.size());
                List<GrLineV2> dbGrLineList = createGrLineNonCBMV2(grLineV2List);
                // PutAwayHeader
                log.info("GrLine Created Successfully Then PutAway and Inbound Confirm Logic Started ------------------------------------------------->");
                createPutAwayHeader(dbGrLineList, grHeader);
                processSR.add(srV2);
            }
        } catch (Exception e) {
            log.error("Error processing inbound Stock receipt Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Inbound Process Completed for {} Stock Receipt", processSR.size());
        return processSR;

    }

    /**
     * @param srV2
     * @param srLineV2
     * @param preInboundHeader
     * @param inboundHeader
     * @param stagingHeader
     * @param grHeaderV2
     * @param imBasicDataList
     * @param preInboundLineList
     * @param inboundLineList
     * @param stagingLineList
     */
    private void processSingleSRLine(StockReceipt srV2, StockReceiptLine srLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                     InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                     List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList,
                                     List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList, List<AddGrLineV2> grLineV2List) {
        StockReceiptHeader headerV2 = srV2.getStockReceiptHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        // Check and collect IMBASICDATA1

//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");

        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, srLineV2.getItemCode().trim(), srLineV2.getManufacturerFullName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(srLineV2.getItemCode());
            imBasicData1.setUomId(srLineV2.getUnitOfMeasure());
            imBasicData1.setDescription(srLineV2.getItemDescription());
            imBasicData1.setManufacturerPartNo(srLineV2.getManufacturerFullName());
            imBasicData1.setManufacturerName(srLineV2.getManufacturerFullName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicDataList.add(imBasicData1); // Collect for batch save
        }

        // Collect PreInboundLine
        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId, preInboundHeader.getPreInboundNo(), headerV2,
                srLineV2, warehouseId, preInboundHeader.getCompanyDescription(), preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription());
        preInboundLineList.add(preInboundLine);

        // Collect InboundLine
        InboundLineV2 inboundLineV2 = createInboundLine(preInboundLine, inboundHeader);
        inboundLineList.add(inboundLineV2);

        // Collect StagingLine
        StagingLineEntityV2 stagingLine = createStagingLineV2(preInboundLine, stagingHeader, grHeaderV2);
        stagingLineList.add(stagingLine);

        // Collect GrLine
        AddGrLineV2 grLineV2 = addGrLine(stagingLine, grHeaderV2);
        grLineV2List.add(grLineV2);
    }

    /**
     * @param preInboundLine
     * @param stagingHeader
     * @param grHeaderV2
     * @return
     */
    private StagingLineEntityV2 createStagingLineV2(PreInboundLineEntityV2 preInboundLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2) {
        StagingLineEntityV2 dbStagingLineEntity = new StagingLineEntityV2();
        BeanUtils.copyProperties(preInboundLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(preInboundLine));
        dbStagingLineEntity.setCaseCode(grHeaderV2.getCaseCode());
        dbStagingLineEntity.setPalletCode(grHeaderV2.getCaseCode());    //Copy CASE_CODE
        dbStagingLineEntity.setSourceBranchCode(stagingHeader.getSourceBranchCode());
        dbStagingLineEntity.setStatusId(14L);
        dbStagingLineEntity.setLanguageId(stagingHeader.getLanguageId());
        dbStagingLineEntity.setCompanyCode(stagingHeader.getCompanyCode());
        dbStagingLineEntity.setPlantId(stagingHeader.getPlantId());
        dbStagingLineEntity.setCustomerCode(preInboundLine.getCustomerCode());
        dbStagingLineEntity.setTransferRequestType(preInboundLine.getTransferRequestType());
        dbStagingLineEntity.setOrderQty(preInboundLine.getOrderQty());

        //Pass ITM_CODE/SUPPLIER_CODE received in integration API into IMPARTNER table and fetch PARTNER_ITEM_BARCODE values. Values can be multiple
        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(preInboundLine.getItemCode(), preInboundLine.getCompanyCode(),
                preInboundLine.getPlantId(), preInboundLine.getWarehouseId(), preInboundLine.getManufacturerName(), preInboundLine.getLanguageId());
        log.info("Barcode : " + barcode);
        if (barcode != null && !barcode.isEmpty()) {
//                    dbStagingLineEntity.setPartner_item_barcode(barcode.replaceAll("\\s", "").trim());      //to remove white space
            dbStagingLineEntity.setPartner_item_barcode(barcode.get(0));
        }

        statusDescription = stagingLineV2Repository.getStatusDescription(14L, stagingHeader.getLanguageId());
        dbStagingLineEntity.setStatusDescription(statusDescription);
        dbStagingLineEntity.setCompanyDescription(stagingHeader.getCompanyDescription());
        dbStagingLineEntity.setPlantDescription(stagingHeader.getPlantDescription());
        dbStagingLineEntity.setWarehouseDescription(stagingHeader.getWarehouseDescription());
        dbStagingLineEntity.setStagingNo(stagingHeader.getStagingNo());
        dbStagingLineEntity.setManufacturerCode(preInboundLine.getManufacturerName());
        dbStagingLineEntity.setManufacturerName(preInboundLine.getManufacturerName());
        dbStagingLineEntity.setDeletionIndicator(0L);
        dbStagingLineEntity.setCreatedBy(preInboundLine.getCreatedBy());
        dbStagingLineEntity.setUpdatedBy(preInboundLine.getCreatedBy());
        dbStagingLineEntity.setCreatedOn(new Date());
        dbStagingLineEntity.setUpdatedOn(new Date());
        return dbStagingLineEntity;
    }

    /**
     * @param preInboundLine
     * @param inboundHeader
     * @return
     */
    private InboundLineV2 createInboundLine(PreInboundLineEntityV2 preInboundLine, InboundHeaderV2 inboundHeader) {
        InboundLineV2 inboundLine = new InboundLineV2();
        BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));

        inboundLine.setOrderQty(preInboundLine.getOrderQty());
        inboundLine.setAcceptedQty(preInboundLine.getOrderQty());
        inboundLine.setOrderUom(preInboundLine.getOrderUom());
        inboundLine.setDescription(preInboundLine.getItemDescription());
        inboundLine.setVendorCode(preInboundLine.getBusinessPartnerCode());
        inboundLine.setReferenceField4(preInboundLine.getReferenceField4());
        inboundLine.setCompanyDescription(preInboundLine.getCompanyDescription());
        inboundLine.setPlantDescription(preInboundLine.getPlantDescription());
        inboundLine.setWarehouseDescription(preInboundLine.getWarehouseDescription());
        inboundLine.setStatusId(14L);
        statusDescription = stagingLineV2Repository.getStatusDescription(14L, inboundLine.getLanguageId());
        inboundLine.setStatusDescription(statusDescription);
        inboundLine.setContainerNo(preInboundLine.getContainerNo());
        inboundLine.setSupplierName(preInboundLine.getSupplierName());
        inboundLine.setMiddlewareId(preInboundLine.getMiddlewareId());
        inboundLine.setMiddlewareHeaderId(preInboundLine.getMiddlewareHeaderId());
        inboundLine.setMiddlewareTable(preInboundLine.getMiddlewareTable());
        inboundLine.setReferenceDocumentType(preInboundLine.getReferenceDocumentType());
        inboundLine.setManufacturerFullName(preInboundLine.getManufacturerFullName());
        inboundLine.setPurchaseOrderNumber(preInboundLine.getPurchaseOrderNumber());
        inboundLine.setManufacturerCode(preInboundLine.getManufacturerName());
        inboundLine.setManufacturerName(preInboundLine.getManufacturerName());
        inboundLine.setExpectedArrivalDate(preInboundLine.getExpectedArrivalDate());
        inboundLine.setBranchCode(preInboundLine.getBranchCode());
        inboundLine.setTransferOrderNo(preInboundLine.getTransferOrderNo());
        inboundLine.setIsCompleted(preInboundLine.getIsCompleted());
        inboundLine.setSourceCompanyCode(inboundHeader.getSourceCompanyCode());
        inboundLine.setSourceBranchCode(inboundHeader.getSourceBranchCode());
        inboundLine.setDeletionIndicator(0L);
        inboundLine.setCreatedBy(inboundHeader.getCreatedBy());
        inboundLine.setCreatedOn(inboundHeader.getCreatedOn());
        inboundLine.setReferenceField2("true");
//        inboundLineV2Repository.save(inboundLine);
        return inboundLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param headerV2
     * @param srLineV2
     * @param warehouseId
     * @param companyDescription
     * @param plantDescription
     * @param warehouseDescription
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, StockReceiptHeader headerV2, StockReceiptLine srLineV2, String warehouseId, String companyDescription, String plantDescription, String warehouseDescription) {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setRefDocNumber(headerV2.getReceiptNo());
        preInboundLine.setInboundOrderTypeId(5L);
//        preInboundLine.setCustomerCode(asnHeaderV2.getCustomerCode());
//        preInboundLine.setTransferRequestType("Direct StokReceipt");

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(srLineV2.getLineNoForEachItem());

        // ITM_CODE
        preInboundLine.setItemCode(srLineV2.getItemCode());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//        ImBasicData1 imBasicData1 =
//                imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                        languageId, companyCode, plantId, warehouseId, srLineV2.getItemCode(), srLineV2.getManufacturerFullName(), 0L);
        preInboundLine.setItemDescription(srLineV2.getItemDescription());

        // MFR_PART
        preInboundLine.setManufacturerPartNo(srLineV2.getManufacturerFullName());
        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(srLineV2.getSupplierCode());
        // ORD_QTY
        preInboundLine.setOrderQty(srLineV2.getReceiptQty());
        // ORD_UOM
        preInboundLine.setOrderUom(srLineV2.getUnitOfMeasure());
        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
//        log.info("inboundIntegrationLine.getExpectedDate() : " + srLineV2.getReceiptDate());
//        preInboundLine.setExpectedArrivalDate(srLineV2.getReceiptDate());
        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(srLineV2.getReceiptQty());
        preInboundLine.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        preInboundLine.setCompanyDescription(companyDescription);
        preInboundLine.setPlantDescription(plantDescription);
        preInboundLine.setWarehouseDescription(warehouseDescription);
        preInboundLine.setManufacturerCode(srLineV2.getManufacturerCode());
        preInboundLine.setManufacturerName(srLineV2.getManufacturerFullName());
        preInboundLine.setPartnerItemNo(srLineV2.getSupplierCode());
        preInboundLine.setSupplierName(srLineV2.getSupplierName());

        preInboundLine.setMiddlewareId(String.valueOf(srLineV2.getMiddlewareId()));
        preInboundLine.setMiddlewareHeaderId(String.valueOf(srLineV2.getMiddlewareHeaderId()));
        preInboundLine.setMiddlewareTable(srLineV2.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(srLineV2.getSupplierPartNo());
        preInboundLine.setReferenceDocumentType("Stock Receipt");
        preInboundLine.setManufacturerFullName(srLineV2.getManufacturerFullName());

        preInboundLine.setBranchCode(srLineV2.getBranchCode());
        preInboundLine.setTransferOrderNo(srLineV2.getReceiptNo());
        preInboundLine.setIsCompleted(srLineV2.getIsCompleted());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

    /**
     * @param expectedDate
     * @return
     */
    public Date getExpectedDate(String expectedDate) {
        Date reqDelDate = null;
        if (expectedDate != null) {
            if (expectedDate.contains("-")) {
                // EA_DATE
                try {
                    reqDelDate = new Date();
                    if (expectedDate.length() > 10) {
                        reqDelDate = DateUtils.convertStringToDateWithTime(expectedDate);
                    }
                    if (expectedDate.length() == 10) {
                        reqDelDate = DateUtils.convertStringToDate2(expectedDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Date format should be MM-dd-yyyy");
                }
            }
        }
        return reqDelDate;
    }

    /**
     * @param preInboundHeaderEntityV2
     * @param stagingHeaderV2
     * @return
     */
    private GrHeaderV2 createGrHeader(PreInboundHeaderEntityV2 preInboundHeaderEntityV2, StagingHeaderV2 stagingHeaderV2) {
        GrHeaderV2 addGrHeader = new GrHeaderV2();
        BeanUtils.copyProperties(stagingHeaderV2, addGrHeader, CommonUtils.getNullPropertyNames(stagingHeaderV2));

        // GR_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_CODE = 5;
        String nextGRHeaderNumber = getNextRangeNumber(NUM_RAN_CODE, stagingHeaderV2.getCompanyCode(),
                stagingHeaderV2.getPlantId(), stagingHeaderV2.getLanguageId(), stagingHeaderV2.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        addGrHeader.setGoodsReceiptNo(nextGRHeaderNumber);

        String caseCode = getNextRangeNumber(4L, stagingHeaderV2.getCompanyCode(),
                stagingHeaderV2.getPlantId(), stagingHeaderV2.getLanguageId(), stagingHeaderV2.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        if (caseCode == null || caseCode.isEmpty()) {
            throw new BadRequestException("CaseCode is not generated.");
        }

        addGrHeader.setCaseCode(caseCode);
        addGrHeader.setPalletCode(caseCode);
        addGrHeader.setMiddlewareId(stagingHeaderV2.getMiddlewareId());
        addGrHeader.setMiddlewareTable(stagingHeaderV2.getMiddlewareTable());
        addGrHeader.setReferenceDocumentType(stagingHeaderV2.getReferenceDocumentType());
        addGrHeader.setManufacturerFullName(stagingHeaderV2.getManufacturerFullName());
        addGrHeader.setManufacturerName(preInboundHeaderEntityV2.getManufacturerFullName());
        addGrHeader.setTransferOrderDate(preInboundHeaderEntityV2.getTransferOrderDate());
        addGrHeader.setIsCompleted(stagingHeaderV2.getIsCompleted());
        addGrHeader.setIsCancelled(preInboundHeaderEntityV2.getIsCancelled());
        addGrHeader.setMUpdatedOn(preInboundHeaderEntityV2.getMUpdatedOn());
        addGrHeader.setSourceBranchCode(stagingHeaderV2.getSourceBranchCode());
        addGrHeader.setSourceCompanyCode(preInboundHeaderEntityV2.getSourceCompanyCode());
        addGrHeader.setCustomerCode(stagingHeaderV2.getCustomerCode());
        addGrHeader.setTransferRequestType(stagingHeaderV2.getTransferRequestType());

        // STATUS_ID
        addGrHeader.setStatusId(16L);
        statusDescription = stagingLineV2Repository.getStatusDescription(16L, stagingHeaderV2.getLanguageId());
        addGrHeader.setStatusDescription(statusDescription);

        addGrHeader.setDeletionIndicator(0L);
        addGrHeader.setCreatedOn(new Date());
        addGrHeader.setUpdatedOn(new Date());

        return grHeaderRepository.save(addGrHeader);
    }


    /**
     * @param preInboundHeader
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private StagingHeaderV2 createStagingHeader(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText) {
        StagingHeaderV2 stagingHeader = new StagingHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // STG_NO
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();

        long NUMBER_RANGE_CODE = 3;
        String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE,
                preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), preInboundHeader.getWarehouseId(),
                authTokenForIDMasterService.getAccess_token());
        stagingHeader.setStagingNo(nextRangeNumber);

        // GR_MTD
        stagingHeader.setGrMtd("INTEGRATION");

        // STATUS_ID
        stagingHeader.setStatusId(12L);
        statusDescription = stagingLineV2Repository.getStatusDescription(14L, preInboundHeader.getLanguageId());
        stagingHeader.setStatusDescription(statusDescription);
        stagingHeader.setCompanyDescription(companyText);
        stagingHeader.setPlantDescription(plantText);
        stagingHeader.setWarehouseDescription(warehouseText);

//        stagingHeader.setContainerNo(preInboundHeader.getContainerNo());
//        stagingHeader.setMiddlewareId(preInboundHeader.getMiddlewareId());
//        stagingHeader.setMiddlewareTable(preInboundHeader.getMiddlewareTable());
//        stagingHeader.setReferenceDocumentType(preInboundHeader.getReferenceDocumentType());
//        stagingHeader.setManufacturerFullName(preInboundHeader.getManufacturerFullName());
//
//        stagingHeader.setTransferOrderDate(preInboundHeader.getTransferOrderDate());
//        stagingHeader.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
//        stagingHeader.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
//        stagingHeader.setIsCompleted(preInboundHeader.getIsCompleted());
//        stagingHeader.setIsCancelled(preInboundHeader.getIsCancelled());
//        stagingHeader.setMUpdatedOn(preInboundHeader.getMUpdatedOn());
//        stagingHeader.setCreatedBy(preInboundHeader.getCreatedBy());
//        stagingHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        return stagingHeaderV2Repository.save(stagingHeader);
    }

    /**
     * @param preInboundHeader
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param size
     * @return
     */
    private InboundHeaderV2 createInboundHeader(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText, int size) {
        InboundHeaderV2 inboundHeader = new InboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
        inboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
        inboundHeader.setStatusDescription(statusDescription);
        inboundHeader.setCompanyDescription(companyText);
        inboundHeader.setPlantDescription(plantText);
        inboundHeader.setWarehouseDescription(warehouseText);
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
        inboundHeader.setCountOfOrderLines((long) size);       //count of lines
        inboundHeader.setDeletionIndicator(0L);
        inboundHeader.setCreatedBy(preInboundHeader.getCreatedBy());
        inboundHeader.setCreatedOn(preInboundHeader.getCreatedOn());
        InboundHeaderV2 createdInboundHeader = inboundHeaderV2Repository.save(inboundHeader);
        log.info("createdInboundHeader : " + createdInboundHeader);
        return createdInboundHeader;
    }


    /**
     * @param companyCode
     * @param languageId
     * @param plantId
     * @param preInboundNo
     * @param headerV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param mfrName
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCode, String languageId, String plantId, String preInboundNo, StockReceiptHeader headerV2, String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(headerV2, preInboundHeader, CommonUtils.getNullPropertyNames(headerV2));
        preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
        preInboundHeader.setWarehouseId(warehouseId);
        preInboundHeader.setCompanyCode(companyCode);
        preInboundHeader.setPlantId(plantId);
        preInboundHeader.setRefDocNumber(headerV2.getReceiptNo());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType("Stock Receipt");    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(5L);    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
        preInboundHeader.setTransferRequestType("Stock Receipt");
        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundHeader.setStatusDescription(statusDescription);
        preInboundHeader.setCompanyDescription(companyText);
        preInboundHeader.setPlantDescription(plantText);
        preInboundHeader.setWarehouseDescription(warehouseText);
        preInboundHeader.setMiddlewareId(String.valueOf(headerV2.getMiddlewareId()));
        preInboundHeader.setMiddlewareTable(headerV2.getMiddlewareTable());
        preInboundHeader.setManufacturerFullName(mfrName);
        preInboundHeader.setTransferOrderDate(new Date());
        preInboundHeader.setSourceBranchCode(headerV2.getBranchCode());
        preInboundHeader.setSourceCompanyCode(headerV2.getCompanyCode());
        preInboundHeader.setIsCompleted(headerV2.getIsCompleted());
        preInboundHeader.setMUpdatedOn(headerV2.getUpdatedOn());

        preInboundHeader.setDeletionIndicator(0L);
        preInboundHeader.setCreatedBy("MW_AMS");
        preInboundHeader.setCreatedOn(new Date());
        PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
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
            String nextRangeNumber = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
            return nextRangeNumber;
        } catch (Exception e) {
            throw new BadRequestException("Error on Number generation." + e.toString());
        }
    }

    /**
     * @param NUM_RAN_CODE
     * @param warehouseId
     * @param accessToken
     * @return
     */
    protected String getNextRangeNumber(Long NUM_RAN_CODE, String companyCodeId, String plantId,
                                        String languageId, String warehouseId, String accessToken) {
        String nextNumberRange = mastersService.getNextNumberRange(NUM_RAN_CODE, warehouseId, companyCodeId, plantId, languageId, accessToken);
        return nextNumberRange;
    }


    // After GrLine Create add GrLine
    public AddGrLineV2 addGrLine(StagingLineEntityV2 stagingLineEntityV2, GrHeaderV2 grHeader) {

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Double itemLength = 0D;
        Double itemWidth = 0D;
        Double itemHeight = 0D;
        Double orderQty = 0D;
        Double cbm = 0D;
        Double cbmPerQty = 0D;

        String hhtUser = "DirecStockReceipt";
        List<PackBarcode> packBarcodeList = new ArrayList<>();
        AddGrLineV2 newGrLine = new AddGrLineV2();
        PackBarcode newPackBarcode = new PackBarcode();

        BeanUtils.copyProperties(stagingLineEntityV2, newGrLine, CommonUtils.getNullPropertyNames(stagingLineEntityV2));

        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        long NUM_RAN_ID = 6;
        String nextRangeNumber = getNextRangeNumber(NUM_RAN_ID, stagingLineEntityV2.getCompanyCode(),
                stagingLineEntityV2.getPlantId(), stagingLineEntityV2.getLanguageId(), stagingLineEntityV2.getWarehouseId(), authTokenForIDMasterService.getAccess_token());

        boolean capacityCheck = false;
        boolean storageBinCapacityCheck = false;

        ImBasicData imBasicData = new ImBasicData();
        imBasicData.setCompanyCodeId(stagingLineEntityV2.getCompanyCode());
        imBasicData.setPlantId(stagingLineEntityV2.getPlantId());
        imBasicData.setLanguageId(stagingLineEntityV2.getLanguageId());
        imBasicData.setWarehouseId(stagingLineEntityV2.getWarehouseId());
        imBasicData.setItemCode(stagingLineEntityV2.getItemCode());
        imBasicData.setManufacturerName(stagingLineEntityV2.getManufacturerName());
        ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
        log.info("ImbasicData1 : " + itemCodeCapacityCheck);
        if (itemCodeCapacityCheck != null && itemCodeCapacityCheck.getCapacityCheck() != null) {
            capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
            log.info("capacity Check: " + capacityCheck);
        }

        newPackBarcode.setQuantityType("A");
        newPackBarcode.setBarcode(nextRangeNumber);

        if (capacityCheck) {

            if (stagingLineEntityV2.getOrderQty() != null) {
                orderQty = stagingLineEntityV2.getOrderQty();
            }
            if (itemCodeCapacityCheck != null && itemCodeCapacityCheck.getLength() != null) {
                itemLength = itemCodeCapacityCheck.getLength();
            }
            if (itemCodeCapacityCheck != null && itemCodeCapacityCheck.getWidth() != null) {
                itemWidth = itemCodeCapacityCheck.getWidth();
            }
            if (itemCodeCapacityCheck != null && itemCodeCapacityCheck.getHeight() != null) {
                itemHeight = itemCodeCapacityCheck.getHeight();
            }

            cbmPerQty = itemLength * itemWidth * itemHeight;
            cbm = orderQty * cbmPerQty;

            newPackBarcode.setCbmQuantity(cbmPerQty);
            newPackBarcode.setCbm(cbm);

            log.info("item Length, Width, Height, Volume[CbmPerQty], CBM: " + itemLength + ", " + itemWidth + "," + itemHeight + ", " + cbmPerQty + ", " + cbm);
        }
        if (!capacityCheck) {

            newPackBarcode.setCbmQuantity(0D);
            newPackBarcode.setCbm(0D);
        }

        packBarcodeList.add(newPackBarcode);

        newGrLine.setGoodReceiptQty(stagingLineEntityV2.getOrderQty());
        newGrLine.setAcceptedQty(stagingLineEntityV2.getOrderQty());
        newGrLine.setGoodsReceiptNo(grHeader.getGoodsReceiptNo());
        newGrLine.setManufacturerFullName(stagingLineEntityV2.getManufacturerFullName());
        newGrLine.setReferenceDocumentType(stagingLineEntityV2.getReferenceDocumentType());
        newGrLine.setPurchaseOrderNumber(stagingLineEntityV2.getPurchaseOrderNumber());
        if (stagingLineEntityV2.getPartner_item_barcode() != null) {
            newGrLine.setBarcodeId(stagingLineEntityV2.getPartner_item_barcode());
        }
        if (stagingLineEntityV2.getPartner_item_barcode() == null) {
            newGrLine.setBarcodeId(stagingLineEntityV2.getManufacturerName() + stagingLineEntityV2.getItemCode());
        }

        List<String> hhtUserOutputList = stagingLineV2Repository.getHhtUserByOrderType(
                grHeader.getCompanyCode(), grHeader.getLanguageId(), grHeader.getPlantId(), grHeader.getWarehouseId(),
                grHeader.getInboundOrderTypeId());
        if (hhtUserOutputList != null && !hhtUserOutputList.isEmpty()) {
            hhtUser = hhtUserOutputList.get(0);
        }
        if (hhtUserOutputList == null || hhtUserOutputList.isEmpty() || hhtUserOutputList.size() == 0) {
            List<String> hhtUserList = stagingLineV2Repository.getHhtUser(
                    grHeader.getCompanyCode(), grHeader.getLanguageId(), grHeader.getPlantId(), grHeader.getWarehouseId());
            if (hhtUserList != null && !hhtUserList.isEmpty()) {
                hhtUser = hhtUserList.get(0);
            }
        }
        newGrLine.setAssignedUserId(hhtUser);

        newGrLine.setPackBarcodes(packBarcodeList);
        newGrLine.setInterimStorageBin("REC-AL-B2");

        return newGrLine;
    }


    /**
     * @param newGrLines
     * @return
     * @throws Exception
     */
    public List<GrLineV2> createGrLineNonCBMV2(@Valid List<AddGrLineV2> newGrLines)
            throws Exception {
        List<GrLineV2> createdGRLines = new ArrayList<>();
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String goodsReceiptNo = null;
        try {

            // Inserting multiple records
            for (AddGrLineV2 newGrLine : newGrLines) {
                if (newGrLine.getPackBarcodes() == null || newGrLine.getPackBarcodes().isEmpty()) {
                    throw new BadRequestException("Enter either Accept Qty or Damage Qty");
                }
                /*------------Inserting based on the PackBarcodes -----------*/
                for (PackBarcode packBarcode : newGrLine.getPackBarcodes()) {
                    GrLineV2 dbGrLine = new GrLineV2();
                    log.info("newGrLine : " + newGrLine);
                    BeanUtils.copyProperties(newGrLine, dbGrLine, CommonUtils.getNullPropertyNames(newGrLine));
                    dbGrLine.setCompanyCode(newGrLine.getCompanyCode());

                    // GR_QTY
                    if (packBarcode.getQuantityType().equalsIgnoreCase("A")) {
                        Double grQty = newGrLine.getAcceptedQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setAcceptedQty(grQty);
                        dbGrLine.setDamageQty(0D);
                        log.info("A-------->: " + dbGrLine);
                    } else if (packBarcode.getQuantityType().equalsIgnoreCase("D")) {
                        Double grQty = newGrLine.getDamageQty();
                        dbGrLine.setGoodReceiptQty(grQty);
                        dbGrLine.setDamageQty(newGrLine.getDamageQty());
                        dbGrLine.setAcceptedQty(0D);
                        log.info("D-------->: " + dbGrLine);
                    }

                    dbGrLine.setQuantityType(packBarcode.getQuantityType());
                    dbGrLine.setPackBarcodes(packBarcode.getBarcode());
                    dbGrLine.setStatusId(14L);

                    // 12-03-2024 - Ticket No. ALM/2024/006
                    if (dbGrLine.getGoodReceiptQty() < 0) {
                        throw new BadRequestException("Gr Quantity Cannot be Negative");
                    }
                    log.info("StatusId: " + newGrLine.getStatusId());
                    if (newGrLine.getStatusId() == 24L) {
                        throw new BadRequestException("GrLine is already Confirmed");
                    }

                    // GoodReceipt Qty should be less than or equal to ordered qty---> if GrQty >
                    // OrdQty throw Exception
                    Double dbGrQty = grLineV2Repository.getGrLineQuantity(newGrLine.getCompanyCode(),
                            newGrLine.getPlantId(), newGrLine.getLanguageId(), newGrLine.getWarehouseId(),
                            newGrLine.getRefDocNumber(), newGrLine.getPreInboundNo(), newGrLine.getGoodsReceiptNo(),
                            newGrLine.getPalletCode(), newGrLine.getCaseCode(), newGrLine.getItemCode(),
                            newGrLine.getManufacturerName(), newGrLine.getLineNo());
                    log.info("dbGrQty, newGrQty, OrdQty: " + dbGrQty + ", " + dbGrLine.getGoodReceiptQty() + ", "
                            + newGrLine.getOrderQty());
                    if (dbGrQty != null) {
                        Double totalGrQty = dbGrQty + dbGrLine.getGoodReceiptQty();
//                        if (newGrLine.getOrderQty() < totalGrQty) {
//                            throw new BadRequestException("Total Gr Qty is greater than Order Qty ");
//                        }
                    }

                    // V2 Code
                    IKeyValuePair description = stagingLineV2Repository.getDescription(newGrLine.getCompanyCode(),
                            newGrLine.getLanguageId(), newGrLine.getPlantId(), newGrLine.getWarehouseId());

                    statusDescription = stagingLineV2Repository.getStatusDescription(dbGrLine.getStatusId(),
                            newGrLine.getLanguageId());
                    dbGrLine.setStatusDescription(statusDescription);

                    if (description != null) {
                        dbGrLine.setCompanyDescription(description.getCompanyDesc());
                        dbGrLine.setPlantDescription(description.getPlantDesc());
                        dbGrLine.setWarehouseDescription(description.getWarehouseDesc());
                    }

                    dbGrLine.setMiddlewareId(newGrLine.getMiddlewareId());
                    dbGrLine.setMiddlewareHeaderId(newGrLine.getMiddlewareHeaderId());
                    dbGrLine.setMiddlewareTable(newGrLine.getMiddlewareTable());
                    dbGrLine.setManufacturerFullName(newGrLine.getManufacturerFullName());
                    dbGrLine.setReferenceDocumentType(newGrLine.getReferenceDocumentType());
                    dbGrLine.setPurchaseOrderNumber(newGrLine.getPurchaseOrderNumber());

                    Double recAcceptQty = 0D;
                    Double recDamageQty = 0D;
                    Double variance = 0D;
                    Double invoiceQty = 0D;
                    Double acceptQty = 0D;
                    Double damageQty = 0D;

                    if (newGrLine.getOrderQty() != null) {
                        invoiceQty = newGrLine.getOrderQty();
                    }
                    if (newGrLine.getAcceptedQty() != null) {
                        acceptQty = newGrLine.getAcceptedQty();
                    }
                    if (newGrLine.getDamageQty() != null) {
                        damageQty = newGrLine.getDamageQty();
                    }

                    StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(
                            newGrLine.getCompanyCode(), newGrLine.getPlantId(), newGrLine.getLanguageId(),
                            newGrLine.getWarehouseId(), newGrLine.getPreInboundNo(), newGrLine.getRefDocNumber(),
                            newGrLine.getLineNo(), newGrLine.getItemCode(), newGrLine.getManufacturerName());
                    log.info("StagingLine: " + dbStagingLineEntity);

                    if (dbStagingLineEntity != null) {
                        if (dbStagingLineEntity.getRec_accept_qty() != null) {
                            recAcceptQty = dbStagingLineEntity.getRec_accept_qty();
                        }
                        if (dbStagingLineEntity.getRec_damage_qty() != null) {
                            recDamageQty = dbStagingLineEntity.getRec_damage_qty();
                        }
                        dbGrLine.setOrderUom(dbStagingLineEntity.getOrderUom());
                        dbGrLine.setGrUom(dbStagingLineEntity.getOrderUom());
                    }

                    variance = invoiceQty - (acceptQty + damageQty + recAcceptQty + recDamageQty);
                    log.info("Variance: " + variance);

                    if (variance == 0D) {
                        dbGrLine.setStatusId(17L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(17L,
                                newGrLine.getLanguageId());
                        dbGrLine.setStatusDescription(statusDescription);
                    }

                    if (variance < 0D) {
                        throw new BadRequestException("Variance Qty cannot be Less than 0");
                    }
                    dbGrLine.setConfirmedQty(dbGrLine.getGoodReceiptQty());
                    dbGrLine.setBranchCode(newGrLine.getBranchCode());
                    dbGrLine.setTransferOrderNo(newGrLine.getTransferOrderNo());
                    dbGrLine.setIsCompleted(newGrLine.getIsCompleted());

                    dbGrLine.setIsPutAwayHeaderCreated(0L);
                    dbGrLine.setBarcodeId(newGrLine.getBarcodeId());
                    dbGrLine.setDeletionIndicator(0L);
                    dbGrLine.setCreatedBy(newGrLine.getCreatedBy());
                    dbGrLine.setUpdatedBy(newGrLine.getCreatedBy());
                    dbGrLine.setConfirmedBy(newGrLine.getCreatedBy());
                    dbGrLine.setCreatedOn(new Date());
                    dbGrLine.setUpdatedOn(new Date());
                    dbGrLine.setConfirmedOn(new Date());

                    companyCode = dbGrLine.getCompanyCode();
                    plantId = dbGrLine.getPlantId();
                    languageId = dbGrLine.getLanguageId();
                    warehouseId = dbGrLine.getWarehouseId();
                    refDocNumber = dbGrLine.getRefDocNumber();
                    preInboundNo = dbGrLine.getPreInboundNo();
                    goodsReceiptNo = dbGrLine.getGoodsReceiptNo();

                    GrLineV2 createdGRLine = null;
                    try {
                        log.info("-----b4-----createGRLine : " + dbGrLine);
                        dbGrLine.setIsPutAwayHeaderCreated(0L);
                        createdGRLine = grLineV2Repository.saveAndFlush(dbGrLine);
                        log.info("---after---createdGRLine : " + createdGRLine);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    createdGRLines.add(createdGRLine);

                    // Update staging Line using stored Procedure
                    log.info(companyCode + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + refDocNumber
                            + "|" + preInboundNo + "|" + createdGRLine.getLineNo() + "|" + createdGRLine.getItemCode()
                            + "|" + createdGRLine.getManufacturerName());

                    stagingLineV2Repository.updateStagingLineUpdateNewProc(companyCode, plantId, languageId,
                            warehouseId, refDocNumber, preInboundNo, createdGRLine.getLineNo(),
                            createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), new Date());
                    log.info("stagingLine Status updated using Stored Procedure ");

                    // Update InboundLine using Stored Procedure
                    inboundLineV2Repository.updateInboundLineStatusUpdateNewProc(companyCode, plantId, languageId,
                            warehouseId, refDocNumber, preInboundNo, createdGRLine.getLineNo(),
                            createdGRLine.getItemCode(), createdGRLine.getManufacturerName(), 17L, statusDescription,
                            new Date());
                    log.info("inboundLine Status updated using Stored Procedure ");
                }
                log.info("Records were inserted successfully...");
            }

            // Update GrHeader using stored Procedure
            statusDescription = stagingLineV2Repository.getStatusDescription(17L,
                    createdGRLines.get(0).getLanguageId());
            grHeaderV2Repository.updateGrheaderStatusUpdateProc(companyCode, plantId, languageId, warehouseId,
                    refDocNumber, preInboundNo, goodsReceiptNo, 17L, statusDescription, new Date());
            log.info("GrHeader Status 17 Updating Using Stored Procedure when condition met");
            return createdGRLines;
        } catch (Exception e) {
            // Exception Log
//            createGrLineLog10(newGrLines, e.toString());
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * grLineV2 grLineList is input
     */
    public void createPutAwayHeader(List<GrLineV2> grLineV2List, GrHeaderV2 grHeader) throws ParseException, InvocationTargetException, IllegalAccessException {

        for (GrLineV2 grLine : grLineV2List) {
            if (grLine != null) {
                String companyCode = grLine.getCompanyCode();
                String plantId = grLine.getPlantId();
                String languageId = grLine.getLanguageId();
                String warehouseId = grLine.getWarehouseId();
                String refDocNumber = grLine.getRefDocNumber();
                Long inboundOrderTypeId = grLine.getInboundOrderTypeId();
                try {
                    log.info("----createPutAwayHeaderNonCBMV2---1--- " + grLine);
                    grLineService.createPutAwayHeaderNonCBMV2(grLine, grLine.getCreatedBy());
                    log.info("----createPutAwayHeaderNonCBMV2---2--- ");

                    //putaway header successfully created - changing flag to 10
                    grLineV2Repository.updateGrLineStatusV2(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getPreInboundNo(),
                            grLine.getCreatedOn(), grLine.getLineNo(), grLine.getItemCode(), 10L);
                    log.info("-----GrLine status 10 updated..! ");
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("GrLine status 100 updated - putaway header create - failed..! ");
                    log.error("Exception occurred while create putaway header " + e.toString());
                    grLineV2Repository.updateGrLineStatusV2(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getPreInboundNo(),
                            grLine.getCreatedOn(), grLine.getLineNo(), grLine.getItemCode(), 100L);
//                    grLineService.sendMail(companyCode, plantId, languageId, warehouseId, refDocNumber, getInboundOrderTypeTable(inboundOrderTypeId), e.toString());
                }

                grLineV2Repository.updateGrLineStatusV2(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(), grLine.getWarehouseId(), grLine.getPreInboundNo(),
                        grLine.getCreatedOn(), grLine.getLineNo(), grLine.getItemCode(), 1L);
                log.info("-------GrLine status updated---------");
            }
        }
        List<PutAwayLineV2> createdPutawayLine = null;
        //putaway Confirm
        if (grLineV2List != null && !grLineV2List.isEmpty()) {
            log.info("Putaway line Confirm Initiated");
            List<PutAwayLineV2> createPutawayLine = new ArrayList<>();
//            for (GrLineV2 grLine : createGrLine) {

            List<PutAwayHeaderV2> dbPutawayHeaderList = stockReceiptServiceV2.getPutAwayHeaderV2(
                    grLineV2List.get(0).getCompanyCode(),
                    grLineV2List.get(0).getPlantId(),
                    grLineV2List.get(0).getLanguageId(),
                    grLineV2List.get(0).getWarehouseId(),
                    grLineV2List.get(0).getRefDocNumber());
            log.info("Putaway header: " + dbPutawayHeaderList);

            if (dbPutawayHeaderList != null && !dbPutawayHeaderList.isEmpty()) {
                for (PutAwayHeaderV2 dbPutawayHeader : dbPutawayHeaderList) {
                    PutAwayLineV2 putAwayLine = new PutAwayLineV2();

                    List<GrLineV2> grLine = grLineV2List.stream().filter(n -> n.getPackBarcodes().equalsIgnoreCase(dbPutawayHeader.getPackBarcodes())).collect(Collectors.toList());

                    BeanUtils.copyProperties(grLine.get(0), putAwayLine, CommonUtils.getNullPropertyNames(grLine.get(0)));
                    putAwayLine.setProposedStorageBin(dbPutawayHeader.getProposedStorageBin());
                    putAwayLine.setConfirmedStorageBin(dbPutawayHeader.getProposedStorageBin());
                    putAwayLine.setPutawayConfirmedQty(dbPutawayHeader.getPutAwayQuantity());
                    putAwayLine.setPutAwayNumber(dbPutawayHeader.getPutAwayNumber());
                    createPutawayLine.add(putAwayLine);
                }
            }
//            }
            createdPutawayLine = stockReceiptServiceV2.putAwayLineConfirmV2(createPutawayLine, grHeader.getCreatedBy());
            log.info("PutawayLine Confirmed: " + createdPutawayLine);
        }
        if (createdPutawayLine != null && !createdPutawayLine.isEmpty()) {
            log.info("Direct StockReceipt - Inbound Confirmation Process Initiated");
            stockReceiptServiceV2.updateInboundHeaderPartialConfirmV2(
                    grHeader.getCompanyCode(),
                    grHeader.getPlantId(),
                    grHeader.getLanguageId(),
                    grHeader.getWarehouseId(),
                    grHeader.getPreInboundNo(),
                    grHeader.getRefDocNumber(),
                    grHeader.getCreatedBy());
            log.info("Direct Stock Receipt - Inbound Order Confirmed Successfully");
        }
    }

}
