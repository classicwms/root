package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.OutboundIntegrationHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.preoutbound.v2.PreOutboundLineV2;
import com.tekclover.wms.api.transaction.model.outbound.v2.OutboundHeaderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderFulfillmentService extends BaseService {


    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    PreOutboundLineV2Repository preOutboundLineV2Repository;
    @Autowired
    InventoryService inventoryService;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;
    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;
    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;


    /**
     * @param preOutboundHeaderV2List preOutboundHeaderList
     * @return
     * @throws Exception
     */
    public List<OutboundHeaderV2> sapOutboundOrderFullfillment(List<OutboundIntegrationHeaderV2> preOutboundHeaderV2List)  {

        String companyCodeId = preOutboundHeaderV2List.get(0).getCompanyCode();
        String plantId = preOutboundHeaderV2List.get(0).getBranchCode();
        String warehouseId = preOutboundHeaderV2List.get(0).getWarehouseID();
        String languageId = preOutboundHeaderV2List.get(0).getLanguageId();

        List<OutboundHeaderV2> outboundHeaderV2List = new ArrayList<>();

        statusDescription = getStatusDescription(48L, "EN");
        List<String> refDocNoList = preOutboundHeaderV2List.stream().map(OutboundIntegrationHeaderV2 :: getRefDocumentNo).collect(Collectors.toList());
        log.info("PreOutboundHeader Update process List of Orders : {} ", refDocNoList);
        int preOutbound = preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusListOfOrders(companyCodeId, plantId, languageId, warehouseId, refDocNoList, 48L, statusDescription);
        log.info("PreOutboundHeader Update Affected Row's : {} ", preOutbound);
        for(OutboundIntegrationHeaderV2 headerV2 : preOutboundHeaderV2List) {
            try {
//                preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV3(companyCodeId, plantId, languageId, warehouseId, headerV2.getRefDocumentNo(), 48L, "IN PICKING");
                log.info("PreOutboundHeader Status Updated Successfully -- RefDocNo : {}", headerV2.getRefDocumentNo());
                List<PreOutboundLineV2> preOutboundLineV2List = preOutboundLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndRefDocNumberAndDeletionIndicator(companyCodeId, plantId, languageId, warehouseId, headerV2.getRefDocumentNo(), 0L);
                log.info("PreOutboundLine List Value size :{} --> RefDocNo : {}", preOutboundLineV2List.size(), headerV2.getRefDocumentNo());
                log.info("OrderAllocation Process Started ------>");
                orderAllocation(preOutboundLineV2List);
                log.info("OrderAllocation Process Completed ------>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("PickupHeader Validation Started-------------------->");
        pickupHeaderCreation(companyCodeId, plantId, languageId, warehouseId, preOutboundHeaderV2List);
        return outboundHeaderV2List;
    }


    // PreOutbound Line Process
    public void orderAllocation(List<PreOutboundLineV2> preOutboundLineV2List) {

        for (PreOutboundLineV2 preOutboundLineV2 : preOutboundLineV2List) {

            log.error("--------1-------fromOrderFullfillment----> " + preOutboundLineV2);
            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));

            if (preOutboundLineV2.getCustomerId() == null) {
                orderManagementLine.setPartnerCode("STO");
            }
            log.info("orderManagementLine : " + orderManagementLine);

            Long OB_ORD_TYP_ID = preOutboundLineV2.getOutboundOrderTypeId();
            List<Long> BIN_CLASS_ID = new ArrayList<>();

            if (OB_ORD_TYP_ID == 0L || OB_ORD_TYP_ID == 1L || OB_ORD_TYP_ID == 3L) {
                if (preOutboundLineV2.getMtoNumber() != null && !preOutboundLineV2.getMtoNumber().isEmpty()) {
                    log.info("MTO Number is ------------------> {} ", preOutboundLineV2.getMtoNumber());
                    BIN_CLASS_ID.add(10L);
                    BIN_CLASS_ID.add(3L);
                    BIN_CLASS_ID.add(1L);
                } else {
                    BIN_CLASS_ID.add(1L);
                    BIN_CLASS_ID.add(10L);
                    BIN_CLASS_ID.add(3L);
                }

                log.info("OrderAllocation Process Started Ref_Doc_No Is {} , OrderTypeId: {} ", preOutboundLineV2.getRefDocNumber(), OB_ORD_TYP_ID);
                postOrderManagementLine(preOutboundLineV2, BIN_CLASS_ID);
                log.info("OrderFullfillment method OrderManagement Line Created Successfully ----------> OrderId is {}", preOutboundLineV2.getRefDocNumber());
            }
            if (OB_ORD_TYP_ID == 2L) {
                BIN_CLASS_ID.add(7L);
                log.info("OrderAllocation Process Started Ref_Doc_No Is {} , OrderTypeId: {} ", preOutboundLineV2.getRefDocNumber(), OB_ORD_TYP_ID);
                postOrderManagementLine(preOutboundLineV2, BIN_CLASS_ID);
                log.info("OrderFullfillment method OrderManagement Line Created Successfully ----------> OrderId is {}", preOutboundLineV2.getRefDocNumber());
            }
        }
    }


    // OrderMangementLine Creation Process
    public void postOrderManagementLine(PreOutboundLineV2 preOutboundLineV2, List<Long> binClassId) {

        String companyCodeId = preOutboundLineV2.getCompanyCodeId();
        String plantId = preOutboundLineV2.getPlantId();
        String languageId = preOutboundLineV2.getLanguageId();
        String warehouseId = preOutboundLineV2.getWarehouseId();
        String itemCode = preOutboundLineV2.getItemCode();
        String manufacturerName = preOutboundLineV2.getManufacturerName();
        Double ORD_QTY = preOutboundLineV2.getOrderQty();
        List<InventoryV2> findInvList = inventoryService.getInventoryForFFM(companyCodeId, plantId, languageId, warehouseId,
                itemCode, manufacturerName, 1L, binClassId, preOutboundLineV2.getMtoNumber());
        log.info("Inventory List in Order ItemCode :{} And QTY: {} And Size of Values : {} ", itemCode, ORD_QTY, findInvList.size());

        List<OrderManagementLineV2> orderManagementLineV2List = new ArrayList<>();
        Long lineNo = 1L;
        for (InventoryV2 stBinInventory : findInvList) {
            log.info("Walkaroo Inventory for Allocation Bin wise ---->: " + stBinInventory);

            OrderManagementLineV2 orderManagementLine = new OrderManagementLineV2();

            BeanUtils.copyProperties(preOutboundLineV2, orderManagementLine, CommonUtils.getNullPropertyNames(preOutboundLineV2));
            Long STATUS_ID = 0L;
            // INV_QTY
            orderManagementLine.setInventoryQty(1D);
            orderManagementLine.setAllocatedQty(1D);
            orderManagementLine.setReAllocatedQty(1D);
            STATUS_ID = 43L;

            statusDescription = getStatusDescription(STATUS_ID, orderManagementLine.getLanguageId());
            orderManagementLine.setStatusId(STATUS_ID);
            orderManagementLine.setStatusDescription(statusDescription);
            orderManagementLine.setReferenceField7(statusDescription);
            orderManagementLine.setPickupUpdatedBy(preOutboundLineV2.getUpdatedBy());
            orderManagementLine.setPickupUpdatedOn(new Date());

            if (orderManagementLine.getCompanyDescription() == null) {
                IKeyValuePair description = getDescription(companyCodeId, plantId, languageId, warehouseId);
                orderManagementLine.setCompanyDescription(description.getCompanyDesc());
                orderManagementLine.setPlantDescription(description.getPlantDesc());
                orderManagementLine.setWarehouseDescription(description.getWarehouseDesc());
            }

            orderManagementLine.setProposedStorageBin(stBinInventory.getStorageBin());
            orderManagementLine.setBarcodeId(stBinInventory.getBarcodeId());
            orderManagementLine.setLevelId(stBinInventory.getLevelId());
            orderManagementLine.setStorageSectionId(stBinInventory.getStorageSectionId());
            orderManagementLine.setPalletId(stBinInventory.getPalletId());
            orderManagementLine.setProposedPackBarCode(stBinInventory.getPackBarcodes());
            orderManagementLine.setProposedBatchSerialNumber(stBinInventory.getBatchSerialNumber());
            orderManagementLine.setArticleNo(stBinInventory.getArticleNo());
            orderManagementLine.setGender(stBinInventory.getGender());
            orderManagementLine.setMaterialNo(stBinInventory.getMaterialNo());
            orderManagementLine.setNoPairs(stBinInventory.getNoPairs());
            orderManagementLine.setSize(stBinInventory.getSize());
            orderManagementLine.setColor(stBinInventory.getColor());
            orderManagementLine.setDeletionIndicator(0L);
            orderManagementLine.setLineNumber(lineNo);
            lineNo ++;

            log.info("--before-create---new-createdOrderManagementLine------: " + orderManagementLine);
            List<Long> statusIds = Arrays.asList(42L, 43L, 48L);  //42,43,48
            boolean existingOrderManagementLine = orderManagementLineV2Repository.existsByBarcodeIdAndStatusIdInAndDeletionIndicator(orderManagementLine.getBarcodeId(), statusIds, 0L);

            if (existingOrderManagementLine) {
                log.warn("OrderManagementLine with same barcodeId is existing ---> {}", orderManagementLine.getBarcodeId());
            } else {
                orderManagementLineV2List.add(orderManagementLine);
//                createdOrderManagementLine = orderManagementLineV2Repository.saveAndFlush(newOrderManagementLine);
                log.info("OrderManagementLine Saved Values is  ---> {} ", orderManagementLine);

                log.info("ORD_QTY {}, ALLOC_QTY {} ", ORD_QTY, 1D);
                ORD_QTY = ORD_QTY - 1D;
                log.info("ORD_QTY is  ------------------>  {} ", ORD_QTY);

                if (ORD_QTY == 0.0) {
                    log.info("ORD_QTY fully allocated: " + ORD_QTY);
                    break;
                }
            }
        }
        if (ORD_QTY > 0.0) {
            log.info("OrderQty ----------Value is {} ", ORD_QTY);
            unAllocationProcess(preOutboundLineV2, ORD_QTY);
        }

        if(!orderManagementLineV2List.isEmpty()) {
            orderManagementLineV2Repository.saveAll(orderManagementLineV2List);
            log.info("OrderManagementLine Values Saved Successfully -------->");
        }
    }


    /**
     *
     * @param orderManagementLine orderManagementLine
     * @return
     */
    private void unAllocationProcess(PreOutboundLineV2 orderManagementLine, Double ORD_QTY) {
        List<Long> statusIds = Arrays.asList(42L,43L);
        log.info("UnAllocation Process Started  RefDocNo is {} ----------------> ", orderManagementLine.getRefDocNumber() );
        //10

        List<OrderManagementLineV2> existingLines = orderManagementLineV2Repository.findByCompanyCodeIdAndPlantIdAndLanguageIdAndWarehouseIdAndItemCodeAndStatusIdInAndDeletionIndicatorOrderByPickupCreatedOnAsc(
                orderManagementLine.getCompanyCodeId(), orderManagementLine.getPlantId(), orderManagementLine.getLanguageId(), orderManagementLine.getWarehouseId(), orderManagementLine.getItemCode(), statusIds, 0L);

        double requestQty = ORD_QTY;
        for (int i = 0; i < ORD_QTY; i++) {

            if (!existingLines.isEmpty() && i < existingLines.size()) {
                OrderManagementLineV2 newLine = new OrderManagementLineV2();
                OrderManagementLineV2 existing = existingLines.get(i);
                BeanUtils.copyProperties(orderManagementLine, newLine, CommonUtils.getNullPropertyNames(orderManagementLine));
                newLine.setPreOutboundNo(orderManagementLine.getPreOutboundNo());
                newLine.setLineNumber(orderManagementLine.getLineNumber());
                newLine.setRefDocNumber(orderManagementLine.getRefDocNumber());
                newLine.setPartnerCode(orderManagementLine.getPartnerCode());
                newLine.setOrderQty(orderManagementLine.getOrderQty());
                newLine.setAllocatedQty(1D);
                newLine.setInventoryQty(1D);
                newLine.setProposedStorageBin(existing.getProposedStorageBin());
                newLine.setProposedPackBarCode("99999");
                newLine.setBarcodeId(existing.getBarcodeId());
                newLine.setArticleNo(existing.getArticleNo());
                newLine.setGender(existing.getGender());
                newLine.setColor(existing.getColor());
                newLine.setSize(existing.getSize());
                newLine.setNoPairs(existing.getNoPairs());
                newLine.setStatusId(43L);
                orderManagementLineV2Repository.save(newLine);
                log.info("Saved Order ManagementLine in Allocate Process {} ", newLine);
                requestQty = requestQty -1 ;
            }
        }

        if (requestQty > 0) {
            OrderManagementLineV2 newLine = new OrderManagementLineV2();
            BeanUtils.copyProperties(orderManagementLine, newLine, CommonUtils.getNullPropertyNames(orderManagementLine));
            newLine.setStatusId(47L);
            statusDescription = stagingLineV2Repository.getStatusDescription(47L, orderManagementLine.getLanguageId());
            newLine.setStatusDescription(statusDescription);
            newLine.setReferenceField7(statusDescription);
            newLine.setProposedStorageBin("");
            newLine.setProposedPackBarCode("");
            newLine.setBarcodeId(UUID.randomUUID().toString());
            newLine.setOrderQty(requestQty);
            newLine.setInventoryQty(0D);
            newLine.setAllocatedQty(0D);
            newLine = orderManagementLineV2Repository.save(newLine);
            log.info("orderManagementLine created: " + newLine);
            int rows= preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV3(newLine.getCompanyCodeId(), newLine.getPlantId(), newLine.getLanguageId(), newLine.getWarehouseId(), newLine.getRefDocNumber(), 47L, "No Stock Item");
            log.info("PreOutboundHeader UnAllocation Status Updated for RefDocNo : {} and Affected Row's :{} ", newLine.getRefDocNumber(), rows);

        }
    }


    // PickupHeader Creation Process
    public void pickupHeaderCreation(String companyCodeId, String plantId, String languageId, String warehouseId, List<OutboundIntegrationHeaderV2> preOutboundHeader) {

        List<String> refDocNoList = preOutboundHeader.stream().map(OutboundIntegrationHeaderV2 :: getRefDocumentNo).collect(Collectors.toList());
        log.info("PickupHeader Creation RefDocNo List: {} ", refDocNoList);

        List<OrderManagementLineV2> orderManagementLineV2List = orderManagementLineV2Repository.getOrderManagementLine(companyCodeId, plantId, warehouseId, refDocNoList);
        log.info("OrderManagementLine List : {} ", orderManagementLineV2List.size());

//        Map<Long, List<OrderManagementLineV2>> groupByStatusID = orderManagementLineV2List.stream().collect(Collectors.groupingBy(OrderManagementLineV2 :: getStatusId));
//
//        for(Map.Entry<Long, List<OrderManagementLineV2>> groupByStatus : groupByStatusID.entrySet()) {
//            log.info("StatusId -----> " + groupByStatus.getKey());
//            log.info("OrderManagementLine list of size: {} ", groupByStatus.getValue().size());
//
//            createPickupHeaderV4(companyCodeId, plantId, languageId, warehouseId, groupByStatus.getValue(), MW_AMS);
//        }

        createPickupHeaderV4(companyCodeId, plantId, languageId, warehouseId, orderManagementLineV2List, MW_AMS);
    }

    /**
     *
     * @param companyCodeId companyId
     * @param plantId plantId
     * @param languageId languageId
     * @param warehouseId warehouseId
     * @param orderManagementLineList orderList
     * @param loginUserId loginUserID
     */
    private void createPickupHeaderV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      List<OrderManagementLineV2> orderManagementLineList, String loginUserId) {
        try {
            log.info ("-------createPickupHeaderV4--------called-------");
            List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
            double sumOfAllocatedQty = orderManagementLineList.stream().filter(n -> n.getAllocatedQty() != null).mapToDouble(OrderManagementLineV2::getAllocatedQty).sum();
            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
            log.info("caseTolerance: " + caseTolerance);

            List<OrderManagementLineV2> sortedOrderManagementLineList = orderManagementLineList.stream().sorted(Comparator.comparing(OrderManagementLineV2::getProposedStorageBin).reversed()).collect(Collectors.toList());
            String PU_NO = null;
            if (caseTolerance != null) {
                double noOfCases = getQuantity(caseTolerance.getNoOfCase());
                double plusTolerance = getQuantity(caseTolerance.getPlusTolerance());
                double totalCases = noOfCases + plusTolerance;
                int PU_NO_COUNT = (int) Math.ceil(sumOfAllocatedQty / totalCases);
                log.info(noOfCases + "|" + plusTolerance + "|" + totalCases + "|" + sumOfAllocatedQty+ "|" + PU_NO_COUNT);
                int i = 1;
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                log.info("OrderFullfillment PickupHeader Creation -------------> Sorted OrdermanagementList ---->  {} ", orderManagementLineList.size());

                for (OrderManagementLineV2 createdOrderManagementLine : sortedOrderManagementLineList) {
                    log.info("OutboundOrderType ID is --------------------> {} ", createdOrderManagementLine.getOutboundOrderTypeId() );
                    if (createdOrderManagementLine.getOutboundOrderTypeId() == 3) {
                        if (i <= totalCases) {
                            PickupHeaderV2 pickupHeaderV2 =  createPickUpHeaderV4(companyCodeId, plantId, languageId, warehouseId, PU_NO,
                                    createdOrderManagementLine, loginUserId);
                            if (pickupHeaderV2 != null) {
                                log.info("PickupHeader is Created -------------------> RefDocNo is {} ", pickupHeaderV2.getRefDocNumber());
                                pickupHeaderV2List.add(pickupHeaderV2);
                            }
                            i++;
                        }
                        if (i > totalCases) {
                            i = 1;
                            PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                        }
                    }
                }

            } else {
                log.info("CaseTolerance is null ------------------------------------> ");
                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                for (OrderManagementLineV2 orderManagementLine : orderManagementLineList) {
                    log.info("OutboundOrderType ID is --------------------> {} ", orderManagementLine.getOutboundOrderTypeId() );
                    if(orderManagementLine.getOutboundOrderTypeId() == 3) {
                        PickupHeaderV2 pickupHeaderV2 = createPickUpHeaderV4(companyCodeId, plantId, languageId, warehouseId, PU_NO,
                                orderManagementLine, loginUserId);
                        if (pickupHeaderV2 != null) {
                            log.info("PickupHeader is Created -------------------> RefDocNo is {} ", pickupHeaderV2.getRefDocNumber());
                            pickupHeaderV2List.add(pickupHeaderV2);
                        }
                    }
                }
            }

            if(!pickupHeaderV2List.isEmpty()) {
                // PickupHeader Save Process
                List<PickupHeaderV2> savedPickupHeaders = savePickupHeadersTx(pickupHeaderV2List);
                log.info("PickupHeader Saved List of size : {} ", savedPickupHeaders.size());

                List<String> refDocNoList = savedPickupHeaders.stream().map(PickupHeaderV2 :: getRefDocNumber).collect(Collectors.toList());
                int preoutboundHeader = preOutboundHeaderV2Repository.updatePreOutboundHeader(companyCodeId, plantId, languageId, warehouseId, refDocNoList, PU_NO);
                log.info("PreOutboundHeader Status Update Count {} ", preoutboundHeader);

                // 4) outboundOrder text update
                int obOrderStatusUpdateCount = outboundOrderV2Repository.updateOBHeaderTextBasedOnOrders(refDocNoList, "PickupHeader Created");
                log.info("OB Order PickupHeader Status Updated Count {} ", obOrderStatusUpdateCount);
            }

        } catch (Exception e) {
            log.error("Exception in createPickupHeaderV4: ", e);
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

    /**
     * @param companyCodeId companyId
     * @param plantId plantId
     * @param languageId languageId
     * @param warehouseId warehouseId
     * @param orderManagementLine orderLine
     * @param loginUserId userId
     * @throws Exception
     */
    private PickupHeaderV2 createPickUpHeaderV4(String companyCodeId, String plantId, String languageId, String warehouseId, String pickupNumber,
                                                OrderManagementLineV2 orderManagementLine, String loginUserId) throws Exception {

        PickupHeaderV2 newPickupHeader = new PickupHeaderV2();
        BeanUtils.copyProperties(orderManagementLine, newPickupHeader, CommonUtils.getNullPropertyNames(orderManagementLine));

//            newPickupHeader.setAssignedPickerId(assignPickerId);
        newPickupHeader.setPickupNumber(pickupNumber);
        newPickupHeader.setPickToQty(orderManagementLine.getAllocatedQty());
        // PICK_UOM
        newPickupHeader.setPickUom(orderManagementLine.getOrderUom());

        // STATUS_ID
        newPickupHeader.setStatusId(48L);
        statusDescription = getStatusDescription(48L, languageId);
        newPickupHeader.setStatusDescription(statusDescription);

        newPickupHeader.setReferenceField5(orderManagementLine.getDescription());
        if (newPickupHeader.getCompanyDescription() == null) {
            description = getDescription(companyCodeId, plantId, languageId, warehouseId);
            newPickupHeader.setCompanyDescription(description.getCompanyDesc());
            newPickupHeader.setPlantDescription(description.getPlantDesc());
            newPickupHeader.setWarehouseDescription(description.getWarehouseDesc());
        }
        newPickupHeader.setDeletionIndicator(0L);
        newPickupHeader.setPickupCreatedBy(loginUserId);
        newPickupHeader.setPickUpdatedBy(loginUserId);
        newPickupHeader.setPickupCreatedOn(new Date());
        newPickupHeader.setPickUpdatedOn(new Date());
        return newPickupHeader;

    }

    /**
     * Persist a batch of pickup headers in a short transactional block.
     */
    public List<PickupHeaderV2> savePickupHeadersTx(List<PickupHeaderV2> pickupHeaderV2List) {
        log.info("PickupHeader Saved Process Completed Size is -----------------------> " + pickupHeaderV2List.size());
        return pickupHeaderV2Repository.saveAll(pickupHeaderV2List);
    }


//    /**
//     * Perform post-save updates in a deterministic order for each saved PickupHeader.
//     * Keep this transaction short. Retryable for transient lock acquisition issues.
//     */
//    @Retryable(value = { CannotAcquireLockException.class, ObjectOptimisticLockingFailureException.class, Exception.class },
//            maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
//    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
//    public void doPostPickupUpdatesTx(String companyCodeId, String plantId, String languageId, String warehouseId, List<PickupHeaderV2> savedPickupHeaders) {
//        for (PickupHeaderV2 ph : savedPickupHeaders) {
//            String refDoc = ph.getRefDocNumber();
//            String pickupNumber = ph.getPickupNumber();
//            String statusDescription = ph.getStatusDescription();
//
//            // 1) outboundLine update
////            int outboundLineCount = outboundLineV2Repository.updateOutboundLineStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, ph.getPreOutboundNo(), 48L, statusDescription, ph.getLineNumber(), ph.getItemCode());
////            log.info("OutboundLine Status Update Count Size {} ", outboundLineCount);
//
//            // 2) outboundHeader update
//            int outboundHeaderCount = outboundHeaderV2Repository.updateOutboundHeaderStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, ph.getPreOutboundNo(), 48L, statusDescription);
//            log.info("OutboundHeader Status Update Count {} ", outboundHeaderCount);
//
//            // 3) preOutboundHeader update - include pickup number
//            int preoutboundHeader = preOutboundHeaderV2Repository.updatePreOutboundHeaderStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, pickupNumber, 48L, statusDescription);
//            log.info("PreOutboundHeader Status Update Count {} ", preoutboundHeader);
//
//            // 4) outboundOrder text update
//            int obOrderStatusUpdateCount =  outboundOrderV2Repository.updateOBHeaderText(ph.getOutboundOrderTypeId(), refDoc, "PickupHeader Created");
//            log.info("OB Order PickupHeader Status Updated Count {} ", obOrderStatusUpdateCount);
//
//            log.info("Completed post updates for RefDocNo {}", refDoc);
//        }
//    }


}
