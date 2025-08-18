//package com.tekclover.wms.api.inbound.transaction.service;
//
//import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
//import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
//import com.tekclover.wms.api.inbound.transaction.model.dto.StorageBinV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.v2.GrLineV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound.v2.PreInboundHeaderV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
//import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
//import com.tekclover.wms.api.inbound.transaction.model.notification.NotificationSave;
//import com.tekclover.wms.api.inbound.transaction.repository.*;
//import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
//
//@Service
//@Slf4j
//public class InboundProcessingService extends BaseService{
//
//    @Autowired
//     PushNotificationService pushNotificationService;
//
//    @Autowired
//    public InboundLineService inboundLineService;
//
//    @Autowired
//    public StagingLineService stagingLineService;
//
//    @Autowired
//    public AuthTokenService authTokenService;
//
//    @Autowired
//    public MastersService mastersService;
//
//    @Autowired
//    public ImBasicData1Repository imbasicdata1Repository;
//
//    @Autowired
//    public PreInboundLineV2Repository preInboundLineV2Repository;
//
//    //----------------------------------------------------------------------------------------------------
//    @Autowired
//    public InboundLineV2Repository inboundLineV2Repository;
//
//    @Autowired
//    public PreInboundHeaderService preInboundHeaderService;
//
//    @Autowired
//    public GrHeaderV2Repository grHeaderV2Repository;
//
//    @Autowired
//    public GrLineV2Repository grLineV2Repository;
//
//    @Autowired
//    public InventoryV2Repository inventoryV2Repository;
//
//    @Autowired
//    public StagingLineV2Repository stagingLineV2Repository;
//
//    @Autowired
//    public PutAwayHeaderV2Repository putAwayHeaderV2Repository;
//
//    @Autowired
//    public PutAwayHeaderService putAwayHeaderService;
//
//    @Autowired
//    public PutAwayLineV2Repository putAwayLineV2Repository;
//
//    @Autowired
//     InventoryService inventoryService;
//
//    @Autowired
//    StorageBinService storageBinService;
//
//    String statusDescription = null;
//    @Autowired
//    PutAwayLineService putAwayLineService;
//
//    @Autowired
//    InboundQualityHeaderService inboundQualityHeaderService;
//    protected Long NUMBER_RANGE_CODE = 0L;
//    protected String numberRangeId = null;
//    protected IKeyValuePair description = null;
//
//    protected static final String MFR_NAME = "NAMRATHA";
//
//    /**
//     * @param company
//     * @param plant
//     * @param language
//     * @param warehouse
//     * @param item
//     * @param manufactureName
//     * @param preInbound
//     * @param refDocNo
//     * @param createdGRLines
//     * @param loginUserID
//     * @throws Exception
//     */
////    @Async("asyncExecutor")
//    public void createPutAwayHeaderNonCBMV4(String company, String plant, String language,
//                                            String warehouse, String item, String manufactureName,
//                                            String preInbound, String refDocNo,
//                                            List<GrLineV2> createdGRLines, String loginUserID) throws Exception {
//        try {
//
//            String idMasterToken = getIDMasterAuthToken();
//            //PA_NO
//            NUMBER_RANGE_CODE = 7L;
//            String nextPANumber = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
//
//            log.info("PA number ----------------> {}", nextPANumber);
//            fireBaseNotification(createdGRLines.get(0),nextPANumber, loginUserID);
//            for (GrLineV2 createdGRLine : createdGRLines) {
//
//                // Setting params
//                String languageId = createdGRLine.getLanguageId();
//                String companyCode = createdGRLine.getCompanyCode();
//                String plantId = createdGRLine.getPlantId();
//                String warehouseId = createdGRLine.getWarehouseId();
//                String itemCode = createdGRLine.getItemCode();
//                String manufacturerName = createdGRLine.getManufacturerName();
//                String preInboundNo = createdGRLine.getPreInboundNo();
//                String refDocNumber = createdGRLine.getRefDocNumber();
//
//
//                String proposedStorageBin = createdGRLine.getInterimStorageBin();
//                String alternateUom = createdGRLine.getAlternateUom();
//                Double bagSize = createdGRLine.getBagSize();
//
//                StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//                storageBinPutAway.setCompanyCodeId(companyCode);
//                storageBinPutAway.setPlantId(plantId);
//                storageBinPutAway.setLanguageId(languageId);
//                storageBinPutAway.setWarehouseId(warehouseId);
//
//                //  ASS_HE_NO
//                if (createdGRLine != null) {
//                    // Insert record into PutAwayHeader
//                    PutAwayHeaderV2 putAwayHeader = new PutAwayHeaderV2();
//                    BeanUtils.copyProperties(createdGRLine, putAwayHeader, CommonUtils.getNullPropertyNames(createdGRLine));
//                    putAwayHeader.setCompanyCodeId(companyCode);
//                    putAwayHeader.setPutAwayUom(createdGRLine.getOrderUom());
//                    putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//
//                    //Inbound Quality Number
//                    NUMBER_RANGE_CODE = 23L;
//                    String nextQualityNumber = getNextRangeNumber(NUMBER_RANGE_CODE, companyCode, plantId, languageId, warehouseId, idMasterToken);
//                    putAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number
//                    log.info("NewNumber Generated---> PutAway: " + nextPANumber + " ------> Quality: " + nextQualityNumber);
//
//                    //-----------------PROP_ST_BIN---------------------------------------------
//
//                    //V2 Code
//                    Long binClassId = 0L;                   //actual code follows
//                    if (createdGRLine.getInboundOrderTypeId() == null) {
//                        throw new BadRequestException("inbound order type id cannot be null");
//                    }
//                    if (createdGRLine.getInboundOrderTypeId() == 1 || createdGRLine.getInboundOrderTypeId() == 3 ||
//                            createdGRLine.getInboundOrderTypeId() == 4 || createdGRLine.getInboundOrderTypeId() == 5 ||
//                            createdGRLine.getInboundOrderTypeId() == 6 || createdGRLine.getInboundOrderTypeId() == 7) {
//                        binClassId = 1L;
//                    }
//                    if (createdGRLine.getInboundOrderTypeId() == 2) {
//                        binClassId = 7L;
//                    }
//                    log.info("BinClassId : " + binClassId);
//
////                String packBarCode = createdGRLine.getBatchSerialNumber() != null ? createdGRLine.getBatchSerialNumber() : "99999";
////                List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, createdGRLine.getBarcodeId(), createdGRLine.getBatchSerialNumber(), packBarCode, alternateUom, bagSize, binClassId);
//                    List<String> inventoryStorageBinList = inventoryService.getPutAwayHeaderCreateInventoryV4(companyCode, plantId, languageId, warehouseId, itemCode,
//                            manufacturerName, alternateUom, bagSize, binClassId);
//                    log.info("Inventory StorageBin List: " + inventoryStorageBinList.size() + " | ----> " + inventoryStorageBinList);
//
//                    if (createdGRLine.getInterimStorageBin() != null) {                         //Direct Stock Receipt - Fixed Bin - Inbound OrderTypeId - 5
//                        storageBinPutAway.setBinClassId(binClassId);
//                        storageBinPutAway.setBin(proposedStorageBin);
//                        StorageBinV2 storageBin = null;
//                        try {
//                            log.info("getStorageBin Input: " + storageBinPutAway);
//                            storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
//                        } catch (Exception e) {
//                            throw new BadRequestException("Invalid StorageBin");
//                        }
//                        log.info("InterimStorageBin: " + storageBin);
//                        putAwayHeader.setPutAwayQuantity(createdGRLine.getGoodReceiptQty());
//                        if (storageBin != null) {
//                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                            putAwayHeader.setLevelId(String.valueOf(storageBin.getFloorId()));
//                        }
//                        if (storageBin == null) {
//                            putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                        }
//                    }
//                    //BinClassId - 7 - Return Order(Sale Return)
//                    if (createdGRLine.getInboundOrderTypeId() == 2) {
//                        storageBinPutAway.setBinClassId(binClassId);
//                        log.info("BinClassId : " + binClassId);
//
//                        StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
//                        if (proposedBin != null) {
//                            putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
//                            putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
//                            log.info("Return Order --> Proposed Bin: " + proposedBin.getStorageBin());
//                        }
//                    }
//
//                    if (createdGRLine.getInterimStorageBin() == null && putAwayHeader.getProposedStorageBin() == null) {
//                        log.info("BinClassId : " + binClassId);
//                        if (inventoryStorageBinList != null && !inventoryStorageBinList.isEmpty()) {
//                            if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
//                                storageBinPutAway.setBinClassId(binClassId);
//                                storageBinPutAway.setStorageBin(inventoryStorageBinList);
//
//                                StorageBinV2 proposedExistingBin = storageBinService.getExistingProposedStorageBinNonCBM(storageBinPutAway);
//                                if (proposedExistingBin != null) {
//                                    proposedStorageBin = proposedExistingBin.getStorageBin();
//                                    log.info("Existing NON-CBM ProposedBin: " + proposedExistingBin);
//
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                    putAwayHeader.setLevelId(String.valueOf(proposedExistingBin.getFloorId()));
//                                }
//                                log.info("Existing NON-CBM ProposedBin, GrQty: " + proposedStorageBin + ", " + createdGRLine.getGoodReceiptQty());
//                            }
//                            if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
//                                storageBinPutAway.setBinClassId(7L);
//                                StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
//                                if (proposedBin != null) {
//                                    putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
//                                    putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
//                                    log.info("Damage Qty --> Proposed Bin: " + proposedBin.getStorageBin());
//                                }
//                            }
//                        }
//                    }
//
//                    //Last Picked Bin as Proposed Bin If it is empty
////                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
////
////                        PickupLineV2 pickupLineList = pickupLineService.getPickupLineForLastBinCheckV4(companyCode, plantId, languageId, warehouseId,
////                                itemCode, manufacturerName, alternateUom, bagSize);
////                        log.info("PickupLineForLastBinCheckV2: " + pickupLineList);
////                        if (pickupLineList != null) {
////                            putAwayHeader.setProposedStorageBin(pickupLineList.getPickedStorageBin());
////                            putAwayHeader.setLevelId(pickupLineList.getLevelId());
////                            log.info("LastPick NonCBM Bin: " + pickupLineList.getPickedStorageBin());
////                            log.info("LastPick NonCBM PutawayQty: " + createdGRLine.getGoodReceiptQty());
////                        }
////                    }
//
//                    //Propose Empty Bin if Last picked bin is unavailable
//                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//                    if (putAwayHeader.getProposedStorageBin() == null && (inventoryStorageBinList == null || inventoryStorageBinList.isEmpty())) {
//                        // If ST_BIN value is null
//                        // Validate if ACCEPT_QTY is not null and DAMAGE_QTY is NULL,
//                        // then pass WH_ID in STORAGEBIN table and fetch ST_BIN values for STATUS_ID=EMPTY.
//                        log.info("QuantityType : " + createdGRLine.getQuantityType());
//                        log.info("BinClassId : " + binClassId);
//
//                        storageBinPutAway.setStatusId(0L);
//                        StorageBinV2 proposedNonCbmStorageBin = null;
//
//                        if (createdGRLine.getQuantityType().equalsIgnoreCase("A")) {
//                            storageBinPutAway.setBinClassId(binClassId);
//
//                            //Checking confirmed bin in putAway line for that item
//                            PutAwayLineV2 existingBinPutAwayLineItemCheck = putAwayLineService.getPutAwayLineExistingItemCheckV4(companyCode, plantId, languageId, warehouseId,
//                                    itemCode, manufacturerName, alternateUom, bagSize);
//                            log.info("existingBinPutAwayLineItemCheck: " + existingBinPutAwayLineItemCheck);
//                            if (existingBinPutAwayLineItemCheck != null) {
//                                proposedStorageBin = existingBinPutAwayLineItemCheck.getConfirmedStorageBin();
//                                putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                if (existingBinPutAwayLineItemCheck.getLevelId() != null) {
//                                    putAwayHeader.setLevelId(String.valueOf(existingBinPutAwayLineItemCheck.getLevelId()));
//                                } else {
//                                    storageBinPutAway.setBin(proposedStorageBin);
//                                    StorageBinV2 getLevelIdForProposedBin = storageBinService.getaStorageBinV2(storageBinPutAway);
//                                    if (getLevelIdForProposedBin != null) {
//                                        putAwayHeader.setLevelId(String.valueOf(getLevelIdForProposedBin.getFloorId()));
//                                    }
//                                }
//                                log.info("Existing PutAwayCreate ProposedStorageBin from putAway line-->A : " + proposedStorageBin);
//                            }
//                            //Checking proposed bin in putAway header for that item
//                            if (putAwayHeader.getProposedStorageBin() == null) {
//                                PutAwayHeaderV2 existingBinItemCheck = putAwayHeaderService.getPutawayHeaderExistingBinItemCheckV4(companyCode, plantId, languageId, warehouseId,
//                                        itemCode, manufacturerName, alternateUom, bagSize);
//                                log.info("existingBinItemCheck: " + existingBinItemCheck);
//                                if (existingBinItemCheck != null) {
//                                    proposedStorageBin = existingBinItemCheck.getProposedStorageBin();
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                    putAwayHeader.setLevelId(String.valueOf(existingBinItemCheck.getLevelId()));
//                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
//                                }
//                            }
//                            List<String> existingBinCheck = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
//                            log.info("existingBinCheck: " + existingBinCheck);
//                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck != null && !existingBinCheck.isEmpty())) {
//                                storageBinPutAway.setStorageBin(existingBinCheck);
//                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
//                                if (proposedNonCbmStorageBin != null) {
//                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
//                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
//                                    log.info("Existing PutawayCreate ProposedStorageBin -->A : " + proposedStorageBin);
//                                }
//                            }
//                            if (putAwayHeader.getProposedStorageBin() == null && (existingBinCheck == null || existingBinCheck.isEmpty() || existingBinCheck.size() == 0)) {
//                                List<String> existingProposedPutawayStorageBin = putAwayHeaderService.getPutawayHeaderExistingBinCheckV4(companyCode, plantId, languageId, warehouseId);
//                                log.info("existingProposedPutawayStorageBin: " + existingProposedPutawayStorageBin);
//                                log.info("BinClassId: " + binClassId);
//                                storageBinPutAway.setStorageBin(existingProposedPutawayStorageBin);
//                                proposedNonCbmStorageBin = storageBinService.getProposedStorageBinNonCBM(storageBinPutAway);
//                                log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin);
//                                if (proposedNonCbmStorageBin != null) {
//                                    proposedStorageBin = proposedNonCbmStorageBin.getStorageBin();
//                                    log.info("proposedNonCbmStorageBin: " + proposedNonCbmStorageBin.getStorageBin());
//                                    putAwayHeader.setProposedStorageBin(proposedStorageBin);
//                                    putAwayHeader.setLevelId(String.valueOf(proposedNonCbmStorageBin.getFloorId()));
//                                }
//                                if (proposedNonCbmStorageBin == null) {
//                                    StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
//                                    log.info("A --> NonCBM reserveBin: " + stBin.getStorageBin());
//                                    putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                                    putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
//                                }
//                            }
//                        }
//
//                        /*
//                         * Validate if ACCEPT_QTY is null and DAMAGE_QTY is not NULL , then pass WH_ID in STORAGEBIN table and
//                         * fetch ST_BIN values for STATUS_ID=EMPTY.
//                         */
//                        if (createdGRLine.getQuantityType().equalsIgnoreCase("D")) {
//                            binClassId = 7L;
//                            storageBinPutAway.setBinClassId(binClassId);
//                            log.info("BinClassId : " + binClassId);
//                            StorageBinV2 proposedBin = storageBinService.getStorageBinByBinClassIdV4(storageBinPutAway);
//                            if (proposedBin != null) {
//                                putAwayHeader.setProposedStorageBin(proposedBin.getStorageBin());
//                                putAwayHeader.setLevelId(String.valueOf(proposedBin.getFloorId()));
//                                log.info("D --> Proposed Bin: " + proposedBin.getStorageBin());
//                            }
//                        }
//                    }
//                    if (putAwayHeader.getProposedStorageBin() == null) {
//                        StorageBinV2 stBin = getReserveBin(warehouseId, 2L, companyCode, plantId, languageId);
//                        log.info("Bin Unavailable --> Proposing reserveBin: " + stBin.getStorageBin());
//                        putAwayHeader.setProposedStorageBin(stBin.getStorageBin());
//                        putAwayHeader.setLevelId(String.valueOf(stBin.getFloorId()));
//                    }
//                    /////////////////////////////////////////////////////////////////////////////////////////////////////
//                    log.info("Proposed Storage Bin: " + putAwayHeader.getProposedStorageBin());
//                    log.info("Proposed Storage Bin level/Floor Id: " + putAwayHeader.getLevelId());
//                    PreInboundHeaderV2 dbPreInboundHeader = preInboundHeaderService.getPreInboundHeaderV2ForPutAwayCreate(companyCode, plantId, languageId, warehouseId,
//                            preInboundNo, refDocNumber);
//                    putAwayHeader.setMiddlewareId(dbPreInboundHeader.getMiddlewareId());
//                    putAwayHeader.setMiddlewareTable(dbPreInboundHeader.getMiddlewareTable());
//                    putAwayHeader.setReferenceDocumentType(dbPreInboundHeader.getReferenceDocumentType());
//                    putAwayHeader.setManufacturerFullName(dbPreInboundHeader.getManufacturerFullName());
//                    putAwayHeader.setTransferOrderDate(dbPreInboundHeader.getTransferOrderDate());
//                    putAwayHeader.setSourceBranchCode(dbPreInboundHeader.getSourceBranchCode());
//                    putAwayHeader.setSourceCompanyCode(dbPreInboundHeader.getSourceCompanyCode());
//                    putAwayHeader.setIsCompleted(dbPreInboundHeader.getIsCompleted());
//                    putAwayHeader.setIsCancelled(dbPreInboundHeader.getIsCancelled());
//                    putAwayHeader.setMUpdatedOn(dbPreInboundHeader.getMUpdatedOn());
//
//                    //PROP_HE_NO	<- PAWAY_HE_NO
//                    putAwayHeader.setProposedHandlingEquipment(createdGRLine.getPutAwayHandlingEquipment());
//                    putAwayHeader.setReferenceField5(itemCode);
//                    putAwayHeader.setReferenceField6(manufacturerName);
//                    putAwayHeader.setReferenceField7(createdGRLine.getBarcodeId());
//                    putAwayHeader.setReferenceField8(createdGRLine.getItemDescription());
//                    putAwayHeader.setReferenceField9(String.valueOf(createdGRLine.getLineNo()));
//
//                    Long statusId = 19L;
//                    putAwayHeader.setStatusId(statusId);
//                    statusDescription = stagingLineV2Repository.getStatusDescription(statusId, createdGRLine.getLanguageId());
//                    putAwayHeader.setStatusDescription(statusDescription);
//
//                    //PA_NO
//                    NUMBER_RANGE_CODE = 6L;
//                    String packBarcode = getNextRangeNumber(NUMBER_RANGE_CODE, company, plant, language, warehouse, idMasterToken);
//                    putAwayHeader.setDeletionIndicator(0L);
//                    putAwayHeader.setPackBarcodes(packBarcode);
//                    putAwayHeader.setCreatedBy(loginUserID);
//                    putAwayHeader.setUpdatedBy(loginUserID);
//                    putAwayHeader.setCreatedOn(new Date());
//                    putAwayHeader.setUpdatedOn(new Date());
//                    putAwayHeader.setConfirmedOn(new Date());
//                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
//                    log.info("putAwayHeader : " + putAwayHeader);
//
//                    // Updating Grline field -------------> PutAwayNumber
//                    log.info("Updation of PutAwayNumber on GrLine Started");
//                    putAwayHeaderV2Repository.updatePutAwayNumber(putAwayHeader.getCompanyCodeId(), putAwayHeader.getPlantId(),
//                            putAwayHeader.getLanguageId(), putAwayHeader.getWarehouseId(), putAwayHeader.getRefDocNumber(),
//                            putAwayHeader.getPreInboundNo(), createdGRLine.getItemCode(), createdGRLine.getLineNo(), createdGRLine.getCreatedOn(),
//                            putAwayHeader.getPutAwayNumber());
//
//                    log.info("Updation of PutAwayNumber on GrLine Completed");
//
//                    /*----------------Inventory tables Create---------------------------------------------*/
//                    inventoryService.createInventoryNonCBMV4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, refDocNumber, createdGRLine);
//
//                    //bypass quality header and line
//                    inboundQualityHeaderService.createInboundQualityHeaderV4(createdGRLine, statusId, statusDescription, nextQualityNumber);
//                }
//            }
//        } catch (Exception e) {
//            log.error("Exception while creating Putaway Header----> " + e.toString());
//            throw e;
//        }
//    }
//
//    /**
//     * @param grLineV2
//     */
//    private void fireBaseNotification(GrLineV2 grLineV2, String putAwayNumber, String loginUserID) {
//        try {
////            try {
////                DataBaseContextHolder.setCurrentDb("MT");
////                String profile = dbConfigRepository.getDbName(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getWarehouseId());
////                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
////                DataBaseContextHolder.clear();
////                DataBaseContextHolder.setCurrentDb(profile);
//
//            log.info("Notification Input ----> | " + grLineV2.getCompanyCode() + " | " + grLineV2.getPlantId() + " | " + grLineV2.getLanguageId() + " | " + grLineV2.getWarehouseId());
//            List<String> deviceToken = grLineV2Repository.getDeviceToken(grLineV2.getCompanyCode(), grLineV2.getPlantId(), grLineV2.getLanguageId(), grLineV2.getWarehouseId(), loginUserID);
//            log.info("deviceToken ------> {}", deviceToken);
//            if (deviceToken != null && !deviceToken.isEmpty()) {
//                String title = "Inbound Create";
//                String message = "Putaway Header Created Sucessfully - " + putAwayNumber;
//
//                NotificationSave notificationInput = new NotificationSave();
//                notificationInput.setUserId(Collections.singletonList(loginUserID));
//                notificationInput.setUserType(null);
//                notificationInput.setMessage(message);
//                notificationInput.setTopic(title);
//                notificationInput.setReferenceNumber(grLineV2.getRefDocNumber());
//                notificationInput.setDocumentNumber(grLineV2.getPreInboundNo());
//                notificationInput.setCompanyCodeId(grLineV2.getCompanyCode());
//                notificationInput.setPlantId(grLineV2.getPlantId());
//                notificationInput.setLanguageId(grLineV2.getLanguageId());
//                notificationInput.setWarehouseId(grLineV2.getWarehouseId());
//                notificationInput.setCreatedOn(grLineV2.getCreatedOn());
//                notificationInput.setCreatedBy(loginUserID);
//
//                log.info("pushNotification started");
//                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
//                log.info("pushNotification completed");
//            }
////            } finally {
////                DataBaseContextHolder.clear();
////            }
//        } catch (Exception e) {
//            log.error("Inbound firebase notification error", e); // This logs the full stack trace
//        }
//    }
//
//    /**
//     * @param warehouseId
//     * @param binClassId
//     * @param companyCode
//     * @param plantId
//     * @param languageId
//     * @return
//     */
//    private StorageBinV2 getReserveBin(String warehouseId, Long binClassId, String companyCode, String plantId, String languageId) {
//        log.info("BinClassId : " + binClassId);
//        return storageBinService.getStorageBinByBinClassIdV2(warehouseId, binClassId, companyCode, plantId, languageId);
//    }
//}
