package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.tekclover.wms.api.transaction.repository.*;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.HHTUser;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.PutawayUserGroup;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.InboundLine;
import com.tekclover.wms.api.transaction.model.inbound.gr.StorageBinPutAway;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.AddPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayHeader;
import com.tekclover.wms.api.transaction.model.inbound.putaway.PutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.SearchPutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.UpdatePutAwayLine;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayHeaderV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.PutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.putaway.v2.SearchPutAwayLineV2;
import com.tekclover.wms.api.transaction.model.inbound.staging.v2.StagingLineEntityV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.InboundLineV2;
import com.tekclover.wms.api.transaction.model.inbound.v2.PutAwayLineConfirm;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PutAwayLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PutAwayLineService extends BaseService {

    @Autowired
    private PutAwayHeaderRepository putAwayHeaderRepository;

    @Autowired
    private PutAwayLineRepository putAwayLineRepository;

    @Autowired
    private PutAwayHeaderService putAwayHeaderService;

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
    private InboundHeaderV2Repository inboundHeaderV2Repository;

    @Autowired
    private InboundLineService inboundLineService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;
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
    PalletIdAssignmentRepository palletIdAssignmentRepository;

    @Autowired
    private PalletIdAssignmentService palletIdAssignmentService;

    @Autowired
    private InboundHeaderService inboundHeaderService;

    @Autowired
    IDMasterService idMasterService;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PutAwayLineAsyncProcess putAwayLineAsyncProcess;

    @Autowired
    StorageBinV2Repository storageBinV2Repository;

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
     *
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
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param barcodeId
     * @return
     */
    public List<PutAwayLineV2> getPutAwayLineForReversalV3(String companyCode, String plantId, String languageId,
                                                           String warehouseId, String refDocNumber, String barcodeId) {
        List<PutAwayLineV2> putAwayLine =
                putAwayLineV2Repository.findByLanguageIdAndCompanyCodeAndPlantIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndDeletionIndicator(
                        languageId,
                        companyCode,
                        plantId,
                        warehouseId,
                        refDocNumber,
                        barcodeId,
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
     *
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
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV2(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
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

                if(newPutAwayLine.getPutawayConfirmedQty() <= 0) {
                    throw new BadRequestException("Putaway Confirm Qty cannot be zero or negative");
                }
                PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
//                PutAwayHeaderV2 dbPutAwayHeader = new PutAwayHeaderV2();

                if(newPutAwayLine.getQuantityType() == null) {
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
                if(findPutawayHeader != null) {
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
                                if(dbPutawayQty == null) {
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
                                        if(PUTAWAY_QTY < 0) {
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
                                        if(PUTAWAY_QTY < 0) {
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
                            if(addedAcceptQty > inboundLine.getOrderQty()) {
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
                            if(addedDamageQty > inboundLine.getOrderQty()) {
                                throw new BadRequestException("Damage qty cannot be greater than order qty");
                            }
                            inboundLine.setDamageQty(addedDamageQty);
                            inboundLine.setVarianceQty(inboundLine.getOrderQty() - addedDamageQty);
                        }

                        if(inboundLine.getInboundOrderTypeId() == 5L){          //condition added for final Inbound confirm
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
     *
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws Exception
     */
//    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmV3(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID)
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

        List<PutAwayLineConfirm> refDocNumbers = new ArrayList<>();

        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDMasterService = authTokenService.getIDMasterServiceAuthToken();
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
                boolean isDuplicate = refDocNumbers.stream().anyMatch(n -> n.getRefDocNumber() != null &&  n.getRefDocNumber().equalsIgnoreCase(finalRefDocNumber));
                log.info("isDuplicate: " + isDuplicate);
                if(!isDuplicate) {
                    refDocNumbers.add(putAwayLineConfirm);
                }

                Double cbmPerQuantity = 0D;
                Double cbm = 0D;
                Double allocatedVolume = 0D;
                Double occupiedVolume = 0D;
                Double remainingVolume = 0D;
                Double totalVolume = 0D;

                Double allocateQty = 0D;
                Double orderedQty = 0D;
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
                        log.info("confirmedStorageBin == proposedBin" + confirmedStorageBin + ", " + proposedStorageBin);
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

                // V2 Code
                IKeyValuePair description = stagingLineV2Repository.getDescription(companyCode, languageId, plantId, warehouseId);
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

                BeanUtils.copyProperties(newPutAwayLine, dbPutAwayLine,
                        CommonUtils.getNullPropertyNames(newPutAwayLine));
                dbPutAwayLine.setCompanyCode(newPutAwayLine.getCompanyCode());

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
                    dbPutAwayLine.setBatchSerialNumber(findPutawayHeader.getBatchSerialNumber());
                    dbPutAwayLine.setCreatedOn(findPutawayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(findPutawayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(findPutawayHeader.getInboundOrderTypeId());
                    dbPutAwayLine.setStorageSectionId(findPutawayHeader.getStorageSectionId());
                    dbPutAwayLine.setLineNo(Long.valueOf(findPutawayHeader.getReferenceField9()));

                    dbPutAwayLine.setMaterialNo(findPutawayHeader.getMaterialNo());
                    dbPutAwayLine.setPriceSegment(findPutawayHeader.getPriceSegment());
                    dbPutAwayLine.setArticleNo(findPutawayHeader.getArticleNo());
                    dbPutAwayLine.setGender(findPutawayHeader.getGender());
                    dbPutAwayLine.setColor(findPutawayHeader.getColor());
                    dbPutAwayLine.setSize(findPutawayHeader.getSize());
                    dbPutAwayLine.setNoPairs(findPutawayHeader.getNoPairs());

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
                        throw new BadRequestException("Exception : " + e);
                    }

                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);

                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);

                    createdPutAwayLines.add(createdPutAwayLine);
//                    websocketNotification(createdPutAwayLine, loginUserID);

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                        // Insert a record into INVENTORY table as below
                        /*
                         * Commenting out Inventory creation alone
                         */
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

                        // Updating StorageBin StatusId as '1'
                        dbStorageBin.setStatusId(1L);
                        mastersService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
                                dbPutAwayLine.getCompanyCode(), dbPutAwayLine.getPlantId(),
                                dbPutAwayLine.getLanguageId(), dbPutAwayLine.getWarehouseId(), loginUserID,
                                authTokenForMastersService.getAccess_token());

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
                                    throw new BadRequestException(
                                            "sum of confirm Putaway line qty is greater than assigned putaway header qty");
                                }
                                if (dbPutawayQty <= dbAssignedPutawayQty) {
                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
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
                                        statusDescription = stagingLineV2Repository.getStatusDescription(
                                                putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
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

                        inboundLine.setBatchSerialNumber(createdPutAwayLine.getBatchSerialNumber());
                        inboundLine.setManufacturerDate(createdPutAwayLine.getManufacturerDate());
                        inboundLine.setExpiryDate(createdPutAwayLine.getExpiryDate());
                        inboundLine.setReferenceField2("TRUE");
                        inboundLine.setStatusId(20L);
                        statusDescription = stagingLineV2Repository.getStatusDescription(20L,
                                createdPutAwayLine.getLanguageId());
                        inboundLine.setStatusDescription(statusDescription);
                        InboundLineV2 updatedInboundLine = inboundLineV2Repository.saveAndFlush(inboundLine);
                        log.info("inboundLine updated : " + updatedInboundLine);
                    }
                } else {
                    log.info("Putaway Line already exist : " + existingPutAwayLine);
                }
            }
            if(refDocNumbers !=null && !refDocNumbers.isEmpty()) {
                log.info("refDocNumbers : " + refDocNumbers);
                for(PutAwayLineConfirm docNumber : refDocNumbers) {
                    putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId, warehouseId,
                            docNumber.getRefDocNumber(), docNumber.getPreInboundNo());
                    log.info("InboundHeader received lines count updated: " + docNumber.getRefDocNumber() + ", " + docNumber.getPreInboundNo());

                    Long confirmedLines = inboundHeaderV2Repository.findCountOfConfirmedInboundLines(docNumber.getPreInboundNo());
                    log.info("PreIbNo: " + preInboundNo);
                    Long confirmedPutAwayLines = inboundHeaderV2Repository.findCountOfConfirmedPutAwayLines(docNumber.getPreInboundNo());
                    log.info("InboundHeader confirmedLines: " + confirmedLines + ": putawaylines count: " + confirmedPutAwayLines);
                    if (confirmedPutAwayLines == confirmedLines) {
                        inboundHeaderService.updateInboundHeaderPartialConfirmV2(companyCode, plantId, languageId, warehouseId, docNumber.getPreInboundNo(), docNumber.getRefDocNumber(), loginUserID);
                    }
                }
            }
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param putAwayLine
     * @param loginUserID
     */
//    private void websocketNotification(PutAwayLineV2 putAwayLine, String loginUserID) {
//        try {
//            NotificationSave notificationInput = new NotificationSave();
//            notificationInput.setUserId(Collections.singletonList(loginUserID));
//            notificationInput.setUserType(null);
//            notificationInput.setMessage("A new Inbound- " + putAwayLine.getConfirmedStorageBin() + " has been created on ");
//            notificationInput.setTopic("Inbound Create");
//            notificationInput.setReferenceNumber(putAwayLine.getRefDocNumber());
//            notificationInput.setDocumentNumber(putAwayLine.getPreInboundNo());
//            notificationInput.setCompanyCodeId(putAwayLine.getCompanyCode());
//            notificationInput.setPlantId(putAwayLine.getPlantId());
//            notificationInput.setLanguageId(putAwayLine.getLanguageId());
//            notificationInput.setWarehouseId(putAwayLine.getWarehouseId());
//            notificationInput.setCreatedOn(putAwayLine.getCreatedOn());
//            notificationInput.setCreatedBy(loginUserID);
//            notificationInput.setStorageBin(putAwayLine.getConfirmedStorageBin());
//            this.idMasterService.createNotification(notificationInput);
//        } catch (Exception e) {
//            log.error("Inbound websocket notification error " + e.toString());
//        }
//    }

    /**
     *
     * @param putAwayLine
     */
//    private void fireBaseNotification(PutAwayLineV2 putAwayLine, String loginUserID) {
//        try {
//            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
//                    putAwayLine.getCompanyCode(), putAwayLine.getPlantId(), putAwayLine.getLanguageId(), putAwayLine.getWarehouseId());
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
//                notificationInput.setCompanyCodeId(putAwayLine.getCompanyCode());
//                notificationInput.setPlantId(putAwayLine.getPlantId());
//                notificationInput.setLanguageId(putAwayLine.getLanguageId());
//                notificationInput.setWarehouseId(putAwayLine.getWarehouseId());
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
//
//    /**
//     *
//     * @param putAwayLine
//     */
//    private void fireBaseNotificationV3(String companyCodeId, String plantId, String languageId,
//                                        String warehouseId, FetchImpl putAwayLine, String loginUserID) {
//        try {
//            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(companyCodeId, plantId, languageId, warehouseId);
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

    /**
     *
     * @param newPutAwayLine
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws ParseException
     */
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


    public List<PutawayUserGroup> findPutawayLineDistribution(SearchPutAwayLineV2 searchPutAwayLine) throws Exception {
        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(
                    searchPutAwayLine.getFromConfirmedDate(),
                    searchPutAwayLine.getToConfirmedDate());

            searchPutAwayLine.setFromConfirmedDate(dates[0]);
            searchPutAwayLine.setToConfirmedDate(dates[1]);
        }

        PutAwayLineV2Specification spec = new PutAwayLineV2Specification(searchPutAwayLine);
        List<PutAwayLineV2> results = putAwayLineV2Repository.findAll(spec);

        return results.stream()
                .filter(line -> line.getAssignedUserId() != null)
                .collect(Collectors.groupingBy(
                        PutAwayLineV2::getAssignedUserId,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                userLines -> {
                                    double totalQty = userLines.stream()
                                            .mapToDouble(line -> line.getPutawayConfirmedQty() != null ? line.getPutawayConfirmedQty() : 0.0)
                                            .sum();
                                    long count = userLines.size();

                                    PutAwayLineV2 sampleLine = userLines.get(0);
                                    int nonNullTeamCount = 0;
                                    if (sampleLine.getTeamMember1() != null) nonNullTeamCount++;
                                    if (sampleLine.getTeamMember2() != null) nonNullTeamCount++;
                                    if (sampleLine.getTeamMember3() != null) nonNullTeamCount++;
                                    if (sampleLine.getTeamMember4() != null) nonNullTeamCount++;
                                    if (sampleLine.getTeamMember5() != null) nonNullTeamCount++;

                                    double qtyPerMember = (nonNullTeamCount > 0) ? (totalQty / nonNullTeamCount) : 0.0;

                                    // Duration from refField1
                                    Double durationInMin = 0.0;
                                    try {
                                        if (sampleLine.getReferenceField1() != null) {
                                            durationInMin = Double.parseDouble(sampleLine.getReferenceField1());
                                        }
                                    } catch (NumberFormatException e) {
                                        durationInMin = 0.0;
                                    }

                                    double qtyPerUserPerMin = (durationInMin > 0 && qtyPerMember > 0) ? (qtyPerMember / durationInMin) : 0.0;
                                    double minPerQtyPerUser = (qtyPerMember > 0 && durationInMin > 0) ? (durationInMin / qtyPerMember) : 0.0;

                                    return new PutawayUserGroup(sampleLine.getAssignedUserId(), totalQty, count, qtyPerMember,
                                            durationInMin, qtyPerMember > 0 && durationInMin > 0 ? qtyPerMember / durationInMin : 0.0,
                                            qtyPerMember > 0 && durationInMin > 0 ? durationInMin / qtyPerMember : 0.0);

                                }
                        )
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }



    /**
     *
     * @param searchPutAwayLine
     * @return
     * @throws Exception
     */
    public List<PutAwayLineV2> findPutAwayLineV3(SearchPutAwayLineV2 searchPutAwayLine) throws Exception {
        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayLine.getFromConfirmedDate(), searchPutAwayLine.getToConfirmedDate());
            searchPutAwayLine.setFromConfirmedDate(dates[0]);
            searchPutAwayLine.setToConfirmedDate(dates[1]);
        }

        PutAwayLineV2Specification spec = new PutAwayLineV2Specification(searchPutAwayLine);
        List<PutAwayLineV2> results = putAwayLineV2Repository.findAll(spec);


        results.forEach( response -> {
            // Fetch HHT users
            List<HHTUser> hhtUserList = putAwayHeaderV2Repository.getHHtDetails(searchPutAwayLine.getUserId());
            log.info("hhtUserList -----> {}", hhtUserList);

            if (hhtUserList.get(0).getTeamMember1() != null) {
                response.setTeamMember1(hhtUserList.get(0).getTeamMember1());
            }
            if (hhtUserList.get(0).getTeamMember2() != null) {
                response.setTeamMember2(hhtUserList.get(0).getTeamMember2());
            }
            if (hhtUserList.get(0).getTeamMember3() != null) {
                response.setTeamMember3(hhtUserList.get(0).getTeamMember3());
            }
            if (hhtUserList.get(0).getTeamMember4() != null) {
                response.setTeamMember4(hhtUserList.get(0).getTeamMember4());
            }
            if (hhtUserList.get(0).getTeamMember5() != null) {
                response.setTeamMember5(hhtUserList.get(0).getTeamMember5());
            }
        });

        return results;
    }

//    /**
//     * @param searchPutAwayLine
//     * @return
//     * @throws Exception
//     */
//    public List<PutAwayPalletGroupResponse> findPutAwayLineMobApp(SearchPutAwayLineV2 searchPutAwayLine) throws Exception {
//
//        if (searchPutAwayLine.getFromConfirmedDate() != null && searchPutAwayLine.getToConfirmedDate() != null) {
//            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPutAwayLine.getFromConfirmedDate(), searchPutAwayLine.getToConfirmedDate());
//            searchPutAwayLine.setFromConfirmedDate(dates[0]);
//            searchPutAwayLine.setToConfirmedDate(dates[1]);
//        }
//
//        PutAwayLineV2Specification spec = new PutAwayLineV2Specification(searchPutAwayLine);
//        List<PutAwayLineV2> results = putAwayLineV2Repository.findAll(spec);
//
//        // Grouping by custom key: refDocNumber, palletId, proposedStorageBin
//        Map<PutAwayGroupKey, List<PutAwayLineV2>> groupedMap = results.stream()
//                .collect(Collectors.groupingBy(p ->
//                        new PutAwayGroupKey(
//                                p.getRefDocNumber(),
//                                p.getPalletId(),
//                                p.getProposedStorageBin()
//                        )
//                ));
//
//        List<PutAwayPalletGroupResponse> palletGroupResponses = new ArrayList<>();
//        // Set group count
//        for (Map.Entry<PutAwayGroupKey, List<PutAwayLineV2>> entry : groupedMap.entrySet()) {
//            List<PutAwayLineV2> groupList = entry.getValue();
//            long count = groupList.size();
//            for (PutAwayLineV2 item : groupList) {
//                PutAwayPalletGroupResponse response = new PutAwayPalletGroupResponse();
//                log.info("item -----> {}", item);
//                item.setGroupCount(count);
//                response.setGroupCount(count);
//                response.setCreatedOn(item.getCreatedOn());
//                response.setCreatedBy(item.getCreatedBy());
//                response.setConfirmedStorageBin(item.getConfirmedStorageBin());
//                response.setConfirmedOn(item.getConfirmedOn());
//                response.setConfirmedBy(item.getConfirmedBy());
//                response.setPalletId(item.getPalletId());
//                response.setProposedStorageBin(item.getProposedStorageBin());
//                response.setRefDocNumber(item.getRefDocNumber());
//                response.setAssignedUserId(item.getAssignedUserId());
//
//                palletGroupResponses.add(response);
//            }
//        }
//
//        return palletGroupResponses;
//    }

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
     *
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
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preInboundNo,0L);
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
     *
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
     *
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV2(@Valid List<PutAwayLineV2> newPutAwayLines, String loginUserID) {
        List<PutAwayLineV2> createdPutAwayLines = new ArrayList<>();
        log.info("newPutAwayLines to confirm : " + newPutAwayLines);

        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

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

                log.info("proposedBin, confirmedBin: " + newPutAwayLine.getProposedStorageBin() + ", " + newPutAwayLine.getConfirmedStorageBin());

                StorageBinV2 dbStorageBin = null;
                try {
                    dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getConfirmedStorageBin());
                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
                }

                PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.getPutawayHeaderV2(companyCode, plantId, warehouseId, languageId, newPutAwayLine.getPutAwayNumber());
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
                    dbPutAwayLine.setCreatedOn(putAwayHeader.getCreatedOn());
                    dbPutAwayLine.setPutAwayQuantity(putAwayHeader.getPutAwayQuantity());
                    dbPutAwayLine.setInboundOrderTypeId(putAwayHeader.getInboundOrderTypeId());
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

                if (existingPutAwayLine.isEmpty()) {
                    //Lead Time calculation
                    String leadTime = putAwayLineV2Repository.getleadtime(companyCode, plantId, languageId, warehouseId, newPutAwayLine.getPutAwayNumber(), new Date());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);

                    PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                    log.info("---------->NewPutAwayLine created: " + createdPutAwayLine);
//                    fireBaseNotification(createdPutAwayLine, loginUserID);

                    createdPutAwayLines.add(createdPutAwayLine);

                    if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {

                        dbStorageBin.setStatusId(1L);
                        storageBinService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin, companyCode, plantId, languageId, warehouseId, loginUserID);

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
                                    if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
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
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("PutawayLine Create Exception");
        }
    }


    /**
     *
     * @param newPutAwayLines
     * @param loginUserID
     * @return
     * @throws Exception
     */

    /**
     *
     * @param putAwayLineV2s putAwayLineList
     * @param loginUserID UserID
     * @return
     */
    public List<PutAwayLineV2> putAwayConfirmProcess(List<PutAwayLineV2> putAwayLineV2s, String loginUserID) {

        log.info("PutAwayLine Confirm Process Size is {} ", putAwayLineV2s.size());
        for(PutAwayLineV2 pu : putAwayLineV2s) {
            log.info("PutAwayLine confirm Status Id Updated ItemCode {}, BarcodeIs {} ", pu.getItemCode(), pu.getBarcodeId());
            putAwayHeaderV2Repository.updatePutAwayHeaderStatusId( pu.getCompanyCode(), pu.getPlantId(), pu.getLanguageId(), pu.getWarehouseId(),
                    pu.getItemCode(), pu.getBarcodeId(), 20L);
        }
        putAwayLineAsyncProcess.createPutAwayLine(putAwayLineV2s, loginUserID);

        log.info("Return Response Successfully In PutAwayConfirm --------------------------->");
        return putAwayLineV2s;
    }


    /**
     *
     * @param newPutAwayLines putAwayLine
     * @param loginUserID userID
     * @return
     * @throws Exception Exception
     */
    public List<PutAwayLineV2> putAwayLineConfirmNonCBMV3(@Valid List<PutAwayLineV2> newPutAwayLines,
                                                          String loginUserID) throws Exception {
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
        List<String> palletIdList = new ArrayList<>();
        List<PutAwayLineConfirm> refDocNumbers = new ArrayList<>();

        try {
        	if (newPutAwayLines != null && !newPutAwayLines.isEmpty()) {
        		statusDescription = getStatusDescription(20L, newPutAwayLines.get(0).getLanguageId());
        	}

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
                boolean isDuplicate = refDocNumbers.stream().anyMatch(
                        n -> n.getRefDocNumber() != null && n.getRefDocNumber().equalsIgnoreCase(finalRefDocNumber));
                log.info("isDuplicate: " + isDuplicate);
                if (!isDuplicate) {
                    refDocNumbers.add(putAwayLineConfirm);
                }

                StorageBinV2 dbStorageBin = null;
                try {
                    dbStorageBin = storageBinService.getStorageBinV2(companyCode, plantId, languageId, warehouseId,
                            newPutAwayLine.getConfirmedStorageBin());
                } catch (Exception e) {
                    throw new BadRequestException("Invalid StorageBin --> " + newPutAwayLine.getConfirmedStorageBin());
                }

                PutAwayHeaderV2 putAwayHeader = putAwayHeaderService.getPutAwayHeaderV3(companyCode, plantId,
                        warehouseId, languageId, newPutAwayLine.getPutAwayNumber(), newPutAwayLine.getPreInboundNo(),
                        newPutAwayLine.getBarcodeId(), String.valueOf(newPutAwayLine.getLineNo()));
                log.info("putawayHeader: " + putAwayHeader);

                if (dbStorageBin != null) {
                    dbPutAwayLine.setLevelId(String.valueOf(dbStorageBin.getFloorId()));
                }

                StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode,
                        plantId, languageId, warehouseId, preInboundNo, refDocNumber, newPutAwayLine.getLineNo(),
                        itemCode, newPutAwayLine.getManufacturerName());
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

                    if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                        dbPutAwayLine.setParentProductionOrderNo(putAwayHeader.getParentProductionOrderNo());
                    }

                    if (newPutAwayLine.getManufacturerDate() == null) {
                        dbPutAwayLine.setManufacturerDate(putAwayHeader.getManufacturerDate());
                    }
                    if (newPutAwayLine.getExpiryDate() == null) {
                        dbPutAwayLine.setExpiryDate(putAwayHeader.getExpiryDate());
                    }

                    if (putAwayHeader.getTeamMember1() != null) {
                        dbPutAwayLine.setTeamMember1(putAwayHeader.getTeamMember1());
                    }
                    if (putAwayHeader.getTeamMember2() != null) {
                        dbPutAwayLine.setTeamMember2(putAwayHeader.getTeamMember2());
                    }
                    if (putAwayHeader.getTeamMember3() != null) {
                        dbPutAwayLine.setTeamMember3(putAwayHeader.getTeamMember3());
                    }
                    if (putAwayHeader.getTeamMember4() != null) {
                        dbPutAwayLine.setTeamMember4(putAwayHeader.getTeamMember4());
                    }
                    if (putAwayHeader.getTeamMember5() != null) {
                        dbPutAwayLine.setTeamMember5(putAwayHeader.getTeamMember5());
                    }
                } else {
                    dbPutAwayLine.setCreatedOn(new Date());
                }
                dbPutAwayLine.setUpdatedOn(new Date());
                dbPutAwayLine.setConfirmedOn(new Date());

                String palletId = dbPutAwayLine.getPalletId();
                boolean palletExist = palletIdList.stream().anyMatch(n -> n.equalsIgnoreCase(palletId));
                log.info("PalletId exits : " + palletExist);
                if (!palletExist) {
                    palletIdList.add(palletId);
                    assignedOn = palletIdAssignmentRepository.getAssignedOn(companyCode, plantId, languageId, warehouseId, palletId);
                }
                dbPutAwayLine.setAssignedOn(assignedOn);
                log.info("PalletId assignedOn : " + assignedOn + "|" + palletId);

                boolean existingPutAwayLine = putAwayLineV2Repository
                        .existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndRefDocNumberAndDeletionIndicator(
                                companyCode, plantId, languageId, warehouseId, newPutAwayLine.getBarcodeId(),
                                refDocNumber, 0L);
                log.info("Existing putawayline already created : " + existingPutAwayLine);

                try {
                    String leadTime = putAwayLineV2Repository.getLeadTimeV3(dbPutAwayLine.getAssignedOn(),
                            new Date());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BadRequestException("Exception : " + e);
                }

                PutAwayLineV2 createdPutAwayLine = null;
                if (!existingPutAwayLine) {
                    createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                    createdPutAwayLines.add(createdPutAwayLine);
                    log.info("---------->createdPutAwayLine created: " + createdPutAwayLine);
                } else {
                    createdPutAwayLine = dbPutAwayLine;
                    createdPutAwayLines.add(createdPutAwayLine);
                    log.info("HU Serial Number already exist in PutAwayLine ------ BarcodeId ====  {}", newPutAwayLine.getBarcodeId());
                }

                if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                    // Updating StorageBin StatusId as '1'
                    dbStorageBin.setStatusId(1L);
                    storageBinV2Repository.updateStorageBin(companyCode, plantId, languageId, warehouseId, dbPutAwayLine.getConfirmedStorageBin(), 1L);

//                    storageBinService.updateStorageBinV2(dbPutAwayLine.getConfirmedStorageBin(), dbStorageBin,
//                            companyCode, plantId, languageId, warehouseId, loginUserID);

                    if (putAwayHeader != null) {
                        String confirmedStorageBin = createdPutAwayLine.getConfirmedStorageBin();
                        String proposedStorageBin = putAwayHeader.getProposedStorageBin();
                        log.info("putawayConfirmQty, putawayQty: " + createdPutAwayLine.getPutawayConfirmedQty()
                                + ", " + putAwayHeader.getPutAwayQuantity());

                        putAwayHeader.setStatusId(20L);
                        log.info("PutawayHeader StatusId : 20");
                        
                        putAwayHeader.setStatusDescription(statusDescription);
//                        putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                        putAwayHeaderV2Repository.updateHeaderStatus (companyCode, plantId, languageId, warehouseId, 20L, statusDescription, 
                        		createdPutAwayLine.getItemCode(), createdPutAwayLine.getBarcodeId());
                        log.info("-----putAwayHeader status updated as 20L------>: " + putAwayHeader);

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
                                throw new BadRequestException("sum of confirm Putaway line qty is greater than assigned putaway header qty");
                            }

                            if (dbPutawayQty <= dbAssignedPutawayQty) {
                                if (proposedStorageBin.equalsIgnoreCase(confirmedStorageBin)) {
                                    log.info("New PutawayHeader Creation: ");
                                    PutAwayHeaderV2 newPutAwayHeader = new PutAwayHeaderV2();
                                    BeanUtils.copyProperties(putAwayHeader, newPutAwayHeader, CommonUtils.getNullPropertyNames(putAwayHeader));

                                    // PA_NO
                                    long NUM_RAN_CODE = 7;
                                    String nextPANumber = getNextRangeNumber(NUM_RAN_CODE, companyCode, plantId, languageId, warehouseId);
                                    newPutAwayHeader.setPutAwayNumber(nextPANumber); // PutAway Number
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

                                    Double PUTAWAY_QTY = dbAssignedPutawayQty - dbPutawayQty;
                                    if (PUTAWAY_QTY < 0) {
                                        throw new BadRequestException("total confirm qty greater than putaway qty");
                                    }

                                    newPutAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                    log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);

                                    newPutAwayHeader.setStatusId(19L);
                                    log.info("PutawayHeader StatusId : 19");

                                    statusDescription = getStatusDescription(newPutAwayHeader.getStatusId(),
                                            createdPutAwayLine.getLanguageId());
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
//                                    putAwayHeader.setPutAwayQuantity(PUTAWAY_QTY);
                                    log.info("OrderQty ReCalcuated/Changed : " + PUTAWAY_QTY);
//                                    putAwayHeader.setStatusId(19L);

                                    log.info("PutawayHeader StatusId : 19");
                                    statusDescription = getStatusDescription(putAwayHeader.getStatusId(), createdPutAwayLine.getLanguageId());
//                                    putAwayHeader.setStatusDescription(statusDescription);
//                                    putAwayHeader = putAwayHeaderV2Repository.save(putAwayHeader);
                                    putAwayHeaderV2Repository.updatePutAwayHeaderV2 (companyCode, plantId, languageId, warehouseId, 
                                    		createdPutAwayLine.getItemCode(), createdPutAwayLine.getBarcodeId(), 19L, statusDescription, 
                                    		PUTAWAY_QTY, putAwayHeader.getReferenceField1(), putAwayHeader.getReferenceField2(), 
                                    		putAwayHeader.getReferenceField4());
                                    log.info("putAwayHeader updated: " + putAwayHeader);
                                }
                            }
                        }
                    }

                    try {
                        updateInboudLine(createdPutAwayLine);
                    } catch (Exception e1) {
                        log.error("Error on updateInboudLine: " + e1.toString());
                        e1.printStackTrace();
                    }

                    // Create Inventory
                    try {
                        inventoryService.createInventoryNonCBMV3(createdPutAwayLine, loginUserID);
                    } catch (Exception e) {
                        log.error("Error on Inventory: " + e.toString());
                        e.printStackTrace();
                    }
                }
            }

            if(refDocNumbers !=null && !refDocNumbers.isEmpty()) {
                log.info("refDocNumbers : " + refDocNumbers);
                for (PutAwayLineConfirm docNumber : refDocNumbers) {
                    putAwayLineV2Repository.updateInboundHeaderRxdLinesCountProc(companyCode, plantId, languageId,
                            warehouseId, docNumber.getRefDocNumber(), docNumber.getPreInboundNo());
                    log.info("InboundHeader received lines count updated: " + docNumber.getRefDocNumber() + ", "
                            + docNumber.getPreInboundNo());

                    inboundConfirmValidation(companyCode, plantId, languageId, warehouseId, docNumber.getRefDocNumber(),
                            docNumber.getPreInboundNo(), loginUserID);
                }
            }
            return createdPutAwayLines;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param createdPutAwayLine
     * @throws Exception
     */
//    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class,
//            UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    private void updateInboudLine(PutAwayLine createdPutAwayLine) throws Exception {
        try {
            /*--------------------- INBOUNDTABLE Updates------------------------------------------*/
            // Pass WH_ID/PRE_IB_NO/REF_DOC_NO/IB_LINE_NO/ITM_CODE values in PUTAWAYLINE
            // table and
            // fetch PA_CNF_QTY values and QTY_TYPE values and updated STATUS_ID as 20
            double addedAcceptQty = 0.0;
            double addedDamageQty = 0.0;

            InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(createdPutAwayLine.getCompanyCode(),
                    createdPutAwayLine.getPlantId(), createdPutAwayLine.getLanguageId(),
                    createdPutAwayLine.getWarehouseId(), createdPutAwayLine.getRefDocNumber(),
                    createdPutAwayLine.getPreInboundNo(), createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());

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

            if (inboundLine.getInboundOrderTypeId() == 5L) { // condition added for final Inbound confirm
                inboundLine.setReferenceField2("true");
            }

            inboundLine.setBatchSerialNumber(createdPutAwayLine.getBatchSerialNumber());
            inboundLine.setManufacturerDate(createdPutAwayLine.getManufacturerDate());
            inboundLine.setExpiryDate(createdPutAwayLine.getExpiryDate());
            inboundLine.setReferenceField2("TRUE");
            inboundLine.setStatusId(20L);
            statusDescription = getStatusDescription(20L, createdPutAwayLine.getLanguageId());
            inboundLine.setStatusDescription(statusDescription);
            inboundLineRepository.delete(inboundLine);
            InboundLineV2 updatedInboundLine = inboundLineV2Repository.saveAndFlush(inboundLine);
            log.info("------updatedInboundLine---updated: " + updatedInboundLine);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preInboundNo
     * @param loginUserID
     */
    private void inboundConfirmValidation (String companyCode, String plantId, String languageId, String warehouseId,
                                           String refDocNumber, String preInboundNo, String loginUserID) {
        //Checking sum of ordered qty with sum of putaway cnf qty excluding reversal status
        Long confirmedLines = inboundHeaderV2Repository.checkAutoConfirmStatus(companyCode, plantId, languageId, warehouseId, refDocNumber, 22L);
        log.info("AutoConfirm Inbound: " + confirmedLines);
        if (confirmedLines == 1) {
            log.info("Initiate Automatic Inbound Confirmation------> " + refDocNumber + "---> " + preInboundNo);
            inboundHeaderService.updateInboundHeaderConfirmV3(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber, loginUserID);
        }
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
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param palletId
     * @param loginUserID
     */
    public void updatePalletIdAssignment (String companyCodeId, String plantId, String languageId, String warehouseId, String palletId, String loginUserID) throws Exception {
        try {
            palletIdAssignmentService.updatePalletIdAssignment(companyCodeId, plantId, languageId, warehouseId, palletId, loginUserID);
        } catch (Exception e) {
            log.error("");
            throw e;
        }
    }

    /**
     *
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
    public boolean getPutAwayLineForReversalV3(String companyCode, String plantId, String languageId, String warehouseId,
                                               String refDocNumber, String itemCode, String manufacturerName, Long lineNumber, String preInboundNo) {
        return putAwayLineV2Repository.existsByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreInboundNoAndItemCodeAndManufacturerNameAndLineNoAndStatusIdAndDeletionIndicator(
                companyCode, languageId, plantId, warehouseId, refDocNumber, preInboundNo, itemCode, manufacturerName, lineNumber, 20L, 0L);
    }

    /**
     *
     * @param companyCode
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param barcodeId
     * @param putAwayNumber
     * @param statusId
     * @return
     */
    public boolean getPutAwayLineByStatusIdV3(String companyCode, String plantId, String languageId,
                                              String warehouseId, String refDocNumber, String barcodeId, String putAwayNumber, Long statusId) {
        return putAwayLineV2Repository.existsByCompanyCodeAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndPutAwayNumberAndStatusIdAndDeletionIndicator
                (companyCode, plantId, warehouseId, languageId, refDocNumber, barcodeId, putAwayNumber, statusId, 0L);
    }

    /**
     * orderManagementLine order allocate staging area auto putawayline confirm
     * @param newPutAwayLine
     * @param putAwayHeader
     * @param loginUserID
     * @throws Exception
     */
    public void putAwayLineConfirmNonCBMV3(PutAwayLineV2 newPutAwayLine, PutAwayHeaderV2 putAwayHeader, String loginUserID) throws Exception {
        log.info("2.newPutAwayLine to confirm : " + newPutAwayLine);
        String itemCode = null;
        String companyCode = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String refDocNumber = null;
        String preInboundNo = null;

        try {

            PutAwayLineV2 dbPutAwayLine = new PutAwayLineV2();
            itemCode = newPutAwayLine.getItemCode();
            companyCode = newPutAwayLine.getCompanyCode();
            plantId = newPutAwayLine.getPlantId();
            languageId = newPutAwayLine.getLanguageId();
            warehouseId = newPutAwayLine.getWarehouseId();
            refDocNumber = newPutAwayLine.getRefDocNumber();
            preInboundNo = newPutAwayLine.getPreInboundNo();

            StagingLineEntityV2 dbStagingLineEntity = stagingLineService.getStagingLineForPutAwayLineV2(companyCode, plantId, languageId, warehouseId, preInboundNo, refDocNumber,
                    newPutAwayLine.getLineNo(), itemCode, newPutAwayLine.getManufacturerName());

            if (dbStagingLineEntity != null) {
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

            if (dbPutAwayLine.getParentProductionOrderNo() == null) {
                dbPutAwayLine.setParentProductionOrderNo(putAwayHeader.getParentProductionOrderNo());
            }
            dbPutAwayLine.setUpdatedOn(new Date());
            dbPutAwayLine.setConfirmedOn(new Date());

            boolean existingPutAwayLine = putAwayLineV2Repository.existsByCompanyCodeAndPlantIdAndLanguageIdAndWarehouseIdAndBarcodeIdAndRefDocNumberAndDeletionIndicator(
                    companyCode, plantId, languageId, warehouseId, newPutAwayLine.getBarcodeId(), refDocNumber, 0L);
            log.info("2.Existing putawayline already created : " + existingPutAwayLine);

            if (existingPutAwayLine) {
                throw new BadRequestException("2.HU Serial Number already exist..! + " + newPutAwayLine.getBarcodeId());
            }

            if (!existingPutAwayLine) {
                try {
                    String leadTime = putAwayLineV2Repository.getLeadTimeV3(dbPutAwayLine.getAssignedOn(), new Date());
                    dbPutAwayLine.setReferenceField1(leadTime);
                    log.info("2.LeadTime: " + leadTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                PutAwayLineV2 createdPutAwayLine = putAwayLineV2Repository.save(dbPutAwayLine);
                log.info("---------->2.createdPutAwayLine created: " + createdPutAwayLine);

                if (createdPutAwayLine != null && createdPutAwayLine.getPutawayConfirmedQty() > 0L) {
                    putAwayHeaderV2Repository.updateHeaderStatus(companyCode, plantId, languageId, warehouseId, 20L, statusDescription, itemCode, createdPutAwayLine.getBarcodeId());

                    /*--------------------- INBOUNDTABLE Updates ------------------------------------------*/
                    double addedAcceptQty = 0.0;
                    double addedVarQty = 0.0;

                    InboundLineV2 inboundLine = inboundLineService.getInboundLineV2(
                            createdPutAwayLine.getCompanyCode(), createdPutAwayLine.getPlantId(),
                            createdPutAwayLine.getLanguageId(), createdPutAwayLine.getWarehouseId(),
                            createdPutAwayLine.getRefDocNumber(), createdPutAwayLine.getPreInboundNo(),
                            createdPutAwayLine.getLineNo(), createdPutAwayLine.getItemCode());

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
                        addedVarQty = (inboundLine.getOrderQty() - addedAcceptQty);
                    }
                    statusDescription = getStatusDescription(20L, createdPutAwayLine.getLanguageId());
                    inboundLineV2Repository.updateInboundLineV2 (companyCode, plantId, warehouseId, refDocNumber, itemCode,
                            createdPutAwayLine.getLineNo(), 20L, statusDescription, addedAcceptQty, addedVarQty, "TRUE");

                    log.info("----------updateInboundLineV2---1----##---> {}, {}, {}, {}, {}, {}, {}, {}, {}",
                            companyCode, plantId, warehouseId, refDocNumber, itemCode, createdPutAwayLine.getLineNo(),
                            statusDescription, addedAcceptQty, addedVarQty);
                    log.info("----------updateInboundLineV2---updated----##---");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}