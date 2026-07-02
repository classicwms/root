package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderLinesV2;
import com.tekclover.wms.api.inbound.transaction.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
//@RequiredArgsConstructor
public class InboundOrderProcessService extends BaseService {

//    @Autowired
//    private final RepositoryProvider repo;

    @Autowired
    InboundOrderProcessingService inboundOrderProcessingService;

    @Autowired
    OrderService orderService;


    public void createOrderProcess(List<InboundOrderV2> inboundOrderV2s) {
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        try {
            List<CompletableFuture<Void>> futures = inboundOrderV2s.stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            createInboundOrder(asnLineV2);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();

        } catch (Exception e) {
            log.error("Error processing inbound OrderProcess", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

    @Transactional
    public void createInboundOrder(InboundOrderV2 header) throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        try {
            Set<InboundOrderLinesV2> asnLineV2List = header.getLine();

            String companyCodeId = header.getCompanyCode();
            String plantId = header.getBranchCode();
            String warehouseId = header.getWarehouseID();
            String languageId = header.getLanguageId();

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
            IKeyValuePair description = stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
            String companyText = description.getCompanyDesc();
            String plantText = description.getPlantDesc();
            String warehouseText = description.getWarehouseDesc();


            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
                    companyCodeId, languageId, plantId, preInboundNo, header, warehouseId, companyText, plantText, warehouseText, statusId, statusDescription);
            InboundHeaderV2 inboundHeader = inboundOrderProcessingService.createInboundHeaderV2(preInboundHeader, (long) header.getLine().size());
            StagingHeaderV2 stagingHeader = inboundOrderProcessingService.createStagingHeaderV2(preInboundHeader, stagingNo);

            statusDescription = getStatusDescription(17L, languageId);
            String grStatusDes = getStatusDescription(16L,languageId);
            GrHeaderV2 grHeader = inboundOrderProcessingService.createGrHeaderV2(stagingHeader, caseCode, grNumber, 16L, grStatusDes);
            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());
            List<ImBasicData1V2> imBasicDataList = Collections.synchronizedList(new ArrayList<>());
            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
            List<StagingLineEntityV2> stagingLinesList = null;

            List<CompletableFuture<Void>> futures = asnLineV2List.stream()
                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                        try {
                            processSingleASNLine(header, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
                                    imBasicDataList, preInboundLineList, inboundLineList);

                        } catch (Exception e) {
                            log.error("Error processing ASN Line for ASN: {}", header.getRefDocumentNo(), e);
                            throw new RuntimeException(e);
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            stagingLinesList = Collections.synchronizedList(new ArrayList<>());
            statusDescription = getStatusDescription(14L, languageId);
//            stagingLinesList = inboundOrderProcessingService.createStagingLines(stagingNo, caseCode, 14L, statusDescription, preInboundLineList);

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.join();

            //create stagingLine
            stagingLinesList = inboundOrderProcessingService.createStagingLines(stagingNo, caseCode, 14L, statusDescription, preInboundLineList);

            // Batch Save All Records
            imBasicData1V2Repository.saveAll(imBasicDataList);
            preInboundLineV2Repository.saveAll(preInboundLineList);
            inboundLineV2Repository.saveAll(inboundLineList);
            stagingLineV2Repository.saveAll(stagingLinesList);

            updateProcessedInboundOrderV2(header.getRefDocumentNo(), header.getInboundOrderTypeId(), 10L);

        } catch (Exception e) {
            updateProcessedInboundOrderV2(header.getRefDocumentNo(), header.getInboundOrderTypeId(), 100L);
            log.error("Error processing inbound ASN Lines", e);
            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }

//    @Transactional
//    public ASNV2 createInboundOrder(ASNV2 asnv2) {
//
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        try {
//            ASNHeaderV2 header = asnv2.getAsnHeader();
//            List<ASNLineV2> asnLineV2List = asnv2.getAsnLine();
//
//            String companyCodeId = header.getCompanyCode();
//            String plantId = header.getBranchCode();
//            String warehouseId = header.getWarehouseId();
//            String languageId = header.getLanguageId();
//
//            String idMasterAuthToken = getIDMasterAuthToken();
//            String masterAuthToken = getMasterAuthToken();
//            Long statusId = 13L;
//
//            // Getting PreInboundNo, StagingNo, CaseCode from NumberRangeTable
//            String preInboundNo = getNextRangeNumber(2L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String stagingNo = getNextRangeNumber(3L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String caseCode = getNextRangeNumber(4L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            String grNumber = getNextRangeNumber(5L, companyCodeId, plantId, languageId, warehouseId, idMasterAuthToken);
//            log.info("PreInboundNo, StagingNo, CaseCode, GrNumber : {}|{}|{}|{}", preInboundNo, stagingNo, caseCode, grNumber);
//
//            statusDescription = getStatusDescription(statusId, languageId);
//            IKeyValuePair description = repo.stagingLineV2Repository.getDescription(companyCodeId, languageId, plantId, warehouseId);
//            String companyText = description.getCompanyDesc();
//            String plantText = description.getPlantDesc();
//            String warehouseText = description.getWarehouseDesc();
//
//
//            PreInboundHeaderEntityV2 preInboundHeader = createPreInboundHeaderV2(
//                    companyCodeId, languageId, plantId, preInboundNo, header, warehouseId, companyText, plantText, warehouseText, statusId, statusDescription);
//            InboundHeaderV2 inboundHeader = repo.inboundOrderProcessingService.createInboundHeaderV2(preInboundHeader, (long) asnv2.getAsnLine().size());
//            StagingHeaderV2 stagingHeader = repo.inboundOrderProcessingService.createStagingHeaderV2(preInboundHeader, stagingNo);
//
//            statusDescription = getStatusDescription(17L, languageId);
//            String grStatusDes = getStatusDescription(16L,languageId);
//            GrHeaderV2 grHeader = repo.inboundOrderProcessingService.createGrHeaderV2(stagingHeader, caseCode, grNumber, 16L, grStatusDes);
//            log.info("PreInboundHeader created: {}", preInboundHeader.getPreInboundNo());
//            List<ImBasicData1V2> imBasicDataList = Collections.synchronizedList(new ArrayList<>());
//            List<PreInboundLineEntityV2> preInboundLineList = Collections.synchronizedList(new ArrayList<>());
//            List<InboundLineV2> inboundLineList = Collections.synchronizedList(new ArrayList<>());
//
//            List<CompletableFuture<Void>> futures = asnLineV2List.stream()
//                    .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
//                        try {
//                            processSingleASNLine(asnv2, asnLineV2, preInboundHeader, inboundHeader, stagingHeader, grHeader,
//                                    imBasicDataList, preInboundLineList, inboundLineList);
//
//                        } catch (Exception e) {
//                            log.error("Error processing ASN Line for ASN: {}", header.getAsnNumber(), e);
//                            throw new RuntimeException(e);
//                        }
//                    }, executorService))
//                    .collect(Collectors.toList());
//
//            statusDescription = getStatusDescription(14L, languageId);
//            List<StagingLineEntityV2> stagingLines = repo.inboundOrderProcessingService.createStagingLines(stagingNo, caseCode, 14L, statusDescription, preInboundLineList);
//
//            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
//            allFutures.join();
//            // Batch Save All Records
//            repo.imBasicData1V2Repository.saveAll(imBasicDataList);
//            repo.preInboundLineV2Repository.saveAll(preInboundLineList);
//            repo.inboundLineV2Repository.saveAll(inboundLineList);
//            repo.stagingLineV2Repository.saveAll(stagingLines);
//        } catch (Exception e) {
//            log.error("Error processing inbound ASN Lines", e);
//            throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
//        } finally {
//            executorService.shutdown();
//        }
//        return asnv2;
//    }

    // Create PreInbound_Header
    private PreInboundHeaderEntityV2 createPreInboundHeaderV2(String companyId, String languageId, String plantId, String preInboundNo, InboundOrderV2 asnHeaderV2,
                                                              String warehouseId, String companyText, String plantText, String warehouseText, Long statusId, String statusText) {
        PreInboundHeaderEntityV2 preInboundHeader = new PreInboundHeaderEntityV2();
        BeanUtils.copyProperties(asnHeaderV2, preInboundHeader, CommonUtils.getNullPropertyNames(asnHeaderV2));
        preInboundHeader.setLanguageId(languageId);                                    // LANG_ID
        preInboundHeader.setWarehouseId(warehouseId);
        preInboundHeader.setCompanyCode(companyId);
        preInboundHeader.setPlantId(plantId);
        preInboundHeader.setRefDocNumber(asnHeaderV2.getRefDocumentNo());
        preInboundHeader.setPreInboundNo(preInboundNo);                                                // PRE_IB_NO
        preInboundHeader.setReferenceDocumentType("Supplier Invoice");    // REF_DOC_TYP - Hard Coded Value "ASN"
        preInboundHeader.setInboundOrderTypeId(1L);    // IB_ORD_TYP_ID
        preInboundHeader.setRefDocDate(new Date());                // REF_DOC_DATE
        preInboundHeader.setStatusId(5L);
        statusDescription = stagingLineV2Repository.getStatusDescription(5L, languageId);
        preInboundHeader.setStatusDescription(statusDescription);
        preInboundHeader.setCompanyDescription(companyText);
        preInboundHeader.setPlantDescription(plantText);
        preInboundHeader.setWarehouseDescription(warehouseText);
        preInboundHeader.setMiddlewareId(String.valueOf(asnHeaderV2.getMiddlewareId()));
        preInboundHeader.setMiddlewareTable(asnHeaderV2.getMiddlewareTable());
//        preInboundHeader.setManufacturerFullName();
        preInboundHeader.setStatusId(statusId);
        preInboundHeader.setStatusDescription(statusDescription);
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


    // Create InboundLines
    private void processSingleASNLine(InboundOrderV2 orderV2, InboundOrderLinesV2 linesV2, PreInboundHeaderEntityV2 preInboundHeader,
                                      InboundHeaderV2 inboundHeader, StagingHeaderV2 stagingHeader, GrHeaderV2 grHeaderV2,
                                      List<ImBasicData1V2> imBasicDataList, List<PreInboundLineEntityV2> preInboundLineList,
                                      List<InboundLineV2> inboundLineList) throws Exception {

        String companyCode = orderV2.getCompanyCode();
        String plantId = orderV2.getBranchCode();
        String warehouseId = preInboundHeader.getWarehouseId();
        String languageId = preInboundHeader.getLanguageId();

        // Check and collect IMBASICDATA1
        ImBasicData1V2 imBasicData1 = imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerPartNoAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId, linesV2.getItemCode(), linesV2.getManufacturerName(), 0L);

        if (imBasicData1 == null) {
            imBasicData1 = new ImBasicData1V2();
            imBasicData1.setLanguageId(languageId);
            imBasicData1.setWarehouseId(warehouseId);
            imBasicData1.setCompanyCodeId(companyCode);
            imBasicData1.setPlantId(plantId);
            imBasicData1.setItemCode(linesV2.getItemCode());
            String uomId = linesV2.getUom() != null ? linesV2.getUom() : getUomId(companyCode, plantId, languageId, warehouseId);
            imBasicData1.setUomId(uomId);
            imBasicData1.setDescription(linesV2.getItemText());
            imBasicData1.setManufacturerPartNo(linesV2.getManufacturerName());
            imBasicData1.setManufacturerName(linesV2.getManufacturerName());
            imBasicData1.setCapacityCheck(false);
            imBasicData1.setDeletionIndicator(0L);
            imBasicData1.setStatusId(1L);
            imBasicDataList.add(imBasicData1); // Collect for batch save
        }


        // Collect PreInboundLine
        PreInboundLineEntityV2 preInboundLine = createPreInboundLineV2(companyCode, plantId, languageId, preInboundHeader.getPreInboundNo(), orderV2,
                linesV2, warehouseId, preInboundHeader.getCompanyDescription(), preInboundHeader.getPlantDescription(), preInboundHeader.getWarehouseDescription(), MW_AMS);
        preInboundLineList.add(preInboundLine);

        // Collect InboundLine
        InboundLineV2 inboundLineV2 = createInboundLine(preInboundLine, 17L, statusDescription);
        inboundLineList.add(inboundLineV2);

        // Collect StagingLine
//        StagingLineEntityV2 stagingLine = createStagingLineV2(preInboundLine, stagingHeader, grHeaderV2);
//        stagingLineList.add(stagingLine);
    }


    // Create PreInboundLines
    private PreInboundLineEntityV2 createPreInboundLineV2(String companyCode,String plantId, String languageId, String preInboundNo,InboundOrderV2 headerV2,
                                                          InboundOrderLinesV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText, String loginUserId) throws Exception {
        try {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            BeanUtils.copyProperties(asnLineV2, preInboundLine, CommonUtils.getNullPropertyNames(asnLineV2));
            preInboundLine.setLanguageId(languageId);
            preInboundLine.setCompanyCode(companyCode);
            preInboundLine.setPlantId(plantId);
            preInboundLine.setWarehouseId(warehouseId);
            preInboundLine.setRefDocNumber(headerV2.getRefDocumentNo());
            preInboundLine.setInboundOrderTypeId(headerV2.getInboundOrderTypeId());
            preInboundLine.setParentProductionOrderNo(headerV2.getParentProductionOrderNo());
            preInboundLine.setPreInboundNo(preInboundNo);
            preInboundLine.setLineNo(asnLineV2.getLineReference());
            preInboundLine.setItemCode(asnLineV2.getItemCode());
            preInboundLine.setItemDescription(asnLineV2.getItemText());
//            preInboundLine.setManufacturerPartNo(inboundIntegrationLine.getManufacturerPartNo());
            preInboundLine.setBusinessPartnerCode(asnLineV2.getSupplierCode());
            preInboundLine.setOrderQty(asnLineV2.getExpectedQty());
            preInboundLine.setOrderUom(asnLineV2.getUom());
            preInboundLine.setStockTypeId(1L);
            preInboundLine.setSpecialStockIndicatorId(1L);
            preInboundLine.setExpectedArrivalDate(asnLineV2.getExpectedDate());
//            preInboundLine.setItemCaseQty(inboundIntegrationLine.getItemCaseQty());
            preInboundLine.setReferenceField4(asnLineV2.getSalesOrderReference());
            Long statusId = 13L;
            statusDescription = getStatusDescription(statusId, headerV2.getLanguageId());
            preInboundLine.setStatusId(statusId);
            preInboundLine.setStatusDescription(statusDescription);
            preInboundLine.setCompanyDescription(companyText);
            preInboundLine.setPlantDescription(plantText);
            preInboundLine.setWarehouseDescription(warehouseText);

//            preInboundLine.setOrigin(inboundIntegrationLine.getOrigin());
//            preInboundLine.setBrandName(inboundIntegrationLine.getBrand());
            preInboundLine.setManufacturerCode(asnLineV2.getManufacturerName());
//            preInboundLine.setManufacturerName(inboundIntegrationLine.getManufacturerName());
            preInboundLine.setPartnerItemNo(asnLineV2.getSupplierCode());
            preInboundLine.setContainerNo(asnLineV2.getContainerNumber());
//            preInboundLine.setSupplierName(inboundIntegrationLine.getSupplierName());

//            preInboundLine.setMiddlewareId(inboundIntegrationLine.getMiddlewareId());
//            preInboundLine.setMiddlewareHeaderId(inboundIntegrationLine.getMiddlewareHeaderId());
//            preInboundLine.setMiddlewareTable(inboundIntegrationLine.getMiddlewareTable());
//            preInboundLine.setPurchaseOrderNumber(asnLineV2.getPurchaseOrderNumber());
//            preInboundLine.setReferenceDocumentType(inboundIntegrationHeader.getRefDocumentType());
//            preInboundLine.setManufacturerFullName(inboundIntegrationLine.getManufacturerFullName());

//            preInboundLine.setBranchCode(inboundIntegrationLine.getBranchCode());
//            preInboundLine.setTransferOrderNo(inboundIntegrationLine.getTransferOrderNo());
//            preInboundLine.setIsCompleted(inboundIntegrationLine.getIsCompleted());

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
            preInboundLine.setReferenceField5(asnLineV2.getBinLocation());

            preInboundLine.setDeletionIndicator(0L);
            preInboundLine.setCreatedBy(loginUserId);
            preInboundLine.setCreatedOn(new Date());

            log.info("preInboundLine : " + preInboundLine);
            return preInboundLine;
        } catch (Exception e) {
            log.error("PreInboundLine Create Exception: " + e);
            throw e;
        }
    }


    /**
     * @param preInboundLine preInboundLine
     * @return
     */
    public InboundLineV2 createInboundLine(PreInboundLineEntityV2 preInboundLine, Long statusId, String statusDesc) {

        try {
            InboundLineV2 inboundLine = new InboundLineV2();
            BeanUtils.copyProperties(preInboundLine, inboundLine, CommonUtils.getNullPropertyNames(preInboundLine));
            inboundLine.setStatusId(statusId);
            inboundLine.setStatusDescription(statusDesc);
            log.info("InboundLine : " +inboundLine);
            return inboundLine;
        }  catch (Exception e) {
            log.error("Exception while InboundLines Create : " + e);
            throw e;
        }
    }

    /**
     * @param refDocNumber
     * @param inboundOrderTypeId
     * @param processStatusId
     */
    private void updateProcessedInboundOrderV2(String refDocNumber, Long inboundOrderTypeId, Long processStatusId) throws Exception {
        orderService.updateProcessedInboundOrderV2(refDocNumber, inboundOrderTypeId, processStatusId);
    }

}
