package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PutAwayHeaderAsyncProcessService extends BaseService {

    @Autowired
    StorageBinService storageBinService;

    String statusDescription = null;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    InboundOrderV2Repository inboundOrderV2Repository;

    @Autowired
    PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    InboundQualityHeaderService inboundQualityHeaderService;

    @Autowired
    PreInboundHeaderService preInboundHeaderService;

    @Autowired
    GrLineService grLineService;

    @Autowired
    GrLineV2Repository grLineV2Repository;
    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    /**
     *
     * @param company company
     * @param plant plant
     * @param language language
     * @param warehouse warehouse
     * @param createdGRLines grLine
     * @param loginUserID userId
     */
    @Async("asyncExecutor")
    public void createGrLineAsyncProcessV4(String company, String plant, String language, String warehouse, List<GrLineV2> createdGRLines, String loginUserID) {
        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String idMasterToken = getIDMasterAuthToken();
        //PA_NO
        NUMBER_RANGE_CODE = 7L;
        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

        log.info("PA number ----------------> {}", nextPANumber);
        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
                    try {
                        processPutAwayHeaderV4(grLine, nextPANumber, loginUserID, idMasterToken);
                    } catch (Exception e) {
                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
                    }
                }, asyncExecutor)) // inject the ExecutorService
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//        try {
//            for (GrLineV2 grLine : createdGRLines) {
//                processPutAwayHeaderV4(grLine, nextPANumber, loginUserID, idMasterToken);
//            }
//        } catch (Exception e) {
//            log.error("Error processing GRLine:" + e);
//        }
    }

    /**
     *
     * @param company company
     * @param plant plant
     * @param language language
     * @param warehouse warehouse
     * @param createdGRLines grLine
     * @param loginUserID userId
     */
    @Async("asyncExecutor")
    public void createGrLineAsyncProcessV6(String company, String plant, String language, String warehouse, List<GrLineV2> createdGRLines, String loginUserID) {
        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String idMasterToken = getIDMasterAuthToken();
        //PA_NO
        NUMBER_RANGE_CODE = 7L;
        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

        log.info("PA number ----------------> {}", nextPANumber);
        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
                    try {
                        processPutAwayHeaderV6(grLine, nextPANumber, loginUserID, idMasterToken);
                    } catch (Exception e) {
                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
                    }
                }, asyncExecutor)) // inject the ExecutorService
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     *
     * @param company company
     * @param plant plant
     * @param language language
     * @param warehouse warehouse
     * @param createdGRLines grLine
     * @param loginUserID userId
     */
    @Async("asyncExecutor")
    public void createGrLineAsyncProcessV7(String company, String plant, String language,
                                           String warehouse, List<GrLineV2> createdGRLines, String loginUserID) {
        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String idMasterToken = getIDMasterAuthToken();
        //PA_NO
        NUMBER_RANGE_CODE = 7L;
        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

        log.info("PA number ----------------> {}", nextPANumber);
        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
                    try {
                        putwayHeaderProcessV7(grLine, nextPANumber, loginUserID, idMasterToken);
                    } catch (Exception e) {
                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
                    }
                }, asyncExecutor)) // inject the ExecutorService
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     *
     * @param createdGRLine namratha putawayHeader Creation
     * @param nextPANumber putAwayNumber
     * @param loginUserID userID
     * @param idMasterToken IDMasterToken
     * @throws Exception exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processPutAwayHeaderV4(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken) throws Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("NAMRATHA");
                // Setting params
                String languageId = createdGRLine.getLanguageId();
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String warehouseId = createdGRLine.getWarehouseId();
                String itemCode = createdGRLine.getItemCode();
                String manufacturerName = createdGRLine.getManufacturerName();
                String preInboundNo = createdGRLine.getPreInboundNo();
                String refDocNumber = createdGRLine.getRefDocNumber();


                String proposedStorageBin = createdGRLine.getInterimStorageBin();
                String alternateUom = createdGRLine.getAlternateUom();
                Double bagSize = createdGRLine.getBagSize();

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(companyCode);
                storageBinPutAway.setPlantId(plantId);
                storageBinPutAway.setLanguageId(languageId);
                storageBinPutAway.setWarehouseId(warehouseId);

                //  ASS_HE_NO
                if (createdGRLine != null) {
                    // Insert record into PutAwayHeader
                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayHeader.setCompanyCodeId(companyCode);
                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                    //Inbound Quality Number
                    NUMBER_RANGE_CODE = 23L;
                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

                    //-----------------PROP_ST_BIN---------------------------------------------

                    //V2 Code
                    Long binClassId = 0L;                   //actual code follows
                    if (createdGRLine.getInboundOrderTypeId() == null) {
                        throw new BadRequestException("inbound order type id cannot be null");
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
                            createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
                            createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
                        binClassId = 1L;
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        binClassId = 7L;
                    }
                    log.info("BinClassId : " + binClassId);

                    List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
                            manufacturerName, alternateUom, bagSize, binClassId);
                    log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);

                    if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                        storageBinPutAway.setBinClassId(binClassId);
                        storageBinPutAway.setBin(proposedStorageBin);
                        StorageBinV2 storageBin = null;
                        try {
                            log.info("getStorageBin Input: " + storageBinPutAway);
                            storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }
                        log.info("InterimStorageBin: " + storageBin);
                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                        if (storageBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                        }
                        if (storageBin == null) {
                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        }
                    }
                    //BinClassId - 7 - Return Order(Sale Return)
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        storageBinPutAway.setBinClassId(binClassId);
                        log.info("BinClassId : " + binClassId);

                        StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                        if (proposedBin != null) {
                            putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                            log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
                        }
                    }

                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                        log.info("BinClassId : " + binClassId);
                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                                storageBinPutAway.setBinClassId(binClassId);
                                storageBinPutAway.setStorageBin(inventoryStorageBinList);

                                StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
                                if (proposedExistingBin != null) {
                                    proposedStorageBin = proposedExistingBin.getStorageBin();
                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                                }
                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                            }
                        }
                    }

                    if (putAwayHeader.getProposedStorageBin() == null) {
                        StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                        log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                    }
                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber);
                    putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                    putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
                    putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                    putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                    putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                    putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
                    putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
                    putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());

                    //PROP_HE_NO	<- PAWAY_HE_NO
                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                    putAwayHeader.setReferenceField5(itemCode);
                    putAwayHeader.setReferenceField6(manufacturerName);
                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                    Long statusId = 19L;
                    putAwayHeader.setStatusId(statusId);
                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    //PA_NO
                    NUMBER_RANGE_CODE = 6L;
                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setPackBarcodes(packBarcode);
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    // Updating Grline field -------------> PutAwayNumber
                    log.info("Updation of PutAwayNumber on GrLine Started");
                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                            putAwayHeader.getPutAwayNumber());

                    log.info("Updation of PutAwayNumber on GrLine Completed");

                    /*----------------Inventory tables Create---------------------------------------------*/
                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    //bypass quality header and line
//                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
                }

        } catch (Exception e) {
            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }

    /**
     *
     * @param createdGRLine namratha putawayHeader Creation
     * @param nextPANumber putAwayNumber
     * @param loginUserID userID
     * @param idMasterToken IDMasterToken
     * @throws Exception exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void processPutAwayHeaderV6(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken) throws Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("BP");
            // Setting params
            String languageId = createdGRLine.getLanguageId();
            String companyCode = createdGRLine.getCompanyCode();
            String plantId = createdGRLine.getPlantId();
            String warehouseId = createdGRLine.getWarehouseId();
            String itemCode = createdGRLine.getItemCode();
            String manufacturerName = createdGRLine.getManufacturerName();
            String preInboundNo = createdGRLine.getPreInboundNo();
            String refDocNumber = createdGRLine.getRefDocNumber();


            String proposedStorageBin = createdGRLine.getInterimStorageBin();
            String alternateUom = createdGRLine.getAlternateUom();
            Double bagSize = createdGRLine.getBagSize();

            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);

            //  ASS_HE_NO
            if (createdGRLine != null) {
                // Insert record into PutAwayHeader
                PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                putAwayHeader.setCompanyCodeId(companyCode);
                putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                //Inbound Quality Number
                NUMBER_RANGE_CODE = 23L;
                String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

                //-----------------PROP_ST_BIN---------------------------------------------

                //V2 Code
                Long binClassId = 0L;                   //actual code follows
                if (createdGRLine.getInboundOrderTypeId() == null) {
                    throw new BadRequestException("inbound order type id cannot be null");
                }
                if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
                        createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
                        createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
                    binClassId = 1L;
                }
                if (createdGRLine.getInboundOrderTypeId() == 2) {
                    binClassId = 7L;
                }
                log.info("BinClassId : " + binClassId);

                List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
                        manufacturerName, alternateUom, bagSize, binClassId);
                log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);

                if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
                    storageBinPutAway.setBinClassId(binClassId);
                    storageBinPutAway.setBin(proposedStorageBin);
                    StorageBinV2 storageBin = null;
                    try {
                        log.info("getStorageBin Input: " + storageBinPutAway);
                        storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
                    } catch (Exception e) {
                        throw new BadRequestException("Invalid StorageBin");
                    }
                    log.info("InterimStorageBin: " + storageBin);
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
                    if (storageBin != null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                        putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
                    }
                    if (storageBin == null) {
                        putAwayHeader.setProposedStorageBin(proposedStorageBin);
                    }
                }
                //BinClassId - 7 - Return Order(Sale Return)
                if (createdGRLine.getInboundOrderTypeId() == 2) {
                    storageBinPutAway.setBinClassId(binClassId);
                    log.info("BinClassId : " + binClassId);

                    StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
                    if (proposedBin != null) {
                        putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                        putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                        log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
                    }
                }

                if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                    log.info("BinClassId : " + binClassId);
                    if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
                        if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
                            storageBinPutAway.setBinClassId(binClassId);
                            storageBinPutAway.setStorageBin(inventoryStorageBinList);

                            StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
                            if (proposedExistingBin != null) {
                                proposedStorageBin = proposedExistingBin.getStorageBin();
                                log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);

                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
                                putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
                            }
                            log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
                        }
                    }
                }

                if (putAwayHeader.getProposedStorageBin() == null) {
                    StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                    log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                }
                /////////////////////////////////////////////////////////////////////////////////////////////////////
                log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                        preInboundNo, refDocNumber);
                putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
                putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
                putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
                putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
                putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
                putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());

                //PROP_HE_NO	<- PAWAY_HE_NO
                putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                putAwayHeader.setReferenceField5(itemCode);
                putAwayHeader.setReferenceField6(manufacturerName);
                putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                Long statusId = 19L;
                putAwayHeader.setStatusId(statusId);
                statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                putAwayHeader.setStatusDescription(statusDescription);

                //PA_NO
                NUMBER_RANGE_CODE = 6L;
                String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                putAwayHeader.setDeletionIndicator(0L);
                putAwayHeader.setPackBarcodes(packBarcode);
                putAwayHeader.setCreatedBy(loginUserID);
                putAwayHeader.setUpdatedBy(loginUserID);
                putAwayHeader.setCreatedOn(new Date());
                putAwayHeader.setUpdatedOn(new Date());
                putAwayHeader.setConfirmedOn(new Date());
                putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                log.info("putAwayHeader : " + putAwayHeader);

                // Updating Grline field -------------> PutAwayNumber
                log.info("Updation of PutAwayNumber on GrLine Started");
                putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                        putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                        putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                        putAwayHeader.getPutAwayNumber());

                log.info("Updation of PutAwayNumber on GrLine Completed");

                /*----------------Inventory tables Create---------------------------------------------*/
                inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                //bypass quality header and line
                inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
            }

        } catch (Exception e) {
            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }



    /**
     * @param warehouseId
     * @param binClassId
     * @param companyCode
     * @param plantId
     * @param languageId
     * @return
     */
    private StorageBinV2 getReserveBin(String warehouseId, Long binClassId, String companyCode, String plantId, String languageId) {
        log.info("BinClassId : " + binClassId);
        return storageBinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
    }

    /**
     * @param createdGRLine grLine
     * @param loginUserID userId
     * @throws Exception exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private void putwayHeaderProcessV7(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken) throws Exception {
        try {
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("KNOWELL");
                // Setting params
                String languageId = createdGRLine.getLanguageId();
                String companyCode = createdGRLine.getCompanyCode();
                String plantId = createdGRLine.getPlantId();
                String warehouseId = createdGRLine.getWarehouseId();
                String itemCode = createdGRLine.getItemCode();
                String manufacturerName = createdGRLine.getManufacturerName();
                String preInboundNo = createdGRLine.getPreInboundNo();
                String refDocNumber = createdGRLine.getRefDocNumber();

                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                storageBinPutAway.setCompanyCodeId(companyCode);
                storageBinPutAway.setPlantId(plantId);
                storageBinPutAway.setLanguageId(languageId);
                storageBinPutAway.setWarehouseId(warehouseId);

                //  ASS_HE_NO
                if (createdGRLine != null) {
                    // Insert record into PutAwayHeader
                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
                    putAwayHeader.setCompanyCodeId(companyCode);
                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());

                    //Inbound Quality Number
                    NUMBER_RANGE_CODE = 23L;
                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);

                    //V2 Code
                    Long binClassId = 0L;                   //actual code follows
                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 || createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5) {
                        binClassId = 1L;
                    }
                    if (createdGRLine.getInboundOrderTypeId() == 2) {
                        binClassId = 7L;
                    }
                    log.info("BinClassId : " + binClassId);

                    StorageBinV2 dbStorageBinEorP = storageBinV2Repository.getEorPBin(companyCode, plantId, warehouseId);
                    log.info("dbStorageBin E or P Series proposed bin Record ----> {}", dbStorageBinEorP);

                    putAwayHeader.setProposedStorageBin(dbStorageBinEorP.getStorageBin());
                    putAwayHeader.setLevelId(String.valueOf(dbStorageBinEorP.getFloorId()));

                    /////////////////////////////////////////////////////////////////////////////////////////////////////
                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                            preInboundNo, refDocNumber);
                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
                    putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
                    putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
                    putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
                    //PROP_HE_NO	<- PAWAY_HE_NO
                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
                    putAwayHeader.setReferenceField5(itemCode);
                    putAwayHeader.setReferenceField6(manufacturerName);
                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));

                    Long statusId = 19L;
                    putAwayHeader.setStatusId(statusId);
                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
                    putAwayHeader.setStatusDescription(statusDescription);

                    //PA_NO
                    NUMBER_RANGE_CODE = 6L;
                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
                    putAwayHeader.setDeletionIndicator(0L);
                    putAwayHeader.setPackBarcodes(packBarcode);
                    putAwayHeader.setCreatedBy(loginUserID);
                    putAwayHeader.setUpdatedBy(loginUserID);
                    putAwayHeader.setCreatedOn(new Date());
                    putAwayHeader.setUpdatedOn(new Date());
                    putAwayHeader.setConfirmedOn(new Date());
                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                    log.info("putAwayHeader : " + putAwayHeader);

                    // Staging_Header
                    String orderText = "PutAwayHeader Created";
                    inboundOrderV2Repository.updatePutawayHeader(putAwayHeader.getInboundOrderTypeId(), putAwayHeader.getRefDocNumber(), orderText);
                    log.info("Update Staging Header Update Successfully");

//                    putAwayHeaderV2Repository.updatePutAwayNumberV7(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
//                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
//                            putAwayHeader.getPutAwayNumber());
//                    log.info("putAwayHeader Number Updated for Same RefDocNo");

                    // Updating Grline field -------------> PutAwayNumber
                    log.info("Updation of PutAwayNumber on GrLine Started");
                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                            putAwayHeader.getPutAwayNumber());

                    log.info("Updation of PutAwayNumber on GrLine Completed");
                    /*----------------Inventory tables Create---------------------------------------------*/
                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    //bypass quality header and line
                    //inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
                }
        } catch (Exception e) {
            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }
}
