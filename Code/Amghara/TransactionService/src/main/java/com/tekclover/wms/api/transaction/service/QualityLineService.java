package com.tekclover.wms.api.transaction.service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.auth.AuthToken;
import com.tekclover.wms.api.transaction.model.dto.IImbasicData1;
import com.tekclover.wms.api.transaction.model.dto.StatusId;
import com.tekclover.wms.api.transaction.model.dto.StorageBin;
import com.tekclover.wms.api.transaction.model.dto.Warehouse;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.AddInventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.Inventory;
import com.tekclover.wms.api.transaction.model.inbound.inventory.InventoryMovement;
import com.tekclover.wms.api.transaction.model.inbound.inventory.UpdateInventory;
import com.tekclover.wms.api.transaction.model.outbound.OutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLine;
import com.tekclover.wms.api.transaction.model.outbound.OutboundLineInterim;
import com.tekclover.wms.api.transaction.model.outbound.UpdateOutboundHeader;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.AddQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.QualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.SearchQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityHeader;
import com.tekclover.wms.api.transaction.model.outbound.quality.UpdateQualityLine;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.AddQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.quality.v2.SearchQualityLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundLineV2;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.InventoryMovementRepository;
import com.tekclover.wms.api.transaction.repository.InventoryRepository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.repository.OrderManagementLineV2Repository;
import com.tekclover.wms.api.transaction.repository.OutboundLineInterimRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupLineV2Repository;
import com.tekclover.wms.api.transaction.repository.QualityHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.QualityLineRepository;
import com.tekclover.wms.api.transaction.repository.QualityLineV2Repository;
import com.tekclover.wms.api.transaction.repository.StagingLineV2Repository;
import com.tekclover.wms.api.transaction.repository.specification.QualityLineSpecification;
import com.tekclover.wms.api.transaction.repository.specification.QualityLineV2Specification;
import com.tekclover.wms.api.transaction.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QualityLineService extends BaseService {
    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private QualityHeaderV2Repository qualityHeaderV2Repository;
    @Autowired
    private PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    private QualityLineRepository qualityLineRepository;

    @Autowired
    private OutboundLineRepository outboundLineRepository;

    @Autowired
    private QualityHeaderService qualityHeaderService;

    @Autowired
    private OutboundHeaderService outboundHeaderService;

    @Autowired
    private OutboundLineService outboundLineService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private MastersService mastersService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private InventoryMovementService inventoryMovementService;

    @Autowired
    private ImBasicData1Repository imbasicdata1Repository;

    @Autowired
    private OutboundLineInterimRepository outboundLineInterimRepository;

    //------------------------------------------------------------------------------------------------------
    @Autowired
    private OrderManagementLineService orderManagementLineService;
    @Autowired
    private OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    private OutboundLineV2Repository outboundLineV2Repository;
    @Autowired
    private InventoryV2Repository inventoryV2Repository;
    @Autowired
    private PickupHeaderService pickupHeaderService;
    @Autowired
    private QualityLineV2Repository qualityLineV2Repository;
    @Autowired
    private StagingLineV2Repository stagingLineV2Repository;
    String statusDescription = null;
    //------------------------------------------------------------------------------------------------------

    /**
     * getQualityLines
     *
     * @return
     */
    public List<QualityLine> getQualityLines() {
        List<QualityLine> qualityLineList = qualityLineRepository.findAll();
        qualityLineList = qualityLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return qualityLineList;
    }

    /**
     * getQualityLine
     *
     * @return
     */
    public QualityLine getQualityLine(String partnerCode) {
        QualityLine qualityLine = qualityLineRepository.findByPartnerCode(partnerCode).orElse(null);
        if (qualityLine != null && qualityLine.getDeletionIndicator() == 0) {
            return qualityLine;
        } else {
            throw new BadRequestException("The given QualityLine ID : " + partnerCode + " doesn't exist.");
        }
    }

    /**
     * // Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public QualityLine getQualityLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode,
                                      Long lineNumber, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        throw new BadRequestException("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
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
    public QualityLine getQualityLineValidated(String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
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
    public List<QualityLine> getQualityLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                       String partnerCode, Long lineNumber, String itemCode) {
        List<QualityLine> qualityLine = qualityLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
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
    public QualityLine getQualityLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                + ",itemCode:" + itemCode + " doesn't exist.");
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
    public List<QualityLine> getQualityLineForUpdateForDeliverConformation(String warehouseId, String preOutboundNo,
                                                                           String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<QualityLine> qualityLine = qualityLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                + ",itemCode:" + itemCode + " doesn't exist.");
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
    public List<QualityLine> getQualityLine(String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
        List<QualityLine> qualityLine = qualityLineRepository
                .findAllByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLine getQualityLineForUpdate(String warehouseId, String preOutboundNo, String refDocNumber,
                                                String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
                        itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        throw new BadRequestException(
                "The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                        + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                        + ",qualityInspectionNo:" + qualityInspectionNo + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLine findDuplicateRecord(String warehouseId, String preOutboundNo, String refDocNumber,
                                            String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
                        itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLine findQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                        String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode) {
        QualityLine qualityLine = qualityLineRepository
                .findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
                        warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
                        itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param searchQualityLine
     * @return
     * @throws ParseException
     */
    public List<QualityLine> findQualityLine(SearchQualityLine searchQualityLine) throws ParseException {
        QualityLineSpecification spec = new QualityLineSpecification(searchQualityLine);
        List<QualityLine> results = qualityLineRepository.findAll(spec);
        return results;
    }

    /**
     * @param qualityLineList
     * @return
     */
    public static List<AddQualityLine> getDuplicates(List<AddQualityLine> qualityLineList) {
        return getDuplicatesMap(qualityLineList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static List<AddQualityLineV2> getDuplicatesV2(List<AddQualityLineV2> qualityLineList) {
        return getDuplicatesMapV2(qualityLineList).values().stream()
                .filter(duplicates -> duplicates.size() > 1)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * @param addQualityLineList
     * @return
     */
    private static Map<String, List<AddQualityLine>> getDuplicatesMap(List<AddQualityLine> addQualityLineList) {
        return addQualityLineList.stream().collect(Collectors.groupingBy(AddQualityLine::uniqueAttributes));
    }

    private static Map<String, List<AddQualityLineV2>> getDuplicatesMapV2(List<AddQualityLineV2> addQualityLineList) {
        return addQualityLineList.stream().collect(Collectors.groupingBy(AddQualityLineV2::uniqueAttributes));
    }

    /**
     * @param newQualityLines
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<QualityLine> createQualityLine(List<AddQualityLine> newQualityLines, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        try {
            log.info("-------createQualityLine--------called-------> " + new Date());

            List<AddQualityLine> dupQualityLines = getDuplicates(newQualityLines);
            log.info("-------dupQualityLines--------> " + dupQualityLines);
            if (dupQualityLines != null && !dupQualityLines.isEmpty()) {
                newQualityLines.removeAll(dupQualityLines);
                newQualityLines.add(dupQualityLines.get(0));
                log.info("-------newQualityLines---removed-dupQualityLines-----> " + newQualityLines);
            }

            /*
             * The below flag helps to avoid duplicate request and updating of outboundline
             * table
             */
            List<QualityLine> createdQualityLineList = new ArrayList<>();
            for (AddQualityLine newQualityLine : newQualityLines) {
                log.info("Input from UI:  " + newQualityLine);

                QualityLine dbQualityLine = new QualityLine();
                BeanUtils.copyProperties(newQualityLine, dbQualityLine,
                        CommonUtils.getNullPropertyNames(newQualityLine));

                // STATUS_ID - HardCoded Value "55"
                dbQualityLine.setStatusId(55L);
                dbQualityLine.setDeletionIndicator(0L);
                dbQualityLine.setQualityCreatedBy(loginUserID);
                dbQualityLine.setQualityUpdatedBy(loginUserID);
                dbQualityLine.setQualityCreatedOn(new Date());
                dbQualityLine.setQualityUpdatedOn(new Date());

                /*
                 * String warehouseId, String preOutboundNo, String refDocNumber, String
                 * partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode
                 */
                QualityLine existingQualityLine = findDuplicateRecord(newQualityLine.getWarehouseId(),
                        newQualityLine.getPreOutboundNo(), newQualityLine.getRefDocNumber(),
                        newQualityLine.getPartnerCode(), newQualityLine.getLineNumber(),
                        newQualityLine.getQualityInspectionNo(), newQualityLine.getItemCode());
                log.info("existingQualityLine record status : " + existingQualityLine);

                /*
                 * Checking whether the record already exists (created) or not. If it is not
                 * created then only the rest of the logic has been carry forward
                 */
                if (existingQualityLine == null) {
                    QualityLine createdQualityLine = qualityLineRepository.save(dbQualityLine);
                    log.info("createdQualityLine: " + createdQualityLine);

                    // createOutboundLineInterim
                    createOutboundLineInterim(createdQualityLine);
                    createdQualityLineList.add(createdQualityLine);
                }
            }

            /*
             * Based on created QualityLine List, updating respective tables
             */
            AuthToken authTokenForIDService = authTokenService.getIDMasterServiceAuthToken();
            for (QualityLine dbQualityLine : createdQualityLineList) {
                /*-----------------STATUS updates in QualityHeader-----------------------*/
                try {
                    UpdateQualityHeader updateQualityHeader = new UpdateQualityHeader();
                    updateQualityHeader.setStatusId(55L);
                    StatusId idStatus = idmasterService.getStatus(55L, dbQualityLine.getWarehouseId(), authTokenForIDService.getAccess_token());
                    updateQualityHeader.setReferenceField10(idStatus.getStatus());
                    QualityHeader qualityHeader = qualityHeaderService.updateQualityHeader(
                            dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                            dbQualityLine.getRefDocNumber(), dbQualityLine.getQualityInspectionNo(),
                            dbQualityLine.getActualHeNo(), loginUserID, updateQualityHeader);
                    log.info("qualityHeader updated : " + qualityHeader);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.info("qualityHeader updated Error : " + e1.toString());
                }

                /*-------------------OUTBOUNDLINE------Update---------------------------*/
                /*
                 * Pass WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE /OB_LINE_NO/_ITM_CODE values in
                 * QUALITYILINE table and fetch QC_QTY values and pass the same values in
                 * OUTBOUNDLINE table and update DLV_QTY
                 *
                 * Pass Unique keys in OUTBOUNDLINE table and update STATUS_ID as "57"
                 */
                Long NUM_RAN_CODE = 12L;
                String DLV_ORD_NO = getNextRangeNumber(NUM_RAN_CODE, dbQualityLine.getWarehouseId());

                updateOutboundLine(dbQualityLine, DLV_ORD_NO);
                try {
                    /*-------------------OUTBOUNDHEADER------Update---------------------------*/
                    boolean isStatus57 = false;
                    List<OutboundLine> outboundLines = outboundLineService.getOutboundLine(
                            dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                            dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode());
//					log.info("outboundLine re-queried-----> : " + outboundLines);

                    outboundLines = outboundLines.stream().filter(o -> o.getStatusId() == 57L)
                            .collect(Collectors.toList());
                    if (outboundLines != null) {
                        isStatus57 = true;
                    }

                    UpdateOutboundHeader updateOutboundHeader = new UpdateOutboundHeader();
                    updateOutboundHeader.setDeliveryOrderNo(DLV_ORD_NO);
                    if (isStatus57) { // If Status if 57 then update OutboundHeader with Status 57.
                        updateOutboundHeader.setStatusId(57L);
                    }

                    OutboundHeader outboundHeader = outboundHeaderService.updateOutboundHeader(
                            dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                            dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), updateOutboundHeader,
                            loginUserID);
                    log.info("outboundHeader updated : " + outboundHeader);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.info("outboundHeader updated error: " + e1.toString());
                }

                /*-----------------Inventory Updates--------------------------------------*/
                // Pass WH_ID/ITM_CODE/ST_BIN/PACK_BARCODE in INVENTORY table
                AuthToken authTokenForMastersService = authTokenService.getMastersServiceAuthToken();
                Long BIN_CLASS_ID = 4L;
                StorageBin storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
                        authTokenForMastersService.getAccess_token());
                Warehouse warehouse = getWarehouse(dbQualityLine.getWarehouseId());
                Inventory inventory = null;
                try {
                    inventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
                            dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
                            storageBin.getStorageBin());
                    log.info("inventory---BIN_CLASS_ID-4----> : " + inventory);

                    if (inventory != null) {
                        Double INV_QTY = inventory.getInventoryQuantity() - dbQualityLine.getQualityQty();
                        log.info("Calculated inventory INV_QTY: " + INV_QTY);
                        inventory.setInventoryQuantity(INV_QTY);

                        // INV_QTY > 0 then, update Inventory Table
                        inventory = inventoryRepository.save(inventory);
                        log.info("inventory updated : " + inventory);

                        if (INV_QTY == 0) {
                            log.info("inventory INV_QTY: " + INV_QTY);
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                /*-------------------Inserting record in InventoryMovement-------------------------------------*/
                Long subMvtTypeId = 2L;
                String movementDocumentNo = dbQualityLine.getQualityInspectionNo();
                String stBin = storageBin.getStorageBin();
                String movementQtyValue = "N";
                InventoryMovement inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId,
                        movementDocumentNo, stBin, movementQtyValue, loginUserID);
                log.info("InventoryMovement created : " + inventoryMovement);

                /*--------------------------------------------------------------------------*/
                // 2.Insert a new record in INVENTORY table as below
                // Fetch from QUALITYLINE table and insert WH_ID/ITM_CODE/ST_BIN= (ST_BIN value
                // of BIN_CLASS_ID=5
                // from STORAGEBIN table)/PACK_BARCODE/INV_QTY = QC_QTY - INVENTORY UPDATE 2
                BIN_CLASS_ID = 5L;
                storageBin = mastersService.getStorageBin(dbQualityLine.getWarehouseId(), BIN_CLASS_ID,
                        authTokenForMastersService.getAccess_token());
                warehouse = getWarehouse(dbQualityLine.getWarehouseId());

                /*
                 * Checking Inventory table before creating new record inventory
                 */
                // Pass WH_ID/ITM_CODE/ST_BIN = (ST_BIN value of BIN_CLASS_ID=5 /PACK_BARCODE
                Inventory existingInventory = inventoryService.getInventory(dbQualityLine.getWarehouseId(),
                        dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(), storageBin.getStorageBin());
                log.info("existingInventory : " + existingInventory);
                if (existingInventory != null) {
                    Double INV_QTY = existingInventory.getInventoryQuantity() + dbQualityLine.getQualityQty();
                    UpdateInventory updateInventory = new UpdateInventory();
                    updateInventory.setInventoryQuantity(INV_QTY);
                    try {
                        Inventory updatedInventory = inventoryService.updateInventory(dbQualityLine.getWarehouseId(),
                                dbQualityLine.getPickPackBarCode(), dbQualityLine.getItemCode(),
                                storageBin.getStorageBin(), 1L, 1L, updateInventory, loginUserID);
                        log.info("updatedInventory----------> : " + updatedInventory);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("updatedInventory error----------> : " + e.toString());
                    }
                } else {
                    log.info("AddInventory========>");
                    AddInventory newInventory = new AddInventory();
                    newInventory.setLanguageId(warehouse.getLanguageId());
                    newInventory.setCompanyCodeId(warehouse.getCompanyCode());
                    newInventory.setPlantId(warehouse.getPlantId());
                    newInventory.setStockTypeId(inventory.getStockTypeId());
                    newInventory.setBinClassId(BIN_CLASS_ID);
                    newInventory.setWarehouseId(dbQualityLine.getWarehouseId());
                    newInventory.setPackBarcodes(dbQualityLine.getPickPackBarCode());
                    newInventory.setItemCode(dbQualityLine.getItemCode());
                    newInventory.setStorageBin(storageBin.getStorageBin());
                    newInventory.setInventoryQuantity(dbQualityLine.getQualityQty());
                    newInventory.setSpecialStockIndicatorId(1L);

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
                }

                /*-----------------------InventoryMovement----------------------------------*/
                // Inserting record in InventoryMovement
                subMvtTypeId = 2L;
                movementDocumentNo = DLV_ORD_NO;
                stBin = storageBin.getStorageBin();
                movementQtyValue = "P";
                inventoryMovement = createInventoryMovement(dbQualityLine, subMvtTypeId, movementDocumentNo, stBin,
                        movementQtyValue, loginUserID);
                log.info("InventoryMovement created for update2: " + inventoryMovement);
            }
            return createdQualityLineList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param dbQualityLine
     */
    private void createOutboundLineInterim(QualityLine dbQualityLine) {
//		OutboundLine dbOutboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
//				dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
//				dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//				dbQualityLine.getItemCode());
//		log.info("##############dbOutboundLine QUERIED ----------->: " + dbOutboundLine);

        OutboundLineInterim outboundLineInterim = new OutboundLineInterim();
        BeanUtils.copyProperties(dbQualityLine, outboundLineInterim, CommonUtils.getNullPropertyNames(dbQualityLine));
        outboundLineInterim.setDeletionIndicator(0L);
        outboundLineInterim.setDeliveryQty(dbQualityLine.getQualityQty());
        outboundLineInterim.setCreatedBy(dbQualityLine.getQualityCreatedBy());
        outboundLineInterim.setCreatedOn(new Date());

        OutboundLineInterim createdOutboundLine = outboundLineInterimRepository.saveAndFlush(outboundLineInterim);
        log.info("outboundLineInterim created ----------->: " + createdOutboundLine);
    }

    /**
     *
     * @param dbQualityLine
     * @param DLV_ORD_NO
     */
//	@Transactional(isolation = Isolation.READ_COMMITTED)
//	private void updateOutboundLine (QualityLine dbQualityLine, String DLV_ORD_NO) {
//		try {
//			//---------------Update-Lock-Applied---------------------------------------------------------
//			OutboundLine outboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
//					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
//					dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//					dbQualityLine.getItemCode());
//			log.info("DB outboundLine : " + outboundLine);
//			if (outboundLine != null) {
//				Double exisitingDelQty = 0D;
//				if (outboundLine.getDeliveryQty() != null) {
//					exisitingDelQty = outboundLine.getDeliveryQty();
//				} else {
//					exisitingDelQty = 0D;
//				}
//				exisitingDelQty = exisitingDelQty + dbQualityLine.getQualityQty();
//				log.info("DB after outboundLine existingDelQty : " + exisitingDelQty);
//				outboundLineRepository.updateOutboundLine(dbQualityLine.getWarehouseId(),
//						dbQualityLine.getRefDocNumber(), dbQualityLine.getPreOutboundNo(),
//						dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//						dbQualityLine.getItemCode(), DLV_ORD_NO, 57L, exisitingDelQty);
//				log.info("outboundLine updated.");
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			log.info("outboundLine updated error: " + e1.toString());
//		}
//	}

    /**
     * @param dbQualityLine
     * @param DLV_ORD_NO
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000, multiplier = 2))
    private void updateOutboundLine(QualityLine dbQualityLine, String DLV_ORD_NO) {
        try {
            Double deliveryQty = outboundLineInterimRepository.getSumOfDeliveryLine(dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                    dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
                    dbQualityLine.getItemCode());
            log.info("=======updateOutboundLine==========>: " + deliveryQty);

            // Get Existing Record
            OutboundLine existingOutboundLine = outboundLineService.getOutboundLine(dbQualityLine.getWarehouseId(),
                    dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
                    dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
                    dbQualityLine.getItemCode());

            // Insert
            OutboundLine outboundLine = new OutboundLine();
            BeanUtils.copyProperties(existingOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(existingOutboundLine));
            outboundLine.setDeliveryQty(deliveryQty);
            outboundLine.setDeliveryOrderNo(DLV_ORD_NO);
            outboundLine.setStatusId(57L);
            outboundLine.setDeletionIndicator(0L);

            // Delete
            outboundLineRepository.delete(existingOutboundLine);
//			outboundLineRepository.deleteOutboundLineMain(dbQualityLine.getWarehouseId(),
//					dbQualityLine.getPreOutboundNo(), dbQualityLine.getRefDocNumber(),
//					dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
//					dbQualityLine.getItemCode());

            OutboundLine createdOutboundLineNewly = outboundLineRepository.save(outboundLine);
            log.info("createdOutboundLineNewly created ----------->: " + createdOutboundLineNewly);
        } catch (Exception e1) {
            e1.printStackTrace();
            log.info("outboundLine updated error: " + e1.toString());
        }
    }

    /**
     * @param dbQualityLine
     * @param subMvtTypeId
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovement(QualityLine dbQualityLine, Long subMvtTypeId,
                                                      String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        AddInventoryMovement inventoryMovement = new AddInventoryMovement();
        BeanUtils.copyProperties(dbQualityLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbQualityLine));

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

        // PACK_BAR_CODE
        inventoryMovement.setPackBarcodes(dbQualityLine.getPickPackBarCode());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbQualityLine.getPickConfirmQty());

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbQualityLine.getQualityConfirmUom());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(dbQualityLine.getQualityConfirmedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbQualityLine.getQualityCreatedOn());

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
     * @param updateQualityLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<QualityLine> updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                               UpdateQualityLine updateQualityLine) throws IllegalAccessException, InvocationTargetException {
        List<QualityLine> dbQualityLine = getQualityLineForUpdateForDeliverConformation(warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.forEach(data -> {
                BeanUtils.copyProperties(updateQualityLine, data, CommonUtils.getNullPropertyNames(updateQualityLine));
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
            });
            return qualityLineRepository.saveAll(dbQualityLine);
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @param loginUserID
     * @param updateQualityLine
     * @return
     */
    public QualityLine updateQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                         String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID,
                                         @Valid UpdateQualityLine updateQualityLine) {
        QualityLine dbQualityLine = getQualityLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode);
        if (dbQualityLine != null) {
            BeanUtils.copyProperties(updateQualityLine, dbQualityLine,
                    CommonUtils.getNullPropertyNames(updateQualityLine));
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineRepository.save(dbQualityLine);
        }
        return null;
    }

    /**
     * deleteQualityLine
     *
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
    public QualityLine deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                         String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        QualityLine dbQualityLine = getQualityLine(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineRepository.save(dbQualityLine);
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
    public List<QualityLine> deleteQualityLineForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                          String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<QualityLine> dbQualityLine = getQualityLineForReversal(warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);

        if (dbQualityLine != null && !dbQualityLine.isEmpty()) {
            List<QualityLine> qualityLineList = new ArrayList<>();
            dbQualityLine.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
                qualityLineList.add(data);
            });
            return qualityLineRepository.saveAll(qualityLineList);
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
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineInterim> deleteOutboundLineInterimForReversal(String warehouseId, String preOutboundNo, String refDocNumber,
                                                                          String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<OutboundLineInterim> listOutboundLineInterim = outboundLineInterimRepository.
                findByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(warehouseId,
                        preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (listOutboundLineInterim != null && !listOutboundLineInterim.isEmpty()) {
            listOutboundLineInterim.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setUpdatedBy(loginUserID);
                data.setUpdatedOn(new Date());
            });
            return outboundLineInterimRepository.saveAll(listOutboundLineInterim);
        }
        return listOutboundLineInterim;
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
    public QualityLine deleteQualityLineValidated(String warehouseId, String preOutboundNo, String refDocNumber,
                                                  String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        QualityLine dbQualityLine = getQualityLineValidated(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineRepository.save(dbQualityLine);
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
     * @param qualityInspectionNo
     * @param itemCode
     * @param loginUserID
     * @return
     */
    public QualityLine deleteQualityLine(String warehouseId, String preOutboundNo, String refDocNumber,
                                         String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID) {
        QualityLine dbQualityLine = getQualityLineForUpdate(warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineRepository.save(dbQualityLine);
        } else {
            throw new EntityNotFoundException("Error in deleting Id: " + lineNumber);
        }
    }

    //====================================================================V2===============================================================================

    /**
     * getQualityLines
     *
     * @return
     */
    public List<QualityLineV2> getQualityLinesV2() {
        List<QualityLineV2> qualityLineList = qualityLineV2Repository.findAll();
        qualityLineList = qualityLineList.stream().filter(n -> n.getDeletionIndicator() == 0)
                .collect(Collectors.toList());
        return qualityLineList;
    }

    /**
     * getQualityLine
     *
     * @return
     */
    public QualityLineV2 getQualityLineV2(String companyCodeId, String plantId, String languageId, String partnerCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndPartnerCode(
                companyCodeId, plantId, languageId, partnerCode);
        if (qualityLine != null && qualityLine.getDeletionIndicator() == 0) {
            return qualityLine;
        } else {
            throw new BadRequestException("The given QualityLine ID : " + partnerCode + " doesn't exist.");
        }
    }

    /**
     * // Fetch WH_ID/PRE_OB_NO/REF_DOC_NO/PARTNER_CODE/OB_LINE_NO/ITM_CODE
     *
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public QualityLineV2 getQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                          String preOutboundNo, String refDocNumber, String partnerCode,
                                          Long lineNumber, String itemCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        throw new BadRequestException("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:"
                + preOutboundNo + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:"
                + lineNumber + ",itemCode:" + itemCode + " doesn't exist.");
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
    public QualityLineV2 getQualityLineValidatedV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber,
                                                   String partnerCode, Long lineNumber, String itemCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
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
    public List<QualityLineV2> getQualityLineForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                           String preOutboundNo, String refDocNumber,
                                                           String partnerCode, Long lineNumber, String itemCode, String manufacturerName) {
        List<QualityLineV2> qualityLine = qualityLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
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
    public QualityLineV2 getQualityLineForUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber,
                                                   String partnerCode, Long lineNumber, String itemCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                + ",itemCode:" + itemCode + " doesn't exist.");
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
    public List<QualityLineV2> getQualityLineForUpdateForDeliverConformationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber, String itemCode) {
        List<QualityLineV2> qualityLine = qualityLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        log.info("The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                + ",itemCode:" + itemCode + " doesn't exist.");
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
    public List<QualityLineV2> getQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String preOutboundNo, String refDocNumber, String partnerCode, List<Long> lineNumbers, List<String> itemCodes) {
        List<QualityLineV2> qualityLine = qualityLineV2Repository
                .findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberInAndItemCodeInAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumbers, itemCodes, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLineV2 getQualityLineForUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                    String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                    String qualityInspectionNo, String itemCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
                        itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        throw new BadRequestException(
                "The given QualityLine ID : " + "warehouseId:" + warehouseId + ",preOutboundNo:" + preOutboundNo
                        + ",refDocNumber:" + refDocNumber + ",partnerCode:" + partnerCode + ",lineNumber:" + lineNumber
                        + ",qualityInspectionNo:" + qualityInspectionNo + ",itemCode:" + itemCode + " doesn't exist.");
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLineV2 findDuplicateRecordV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                String qualityInspectionNo, String itemCode, String manufacturerName) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber,
                        partnerCode, lineNumber, qualityInspectionNo, itemCode, manufacturerName, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @return
     */
    private QualityLineV2 findQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                            String qualityInspectionNo, String itemCode) {
        QualityLineV2 qualityLine = qualityLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndQualityInspectionNoAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo,
                        itemCode, 0L);
        if (qualityLine != null) {
            return qualityLine;
        }
        return null;
    }

    /**
     * @param searchQualityLine
     * @return
     * @throws ParseException
     */
    public Stream<QualityLineV2> findQualityLineV2(SearchQualityLineV2 searchQualityLine) throws ParseException {
        QualityLineV2Specification spec = new QualityLineV2Specification(searchQualityLine);
        Stream<QualityLineV2> results = qualityLineV2Repository.stream(spec, QualityLineV2.class);
        return results;
    }

    /**
     * @param newQualityLines
     * @param loginUserID
     * @return
     * @throws Exception 
     */
//    @Transactional
    public List<QualityLineV2> createQualityLineV2(List<AddQualityLineV2> newQualityLines, String loginUserID)
            throws Exception {
        try {
            log.info("-------createQualityLine--------called-------> " + newQualityLines);

            List<QualityLineV2> toBeCreatedQLList = new ArrayList<>();
            for (AddQualityLineV2 newQualityLine : newQualityLines) {
                log.info("Input from UI:  " + newQualityLine);
                log.info("QualityQty, PickConfirmQty: " + newQualityLine.getQualityQty() + ", " + newQualityLine.getPickConfirmQty());

                QualityLineV2 dbQualityLine = new QualityLineV2();
                BeanUtils.copyProperties(newQualityLine, dbQualityLine, CommonUtils.getNullPropertyNames(newQualityLine));

                // STATUS_ID - HardCoded Value "55"
                dbQualityLine.setStatusId(55L);

                IKeyValuePair description = stagingLineV2Repository.getDescription(dbQualityLine.getCompanyCodeId(),
                        dbQualityLine.getLanguageId(),
                        dbQualityLine.getPlantId(),
                        dbQualityLine.getWarehouseId());

                statusDescription = stagingLineV2Repository.getStatusDescription(55L, dbQualityLine.getLanguageId());
                dbQualityLine.setStatusDescription(statusDescription);
                dbQualityLine.setCompanyDescription(description.getCompanyDesc());
                dbQualityLine.setPlantDescription(description.getPlantDesc());
                dbQualityLine.setWarehouseDescription(description.getWarehouseDesc());

                OrderManagementLineV2 dbOrderManagementLine = orderManagementLineService.getOrderManagementLineForLineUpdateV2(
                        newQualityLine.getCompanyCodeId(),
                        newQualityLine.getPlantId(),
                        newQualityLine.getLanguageId(),
                        newQualityLine.getWarehouseId(),
                        newQualityLine.getPreOutboundNo(),
                        newQualityLine.getRefDocNumber(),
                        newQualityLine.getLineNumber(),
                        newQualityLine.getItemCode());
                log.info("OrderManagementLine: " + dbOrderManagementLine);

                if (dbOrderManagementLine != null) {
                    dbQualityLine.setManufacturerName(dbOrderManagementLine.getManufacturerName());
                    dbQualityLine.setManufacturerFullName(dbOrderManagementLine.getManufacturerFullName());
                    dbQualityLine.setMiddlewareId(dbOrderManagementLine.getMiddlewareId());
                    dbQualityLine.setMiddlewareHeaderId(dbOrderManagementLine.getMiddlewareHeaderId());
                    dbQualityLine.setMiddlewareTable(dbOrderManagementLine.getMiddlewareTable());
                    dbQualityLine.setReferenceDocumentType(dbOrderManagementLine.getReferenceDocumentType());
                    dbQualityLine.setSalesInvoiceNumber(dbOrderManagementLine.getSalesInvoiceNumber());
                    dbQualityLine.setSalesOrderNumber(dbOrderManagementLine.getSalesOrderNumber());
                    dbQualityLine.setPickListNumber(dbOrderManagementLine.getPickListNumber());
                    dbQualityLine.setOutboundOrderTypeId(dbOrderManagementLine.getOutboundOrderTypeId());
                    dbQualityLine.setDescription(dbOrderManagementLine.getDescription());
                    dbQualityLine.setSupplierInvoiceNo(dbOrderManagementLine.getSupplierInvoiceNo());
                    dbQualityLine.setTokenNumber(dbOrderManagementLine.getTokenNumber());
                    dbQualityLine.setBarcodeId(dbOrderManagementLine.getBarcodeId());
                    dbQualityLine.setTargetBranchCode(dbOrderManagementLine.getTargetBranchCode());
                    dbQualityLine.setImsSaleTypeCode(dbOrderManagementLine.getImsSaleTypeCode());
                }

                dbQualityLine.setBarcodeId(newQualityLine.getBarcodeId());
                dbQualityLine.setDeletionIndicator(0L);
                dbQualityLine.setQualityCreatedBy(loginUserID);
                dbQualityLine.setQualityUpdatedBy(loginUserID);
                dbQualityLine.setQualityCreatedOn(new Date());
                dbQualityLine.setQualityUpdatedOn(new Date());

                /*
                 * String warehouseId, String preOutboundNo, String refDocNumber, String
                 * partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode
                 */
                QualityLineV2 existingQualityLine = findDuplicateRecordV2(
                        newQualityLine.getCompanyCodeId(), newQualityLine.getPlantId(),
                        newQualityLine.getLanguageId(), newQualityLine.getWarehouseId(),
                        newQualityLine.getPreOutboundNo(), newQualityLine.getRefDocNumber(),
                        newQualityLine.getPartnerCode(), newQualityLine.getLineNumber(),
                        newQualityLine.getQualityInspectionNo(), newQualityLine.getItemCode(), newQualityLine.getManufacturerName());
                log.info("existingQualityLine record status : " + existingQualityLine);
                
                if (existingQualityLine == null) {
                	toBeCreatedQLList.add(dbQualityLine);
                }
            } // End of for
            
            if (toBeCreatedQLList != null) {
            	List<QualityLineV2> createdQualityLineList = qualityLineV2Repository.saveAll(toBeCreatedQLList);
            	log.info("-----------createdQualityLineList-------> : " + createdQualityLineList);
            	
                createOutboundLineInterimV2(createdQualityLineList);
                statusDescription = stagingLineV2Repository.getStatusDescription(55L, createdQualityLineList.get(0).getLanguageId());
                
                List<String> getQualityInspectionNos = 
                		createdQualityLineList.stream().map(QualityLineV2::getQualityInspectionNo).distinct().collect(Collectors.toList());
                log.info("-----------getQualityInspectionNos-------> : " + getQualityInspectionNos);
                qualityHeaderV2Repository.updateQualityHeader(statusDescription, getQualityInspectionNos);
                
                /*
                 * 
                 */
				try {
					statusDescription = stagingLineV2Repository.getStatusDescription(57L, createdQualityLineList.get(0).getLanguageId());
					String companyCodeId = null;
					String plantId = null;
					String languageId = null;
					String warehouseId = null;
					String preOutboundNo = null;
					String refDocNumber = null;
					String partnerCode = null;
					List<Long> lineNumbers = new ArrayList<>();
					for (QualityLineV2 dbQualityLine : createdQualityLineList) {
						updateOutboundLineV2(dbQualityLine, statusDescription);
						companyCodeId = dbQualityLine.getCompanyCodeId();
						plantId = dbQualityLine.getPlantId();
						languageId = dbQualityLine.getLanguageId();
						warehouseId = dbQualityLine.getWarehouseId();
						preOutboundNo = dbQualityLine.getPreOutboundNo();
						refDocNumber = dbQualityLine.getRefDocNumber();
						partnerCode = dbQualityLine.getPartnerCode();
						lineNumbers.add (dbQualityLine.getLineNumber());
					}
					List<OutboundLineV2> deliveryConfirmedOutboundLineV2 = 
							outboundLineService.deliveryConfirmationV2 (companyCodeId, plantId,languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, loginUserID, lineNumbers);
					log.info("-----------deliveryConfirmedOutboundLineV2-------> : " + deliveryConfirmedOutboundLineV2);
					return createdQualityLineList;
				} catch (Exception e) {
                    e.printStackTrace();
                    log.error("---ERROR:--------deliveryConfirmedOutboundLineV2-------> : " + e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		return null;
    }

    /**
     * 
     * @param dbQualityLineList
     * @throws Exception
     */
    private void createOutboundLineInterimV2 (List<QualityLineV2> dbQualityLineList) throws Exception {
    	List<OutboundLineInterim> outboundLineInterimList = new ArrayList<>();
    	dbQualityLineList.stream().forEach(dbQualityLine -> {
    		OutboundLineInterim outboundLineInterim = new OutboundLineInterim();
            BeanUtils.copyProperties(dbQualityLine, outboundLineInterim, CommonUtils.getNullPropertyNames(dbQualityLine));
            outboundLineInterim.setDeletionIndicator(0L);
            outboundLineInterim.setDeliveryQty(dbQualityLine.getQualityQty());
            outboundLineInterim.setCreatedBy(dbQualityLine.getQualityCreatedBy());
            outboundLineInterim.setCreatedOn(new Date());
            outboundLineInterim.setIsOutboundLineUpdated(0L);
            outboundLineInterimList.add (outboundLineInterim);
    	});   	
    	
        List<OutboundLineInterim> createdOutboundLineList = outboundLineInterimRepository.saveAll(outboundLineInterimList);
        log.info("outboundLineInterim created ----------->: " + createdOutboundLineList);
    }

    /**
     * @param dbQualityLine
     * @param statusDescription2 
     * @param DLV_ORD_NO
     */
    private void updateOutboundLineV2(QualityLineV2 dbQualityLine, String statusDescription2) {
        try {
            Double deliveryQty = outboundLineInterimRepository.getSumOfDeliveryLine(dbQualityLine.getWarehouseId(), dbQualityLine.getPreOutboundNo(),
                    dbQualityLine.getRefDocNumber(), dbQualityLine.getPartnerCode(), dbQualityLine.getLineNumber(),
                    dbQualityLine.getItemCode());
            log.info("=======updateOutboundLine==========>: " + deliveryQty);
            outboundLineV2Repository.updateOutboundLineV2ForQuality(
                  dbQualityLine.getCompanyCodeId(),
                  dbQualityLine.getPlantId(),
                  dbQualityLine.getLanguageId(),
                  dbQualityLine.getWarehouseId(),
                  dbQualityLine.getPreOutboundNo(),
                  dbQualityLine.getRefDocNumber(),
                  dbQualityLine.getPartnerCode(),
                  dbQualityLine.getLineNumber(),
                  dbQualityLine.getItemCode(),
                  57L,
                  statusDescription,
                  deliveryQty);
            
            // WarehouseId, PreOutboundNo, RefDocNumber, PartnerCode, LineNumber, ItemCode, DeliveryQty, DeliveryOrderNo, StatusId(57L);
//            outboundLineService.updateOutboundLineByQLCreateProc(
//                    dbQualityLine.getCompanyCodeId(),
//                    dbQualityLine.getPlantId(),
//                    dbQualityLine.getLanguageId(),
//                    dbQualityLine.getWarehouseId(),
//                    dbQualityLine.getPreOutboundNo(),
//                    dbQualityLine.getRefDocNumber(),
//                    dbQualityLine.getPartnerCode(),
//                    dbQualityLine.getLineNumber(),
//                    dbQualityLine.getItemCode(),
//                    deliveryQty,
//                    DLV_ORD_NO,
//                    57L,
//                    statusDescription);
//            log.info("----------updateOutboundLineByQLCreateProc updated as StatusID = 57----------->");
            
        } catch (Exception e1) {
            e1.printStackTrace();
            log.info("outboundLine updated error: " + e1.toString());
        }
    }

    /**
     * @param dbQualityLines
     */
//    private void postDeliveryConfirm(List<QualityLineV2> dbQualityLines, String loginUserID ) {
//        try {
//            log.info("Delivery Confirm check: -------> started");
//
//            List<String> companyCodeId = dbQualityLines.stream().map(QualityLineV2::getCompanyCodeId).distinct().collect(Collectors.toList());
//            List<String> plantId = dbQualityLines.stream().map(QualityLineV2::getPlantId).distinct().collect(Collectors.toList());
//            List<String> languageId = dbQualityLines.stream().map(QualityLineV2::getLanguageId).distinct().collect(Collectors.toList());
//            List<String> warehouseId = dbQualityLines.stream().map(QualityLineV2::getWarehouseId).distinct().collect(Collectors.toList());
//            List<String> refDocNumber = dbQualityLines.stream().map(QualityLineV2::getRefDocNumber).distinct().collect(Collectors.toList());
//            List<String> preOutboundNo = dbQualityLines.stream().map(QualityLineV2::getPreOutboundNo).distinct().collect(Collectors.toList());
//
//            Long[] statusId = new Long[]{42L, 43L};
//            SearchOrderManagementLineV2 searchOrderManagementLine = new SearchOrderManagementLineV2();
//            searchOrderManagementLine.setStatusId(List.of(statusId));
//            searchOrderManagementLine.setCompanyCodeId(companyCodeId);
//            searchOrderManagementLine.setPlantId(plantId);
//            searchOrderManagementLine.setLanguageId(languageId);
//            searchOrderManagementLine.setWarehouseId(warehouseId);
//            searchOrderManagementLine.setRefDocNumber(refDocNumber);
//            searchOrderManagementLine.setPreOutboundNo(preOutboundNo);
//
//            List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.findOrderManagementLineV2(searchOrderManagementLine).collect(Collectors.toList());
//            log.info("orderManagementLineList statusId [42,43]----------->: " + orderManagementLineList.stream().count());
//
//            SearchPickupHeaderV2 searchPickupHeader = new SearchPickupHeaderV2();
//            statusId = new Long[]{48L};
//            searchPickupHeader.setStatusId(List.of(statusId));
//            searchPickupHeader.setCompanyCodeId(companyCodeId);
//            searchPickupHeader.setPlantId(plantId);
//            searchPickupHeader.setLanguageId(languageId);
//            searchPickupHeader.setWarehouseId(warehouseId);
//            searchPickupHeader.setRefDocNumber(refDocNumber);
//            searchPickupHeader.setPreOutboundNo(preOutboundNo);
//
//            List<PickupHeaderV2> pickupHeaderList = pickupHeaderService.findPickupHeaderV2(searchPickupHeader).collect(Collectors.toList());
//            log.info("pickupHeaderList statusId [48]----------->: " + pickupHeaderList.stream().count());
//
//            SearchQualityHeaderV2 searchQualityHeader = new SearchQualityHeaderV2();
//            statusId = new Long[]{54L};
//            searchQualityHeader.setStatusId(List.of(statusId));
//            searchQualityHeader.setCompanyCodeId(companyCodeId);
//            searchQualityHeader.setPlantId(plantId);
//            searchQualityHeader.setLanguageId(languageId);
//            searchQualityHeader.setWarehouseId(warehouseId);
//            searchQualityHeader.setRefDocNumber(refDocNumber);
//            searchQualityHeader.setPreOutboundNo(preOutboundNo);
//
//            List<QualityHeaderV2> qualityHeaderList = qualityHeaderService.findQualityHeaderNewV2(searchQualityHeader).collect(Collectors.toList());
//            log.info("qualityHeaderList statusId [54]----------->: " + qualityHeaderList.stream().count());
//
//            if ((orderManagementLineList == null || orderManagementLineList.isEmpty()) &&
//                    (pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
//                    (qualityHeaderList == null || qualityHeaderList.isEmpty())) {
//                Long outboundLineCount = outboundLineService.getOutboundLineCountV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
//                log.info("OuboundLine count :----------->" + outboundLineCount);
//                Long count_57 = 0L;
//                if (outboundLineCount != null && outboundLineCount > 0) {
//                    List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L);
//                    count_57 = outboundLineService.getOutboundLineStatusIdCountV2(
//                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, statusIdsToBeChecked);
//                    log.info("Count_57, OutboundLineList Size: " + count_57 + ", " + outboundLineCount);
//
//                    if (count_57.equals(outboundLineCount)) {
//                        log.info("All Outbound Lines Confirmed - Automate/Calling the Delivery Confirm Procedure");
//
//                        SearchOutboundHeaderV2 searchOutboundHeaderV2 = new SearchOutboundHeaderV2();
//                        searchOutboundHeaderV2.setCompanyCodeId(companyCodeId);
//                        searchOutboundHeaderV2.setPlantId(plantId);
//                        searchOutboundHeaderV2.setLanguageId(languageId);
//                        searchOutboundHeaderV2.setWarehouseId(warehouseId);
//                        searchOutboundHeaderV2.setRefDocNumber(refDocNumber);
//                        searchOutboundHeaderV2.setPreOutboundNo(preOutboundNo);
//
//                        List<OutboundHeaderV2Stream> outboundHeaderV2List = outboundHeaderService.findOutboundHeadernewV2(searchOutboundHeaderV2);
//                        log.info("outboundHeaderV2List ----------->: " + outboundHeaderV2List.stream().count());
//
//                        for (OutboundHeaderV2Stream dbOutboundHeader : outboundHeaderV2List) {
//                            SearchOutboundLineV2 searchOutboundLineV2 = new SearchOutboundLineV2();
//                            searchOutboundLineV2.setCompanyCodeId(List.of(dbOutboundHeader.getCompanyCodeId()));
//                            searchOutboundLineV2.setPlantId(List.of(dbOutboundHeader.getPlantId()));
//                            searchOutboundLineV2.setLanguageId(List.of(dbOutboundHeader.getLanguageId()));
//                            searchOutboundLineV2.setWarehouseId(List.of(dbOutboundHeader.getWarehouseId()));
//
//                            searchOutboundLineV2.setRefDocNumber(List.of(dbOutboundHeader.getRefDocNumber()));
//                            searchOutboundLineV2.setPreOutboundNo(List.of(dbOutboundHeader.getPreOutboundNo()));
//                            List<OutboundLineOutput> outboundLineV2s = outboundLineService.findOutboundLineNewV2(searchOutboundLineV2);
//                            log.info("outboundLineV2s ----------->: " + outboundLineV2s.stream().count());
//
//                            List<OutboundLineV2> updatedOutboundLinesV2 = outboundLineService.updateOutboundLinesV2(loginUserID, outboundLineV2s);
//                            log.info("updatedOutboundLinesV2 ----------->: " + updatedOutboundLinesV2.stream().count());
//                            log.info("updatedOutboundLinesV2 ----------->: " + updatedOutboundLinesV2);
//
//                            if (updatedOutboundLinesV2 != null) {
//                                log.info("Initiating deliveryConfirm ----------->: " + updatedOutboundLinesV2);
//                                List<OutboundLineV2> deliveryConfirm = outboundLineService.deliveryConfirmationV2(
//                                        updatedOutboundLinesV2.get(0).getCompanyCodeId(), updatedOutboundLinesV2.get(0).getPlantId(),
//                                        updatedOutboundLinesV2.get(0).getLanguageId(), updatedOutboundLinesV2.get(0).getWarehouseId(),
//                                        updatedOutboundLinesV2.get(0).getPreOutboundNo(), updatedOutboundLinesV2.get(0).getRefDocNumber(),
//                                        updatedOutboundLinesV2.get(0).getPartnerCode(), loginUserID);
//                            }
//                            log.info("<------------------Delivery Confirm Finished Processing------------------>");
//                        }
//                    }
//                }
//            }
//        } catch (Exception e1) {
//            e1.printStackTrace();
//            log.info("outboundLine delivery confirm error: " + e1.toString());
//        }
//    }

    /**
     * @param dbQualityLine
     * @param subMvtTypeId
     * @param movementDocumentNo
     * @param storageBin
     * @param movementQtyValue
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private InventoryMovement createInventoryMovementV2(QualityLineV2 dbQualityLine, Long subMvtTypeId,
                                                        String movementDocumentNo, String storageBin, String movementQtyValue, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        InventoryMovement inventoryMovement = new InventoryMovement();
        BeanUtils.copyProperties(dbQualityLine, inventoryMovement, CommonUtils.getNullPropertyNames(dbQualityLine));

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

        inventoryMovement.setManufacturerName(dbQualityLine.getManufacturerName());
        inventoryMovement.setRefDocNumber(dbQualityLine.getRefDocNumber());
        inventoryMovement.setDescription(dbQualityLine.getDescription());
        inventoryMovement.setCompanyDescription(dbQualityLine.getCompanyDescription());
        inventoryMovement.setPlantDescription(dbQualityLine.getPlantDescription());
        inventoryMovement.setWarehouseDescription(dbQualityLine.getWarehouseDescription());
        inventoryMovement.setBarcodeId(dbQualityLine.getBarcodeId());

        // ST_BIN
        inventoryMovement.setStorageBin(storageBin);

        // MVT_QTY_VAL
        inventoryMovement.setMovementQtyValue(movementQtyValue);

        // PACK_BAR_CODE
        inventoryMovement.setPackBarcodes(dbQualityLine.getPickPackBarCode());

        // MVT_QTY
        inventoryMovement.setMovementQty(dbQualityLine.getPickConfirmQty());

        // MVT_UOM
        inventoryMovement.setInventoryUom(dbQualityLine.getQualityConfirmUom());

        // IM_CTD_BY
        inventoryMovement.setCreatedBy(dbQualityLine.getQualityConfirmedBy());

        // IM_CTD_ON
        inventoryMovement.setCreatedOn(dbQualityLine.getQualityCreatedOn());
        inventoryMovement.setCreatedBy(loginUserID);

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
     * @param updateQualityLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<QualityLineV2> updateQualityLineV2(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String preOutboundNo, String refDocNumber,
                                                   String partnerCode, Long lineNumber, String itemCode, String loginUserID,
                                                   QualityLineV2 updateQualityLine) throws IllegalAccessException, InvocationTargetException {
        List<QualityLineV2> dbQualityLine = getQualityLineForUpdateForDeliverConformationV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo,
                refDocNumber, partnerCode, lineNumber, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.forEach(data -> {
                BeanUtils.copyProperties(updateQualityLine, data, CommonUtils.getNullPropertyNames(updateQualityLine));
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
            });
            return qualityLineV2Repository.saveAll(dbQualityLine);
        }
        return null;
    }

    /**
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param lineNumber
     * @param qualityInspectionNo
     * @param itemCode
     * @param loginUserID
     * @param updateQualityLine
     * @return
     */
    public QualityLineV2 updateQualityLineV2(String companyCodeId, String plantId, String languageId,
                                             String warehouseId, String preOutboundNo, String refDocNumber,
                                             String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID,
                                             @Valid QualityLineV2 updateQualityLine) throws java.text.ParseException {
        QualityLineV2 dbQualityLine = getQualityLineForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode,
                lineNumber, qualityInspectionNo, itemCode);
        if (dbQualityLine != null) {
            BeanUtils.copyProperties(updateQualityLine, dbQualityLine,
                    CommonUtils.getNullPropertyNames(updateQualityLine));
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineV2Repository.save(dbQualityLine);
        }
        return null;
    }

    /**
     * deleteQualityLine
     *
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
    public QualityLineV2 deleteQualityLineV2(String companyCodeId, String plantId, String languageId,
                                             String warehouseId, String preOutboundNo, String refDocNumber,
                                             String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        QualityLineV2 dbQualityLine = getQualityLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber,
                itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineV2Repository.save(dbQualityLine);
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
    public List<QualityLineV2> deleteQualityLineForReversalV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preOutboundNo, String refDocNumber,
                                                              String partnerCode, Long lineNumber, String itemCode, String manufacturerName, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<QualityLineV2> dbQualityLine = getQualityLineForReversalV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName);

        if (dbQualityLine != null && !dbQualityLine.isEmpty()) {
            List<QualityLineV2> qualityLineList = new ArrayList<>();
            dbQualityLine.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setQualityUpdatedBy(loginUserID);
                data.setQualityUpdatedOn(new Date());
                qualityLineList.add(data);
            });
            return qualityLineV2Repository.saveAll(qualityLineList);
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
     * @param itemCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineInterim> deleteOutboundLineInterimForReversalV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                            String preOutboundNo, String refDocNumber, String partnerCode, Long lineNumber,
                                                                            String itemCode, String manufacturerName, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        List<OutboundLineInterim> listOutboundLineInterim = outboundLineInterimRepository.
                findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName, 0L);
        if (listOutboundLineInterim != null && !listOutboundLineInterim.isEmpty()) {
            listOutboundLineInterim.forEach(data -> {
                data.setDeletionIndicator(1L);
                data.setUpdatedBy(loginUserID);
                data.setUpdatedOn(new Date());
            });
            return outboundLineInterimRepository.saveAll(listOutboundLineInterim);
        }
        return listOutboundLineInterim;
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
    public QualityLineV2 deleteQualityLineValidatedV2(String companyCodeId, String plantId, String languageId,
                                                      String warehouseId, String preOutboundNo, String refDocNumber,
                                                      String partnerCode, Long lineNumber, String itemCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        QualityLineV2 dbQualityLine = getQualityLineValidatedV2(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            return qualityLineV2Repository.save(dbQualityLine);
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
     * @param qualityInspectionNo
     * @param itemCode
     * @param loginUserID
     * @return
     */
    public void deleteQualityLineV2(String companyCodeId, String plantId, String languageId,
                                    String warehouseId, String preOutboundNo, String refDocNumber,
                                    String partnerCode, Long lineNumber, String qualityInspectionNo, String itemCode, String loginUserID) throws java.text.ParseException {
        QualityLineV2 dbQualityLine = getQualityLineForUpdateV2(companyCodeId, plantId, languageId, warehouseId,
                preOutboundNo, refDocNumber, partnerCode, lineNumber, qualityInspectionNo, itemCode);
        if (dbQualityLine != null) {
            dbQualityLine.setDeletionIndicator(1L);
            dbQualityLine.setQualityUpdatedBy(loginUserID);
            dbQualityLine.setQualityUpdatedOn(new Date());
            qualityLineV2Repository.save(dbQualityLine);
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
     * @param refDocNumber
     * @param loginUserID
     * @return
     * @throws Exception
     */
    //DeleteQualityHeaderV2
    public List<QualityLineV2> deleteQualityLine(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String preOutboundNo, String loginUserID) throws Exception {

        List<QualityLineV2> qualityLineV2List = new ArrayList<>();
        List<QualityLineV2> dbQualityLineList = qualityLineV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, refDocNumber, preOutboundNo,0L);
        log.info("PickList Cancellation - QualityLine : " + dbQualityLineList);
        if (dbQualityLineList != null && !dbQualityLineList.isEmpty()) {
            for (QualityLineV2 qualityLineV2 : dbQualityLineList) {
                qualityLineV2.setDeletionIndicator(1L);
                qualityLineV2.setQualityUpdatedBy(loginUserID);
                qualityLineV2.setQualityUpdatedOn(new Date());
                QualityLineV2 saveQualityLine = qualityLineV2Repository.save(qualityLineV2);
                qualityLineV2List.add(saveQualityLine);
            }
        }
        return qualityLineV2List;

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
    public List<QualityLineV2> getPLCQualityLine(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String preOutboundNo) {
        List<QualityLineV2> dbQualityLineList = qualityLineV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - QualityLine : " + dbQualityLineList);
        return dbQualityLineList;
    }
}