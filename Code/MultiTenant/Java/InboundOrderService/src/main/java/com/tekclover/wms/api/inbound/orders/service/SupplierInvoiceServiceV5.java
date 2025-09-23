package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.ImBasicData1V2;
import com.tekclover.wms.api.inbound.orders.model.inbound.gr.v2.GrHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundHeaderEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.preinbound.v2.PreInboundLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNLineV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.ASNV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupplierInvoiceServiceV5 extends BaseService {

    private final RepositoryProvider repo;

    @Autowired
    PreInboundHeaderV2Repository preInboundHeaderV2Repository;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

//    @Autowired
//    ErrorLogService errorLogService;


    @Async("asyncExecutor")
    public List<ASNV2> processInboundReceivedV2(List<ASNV2> asnList) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        for (ASNV2 asnv2 : asnList) {
            try {
                ASNHeaderV2 headerV2 = asnv2.getAsnHeader();
                List<ASNLineV2> lineV2List = asnv2.getAsnLine();
                String companyCode = headerV2.getCompanyCode();
                String plantId = headerV2.getBranchCode();
                String warehouseId = headerV2.getWarehouseId();
                String languageId = headerV2.getLanguageId();
                String mfrName = lineV2List.get(0).getManufacturerName();

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
                        companyCode, languageId, plantId, preInboundNo, headerV2, warehouseId, companyText, plantText, warehouseText, mfrName);
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

                String partBarCode = generateBarCodeId(grHeader.getRefDocNumber());

                // Process lines in parallel
                List<CompletableFuture<Void>> futures = asnv2.getAsnLine().stream()
                        .map(asnLineV2 -> CompletableFuture.runAsync(() -> {
                            try {
                                processSingleASNLine(asnv2, asnLineV2, preInboundHeader, stagingHeader, grHeader,
                                        preInboundLineList, inboundLineList, stagingLineList, partBarCode);
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
                repo.preInboundLineV2Repository.saveAll(preInboundLineList);
                repo.inboundLineV2Repository.saveAll(inboundLineList);
                repo.stagingLineV2Repository.saveAll(stagingLineList);

                log.info("InboundOrder Status 10 Updated");
                DataBaseContextHolder.clear();
                DataBaseContextHolder.setCurrentDb("MT");
                inboundOrderV2Repository.updateIbOrderStatus(companyCode, plantId, warehouseId, preInboundHeader.getRefDocNumber(), 10L);
            } catch (Exception e) {
                log.error("Error processing inbound ASN Lines", e);

//                errorLogService.createProcessInboundReceivedV2(asnv2, e.getMessage());
                throw new BadRequestException("Inbound Order Processing failed: " + e.getMessage());
            } finally {
                executorService.shutdown();
            }
        }
        return asnList;
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

//            PreInboundHeaderEntityV2 createdPreInboundHeader = repo.preInboundHeaderV2Repository.save(preInboundHeader);
//            log.info("createdPreInboundHeader : " + createdPreInboundHeader);

            // IB_Order
            String preInbound = "PreInbound Created";
            inboundOrderV2Repository.updateIbOrder(preInboundHeader.getInboundOrderTypeId(), preInboundHeader.getRefDocNumber(), preInbound);
            log.info("Update Inbound Order Update Successfully");
            return preInboundHeader;
        } catch (Exception e) {
            log.info("PreOutboundHeader Creation Failed -----------> " + e.getMessage());
            throw new BadRequestException("PreOutboundHeader Failed -----------------> " + e);
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
                                      List<InboundLineV2> inboundLineList, List<StagingLineEntityV2> stagingLineList, String partBarCode) throws Exception {

        asnLineV2.setManufacturerCode(asnLineV2.getManufacturerName());
        asnLineV2.setManufacturerName(asnLineV2.getManufacturerName());
        asnLineV2.setManufacturerFullName(asnLineV2.getManufacturerName());
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
        DataBaseContextHolder.setCurrentDb("KNOWELL");
        ImBasicData1V2 imBasicData1 = repo.imBasicData1V2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicator(
                languageId, companyCode, plantId, warehouseId,
                asnLineV2.getSku().trim(), asnLineV2.getManufacturerName(), 0L);
        log.info("ImBasicData1 Values {} ", imBasicData1);
        if (imBasicData1 != null) {
            if (asnLineV2.getSkuDescription() == null) {
                asnLineV2.setSkuDescription(imBasicData1.getDescription());
            }
            asnLineV2.setBrand(imBasicData1.getBrand());
            asnLineV2.setSize(imBasicData1.getSize());
            asnLineV2.setManufacturerCode(imBasicData1.getManufacturerCode());
            asnLineV2.setManufacturerName(imBasicData1.getManufacturerName());
            asnLineV2.setManufacturerFullName(imBasicData1.getManufacturerFullName());
        }
        if (imBasicData1 == null) {
            throw new BadRequestException("The given values: manufacturerName:" + asnLineV2.getManufacturerName() +
                    ",ItemCode: " + asnLineV2.getSku() + " doesn't exist.");
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
    private List<PreInboundLineEntityV2> createPreInboundLineV2(String companyCode, String plantId, String languageId, String preInboundNo, ASNHeaderV2 asnHeaderV2,
                                                                ASNLineV2 asnLineV2, String warehouseId, String companyText, String plantText, String warehouseText,
                                                                String partBarCode, String itemText) {

        String idMasterAuthToken = authTokenService.getIDMasterServiceAuthToken().getAccess_token();
        List<PreInboundLineEntityV2> preInboundLineEntityV2List = new ArrayList<>();
        double noOfBags = asnLineV2.getNoBags() != 0.0 ? asnLineV2.getNoBags() : 1L;
        log.info("no of bag size {} ------> ", noOfBags);

        Long lineNumber = 1L;
        for (long i = 1; i <= noOfBags; i++) {
            PreInboundLineEntityV2 preInboundLine = new PreInboundLineEntityV2();
            String barcodeId = "";
            try {
                barcodeId = generateKnowellBarCodeId(companyCode, plantId, languageId, warehouseId, idMasterAuthToken);
                preInboundLine.setBarcodeId(barcodeId);
            } catch (Exception e) {
                throw new RuntimeException("Failed to generate barcode for item code: "
                        + asnLineV2.getSku(), e);
            }
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
        return preInboundLineEntityV2List;
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


}
