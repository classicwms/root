package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.IKeyValuePair;
import com.tekclover.wms.api.inbound.orders.model.dto.IInventory;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.IInventoryImpl;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.OutboundLine;
import com.tekclover.wms.api.inbound.orders.model.outbound.OutboundLineInterim;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement.v2.SearchOrderManagementLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.pickup.v2.SearchPickupHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.QualityLineV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.quality.v2.SearchQualityHeaderV2;
import com.tekclover.wms.api.inbound.orders.model.outbound.v2.*;
import com.tekclover.wms.api.inbound.orders.model.trans.InventoryTrans;
import com.tekclover.wms.api.inbound.orders.model.warehouse.inbound.v2.InboundOrderV2;
import com.tekclover.wms.api.inbound.orders.model.warehouse.outbound.v2.OutboundOrderV2;
import com.tekclover.wms.api.inbound.orders.repository.*;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import com.tekclover.wms.api.inbound.orders.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.tekclover.wms.api.inbound.orders.service.BaseService.PACK_BARCODE;

@Service
@Slf4j
public class OrderService {

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;

    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

    @Autowired
    UnallocatedOrderLineService unallocatedOrderLineService;

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    QualityLineV2Repository qualityLineV2Repository;

    @Autowired
    PickListHeaderRepository pickListHeaderRepository;

    @Autowired
    InventoryService inventoryService;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    StagingLineV2Repository stagingLineV2Repository;

    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;

    @Autowired
    OrderManagementHeaderV2Repository orderManagementHeaderV2Repository;

    @Autowired
    OutboundLineInterimRepository outboundLineInterimRepository;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    InventoryTransRepository inventoryTransRepository;
    @Autowired
    DbConfigRepository dbConfigRepository;

    String statusDescription = null;

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param oldPickListNumber
     * @param newPickListNumber
     * @param oldPreOutboundNo
     * @param loginUserID
     * @return
     */
    public PickListCancellation pickListCancellationNew(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                        String oldPickListNumber, String newPickListNumber, String oldPreOutboundNo, String loginUserID) {

        try {
            log.info("PickList Cancellation Initiated---> " + companyCodeId + "," + plantId + "," + languageId + "," + warehouseId + "," + oldPickListNumber + "," + oldPreOutboundNo);
            //Delete OutBoundHeader
            OutboundHeaderV2 outboundHeaderV2 = getPLCOutBoundHeader(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete OutBoundLine
            List<OutboundLineV2> outboundLineV2 = getPLCOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete PreOutboundLine
            List<PreOutboundLineV2> preOutboundLineV2List = getPLCPreOutBoundLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //DeleteOrderManagementLine
            List<OrderManagementLineV2> orderManagementLine = getPLCOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Delete PickUpLine
            List<PickupLineV2> pickupLineV2 = getPLCPickUpLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            //Quality Line
            List<QualityLineV2> qualityLineV2 = getPLCQualityLine(companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo);

            List<String> pickuplineItemCodeMfrNameList = new ArrayList<>();
            List<String> pickuplineLineNoItemCodeMfrNameList = new ArrayList<>();
            if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
                for (PickupLineV2 pickupLine : pickupLineV2) {
                    pickuplineItemCodeMfrNameList.add(pickupLine.getItemCode() + pickupLine.getManufacturerName());
                    InventoryV2 inventory = inventoryService.getInventoryV2(pickupLine.getCompanyCodeId(), pickupLine.getPlantId(), pickupLine.getLanguageId(),
                            pickupLine.getWarehouseId(), pickupLine.getPickedPackCode(), pickupLine.getItemCode(), pickupLine.getPickedStorageBin(), pickupLine.getManufacturerName());

                    List<PickupLineV2> filteredList = pickupLineV2.stream().filter(a -> a.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                            a.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                            a.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                    List<PreOutboundLineV2> filteredPreOutboundLineList = preOutboundLineV2List.stream().filter(n -> n.getItemCode().equalsIgnoreCase(pickupLine.getItemCode()) &&
                            n.getManufacturerName().equalsIgnoreCase(pickupLine.getManufacturerName()) &&
                            n.getLineNumber().equals(pickupLine.getLineNumber())).collect(Collectors.toList());
                    Double PICK_CNF_QTY = 0D;
                    Double ORD_QTY = 0D;
                    if (filteredList != null && !filteredList.isEmpty()) {
                        PICK_CNF_QTY = filteredList.stream().mapToDouble(a -> a.getPickConfirmQty()).sum();
                    }
                    if (filteredPreOutboundLineList != null && !filteredPreOutboundLineList.isEmpty()) {
                        ORD_QTY = filteredPreOutboundLineList.stream().mapToDouble(n -> n.getOrderQty()).sum();
                    }
                    boolean equalQty = PICK_CNF_QTY.equals(ORD_QTY);
                    log.info("PICK_CNF_QTY, ORD_TY, EqualQty Condition: " + PICK_CNF_QTY + ", " + ORD_QTY + ", " + equalQty);
                    if (equalQty) {
                        if (inventory != null) {
                            Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (pickupLine.getPickConfirmQty() != null ? pickupLine.getPickConfirmQty() : 0);
                            if (INV_QTY < 0) {
                                log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                INV_QTY = 0D;
                            }
                            inventory.setInventoryQuantity(INV_QTY);
                            Double ALLOC_QTY = 0D;
                            if (inventory.getAllocatedQuantity() != null) {
                                ALLOC_QTY = inventory.getAllocatedQuantity();
                            }
                            Double TOT_QTY = INV_QTY + ALLOC_QTY;
                            inventory.setReferenceField4(TOT_QTY);              //Total Qty

                            InventoryV2 newInventoryV2 = new InventoryV2();
                            BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                            newInventoryV2.setUpdatedOn(new Date());
                            try {
                                InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                                log.info("InventoryV2 created : " + updateInventoryV2);
                            } catch (Exception e) {
                                log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                e.printStackTrace();
                                InventoryTrans newInventoryTrans = new InventoryTrans();
                                BeanUtils.copyProperties(newInventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(newInventoryV2));
                                newInventoryTrans.setReRun(0L);
                                InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                            }

                        }
                    }
                    if (!equalQty) {
                        String itmMfrNameLineNo = pickupLine.getItemCode() + pickupLine.getManufacturerName() + pickupLine.getLineNumber();
                        List<String> filterList = pickuplineLineNoItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrNameLineNo)).collect(Collectors.toList());
                        if (filterList.size() == 0) {
                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + ORD_QTY;
                                if (INV_QTY < 0) {
                                    log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                    INV_QTY = 0D;
                                }
                                inventory.setInventoryQuantity(INV_QTY);
                                Double ALLOC_QTY = 0D;
                                if (inventory.getAllocatedQuantity() != null) {
                                    ALLOC_QTY = inventory.getAllocatedQuantity() - (ORD_QTY - PICK_CNF_QTY);
                                }
                                if (ALLOC_QTY < 0) {
                                    log.info("allocated qty calculated is less than 0: " + ALLOC_QTY);
                                    ALLOC_QTY = 0D;
                                }
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                inventory.setReferenceField4(TOT_QTY);              //Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                                newInventoryV2.setUpdatedOn(new Date());
                                try {
                                    InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                                    log.info("InventoryV2 created : " + updateInventoryV2);
                                } catch (Exception e) {
                                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                    e.printStackTrace();
                                    InventoryTrans newInventoryTrans = new InventoryTrans();
                                    BeanUtils.copyProperties(newInventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(newInventoryV2));
                                    newInventoryTrans.setReRun(0L);
                                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                                }
                            }
                            pickuplineLineNoItemCodeMfrNameList.add(itmMfrNameLineNo);
                        }
                    }
                }
            }

            if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
                log.info("Inventory update for OrderManagementLine");
                log.info("Pickupline ItmCodeMfrName: " + pickuplineItemCodeMfrNameList);
                for (OrderManagementLineV2 dbOrderManagementLine : orderManagementLine) {
                    if (dbOrderManagementLine.getStatusId() != 47L) {
                        if (dbOrderManagementLine.getProposedStorageBin() == null || dbOrderManagementLine.getProposedStorageBin().equalsIgnoreCase("")) {
                            throw new BadRequestException("OrderManagementLine ProposedStorageBin is Empty, hence inventory cannot be reversed - PickList Cancellation Aborting");
                        }
                        String itmMfrName = dbOrderManagementLine.getItemCode() + dbOrderManagementLine.getManufacturerName();
                        log.info("ItmMfrName: " + itmMfrName);
                        List<String> itmPresent = pickuplineItemCodeMfrNameList.stream().filter(a -> a.equalsIgnoreCase(itmMfrName)).collect(Collectors.toList());
                        log.info("itmPresent: " + itmPresent);
                        if (itmPresent.size() == 0) {
                            InventoryV2 inventory = inventoryService.getInventoryV2(dbOrderManagementLine.getCompanyCodeId(), dbOrderManagementLine.getPlantId(), dbOrderManagementLine.getLanguageId(),
                                    dbOrderManagementLine.getWarehouseId(), dbOrderManagementLine.getProposedPackBarCode(), dbOrderManagementLine.getItemCode(),
                                    dbOrderManagementLine.getProposedStorageBin(), dbOrderManagementLine.getManufacturerName());

                            if (inventory != null) {
                                Double INV_QTY = (inventory.getInventoryQuantity() != null ? inventory.getInventoryQuantity() : 0) + (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                                if (INV_QTY < 0) {
                                    log.info("inventory qty calculated is less than 0: " + INV_QTY);
                                    INV_QTY = 0D;
                                }
                                inventory.setInventoryQuantity(INV_QTY);
                                Double ALLOC_QTY = (inventory.getAllocatedQuantity() != null ? inventory.getAllocatedQuantity() : 0) - (dbOrderManagementLine.getAllocatedQty() != null ? dbOrderManagementLine.getAllocatedQty() : 0);
                                if (ALLOC_QTY < 0) {
                                    ALLOC_QTY = 0D;
                                }
                                Double TOT_QTY = INV_QTY + ALLOC_QTY;
                                inventory.setAllocatedQuantity(ALLOC_QTY);
                                inventory.setReferenceField4(TOT_QTY);              //Total Qty

                                InventoryV2 newInventoryV2 = new InventoryV2();
                                BeanUtils.copyProperties(inventory, newInventoryV2, CommonUtils.getNullPropertyNames(inventory));
                                newInventoryV2.setUpdatedOn(new Date());
                                try {
                                    InventoryV2 updateInventoryV2 = inventoryV2Repository.save(newInventoryV2);
                                    log.info("InventoryV2 created : " + updateInventoryV2);
                                } catch (Exception e) {
                                    log.error("--ERROR--updateInventoryV3----level1--inventory--error----> :" + e.toString());
                                    e.printStackTrace();
                                    InventoryTrans newInventoryTrans = new InventoryTrans();
                                    BeanUtils.copyProperties(newInventoryV2, newInventoryTrans, CommonUtils.getNullPropertyNames(newInventoryV2));
                                    newInventoryTrans.setReRun(0L);
                                    InventoryTrans inventoryTransCreated = inventoryTransRepository.save(newInventoryTrans);
                                    log.error("inventoryTransCreated -------- :" + inventoryTransCreated);
                                }
                            }
                        }
                    }
                }
            }

            PickListCancellation pickListCancellation = new PickListCancellation();
            if (pickupLineV2 != null && !pickupLineV2.isEmpty()) {
                pickListCancellation.setOldPickupLineList(pickupLineV2);
            }
            if (qualityLineV2 != null && !qualityLineV2.isEmpty()) {
                pickListCancellation.setOldQualityLineList(qualityLineV2);
            }
            if (outboundHeaderV2 != null) {
                pickListCancellation.setOldOutboundHeader(outboundHeaderV2);
            }
            if (outboundLineV2 != null && !outboundLineV2.isEmpty()) {
                pickListCancellation.setOldOutboundLineList(outboundLineV2);
            }
            if (orderManagementLine != null && !orderManagementLine.isEmpty()) {
                pickListCancellation.setOldOrderManagementLineList(orderManagementLine);
            }
            pickListCancellation.setOldPickListNumber(oldPickListNumber);
            pickListCancellation.setNewPickListNumber(newPickListNumber);

            //stored procedure to update deletionIndicator Flag
            pickListHeaderRepository.updateDeletionIndicatorPickListCancellationProc(
                    companyCodeId, plantId, languageId, warehouseId, oldPickListNumber, oldPreOutboundNo, loginUserID, new Date());
            log.info("Pick List cancellation - stored procedure update - deletion indicator finished processing");

            return pickListCancellation;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException("Exception : " + e);
        }
    }

    /**
     * @param newPickupHeader
     * @param loginUserID
     * @return
     * @throws Exception
     */
    public PickupHeaderV2 createOutboundOrderProcessingPickupHeaderV2(PickupHeaderV2 newPickupHeader, String loginUserID) throws Exception {
        try {
            PickupHeaderV2 dbPickupHeader = new PickupHeaderV2();
            log.info("newPickupHeader : " + newPickupHeader);
            BeanUtils.copyProperties(newPickupHeader, dbPickupHeader, CommonUtils.getNullPropertyNames(newPickupHeader));

            IKeyValuePair description = stagingLineV2Repository.getDescription(dbPickupHeader.getCompanyCodeId(),
                    dbPickupHeader.getLanguageId(),
                    dbPickupHeader.getPlantId(),
                    dbPickupHeader.getWarehouseId());

            if (dbPickupHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbPickupHeader.getStatusId(), dbPickupHeader.getLanguageId());
                dbPickupHeader.setStatusDescription(statusDescription);
            }

            dbPickupHeader.setCompanyDescription(description.getCompanyDesc());
            dbPickupHeader.setPlantDescription(description.getPlantDesc());
            dbPickupHeader.setWarehouseDescription(description.getWarehouseDesc());

            statusDescription = stagingLineV2Repository.getStatusDescription(48L, dbPickupHeader.getLanguageId());
            outboundLineV2Repository.updateOutboundLineV2(dbPickupHeader.getCompanyCodeId(),
                    dbPickupHeader.getPlantId(),
                    dbPickupHeader.getLanguageId(),
                    dbPickupHeader.getWarehouseId(),
                    dbPickupHeader.getPreOutboundNo(),
                    dbPickupHeader.getRefDocNumber(),
                    dbPickupHeader.getPartnerCode(),
                    dbPickupHeader.getLineNumber(),
                    dbPickupHeader.getItemCode(),
                    48L,
                    statusDescription,
                    dbPickupHeader.getAssignedPickerId(),
                    dbPickupHeader.getManufacturerName(),
                    loginUserID,
                    new Date());

            String customerName = getCustomerName(dbPickupHeader.getCompanyCodeId(), dbPickupHeader.getPlantId(),
                    dbPickupHeader.getLanguageId(), dbPickupHeader.getWarehouseId(),
                    dbPickupHeader.getCustomerCode());
            if (customerName != null) {
                dbPickupHeader.setCustomerName(customerName);
            }
            dbPickupHeader.setDeletionIndicator(0L);
            dbPickupHeader.setPickupCreatedBy(loginUserID);
            dbPickupHeader.setPickupCreatedOn(new Date());
            PickupHeaderV2 pickupHeaderV2 = pickupHeaderV2Repository.save(dbPickupHeader);

            return pickupHeaderV2;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineForPickupLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String preOutboundNo, String refDocNumber) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        return null;
    }

    /**
     * picklistcancel
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public OutboundHeaderV2 getPLCOutBoundHeader(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String preOutboundNo) {

        OutboundHeaderV2 outboundHeaderV2 = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OutboundHeader : " + outboundHeaderV2);
        return outboundHeaderV2;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<OutboundLineV2> getPLCOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                   String warehouseId, String refDocNumber, String preOutboundNo) {
        List<OutboundLineV2> outboundLineV2List = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OutboundLine : " + outboundLineV2List);
        return outboundLineV2List;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<PreOutboundLineV2> getPLCPreOutBoundLine(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String refDocNumber, String preOutboundNo) {
        List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - PreOutboundLine : " + preOutboundLineV2List);
        return preOutboundLineV2List;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<OrderManagementLineV2> getPLCOrderManagementLineV2(String companyCodeId, String plantId, String languageId,
                                                                   String warehouseId, String refDocNumber, String preOutboundNo) {
        List<OrderManagementLineV2> orderManagementLine =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<PickupLineV2> getPLCPickUpLine(String companyCodeId, String plantId, String languageId,
                                               String warehouseId, String refDocNumber, String preOutboundNo) {
        List<PickupLineV2> dbPickUpLine = pickupLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - PickupLine : " + dbPickUpLine);
        return dbPickUpLine;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<QualityLineV2> getPLCQualityLine(String companyCodeId, String plantId, String languageId,
                                                 String warehouseId, String refDocNumber, String preOutboundNo) {
        List<QualityLineV2> dbQualityLineList = qualityLineV2Repository.findByCompanyCodeIdAndLanguageIdAndPlantIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                companyCodeId, languageId, plantId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("PickList Cancellation - QualityLine : " + dbQualityLineList);
        return dbQualityLineList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param outboundOrderTypeId
     * @return
     */
    public PickupHeaderV2 getPickupHeaderAutomationByOutboundOrderTypeId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, Long outboundOrderTypeId) {
        PickupHeaderV2 header =
                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndOutboundOrderTypeIdAndDeletionIndicatorOrderByPickupCreatedOn(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 48L, outboundOrderTypeId, 0L);
        return header;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @param refDocNumber
     * @return
     * @throws java.text.ParseException
     */
    public PickupHeaderV2 getPickupHeaderAutomateCurrentDateSameOrderNew(String companyCodeId, String plantId, String languageId,
                                                                         String warehouseId, String assignedPickerId, String refDocNumber)
            throws java.text.ParseException {

        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());
        PickupHeaderV2 header =
                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndRefDocNumberAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, refDocNumber, 48L, dates[0], dates[1], 0L);
        return header;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @return
     */
    public List<PickupHeaderV2> getPickupHeaderAutomationWithOutStatusIdV2(String companyCodeId, String plantId, String languageId,
                                                                           String warehouseId, String assignedPickerId) {
        List<PickupHeaderV2> header =
                pickupHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, 0L);
        return header;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param assignedPickerId
     * @param statusId
     * @return
     * @throws java.text.ParseException
     */
    public List<PickupHeaderV2> getPickupHeaderAutomation(String companyCodeId, String plantId, String languageId, String warehouseId, String assignedPickerId, Long statusId) throws java.text.ParseException {
        Date[] dates = DateUtils.addTimeToDatesForSearch(new Date(), new Date());
        List<PickupHeaderV2> header =
                pickupHeaderV2Repository.findAllByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndAssignedPickerIdAndStatusIdAndPickupCreatedOnBetweenAndDeletionIndicatorOrderByPickupCreatedOn(
                        companyCodeId, plantId, languageId, warehouseId, assignedPickerId, statusId, dates[0], dates[1], 0L);
        return header;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param levelId
     * @return
     */
    public PickupHeaderV2 getPickupHeaderAutomationByLevelId(String companyCodeId, String plantId, String languageId, String warehouseId, String itemCode, String manufacturerName, String levelId) {
        PickupHeaderV2 header =
                pickupHeaderV2Repository.findTopByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndManufacturerNameAndStatusIdAndLevelIdAndDeletionIndicatorOrderByPickupCreatedOn(
                        companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, 48L, levelId, 0L);
        return header;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<PickupHeaderV2> getPickupHeaderForPickListCancellationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo) {
        List<PickupHeaderV2> pickupHeader =
                pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        log.info("New Picklist ---> pickup header : " + pickupHeader);
        if (pickupHeader != null && !pickupHeader.isEmpty()) {
            return pickupHeader;
        }
        log.info("No Pickup Header for New Picklist");
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @param preOutboundNo
     * @return
     */
    public List<QualityHeaderV2> getQualityHeaderForPickListCancellationV2(String companyCodeId, String plantId, String languageId, String warehouseId, String refDocNumber, String preOutboundNo) {
        List<QualityHeaderV2> qualityHeader =
                qualityHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndPreOutboundNoAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 0L);
        if (qualityHeader != null && !qualityHeader.isEmpty()) {
            log.info("Quality Header: " + qualityHeader);
            return qualityHeader;
        }
        log.info("The given picklist ID : " + refDocNumber + " doesn't exist.");
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param refDocNumber
     * @param warehouseId
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String refDocNumber, String warehouseId) {
        OutboundHeaderV2 outboundHeader = outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndRefDocNumberAndWarehouseIdAndDeletionIndicator(
                companyCodeId, plantId, languageId, refDocNumber, warehouseId, 0L);
        return outboundHeader;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId,
                                                  String warehouseId, String refDocNumber) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        return outboundLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public List<OutboundLineV2> getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                  String preOutboundNo, String refDocNumber, String partnerCode) {
        List<OutboundLineV2> outboundLine =
                outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundLine != null) {
            return outboundLine;
        } else {
            throw new BadRequestException("The given OutboundLine ID : "
                    + "companyCodeId : " + companyCodeId
                    + "plantId : " + plantId
                    + "languageId : " + languageId
                    + "warehouseId : " + warehouseId
                    + ", preOutboundNo : " + preOutboundNo
                    + ", refDocNumber : " + refDocNumber
                    + ", partnerCode : " + partnerCode
                    + " doesn't exist.");
        }
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param updateOutboundHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public OutboundHeaderV2 updateOutboundHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                   String preOutboundNo, String refDocNumber, String partnerCode,
                                                   OutboundHeaderV2 updateOutboundHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        OutboundHeaderV2 dbOutboundHeader = getOutboundHeaderForUpdateV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode);
        Date ctdOn = dbOutboundHeader.getCreatedOn();
        Date refDocDate = dbOutboundHeader.getRefDocDate();
//        log.info("dbOutboundHeader for updating status 57: " + dbOutboundHeader);
        if (dbOutboundHeader != null) {
            BeanUtils.copyProperties(updateOutboundHeader, dbOutboundHeader, CommonUtils.getNullPropertyNames(updateOutboundHeader));
            dbOutboundHeader.setUpdatedBy(loginUserID);
            dbOutboundHeader.setUpdatedOn(new Date());
            if (dbOutboundHeader.getStatusId() != null) {
                statusDescription = stagingLineV2Repository.getStatusDescription(dbOutboundHeader.getStatusId(), dbOutboundHeader.getLanguageId());
                dbOutboundHeader.setStatusDescription(statusDescription);
            }
            log.info("dbOutboundHeader.getCreatedOn(), ref_doc_date :--->" + ctdOn + ", " + refDocDate);
            dbOutboundHeader.setCreatedOn(ctdOn);
            dbOutboundHeader.setRefDocDate(refDocDate);
            return outboundHeaderV2Repository.save(dbOutboundHeader);
        }
        return null;
    }


    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @return
     */
    public OutboundHeaderV2 getOutboundHeaderForUpdateV2(String companyCodeId, String plantId, String languageId,
                                                         String warehouseId, String preOutboundNo, String refDocNumber, String partnerCode) {
        OutboundHeaderV2 outboundHeader =
                outboundHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, 0L);
        if (outboundHeader != null) {
            return outboundHeader;
        }
        throw new BadRequestException("The given OutboundHeader ID : " +
                "companyCodeId : " + companyCodeId +
                "plantId : " + plantId +
                "languageId : " + languageId +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForLineUpdateV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                       String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode) {
        List<OrderManagementLineV2> orderManagementHeader = orderManagementLineV2Repository
                .findByPlantIdAndCompanyCodeIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
                        plantId, companyCodeId, languageId, warehouseId, preOutboundNo, refDocNumber, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null && !orderManagementHeader.isEmpty()) {
            return orderManagementHeader.get(0);
        } else {
            return null;
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
     * @param pickupNumber
     * @return
     */
    public PickupHeaderV2 getPickupHeaderV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                            String preOutboundNo, String refDocNumber, String partnerCode, String pickupNumber) {
        PickupHeaderV2 pickupHeader =
                pickupHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndPickupNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, pickupNumber, 0L);
        if (pickupHeader != null && pickupHeader.getDeletionIndicator() == 0) {
            return pickupHeader;
        }
        throw new BadRequestException("The given PickupHeader ID : " +
                "warehouseId : " + warehouseId +
                ",preOutboundNo : " + preOutboundNo +
                ",refDocNumber : " + refDocNumber +
                ",partnerCode : " + partnerCode +
                ",pickupNumber : " + pickupNumber +
                " doesn't exist.");
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param lineNumber
     * @param itemCode
     * @return
     */
    public OrderManagementLineV2 getOrderManagementLineForQualityLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String preOutboundNo, String refDocNumber, Long lineNumber, String itemCode) {
        OrderManagementLineV2 orderManagementHeader = orderManagementLineV2Repository
                .findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndLineNumberAndItemCodeAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, lineNumber, itemCode, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        } else {
            return null;
        }
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param refDocNumber
     * @return
     */
    public List<OrderManagementLineV2> getOrderManagementLineForPickListCancellationV2(String companyCodeId, String plantId, String languageId,
                                                                                       String warehouseId, String refDocNumber) {
        List<OrderManagementLineV2> orderManagementLineList =
                orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, refDocNumber, 0L);
        log.info("PickList Cancellation - OrderManagementLine : " + orderManagementLineList);
        return orderManagementLineList;
    }

    /**
     * @param newQualityHeader
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public QualityHeaderV2 createQualityHeaderV2(QualityHeaderV2 newQualityHeader, String loginUserID)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        QualityHeaderV2 dbQualityHeader = new QualityHeaderV2();
        log.info("newQualityHeader : " + newQualityHeader + "; - " + loginUserID);
        BeanUtils.copyProperties(newQualityHeader, dbQualityHeader, CommonUtils.getNullPropertyNames(newQualityHeader));

        IKeyValuePair description = stagingLineV2Repository.getDescription(dbQualityHeader.getCompanyCodeId(),
                dbQualityHeader.getLanguageId(),
                dbQualityHeader.getPlantId(),
                dbQualityHeader.getWarehouseId());

        OrderManagementHeaderV2 orderManagementHeaderV2 = getOrderManagementHeaderV2(newQualityHeader.getCompanyCodeId(),
                newQualityHeader.getPlantId(), newQualityHeader.getLanguageId(), newQualityHeader.getWarehouseId(),
                newQualityHeader.getPreOutboundNo(), newQualityHeader.getRefDocNumber());
        if (orderManagementHeaderV2 != null) {
            dbQualityHeader.setMiddlewareId(orderManagementHeaderV2.getMiddlewareId());
            dbQualityHeader.setMiddlewareTable(orderManagementHeaderV2.getMiddlewareTable());
            dbQualityHeader.setReferenceDocumentType(orderManagementHeaderV2.getReferenceDocumentType());
        }

        if (dbQualityHeader.getStatusId() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(dbQualityHeader.getStatusId(), dbQualityHeader.getLanguageId());
            dbQualityHeader.setStatusDescription(statusDescription);
        }

        dbQualityHeader.setCompanyDescription(description.getCompanyDesc());
        dbQualityHeader.setPlantDescription(description.getPlantDesc());
        dbQualityHeader.setWarehouseDescription(description.getWarehouseDesc());

        dbQualityHeader.setDeletionIndicator(0L);
        dbQualityHeader.setQualityCreatedBy(loginUserID);
        dbQualityHeader.setQualityUpdatedBy(loginUserID);
        dbQualityHeader.setQualityCreatedOn(new Date());
        dbQualityHeader.setQualityUpdatedOn(new Date());
        return qualityHeaderV2Repository.save(dbQualityHeader);
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @return
     */
    public OrderManagementHeaderV2 getOrderManagementHeaderV2(String companyCodeId, String plantId, String languageId,
                                                              String warehouseId, String preOutboundNo,
                                                              String refDocNumber) {
        OrderManagementHeaderV2 orderManagementHeader =
                orderManagementHeaderV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndDeletionIndicator(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, 0L);
        if (orderManagementHeader != null) {
            return orderManagementHeader;
        }
        return null;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param customerCode
     * @return
     */
    public String getCustomerName(String companyCodeId, String plantId, String languageId, String warehouseId, String customerCode) {
        return stagingLineV2Repository.getCustomerName(companyCodeId, plantId, languageId, warehouseId, customerCode);
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
     * @param qualityInspectionNo
     * @param itemCode
     * @param manufacturerName
     * @return
     */
    public QualityLineV2 findDuplicateRecordV2(String companyCodeId, String plantId, String languageId, String warehouseId,
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
     * @param updateOutboundLine
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws java.text.ParseException
     */
    public OutboundLineV2 updateOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo, String refDocNumber,
                                               String partnerCode, Long lineNumber, String itemCode, String loginUserID, OutboundLineV2 updateOutboundLine)
            throws IllegalAccessException, InvocationTargetException, java.text.ParseException {
        OutboundLineV2 outboundLine = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, updateOutboundLine.getManufacturerName());
        BeanUtils.copyProperties(updateOutboundLine, outboundLine, CommonUtils.getNullPropertyNames(updateOutboundLine));
        outboundLine.setUpdatedBy(loginUserID);
        outboundLine.setUpdatedOn(new Date());

        if (updateOutboundLine != null && updateOutboundLine.getStatus() != null) {
            statusDescription = stagingLineV2Repository.getStatusDescription(updateOutboundLine.getStatusId(), languageId);
            outboundLine.setStatusDescription(statusDescription);
        }

        List<OutboundLineV2> outboundLineList = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
        List<OutboundLineV2> outboundLineListStatus90 = null;
        if (outboundLineList != null && !outboundLineList.isEmpty()) {
            log.info("outboundLineList : " + outboundLineList);
            Long outboundLineCount = outboundLineList.stream().count();
            log.info("outboundLine count : " + outboundLineCount);
            outboundLineListStatus90 = outboundLineList.stream().filter(a -> a.getStatusId() == 90L).collect(Collectors.toList());
            Long outboundLineCountStatus90 = outboundLineListStatus90.stream().count();
            log.info("outboundLineListCount : " + outboundLineCountStatus90);
            if (outboundLineCount.equals(outboundLineCountStatus90)) {
                OutboundHeaderV2 outboundHeader = getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber);
                if (outboundHeader != null) {
                    outboundHeader.setStatusId(90L);
                    statusDescription = stagingLineV2Repository.getStatusDescription(90L, languageId);
                    outboundHeader.setStatusDescription(statusDescription);
                    outboundHeaderV2Repository.save(outboundHeader);
                    log.info("Outbound Header Updated to Status 90: " + outboundHeader);
                }
            }
        }

        outboundLineV2Repository.save(outboundLine);
        return outboundLine;
    }


    public OutboundLineV2 getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId, String preOutboundNo,
                                            String refDocNumber, String partnerCode, Long lineNumber, String itemCode, String manufacturerName) {
        OutboundLineV2 outboundLine = outboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndLineNumberAndItemCodeAndManufacturerNameAndDeletionIndicator(
                companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, lineNumber, itemCode, manufacturerName, 0L);
        if (outboundLine != null) {
            return outboundLine;
        }
        throw new BadRequestException("The given OutboundLine ID : " +
                "companyCodeId:" + companyCodeId +
                "plantId:" + plantId +
                "languageId:" + languageId +
                "warehouseId:" + warehouseId +
                ",preOutboundNo:" + preOutboundNo +
                ",refDocNumber:" + refDocNumber +
                ",partnerCode:" + partnerCode +
                ",lineNumber:" + lineNumber +
                ",itemCode:" + itemCode +
                ",manufacturerName:" + manufacturerName +
                " doesn't exist.");
    }

    /**
     * @param dbQualityLine
     * @throws java.text.ParseException
     */
    public void createOutboundLineInterimV2(QualityLineV2 dbQualityLine) throws java.text.ParseException {
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
     * @param dbQualityLines
     * @param loginUserID
     */
    public void postDeliveryConfirm(List<QualityLineV2> dbQualityLines, String loginUserID) {
        try {
            log.info("Delivery Confirm check: -------> started");

            List<String> companyCodeId = dbQualityLines.stream().map(QualityLineV2::getCompanyCodeId).distinct().collect(Collectors.toList());
            List<String> plantId = dbQualityLines.stream().map(QualityLineV2::getPlantId).distinct().collect(Collectors.toList());
            List<String> languageId = dbQualityLines.stream().map(QualityLineV2::getLanguageId).distinct().collect(Collectors.toList());
            List<String> warehouseId = dbQualityLines.stream().map(QualityLineV2::getWarehouseId).distinct().collect(Collectors.toList());
            List<String> refDocNumber = dbQualityLines.stream().map(QualityLineV2::getRefDocNumber).distinct().collect(Collectors.toList());
            List<String> preOutboundNo = dbQualityLines.stream().map(QualityLineV2::getPreOutboundNo).distinct().collect(Collectors.toList());

            Long[] statusId = new Long[]{42L, 43L};

            SearchOrderManagementLineV2 searchOrderManagementLine = new SearchOrderManagementLineV2();
            searchOrderManagementLine.setStatusId(List.of(statusId));
            searchOrderManagementLine.setCompanyCodeId(companyCodeId);
            searchOrderManagementLine.setPlantId(plantId);
            searchOrderManagementLine.setLanguageId(languageId);
            searchOrderManagementLine.setWarehouseId(warehouseId);

            searchOrderManagementLine.setRefDocNumber(refDocNumber);
            searchOrderManagementLine.setPreOutboundNo(preOutboundNo);

            List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.findOrderManagementLineV2(searchOrderManagementLine).collect(Collectors.toList());
            log.info("orderManagementLineList statusId [42,43]----------->: " + orderManagementLineList.stream().count());

            SearchPickupHeaderV2 searchPickupHeader = new SearchPickupHeaderV2();
            statusId = new Long[]{48L};
            searchPickupHeader.setStatusId(List.of(statusId));
            searchPickupHeader.setCompanyCodeId(companyCodeId);
            searchPickupHeader.setPlantId(plantId);
            searchPickupHeader.setLanguageId(languageId);
            searchPickupHeader.setWarehouseId(warehouseId);

            searchPickupHeader.setRefDocNumber(refDocNumber);
            searchPickupHeader.setPreOutboundNo(preOutboundNo);

            List<PickupHeaderV2> pickupHeaderList = orderManagementLineService.findPickupHeaderV2(searchPickupHeader).collect(Collectors.toList());
            log.info("pickupHeaderList statusId [48]----------->: " + pickupHeaderList.stream().count());

            SearchQualityHeaderV2 searchQualityHeader = new SearchQualityHeaderV2();
            statusId = new Long[]{54L};
            searchQualityHeader.setStatusId(List.of(statusId));
            searchQualityHeader.setCompanyCodeId(companyCodeId);
            searchQualityHeader.setPlantId(plantId);
            searchQualityHeader.setLanguageId(languageId);
            searchQualityHeader.setWarehouseId(warehouseId);

            searchQualityHeader.setRefDocNumber(refDocNumber);
            searchQualityHeader.setPreOutboundNo(preOutboundNo);

            List<QualityHeaderV2> qualityHeaderList = orderManagementLineService.findQualityHeaderNewV2(searchQualityHeader).collect(Collectors.toList());
            log.info("qualityHeaderList statusId [54]----------->: " + qualityHeaderList.stream().count());

            if ((orderManagementLineList == null || orderManagementLineList.isEmpty()) &&
                    (pickupHeaderList == null || pickupHeaderList.isEmpty()) &&
                    (qualityHeaderList == null || qualityHeaderList.isEmpty())) {

                Long outboundLineCount = orderManagementLineService.getOutboundLineCountV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
                log.info("OuboundLine count :----------->" + outboundLineCount);
                Long count_57 = 0L;
//                if (outboundLineV2List != null && !outboundLineV2List.isEmpty()) {
                if (outboundLineCount != null && outboundLineCount > 0) {
                    List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L);
                    count_57 = orderManagementLineService.getOutboundLineStatusIdCountV2(
                            companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, statusIdsToBeChecked);

                    log.info("Count_57, OutboundLineList Size: " + count_57 + ", " + outboundLineCount);

                    if (count_57.equals(outboundLineCount)) {
                        log.info("All Outbound Lines Confirmed - Automate/Calling the Delivery Confirm Procedure");

                        SearchOutboundHeaderV2 searchOutboundHeaderV2 = new SearchOutboundHeaderV2();
                        searchOutboundHeaderV2.setCompanyCodeId(companyCodeId);
                        searchOutboundHeaderV2.setPlantId(plantId);
                        searchOutboundHeaderV2.setLanguageId(languageId);
                        searchOutboundHeaderV2.setWarehouseId(warehouseId);

                        searchOutboundHeaderV2.setRefDocNumber(refDocNumber);
                        searchOutboundHeaderV2.setPreOutboundNo(preOutboundNo);

                        List<OutboundHeaderV2Stream> outboundHeaderV2List = orderManagementLineService.findOutboundHeadernewV2(searchOutboundHeaderV2);
                        log.info("outboundHeaderV2List ----------->: " + outboundHeaderV2List.stream().count());

                        for (OutboundHeaderV2Stream dbOutboundHeader : outboundHeaderV2List) {
                            SearchOutboundLineV2 searchOutboundLineV2 = new SearchOutboundLineV2();
                            searchOutboundLineV2.setCompanyCodeId(List.of(dbOutboundHeader.getCompanyCodeId()));
                            searchOutboundLineV2.setPlantId(List.of(dbOutboundHeader.getPlantId()));
                            searchOutboundLineV2.setLanguageId(List.of(dbOutboundHeader.getLanguageId()));
                            searchOutboundLineV2.setWarehouseId(List.of(dbOutboundHeader.getWarehouseId()));

                            searchOutboundLineV2.setRefDocNumber(List.of(dbOutboundHeader.getRefDocNumber()));
                            searchOutboundLineV2.setPreOutboundNo(List.of(dbOutboundHeader.getPreOutboundNo()));
                            List<OutboundLineOutput> outboundLineV2s = orderManagementLineService.findOutboundLineNewV2(searchOutboundLineV2);
                            log.info("outboundLineV2s ----------->: " + outboundLineV2s.stream().count());

                            List<OutboundLineV2> updatedOutboundLinesV2 = orderManagementLineService.updateOutboundLinesV2(loginUserID, outboundLineV2s);
                            log.info("updatedOutboundLinesV2 ----------->: " + updatedOutboundLinesV2.stream().count());
                            log.info("updatedOutboundLinesV2 ----------->: " + updatedOutboundLinesV2);

                            if (updatedOutboundLinesV2 != null) {
                                log.info("Initiating deliveryConfirm ----------->: " + updatedOutboundLinesV2);
                                List<OutboundLineV2> deliveryConfirm = deliveryConfirmationV2(
                                        updatedOutboundLinesV2.get(0).getCompanyCodeId(), updatedOutboundLinesV2.get(0).getPlantId(),
                                        updatedOutboundLinesV2.get(0).getLanguageId(), updatedOutboundLinesV2.get(0).getWarehouseId(),
                                        updatedOutboundLinesV2.get(0).getPreOutboundNo(), updatedOutboundLinesV2.get(0).getRefDocNumber(),
                                        updatedOutboundLinesV2.get(0).getPartnerCode(), loginUserID);
                            }
                            log.info("<------------------Delivery Confirm Finished Processing------------------>");
                        }
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            log.info("outboundLine delivery confirm error: " + e1.toString());
        }
    }



    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param loginUserID
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public List<OutboundLineV2> deliveryConfirmationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                       String preOutboundNo, String refDocNumber, String partnerCode, String loginUserID)
            throws IllegalAccessException, InvocationTargetException {
        /*--------------------OutboundLine-Check---------------------------------------------------------------------------*/
        List<Long> statusIds = Arrays.asList(59L);
        long outboundLineProcessedCount = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds);
        log.info("outboundLineProcessedCount : " + outboundLineProcessedCount);
        boolean isAlreadyProcessed = (outboundLineProcessedCount > 0 ? true : false);
        log.info("outboundLineProcessed Already Processed? : " + isAlreadyProcessed);
        if (isAlreadyProcessed) {
            throw new BadRequestException("Order is already processed.");
        }

        /*--------------------OrderManagementLine-Check---------------------------------------------------------------------*/
        // OrderManagementLine checking for STATUS_ID - 42L, 43L
        long orderManagementLineCount = orderManagementLineService.getOrderManagementLineV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, Arrays.asList(42L, 43L));
        boolean isConditionMet = (orderManagementLineCount > 0 ? true : false);
        log.info("orderManagementLineCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("OrderManagementLine is not completely Processed.");
        }

        /*--------------------PickupHeader-Check---------------------------------------------------------------------*/
        // PickupHeader checking for STATUS_ID - 48
        long pickupHeaderCount = orderManagementLineService.getPickupHeaderCountForDeliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 48L);
        isConditionMet = (pickupHeaderCount > 0 ? true : false);
        log.info("pickupHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Pickup is not completely Processed.");
        }

        // QualityHeader checking for STATUS_ID - 54
        long qualityHeaderCount = orderManagementLineService.getQualityHeaderCountForDeliveryConfirmationV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, 54L);
        isConditionMet = (qualityHeaderCount > 0 ? true : false);
        log.info("qualityHeaderCount ---- isConditionMet : " + isConditionMet);
        if (isConditionMet) {
            throw new BadRequestException("Quality check is not completely Processed.");
        }

        //----------------------------------------------------------------------------------------------------------
//        List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L, 41L);
        List<Long> statusIdsToBeChecked = Arrays.asList(57L, 47L, 51L);
        long outboundLineListCount = getOutboundLineV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIdsToBeChecked);
        log.info("outboundLineListCount : " + outboundLineListCount);
        isConditionMet = (outboundLineListCount > 0 ? true : false);
        log.info("isConditionMet : " + isConditionMet);

        if (!isConditionMet) {
            throw new BadRequestException("OutboundLine: Order is not completely Processed.");
        } else {
            log.info("Order can be Processed.");
        }

        try {
            Long STATUS_ID_59 = 59L;
            List<Long> statusId57 = Arrays.asList(57L);
            statusDescription = stagingLineV2Repository.getStatusDescription(STATUS_ID_59, languageId);
            List<OutboundLineV2> outboundLineByStatus57List = orderManagementLineService.findOutboundLineByStatusV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusId57);

            // ----------------OutboundLine update-----------------------------------------------------------------------------------------
            List<Long> lineNumbers = outboundLineByStatus57List.stream().map(OutboundLine::getLineNumber).collect(Collectors.toList());
            List<String> itemCodes = outboundLineByStatus57List.stream().map(OutboundLine::getItemCode).collect(Collectors.toList());
            Date deliveryConfirmedOn = new Date();
            outboundLineV2Repository.updateOutboundLineStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, lineNumbers, deliveryConfirmedOn);
            log.info("OutboundLine updated ");

            //----------------Outbound Header update----------------------------------------------------------------------------------------
            log.info("c_id, plant_id, lang_id, wh_id, ref_doc_no, status_id, Status_desc, date:---->OBH Update----> "
                    + companyCodeId + "," + plantId + "," + languageId + "," + warehouseId + "," + refDocNumber + "," + STATUS_ID_59 + "," + statusDescription + "," + new Date());
            outboundHeaderV2Repository.updateOutboundHeaderStatusNewV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription, new Date());
            OutboundHeaderV2 isOrderConfirmedOutboundHeader = orderManagementLineService.getOutboundHeaderV2(companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber);
            log.info("OutboundHeader updated----1---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
            if (isOrderConfirmedOutboundHeader.getStatusId() != 59L) {
                log.info("OutboundHeader is still updated not updated.");
                log.info("Updating again OutboundHeader.");
                isOrderConfirmedOutboundHeader.setStatusId(STATUS_ID_59);
                isOrderConfirmedOutboundHeader.setStatusDescription(statusDescription);
                isOrderConfirmedOutboundHeader.setUpdatedBy(loginUserID);
                isOrderConfirmedOutboundHeader.setUpdatedOn(new Date());
                isOrderConfirmedOutboundHeader.setDeliveryConfirmedOn(new Date());
                outboundHeaderV2Repository.saveAndFlush(isOrderConfirmedOutboundHeader);
                log.info("OutboundHeader updated---2---> : " + isOrderConfirmedOutboundHeader.getRefDocNumber() + "---" + isOrderConfirmedOutboundHeader.getStatusId());
            }

            //----------------Preoutbound Line----------------------------------------------------------------------------------------------
            preOutboundLineV2Repository.updatePreOutboundLineStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
            log.info("PreOutbound Line updated");

            //----------------Preoutbound Header--------------------------------------------------------------------------------------------
            preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV2(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
            log.info("PreOutbound Header updated");

            //----------------OrderManagement Line--------------------------------------------------------------------------------------------
            orderManagementLineV2Repository.updateOrderManagementLineStatus(companyCodeId, plantId, languageId, warehouseId, refDocNumber, preOutboundNo, STATUS_ID_59, statusDescription);
            log.info("OrderManagement Line updated");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param preOutboundNo
     * @param refDocNumber
     * @param partnerCode
     * @param statusIds
     * @return
     */
    public long getOutboundLineV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                  String preOutboundNo, String refDocNumber, String partnerCode, List<Long> statusIds) {
        long outboundLineCount =
                outboundLineV2Repository.getOutboudLineByWarehouseIdAndPreOutboundNoAndRefDocNumberAndPartnerCodeAndStatusIdInAndDeletionIndicatorV2(
                        companyCodeId, plantId, languageId, warehouseId, preOutboundNo, refDocNumber, partnerCode, statusIds, 0);
        log.info("OuboundLine status Id 47L,51L,57L :----------->" + outboundLineCount);
        return outboundLineCount;
    }

    /**
     *
     * @param orderId
     * @param outboundOrderTypeID
     * @param processStatusId
     * @return
     * @throws ParseException
     */
    public OutboundOrderV2 updateProcessedOrderV2(String orderId, Long outboundOrderTypeID, Long processStatusId) throws ParseException {
        OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
        log.info("orderId : " + orderId);
        log.info("dbOutBoundOrder : " + dbOutboundOrder);
        OutboundOrderV2 orderV2 = getOrder(orderId);
        orderV2.setProcessedStatusId(processStatusId);
        orderV2.setOrderProcessedOn(new Date());
        outboundOrderV2Repository.save(orderV2);

        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        outboundOrderV2Repository.save(orderV2);
        return orderV2;
    }

    /**
     *
     * @param orderId
     * @return
     */
    public OutboundOrderV2 getOrder(String orderId) {
        OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNo (orderId);
        if(dbOutboundOrder != null){
            return dbOutboundOrder;
        }
        else {
            return null;
        }
    }

    /**
     *
     * @param orderId
     * @param outboundOrderTypeID
     */
    public void updateProcessedOrderV2(String orderId, Long outboundOrderTypeID) {
        log.info("rollback - rerun - orderId : " + orderId + "," + outboundOrderTypeID);
        OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(orderId, outboundOrderTypeID);
        if (dbOutboundOrder != null) {
            Long numberOfAttemts = 0L;
            Long attempted = 0L;
            Long processStatusId = 0L;
            if (dbOutboundOrder.getNumberOfAttempts() != null) {
                if (dbOutboundOrder.getNumberOfAttempts().equals(0L)) {
                    numberOfAttemts = 1L;
                    processStatusId = 0L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(1L)) {
                    numberOfAttemts = 2L;
                    processStatusId = 0L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(2L)) {
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
                if (dbOutboundOrder.getNumberOfAttempts().equals(3L)) {
                    numberOfAttemts = 3L;
                    processStatusId = 100L;
                }
            } else {
                numberOfAttemts = 1L;
                processStatusId = 0L;
            }
            dbOutboundOrder.setProcessedStatusId(processStatusId);
            dbOutboundOrder.setNumberOfAttempts(numberOfAttemts);
            dbOutboundOrder.setOrderProcessedOn(new Date());
            OutboundOrderV2 updatedOutboundOrder = outboundOrderV2Repository.save(dbOutboundOrder);
            log.info("rollback rerun - updatedOutboundOrder : " + updatedOutboundOrder);
        }
    }

    /**
     *
     * @param orderId
     * @param outboundOrderTypeID
     * @return
     */
    public OutboundOrderV2 getOBOrderByIdV2(String orderId, Long outboundOrderTypeID) {
        DataBaseContextHolder.clear();
        DataBaseContextHolder.setCurrentDb("MT");
        OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNoAndOutboundOrderTypeID (orderId, outboundOrderTypeID);

        if(dbOutboundOrder!= null) {
            return dbOutboundOrder;
        } else {
            return null;
        }
    }

    /**
     *
     * @param dbPickListHeader
     * @param loginUserID
     * @return
     */
    public PickListHeader createPickListHeader(PickListHeader dbPickListHeader, String loginUserID) {

        log.info("newPickListHeader : " + dbPickListHeader);
        dbPickListHeader.setDeletionIndicator(0L);
        dbPickListHeader.setCreatedBy(loginUserID);
        dbPickListHeader.setUpdatedBy(loginUserID);
        dbPickListHeader.setCreatedOn(new Date());
        dbPickListHeader.setUpdatedOn(new Date());
        PickListHeader createdPickListHeader = pickListHeaderRepository.save(dbPickListHeader);
        return createdPickListHeader;
    }

    public OutboundOrderV2 createOutboundOrdersV2(OutboundOrderV2 newOutboundOrder) throws ParseException {
//		OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.findByRefDocumentNoAndProcessedStatusIdOrderByOrderReceivedOn(newOutboundOrder.getOrderId(), 0L);
//		OutboundOrderV2 dbOutboundOrder = getOBOrderByIdV2(newOutboundOrder.getOrderId());
        OutboundOrderV2 dbOutboundOrder = outboundOrderV2Repository.
                findByRefDocumentNoAndOutboundOrderTypeID(newOutboundOrder.getOrderId(), newOutboundOrder.getOutboundOrderTypeID());
        if(dbOutboundOrder != null) {
            throw new BadRequestException("Order is getting Duplicated");
        }

        newOutboundOrder.setUpdatedOn(new Date());
        OutboundOrderV2 outboundOrder = outboundOrderV2Repository.save(newOutboundOrder);
        return outboundOrder;
    }


    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }
        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
        return inventory;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @param alternateUom
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                 String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.findInventoryForOrderManagementV4(companyCodeId, plantId, languageId, warehouseId, itemCode, manufacturerName, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     * @param orderManagementLine
     * @return
     */
    public OrderManagementLineV2 updateOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByCreatedOnV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                 String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByCreatedOn(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByExpiryDateV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                                  String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByExpiryDate(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                               String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByLevelId(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventory> getInventoryForOrderManagementByBatchV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                    String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryByBatchV4(companyCodeId, plantId, languageId, warehouseId, binClassId,
                stockTypeId, itemCode, manufacturerName, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.findInventoryByBatchV4(companyCodeId, plantId, languageId, warehouseId, binClassId,
                    stockTypeId, itemCode, manufacturerName, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementOrderByBatchV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                             String itemCode, String manufacturerName, Long stockTypeId, Long binClassId, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
                "|" + alternateUom + "|" + binClassId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByBatch(companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryV4OrderByBatch(companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param storageBin
     * @param alternateUom
     * @return
     */
    public synchronized InventoryV2 getInventoryV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      String itemCode, String manufacturerName, String barcodeId, String storageBin, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName +
                "|" + alternateUom + "|" + barcodeId + "|" + storageBin);
        IInventoryImpl dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                itemCode, manufacturerName, PACK_BARCODE, storageBin, alternateUom);
        if (dbInventory == null) {
            dbInventory = inventoryV2Repository.getInventoryV4(companyCodeId, plantId, languageId, warehouseId, barcodeId, null,
                    itemCode, manufacturerName, PACK_BARCODE, storageBin, null);
        }
        if (dbInventory != null) {
            InventoryV2 inventory = new InventoryV2();
            BeanUtils.copyProperties(dbInventory, inventory, CommonUtils.getNullPropertyNames(dbInventory));
            return inventory;
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
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @param alternateUom
     * @return
     */
    public synchronized List<IInventory> getInventoryForOrderManagementGroupByLevelIdV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                           String itemCode, Long stockTypeId, Long binClassId, String manufacturerName, String alternateUom) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId);
        List<IInventory> inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                manufacturerName, stockTypeId, binClassId, alternateUom);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.findInventoryGroupByLevelIdV4(companyCodeId, plantId, languageId, warehouseId, itemCode,
                    manufacturerName, stockTypeId, binClassId, null);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param levelId
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                        String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                        String alternateUom, Long levelId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
                companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
                    companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
        }
        return inventoryList;
    }

    /**
     *
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param manufacturerName
     * @param stockTypeId
     * @param binClassId
     * @param alternateUom
     * @param levelId
     * @return
     */
    public synchronized List<IInventoryImpl> getInventoryForOrderManagementLevelAsscIdV6(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                            String itemCode, String manufacturerName, Long stockTypeId, Long binClassId,
                                                                            String alternateUom, Long levelId) {
        log.info(companyCodeId + "|" + plantId + "|" + languageId + "|" + warehouseId + "|" + itemCode + "|" + manufacturerName + "|" + alternateUom + "|" + binClassId + "|" + levelId);
        List<IInventoryImpl> inventoryList = inventoryV2Repository.getOMLInventoryLevelAsscIdV6(
                companyCodeId, plantId, languageId, warehouseId, null, null,
                itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, alternateUom, levelId);
        if (inventoryList == null || inventoryList.isEmpty()) {
            inventoryList = inventoryV2Repository.getOMLInventoryLevelIdV6(
                    companyCodeId, plantId, languageId, warehouseId, null, null,
                    itemCode, manufacturerName, PACK_BARCODE, binClassId, stockTypeId, null, levelId);
        }
        return inventoryList;
    }

    /**
     * @param companyCodeId
     * @param plantId
     * @param languageId
     * @param warehouseId
     * @param itemCode
     * @param stockTypeId
     * @param binClassId
     * @param manufacturerName
     * @return
     */
    public List<IInventoryImpl> getInventoryForOrderManagementV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                                 String itemCode, Long stockTypeId, Long binClassId, String manufacturerName) {
        if (companyCodeId == null || plantId == null || languageId == null || warehouseId == null ||
                itemCode == null || stockTypeId == null || binClassId == null || manufacturerName == null) {
            throw new BadRequestException("Parameter cannot be null: C_ID, PlantId, Lang_Id, WhId, itm_code, stockTypeId, bin_cl_id, mfr_name---> "
                    + companyCodeId + ", " + plantId + ", " + languageId + "," + warehouseId + "," + itemCode + "," + stockTypeId + "," + binClassId + "," + manufacturerName);
        }
        List<IInventoryImpl> inventory = inventoryV2Repository.findInventoryForOrderManagement(companyCodeId, plantId, languageId, warehouseId, itemCode, binClassId, stockTypeId, manufacturerName);
        return inventory;
    }

    // UnAllocate OrdermanagementLine create
    public OrderManagementLineV2 createEMPTYOrderManagementLineV2(OrderManagementLineV2 orderManagementLine) {

        log.info("Unallocated Orders creation started ...........");
        orderManagementLine.setStatusId(47L);
        statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
        orderManagementLine.setReferenceField7(statusDescription);
        orderManagementLine.setProposedStorageBin("");
        orderManagementLine.setProposedPackBarCode("");
        orderManagementLine.setInventoryQty(0D);
        orderManagementLine.setAllocatedQty(0D);

        IKeyValuePair description = stagingLineV2Repository.getDescription(orderManagementLine.getCompanyCodeId(),
                orderManagementLine.getLanguageId(),
                orderManagementLine.getPlantId(),
                orderManagementLine.getWarehouseId());
        orderManagementLine.setCompanyDescription(description.getCompanyDesc());
        orderManagementLine.setPlantDescription(description.getPlantDesc());
        orderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
        orderManagementLine.setStatusDescription(statusDescription);
        orderManagementLine = orderManagementLineV2Repository.save(orderManagementLine);
        unallocatedOrderLineService.createUnallocatedOrderLine(orderManagementLine);
        log.info("orderManagementLine created: " + orderManagementLine);
        return orderManagementLine;
    }
}
