//package com.tekclover.wms.api.inbound.transaction.service;
//
//import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
//import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
//import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
//import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
//import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
//import com.tekclover.wms.api.inbound.transaction.repository.*;
//import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class PutAwayHeaderAsyncProcessService extends BaseService {
//
//    @Autowired
//    StorageBinService storageBinService;
//
//    String statusDescription = null;
//
//    @Autowired
//    InventoryService inventoryService;
//
//    @Autowired
//    InboundOrderV2Repository inboundOrderV2Repository;
//
//    @Autowired
//    PutAwayHeaderV2Repository putAwayHeaderV2Repository;
//
//    @Autowired
//    StagingLineV2Repository stagingLineV2Repository;
//
//    @Autowired
//    InboundQualityHeaderService inboundQualityHeaderService;
//
//    @Autowired
//    PreInboundHeaderService preInboundHeaderService;
//
//    @Autowired
//    GrLineService grLineService;
//
//    @Autowired
//    GrLineV2Repository grLineV2Repository;
//    @Autowired
//    StorageBinV2Repository storageBinV2Repository;
//
//    @Autowired
//    MastersService mastersService;
//
//    @Autowired
//    PutAwayLineService putAwayLineService;
//
//    @Autowired
//    PutAwayHeaderService putAwayHeaderService;
//
//    @Autowired
//    InventoryV2Repository inventoryV2Repository;
//
////
////    /**
////     *
////     * @param company company
////     * @param plant plant
////     * @param language language
////     * @param warehouse warehouse
////     * @param createdGRLines grLine
////     * @param loginUserID userId
////     */
//////    @Async("asyncExecutor")
//////    @Scheduled(fixedRate = 5000)
////    public void createGrLineAsyncProcessV4(String company, String plant, String language, String warehouse, List<GrLineV2> createdGRLines, String loginUserID) {
////        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
////        String idMasterToken = getIDMasterAuthToken();
////        //PA_NO
////        NUMBER_RANGE_CODE = 7L;
////        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
////
////        log.info("PA number ----------------> {}", nextPANumber);
////        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
//////        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
//////                    try {
//////                        processPutAwayHeaderV4(grLine, nextPANumber, loginUserID, idMasterToken);
//////                    } catch (Exception e) {
//////                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
//////                    }
//////                }
//////                , asyncExecutor)) // inject the ExecutorService
//////                .collect(Collectors.toList());
//////        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
////        try {
////            for (GrLineV2 grLine : createdGRLines) {
////                processPutAwayHeaderV4(grLine, nextPANumber, loginUserID, idMasterToken);
////            }
////        } catch (Exception e) {
////            log.error("Error processing GRLine:" + e);
////        }
////    }
//
//    /**
//     *
//     * @param company company
//     * @param plant plant
//     * @param language language
//     * @param warehouse warehouse
//     * @param createdGRLines grLine
//     * @param loginUserID userId
//     */
////    @Async("asyncExecutor")
////    public void createGrLineAsyncProcessV6(String company, String plant, String language, String warehouse, List<GrLineV2> createdGRLines, String loginUserID) {
////        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
////        String idMasterToken = getIDMasterAuthToken();
////        //PA_NO
////        NUMBER_RANGE_CODE = 7L;
////        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
////
////        log.info("PA number ----------------> {}", nextPANumber);
////        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
////        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
////                    try {
////                        processPutAwayHeaderV6(grLine, nextPANumber, loginUserID, idMasterToken);
////                    } catch (Exception e) {
////                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
////                    }
////                }, asyncExecutor)) // inject the ExecutorService
////                .collect(Collectors.toList());
////        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
////    }
//
////    /**
////     *
////     * @param company company
////     * @param plant plant
////     * @param language language
////     * @param warehouse warehouse
////     * @param createdGRLines grLine
////     * @param loginUserID userId
////     */
////    @Async("asyncExecutor")
////    public void createGrLineAsyncProcessV7(String company, String plant, String language,
////                                           String warehouse, List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
//////        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
////        String idMasterToken = getIDMasterAuthToken();
////        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
////        //PA_NO
////        NUMBER_RANGE_CODE = 7L;
////        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
////
////        log.info("PA number ----------------> {}", nextPANumber);
////        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
//////        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
//////                    try {
//////                        putwayHeaderProcessV7(grLine, nextPANumber, loginUserID, idMasterToken);
//////                    } catch (Exception e) {
//////                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
//////                    }
//////                }, asyncExecutor)) // inject the ExecutorService
//////                .collect(Collectors.toList());
//////        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
////
////        for (GrLineV2 grLine : createdGRLines) {
////            putwayHeaderProcessV7(grLine, nextPANumber, loginUserID, idMasterToken, authTokenForMastersService.getAccess_token());
////        }
////    }
//
////    /**
////     *
////     * @param createdGRLine namratha putawayHeader Creation
////     * @param nextPANumber putAwayNumber
////     * @param loginUserID userID
////     * @param idMasterToken IDMasterToken
////     * @throws Exception exception
////     */
////    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
////    public void processPutAwayHeaderV4(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken) throws Exception {
////        try {
////            DataBaseContextHolder.clear();
////            DataBaseContextHolder.setCurrentDb("NAMRATHA");
////                // Setting params
////                String languageId = createdGRLine.getLanguageId();
////                String companyCode = createdGRLine.getCompanyCode();
////                String plantId = createdGRLine.getPlantId();
////                String warehouseId = createdGRLine.getWarehouseId();
////                String itemCode = createdGRLine.getItemCode();
////                String manufacturerName = createdGRLine.getManufacturerName();
////                String preInboundNo = createdGRLine.getPreInboundNo();
////                String refDocNumber = createdGRLine.getRefDocNumber();
////
////
////                String proposedStorageBin = createdGRLine.getInterimStorageBin();
////                String alternateUom = createdGRLine.getAlternateUom();
////                Double bagSize = createdGRLine.getBagSize();
////
////                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
////                storageBinPutAway.setCompanyCodeId(companyCode);
////                storageBinPutAway.setPlantId(plantId);
////                storageBinPutAway.setLanguageId(languageId);
////                storageBinPutAway.setWarehouseId(warehouseId);
////
////                //  ASS_HE_NO
////                if (createdGRLine != null) {
////                    // Insert record into PutAwayHeader
////                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
////                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
////                    putAwayHeader.setCompanyCodeId(companyCode);
////                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
////                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
////
////                    //Inbound Quality Number
////                    NUMBER_RANGE_CODE = 23L;
////                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
////                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);
////
////                    //-----------------PROP_ST_BIN---------------------------------------------
////
////                    //V2 Code
////                    Long binClassId = 0L;                   //actual code follows
////                    if (createdGRLine.getInboundOrderTypeId() == null) {
////                        throw new BadRequestException("inbound order type id cannot be null");
////                    }
////                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
////                            createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
////                            createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
////                        binClassId = 1L;
////                    }
////                    if (createdGRLine.getInboundOrderTypeId() == 2) {
////                        binClassId = 7L;
////                    }
////                    log.info("BinClassId : " + binClassId);
////
////                    List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
////                            manufacturerName, alternateUom, bagSize, binClassId);
////                    log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);
////
////                    if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
////                        storageBinPutAway.setBinClassId(binClassId);
////                        storageBinPutAway.setBin(proposedStorageBin);
////                        StorageBinV2 storageBin = null;
////                        try {
////                            log.info("getStorageBin Input: " + storageBinPutAway);
////                            storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
////                        } catch (Exception e) {
////                            throw new BadRequestException("Invalid StorageBin");
////                        }
////                        log.info("InterimStorageBin: " + storageBin);
////                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
////                        if (storageBin != null) {
////                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
////                        }
////                        if (storageBin == null) {
////                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                        }
////                    }
////                    //BinClassId - 7 - Return Order(Sale Return)
////                    if (createdGRLine.getInboundOrderTypeId() == 2) {
////                        storageBinPutAway.setBinClassId(binClassId);
////                        log.info("BinClassId : " + binClassId);
////
////                        StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
////                        if (proposedBin != null) {
////                            putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
////                            putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
////                            log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
////                        }
////                    }
////
////                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
////                        log.info("BinClassId : " + binClassId);
////                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
////                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
////                                storageBinPutAway.setBinClassId(binClassId);
////                                storageBinPutAway.setStorageBin(inventoryStorageBinList);
////
////                                StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
////                                if (proposedExistingBin != null) {
////                                    proposedStorageBin = proposedExistingBin.getStorageBin();
////                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);
////
////                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
////                                }
////                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
////                            }
////                        }
////                    }
////
////                    if (putAwayHeader.getProposedStorageBin() == null) {
////                        StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
////                        log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
////                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                    }
////                    /////////////////////////////////////////////////////////////////////////////////////////////////////
////                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
////                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
////                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
////                            preInboundNo, refDocNumber);
////                    putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
////                    putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
////                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
////                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
////                    putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
////                    putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
////                    putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
////                    putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
////                    putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
////                    putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());
////
////                    //PROP_HE_NO	<- PAWAY_HE_NO
////                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
////                    putAwayHeader.setReferenceField5(itemCode);
////                    putAwayHeader.setReferenceField6(manufacturerName);
////                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
////                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
////                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
////
////                    Long statusId = 19L;
////                    putAwayHeader.setStatusId(statusId);
////                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
////                    putAwayHeader.setStatusDescription(statusDescription);
////
////                    //PA_NO
////                    NUMBER_RANGE_CODE = 6L;
////                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                    putAwayHeader.setDeletionIndicator(0L);
////                    putAwayHeader.setPackBarcodes(packBarcode);
////                    putAwayHeader.setCreatedBy(loginUserID);
////                    putAwayHeader.setUpdatedBy(loginUserID);
////                    putAwayHeader.setCreatedOn(new Date());
////                    putAwayHeader.setUpdatedOn(new Date());
////                    putAwayHeader.setConfirmedOn(new Date());
////                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
////                    log.info("putAwayHeader : " + putAwayHeader);
////
////                    // Updating Grline field -------------> PutAwayNumber
////                    log.info("Updation of PutAwayNumber on GrLine Started");
////                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
////                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
////                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
////                            putAwayHeader.getPutAwayNumber());
////
////                    log.info("Updation of PutAwayNumber on GrLine Completed");
////
////                    /*----------------Inventory tables Create---------------------------------------------*/
//////                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);
////
////                    //bypass quality header and line
////                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
////                }
////
////        } catch (Exception e) {
////            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            log.error("Exception while creating Putaway Header----> " + e.toString());
////            throw e;
////        }
////    }
//
////    /**
////     *
////     * @param createdGRLine namratha putawayHeader Creation
////     * @param nextPANumber putAwayNumber
////     * @param loginUserID userID
////     * @param idMasterToken IDMasterToken
////     * @throws Exception exception
////     */
////    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
////    public void processPutAwayHeaderV6(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken) throws Exception {
////        try {
////            DataBaseContextHolder.clear();
////            DataBaseContextHolder.setCurrentDb("BP");
////            // Setting params
////            String languageId = createdGRLine.getLanguageId();
////            String companyCode = createdGRLine.getCompanyCode();
////            String plantId = createdGRLine.getPlantId();
////            String warehouseId = createdGRLine.getWarehouseId();
////            String itemCode = createdGRLine.getItemCode();
////            String manufacturerName = createdGRLine.getManufacturerName();
////            String preInboundNo = createdGRLine.getPreInboundNo();
////            String refDocNumber = createdGRLine.getRefDocNumber();
////
////
////            String proposedStorageBin = createdGRLine.getInterimStorageBin();
////            String alternateUom = createdGRLine.getAlternateUom();
////            Double bagSize = createdGRLine.getBagSize();
////
////            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
////            storageBinPutAway.setCompanyCodeId(companyCode);
////            storageBinPutAway.setPlantId(plantId);
////            storageBinPutAway.setLanguageId(languageId);
////            storageBinPutAway.setWarehouseId(warehouseId);
////
////            //  ASS_HE_NO
////            if (createdGRLine != null) {
////                // Insert record into PutAwayHeader
////                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
////                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
////                putAwayHeader.setCompanyCodeId(companyCode);
////                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
////                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
////
////                //Inbound Quality Number
////                NUMBER_RANGE_CODE = 23L;
////                String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
////                log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);
////
////                //-----------------PROP_ST_BIN---------------------------------------------
////
////                //V2 Code
////                Long binClassId = 0L;                   //actual code follows
////                if (createdGRLine.getInboundOrderTypeId() == null) {
////                    throw new BadRequestException("inbound order type id cannot be null");
////                }
////                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
////                        createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
////                        createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
////                    binClassId = 1L;
////                }
////                if (createdGRLine.getInboundOrderTypeId() == 2) {
////                    binClassId = 7L;
////                }
////                log.info("BinClassId : " + binClassId);
////
////                List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
////                        manufacturerName, alternateUom, bagSize, binClassId);
////                log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);
////
////                if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
////                    storageBinPutAway.setBinClassId(binClassId);
////                    storageBinPutAway.setBin(proposedStorageBin);
////                    StorageBinV2 storageBin = null;
////                    try {
////                        log.info("getStorageBin Input: " + storageBinPutAway);
////                        storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
////                    } catch (Exception e) {
////                        throw new BadRequestException("Invalid StorageBin");
////                    }
////                    log.info("InterimStorageBin: " + storageBin);
////                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
////                    if (storageBin != null) {
////                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
////                    }
////                    if (storageBin == null) {
////                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                    }
////                }
////                //BinClassId - 7 - Return Order(Sale Return)
////                if (createdGRLine.getInboundOrderTypeId() == 2) {
////                    storageBinPutAway.setBinClassId(binClassId);
////                    log.info("BinClassId : " + binClassId);
////
////                    StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
////                    if (proposedBin != null) {
////                        putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
////                        putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
////                        log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
////                    }
////                }
////
////                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
////                    log.info("BinClassId : " + binClassId);
////                    if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
////                        if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
////                            storageBinPutAway.setBinClassId(binClassId);
////                            storageBinPutAway.setStorageBin(inventoryStorageBinList);
////
////                            StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
////                            if (proposedExistingBin != null) {
////                                proposedStorageBin = proposedExistingBin.getStorageBin();
////                                log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);
////
////                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                                putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
////                            }
////                            log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
////                        }
////                    }
////                }
////
////                if (putAwayHeader.getProposedStorageBin() == null) {
////                    StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
////                    log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
////                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                }
////                /////////////////////////////////////////////////////////////////////////////////////////////////////
////                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
////                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
////                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
////                        preInboundNo, refDocNumber);
////                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
////                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
////                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
////                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
////                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
////                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
////                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
////                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
////                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
////                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());
////
////                //PROP_HE_NO	<- PAWAY_HE_NO
////                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
////                putAwayHeader.setReferenceField5(itemCode);
////                putAwayHeader.setReferenceField6(manufacturerName);
////                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
////                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
////                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
////
////                Long statusId = 19L;
////                putAwayHeader.setStatusId(statusId);
////                statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
////                putAwayHeader.setStatusDescription(statusDescription);
////
////                //PA_NO
////                NUMBER_RANGE_CODE = 6L;
////                String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                putAwayHeader.setDeletionIndicator(0L);
////                putAwayHeader.setPackBarcodes(packBarcode);
////                putAwayHeader.setCreatedBy(loginUserID);
////                putAwayHeader.setUpdatedBy(loginUserID);
////                putAwayHeader.setCreatedOn(new Date());
////                putAwayHeader.setUpdatedOn(new Date());
////                putAwayHeader.setConfirmedOn(new Date());
////                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
////                log.info("putAwayHeader : " + putAwayHeader);
////
////                // Updating Grline field -------------> PutAwayNumber
////                log.info("Updation of PutAwayNumber on GrLine Started");
////                putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
////                        putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
////                        putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
////                        putAwayHeader.getPutAwayNumber());
////
////                log.info("Updation of PutAwayNumber on GrLine Completed");
////
////                /*----------------Inventory tables Create---------------------------------------------*/
////                inventoryService.createInventoryNonCBMV6(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);
////
////                //bypass quality header and line
////                inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
////            }
////
////        } catch (Exception e) {
////            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            log.error("Exception while creating Putaway Header----> " + e.toString());
////            throw e;
////        }
////    }
//
//
////
////    /**
////     * @param warehouseId
////     * @param binClassId
////     * @param companyCode
////     * @param plantId
////     * @param languageId
////     * @return
////     */
////    private StorageBinV2 getReserveBin(String warehouseId, Long binClassId, String companyCode, String plantId, String languageId) {
////        log.info("BinClassId : " + binClassId);
////        return storageBinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
////    }
//
////    /**
////     * @param createdGRLine grLine
////     * @param loginUserID userId
////     * @throws Exception exception
////     */
////    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
////    private void putwayHeaderProcessV7(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken, String masterToken) throws Exception {
////        try {
////            DataBaseContextHolder.clear();
////            DataBaseContextHolder.setCurrentDb("KNOWELL");
////                // Setting params
////                String languageId = createdGRLine.getLanguageId();
////                String companyCode = createdGRLine.getCompanyCode();
////                String plantId = createdGRLine.getPlantId();
////                String warehouseId = createdGRLine.getWarehouseId();
////                String itemCode = createdGRLine.getItemCode();
////                String manufacturerName = createdGRLine.getManufacturerName();
////                String preInboundNo = createdGRLine.getPreInboundNo();
////                String refDocNumber = createdGRLine.getRefDocNumber();
////
////                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
////                storageBinPutAway.setCompanyCodeId(companyCode);
////                storageBinPutAway.setPlantId(plantId);
////                storageBinPutAway.setLanguageId(languageId);
////                storageBinPutAway.setWarehouseId(warehouseId);
////
////                //  ASS_HE_NO
////                if (createdGRLine != null) {
////                    // Insert record into PutAwayHeader
////                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
////                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
////                    putAwayHeader.setCompanyCodeId(companyCode);
////                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
////                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
////
////                    //Inbound Quality Number
////                    NUMBER_RANGE_CODE = 23L;
////                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
////                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);
////
////                    //V2 Code
////                    Long binClassId = 0L;                   //actual code follows
////                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
////                        binClassId = 1L;
////                    }
////                    if (createdGRLine.getInboundOrderTypeId() == 2) {
////                        binClassId = 7L;
////                    }
////                    log.info("BinClassId : " + binClassId);
////
////                    Long CASE_QTY = createdGRLine.getNoBags().longValue();
////                    log.info("CASE_QTY ---> {}", CASE_QTY);
////                    Long REMAIN_BIN_QTY;
////                    Long OCC_BIN_QTY;
////
////                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
////                        // BinClassId - 1 - Live Bin E to P series empty bins and occ_qty < 20
////                        if (createdGRLine.getAcceptedQty() > 0.0) {
////                            StorageBinV2 dbStorageBinEorP = storageBinV2Repository.getEorPBinWithOccQty(companyCode, plantId, warehouseId);
////                            log.info("dbStorageBin E or P Series proposed bin Record ----> {}", dbStorageBinEorP);
////
////                            if (dbStorageBinEorP != null) {
////                                Long TOTAL_BIN_QTY = Long.valueOf(dbStorageBinEorP.getTotalQuantity());
////                                log.info("dbStorageBin E or P Series proposed bin TOTAL_BIN_QTY ----> {}", TOTAL_BIN_QTY);
////
////                                OCC_BIN_QTY = Long.valueOf(dbStorageBinEorP.getOccupiedQuantity()) + CASE_QTY;
////                                log.info("dbStorageBin E or P Series proposed bin OCC_BIN_QTY ----> {}", OCC_BIN_QTY);
////
////                                REMAIN_BIN_QTY = TOTAL_BIN_QTY - OCC_BIN_QTY;
////                                log.info("dbStorageBin E or P Series proposed bin REMAIN_BIN_QTY ----> {}", REMAIN_BIN_QTY);
////
////                                // Update TBLSTORAGEBIN occ_qty, remain_qty
////                                String occQty = String.valueOf(OCC_BIN_QTY);
////                                String remainQty = String.valueOf(REMAIN_BIN_QTY);
////                                storageBinV2Repository.updateBinQty(occQty, remainQty, dbStorageBinEorP.getStorageBin(), createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getWarehouseId());
////
////                                putAwayHeader.setProposedStorageBin(dbStorageBinEorP.getStorageBin());
////                                putAwayHeader.setLevelId(String.valueOf(dbStorageBinEorP.getFloorId()));
////                            }
////                        }
////
////                        //BinClassId - 7 - Return Order(Sale Return)
////                        if (createdGRLine.getDamageQty() > 0.0) {
////                            binClassId = 7L;
////                            storageBinPutAway.setBinClassId(binClassId);
////                            log.info("BinClassId : " + binClassId);
////
////                            StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV7(storageBinPutAway);
////                            if (proposedBin != null) {
////                                putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
////                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
////                                log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
////                            }
////                        }
////                        //BinClassId - 2 - RESEIVINGSTAGING bin
////                        if (putAwayHeader.getProposedStorageBin() == null) {
////                            StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
////                            log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
////                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                        }
////                    }
////                    /////////////////////////////////////////////////////////////////////////////////////////////////////
////                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
////                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
////                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
////                            preInboundNo, refDocNumber);
////                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
////                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
////                    putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
////                    putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
////                    putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
////                    //PROP_HE_NO	<- PAWAY_HE_NO
////                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
////                    putAwayHeader.setReferenceField5(itemCode);
////                    putAwayHeader.setReferenceField6(manufacturerName);
////                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
////                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
////                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
////
////                    Long statusId = 19L;
////                    putAwayHeader.setStatusId(statusId);
////                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
////                    putAwayHeader.setStatusDescription(statusDescription);
////
////                    //PA_NO
////                    NUMBER_RANGE_CODE = 6L;
////                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
////                    putAwayHeader.setDeletionIndicator(0L);
////                    putAwayHeader.setPackBarcodes(packBarcode);
////                    putAwayHeader.setCreatedBy(loginUserID);
////                    putAwayHeader.setUpdatedBy(loginUserID);
////                    putAwayHeader.setCreatedOn(new Date());
////                    putAwayHeader.setUpdatedOn(new Date());
////                    putAwayHeader.setConfirmedOn(new Date());
////                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
////                    log.info("putAwayHeader : " + putAwayHeader);
////
////                    // Staging_Header
////                    String orderText = "PutAwayHeader Created";
////                    inboundOrderV2Repository.updatePutawayHeader(putAwayHeader.getInboundOrderTypeId(), putAwayHeader.getRefDocNumber(), orderText);
////                    log.info("Update Staging Header Update Successfully");
////
//////                    putAwayHeaderV2Repository.updatePutAwayNumberV7(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
//////                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
//////                            putAwayHeader.getPutAwayNumber());
//////                    log.info("putAwayHeader Number Updated for Same RefDocNo");
////
////                    // Updating Grline field -------------> PutAwayNumber
////                    log.info("Updation of PutAwayNumber on GrLine Started");
////                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
////                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
////                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
////                            putAwayHeader.getPutAwayNumber());
////
////                    log.info("Updation of PutAwayNumber on GrLine Completed");
////                    /*----------------Inventory tables Create---------------------------------------------*/
////                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);
////
////                    //bypass quality header and line
////                    //inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
////                }
////        } catch (Exception e) {
////            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            log.error("Exception while creating Putaway Header----> " + e.toString());
////            throw e;
////        }
////    }
//
////    // V5
////    @Async("asyncExecutor")
////    public void createPutAwayHeaderNonCBMV5(List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
////        DataBaseContextHolder.clear();
////        DataBaseContextHolder.setCurrentDb("REEFERON");
////
////        String idMasterToken = getIDMasterAuthToken();
////        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
////        //PA_NO
////        NUMBER_RANGE_CODE = 7L;
////        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, createdGRLines.get(0).getCompanyCode(), createdGRLines.get(0).getPlantId(),
////                createdGRLines.get(0).getLanguageId(), createdGRLines.get(0).getWarehouseId(), idMasterToken);
////
////        Map<String, GrLineV2> groupedLines = createdGRLines.stream()
////                .collect(Collectors.toMap(
////                        gr -> gr.getItemCode() + "::" + gr.getBarcodeId(), // Composite key
////                        gr -> gr, // Initial value
////                        (gr1, gr2) -> {
////                            // Merge logic: Sum quantities
////                            gr1.setQtyInPiece(gr1.getQtyInPiece() + gr2.getQtyInPiece());
////                            gr1.setQtyInCase(gr1.getQtyInCase() + gr2.getQtyInCase());
////                            gr1.setQtyInCreate(gr1.getQtyInCreate() + gr2.getQtyInCreate());
////                            gr1.setGoodReceiptQty(gr1.getGoodReceiptQty() + gr2.getGoodReceiptQty());
////                            return gr1;
////                        }
////                ));
////
////        log.info("GrLine Process Started ------------> V5");
////        for (GrLineV2 grLine : groupedLines.values()) {
////            createPutAwayHeaderProcessV5(grLine, nextPANumber, loginUserID, authTokenForMastersService);
////        }
////    }
//
//
////    /**
////     * @param createdGRLine grLine Input's one by one
////     * @param loginUserID userID
////     * @throws Exception exceptin
////     */
////    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
////    private void createPutAwayHeaderProcessV5(GrLineV2 createdGRLine, String nextPANumber, String loginUserID,
////                                              AuthToken authTokenForMastersService) throws Exception {
////        try {
////            DataBaseContextHolder.clear();
////            DataBaseContextHolder.setCurrentDb("REEFERON");
////            log.info("PutAwayNumber------->" + createdGRLine);
////
////            String itemCode = createdGRLine.getItemCode();
////            String companyCode = createdGRLine.getCompanyCode();
////            String plantId = createdGRLine.getPlantId();
////            String languageId = createdGRLine.getLanguageId();
////            String warehouseId = createdGRLine.getWarehouseId();
////            String proposedStorageBin = createdGRLine.getInterimStorageBin();
////
////            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
////            storageBinPutAway.setCompanyCodeId(companyCode);
////            storageBinPutAway.setPlantId(plantId);
////            storageBinPutAway.setLanguageId(languageId);
////            storageBinPutAway.setWarehouseId(warehouseId);
////
////            Double cbm = 0D;
////
////            if (createdGRLine.getCbm() != null) {
////                cbm = createdGRLine.getCbm();
////                log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
////            }
////            //  ASS_HE_NO
////            if (createdGRLine != null) {
////                // Insert record into PutAwayHeader
////                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
////                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
////                putAwayHeader.setCompanyCodeId(companyCode);
////                putAwayHeader.setReferenceField5(itemCode);
////                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
////                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
////                //set bar code id for packbarcode
////                putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());
////                //set pack bar code for actual packbarcode
////                putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
////                if (createdGRLine.getAcceptedQty() != 0 && createdGRLine.getAcceptedQty() != null) {
////                    putAwayHeader.setPutAwayQuantity(createdGRLine.getAcceptedQty());
////                } else {
////                    putAwayHeader.setPutAwayQuantity(createdGRLine.getDamageQty());
////                }
////                //-----------------PROP_ST_BIN---------------------------------------------
////
////                //V2 Code
////                Long binClassId = 0L;                   //actual code follows
////                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 2 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 || createdGRLine.getInboundOrderTypeId() == 11L) {
////                    binClassId = 1L;
////                }
////                if (createdGRLine.getInboundOrderTypeId() == 3) {
////                    binClassId = 7L;
////                }
////                log.info("BinClassId : " + binClassId);
////                List<String> inventoryStorageBinList = inventoryV2Repository.getStorageBinListV5(companyCode, plantId, languageId, warehouseId, itemCode,
////                        createdGRLine.getManufacturerName(), binClassId, createdGRLine.getBarcodeId());
////                log.info("Inventory StorageBin List: " + inventoryStorageBinList);
////
////                log.info("InboundOrderTypeId----->" + createdGRLine.getInboundOrderTypeId());
////                if (createdGRLine.getInboundOrderTypeId() == 11L) {
////                    storageBinPutAway.setBinClassId(binClassId);
////                    log.info("BinClassId : " + binClassId);
////
////                    StorageBinV2 proposedBinClass1Bin = mastersService.getStorageBinBinClassId1(storageBinPutAway, authTokenForMastersService.getAccess_token());
////                    if (proposedBinClass1Bin != null) {
////                        String proposedStBin = proposedBinClass1Bin.getStorageBin();
////                        putAwayHeader.setProposedStorageBin(proposedStBin);
////                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass1Bin.getFloorId()));
////                        log.info("Return Order --> BinClassId1 Proposed Bin: " + proposedStBin);
////                        cbm = 0D;   //break the loop
////                    }
////                }
////
////                //BinClassId - 7 - Return Order(Sale Return)
////                if (createdGRLine.getInboundOrderTypeId() == 3L) {
////
////                    storageBinPutAway.setBinClassId(binClassId);
////                    log.info("BinClassId : " + binClassId);
////
////                    StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
////                    if (proposedBinClass7Bin != null) {
////                        String proposedStBin = proposedBinClass7Bin.getStorageBin();
////                        putAwayHeader.setProposedStorageBin(proposedStBin);
////                        putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
////                        log.info("Return Order --> BinClassId7 Proposed Bin: " + proposedStBin);
////                        cbm = 0D;   //break the loop
////                    }
////                    if (proposedBinClass7Bin == null) {
////                        binClassId = 2L;
////                        log.info("BinClassId : " + binClassId);
////                        StorageBinV2 stBin = mastersService.getStorageBin(
////                                companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
////                        log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
////                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                        cbm = 0D;   //break the loop
////                    }
////                }
////
////                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
////                    if (inventoryStorageBinList != null) {
////                        log.info("BinClassId : " + binClassId);
////                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
////                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
////                                storageBinPutAway.setBinClassId(binClassId);
////                                storageBinPutAway.setStorageBin(inventoryStorageBinList);
////
////                                StorageBinV2 proposedExistingBin = mastersService.getExistingStorageBinNonCbm(storageBinPutAway, authTokenForMastersService.getAccess_token());
////                                if (proposedExistingBin != null) {
////                                    proposedStorageBin = proposedExistingBin.getStorageBin();
////                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);
////
////                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
////                                }
////                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
////                                cbm = 0D;   //break the loop
////                            }
////                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
////                                storageBinPutAway.setBinClassId(7L);
////                                StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
////                                if (proposedBinClass7Bin != null) {
////                                    String proposedStBin = proposedBinClass7Bin.getStorageBin();
////                                    putAwayHeader.setProposedStorageBin(proposedStBin);
////                                    putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
////                                    log.info("Damage Qty --> BinClassId7 Proposed Bin: " + proposedStBin);
////                                    cbm = 0D;   //break the loop
////                                }
////                                if (proposedBinClass7Bin == null) {
////                                    binClassId = 2L;
////                                    log.info("BinClassId : " + binClassId);
////                                    StorageBinV2 stBin = mastersService.getStorageBin(
////                                            companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
////                                    log.info("Return Order --> reserveBin: " + stBin.getStorageBin());
////                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                                    cbm = 0D;   //break the loop
////                                }
////                            }
////                        }
////                    }
////                }
////                //Propose Empty Bin if Last picked bin is unavailable
////                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////                if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
////                    // If ST_BIN value is null
////                    // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
////                    // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
////                    log.info("QuantityType : " + createdGRLine.getQuantityType());
////                    log.info("BinClassId : " + binClassId);
////                    storageBinPutAway.setStatusId(0L);
////
////                    if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
////                        storageBinPutAway.setBinClassId(binClassId);
////
////                        if (putAwayHeader.getProposedStorageBin() == null) {
////                            binClassId = 1L;
////                            log.info("BinClassId : " + binClassId);
////                            StorageBinV2 stBin = storageBinV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndBinClassIdAndStatusIdAndDeletionIndicator(
////                                    companyCode, plantId, languageId, warehouseId, binClassId, 0L, 0L);
////                            if (stBin != null) {
////                                putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                                putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                                storageBinV2Repository.updateEmptyBinStatus(stBin.getStorageBin(), companyCode, plantId, warehouseId, 1L);
////                            } else {
////                                log.info("StorageBin BinClassID 1 Doesn't Exist ---------------->");
////                            }
////                            cbm = 0D;   //break the loop
////                        }
////
////                        if (putAwayHeader.getProposedStorageBin() == null) {
////                            binClassId = 2L;
////                            log.info("BinClassId : " + binClassId);
////                            StorageBinV2 stBin = mastersService.getStorageBin(
////                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
////                            log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
////                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                            cbm = 0D;   //break the loop
////                        }
////                    }
////
////                    /*
////                     * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
////                     * fetch ST_BIN values for STATUS_ID=EMPTY.
////                     */
////                    if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
////                        binClassId = 7L;
////                        storageBinPutAway.setBinClassId(binClassId);
////                        log.info("BinClassId : " + binClassId);
////                        StorageBinV2 proposedBinClass7Bin = mastersService.getStorageBinBinClassId7(storageBinPutAway, authTokenForMastersService.getAccess_token());
////                        if (proposedBinClass7Bin != null) {
////                            proposedStorageBin = proposedBinClass7Bin.getStorageBin();
////                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
////                            putAwayHeader.setLevelId(String.valueOf(proposedBinClass7Bin.getFloorId()));
////                            log.info("D --> BinClassId7 Proposed Bin: " + proposedStorageBin);
////                            cbm = 0D;   //break the loop
////                        }
////                        if (proposedBinClass7Bin == null) {
////                            binClassId = 2L;
////                            log.info("BinClassId : " + binClassId);
////                            StorageBinV2 stBin = mastersService.getStorageBin(
////                                    companyCode, plantId, languageId, warehouseId, binClassId, authTokenForMastersService.getAccess_token());
////                            log.info("D --> reserveBin: " + stBin.getStorageBin());
////                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
////                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
////                            cbm = 0D;   //break the loop
////                        }
////                    }
////                }
////                /////////////////////////////////////////////////////////////////////////////////////////////////////
////                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
////                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
////                //PROP_HE_NO	<- PAWAY_HE_NO
////                if (createdGRLine.getReferenceDocumentType() != null) {
////                    putAwayHeader.setReferenceDocumentType(createdGRLine.getReferenceDocumentType());
////                } else {
////                    putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(companyCode, plantId, languageId, warehouseId, createdGRLine.getInboundOrderTypeId()));
////                }
////                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
////                putAwayHeader.setCbmQuantity(createdGRLine.getCbmQuantity());
////
////                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
////                        languageId,
////                        plantId,
////                        warehouseId);
////
////                putAwayHeader.setCompanyDescription(description.getCompanyDesc());
////                putAwayHeader.setPlantDescription(description.getPlantDesc());
////                putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());
////
////                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
////                        createdGRLine.getPreInboundNo(), createdGRLine.getRefDocNumber());
////
////                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
////                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
////                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
////                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
////                putAwayHeader.setBatchSerialNumber(createdGRLine.getBatchSerialNumber());
////
////                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
////                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
////                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
////                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
////                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
////                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());
////
////                putAwayHeader.setReferenceField5(createdGRLine.getItemCode());
////                putAwayHeader.setReferenceField6(createdGRLine.getManufacturerName());
////                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
////                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
////                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
////                putAwayHeader.setReferenceField6(createdGRLine.getReferenceField6());
////
////                putAwayHeader.setStatusId(19L);
////                statusDescription = stagingLineV2Repository.getStatusDescription(19L, createdGRLine.getLanguageId());
////                putAwayHeader.setStatusDescription(statusDescription);
////
////                putAwayHeader.setDeletionIndicator(0L);
////                putAwayHeader.setCreatedBy(loginUserID);
////                putAwayHeader.setUpdatedBy(loginUserID);
////                putAwayHeader.setCreatedOn(new Date());
////                putAwayHeader.setUpdatedOn(new Date());
////                putAwayHeader.setConfirmedOn(new Date());
////                putAwayHeader.setQtyInCreate(createdGRLine.getQtyInCreate());
////                putAwayHeader.setQtyInCase(createdGRLine.getQtyInCase());
////                putAwayHeader.setQtyInPiece(createdGRLine.getQtyInPiece());
////                putAwayHeader.setManufacturerDate(createdGRLine.getManufacturerDate());
////                putAwayHeader.setVehicleNo(createdGRLine.getVehicleNo());
////                putAwayHeader.setVehicleUnloadingDate(createdGRLine.getVehicleUnloadingDate());
////                putAwayHeader.setVehicleReportingDate(createdGRLine.getVehicleReportingDate());
////                putAwayHeader.setReceivingVariance(createdGRLine.getReceivingVariance());
////                if (createdGRLine.getAcceptedQty() != null && createdGRLine.getAcceptedQty() != 0) {
////                    if (createdGRLine.getOrderUom().equalsIgnoreCase("Case")) {
////                        putAwayHeader.setOrderQty(createdGRLine.getQtyInCase());
////                    }
////                    if (createdGRLine.getOrderUom().equalsIgnoreCase("Crate")) {
////                        putAwayHeader.setOrderQty(createdGRLine.getQtyInCreate());
////                    }
////                    if (createdGRLine.getOrderUom().equalsIgnoreCase("Piece")) {
////                        putAwayHeader.setOrderQty(createdGRLine.getQtyInPiece());
////                    }
////                } else {
////                    putAwayHeader.setOrderQty(createdGRLine.getDamageQty());
////                }
////                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
////                log.info("putAwayHeader : " + putAwayHeader);
////
////                /*----------------Inventory tables Create---------------------------------------------*/
////                grLineService.createInventoryNonCBMV5(createdGRLine);
////            }
////
////        } catch (Exception e) {
////            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
////            log.error("Exception while creating Putaway Header----> " + e.toString());
////            throw e;
////        }
////    }
//
////    @Scheduled(fixedDelay = 5000)
////    public void getInventoryCreate() {
////        DataBaseContextHolder.clear();
////        DataBaseContextHolder.setCurrentDb("NAMRATHA");
////
////        List<GrLineV2> grLineV2s = grLineV2Repository.findByReferenceField4("0");
////        if(!grLineV2s.isEmpty()) {
////            log.info("Get GrLine Values Size is  {} ", grLineV2s.size());
////        }
////        for (GrLineV2 grLineV2 : grLineV2s) {
////            grLineV2Repository.updateGrLineRefField("10", grLineV2.getRefDocNumber(), grLineV2.getBarcodeId());
////            log.info("Grline Ref_field_10 updated BarcodeId is ---> {}", grLineV2.getBarcodeId());
////            String companyCodeId = grLineV2.getCompanyCode();
////            String plantId = grLineV2.getPlantId();
////            String languageId = grLineV2.getLanguageId();
////            String warehouseId = grLineV2.getWarehouseId();
////            String itemCode = grLineV2.getItemCode();
////            String mfrName = grLineV2.getManufacturerName();
////            String refDocNo = grLineV2.getRefDocNumber();
////            inventoryService.createInventoryNonCBMV8(companyCodeId, plantId, languageId, warehouseId, itemCode, mfrName, refDocNo, grLineV2);
////        }
////
////    }
//
//
//}
