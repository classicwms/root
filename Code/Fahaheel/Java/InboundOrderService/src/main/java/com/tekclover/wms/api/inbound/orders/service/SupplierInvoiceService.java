package com.tekclover.wms.api.inbound.orders.service;


import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.InboundOrderRequestException;
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
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
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

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SupplierInvoiceService {

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;
    @Autowired
    WarehouseRepository warehouseRepository;
    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;
    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;
    @Autowired
    StagingLineV2Repository stagingLineV2Repository;
    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;
    @Autowired
    InboundLineV2Repository inboundLineV2Repository;
    @Autowired
    ImBasicData1Repository imBasicData1Repository;
    @Autowired
    GrHeaderV2Repository grHeaderRepository;
    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    MastersService mastersService;
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    OrderService orderService;

    @Autowired
    DbConfigRepository dbConfigRepository;

    private static String WAREHOUSEID_NUMBERRANGE = "110";

    // StatusDescription
    String statusDescription = null;

    public String getAuthToken() {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        return authTokenForMastersService.getAccess_token();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<ASNV2> processInboundReceivedV2(List<ASNV2> asnv2List) throws Exception {

        List<ASNV2> processedAsns = Collections.synchronizedList(new ArrayList<>());
        log.info("Inbound Process Started for {} ASNs", asnv2List.size());

        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            for (ASNV2 asnv2 : asnv2List) {
                ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
                List<ASNLineV2> lineV2List = asnv2.getAsnLine();
                String companyCode = headerV2.getCompanyCode();
                String plantId = headerV2.getBranchCode();
                String mfrName = lineV2List.get(0).getManufacturerName();

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

                // Process lines in parallel
                List<CompletableFuture<Void>> futures = lineV2List.stream()
                        .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                            try {
                                processSingleASNLine(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
                                        imBasicDataList, preInboundLineList, inboundLineList, stagingLineList);
                            } catch (Exception e) {
                                log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
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

                processedAsns.add(asnv2);
            }
        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
        log.info("Inbound Process Completed for {} ASNs", processedAsns.size());
        return processedAsns;
    }


    //ProcessSingleAsnLine
    private void processSingleASNLine(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                      InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                      List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList,
                                      List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList) throws Exception {

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        // Check and collect IMBASICDATA1
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);

        log.info("ImBasicData1 Values {} ", imBasicData1);
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
        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId, preInboundHeader.getPreInboundNo(), headerV2,
                asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(), preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription());
        preInboundLineList.add(preInboundLine);

        // Collect InboundLine
        InboundLineV2 inboundLineV2 = createInboundLine(preInboundLine, inboundHeader);
        inboundLineList.add(inboundLineV2);

        // Collect StagingLine
        StagingLineEntityV2 stagingLine = createStagingLineV2(preInboundLine, stagingHeader, grHeaderV2);
        stagingLineList.add(stagingLine);
    }


//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
//    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
//            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
//    public List<ASNV2> processInboundReceivedV2(List<ASNV2> asnv2List)
//            throws Exception {
//
//        List<ASNV2> processedAsns = Collections.synchronizedList(new ArrayList<>());
//        log.info("Inbound Process Started for {} ASNs", asnv2List.size());
//
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        try {
//            for (ASNV2 asnv2 : asnv2List) {
//                ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
//                List<ASNLineV2> lineV2List = asnv2.getAsnLine();
//                String companyCode = headerV2.getCompanyCode();
//                String plantId = headerV2.getBranchCode();
//                String mfrName = lineV2List.get(0).getManufacturerName();
//                int lineSize = lineV2List.size();
//
//                // Get Warehouse
//                Optional<Warehouse> dbWarehouse =
//                        warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
//                String warehouseId = dbWarehouse.get().getWarehouseId();
//                String languageId = dbWarehouse.get().getLanguageId();
//                log.info("Warehouse ID: {}", warehouseId);
//
//                // Description_Set
//                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
//                String companyText = description.getCompanyDesc();
//                String plantText = description.getPlantDesc();
//                String warehouseText = description.getWarehouseDesc();
//
//                // Getting PreInboundNo from NumberRangeTable
//                String preInboundNo = getPreInboundNo(warehouseId, companyCode, plantId, languageId);
//
//                // Step 1: Create headers before line processing
//                PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
//                        companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
//                log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());
//
//                InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, companyText, plantText, warehouseText, lineSize);
//                log.info("Inbound Header Created: {}", inboundHeader);
//
//                StagingHeaderV2 stagingHeader = createStagingHeader(preInboundHeader, companyText, plantText, warehouseText);
//                log.info("StagingHeader Created: {}", stagingHeader);
//
//                GrHeaderV2 createGrHeader = createGrHeader(preInboundHeader, stagingHeader);
//                log.info("GrHeader Created: {}", createGrHeader);
//
//                List<CompletableFuture<Void>> futures = lineV2List.stream()
//                        .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
//                            try {
//                                processSingleASNLine(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, createGrHeader);
//                            } catch (Exception e) {
//                                log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
//                                throw new RuntimeException(e);
//                            }
//                        }, executorService))
//                        .collect(Collectors.toList());
//
//                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//                try {
//                    allFutures.join(); // Wait for all tasks to finish
//                } catch (CompletionException e) {
//                    log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
//                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//                }
//                processedAsns.add(asnv2);
//            }
//        } catch (Exception e) {
//            log.error("Error processing inbound ASN Lines", e);
//            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//        }
//
//        log.info("Inbound Process Completed for {} ASNs", processedAsns.size());
//        return processedAsns;
//    }


//    /**
//     * @param asnv2
//     * @param asnLineV2
//     * @param preInboundHeader
//     * @param inboundHeader
//     * @param stagingHeader
//     * @throws Exception
//     */
//    private void processSingleASNLine(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
//                                      InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2) throws Exception {
//
//        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
//        String companyCode = headerV2.getCompanyCode();
//        String plantId = headerV2.getBranchCode();
//        String warehouseId = preInboundHeader.getWarehouseId();
//        String languageId = preInboundHeader.getLanguageId();
//        log.info("Processing ASN Line: {}", asnLineV2);
//
//        // Check if IMBASICDATA1 exists for this line, create if not
//        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
//                languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);
//
//        if (imBasicData1 == null) {
//            imBasicData1 = new ImBasicData1V2();
//            imBasicData1.setLanguageId(languageId);
//            imBasicData1.setWarehouseId(warehouseId);
//            imBasicData1.setCompanyCodeId(companyCode);
//            imBasicData1.setPlantId(plantId);
//            imBasicData1.setItemCode(asnLineV2.getSku());
//            imBasicData1.setUomId(asnLineV2.getUom());
//            imBasicData1.setDescription(asnLineV2.getSkuDescription());
//            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
//            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
//            imBasicData1.setCapacityCheck(false);
//            imBasicData1.setDeletionIndicator(0L);
//            imBasicData1.setStatusId(1L);
//            mastersService.createImBasicData1V2(imBasicData1, "MW_AMS", getAuthToken());
//            log.info("Created new ImBasicData1 for SKU: {}", asnLineV2.getSku());
//        }
//
//        // Create PreInboundLine for this ASN Line
//        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId, preInboundHeader.getPreInboundNo(), headerV2,
//                asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(), preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription());
//        preInboundLineV2Repository.save(preInboundLine);
//        log.info("PreInboundLine created for ASN Line: {}", asnLineV2.getSku());
//
//        InboundLineV2 inboundLineV2 = createInboundLine(preInboundLine, inboundHeader);
//        log.info("InboundLine Created for ASN Line: {}", inboundLineV2);
//
//        StagingLineEntityV2 stagingLine = createStagingLineV2(preInboundLine, stagingHeader, grHeaderV2);
//        log.info("StagingLine created for ASN Line: {}", stagingLine);
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


    /**
     * @param companyId
     * @param languageId
     * @param plantId
     * @param preInboundNo
     * @param asnHeaderV2
     * @param warehouseId
     * @return
     * @throws ParseException
     */
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyId, String languageId, String plantId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                              String warehouseId, String companyText, String plantText, String warehouseText, String mfrName) {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(asnHeaderV2, preInboundHeader, CommonUtils.getNullPropertyNames(asnHeaderV2));
        preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
        preInboundHeader.setWarehouseId(warehouseId);
        preInboundHeader.setCompanyCode(companyId);
        preInboundHeader.setPlantId(plantId);
        preInboundHeader.setRefDocNumber(asnHeaderV2.getAsnNumber());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType("Supplier Invoice");    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(1L);    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
        preInboundHeader.setTransferRequestType("Supplier Invoice");
        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundHeader.setStatusDescription(statusDescription);
        preInboundHeader.setCompanyDescription(companyText);
        preInboundHeader.setPlantDescription(plantText);
        preInboundHeader.setWarehouseDescription(warehouseText);
        preInboundHeader.setMiddlewareId(String.valueOf(asnHeaderV2.getMiddlewareId()));
        preInboundHeader.setMiddlewareTable(asnHeaderV2.getMiddlewareTable());
        preInboundHeader.setManufacturerFullName(mfrName);
//        preInboundHeader.setContainerNo(asnHeaderV2.getContainerNo());

        preInboundHeader.setTransferOrderDate(new Date());
        preInboundHeader.setSourceBranchCode(asnHeaderV2.getBranchCode());
        preInboundHeader.setSourceCompanyCode(asnHeaderV2.getCompanyCode());
        preInboundHeader.setIsCompleted(asnHeaderV2.getIsCompleted());
        preInboundHeader.setIsCancelled(asnHeaderV2.getIsCancelled());
        preInboundHeader.setMUpdatedOn(asnHeaderV2.getUpdatedOn());

        preInboundHeader.setDeletionIndicator(0L);
        preInboundHeader.setCreatedBy("MW_AMS");
        preInboundHeader.setCreatedOn(new Date());
        PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
    }

    /**
     * @param preInboundHeader
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private InboundHeaderV2 createInboundHeader(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText, int lineSize) {
        InboundHeaderV2 inboundHeader = new InboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
        inboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
        inboundHeader.setStatusDescription(statusDescription);
        inboundHeader.setCompanyDescription(companyText);
        inboundHeader.setPlantDescription(plantText);
        inboundHeader.setWarehouseDescription(warehouseText);
        inboundHeader.setCountOfOrderLines((long) lineSize);       //count of lines
        inboundHeader.setDeletionIndicator(0L);
        InboundHeaderV2 createdInboundHeader = inboundHeaderV2Repository.save(inboundHeader);
        log.info("createdInboundHeader : " + createdInboundHeader);
        return createdInboundHeader;
    }

    /**
     * To avoid Deadlock
     *
     * @param preInboundHeader
     * @return
     */
    public StagingHeaderV2 createStagingHeader(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText) {

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
        stagingHeader.setStatusId(14L);
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
     * @param preInboundHeaderEntityV2
     * @param stagingHeaderV2
     * @return
     * @throws java.text.ParseException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public GrHeaderV2 createGrHeader(PreInboundHeaderEntityV2 preInboundHeaderEntityV2, StagingHeaderV2 stagingHeaderV2) throws java.text.ParseException, InvocationTargetException, IllegalAccessException {

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
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                          ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText) {
        PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();

        preInboundLine.setLanguageId(languageId);
        preInboundLine.setCompanyCode(companyCode);
        preInboundLine.setPlantId(plantId);
        preInboundLine.setWarehouseId(warehouseId);
        preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
        preInboundLine.setInboundOrderTypeId(1L);
//        preInboundLine.setCustomerCode(asnHeaderV2.getCustomerCode());
        preInboundLine.setTransferRequestType("Supplier Invoice");

        // PRE_IB_NO
        preInboundLine.setPreInboundNo(preInboundNo);

        // IB__LINE_NO
        preInboundLine.setLineNo(asnLineV2.getLineReference());

        // ITM_CODE
        preInboundLine.setItemCode(asnLineV2.getSku());

        // ITEM_TEXT - Pass CHL_ITM_CODE as ITM_CODE in IMBASICDATA1 table and fetch ITEM_TEXT and insert
//        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        ImBasicData1 imBasicData1 =
                imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);
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
     * @param preInboundLine
     * @param preInboundHeader
     * @return
     */
    public InboundLineV2 createInboundLine(PreInboundLineEntityV2 preInboundLine, InboundHeaderV2 preInboundHeader) {

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
        inboundLine.setSourceCompanyCode(preInboundHeader.getSourceCompanyCode());
        inboundLine.setSourceBranchCode(preInboundHeader.getSourceBranchCode());
        inboundLine.setDeletionIndicator(0L);
        inboundLine.setCreatedBy(preInboundHeader.getCreatedBy());
        inboundLine.setCreatedOn(preInboundHeader.getCreatedOn());
//        inboundLineV2Repository.save(inboundLine);
        return inboundLine;
    }


    /**
     * @param preIBLine
     * @param stagingHeader
     * @return
     */
    public StagingLineEntityV2 createStagingLineV2(PreInboundLineEntityV2 preIBLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2) {

        StagingLineEntityV2 dbStagingLineEntity = new StagingLineEntityV2();
        BeanUtils.copyProperties(preIBLine, dbStagingLineEntity, CommonUtils.getNullPropertyNames(preIBLine));
        dbStagingLineEntity.setCaseCode(grHeaderV2.getCaseCode());
        dbStagingLineEntity.setPalletCode(grHeaderV2.getCaseCode());    //Copy CASE_CODE
        dbStagingLineEntity.setSourceBranchCode(stagingHeader.getSourceBranchCode());
        dbStagingLineEntity.setStatusId(14L);
        dbStagingLineEntity.setLanguageId(stagingHeader.getLanguageId());
        dbStagingLineEntity.setCompanyCode(stagingHeader.getCompanyCode());
        dbStagingLineEntity.setPlantId(stagingHeader.getPlantId());
        dbStagingLineEntity.setCustomerCode(preIBLine.getCustomerCode());
        dbStagingLineEntity.setTransferRequestType(preIBLine.getTransferRequestType());
        dbStagingLineEntity.setOrderQty(preIBLine.getOrderQty());

        //Pass ITM_CODE/SUPPLIER_CODE received in integration API into IMPARTNER table and fetch PARTNER_ITEM_BARCODE values. Values can be multiple
        List<String> barcode = stagingLineV2Repository.getPartnerItemBarcode(preIBLine.getItemCode(), preIBLine.getCompanyCode(),
                preIBLine.getPlantId(), preIBLine.getWarehouseId(), preIBLine.getManufacturerName(), preIBLine.getLanguageId());
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
        dbStagingLineEntity.setManufacturerCode(preIBLine.getManufacturerName());
        dbStagingLineEntity.setManufacturerName(preIBLine.getManufacturerName());
        dbStagingLineEntity.setDeletionIndicator(0L);
        dbStagingLineEntity.setCreatedBy(preIBLine.getCreatedBy());
        dbStagingLineEntity.setUpdatedBy(preIBLine.getCreatedBy());
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


////    /**
////     *
////     * @param asnv2
////     * @return
////     */
//    public InboundOrderV2 postWarehouseASNV2 (ASNV2 asnv2) {
//        log.info("ASNV2Header received from External: " + asnv2);
//        InboundOrderV2 savedAsnV2Header = saveASNV2 (asnv2);
//        log.info("savedAsnV2Header: " + savedAsnV2Header);
//        return savedAsnV2Header;
//    }
//
//    // POST ASNV2Header
//    private InboundOrderV2 saveASNV2 (ASNV2 asnv2) {
//        try {
//            log.info("DB CHECK --> {}", DataBaseContextHolder.getCurrentDb());
//            ASNHeaderV2 asnV2Header = asnv2.getAsnHeader();
//            List<ASNLineV2> asnLineV2s = asnv2.getAsnLine();
//            InboundOrderV2 apiHeader = new InboundOrderV2();
//
//            apiHeader.setAMSSupplierInvoiceNo(asnV2Header.getAMSSupplierInvoiceNo());
//            apiHeader.setOrderId(asnV2Header.getAsnNumber());
//            apiHeader.setCompanyCode(asnV2Header.getCompanyCode());
//            apiHeader.setBranchCode(asnV2Header.getBranchCode());
//            apiHeader.setRefDocumentNo(asnV2Header.getAsnNumber());
//            apiHeader.setRefDocumentType("SupplierInvoice");
//            apiHeader.setInboundOrderTypeId(1L);
//            apiHeader.setOrderReceivedOn(new Date());
//            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
//            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());
//
//            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
//            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
//            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
//
//            // Get Warehouse
////            Optional<Warehouse> dbWarehouse =
////                    warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(
////                            asnV2Header.getCompanyCode(),
////                            asnV2Header.getBranchCode(),
////                            "EN",
////                            0L
////                    );
////            log.info("dbWarehouse : " + dbWarehouse);
//            apiHeader.setWarehouseID(asnV2Header.getWarehouseId());
////			apiHeader.setInboundOrderHeaderId(System.currentTimeMillis());
//
//            Set<InboundOrderLinesV2> orderLines = new HashSet<>();
//            for (ASNLineV2 asnLineV2 : asnLineV2s) {
//                InboundOrderLinesV2 apiLine = new InboundOrderLinesV2();
//                apiLine.setLineReference(asnLineV2.getLineReference()); 			// IB_LINE_NO
//                apiLine.setItemCode(asnLineV2.getSku());							// ITM_CODE
//                apiLine.setItemText(asnLineV2.getSkuDescription()); 				// ITEM_TEXT
//                apiLine.setContainerNumber(asnLineV2.getContainerNumber());			// CONT_NO
//                apiLine.setSupplierCode(asnLineV2.getSupplierCode());				// PARTNER_CODE
//                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
//                apiLine.setManufacturerName(asnLineV2.getManufacturerName());		// BRAND_NM
//                apiLine.setManufacturerCode(asnLineV2.getManufacturerCode());
//                apiLine.setOrigin(asnLineV2.getOrigin());
//                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
//                apiLine.setBranchCode(asnLineV2.getBranchCode());
//                apiLine.setExpectedQty(asnLineV2.getExpectedQty());
//                apiLine.setSupplierName(asnLineV2.getSupplierName());
//                apiLine.setBrand(asnLineV2.getBrand());
//                apiLine.setOrderId(apiHeader.getOrderId());
//                apiLine.setAMSSupplierInvoiceNo(asnLineV2.getAMSSupplierInvoiceNo());
////				apiLine.setInboundOrderHeaderId(apiHeader.getInboundOrderHeaderId());
//                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
//                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
//                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
//                apiLine.setInboundOrderTypeId(1L);
//
//                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
//                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
//                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
//                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
//                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
//                apiLine.setIsCompleted(asnLineV2.getIsCompleted());
//
//                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
//                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
//                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());
//
//                if (asnLineV2.getExpectedDate() != null) {
//                    if (asnLineV2.getExpectedDate().contains("-")) {
//                        // EA_DATE
//                        try {
//                            Date reqDelDate = new Date();
//                            if(asnLineV2.getExpectedDate().length() > 10) {
//                                reqDelDate = DateUtils.convertStringToDateWithTime(asnLineV2.getExpectedDate());
//                            }
//                            if(asnLineV2.getExpectedDate().length() == 10) {
//                                reqDelDate = DateUtils.convertStringToDate2(asnLineV2.getExpectedDate());
//                            }
//                            apiLine.setExpectedDate(reqDelDate);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            throw new BadRequestException("Date format should be MM-dd-yyyy");
//                        }
//                    }
//                    if (asnLineV2.getExpectedDate().contains("/")) {
//                        // EA_DATE
//                        try {
//                            ZoneId defaultZoneId = ZoneId.systemDefault();
//                            String sdate = asnLineV2.getExpectedDate();
//                            String firstHalf = sdate.substring(0, sdate.lastIndexOf("/"));
//                            String secondHalf = sdate.substring(sdate.lastIndexOf("/") + 1);
//                            secondHalf = "/20" + secondHalf;
//                            sdate = firstHalf + secondHalf;
//                            log.info("sdate--------> : " + sdate);
//
//                            LocalDate localDate = DateUtils.dateConv2(sdate);
//                            log.info("localDate--------> : " + localDate);
//                            Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
//                            apiLine.setExpectedDate(date);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            throw new InboundOrderRequestException("Date format should be MM-dd-yyyy");
//                        }
//                    }
//                }
//
//                apiLine.setOrderedQty(asnLineV2.getExpectedQty());				// ORD_QTY
//                apiLine.setUom(asnLineV2.getUom());								// ORD_UOM
//                apiLine.setPackQty(asnLineV2.getPackQty());					// ITM_CASE_QTY
//                orderLines.add(apiLine);
//            }
//            apiHeader.setLine(orderLines);
//            apiHeader.setOrderProcessedOn(new Date());
//            if (asnv2.getAsnLine() != null && !asnv2.getAsnLine().isEmpty()) {
//                apiHeader.setProcessedStatusId(0L);
//                log.info("apiHeader : " + apiHeader);
//                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
//                log.info("ASNV2 Order Success : " + createdOrder);
//                return createdOrder;
//            } else if (asnv2.getAsnLine() == null || asnv2.getAsnLine().isEmpty()) {
//                // throw the error as Lines are Empty and set the Indicator as '100'
//                apiHeader.setProcessedStatusId(100L);
//                log.info("apiHeader : " + apiHeader);
//                InboundOrderV2 createdOrder = orderService.createInboundOrdersV2(apiHeader);
//                log.info("ASNV2 Order Failed : " + createdOrder);
//                throw new BadRequestException("ASNV2 Order doesn't contain any Lines.");
//            }
//        } catch (Exception e) {
//            throw e;
//        }
//        return null;
//    }


}
