package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData;
import com.tekclover.wms.api.transaction.model.dto.ImBasicData1;
import com.tekclover.wms.api.transaction.model.dto.ImPartner;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.StorageBinV2;
import com.tekclover.wms.api.transaction.model.dto.UpdateBarcodeInput;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupHeader;
import com.tekclover.wms.api.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.SearchPickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.UpdatePickupLine;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.SearchPickupLineV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.PreOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.model.trans.InventoryTrans;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.InventoryTransRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PickupHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupLineRepository;
import com.tekclover.wms.api.transaction.repository.PickupLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.StorageBinRepository;
import com.tekclover.wms.api.transaction.repository.specification.PickupLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.PickupLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import com.tekclover.wms.api.transaction.util.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PickupLineService extends BaseService {

    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private PickupLineRepository pickupLineRepository;

    @Autowired
    private PickupHeaderRepository pickupHeaderRepository;

    @Autowired
    private PickupHeaderService pickupHeaderService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @Autowired
    private OutboundLineService outboundLineService;

    @Autowired
    private OutboundHeaderService outboundHeaderService;

    @Autowired
    private PreOutboundHeaderService preOutboundHeaderService;

    @Autowired
    private OutboundHeaderRepository outboundHeaderRepository;

    @Autowired
    private PreOutboundHeaderRepository preOutboundHeaderRepository;

    @Autowired
    private QualityHeaderService qualityHeaderService;
    
    @Autowired
    private QualityLineService qualityLineService;
    
    @Autowired
    private MastersService mastersService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    StorageBinService storageBinService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    
    @Autowired
    private PickupHeaderV2Repository pickupHeaderV2Repository;
    
    @Autowired
    private OutboundHeaderV2Repository outboundHeaderV2Repository;
    
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    
    @Autowired
    private PickupLineV2Repository pickupLineV2Repository;
    
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    private OrderManagementLineService orderManagementLineService;

    @Autowired
    private StorageBinRepository storageBinRepository;

    @Autowired
    private InventoryTransRepository inventoryTransRepository;
    
    @Autowired
    private ImPartnerService imPartnerService;

    @Autowired
    IDMasterService idMasterService;

    @Autowired
    PushNotificationService pushNotificationService;

    @Autowired
    PutAwayHeaderService putAwayHeaderService;

    //------------------------------------------------------------------------------------------------------

    /**
     * getPickupLines
     *
     * @return
     */
    public List<PickupLine> getPickupLines() {
        List<PickupLine> pickupLineList = pickupLineRepository.findAll();
        pickupLineList = pickupLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return pickupLineList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public PickupLine getPickupLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                    Long lineNumber, String itemCode) {
        PickupLine pickupLine = pickupLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<PickupLine> getPickupLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                     String partnerCode, Long lineNumber, String itemCode) {
        List<PickupLine> pickupLine = pickupLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        throw new BadRequestException("The given PickupLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param actualHeNo
     * @return
     */
    private PickupLine getPickupLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
                                              String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                              String pickedPackCode, String actualHeNo) {
        PickupLine pickupLine = pickupLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, pickupNumber, itemCode,
                        pickedStorageBin, pickedPackCode, actualHeNo, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",pickupNumber:" + pickupNumber + ",itemCode:" + itemCode
                + ",pickedStorageBin:" + pickedStorageBin + ",pickedPackCode:" + pickedPackCode + ",actualHeNo:"
                + actualHeNo + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public PickupLine getPickupLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
                                             String partnerCode, Long lineNumber, String itemCode) {
        PickupLine pickupLine = pickupLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<PickupLine> getPickupLineForUpdateConfirmation(String warehouseId, String preOutboundNo,
                                                               String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<PickupLine> pickupLine = pickupLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumbers
     * @param itemCodes
     * @return
     */
    public List<PickupLine> getPickupLine(String warehouseId, String preOutboundNo,
                                          String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
        List<PickupLine> pickupLine = pickupLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public Double getPickupLineCount(String warehouseId, String preOutboundNo, List<String> refDocNumber) {
        Double pickupLineCount = pickupLineRepository.getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                warehouseId, preOutboundNo, refDocNumber);
        if (pickupLineCount != null) {
            return pickupLineCount;
        }
        return 0D;
    }

    /**
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public double getPickupLineCount(String languageId, String companyCode, String plantId, String warehouseId,
                                     List<String> preOutboundNo, List<String> refDocNumber, String partnerCode) {
        List<PickupLine> pickupLineList = pickupLineRepository
                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndPartnerCodeAndStatusIdAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 50L, 0L);
        if (pickupLineList != null && !pickupLineList.isEmpty()) {
            return pickupLineList.size();
        }
        return 0;

//		throw new BadRequestException("The given PickupLine ID : " + "warehouseId:" + warehouseId
//				+ ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + " doesn't exist.");
    }

    /**
     * @param searchPickupLine
     * @return
     * @throws ParseException
     */
    public List<PickupLine> findPickupLine(SearchPickupLine searchPickupLine)
            throws ParseException, java.text.ParseException {

        if (searchPickupLine.getFromPickConfirmedOn() != null && searchPickupLine.getToPickConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickupLine.getFromPickConfirmedOn(),
                    searchPickupLine.getToPickConfirmedOn());
            searchPickupLine.setFromPickConfirmedOn(dates[0]);
            searchPickupLine.setToPickConfirmedOn(dates[1]);
        }
        PickupLineSpecification spec = new PickupLineSpecification(searchPickupLine);
        List<PickupLine> results = pickupLineRepository.findAll(spec);
        return results;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param OB_ORD_TYP_ID
     * @param proposedPackBarCode
     * @param proposedStorageBin
     * @return
     */
    public List<Inventory> getAdditionalBins(String warehouseId, String itemCode, Long OB_ORD_TYP_ID,
                                             String proposedPackBarCode, String proposedStorageBin) {
        log.info("---OB_ORD_TYP_ID--------> : " + OB_ORD_TYP_ID);

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
            List<Inventory> inventoryAdditionalBins = fetchAdditionalBins(storageSectionIds, warehouseId, itemCode,
                    proposedPackBarCode, proposedStorageBin);
            return inventoryAdditionalBins;
        }

        /*
         * Pass the selected
         * ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2/SP_ST_IND_ID=2 for
         * OB_ORD_TYP_ID = 2 and fetch ST_BIN / PACK_BARCODE / INV_QTY values and
         * display
         */
        if (OB_ORD_TYP_ID == 2L) {
            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
            List<Inventory> inventoryAdditionalBins = fetchAdditionalBinsForOB2(storageSectionIds, warehouseId,
                    itemCode, proposedPackBarCode, proposedStorageBin);
            return inventoryAdditionalBins;
        }
        return null;
    }

    /**
     * createPickupLine
     *
     * @param newPickupLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PickupLine> createPickupLine(@Valid List<AddPickupLine> newPickupLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        Long STATUS_ID = 0L;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String pickupNumber = null;
        boolean isQtyAvail = false;

        List<AddPickupLine> dupPickupLines = getDuplicates(newPickupLines);
        log.info("-------dupPickupLines--------> " + dupPickupLines);
        if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
            newPickupLines.removeAll(dupPickupLines);
            newPickupLines.add(dupPickupLines.get(0));
            log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
        }

        // Create PickUpLine
        List<PickupLine> createdPickupLineList = new ArrayList<>();
        for (AddPickupLine newPickupLine : newPickupLines) {
            PickupLine dbPickupLine = new PickupLine();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));
            Warehouse warehouse = getWarehouse(newPickupLine.getWarehouseId());
            dbPickupLine.setLanguageId(warehouse.getLanguageId());
            dbPickupLine.setCompanyCodeId(warehouse.getCompanyCode());
            dbPickupLine.setPlantId(warehouse.getPlantId());

            // STATUS_ID
            if (newPickupLine.getPickConfirmQty() > 0) {
                isQtyAvail = true;
            }

            if (isQtyAvail) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);

            dbPickupLine.setStatusId(STATUS_ID);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupCreatedBy(loginUserID);
            dbPickupLine.setPickupCreatedOn(new Date());
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());

            // Checking for Duplicates
            PickupLine existingPickupLine = pickupLineRepository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndActualHeNoAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
                    dbPickupLine.getLanguageId(),
                    dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(),
                    dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getPartnerCode(),
                    dbPickupLine.getLineNumber(),
                    dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(),
                    dbPickupLine.getActualHeNo(),
                    dbPickupLine.getPickedStorageBin(),
                    dbPickupLine.getPickedPackCode(),
                    0L);
            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null) {
                PickupLine createdPickupLine = pickupLineRepository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        }

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        for (PickupLine dbPickupLine : createdPickupLineList) {
            //------------------------UpdateLock-Applied------------------------------------------------------------
            Inventory inventory = inventoryService.getInventory(dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin());
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty())
                                - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End

                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);

                        // INV_QTY > 0 then, update Inventory Table
                        inventory = inventoryRepository.save(inventory);
                        log.info("inventory updated : " + inventory);

                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                Inventory inventoryByStBin = inventoryService.getInventoryByStorageBin(warehouseId, inventory.getStorageBin());
                                if (inventoryByStBin == null) {
                                    StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
                                    dbStorageBin.setStatusId(0L);
                                    dbStorageBin.setWarehouseId(dbPickupLine.getWarehouseId());
                                    mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, loginUserID,
                                            authTokenForMastersService.getAccess_token());
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory = inventoryRepository.save(inventory);
                        log.info("inventory updated : " + inventory);

                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        Inventory inventoryBySTBIN = inventoryService.getInventoryByStorageBin(warehouseId, dbPickupLine.getPickedStorageBin());
                        //if (INV_QTY == 0) {
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {

                                // Setting up statusId = 0
                                StorageBin dbStorageBin = mastersService.getStorageBin(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);
                                mastersService.updateStorageBin(inventory.getStorageBin(), dbStorageBin, loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

            // Inserting record in InventoryMovement
            Long subMvtTypeId;
            String movementDocumentNo;
            String stBin;
            String movementQtyValue;
            InventoryMovement inventoryMovement;
            try {
                subMvtTypeId = 1L;
                movementDocumentNo = dbPickupLine.getPickupNumber();
                stBin = dbPickupLine.getPickedStorageBin();
                movementQtyValue = "N";
                inventoryMovement = createInventoryMovement(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);
            } catch (Exception e) {
                log.error("InventoryMovement create Error :" + e.toString());
                e.printStackTrace();
            }

            /*--------------------------------------------------------------------------*/
            // 3. Insert a new record in INVENTORY table as below
            // Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
            // of BIN_CLASS_ID=4
            // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
            // Checking Inventory table before creating new record inventory
            // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE
            Long BIN_CLASS_ID = 4L;
            StorageBin storageBin = mastersService.getStorageBin(dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
                    authTokenForMastersService.getAccess_token());
            Inventory existingInventory = inventoryService.getInventory(dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin());
            if (existingInventory != null) {
                try {
                    Double INV_QTY = existingInventory.getInventoryQuantity() + dbPickupLine.getPickConfirmQty();
                    UpdateInventory updateInventory = new UpdateInventory();
                    updateInventory.setInventoryQuantity(INV_QTY);
                    Inventory updatedInventory = inventoryService.updateInventory(dbPickupLine.getWarehouseId(),
                            dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin(),
                            dbPickupLine.getStockTypeId(), dbPickupLine.getSpecialStockIndicatorId(), updateInventory, loginUserID);
                    log.info("Inventory is Updated : " + updatedInventory);
                } catch (Exception e) {
                    log.error("Inventory update Error :" + e.toString());
                    e.printStackTrace();
                }
            } else {
                if (dbPickupLine.getStatusId() == 50L) {
                    try {
                        AddInventory newInventory = new AddInventory();
                        newInventory.setLanguageId(dbPickupLine.getLanguageId());
                        newInventory.setCompanyCodeId(dbPickupLine.getCompanyCodeId());
                        newInventory.setPlantId(dbPickupLine.getPlantId());
                        newInventory.setBinClassId(BIN_CLASS_ID);
                        newInventory.setStockTypeId(inventory.getStockTypeId());
                        newInventory.setWarehouseId(dbPickupLine.getWarehouseId());
                        newInventory.setPackBarcodes(dbPickupLine.getPickedPackCode());
                        newInventory.setItemCode(dbPickupLine.getItemCode());
                        newInventory.setStorageBin(storageBin.getStorageBin());
                        newInventory.setInventoryQuantity(dbPickupLine.getPickConfirmQty());
                        newInventory.setSpecialStockIndicatorId(dbPickupLine.getSpecialStockIndicatorId());

                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository
                                .findByItemCode(newInventory.getItemCode());
                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
                            newInventory.setReferenceField8(imbasicdata1.get(0).getDescription());
                            newInventory.setReferenceField9(imbasicdata1.get(0).getManufacturePart());
                        }
                        if (storageBin != null) {
                            newInventory.setReferenceField10(storageBin.getStorageSectionId());
                            newInventory.setReferenceField5(storageBin.getAisleNumber());
                            newInventory.setReferenceField6(storageBin.getShelfId());
                            newInventory.setReferenceField7(storageBin.getRowId());
                        }

                        Inventory createdInventory = inventoryService.createInventory(newInventory, loginUserID);
                        log.info("newInventory created : " + createdInventory);
                    } catch (Exception e) {
                        log.error("newInventory create Error :" + e.toString());
                        e.printStackTrace();
                    }
                }
            }

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
                UpdateOutboundLine updateOutboundLine = new UpdateOutboundLine();
                updateOutboundLine.setStatusId(STATUS_ID);
                OutboundLine outboundLine = outboundLineService.updateOutboundLine(dbPickupLine.getWarehouseId(),
                        dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getLineNumber(), dbPickupLine.getItemCode(), loginUserID, updateOutboundLine);
                log.info("outboundLine updated : " + outboundLine);
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ------------------Record insertion in QUALITYHEADER table-----------------------------------
             * Allow to create QualityHeader only
             * for STATUS_ID = 50
             */
            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    AddQualityHeader newQualityHeader = new AddQualityHeader();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader,
                            CommonUtils.getNullPropertyNames(dbPickupLine));

                    // QC_NO
                    /*
                     * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
                     * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
                     * insert
                     */
                    Long NUM_RAN_CODE = 11L;
                    QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getWarehouseId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));

                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
                    StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    newQualityHeader.setReferenceField10(idStatus.getStatus());

                    QualityHeader createdQualityHeader = qualityHeaderService.createQualityHeader(newQualityHeader,
                            loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }

                /*-----------------------InventoryMovement----------------------------------*/
                // Inserting record in InventoryMovement
                try {
                    subMvtTypeId = 2L;
                    movementDocumentNo = QC_NO;
                    stBin = storageBin.getStorageBin();
                    movementQtyValue = "P";
                    inventoryMovement = createInventoryMovement(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                            movementQtyValue, loginUserID);
                    log.info("InventoryMovement created for update2: " + inventoryMovement);
                } catch (Exception e) {
                    log.error("InventoryMovement create Error for update2 :" + e.toString());
                    e.printStackTrace();
                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 47 only if all OutboundLines are STATUS_ID is 51
         */
        List<OutboundLine> outboundLineList = outboundLineService.getOutboundLine(warehouseId, preOutboundNo, refDocNumber);
        boolean hasStatus51 = false;
        List<Long> status51List = outboundLineList.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
        long status51IdCount = status51List.stream().filter(a -> a == 51L || a == 47L).count();
        log.info("status count : " + (status51IdCount == status51List.size()));
        hasStatus51 = (status51IdCount == status51List.size());
        if (!status51List.isEmpty() && hasStatus51) {
            //------------------------UpdateLock-Applied------------------------------------------------------------
            OutboundHeader outboundHeader = outboundHeaderService.getOutboundHeader(refDocNumber, warehouseId);
            outboundHeader.setStatusId(51L);
            outboundHeader.setUpdatedBy(loginUserID);
            outboundHeader.setUpdatedOn(new Date());
            outboundHeaderRepository.save(outboundHeader);
            log.info("outboundHeader updated as 51.");

            //------------------------UpdateLock-Applied------------------------------------------------------------
            PreOutboundHeader preOutboundHeader = preOutboundHeaderService.getPreOutboundHeader(warehouseId, refDocNumber);
            preOutboundHeader.setStatusId(51L);
            preOutboundHeader.setUpdatedBy(loginUserID);
            preOutboundHeader.setUpdatedOn(new Date());
            preOutboundHeaderRepository.save(preOutboundHeader);
            log.info("PreOutboundHeader updated as 51.");
        }

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go o update header-----------*/
        try {
            boolean isStatus51 = false;
            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
                    .collect(Collectors.toList());
            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
            log.info("status count : " + (statusIdCount == statusList.size()));
            isStatus51 = (statusIdCount == statusList.size());
            if (!statusList.isEmpty() && isStatus51) {
                STATUS_ID = 51L;
            } else {
                STATUS_ID = 50L;
            }

            //------------------------UpdateLock-Applied------------------------------------------------------------
            PickupHeader pickupHeader = pickupHeaderService.getPickupHeader(warehouseId, preOutboundNo, refDocNumber,
                    partnerCode, pickupNumber);
            pickupHeader.setStatusId(STATUS_ID);

            StatusId idStatus = idmasterService.getStatus(STATUS_ID, warehouseId, authTokenForIDService.getAccess_token());
            pickupHeader.setReferenceField7(idStatus.getStatus());        // tblpickupheader REF_FIELD_7

            pickupHeader.setPickUpdatedBy(loginUserID);
            pickupHeader.setPickUpdatedOn(new Date());
            pickupHeader = pickupHeaderRepository.save(pickupHeader);
            log.info("PickupHeader updated: " + pickupHeader);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("PickupHeader update error: " + e.toString());
        }
        return createdPickupLineList;
    }

    /**
     * @param newPickupLines
     * @return
     */
    public static List<AddPickupLine> getDuplicates(@Valid List<AddPickupLine> newPickupLines) {
        return getDuplicatesMap(newPickupLines).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static List<AddPickupLine> getDuplicatesV2(@Valid List<AddPickupLine> newPickupLines) {
        return getDuplicatesMapV2(newPickupLines).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param newPickupLines
     * @return
     */
    private static Map<String, List<AddPickupLine>> getDuplicatesMap(@Valid List<AddPickupLine> newPickupLines) {
        return newPickupLines.stream().collect(Collectors.groupingBy(AddPickupLine::uniqueAttributes));
    }

    private static Map<String, List<AddPickupLine>> getDuplicatesMapV2(@Valid List<AddPickupLine> newPickupLines) {
        return newPickupLines.stream().collect(Collectors.groupingBy(AddPickupLine::uniqueAttributes));
    }

    /**
     * @param dbPickupLine
     * @param subMvtTypeId
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovement(PickupLine dbPickupLine, Long subMvtTypeId,
                                                      String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        AddInventoryMovement inventoryMovement = new AddInventoryMovement();
        BeanUtils.copyProperties(dbPickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPickupLine));

        inventoryMovement.setCompanyCodeId(dbPickupLine.getCompanyCodeId());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(3L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(subMvtTypeId);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
        inventoryMovement.setMovementDocumentNo(movementDocumentNo);

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // BAR_CODE
        inventoryMovement.setPackBarcodes(dbPickupLine.getPickedPackCode());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbPickupLine.getPickConfirmQty());

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbPickupLine.getPickUom());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(dbPickupLine.getPickupCreatedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbPickupLine.getPickupCreatedOn());

        InventoryMovement createdInventoryMovement = inventoryMovementService.createInventoryMovement(inventoryMovement,
                loginUserID);
        return createdInventoryMovement;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updatePickupLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLine updatePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                       String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdatePickupLine updatePickupLine)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode);
        if (dbPickupLine != null) {
            BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
                    CommonUtils.getNullPropertyNames(updatePickupLine));
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineRepository.save(dbPickupLine);
        }
        return null;
    }

    public List<PickupLine> updatePickupLineForConfirmation(String warehouseId, String preOutboundNo,
                                                            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                                            UpdatePickupLine updatePickupLine) throws IllegalAccessException, InvocationTargetException {
        List<PickupLine> dbPickupLine = getPickupLineForUpdateConfirmation(warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode);
        if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
            List<PickupLine> toSave = new ArrayList<>();
            for (PickupLine data : dbPickupLine) {
                BeanUtils.copyProperties(updatePickupLine, data, CommonUtils.getNullPropertyNames(updatePickupLine));
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupUpdatedOn(new Date());
                toSave.add(data);
            }
            return pickupLineRepository.saveAll(toSave);
        }
        return null;
    }

    /**
     * @param actualHeNo
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param loginUserID
     * @param updatePickupLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLine updatePickupLine(String actualHeNo, String warehouseId, String preOutboundNo, String refDocNumber,
                                       String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                       String pickedPackCode, String loginUserID, UpdatePickupLine updatePickupLine)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
        if (dbPickupLine != null) {
            BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
                    CommonUtils.getNullPropertyNames(updatePickupLine));
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineRepository.save(dbPickupLine);
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param actualHeNo
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLine deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                       String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String actualHeNo,
                                       String pickedStorageBin, String pickedPackCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine dbPickupLine = getPickupLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
        if (dbPickupLine != null) {
            dbPickupLine.setDeletionIndicator(1L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineRepository.save(dbPickupLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLine deletePickupLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                       String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        PickupLine dbPickupLine = getPickupLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode);
        if (dbPickupLine != null) {
            dbPickupLine.setDeletionIndicator(1L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineRepository.save(dbPickupLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    public List<PickupLine> deletePickupLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                        String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PickupLine> dbPickupLine = getPickupLineForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode);
        if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
            List<PickupLine> toSavePickupLineList = new ArrayList<>();
            dbPickupLine.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupUpdatedOn(new Date());
                toSavePickupLineList.add(data);
            });
            return pickupLineRepository.saveAll(toSavePickupLineList);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param storageSectionIds
     * @param warehouseId
     * @param itemCode
     * @param proposedPackBarCode
     * @param proposedStorageBin
     * @return
     */
    private List<Inventory> fetchAdditionalBins(List<String> storageSectionIds, String warehouseId, String itemCode,
                                                String proposedPackBarCode, String proposedStorageBin) {
        List<Inventory> finalizedInventoryList = new ArrayList<>();
        List<Inventory> listInventory = inventoryService.getInventoryForAdditionalBins(warehouseId, itemCode,
                storageSectionIds);
        log.info("selected listInventory--------: " + listInventory);
        boolean toBeIncluded = false;
        for (Inventory inventory : listInventory) {
            if (inventory.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)) {
                toBeIncluded = false;
                log.info("toBeIncluded----Pack----: " + toBeIncluded);
                if (inventory.getStorageBin().equalsIgnoreCase(proposedStorageBin)) {
                    toBeIncluded = false;
                } else {
                    toBeIncluded = true;
                }
            } else {
                toBeIncluded = true;
            }

            log.info("toBeIncluded--------: " + toBeIncluded);
            if (toBeIncluded) {
                finalizedInventoryList.add(inventory);
            }
        }
        return finalizedInventoryList;
    }

    /**
     * @param storageSectionIds
     * @param warehouseId
     * @param itemCode
     * @return
     */
    private List<Inventory> fetchAdditionalBinsForOB2(List<String> storageSectionIds, String warehouseId,
                                                      String itemCode, String proposedPackBarCode, String proposedStorageBin) {
        List<Inventory> listInventory = inventoryService.getInventoryForAdditionalBinsForOB2(warehouseId, itemCode,
                storageSectionIds, 1L /* STCK_TYP_ID */);
        listInventory = listInventory.stream().filter(i -> !i.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode))
                .collect(Collectors.toList());
        listInventory = listInventory.stream().filter(i -> !i.getStorageBin().equalsIgnoreCase(proposedStorageBin))
                .collect(Collectors.toList());
        return listInventory;
    }

//	private List<Inventory> fetchAdditionalBins (List<String> stBins, List<String> storageSectionIds, 
//			String warehouseId, String itemCode, String proposedPackBarCode, String proposedStorageBin) {
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		List<Inventory> responseInventoryList = new ArrayList<>();
//		
//		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//		storageBinPutAway.setStorageBin(stBins);
//		storageBinPutAway.setStorageSectionIds(storageSectionIds);
//		storageBinPutAway.setWarehouseId(warehouseId);
//		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//		log.info("storageBin : " + Arrays.asList(storageBin));
//		
//		if (storageBin != null && storageBin.length > 0) {
//			// Pass the filtered ST_BIN/WH_ID/ITM_CODE/BIN_CL_ID=01/STCK_TYP_ID=1 in Inventory table and 
//			
//			List<Inventory> finalizedInventoryList = new ArrayList<>();
//			for (StorageBin dbStorageBin : storageBin) {
//				List<Inventory> listInventory = 
//						inventoryService.getInventoryForAdditionalBins (warehouseId, itemCode, dbStorageBin.getStorageBin());
//				log.info("selected listInventory--------: " + listInventory);
//				boolean toBeIncluded = false;
//				for (Inventory inventory : listInventory) {
//					if (inventory.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)) {
//						toBeIncluded = false;
//						log.info("toBeIncluded----Pack----: " + toBeIncluded);
//						if (inventory.getStorageBin().equalsIgnoreCase(proposedStorageBin)) {
//							toBeIncluded = false;
//						} else {
//							toBeIncluded = true;
//						}
//					} else {
//						toBeIncluded = true;
//					}
//					
//					log.info("toBeIncluded--------: " + toBeIncluded);
//					if (toBeIncluded) {
//						finalizedInventoryList.add(inventory);
//					}
//				}
//			}
//			return finalizedInventoryList;
//		}
//		return responseInventoryList;
//	}
//	
//	/**
//	 * 
//	 * @param storageSectionIds
//	 * @param warehouseId
//	 * @param itemCode
//	 * @return
//	 */
//	private List<Inventory> fetchAdditionalBinsForOB2 (List<String> stBins, List<String> storageSectionIds, 
//			String warehouseId, String itemCode, String proposedPackBarCode, String proposedStorageBin) {
//		AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
//		List<Inventory> responseInventoryList = new ArrayList<>();
//		StorageBinPutAway storageBinPutAway = new StorageBinPutAway();
//		storageBinPutAway.setStorageBin(stBins);
//		storageBinPutAway.setStorageSectionIds(storageSectionIds);
//		storageBinPutAway.setWarehouseId(warehouseId);
//		StorageBin[] storageBin = mastersService.getStorageBin(storageBinPutAway, authTokenForMastersService.getAccess_token());
//		if (storageBin != null && storageBin.length > 0) {
//			/* Discussed to remove SP_INND_ID parameter from get */
//			// Pass the selected ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2 for OB_ORD_TYP_ID = 2
//			for (StorageBin dbStorageBin : storageBin) {
//				List<Inventory> listInventory = 
//						inventoryService.getInventoryForAdditionalBinsForOB2(warehouseId, itemCode, 
//									dbStorageBin.getStorageBin(), 1L /*STCK_TYP_ID*/);
//				listInventory = listInventory.stream().filter(i -> !i.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)).collect(Collectors.toList());
//				listInventory = listInventory.stream().filter(i -> !i.getStorageBin().equalsIgnoreCase(proposedStorageBin)).collect(Collectors.toList());
//				responseInventoryList.addAll(listInventory);
//			}
//		}
//		return responseInventoryList;
//	}

    //==========================================================================V2========================================================================

    /**
     * getPickupLines
     *
     * @return
     */
    public List<PickupLineV2> getPickupLinesV2() {
        List<PickupLineV2> pickupLineList = pickupLineV2Repository.findAll();
        pickupLineList = pickupLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return pickupLineList;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public PickupLineV2 getPickupLineV2(String companyCodeId, String plantId, String languageId,
                                        String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                        Long lineNumber, String itemCode) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public PickupLineV2 getPickupLineV2(String companyCodeId, String plantId, String languageId,
                                        String warehouseId, String actualHeNo, String pickupNumber,
                                        String preOutboundNo, String refDocNumber, String partnerCode,
                                        Long lineNumber, String itemCode) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndActualHeNoAndPickupNumberAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, actualHeNo, pickupNumber, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<PickupLineV2> getPickupLineForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String preOutboundNo, String refDocNumber,
                                                         String partnerCode, Long lineNumber, String itemCode, String manufacturerName) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        throw new BadRequestException("The given PickupLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param barcodeId
     * @return
     */
    public PickupLineV2 getPickupLineForReversalV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String refDocNumber, String barcodeId) {
        return pickupLineV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndBarcodeIdAndStatusIdNotAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, barcodeId, 22L, 0L);
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param lineNumber
     * @return
     */
    public List<PickupLineV2> getPickupLineForReversalV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                         String refDocNumber, String itemCode, Long lineNumber) {
        return pickupLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndLineNumberAndStatusIdNotAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, itemCode, lineNumber, 22L, 0L);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @return
     */
    public List<PickupLineV2> getPickupLineForPerpetualCountV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                               String itemCode, String manufacturerName, String storageBin, Date stockCountDate) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
              .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicator(
                    languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, 50L, stockCountDate, new Date(), 0L);
//        List<PickupLineV2> pickupLine = pickupLineV2Repository
//                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndItemCodeAndManufacturerNameAndPickedStorageBinAndStatusIdAndDeletionIndicator(
//                        languageId, companyCodeId, plantId, warehouseId, itemCode, manufacturerName, storageBin, 50L, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            log.info("PickUpline Status 50 ---> " + pickupLine);
            return pickupLine;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @return
     */
    public List<PickupLineV2> getPickupLineAutomation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                      List<String> assignedPickerId) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdInAndDeletionIndicatorOrderByPickupConfirmedOn(
                        companyCodeId, plantId, languageId, warehouseId, 50L, assignedPickerId, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        } else {
            return null;
//            throw new BadRequestException("The PickupLine doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @return
     * @throws java.text.ParseException
     */
    public PickupLineV2 getPickupLineAutomateCurrentDateNew(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                            List<String> assignedPickerId) throws java.text.ParseException {
        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdInAndDeletionIndicatorAndPickupConfirmedOnBetweenOrderByPickupConfirmedOn(
                        companyCodeId, plantId, languageId, warehouseId, 50L, assignedPickerId, 0L, dates[0], dates[1]);
            return pickupLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @return
     * @throws java.text.ParseException
     */
    public List<PickupLineV2> getPickupLineAutomateCurrentDate(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                               String assignedPickerId) throws java.text.ParseException {

        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());

        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndStatusIdAndAssignedPickerIdAndDeletionIndicatorAndPickupConfirmedOnBetweenOrderByPickupConfirmedOn(
                        companyCodeId, plantId, languageId, warehouseId, 50L, assignedPickerId, 0L, dates[0], dates[1]);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        } else {
            return null;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @return
     */
    public String getAssignedPickerPickupLineAutomation(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        List<String> assignedPickerId) {
        String assignedPicker = pickupLineV2Repository.getHHTUser(assignedPickerId, companyCodeId, plantId, languageId, warehouseId);
        if (assignedPicker != null && !assignedPicker.isEmpty()) {
            return assignedPicker;
        } else {
            return null;
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param actualHeNo
     * @return
     */
    private PickupLineV2 getPickupLineForUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                                  String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                                  String pickedPackCode, String actualHeNo) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndActualHeNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, pickupNumber, itemCode,
                        pickedStorageBin, pickedPackCode, actualHeNo, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        throw new BadRequestException("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId
                + ",preOutboundNo:" + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode
                + ",lineNumber:" + lineNumber + ",pickupNumber:" + pickupNumber + ",itemCode:" + itemCode
                + ",pickedStorageBin:" + pickedStorageBin + ",pickedPackCode:" + pickedPackCode + ",actualHeNo:"
                + actualHeNo + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public PickupLineV2 getPickupLineForUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                                 String partnerCode, Long lineNumber, String itemCode) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null) {
            return pickupLine;
        }
        log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public List<PickupLineV2> getPickupLineForUpdateConfirmationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                   String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
        return null;
    }
    
    /**
     * --------------------------------------WALKAROO CHANGES-----------------------------------------------------------------
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param itemCode
     * @return
     */
	public List<PickupLineV2> getPickupLineV3 (String companyCodeId, String plantId,
			String languageId, String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, String itemCode) {
		List<PickupLineV2> pickupLine = pickupLineV2Repository
				.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndItemCodeAndDeletionIndicator(
						companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, itemCode, 0L);
		if (pickupLine != null && !pickupLine.isEmpty()) {
			return pickupLine;
		}
		log.info("The given OrderManagementLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
				+ preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + "," + ",itemCode:" + itemCode + " doesn't exist.");
		return null;
	}

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumbers
     * @param itemCodes
     * @return
     */
    public List<PickupLineV2> getPickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                              String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        return null;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param manufacturerName
     * @param itemCode
     * @return
     */
    public List<PickupLineV2> getPickupLineForLastBinCheckV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                             String itemCode, String manufacturerName) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorOrderByPickupConfirmedOnDesc(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L);
        if (pickupLine != null && !pickupLine.isEmpty()) {
            return pickupLine;
        }
        return null;
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
    public PickupLineV2 getPickupLineForLastBinCheck(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                     String itemCode, String manufacturerName) {
        PickupLineV2 pickupLine = pickupLineV2Repository
                .findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndDeletionIndicatorAndPickedStorageBinNotOrderByPickupConfirmedOnDesc(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 0L, DIRECT_STOCK_BIN);
        if (pickupLine != null) {
            return pickupLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public Double getPickupLineCountV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, List<String> refDocNumber) {
        Double pickupLineCount = pickupLineV2Repository.getCountByWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicatorV2(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        if (pickupLineCount != null) {
            return pickupLineCount;
        }
        return 0D;
    }

    /**
     *
     * @param languageId
     * @param companyCode
     * @param plantId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param outboundOrderTypeId
     * @return
     */
    public double getPickupLineCountV2(String languageId, String companyCode, String plantId, String warehouseId,
                                       List<String> preOutboundNo, List<String> refDocNumber, Long outboundOrderTypeId) {
        List<PickupLineV2> pickupLineList = pickupLineV2Repository
                .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoInAndRefDocNumberInAndOutboundOrderTypeIdAndStatusIdAndDeletionIndicator(
                        languageId, companyCode, plantId, warehouseId, preOutboundNo, refDocNumber, outboundOrderTypeId, 50L, 0L);
        if (pickupLineList != null && !pickupLineList.isEmpty()) {
            return pickupLineList.size();
        }
        return 0;
    }

    /**
     * @param searchPickupLine
     * @return
     * @throws ParseException
     */
    public Stream<PickupLineV2> findPickupLineV2(SearchPickupLineV2 searchPickupLine)
            throws ParseException, java.text.ParseException {

        if (searchPickupLine.getFromPickConfirmedOn() != null && searchPickupLine.getToPickConfirmedOn() != null) {
            Date[] dates = DateUtils.addTimeToDatesForSearch(searchPickupLine.getFromPickConfirmedOn(),
                    searchPickupLine.getToPickConfirmedOn());
            searchPickupLine.setFromPickConfirmedOn(dates[0]);
            searchPickupLine.setToPickConfirmedOn(dates[1]);
        }
        PickupLineV2Specification spec = new PickupLineV2Specification(searchPickupLine);
        Stream<PickupLineV2> results = pickupLineV2Repository.stream(spec, PickupLineV2.class);
        return results;
    }

    /**
     * @param warehouseId
     * @param itemCode
     * @param OB_ORD_TYP_ID
     * @param proposedPackBarCode
     * @param proposedStorageBin
     * @return
     */
//    public List<InventoryV2> getAdditionalBinsV2(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, Long OB_ORD_TYP_ID,
//                                                 String proposedPackBarCode, String proposedStorageBin) {
//        log.info("---OB_ORD_TYP_ID--------> : " + OB_ORD_TYP_ID);
//
//        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
//            List<InventoryV2> inventoryAdditionalBins = fetchAdditionalBinsV2(companyCodeId, plantId, languageId, storageSectionIds, warehouseId, itemCode,
//                    proposedPackBarCode, proposedStorageBin);
//            return inventoryAdditionalBins;
//        }
    public List<IInventoryImpl> getAdditionalBinsV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String itemCode, Long OB_ORD_TYP_ID,
                                                    String proposedPackBarCode, String proposedStorageBin, String manufacturerName) {
        log.info("---OB_ORD_TYP_ID--------> : " + OB_ORD_TYP_ID);

        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
            List<IInventoryImpl> inventoryAdditionalBins = fetchAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    proposedPackBarCode, proposedStorageBin, manufacturerName, 1L);
            return inventoryAdditionalBins;
//            return null;
        }

        /*
         * Pass the selected
         * ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2/SP_ST_IND_ID=2 for
         * OB_ORD_TYP_ID = 2 and fetch ST_BIN / PACK_BARCODE / INV_QTY values and
         * display
         */
        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
//            List<InventoryV2> inventoryAdditionalBins = fetchAdditionalBinsForOB2V2(companyCodeId, plantId, languageId, storageSectionIds, warehouseId,
//                    itemCode, proposedPackBarCode, proposedStorageBin);
//            return inventoryAdditionalBins;
            List<IInventoryImpl> inventoryAdditionalBins = fetchAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    proposedPackBarCode, proposedStorageBin, manufacturerName, 7L);
            return inventoryAdditionalBins;
        }
        return null;
    }

    /**
     * @param newPickupLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
//    @Transactional
    public List<PickupLineV2> createPickupLineNonCBMV2(@Valid List<AddPickupLine> newPickupLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        log.info("login UserId : {}" , loginUserID);
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Long STATUS_ID = 0L;
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String pickupNumber = null;
        String itemCode = null;
        String manufacturerName = null;
        boolean isQtyAvail = false;

        List<AddPickupLine> dupPickupLines = getDuplicatesV2(newPickupLines);
        log.info("-------dupPickupLines--------> " + dupPickupLines);
        if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
            newPickupLines.removeAll(dupPickupLines);
            newPickupLines.add(dupPickupLines.get(0));
            log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
        }

        // Create PickUpLine
        List<PickupLineV2> createdPickupLineList = new ArrayList<>();
        for (AddPickupLine newPickupLine : newPickupLines) {
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));

            dbPickupLine.setLanguageId(newPickupLine.getLanguageId());
            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));
            dbPickupLine.setPlantId(newPickupLine.getPlantId());

            // STATUS_ID
            if (newPickupLine.getPickConfirmQty() > 0) {
                isQtyAvail = true;
            }

            if (isQtyAvail) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, newPickupLine.getLanguageId());
            dbPickupLine.setStatusDescription(statusDescription);

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getPlantId(),
                    newPickupLine.getWarehouseId());
            if(description != null) {
                dbPickupLine.setCompanyDescription(description.getCompanyDesc());
                dbPickupLine.setPlantDescription(description.getPlantDesc());
                dbPickupLine.setWarehouseDescription(description.getWarehouseDesc());
            }
            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForLineUpdateV2(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getPlantId(),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getWarehouseId(),
                    newPickupLine.getPreOutboundNo(),
                    newPickupLine.getRefDocNumber(),
                    newPickupLine.getLineNumber(),
                    newPickupLine.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                dbPickupLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                dbPickupLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                dbPickupLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                dbPickupLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
//                dbPickupLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
            }

            PickupHeaderV2 dbPickupHeader = pickupHeaderService.getPickupHeaderV2(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber());
            if (dbPickupHeader != null) {
                dbPickupLine.setPickupCreatedOn(dbPickupHeader.getPickupCreatedOn());
                if(dbPickupHeader.getPickupCreatedBy() != null) {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickupCreatedBy());
                } else {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickUpdatedBy());
                }
            }

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setBarcodeId(newPickupLine.getBarcodeId());
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
                    dbPickupLine.getLanguageId(),
                    dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(),
                    dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getPartnerCode(),
                    dbPickupLine.getLineNumber(),
                    dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(),
                    dbPickupLine.getPickedStorageBin(),
                    dbPickupLine.getPickedPackCode(),
                    0L);

            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                                    dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(),
                                    dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                PickupLineV2 createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        }

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        for (PickupLineV2 dbPickupLine : createdPickupLineList) {

            //------------------------UpdateLock-Applied------------------------------------------------------------
            InventoryV2 inventory = inventoryService.getInventoryV2(dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(), dbPickupLine.getManufacturerName());
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty()) - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End

                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);
                        inventory.setReferenceField4(INV_QTY + ALLOC_QTY);

                        // INV_QTY > 0 then, update Inventory Table
//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        inventoryV2.setUpdatedOn(new Date());
//                        inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        log.info("-----Inventory2 updated-------: " + inventoryV2);

                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                                        dbPickupLine.getWarehouseId(), inventory.getStorageBin());
                                if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                    StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(),
                                            dbPickupLine.getCompanyCodeId(),
                                            dbPickupLine.getPlantId(),
                                            dbPickupLine.getLanguageId(),
                                            authTokenForMastersService.getAccess_token());

                                    if (dbStorageBin != null) {

                                        dbStorageBin.setStatusId(0L);
                                        log.info("Bin Emptied");

                                        mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                                authTokenForMastersService.getAccess_token());
                                        log.info("Bin Update Success");
                                    }
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setReferenceField4(INV_QTY);

//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
//                        newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + createdInventoryV2);

                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {
                                // Setting up statusId = 0
                                StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(),
                                        dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(),
                                        dbPickupLine.getLanguageId(),
                                        authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);

                                mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

            // Inserting record in InventoryMovement
            Long subMvtTypeId;
            String movementDocumentNo;
            String stBin;
            String movementQtyValue;
            InventoryMovement inventoryMovement;
            try {
                subMvtTypeId = 1L;
                movementDocumentNo = dbPickupLine.getPickupNumber();
                stBin = dbPickupLine.getPickedStorageBin();
                movementQtyValue = "N";
                inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);
            } catch (Exception e) {
                log.error("InventoryMovement create Error :" + e.toString());
                e.printStackTrace();
            }

            /*--------------------------------------------------------------------------*/
            // 3. Insert a new record in INVENTORY table as below
            // Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
            // of BIN_CLASS_ID=4
            // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
            // Checking Inventory table before creating new record inventory
            // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE
//            Long BIN_CLASS_ID = 4L;

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
//                OutboundLineV2 updateOutboundLine = new OutboundLineV2();
//                updateOutboundLine.setStatusId(STATUS_ID);

                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
                if(dbPickupLine.getAssignedPickerId() == null) {
                    dbPickupLine.setAssignedPickerId("0");
                }

                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, dbPickupLine.getLanguageId());
                outboundLineV2Repository.updateOutboundlineStatusUpdateProc(
                        dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                        dbPickupLine.getWarehouseId(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPreOutboundNo(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
                        dbPickupLine.getLineNumber(), STATUS_ID, statusDescription, new Date());
                log.info("outboundLine updated using Stored Procedure: ");
//                updateOutboundLine.setStatusDescription(statusDescription);
//                updateOutboundLine.setHandlingEquipment(dbPickupLine.getActualHeNo());

//                OutboundLineV2 outboundLine = outboundLineService.updateOutboundLineV2(dbPickupLine.getCompanyCodeId(),
//                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
//                        dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(),
//                        dbPickupLine.getLineNumber(), dbPickupLine.getItemCode(), loginUserID, updateOutboundLine);
//                log.info("outboundLine updated : " + outboundLine);
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ------------------Record insertion in QUALITYHEADER table-----------------------------------
             * Allow to create QualityHeader only
             * for STATUS_ID = 50
             */
            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

                    // QC_NO
                    /*
                     * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
                     * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
                     * insert
                     */
                    Long NUM_RAN_CODE = 11L;
                    QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                            dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
                    newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());

                    newQualityHeader.setManufacturerName(dbPickupLine.getManufacturerName());
                    newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setReferenceDocumentType(dbPickupLine.getReferenceDocumentType());
                    newQualityHeader.setPickListNumber(dbPickupLine.getPickListNumber());
                    newQualityHeader.setSalesInvoiceNumber(dbPickupLine.getSalesInvoiceNumber());
                    newQualityHeader.setSalesOrderNumber(dbPickupLine.getSalesOrderNumber());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setSupplierInvoiceNo(dbPickupLine.getSupplierInvoiceNo());
                    newQualityHeader.setTokenNumber(dbPickupLine.getTokenNumber());


                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
//                    StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    statusDescription = stagingLineV2Repository.getStatusDescription(54L, dbPickupLine.getLanguageId());
                    newQualityHeader.setReferenceField10(statusDescription);
                    newQualityHeader.setStatusDescription(statusDescription);
                    log.info("login UserId : {}" , loginUserID);
                    QualityHeaderV2 createdQualityHeader = qualityHeaderService.createQualityHeaderV2(newQualityHeader, loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }

                /*-----------------------InventoryMovement----------------------------------*/
                // Inserting record in InventoryMovement
//                try {
//                    Long BIN_CLASS_ID = 4L;
//                    StorageBinV2 storageBin = mastersService.getStorageBin(dbPickupLine.getCompanyCodeId(),
//                            dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
//                            authTokenForMastersService.getAccess_token());
//                    subMvtTypeId = 2L;
//                    movementDocumentNo = QC_NO;
//                    stBin = storageBin.getStorageBin();
//                    movementQtyValue = "P";
//                    inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin, movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created for update2: " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error for update2 :" + e.toString());
//                    e.printStackTrace();
//                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
            companyCodeId = dbPickupLine.getCompanyCodeId();
            plantId = dbPickupLine.getPlantId();
            languageId = dbPickupLine.getLanguageId();
            itemCode = dbPickupLine.getItemCode();
            manufacturerName = dbPickupLine.getManufacturerName();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 51 only if all OutboundLines are STATUS_ID is 51
         */
        String statusDescription50 = stagingLineV2Repository.getStatusDescription(50L, languageId);
        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
                loginUserID, 47L, 50L, 51L, statusDescription50, statusDescription51);
        log.info("outboundHeader, preOutboundHeader updated as 50 / 51 when respective condition met");

//        List<OutboundLineV2> outboundLineList = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
//        boolean hasStatus51 = false;
//        List<Long> status51List = outboundLineList.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
//        long status51IdCount = status51List.stream().filter(a -> a == 51L || a == 47L).count();
//        log.info("status count : " + (status51IdCount == status51List.size()));
//        hasStatus51 = (status51IdCount == status51List.size());
//        if (!status51List.isEmpty() && hasStatus51) {
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
//            outboundHeader.setStatusId(51L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(51L, languageId);
//            outboundHeader.setStatusDescription(statusDescription);
//            outboundHeader.setUpdatedBy(loginUserID);
//            outboundHeader.setUpdatedOn(new Date());
//            outboundHeaderV2Repository.save(outboundHeader);
//            log.info("outboundHeader updated as 51.");
//
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            PreOutboundHeaderV2 preOutboundHeader = preOutboundHeaderService.getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
//            preOutboundHeader.setStatusId(51L);
//            preOutboundHeader.setStatusDescription(statusDescription);
//            preOutboundHeader.setUpdatedBy(loginUserID);
//            preOutboundHeader.setUpdatedOn(new Date());
//            preOutboundHeaderV2Repository.save(preOutboundHeader);
//            log.info("PreOutboundHeader updated as 51.");
//        }
//
//        /*
//         * Update OutboundHeader & Preoutbound Header STATUS_ID as 50 only if all OutboundLines are STATUS_ID is 50
//         */
//        List<OutboundLineV2> outboundLine50List = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
//        boolean hasStatus50 = false;
//        List<Long> status50List = outboundLine50List.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
//        long status50IdCount = status50List.stream().filter(a -> a == 50L).count();
//        log.info("status count : " + (status50IdCount == status50List.size()));
//        hasStatus50 = (status50IdCount == status50List.size());
//        if (!status50List.isEmpty() && hasStatus50) {
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            OutboundHeaderV2 outboundHeader50 = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
//            outboundHeader50.setStatusId(50L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(50L, languageId);
//            outboundHeader50.setStatusDescription(statusDescription);
//            outboundHeader50.setUpdatedBy(loginUserID);
//            outboundHeader50.setUpdatedOn(new Date());
//            outboundHeaderV2Repository.save(outboundHeader50);
//            log.info("outboundHeader updated as 50.");
//        }

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go to update header-----------*/
        try {
            boolean isStatus51 = false;
            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
                    .collect(Collectors.toList());
            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
            log.info("status count : " + (statusIdCount == statusList.size()));
            isStatus51 = (statusIdCount == statusList.size());
            if (!statusList.isEmpty() && isStatus51) {
                STATUS_ID = 51L;
            } else {
                STATUS_ID = 50L;
            }
            //------------------------UpdateLock-Applied------------------------------------------------------------
            for (PickupLineV2 dbPickupLine : createdPickupLineList) {
                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProc(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        partnerCode, dbPickupLine.getPickupNumber(), STATUS_ID, statusDescription, loginUserID, new Date());
                log.info("PickupNumber: " + dbPickupLine.getPickupNumber());
            }
            log.info("PickUpHeader status updated through stored procedure");
//            PickupHeaderV2 pickupHeader = pickupHeaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
//                    partnerCode, pickupNumber);
//            pickupHeader.setStatusId(STATUS_ID);
//
//            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
//            pickupHeader.setReferenceField7(statusDescription);        // tblpickupheader REF_FIELD_7
//            pickupHeader.setStatusDescription(statusDescription);
//
//            pickupHeader.setPickUpdatedBy(loginUserID);
//            pickupHeader.setPickUpdatedOn(new Date());
//            pickupHeader = pickupHeaderV2Repository.save(pickupHeader);
//            log.info("PickupHeader updated: " + pickupHeader);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("PickupHeader update error: " + e.toString());
        }
        return createdPickupLineList;
    }
    
    /**
     * -----------------------------API V3 is for WALKAROO-----------------------------------------------------------
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public List<PickupLineV2> createPickupLineV3 (String pickupNumber, String loginUserID) throws Exception {
        log.info("V3 pickup number, login UserId : {}|{}" , pickupNumber, loginUserID);
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String itemCode = null;
        String manufacturerName = null;
        List<PickupHeaderV2> pickupHeaders = pickupHeaderService.getPickupHeaderV3 (pickupNumber);
        List<PickupLineV2> createdPickupLineList = new ArrayList<>();
        List<String> refDocNumbers = pickupHeaders.stream().map(PickupHeaderV2::getRefDocNumber).distinct().collect(Collectors.toList());
        
        pickupHeaders.stream().forEach(ph -> {
        	Long STATUS_ID = 0L;
        	
        	// Create PickUpLine
            PickupLineV2 dbPickupLine = new PickupLineV2();
            dbPickupLine.setLanguageId(ph.getLanguageId());
            dbPickupLine.setCompanyCodeId(String.valueOf(ph.getCompanyCodeId()));
            dbPickupLine.setPlantId(ph.getPlantId());

            if (ph.getPickToQty() > 0) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, ph.getLanguageId());
            dbPickupLine.setStatusDescription(statusDescription);

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(String.valueOf(ph.getCompanyCodeId()),
                    ph.getLanguageId(),
                    ph.getPlantId(),
                    ph.getWarehouseId());
            if(description != null) {
                dbPickupLine.setCompanyDescription(description.getCompanyDesc());
                dbPickupLine.setPlantDescription(description.getPlantDesc());
                dbPickupLine.setWarehouseDescription(description.getWarehouseDesc());
            }
            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForLineUpdateV2(String.valueOf(ph.getCompanyCodeId()),
                    ph.getPlantId(),
                    ph.getLanguageId(),
                    ph.getWarehouseId(),
                    ph.getPreOutboundNo(),
                    ph.getRefDocNumber(),
                    ph.getLineNumber(),
                    ph.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
            	dbPickupLine.setCompanyCodeId(ph.getCompanyCodeId());
            	dbPickupLine.setPlantId(ph.getPlantId());
            	dbPickupLine.setLanguageId(ph.getLanguageId());
            	dbPickupLine.setWarehouseId(ph.getWarehouseId());
            	dbPickupLine.setPreOutboundNo(ph.getPreOutboundNo());
            	dbPickupLine.setRefDocNumber(ph.getRefDocNumber());
            	dbPickupLine.setPartnerCode(ph.getPartnerCode());
                dbPickupLine.setPickupNumber(ph.getPickupNumber());    
                dbPickupLine.setItemCode(ph.getItemCode());
                dbPickupLine.setLineNumber(dbOrderManagementLine.getLineNumber());
                dbPickupLine.setActualHeNo("HE_01");
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                dbPickupLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                dbPickupLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                dbPickupLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                dbPickupLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
                dbPickupLine.setPickedStorageBin(ph.getProposedStorageBin());
                dbPickupLine.setPickedPackCode(ph.getProposedPackBarCode());
                dbPickupLine.setPickConfirmQty(ph.getPickToQty());
                dbPickupLine.setAllocatedQty(ph.getPickToQty());
                dbPickupLine.setAssignedPickerId(ph.getAssignedPickerId());
                dbPickupLine.setBarcodeId(ph.getBarcodeId());
                //V3
                dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
                dbPickupLine.setGender(dbOrderManagementLine.getGender());
                dbPickupLine.setColor(dbOrderManagementLine.getColor());
                dbPickupLine.setSize(dbOrderManagementLine.getSize());
                dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
           }

            PickupHeaderV2 dbPickupHeader = pickupHeaderService.getPickupHeaderV3(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(),
                    dbPickupLine.getBarcodeId());
            if (dbPickupHeader != null) {
                dbPickupLine.setPickupCreatedOn(dbPickupHeader.getPickupCreatedOn());
                if(dbPickupHeader.getPickupCreatedBy() != null) {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickupCreatedBy());
                } else {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickUpdatedBy());
                }
            }

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setBarcodeId(ph.getBarcodeId());
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = 
            		pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
                    dbPickupLine.getLanguageId(),
                    dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(),
                    dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getPartnerCode(),
                    dbPickupLine.getLineNumber(),
                    dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(),
                    dbPickupLine.getPickedStorageBin(),
                    dbPickupLine.getPickedPackCode(),
                    dbPickupLine.getBarcodeId(),
                    0L);

            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                                    dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                PickupLineV2 createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        });;
        		

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        Long BIN_CLS_ID = 1L;
        for (PickupLineV2 dbPickupLine : createdPickupLineList) {

//            webSocketNotification(dbPickupLine, loginUserID);
//            fireBaseNotification(dbPickupLine, loginUserID);

            //------------------------UpdateLock-Applied------------------------------------------------------------
//            InventoryV2 inventory = inventoryService.getInventory(dbPickupLine.getCompanyCodeId(),
//                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
//                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), BIN_CLS_ID);
        	/*
        	 * getInventoryV3(String companyCode, String plantId, String languageId,
                                      String warehouseId, String barcodeId, String itemCode, Long binClassId) {
        	 */
        	 InventoryV2 inventory = inventoryService.getInventoryV3(dbPickupLine.getCompanyCodeId(),
                     dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                     dbPickupLine.getBarcodeId(), dbPickupLine.getItemCode(), BIN_CLS_ID);
        	 
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty()) - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End

                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);
                        inventory.setReferenceField4(INV_QTY + ALLOC_QTY);

                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        inventoryV2.setUpdatedOn(new Date());
//                        inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        log.info("-----Inventory2 updated-------: " + inventoryV2);
                        
                        //--------------------------------------------------------------------------------------------------------------
                        
                        // BIN_CLASS_5
                        InventoryV2 inventory2_B5 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventory2_B5, CommonUtils.getNullPropertyNames(inventory));
                        String stockTypeDesc = getStockTypeDesc(dbPickupLine.getCompanyCodeId(), plantId, languageId, warehouseId, inventory.getStockTypeId());
                        inventory2_B5.setStockTypeDescription(stockTypeDesc);
                        
                        ALLOC_QTY = 0D;
                        INV_QTY = dbPickupLine.getPickConfirmQty() + ALLOC_QTY;
                        log.info("INV_QTY---->TOT_QTY---->: " + INV_QTY + ", " + INV_QTY);
                        
                        inventory2_B5.setInventoryQuantity(round(dbPickupLine.getPickConfirmQty()));
                        inventory2_B5.setAllocatedQuantity(ALLOC_QTY) ;
                        inventory2_B5.setReferenceField4(round(INV_QTY));         //Allocated Qty is always 0 for BinClassId 3
                        
                        StorageBin dbStorageBinB5 = mastersService.getStorageBin(dbPickupLine.getWarehouseId(), 5L, authTokenForMastersService.getAccess_token());
                        log.info("---->dbStorageBin---->: " + dbStorageBinB5);
                        if (dbStorageBinB5 != null) {
                        	inventory2_B5.setStorageBin(dbStorageBinB5.getStorageBin());
                    	}
                        
                        inventory2_B5.setBinClassId(5L);
                        String palletCode = inventory.getPalletCode();
                        String caseCode = inventory.getCaseCode();

                        inventory2_B5.setPackBarcodes(dbPickupLine.getPickedPackCode());
                        
                        if (inventory2_B5.getItemType() == null) {
                            IKeyValuePair itemType = getItemTypeAndDesc(dbPickupLine.getCompanyCodeId(), plantId, languageId, warehouseId, dbPickupLine.getItemCode());
                            if (itemType != null) {
                            	inventory2_B5.setItemType(itemType.getItemType());
                            	inventory2_B5.setItemTypeDescription(itemType.getItemTypeDescription());
                            }
                        }
                        
                        inventory2_B5.setExpiryDate(new Date());
                        inventory2_B5.setCreatedOn(new Date());
                        inventory2_B5.setUpdatedOn(new Date());
//                        inventory2_B5.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 8));
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(inventory2_B5);
                        log.info("----existinginventory--createdInventoryV2--------> : " + createdInventoryV2);
                        
                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                                        dbPickupLine.getWarehouseId(), inventory.getStorageBin());
                                if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                    StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(),
                                            dbPickupLine.getCompanyCodeId(),
                                            dbPickupLine.getPlantId(),
                                            dbPickupLine.getLanguageId(),
                                            authTokenForMastersService.getAccess_token());

                                    if (dbStorageBin != null) {
                                        dbStorageBin.setStatusId(0L);
                                        log.info("Bin Emptied");
                                        mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                                authTokenForMastersService.getAccess_token());
                                        log.info("Bin Update Success");
                                    }
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setReferenceField4(INV_QTY);

                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
//                        newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + createdInventoryV2);

                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {
                                // Setting up statusId = 0
                                StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(),
                                        dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(),
                                        dbPickupLine.getLanguageId(),
                                        authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);

                                mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

//            // Inserting record in InventoryMovement
//            Long subMvtTypeId;
//            String movementDocumentNo;
//            String stBin;
//            String movementQtyValue;
//            InventoryMovement inventoryMovement;
//            try {
//                subMvtTypeId = 1L;
//                movementDocumentNo = dbPickupLine.getPickupNumber();
//                stBin = dbPickupLine.getPickedStorageBin();
//                movementQtyValue = "N";
//                inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
//                        movementQtyValue, loginUserID);
//                log.info("InventoryMovement created : " + inventoryMovement);
//            } catch (Exception e) {
//                log.error("InventoryMovement create Error :" + e.toString());
//                e.printStackTrace();
//            }

            /*--------------------------------------------------------------------------*/
            // 3. Insert a new record in INVENTORY table as below
            // Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
            // of BIN_CLASS_ID=4
            // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
            // Checking Inventory table before creating new record inventory
            // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
                if(dbPickupLine.getAssignedPickerId() == null) {
                    dbPickupLine.setAssignedPickerId("0");
                }

                statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupLine.getStatusId(), dbPickupLine.getLanguageId());
                outboundLineV2Repository.updateOutboundlineStatusUpdateProc(
                        dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                        dbPickupLine.getWarehouseId(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPreOutboundNo(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
                        dbPickupLine.getLineNumber(), dbPickupLine.getStatusId(), statusDescription, new Date());
                log.info("outboundLine updated using Stored Procedure: ");
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ------------------Record insertion in QUALITYHEADER table-----------------------------------
             * Allow to create QualityHeader only
             * for STATUS_ID = 50
             */
            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

                    // QC_NO
                    /*
                     * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
                     * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
                     * insert
                     */
                    Long NUM_RAN_CODE = 11L;
                    QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                            dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
                    newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());

                    newQualityHeader.setManufacturerName(dbPickupLine.getManufacturerName());
                    newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setReferenceDocumentType(dbPickupLine.getReferenceDocumentType());
                    newQualityHeader.setPickListNumber(dbPickupLine.getPickListNumber());
                    newQualityHeader.setSalesInvoiceNumber(dbPickupLine.getSalesInvoiceNumber());
                    newQualityHeader.setSalesOrderNumber(dbPickupLine.getSalesOrderNumber());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setSupplierInvoiceNo(dbPickupLine.getSupplierInvoiceNo());
                    newQualityHeader.setTokenNumber(dbPickupLine.getTokenNumber());

                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
                    newQualityHeader.setActualHeNo("HE_USER1"); // Hardcoding
                    
                    statusDescription = stagingLineV2Repository.getStatusDescription(54L, dbPickupLine.getLanguageId());
                    newQualityHeader.setReferenceField10(statusDescription);
                    newQualityHeader.setStatusDescription(statusDescription);
                    log.info("login UserId : {}" , loginUserID);
                    QualityHeaderV2 createdQualityHeader = qualityHeaderService.createQualityHeaderV2(newQualityHeader, loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);

                    // Create Quality Line
                    List<AddQualityLineV2> newQualityLines = new ArrayList<>();
                    AddQualityLineV2 addQualityLineV2 = new AddQualityLineV2();
                    BeanUtils.copyProperties(dbPickupLine, addQualityLineV2, CommonUtils.getNullPropertyNames(dbPickupLine));
                    addQualityLineV2.setCompanyCodeId(dbPickupLine.getCompanyCodeId());
                    addQualityLineV2.setPlantId(dbPickupLine.getPlantId());
                    addQualityLineV2.setLanguageId(dbPickupLine.getLanguageId());
                    addQualityLineV2.setWarehouseId(dbPickupLine.getWarehouseId());
                    addQualityLineV2.setItemCode(dbPickupLine.getItemCode());
                    addQualityLineV2.setPreOutboundNo(dbPickupLine.getPreOutboundNo());
                    addQualityLineV2.setRefDocNumber(dbPickupLine.getRefDocNumber());
                    addQualityLineV2.setPartnerCode(dbPickupLine.getPartnerCode());
                    addQualityLineV2.setLineNumber(dbPickupLine.getLineNumber());
                    addQualityLineV2.setQualityInspectionNo(createdQualityHeader.getQualityInspectionNo());
                    addQualityLineV2.setQualityQty(dbPickupLine.getPickConfirmQty());
                    addQualityLineV2.setPickConfirmQty(dbPickupLine.getPickConfirmQty());
                    
                    List<QualityLineV2> createdQualityLines = qualityLineService.createQualityLineV3(newQualityLines, loginUserID);
                    log.info("createdQualityLines : " + createdQualityLines);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
            companyCodeId = dbPickupLine.getCompanyCodeId();
            plantId = dbPickupLine.getPlantId();
            languageId = dbPickupLine.getLanguageId();
            itemCode = dbPickupLine.getItemCode();
            manufacturerName = dbPickupLine.getManufacturerName();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 51 only if all OutboundLines are STATUS_ID is 51
         */
//        String statusDescription50 = stagingLineV2Repository.getStatusDescription(50L, languageId);
//        String statusDescription50 = "Delivery Open";
//        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
//        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
//                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
//                loginUserID, 47L, 50L, 51L, statusDescription50, statusDescription51);
//        log.info("outboundHeader, preOutboundHeader updated as 50 / 51 when respective condition met");

        statusDescription = stagingLineV2Repository.getStatusDescription(57L, languageId);
        if(refDocNumbers!=null && !refDocNumbers.isEmpty()) {
            outboundHeaderV2Repository.updateOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumbers, 57L, statusDescription);
            outboundHeaderV2Repository.updateOutboundLineStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumbers, 57L, statusDescription);
            outboundHeaderV2Repository.updatePickupHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, refDocNumbers, 57L, statusDescription);
        }

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go to update header-----------*/
//        try {
//            boolean isStatus51 = false;
//            Long STATUS_ID = 0L;
//            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
//                    .collect(Collectors.toList());
//            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
//            log.info("status count : " + (statusIdCount == statusList.size()));
//            isStatus51 = (statusIdCount == statusList.size());
//            if (!statusList.isEmpty() && isStatus51) {
//                STATUS_ID = 51L;
//            } else {
//                STATUS_ID = 50L;
//            }
            //------------------------UpdateLock-Applied------------------------------------------------------------
//            for (PickupLineV2 dbPickupLine : createdPickupLineList) {
//                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
//                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProc(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
//                        partnerCode, dbPickupLine.getPickupNumber(), STATUS_ID, statusDescription, loginUserID, new Date());
//                log.info("PickupNumber: " + dbPickupLine.getPickupNumber());
//            }
//            log.info("PickUpHeader status updated through stored procedure");
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info("PickupHeader update error: " + e.toString());
//        }
        return createdPickupLineList;
    }

    /**
     *
//     * @param createdPickupLine
//     * @param loginUserId
     */
//    private void webSocketNotification (PickupLineV2 createdPickupLine, String loginUserId) {
//        try {
//            NotificationSave notificationInput = new NotificationSave();
//            notificationInput.setUserId(Collections.singletonList(loginUserId));
//            notificationInput.setUserType(null);
//            notificationInput.setMessage("A new Outbound - " + createdPickupLine.getPickedStorageBin() + " has been created on ");
//            notificationInput.setTopic("Outbound Create");
//            notificationInput.setReferenceNumber(createdPickupLine.getRefDocNumber());
//            notificationInput.setDocumentNumber(createdPickupLine.getPreOutboundNo());
//            notificationInput.setCompanyCodeId(createdPickupLine.getCompanyCodeId());
//            notificationInput.setPlantId(createdPickupLine.getPlantId());
//            notificationInput.setLanguageId(createdPickupLine.getLanguageId());
//            notificationInput.setWarehouseId(createdPickupLine.getWarehouseId());
//            notificationInput.setCreatedOn(createdPickupLine.getPickupCreatedOn());
//            notificationInput.setCreatedBy(loginUserId);
//            notificationInput.setStorageBin(createdPickupLine.getPickedStorageBin());
//            this.idMasterService.createNotification(notificationInput);
//        } catch (Exception e) {
//            log.error("Outbound websocket notification error " + e.toString());
//        }
//    }
//
//    /**
//     *
//     * @param createdPickupLine
//     * @param loginUserId
//     */
//    private void fireBaseNotification(PickupLineV2 createdPickupLine, String loginUserId) {
//        try {
//            List<String> deviceToken = pickupHeaderV2Repository.getDeviceToken(
//                    createdPickupLine.getCompanyCodeId(), createdPickupLine.getPlantId(), createdPickupLine.getLanguageId(), createdPickupLine.getWarehouseId());
//            if (deviceToken != null && !deviceToken.isEmpty()) {
//                String title = "Outbound Create";
//                String message = "A new Outbound - " + createdPickupLine.getPickedStorageBin() + " has been created on ";
//                NotificationSave notificationInput = new NotificationSave();
//                notificationInput.setUserId(Collections.singletonList(loginUserId));
//                notificationInput.setUserType(null);
//                notificationInput.setMessage(message);
//                notificationInput.setTopic(title);
//                notificationInput.setReferenceNumber(createdPickupLine.getRefDocNumber());
//                notificationInput.setDocumentNumber(createdPickupLine.getPreOutboundNo());
//                notificationInput.setCompanyCodeId(createdPickupLine.getCompanyCodeId());
//                notificationInput.setPlantId(createdPickupLine.getPlantId());
//                notificationInput.setLanguageId(createdPickupLine.getLanguageId());
//                notificationInput.setWarehouseId(createdPickupLine.getWarehouseId());
//                notificationInput.setCreatedOn(createdPickupLine.getPickupCreatedOn());
//                notificationInput.setCreatedBy(loginUserId);
//                notificationInput.setStorageBin(createdPickupLine.getPickedStorageBin());
//                pushNotificationService.sendPushNotification(deviceToken, notificationInput);
//            }
//        } catch (Exception e) {
//            log.error("Outbound fireBase notification error " + e.toString());
//        }
//    }

    public IKeyValuePair getItemTypeAndDesc(String companyCodeId, String plantId, String languageId,
            String warehouseId, String itemCode) {
    	return stagingLineV2Repository.getItemTypeAndDescription(companyCodeId, plantId, languageId, warehouseId, itemCode);
	}

    
    /**
     * 
     * @param newPickupLines
     * @param loginUserID
     * @return
     */
    @Transactional
    public List<PickupLineV2> createPickupLineNonCBMPickListCancellationV2(@Valid List<AddPickupLine> newPickupLines, String loginUserID) {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        Long STATUS_ID = 0L;
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String pickupNumber = null;
        String itemCode = null;
        String manufacturerName = null;
        boolean isQtyAvail = false;

        List<AddPickupLine> dupPickupLines = getDuplicatesV2(newPickupLines);
        log.info("-------dupPickupLines--------> " + dupPickupLines);
        if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
            newPickupLines.removeAll(dupPickupLines);
            newPickupLines.add(dupPickupLines.get(0));
            log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
        }

        // Create PickUpLine
        List<PickupLineV2> createdPickupLineList = new ArrayList<>();
        for (AddPickupLine newPickupLine : newPickupLines) {
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));

            dbPickupLine.setLanguageId(newPickupLine.getLanguageId());
            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));
            dbPickupLine.setPlantId(newPickupLine.getPlantId());

            // STATUS_ID
            if (newPickupLine.getPickConfirmQty() > 0) {
                isQtyAvail = true;
            }

            if (isQtyAvail) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, newPickupLine.getLanguageId());
            dbPickupLine.setStatusDescription(statusDescription);

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getPlantId(),
                    newPickupLine.getWarehouseId());
            if(description != null) {
                dbPickupLine.setCompanyDescription(description.getCompanyDesc());
                dbPickupLine.setPlantDescription(description.getPlantDesc());
                dbPickupLine.setWarehouseDescription(description.getWarehouseDesc());
            }
            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForQualityLineV2(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getPlantId(),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getWarehouseId(),
                    newPickupLine.getPreOutboundNo(),
                    newPickupLine.getRefDocNumber(),
                    newPickupLine.getLineNumber(),
                    newPickupLine.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                dbPickupLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                dbPickupLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                dbPickupLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                dbPickupLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
//                dbPickupLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
            }

            PickupHeaderV2 dbPickupHeader = pickupHeaderService.getPickupHeaderV2(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber());
            if (dbPickupHeader != null) {
                dbPickupLine.setPickupCreatedOn(dbPickupHeader.getPickupCreatedOn());
                if(dbPickupHeader.getPickupCreatedBy() != null) {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickupCreatedBy());
                } else {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickUpdatedBy());
                }
            }

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setBarcodeId(newPickupLine.getBarcodeId());
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
                    dbPickupLine.getLanguageId(),
                    dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(),
                    dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getPartnerCode(),
                    dbPickupLine.getLineNumber(),
                    dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(),
                    dbPickupLine.getPickedStorageBin(),
                    dbPickupLine.getPickedPackCode(),
                    0L);

            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                                    dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                PickupLineV2 createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        }

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        for (PickupLineV2 dbPickupLine : createdPickupLineList) {

            //------------------------UpdateLock-Applied------------------------------------------------------------
            InventoryV2 inventory = inventoryService.getInventoryV2(dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(), dbPickupLine.getManufacturerName());
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty()) - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End

                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);
                        inventory.setReferenceField4(INV_QTY + ALLOC_QTY);

                        // INV_QTY > 0 then, update Inventory Table
//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        inventoryV2.setUpdatedOn(new Date());
//                        inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        log.info("-----Inventory2 updated-------: " + inventoryV2);

                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                                        dbPickupLine.getWarehouseId(), inventory.getStorageBin());
                                if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                    StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(),
                                            dbPickupLine.getCompanyCodeId(),
                                            dbPickupLine.getPlantId(),
                                            dbPickupLine.getLanguageId(),
                                            authTokenForMastersService.getAccess_token());

                                    if (dbStorageBin != null) {

                                        dbStorageBin.setStatusId(0L);
                                        log.info("Bin Emptied");

                                        mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                                authTokenForMastersService.getAccess_token());
                                        log.info("Bin Update Success");
                                    }
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setReferenceField4(INV_QTY);

//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
                        newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + createdInventoryV2);

                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {
                                // Setting up statusId = 0
                                StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(),
                                        dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(),
                                        dbPickupLine.getLanguageId(),
                                        authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);

                                mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

            // Inserting record in InventoryMovement
            Long subMvtTypeId;
            String movementDocumentNo;
            String stBin;
            String movementQtyValue;
            InventoryMovement inventoryMovement;
            try {
                subMvtTypeId = 1L;
                movementDocumentNo = dbPickupLine.getPickupNumber();
                stBin = dbPickupLine.getPickedStorageBin();
                movementQtyValue = "N";
                inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);
            } catch (Exception e) {
                log.error("InventoryMovement create Error :" + e.toString());
                e.printStackTrace();
            }

            /*--------------------------------------------------------------------------*/
            // 3. Insert a new record in INVENTORY table as below
            // Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
            // of BIN_CLASS_ID=4
            // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
            // Checking Inventory table before creating new record inventory
            // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE
//            Long BIN_CLASS_ID = 4L;

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
//                OutboundLineV2 updateOutboundLine = new OutboundLineV2();
//                updateOutboundLine.setStatusId(STATUS_ID);

                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
                if(dbPickupLine.getAssignedPickerId() == null) {
                    dbPickupLine.setAssignedPickerId("0");
                }

                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, dbPickupLine.getLanguageId());
                outboundLineV2Repository.updateOutboundlineStatusUpdateProc(
                        dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                        dbPickupLine.getWarehouseId(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPreOutboundNo(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
                        dbPickupLine.getLineNumber(), STATUS_ID, statusDescription, new Date());
                log.info("outboundLine updated using Stored Procedure: ");
//                updateOutboundLine.setStatusDescription(statusDescription);
//                updateOutboundLine.setHandlingEquipment(dbPickupLine.getActualHeNo());

//                OutboundLineV2 outboundLine = outboundLineService.updateOutboundLineV2(dbPickupLine.getCompanyCodeId(),
//                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
//                        dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(),
//                        dbPickupLine.getLineNumber(), dbPickupLine.getItemCode(), loginUserID, updateOutboundLine);
//                log.info("outboundLine updated : " + outboundLine);
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ------------------Record insertion in QUALITYHEADER table-----------------------------------
             * Allow to create QualityHeader only
             * for STATUS_ID = 50
             */
            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

                    // QC_NO
                    /*
                     * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
                     * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
                     * insert
                     */
                    Long NUM_RAN_CODE = 11L;
                    QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                            dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
                    newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());

                    newQualityHeader.setManufacturerName(dbPickupLine.getManufacturerName());
                    newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setReferenceDocumentType(dbPickupLine.getReferenceDocumentType());
                    newQualityHeader.setPickListNumber(dbPickupLine.getPickListNumber());
                    newQualityHeader.setSalesInvoiceNumber(dbPickupLine.getSalesInvoiceNumber());
                    newQualityHeader.setSalesOrderNumber(dbPickupLine.getSalesOrderNumber());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setSupplierInvoiceNo(dbPickupLine.getSupplierInvoiceNo());
                    newQualityHeader.setTokenNumber(dbPickupLine.getTokenNumber());


                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
//                    StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    statusDescription = stagingLineV2Repository.getStatusDescription(54L, dbPickupLine.getLanguageId());
                    newQualityHeader.setReferenceField10(statusDescription);
                    newQualityHeader.setStatusDescription(statusDescription);

                    QualityHeaderV2 createdQualityHeader = qualityHeaderService.createQualityHeaderV2(newQualityHeader, loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }

                /*-----------------------InventoryMovement----------------------------------*/
                // Inserting record in InventoryMovement
//                try {
//                    Long BIN_CLASS_ID = 4L;
//                    StorageBinV2 storageBin = mastersService.getStorageBin(dbPickupLine.getCompanyCodeId(),
//                            dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
//                            authTokenForMastersService.getAccess_token());
//                    subMvtTypeId = 2L;
//                    movementDocumentNo = QC_NO;
//                    stBin = storageBin.getStorageBin();
//                    movementQtyValue = "P";
//                    inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin, movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created for update2: " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error for update2 :" + e.toString());
//                    e.printStackTrace();
//                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
            companyCodeId = dbPickupLine.getCompanyCodeId();
            plantId = dbPickupLine.getPlantId();
            languageId = dbPickupLine.getLanguageId();
            itemCode = dbPickupLine.getItemCode();
            manufacturerName = dbPickupLine.getManufacturerName();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 51 only if all OutboundLines are STATUS_ID is 51
         */
        String statusDescription50 = stagingLineV2Repository.getStatusDescription(50L, languageId);
        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
                loginUserID, 47L, 50L, 51L, statusDescription50, statusDescription51);
        log.info("outboundHeader, preOutboundHeader updated as 50 / 51 when respective condition met");

//        List<OutboundLineV2> outboundLineList = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
//        boolean hasStatus51 = false;
//        List<Long> status51List = outboundLineList.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
//        long status51IdCount = status51List.stream().filter(a -> a == 51L || a == 47L).count();
//        log.info("status count : " + (status51IdCount == status51List.size()));
//        hasStatus51 = (status51IdCount == status51List.size());
//        if (!status51List.isEmpty() && hasStatus51) {
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
//            outboundHeader.setStatusId(51L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(51L, languageId);
//            outboundHeader.setStatusDescription(statusDescription);
//            outboundHeader.setUpdatedBy(loginUserID);
//            outboundHeader.setUpdatedOn(new Date());
//            outboundHeaderV2Repository.save(outboundHeader);
//            log.info("outboundHeader updated as 51.");
//
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            PreOutboundHeaderV2 preOutboundHeader = preOutboundHeaderService.getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
//            preOutboundHeader.setStatusId(51L);
//            preOutboundHeader.setStatusDescription(statusDescription);
//            preOutboundHeader.setUpdatedBy(loginUserID);
//            preOutboundHeader.setUpdatedOn(new Date());
//            preOutboundHeaderV2Repository.save(preOutboundHeader);
//            log.info("PreOutboundHeader updated as 51.");
//        }
//
//        /*
//         * Update OutboundHeader & Preoutbound Header STATUS_ID as 50 only if all OutboundLines are STATUS_ID is 50
//         */
//        List<OutboundLineV2> outboundLine50List = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
//        boolean hasStatus50 = false;
//        List<Long> status50List = outboundLine50List.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
//        long status50IdCount = status50List.stream().filter(a -> a == 50L).count();
//        log.info("status count : " + (status50IdCount == status50List.size()));
//        hasStatus50 = (status50IdCount == status50List.size());
//        if (!status50List.isEmpty() && hasStatus50) {
//            //------------------------UpdateLock-Applied------------------------------------------------------------
//            OutboundHeaderV2 outboundHeader50 = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
//            outboundHeader50.setStatusId(50L);
//            statusDescription = stagingLineV2Repository.getStatusDescription(50L, languageId);
//            outboundHeader50.setStatusDescription(statusDescription);
//            outboundHeader50.setUpdatedBy(loginUserID);
//            outboundHeader50.setUpdatedOn(new Date());
//            outboundHeaderV2Repository.save(outboundHeader50);
//            log.info("outboundHeader updated as 50.");
//        }

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go to update header-----------*/
        try {
            boolean isStatus51 = false;
            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
                    .collect(Collectors.toList());
            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
            log.info("status count : " + (statusIdCount == statusList.size()));
            isStatus51 = (statusIdCount == statusList.size());
            if (!statusList.isEmpty() && isStatus51) {
                STATUS_ID = 51L;
            } else {
                STATUS_ID = 50L;
            }
            //------------------------UpdateLock-Applied------------------------------------------------------------
            for (PickupLineV2 dbPickupLine : createdPickupLineList) {
            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
            pickupHeaderV2Repository.updatePickupheaderStatusUpdateProc(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        partnerCode, dbPickupLine.getPickupNumber(), STATUS_ID, statusDescription, loginUserID, new Date());
                log.info("PickupNumber: " + dbPickupLine.getPickupNumber());

                PickupHeaderV2 pickupHeader = pickupHeaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, partnerCode, dbPickupLine.getPickupNumber());
                Double headerPickToQty = pickupHeader.getPickToQty();
                Double pickupLineQty = pickupLineV2Repository.getPickupLineSumV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, dbPickupLine.getPickupNumber(), 50L, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName());

                if(pickupLineQty < headerPickToQty){
                    PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
                    BeanUtils.copyProperties(pickupHeader, newPickupHeader, CommonUtils.getNullPropertyNames(pickupHeader));
                    long NUM_RAN_CODE = 10;
                    String PU_NO = getNextRangeNumber(NUM_RAN_CODE, companyCodeId, plantId, languageId, warehouseId);
                    log.info("PU_NO : " + PU_NO);

                    newPickupHeader.setAssignedPickerId(pickupHeader.getAssignedPickerId());
                    newPickupHeader.setPickupNumber(PU_NO);

                    Double pickToQty = headerPickToQty - pickupLineQty;
                    newPickupHeader.setPickToQty(pickToQty);
                    // PICK_UOM
                    newPickupHeader.setPickUom(pickupHeader.getPickUom());

                    // STATUS_ID
                    newPickupHeader.setStatusId(48L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(48L, languageId);
                    newPickupHeader.setStatusDescription(statusDescription);

                    // ProposedPackbarcode
                    newPickupHeader.setProposedPackBarCode(pickupHeader.getProposedPackBarCode());

                    // REF_FIELD_1
                    newPickupHeader.setReferenceField1(pickupHeader.getReferenceField1());

                    newPickupHeader.setManufacturerCode(pickupHeader.getManufacturerCode());
                    newPickupHeader.setManufacturerName(pickupHeader.getManufacturerName());
                    newPickupHeader.setManufacturerPartNo(pickupHeader.getManufacturerPartNo());
                    newPickupHeader.setSalesOrderNumber(pickupHeader.getSalesOrderNumber());
                    newPickupHeader.setPickListNumber(pickupHeader.getPickListNumber());
                    newPickupHeader.setSalesInvoiceNumber(pickupHeader.getSalesInvoiceNumber());
                    newPickupHeader.setOutboundOrderTypeId(pickupHeader.getOutboundOrderTypeId());
                    newPickupHeader.setReferenceDocumentType(pickupHeader.getReferenceDocumentType());
                    newPickupHeader.setSupplierInvoiceNo(pickupHeader.getSupplierInvoiceNo());
                    newPickupHeader.setTokenNumber(pickupHeader.getTokenNumber());
                    newPickupHeader.setLevelId(pickupHeader.getLevelId());
                    newPickupHeader.setTargetBranchCode(pickupHeader.getTargetBranchCode());
                    newPickupHeader.setLineNumber(pickupHeader.getLineNumber());

                    newPickupHeader.setFromBranchCode(pickupHeader.getFromBranchCode());
                    newPickupHeader.setIsCompleted(pickupHeader.getIsCompleted());
                    newPickupHeader.setIsCancelled(pickupHeader.getIsCancelled());
                    newPickupHeader.setMUpdatedOn(pickupHeader.getMUpdatedOn());

                    PickupHeaderV2 createdPickupHeader = pickupHeaderService.createPickupHeaderV2(newPickupHeader, pickupHeader.getPickupCreatedBy());
                    log.info("pickupHeader created: " + createdPickupHeader);
                }

            }
            log.info("PickUpHeader status updated through stored procedure");
//            PickupHeaderV2 pickupHeader = pickupHeaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
//                    partnerCode, pickupNumber);
//            pickupHeader.setStatusId(STATUS_ID);
//
//            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
//            pickupHeader.setReferenceField7(statusDescription);        // tblpickupheader REF_FIELD_7
//            pickupHeader.setStatusDescription(statusDescription);
//
//            pickupHeader.setPickUpdatedBy(loginUserID);
//            pickupHeader.setPickUpdatedOn(new Date());
//            pickupHeader = pickupHeaderV2Repository.save(pickupHeader);
//            log.info("PickupHeader updated: " + pickupHeader);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("PickupHeader update error: " + e.toString());
        }
        return createdPickupLineList;
    }

    @Transactional
    public List<PickupLineV2> createPickupLineV2(@Valid List<AddPickupLine> newPickupLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
        AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
        Long STATUS_ID = 0L;
        String companyCodeId = null;
        String plantId = null;
        String languageId = null;
        String warehouseId = null;
        String preOutboundNo = null;
        String refDocNumber = null;
        String partnerCode = null;
        String pickupNumber = null;
        boolean isQtyAvail = false;

        List<AddPickupLine> dupPickupLines = getDuplicatesV2(newPickupLines);
        log.info("-------dupPickupLines--------> " + dupPickupLines);
        if (dupPickupLines != null && !dupPickupLines.isEmpty()) {
            newPickupLines.removeAll(dupPickupLines);
            newPickupLines.add(dupPickupLines.get(0));
            log.info("-------PickupLines---removed-dupPickupLines-----> " + newPickupLines);
        }

        // Create PickUpLine
        List<PickupLineV2> createdPickupLineList = new ArrayList<>();
        for (AddPickupLine newPickupLine : newPickupLines) {
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));

            dbPickupLine.setLanguageId(newPickupLine.getLanguageId());
            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));
            dbPickupLine.setPlantId(newPickupLine.getPlantId());

            // STATUS_ID
            if (newPickupLine.getPickConfirmQty() > 0) {
                isQtyAvail = true;
            }

            if (isQtyAvail) {
                STATUS_ID = 50L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);

            dbPickupLine.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, newPickupLine.getLanguageId());
            dbPickupLine.setStatusDescription(statusDescription);

            //V2 Code
            IKeyValuePair description = stagingLineV2Repository.getDescription(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getPlantId(),
                    newPickupLine.getWarehouseId());

            dbPickupLine.setCompanyDescription(description.getCompanyDesc());
            dbPickupLine.setPlantDescription(description.getPlantDesc());
            dbPickupLine.setWarehouseDescription(description.getWarehouseDesc());

            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForQualityLineV2(String.valueOf(newPickupLine.getCompanyCodeId()),
                    newPickupLine.getPlantId(),
                    newPickupLine.getLanguageId(),
                    newPickupLine.getWarehouseId(),
                    newPickupLine.getPreOutboundNo(),
                    newPickupLine.getRefDocNumber(),
                    newPickupLine.getLineNumber(),
                    newPickupLine.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                dbPickupLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                dbPickupLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                dbPickupLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                dbPickupLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
//                dbPickupLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
            }

            PickupHeaderV2 dbPickupHeader = pickupHeaderService.getPickupHeaderV2(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber());
            if (dbPickupHeader != null) {
                dbPickupLine.setPickupCreatedOn(dbPickupHeader.getPickupCreatedOn());
                if(dbPickupHeader.getPickupCreatedBy() != null) {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickupCreatedBy());
                } else {
                    dbPickupLine.setPickupCreatedBy(dbPickupHeader.getPickUpdatedBy());
                }
            }

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setBarcodeId(newPickupLine.getBarcodeId());
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndDeletionIndicator(
                    dbPickupLine.getLanguageId(),
                    dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(),
                    dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(),
                    dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getPartnerCode(),
                    dbPickupLine.getLineNumber(),
                    dbPickupLine.getPickupNumber(),
                    dbPickupLine.getItemCode(),
                    dbPickupLine.getPickedStorageBin(),
                    dbPickupLine.getPickedPackCode(),
                    0L);

            log.info("existingPickupLine : " + existingPickupLine);
            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                try {
                    pickupNumber = dbPickupLine.getPickupNumber();
                    companyCodeId = dbPickupLine.getCompanyCodeId();
                    plantId = dbPickupLine.getPlantId();
                    languageId = dbPickupLine.getLanguageId();
                    warehouseId = dbPickupLine.getWarehouseId();
                    String leadTime = pickupLineV2Repository.getleadtime(companyCodeId, plantId, languageId, warehouseId, pickupNumber, dbPickupLine.getBarcodeId(), new Date());
                    dbPickupLine.setReferenceField1(leadTime);
                    log.info("LeadTime: " + leadTime);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                PickupLineV2 createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
                createdPickupLineList.add(createdPickupLine);
            } else {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existingPickupLine);
            }
        }

        /*---------------------------------------------Inventory Updates-------------------------------------------*/
        // Updating respective tables
        for (PickupLineV2 dbPickupLine : createdPickupLineList) {

            //--------------------------------------CBM Check---------------------------------------------------------

            ImBasicData imBasicData = new ImBasicData();
            imBasicData.setCompanyCodeId(dbPickupLine.getCompanyCodeId());
            imBasicData.setPlantId(dbPickupLine.getPlantId());
            imBasicData.setLanguageId(dbPickupLine.getLanguageId());
            imBasicData.setWarehouseId(dbPickupLine.getWarehouseId());
            imBasicData.setItemCode(dbPickupLine.getItemCode());
            imBasicData.setManufacturerName(dbPickupLine.getManufacturerName());
            ImBasicData1 itemCodeCapacityCheck = mastersService.getImBasicData1ByItemCodeV2(imBasicData, authTokenForMastersService.getAccess_token());
            log.info("ImbasicData1 : " + itemCodeCapacityCheck);

            boolean capacityCheck = false;
            boolean storageBinCapacityCheck = false;
            if (itemCodeCapacityCheck != null) {
                if (itemCodeCapacityCheck.getCapacityCheck() != null) {
                    capacityCheck = itemCodeCapacityCheck.getCapacityCheck();
                }
            }
            log.info("capacity Check: " + capacityCheck);

            StorageBinV2 capStorageBin = storageBinRepository.getStorageBin(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                    dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
            if (capStorageBin != null) {
                if (capStorageBin.isCapacityCheck()) {
                    storageBinCapacityCheck = capStorageBin.isCapacityCheck();
                }
            }
            log.info("Picked storageBinCapacityCheck: " + storageBinCapacityCheck);

            Double cbm = 0D;
            Double cbmPerQty = 0D;
            Double inventory_qty = 0D;
            Double pickedCbm = 0D;
            Double remainVol = 0D;
            Double occupiedVol = 0D;

            //------------------------UpdateLock-Applied------------------------------------------------------------
            InventoryV2 inventory = inventoryService.getInventoryV2(dbPickupLine.getCompanyCodeId(),
                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin());
            log.info("inventory record queried: " + inventory);
            if (inventory != null) {
                if (dbPickupLine.getAllocatedQty() > 0D) {
                    try {
                        Double INV_QTY = (inventory.getInventoryQuantity() + dbPickupLine.getAllocatedQty())
                                - dbPickupLine.getPickConfirmQty();
                        Double ALLOC_QTY = inventory.getAllocatedQuantity() - dbPickupLine.getAllocatedQty();

                        if (capacityCheck) {
                            log.info("CapacityCheck: " + capacityCheck);

                            if (inventory.getCbm() != null) {
                                cbm = Double.valueOf(inventory.getCbm());
                            }
                            if (inventory.getInventoryQuantity() != null) {
                                inventory_qty = inventory.getInventoryQuantity();
                            }
                            if (dbPickupLine.getPickedCbm() != null) {
                                pickedCbm = dbPickupLine.getPickedCbm();
                            }
                            if (dbPickupLine.getPickedCbm() == null) {
                                if (inventory.getCbmPerQuantity() != null) {
                                    cbmPerQty = Double.valueOf(inventory.getCbmPerQuantity());
                                }
                                if (inventory.getCbmPerQuantity() == null) {
                                    cbmPerQty = cbm / inventory_qty;
                                }
                                pickedCbm = cbmPerQty * dbPickupLine.getPickConfirmQty();
                                dbPickupLine.setPickedCbm(pickedCbm);
                            }
                            cbm = cbm - pickedCbm;

                            inventory.setCbm(String.valueOf(cbm));

                            StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(), dbPickupLine.getWarehouseId(), authTokenForMastersService.getAccess_token());
                            if (dbStorageBin.isCapacityCheck()) {
                                if (dbStorageBin.getRemainingVolume() != null) {
                                    remainVol = Double.valueOf(dbStorageBin.getRemainingVolume());
                                    log.info("Remaining Volume:" + remainVol);
                                }
                                if (dbStorageBin.getOccupiedVolume() != null) {
                                    occupiedVol = Double.valueOf(dbStorageBin.getOccupiedVolume());
                                    log.info("Occupied Volume: " + occupiedVol);
                                }
                                remainVol = remainVol + pickedCbm;
                                occupiedVol = occupiedVol - pickedCbm;

                                log.info("After Picking ---> remainingVolume, OccupiedVolume: " + remainVol + ", " + occupiedVol);

                                dbStorageBin.setRemainingVolume(String.valueOf(remainVol));
                                dbStorageBin.setOccupiedVolume(String.valueOf(occupiedVol));
                            }
                        }

                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }

                        if (ALLOC_QTY < 0D) {
                            ALLOC_QTY = 0D;
                        }
                        // End

                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setAllocatedQuantity(ALLOC_QTY);
                        inventory.setReferenceField4(INV_QTY + ALLOC_QTY);

                        // INV_QTY > 0 then, update Inventory Table
//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 inventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        inventoryV2.setUpdatedOn(new Date());
                        inventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        inventoryV2 = inventoryV2Repository.save(inventoryV2);
                        log.info("-----Inventory2 updated-------: " + inventoryV2);

                        if (INV_QTY == 0) {
                            // Setting up statusId = 0
                            try {
                                // Check whether Inventory has record or not
                                InventoryV2 inventoryByStBin = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(),
                                        dbPickupLine.getWarehouseId(), inventory.getStorageBin());
                                if (inventoryByStBin == null || (inventoryByStBin != null && inventoryByStBin.getReferenceField4() == 0)) {
                                    StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                            dbPickupLine.getWarehouseId(),
                                            dbPickupLine.getCompanyCodeId(),
                                            dbPickupLine.getPlantId(),
                                            dbPickupLine.getLanguageId(),
                                            authTokenForMastersService.getAccess_token());

                                    if (dbStorageBin != null) {

                                        dbStorageBin.setStatusId(0L);
                                        log.info("Bin Emptied");
//                                        dbStorageBin.setWarehouseId(dbPickupLine.getWarehouseId());

                                        if (dbStorageBin.isCapacityCheck()) {
                                            dbStorageBin.setRemainingVolume(dbStorageBin.getTotalVolume());
                                            dbStorageBin.setOccupiedVolume("0");
                                            dbStorageBin.setAllocatedVolume(0D);
                                        }

                                        mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                                authTokenForMastersService.getAccess_token());
                                        log.info("Bin Update Success");
                                    }
                                }
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        log.error("Inventory Update :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if (dbPickupLine.getAllocatedQty() == null || dbPickupLine.getAllocatedQty() == 0D) {
                    Double INV_QTY;
                    try {
                        INV_QTY = inventory.getInventoryQuantity() - dbPickupLine.getPickConfirmQty();
                        /*
                         * [Prod Fix: 17-08] - Discussed to make negative inventory to zero
                         */
                        // Start
                        if (INV_QTY < 0D) {
                            INV_QTY = 0D;
                        }
                        // End
                        inventory.setInventoryQuantity(INV_QTY);
                        inventory.setReferenceField4(INV_QTY);

                        if (capacityCheck) {
                            log.info("CapacityCheck: " + capacityCheck);

                            if (inventory.getCbm() != null) {
                                cbm = Double.valueOf(inventory.getCbm());
                            }
                            if (inventory.getInventoryQuantity() != null) {
                                inventory_qty = inventory.getInventoryQuantity();
                            }
                            if (dbPickupLine.getPickedCbm() != null) {
                                pickedCbm = dbPickupLine.getPickedCbm();
                            }
                            if (dbPickupLine.getPickedCbm() == null) {
                                if (inventory.getCbmPerQuantity() != null) {
                                    cbmPerQty = Double.valueOf(inventory.getCbmPerQuantity());
                                }
                                if (inventory.getCbmPerQuantity() == null) {
                                    cbmPerQty = cbm / inventory_qty;
                                }
                                pickedCbm = cbmPerQty * dbPickupLine.getPickConfirmQty();
                                dbPickupLine.setPickedCbm(pickedCbm);
                            }
                            cbm = cbm - pickedCbm;

                            inventory.setCbm(String.valueOf(cbm));

                            StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                    dbPickupLine.getWarehouseId(),
                                    dbPickupLine.getCompanyCodeId(),
                                    dbPickupLine.getPlantId(),
                                    dbPickupLine.getLanguageId(),
                                    authTokenForMastersService.getAccess_token());
                            if (dbStorageBin.isCapacityCheck()) {
                                if (dbStorageBin.getRemainingVolume() != null) {
                                    remainVol = Double.valueOf(dbStorageBin.getRemainingVolume());
                                    log.info("Remaining Volume:" + remainVol);
                                }
                                if (dbStorageBin.getOccupiedVolume() != null) {
                                    occupiedVol = Double.valueOf(dbStorageBin.getOccupiedVolume());
                                    log.info("Occupied Volume: " + occupiedVol);
                                }
                                remainVol = remainVol + pickedCbm;
                                occupiedVol = occupiedVol - pickedCbm;

                                log.info("After Picking ---> remainingVolume, OccupiedVolume: " + remainVol + ", " + occupiedVol);

                                dbStorageBin.setRemainingVolume(String.valueOf(remainVol));
                                dbStorageBin.setOccupiedVolume(String.valueOf(occupiedVol));
                            }
                        }

//                        inventory = inventoryV2Repository.save(inventory);
//                        log.info("inventory updated : " + inventory);
                        InventoryV2 newInventoryV2 = new InventoryV2();
                        BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                        newInventoryV2.setUpdatedOn(new Date());
                        newInventoryV2.setInventoryId(Long.valueOf(System.currentTimeMillis() + "" + 3));
                        InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                        log.info("InventoryV2 created : " + createdInventoryV2);

                        //-------------------------------------------------------------------
                        // PASS PickedConfirmedStBin, WH_ID to inventory
                        // 	If inv_qty && alloc_qty is zero or null then do the below logic.
                        //-------------------------------------------------------------------
                        InventoryV2 inventoryBySTBIN = inventoryService.getInventoryByStorageBinV2(dbPickupLine.getCompanyCodeId(),
                                dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickedStorageBin());
                        //if (INV_QTY == 0) {
                        if (inventoryBySTBIN != null && (inventoryBySTBIN.getAllocatedQuantity() == null || inventoryBySTBIN.getAllocatedQuantity() == 0D)
                                && (inventoryBySTBIN.getInventoryQuantity() == null || inventoryBySTBIN.getInventoryQuantity() == 0D)) {
                            try {
                                // Setting up statusId = 0
                                StorageBinV2 dbStorageBin = mastersService.getStorageBinV2(inventory.getStorageBin(),
                                        dbPickupLine.getWarehouseId(),
                                        dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(),
                                        dbPickupLine.getLanguageId(),
                                        authTokenForMastersService.getAccess_token());
                                dbStorageBin.setStatusId(0L);

                                if (dbStorageBin.isCapacityCheck()) {
                                    dbStorageBin.setRemainingVolume(dbStorageBin.getTotalVolume());
                                    dbStorageBin.setOccupiedVolume("0");
                                    dbStorageBin.setAllocatedVolume(0D);
                                }

                                mastersService.updateStorageBinV2(inventory.getStorageBin(), dbStorageBin, dbPickupLine.getCompanyCodeId(),
                                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), loginUserID,
                                        authTokenForMastersService.getAccess_token());
                            } catch (Exception e) {
                                log.error("updateStorageBin Error :" + e.toString());
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e1) {
                        log.error("Inventory cum StorageBin update: Error :" + e1.toString());
                        e1.printStackTrace();
                    }
                }
            }

            // Inserting record in InventoryMovement
            Long subMvtTypeId;
            String movementDocumentNo;
            String stBin;
            String movementQtyValue;
            InventoryMovement inventoryMovement;
            try {
                subMvtTypeId = 1L;
                movementDocumentNo = dbPickupLine.getPickupNumber();
                stBin = dbPickupLine.getPickedStorageBin();
                movementQtyValue = "N";
                inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);
            } catch (Exception e) {
                log.error("InventoryMovement create Error :" + e.toString());
                e.printStackTrace();
            }

            /*--------------------------------------------------------------------------*/
            // 3. Insert a new record in INVENTORY table as below
            // Fetch from PICKUPLINE table and insert WH_ID/ITM_CODE/ST_BIN = (ST_BIN value
            // of BIN_CLASS_ID=4
            // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = PICK_CNF_QTY.
            // Checking Inventory table before creating new record inventory
            // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=4 /PACK_BARCODE
//            Long BIN_CLASS_ID = 4L;
//            StorageBinV2 storageBin = mastersService.getStorageBin(dbPickupLine.getCompanyCodeId(),
//                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
//                    authTokenForMastersService.getAccess_token());
//            InventoryV2 existingInventory = inventoryService.getInventoryV2(dbPickupLine.getCompanyCodeId(),
//                    dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
//                    dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin());
//            if (existingInventory != null) {
//                try {
//                    Double INV_QTY = existingInventory.getInventoryQuantity() + dbPickupLine.getPickConfirmQty();
//                    InventoryV2 updateInventory = new InventoryV2();
//                    updateInventory.setInventoryQuantity(INV_QTY);
//                    if (capacityCheck) {
//                        if (existingInventory.getCbm() != null) {
//                            cbm = Double.valueOf(existingInventory.getCbm());
//                        }
//                        if (existingInventory.getInventoryQuantity() != null) {
//                            inventory_qty = existingInventory.getInventoryQuantity();
//                        }
//                        if (dbPickupLine.getPickedCbm() != null) {
//                            pickedCbm = dbPickupLine.getPickedCbm();
//                        }
//                        if (dbPickupLine.getPickedCbm() == null) {
//                            if (existingInventory.getCbmPerQuantity() != null) {
//                                cbmPerQty = Double.valueOf(existingInventory.getCbmPerQuantity());
//                            }
//                            if (existingInventory.getCbmPerQuantity() == null) {
//                                cbmPerQty = cbm / inventory_qty;
//                            }
//                            pickedCbm = cbmPerQty * dbPickupLine.getPickConfirmQty();
//                            dbPickupLine.setPickedCbm(pickedCbm);
//                        }
//                        cbm = cbm - pickedCbm;
//                        updateInventory.setCbm(String.valueOf(cbm));
//                    }
////                    InventoryV2 updatedInventory = inventoryService.updateInventoryV2(dbPickupLine.getCompanyCodeId(),
////                            dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
////                            dbPickupLine.getPickedPackCode(), dbPickupLine.getItemCode(), storageBin.getStorageBin(),
////                            dbPickupLine.getStockTypeId(), dbPickupLine.getSpecialStockIndicatorId(), updateInventory, loginUserID);
////                    log.info("Inventory is Updated : " + updatedInventory);
//                    InventoryV2 newInventoryV2 = new InventoryV2();
//                    inventory.setInventoryId(System.currentTimeMillis());
//                    BeanUtils.copyProperties(updateInventory, newInventoryV2, CommonUtils.getNullPropertyNames(updateInventory));
//                    InventoryV2 createdInventoryV2 = inventoryV2Repository.save(newInventoryV2);
//                    log.info("InventoryV2 created : " + createdInventoryV2);
//                } catch (Exception e) {
//                    log.error("Inventory update Error :" + e.toString());
//                    e.printStackTrace();
//                }
//            } else {
//                if (dbPickupLine.getStatusId() == 50L) {
//                    try {
//                        InventoryV2 newInventory = new InventoryV2();
//                        newInventory.setInventoryId(System.currentTimeMillis());
//                        newInventory.setLanguageId(dbPickupLine.getLanguageId());
//                        newInventory.setCompanyCodeId(dbPickupLine.getCompanyCodeId());
//                        newInventory.setPlantId(dbPickupLine.getPlantId());
//                        newInventory.setBinClassId(BIN_CLASS_ID);
//                        newInventory.setStockTypeId(inventory.getStockTypeId());
//                        newInventory.setWarehouseId(dbPickupLine.getWarehouseId());
//                        newInventory.setPackBarcodes(dbPickupLine.getPickedPackCode());
//                        newInventory.setItemCode(dbPickupLine.getItemCode());
//                        newInventory.setStorageBin(storageBin.getStorageBin());
//                        newInventory.setInventoryQuantity(dbPickupLine.getPickConfirmQty());
//                        newInventory.setSpecialStockIndicatorId(dbPickupLine.getSpecialStockIndicatorId());
//                        newInventory.setManufacturerCode(dbPickupLine.getManufacturerName());
//                        newInventory.setManufacturerName(dbPickupLine.getManufacturerName());
//                        newInventory.setCompanyDescription(dbPickupLine.getCompanyDescription());
//                        newInventory.setPlantDescription(dbPickupLine.getPlantDescription());
//                        newInventory.setWarehouseDescription(dbPickupLine.getWarehouseDescription());
//
//                        if (capacityCheck) {
//
//                            if (dbPickupLine.getPickedCbm() != null) {
//                                pickedCbm = dbPickupLine.getPickedCbm();
//                            }
//                            newInventory.setCbm(String.valueOf(pickedCbm));
//                        }
//
//                        if (dbPickupLine.getManufacturerName() == null) {
//                            OrderManagementLineV2 orderManagementLine = orderManagementLineService.getOrderManagementLineForQualityLineV2(
//                                    dbPickupLine.getCompanyCodeId(),
//                                    dbPickupLine.getPlantId(),
//                                    dbPickupLine.getLanguageId(),
//                                    dbPickupLine.getWarehouseId(),
//                                    dbPickupLine.getPreOutboundNo(),
//                                    dbPickupLine.getRefDocNumber(),
//                                    dbPickupLine.getLineNumber(),
//                                    dbPickupLine.getItemCode());
//                            log.info("OrderManagementLine: " + orderManagementLine);
//                            if (orderManagementLine != null) {
//                                newInventory.setManufacturerName(orderManagementLine.getManufacturerName());
//                                newInventory.setManufacturerCode(orderManagementLine.getManufacturerName());
//                            }
//                        }
//
//                        ImBasicData1 imbasicdata1 = mastersService.getImBasicData1ByItemCodeV2(newInventory.getItemCode(),
//                                newInventory.getLanguageId(), newInventory.getCompanyCodeId(),
//                                newInventory.getPlantId(), newInventory.getWarehouseId(),
//                                newInventory.getManufacturerName(), authTokenForMastersService.getAccess_token());
//
////                        List<IImbasicData1> imbasicdata1 = imbasicdata1Repository
////                                .findByItemCode(newInventory.getItemCode(), newInventory.getCompanyCodeId(), newInventory.getPlantId(), newInventory.getLanguageId());
////                        if (imbasicdata1 != null && !imbasicdata1.isEmpty()) {
//                        if (imbasicdata1 != null) {
//                            newInventory.setReferenceField8(imbasicdata1.getDescription());
//                            newInventory.setReferenceField9(imbasicdata1.getManufacturerPartNo());
//                        }
//                        if (storageBin != null) {
//                            newInventory.setReferenceField10(storageBin.getStorageSectionId());
//                            newInventory.setReferenceField5(storageBin.getAisleNumber());
//                            newInventory.setReferenceField6(storageBin.getShelfId());
//                            newInventory.setReferenceField7(storageBin.getRowId());
//                            newInventory.setLevelId(String.valueOf(storageBin.getFloorId()));
//                        }
//
//                        InventoryV2 createdInventory = inventoryService.createInventoryV2(newInventory, loginUserID);
//                        log.info("newInventory created : " + createdInventory);
//                    } catch (Exception e) {
//                        log.error("newInventory create Error :" + e.toString());
//                        e.printStackTrace();
//                    }
//                }
//            }

            /*
             * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
             */
            try {
                OutboundLineV2 updateOutboundLine = new OutboundLineV2();
                updateOutboundLine.setStatusId(STATUS_ID);

                statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, dbPickupLine.getLanguageId());
                updateOutboundLine.setStatusDescription(statusDescription);
                updateOutboundLine.setHandlingEquipment(dbPickupLine.getActualHeNo());
                updateOutboundLine.setAssignedPickerId(dbPickupLine.getAssignedPickerId());

                OutboundLineV2 outboundLine = outboundLineService.updateOutboundLineV2(dbPickupLine.getCompanyCodeId(),
                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                        dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(), dbPickupLine.getPartnerCode(),
                        dbPickupLine.getLineNumber(), dbPickupLine.getItemCode(), loginUserID, updateOutboundLine);
                log.info("outboundLine updated : " + outboundLine);
            } catch (Exception e) {
                log.error("outboundLine update Error :" + e.toString());
                e.printStackTrace();
            }

            /*
             * ------------------Record insertion in QUALITYHEADER table-----------------------------------
             * Allow to create QualityHeader only
             * for STATUS_ID = 50
             */
            if (dbPickupLine.getStatusId() == 50L) {
                String QC_NO = null;
                try {
                    QualityHeaderV2 newQualityHeader = new QualityHeaderV2();
                    BeanUtils.copyProperties(dbPickupLine, newQualityHeader, CommonUtils.getNullPropertyNames(dbPickupLine));

                    // QC_NO
                    /*
                     * Pass WH_ID - User logged in WH_ID and NUM_RAN_CODE =11 in NUMBERRANGE table
                     * and fetch NUM_RAN_CURRENT value of FISCALYEAR=CURRENT YEAR and add +1 and
                     * insert
                     */
                    Long NUM_RAN_CODE = 11L;
                    QC_NO = getNextRangeNumber(NUM_RAN_CODE, dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                            dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
                    newQualityHeader.setQualityInspectionNo(QC_NO);

                    // ------ PROD FIX : 29/09/2022:HAREESH -------(CWMS/IW/2022/018)
                    if (dbPickupLine.getPickConfirmQty() != null) {
                        newQualityHeader.setQcToQty(String.valueOf(dbPickupLine.getPickConfirmQty()));
                    }

                    newQualityHeader.setReferenceField1(dbPickupLine.getPickedStorageBin());
                    newQualityHeader.setReferenceField2(dbPickupLine.getPickedPackCode());
                    newQualityHeader.setReferenceField3(dbPickupLine.getDescription());
                    newQualityHeader.setReferenceField4(dbPickupLine.getItemCode());
                    newQualityHeader.setReferenceField5(String.valueOf(dbPickupLine.getLineNumber()));
                    newQualityHeader.setReferenceField6(dbPickupLine.getBarcodeId());

                    newQualityHeader.setManufacturerName(dbPickupLine.getManufacturerName());
                    newQualityHeader.setManufacturerPartNo(dbPickupLine.getManufacturerName());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setReferenceDocumentType(dbPickupLine.getReferenceDocumentType());
                    newQualityHeader.setPickListNumber(dbPickupLine.getPickListNumber());
                    newQualityHeader.setSalesInvoiceNumber(dbPickupLine.getSalesInvoiceNumber());
                    newQualityHeader.setSalesOrderNumber(dbPickupLine.getSalesOrderNumber());
                    newQualityHeader.setOutboundOrderTypeId(dbPickupLine.getOutboundOrderTypeId());
                    newQualityHeader.setSupplierInvoiceNo(dbPickupLine.getSupplierInvoiceNo());
                    newQualityHeader.setTokenNumber(dbPickupLine.getTokenNumber());


                    // STATUS_ID - Hard Coded Value "54"
                    newQualityHeader.setStatusId(54L);
//                    StatusId idStatus = idmasterService.getStatus(54L, dbPickupLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    statusDescription = stagingLineV2Repository.getStatusDescription(54L, dbPickupLine.getLanguageId());
                    newQualityHeader.setReferenceField10(statusDescription);
                    newQualityHeader.setStatusDescription(statusDescription);

                    QualityHeaderV2 createdQualityHeader = qualityHeaderService.createQualityHeaderV2(newQualityHeader,
                            loginUserID);
                    log.info("createdQualityHeader : " + createdQualityHeader);
                } catch (Exception e) {
                    log.error("createdQualityHeader Error :" + e.toString());
                    e.printStackTrace();
                }

                /*-----------------------InventoryMovement----------------------------------*/
                // Inserting record in InventoryMovement
//                try {
//                    Long BIN_CLASS_ID = 4L;
//                    StorageBinV2 storageBin = mastersService.getStorageBin(dbPickupLine.getCompanyCodeId(),
//                            dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), BIN_CLASS_ID,
//                            authTokenForMastersService.getAccess_token());
//                    subMvtTypeId = 2L;
//                    movementDocumentNo = QC_NO;
//                    stBin = storageBin.getStorageBin();
//                    movementQtyValue = "P";
//                    inventoryMovement = createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin,
//                            movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created for update2: " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error for update2 :" + e.toString());
//                    e.printStackTrace();
//                }
            }

            // Properties needed for updating PickupHeader
            warehouseId = dbPickupLine.getWarehouseId();
            preOutboundNo = dbPickupLine.getPreOutboundNo();
            refDocNumber = dbPickupLine.getRefDocNumber();
            partnerCode = dbPickupLine.getPartnerCode();
            pickupNumber = dbPickupLine.getPickupNumber();
            companyCodeId = dbPickupLine.getCompanyCodeId();
            plantId = dbPickupLine.getPlantId();
            languageId = dbPickupLine.getLanguageId();
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 47 only if all OutboundLines are STATUS_ID is 51
         */
        List<OutboundLineV2> outboundLineList = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        boolean hasStatus51 = false;
        List<Long> status51List = outboundLineList.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
        long status51IdCount = status51List.stream().filter(a -> a == 51L || a == 47L).count();
        log.info("status count : " + (status51IdCount == status51List.size()));
        hasStatus51 = (status51IdCount == status51List.size());
        if (!status51List.isEmpty() && hasStatus51) {
            //------------------------UpdateLock-Applied------------------------------------------------------------
            OutboundHeaderV2 outboundHeader = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
            outboundHeader.setStatusId(51L);
            statusDescription = stagingLineV2Repository.getStatusDescription(51L, languageId);
            outboundHeader.setStatusDescription(statusDescription);
            outboundHeader.setUpdatedBy(loginUserID);
            outboundHeader.setUpdatedOn(new Date());
            outboundHeaderV2Repository.save(outboundHeader);
            log.info("outboundHeader updated as 51.");

            //------------------------UpdateLock-Applied------------------------------------------------------------
            PreOutboundHeaderV2 preOutboundHeader = preOutboundHeaderService.getPreOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
            preOutboundHeader.setStatusId(51L);
            preOutboundHeader.setStatusDescription(statusDescription);
            preOutboundHeader.setUpdatedBy(loginUserID);
            preOutboundHeader.setUpdatedOn(new Date());
            preOutboundHeaderV2Repository.save(preOutboundHeader);
            log.info("PreOutboundHeader updated as 51.");
        }

        /*
         * Update OutboundHeader & Preoutbound Header STATUS_ID as 47 only if all OutboundLines are STATUS_ID is 50
         */
        List<OutboundLineV2> outboundLine50List = outboundLineService.getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
        boolean hasStatus50 = false;
        List<Long> status50List = outboundLine50List.stream().map(OutboundLine::getStatusId).collect(Collectors.toList());
        long status50IdCount = status50List.stream().filter(a -> a == 50L).count();
        log.info("status count : " + (status50IdCount == status50List.size()));
        hasStatus50 = (status50IdCount == status50List.size());
        if (!status50List.isEmpty() && hasStatus50) {
            //------------------------UpdateLock-Applied------------------------------------------------------------
            OutboundHeaderV2 outboundHeader50 = outboundHeaderService.getOutboundHeaderV2(companyCodeId, plantId, languageId, refDocNumber, warehouseId);
            outboundHeader50.setStatusId(50L);
            statusDescription = stagingLineV2Repository.getStatusDescription(50L, languageId);
            outboundHeader50.setStatusDescription(statusDescription);
            outboundHeader50.setUpdatedBy(loginUserID);
            outboundHeader50.setUpdatedOn(new Date());
            outboundHeaderV2Repository.save(outboundHeader50);
            log.info("outboundHeader updated as 50.");
        }

        /*---------------------------------------------PickupHeader Updates---------------------------------------*/
        // -----------------logic for checking all records as 51 then only it should go to update header-----------*/
        try {
            boolean isStatus51 = false;
            List<Long> statusList = createdPickupLineList.stream().map(PickupLine::getStatusId)
                    .collect(Collectors.toList());
            long statusIdCount = statusList.stream().filter(a -> a == 51L).count();
            log.info("status count : " + (statusIdCount == statusList.size()));
            isStatus51 = (statusIdCount == statusList.size());
            if (!statusList.isEmpty() && isStatus51) {
                STATUS_ID = 51L;
            } else {
                STATUS_ID = 50L;
            }

            //------------------------UpdateLock-Applied------------------------------------------------------------
            PickupHeaderV2 pickupHeader = pickupHeaderService.getPickupHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                    partnerCode, pickupNumber);
            pickupHeader.setStatusId(STATUS_ID);

            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID, languageId);
//            StatusId idStatus = idmasterService.getStatus(STATUS_ID, warehouseId, authTokenForIDService.getAccess_token());
            pickupHeader.setReferenceField7(statusDescription);        // tblpickupheader REF_FIELD_7
            pickupHeader.setStatusDescription(statusDescription);

            pickupHeader.setPickUpdatedBy(loginUserID);
            pickupHeader.setPickUpdatedOn(new Date());
            pickupHeader = pickupHeaderV2Repository.save(pickupHeader);
            log.info("PickupHeader updated: " + pickupHeader);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("PickupHeader update error: " + e.toString());
        }
        return createdPickupLineList;
    }


    /**
     * @param dbPickupLine
     * @param subMvtTypeId
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovementV2(PickupLineV2 dbPickupLine, Long subMvtTypeId,
                                                        String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(dbPickupLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbPickupLine));

        inventoryMovement.setCompanyCodeId(dbPickupLine.getCompanyCodeId());

        // MVT_TYP_ID
        inventoryMovement.setMovementType(3L);

        // SUB_MVT_TYP_ID
        inventoryMovement.setSubmovementType(subMvtTypeId);

        // VAR_ID
        inventoryMovement.setVariantCode(1L);

        // VAR_SUB_ID
        inventoryMovement.setVariantSubCode("1");

        // STR_MTD
        inventoryMovement.setStorageMethod("1");

        // STR_NO
        inventoryMovement.setBatchSerialNumber("1");

        // MVT_DOC_NO
//        inventoryMovement.setMovementDocumentNo(movementDocumentNo);
        inventoryMovement.setReferenceField10(movementDocumentNo);
        inventoryMovement.setManufacturerName(dbPickupLine.getManufacturerName());
        inventoryMovement.setDescription(dbPickupLine.getDescription());
        inventoryMovement.setBarcodeId(dbPickupLine.getBarcodeId());
        inventoryMovement.setCompanyDescription(dbPickupLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(dbPickupLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(dbPickupLine.getWarehouseDescription());
        inventoryMovement.setRefDocNumber(dbPickupLine.getRefDocNumber());
        inventoryMovement.setBarcodeId(dbPickupLine.getBarcodeId());
        inventoryMovement.setReferenceNumber(dbPickupLine.getPreOutboundNo());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);


        // BAR_CODE
        inventoryMovement.setPackBarcodes(dbPickupLine.getPickedPackCode());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbPickupLine.getPickConfirmQty());

        // BAL_OH_QTY
        Double sumOfInvQty = inventoryService.getInventoryQtyCountForInvMmt(
                dbPickupLine.getCompanyCodeId(),
                dbPickupLine.getPlantId(),
                dbPickupLine.getLanguageId(),
                dbPickupLine.getWarehouseId(),
                dbPickupLine.getManufacturerName(),
                dbPickupLine.getItemCode());
        log.info("BalanceOhQty: " + sumOfInvQty);
        if(sumOfInvQty != null) {
        inventoryMovement.setBalanceOHQty(sumOfInvQty);
            Double openQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - dbPickupLine.getPickConfirmQty();
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + dbPickupLine.getPickConfirmQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
            log.info("OH Qty, OpenQty : " + sumOfInvQty + ", " + openQty);
        }
        if(sumOfInvQty == null) {
            inventoryMovement.setBalanceOHQty(0D);
            Double openQty = 0D;
            sumOfInvQty = 0D;
            if(movementQtyValue.equalsIgnoreCase("P")) {
                openQty = sumOfInvQty - dbPickupLine.getPickConfirmQty();
                if(openQty < 0){
                    openQty = 0D;
                }
            }
            if(movementQtyValue.equalsIgnoreCase("N")) {
                openQty = sumOfInvQty + dbPickupLine.getPickConfirmQty();
            }
            inventoryMovement.setReferenceField2(String.valueOf(openQty));          //Qty before inventory Movement occur
        }

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbPickupLine.getPickUom());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(loginUserID);

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbPickupLine.getPickupCreatedOn());
        inventoryMovement.setMovementDocumentNo(String.valueOf(System.currentTimeMillis()));
        InventoryMovement createdInventoryMovement = inventoryMovementRepository.save(inventoryMovement);
        return createdInventoryMovement;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @param updatePickupLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLineV2 updatePickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String itemCode, String loginUserID, UpdatePickupLine updatePickupLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PickupLineV2 dbPickupLine = getPickupLineForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode);
        if (dbPickupLine != null) {
            BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
                    CommonUtils.getNullPropertyNames(updatePickupLine));
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineV2Repository.save(dbPickupLine);
        }
        return null;
    }

    /**
     *
     * @param updateBarcodeInput
     * @return
     */
//    @Transactional
    public ImPartner updatePickupLineForBarcodeV2(UpdateBarcodeInput updateBarcodeInput) {

        if (updateBarcodeInput != null) {
            String companyCodeId = updateBarcodeInput.getCompanyCodeId();
            String plantId = updateBarcodeInput.getPlantId();
            String languageId = updateBarcodeInput.getLanguageId();
            String warehouseId = updateBarcodeInput.getWarehouseId();
            String itemCode = updateBarcodeInput.getItemCode();
            String manufacturerName = updateBarcodeInput.getManufacturerName();
            String barcodeId = updateBarcodeInput.getBarcodeId();
            String loginUserID = updateBarcodeInput.getLoginUserID();

            ImPartner updateBarcode = imPartnerService.updateImPartner(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, loginUserID);
            if (updateBarcode != null) {
//            List<PickupLineV2> dbPickupLine = pickupLineV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndDeletionIndicator(
//                    companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 48L, 0L);
//            log.info("Pickupline statusId_48: " + dbPickupLine);
//            if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
//                for (PickupLineV2 pickupLineV2 : dbPickupLine) {
//                    pickupLineV2.setBarcodeId(barcodeId);
//                    pickupLineV2.setPickupUpdatedBy(loginUserID);
//                    pickupLineV2.setPickupUpdatedOn(new Date());
//                    pickupLineV2Repository.save(pickupLineV2);
//                }
//            }
                pickupHeaderService.updatePickupHeaderForBarcodeV2(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, loginUserID);
                orderManagementLineService.updateOrderManagementLineForBarcodeV2(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, loginUserID);
//            inventoryService.updateInventoryForBarcodeV2(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, loginUserID);
                log.info("BarcodeId Update Successful in ImPartner, PickupHeader, OrderManagementLine");
            }
            return updateBarcode;
        }
        return null;
    }

    public List<PickupLineV2> updatePickupLineForConfirmationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                                                String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                                                PickupLineV2 updatePickupLine) throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        List<PickupLineV2> dbPickupLine = getPickupLineForUpdateConfirmationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                partnerCode, lineNumber, itemCode);
        if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
            List<PickupLineV2> toSave = new ArrayList<>();
            for (PickupLineV2 data : dbPickupLine) {
                BeanUtils.copyProperties(updatePickupLine, data, CommonUtils.getNullPropertyNames(updatePickupLine));
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupUpdatedOn(new Date());
                toSave.add(data);
            }
            return pickupLineV2Repository.saveAll(toSave);
        }
        return null;
    }

    /**
     * @param actualHeNo
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param loginUserID
     * @param updatePickupLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLineV2 updatePickupLineV2(String companyCodeId, String plantId, String languageId, String actualHeNo, String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String pickedStorageBin,
                                           String pickedPackCode, String loginUserID, PickupLineV2 updatePickupLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PickupLineV2 dbPickupLine = getPickupLineForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
        if (dbPickupLine != null) {
            BeanUtils.copyProperties(updatePickupLine, dbPickupLine,
                    CommonUtils.getNullPropertyNames(updatePickupLine));
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineV2Repository.save(dbPickupLine);
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param pickupNumber
     * @param itemCode
     * @param actualHeNo
     * @param pickedStorageBin
     * @param pickedPackCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLineV2 deletePickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String pickupNumber, String itemCode, String actualHeNo,
                                           String pickedStorageBin, String pickedPackCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PickupLineV2 dbPickupLine = getPickupLineForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, pickupNumber, itemCode, pickedStorageBin, pickedPackCode, actualHeNo);
        if (dbPickupLine != null) {
            dbPickupLine.setDeletionIndicator(1L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineV2Repository.save(dbPickupLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public PickupLineV2 deletePickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                           String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        PickupLineV2 dbPickupLine = getPickupLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode);
        if (dbPickupLine != null) {
            dbPickupLine.setDeletionIndicator(1L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            return pickupLineV2Repository.save(dbPickupLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<PickupLineV2> deletePickupLineForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                            String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                            String itemCode, String manufacturerName, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<PickupLineV2> dbPickupLine = getPickupLineForReversalV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode, manufacturerName);
        if (dbPickupLine != null && !dbPickupLine.isEmpty()) {
            List<PickupLineV2> toSavePickupLineList = new ArrayList<>();
            dbPickupLine.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setPickupUpdatedBy(loginUserID);
                data.setPickupUpdatedOn(new Date());
                toSavePickupLineList.add(data);
            });
            return pickupLineV2Repository.saveAll(toSavePickupLineList);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param OB_ORD_TYP_ID
     * @param proposedPackBarCode
     * @param proposedStorageBin
     * @return
     */
//    public List<InventoryV2> getAdditionalBinsV2(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, Long OB_ORD_TYP_ID,
//                                                 String proposedPackBarCode, String proposedStorageBin) {
//        log.info("---OB_ORD_TYP_ID--------> : " + OB_ORD_TYP_ID);
//
//        if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
//            List<String> storageSectionIds = Arrays.asList("ZB", "ZC", "ZG", "ZT"); // ZB,ZC,ZG,ZT
//            List<InventoryV2> inventoryAdditionalBins = fetchAdditionalBinsV2(companyCodeId, plantId, languageId, storageSectionIds, warehouseId, itemCode,
//                    proposedPackBarCode, proposedStorageBin);
//            return inventoryAdditionalBins;
//        }
//
//        /*
//         * Pass the selected
//         * ST_BIN/WH_ID/ITM_CODE/ALLOC_QTY=0/STCK_TYP_ID=2/SP_ST_IND_ID=2 for
//         * OB_ORD_TYP_ID = 2 and fetch ST_BIN / PACK_BARCODE / INV_QTY values and
//         * display
//         */
//        if (OB_ORD_TYP_ID == 2L) {
//            List<String> storageSectionIds = Arrays.asList("ZD"); // ZD
//            List<InventoryV2> inventoryAdditionalBins = fetchAdditionalBinsForOB2V2(companyCodeId, plantId, languageId, storageSectionIds, warehouseId,
//                    itemCode, proposedPackBarCode, proposedStorageBin);
//            return inventoryAdditionalBins;
//        }
//        return null;
//    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param proposedPackBarCode
     * @param proposedStorageBin
     * @return
     */
    private List<IInventoryImpl> fetchAdditionalBinsV2(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode,
                                                       String proposedPackBarCode, String proposedStorageBin, String manufacturerName, Long binclassId) {
        List<IInventoryImpl> finalizedInventoryList = new ArrayList<>();
        List<IInventoryImpl> listInventory = inventoryService.getInventoryV2ForAdditionalBinsV2(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binclassId);
        log.info("selected listInventory--------: " + listInventory.size());
        boolean toBeIncluded = false;
        for (IInventoryImpl inventory : listInventory) {
            if (inventory.getPackBarcodes().equalsIgnoreCase(proposedPackBarCode)) {
                toBeIncluded = false;
                log.info("toBeIncluded----Pack----: " + toBeIncluded);
                if (inventory.getStorageBin().equalsIgnoreCase(proposedStorageBin)) {
                    toBeIncluded = false;
                } else {
                    toBeIncluded = true;
                }
            } else {
                toBeIncluded = true;
            }

            log.info("toBeIncluded--------: " + toBeIncluded);
            if (toBeIncluded) {
                finalizedInventoryList.add(inventory);
            }
        }
        log.info("Additional Bins: " + finalizedInventoryList.size());
        return finalizedInventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<PickupLineV2> getPickupLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
            return pickupLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public List<PickupLineV2> getPickupLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                     String refDocNumber, String itemCode, String manufacturerName) {
        List<PickupLineV2> pickupLine = pickupLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, itemCode, manufacturerName, 0L);
            return pickupLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //DeletePickupLine
    public List<PickupLineV2> deletePickUpLine(String companyCodeId, String plantId, String languageId,
                                               String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID)throws Exception {

        List<PickupLineV2> pickupLineV2List = new ArrayList<>();
        List<PickupLineV2> dbPickUpLine = pickupLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - PickupLine : " + dbPickUpLine);
        if(dbPickUpLine != null && !dbPickUpLine.isEmpty()){
            for(PickupLineV2 pickupLineV2 : dbPickUpLine){
                pickupLineV2.setPickupUpdatedBy(loginUserID);
                pickupLineV2.setPickupUpdatedOn(new Date());
                pickupLineV2.setDeletionIndicator(1L);
                PickupLineV2 pickUp = pickupLineV2Repository.save(pickupLineV2);
                pickupLineV2List.add(pickUp);
            }
        }
        return pickupLineV2List;
    }

    /**
     * Pick List cancel
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    //DeletePickupLine
    public List<PickupLineV2> getPLCPickUpLine(String companyCodeId, String plantId, String languageId,
                                               String warehouseId, String refDocNumber, String preOutboundNo) {
        List<PickupLineV2> dbPickUpLine = pickupLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - PickupLine : " + dbPickUpLine);
        return dbPickUpLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param pickupHeader
     * @param pickedQty
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public void createPickupLineNewV3(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
			String preOutboundNo, PickupHeaderV2 pickupHeader, Double pickedQty, String loginUserID,
			String idMasterAuthToken) throws Exception {
		try {
			log.info("1.V3 create pickupLine {}|{}", pickupHeader, loginUserID);
			Long STATUS_ID = 0L;
			// Create PickUpLine
			PickupLineV2 createdPickupLine = null;
			PickupLineV2 dbPickupLine = new PickupLineV2();
			BeanUtils.copyProperties(pickupHeader, dbPickupLine, CommonUtils.getNullPropertyNames(pickupHeader));
			dbPickupLine.setCompanyCodeId(companyCodeId);
			dbPickupLine.setLanguageId(languageId);
			dbPickupLine.setPlantId(plantId);
			dbPickupLine.setWarehouseId(warehouseId);

			if (pickedQty > 0) {
				STATUS_ID = 59L;
			} else {
				STATUS_ID = 51L;
			}

			log.info("newPickupLine STATUS: " + STATUS_ID);
			dbPickupLine.setStatusId(STATUS_ID);
			statusDescription = getStatusDescription(STATUS_ID, languageId);
			dbPickupLine.setStatusDescription(statusDescription);

			OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(
					companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
					pickupHeader.getLineNumber(), pickupHeader.getItemCode(), pickupHeader.getBarcodeId());
			log.info("OrderManagementLine: " + dbOrderManagementLine);
			if (dbOrderManagementLine == null) {
				dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(companyCodeId, plantId,
						languageId, warehouseId, refDocNumber, pickupHeader.getLineNumber(),
						pickupHeader.getItemCode());
				log.info("OrderManagementLine: " + dbOrderManagementLine);
			}

			if (dbOrderManagementLine != null) {
				dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
				dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
				dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
				dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
				dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
				dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
				dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
				dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
				dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
				dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());

				// V3
				dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
				dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
				dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
				dbPickupLine.setGender(dbOrderManagementLine.getGender());
				dbPickupLine.setColor(dbOrderManagementLine.getColor());
				dbPickupLine.setSize(dbOrderManagementLine.getSize());
				dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
			}

			dbPickupLine.setPickedStorageBin(pickupHeader.getProposedStorageBin());
			dbPickupLine.setPickedPackCode(pickupHeader.getProposedPackBarCode());
			dbPickupLine.setPickConfirmQty(pickedQty);
			dbPickupLine.setAllocatedQty(pickupHeader.getPickToQty());
			dbPickupLine.setPickupCreatedOn(pickupHeader.getPickupCreatedOn());

			if (pickupHeader.getPickupCreatedBy() != null) {
				dbPickupLine.setPickupCreatedBy(pickupHeader.getPickupCreatedBy());
			} else {
				dbPickupLine.setPickupCreatedBy(pickupHeader.getPickUpdatedBy());
			}

			Double VAR_QTY = getQuantity(dbPickupLine.getAllocatedQty())
					- getQuantity(dbPickupLine.getPickConfirmQty());
			dbPickupLine.setVarianceQuantity(VAR_QTY);
			log.info("Var_Qty: " + VAR_QTY);

			dbPickupLine.setActualHeNo(PICK_HE_NO);
			dbPickupLine.setDeletionIndicator(0L);
			dbPickupLine.setPickupUpdatedBy(loginUserID);
			dbPickupLine.setPickupConfirmedBy(loginUserID);
			dbPickupLine.setPickupUpdatedOn(new Date());
			dbPickupLine.setPickupConfirmedOn(new Date());

			// Checking for Duplicates
			List<PickupLineV2> existingPickupLine = pickupLineV2Repository
					.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
							languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber,
							dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(),
							dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(),
							dbPickupLine.getPickedPackCode(), dbPickupLine.getBarcodeId(), 0L);
			log.info("existingPickupLine : " + existingPickupLine);

			if (existingPickupLine == null || existingPickupLine.isEmpty()) {
				String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(),
						dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
						dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
				dbPickupLine.setReferenceField1(leadTime);
				log.info("LeadTime: " + leadTime);

				createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
				log.info("dbPickupLine created: " + createdPickupLine);
			} else {
				throw new BadRequestException(
						"PickupLine Record is getting duplicated. Given data already exists in the Database. : "
								+ existingPickupLine);
			}

			/*---------------------------------------------Inventory Updates-------------------------------------------*/
			// Updating respective tables
			if (createdPickupLine != null) {

				// webSocketNotification(dbPickupLine, loginUserID);
				// fireBaseNotification(dbPickupLine, loginUserID);

//                    pickupHeader.setStatusId(57L);
				statusDescription = getStatusDescription(59L, languageId);
//                    pickupHeader.setStatusDescription(statusDescription);
//                    pickupHeaderV2Repository.saveAndFlush(pickupHeader);
				pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
						dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), 59L, statusDescription);
				log.info("PickUpHeader Updated");

				// ------------------------UpdateLock-Applied------------------------------------------------------------
				//
 				// 	---------Commenting the below code for Walkaroo Implementation specifically
				// 	-----------------------------------------------------------------------------------------------------
				if (STATUS_ID == 59) {
					updateInventoryV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(),
							dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
							dbPickupLine.getPickedStorageBin(), dbPickupLine.getAllocatedQty(),
							dbPickupLine.getPickConfirmQty(), loginUserID);
				}
				if (STATUS_ID == 51) { // Picker denial
					updateInventoryV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(),
							dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
							dbPickupLine.getPickedStorageBin(), dbPickupLine.getAllocatedQty(), loginUserID);
				}

				// CreateQualityHeader
				qualityHeaderService.createQualityHeaderV3(companyCodeId, plantId, languageId, warehouseId,
						dbPickupLine, pickupHeader, idMasterAuthToken, loginUserID);
			}
		} catch (Exception e) {
			log.error("1.Exception while pickupLine create");
			throw e;
		}
	}

    /**
     * Modified for SAP orders - 30/06/2025
     * Aakash Vinayak
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param pickupHeader
     * @param pickedQty
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public void createPickupLineNewV4(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                      String preOutboundNo, PickupHeaderV2 pickupHeader, Double pickedQty, String loginUserID,
                                      String idMasterAuthToken) throws Exception {
        try {
            log.info("1.V3 create pickupLine {}|{}", pickupHeader, loginUserID);
            Long STATUS_ID = 0L;
            // Create PickUpLine
            PickupLineV2 createdPickupLine = null;
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(pickupHeader, dbPickupLine, CommonUtils.getNullPropertyNames(pickupHeader));
            dbPickupLine.setCompanyCodeId(companyCodeId);
            dbPickupLine.setLanguageId(languageId);
            dbPickupLine.setPlantId(plantId);
            dbPickupLine.setWarehouseId(warehouseId);

            if (pickedQty > 0) {
                STATUS_ID = 59L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);
            statusDescription = getStatusDescription(STATUS_ID, languageId);
            dbPickupLine.setStatusDescription(statusDescription);

            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(
                    companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                    pickupHeader.getLineNumber(), pickupHeader.getItemCode(), pickupHeader.getBarcodeId());
            log.info("OrderManagementLine: " + dbOrderManagementLine);
            if (dbOrderManagementLine == null) {
                dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(companyCodeId, plantId,
                        languageId, warehouseId, refDocNumber, pickupHeader.getLineNumber(),
                        pickupHeader.getItemCode());
                log.info("OrderManagementLine: " + dbOrderManagementLine);
            }

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());

                // V3
                dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
                dbPickupLine.setGender(dbOrderManagementLine.getGender());
                dbPickupLine.setColor(dbOrderManagementLine.getColor());
                dbPickupLine.setSize(dbOrderManagementLine.getSize());
                dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
            }

            dbPickupLine.setPickedStorageBin(pickupHeader.getProposedStorageBin());
            dbPickupLine.setPickedPackCode(pickupHeader.getProposedPackBarCode());
            dbPickupLine.setPickConfirmQty(pickedQty);
            dbPickupLine.setAllocatedQty(pickupHeader.getPickToQty());
            dbPickupLine.setPickupCreatedOn(pickupHeader.getPickupCreatedOn());

            if (pickupHeader.getPickupCreatedBy() != null) {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickupCreatedBy());
            } else {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickUpdatedBy());
            }

            Double VAR_QTY = getQuantity(dbPickupLine.getAllocatedQty())
                    - getQuantity(dbPickupLine.getPickConfirmQty());
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setActualHeNo(PICK_HE_NO);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository
                    .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber,
                            dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(),
                            dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(),
                            dbPickupLine.getPickedPackCode(), dbPickupLine.getBarcodeId(), 0L);
            log.info("existingPickupLine : " + existingPickupLine);

            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                log.info("Inputs for LeadTime : companyCode ---> " + dbPickupLine.getCompanyCodeId() + ", plantId ---> " + dbPickupLine.getPlantId() +
                         ", languageId ---> " + dbPickupLine.getLanguageId() + ", warehouse ---> " + dbPickupLine.getWarehouseId() +
                         ", pickupNumber ---> " + dbPickupLine.getPickupNumber() + ", barcodeId ---> " + dbPickupLine.getBarcodeId() + ", lDate ---> " + new Date());
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(),
                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
            } else {
                throw new BadRequestException(
                        "PickupLine Record is getting duplicated. Given data already exists in the Database. : "
                                + existingPickupLine);
            }

            /*---------------------------------------------Inventory Updates-------------------------------------------*/
            // Updating respective tables
            if (createdPickupLine != null) {

                // webSocketNotification(dbPickupLine, loginUserID);
                // fireBaseNotification(dbPickupLine, loginUserID);

//                    pickupHeader.setStatusId(57L);
                statusDescription = getStatusDescription(59L, languageId);
//                    pickupHeader.setStatusDescription(statusDescription);
//                    pickupHeaderV2Repository.saveAndFlush(pickupHeader);
                pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), 59L, statusDescription);
                log.info("PickUpHeader Updated");

                //  ------------------------UpdateLock-Applied------------------------------------------------------------
				//	---------Commenting the below code for Walkaroo Implementation specifically
				// 	-----------------------------------------------------------------------------------------------------
				if (STATUS_ID == 59) {
					updateInventoryV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(),
							dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
							dbPickupLine.getPickedStorageBin(), dbPickupLine.getAllocatedQty(),
							dbPickupLine.getPickConfirmQty(), loginUserID);
				}
				/*
				 * The below block has been commented as SAP is not needed the below logic
				 */
//				if (STATUS_ID == 51) { // Picker denial
//					updateInventoryV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(),
//							dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
//							dbPickupLine.getPickedStorageBin(), dbPickupLine.getAllocatedQty(), loginUserID);
//				}

                Double deliveryQty = pickupLineV2Repository.getPickupLineCountV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, dbPickupLine.getLineNumber(), dbPickupLine.getItemCode());

                log.info("Sum Of PickConfirmQty Group By RefDocNo From PickupLine  ----------> Qty is -->  {} ", deliveryQty);
                outboundLineV2Repository.updateOutboundLineForDeliveryConfirm(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        deliveryQty, STATUS_ID, statusDescription, loginUserID, new Date());

                Long STATUS_ID_59 = 59L;
                statusDescription = getStatusDescription(STATUS_ID_59, languageId);
                outboundLineV2Repository.deliveryConfirmationProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, loginUserID, new Date());

                // CreateQualityHeader
//                qualityHeaderService.createQualityHeaderV3(companyCodeId, plantId, languageId, warehouseId,
//                        dbPickupLine, pickupHeader, idMasterAuthToken, loginUserID);
            }
        } catch (Exception e) {
            log.error("1.Exception while pickupLine create");
            throw e;
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param storageBin
     * @param loginUserID
     */
    public void updateStorageBinEmptyStatus (String companyCodeId, String plantId, String languageId,
                                             String warehouseId, String storageBin, String loginUserID) {
        StorageBinV2 dbStorageBin = storageBinService.getStorageBinV2(companyCodeId, plantId, languageId, warehouseId, storageBin);
        if (dbStorageBin != null) {
            dbStorageBin.setStatusId(0L);
            StorageBinV2 updateStorageBin = storageBinService.updateStorageBinV2(storageBin, dbStorageBin, companyCodeId, plantId, languageId, warehouseId, loginUserID);
            log.info("Bin Emptied Update Success----> " + updateStorageBin);
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param barcodeId
     * @param pickupHeader
     * @param pickedQty
     * @param loginUserID
     * @throws Exception
     */
    public void createPickupLineNewV3(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                      String preOutboundNo, String barcodeId, PickupHeaderV2 pickupHeader, Double pickedQty, String loginUserID, String idMasterAuthToken) throws Exception {
        try {
            log.info("2.V3 create pickupLine {}|{}", pickupHeader, loginUserID);
            Long STATUS_ID = 59L;
            // Create PickUpLine
            PickupLineV2 createdPickupLine = null;
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(pickupHeader, dbPickupLine, CommonUtils.getNullPropertyNames(pickupHeader));
            dbPickupLine.setCompanyCodeId(companyCodeId);
            dbPickupLine.setLanguageId(languageId);
            dbPickupLine.setPlantId(plantId);
            dbPickupLine.setWarehouseId(warehouseId);

            dbPickupLine.setStatusId(STATUS_ID);
            statusDescription = getStatusDescription(STATUS_ID, languageId);
            dbPickupLine.setStatusDescription(statusDescription);

            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(
                    companyCodeId, plantId, languageId, warehouseId, refDocNumber, pickupHeader.getLineNumber(), pickupHeader.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());

                //V3
                dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
                dbPickupLine.setGender(dbOrderManagementLine.getGender());
                dbPickupLine.setColor(dbOrderManagementLine.getColor());
                dbPickupLine.setSize(dbOrderManagementLine.getSize());
                dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
            }

            dbPickupLine.setPickupCreatedOn(pickupHeader.getPickupCreatedOn());
            if (pickupHeader.getPickupCreatedBy() != null) {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickupCreatedBy());
            } else {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickUpdatedBy());
            }

            Double VAR_QTY = getQuantity(dbPickupLine.getAllocatedQty()) - getQuantity(dbPickupLine.getPickConfirmQty());
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            InventoryV2 inventory = 
            		inventoryService.getInventoryForPickUpLineV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), barcodeId);
            log.info("PickedInventory Bin: " + inventory);

            dbPickupLine.setActualHeNo(PICK_HE_NO);
            dbPickupLine.setBarcodeId(barcodeId);
            dbPickupLine.setPickedStorageBin(inventory.getStorageBin());
            dbPickupLine.setPickedPackCode(pickupHeader.getProposedPackBarCode());
            dbPickupLine.setPickConfirmQty(pickedQty);
            dbPickupLine.setAllocatedQty(pickedQty);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine =
                    pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber,
                            dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(), dbPickupLine.getItemCode(),
                            dbPickupLine.getPickedStorageBin(), dbPickupLine.getPickedPackCode(), dbPickupLine.getBarcodeId(), 0L);
            log.info("existingPickupLine : " + existingPickupLine);

            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                        dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
            }

            /*---------------------------------------------Inventory Updates-------------------------------------------*/
            // Updating respective tables
            if (createdPickupLine != null) {

    //            webSocketNotification(dbPickupLine, loginUserID);
    //            fireBaseNotification(dbPickupLine, loginUserID);

//                pickupHeader.setStatusId(57L);
//                statusDescription = getStatusDescription(57L, languageId);
//                pickupHeader.setStatusDescription(statusDescription);
//                pickupHeaderV2Repository.saveAndFlush(pickupHeader);

                statusDescription = getStatusDescription(59L, languageId);
                pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), 59L, statusDescription);
                log.info("PickUpHeader Updated");

                //------------------------UpdateLock-Applied------------------------------------------------------------
                updateInventoryPickedBarcodeV3(companyCodeId, plantId, languageId, warehouseId,
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        dbPickupLine.getBarcodeId(), dbPickupLine.getPickedStorageBin(),
                        dbPickupLine.getPickConfirmQty(), loginUserID);
                        
                //CreateQualityHeader
                qualityHeaderService.createQualityHeaderV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, pickupHeader, idMasterAuthToken, loginUserID);
            }
        } catch (Exception e) {
            log.error("2.Exception while pickupLine create");
            throw e;
        }
    }

    /**
     * Modified for SAP orders - 30/06/2025
     * Aakash Vinayak
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @param barcodeId
     * @param pickupHeader
     * @param pickedQty
     * @param loginUserID
     * @throws Exception
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public void createPickupLineNewV4(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                      String preOutboundNo, String barcodeId, PickupHeaderV2 pickupHeader, Double pickedQty, String loginUserID, String idMasterAuthToken) throws Exception {
        try {
            log.info("2.V3 create pickupLine {}|{}", pickupHeader, loginUserID);
            Long STATUS_ID = 59L;
            // Create PickUpLine
            PickupLineV2 createdPickupLine = null;
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(pickupHeader, dbPickupLine, CommonUtils.getNullPropertyNames(pickupHeader));
            dbPickupLine.setCompanyCodeId(companyCodeId);
            dbPickupLine.setLanguageId(languageId);
            dbPickupLine.setPlantId(plantId);
            dbPickupLine.setWarehouseId(warehouseId);

            dbPickupLine.setStatusId(STATUS_ID);
            statusDescription = getStatusDescription(STATUS_ID, languageId);
            dbPickupLine.setStatusDescription(statusDescription);

            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(
                    companyCodeId, plantId, languageId, warehouseId, refDocNumber, pickupHeader.getLineNumber(), pickupHeader.getItemCode());
            log.info("OrderManagementLine: " + dbOrderManagementLine);

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());

                //V3
                dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
                dbPickupLine.setGender(dbOrderManagementLine.getGender());
                dbPickupLine.setColor(dbOrderManagementLine.getColor());
                dbPickupLine.setSize(dbOrderManagementLine.getSize());
                dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
            }

            dbPickupLine.setPickupCreatedOn(pickupHeader.getPickupCreatedOn());
            if (pickupHeader.getPickupCreatedBy() != null) {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickupCreatedBy());
            } else {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickUpdatedBy());
            }

            Double VAR_QTY = getQuantity(dbPickupLine.getAllocatedQty()) - getQuantity(dbPickupLine.getPickConfirmQty());
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            InventoryV2 inventory =
                    inventoryService.getInventoryForPickUpLineV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), barcodeId);
            log.info("PickedInventory Bin: " + inventory);

            dbPickupLine.setActualHeNo(PICK_HE_NO);
            dbPickupLine.setBarcodeId(barcodeId);
            dbPickupLine.setPickedStorageBin(inventory.getStorageBin());
            dbPickupLine.setPickedPackCode(pickupHeader.getProposedPackBarCode());
            dbPickupLine.setPickConfirmQty(pickedQty);
            dbPickupLine.setAllocatedQty(pickedQty);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine =
                    pickupLineV2Repository.findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber,
                            dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(), dbPickupLine.getItemCode(),
                            dbPickupLine.getPickedStorageBin(), dbPickupLine.getPickedPackCode(), dbPickupLine.getBarcodeId(), 0L);
            log.info("existingPickupLine : " + existingPickupLine);

            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(),
                        dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(), dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
            }

            /*---------------------------------------------Inventory Updates-------------------------------------------*/
            // Updating respective tables
            if (createdPickupLine != null) {

                //            webSocketNotification(dbPickupLine, loginUserID);
                //            fireBaseNotification(dbPickupLine, loginUserID);

//                pickupHeader.setStatusId(57L);
//                statusDescription = getStatusDescription(57L, languageId);
//                pickupHeader.setStatusDescription(statusDescription);
//                pickupHeaderV2Repository.saveAndFlush(pickupHeader);

                statusDescription = getStatusDescription(59L, languageId);
                pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), 59L, statusDescription);
                log.info("PickUpHeader Updated");

                //------------------------UpdateLock-Applied------------------------------------------------------------
                updateInventoryPickedBarcodeV3(companyCodeId, plantId, languageId, warehouseId,
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        dbPickupLine.getBarcodeId(), dbPickupLine.getPickedStorageBin(),
                        dbPickupLine.getPickConfirmQty(), loginUserID);

                Double deliveryQty = pickupLineV2Repository.getPickupLineCountV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, dbPickupLine.getLineNumber(), dbPickupLine.getItemCode());

                log.info("Sum Of PickConfirmQty Group By RefDocNo From PickupLine  ----------> Qty is -->  {} ", deliveryQty);
                outboundLineV2Repository.updateOutboundLineForDeliveryConfirm(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        deliveryQty, STATUS_ID, statusDescription, loginUserID, new Date());

                Long STATUS_ID_59 = 59L;
                statusDescription = getStatusDescription(STATUS_ID_59, languageId);
                outboundLineV2Repository.deliveryConfirmationProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, loginUserID, new Date());

                //CreateQualityHeader
//                qualityHeaderService.createQualityHeaderV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, pickupHeader, idMasterAuthToken, loginUserID);
            }
        } catch (Exception e) {
            log.error("2.Exception while pickupLine create");
            throw e;
        }
    }


    /**
     * pickCnf
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param allocatedQty
     * @param pickConfirmQty
     * @param loginUserID
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private void updateInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                   String itemCode, String manufacturerName, String barcodeId, String storageBin,
			Double allocatedQty, Double pickConfirmQty, String loginUserID) {
		InventoryV2 inventory = inventoryService.getInventoryForPickUpLineV3(companyCodeId, plantId, languageId,
				warehouseId, itemCode, manufacturerName, barcodeId, storageBin);
		log.info("inventory record queried: " + inventory);
		if (inventory != null) {
			try {
				double[] inventoryQty = calculateInventory(pickConfirmQty, inventory.getInventoryQuantity(),
                        inventory.getAllocatedQuantity());
				if (inventoryQty != null && inventoryQty.length > 2) {
					inventory.setInventoryQuantity(inventoryQty[0]);
					inventory.setAllocatedQuantity(inventoryQty[1]);
					inventory.setReferenceField4(inventoryQty[2]);
				}

				InventoryV2 inventoryV2 = new InventoryV2();
				BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
				inventoryV2.setUpdatedOn(new Date());
				try {
					inventoryV2 = inventoryV2Repository.save(inventoryV2);
					log.info("-----Inventory2 updated-------: " + inventoryV2);
				} catch (Exception e1) {
					log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e1.toString());
					e1.printStackTrace();
					
					// Inventory Error Handling
					InventoryTrans newInventoryTrans = new InventoryTrans();
					BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
					newInventoryTrans.setReRun(0L);	
					InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
					log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
				}

				// --------------------------------------------------------------------------------------------------------------

				if (inventory.getReferenceField4() == 0) {
					// Setting up statusId = 0
					try {
						// Check whether Inventory has record or not for that storageBin
						Double inventoryByStBin = inventoryService.getInventoryByStorageBinV3(companyCodeId, plantId,
								languageId, warehouseId, inventory.getStorageBin());
						if (inventoryByStBin == null) {
							// Setting up statusId = 0
							updateStorageBinEmptyStatus(companyCodeId, plantId, languageId, warehouseId,
									inventory.getStorageBin(), loginUserID);
						}
					} catch (Exception e) {
						log.error("updateStorageBin Error :" + e.toString());
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				log.error("Inventory Update :" + e.toString());
				e.printStackTrace();
			}
		}
	}
    
//    /**
//     *
//     */
//	@Scheduled(fixedDelayString = "PT1M", initialDelayString = "PT2M")
//	private void updateErroredOutInventory () {
//		List<InventoryTrans> inventoryTransList = inventoryTransRepository.findByReRun(0L);
//		inventoryTransList.stream().forEach( it -> {
//			log.info("----updateErroredOutInventory-->: " + it);
//			inventoryV2Repository.updateInventory(it.getWarehouseId(),
//					it.getPackBarcodes(), it.getItemCode(),
//					it.getStorageBin(), it.getInventoryQuantity(), it.getAllocatedQuantity());
//			it.setReRun(1L);
//			inventoryTransRepository.save(it);
//			log.info("----updateInventoryTrans-is-done-->: " + it);
//		});
//	}

    /**
     * PickerDenial
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param allocatedQty
     * @param loginUserID
     */
    @Transactional
    private void updateInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                   String itemCode, String manufacturerName, String barcodeId, String storageBin,
                                   Double allocatedQty, String loginUserID) {
        InventoryV2 inventory = inventoryService.getInventoryForPickUpLineV3(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, storageBin);
        log.info("inventory record queried: " + inventory);
        if (inventory != null) {
            try {
                double[] inventoryQty = calculateInventoryUnAllocate(allocatedQty, inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                if (inventoryQty != null && inventoryQty.length > 2) {
                    inventory.setInventoryQuantity(inventoryQty[0]);
                    inventory.setAllocatedQuantity(inventoryQty[1]);
                    inventory.setReferenceField4(inventoryQty[2]);
                }

                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                inventoryV2.setUpdatedOn(new Date());
                inventoryV2.setUpdatedBy(loginUserID);
               
                try {
                	 inventoryV2 = inventoryV2Repository.save(inventoryV2);
                     log.info("-----PickerDenialInventory2 updated-------: " + inventoryV2);
				} catch (Exception e1) {
					log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e1.toString());
					e1.printStackTrace();
					
					// Inventory Error Handling
					InventoryTrans newInventoryTrans = new InventoryTrans();
					BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
					newInventoryTrans.setReRun(0L);	
					InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
					log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
				}
            } catch (Exception e) {
                log.error("Inventory Update :" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param pickCnfQty
     * @param loginUserID
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    private void updateInventoryPickedBarcodeV3(String companyCodeId, String plantId, String languageId, String warehouseId,
            String itemCode, String manufacturerName, String barcodeId, String storageBin, Double pickCnfQty, String loginUserID) {
		InventoryV2 inventory = inventoryService.getInventoryForPickUpLineV3(companyCodeId, plantId, languageId,
				warehouseId, itemCode, manufacturerName, barcodeId, storageBin);
        log.info("inventory record queried: " + inventory);
        if (inventory != null) {
            try {
				double[] inventoryQty = calculateInventory(pickCnfQty, inventory.getInventoryQuantity(),
						inventory.getAllocatedQuantity());
                if (inventoryQty != null && inventoryQty.length > 2) {
                    inventory.setInventoryQuantity(inventoryQty[0]);
                    inventory.setAllocatedQuantity(inventoryQty[1]);
                    inventory.setReferenceField4(inventoryQty[2]);
                }

                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                inventoryV2.setUpdatedOn(new Date());
				try {
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----PickedBarcodeInventory2 updated-------: " + inventoryV2);
				} catch (Exception e1) {
					log.error("--ERROR--schedulePostPickupLineProcessV2 ----level1--inventory--error----> :" + e1.toString());
					e1.printStackTrace();

					// Inventory Error Handling
					InventoryTrans newInventoryTrans = new InventoryTrans();
					BeanUtils.copyProperties(inventory, newInventoryTrans, CommonUtils.getNullPropertyNames(inventory));
					newInventoryTrans.setReRun(0L);	
					InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
					log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
				}

				// --------------------------------------------------------------------------------------------------------------

                if (inventory.getReferenceField4() == 0) {
                    // Setting up statusId = 0
                    try {
                        // Check whether Inventory has record or not for that storageBin
						Double inventoryByStBin = inventoryService.getInventoryByStorageBinV3(companyCodeId, plantId,
								languageId, warehouseId, inventory.getStorageBin());
                        if (inventoryByStBin == null) {
                            // Setting up statusId = 0
							updateStorageBinEmptyStatus(companyCodeId, plantId, languageId, warehouseId,
									inventory.getStorageBin(), loginUserID);
                        }
                    } catch (Exception e) {
                        log.error("updateStorageBin Error :" + e.toString());
                        e.printStackTrace();
                    }
                }

                if(inventory.getBinClassId() == 3L) {
                    log.info("In DeliveryConfirmation ------------------------> PutAwayLine Creation Process BinClassId is 3 ------------> BarCodeId is ---> " + barcodeId);
                    putAwayHeaderService.updatePutAwayHeaderV3(companyCodeId, plantId, languageId, warehouseId,
                            itemCode,barcodeId);
                }
            } catch (Exception e) {
                log.error("Inventory Update :" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param pickCnfQty
     * @param loginUserID
     */
    @Transactional
    private void updateRollBackInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                           String itemCode, String manufacturerName, String barcodeId, String storageBin,
                                           Double pickCnfQty, String loginUserID) {
        InventoryV2 inventory = inventoryService.getOutboundReversalInventoryV3(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, storageBin);
        log.info("rollback - inventory record queried: " + inventory);
        if (inventory != null) {
            try {
                double[] inventoryQty = rollbackInventory(pickCnfQty, inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                if (inventoryQty != null && inventoryQty.length > 2) {
                    inventory.setInventoryQuantity(inventoryQty[0]);
                    inventory.setAllocatedQuantity(inventoryQty[1]);
                    inventory.setReferenceField4(inventoryQty[2]);
                }

                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                inventoryV2.setUpdatedOn(new Date());
                inventoryV2.setUpdatedBy(loginUserID);
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----Inventory2 updated-------: " + inventoryV2);

            } catch (Exception e) {
                log.error("Inventory Update :" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param barcodeId
     * @param storageBin
     * @param pickCnfQty
     * @param loginUserID
     */
    @Transactional
    private void updateRollBackDenialInventoryV3(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                 String itemCode, String manufacturerName, String barcodeId, String storageBin,
                                                 Double pickCnfQty, String loginUserID) {
        InventoryV2 inventory = inventoryService.getOutboundReversalInventoryV3(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, barcodeId, storageBin);
        log.info("rollback - inventory record queried: " + inventory);
        if (inventory != null) {
            try {
                if(inventory.getReferenceField4() == 0) {
                double[] inventoryQty = rollbackInventoryV3 (pickCnfQty, inventory.getInventoryQuantity(), inventory.getAllocatedQuantity());
                if (inventoryQty != null && inventoryQty.length > 2) {
                    inventory.setInventoryQuantity(inventoryQty[0]);
                    inventory.setAllocatedQuantity(inventoryQty[1]);
                    inventory.setReferenceField4(inventoryQty[2]);
            }

                InventoryV2 inventoryV2 = new InventoryV2();
                BeanUtils.copyProperties(inventory, inventoryV2, CommonUtils.getNullPropertyNames(inventory));
                inventoryV2.setUpdatedOn(new Date());
                inventoryV2.setUpdatedBy(loginUserID);
                inventoryV2 = inventoryV2Repository.save(inventoryV2);
                log.info("-----Inventory2 updated-------: " + inventoryV2);
                }
        } catch (Exception e) {
                log.error("Inventory Update :" + e.toString());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @throws Exception
     */
    public void rollbackInventory (String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber) {
        List<PickupHeaderV2> pickupHeaders = pickupHeaderService.getPickupHeadersV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
        log.info("Pickupheader for RollBack: " + pickupHeaders.size());
        statusDescription = getStatusDescription(48L, languageId);
        if(pickupHeaders != null && !pickupHeaders.isEmpty()) {
            for (PickupHeaderV2 pickupHeader : pickupHeaders) {
                PickupLineV2 pickupLine = pickupLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndItemCodeAndBarcodeIdAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, pickupHeader.getItemCode(), pickupHeader.getBarcodeId(), 0L);
                log.info("PickupLine : " + pickupLine);
                if(pickupLine != null) {
//                    if(pickupLine.getStatusId() == 59L) {
                        //Picker Denial
//                        updateRollBackInventoryV3(companyCodeId, plantId, languageId, warehouseId,
//                                pickupLine.getItemCode(), pickupLine.getManufacturerName(), pickupLine.getBarcodeId(),
//                                pickupLine.getPickedStorageBin(), pickupLine.getPickConfirmQty(), pickupLine.getPickupCreatedBy());
//                    } else {
                        updateRollBackDenialInventoryV3(companyCodeId, plantId, languageId, warehouseId,
                                pickupLine.getItemCode(), pickupLine.getManufacturerName(), pickupLine.getBarcodeId(),
                                pickupLine.getPickedStorageBin(), pickupHeader.getPickToQty(), pickupLine.getPickupCreatedBy());
//                    }
                }
                pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        pickupHeader.getPickupNumber(), pickupHeader.getBarcodeId(), 48L, statusDescription);
            }
        }
    }



    // PickupLine Creation Process in Delivery Confirmation
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Retryable(value = {SQLException.class, SQLServerException.class, CannotAcquireLockException.class, LockAcquisitionException.class, UnexpectedRollbackException.class}, maxAttempts = 2, backoff = @Backoff(delay = 2000))
    public void createPickupLineInDeliveryConfirmation(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber,
                                      String preOutboundNo, PickupHeaderV2 pickupHeader, Double pickedQty, String loginUserID,
                                      String idMasterAuthToken) throws Exception {
        try {
            log.info("1.V3 create pickupLine {}|{}", pickupHeader, loginUserID);
            Long STATUS_ID = 0L;
            // Create PickUpLine
            PickupLineV2 createdPickupLine = null;
            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(pickupHeader, dbPickupLine, CommonUtils.getNullPropertyNames(pickupHeader));
            dbPickupLine.setCompanyCodeId(companyCodeId);
            dbPickupLine.setLanguageId(languageId);
            dbPickupLine.setPlantId(plantId);
            dbPickupLine.setWarehouseId(warehouseId);

            if (pickedQty > 0) {
                STATUS_ID = 59L;
            } else {
                STATUS_ID = 51L;
            }

            log.info("newPickupLine STATUS: " + STATUS_ID);
            dbPickupLine.setStatusId(STATUS_ID);
            statusDescription = getStatusDescription(STATUS_ID, languageId);
            dbPickupLine.setStatusDescription(statusDescription);

            OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(
                    companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                    pickupHeader.getLineNumber(), pickupHeader.getItemCode(), pickupHeader.getBarcodeId());
            log.info("OrderManagementLine: " + dbOrderManagementLine);
            if (dbOrderManagementLine == null) {
                dbOrderManagementLine = orderManagementLineService.getOrderManagementLineV3(companyCodeId, plantId,
                        languageId, warehouseId, refDocNumber, pickupHeader.getLineNumber(),
                        pickupHeader.getItemCode());
                log.info("OrderManagementLine: " + dbOrderManagementLine);
            }

            if (dbOrderManagementLine != null) {
                dbPickupLine.setManufacturerCode(dbOrderManagementLine.getManufacturerCode());
                dbPickupLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                dbPickupLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                dbPickupLine.setDescription(dbOrderManagementLine.getDescription());
                dbPickupLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                dbPickupLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                dbPickupLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                dbPickupLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                dbPickupLine.setLevelId(dbOrderManagementLine.getLevelId());
                dbPickupLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());

                // V3
                dbPickupLine.setMaterialNo(dbOrderManagementLine.getMaterialNo());
                dbPickupLine.setPriceSegment(dbOrderManagementLine.getPriceSegment());
                dbPickupLine.setArticleNo(dbOrderManagementLine.getArticleNo());
                dbPickupLine.setGender(dbOrderManagementLine.getGender());
                dbPickupLine.setColor(dbOrderManagementLine.getColor());
                dbPickupLine.setSize(dbOrderManagementLine.getSize());
                dbPickupLine.setNoPairs(dbOrderManagementLine.getNoPairs());
            }

            dbPickupLine.setPickedStorageBin(pickupHeader.getProposedStorageBin());
            dbPickupLine.setPickedPackCode(pickupHeader.getProposedPackBarCode());
            dbPickupLine.setPickConfirmQty(pickedQty);
            dbPickupLine.setAllocatedQty(pickupHeader.getPickToQty());
            dbPickupLine.setPickupCreatedOn(pickupHeader.getPickupCreatedOn());

            if (pickupHeader.getPickupCreatedBy() != null) {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickupCreatedBy());
            } else {
                dbPickupLine.setPickupCreatedBy(pickupHeader.getPickUpdatedBy());
            }

            Double VAR_QTY = getQuantity(dbPickupLine.getAllocatedQty())
                    - getQuantity(dbPickupLine.getPickConfirmQty());
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            dbPickupLine.setActualHeNo(PICK_HE_NO);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            // Checking for Duplicates
            List<PickupLineV2> existingPickupLine = pickupLineV2Repository
                    .findByLanguageIdAndCompanyCodeIdAndPlantIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndPickupNumberAndItemCodeAndPickedStorageBinAndPickedPackCodeAndBarcodeIdAndDeletionIndicator(
                            languageId, companyCodeId, plantId, warehouseId, preOutboundNo, refDocNumber,
                            dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(), dbPickupLine.getPickupNumber(),
                            dbPickupLine.getItemCode(), dbPickupLine.getPickedStorageBin(),
                            dbPickupLine.getPickedPackCode(), dbPickupLine.getBarcodeId(), 0L);
            log.info("existingPickupLine : " + existingPickupLine);

            if (existingPickupLine == null || existingPickupLine.isEmpty()) {
                log.info("Inputs for LeadTime : companyCode ---> " + dbPickupLine.getCompanyCodeId() + ", plantId ---> " + dbPickupLine.getPlantId() +
                        ", languageId ---> " + dbPickupLine.getLanguageId() + ", warehouse ---> " + dbPickupLine.getWarehouseId() +
                        ", pickupNumber ---> " + dbPickupLine.getPickupNumber() + ", barcodeId ---> " + dbPickupLine.getBarcodeId() + ", lDate ---> " + new Date());
                String leadTime = pickupLineV2Repository.getleadtime(dbPickupLine.getCompanyCodeId(),
                        dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId(),
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), new Date());
                dbPickupLine.setReferenceField1(leadTime);
                log.info("LeadTime: " + leadTime);

                createdPickupLine = pickupLineV2Repository.save(dbPickupLine);
                log.info("dbPickupLine created: " + createdPickupLine);
            } else {
                throw new BadRequestException(
                        "PickupLine Record is getting duplicated. Given data already exists in the Database. : "
                                + existingPickupLine);
            }

            /*---------------------------------------------Inventory Updates-------------------------------------------*/
            // Updating respective tables
            if (createdPickupLine != null) {

                statusDescription = getStatusDescription(59L, languageId);
                pickupHeaderService.updatePickupHeaderV3(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        dbPickupLine.getPickupNumber(), dbPickupLine.getBarcodeId(), 59L, statusDescription);
                log.info("PickUpHeader Updated");

                if (STATUS_ID == 59) {
                    updateInventoryV3(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(),
                            dbPickupLine.getManufacturerName(), dbPickupLine.getBarcodeId(),
                            dbPickupLine.getPickedStorageBin(), dbPickupLine.getAllocatedQty(),
                            dbPickupLine.getPickConfirmQty(), loginUserID);
                }
                // CreateQualityHeader
//                qualityHeaderService.createQualityHeaderV3(companyCodeId, plantId, languageId, warehouseId,
//                        dbPickupLine, pickupHeader, idMasterAuthToken, loginUserID);

                Double deliveryQty = pickupLineV2Repository.getPickupLineCountV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber,
                        preOutboundNo, dbPickupLine.getLineNumber(), dbPickupLine.getItemCode());

                log.info("Sum Of PickConfirmQty Group By RefDocNo From PickupLine  ----------> Qty is -->  {} ", deliveryQty);
                outboundLineV2Repository.updateOutboundLineForDeliveryConfirm(companyCodeId, plantId, languageId, warehouseId,
                        preOutboundNo, refDocNumber, dbPickupLine.getPartnerCode(), dbPickupLine.getLineNumber(),
                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
                        deliveryQty, STATUS_ID, statusDescription, loginUserID, new Date());

                Long STATUS_ID_59 = 59L;
                statusDescription = getStatusDescription(STATUS_ID_59, languageId);
                outboundLineV2Repository.deliveryConfirmationProc(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, loginUserID, new Date());

            }
        } catch (Exception e) {
            log.error("1.Exception while pickupLine create");
            throw e;
        }
    }
}