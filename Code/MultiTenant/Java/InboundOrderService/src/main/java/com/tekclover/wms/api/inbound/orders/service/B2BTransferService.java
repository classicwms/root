package com.tekclover.wms.api.inbound.orders.service;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.B2bTransferIn;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.B2bTransferInHeader;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.B2bTransferInLine;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
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

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class B2BTransferService {

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    MastersService mastersService;
    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    String statusDescription = null;
    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;
    @Autowired
    InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    ImBasicData1Repository imBasicData1Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    GrHeaderV2Repository grHeaderRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    private static String WAREHOUSEID_NUMBERRANGE = "110";


    /**
     *
     * @param b2bTransferIn
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<B2bTransferIn> processInboundReceivedV2(List<B2bTransferIn> b2bTransferIn) {

        List<B2bTransferIn> processB2bTransfer = Collections.synchronizedList(new ArrayList<>());
        log.info("Inbound Process Started for {} B2b Transfer", b2bTransferIn.size());

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            for (B2bTransferIn b2bV2 : b2bTransferIn) {
                B2bTransferInHeader headerV2 = b2bV2.getB2bTransferInHeader();
                List<B2bTransferInLine> lineV2List = b2bV2.getB2bTransferLine();
                String companyCode = headerV2.getCompanyCode();
                String plantId = headerV2.getBranchCode();
                String mfrName = lineV2List.get(0).getManufacturerFullName();
                String refDocNumber = headerV2.getTransferOrderNumber();

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

                //Checking whether received refDocNumber processed already.
                Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                        findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(refDocNumber, 4L, 0L);

                if (!orderProcessedStatus.isEmpty()) {
                    log.info("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
                    continue;
//                    throw new BadRequestException("Order :" + refDocNumber + " already processed. Reprocessing can't be allowed.");
                }

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

                // Process lines in parallel
                List<CompletableFuture<Void>> futures = lineV2List.stream()
                        .map(srLineV2 -> CompletableFuture.runAsync(() -> {
                            try {
                                processSingleB2bTransferInLine(b2bV2, srLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
                                        imBasicDataList, preInboundLineList, inboundLineList, stagingLineList);
                            } catch (Exception e) {
                                log.error("Error processing Sales Return Line for IBSor: {}", headerV2.getTransferOrderNumber(), e);
                                throw new RuntimeException(e);
                            }
                        }, executorService))
                        .collect(Collectors.toList());

                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

                try {
                    allFutures.join(); // Wait for all tasks to finish
                } catch (CompletionException e) {
                    log.error("Exception during Sales Return line processing: {}", e.getCause().getMessage());
                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
                }

                // Batch Save All Records
                imBasicData1V2Repository.saveAll(imBasicDataList);
                preInboundLineV2Repository.saveAll(preInboundLineList);
                inboundLineV2Repository.saveAll(inboundLineList);
                stagingLineV2Repository.saveAll(stagingLineList);

                processB2bTransfer.add(b2bV2);
            }
        } catch (Exception e) {
            log.error("Error processing inbound b2b transfer Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Inbound Process Completed for {} B2B Transfer In", processB2bTransfer.size());
        return processB2bTransfer;

    }

    /**
     *
     * @param b2bV2
     * @param srLineV2
     * @param preInboundHeader
     * @param inboundHeader
     * @param stagingHeader
     * @param grHeader
     * @param imBasicDataList
     * @param preInboundLineList
     * @param inboundLineList
     * @param stagingLineList
     */
    private void processSingleB2bTransferInLine(B2bTransferIn b2bV2, B2bTransferInLine srLineV2, PreInboundHeaderEntityV2 preInboundHeader, InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeader, List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList, List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList) {
        B2bTransferInHeader headerV2 = b2bV2.getB2bTransferInHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);

//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        // Check and collect IMBASICDATA1
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, srLineV2.getSku().trim(), srLineV2.getManufacturerFullName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(srLineV2.getSku());
            imBasicData1.setUomId(srLineV2.getUom());
            imBasicData1.setDescription(srLineV2.getSkuDescription());
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
        StagingLineEntityV2 stagingLine = createStagingLineV2(preInboundLine, stagingHeader, grHeader);
        stagingLineList.add(stagingLine);
    }

    /**
     *
     * @param preInboundLine
     * @param stagingHeader
     * @param grHeader
     * @return
     */
    private StagingLineEntityV2 createStagingLineV2(PreInboundLineEntityV2 preInboundLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeader) {
        StagingLineEntityV2 dbStagingLineEntity = new StagingLineEntityV2();
        BeanUtils.copyProperties(preInboundLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(preInboundLine));
        dbStagingLineEntity.setCaseCode(grHeader.getCaseCode());
        dbStagingLineEntity.setPalletCode(grHeader.getCaseCode());    //Copy CASE_CODE
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
//        StagingLineEntityV2 createStagingLine = stagingLineV2Repository.save(dbStagingLineEntity);
//        log.info("created StagingLine records." + createStagingLine);
        //update INV_QTY in stagingLine - calling stored procedure
//        stagingLineV2Repository.updateStagingLineInvQtyUpdateProc(stagingHeader.getCompanyCode(), stagingHeader.getPlantId(),
//                stagingHeader.getLanguageId(), stagingHeader.getWarehouseId(), stagingHeader.getRefDocNumber(), preIBLine.getPreInboundNo());
        return dbStagingLineEntity;
    }

    /**
     *
     * @param preInboundLine
     * @param inboundHeader
     * @return
     */
    private InboundLineV2 createInboundLine(PreInboundLineEntityV2 preInboundLine, InboundHeaderV2 inboundHeader) {
        InboundLineV2 inboundLine = new InboundLineV2();
        BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));

        inboundLine.setOrderQty(preInboundLine.getOrderQty());
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
//        inboundLineV2Repository.save(inboundLine);
        return inboundLine;
    }

    /**
     *
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
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, B2bTransferInHeader headerV2, B2bTransferInLine srLineV2, String warehouseId, String companyDescription, String plantDescription, String warehouseDescription) {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();

        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setRefDocNumber(headerV2.getTransferOrderNumber());
        preInboundLine.setInboundOrderTypeId(3L);
//        preInboundLine.setCustomerCode(asnHeaderV2.getCustomerCode());
        preInboundLine.setTransferRequestType("Non-WMS to WMS");

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(srLineV2.getLineReference());

        // ITM_CODE
        preInboundLine.setItemCode(srLineV2.getSku());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, srLineV2.getSku(), srLineV2.getManufacturerFullName(), 0L);
        preInboundLine.setItemDescription(srLineV2.getSkuDescription());

        // MFR_PART
        preInboundLine.setManufacturerPartNo(srLineV2.getManufacturerFullName());
        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(srLineV2.getManufacturerCode());
        // ORD_QTY
        preInboundLine.setOrderQty(srLineV2.getExpectedQty());
        // ORD_UOM
        preInboundLine.setOrderUom(srLineV2.getUom());
        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
        log.info("inboundIntegrationLine.getExpectedDate() : " + srLineV2.getExpectedDate());

        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(srLineV2.getExpectedQty());
        preInboundLine.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        preInboundLine.setCompanyDescription(companyDescription);
        preInboundLine.setPlantDescription(plantDescription);
        preInboundLine.setWarehouseDescription(warehouseDescription);
        preInboundLine.setManufacturerCode(srLineV2.getManufacturerCode());
        preInboundLine.setManufacturerName(srLineV2.getManufacturerFullName());
        preInboundLine.setPartnerItemNo(srLineV2.getManufacturerCode());
        preInboundLine.setSupplierName(srLineV2.getSupplierName());

        preInboundLine.setMiddlewareId(String.valueOf(srLineV2.getMiddlewareId()));
        preInboundLine.setMiddlewareHeaderId(String.valueOf(srLineV2.getMiddlewareHeaderId()));
        preInboundLine.setMiddlewareTable(srLineV2.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(srLineV2.getSupplierPartNumber());
        preInboundLine.setReferenceDocumentType("B2b Transfer In");
        preInboundLine.setManufacturerFullName(srLineV2.getManufacturerFullName());
        preInboundLine.setTransferOrderNo(srLineV2.getTransferOrderNo());
        preInboundLine.setIsCompleted(srLineV2.getIsCompleted());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

    /**
     *
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
     *
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
        WAREHOUSEID_NUMBERRANGE = preInboundHeader.getWarehouseId();
        String nextRangeNumber = getNextRangeNumber(NUMBER_RANGE_CODE,
                preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), WAREHOUSEID_NUMBERRANGE,
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
     *
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
     *
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
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyCode, String languageId, String plantId, String preInboundNo, B2bTransferInHeader headerV2, String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(headerV2, preInboundHeader, CommonUtils.getNullPropertyNames(headerV2));
        preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
        preInboundHeader.setWarehouseId(warehouseId);
        preInboundHeader.setCompanyCode(companyCode);
        preInboundHeader.setPlantId(plantId);
        preInboundHeader.setRefDocNumber(headerV2.getTransferOrderNumber());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType("Non-WMS to WMS");    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(3L);    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
        preInboundHeader.setTransferRequestType("B2B Transfer In");
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

}
