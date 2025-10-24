package com.tekclover.wms.api.outbound.transaction.service;

import com.tekclover.wms.api.outbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.outbound.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.outbound.transaction.model.inventory.InventoryMovement;
import com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.AddPickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.PickupLine;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.outbound.transaction.repository.*;
import com.tekclover.wms.api.outbound.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.tekclover.wms.api.outbound.transaction.service.PickupLineService.PICK_HE_NO;


@Service
@Slf4j
public class PickupLineAsyncProcessService extends BaseService {

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    QualityHeaderService qualityHeaderService;


    /**
     *
     * @param newPickupLine pickupLine
     * @param loginUserID userID
     * @return
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//    public PickupLineV2 processPickupLine(AddPickupLine newPickupLine, String loginUserID) {
//        try {
//            if (newPickupLine.getPickConfirmQty() < 0) {
//                throw new BadRequestException("Invalid Qty: " + newPickupLine.getPickConfirmQty());
//            }
//
//            PickupLineV2 dbPickupLine = new PickupLineV2();
//            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));
//            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));
//
//            log.info("Inputs for existing check : barcodeId -----> {}", dbPickupLine.getBarcodeId());
//            // Check if duplicate already exists in DB
//            List<PickupLineV2> existing = pickupLineV2Repository.getExistingPickupLine(
//                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getLanguageId(),
//                    dbPickupLine.getPlantId(), dbPickupLine.getWarehouseId(),
//                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(),
//                    dbPickupLine.getItemCode(), dbPickupLine.getBarcodeId()
//            );
//
//            if (existing != null && !existing.isEmpty()) {
//                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existing);
//            }
//            log.info("Allocated_Qty : " + dbPickupLine.getAllocatedQty());
//
//            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
//            dbPickupLine.setVarianceQuantity(VAR_QTY);
//            log.info("Var_Qty: " + VAR_QTY);
//
//            String handlingEquipment = pickupLineV2Repository.getHandlingEquipment(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
//            handlingEquipment = handlingEquipment != null ? handlingEquipment : PICK_HE_NO;
//            log.info("HE_NO : " + handlingEquipment);
//
//            double actualInventoryQty = dbPickupLine.getPickConfirmQty();
//            dbPickupLine.setActualInventoryQty(actualInventoryQty);
//
//            dbPickupLine.setActualHeNo(handlingEquipment);
//            dbPickupLine.setStatusId(newPickupLine.getPickConfirmQty() > 0 ? 57L : 51L);
//            dbPickupLine.setDeletionIndicator(0L);
//            dbPickupLine.setPickupUpdatedBy(loginUserID);
//            dbPickupLine.setPickupConfirmedBy(loginUserID);
//            dbPickupLine.setPickupUpdatedOn(new Date());
//            dbPickupLine.setPickupConfirmedOn(new Date());
//
//            return pickupLineV2Repository.save(dbPickupLine);
//        } catch (Exception e) {
//            log.error("Error in processPickupLine: {}", e.getMessage());
//            return null;
//        }
//    }


    /**
     *
     * @param newPickupLine pickupLine
     * @param loginUserID userID
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public PickupLineV2 processPickupLineV6(AddPickupLine newPickupLine, String loginUserID) {
        try {
            if (newPickupLine.getPickConfirmQty() < 0) {
                throw new BadRequestException("Invalid Qty: " + newPickupLine.getPickConfirmQty());
            }

            PickupLineV2 dbPickupLine = new PickupLineV2();
            BeanUtils.copyProperties(newPickupLine, dbPickupLine, CommonUtils.getNullPropertyNames(newPickupLine));
            dbPickupLine.setCompanyCodeId(String.valueOf(newPickupLine.getCompanyCodeId()));

            log.info("Inputs for existing check : barcodeId -----> {}", dbPickupLine.getBarcodeId());
            // Check if duplicate already exists in DB
            List<PickupLineV2> existing = pickupLineV2Repository.getExistingPickupLine(
                    dbPickupLine.getCompanyCodeId(), dbPickupLine.getLanguageId(),
                    dbPickupLine.getPlantId(), dbPickupLine.getWarehouseId(),
                    dbPickupLine.getPreOutboundNo(), dbPickupLine.getRefDocNumber(),
                    dbPickupLine.getItemCode(), dbPickupLine.getBarcodeId()
            );

            if (existing != null && !existing.isEmpty()) {
                throw new BadRequestException("PickupLine Record is getting duplicated. Given data already exists in the Database. : " + existing);
            }
            log.info("Allocated_Qty : " + dbPickupLine.getAllocatedQty());

            Double VAR_QTY = (dbPickupLine.getAllocatedQty() != null ? dbPickupLine.getAllocatedQty() : 0) - (dbPickupLine.getPickConfirmQty() != null ? dbPickupLine.getPickConfirmQty() : 0);
            dbPickupLine.setVarianceQuantity(VAR_QTY);
            log.info("Var_Qty: " + VAR_QTY);

            String handlingEquipment = pickupLineV2Repository.getHandlingEquipment(dbPickupLine.getCompanyCodeId(), dbPickupLine.getPlantId(), dbPickupLine.getLanguageId(), dbPickupLine.getWarehouseId());
            handlingEquipment = handlingEquipment != null ? handlingEquipment : PICK_HE_NO;
            log.info("HE_NO : " + handlingEquipment);

            double actualInventoryQty = dbPickupLine.getPickConfirmQty();
            dbPickupLine.setActualInventoryQty(actualInventoryQty);

            dbPickupLine.setActualHeNo(handlingEquipment);
            dbPickupLine.setStatusId(newPickupLine.getPickConfirmQty() > 0 ? 57L : 51L);
            dbPickupLine.setDeletionIndicator(0L);
            dbPickupLine.setPickupUpdatedBy(loginUserID);
            dbPickupLine.setPickupConfirmedBy(loginUserID);
            dbPickupLine.setPickupUpdatedOn(new Date());
            dbPickupLine.setPickupConfirmedOn(new Date());

            return pickupLineV2Repository.save(dbPickupLine);
        } catch (Exception e) {
            log.error("Error in processPickupLine: {}", e.getMessage());
            return null;
        }
    }
    /**
     *
     * @param pickupLines pickupLine
     * @param loginUserID loginUserId
     * @throws Exception
     */
//    @Async("asyncTaskExecutor")
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//    public void updateHeaderAndInventory(List<PickupLineV2> pickupLines, String loginUserID) throws Exception {
//        if (pickupLines.isEmpty()) return;
//
//        String companyCodeId = pickupLines.get(0).getCompanyCodeId();
//        String plantId = pickupLines.get(0).getPlantId();
//        String languageId = pickupLines.get(0).getLanguageId();
//        String warehouseId = pickupLines.get(0).getWarehouseId();
//        String refDocNumber = pickupLines.get(0).getRefDocNumber();
//        String preOutboundNo = pickupLines.get(0).getPreOutboundNo();
//
//        boolean isStatus51 = pickupLines.stream().allMatch(line -> line.getStatusId().equals(51L));
//        Long headerStatusId = isStatus51 ? 51L : 57L;
//        String headerStatusDescription = getStatusDescription(headerStatusId, languageId);
//
//        List<OrderManagementLineV2> dbOrderManagementLineList = orderManagementLineV2Repository.getOrderManagementForPickup(companyCodeId, plantId, warehouseId, languageId, refDocNumber, preOutboundNo);
//        log.info("Queried OrderManagementList ------> {}", dbOrderManagementLineList);
//        log.info("dbOrderManagementLine List size ----> {}", dbOrderManagementLineList.size());
//
//        Set<String> dbBarcodeSet = dbOrderManagementLineList.stream()
//                .map(OrderManagementLineV2::getBarcodeId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        log.info("dbBarcodeSet OrderManagement Set -----> {}", dbBarcodeSet);
//
//        List<PickupLineV2> sameBarcodeList = new ArrayList<>();
//        List<PickupLineV2> differentBarcodeList = new ArrayList<>();
//        List<PickupLineV2> missingInDBList = new ArrayList<>();
//
//        Set<String> matchedBarcodes = new HashSet<>();
//
//        for (PickupLineV2 dbPickupLine : pickupLines) {
//            String pickupBarcode = dbPickupLine.getBarcodeId();
//            if (pickupBarcode == null) {
//                missingInDBList.add(dbPickupLine);
//                continue;
//            }
//
//            if (dbBarcodeSet.contains(pickupBarcode)) {
//                sameBarcodeList.add(dbPickupLine);
//                matchedBarcodes.add(pickupBarcode); // Track matched barcodes
//            } else {
//                differentBarcodeList.add(dbPickupLine);
//            }
//        }
//
//        log.info("matchedBarcodes --------- {}", matchedBarcodes);
//
//        // Find leftover (unmatched) barcodes from DB
//        Set<String> leftoverBarcodes = new HashSet<>(dbBarcodeSet);
//        leftoverBarcodes.removeAll(matchedBarcodes);
//
//        log.info("Leftover (unmatched) DB barcodes -----> {}", leftoverBarcodes);
//
//        // Step 1: Create a map from barcode to itemCode for leftover barcodes
//        Map<String, String> leftoverBarcodeToItemCode = dbOrderManagementLineList.stream()
//                .filter(line -> leftoverBarcodes.contains(line.getBarcodeId()))
//                .collect(Collectors.toMap(
//                        OrderManagementLineV2::getBarcodeId,
//                        OrderManagementLineV2::getItemCode
//                ));
//
//        // Step 2: Assign matching barcodes only when itemCode matches
//        for (PickupLineV2 line : differentBarcodeList) {
//            String groupItemCode = line.getItemCode();
//
//            // Find a matching leftover barcode for the same itemCode
//            Optional<Map.Entry<String, String>> matchingEntry = leftoverBarcodeToItemCode.entrySet()
//                    .stream()
//                    .filter(entry -> entry.getValue().equals(groupItemCode))
//                    .findFirst();
//
//            if (matchingEntry.isPresent()) {
//                String matchingBarcode = matchingEntry.get().getKey();
//                line.setReferenceField3(matchingBarcode);
//                leftoverBarcodeToItemCode.remove(matchingBarcode); // Remove to avoid reuse
//            }
//        }
//
//        log.info("sameBarcodeList ----> {}", sameBarcodeList);
//        log.info("differentBarcodeList ----> {}", differentBarcodeList);
//        log.info("missingInDBList ----> {}", missingInDBList);
//
//        if (!sameBarcodeList.isEmpty()) {
//            for (PickupLineV2 dbPickupLine : sameBarcodeList) {
//                log.info("sameBarcodePicked");
//                pickupLineService.modifyInventoryForMatchingBarcodeIdV4(companyCodeId, plantId, languageId, warehouseId,
//                        dbPickupLine.getItemCode(), refDocNumber, dbPickupLine, loginUserID);
//
//                // Inserting record in InventoryMovement
//                Long subMvtTypeId;
//                String movementDocumentNo;
//                String stBin;
//                String movementQtyValue;
//                InventoryMovement inventoryMovement;
//                try {
//                    subMvtTypeId = 1L;
//                    movementDocumentNo = dbPickupLine.getPickupNumber();
//                    stBin = dbPickupLine.getPickedStorageBin();
//                    movementQtyValue = "N";
//                    inventoryMovement = pickupLineService.createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin, movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created : " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error :" + e.toString());
//                    e.printStackTrace();
//                }
//
//                /*
//                 * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
//                 */
//                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
//                if (dbPickupLine.getAssignedPickerId() == null) {
//                    dbPickupLine.setAssignedPickerId("0");
//                }
//
//                log.info("outboundLine updated using Stored Procedure Started... ");
//                outboundLineV2Repository.updateOutboundlineStatusUpdateBagsProc(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(), dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
//                        dbPickupLine.getLineNumber(), headerStatusId, statusDescription, new Date(), dbPickupLine.getBagSize(), dbPickupLine.getNoBags());
//                log.info("outboundLine updated using Stored Procedure: ");
//
//                if (dbPickupLine.getReferenceField6() != null) {
//                    log.info("outboundline update ref_field_6 for Reasons");
//                    outboundLineV2Repository.updateOutboundLineV6(companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo,
//                            dbPickupLine.getItemCode(), dbPickupLine.getLineNumber(), dbPickupLine.getReferenceField6());
//                    log.info("outboundline update ref_field_6 for Reasons completed");
//                }
//
//                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProcV6(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
//                        dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(), dbPickupLine.getLineNumber(), headerStatusId, headerStatusDescription, loginUserID, new Date());
//                log.info("PickupHeader Updated using Stored Procedure..!");
//
//                /*
//                 * ------------------Record insertion in QUALITYHEADER table-----------------------------------
//                 * Allow to create QualityHeader only
//                 */
//                if (dbPickupLine.getStatusId().equals(57L)) {
//                    qualityHeaderService.createQualityHeaderV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, null, loginUserID);
//                }
//            }
//        }
//
//        if (!differentBarcodeList.isEmpty()) {
//            for (PickupLineV2 dbPickupLine : differentBarcodeList) {
//                log.info("DifferentBarcodePicked");
//                pickupLineService.modifyInventoryForNonMatchingBarcodeIdV4(companyCodeId, plantId, languageId, warehouseId,
//                        dbPickupLine.getItemCode(), refDocNumber, dbPickupLine.getReferenceField3(), dbPickupLine, loginUserID);
//
//                // Inserting record in InventoryMovement
//                Long subMvtTypeId;
//                String movementDocumentNo;
//                String stBin;
//                String movementQtyValue;
//                InventoryMovement inventoryMovement;
//                try {
//                    subMvtTypeId = 1L;
//                    movementDocumentNo = dbPickupLine.getPickupNumber();
//                    stBin = dbPickupLine.getPickedStorageBin();
//                    movementQtyValue = "N";
//                    inventoryMovement = pickupLineService.createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin, movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created : " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error :" + e.toString());
//                    e.printStackTrace();
//                }
//
//                /*
//                 * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
//                 */
//                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
//                if (dbPickupLine.getAssignedPickerId() == null) {
//                    dbPickupLine.setAssignedPickerId("0");
//                }
//
//                log.info("outboundLine updated using Stored Procedure Started... ");
//                outboundLineV2Repository.updateOutboundlineStatusUpdateBagsProc(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(), dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
//                        dbPickupLine.getLineNumber(), dbPickupLine.getStatusId(), statusDescription, new Date(), dbPickupLine.getBagSize(), dbPickupLine.getNoBags());
//                log.info("outboundLine updated using Stored Procedure: ");
//
//                if (dbPickupLine.getReferenceField6() != null) {
//                    log.info("outboundline update ref_field_6 for Reasons");
//                    outboundLineV2Repository.updateOutboundLineV6(companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo,
//                            dbPickupLine.getItemCode(), dbPickupLine.getLineNumber(), dbPickupLine.getReferenceField6());
//                    log.info("outboundline update ref_field_6 for Reasons completed");
//                }
//
//                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProcV6(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
//                        dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(), dbPickupLine.getLineNumber(), headerStatusId, headerStatusDescription, loginUserID, new Date());
//                log.info("PickupHeader Updated using Stored Procedure..!");
//
//                /*
//                 * ------------------Record insertion in QUALITYHEADER table-----------------------------------
//                 * Allow to create QualityHeader only
//                 */
//                if (dbPickupLine.getStatusId().equals(57L)) {
//                    qualityHeaderService.createQualityHeaderV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, null, loginUserID);
//                }
//            }
//        }
//
//        if (!missingInDBList.isEmpty()) {
//            for (PickupLineV2 dbPickupLine : missingInDBList) {
//                log.info("missingBarcode");
//                pickupLineService.modifyInventoryForNonMatchingBarcodeIdV4(companyCodeId, plantId, languageId, warehouseId,
//                        dbPickupLine.getItemCode(), refDocNumber, dbPickupLine.getReferenceField2(), dbPickupLine, loginUserID);
//
//                // Inserting record in InventoryMovement
//                Long subMvtTypeId;
//                String movementDocumentNo;
//                String stBin;
//                String movementQtyValue;
//                InventoryMovement inventoryMovement;
//                try {
//                    subMvtTypeId = 1L;
//                    movementDocumentNo = dbPickupLine.getPickupNumber();
//                    stBin = dbPickupLine.getPickedStorageBin();
//                    movementQtyValue = "N";
//                    inventoryMovement = pickupLineService.createInventoryMovementV2(dbPickupLine, subMvtTypeId, movementDocumentNo, stBin, movementQtyValue, loginUserID);
//                    log.info("InventoryMovement created : " + inventoryMovement);
//                } catch (Exception e) {
//                    log.error("InventoryMovement create Error :" + e.toString());
//                    e.printStackTrace();
//                }
//
//                /*
//                 * ---------------------Update-OUTBOUNDLINE----------------------------------------------------
//                 */
//                //spring boot to Stored procedure null unable to pass so assigned picker is set as 0 and it is handled inside stored procedure
//                if (dbPickupLine.getAssignedPickerId() == null) {
//                    dbPickupLine.setAssignedPickerId("0");
//                }
//
//                log.info("outboundLine updated using Stored Procedure Started... ");
//                outboundLineV2Repository.updateOutboundlineStatusUpdateBagsProc(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(), dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(),
//                        dbPickupLine.getLineNumber(), dbPickupLine.getStatusId(), statusDescription, new Date(), dbPickupLine.getBagSize(), dbPickupLine.getNoBags());
//                log.info("outboundLine updated using Stored Procedure: ");
//
//                if (dbPickupLine.getReferenceField6() != null) {
//                    log.info("outboundline update ref_field_6 for Reasons");
//                    outboundLineV2Repository.updateOutboundLineV6(companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo,
//                            dbPickupLine.getItemCode(), dbPickupLine.getLineNumber(), dbPickupLine.getReferenceField6());
//                    log.info("outboundline update ref_field_6 for Reasons completed");
//                }
//
//                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProcV6(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(),
//                        dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(), dbPickupLine.getLineNumber(), headerStatusId, headerStatusDescription, loginUserID, new Date());
//                log.info("PickupHeader Updated using Stored Procedure..!");
//
//                /*
//                 * ------------------Record insertion in QUALITYHEADER table-----------------------------------
//                 * Allow to create QualityHeader only
//                 */
//                if (dbPickupLine.getStatusId().equals(57L)) {
//                    qualityHeaderService.createQualityHeaderV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, null, loginUserID);
//                }
//            }
//        }
//
//
//        /*
//         * Update OutboundHeader & Preoutbound Header STATUS_ID as 51 only if all OutboundLines are STATUS_ID is 51
//         */
//        String statusDescription50 = stagingLineV2Repository.getStatusDescription(57L, languageId);
//        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
//        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
//                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
//                loginUserID, 47L, 57L, 51L, statusDescription50, statusDescription51);
//        log.info("outboundHeader, preOutboundHeader updated as 57 / 51 when respective condition met");
//    }

//    @Async("asyncTaskExecutor")
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//    public void updateHeaderAndInventory(List<PickupLineV2> pickupLines, String loginUserID) throws Exception {
//        if (pickupLines.isEmpty()) return;
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("NAMRATHA");
//
//        String companyCodeId = pickupLines.get(0).getCompanyCodeId();
//        String plantId = pickupLines.get(0).getPlantId();
//        String languageId = pickupLines.get(0).getLanguageId();
//        String warehouseId = pickupLines.get(0).getWarehouseId();
//        String refDocNumber = pickupLines.get(0).getRefDocNumber();
//        String preOutboundNo = pickupLines.get(0).getPreOutboundNo();
//
//        boolean isStatus51 = pickupLines.stream().allMatch(line -> line.getStatusId().equals(51L));
//        Long headerStatusId = isStatus51 ? 51L : 57L;
//        String headerStatusDescription = getStatusDescription(headerStatusId, languageId);
//
//        List<OrderManagementLineV2> dbOrderManagementLineList = orderManagementLineV2Repository.getOrderManagementForPickup(companyCodeId, plantId, warehouseId, languageId, refDocNumber, preOutboundNo);
//        Set<String> dbBarcodeSet = dbOrderManagementLineList.stream()
//                .map(OrderManagementLineV2::getBarcodeId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        List<PickupLineV2> sameBarcodeList = new ArrayList<>();
//        List<PickupLineV2> differentBarcodeList = new ArrayList<>();
//        List<PickupLineV2> missingInDBList = new ArrayList<>();
//        Set<String> matchedBarcodes = new HashSet<>();
//
//        for (PickupLineV2 dbPickupLine : pickupLines) {
//            String pickupBarcode = dbPickupLine.getBarcodeId();
//            if (pickupBarcode == null) {
//                missingInDBList.add(dbPickupLine);
//            } else if (dbBarcodeSet.contains(pickupBarcode)) {
//                sameBarcodeList.add(dbPickupLine);
//                matchedBarcodes.add(pickupBarcode);
//            } else {
//                differentBarcodeList.add(dbPickupLine);
//            }
//        }
//
//        Set<String> leftoverBarcodes = new HashSet<>(dbBarcodeSet);
//        leftoverBarcodes.removeAll(matchedBarcodes);
//
//        Map<String, String> leftoverBarcodeToItemCode = dbOrderManagementLineList.stream()
//                .filter(line -> leftoverBarcodes.contains(line.getBarcodeId()))
//                .collect(Collectors.toMap(OrderManagementLineV2::getBarcodeId, OrderManagementLineV2::getItemCode));
//
//        for (PickupLineV2 line : differentBarcodeList) {
//            Optional<Map.Entry<String, String>> match = leftoverBarcodeToItemCode.entrySet().stream()
//                    .filter(entry -> entry.getValue().equals(line.getItemCode()))
//                    .findFirst();
//            match.ifPresent(entry -> {
//                line.setReferenceField3(entry.getKey());
//                leftoverBarcodeToItemCode.remove(entry.getKey());
//            });
//        }
//
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        List<PickupLineV2> allLines = new ArrayList<>();
//        allLines.addAll(sameBarcodeList);
//        allLines.addAll(differentBarcodeList);
//        allLines.addAll(missingInDBList);
//
//        log.info("sameBarcodeList ----> {}", sameBarcodeList);
//        log.info("differentBarcodeList ----> {}", differentBarcodeList);
//        log.info("missingInDBList ----> {}", missingInDBList);
//
////        List<Future<?>> futures = new ArrayList<>();
//
//        for (PickupLineV2 dbPickupLine : allLines) {
////            futures.add(executor.submit(() -> {
//                try {
//                    DataBaseContextHolder.clear();
//                    DataBaseContextHolder.setCurrentDb("NAMRATHA");
//
//                    String refField = dbPickupLine.getReferenceField3() != null ? dbPickupLine.getReferenceField3() : dbPickupLine.getReferenceField2();
//                    if (sameBarcodeList.contains(dbPickupLine)) {
//                        log.info("sameBarcodePicked");
//                        pickupLineService.modifyInventoryForMatchingBarcodeIdV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), refDocNumber, dbPickupLine, loginUserID);
//                    } else {
//                        log.info("DifferentBarcodePicked");
//                        pickupLineService.modifyInventoryForNonMatchingBarcodeIdV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), refDocNumber, refField, dbPickupLine, loginUserID);
//                    }
//
//                    InventoryMovement inventoryMovement = pickupLineService.createInventoryMovementV2(
//                            dbPickupLine, 1L, dbPickupLine.getPickupNumber(), dbPickupLine.getPickedStorageBin(), "N", loginUserID);
//                    log.info("InventoryMovement created : {}", inventoryMovement);
//
//                    if (dbPickupLine.getAssignedPickerId() == null) {
//                        dbPickupLine.setAssignedPickerId("0");
//                    }
//
//                    log.info("outboundLine updated using Stored Procedure Started.........");
//                    outboundLineV2Repository.updateOutboundlineStatusUpdateBagsProc(
//                            companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                            dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
//                            dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(), dbPickupLine.getLineNumber(),
//                            dbPickupLine.getStatusId(), getStatusDescription(dbPickupLine.getStatusId(), languageId), new Date(),
//                            dbPickupLine.getBagSize(), dbPickupLine.getNoBags());
//                    log.info("outboundLine updated using Stored Procedure Completed...... ");
//
//                    if (dbPickupLine.getReferenceField6() != null) {
//                        log.info("outboundline update ref_field_6 for Reasons Started......");
//                        outboundLineV2Repository.updateOutboundLineV6(companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo,
//                                dbPickupLine.getItemCode(), dbPickupLine.getLineNumber(), dbPickupLine.getReferenceField6());
//                        log.info("outboundline update ref_field_6 for Reasons Completed......");
//                    }
//
//                    log.info("PickupHeader Updated using Stored Procedure..!");
//                    pickupHeaderV2Repository.updatePickupheaderStatusUpdateProcV6(
//                            companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(),
//                            dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(),
//                            dbPickupLine.getLineNumber(), headerStatusId, headerStatusDescription, loginUserID, new Date());
//
//                    if (dbPickupLine.getStatusId().equals(57L)) {
//                        log.info("Quality Header Creation Started ...............");
//                        qualityHeaderService.createQualityHeaderV4(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, null, loginUserID);
//                    }
//                } catch (Exception e) {
//                    log.error("Error processing pickup line {}: {}", dbPickupLine.getLineNumber(), e.getMessage());
//                    e.printStackTrace();
//                }
////            }));
//        }
//
////        for (Future<?> f : futures) f.get();
////        executor.shutdown();
//
//        String statusDescription50 = stagingLineV2Repository.getStatusDescription(57L, languageId);
//        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
//        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
//                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
//                loginUserID, 47L, 57L, 51L, statusDescription50, statusDescription51);
//
//        log.info("outboundHeader, preOutboundHeader updated as 57 / 51 when respective condition met");
//    }

//    @Async("asyncTaskExecutor")
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//    public void updateHeaderAndInventoryV6(List<PickupLineV2> pickupLines, String loginUserID) throws Exception {
//        if (pickupLines.isEmpty()) return;
//        DataBaseContextHolder.clear();
//        DataBaseContextHolder.setCurrentDb("BP");
//
//        String companyCodeId = pickupLines.get(0).getCompanyCodeId();
//        String plantId = pickupLines.get(0).getPlantId();
//        String languageId = pickupLines.get(0).getLanguageId();
//        String warehouseId = pickupLines.get(0).getWarehouseId();
//        String refDocNumber = pickupLines.get(0).getRefDocNumber();
//        String preOutboundNo = pickupLines.get(0).getPreOutboundNo();
//
//        boolean isStatus51 = pickupLines.stream().allMatch(line -> line.getStatusId().equals(51L));
//        Long headerStatusId = isStatus51 ? 51L : 57L;
//        String headerStatusDescription = getStatusDescription(headerStatusId, languageId);
//
//        List<OrderManagementLineV2> dbOrderManagementLineList = orderManagementLineV2Repository.getOrderManagementForPickup(companyCodeId, plantId, warehouseId, languageId, refDocNumber, preOutboundNo);
//        Set<String> dbBarcodeSet = dbOrderManagementLineList.stream()
//                .map(OrderManagementLineV2::getBarcodeId)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toSet());
//
//        List<PickupLineV2> sameBarcodeList = new ArrayList<>();
//        List<PickupLineV2> differentBarcodeList = new ArrayList<>();
//        List<PickupLineV2> missingInDBList = new ArrayList<>();
//        Set<String> matchedBarcodes = new HashSet<>();
//
//        for (PickupLineV2 dbPickupLine : pickupLines) {
//            String pickupBarcode = dbPickupLine.getBarcodeId();
//            if (pickupBarcode == null) {
//                missingInDBList.add(dbPickupLine);
//            } else if (dbBarcodeSet.contains(pickupBarcode)) {
//                sameBarcodeList.add(dbPickupLine);
//                matchedBarcodes.add(pickupBarcode);
//            } else {
//                differentBarcodeList.add(dbPickupLine);
//            }
//        }
//
//        Set<String> leftoverBarcodes = new HashSet<>(dbBarcodeSet);
//        leftoverBarcodes.removeAll(matchedBarcodes);
//
//        Map<String, String> leftoverBarcodeToItemCode = dbOrderManagementLineList.stream()
//                .filter(line -> leftoverBarcodes.contains(line.getBarcodeId()))
//                .collect(Collectors.toMap(OrderManagementLineV2::getBarcodeId, OrderManagementLineV2::getItemCode));
//
//        for (PickupLineV2 line : differentBarcodeList) {
//            Optional<Map.Entry<String, String>> match = leftoverBarcodeToItemCode.entrySet().stream()
//                    .filter(entry -> entry.getValue().equals(line.getItemCode()))
//                    .findFirst();
//            match.ifPresent(entry -> {
//                line.setReferenceField3(entry.getKey());
//                leftoverBarcodeToItemCode.remove(entry.getKey());
//            });
//        }
//
//        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
//        List<PickupLineV2> allLines = new ArrayList<>();
//        allLines.addAll(sameBarcodeList);
//        allLines.addAll(differentBarcodeList);
//        allLines.addAll(missingInDBList);
//
//        log.info("sameBarcodeList ----> {}", sameBarcodeList);
//        log.info("differentBarcodeList ----> {}", differentBarcodeList);
//        log.info("missingInDBList ----> {}", missingInDBList);
//
////        List<Future<?>> futures = new ArrayList<>();
//
//        for (PickupLineV2 dbPickupLine : allLines) {
////            futures.add(executor.submit(() -> {
//            try {
//                DataBaseContextHolder.clear();
//                DataBaseContextHolder.setCurrentDb("BP");
//
//                String refField = dbPickupLine.getReferenceField3() != null ? dbPickupLine.getReferenceField3() : dbPickupLine.getReferenceField2();
//                if (sameBarcodeList.contains(dbPickupLine)) {
//                    log.info("sameBarcodePicked");
//                    pickupLineService.modifyInventoryForMatchingBarcodeIdV6(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), refDocNumber, dbPickupLine, loginUserID);
//                } else {
//                    log.info("DifferentBarcodePicked");
//                    pickupLineService.modifyInventoryForNonMatchingBarcodeIdV6(companyCodeId, plantId, languageId, warehouseId, dbPickupLine.getItemCode(), refDocNumber, refField, dbPickupLine, loginUserID);
//                }
//
//                InventoryMovement inventoryMovement = pickupLineService.createInventoryMovementV2(
//                        dbPickupLine, 1L, dbPickupLine.getPickupNumber(), dbPickupLine.getPickedStorageBin(), "N", loginUserID);
//                log.info("InventoryMovement created : {}", inventoryMovement);
//
//                if (dbPickupLine.getAssignedPickerId() == null) {
//                    dbPickupLine.setAssignedPickerId("0");
//                }
//
//                log.info("outboundLine updated using Stored Procedure Started.........");
//                outboundLineV2Repository.updateOutboundlineStatusUpdateBagsProc(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo,
//                        dbPickupLine.getItemCode(), dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(),
//                        dbPickupLine.getActualHeNo(), dbPickupLine.getAssignedPickerId(), dbPickupLine.getLineNumber(),
//                        dbPickupLine.getStatusId(), getStatusDescription(dbPickupLine.getStatusId(), languageId), new Date(),
//                        dbPickupLine.getBagSize(), dbPickupLine.getNoBags());
//                log.info("outboundLine updated using Stored Procedure Completed...... ");
//
//                if (dbPickupLine.getReferenceField6() != null) {
//                    log.info("outboundline update ref_field_6 for Reasons Started......");
//                    outboundLineV2Repository.updateOutboundLineV6(companyCodeId, plantId, warehouseId, refDocNumber, preOutboundNo,
//                            dbPickupLine.getItemCode(), dbPickupLine.getLineNumber(), dbPickupLine.getReferenceField6());
//                    log.info("outboundline update ref_field_6 for Reasons Completed......");
//                }
//
//                log.info("PickupHeader Updated using Stored Procedure..!");
//                pickupHeaderV2Repository.updatePickupheaderStatusUpdateProcV6(
//                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, dbPickupLine.getItemCode(),
//                        dbPickupLine.getManufacturerName(), dbPickupLine.getPartnerCode(), dbPickupLine.getPickupNumber(),
//                        dbPickupLine.getLineNumber(), headerStatusId, headerStatusDescription, loginUserID, new Date());
//
//                if (dbPickupLine.getStatusId().equals(57L)) {
//                    log.info("Quality Header Creation Started ...............");
//                    qualityHeaderService.createQualityHeaderV6(companyCodeId, plantId, languageId, warehouseId, dbPickupLine, null, loginUserID);
//                }
//            } catch (Exception e) {
//                log.error("Error processing pickup line {}: {}", dbPickupLine.getLineNumber(), e.getMessage());
//                e.printStackTrace();
//            }
////            }));
//        }
//
////        for (Future<?> f : futures) f.get();
////        executor.shutdown();
//
//        String statusDescription50 = stagingLineV2Repository.getStatusDescription(57L, languageId);
//        String statusDescription51 = stagingLineV2Repository.getStatusDescription(51L, languageId);
//        outboundHeaderV2Repository.updateObheaderPreobheaderUpdateProc(
//                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, new Date(),
//                loginUserID, 47L, 57L, 51L, statusDescription50, statusDescription51);
//
//        log.info("outboundHeader, preOutboundHeader updated as 57 / 51 when respective condition met");
//    }

}
