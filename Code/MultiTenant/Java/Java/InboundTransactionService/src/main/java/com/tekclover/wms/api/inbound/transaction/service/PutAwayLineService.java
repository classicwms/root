package com.tekclover.wms.api.inbound.transaction.service;

import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.inbound.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.inbound.transaction.model.dto.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.inbound.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.*;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.putaway.v2.SearchPutAwayLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.inbound.transaction.model.inbound.v2.PutAwayLineConfirm;
import com.tekclover.wms.api.inbound.transaction.model.notification.NotificationSave;
import com.tekclover.wms.api.inbound.transaction.repository.*;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PutAwayLineSpecification;
import com.tekclover.wms.api.inbound.transaction.repository.specification.PutAwayLineV2Specification;
import com.tekclover.wms.api.inbound.transaction.util.CommonUtils;
import com.tekclover.wms.api.inbound.transaction.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PutAwayLineService extends BaseService {

    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;


    @Autowired
    private StorageBinV2Repository storageBinV2Repository;

    @Autowired
    private PalletIdAssignmentService palletIdAssignmentService;

    @Autowired
    private PalletIdAssignmentRepository palletIdAssignmentRepository;

    @Autowired
    private PutAwayLineRepository putAwayLineRepository;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

    @Autowired
    private StorageBinService storageBinService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InboundLineRepository inboundLineRepository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;
    //--------------------------------------------------------------------------------------------
    @Autowired
    private InboundLineV2Repository inboundLineV2Repository;

    @Autowired
    private InventoryV2Repository inventoryV2Repository;

    @Autowired
    private PutAwayHeaderV2Repository putAwayHeaderV2Repository;

    @Autowired
    private PutAwayLineV2Repository putAwayLineV2Repository;

    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private StagingLineService stagingLineService;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Autowired
    private GrLineService grLineService;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    InventoryAsyncProcessService inventoryAsyncProcessService;


    String statusDescription = null;
    boolean alreadyExecuted = true;

    //--------------------------------------------------------------------------------------------

    /**
     * getPutAwayLines
     *
     * @return
     */
    public List<PutAwayLine> getPutAwayLines() {
        List<PutAwayLine> putAwayLineList = putAwayLineRepository.findAll();
        putAwayLineList = putAwayLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return putAwayLineList;
    }

    /**
     * WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE
     *
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */
    public List<PutAwayLine> getPutAwayLine(String warehouseId, String preInboundNo, String refDocNumber, Long lineNo, String itemCode) {
        List<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values in PutAwayLine: warehouseId:" + warehouseId +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    ",lineNo: " + lineNo +
                    " doesn't exist.");
        }

        return putAwayLine;
    }

    /**
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<PutAwayLine> getPutAwayLine2(String warehouseId, String preInboundNo, String refDocNumber) {
        List<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values in PutAwayLine: warehouseId:" + warehouseId +
                    ",preInboundNo: " + preInboundNo +
                    ",refDocNumber: " + refDocNumber +
                    " doesn't exist.");
        }

        return putAwayLine;
    }

    /**
     * getPutAwayLineByStatusId
     *
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutAwayLineByStatusId(String warehouseId, String preInboundNo, String refDocNumber) {
        long putAwayLineStatusIdCount = putAwayLineRepository.getPutawayLineCountByStatusId(getCompanyCode(), getPlantId(), warehouseId, preInboundNo, refDocNumber);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param warehouseId
     * @param putAwayNumber
     * @param refDocNumber
     * @param statusId
     * @return
     */
    public long getPutAwayLineByStatusId(String warehouseId, String putAwayNumber, String refDocNumber, Long statusId) {
        long putAwayLineStatusIdCount =
                putAwayLineRepository.getPutawayLineCountByStatusId(getCompanyCode(), getPlantId(), warehouseId, putAwayNumber, refDocNumber, statusId);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param putAwayNumber
     * @return
     */
    public List<PutAwayLine> getPutAwayLine(String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }

        return putAwayLine;
    }

    /**
     * getPutAwayLine
     *
     * @param confirmedStorageBin
     * @return
     */
    public PutAwayLine getPutAwayLine(String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber,
                                      String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin, List<String> confirmedStorageBin) {
        Optional<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        warehouseId,
                        goodsReceiptNo,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        lineNo,
                        itemCode,
                        proposedStorageBin,
                        confirmedStorageBin,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",goodsReceiptNo: " + goodsReceiptNo + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",putAwayNumber: " + putAwayNumber + "," +
                    ",putAwayNumber: " + putAwayNumber + "," +
                    ",lineNo: " + lineNo + "," +
                    ",itemCode: " + itemCode + "," +
                    ",lineNo: " + lineNo + "," +
                    ",proposedStorageBin: " + proposedStorageBin +
                    ",confirmedStorageBin: " + confirmedStorageBin +
                    " doesn't exist.");
        }

        return putAwayLine.get();
    }

    /**
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<PutAwayLine> getPutAwayLine(String refDocNumber, String packBarcodes) {
        List<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        packBarcodes,
                        0L
                );
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist.");
        }
        return putAwayLine;
    }

    /**
     * @param refDocNumber
     * @return
     */
    public List<PutAwayLine> getPutAwayLine(String refDocNumber) {
        List<PutAwayLine> putAwayLine =
                putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
                        getLanguageId(),
                        getCompanyCode(),
                        getPlantId(),
                        refDocNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    "refDocNumber: " + refDocNumber +
                    " doesn't exist.");
        }
        return putAwayLine;
    }

    /**
     * @param searchPutAwayLine
     * @return
     * @throws Exception
     */
    public List<PutAwayLine> findPutAwayLine(SearchPutAwayLine searchPutAwayLine) throws Exception {

        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayLine.getFromConfirmedDate(), searchPutAwayLine.getToConfirmedDate());
            searchPutAwayLine.setFromConfirmedDate(dates[0]);
            searchPutAwayLine.setToConfirmedDate(dates[1]);
        }

        PutAwayLineSpecification spec = new PutAwayLineSpecification(searchPutAwayLine);
        List<PutAwayLine> results = putAwayLineRepository.findAll(spec);
        return results;
    }

    /**
     * createPutAwayLine
     *
     * @param newPutAwayLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayLine createPutAwayLine(AddPutAwayLine newPutAwayLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLine dbPutAwayLine = new PutAwayLine();
        log.info("newPutAwayLine : " + newPutAwayLine);
        BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
        dbPutAwayLine.setDeletionIndicator(0L);
        dbPutAwayLine.setCreatedBy(loginUserID);
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setCreatedOn(new Date());
        dbPutAwayLine.setUpdatedOn(new Date());
        return putAwayLineRepository.save(dbPutAwayLine);
    }

    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @param loginUserID
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public List<PutAwayLine> putAwayLineConfirm(@Valid List<AddPutAwayLine> newPutAwayLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PutAwayLine> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);
        try {
            for (AddPutAwayLine newPutAwayLine : newPutAwayLines) {
                PutAwayLine dbPutAwayLine = new PutAwayLine();
                Warehouse warehouse = getWarehouse(newPutAwayLine.getWarehouseId());

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
                if (newPutAwayLine.getCompanyCode() == null) {
                    dbPutAwayLine.setCompanyCode(warehouse.getCompanyCode());
                } else {
                    dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());
                }
                dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                dbPutAwayLine.setStatusId(20L);
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setCreatedOn(new Date());
                dbPutAwayLine.setUpdatedOn(new Date());

                Optional<PutAwayLine> existingPutAwayLine = putAwayLineRepository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        getLanguageId(), getCompanyCode(), getPlantId(), newPutAwayLine.getWarehouseId(), newPutAwayLine.getGoodsReceiptNo(),
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(),
                        newPutAwayLine.getItemCode(), newPutAwayLine.getProposedStorageBin(), Arrays.asList(newPutAwayLine.getConfirmedStorageBin()),
                        newPutAwayLine.getDeletionIndicator());
                log.info("Existing putawayline already created : " + existingPutAwayLine);
                if (existingPutAwayLine.isEmpty()) {
                    PutAwayLine createdPutAwayLine = putAwayLineRepository.save(dbPutAwayLine);
                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);
                    createdPutAwayLines.add(createdPutAwayLine);
                    boolean isInventoryCreated = false;
                    boolean isInventoryMovemoentCreated = false;
                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        Inventory inventory = new Inventory();
                        BeanUtils.copyProperties(createdPutAwayLine, inventory, CommonUtils.getNullPropertyNames(createdPutAwayLine));
                        inventory.setCompanyCodeId(createdPutAwayLine.getCompanyCode());
                        inventory.setVariantCode(1L);                // VAR_ID
                        inventory.setVariantSubCode("1");            // VAR_SUB_ID
                        inventory.setStorageMethod("1");            // STR_MTD
                        inventory.setBatchSerialNumber("1");        // STR_NO
                        inventory.setBatchSerialNumber(newPutAwayLine.getBatchSerialNumber());
                        inventory.setStorageBin(createdPutAwayLine.getConfirmedStorageBin());

                        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                        StorageBin dbStorageBin =
                                mastersService.getStorageBin(dbPutAwayLine.getConfirmedStorageBin(),
                                        dbPutAwayLine.getWarehouseId(),
                                        authTokenForMastersService.getAccess_token());
                        inventory.setBinClassId(dbStorageBin.getBinClassId());

                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
                            inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
                            inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
                        }

                        if (dbStorageBin != null) {
                            inventory.setReferenceField10(dbStorageBin.getStorageSectionId());
                            inventory.setReferenceField5(dbStorageBin.getAisleNumber());
                            inventory.setReferenceField6(dbStorageBin.getShelfId());
                            inventory.setReferenceField7(dbStorageBin.getRowId());
                        }

                        /*
                         * Insert PA_CNF_QTY value in this field.
                         * Also Pass WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table and fetch ST_BIN/INV_QTY value.
                         * Update INV_QTY value by (INV_QTY - PA_CNF_QTY) . If this value becomes Zero, then delete the record"
                         */
                        try {
                            Inventory existinginventory = inventoryService.getInventory(createdPutAwayLine.getWarehouseId(),
                                    createdPutAwayLine.getPackBarcodes(), dbPutAwayLine.getItemCode(), 3L);
                            double INV_QTY = existinginventory.getInventoryQuantity() - createdPutAwayLine.getPutawayConfirmedQty();
                            log.info("INV_QTY : " + INV_QTY);
                            if (INV_QTY >= 0) {
                                existinginventory.setInventoryQuantity(INV_QTY);
                                Inventory updatedInventory = inventoryRepository.save(existinginventory);
                                log.info("updatedInventory--------> : " + updatedInventory);
                            }
                        } catch (Exception e) {
                            log.info("Existing Inventory---Error-----> : " + e.toString());
                        }

                        // INV_QTY
                        inventory.setInventoryQuantity(createdPutAwayLine.getPutawayConfirmedQty());

                        // INV_UOM
                        inventory.setInventoryUom(createdPutAwayLine.getPutAwayUom());
                        inventory.setCreatedBy(createdPutAwayLine.getCreatedBy());
                        inventory.setCreatedOn(createdPutAwayLine.getCreatedOn());
                        Inventory createdInventory = inventoryRepository.save(inventory);
                        log.info("createdInventory : " + createdInventory);
                        if (createdInventory != null) {
                            isInventoryCreated = true;
                        }

                        /* Insert a record into INVENTORYMOVEMENT table */
                        InventoryMovement createdInventoryMovement = createInventoryMovement(createdPutAwayLine);
                        log.info("inventoryMovement created: " + createdInventoryMovement);
                        if (createdInventoryMovement != null) {
                            isInventoryMovemoentCreated = true;
                        }

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBin(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin, loginUserID, authTokenForMastersService.getAccess_token());

                        if (isInventoryCreated && isInventoryMovemoentCreated) {
                            List<PutAwayHeader> headers = putAwayHeaderService.getPutAwayHeader(createdPutAwayLine.getWarehouseId(),
                                    createdPutAwayLine.getPreInboundNo(), createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPutAwayNumber());
                            for (PutAwayHeader putAwayHeader : headers) {
                                putAwayHeader.setStatusId(20L);
                                putAwayHeader = putAwayHeaderRepository.save(putAwayHeader);
                                log.info("putAwayHeader updated: " + putAwayHeader);
                            }

                            /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                            // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and
                            // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                            updateInboundLine(createdPutAwayLine);
//							double addedAcceptQty = 0.0;
//							double addedDamageQty = 0.0;
//							
//							InboundLine inboundLine = inboundLineService.getInboundLine(createdPutAwayLine.getWarehouseId(), 
//									createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(), createdPutAwayLine.getLineNo(), 
//									createdPutAwayLine.getItemCode());
//							log.info("inboundLine----from--DB---------> " + inboundLine);
//							
//							// If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
//							if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
//								if (inboundLine.getAcceptedQty() != null) {
//									addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
//								} else {
//									addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
//								}
//								
//								inboundLine.setAcceptedQty(addedAcceptQty);
//							}
//							
//							// if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
//							if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
//								if (inboundLine.getDamageQty() != null) {
//									addedDamageQty = inboundLine.getDamageQty() + createdPutAwayLine.getPutawayConfirmedQty();
//								} else {
//									addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
//								}
//								
//								inboundLine.setDamageQty(addedDamageQty);
//							}
//							
//							inboundLine.setStatusId(20L);
//							inboundLine = inboundLineRepository.save(inboundLine);
//							log.info("inboundLine updated : " + inboundLine);
                        }
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param createdPutAwayLine
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    private void updateInboundLine(PutAwayLine createdPutAwayLine) {
        double addedAcceptQty = 0.0;
        double addedDamageQty = 0.0;

        InboundLine inboundLine = inboundLineService.getInboundLine(createdPutAwayLine.getWarehouseId(),
                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(), createdPutAwayLine.getLineNo(),
                createdPutAwayLine.getItemCode());
        log.info("inboundLine----from--DB---------> " + inboundLine);

        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
            if (inboundLine.getAcceptedQty() != null) {
                addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
            } else {
                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
            }

            inboundLine.setAcceptedQty(addedAcceptQty);
        }

        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
            if (inboundLine.getDamageQty() != null) {
                addedDamageQty = inboundLine.getDamageQty() + createdPutAwayLine.getPutawayConfirmedQty();
            } else {
                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
            }

            inboundLine.setDamageQty(addedDamageQty);
        }

        inboundLine.setStatusId(20L);
        inboundLine = inboundLineRepository.save(inboundLine);
        log.info("inboundLine updated : " + inboundLine);
    }

    /**
     * @param dbPutAwayLine
     * @return
     */
    private InventoryMovement createInventoryMovement(PutAwayLine dbPutAwayLine) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(dbPutAwayLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPutAwayLine));
        inventoryMovement.setCompanyCodeId(dbPutAwayLine.getCompanyCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(2L);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // CASE_CODE
        inventoryMovement.setCaseCode("999999");

        // PAL_CODE
        inventoryMovement.setPalletCode("999999");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(dbPutAwayLine.getRefDocNumber());

        // ST_BIN
        inventoryMovement.setStorageBin(dbPutAwayLine.getConfirmedStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbPutAwayLine.getPutawayConfirmedQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("P");

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbPutAwayLine.getPutAwayUom());

        /*
         * -----THE BELOW IS NOT USED-------------
         * Pass WH_ID/ITM_CODE/PACK_BARCODE/BIN_CL_ID is equal to 1 in INVENTORY table and fetch INV_QTY
         * BAL_OH_QTY = INV_QTY
         */
        // PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
        List<Inventory> inventoryList =
                inventoryService.getInventory(dbPutAwayLine.getWarehouseId(), dbPutAwayLine.getItemCode(), 1L);
        double sumOfInvQty = inventoryList.stream().mapToDouble(a -> a.getInventoryQuantity()).sum();
        log.info("BalanceOhQty: " + sumOfInvQty);
        inventoryMovement.setBalanceOHQty(sumOfInvQty);

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(dbPutAwayLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbPutAwayLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        return inventoryMovement;
    }

    /**
     * updatePutAwayLine
     *
     * @param loginUserID
     * @param confirmedStorageBin
     * @param updatePutAwayLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayLine updatePutAwayLine(String warehouseId, String goodsReceiptNo, String preInboundNo, String refDocNumber,
                                         String putAwayNumber, Long lineNo, String itemCode, String proposedStorageBin, String confirmedStorageBin,
                                         String loginUserID, UpdatePutAwayLine updatePutAwayLine)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLine dbPutAwayLine = getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
                lineNo, itemCode, proposedStorageBin, Arrays.asList(confirmedStorageBin));
        BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setUpdatedOn(new Date());
        return putAwayLineRepository.save(dbPutAwayLine);
    }

    /**
     * @param updatePutAwayLine
     * @param loginUserID
     * @return
     */
    public PutAwayLine updatePutAwayLine(UpdatePutAwayLine updatePutAwayLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLine dbPutAwayLine = new PutAwayLine();
        BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setUpdatedOn(new Date());
        return putAwayLineRepository.save(dbPutAwayLine);
    }

    /**
     * @param asnNumber
     */
    public void updateASN(String asnNumber) {
        List<PutAwayLine> putAwayLines = getPutAwayLines();
        putAwayLines.forEach(p -> p.setReferenceField1(asnNumber));
        putAwayLineRepository.saveAll(putAwayLines);
    }

    /**
     * deletePutAwayLine
     *
     * @param loginUserID
     * @param confirmedStorageBin
     */
    public void deletePutAwayLine(String languageId, String companyCodeId, String plantId, String warehouseId,
                                  String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo,
                                  String itemCode, String proposedStorageBin, String confirmedStorageBin, String loginUserID) {
        PutAwayLine putAwayLine = getPutAwayLine(warehouseId, goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
                lineNo, itemCode, proposedStorageBin, Arrays.asList(confirmedStorageBin));
        if (putAwayLine != null) {
            putAwayLine.setDeletionIndicator(1L);
            putAwayLine.setUpdatedBy(loginUserID);
            putAwayLineRepository.save(putAwayLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + confirmedStorageBin);
        }
    }

    //=====================================================================V2====================================================

    /**
     * @param companyId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public long getPutAwayLineByStatusIdV2(String companyId, String plantId, String languageId, String warehouseId,
                                           String preInboundNo, String refDocNumber) {
        long putAwayLineStatusIdCount = putAwayLineV2Repository.getPutawayLineCountByStatusId(companyId, plantId, warehouseId, languageId, preInboundNo, refDocNumber);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param companyId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public double getSumOfPutawayLineQtyByStatusId20(String companyId, String plantId, String languageId, String warehouseId,
                                                     String preInboundNo, String refDocNumber) {
        double sumOfPutAwayLineQty =
                putAwayLineV2Repository.getSumOfPutawayLineQtyByStatusId20(companyId, plantId, warehouseId, languageId, preInboundNo, refDocNumber);
        return sumOfPutAwayLineQty;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param putAwayNumber
     * @param refDocNumber
     * @param statusId
     * @return
     */
    public long getPutAwayLineByStatusIdV2(String companyCode, String plantId, String languageId,
                                           String warehouseId, String putAwayNumber, String refDocNumber, Long statusId) {
        long putAwayLineStatusIdCount =
                putAwayLineV2Repository.getPutawayLineCountByStatusId(companyCode, plantId, warehouseId, languageId, putAwayNumber, refDocNumber, statusId);
        return putAwayLineStatusIdCount;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @param lineNo
     * @param itemCode
     * @return
     */

    public List<PutAwayLineV2> getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String preInboundNo, String refDocNumber,
                                                Long lineNo, String itemCode) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndPreInboundNoAndRefDocNumberAndLineNoAndItemCodeAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        preInboundNo,
                        refDocNumber,
                        lineNo,
                        itemCode,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values in PutAwayLine: warehouseId:" + warehouseId +
                    ",preInboundNo: " + preInboundNo +
                    ",lineNo: " + lineNo +
                    ",itemCode: " + itemCode +
                    ",lineNo: " + lineNo +
                    " doesn't exist.");
        }

        return putAwayLine;
    }

    public PutAwayLineV2 getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                          String warehouseId, String goodsReceiptNo, String preInboundNo,
                                          String refDocNumber, String putAwayNumber, Long lineNo,
                                          String itemCode, String proposedStorageBin, List<String> confirmedStorageBin) {
        Optional<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        goodsReceiptNo,
                        preInboundNo,
                        refDocNumber,
                        putAwayNumber,
                        lineNo,
                        itemCode,
                        proposedStorageBin,
                        confirmedStorageBin,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",goodsReceiptNo: " + goodsReceiptNo + "," +
                    ",preInboundNo: " + preInboundNo + "," +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",putAwayNumber: " + putAwayNumber + "," +
                    ",putAwayNumber: " + putAwayNumber + "," +
                    ",lineNo: " + lineNo + "," +
                    ",itemCode: " + itemCode + "," +
                    ",lineNo: " + lineNo + "," +
                    ",proposedStorageBin: " + proposedStorageBin +
                    ",confirmedStorageBin: " + confirmedStorageBin +
                    " doesn't exist.");
        }

        return putAwayLine.get();
    }

    /**
     * @param warehouseId
     * @param refDocNumber
     * @param putAwayNumber
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                                String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: warehouseId:" + warehouseId +
                    ",refDocNumber: " + refDocNumber +
                    ",putAwayNumber: " + putAwayNumber +
                    " doesn't exist.");
        }

        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param putAwayNumber
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineV2ForReversal(String companyCode, String plantId, String languageId,
                                                           String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForPerpetualCountV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, String storageBin, Date stockCountDate) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndProposedStorageBinAndStatusIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        itemCode,
                        manufacturerName,
                        storageBin,
                        20L,
                        0L);
        if (putAwayLine == null || putAwayLine.isEmpty()) {
            return null;
        }

        return putAwayLine;
    }

    public List<PutAwayLineV2> getPutAwayLineV2ForPutawayConfirm(String companyCode, String plantId, String languageId,
                                                                 String warehouseId, String refDocNumber, String putAwayNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPutAwayNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        putAwayNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            return null;
        }

        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param refDocNumber
     * @param packBarcodes
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineV2(String companyCode, String plantId, String languageId,
                                                String refDocNumber, String packBarcodes) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        packBarcodes,
                        0L
                );
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    ",refDocNumber: " + refDocNumber + "," +
                    ",packBarcodes: " + packBarcodes + "," +
                    " doesn't exist.");
        }
        return putAwayLine;
    }

    public List<PutAwayLineV2> getPutAwayLineV2(String companyCode, String plantId,
                                                String languageId, String refDocNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndRefDocNumberAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        refDocNumber,
                        0L);
        if (putAwayLine.isEmpty()) {
            throw new BadRequestException("The given values: " +
                    "refDocNumber: " + refDocNumber +
                    " doesn't exist.");
        }
        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndStatusIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        20L,
                        0L);
        if (putAwayLine.isEmpty()) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
//    public PutAwayLineV2 getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
//                                                           String refDocNumber, String itemCode, String manufacturerName,
//                                                           Long lineNumber, String preInboundNo, String packBarcodes) {
//        PutAwayLineV2 putAwayLine =
//                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
//                        languageId,
//                        companyCode,
//                        plantId,
//                        warehouseId,
//                        refDocNumber,
//                        preInboundNo,
//                        itemCode,
//                        manufacturerName,
//                        lineNumber,
//                        20L,
//                        packBarcodes,
//                        0L);
//        if (putAwayLine == null) {
//            return null;
//        }
//        return putAwayLine;
//    }
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String refDocNumber, String itemCode, String manufacturerName,
                                                                 Long lineNumber, String preInboundNo, String packBarcodes) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndPackBarcodesAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        20L,
                        packBarcodes,
                        0L);
        if (putAwayLine == null) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV2(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String refDocNumber, String itemCode, String manufacturerName,
                                                                 Long lineNumber, String preInboundNo) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
                        companyCode,
                        languageId,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        20L,
                        0L);
        if (putAwayLine == null) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @param lineNumber
     * @param preInboundNo
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForInboundConfirmV8(String companyCode, String plantId, String languageId, String warehouseId,
                                                                 String refDocNumber, String itemCode, String manufacturerName,
                                                                 Long lineNumber, String preInboundNo) {
        Long[] statusIds = new Long[]{
                20L, 24L
        };
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdInAndDeletionIndicator(
                        companyCode,
                        languageId,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        itemCode,
                        manufacturerName,
                        lineNumber,
                        statusIds,
                        0L);
        if (putAwayLine == null) {
            return null;
        }
        return putAwayLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param palletId
     * @param loginUserID
     */
    public void updatePalletIdAssignment(String companyCodeId, String plantId, String languageId, String warehouseId, String palletId, String loginUserID) throws Exception {
        try {
            palletIdAssignmentService.updatePalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, palletId, loginUserID);
        } catch (Exception e) {
            log.error("");
            throw e;
        }
    }

    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV5(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID) throws Exception {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        Date assignedOn = null;
        List<FetchImpl> fbNotificationList = new ArrayList<>();
        List<String> storageBinList = new ArrayList<>();
        List<String> palletIdList = new ArrayList<>();

        List<PutAwayLineConfirm> refDocNumbers = new ArrayList<>();

        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {
                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }

                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();

                PutAwayLineConfirm putAwayLineConfirm = new PutAwayLineConfirm();
                putAwayLineConfirm.setRefDocNumber(refDocNumber);
                putAwayLineConfirm.setPreInboundNo(preInboundNo);
                String finalRefDocNumber = refDocNumber;
                log.info("FinalRefDocNumber" + finalRefDocNumber);
                boolean isDuplicate = refDocNumbers.stream().anyMatch(n -> n.getRefDocNumber() != null && n.getRefDocNumber().equalsIgnoreCase(finalRefDocNumber));
                log.info("isDuplicate: " + isDuplicate);
                if (!isDuplicate) {
                    refDocNumbers.add(putAwayLineConfirm);
                }

                StorageBinV2 dbStorageBin = null;
                try {
                    DataBaseContextHolder.setCurrentDb("REEFERON");
                    log.info("Inputs----->" + companyCode);
                    log.info("Inputs----->" + plantId);
                    log.info("Inputs----->" + warehouseId);
                    log.info("Inputs----->" + newPutAwayLine.getConfirmedStorageBin());
                    if (newPutAwayLine.getInboundOrderTypeId() == 11L) {
                        dbStorageBin = storageBinService.getStorageBinEmptyCrate(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                    }
                    if (newPutAwayLine.getInboundOrderTypeId() == 1L) {
                        dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                    }
                    if (newPutAwayLine.getInboundOrderTypeId() == 3L) {
                        dbStorageBin = storageBinService.getStorageBin5(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                    }


                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
                }

                PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.getPutAwayHeaderV5(companyCode, plantId, warehouseId, languageId, newPutAwayLine.getPutAwayNumber(),
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getBarcodeId(), String.valueOf(newPutAwayLine.getLineNo()));
                log.info("putawayHeader: " + putAwayHeader);

                if (dbStorageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                }

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                        newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
//                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                    newPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                    newPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                    newPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                    newPutAwayLine.setSize(dbStagingLineEntity.getSize());
                    newPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
                }

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

                dbPutAwayLine.setStatusId(20L);
                statusDescription = getStatusDescription(20L, languageId);
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                if (putAwayHeader != null) {
                    dbPutAwayLine.setBatchSerialNumber(putAwayHeader.getBatchSerialNumber());
                    dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(putAwayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
                    dbPutAwayLine.setStorageSectionId(putAwayHeader.getStorageSectionId());
                    if (dbPutAwayLine.getLineNo() == null) {
                        dbPutAwayLine.setLineNo(Long.valueOf(putAwayHeader.getReferenceField9()));
                    }

                    dbPutAwayLine.setMaterialNo(putAwayHeader.getMaterialNo());
                    dbPutAwayLine.setPriceSegment(putAwayHeader.getPriceSegment());
                    dbPutAwayLine.setArticleNo(putAwayHeader.getArticleNo());
                    dbPutAwayLine.setGender(putAwayHeader.getGender());
                    dbPutAwayLine.setColor(putAwayHeader.getColor());
                    dbPutAwayLine.setSize(putAwayHeader.getSize());
                    dbPutAwayLine.setNoPairs(putAwayHeader.getNoPairs());
                    dbPutAwayLine.setReferenceField6(putAwayHeader.getReferenceField6());

                    if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                        dbPutAwayLine.setParentProductionOrderNo(putAwayHeader.getParentProductionOrderNo());
                    }

                    if (newPutAwayLine.getManufacturerDate() == null) {
                        dbPutAwayLine.setManufacturerDate(putAwayHeader.getManufacturerDate());
                    }
                    if (newPutAwayLine.getExpiryDate() == null) {
                        dbPutAwayLine.setExpiryDate(putAwayHeader.getExpiryDate());
                    }

                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());
                dbPutAwayLine.setVehicleNo(putAwayHeader.getVehicleNo());
                dbPutAwayLine.setVehicleUnloadingDate(putAwayHeader.getVehicleUnloadingDate());
                dbPutAwayLine.setVehicleReportingDate(putAwayHeader.getVehicleReportingDate());
                dbPutAwayLine.setReceivingVariance(putAwayHeader.getReceivingVariance());

                // PalletId CMD 11-04-2025
//                String palletId = dbPutAwayLine.getPalletId();
//                boolean palletExist = palletIdList.stream().anyMatch(n -> n.equalsIgnoreCase(palletId));
//                log.info("PalletId exits : " + palletExist);
//                if(!palletExist) {
//                    palletIdList.add(palletId);
//                    assignedOn = palletIdAssignmentRepository.getAssignedOn(companyCode, plantId, languageId, warehouseId, palletId);
//                }
//                dbPutAwayLine.setAssignedOn(assignedOn);
//                log.info("PalletId assignedOn : " + assignedOn + "|" + palletId);

                boolean existingPutAwayLine = putAwayLineV2Repository.existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndRefDocNumberAndDeletionIndicator(
                        companyCode, plantId, languageId, warehouseId, newPutAwayLine.getBarcodeId(), refDocNumber, 0L);
//                log.info("Existing putawayline already created : " + existingPutAwayLine);
//
//                if(existingPutAwayLine) {
//                    throw new BadRequestException("HU Serial Number already exist..! + " + newPutAwayLine.getBarcodeId());
//                }

                if (!existingPutAwayLine) {
//                    try {
//                        String leadTime = putAwayLineV2Repository.getLeadTimeByPackBarCode(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getPackBarcodes(), new Date());
                    String leadTime = putAwayLineV2Repository.getLeadTimeV3(dbPutAwayLine.getAssignedOn(), new Date());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        throw new BadRequestException("Exception : " + e);
//                    }

                    if (newPutAwayLine.getInboundOrderTypeId() == 11L) {
                        dbPutAwayLine.setInventoryQuantity(newPutAwayLine.getQtyInCreate());
                    } else {
                        dbPutAwayLine.setInventoryQuantity(newPutAwayLine.getQtyInPiece());
                    }
                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);
//                    websocketNotification(createdPutAwayLine, loginUserID);
//                    fireBaseNotification(createdPutAwayLine, loginUserID);
                    //notiifcation code
                    String cnfStBin = createdPutAwayLine.getConfirmedStorageBin();
                    boolean isExist = storageBinList.stream().anyMatch(n -> n.equalsIgnoreCase(cnfStBin));
                    log.info("cnfBin: " + isExist);
                    if (!isExist) {
                        log.info("fb init()--->");
                        FetchImpl fetch = new FetchImpl();
                        storageBinList.add(cnfStBin);
                        fetch.setConfirmedStorageBin(cnfStBin);
                        fetch.setRefDocNumber(createdPutAwayLine.getRefDocNumber());
                        fetch.setPreInboundNo(createdPutAwayLine.getPreInboundNo());
                        fetch.setCreatedOn(createdPutAwayLine.getCreatedOn());
                        fbNotificationList.add(fetch);
                    }

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        storageBinService.updateStorageBinV5(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin, companyCode, plantId, languageId, warehouseId, loginUserID);

                        if (putAwayHeader != null) {
                            String confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                            String proposedStorageBin = putAwayHeader.getProposedStorageBin();
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty()
                                    + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            log.info("PutawayHeader StatusId : 20");
                            statusDescription = stagingLineV2Repository.getStatusDescription(
                                    putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated: " + putAwayHeader);

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(
                                        createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                        createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                        createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                        createdPutAwayLine.getItemCode(), createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info("V3-tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                        + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                        + ", " + putAwayHeader.getPutAwayQuantity() + ", "
                                        + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException(
                                            "sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                log.info("DbPutAwayQty---->" +dbPutawayQty);
                                log.info("DbAssignedPutawayQty---->" +dbAssignedPutawayQty);
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        log.info("New PutawayHeader Creation: ");
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader,
                                                CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId);
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber); // PutAway Number

                                        newPutAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(
                                                    String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }

                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }

                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }

                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = getStatusDescription(newPutAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);
                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("putAwayHeader created: " + newPutAwayHeader);
                                    }
                                    if (!proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        putAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            putAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            putAwayHeader.setReferenceField4("1");
                                        }

                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        putAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        putAwayHeader.setStatusId(19L);

                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        putAwayHeader.setStatusDescription(statusDescription);
                                        putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                                        log.info("putAwayHeader updated: " + putAwayHeader);
                                    }
                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE
                        // table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(
                                createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());
//                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null
                                    && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
//                            if (addedAcceptQty > inboundLine.getOrderQty()) {
//                                throw new BadRequestException("Accept qty cannot be greater than order qty");
//                            }
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null
                                    && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
//                            if (addedDamageQty > inboundLine.getOrderQty()) {
//                                throw new BadRequestException("Damage qty cannot be greater than order qty");
//                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) { // condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }
                        inboundLine.setBatchSerialNumber(createdPutAwayLine.getBatchSerialNumber());
                        inboundLine.setManufacturerDate(createdPutAwayLine.getManufacturerDate());
                        inboundLine.setExpiryDate(createdPutAwayLine.getExpiryDate());
                        inboundLine.setReferenceField2("TRUE");
                        inboundLine.setStatusId(20L);
                        inboundLine.setQtyInCase(createdPutAwayLine.getQtyInCase());
                        inboundLine.setQtyInPiece(createdPutAwayLine.getQtyInPiece());
                        inboundLine.setQtyInCreate(createdPutAwayLine.getQtyInCreate());
                        inboundLine.setReceivingVariance(createdPutAwayLine.getReceivingVariance());
                        statusDescription = getStatusDescription(20L, createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLineV2Repository.delete(inboundLine);
                        InboundLineV2 updatedInboundLine = inboundLineV2Repository.save(inboundLine);
//                        log.info("inboundLine updated : " + updatedInboundLine);

                        //Create Inventory
                        inventoryService.createInventoryNonCBMV5(createdPutAwayLine, loginUserID);
                    }
                }
//                else {
//                    log.info("Putaway Line already exist : " + existingPutAwayLine);
//                }
            }
            log.info("Update palletIdList : " + palletIdList);
            if (palletIdList != null && !palletIdList.isEmpty()) {
                for (String pallet : palletIdList) {
                    updatePalletIdAssignment(companyCode, plantId, languageId, warehouseId, pallet, loginUserID);
                }
            }
            log.info("send fbNotificationList: " + fbNotificationList);
//            if(fbNotificationList != null && !fbNotificationList.isEmpty()) {
//                for(FetchImpl inbound : fbNotificationList) {
//                    fireBaseNotificationV3(companyCode, plantId, languageId, warehouseId, inbound, loginUserID);
//                }
//            }
            if (refDocNumbers != null && !refDocNumbers.isEmpty()) {
                log.info("refDocNumbers : " + refDocNumbers);
                for (PutAwayLineConfirm docNumber : refDocNumbers) {
                    putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId,
                            docNumber.getRefDocNumber(), docNumber.getPreInboundNo());
                    log.info("InboundHeader received lines count updated: " + docNumber.getRefDocNumber() + ", " + docNumber.getPreInboundNo());

                    inboundConfirmValidation(companyCode, plantId, languageId, warehouseId, docNumber.getRefDocNumber(), docNumber.getPreInboundNo(), loginUserID);
                }
            }
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

//    /**
//     *
//     * @param putAwayLine
//     */
//    private void fireBaseNotificationV3(String companyCodeId, String plantId, String languageId,
//                                        String warehouseId, FetchImpl putAwayLine, String loginUserID) {
//        try {
//            List<String> deviceToken = pickupHeaderV2Repository.getDeviceTokenV3(companyCodeId, plantId, languageId, warehouseId, true);
//            if (deviceToken != null && !deviceToken.isEmpty()) {
//                String title = "Inbound Create";
//                String message = "A new Inbound- " + putAwayLine.getConfirmedStorageBin() + " has been created on ";
//
//                NotificationSave notificationInput = new NotificationSave();
//                notificationInput.setUserId(Collections.singletonList(loginUserID));
//                notificationInput.setUserType(null);
//                notificationInput.setMessage(message);
//                notificationInput.setTopic(title);
//                notificationInput.setReferenceNumber(putAwayLine.getRefDocNumber());
//                notificationInput.setDocumentNumber(putAwayLine.getPreInboundNo());
//                notificationInput.setCompanyCodeId(companyCodeId);
//                notificationInput.setPlantId(plantId);
//                notificationInput.setLanguageId(languageId);
//                notificationInput.setWarehouseId(warehouseId);
//                notificationInput.setCreatedOn(putAwayLine.getCreatedOn());
//                notificationInput.setCreatedBy(loginUserID);
//                notificationInput.setStorageBin(putAwayLine.getConfirmedStorageBin());
//
//                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
//            }
//        } catch (Exception e) {
//            log.error("Inbound firebase notification error " + e.toString());
//        }
//    }

    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV5(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, Exception {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {
//                if (newPutAwayLine.getPutAwayQuantity() == null) {
//                    newPutAwayLine.setPutAwayQuantity(0D);
//                }
//                if (newPutAwayLine.getPutawayConfirmedQty() == null) {
//                    newPutAwayLine.setPutawayConfirmedQty(0D);
//                }
//                if (newPutAwayLine.getPutawayConfirmedQty() < newPutAwayLine.getPutAwayQuantity()) {
//                    List<PutAwayHeaderV2> dbPutAwayHeaderList = putAwayHeaderService.getPutAwayHeaderV2(newPutAwayLine.getWarehouseId(),
//                            newPutAwayLine.getPreInboundNo(),
//                            newPutAwayLine.getRefDocNumber(),
//                            newPutAwayLine.getPutAwayNumber(),
//                            newPutAwayLine.getCompanyCode(),
//                            newPutAwayLine.getPlantId(),
//                            newPutAwayLine.getLanguageId());
//                    if (dbPutAwayHeaderList != null) {
//                        for (PutAwayHeaderV2 newPutAwayHeader : dbPutAwayHeaderList) {
//                            if (!newPutAwayHeader.getApprovalStatus().equalsIgnoreCase("Approved")) {
//                                throw new BadRequestException("Binned Quantity is less than Received");
//                            }
//                        }
//                    }
//                }

                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
//                PutAwayHeaderV2 dbPutAwayHeader = new PutAwayHeaderV2();

                if (newPutAwayLine.getQuantityType() == null) {
                    newPutAwayLine.setQuantityType("A");
                }
                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();

                Double cbmPerQuantity = 0D;
                Double cbm = 0D;
                Double allocatedVolume = 0D;
                Double occupiedVolume = 0D;
                Double remainingVolume = 0D;
                Double totalVolume = 0D;

                Double allocateQty = 0D;
                Double orderedQty = 0D;
                Double differenceQty = 0D;
                Double assignedProposedBinVolume = 0D;

                Long statusId = 0L;

                boolean capacityCheck = false;
                boolean storageBinCapacityCheck = false;

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(newPutAwayLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck != null) {
                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                        log.info("capacity Check: " + capacityCheck);
                    }
                }

                String confirmedStorageBin = newPutAwayLine.getConfirmedStorageBin();
                String proposedStorageBin = newPutAwayLine.getProposedStorageBin();
                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 storageBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                StorageBinV2 proposedBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getProposedStorageBin());

                PutAwayHeaderV2 findPutawayHeader = putAwayHeaderService.getPutawayHeaderV2(companyCode, plantId, warehouseId, languageId, newPutAwayLine.getPutAwayNumber());
                List<PutAwayLineV2> findPutawayLine = getPutAwayLineV2ForPutawayConfirm(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber());

                if (storageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(storageBin.getFloorId()));
                    if (storageBin.isCapacityCheck()) {
                        storageBinCapacityCheck = storageBin.isCapacityCheck();
                        log.info("confirmed storageBinCapacityCheck: " + storageBinCapacityCheck);
                    }
                }

                if (capacityCheck && !storageBinCapacityCheck) {
                    throw new BadRequestException("Selected Bin is not under Capacity Check. Kindly Select a Capacity Enabled Bin!");
                }
                if (!capacityCheck && storageBinCapacityCheck) {
                    throw new BadRequestException("Selected ItemCode is not under Capacity Check. Kindly Select a Capacity Enabled Item!");
                }
//                if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
//                    if (storageBin.getStatusId() == 1 && storageBin.getBinClassId() != 7) {
//                        log.info("confirmed storageBin is Not Empty: " + storageBin.getStorageBin());
//                        List<InventoryV2> stBinInventoryList = inventoryService.getInventoryForPutawayHeader(itemCode, newPutAwayLine.getManufacturerName(), storageBin.getBinClassId(), companyCode, plantId, languageId, warehouseId);
//                        List<PutAwayHeaderV2> stBinPutawayHeaderList = putAwayHeaderService.getPutAwayHeaderForPutAwayConfirm(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPreInboundNo());
//                        if (stBinInventoryList != null) {
//                            log.info("Item present in confirmed storageBin : " + stBinInventoryList.get(0));
//                        }
//                        if (stBinInventoryList == null && stBinPutawayHeaderList == null) {
//                            throw new BadRequestException("Selected Bin is not empty and item present in the bin is not same as selected. Kindly Select a different Bin!");
//                        }
//                    }
//                }

                if (capacityCheck && storageBinCapacityCheck) {

                    if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin != proposedBin: " + confirmedStorageBin + ", " + proposedStorageBin);

                        if (newPutAwayLine.getCbmQuantity() != null) {
                            cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                        }
                        if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                            cbm = Double.valueOf(newPutAwayLine.getCbm());
                        }
                        if (storageBin.getTotalVolume() != null && storageBin.getTotalVolume() != "") {
                            totalVolume = Double.valueOf(storageBin.getTotalVolume());
                        }
                        if (storageBin.getAllocatedVolume() != null) {
                            allocatedVolume = Double.valueOf(storageBin.getAllocatedVolume());
                        }
                        if (storageBin.getOccupiedVolume() != null && storageBin.getOccupiedVolume() != "") {
                            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
                        }
                        if (storageBin.getRemainingVolume() != null && storageBin.getRemainingVolume() != "") {
                            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                        }

                        if (remainingVolume <= 0) {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocateQty = newPutAwayLine.getPutawayConfirmedQty();

                        if (remainingVolume < cbmPerQuantity) {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocatedVolume = allocateQty * cbmPerQuantity;
                        if (allocatedVolume <= remainingVolume) {
                            allocatedVolume = allocateQty * cbmPerQuantity;
                        } else {
                            throw new BadRequestException("Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }
                        if (totalVolume >= remainingVolume) {
                            remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                        } else {
                            remainingVolume = remainingVolume - allocatedVolume;
                        }
                        occupiedVolume = occupiedVolume + allocatedVolume;

                        log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                        if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                            statusId = 0L;
                            log.info("StorageBin Emptied");
                        } else {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                            statusId = 1L;
                            log.info("StorageBin Occupied");
                        }

                        //confirmed Bin volume update
                        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getConfirmedStorageBin(),
                                companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                        if (findPutawayLine == null) {
                            //proposed Bin revert volume update done during putaway header create
                            remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
//                            allocatedVolume = proposedBin.getAllocatedVolume();
                            occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            log.info("proposed Bin before confirm remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + allocatedVolume;
                            occupiedVolume = occupiedVolume - allocatedVolume;

                            log.info("proposed bin after confirm remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getProposedStorageBin(),
                                    companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());
                        }

                        log.info("Storage Bin occupied volume got updated");

                    }
                    if (confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin == proposedBin" + confirmedStorageBin + ", " + proposedStorageBin);

                        if (findPutawayHeader.getPutAwayQuantity() > newPutAwayLine.getPutawayConfirmedQty()) {
                            log.info("putAwayQty > confirmQty" + findPutawayHeader.getPutAwayQuantity() + ", " + newPutAwayLine.getPutawayConfirmedQty());

                            if (newPutAwayLine.getCbmQuantity() != null) {
                                cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                            }
                            if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                                cbm = Double.valueOf(newPutAwayLine.getCbm());
                            }
                            if (proposedBin.getTotalVolume() != null && proposedBin.getTotalVolume() != "") {
                                totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            }
                            if (proposedBin.getAllocatedVolume() != null) {
                                allocatedVolume = Double.valueOf(proposedBin.getAllocatedVolume());
                            }
                            if (proposedBin.getOccupiedVolume() != null && proposedBin.getOccupiedVolume() != "") {
                                occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            }
                            if (proposedBin.getRemainingVolume() != null && proposedBin.getRemainingVolume() != "") {
                                remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
                            }

                            allocateQty = newPutAwayLine.getPutawayConfirmedQty();
                            if (newPutAwayLine.getOrderQty() != null) {
                                orderedQty = newPutAwayLine.getOrderQty();
                            }
                            log.info("allocateQty(confirmed PutawayQty), putawayQty, orderQty: " + allocateQty + ", " + findPutawayHeader.getPutAwayQuantity() + ", " + orderedQty);

                            assignedProposedBinVolume = findPutawayHeader.getPutAwayQuantity() * cbmPerQuantity;
                            allocatedVolume = allocateQty * cbmPerQuantity;

                            log.info("assignedProposedBinVolume, allocatedVolume: " + assignedProposedBinVolume + ", " + allocatedVolume);

                            remainingVolume = remainingVolume + assignedProposedBinVolume - allocatedVolume;
                            occupiedVolume = occupiedVolume - assignedProposedBinVolume + allocatedVolume;

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0) && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", " + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            //confirmed Bin volume update
                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume, newPutAwayLine.getConfirmedStorageBin(),
                                    companyCode, plantId, languageId, warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                            log.info("Storage Bin occupied volume got updated");

                        }
                    }
                }
                //this code is set for mobile device to work
//                if (newPutAwayLine.getCompanyCode() == null || newPutAwayLine.getBarcodeId() == null || newPutAwayLine.getManufacturerName() == null || newPutAwayLine.getPutAwayUom() == null) {
//                    dbPutAwayHeader = putAwayHeaderService.getPutawayHeaderV2(newPutAwayLine.getPutAwayNumber());
//                    newPutAwayLine.setCompanyCode(dbPutAwayHeader.getCompanyCodeId());
//                    newPutAwayLine.setBarcodeId(dbPutAwayHeader.getBarcodeId());
//                    newPutAwayLine.setManufacturerName(dbPutAwayHeader.getManufacturerName());
//                    newPutAwayLine.setPutAwayUom(dbPutAwayHeader.getPutAwayUom());
//                }
//                if (newPutAwayLine.getBarcodeId() == null) {
//                    newPutAwayLine.setBarcodeId(dbPutAwayHeader.getBarcodeId());
//                }
//                if (newPutAwayLine.getManufacturerName() == null) {
//                    newPutAwayLine.setManufacturerName(dbPutAwayHeader.getManufacturerName());
//                }
//                if (newPutAwayLine.getPutAwayUom() == null) {
//                    newPutAwayLine.setPutAwayUom(dbPutAwayHeader.getPutAwayUom());
//                }

                //V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode,
                        languageId,
                        plantId,
                        warehouseId);

                newPutAwayLine.setCompanyDescription(description.getCompanyDesc());
                newPutAwayLine.setPlantDescription(description.getPlantDesc());
                newPutAwayLine.setWarehouseDescription(description.getWarehouseDesc());

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(), newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    if (newPutAwayLine.getManufacturerFullName() != null) {
                        newPutAwayLine.setManufacturerFullName(newPutAwayLine.getManufacturerFullName());
                    } else {
                        newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    }
                    if (newPutAwayLine.getMiddlewareId() != null) {
                        newPutAwayLine.setMiddlewareId(newPutAwayLine.getMiddlewareId());
                    } else {
                        newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    }
                    if (newPutAwayLine.getMiddlewareHeaderId() != null) {
                        newPutAwayLine.setMiddlewareHeaderId(newPutAwayLine.getMiddlewareHeaderId());
                    } else {
                        newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    }
                    if (newPutAwayLine.getMiddlewareTable() != null) {
                        newPutAwayLine.setMiddlewareTable(newPutAwayLine.getMiddlewareTable());
                    } else {
                        newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    }
                    if (newPutAwayLine.getPurchaseOrderNumber() != null) {
                        newPutAwayLine.setPurchaseOrderNumber(newPutAwayLine.getPurchaseOrderNumber());
                    } else {
                        newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    }
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                }

//                Warehouse warehouse = getWarehouse(newPutAwayLine.getWarehouseId(),
//                        newPutAwayLine.getCompanyCode(),
//                        newPutAwayLine.getPlantId(),
//                        newPutAwayLine.getLanguageId());

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
//                if (newPutAwayLine.getCompanyCode() == null) {
//                    dbPutAwayLine.setCompanyCode(warehouse.getCompanyCode());
//                } else {
                dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());
//                }

                dbPutAwayLine.setBranchCode(newPutAwayLine.getBranchCode());
                dbPutAwayLine.setTransferOrderNo(newPutAwayLine.getTransferOrderNo());
                dbPutAwayLine.setIsCompleted(newPutAwayLine.getIsCompleted());

                dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                dbPutAwayLine.setStatusId(20L);
                String statusDescription = stagingLineV2Repository.getStatusDescription(20L, newPutAwayLine.getLanguageId());
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setPackBarcodes(newPutAwayLine.getPackBarcodes());
                dbPutAwayLine.setBarcodeId(newPutAwayLine.getBarcodeId());
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                log.info("putawayHeader: " + findPutawayHeader);
                if (findPutawayHeader != null) {
                    dbPutAwayLine.setBatchSerialNumber(findPutawayHeader.getBatchSerialNumber());
                    dbPutAwayLine.setCreatedOn(findPutawayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(findPutawayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(findPutawayHeader.getInboundOrderTypeId());
                    dbPutAwayLine.setStorageSectionId(findPutawayHeader.getStorageSectionId());

                    if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                        dbPutAwayLine.setParentProductionOrderNo(findPutawayHeader.getParentProductionOrderNo());
                    }

                    if (newPutAwayLine.getManufacturerDate() == null) {
                        dbPutAwayLine.setManufacturerDate(findPutawayHeader.getManufacturerDate());
                    }
                    if (newPutAwayLine.getExpiryDate() == null) {
                        dbPutAwayLine.setExpiryDate(findPutawayHeader.getExpiryDate());
                    }

                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        newPutAwayLine.getLanguageId(), newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(),
                        newPutAwayLine.getWarehouseId(), newPutAwayLine.getGoodsReceiptNo(),
                        newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(),
                        newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(),
                        newPutAwayLine.getItemCode(), newPutAwayLine.getProposedStorageBin(),
                        Arrays.asList(newPutAwayLine.getConfirmedStorageBin()),
                        newPutAwayLine.getDeletionIndicator());

                log.info("Existing putawayline already created : " + existingPutAwayLine);

                if (existingPutAwayLine.isEmpty()) {

                    try {
                        String leadTime = putAwayLineV2Repository.getleadtime(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getPutAwayNumber(), new Date());
                        dbPutAwayLine.setReferenceField1(leadTime);
                        log.info("LeadTime: " + leadTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new BadRequestException("Exception : " + e);
                    }
                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);

                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);

//                    boolean isInventoryCreated = false;
//                    boolean isInventoryMovemoentCreated = false;

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        /*
                         * Commenting out Inventory creation alone
                         */
//                        InventoryV2 inventory = new InventoryV2();
//                        BeanUtils.copyProperties(createdPutAwayLine, inventory, CommonUtils.getNullPropertyNames(createdPutAwayLine));
//                        inventory.setInventoryId(System.currentTimeMillis());
//                        inventory.setCompanyCodeId(createdPutAwayLine.getCompanyCode());
//                        inventory.setVariantCode(1L);                // VAR_ID
//                        inventory.setVariantSubCode("1");            // VAR_SUB_ID
//                        inventory.setStorageMethod("1");            // STR_MTD
//                        inventory.setBatchSerialNumber("1");        // STR_NO
//                        inventory.setBatchSerialNumber(newPutAwayLine.getBatchSerialNumber());
//                        inventory.setStorageBin(createdPutAwayLine.getConfirmedStorageBin());
//                        inventory.setBarcodeId(createdPutAwayLine.getBarcodeId());

                        //v2 code
//                        if (createdPutAwayLine.getCbm() == null) {
//                            createdPutAwayLine.setCbm("0");
//                        }
//                        if (createdPutAwayLine.getCbmQuantity() == null) {
//                            createdPutAwayLine.setCbmQuantity(0D);
//                        }
//                        inventory.setCbmPerQuantity(String.valueOf(Double.valueOf(createdPutAwayLine.getCbm()) / createdPutAwayLine.getCbmQuantity()));
//                        inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));

//                        Double invQty = 0D;
//                        Double cbmPerQty = 0D;
//                        Double invCbm = 0D;
//
//                        log.info("CapacityCheck for Create Inventory-----------> : " + capacityCheck);

//                        if (capacityCheck) {
//                            if (createdPutAwayLine.getCbmQuantity() != null) {
//                                inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));
//                            }
//                            if (createdPutAwayLine.getPutawayConfirmedQty() != null) {
//                                invQty = createdPutAwayLine.getPutawayConfirmedQty();
//                            }
//                            if (createdPutAwayLine.getCbmQuantity() == null) {
//
//                                if (createdPutAwayLine.getCbm() != null) {
//                                    cbm = Double.valueOf(createdPutAwayLine.getCbm());
//                                }
//                                cbmPerQty = cbm / invQty;
//                                inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
//                            }
//                            if (createdPutAwayLine.getCbm() != null) {
//                                invCbm = Double.valueOf(createdPutAwayLine.getCbm());
//                            }
//                            if (createdPutAwayLine.getCbm() == null) {
//                                invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
//                            }
//                            inventory.setCbm(String.valueOf(invCbm));
//                        }

                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
                        storageBinPutAway.setPlantId(dbPutAwayLine.getPlantId());
                        storageBinPutAway.setLanguageId(dbPutAwayLine.getLanguageId());
                        storageBinPutAway.setWarehouseId(dbPutAwayLine.getWarehouseId());
                        storageBinPutAway.setBin(dbPutAwayLine.getConfirmedStorageBin());

                        StorageBinV2 dbStorageBin = null;
                        try {
                            dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }

//                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
//                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
//                            inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
//                            inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
//                        }
//                        if (itemCodeCapacityCheck != null) {
//                            inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
//                            inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
//                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        }

//                        if (dbStorageBin != null) {
//                            inventory.setBinClassId(dbStorageBin.getBinClassId());
//                            inventory.setReferenceField10(dbStorageBin.getStorageSectionId());
//                            inventory.setReferenceField5(dbStorageBin.getAisleNumber());
//                            inventory.setReferenceField6(dbStorageBin.getShelfId());
//                            inventory.setReferenceField7(dbStorageBin.getRowId());
//                            inventory.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
//                        }

//                        inventory.setCompanyDescription(dbPutAwayLine.getCompanyDescription());
//                        inventory.setPlantDescription(dbPutAwayLine.getPlantDescription());
//                        inventory.setWarehouseDescription(dbPutAwayLine.getWarehouseDescription());

                        /*
                         * Insert PA_CNF_QTY value in this field.
                         * Also Pass WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table and fetch ST_BIN/INV_QTY value.
                         * Update INV_QTY value by (INV_QTY - PA_CNF_QTY) . If this value becomes Zero, then delete the record"
                         */
//                        try {
//                            InventoryV2 existinginventory = inventoryService.getInventory(
//                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
//                                    createdPutAwayLine.getWarehouseId(),
//                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
//                                    createdPutAwayLine.getManufacturerName(), 3L);
//
////                            IInventoryImpl existinginventory = inventoryService.getInventoryforExistingBin(
////                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
////                                    createdPutAwayLine.getWarehouseId(),
////                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
////                                    createdPutAwayLine.getManufacturerName(), 3L);
//
//                            double INV_QTY = existinginventory.getInventoryQuantity() - createdPutAwayLine.getPutawayConfirmedQty();
//                            log.info("INV_QTY : " + INV_QTY);
//
////                            inventory.setPalletCode(existinginventory.getPalletCode());
////                            inventory.setCaseCode(existinginventory.getCaseCode());
////                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//
////                            if (capacityCheck) {
////                                if (existinginventory.getCbm() != null && createdPutAwayLine.getCbm() != null) {
////                                    invCbm = Double.valueOf(existinginventory.getCbm()) - Double.valueOf(createdPutAwayLine.getCbm());
////                                    log.info("INV_CBM: " + invCbm);
////                                }
////                                if (invCbm >= 0) {
////                                    existinginventory.setCbm(String.valueOf(invCbm));
////                                }
////                            }
//                            if (INV_QTY >= 0) {
////                                existinginventory.setInventoryQuantity(INV_QTY);
////                                InventoryV2 updatedInventory = inventoryV2Repository.save(existinginventory);
////                                log.info("updatedInventory--------> : " + updatedInventory);
//                                InventoryV2 inventory2 = new InventoryV2();
//                                BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                                stockTypeDesc = getStockTypeDesc(createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
//                                inventory2.setStockTypeDescription(stockTypeDesc);
//                                inventory2.setInventoryQuantity(INV_QTY);
//                                inventory2.setInventoryId(System.currentTimeMillis());
//                                InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                                log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//                            }
//
//                        } catch (Exception e) {
//                            log.info("Existing Inventory---Error-----> : " + e.toString());
//                        }

                        // INV_QTY
//                        inventory.setInventoryQuantity(createdPutAwayLine.getPutawayConfirmedQty());

//                        inventory.setStockTypeId(10L);
//                        stockTypeDesc = getStockTypeDesc(dbPutAwayLine.getCompanyCode(),dbPutAwayLine.getPlantId(),dbPutAwayLine.getLanguageId(),dbPutAwayLine.getWarehouseId(),10L);
//                        inventory.setStockTypeDescription(stockTypeDesc);

                        // INV_UOM
//                        inventory.setInventoryUom(createdPutAwayLine.getPutAwayUom());
//                        inventory.setReferenceDocumentNo(createdPutAwayLine.getRefDocNumber());
//                        inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        inventory.setCreatedBy(createdPutAwayLine.getCreatedBy());
//                        inventory.setCreatedOn(createdPutAwayLine.getCreatedOn());
                        //InventoryV2 createdInventory = inventoryV2Repository.save(inventory);
//                        log.info("createdInventory : " + createdInventory);
//                        log.info("createdInventory BinClassId : " + createdInventory.getBinClassId());
//
//                        if (createdInventory != null) {
//                            isInventoryCreated = true;
//                        }

                        /* Insert a record into INVENTORYMOVEMENT table */
//                        InventoryMovement createdInventoryMovement = createInventoryMovementV2(createdPutAwayLine);
//                        log.info("inventoryMovement created: " + createdInventoryMovement);
//                        log.info("inventoryMovement created binClassId: " + createdInventory.getBinClassId());

//                        if (createdInventoryMovement != null) {
//                            isInventoryMovemoentCreated = true;
//                        }

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
                                dbPutAwayLine.getCompanyCode(), dbPutAwayLine.getPlantId(), dbPutAwayLine.getLanguageId(), dbPutAwayLine.getWarehouseId(),
                                loginUserID, authTokenForMastersService.getAccess_token());

//                        if (isInventoryCreated && isInventoryMovemoentCreated) {
//                        if (isInventoryMovemoentCreated) {
                        PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.getPutAwayHeaderV2ForPutAwayLine(createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getRefDocNumber(),
                                createdPutAwayLine.getPutAwayNumber(),
                                createdPutAwayLine.getCompanyCode(),
                                createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId());

                        confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                        proposedStorageBin = putAwayHeader.getProposedStorageBin();
                        if (putAwayHeader != null) {
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty() + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            log.info("PutawayHeader StatusId : 20");
                            statusDescription = stagingLineV2Repository.getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated: " + putAwayHeader);

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
//                                List<PutAwayLineV2> filteredlist = newPutAwayLines
//                                        .stream()
//                                        .filter(a ->
//                                                a.getPutAwayNumber().equalsIgnoreCase(createdPutAwayLine.getPutAwayNumber()) &&
//                                                a.getRefDocNumber().equalsIgnoreCase(createdPutAwayLine.getRefDocNumber()) &&
//                                                a.getPreInboundNo().equalsIgnoreCase(createdPutAwayLine.getPreInboundNo()) &&
//                                                a.getItemCode().equalsIgnoreCase(createdPutAwayLine.getItemCode()) &&
//                                                a.getManufacturerName().equalsIgnoreCase(createdPutAwayLine.getManufacturerName()) &&
//                                                a.getCompanyCode().equalsIgnoreCase(createdPutAwayLine.getCompanyCode()) &&
//                                                a.getPlantId().equalsIgnoreCase(createdPutAwayLine.getPlantId()) &&
//                                                a.getLanguageId().equalsIgnoreCase(createdPutAwayLine.getLanguageId()) &&
//                                                a.getWarehouseId().equalsIgnoreCase(createdPutAwayLine.getWarehouseId()) &&
//                                                a.getLineNo().equals(createdPutAwayLine.getLineNo()) &&
//                                                a.getConfirmedStorageBin().equalsIgnoreCase(createdPutAwayLine.getConfirmedStorageBin()))
//                                        .collect(Collectors.toList());
//                                log.info("PutawayLine filtered List: " + filteredlist);
//                                if (filteredlist != null && !filteredlist.isEmpty()) {
//                                    Double putawayQty = filteredlist.stream().mapToDouble(a -> a.getPutawayConfirmedQty()).sum();
//                                    Double dbPutawayQty = 0D;
//                                    if(putAwayHeader.getReferenceField3() != null) {
//                                        dbPutawayQty = Double.valueOf(putAwayHeader.getReferenceField3());
//                                    }
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(createdPutAwayLine.getCompanyCode(),
                                        createdPutAwayLine.getPlantId(),
                                        createdPutAwayLine.getLanguageId(),
                                        createdPutAwayLine.getWarehouseId(),
                                        createdPutAwayLine.getRefDocNumber(),
                                        createdPutAwayLine.getPreInboundNo(),
                                        createdPutAwayLine.getItemCode(),
                                        createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info("tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                        + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                        + ", " + putAwayHeader.getPutAwayQuantity() + ", " + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        log.info("New PutawayHeader Creation: ");
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number

                                        newPutAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }
                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//                                    Double PUTAWAY_QTY = (putAwayHeader.getPutAwayQuantity() != null ? putAwayHeader.getPutAwayQuantity() : 0) - (createdPutAwayLine.getPutawayConfirmedQty() != null ? createdPutAwayLine.getPutawayConfirmedQty() : 0);
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(newPutAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);
                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("putAwayHeader created: " + newPutAwayHeader);
                                    }
                                    if (!proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {

                                        putAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            putAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            putAwayHeader.setReferenceField4("1");
                                        }
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        putAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        putAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        putAwayHeader.setStatusDescription(statusDescription);
                                        putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                                        log.info("putAwayHeader updated: " + putAwayHeader);
                                    }
                                }
//                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(createdPutAwayLine.getCompanyCode(),
                                createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId(),
                                createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getRefDocNumber(),
                                createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(),
                                createdPutAwayLine.getItemCode());
                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedAcceptQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Accept qty cannot be greater than order qty");
                            }
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) {          //condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setBatchSerialNumber(createdPutAwayLine.getBatchSerialNumber());
                        inboundLine.setManufacturerDate(createdPutAwayLine.getManufacturerDate());
                        inboundLine.setExpiryDate(createdPutAwayLine.getExpiryDate());
                        inboundLine.setStatusId(20L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(20L, createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
//                        }
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);
            return createdPutAwayLines;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV2(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {
//                if (newPutAwayLine.getPutAwayQuantity() == null) {
//                    newPutAwayLine.setPutAwayQuantity(0D);
//                }
//                if (newPutAwayLine.getPutawayConfirmedQty() == null) {
//                    newPutAwayLine.setPutawayConfirmedQty(0D);
//                }
//                if (newPutAwayLine.getPutawayConfirmedQty() < newPutAwayLine.getPutAwayQuantity()) {
//                    List<PutAwayHeaderV2> dbPutAwayHeaderList = putAwayHeaderService.getPutAwayHeaderV2(newPutAwayLine.getWarehouseId(),
//                            newPutAwayLine.getPreInboundNo(),
//                            newPutAwayLine.getRefDocNumber(),
//                            newPutAwayLine.getPutAwayNumber(),
//                            newPutAwayLine.getCompanyCode(),
//                            newPutAwayLine.getPlantId(),
//                            newPutAwayLine.getLanguageId());
//                    if (dbPutAwayHeaderList != null) {
//                        for (PutAwayHeaderV2 newPutAwayHeader : dbPutAwayHeaderList) {
//                            if (!newPutAwayHeader.getApprovalStatus().equalsIgnoreCase("Approved")) {
//                                throw new BadRequestException("Binned Quantity is less than Received");
//                            }
//                        }
//                    }
//                }

                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
//                PutAwayHeaderV2 dbPutAwayHeader = new PutAwayHeaderV2();

                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();

                Double cbmPerQuantity = 0D;
                Double cbm = 0D;
                Double allocatedVolume = 0D;
                Double occupiedVolume = 0D;
                Double remainingVolume = 0D;
                Double totalVolume = 0D;

                Double allocateQty = 0D;
                Double orderedQty = 0D;
                Double differenceQty = 0D;
                Double assignedProposedBinVolume = 0D;

                Long statusId = 0L;

                boolean capacityCheck = false;
                boolean storageBinCapacityCheck = false;

                ImBasicData imBasicData = new ImBasicData();
                imBasicData.setCompanyCodeId(companyCode);
                imBasicData.setPlantId(plantId);
                imBasicData.setLanguageId(languageId);
                imBasicData.setWarehouseId(warehouseId);
                imBasicData.setItemCode(itemCode);
                imBasicData.setManufacturerName(newPutAwayLine.getManufacturerName());
                ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData,
                        authTokenForMastersService.getAccess_token());
                log.info("ImbasicData1 : " + itemCodeCapacityCheck);

                if (itemCodeCapacityCheck != null) {
                    if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                        capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                        log.info("capacity Check: " + capacityCheck);
                    }
                }

                String confirmedStorageBin = newPutAwayLine.getConfirmedStorageBin();
                String proposedStorageBin = newPutAwayLine.getProposedStorageBin();
                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", "
                        + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 storageBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId,
                        warehouseId, newPutAwayLine.getConfirmedStorageBin());
                StorageBinV2 proposedBin = storageBinRepository.getStorageBin(companyCode, plantId, languageId,
                        warehouseId, newPutAwayLine.getProposedStorageBin());

                PutAwayHeaderV2 findPutawayHeader = putAwayHeaderService.getPutawayHeaderV2(companyCode, plantId,
                        warehouseId, languageId, newPutAwayLine.getPutAwayNumber());
                List<PutAwayLineV2> findPutawayLine = getPutAwayLineV2ForPutawayConfirm(companyCode, plantId,
                        languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber());

                if (storageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(storageBin.getFloorId()));
                    if (storageBin.isCapacityCheck()) {
                        storageBinCapacityCheck = storageBin.isCapacityCheck();
                        log.info("confirmed storageBinCapacityCheck: " + storageBinCapacityCheck);
                    }
                }

                if (capacityCheck && !storageBinCapacityCheck) {
                    throw new BadRequestException(
                            "Selected Bin is not under Capacity Check. Kindly Select a Capacity Enabled Bin!");
                }
                if (!capacityCheck && storageBinCapacityCheck) {
                    throw new BadRequestException(
                            "Selected ItemCode is not under Capacity Check. Kindly Select a Capacity Enabled Item!");
                }
//                if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
//                    if (storageBin.getStatusId() == 1 && storageBin.getBinClassId() != 7) {
//                        log.info("confirmed storageBin is Not Empty: " + storageBin.getStorageBin());
//                        List<InventoryV2> stBinInventoryList = inventoryService.getInventoryForPutawayHeader(itemCode, newPutAwayLine.getManufacturerName(), storageBin.getBinClassId(), companyCode, plantId, languageId, warehouseId);
//                        List<PutAwayHeaderV2> stBinPutawayHeaderList = putAwayHeaderService.getPutAwayHeaderForPutAwayConfirm(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPreInboundNo());
//                        if (stBinInventoryList != null) {
//                            log.info("Item present in confirmed storageBin : " + stBinInventoryList.get(0));
//                        }
//                        if (stBinInventoryList == null && stBinPutawayHeaderList == null) {
//                            throw new BadRequestException("Selected Bin is not empty and item present in the bin is not same as selected. Kindly Select a different Bin!");
//                        }
//                    }
//                }

                if (capacityCheck && storageBinCapacityCheck) {

                    if (!confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info("confirmedStorageBin != proposedBin: " + confirmedStorageBin + ", "
                                + proposedStorageBin);

                        if (newPutAwayLine.getCbmQuantity() != null) {
                            cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                        }
                        if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                            cbm = Double.valueOf(newPutAwayLine.getCbm());
                        }
                        if (storageBin.getTotalVolume() != null && storageBin.getTotalVolume() != "") {
                            totalVolume = Double.valueOf(storageBin.getTotalVolume());
                        }
                        if (storageBin.getAllocatedVolume() != null) {
                            allocatedVolume = Double.valueOf(storageBin.getAllocatedVolume());
                        }
                        if (storageBin.getOccupiedVolume() != null && storageBin.getOccupiedVolume() != "") {
                            occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
                        }
                        if (storageBin.getRemainingVolume() != null && storageBin.getRemainingVolume() != "") {
                            remainingVolume = Double.valueOf(storageBin.getRemainingVolume());
                        }

                        if (remainingVolume <= 0) {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocateQty = newPutAwayLine.getPutawayConfirmedQty();

                        if (remainingVolume < cbmPerQuantity) {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }

                        allocatedVolume = allocateQty * cbmPerQuantity;
                        if (allocatedVolume <= remainingVolume) {
                            allocatedVolume = allocateQty * cbmPerQuantity;
                        } else {
                            throw new BadRequestException(
                                    "Selected Bin doesn't have required space to store the selected quantity. Kindly Select a different Bin!");
                        }
                        if (totalVolume >= remainingVolume) {
                            remainingVolume = totalVolume - (allocatedVolume + occupiedVolume);
                        } else {
                            remainingVolume = remainingVolume - allocatedVolume;
                        }
                        occupiedVolume = occupiedVolume + allocatedVolume;

                        log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                        if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                && remainingVolume.equals(totalVolume)) {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                    + remainingVolume + "," + totalVolume);
                            statusId = 0L;
                            log.info("StorageBin Emptied");
                        } else {
                            log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                    + remainingVolume + "," + totalVolume);
                            statusId = 1L;
                            log.info("StorageBin Occupied");
                        }

                        // confirmed Bin volume update
                        updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
                                newPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, languageId, warehouseId,
                                statusId, loginUserID, authTokenForMastersService.getAccess_token());

                        if (findPutawayLine == null) {
                            // proposed Bin revert volume update done during putaway header create
                            remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
//                            allocatedVolume = proposedBin.getAllocatedVolume();
                            occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            log.info("proposed Bin before confirm remainingVolume, occupiedVolume: " + remainingVolume
                                    + ", " + occupiedVolume);

                            remainingVolume = remainingVolume + allocatedVolume;
                            occupiedVolume = occupiedVolume - allocatedVolume;

                            log.info("proposed bin after confirm remainingVolume, occupiedVolume: " + remainingVolume
                                    + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                    && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
                                    newPutAwayLine.getProposedStorageBin(), companyCode, plantId, languageId,
                                    warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());
                        }

                        log.info("Storage Bin occupied volume got updated");

                    }
                    if (confirmedStorageBin.equalsIgnoreCase(proposedStorageBin)) {
                        log.info(
                                "confirmedStorageBin == proposedBin" + confirmedStorageBin + ", " + proposedStorageBin);

                        if (findPutawayHeader.getPutAwayQuantity() > newPutAwayLine.getPutawayConfirmedQty()) {
                            log.info("putAwayQty > confirmQty" + findPutawayHeader.getPutAwayQuantity() + ", "
                                    + newPutAwayLine.getPutawayConfirmedQty());

                            if (newPutAwayLine.getCbmQuantity() != null) {
                                cbmPerQuantity = newPutAwayLine.getCbmQuantity();
                            }
                            if (newPutAwayLine.getCbm() != null && newPutAwayLine.getCbm() != "") {
                                cbm = Double.valueOf(newPutAwayLine.getCbm());
                            }
                            if (proposedBin.getTotalVolume() != null && proposedBin.getTotalVolume() != "") {
                                totalVolume = Double.valueOf(proposedBin.getTotalVolume());
                            }
                            if (proposedBin.getAllocatedVolume() != null) {
                                allocatedVolume = Double.valueOf(proposedBin.getAllocatedVolume());
                            }
                            if (proposedBin.getOccupiedVolume() != null && proposedBin.getOccupiedVolume() != "") {
                                occupiedVolume = Double.valueOf(proposedBin.getOccupiedVolume());
                            }
                            if (proposedBin.getRemainingVolume() != null && proposedBin.getRemainingVolume() != "") {
                                remainingVolume = Double.valueOf(proposedBin.getRemainingVolume());
                            }

                            allocateQty = newPutAwayLine.getPutawayConfirmedQty();
                            if (newPutAwayLine.getOrderQty() != null) {
                                orderedQty = newPutAwayLine.getOrderQty();
                            }
                            log.info("allocateQty(confirmed PutawayQty), putawayQty, orderQty: " + allocateQty + ", "
                                    + findPutawayHeader.getPutAwayQuantity() + ", " + orderedQty);

                            assignedProposedBinVolume = findPutawayHeader.getPutAwayQuantity() * cbmPerQuantity;
                            allocatedVolume = allocateQty * cbmPerQuantity;

                            log.info("assignedProposedBinVolume, allocatedVolume: " + assignedProposedBinVolume + ", "
                                    + allocatedVolume);

                            remainingVolume = remainingVolume + assignedProposedBinVolume - allocatedVolume;
                            occupiedVolume = occupiedVolume - assignedProposedBinVolume + allocatedVolume;

                            log.info("remainingVolume, occupiedVolume: " + remainingVolume + ", " + occupiedVolume);

                            if ((occupiedVolume == 0 || occupiedVolume == 0D || occupiedVolume == 0.0)
                                    && remainingVolume.equals(totalVolume)) {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 0L;
                                log.info("StorageBin Emptied");
                            } else {
                                log.info("occupiedVolume,remainingVolume,totalVolume: " + occupiedVolume + ", "
                                        + remainingVolume + "," + totalVolume);
                                statusId = 1L;
                                log.info("StorageBin Occupied");
                            }

                            // confirmed Bin volume update
                            updateStorageBin(remainingVolume, occupiedVolume, allocatedVolume,
                                    newPutAwayLine.getConfirmedStorageBin(), companyCode, plantId, languageId,
                                    warehouseId, statusId, loginUserID, authTokenForMastersService.getAccess_token());

                            log.info("Storage Bin occupied volume got updated");

                        }
                    }
                }
                // this code is set for mobile device to work
//                if (newPutAwayLine.getCompanyCode() == null || newPutAwayLine.getBarcodeId() == null || newPutAwayLine.getManufacturerName() == null || newPutAwayLine.getPutAwayUom() == null) {
//                    dbPutAwayHeader = putAwayHeaderService.getPutawayHeaderV2(newPutAwayLine.getPutAwayNumber());
//                    newPutAwayLine.setCompanyCode(dbPutAwayHeader.getCompanyCodeId());
//                    newPutAwayLine.setBarcodeId(dbPutAwayHeader.getBarcodeId());
//                    newPutAwayLine.setManufacturerName(dbPutAwayHeader.getManufacturerName());
//                    newPutAwayLine.setPutAwayUom(dbPutAwayHeader.getPutAwayUom());
//                }
//                if (newPutAwayLine.getBarcodeId() == null) {
//                    newPutAwayLine.setBarcodeId(dbPutAwayHeader.getBarcodeId());
//                }
//                if (newPutAwayLine.getManufacturerName() == null) {
//                    newPutAwayLine.setManufacturerName(dbPutAwayHeader.getManufacturerName());
//                }
//                if (newPutAwayLine.getPutAwayUom() == null) {
//                    newPutAwayLine.setPutAwayUom(dbPutAwayHeader.getPutAwayUom());
//                }

                // V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId,
                        warehouseId);

                newPutAwayLine.setCompanyDescription(description.getCompanyDesc());
                newPutAwayLine.setPlantDescription(description.getPlantDesc());
                newPutAwayLine.setWarehouseDescription(description.getWarehouseDesc());

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode,
                        plantId, languageId, warehouseId, newPutAwayLine.getPreInboundNo(),
                        newPutAwayLine.getRefDocNumber(), newPutAwayLine.getLineNo(), itemCode,
                        newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    if (newPutAwayLine.getManufacturerFullName() != null) {
                        newPutAwayLine.setManufacturerFullName(newPutAwayLine.getManufacturerFullName());
                    } else {
                        newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    }
                    if (newPutAwayLine.getMiddlewareId() != null) {
                        newPutAwayLine.setMiddlewareId(newPutAwayLine.getMiddlewareId());
                    } else {
                        newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    }
                    if (newPutAwayLine.getMiddlewareHeaderId() != null) {
                        newPutAwayLine.setMiddlewareHeaderId(newPutAwayLine.getMiddlewareHeaderId());
                    } else {
                        newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    }
                    if (newPutAwayLine.getMiddlewareTable() != null) {
                        newPutAwayLine.setMiddlewareTable(newPutAwayLine.getMiddlewareTable());
                    } else {
                        newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    }
                    if (newPutAwayLine.getPurchaseOrderNumber() != null) {
                        newPutAwayLine.setPurchaseOrderNumber(newPutAwayLine.getPurchaseOrderNumber());
                    } else {
                        newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    }
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                }

//                Warehouse warehouse = getWarehouse(newPutAwayLine.getWarehouseId(),
//                        newPutAwayLine.getCompanyCode(),
//                        newPutAwayLine.getPlantId(),
//                        newPutAwayLine.getLanguageId());

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine,
                        CommonUtils.getNullPropertyNames(newPutAwayLine));
//                if (newPutAwayLine.getCompanyCode() == null) {
//                    dbPutAwayLine.setCompanyCode(warehouse.getCompanyCode());
//                } else {
                dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());
//                }

                dbPutAwayLine.setBranchCode(newPutAwayLine.getBranchCode());
                dbPutAwayLine.setTransferOrderNo(newPutAwayLine.getTransferOrderNo());
                dbPutAwayLine.setIsCompleted(newPutAwayLine.getIsCompleted());

                dbPutAwayLine.setPutawayConfirmedQty(newPutAwayLine.getPutawayConfirmedQty());
                dbPutAwayLine.setConfirmedStorageBin(newPutAwayLine.getConfirmedStorageBin());
                dbPutAwayLine.setStatusId(20L);
                String statusDescription = stagingLineV2Repository.getStatusDescription(20L,
                        newPutAwayLine.getLanguageId());
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setPackBarcodes(newPutAwayLine.getPackBarcodes());
                dbPutAwayLine.setBarcodeId(newPutAwayLine.getBarcodeId());
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                log.info("putawayHeader: " + findPutawayHeader);
                if (findPutawayHeader != null) {
                    dbPutAwayLine.setCreatedOn(findPutawayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(findPutawayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(findPutawayHeader.getInboundOrderTypeId());
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository
                        .findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                                newPutAwayLine.getLanguageId(), newPutAwayLine.getCompanyCode(),
                                newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId(),
                                newPutAwayLine.getGoodsReceiptNo(), newPutAwayLine.getPreInboundNo(),
                                newPutAwayLine.getRefDocNumber(), newPutAwayLine.getPutAwayNumber(),
                                newPutAwayLine.getLineNo(), newPutAwayLine.getItemCode(),
                                newPutAwayLine.getProposedStorageBin(),
                                Arrays.asList(newPutAwayLine.getConfirmedStorageBin()),
                                newPutAwayLine.getDeletionIndicator());

                log.info("Existing putawayline already created : " + existingPutAwayLine);

                if (existingPutAwayLine.isEmpty()) {

                    try {
                        String leadTime = putAwayLineV2Repository.getleadtime(companyCode, plantId, languageId,
                                warehouseId, newPutAwayLine.getPutAwayNumber(), new Date());
                        dbPutAwayLine.setReferenceField1(leadTime);
                        log.info("LeadTime: " + leadTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);

                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);

//                    boolean isInventoryCreated = false;
//                    boolean isInventoryMovemoentCreated = false;

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        /*
                         * Commenting out Inventory creation alone
                         */
//                        InventoryV2 inventory = new InventoryV2();
//                        BeanUtils.copyProperties(createdPutAwayLine, inventory, CommonUtils.getNullPropertyNames(createdPutAwayLine));
//                        inventory.setCompanyCodeId(createdPutAwayLine.getCompanyCode());
//                        inventory.setVariantCode(1L);                // VAR_ID
//                        inventory.setVariantSubCode("1");            // VAR_SUB_ID
//                        inventory.setStorageMethod("1");            // STR_MTD
//                        inventory.setBatchSerialNumber("1");        // STR_NO
//                        inventory.setBatchSerialNumber(newPutAwayLine.getBatchSerialNumber());
//                        inventory.setStorageBin(createdPutAwayLine.getConfirmedStorageBin());
//                        inventory.setBarcodeId(createdPutAwayLine.getBarcodeId());

                        // v2 code
//                        if (createdPutAwayLine.getCbm() == null) {
//                            createdPutAwayLine.setCbm("0");
//                        }
//                        if (createdPutAwayLine.getCbmQuantity() == null) {
//                            createdPutAwayLine.setCbmQuantity(0D);
//                        }
//                        inventory.setCbmPerQuantity(String.valueOf(Double.valueOf(createdPutAwayLine.getCbm()) / createdPutAwayLine.getCbmQuantity()));
//                        inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));

//                        Double invQty = 0D;
//                        Double cbmPerQty = 0D;
//                        Double invCbm = 0D;
//
//                        log.info("CapacityCheck for Create Inventory-----------> : " + capacityCheck);

//                        if (capacityCheck) {
//                            if (createdPutAwayLine.getCbmQuantity() != null) {
//                                inventory.setCbmPerQuantity(String.valueOf(createdPutAwayLine.getCbmQuantity()));
//                            }
//                            if (createdPutAwayLine.getPutawayConfirmedQty() != null) {
//                                invQty = createdPutAwayLine.getPutawayConfirmedQty();
//                            }
//                            if (createdPutAwayLine.getCbmQuantity() == null) {
//
//                                if (createdPutAwayLine.getCbm() != null) {
//                                    cbm = Double.valueOf(createdPutAwayLine.getCbm());
//                                }
//                                cbmPerQty = cbm / invQty;
//                                inventory.setCbmPerQuantity(String.valueOf(cbmPerQty));
//                            }
//                            if (createdPutAwayLine.getCbm() != null) {
//                                invCbm = Double.valueOf(createdPutAwayLine.getCbm());
//                            }
//                            if (createdPutAwayLine.getCbm() == null) {
//                                invCbm = invQty * Double.valueOf(inventory.getCbmPerQuantity());
//                            }
//                            inventory.setCbm(String.valueOf(invCbm));
//                        }

                        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
                        storageBinPutAway.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
                        storageBinPutAway.setPlantId(dbPutAwayLine.getPlantId());
                        storageBinPutAway.setLanguageId(dbPutAwayLine.getLanguageId());
                        storageBinPutAway.setWarehouseId(dbPutAwayLine.getWarehouseId());
                        storageBinPutAway.setBin(dbPutAwayLine.getConfirmedStorageBin());

                        StorageBinV2 dbStorageBin = null;
                        try {
                            dbStorageBin = mastersService.getaStorageBinV2(storageBinPutAway,
                                    authTokenForMastersService.getAccess_token());
                        } catch (Exception e) {
                            throw new BadRequestException("Invalid StorageBin");
                        }

//                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository.findByItemCode(inventory.getItemCode());
//                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
//                            inventory.setReferenceField8(imbasicdata1.get(0).getDescription());
//                            inventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
//                        }
//                        if (itemCodeCapacityCheck != null) {
//                            inventory.setReferenceField8(itemCodeCapacityCheck.getDescription());
//                            inventory.setReferenceField9(itemCodeCapacityCheck.getManufacturerPartNo());
//                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        }

//                        if (dbStorageBin != null) {
//                            inventory.setBinClassId(dbStorageBin.getBinClassId());
//                            inventory.setReferenceField10(dbStorageBin.getStorageSectionId());
//                            inventory.setReferenceField5(dbStorageBin.getAisleNumber());
//                            inventory.setReferenceField6(dbStorageBin.getShelfId());
//                            inventory.setReferenceField7(dbStorageBin.getRowId());
//                            inventory.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
//                        }

//                        inventory.setCompanyDescription(dbPutAwayLine.getCompanyDescription());
//                        inventory.setPlantDescription(dbPutAwayLine.getPlantDescription());
//                        inventory.setWarehouseDescription(dbPutAwayLine.getWarehouseDescription());

                        /*
                         * Insert PA_CNF_QTY value in this field. Also Pass
                         * WH_ID/PACK_BARCODE/ITM_CODE/BIN_CL_ID=3 in INVENTORY table and fetch
                         * ST_BIN/INV_QTY value. Update INV_QTY value by (INV_QTY - PA_CNF_QTY) . If
                         * this value becomes Zero, then delete the record"
                         */
//                        try {
//                            InventoryV2 existinginventory = inventoryService.getInventory(
//                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
//                                    createdPutAwayLine.getWarehouseId(),
//                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
//                                    createdPutAwayLine.getManufacturerName(), 3L);
//
////                            IInventoryImpl existinginventory = inventoryService.getInventoryforExistingBin(
////                                    createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
////                                    createdPutAwayLine.getWarehouseId(),
////                                    createdPutAwayLine.getPackBarcodes(), createdPutAwayLine.getItemCode(),
////                                    createdPutAwayLine.getManufacturerName(), 3L);
//
//                            double INV_QTY = existinginventory.getInventoryQuantity() - createdPutAwayLine.getPutawayConfirmedQty();
//                            log.info("INV_QTY : " + INV_QTY);
//
////                            inventory.setPalletCode(existinginventory.getPalletCode());
////                            inventory.setCaseCode(existinginventory.getCaseCode());
////                            inventory.setDescription(itemCodeCapacityCheck.getDescription());
//
////                            if (capacityCheck) {
////                                if (existinginventory.getCbm() != null && createdPutAwayLine.getCbm() != null) {
////                                    invCbm = Double.valueOf(existinginventory.getCbm()) - Double.valueOf(createdPutAwayLine.getCbm());
////                                    log.info("INV_CBM: " + invCbm);
////                                }
////                                if (invCbm >= 0) {
////                                    existinginventory.setCbm(String.valueOf(invCbm));
////                                }
////                            }
//                            if (INV_QTY >= 0) {
////                                existinginventory.setInventoryQuantity(INV_QTY);
////                                InventoryV2 updatedInventory = inventoryV2Repository.save(existinginventory);
////                                log.info("updatedInventory--------> : " + updatedInventory);
//                                InventoryV2 inventory2 = new InventoryV2();
//                                BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
//                                stockTypeDesc = getStockTypeDesc(createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(), existinginventory.getStockTypeId());
//                                inventory2.setStockTypeDescription(stockTypeDesc);
//                                inventory2.setInventoryQuantity(INV_QTY);
//                                InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2);
//                                log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
//                            }
//
//                        } catch (Exception e) {
//                            log.info("Existing Inventory---Error-----> : " + e.toString());
//                        }

                        // INV_QTY
//                        inventory.setInventoryQuantity(createdPutAwayLine.getPutawayConfirmedQty());

//                        inventory.setStockTypeId(10L);
//                        stockTypeDesc = getStockTypeDesc(dbPutAwayLine.getCompanyCode(),dbPutAwayLine.getPlantId(),dbPutAwayLine.getLanguageId(),dbPutAwayLine.getWarehouseId(),10L);
//                        inventory.setStockTypeDescription(stockTypeDesc);

                        // INV_UOM
//                        inventory.setInventoryUom(createdPutAwayLine.getPutAwayUom());
//                        inventory.setReferenceDocumentNo(createdPutAwayLine.getRefDocNumber());
//                        inventory.setDescription(itemCodeCapacityCheck.getDescription());
//                        inventory.setCreatedBy(createdPutAwayLine.getCreatedBy());
//                        inventory.setCreatedOn(createdPutAwayLine.getCreatedOn());
                        // InventoryV2 createdInventory = inventoryV2Repository.save(inventory);
//                        log.info("createdInventory : " + createdInventory);
//                        log.info("createdInventory BinClassId : " + createdInventory.getBinClassId());
//
//                        if (createdInventory != null) {
//                            isInventoryCreated = true;
//                        }

                        /* Insert a record into INVENTORYMOVEMENT table */
//                        InventoryMovement createdInventoryMovement = createInventoryMovementV2(createdPutAwayLine);
//                        log.info("inventoryMovement created: " + createdInventoryMovement);
//                        log.info("inventoryMovement created binClassId: " + createdInventory.getBinClassId());

//                        if (createdInventoryMovement != null) {
//                            isInventoryMovemoentCreated = true;
//                        }

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
                                dbPutAwayLine.getCompanyCode(), dbPutAwayLine.getPlantId(),
                                dbPutAwayLine.getLanguageId(), dbPutAwayLine.getWarehouseId(), loginUserID,
                                authTokenForMastersService.getAccess_token());

//                        if (isInventoryCreated && isInventoryMovemoentCreated) {
//                        if (isInventoryMovemoentCreated) {
                        PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.getPutAwayHeaderV2ForPutAwayLine(
                                createdPutAwayLine.getWarehouseId(), createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPutAwayNumber(),
                                createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId());

                        confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                        proposedStorageBin = putAwayHeader.getProposedStorageBin();
                        if (putAwayHeader != null) {
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty()
                                    + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            log.info("PutawayHeader StatusId : 20");
                            statusDescription = stagingLineV2Repository.getStatusDescription(
                                    putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated: " + putAwayHeader);

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
//                                List<PutAwayLineV2> filteredlist = newPutAwayLines
//                                        .stream()
//                                        .filter(a ->
//                                                a.getPutAwayNumber().equalsIgnoreCase(createdPutAwayLine.getPutAwayNumber()) &&
//                                                a.getRefDocNumber().equalsIgnoreCase(createdPutAwayLine.getRefDocNumber()) &&
//                                                a.getPreInboundNo().equalsIgnoreCase(createdPutAwayLine.getPreInboundNo()) &&
//                                                a.getItemCode().equalsIgnoreCase(createdPutAwayLine.getItemCode()) &&
//                                                a.getManufacturerName().equalsIgnoreCase(createdPutAwayLine.getManufacturerName()) &&
//                                                a.getCompanyCode().equalsIgnoreCase(createdPutAwayLine.getCompanyCode()) &&
//                                                a.getPlantId().equalsIgnoreCase(createdPutAwayLine.getPlantId()) &&
//                                                a.getLanguageId().equalsIgnoreCase(createdPutAwayLine.getLanguageId()) &&
//                                                a.getWarehouseId().equalsIgnoreCase(createdPutAwayLine.getWarehouseId()) &&
//                                                a.getLineNo().equals(createdPutAwayLine.getLineNo()) &&
//                                                a.getConfirmedStorageBin().equalsIgnoreCase(createdPutAwayLine.getConfirmedStorageBin()))
//                                        .collect(Collectors.toList());
//                                log.info("PutawayLine filtered List: " + filteredlist);
//                                if (filteredlist != null && !filteredlist.isEmpty()) {
//                                    Double putawayQty = filteredlist.stream().mapToDouble(a -> a.getPutawayConfirmedQty()).sum();
//                                    Double dbPutawayQty = 0D;
//                                    if(putAwayHeader.getReferenceField3() != null) {
//                                        dbPutawayQty = Double.valueOf(putAwayHeader.getReferenceField3());
//                                    }
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(
                                        createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                        createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                        createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                        createdPutAwayLine.getItemCode(), createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info(
                                        "tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                                + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                                + ", " + putAwayHeader.getPutAwayQuantity() + ", "
                                                + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }

                                // dbPutawayQty = SumOfPALineQty, dbAssignedPutawayQty (current conf. qty) =
                                // Ref.Field.2
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200")
                                            || putAwayHeader.getWarehouseId().equalsIgnoreCase("100"))
                                            && proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//                                        if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                        log.info("New PutawayHeader Creation: ");
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader,
                                                CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId,
                                                languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber); // PutAway Number

                                        newPutAwayHeader
                                                .setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(
                                                    String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }
                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//                                    Double PUTAWAY_QTY = (putAwayHeader.getPutAwayQuantity() != null ? putAwayHeader.getPutAwayQuantity() : 0) - (createdPutAwayLine.getPutawayConfirmedQty() != null ? createdPutAwayLine.getPutawayConfirmedQty() : 0);
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");

                                        statusDescription = stagingLineV2Repository.getStatusDescription(
                                                newPutAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);
                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("1.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                    if ((putAwayHeader.getWarehouseId().equalsIgnoreCase("200")
                                            || putAwayHeader.getWarehouseId().equalsIgnoreCase("100"))
                                            && !proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//                                        if (!proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {

                                        // create new putaway header when partial putaway done and confirmed storage
                                        // bin set as proposed bin for new putaway header
                                        PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                        BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader,
                                                CommonUtils.getNullPropertyNames(putAwayHeader));

                                        // PA_NO
                                        long NUM_RAN_CODE = 7;
                                        String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId,
                                                languageId, warehouseId, authTokenForIDMasterService.getAccess_token());
                                        newPutAwayHeader.setPutAwayNumber(nextPANumber);
                                        newPutAwayHeader.setProposedStorageBin(confirmedStorageBin);

                                        newPutAwayHeader
                                                .setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        if (putAwayHeader.getReferenceField4() == null) {
                                            newPutAwayHeader.setReferenceField2(
                                                    String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                            newPutAwayHeader.setReferenceField4("1");
                                        }

                                        Double putawaycnfQty = 0D;
                                        if (newPutAwayHeader.getReferenceField3() != null) {
                                            putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                        }
                                        putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                        newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//											Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;

                                        log.info("---------PUTAWAY_QTY---------1---------> : "
                                                + createdPutAwayLine.getPutawayConfirmedQty());
                                        Double PUTAWAY_QTY = dbAssignedPutawayQty
                                                - createdPutAwayLine.getPutawayConfirmedQty();
                                        log.info("---------PUTAWAY_QTY---------2---------> : " + PUTAWAY_QTY);

                                        if (PUTAWAY_QTY < 0) {
                                            throw new BadRequestException("total confirm qty greater than putaway qty");
                                        }
                                        newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                        newPutAwayHeader.setStatusId(19L);
                                        log.info("PutawayHeader StatusId : 19");
                                        statusDescription = stagingLineV2Repository.getStatusDescription(
                                                putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
                                        newPutAwayHeader.setStatusDescription(statusDescription);

                                        newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                        log.info("2.putAwayHeader created: " + newPutAwayHeader);
                                    }
                                }
//                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE
                        // table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(
                                createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                                createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                                createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());
                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null
                                    && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedAcceptQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Accept qty cannot be greater than order qty");
                            }
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null
                                    && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty()
                                        + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) { // condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setStatusId(20L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(20L,
                                createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId,
                    refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);
            return createdPutAwayLines;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     *
     * @param newPutAwayLines putAwayLines
     * @param loginUserID userID
     * @return
     */
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV4(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID) {
        ExecutorService asyncExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<CompletableFuture<PutAwayLineV2>> futures = newPutAwayLines.stream().map(putAwayLineV2 -> CompletableFuture.supplyAsync(() -> {
            try {
                return createPutAwayLineProcessV4(putAwayLineV2, loginUserID);
            } catch (Exception e) {
                log.error("Error processing GRLine: {}", putAwayLineV2.getLineNo(), e);
                return null; // or throw RuntimeException
            }
        }, asyncExecutor)).collect(Collectors.toList());

        List<PutAwayLineV2> createdPutAwayLines = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        log.info("PutAwayLine Value size is {}", createdPutAwayLines.size());
        if (!createdPutAwayLines.isEmpty()) {
            putAwayLineV2Repository.saveAll(createdPutAwayLines);
        } else {
            throw new BadRequestException("PutAwayLine List is Empty  ------------------> ");
        }

        PutAwayLineV2 putAwayLine = createdPutAwayLines.get(0);
        putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(),
                putAwayLine.getWarehouseId(), putAwayLine.getRefDocNumber(), putAwayLine.getPreInboundNo());
        log.info("InboundHeader received lines count updated: " + putAwayLine.getRefDocNumber());

        log.info("Inventory Async Process Started -------------------> ");
        inventoryAsyncProcessService.createInventoryAsyncProcessV4(createdPutAwayLines, loginUserID);

        inboundConfirmValidation(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(),
                putAwayLine.getRefDocNumber(), putAwayLine.getPreInboundNo(), loginUserID);
        return createdPutAwayLines;

    }


    /**
     * @param newPutAwayLine putAwayLine
     * @param loginUserID userID
     * @return
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PutAwayLineV2 createPutAwayLineProcessV4(@Valid PutAwayLineV2 newPutAwayLine, String loginUserID) {
//        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLine);

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("NAMRATHA");
        try {
            if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
            }

            PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
            log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

            StorageBinV2 dbStorageBin = null;
            try {
                dbStorageBin = storageBinService.getStorageBinV2(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(),
                        newPutAwayLine.getLanguageId(), newPutAwayLine.getWarehouseId(), newPutAwayLine.getConfirmedStorageBin());
            } catch (Exception e) {
                throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
            }

            if (dbStorageBin != null) {
                dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
            }
            BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));
            dbPutAwayLine.setStatusId(20L);
            statusDescription = getStatusDescription(20L, newPutAwayLine.getLanguageId());
            dbPutAwayLine.setStatusDescription(statusDescription);
            dbPutAwayLine.setDeletionIndicator(0L);
            dbPutAwayLine.setCreatedBy(loginUserID);
            dbPutAwayLine.setUpdatedBy(loginUserID);
            dbPutAwayLine.setConfirmedBy(loginUserID);

            PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.fetchPutawayHeaderByItemV2(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId(),
                    newPutAwayLine.getItemCode(), newPutAwayLine.getManufacturerName(), String.valueOf(newPutAwayLine.getLineNo()), newPutAwayLine.getLanguageId(), newPutAwayLine.getPutAwayNumber());
            log.info("putawayHeader: " + putAwayHeader);

            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getLanguageId(),
                    newPutAwayLine.getWarehouseId(), newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(), newPutAwayLine.getLineNo(), newPutAwayLine.getItemCode(),
                    newPutAwayLine.getManufacturerName());

            Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                    newPutAwayLine.getLanguageId(), newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId(), newPutAwayLine.getGoodsReceiptNo(), newPutAwayLine.getPreInboundNo(), newPutAwayLine.getRefDocNumber(),
                    newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(), newPutAwayLine.getItemCode(), newPutAwayLine.getProposedStorageBin(),
                    Arrays.asList(newPutAwayLine.getConfirmedStorageBin()), 0L);
            log.info("Existing putawayline already created : " + existingPutAwayLine);

            if (dbStagingLineEntity != null) {
                log.info("StagingLine");
                dbPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                dbPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                dbPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                dbPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                dbPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                dbPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                dbPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                dbPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                dbPutAwayLine.setMrp(dbStagingLineEntity.getMrp());
                dbPutAwayLine.setItemType(dbStagingLineEntity.getItemType());
                dbPutAwayLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                dbPutAwayLine.setSize(dbStagingLineEntity.getSize());
                dbPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
                dbPutAwayLine.setBagSize(dbStagingLineEntity.getBagSize());
            }

            double actualInventoryQty = dbPutAwayLine.getPutawayConfirmedQty();
            dbPutAwayLine.setActualInventoryQty(actualInventoryQty);
            dbPutAwayLine.setCreatedOn(new Date());
            dbPutAwayLine.setUpdatedOn(new Date());
            dbPutAwayLine.setConfirmedOn(new Date());

            if (existingPutAwayLine.isEmpty()) {
                //Lead Time calculation
                log.info("---------->NewPutAwayLine created: " + dbPutAwayLine);
                try {
                    fireBaseNotification(dbPutAwayLine, loginUserID);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("FireBaseNotification Error in PutAwayLine " + e.getMessage());
                }
                if (dbPutAwayLine.getPutawayConfirmedQty() > 0L) {

                    storageBinV2Repository.updateEmptyBinStatus(dbPutAwayLine.getConfirmedStorageBin(), newPutAwayLine.getCompanyCode(),
                            newPutAwayLine.getPlantId(), newPutAwayLine.getWarehouseId(), 1L);
                    log.info("StorageBin status Updated Successfully");
                    if (putAwayHeader != null) {
                        if (newPutAwayLine.getManufacturerDate() == null) {
                            dbPutAwayLine.setManufacturerDate(putAwayHeader.getManufacturerDate());
                        }
                        if (newPutAwayLine.getExpiryDate() == null) {
                            dbPutAwayLine.setExpiryDate(putAwayHeader.getExpiryDate());
                        }
                        log.info("putawayConfirmQty, putawayQty: " + dbPutAwayLine.getPutawayConfirmedQty() + ", " + putAwayHeader.getPutAwayQuantity());
                        putAwayHeaderV2Repository.updatePutAwayHeaderV4(putAwayHeader.getWarehouseId(), putAwayHeader.getCompanyCodeId(),
                                putAwayHeader.getPlantId(), putAwayHeader.getLanguageId(), putAwayHeader.getRefDocNumber(), 20L, statusDescription, putAwayHeader.getPackBarcodes());
                        log.info("putAwayHeader updated----> StatusId : " + putAwayHeader + " ---->----> " + putAwayHeader.getStatusId());
                    }

                    /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                    double addedAcceptQty = 0.0;
                    InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(),
                            newPutAwayLine.getLanguageId(), newPutAwayLine.getWarehouseId(), dbPutAwayLine.getRefDocNumber(),
                            dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode());
                    log.info("inboundLine----from--DB---------> " + inboundLine);

                    // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                    if (dbPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                        if (inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                            addedAcceptQty = inboundLine.getAcceptedQty() + dbPutAwayLine.getPutawayConfirmedQty();
                        } else {
                            addedAcceptQty = dbPutAwayLine.getPutawayConfirmedQty();
                        }
                        if (addedAcceptQty > inboundLine.getOrderQty()) {
                            throw new BadRequestException("Accept qty cannot be greater than order qty");
                        }
                        double actualAcceptQty = addedAcceptQty;
                        inboundLine.setActualAcceptedQty(actualAcceptQty);
                        inboundLine.setAcceptedQty(addedAcceptQty);
                        inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                    }
                    statusDescription = getStatusDescription(20L, newPutAwayLine.getLanguageId());
                    inboundLineV2Repository.updateInboundLineStatusAndQuantity(newPutAwayLine.getCompanyCode(), newPutAwayLine.getPlantId(),
                            newPutAwayLine.getLanguageId(), newPutAwayLine.getWarehouseId(), dbPutAwayLine.getRefDocNumber(),
                            dbPutAwayLine.getPreInboundNo(), dbPutAwayLine.getLineNo(), dbPutAwayLine.getItemCode(), 20L, statusDescription,
                            inboundLine.getActualAcceptedQty(), inboundLine.getAcceptedQty(), inboundLine.getVarianceQty());
                    log.info("inboundLine updated : " + inboundLine);
                }
            } else {
                log.info("Putaway Line already exist : " + existingPutAwayLine);
            }
            return dbPutAwayLine;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("PutawayLine Create Exception");
        }
    }



    /**
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV7(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID) {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;
        String manufactureName = null;
        String lineReferenceNo = null;

        try {
            for (PutAwayLineV2 newPutAwayLine : newPutAwayLines) {
                if (newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();

                itemCode = newPutAwayLine.getItemCode();
                companyCode = newPutAwayLine.getCompanyCode();
                plantId = newPutAwayLine.getPlantId();
                languageId = newPutAwayLine.getLanguageId();
                warehouseId = newPutAwayLine.getWarehouseId();
                refDocNumber = newPutAwayLine.getRefDocNumber();
                preInboundNo = newPutAwayLine.getPreInboundNo();
                manufactureName = newPutAwayLine.getManufacturerName();
                lineReferenceNo = String.valueOf(newPutAwayLine.getLineNo());

                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 dbStorageBin = null;
                try {
                    dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
                }

                PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.fetchPutawayHeaderByItemV2(companyCode, plantId, warehouseId, itemCode, manufactureName, lineReferenceNo, languageId, newPutAwayLine.getPutAwayNumber());
                log.info("putawayHeader: " + putAwayHeader);

                if (dbStorageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                }

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                        newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());
                log.info("StagingLine: " + dbStagingLineEntity);
                if (dbStagingLineEntity != null) {
                    newPutAwayLine.setManufacturerFullName(dbStagingLineEntity.getManufacturerFullName());
                    newPutAwayLine.setMiddlewareId(dbStagingLineEntity.getMiddlewareId());
                    newPutAwayLine.setMiddlewareHeaderId(dbStagingLineEntity.getMiddlewareHeaderId());
                    newPutAwayLine.setMiddlewareTable(dbStagingLineEntity.getMiddlewareTable());
                    newPutAwayLine.setPurchaseOrderNumber(dbStagingLineEntity.getPurchaseOrderNumber());
                    newPutAwayLine.setReferenceDocumentType(dbStagingLineEntity.getReferenceDocumentType());
                    newPutAwayLine.setPutAwayUom(dbStagingLineEntity.getOrderUom());
                    newPutAwayLine.setDescription(dbStagingLineEntity.getItemDescription());
                    newPutAwayLine.setCompanyDescription(dbStagingLineEntity.getCompanyDescription());
                    newPutAwayLine.setPlantDescription(dbStagingLineEntity.getPlantDescription());
                    newPutAwayLine.setWarehouseDescription(dbStagingLineEntity.getWarehouseDescription());
                    newPutAwayLine.setMrp(dbStagingLineEntity.getMrp());
                    newPutAwayLine.setItemType(dbStagingLineEntity.getItemType());
                    newPutAwayLine.setItemGroup(dbStagingLineEntity.getItemGroup());
                    newPutAwayLine.setSize(dbStagingLineEntity.getSize());
                    newPutAwayLine.setBrand(dbStagingLineEntity.getBrand());
                    newPutAwayLine.setBagSize(dbStagingLineEntity.getBagSize());
                }

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

                dbPutAwayLine.setStatusId(20L);
                statusDescription = getStatusDescription(20L, languageId);
                dbPutAwayLine.setStatusDescription(statusDescription);
                dbPutAwayLine.setDeletionIndicator(0L);
                dbPutAwayLine.setCreatedBy(loginUserID);
                dbPutAwayLine.setUpdatedBy(loginUserID);
                dbPutAwayLine.setConfirmedBy(loginUserID);

                if (putAwayHeader != null) {
                    dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(putAwayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
                    if (dbPutAwayLine.getBagSize() == null || dbPutAwayLine.getNoBags() == null) {
                        dbPutAwayLine.setNoBags(putAwayHeader.getNoBags());
                        dbPutAwayLine.setBagSize(putAwayHeader.getBagSize());
                        dbPutAwayLine.setAlternateUom(putAwayHeader.getAlternateUom());
                    }
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                Optional<PutAwayLineV2> existingPutAwayLine = putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndGoodsReceiptNoAndPreInboundNoAndRefDocNumberAndPutAwayNumberAndLineNoAndItemCodeAndProposedStorageBinAndConfirmedStorageBinInAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, newPutAwayLine.getGoodsReceiptNo(), preInboundNo, refDocNumber,
                        newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getProposedStorageBin(),
                        Arrays.asList(newPutAwayLine.getConfirmedStorageBin()), 0L);
                log.info("Existing putawayline already created : " + existingPutAwayLine);

//                double actualInventoryQty = getQuantity(dbPutAwayLine.getPutawayConfirmedQty(), dbPutAwayLine.getBagSize());
                double actualInventoryQty = dbPutAwayLine.getPutawayConfirmedQty();
                dbPutAwayLine.setActualInventoryQty(actualInventoryQty);

                if (existingPutAwayLine.isEmpty()) {
                    //Lead Time calculation
                    String leadTime = putAwayLineV2Repository.getleadtimeV2(new Date(), putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);

                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                    log.info("---------->NewPutAwayLine created: " + createdPutAwayLine);
//                    fireBaseNotification(createdPutAwayLine, loginUserID);

                    createdPutAwayLines.add(createdPutAwayLine);

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {

                        dbStorageBin.setStatusId(1L);
                        storageBinService.updateStorageBinV7(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin, companyCode, plantId, languageId, warehouseId, loginUserID);

                        if (putAwayHeader != null) {
                            String confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                            String proposedStorageBin = putAwayHeader.getProposedStorageBin();
                            log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty() + ", " + putAwayHeader.getPutAwayQuantity());

                            putAwayHeader.setStatusId(20L);
                            putAwayHeader.setStatusDescription(statusDescription);
                            putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                            log.info("putAwayHeader updated----> StatusId : " + putAwayHeader + " ---->----> " + putAwayHeader.getStatusId());

                            if (createdPutAwayLine.getPutawayConfirmedQty() < putAwayHeader.getPutAwayQuantity()) {
                                Double dbAssignedPutawayQty = 0D;
                                if (putAwayHeader.getReferenceField2() != null) {
                                    dbAssignedPutawayQty = Double.valueOf(putAwayHeader.getReferenceField2());
                                }
                                if (putAwayHeader.getReferenceField2() == null) {
                                    dbAssignedPutawayQty = putAwayHeader.getPutAwayQuantity();
                                }
                                Double dbPutawayQty = putAwayLineV2Repository.getPutawayCnfQuantity(companyCode, plantId, languageId, warehouseId,
                                        refDocNumber, preInboundNo, itemCode,
                                        createdPutAwayLine.getManufacturerName(),
                                        createdPutAwayLine.getLineNo());
                                if (dbPutawayQty == null) {
                                    dbPutawayQty = 0D;
                                }

                                log.info("NON CBM ---> tot_pa_cnf_qty,created_pa_line_cnf_qty,partial_pa_header_pa_qty,pa_header_pa_qty,RF2 : "
                                        + dbPutawayQty + ", " + createdPutAwayLine.getPutawayConfirmedQty()
                                        + ", " + putAwayHeader.getPutAwayQuantity() + ", " + putAwayHeader.getReferenceField2());
                                if (dbPutawayQty > dbAssignedPutawayQty) {
                                    throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
//                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                    log.info("New PutawayHeader Create Initiated---> ");
                                    PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                    BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                    // PA_NO
                                    long NUM_RAN_CODE = 7;
                                    String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId);
                                    newPutAwayHeader.setPutAwayNumber(nextPANumber);                           //PutAway Number

                                    newPutAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                    if (putAwayHeader.getReferenceField4() == null) {
                                        newPutAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
                                        newPutAwayHeader.setReferenceField4("1");
                                    }
                                    Double putawaycnfQty = 0D;
                                    if (newPutAwayHeader.getReferenceField3() != null) {
                                        putawaycnfQty = Double.valueOf(newPutAwayHeader.getReferenceField3());
                                    }
                                    putawaycnfQty = putawaycnfQty + createdPutAwayLine.getPutawayConfirmedQty();
                                    newPutAwayHeader.setReferenceField3(String.valueOf(putawaycnfQty));

//                                    Double PUTAWAY_QTY = (putAwayHeader.getPutAwayQuantity() != null ? putAwayHeader.getPutAwayQuantity() : 0) - (createdPutAwayLine.getPutawayConfirmedQty() != null ? createdPutAwayLine.getPutawayConfirmedQty() : 0);
                                    Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                    if (PUTAWAY_QTY < 0) {
                                        throw new BadRequestException("total confirm qty greater than putaway qty");
                                    }
                                    newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                    log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
                                    newPutAwayHeader.setStatusId(19L);
                                    log.info("PutawayHeader StatusId : 19");
                                    statusDescription = getStatusDescription(newPutAwayHeader.getStatusId(), languageId);
                                    newPutAwayHeader.setStatusDescription(statusDescription);
                                    newPutAwayHeader = putAwayHeaderV2Repository.save(newPutAwayHeader);
                                    log.info("putAwayHeader created: " + newPutAwayHeader);
//                                    }
//                                    if (!proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
//
//                                        putAwayHeader.setReferenceField1(String.valueOf(putAwayHeader.getPutAwayQuantity()));
//                                        if (putAwayHeader.getReferenceField4() == null) {
//                                            putAwayHeader.setReferenceField2(String.valueOf(putAwayHeader.getPutAwayQuantity()));
//                                            putAwayHeader.setReferenceField4("1");
//                                        }
//                                        Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
//                                        if (PUTAWAY_QTY < 0) {
//                                            throw new BadRequestException("total confirm qty greater than putaway qty");
//                                        }
//                                        putAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
//                                        log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
//                                        putAwayHeader.setStatusId(19L);
//                                        log.info("PutawayHeader StatusId : 19");
//                                        statusDescription = getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
//                                        putAwayHeader.setStatusDescription(statusDescription);
//                                        putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
//                                        log.info("putAwayHeader updated: " + putAwayHeader);
//                                    }
                                }
                            }
                        }

                        /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                        // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE table and
                        // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
                        double addedAcceptQty = 0.0;
                        double addedDamageQty = 0.0;

                        InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(companyCode, plantId, languageId, warehouseId,
                                createdPutAwayLine.getRefDocNumber(),
                                createdPutAwayLine.getPreInboundNo(),
                                createdPutAwayLine.getLineNo(),
                                createdPutAwayLine.getItemCode());
                        log.info("inboundLine----from--DB---------> " + inboundLine);

                        // If QTY_TYPE = A, add PA_CNF_QTY with existing value in ACCEPT_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("A")) {
                            if (inboundLine.getAcceptedQty() != null && inboundLine.getAcceptedQty() < inboundLine.getOrderQty()) {
                                addedAcceptQty = inboundLine.getAcceptedQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedAcceptQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedAcceptQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Accept qty cannot be greater than order qty");
                            }
//                            double actualAcceptQty = getQuantity(addedAcceptQty, createdPutAwayLine.getBagSize());
                            double actualAcceptQty = addedAcceptQty;
                            inboundLine.setActualAcceptedQty(actualAcceptQty);
                            inboundLine.setAcceptedQty(addedAcceptQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedAcceptQty);
                        }

                        // if QTY_TYPE = D, add PA_CNF_QTY with existing value in DAMAGE_QTY field
                        if (createdPutAwayLine.getQuantityType().equalsIgnoreCase("D")) {
                            if (inboundLine.getDamageQty() != null && inboundLine.getDamageQty() < inboundLine.getOrderQty()) {
                                addedDamageQty = inboundLine.getDamageQty() + createdPutAwayLine.getPutawayConfirmedQty();
                            } else {
                                addedDamageQty = createdPutAwayLine.getPutawayConfirmedQty();
                            }
                            if (addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
//                            double actualDamageQty = getQuantity(addedDamageQty, createdPutAwayLine.getBagSize());
                            double actualDamageQty = addedDamageQty;
                            inboundLine.setActualDamageQty(actualDamageQty);
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if (inboundLine.getInboundOrderTypeId() == 5L) {          //condition added for final Inbound confirm
                            inboundLine.setReferenceField2("true");
                        }

                        inboundLine.setStatusId(20L);
                        statusDescription = getStatusDescription(20L, languageId);
                        inboundLine.setStatusDescription(statusDescription);
                        inboundLine = inboundLineV2Repository.save(inboundLine);
                        log.info("inboundLine updated : " + inboundLine);
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo);
            log.info("InboundHeader received lines count updated: " + refDocNumber);

            if (createdPutAwayLines != null && !createdPutAwayLines.isEmpty()) {
                for (PutAwayLineV2 putAwayLine : createdPutAwayLines) {
                    createInventoryNonCBMV7(companyCode, plantId, languageId, warehouseId, putAwayLine.getItemCode(),
                            putAwayLine.getManufacturerName(), refDocNumber, putAwayLine, loginUserID);
                }
                log.info("Inventory Created Successfully -----> for All Putaway Lines");
            }

//             inboundConfirmValidation(companyCode, plantId, languageId, warehouseId, refDocNumber, preInboundNo, loginUserID);
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("PutawayLine Create Exception");
        }
    }

    /**
     * @param putAwayLine
     */
    private void fireBaseNotification(PutAwayLineV2 putAwayLine, String loginUserID) {
        try {
//            try {
//                DataBaseContextHolder.setCurrentDb("MT");
//                String profile = dbConfigRepository.getDbName(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getWarehouseId());
//                log.info("ROUTING DB FETCH FROM DB CONFIG TABLE --> {}", profile);
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb(profile);

            log.info("Notification Input ----> | " + putAwayLine.getCompanyCode() + " | " + putAwayLine.getPlantId() + " | " + putAwayLine.getLanguageId() + " | " + putAwayLine.getWarehouseId());
            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId(), loginUserID);
            log.info("deviceToken ------> {}", deviceToken);
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String title = "PutawayLine Create";
                String message = "A new PutAwayLine- " + putAwayLine.getConfirmedStorageBin() + " has been created on ";

                NotificationSave notificationInput = new NotificationSave();
                notificationInput.setUserId(Collections.singletonList(loginUserID));
                notificationInput.setUserType(null);
                notificationInput.setMessage(message);
                notificationInput.setTopic(title);
                notificationInput.setReferenceNumber(putAwayLine.getRefDocNumber());
                notificationInput.setDocumentNumber(putAwayLine.getPreInboundNo());
                notificationInput.setCompanyCodeId(putAwayLine.getCompanyCode());
                notificationInput.setPlantId(putAwayLine.getPlantId());
                notificationInput.setLanguageId(putAwayLine.getLanguageId());
                notificationInput.setWarehouseId(putAwayLine.getWarehouseId());
                notificationInput.setCreatedOn(putAwayLine.getCreatedOn());
                notificationInput.setCreatedBy(loginUserID);
                notificationInput.setStorageBin(putAwayLine.getConfirmedStorageBin());

                log.info("pushNotification started");
                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
                log.info("pushNotification completed");
            }
//            } finally {
//                DataBaseContextHolder.clear();
//            }
        } catch (Exception e) {
            log.error("Inbound firebase notification error", e); // This logs the full stack trace
        }
    }

    /**
     * @param remainingVolume
     * @param occupiedVolume
     * @param allocatedVolume
     * @param storageBin
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param loginUserId
     * @param authToken
     */
    public void updateStorageBin(Double remainingVolume, Double occupiedVolume, Double allocatedVolume,
                                 String storageBin, String companyCode, String plantId, String languageId,
                                 String warehouseId, Long statusId, String loginUserId, String authToken) {

        StorageBinV2 modifiedStorageBin = new StorageBinV2();
        modifiedStorageBin.setRemainingVolume(String.valueOf(remainingVolume));
        modifiedStorageBin.setAllocatedVolume(allocatedVolume);
        modifiedStorageBin.setOccupiedVolume(String.valueOf(occupiedVolume));
        modifiedStorageBin.setCapacityCheck(true);
        modifiedStorageBin.setStatusId(statusId);

        StorageBinV2 updateStorageBinV2 = mastersService.updateStorageBinV2(storageBin,
                modifiedStorageBin,
                companyCode,
                plantId,
                languageId,
                warehouseId,
                loginUserId,
                authToken);

        if (updateStorageBinV2 != null) {
            log.info("Storage Bin Volume Updated successfully ");
        }
    }

    public PutAwayLineV2 createPutAwayLineV2(PutAwayLineV2 newPutAwayLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
        log.info("newPutAwayLine : " + newPutAwayLine);
        BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(newPutAwayLine));

//        if (newPutAwayLine.getPutawayConfirmedQty() < newPutAwayLine.getPutAwayQuantity()) {
//            List<PutAwayHeaderV2> dbPutAwayHeaderList = putAwayHeaderService.getPutAwayHeaderV2(newPutAwayLine.getWarehouseId(),
//                    newPutAwayLine.getPreInboundNo(),
//                    newPutAwayLine.getRefDocNumber(),
//                    newPutAwayLine.getPutAwayNumber(),
//                    newPutAwayLine.getCompanyCode(),
//                    newPutAwayLine.getPlantId(),
//                    newPutAwayLine.getLanguageId());
//            if (dbPutAwayHeaderList != null) {
//                for (PutAwayHeaderV2 newPutAwayHeader : dbPutAwayHeaderList) {
//                    if (!newPutAwayHeader.getApprovalStatus().equalsIgnoreCase("Approved")) {
//                        throw new BadRequestException("Binned Quantity is less than Received");
//                    }
//                }
//            }
//        }

        IKeyValuePair description = stagingLineV2Repository.getDescription(newPutAwayLine.getCompanyCode(),
                newPutAwayLine.getLanguageId(),
                newPutAwayLine.getPlantId(),
                newPutAwayLine.getWarehouseId());

        dbPutAwayLine.setCompanyDescription(description.getCompanyDesc());
        dbPutAwayLine.setPlantDescription(description.getPlantDesc());
        dbPutAwayLine.setWarehouseDescription(description.getWarehouseDesc());

        dbPutAwayLine.setDeletionIndicator(0L);
        dbPutAwayLine.setCreatedBy(loginUserID);
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setCreatedOn(new Date());
        dbPutAwayLine.setUpdatedOn(new Date());
        PutAwayLineV2 createdPutAwayLine = putAwayLineRepository.save(dbPutAwayLine);

        /*----------------Inventory tables Create---------------------------------------------*/
        InventoryV2 createdinventory = createInventory(dbPutAwayLine);

        if (dbPutAwayLine.getCbm() != null) {

            // ST_BIN ---Pass WH_ID/CNF_ST_BIN in STORAGEBIN table and fetch OCC_VOL value and update
            AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
            StorageBinV2 storageBin = mastersService.getStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbPutAwayLine.getWarehouseId(), authTokenForMastersService.getAccess_token());

            Double occupiedVolume = Double.valueOf(storageBin.getOccupiedVolume());
            Double cbm = Double.valueOf(dbPutAwayLine.getCbm());

            storageBin.setOccupiedVolume(String.valueOf(occupiedVolume + cbm));
            storageBin.setRemainingVolume(String.valueOf(Double.valueOf(storageBin.getTotalVolume()) - Double.valueOf(storageBin.getOccupiedVolume())));

            StorageBinV2 updateStorageBin = mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(),
                    storageBin,
                    dbPutAwayLine.getCompanyCode(),
                    dbPutAwayLine.getPlantId(),
                    dbPutAwayLine.getLanguageId(),
                    dbPutAwayLine.getWarehouseId(),
                    loginUserID,
                    authTokenForMastersService.getAccess_token());

        }

        return createdPutAwayLine;
    }

    /**
     * @param createdPALine
     * @return
     */
    private InventoryV2 createInventory(PutAwayLineV2 createdPALine) {

        InventoryV2 inventory = new InventoryV2();

        BeanUtils.copyProperties(createdPALine, inventory, CommonUtils.getNullPropertyNames(createdPALine));

        inventory.setCompanyCodeId(createdPALine.getCompanyCode());


        //V2 Code (remaining all fields copied already using beanUtils.copyProperties)
        if (createdPALine.getCbm() == null) {
            createdPALine.setCbm("0");
        }
        if (createdPALine.getCbmQuantity() == null) {
            createdPALine.setCbmQuantity(0D);
        }
        inventory.setCbmPerQuantity(String.valueOf(Double.valueOf(createdPALine.getCbm()) / createdPALine.getCbmQuantity()));
        inventory.setStockTypeId(10L);

        InventoryV2 createdinventory = inventoryV2Repository.save(inventory);
        log.info("created inventory : " + createdinventory);
        return createdinventory;
    }

    /**
     * @param searchPutAwayLine
     * @return
     * @throws Exception
     */
    public List<PutAwayLineV2> findPutAwayLineV2(SearchPutAwayLineV2 searchPutAwayLine) throws Exception {

        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayLine.getFromConfirmedDate(), searchPutAwayLine.getToConfirmedDate());
            searchPutAwayLine.setFromConfirmedDate(dates[0]);
            searchPutAwayLine.setToConfirmedDate(dates[1]);
        }

        PutAwayLineV2Specification spec = new PutAwayLineV2Specification(searchPutAwayLine);
        List<PutAwayLineV2> results = putAwayLineV2Repository.findAll(spec);
        return results;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param goodsReceiptNo
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param lineNo
     * @param itemCode
     * @param proposedStorageBin
     * @param confirmedStorageBin
     * @param loginUserID
     * @param updatePutAwayLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PutAwayLineV2 updatePutAwayLinev2(String companyCode, String plantId, String languageId,
                                             String warehouseId, String goodsReceiptNo, String preInboundNo,
                                             String refDocNumber, String putAwayNumber, Long lineNo, String itemCode,
                                             String proposedStorageBin, String confirmedStorageBin,
                                             String loginUserID, PutAwayLineV2 updatePutAwayLine)
            throws IllegalAccessException, InvocationTargetException {
        PutAwayLineV2 dbPutAwayLine = getPutAwayLineV2(companyCode, plantId, languageId, warehouseId,
                goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
                lineNo, itemCode, proposedStorageBin, Arrays.asList(confirmedStorageBin));
        BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setUpdatedOn(new Date());
        return putAwayLineV2Repository.save(dbPutAwayLine);
    }

    /**
     * @param updatePutAwayLine
     * @param loginUserID
     * @return
     */
    public PutAwayLineV2 updatePutAwayLineV2(PutAwayLineV2 updatePutAwayLine, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, ParseException {
        PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
        BeanUtils.copyProperties(updatePutAwayLine, dbPutAwayLine, CommonUtils.getNullPropertyNames(updatePutAwayLine));
        dbPutAwayLine.setUpdatedBy(loginUserID);
        dbPutAwayLine.setUpdatedOn(new Date());
        return putAwayLineV2Repository.save(dbPutAwayLine);
    }

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param goodsReceiptNo
     * @param preInboundNo
     * @param refDocNumber
     * @param putAwayNumber
     * @param lineNo
     * @param itemCode
     * @param proposedStorageBin
     * @param confirmedStorageBin
     * @param loginUserID
     */
    public void deletePutAwayLineV2(String languageId, String companyCodeId, String plantId, String warehouseId,
                                    String goodsReceiptNo, String preInboundNo, String refDocNumber, String putAwayNumber, Long lineNo,
                                    String itemCode, String proposedStorageBin, String confirmedStorageBin, String loginUserID) throws ParseException {
        PutAwayLineV2 putAwayLine = getPutAwayLineV2(companyCodeId, plantId, languageId, warehouseId,
                goodsReceiptNo, preInboundNo, refDocNumber, putAwayNumber,
                lineNo, itemCode, proposedStorageBin, Arrays.asList(confirmedStorageBin));
        if (putAwayLine != null) {
            putAwayLine.setDeletionIndicator(1L);
            putAwayLine.setUpdatedBy(loginUserID);
            putAwayLine.setUpdatedOn(new Date());
            putAwayLineRepository.save(putAwayLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + confirmedStorageBin);
        }
    }

    /**
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLinesV2() {
        List<PutAwayLineV2> putAwayLineList = putAwayLineV2Repository.findAll();
        putAwayLineList = putAwayLineList.stream().filter(n -> n.getDeletionIndicator() == 0).collect(Collectors.toList());
        return putAwayLineList;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preInboundNo
     * @param refDocNumber
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLinesV2(String companyCode, String plantId, String languageId,
                                                 String warehouseId, String preInboundNo, String refDocNumber) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndStatusIdNotAndDeletionIndicator(
                        companyCode,
                        plantId,
                        languageId,
                        warehouseId,
                        refDocNumber,
                        preInboundNo,
                        24L,
                        0L);
        return putAwayLine;
    }

    /**
     * @param asnNumber
     */
    public void updateASNV2(String asnNumber) {
        List<PutAwayLineV2> putAwayLines = getPutAwayLinesV2();
        putAwayLines.stream().forEach(p -> p.setReferenceField1(asnNumber));
        putAwayLineV2Repository.saveAll(putAwayLines);
    }

    /**
     * @param dbPutAwayLine
     * @return
     */
    private InventoryMovement createInventoryMovementV2(PutAwayLineV2 dbPutAwayLine) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(dbPutAwayLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPutAwayLine));
        inventoryMovement.setCompanyCodeId(dbPutAwayLine.getCompanyCode());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(1L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(2L);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        inventoryMovement.setManufacturerName(dbPutAwayLine.getManufacturerName());
        inventoryMovement.setRefDocNumber(dbPutAwayLine.getRefDocNumber());
        inventoryMovement.setCompanyDescription(dbPutAwayLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(dbPutAwayLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(dbPutAwayLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(dbPutAwayLine.getBarcodeId());
        inventoryMovement.setDescription(dbPutAwayLine.getDescription());

        // CASE_CODE
        inventoryMovement.setCaseCode("999999");

        // PAL_CODE
        inventoryMovement.setPalletCode("999999");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(dbPutAwayLine.getRefDocNumber());

        // ST_BIN
        inventoryMovement.setStorageBin(dbPutAwayLine.getConfirmedStorageBin());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbPutAwayLine.getPutawayConfirmedQty());

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue("P");

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbPutAwayLine.getPutAwayUom());

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();

        StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
        storageBinPutAway.setCompanyCodeId(dbPutAwayLine.getCompanyCode());
        storageBinPutAway.setPlantId(dbPutAwayLine.getPlantId());
        storageBinPutAway.setLanguageId(dbPutAwayLine.getLanguageId());
        storageBinPutAway.setWarehouseId(dbPutAwayLine.getWarehouseId());
        storageBinPutAway.setBin(dbPutAwayLine.getConfirmedStorageBin());

        StorageBinV2 dbStoragebin = null;
        try {
            dbStoragebin = mastersService.getaStorageBinV2(storageBinPutAway, authTokenForMastersService.getAccess_token());
        } catch (Exception e) {
            throw new BadRequestException("Invalid StorageBin");
        }

        log.info("StorageBin: " + dbStoragebin);

        /*
         * -----THE BELOW IS NOT USED-------------
         * Pass WH_ID/ITM_CODE/PACK_BARCODE/BIN_CL_ID is equal to 1 in INVENTORY table and fetch INV_QTY
         * BAL_OH_QTY = INV_QTY
         */
        // PASS WH_ID/ITM_CODE/BIN_CL_ID and sum the INV_QTY for all selected inventory
        Long binClassId = 0L;                   //actual code follows
        if (dbPutAwayLine.getInboundOrderTypeId() == 1 || dbPutAwayLine.getInboundOrderTypeId() == 3 || dbPutAwayLine.getInboundOrderTypeId() == 4 || dbPutAwayLine.getInboundOrderTypeId() == 5) {
            binClassId = 1L;
            log.info("Inv Mmt BinClassId: " + binClassId);
        }
        if (dbPutAwayLine.getInboundOrderTypeId() == 2) {
            if (dbStoragebin != null) {
                binClassId = dbStoragebin.getBinClassId();
            } else {
                binClassId = 7L;
            }
            log.info("Inv Mmt Ib_Ord_Typ_Id 2 - BinClassId: " + binClassId);
        }
        List<IInventoryImpl> inventoryList = inventoryService.getInventoryForInvMmt(dbPutAwayLine.getCompanyCode(),
                dbPutAwayLine.getPlantId(),
                dbPutAwayLine.getLanguageId(),
                dbPutAwayLine.getWarehouseId(),
                dbPutAwayLine.getItemCode(),
                dbPutAwayLine.getManufacturerName(),
                binClassId);
        if (inventoryList != null) {
            double sumOfInvQty = inventoryList.stream().mapToDouble(a -> a.getInventoryQuantity()).sum();

            log.info("Inv Mmt sumOfInvQty: " + sumOfInvQty);

            inventoryMovement.setBalanceOHQty(sumOfInvQty);
        }
        if (inventoryList == null) {
            inventoryMovement.setBalanceOHQty(dbPutAwayLine.getPutawayConfirmedQty());
        }

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(dbPutAwayLine.getCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbPutAwayLine.getCreatedOn());
        inventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        return inventoryMovement;
    }

    /**
     * @param languageId
     * @param companyCodeId
     * @param plantId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws ParseException
     */
    //Delete PutAwayLine
    public List<PutAwayLineV2> deletePutAwayLineV2(String languageId, String companyCodeId, String plantId, String warehouseId,
                                                   String refDocNumber, String preInboundNo, String loginUserID) throws ParseException {
        List<PutAwayLineV2> putAwayLineV2List = new ArrayList<>();
        List<PutAwayLineV2> putAwayLineList = putAwayLineV2Repository.findByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo, 0L);
        log.info("putAwayLineList - cancellation : " + putAwayLineList);
        if (putAwayLineList != null && !putAwayLineList.isEmpty()) {
            for (PutAwayLineV2 putAwayLineV2 : putAwayLineList) {
                putAwayLineV2.setDeletionIndicator(1L);
                putAwayLineV2.setUpdatedBy(loginUserID);
                putAwayLineV2.setUpdatedOn(new Date());
                PutAwayLineV2 dbPutAwayLine = putAwayLineV2Repository.save(putAwayLineV2);
                putAwayLineV2List.add(dbPutAwayLine);
            }
        }
        return putAwayLineV2List;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public PutAwayLineV2 getPutAwayLineExistingItemCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String itemCode, String manufacturerName) {
        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.
                findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                        companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, 0L);
        if (dbPutawayLine != null) {
            return dbPutawayLine;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param bagSize
     * @return
     */
    public PutAwayLineV2 getPutAwayLineExistingItemCheckV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String itemCode, String manufacturerName, String alternateUom, Double bagSize) {
//        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndAlternateUomAndBagSizeAndDeletionIndicatorOrderByCreatedOn(companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, alternateUom, bagSize, 0L);
        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, 0L);
        return dbPutawayLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param bagSize
     * @return
     */
    public PutAwayLineV2 getPutAwayLineExistingItemCheckV7(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String itemCode, String manufacturerName, String alternateUom, Double bagSize, String barcodeId) {
//        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.findTopByCompanyCodeAndPlantIdAndWarehouseIdAndLanguageIdAndItemCodeAndManufacturerNameAndStatusIdAndAlternateUomAndBagSizeAndDeletionIndicatorOrderByCreatedOn(companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, alternateUom, bagSize, 0L);
        PutAwayLineV2 dbPutawayLine = putAwayLineV2Repository.getExistingPutAwayLineForKnowell(
                companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 20L, barcodeId);
        return dbPutawayLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param alternateUom
     * @param bagSize
     * @return
     */
    public PutAwayHeaderV2 getPutawayHeaderExistingBinItemCheckV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                  String itemCode, String manufacturerName, String alternateUom, Double bagSize) {
//        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndAlternateUomAndBagSizeAndDeletionIndicatorOrderByCreatedOn(companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 19L, alternateUom, bagSize, 0L);
        PutAwayHeaderV2 dbPutAwayHeader = putAwayHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndWarehouseIdAndLanguageIdAndReferenceField5AndManufacturerNameAndStatusIdAndDeletionIndicatorOrderByCreatedOn(
                companyCodeId, plantId, warehouseId, languageId, itemCode, manufacturerName, 19L, 0L);
        return dbPutAwayHeader;
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param refDocNumber
     * @param putAwayLine
     * @return
     */
    private InventoryV2 createInventoryNonCBMV4(String companyCode, String plantId, String languageId,
                                                String warehouseId, String itemCode, String manufacturerName,
                                                String refDocNumber, PutAwayLineV2 putAwayLine, String loginUserId) {
        alreadyExecuted = false;
        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
        String palletCode = null;
        String caseCode = null;
        try {
//            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize());

            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom());
            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();
                double INV_QTY = existinginventory.getInventoryQuantity() - physicalQty;
                if (INV_QTY < 0) {
                    INV_QTY = 0;
                }
                log.info("INV_QTY : " + INV_QTY);
                Double TOT_NO_OF_BAGS = (existinginventory.getNoBags() != null ? existinginventory.getNoBags() : 0D) - (putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D);

                if (INV_QTY >= 0) {
                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    inventory2.setNoBags(TOT_NO_OF_BAGS);
                    inventory2.setBagSize(putAwayLine.getBagSize());
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY + ", " + TOT_NO_OF_BAGS);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes(PACK_BARCODE);
                    }
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setReferenceDocumentNo(refDocNumber);
                    inventory2.setReferenceOrderNo(refDocNumber);
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setUpdatedBy(loginUserId);
                    if (!alreadyExecuted) {
                        InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                        log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(companyCode);
            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());        // STR_NO
            } else {
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setPackBarcodes(PACK_BARCODE);
            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
            log.info("storageBin: " + storageBin);

            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(),
                    putAwayLine.getConfirmedStorageBin());
//            String packBarCode = putAwayLine.getBatchSerialNumber() != null ? putAwayLine.getBatchSerialNumber() : "99999";
//            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize(), putAwayLine.getConfirmedStorageBin(), packBarCode);
//            if (putAwayLine.getBatchSerialNumber() != null) {
//                existingInventory = inventoryService.getInventoryForInboundConfirmV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        putAwayLine.getBatchSerialNumber(), itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin(),
//                        putAwayLine.getBatchSerialNumber());
//            } else {
//                existingInventory = inventoryService.getInventoryV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        "99999", itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin());
//            }

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                Double NO_OF_BAGS = existingInventory.getNoBags() != null ? existingInventory.getNoBags() : 0D;
                Double NEW_NO_OF_BAGS = putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D;
                Double TOT_NO_OF_BAGS = NO_OF_BAGS + NEW_NO_OF_BAGS;
                log.info("Existing Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                        + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty() + ", " + NO_OF_BAGS);

//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();

                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                Double ALLOC_QTY = 0D;
//                Double INV_QTY = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                Double INV_QTY = putAwayLine.getPutawayConfirmedQty();
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                Double TOT_NO_OF_BAGS = putAwayLine.getNoBags();
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setCreatedBy(loginUserId);
                inventory.setCreatedOn(new Date());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, languageId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());

            InventoryV2 createdinventory = null;
            if (!alreadyExecuted) {
                createdinventory = inventoryV2Repository.save(inventory);
                alreadyExecuted = true;             //to ensure method executing only once
                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
            }

            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    /**
     * Create inventory for Knowell putawayLine creation
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param refDocNumber
     * @param putAwayLine
     * @return
     */
    private InventoryV2 createInventoryNonCBMV7(String companyCode, String plantId, String languageId,
                                                String warehouseId, String itemCode, String manufacturerName,
                                                String refDocNumber, PutAwayLineV2 putAwayLine, String loginUserId) {
        alreadyExecuted = false;
        log.info("Create Inventory Initiated ---> alreadyExecuted ---> " + new Date() + ", " + alreadyExecuted);
        String palletCode = null;
        String caseCode = null;
        try {
//            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize());

            InventoryV2 existinginventory = grLineService.getInventoryBinClassId3V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom());
            if (existinginventory != null) {
                log.info("Create Inventory bin Class Id 3 Initiated: " + new Date());
//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();
                double INV_QTY = existinginventory.getInventoryQuantity() - physicalQty;
                if (INV_QTY < 0) {
                    INV_QTY = 0;
                }
                log.info("INV_QTY : " + INV_QTY);
                Double TOT_NO_OF_BAGS = (existinginventory.getNoBags() != null ? existinginventory.getNoBags() : 0D) - (putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D);

                if (INV_QTY >= 0) {
                    InventoryV2 inventory2 = new InventoryV2();
                    BeanUtils.copyProperties(existinginventory, inventory2, CommonUtils.getNullPropertyNames(existinginventory));
                    String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, existinginventory.getStockTypeId());
                    inventory2.setStockTypeDescription(stockTypeDesc);
                    inventory2.setInventoryQuantity(round(INV_QTY));
                    inventory2.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                    inventory2.setNoBags(TOT_NO_OF_BAGS);
                    inventory2.setBagSize(putAwayLine.getBagSize());
                    log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY + ", " + TOT_NO_OF_BAGS);

                    palletCode = existinginventory.getPalletCode();
                    caseCode = existinginventory.getCaseCode();

                    inventory2.setBusinessPartnerCode(putAwayLine.getBusinessPartnerCode());
                    inventory2.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());
                    if (putAwayLine.getBatchSerialNumber() != null) {
                        inventory2.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                    } else {
                        inventory2.setPackBarcodes(PACK_BARCODE);
                    }
                    if (inventory2.getItemType() == null) {
                        IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, warehouseId, itemCode);
                        if (itemType != null) {
                            inventory2.setItemType(itemType.getItemType());
                            inventory2.setItemTypeDescription(itemType.getItemTypeDescription());
                        }
                    }
                    inventory2.setReferenceDocumentNo(refDocNumber);
                    inventory2.setReferenceOrderNo(refDocNumber);
                    inventory2.setManufacturerDate(putAwayLine.getManufacturerDate());
                    inventory2.setExpiryDate(putAwayLine.getExpiryDate());
                    inventory2.setCreatedOn(existinginventory.getCreatedOn());
                    inventory2.setUpdatedOn(new Date());
                    inventory2.setUpdatedBy(loginUserId);
                    if (!alreadyExecuted) {
                        InventoryV2 createdinventoryV2 = inventoryV2Repository.save(inventory2);
                        log.info("----existinginventory--createdInventoryV2--------> : " + createdinventoryV2);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Existing Inventory---Error-----> : " + e.toString());
        }

        try {
            log.info("Create Inventory bin Class Id 1 Initiated: " + new Date());
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(putAwayLine, inventory, CommonUtils.getNullPropertyNames(putAwayLine));

            inventory.setCompanyCodeId(companyCode);
            // VAR_ID, VAR_SUB_ID, STR_MTD, STR_NO ---> Hard coded as '1'
            inventory.setVariantCode(1L);                // VAR_ID
            inventory.setVariantSubCode("1");            // VAR_SUB_ID
            inventory.setStorageMethod("1");            // STR_MTD
            inventory.setStorageBin(putAwayLine.getConfirmedStorageBin());
            inventory.setManufacturerCode(putAwayLine.getManufacturerName());
            inventory.setReferenceField9(putAwayLine.getManufacturerName());
            inventory.setDescription(putAwayLine.getDescription());
            inventory.setReferenceField8(putAwayLine.getDescription());
            if (putAwayLine.getBatchSerialNumber() != null) {
                inventory.setPackBarcodes(putAwayLine.getBatchSerialNumber());
                inventory.setBatchSerialNumber(putAwayLine.getBatchSerialNumber());        // STR_NO
            } else {
                inventory.setBatchSerialNumber("1");        // STR_NO
                inventory.setPackBarcodes(PACK_BARCODE);
            }

            // ST_BIN ---Pass WH_ID/BIN_CL_ID=3 in STORAGEBIN table and fetch ST_BIN value and update
            StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
            storageBinPutAway.setCompanyCodeId(companyCode);
            storageBinPutAway.setPlantId(plantId);
            storageBinPutAway.setLanguageId(languageId);
            storageBinPutAway.setWarehouseId(warehouseId);
            storageBinPutAway.setBin(putAwayLine.getConfirmedStorageBin());

            StorageBinV2 storageBin = storageBinService.getaStorageBinV2(storageBinPutAway);
            log.info("storageBin: " + storageBin);

            if (storageBin != null) {
                inventory.setReferenceField10(storageBin.getStorageSectionId());
                inventory.setStorageSectionId(storageBin.getStorageSectionId());
                inventory.setReferenceField5(storageBin.getAisleNumber());
                inventory.setReferenceField6(storageBin.getShelfId());
                inventory.setReferenceField7(storageBin.getRowId());
                inventory.setLevelId(String.valueOf(storageBin.getFloorId()));
                inventory.setBinClassId(storageBin.getBinClassId());
            }

            inventory.setCompanyDescription(putAwayLine.getCompanyDescription());
            inventory.setPlantDescription(putAwayLine.getPlantDescription());
            inventory.setWarehouseDescription(putAwayLine.getWarehouseDescription());

            inventory.setPalletCode(palletCode);
            inventory.setCaseCode(caseCode);
            log.info("PalletCode, CaseCode: " + palletCode + ", " + caseCode);

            // STCK_TYP_ID
            inventory.setStockTypeId(1L);
            String stockTypeDesc = getStockTypeDesc(companyCode, plantId, languageId, warehouseId, 1L);
            inventory.setStockTypeDescription(stockTypeDesc);
            log.info("StockTypeDescription: " + stockTypeDesc);

            // SP_ST_IND_ID
            inventory.setSpecialStockIndicatorId(1L);
            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName,
                    putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(),
                    putAwayLine.getConfirmedStorageBin());
//            String packBarCode = putAwayLine.getBatchSerialNumber() != null ? putAwayLine.getBatchSerialNumber() : "99999";
//            InventoryV2 existingInventory = grLineService.getInventoryBinClassId1V4(companyCode, plantId, languageId, warehouseId, itemCode, manufacturerName, putAwayLine.getBatchSerialNumber(), putAwayLine.getBarcodeId(), putAwayLine.getAlternateUom(), putAwayLine.getBagSize(), putAwayLine.getConfirmedStorageBin(), packBarCode);
//            if (putAwayLine.getBatchSerialNumber() != null) {
//                existingInventory = inventoryService.getInventoryForInboundConfirmV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        putAwayLine.getBatchSerialNumber(), itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin(),
//                        putAwayLine.getBatchSerialNumber());
//            } else {
//                existingInventory = inventoryService.getInventoryV3(
//                        companyCode, plantId, languageId, warehouseId,
//                        "99999", itemCode,
//                        putAwayLine.getBarcodeId(), manufacturerName,
//                        putAwayLine.getConfirmedStorageBin());
//            }

            // INV_QTY
            if (existingInventory != null) {
                Double ALLOC_QTY = existingInventory.getAllocatedQuantity() != null ? existingInventory.getAllocatedQuantity() : 0D;
                Double INV_QTY = existingInventory.getInventoryQuantity() != null ? existingInventory.getInventoryQuantity() : 0D;
                Double NO_OF_BAGS = existingInventory.getNoBags() != null ? existingInventory.getNoBags() : 0D;
                Double NEW_NO_OF_BAGS = putAwayLine.getNoBags() != null ? putAwayLine.getNoBags() : 0D;
                Double TOT_NO_OF_BAGS = NO_OF_BAGS + NEW_NO_OF_BAGS;
                log.info("Existing Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY, PA_CNF_QTY : "
                        + INV_QTY + ", " + ALLOC_QTY + ", " + existingInventory.getReferenceField4() + ", " + putAwayLine.getPutawayConfirmedQty() + ", " + NO_OF_BAGS);

//                double physicalQty = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                double physicalQty = putAwayLine.getPutawayConfirmedQty();

                INV_QTY = INV_QTY + physicalQty;
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setUpdatedBy(loginUserId);
                inventory.setCreatedOn(existingInventory.getCreatedOn());
            }
            if (existingInventory == null) {
                Double ALLOC_QTY = 0D;
//                Double INV_QTY = getQuantity(putAwayLine.getPutawayConfirmedQty(), putAwayLine.getBagSize());
                Double INV_QTY = putAwayLine.getPutawayConfirmedQty();
                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                Double TOT_NO_OF_BAGS = putAwayLine.getNoBags();
                inventory.setInventoryQuantity(round(INV_QTY));
                inventory.setAllocatedQuantity(round(ALLOC_QTY));
                inventory.setReferenceField4(round(TOT_QTY));
                inventory.setNoBags(TOT_NO_OF_BAGS);
                inventory.setBagSize(putAwayLine.getBagSize());
                inventory.setInventoryUom(putAwayLine.getPutAwayUom());
                inventory.setAlternateUom(putAwayLine.getAlternateUom());
                log.info("New Inventory----> INV_QTY, ALLOC_QTY, TOT_QTY : " + INV_QTY + ", " + ALLOC_QTY + ", " + TOT_QTY + ", " + TOT_NO_OF_BAGS);

                inventory.setCreatedBy(loginUserId);
                inventory.setCreatedOn(new Date());
            }

            if (inventory.getItemType() == null) {
                IKeyValuePair itemType = getItemTypeAndDesc(companyCode, plantId, languageId, languageId, itemCode);
                if (itemType != null) {
                    inventory.setItemType(itemType.getItemType());
                    inventory.setItemTypeDescription(itemType.getItemTypeDescription());
                }
            }
            inventory.setReferenceDocumentNo(putAwayLine.getRefDocNumber());
            inventory.setReferenceOrderNo(putAwayLine.getRefDocNumber());
            inventory.setDeletionIndicator(0L);

            inventory.setUpdatedOn(new Date());
            inventory.setBatchDate(new Date());

            InventoryV2 createdinventory = null;
            if (!alreadyExecuted) {
                createdinventory = inventoryV2Repository.save(inventory);
                alreadyExecuted = true;             //to ensure method executing only once
                log.info("created inventory : executed" + createdinventory + " -----> " + alreadyExecuted);
            }

            return createdinventory;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Error While Creating Inventory");
        }
    }

    /**
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     */
    private void inboundConfirmValidation(String companyCode, String plantId, String languageId, String warehouseId,
                                          String refDocNumber, String preInboundNo, String loginUserID) {
        IKeyValuePair confirmedLines = inboundHeaderV2Repository.findSumOfConfirmedInboundLines(companyCode, plantId, languageId, warehouseId, preInboundNo);
        if (confirmedLines != null) {
            log.info("InboundHeader orderQty: " + confirmedLines.getOrdQty() + ", RxdQty: " + confirmedLines.getRxdQty());
            if (confirmedLines.getOrdQty().equals(confirmedLines.getRxdQty())) {
                log.info("Initiate Automatic Inbound Confirmation------> " + refDocNumber + "---> " + preInboundNo);
                inboundHeaderService.updateInboundHeaderConfirmV5(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            }
        }
    }

    /**
     * Knowell Manual Inbound Confirmation
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     */
    public void inboundConfirmValidationV7(String companyCode, String plantId, String languageId, String warehouseId,
                                           String refDocNumber, String preInboundNo, String loginUserID) {
        IKeyValuePair confirmedLines = inboundHeaderV2Repository.findSumOfConfirmedInboundLines(companyCode, plantId, languageId, warehouseId, preInboundNo);
        if (confirmedLines != null) {
            log.info("InboundHeader orderQty: " + confirmedLines.getOrdQty() + ", RxdQty: " + confirmedLines.getRxdQty());
            if (confirmedLines.getOrdQty().equals(confirmedLines.getRxdQty())) {
                log.info("Initiate Automatic Inbound Confirmation------> " + refDocNumber + "---> " + preInboundNo);
                inboundHeaderService.updateInboundHeaderConfirmV5(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
            }
        }
    }

}