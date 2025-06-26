package com.tekclover.wms.api.inbound.orders.service.namratha;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.controller.InboundOrderRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.dto.ImPartner;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.InboundIntegrationHeader;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.CaseConfirmation;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.Warehouse;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.*;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.service.BaseService;
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

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierInvoiceServiceV4 extends BaseService {

    private final RepositoryProvider repo;

    @Autowired
    InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    PreInboundLineV2Repository preInboundLineV2Repository;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    StagingHeaderV2Repository stagingHeaderV2Repository;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    GrHeaderV2Repository grHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    WarehouseRepository warehouseRepository;

    private static String WAREHOUSEID_NUMBERRANGE = "110";

//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
//    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
//            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
//    public List<ASNV2> processInboundReceivedV2(List<ASNV2> asnv2List) throws Exception {
//
//        List<ASNV2> processedAsns = Collections.synchronizedList(new ArrayList<>());
//        log.info("Inbound Process Started for {} ASNs", asnv2List.size());
//
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//
//        try {
//            for (ASNV2 asnv2 : asnv2List) {
//                ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
//                List<ASNLineV2> lineV2List = asnv2.getAsnLine();
//                String companyCode = headerV2.getCompanyCode();
//                String plantId = headerV2.getBranchCode();
//                String mfrName = lineV2List.get(0).getManufacturerName();
//
//                //Checking whether received refDocNumber processed already.
//                Optional<PreInboundHeaderEntityV2> orderProcessedStatus = repo.preInboundHeaderV2Repository.
//                        findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(headerV2.getAsnNumber(), 1L, 0L);
//                if (!orderProcessedStatus.isEmpty()) {
//                    throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
//                }
//
//                // Get Warehouse
//                Optional<Warehouse> dbWarehouse =
//                        repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
//                String warehouseId = dbWarehouse.get().getWarehouseId();
//                String languageId = dbWarehouse.get().getLanguageId();
//                log.info("Warehouse ID: {}", warehouseId);
//
//                // Description_Set
//                IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
//                String companyText = description.getCompanyDesc();
//                String plantText = description.getPlantDesc();
//                String warehouseText = description.getWarehouseDesc();
//
//                String idMasterAuthToken = repo.authTokenService.getIDMasterServiceAuthToken().getAccess_token();
//                String masterAuthToken = repo.authTokenService.getMastersServiceAuthToken().getAccess_token();
//                Long statusId = 13L;
//
//                // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
//                String preInboundNo = getNextRangeNumber(2L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
//                String stagingNo = getNextRangeNumber(3L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
//                String caseCode = getNextRangeNumber(4L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
//                String grNumber = getNextRangeNumber(5L, companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
//                log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : " + preInboundNo + ", " + stagingNo + ", " + caseCode + ", " + grNumber);
//
//                statusDescription = getStatusDescription(statusId, languageId);
//                description = getDescription(companyCode, plantId, languageId, warehouseId);
//
//
//                // Step 1: Create headers before line processing
//                PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
//                        companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
//                log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());
//
//                InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, lineV2List.size());
//                log.info("Inbound Header Created: {}", inboundHeader);
//
//                StagingHeaderV2 stagingHeader = createStagingHeaderV2(preInboundHeader, stagingNo);
//                log.info("StagingHeader Created: {}", stagingHeader);
//
//                GrHeaderV2 grHeader = createGrHeader(stagingHeader, caseCode, grNumber);
//                log.info("GrHeader Created: {}", grHeader);
//
//                // Collections for batch saving
//                List<ImBasicData1V2> imBasicDataList = Collections.synchronizedList(new ArrayList<>());
//                List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
//                List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
//                List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());
//
//                String partBarCode = generateBarCodeId(grHeader.getRefDocNumber());
//                // Process lines in parallel
//                List<CompletableFuture<Void>> futures = lineV2List.stream()
//                        .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
//                            try {
//                                processSingleASNLine(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
//                                        imBasicDataList, preInboundLineList, inboundLineList, stagingLineList, partBarCode);
//                            } catch (Exception e) {
//                                log.error("Error processing ASN Line for ASN: {}", headerV2.getAsnNumber(), e);
//                                throw new RuntimeException(e);
//                            }
//                        }, executorService))
//                        .collect(Collectors.toList());
//
//                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//
//                try {
//                    allFutures.join(); // Wait for all tasks to finish
//                } catch (CompletionException e) {
//                    log.error("Exception during ASN line processing: {}", e.getCause().getMessage());
//                    throw new BadRequestException("Inbound Order Processing failed: " + e.getCause().getMessage());
//                }
//
//                // Batch Save All Records
//                repo.imBasicData1V2Repository.deleteAll(imBasicDataList);
//                repo.imBasicData1V2Repository.saveAll(imBasicDataList);
//                repo.preInboundLineV2Repository.saveAll(preInboundLineList);
//                repo.inboundLineV2Repository.saveAll(inboundLineList);
//                repo.stagingLineV2Repository.saveAll(stagingLineList);
//
//                processedAsns.add(asnv2);
//            }
//        } catch (Exception e) {
//            log.error("Error processing inbound ASN Lines", e);
//            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//        }
//        log.info("Inbound Process Completed for {} ASNs", processedAsns.size());
//        return processedAsns;
//    }


    /**
     * NAMRATHA
     *
     * @param asnv2
     * @return
     */

    public InboundOrderV2 saveASNV6(ASNV2 asnv2) {
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
            apiHeader.setMiddlewareId(asnV2Header.getMiddlewareId());
            apiHeader.setMiddlewareTable(asnV2Header.getMiddlewareTable());

            apiHeader.setIsCancelled(asnV2Header.getIsCancelled());
            apiHeader.setIsCompleted(asnV2Header.getIsCompleted());
            apiHeader.setUpdatedOn(asnV2Header.getUpdatedOn());
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
                apiLine.setLineReference(asnLineV2.getLineReference());            // IB_LINE_NO
                apiLine.setItemCode(asnLineV2.getSku());                            // ITM_CODE
                apiLine.setItemText(asnLineV2.getSkuDescription());                // ITEM_TEXT
                apiLine.setContainerNumber(asnLineV2.getContainerNumber());            // CONT_NO
                if (asnLineV2.getSupplierCode() != null) {
                    apiLine.setSupplierCode(asnLineV2.getSupplierCode());                // PARTNER_CODE
                } else {
                    apiLine.setSupplierCode(asnV2Header.getSupplierCode());
                }
                apiLine.setSupplierPartNumber(asnLineV2.getSupplierPartNumber());  // PARTNER_ITM_CODE
                apiLine.setManufacturerName(MFR_NAME);
                apiLine.setManufacturerCode(MFR_NAME);
                apiLine.setManufacturerPartNo(MFR_NAME);
                apiLine.setOrigin(asnLineV2.getOrigin());
                apiLine.setCompanyCode(asnLineV2.getCompanyCode());
                apiLine.setBranchCode(asnLineV2.getBranchCode());
                apiLine.setSupplierName(asnLineV2.getSupplierName());
                apiLine.setBrand(asnLineV2.getBrand());
                apiLine.setOrderId(apiHeader.getOrderId());
                apiLine.setManufacturerFullName(asnLineV2.getManufacturerFullName());
                apiLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
                apiHeader.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());

                if (asnV2Header.getInboundOrderTypeId() != null) {
                    apiLine.setInboundOrderTypeId(asnV2Header.getInboundOrderTypeId());
                } else {
                    apiLine.setInboundOrderTypeId(1L);                                            //Default
                }
                if (asnLineV2.getExpectedQtyInCases() != null && asnLineV2.getExpectedQtyInPieces() != null) {
                    Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                } else {
                    Double ordQty = asnLineV2.getExpectedQty() / asnLineV2.getNoBags();  // 50 / 2 => 25
                    apiLine.setExpectedQty(ordQty);     // 25
                    apiLine.setOrderedQty(ordQty);      // 25
                    apiLine.setBagSize(ordQty);         // 25
                }

                apiLine.setNoBags(asnLineV2.getExpectedQtyInCases());
                apiLine.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());
                apiLine.setReceivedBy(asnLineV2.getReceivedBy());
                apiLine.setReceivedQty(asnLineV2.getReceivedQty());
                apiLine.setReceivedDate(asnLineV2.getReceivedDate());
                apiLine.setIsCancelled(asnLineV2.getIsCancelled());
                apiLine.setIsCompleted(asnLineV2.getIsCompleted());

                apiLine.setMiddlewareHeaderId(asnLineV2.getMiddlewareHeaderId());
                apiLine.setMiddlewareId(asnLineV2.getMiddlewareId());
                apiLine.setMiddlewareTable(asnLineV2.getMiddlewareTable());

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
                apiHeader.setProcessedStatusId(1L);
                log.info("apiHeader : " + apiHeader);
                InboundOrderV2 createdOrder = createInboundOrdersV2(apiHeader);
                log.info("ASNV2 Order Success : " + createdOrder);
                return createdOrder;
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
        return null;
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public ASNV2 processInboundReceivedV2(ASNV2 asnv2) throws Exception {

//        DataBaseContextHolder.setCurrentDb("NAMRATHA");
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        try {
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("NAMRATHA");
            ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
            List<ASNLineV2> lineV2List = asnv2.getAsnLine();
            String companyCode = headerV2.getCompanyCode();
            String plantId = headerV2.getBranchCode();
            String mfrName = lineV2List.get(0).getManufacturerName();

            //Checking whether received refDocNumber processed already.
            Optional<PreInboundHeaderEntityV2> orderProcessedStatus = repo.preInboundHeaderV2Repository.
                    findByRefDocNumberAndInboundOrderTypeIdAndDeletionIndicator(headerV2.getAsnNumber(), 1L, 0L);
            if (!orderProcessedStatus.isEmpty()) {
                throw new BadRequestException("Order :" + headerV2.getAsnNumber() + " already processed. Reprocessing can't be allowed.");
            }

            // Get Warehouse
            Optional<Warehouse> dbWarehouse =
                    repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
            String warehouseId = dbWarehouse.get().getWarehouseId();
            String languageId = dbWarehouse.get().getLanguageId();
            log.info("Warehouse ID: {}", warehouseId);

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
                    companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

            InboundHeaderV2 inboundHeader = createInboundHeader(preInboundHeader, lineV2List.size());
            log.info("Inbound Header Created: {}", inboundHeader);

            StagingHeaderV2 stagingHeader = createStagingHeaderV2(preInboundHeader, stagingNo);
            log.info("StagingHeader Created: {}", stagingHeader);

            GrHeaderV2 grHeader = createGrHeader(stagingHeader, caseCode, grNumber);
            log.info("GrHeader Created: {}", grHeader);

            // Collections for batch saving
            List<ImBasicData1V2> imBasicDataList = Collections.synchronizedList(new ArrayList<>());
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<StagingLineEntityV2> stagingLineList = Collections.synchronizedList(new ArrayList<>());

            String partBarCode = generateBarCodeId(grHeader.getRefDocNumber());

            // Step 1: Group and reduce lines by SKU (with summed noBags)
            List<ASNLineV2> groupedWithSummedNoBags = lineV2List.stream()
                    .collect(Collectors.toMap(
                            ASNLineV2::getSku,
                            line -> {
                                ASNLineV2 group = new ASNLineV2();
                                group.setSku(line.getSku());
                                group.setNoBags(line.getNoBags());
                                // Set other fields from line if needed
                                return group;
                            },
                            (line1, line2) -> {
                                line1.setNoBags(line1.getNoBags() + line2.getNoBags());
                                return line1;
                            }
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Process lines in parallel
            List<CompletableFuture<Void>> futures = groupedWithSummedNoBags.stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            processSingleASNLine(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
                                    imBasicDataList, preInboundLineList, inboundLineList, stagingLineList, partBarCode);
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
            repo.imBasicData1V2Repository.deleteAll(imBasicDataList);
            repo.imBasicData1V2Repository.saveAll(imBasicDataList);
            repo.preInboundLineV2Repository.saveAll(preInboundLineList);
            repo.inboundLineV2Repository.saveAll(inboundLineList);
            repo.stagingLineV2Repository.saveAll(stagingLineList);

        } catch (Exception e) {
            log.error("Error processing inbound ASN Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
//            DataBaseContextHolder.clear();
        }
        return asnv2;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class, Throwable.class})
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class,
            LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public List<ASNV2> processInboundReceivedV8(List<ASNV2> asnv2List) throws Exception {

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
                        repo.warehouseRepository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndDeletionIndicator(companyCode, plantId, "EN", 0L);
                String warehouseId = dbWarehouse.get().getWarehouseId();
                String languageId = dbWarehouse.get().getLanguageId();
                log.info("Warehouse ID: {}", warehouseId);

                // Description_Set
                IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
                String companyText = description.getCompanyDesc();
                String plantText = description.getPlantDesc();
                String warehouseText = description.getWarehouseDesc();

                // Getting PreInboundNo from NumberRangeTable
                String preInboundNo = getPreInboundNo(warehouseId, companyCode, plantId, languageId);

                // Step 1: Create headers before line processing
                PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV8(
                        companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
                log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());

                InboundHeaderV2 inboundHeader = createInboundHeaderV8(preInboundHeader, companyText, plantText, warehouseText, lineV2List.size());
                log.info("Inbound Header Created: {}", inboundHeader);

                StagingHeaderV2 stagingHeader = createStagingHeaderV8(preInboundHeader, companyText, plantText, warehouseText);
                log.info("StagingHeader Created: {}", stagingHeader);

                GrHeaderV2 grHeader = createGrHeaderV8(preInboundHeader, stagingHeader);
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
                                processSingleASNLineV8(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
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
        PreInboundHeaderEntityV2 createdPreInboundHeader = repo.preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
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
    private PreInboundHeaderEntityV2 createPreInboundHeaderV8(String companyId, String languageId, String plantId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
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
        preInboundHeader.setIsCompleted(asnHeaderV2.getIsCompleted());
        preInboundHeader.setIsCancelled(asnHeaderV2.getIsCancelled());
        preInboundHeader.setMUpdatedOn(asnHeaderV2.getUpdatedOn());

        preInboundHeader.setDeletionIndicator(0L);
        preInboundHeader.setCreatedBy("MW_AMS");
        preInboundHeader.setCreatedOn(new Date());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
//        String routingDb = dbConfigRepository.getDbName(companyId, plantId, warehouseId);
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);
        PreInboundHeaderEntityV2 createdPreInboundHeader = preInboundHeaderV2Repository.save(preInboundHeader);
        log.info("createdPreInboundHeader : " + createdPreInboundHeader);
        return createdPreInboundHeader;
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
            stagingHeaderV2Repository.save(stagingHeader);
            return stagingHeader;
        } catch (Exception e) {
            log.error("Exception while StagingHeader Create : " + e.toString());
            throw e;
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
            return inboundHeaderV2Repository.save(inboundHeader);
//            return inboundHeader;
        } catch (Exception e) {
            log.error("Exception while InboundHeader Create : " + e.toString());
            throw e;
        }
    }

    /**
     * @param preInboundHeader
     * @param companyText
     * @param plantText
     * @param warehouseText
     * @return
     */
    private InboundHeaderV2 createInboundHeaderV8(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText, int lineSize) {
        InboundHeaderV2 inboundHeader = new InboundHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, inboundHeader, CommonUtils.getNullPropertyNames(preInboundHeader));
        inboundHeader.setStatusId(5L);
        statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, preInboundHeader.getLanguageId());
        inboundHeader.setStatusDescription(statusDescription);
        inboundHeader.setCompanyDescription(companyText);
        inboundHeader.setPlantDescription(plantText);
        inboundHeader.setWarehouseDescription(warehouseText);
        inboundHeader.setCountOfOrderLines((long) lineSize);       //count of lines
        inboundHeader.setDeletionIndicator(0L);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
//        String routingDb = dbConfigRepository.getDbName(inboundHeader.getCompanyCode(), inboundHeader.getPlantId(), inboundHeader.getWarehouseId());
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);

//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        InboundHeaderV2 createdInboundHeader = inboundHeaderV2Repository.save(inboundHeader);
        log.info("createdInboundHeader : " + createdInboundHeader);
        return createdInboundHeader;
    }

    /**
     * @param preInboundHeaderEntityV2
     * @param stagingHeaderV2
     * @return
     * @throws java.text.ParseException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public GrHeaderV2 createGrHeaderV8(PreInboundHeaderEntityV2 preInboundHeaderEntityV2, StagingHeaderV2 stagingHeaderV2) throws java.text.ParseException, InvocationTargetException, IllegalAccessException {

        GrHeaderV2 addGrHeader = new GrHeaderV2();
        BeanUtils.copyProperties(stagingHeaderV2, addGrHeader, CommonUtils.getNullPropertyNames(stagingHeaderV2));

        // GR_NO
        AuthToken authTokenForIDMasterService = repo.authTokenService.getIDMasterServiceAuthToken();
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

//        String routingDb = dbConfigRepository.getDbName(addGrHeader.getCompanyCode(), addGrHeader.getPlantId(), addGrHeader.getWarehouseId());
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);

//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        return grHeaderV2Repository.save(addGrHeader);
    }

    /**
     * To avoid Deadlock
     *
     * @param preInboundHeader
     * @return
     */
    public StagingHeaderV2 createStagingHeaderV8(PreInboundHeaderEntityV2 preInboundHeader, String companyText, String plantText, String warehouseText) {

        StagingHeaderV2 stagingHeader = new StagingHeaderV2();
        BeanUtils.copyProperties(preInboundHeader, stagingHeader, CommonUtils.getNullPropertyNames(preInboundHeader));

        // STG_NO
        AuthToken authTokenForIDMasterService = repo.authTokenService.getIDMasterServiceAuthToken();

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

//        String routingDb = dbConfigRepository.getDbName(stagingHeader.getCompanyCode(), stagingHeader.getPlantId(), stagingHeader.getWarehouseId());
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);

//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("FAHAHEEL");
        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", DataBaseContextHolder.getCurrentDb());
        return stagingHeaderV2Repository.save(stagingHeader);
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
            return repo.grHeaderV2Repository.save(grHeader);
        } catch (Exception e) {
            log.error("Exception while GrHeader Create : " + e.toString());
            throw e;
        }
    }

    //ProcessSingleAsnLine
    private void processSingleASNLine(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                      InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                      List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList,
                                      List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList, String partBarCode) throws Exception {

        asnLineV2.setManufacturerCode(MFR_NAME);
        asnLineV2.setManufacturerName(MFR_NAME);
        asnLineV2.setManufacturerFullName(MFR_NAME);
        if (asnLineV2.getExpectedQtyInCases() != null && asnLineV2.getExpectedQtyInPieces() != null) {
            Double ordQty = asnLineV2.getExpectedQtyInPieces() / asnLineV2.getExpectedQtyInCases();  // 50 / 2 => 25
            asnLineV2.setExpectedQty(ordQty);     // 25
//            asnLineV2.setOrderedQty(ordQty);      // 25
            asnLineV2.setBagSize(ordQty);         // 25
        } else {
            Double ordQty = asnLineV2.getExpectedQty() / asnLineV2.getNoBags();  // 50 / 2 => 25
            asnLineV2.setExpectedQty(ordQty);     // 25
//            asnLineV2.setOrderedQty(ordQty);      // 25
            asnLineV2.setBagSize(ordQty);         // 25
        }
        asnLineV2.setNoBags(asnLineV2.getExpectedQtyInCases());
        asnLineV2.setSupplierInvoiceNo(asnLineV2.getSupplierInvoiceNo());

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();
        String itemText = null;

        // Check and collect IMBASICDATA1
        DataBaseContextHolder.setCurrentDb("NAMRATHA");
        ImBasicData1V2 imBasicData1 = repo.imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId,
                asnLineV2.getSku().trim(), asnLineV2.getManufacturerName(), 0L);
        log.info("ImBasicData1 Values {} ", imBasicData1);
        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(asnLineV2.getSku());
            imBasicData1.setUomId(asnLineV2.getUom());
            imBasicData1.setCompanyDescription(preInboundHeader.getCompanyDescription());
            imBasicData1.setPlantDescription(preInboundHeader.getPlantDescription());
            imBasicData1.setWarehouseDescription(preInboundHeader.getWarehouseDescription());
            imBasicData1.setDescription(asnLineV2.getSkuDescription());
            imBasicData1.setManufacturerPartNo(asnLineV2.getManufacturerName());
            imBasicData1.setManufacturerName(asnLineV2.getManufacturerName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicDataList.add(imBasicData1);
        } else {
            itemText = imBasicData1.getDescription();
        }

        if(itemText == null) {
            itemText = asnLineV2.getSkuDescription();
        }
        // Collect PreInboundLine
        List<PreInboundLineEntityV2> preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId,
                preInboundHeader.getPreInboundNo(), headerV2, asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(),
                preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription(), partBarCode, itemText);
        preInboundLineList.addAll(preInboundLine);

        // Collect InboundLine
        List<InboundLineV2> inboundLineV2 = createInboundLines(17L, statusDescription, preInboundLine);
        inboundLineList.addAll(inboundLineV2);

        // Collect StagingLine
        List<StagingLineEntityV2> stagingLine = createStagingLineV2(preInboundLine, grHeaderV2, stagingHeader);
        stagingLineList.addAll(stagingLine);
    }

    //ProcessSingleAsnLine
    private void processSingleASNLineV8(ASNV2 asnv2, ASNLineV2 asnLineV2, PreInboundHeaderEntityV2 preInboundHeader,
                                        InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                        List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList,
                                        List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList) throws Exception {

        ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
        String companyCode = headerV2.getCompanyCode();
        String plantId = headerV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        // Check and collect IMBASICDATA1
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("FAHAHEEL");

//        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
//        log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb(routingDb);

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
        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV8(companyCode, plantId, languageId, preInboundHeader.getPreInboundNo(), headerV2,
                asnLineV2, warehouseId, preInboundHeader.getCompanyDescription(), preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription());
        preInboundLineList.add(preInboundLine);

        // Collect InboundLine
        InboundLineV2 inboundLineV2 = createInboundLineV8(preInboundLine, inboundHeader);
        inboundLineList.add(inboundLineV2);

        // Collect StagingLine
        StagingLineEntityV2 stagingLine = createStagingLineV8(preInboundLine, stagingHeader, grHeaderV2);
        stagingLineList.add(stagingLine);
    }

    /**
     * @param preInboundLine
     * @param preInboundHeader
     * @return
     */
    public InboundLineV2 createInboundLineV8(PreInboundLineEntityV2 preInboundLine, InboundHeaderV2 preInboundHeader) {

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
        statusDescription = repo.stagingLineV2Repository.getStatusDescription(14L, inboundLine.getLanguageId());
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
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @return
     * @throws ParseException
     */
    private List<PreInboundLineEntityV2> createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                                ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText,
                                                                String partBarCode, String itemText) {

        List<PreInboundLineEntityV2> preInboundLineEntityV2List = new ArrayList<>();
        double noOfBags = asnLineV2.getNoBags() != null ? asnLineV2.getNoBags() : 1L;
        log.info("no of bag size {} ------> ", noOfBags);

        List<ImPartner> imPartnerList = new ArrayList<>();
        Long lineNumber = 1L;
        for (long i = 1; i <= noOfBags; i++) {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            String barcodeId = "";
            try {
                if (asnHeaderV2.getSupplierCode() != null && asnHeaderV2.getSupplierCode().equalsIgnoreCase("EVEREST FOOD PRODUCTS PVT LTD")) {
                    //barcodeId = null;
                    barcodeId = "10000" + asnLineV2.getSku();
                } else {
                    barcodeId = generateBarCodeId(asnLineV2.getSku(), partBarCode, i);
                }
                preInboundLine.setBarcodeId(barcodeId);

            } catch (Exception e) {
                throw new RuntimeException("Failed to generate barcode for item code: "
                        + asnLineV2.getSku(), e);
            }
            ImPartner imPartner = createImpartner(companyCode, plantId, languageId, warehouseId, asnLineV2.getSku(),
                    asnLineV2.getManufacturerName(), barcodeId, MW_AMS);
            imPartnerList.add(imPartner);
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(asnHeaderV2.getAsnNumber());
            preInboundLine.setInboundOrderTypeId(1L);
            preInboundLine.setNoBags(1D);
            preInboundLine.setMrp(asnLineV2.getMrp());
            // STCK_TYP_ID
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setStockTypeDescription(getStockTypeDesc(companyCode, plantId, languageId, warehouseId, preInboundLine.getStockTypeId()));

            preInboundLine.setTransferRequestType("SUPPLIER INVOICE");
            preInboundLine.setReferenceDocumentType("SUPPLIER INVOICE");
            preInboundLine.setManufacturerName(MFR_NAME);
            preInboundLine.setManufacturerPartNo(MFR_NAME);
            preInboundLine.setManufacturerCode(MFR_NAME);
            preInboundLine.setTransferOrderNo(asnHeaderV2.getAsnNumber());
            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(lineNumber);
            preInboundLine.setItemCode(asnLineV2.getSku());
            preInboundLine.setItemDescription(itemText);
            preInboundLine.setBusinessPartnerCode(asnHeaderV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setBagSize(asnLineV2.getBagSize());
            preInboundLine.setSpecialStockIndicatorId(1L);
            log.info("inboundIntegrationLine.getExpectedDate() : " + asnLineV2.getExpectedDate());
            preInboundLine.setExpectedArrivalDate(getExpectedDate(asnLineV2.getExpectedDate()));
            preInboundLine.setItemCaseQty(asnLineV2.getPackQty());
            preInboundLine.setStatusId(5L);
            statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
            preInboundLine.setStatusDescription(statusDescription);
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);
            preInboundLine.setOrigin(asnLineV2.getOrigin());
            preInboundLine.setBrandName(asnLineV2.getBrand());
            preInboundLine.setPartnerItemNo(asnHeaderV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
            preInboundLine.setIsCompleted(asnLineV2.getIsCompleted());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy("MW_AMS");
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            lineNumber++;
            preInboundLineEntityV2List.add(preInboundLine);
        }
        if (!imPartnerList.isEmpty()) {
            repo.imPartnerRepository.saveAll(imPartnerList);
        }
        return preInboundLineEntityV2List;
    }

    /**
     * @param preInboundNo
     * @param asnHeaderV2
     * @param asnLineV2
     * @param warehouseId
     * @return
     * @throws ParseException
     */
    private PreInboundLineEntityV2 createPreInboundLineV8(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
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
                repo.imBasicData1Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, asnLineV2.getSku(), asnLineV2.getManufacturerName(), 0L);
        if (imBasicData1 != null) {
            preInboundLine.setItemDescription(imBasicData1.getDescription());
        }

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
        statusDescription = repo.stagingLineV2Repository.getStatusDescription(5L, languageId);
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
     * @param statusId
     * @param statusDesc
     * @param preInboundLines
     * @return
     * @throws Exception
     */
    public List<InboundLineV2> createInboundLines(Long statusId, String statusDesc, List<PreInboundLineEntityV2> preInboundLines) throws Exception {
        try {
            return preInboundLines.stream().map(preInboundLine -> {
                InboundLineV2 inboundLine = new InboundLineV2();
                BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
                inboundLine.setDescription(preInboundLine.getItemDescription());
                inboundLine.setStatusId(statusId);
                inboundLine.setStatusDescription(statusDesc);
                return inboundLine;
            }).collect(toList());
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
    List<StagingLineEntityV2> createStagingLineV2(List<PreInboundLineEntityV2> preInboundLineEntityV2s, GrHeaderV2 grHeaderV2, StagingHeaderV2 stagingHeader) {

        List<StagingLineEntityV2> stagingList = new ArrayList<>();
        preInboundLineEntityV2s.stream().forEach(preInboundLine -> {
            StagingLineEntityV2 stagingLine = new StagingLineEntityV2();
            BeanUtils.copyProperties(preInboundLine, stagingLine, CommonUtils.getNullPropertyNames(preInboundLine));
            stagingLine.setStagingNo(stagingHeader.getStagingNo());
            stagingLine.setCaseCode(grHeaderV2.getCaseCode());
            stagingLine.setPalletCode(grHeaderV2.getCaseCode());
            stagingLine.setPartner_item_barcode(preInboundLine.getBarcodeId());
            stagingLine.setStatusId(14L);
            stagingLine.setStatusDescription(statusDescription);
            stagingLine.setGoodsReceiptNo(grHeaderV2.getGoodsReceiptNo());

            // Cross_Dock_logic_started
            try {
                log.info("Cross Dock logic started");
                log.info("The stagingLine inputs : companyCode --> " + stagingLine.getCompanyCode() + " and plantId --> " + stagingLine.getPlantId() + " and wareHouseId --> " + stagingLine.getWarehouseId() + " and itemCode --> " + stagingLine.getItemCode());
                Optional<OrderManagementLineV2> crossDock = repo.orderManagementLineV2Repository.getOrderManagementLineForCrossDock(
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
            stagingList.add(stagingLine);
        });
        return stagingList;
    }

    /**
     * @param preIBLine
     * @param stagingHeader
     * @return
     */
    public StagingLineEntityV2 createStagingLineV8(PreInboundLineEntityV2 preIBLine, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2) {

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
        List<String> barcode = repo.stagingLineV2Repository.getPartnerItemBarcode(preIBLine.getItemCode(), preIBLine.getCompanyCode(),
                preIBLine.getPlantId(), preIBLine.getWarehouseId(), preIBLine.getManufacturerName(), preIBLine.getLanguageId());
        log.info("Barcode : " + barcode);
        if (barcode != null && !barcode.isEmpty()) {
//                    dbStagingLineEntity.setPartner_item_barcode(barcode.replaceAll("\\s", "").trim());      //to remove white space
            dbStagingLineEntity.setPartner_item_barcode(barcode.get(0));
        }

        statusDescription = repo.stagingLineV2Repository.getStatusDescription(14L, stagingHeader.getLanguageId());
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
//            DataBaseContextHolder.clear();
//            DataBaseContextHolder.setCurrentDb("MT");
            InboundOrderV2 inboundOrderV2 = inboundOrderV2Repository.save(newInboundOrderV2);
            log.info("inboundOrderV2 ----> {}", inboundOrderV2);
            String routingDb = dbConfigRepository.getDbName(newInboundOrderV2.getCompanyCode(), newInboundOrderV2.getBranchCode(), newInboundOrderV2.getWarehouseID());
            log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", routingDb);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            InboundOrderV2 imdDB = inboundOrderV2Repository.save(newInboundOrderV2);
            log.info("imdDB ----> {}", imdDB);

            return inboundOrderV2;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
