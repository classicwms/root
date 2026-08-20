package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.containerreceipt.v2.ContainerReceiptV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    CrossDockServiceV9 crossDockService;

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
    ContainerReceiptRepository containerReceiptRepository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    GrLineV2Repository grLineV2Repository;
    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    /**
     *
     * @param createdGRLines grLine
     * @param loginUserID userId
     */
    @Async("asyncExecutor")
    public void createGrLineAsyncProcessV4(List<GrLineV2> createdGRLines, String loginUserID) {
        String idMasterToken = getIDMasterAuthToken();
        List<PutAwayHeaderV2> putAwayHeaderV2List = new ArrayList<>();
        //PA_NO
//        NUMBER_RANGE_CODE = 7L;
//        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

//        log.info("PA number ----------------> {}", nextPANumber);
        grLineService.fireBaseNotification(createdGRLines.get(0), createdGRLines.get(0).getPutAwayNumber(), loginUserID);
        try {
            for (GrLineV2 grLine : createdGRLines) {
                putAwayHeaderV2List.add(processPutAwayHeaderV4(grLine, loginUserID, idMasterToken));
            }
            if (!putAwayHeaderV2List.isEmpty()) {
                log.info("PutAwayHeader Saved List Size is {}", putAwayHeaderV2List.size());
                putAwayHeaderV2Repository.saveAll(putAwayHeaderV2List);
            }
        } catch (Exception e) {
            log.error("Error processing GRLine:" + e);
        }
    }

    /**
     *
     * @param company company_id
     * @param plant plant_id
     * @param language language_id
     * @param warehouse warehouse_id
     * @param createdGRLines grLine_response
     * @param loginUserID loginUserId
     */
//    public void createGrLineAsyncProcessV4(String company, String plant, String language,
//                                           String warehouse, List<GrLineV2> createdGRLines,
//                                           String loginUserID) {
//
//        final Long NUMBER_RANGE_CODE = 7L;
//        String idMasterToken = getIDMasterAuthToken();
//        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
//
//        log.info("PA number ----------------> {}", nextPANumber);
//        grLineService.fireBaseNotification(createdGRLines.get(0), nextPANumber, loginUserID);
//
//        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        try {
//            List<CompletableFuture<PutAwayHeaderV2>> futures = createdGRLines.stream()
//                    .map(grLine -> CompletableFuture.supplyAsync(() -> {
//                        try {
//                            return processPutAwayHeaderV4(grLine, nextPANumber, loginUserID, idMasterToken);
//                        } catch (Exception e) {
//                            log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
//                            throw new CompletionException(e);
//                        }
//                    }, asyncExecutor))
//                    .collect(Collectors.toList());
//
//            // Wait for all and collect results
//            List<PutAwayHeaderV2> putAwayHeaderV2List = futures.stream()
//                    .map(CompletableFuture::join).filter(Objects::nonNull).collect(Collectors.toList());
//
//            // Save all headers
//            if (!putAwayHeaderV2List.isEmpty()) {
//                log.info("PutAwayHeader Saved List Size is {}", putAwayHeaderV2List.size());
//                putAwayHeaderV2Repository.saveAll(putAwayHeaderV2List);
//            }
//
//        } catch (Exception e) {
//            log.error("Error during async PutAwayHeader processing", e);
//            throw e; // or handle appropriately
//        } finally {
//            asyncExecutor.shutdown();
//        }
//    }

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
                                           String warehouse, List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
//        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String idMasterToken = getIDMasterAuthToken();
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        //PA_NO
        NUMBER_RANGE_CODE = 7L;
        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);

        log.info("PA number ----------------> {}", nextPANumber);
        grLineService.fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
//        List<CompletableFuture<Void>> futures = createdGRLines.stream().map(grLine -> CompletableFuture.runAsync(() -> {
//                    try {
//                        putwayHeaderProcessV7(grLine, nextPANumber, loginUserID, idMasterToken);
//                    } catch (Exception e) {
//                        log.error("Error processing GRLine: {}", grLine.getLineNo(), e);
//                    }
//                }, asyncExecutor)) // inject the ExecutorService
//                .collect(Collectors.toList());
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (GrLineV2 grLine : createdGRLines) {
            putwayHeaderProcessV7(grLine, nextPANumber, loginUserID, idMasterToken, authTokenForMastersService.getAccess_token());
        }
    }

    /**
     *
     * @param createdGRLine namratha putawayHeader Creation
     * @param loginUserID userID
     * @param idMasterToken IDMasterToken
     * @throws Exception exception
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Retryable(value = {org.springframework.dao.DeadlockLoserDataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public PutAwayHeaderV2 processPutAwayHeaderV4(GrLineV2 createdGRLine, String loginUserID, String idMasterToken) throws Exception {
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

            // Insert record into PutAwayHeader
            PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setCompanyCodeId(companyCode);
            putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
            putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
            putAwayHeader.setPutAwayNumber(createdGRLine.getPutAwayNumber());                           //PutAway Number

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
            putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
            putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());

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
//            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
//            log.info("putAwayHeader created : " + putAwayHeader);

            // Updating Grline field -------------> PutAwayNumber
//            log.info("Updation of PutAwayNumber on GrLine Started");
//            putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
//                    putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
//                    putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
//                    putAwayHeader.getPutAwayNumber());
//            log.info("Updation of PutAwayNumber on GrLine Completed");

            /*----------------Inventory tables Create---------------------------------------------*/
            inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

            //bypass quality header and line
//                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);

            return putAwayHeader;
        } catch (Exception e) {
            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }
    /**
     * Updating PutawayNumber in grlines with retryable
     *
     */
    @Retryable(value = { org.springframework.dao.DeadlockLoserDataAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public void updatePutAwayNumber(PutAwayHeaderV2 putAwayHeader, GrLineV2 createdGRLine) {
        putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                putAwayHeader.getPutAwayNumber());
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
    private void putwayHeaderProcessV7(GrLineV2 createdGRLine, String nextPANumber, String loginUserID, String idMasterToken, String masterToken) throws Exception {
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

                    Long CASE_QTY = 1L;
                    log.info("CASE_QTY ---> {}", CASE_QTY);
                    Long REMAIN_BIN_QTY;
                    Long OCC_BIN_QTY;

                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
                        // BinClassId - 1 - Live Bin E to P series empty bins and occ_qty < 20
                        if (createdGRLine.getAcceptedQty() > 0.0) {
                            StorageBinV2 dbStorageBinEorP = storageBinV2Repository.getEorPBinWithOccQty(companyCode, plantId, warehouseId);
                            log.info("dbStorageBin E or P Series proposed bin Record ----> {}", dbStorageBinEorP);

                            if (dbStorageBinEorP != null) {
                                Long TOTAL_BIN_QTY = Long.valueOf(dbStorageBinEorP.getTotalQuantity());
                                log.info("dbStorageBin E or P Series proposed bin TOTAL_BIN_QTY ----> {}", TOTAL_BIN_QTY);

                                OCC_BIN_QTY = Long.valueOf(dbStorageBinEorP.getOccupiedQuantity()) + CASE_QTY;
                                log.info("dbStorageBin E or P Series proposed bin OCC_BIN_QTY ----> {}", OCC_BIN_QTY);

                                REMAIN_BIN_QTY = TOTAL_BIN_QTY - OCC_BIN_QTY;
                                log.info("dbStorageBin E or P Series proposed bin REMAIN_BIN_QTY ----> {}", REMAIN_BIN_QTY);

                                // Update TBLSTORAGEBIN occ_qty, remain_qty
                                String occQty = String.valueOf(OCC_BIN_QTY);
                                String remainQty = String.valueOf(REMAIN_BIN_QTY);
                                storageBinV2Repository.updateBinQty(occQty, remainQty, dbStorageBinEorP.getStorageBin(), createdGRLine.getCompanyCode(), createdGRLine.getPlantId(), createdGRLine.getWarehouseId());

                                putAwayHeader.setProposedStorageBin(dbStorageBinEorP.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(dbStorageBinEorP.getFloorId()));
                            }
                        }

                        //BinClassId - 7 - Return Order(Sale Return)
                        if (createdGRLine.getDamageQty() > 0.0) {
                            binClassId = 7L;
                            storageBinPutAway.setBinClassId(binClassId);
                            log.info("BinClassId : " + binClassId);

                            StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV7(storageBinPutAway);
                            if (proposedBin != null) {
                                putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
                                log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
                            }
                        }
                        //BinClassId - 2 - RESEIVINGSTAGING bin
                        if (putAwayHeader.getProposedStorageBin() == null) {
                            StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
                            log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
                            putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
                            putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
                        }
                    }
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

                    Optional<PutAwayHeaderV2> dbPutawayHeader = putAwayHeaderV2Repository.getPutAwayHeaderForValidation(putAwayHeader.getBarcodeId(), putAwayHeader.getRefDocNumber());

                    if (dbPutawayHeader.isPresent()) {
                        log.error("PutAwayHeader is already Present for the Order " + putAwayHeader.getRefDocNumber() + " and BarcodeId " + putAwayHeader.getBarcodeId());
                    } else {
                        putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                        log.info("putAwayHeader : " + putAwayHeader);

                        // Staging_Header
                        String orderText = "PutAwayHeader Created";
                        inboundOrderV2Repository.updatePutawayHeader(putAwayHeader.getInboundOrderTypeId(), putAwayHeader.getRefDocNumber(), orderText);
                        log.info("Update Staging Header Update Successfully");

                        // Updating Grline field -------------> PutAwayNumber
                        log.info("Updation of PutAwayNumber on GrLine Started");
                        putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
                                putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
                                putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
                                putAwayHeader.getPutAwayNumber());

                        log.info("Updation of PutAwayNumber on GrLine Completed");
                        /*----------------Inventory tables Create---------------------------------------------*/
                        inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);

                    }
                }
        } catch (Exception e) {
            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }

    //================================================BF=============================================
    @Async("asyncExecutor")
    public void createPutAwayHeaderNonCBMV9(List<GrLineV2> createdGRLines, String loginUserID, Long crossDock) throws Exception {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName( createdGRLines.get(0).getCompanyCode(),
                createdGRLines.get(0).getPlantId(), createdGRLines.get(0).getWarehouseId());
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        String idMasterToken = getIDMasterAuthToken();
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        //PA_NO
        NUMBER_RANGE_CODE = 7L;
        String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, createdGRLines.get(0).getCompanyCode(), createdGRLines.get(0).getPlantId(),
                createdGRLines.get(0).getLanguageId(), createdGRLines.get(0).getWarehouseId(), idMasterToken);
        log.info("PutAwayHeader Creation Process Started ------------> V9 <--------------------------------");

        try {
            for (GrLineV2 grLine : createdGRLines) {
                createPutAwayHeaderProcessV9(grLine, nextPANumber, loginUserID, authTokenForMastersService, crossDock);
            }
            String orderText = "PutAwayHeaders Created";
            Long orderStatus = 1L;
            putAwayHeaderV2Repository.updatePutAwayHeader(createdGRLines.get(0).getRefDocNumber(), orderText, orderStatus);
        } catch (Exception e) {
            String orderText = "PutAwayHeaders Create error";
            Long orderStatus = 100L;
            putAwayHeaderV2Repository.updatePutAwayHeader(createdGRLines.get(0).getRefDocNumber(), orderText, orderStatus);
        }
    }
    //================================================BF=============================================

    /**
     * BF
     *
     * @param createdGRLine grLine Input's one by one
     * @param loginUserID   userID
     * @throws Exception exceptin
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private void createPutAwayHeaderProcessV9(GrLineV2 createdGRLine, String nextPANumber, String loginUserID,
                                              AuthToken authTokenForMastersService, Long crossDock) throws Exception {
        try {

            log.info("PutAwayNumber------->" + createdGRLine);

            String itemCode = createdGRLine.getItemCode();
            String companyCode = createdGRLine.getCompanyCode();
            String plantId = createdGRLine.getPlantId();
            String languageId = createdGRLine.getLanguageId();
            String warehouseId = createdGRLine.getWarehouseId();

            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb("MT");
            String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(routingDb);
            log.info("Current Routing Db " + routingDb);

            Double cbm = 0D;
            if (createdGRLine.getCbm() != null) {
                cbm = createdGRLine.getCbm();
                log.info("cbm, createdGrLine.getCbm: " + cbm + ", " + createdGRLine.getCbm());
            }
            //  ASS_HE_NO
            // Insert record into PutAwayHeader
            PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
            BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
            putAwayHeader.setCompanyCodeId(companyCode);
            putAwayHeader.setReferenceField5(itemCode);
            putAwayHeader.setPalletCode(createdGRLine.getPalletCode());
            putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
            putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
            putAwayHeader.setManufacturerDate(createdGRLine.getManufacturerDate());
            putAwayHeader.setExpiryDate(createdGRLine.getExpiryDate());
            putAwayHeader.setReferenceField1(createdGRLine.getReferenceField1());
            putAwayHeader.setParentProductionOrderNo(createdGRLine.getParentProductionOrderNo());

            putAwayHeader.setMrp(createdGRLine.getMrp());                                //MRP
            putAwayHeader.setAMSSupplierInvoiceNo(createdGRLine.getHsnCode());           //NetWeight
            putAwayHeader.setTransferRequestType(createdGRLine.getVariantType());                    //GrossWeight
            putAwayHeader.setBrand(createdGRLine.getSpecificationActual());                 //totalWeight
            ContainerReceiptV2 containerReceiptV2 = containerReceiptRepository.getContainerReceipt(createdGRLine.getCompanyCode(), createdGRLine.getLanguageId(), createdGRLine.getPlantId(),
                    createdGRLine.getWarehouseId(), createdGRLine.getRefDocNumber());
            if(containerReceiptV2 != null){
                putAwayHeader.setItemGroup(containerReceiptV2.getReferenceField1());        //ItemGroup
            }

            //InventoryOwner
            if (createdGRLine.getMaterialNo() != null) {
                putAwayHeader.setMaterialNo(createdGRLine.getMaterialNo());
            }
            //PriceSegment
            if(createdGRLine.getPriceSegment() != null){
                putAwayHeader.setPriceSegment(createdGRLine.getPriceSegment());
            }

            //set bar code id for packbarcode
            putAwayHeader.setBarcodeId(createdGRLine.getBarcodeId());
            //set pack bar code for actual packbarcode
            putAwayHeader.setPackBarcodes(createdGRLine.getPackBarcodes());
            if (createdGRLine.getAcceptedQty() != null && createdGRLine.getAcceptedQty() != 0) {
                putAwayHeader.setPutAwayQuantity(createdGRLine.getAcceptedQty());
            } else {
                putAwayHeader.setPutAwayQuantity(createdGRLine.getDamageQty());
            }
            // PutAwayHeader Created based on bin size
            log.info("PutAwayHeader Creation & Allocated Bin For OrderQty Logic is  Started ------------------------>V9 <--------------------------");
            allocateBinsForQtyV9(createdGRLine, loginUserID, authTokenForMastersService, companyCode, plantId, languageId, warehouseId, putAwayHeader, createdGRLine.getReferenceOrderQty(), crossDock);
            log.info("PutAwayHeader Creation & Allocated Bin For OrderQty Logic is  Completed ------------------------>V9 <--------------------------");
        } catch (Exception e) {
//            log.info("RollPack In GrLine Input Values is RefDocNumber {}, PreInboundNo {}, BarcodeId {} ", createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
//            grLineV2Repository.rollPackGrLine(createdGRLine.getRefDocNumber(), createdGRLine.getPreInboundNo(), createdGRLine.getBarcodeId());
            log.error("Exception while creating Putaway Header----> " + e.toString());
            throw e;
        }
    }

    //================================================BF=====================================================

    /**
     * PutAwayHeader Created for only 30 order capacity qty
     *
     * @throws Exception
     */
    private void allocateBinsForQtyV9(GrLineV2 grLine, String loginUserID, AuthToken authTokenForMastersService,
                                      String companyCode, String plantId, String languageId, String warehouseId,
                                      PutAwayHeaderV2 putAwayHeader, Double uomQty, Long crossDock) throws Exception {

        String itemCode = grLine.getItemCode();
        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(companyCode);
        storageBinPutAway.setPlantId(plantId);
        storageBinPutAway.setLanguageId(languageId);
        storageBinPutAway.setWarehouseId(warehouseId);

        //-----------------PROP_ST_BIN---------------------------------------------
        //V2 Code
        Long binClassId = 0L;                   //actual code follows
        if (grLine.getInboundOrderTypeId() == 1 || grLine.getInboundOrderTypeId() == 3 ||
                grLine.getInboundOrderTypeId() == 4 || grLine.getInboundOrderTypeId() == 5 || grLine.getInboundOrderTypeId() == 11L) {
            binClassId = 1L;
        }
        if (grLine.getInboundOrderTypeId() == 2) {
            binClassId = 7L;
        }
        if (grLine.getQuantityType().equalsIgnoreCase("D")) {
            binClassId = 7L;
        }
        double allocated = 0.0;
        String proposedStorageBin = grLine.getInterimStorageBin();
        if (proposedStorageBin == null) {
            log.info("Proposed StorageBin Logic Started -------------------> V9 <-------------->");
            setStorageBinForPutAwayHeaderV9(putAwayHeader, binClassId, companyCode, plantId, languageId, warehouseId,
                    grLine, storageBinPutAway);
        } else {
            putAwayHeader.setProposedStorageBin(proposedStorageBin);
        }
        log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
        log.info("Proposed StorageBin Logic Completed -------------------> V9 <-------------->");
        if (grLine.getReferenceDocumentType() != null) {
            putAwayHeader.setReferenceDocumentType(grLine.getReferenceDocumentType());
        } else {
            putAwayHeader.setReferenceDocumentType(getInboundOrderTypeDesc(companyCode, plantId, languageId, warehouseId, grLine.getInboundOrderTypeId()));
        }

        // PalletId for DamageBin
        if(grLine.getQuantityType().equalsIgnoreCase("D")) {

            String palletId = inventoryV2Repository.getPalletIdV9(grLine.getCompanyCode(), grLine.getPlantId(), grLine.getLanguageId(),
                    grLine.getWarehouseId(), putAwayHeader.getProposedStorageBin());
            log.info("Damage Item Get The Pallet Id {}  in DamageBin {} ", palletId, putAwayHeader.getProposedStorageBin());
            if(palletId != null) {
                putAwayHeader.setPalletCode(palletId);
                grLine.setPalletCode(palletId);
            } else {
                String palletCode = getNextRangeNumber(30L, grLine.getCompanyCode(),
                        grLine.getPlantId(), grLine.getLanguageId(),
                        grLine.getWarehouseId());
                putAwayHeader.setPalletCode("M" + palletCode);
                grLine.setPalletCode("M" + palletCode);
            }
        }
        putAwayHeader.setProposedHandlingEquipment(grLine.getPutAwayHandlingEquipment());
        putAwayHeader.setCbmQuantity(grLine.getCbmQuantity());

        IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
        putAwayHeader.setCompanyDescription(description.getCompanyDesc());
        putAwayHeader.setPlantDescription(description.getPlantDesc());
        putAwayHeader.setWarehouseDescription(description.getWarehouseDesc());

        PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
                grLine.getPreInboundNo(), grLine.getRefDocNumber());

        putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
        putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
        putAwayHeader.setBatchSerialNumber(grLine.getBatchSerialNumber());

        putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
        putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
        putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());

        putAwayHeader.setReferenceField5(grLine.getItemCode());
        putAwayHeader.setReferenceField3(grLine.getReferenceField7());
        putAwayHeader.setReferenceField7(grLine.getBarcodeId());
        putAwayHeader.setReferenceField8(grLine.getItemDescription());
        putAwayHeader.setReferenceField9(String.valueOf(grLine.getLineNo()));
        putAwayHeader.setReferenceField6(grLine.getReferenceField6());

        putAwayHeader.setStatusId(19L);
        statusDescription = stagingLineV2Repository.getStatusDescription(19L, grLine.getLanguageId());
        putAwayHeader.setStatusDescription(statusDescription);

        putAwayHeader.setDeletionIndicator(0L);
        putAwayHeader.setCreatedBy(loginUserID);
        putAwayHeader.setUpdatedBy(loginUserID);
        putAwayHeader.setCreatedOn(new Date());
        putAwayHeader.setUpdatedOn(new Date());
        putAwayHeader.setConfirmedOn(new Date());
        putAwayHeader.setQtyInCreate(allocated);
        putAwayHeader.setQtyInCase(grLine.getQtyInCase());
        putAwayHeader.setReferenceField8(grLine.getItemDescription());
//        putAwayHeader.setManufacturerDate(grLine.getManufacturerDate());
        putAwayHeader.setVehicleNo(grLine.getVehicleNo());

        putAwayHeader.setItemGroup(putAwayHeader.getItemGroup());

        putAwayHeader.setVehicleUnloadingDate(grLine.getVehicleUnloadingDate());
        putAwayHeader.setVehicleReportingDate(grLine.getVehicleReportingDate());
        putAwayHeader.setReceivingVariance(grLine.getReceivingVariance());
        if (grLine.getAcceptedQty() != null && grLine.getAcceptedQty() != 0) {
            putAwayHeader.setOrderQty(grLine.getAcceptedQty());
        } else {
            putAwayHeader.setOrderQty(grLine.getDamageQty());
        }

        Long NUMBER_RANGE_CODE = 6L;
        String packBarcodeId = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId);
        log.info("Generated PackBarcodeId ---------------> " + packBarcodeId);
        putAwayHeader.setPackBarcodes(packBarcodeId);
        putAwayHeaderV2Repository.save(putAwayHeader);

        log.info("Create Inventory BinClId = 3 -------------------------------> V9");
        grLineService.createInventoryNonCBMV9(putAwayHeader, uomQty);

        if (crossDock == 1L) {
            PutAwayLineV2 putAwayLineV2 = new PutAwayLineV2();
            BeanUtils.copyProperties(putAwayHeader, putAwayLineV2, CommonUtils.getNullPropertyNames(putAwayHeader));
            crossDockService.putAwayLineConfirmNonCBMV9(putAwayLineV2, loginUserID);
        }

    }

    //================================================BF======================================================

    /**
     * Get StorageBin For PutAwayHeader
     */
    public void setStorageBinForPutAwayHeaderV9(PutAwayHeaderV2 putAwayHeader, Long binClassId, String companyCode, String plantId, String languageId,
                                                String warehouseId, GrLineV2 createdGRLine, StorageBinPutAway storageBinPutAway) {

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        String routingDb = dbConfigRepository.getDbName(companyCode, plantId, warehouseId);
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb(routingDb);
        log.info("Current Routing Db " + routingDb);

        StorageBinV2 storageBin = null;
        log.info("BinClassId : " + binClassId);
        log.info("InboundOrderTypeId----->" + createdGRLine.getInboundOrderTypeId());

        String allocatedStatus = createdGRLine.getReferenceField1();

        log.info("Fully Or Partial Checking in Putaway Proposed Bin ---------> Status -- >" + allocatedStatus);
        Long levelId = null;
        if (allocatedStatus == null) {
            levelId = 1L;
        }

        if (putAwayHeader.getInboundOrderTypeId().equals(1L) || putAwayHeader.getInboundOrderTypeId().equals(4L)) {
            if (putAwayHeader.getProposedStorageBin() == null) {
//                storageBinPutAway.setStatusId(0L);
//                storageBinPutAway.setBinClassId(1L);
                storageBin = storageBinV2Repository.getStorageBinForEmptyV9(binClassId, companyCode, plantId, languageId, warehouseId, levelId);
                log.info("Get StorageBin ------> Values is {} ", storageBin);
                if (storageBin == null) {
                    storageBin = storageBinV2Repository.getStorageBinInPutAwayHeader(binClassId, companyCode, plantId, languageId, warehouseId);
                    log.info("Proposing Bin Without LevelId: {} ", storageBin);
                }
                if (storageBin != null) {
                    putAwayHeader.setProposedStorageBin(storageBin.getStorageBin());
                }
            }
        }
        if (putAwayHeader.getInboundOrderTypeId().equals(2L)) {
            if (putAwayHeader.getProposedStorageBin() == null) {
//                storageBinPutAway.setStatusId(0L);
//                storageBinPutAway.setBinClassId(7L);
                storageBin = storageBinV2Repository.getStorageBinForEmptyV9(binClassId, companyCode, plantId, languageId, warehouseId, levelId);
                log.info("Get StorageBin ------> Values is {} ", storageBin);
                if (storageBin != null) {
                    putAwayHeader.setProposedStorageBin(storageBin.getStorageBin());

                }
            }
        }
        if (putAwayHeader.getProposedStorageBin() == null) {
            binClassId = 2L;
            log.info("BinClassId : " + binClassId);
            storageBin = storageBinV2Repository.getStorageBinNonBinCls2V9(binClassId, companyCode, plantId, languageId, warehouseId);
            if (storageBin != null) {
                putAwayHeader.setProposedStorageBin(storageBin.getStorageBin());
                log.info("A --> NonCBM reserveBin: " + storageBin.getStorageBin());
            }
        }
        if (storageBin != null && binClassId == 1) {
            storageBinV2Repository.updateEmptyBinStatus(storageBin.getStorageBin(), companyCode, plantId, warehouseId, 1L);
        }

    }
}
