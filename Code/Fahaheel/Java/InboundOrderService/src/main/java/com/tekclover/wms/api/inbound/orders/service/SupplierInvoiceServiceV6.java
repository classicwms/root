package com.tekclover.wms.api.inbound.orders.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierInvoiceServiceV6 extends BaseService {


    private final RepositoryProvider repo;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ErrorLogService errorLogService;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;


//    @Async("asyncExecutor")
//    public void inboundOrder(List<ASNV2> asnv2List) throws Exception {
//        for (ASNV2 asn : asnv2List) {
//            processInboundReceived(asn);
//        }
//    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processInboundReceived(ASNV2 asnv2) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
//        try {
        // Inboound Order table saved process
//            asnList.forEach(this::saveASNV6);
//            for (ASNV2 asnv2 : asnList) {
        try {
            saveASNV6(asnv2);
            ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
            List<ASNLineV2> lineV2List = asnv2.getAsnLine();
            String companyCode = headerV2.getCompanyCode();
            String plantId = headerV2.getBranchCode();
            String warehouseId = headerV2.getWarehouseId();
            String languageId = headerV2.getLanguageId();
//                    String mfrName = lineV2List.get(0).getManufacturerName();

            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(headerV2.getAsnNumber(), headerV2.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
            }

            // Description_Set
            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;

            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            String stagingNo = getNextRangeNumber(3L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            String caseCode = getNextRangeNumber(4L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            String grNumber = getNextRangeNumber(5L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);
            statusDescription = getStatusDescription(statusId, languageId);

            // Step 1: Create headers before line processing
            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
                    companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, MRF_NAME_V6);
            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

            InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, lineV2List.size());
            log.info("Inbound Header Created: {}", inboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(preInboundHeader, stagingNo);
            log.info("StagingHeader Created: {}", stagingHeader);

            GrHeaderV2 grHeader = createGrHeader(stagingHeader, caseCode, grNumber);
            log.info("GrHeader Created: {}", grHeader);

            repo.preInboundHeaderV2Repository.save(preInboundHeader);
            repo.inboundHeaderV2Repository.save(inboundHeader);
            repo.stagingHeaderV2Repository.save(stagingHeader);
            repo.grHeaderV2Repository.save(grHeader);

            // Collections for batch saving
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());
            List<ImBasicData1V2> imBasicData1V2List = Collections.synchronizedList(new ArrayList<>());

            String partBarCode = generateBarCodeId(grHeader.getRefDocNumber());

            // Process lines in parallel
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(asnv2.getAsnLine().stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            processSingleASNLine(asnv2, asnLineV2, preInboundHeader, stagingHeader, grHeader,
                                    preInboundLineList, inboundLineList, stagingLineList, imBasicData1V2List, partBarCode);
                        } catch (Exception e) {
                            log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService)).toArray(CompletableFuture[]::new));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
            }

            // Batch Save All Records
            if(!imBasicData1V2List.isEmpty()) {
                repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
            }
            repo.preInboundLineV2Repository.saveAll(preInboundLineList);
            repo.inboundLineV2Repository.saveAll(inboundLineList);
            repo.stagingLineV2Repository.saveAll(stagingLineList);

//                    // Normal lists for batch saving
//                    List<PreInboundLineEntityV2> preInboundLineList = new ArrayList<>();
//                    List<InboundLineV2> inboundLineList = new ArrayList<>();
//                    List<StagingLineEntityV2> stagingLineList = new ArrayList<>();
//                    List<ImBasicData1V2> imBasicData1V2List = new ArrayList<>();
//
//                    String partBarCode = generateBarCodeId(grHeader.getRefDocNumber());
//
//                    for (ASNLineV2 asnLineV2 : asnv2.getAsnLine()) {
//                        try {
//                            processSingleASNLine(asnv2, asnLineV2, preInboundHeader, stagingHeader, grHeader,
//                                    preInboundLineList, inboundLineList, stagingLineList, imBasicData1V2List, partBarCode);
//                        } catch (Exception e) {
//                            log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
//                            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
//                        }
//                    }
//
//                  // Batch Save All Records
//                    if (!imBasicData1V2List.isEmpty()) {
//                        repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
//                    }
//                    repo.preInboundLineV2Repository.saveAll(preInboundLineList);
//                    repo.inboundLineV2Repository.saveAll(inboundLineList);
//                    repo.stagingLineV2Repository.saveAll(stagingLineList);


            log.info("InboundOrder Status 10 Updated");

        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);

            errorLogService.createProcessInboundReceivedV2(asnv2, e.getMessage());
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);

            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        }

        finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }
    }


    @Async("asyncExecutor")
//    public void fgInboundOrder(List<ASNV2> asnv2List) throws Exception {
//        for (ASNV2 asnv2 : asnv2List){
//            processInboundReceivedV2(asnv2);
//        }
//    }

    //FG Inbound
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processInboundReceivedV2(ASNV2 asnv2) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");
        // Inbound Order table saved process
        try {
            saveASNV6(asnv2);
            ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
            List<ASNLineV2> lineV2List = asnv2.getAsnLine();
            String companyCode = headerV2.getCompanyCode();
            String plantId = headerV2.getBranchCode();
            String warehouseId = headerV2.getWarehouseId();
            String languageId = headerV2.getLanguageId();
            String itemText = null;
//                    String mfrName = lineV2List.get(0).getManufacturerName();

            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(headerV2.getAsnNumber(), headerV2.getInboundOrderTypeId(), 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
            }

            // Description_Set
            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();

            String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
            Long statusId = 13L;


            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
            String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
            log.info("PreInboundNo : " + preInboundNo);
            statusDescription = getStatusDescription(statusId, languageId);

            // Step 1: Create headers before line processing
            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
                    companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, MRF_NAME_V6);
            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

            repo.preInboundHeaderV2Repository.save(preInboundHeader);
            // Collections for batch saving
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<ImBasicData1V2> imBasicData1V2List = Collections.synchronizedList(new ArrayList<>());

            String partBarCode = generateBarCodeId(preInboundHeader.getRefDocNumber());

            // Process lines in parallel
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(asnv2.getAsnLine().stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            processASNLine(asnv2, asnLineV2, preInboundHeader, preInboundLineList, imBasicData1V2List, partBarCode);
                        } catch (Exception e) {
                            log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService)).toArray(CompletableFuture[]::new));
            try {
                allFutures.join(); // Wait for all tasks to finish
            } catch (CompletionException e) {
                log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
            }

            // Batch Save All Records
            if(!imBasicData1V2List.isEmpty()) {
                repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
            }
            repo.preInboundLineV2Repository.saveAll(preInboundLineList);

        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);

            errorLogService.createProcessInboundReceivedV2(asnv2, e.getMessage());
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                    asnv2.getAsnHeader().getAsnNumber(), 100L);

            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        }finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }

//        } catch (BadRequestException e) {
//            throw new RuntimeException(e);
//        }
    }


    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param mfrName
     * @return
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyId, String languageId, String plantId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                              String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        try {
            PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
            BeanUtils.copyProperties(asnHeaderV2, preInboundHeader, CommonUtils.getNullPropertyNames(asnHeaderV2));
            preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
            preInboundHeader.setWarehouseId(warehouseId);
            preInboundHeader.setCompanyCode(companyId);
            preInboundHeader.setPlantId(plantId);
            preInboundHeader.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
            preInboundHeader.setReferenceDocumentType("Supplier Invoice");    // REF_DOC_TYP - Hard Coded Value "ASN"
            preInboundHeader.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());    // IB_ORD_TYP_ID
            preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
            preInboundHeader.setTransferRequestType("Supplier Invoice");
            preInboundHeader.setStatusId(5L);
            statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, languageId);
            preInboundHeader.setStatusDescription(statusDescription);
            preInboundHeader.setCompanyDescription(companyText);
            preInboundHeader.setPlantDescription(plantText);
            preInboundHeader.setWarehouseDescription(warehouseText);
            preInboundHeader.setMiddlewareId(String.valueOf(asnHeaderV2.getMiddlewareId()));
            preInboundHeader.setMiddlewareTable(asnHeaderV2.getMiddlewareTable());
            preInboundHeader.setManufacturerFullName(mfrName);

            preInboundHeader.setTransferOrderDate(new Date());
            preInboundHeader.setSourceBranchCode(asnHeaderV2.getBranchCode());
            preInboundHeader.setSourceCompanyCode(asnHeaderV2.getCompanyCode());
            preInboundHeader.setMUpdatedOn(asnHeaderV2.getUpdatedOn());

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
    private InboundHeaderV2 createInboundHeader(PreInboundHeaderEntityV2 preInboundHeader, int lineSize) throws Exception {
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
    public StagingHeaderV2 createStagingHeaderV2(PreInboundHeaderEntityV2 preInboundHeader, String stagingNo) throws Exception {
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
    public GrHeaderV2 createGrHeader(StagingHeaderV2 stagingHeader, String caseCode, String grNumber) throws Exception {
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

    //ProcessSingleAsnLine
    private void processSingleASNLine(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                      StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                      List<PreInboundLineEntityV2> preInboundLineList,
                                      List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList, List<ImBasicData1V2> imBasicDataList, String partBarCode) throws Exception {

        asnLineV2.setManufacturerCode(MRF_NAME_V6);
        asnLineV2.setManufacturerName(MRF_NAME_V6);
        asnLineV2.setManufacturerFullName(MRF_NAME_V6);
        Long orderTypeId = grHeaderV2.getInboundOrderTypeId();
        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
        asnLineV2.setNoBags(asnLineV2.getExpectedQty());
        asnLineV2.setNoBags(asnLineV2.getExpectedQtyInCases());
        asnLineV2.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();
        String itemText = null;

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");

        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(asnLineV2.getSku());
            imBasicData1.setUomId(asnLineV2.getUom());
            imBasicData1.setDescription(asnLineV2.getSkuDescription());
            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicDataList.add(imBasicData1); // Collect for batch save
        }


        // Collect PreInboundLine
        PreInboundLineEntityV2 preInboundLine = null;
        InboundLineV2 inboundLineV2 = null;
        List<StagingLineEntityV2> stagingLine = null;

        preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId,
                preInboundHeader.getPreInboundNo(), headerV2, asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(),
                preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription(), partBarCode, itemText);
        preInboundLineList.add(preInboundLine);

        // Collect InboundLine
        inboundLineV2 = createInboundLines(17L, statusDescription, preInboundLine);
        inboundLineList.add(inboundLineV2);

        stagingLineList.add(createStagingLineV2(preInboundLine, grHeaderV2, stagingHeader));
        // Collect StagingLine
//        if (orderTypeId.equals(1L)) {
//            stagingLineList.addAll(createStagingLineV2(preInboundLine, grHeaderV2, stagingHeader));
//        } else {
//            stagingLineList.addAll(createStagingLine(preInboundLine, grHeaderV2, stagingHeader));
//        }
    }


    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @param partBarCode
     * @param itemText
     * @return
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                          ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText,
                                                          String partBarCode, String itemText) {


        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();

        String barcodeId = generateBarCodeId(asnLineV2.getSku(), partBarCode);


        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
        preInboundLine.setInboundOrderTypeId(asnHeaderV2.getInboundOrderTypeId());
        preInboundLine.setBarcodeId(barcodeId + asnLineV2.getLineReference());
//        preInboundLine.setCustomerCode(asnHeaderV2.getCustomerCode());
        preInboundLine.setTransferRequestType("Supplier Invoice");

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(asnLineV2.getLineReference());

        // ITM_CODE
        preInboundLine.setItemCode(asnLineV2.getSku());

//        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
////        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//        ImBasicData1 imBasicData1 =
//                imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                        languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);
        preInboundLine.setItemDescription(asnLineV2.getSkuDescription());

        // MFR_PART
        preInboundLine.setManufacturerPartNo(asnLineV2.getManufacturerName());
        // PARTNER_CODE
        preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
        // ORD_QTY
        preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
        // ORD_UOM
        preInboundLine.setOrderUom(asnLineV2.getUom());
        // STCK_TYP_ID
        preInboundLine.setStockTypeId(1L);

        // SP_ST_IND_ID
        preInboundLine.setSpecialStockIndicatorId(1L);

        // EA_DATE
        log.info("inboundIntegrationLine.getExpectedDate() : " + asnLineV2.getExpectedDate());
        preInboundLine.setExpectedArrivalDate(getExpectedDate(asnLineV2.getExpectedDate()));
        // ITM_CASE_QTY
        preInboundLine.setItemCaseQty(asnLineV2.getPackQty());
        preInboundLine.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundLine.setStatusDescription(statusDescription);

        preInboundLine.setCompanyDescription(companyText);
        preInboundLine.setPlantDescription(plantText);
        preInboundLine.setWarehouseDescription(warehouseText);
        preInboundLine.setOrigin(asnLineV2.getOrigin());
        preInboundLine.setBrandName(asnLineV2.getBrand());
        preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
        preInboundLine.setManufacturerName(asnLineV2.getManufacturerName());
        preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
        preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
        preInboundLine.setSupplierName(asnLineV2.getSupplierName());

        preInboundLine.setMiddlewareId(String.valueOf(asnLineV2.getMiddlewareId()));
        preInboundLine.setMiddlewareHeaderId(String.valueOf(asnLineV2.getMiddlewareHeaderId()));
        preInboundLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
        preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
        preInboundLine.setReferenceDocumentType("Supplier Invoice");
        preInboundLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());

        preInboundLine.setBranchCode(asnLineV2.getBranchCode());
        preInboundLine.setTransferOrderNo(asnHeaderV2.getAsnNumber());
        preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());

        preInboundLine.setDeletionIndicator(0L);
        preInboundLine.setCreatedBy("MW_AMS");
        preInboundLine.setCreatedOn(new Date());

        log.info("preInboundLine : " + preInboundLine);
        return preInboundLine;
    }

    /**
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     * @throws Exception
     */
    public InboundLineV2 createInboundLines(Long statusId, String statusDesc, PreInboundLineEntityV2 preInboundLines) throws Exception {
        try {

            log.info("InboundLines Creation Started ----------------> V6 ");
            InboundLineV2 inboundLine = new InboundLineV2();
            BeanUtils.copyProperties(preInboundLines, inboundLine, CommonUtils.getNullPropertyNames(preInboundLines));
            inboundLine.setDescription(preInboundLines.getItemDescription());
            inboundLine.setStatusId(statusId);
            inboundLine.setStatusDescription(statusDesc);
            inboundLine.setBarcodeId(preInboundLines.getBarcodeId());
            return inboundLine;

        } catch (Exception e) {
            log.error("Exception while InboundLines Create : " + e);
            throw e;
        }
    }

    /**
     * @param preInboundLineEntityV2s
     * @param grHeaderV2
     * @return
     */
    StagingLineEntityV2 createStagingLineV2(PreInboundLineEntityV2 preInboundLineEntityV2s, GrHeaderV2 grHeaderV2, StagingHeaderV2 stagingHeader) {

        StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
        BeanUtils.copyProperties(preInboundLineEntityV2s, stagingLine, CommonUtils.getNullPropertyNames(preInboundLineEntityV2s));
        stagingLine.setStagingNo(stagingHeader.getStagingNo());
        stagingLine.setCaseCode(grHeaderV2.getCaseCode());
        stagingLine.setPalletCode(grHeaderV2.getCaseCode());
        stagingLine.setPartner_item_barcode(preInboundLineEntityV2s.getBarcodeId());
        stagingLine.setBarcodeId(preInboundLineEntityV2s.getBarcodeId());
        stagingLine.setStatusId(14L);
        stagingLine.setStatusDescription(statusDescription);
        stagingLine.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());

        return stagingLine;
    }

    /**
     * @param preInboundLineEntityV2s
     * @param grHeaderV2
     * @param stagingHeader
     * @return
     */
//    List<StagingLineEntityV2> createStagingLine(PreInboundLineEntityV2 preInboundLineEntityV2s, GrHeaderV2 grHeaderV2, StagingHeaderV2 stagingHeader) {
//
//        log.info("FG Order --------> V6 ----------------> StagingLine Creation Started ");
//        List<StagingLineEntityV2> saveStagingLine = new ArrayList<>();
//        Double uomQty = stagingLineV2Repository.getQty(preInboundLineEntityV2s.getCompanyCode(), preInboundLineEntityV2s.getPlantId(),
//                preInboundLineEntityV2s.getWarehouseId(), preInboundLineEntityV2s.getLanguageId(), preInboundLineEntityV2s.getItemCode(), "1");
//
//        log.info("Uom Qty is  ----------> {} ", uomQty);
////        Double uomQty = 10.0;
//        Double orderQty = preInboundLineEntityV2s.getOrderQty();
//        double qty = orderQty / uomQty;
//        int roundedQty = (int) Math.ceil(qty);
//
//        Long lineNumber = preInboundLineEntityV2s.getLineNo();
//        for (int i = 1; i <= roundedQty; i++) {
//
//            StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
//            String partBarCode = generateBarcodeId(preInboundLineEntityV2s.getItemCode(), preInboundLineEntityV2s.getRefDocNumber(), i);
//            BeanUtils.copyProperties(preInboundLineEntityV2s, stagingLine, CommonUtils.getNullPropertyNames(preInboundLineEntityV2s));
//            stagingLine.setStagingNo(stagingHeader.getStagingNo());
//            stagingLine.setCaseCode(grHeaderV2.getCaseCode());
//            stagingLine.setPalletCode(grHeaderV2.getCaseCode());
//
//            stagingLine.setLineNo(lineNumber);
//            stagingLine.setPartner_item_barcode(partBarCode);
//            stagingLine.setBarcodeId(partBarCode);
//            stagingLine.setStatusId(14L);
//            stagingLine.setStatusDescription(statusDescription);
//            stagingLine.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());
//
//            //orderQty for each line
//            if (i < roundedQty) {
//                stagingLine.setOrderQty(uomQty);// 10 qty
//            } else {
//                // Last line gets the balance qty
//                double usedQty = uomQty * (roundedQty - 1); // 10 * (11-1) = 100
//                double lastQty = orderQty - usedQty; // 105 - 100 = 5
//                stagingLine.setOrderQty(lastQty); // 5
//            }
//            lineNumber++;
//            saveStagingLine.add(stagingLine);
//        }
//        return saveStagingLine;
//    }
//-------------------------------------------------IbOrder-----------------------------------------------------------------------

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveASNV6(ASNV2 asnv2) {
        try {
            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();
            InboundOrderV2 apiHeader = new InboundOrderV2();
            BeanUtils.copyProperties(asnV2Header, apiHeader, CommonUtils.getNullPropertyNames(asnV2Header));

            apiHeader.setOrderId(asnV2Header.getAsnNumber());
            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
            apiHeader.setBranchCode(asnV2Header.getBranchCode());
            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());
            apiHeader.setOrderReceivedOn(new Date());
            apiHeader.setRefDocumentType("SUPPLIER INVOICE");
            apiHeader.setLanguageId(asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID);
            if (asnV2Header.getWarehouseId() != null && !asnV2Header.getWarehouseId().isBlank()) {
                apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
            } else {
                // Get Warehouse
                Optional<Warehouse> dbWarehouse =
                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
                                asnV2Header.getCompanyCode(),
                                asnV2Header.getBranchCode(),
                                asnV2Header.getLanguageId() != null ? asnV2Header.getLanguageId() : LANG_ID,
                                0L
                        );
                log.info("dbWarehouse : " + dbWarehouse);
                apiHeader.setWarehouseID(dbWarehouse.get().getWarehouseId());
            }

            if (asnV2Header.getInboundOrderTypeId() != null) {
                apiHeader.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
            } else {
                apiHeader.setInboundOrderTypeId(1L);                                            //Default
            }

            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
            for (ASNLineV2 asnLineV2 : asnLineV2s) {
                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
                BeanUtils.copyProperties(asnLineV2, apiLine, CommonUtils.getNullPropertyNames(asnLineV2));
                apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                if (asnLineV2.getSupplierCode() != null) {
                    apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                } else {
                    apiLine.setSupplierCode(asnV2Header.getSupplierCode());
                }
                apiLine.setManufacturerCode(MRF_NAME_V6);
                apiLine.setManufacturerName(MRF_NAME_V6);
                apiLine.setManufacturerPartNo(MRF_NAME_V6);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(MRF_NAME_V6);
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }

//                if (asnLineV2.getExpectedQtyInCases() > 0.0 && asnLineV2.getExpectedQtyInPieces() > 0.0) {
//                    Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                    apiLine.setNoBags(asnLineV2.getExpectedQtyInCases());
//                } else {
//                    Double ordQty = asnLineV2.getExpectedQty() / asnLineV2.getNoBags();  // 50 / 2 => 25
//                    apiLine.setExpectedQty(ordQty);     // 25
//                    apiLine.setOrderedQty(ordQty);      // 25
//                    apiLine.setBagSize(ordQty);
//                }

                apiLine.setExpectedQty(asnLineV2.getExpectedQty());     // 25
                apiLine.setOrderedQty(asnLineV2.getExpectedQty());      // 25
//                    apiLine.setBagSize(ordQty);         // 25
//                    apiLine.setNoBags(asnLineV2.getExpectedQtyInCases());
                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());

                if (asnLineV2.getExpectedDate() != null) {
                    if (asnLineV2.getExpectedDate().contains("-")) {
                        // EA_DATE
                        try {
                            Date reqDelDate = new Date();
                            if (asnLineV2.getExpectedDate().length() > 10) {
                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
                            }
                            if (asnLineV2.getExpectedDate().length() == 10) {
                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
                            }
                            apiLine.setExpectedDate(reqDelDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new BadRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                    if (asnLineV2.getExpectedDate().contains("/")) {
                        // EA_DATE
                        try {
                            ZoneId defaultZoneId = ZoneId.systemDefault();
                            String sdate = asnLineV2.getExpectedDate();
                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
                            secondHalf = "/20" + secondHalf;
                            sdate = firstHalf + secondHalf;
                            log.info("sdate--------> : " + sdate);

                            LocalDate localDate = DateUtils.dateConv2(sdate);
                            log.info("localDate--------> : " + localDate);
                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                            apiLine.setExpectedDate(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new InboundOrderRequestException("Date format should be yyyy-MM-dd");
                        }
                    }
                }

                apiLine.setPackQty(asnLineV2.getPackQty());                    // ITM_CASE_QTY
                orderLines.add(apiLine);
            }
            apiHeader.setLine(orderLines);
            apiHeader.setOrderProcessedOn(new Date());

            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
                apiHeader.setProcessedStatusId(10L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
//                return createdOrder;
            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
                // throw the error as Lines are Empty and set the Indicator as '100'
                apiHeader.setProcessedStatusId(100L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Failed : " + createdOrder);
                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
            }
        } catch (Exception e) {
            throw e;
        }
//        return null;
    }


    /**
     * @param newInboundOrderV2
     * @return
     */
    public InboundOrderV2 createInboundOrdersV2(InboundOrderV2 newInboundOrderV2) {

        try {

            InboundOrderV2 dbInboundOrder = inboundOrderV2Repository.
                    findByRefDocumentNoAndInboundOrderTypeId(newInboundOrderV2.getOrderId(), newInboundOrderV2.getInboundOrderTypeId());
            if (dbInboundOrder != null) {
                throw new BadRequestException("Order is getting Duplicated");
            }

            log.info("Data Base {} ", DataBaseContextHolder.getCurrentDb());
            InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
            log.info("inboundOrderV2 ----> {}", inboundOrderV2);

            return inboundOrderV2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param companyCode
     * @param branchId
     * @param warehouseId
     * @param asnNumber
     * @param statusId
     */
    public void updateStatusId(String companyCode, String branchId, String warehouseId, String asnNumber, Long statusId) {

        inboundOrderV2Repository.updateIbOrderStatus(companyCode, branchId, warehouseId, asnNumber, statusId);
    }



    //-------------------------------------------------------FG-------------------------------------------------------------

    //FG Inbound
    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processInboundReceivedV2(List<ASNV2> asnList) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("BP");
            // Inbound Order table saved process
            asnList.forEach(this::saveASNV6);
            for (ASNV2 asnv2 : asnList) {
                try {
                    ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
                    List<ASNLineV2> lineV2List = asnv2.getAsnLine();
                    String companyCode = headerV2.getCompanyCode();
                    String plantId = headerV2.getBranchCode();
                    String warehouseId = headerV2.getWarehouseId();
                    String languageId = headerV2.getLanguageId();
                    String itemText = null;
//                    String mfrName = lineV2List.get(0).getManufacturerName();

                    Optional<PreInboundHeaderEntityV2> orderProcessedStatus = preInboundHeaderV2Repository.
                            findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(headerV2.getAsnNumber(), headerV2.getInboundOrderTypeId(), 0L);
                    if (!orderProcessedStatus.isEmpty()) {
                        throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
                    }

                    // Description_Set
                    IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
                    String companyText = description.getCompanyDesc();
                    String plantText = description.getPlantDesc();
                    String warehouseText = description.getWarehouseDesc();

                    String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
                    Long statusId = 13L;


                    // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
                    String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
                    log.info("PreInboundNo : " + preInboundNo);
                    statusDescription = getStatusDescription(statusId, languageId);

                    // Step 1: Create headers before line processing
                    PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
                            companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, MRF_NAME_V6);
                    log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

                    repo.preInboundHeaderV2Repository.save(preInboundHeader);
                    // Collections for batch saving
                    List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
                    List<ImBasicData1V2> imBasicData1V2List = Collections.synchronizedList(new ArrayList<>());

                    String partBarCode = generateBarCodeId(preInboundHeader.getRefDocNumber());

                    // Process lines in parallel
                    CompletableFuture<Void> allFutures = CompletableFuture.allOf(asnv2.getAsnLine().stream()
                            .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                                try {
                                    processASNLine(asnv2, asnLineV2, preInboundHeader, preInboundLineList, imBasicData1V2List, partBarCode);
                                } catch (Exception e) {
                                    log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
                                    throw new RuntimeException(e);
                                }
                            }, executorService)).toArray(CompletableFuture[]::new));
                    try {
                        allFutures.join(); // Wait for all tasks to finish
                    } catch (CompletionException e) {
                        log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
                        throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
                    }

                    // Batch Save All Records
                    if(!imBasicData1V2List.isEmpty()) {
                        repo.imBasicData1V2Repository.saveAll(imBasicData1V2List);
                    }
                    repo.preInboundLineV2Repository.saveAll(preInboundLineList);

                } catch (Exception e) {
                    log.error("Error processing inbound ASN Lines", e);

                    errorLogService.createProcessInboundReceivedV2(asnv2, e.getMessage());
                    updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                            asnv2.getAsnHeader().getAsnNumber(), 100L);

                    DataBaseContextHolder.clear();
                    DataBaseContextHolder.setCurrentDb("MT");
                    updateStatusId(asnv2.getAsnHeader().getCompanyCode(), asnv2.getAsnHeader().getBranchCode(), asnv2.getAsnHeader().getWarehouseId(),
                            asnv2.getAsnHeader().getAsnNumber(), 100L);

                    throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
                }
            }


        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        } finally {
            executorService.shutdown();
            DataBaseContextHolder.clear();
        }
    }


    //ProcessSingleAsnLine

    /**
     *
     * @param asnv2
     * @param asnLineV2
     * @param preInboundHeader
     * @param preInboundLineList
     * @param imBasicDataList
     * @param partBarCode
     * @throws Exception
     */
    private void processASNLine(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                List<PreInboundLineEntityV2> preInboundLineList,
                                List<ImBasicData1V2> imBasicDataList, String partBarCode) throws Exception {

        asnLineV2.setManufacturerCode(MRF_NAME_V6);
        asnLineV2.setManufacturerName(MRF_NAME_V6);
        asnLineV2.setManufacturerFullName(MRF_NAME_V6);
        Long orderTypeId = preInboundHeader.getInboundOrderTypeId();
        asnLineV2.setExpectedQty(asnLineV2.getExpectedQty());
        asnLineV2.setNoBags(asnLineV2.getExpectedQty());
        asnLineV2.setNoBags(asnLineV2.getExpectedQtyInCases());
        asnLineV2.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();
        String itemText = null;

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("BP");

        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(asnLineV2.getSku());
            imBasicData1.setUomId(asnLineV2.getUom());
            imBasicData1.setDescription(asnLineV2.getSkuDescription());
            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicDataList.add(imBasicData1); // Collect for batch save
        }

        PreInboundLineEntityV2  preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId,
                preInboundHeader.getPreInboundNo(), headerV2, asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(),
                preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription(), partBarCode, itemText);
        preInboundLineList.add(preInboundLine);

    }


    /**
     *
     * @param preInboundNo
     * @param refDocNumber
     */
//    @Transactional
//    public void processInbound(String preInboundNo, String refDocNumber) {
//
//        PreInboundHeaderEntityV2 preInboundHeader = preInboundHeaderV2Repository.getPreInboundHeader(preInboundNo,refDocNumber);
//        List<PreInboundLineEntityV2> preInboundLines = repo.preInboundLineV2Repository.getPreInboundLine(preInboundNo,refDocNumber);
//
//        try {
//
//            log.info("PreInboundOrderLines size is -----> {} ", preInboundLines.size());
//            if (preInboundHeader !=null && !preInboundLines.isEmpty()) {
//                String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
//                // Getting StagingNo, CaseCode from NumberRangeTable
//                String stagingNo = getNextRangeNumber(3L, preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), preInboundHeader.getWarehouseId(), idMasterAuthToken);
//                String caseCode = getNextRangeNumber(4L, preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), preInboundHeader.getWarehouseId(), idMasterAuthToken);
//                String grNumber = getNextRangeNumber(5L, preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), preInboundHeader.getWarehouseId(), idMasterAuthToken);
//                log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + stagingNo + ", " + caseCode + ", " + grNumber);
//
//                InboundHeaderV2 duplicateOrder = repo.inboundHeaderV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndInboundOrderTypeIdAndDeletionIndicator(
//                        preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(), preInboundHeader.getLanguageId(), preInboundHeader.getWarehouseId(),
//                        preInboundHeader.getRefDocNumber(),preInboundHeader.getPreInboundNo(), preInboundHeader.getInboundOrderTypeId(),0L);
//                if (duplicateOrder == null) {
//                    InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, preInboundLines.size());
//                    log.info("Inbound Header Created: {}", inboundHeader);
//
//                    StagingHeaderV2 stagingHeader = createStagingHeaderV2(preInboundHeader, stagingNo);
//                    log.info("StagingHeader Created: {}", stagingHeader);
//
//                    GrHeaderV2 grHeader = createGrHeader(stagingHeader, caseCode, grNumber);
//                    log.info("GrHeader Created: {}", grHeader);
//
//                    repo.inboundHeaderV2Repository.save(inboundHeader);
//                    repo.stagingHeaderV2Repository.save(stagingHeader);
//                    repo.grHeaderV2Repository.save(grHeader);
//
//                    List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
//                    List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());
//
//                    // Process lines in parallel
//                    CompletableFuture<Void> allFutures = CompletableFuture.allOf(preInboundLines.stream()
//                            .map(preInboundLine -> CompletableFuture.runAsync(() -> {
//                                try {
//                                    processInboundLines(preInboundLine, stagingHeader, grHeader,
//                                            inboundLineList, stagingLineList);
//                                } catch (Exception e) {
//                                    log.error("Error processing Line for RefDocNumber: {}", preInboundHeader.getRefDocNumber(), e);
//                                    throw new RuntimeException(e);
//                                }
//                            })).toArray(CompletableFuture[]::new));
//                    try {
//                        allFutures.join(); // Wait for all tasks to finish
//                    } catch (CompletionException e) {
//                        log.error("Exception during line processing: {}", e.getCause().getMessage());
//                        throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//                    }
//
//                    // Batch Save All Records
//                    repo.inboundLineV2Repository.saveAll(inboundLineList);
//                    repo.stagingLineV2Repository.saveAll(stagingLineList);
//                }else {
//                    throw new Exception("The given preInboundNo and refDocNumber already exits");
//                }
//            }else {
//                throw new Exception("Record not found");
//            }
//
//        } catch (Exception e) {
//            log.error("Error processing inbound Lines", e);
//
//            updateStatusId(preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(),
//                    preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(), 100L);
//
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("MT");
//            updateStatusId(preInboundHeader.getCompanyCode(), preInboundHeader.getPlantId(),
//                    preInboundHeader.getWarehouseId(), preInboundHeader.getRefDocNumber(), 100L);
//
//            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
//        }
//
//    }

//    /**
//     *
//     * @param preInboundLines
//     * @param stagingHeader
//     * @param grHeaderV2
//     * @param inboundLineList
//     * @param stagingLineList
//     * @throws Exception
//     */
////    private void processInboundLines(PreInboundLineEntityV2 preInboundLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
//                                     List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList) throws Exception {
//
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//
//        try {
//            InboundLineV2 inboundLineV2 = null;
//                // Collect InboundLine
//                inboundLineV2 = createInboundLines(17L, statusDescription, preInboundLine);
//                inboundLineList.add(inboundLineV2);
//
//                // Collect StagingLine
//                stagingLineList.addAll(createStagingLine(preInboundLine, grHeaderV2, stagingHeader));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}