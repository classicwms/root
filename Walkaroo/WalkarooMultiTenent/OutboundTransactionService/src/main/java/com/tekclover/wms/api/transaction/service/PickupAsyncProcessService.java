package com.tekclover.wms.api.transaction.service;


import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.controller.exception.BadRequestException;
import com.tekclover.wms.api.transaction.model.IKeyValuePair;
import com.tekclover.wms.api.transaction.model.outbound.ordermangement.v2.OrderManagementLineV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PickupAsyncProcessService extends BaseService{

    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;

    @Autowired
    OrderManagementLineService orderManagementLineService;

    @Autowired
    OutboundLineV2Repository outboundLineV2Repository;

    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;

    @Autowired
    OutboundOrderV2Repository outboundOrderV2Repository;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    @Autowired
    PreOutboundHeaderService preOutboundHeaderService;


    // PickupHeader Creation AsyncProcess
    @Async("asyncExecutorPickup")
    public void pickupHeaderCreation(String companyCodeId, String plantId, String languageId,
                                     String warehouseId, String salesOrderNumber, String loginUserId) throws Exception {

        try {
            String currentDB = getDataBase(plantId);
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);

            log.info("PickupHeader Validation Started ---> ");
            validatePickupHeaderCreationV2(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, loginUserId);
            log.info("PickupHeader Validation Completed ---> ");
        } finally {
            DataBaseContextHolder.clear();
        }

    }


    // PickupHeader Creation
    private void validatePickupHeaderCreationV2(String companyCodeId, String plantId, String languageId, String warehouseId,
                                                String salesOrderNumber, String loginUserId) throws Exception {

        log.info("SalesOrderNumber : " + salesOrderNumber);
        Long pickupHeaderStatusCheck = preOutboundHeaderV2Repository.getPickupHeaderCreateStatus(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
        log.info("pickupHeaderStatusCheck : {}", pickupHeaderStatusCheck);
        try {
            if (pickupHeaderStatusCheck == 1 ) {
                List<String> shipToPartyList = preOutboundHeaderV2Repository.getShipToParty(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
                log.info("shipToParty List : " + shipToPartyList.size());
                if (shipToPartyList != null && !shipToPartyList.isEmpty()) {
                    for (String shipToParty : shipToPartyList) {
                        log.info("ShipToParty : " + shipToParty);
                        List<OrderManagementLineV2> orderManagementLineList = orderManagementLineService.getOrderManagementLinesShipToPartyV3(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber, shipToParty);
                        log.info("Sales OrderNo : {} , ShipToParty : {} , OrderManagementLine Size : {}, Values: {} ", salesOrderNumber, shipToParty, orderManagementLineList.size(), orderManagementLineList);
                        createPickupHeaderV4(companyCodeId, plantId, languageId, warehouseId, orderManagementLineList, loginUserId);
                    }
                }
                preOutboundHeaderService.assignPicker(companyCodeId, plantId, languageId, warehouseId, salesOrderNumber);
            }
        } catch (Exception e) {
            log.error("Exception while validating PickupHeader creation");
            throw e;
        }
    }


    /**
     * @param companyCodeId companyId
     * @param plantId plantId
     * @param languageId languageId
     * @param warehouseId warehouseId
     * @param orderManagementLineList orderLineList
     * @param loginUserId userId
     */
    private void createPickupHeaderV4(String companyCodeId, String plantId, String languageId, String warehouseId,
                                      List<OrderManagementLineV2> orderManagementLineList, String loginUserId) {
        try {
            log.info ("-------createPickupHeaderV4--------called-------");
            List<PickupHeaderV2> pickupHeaderV2List = new ArrayList<>();
            double sumOfAllocatedQty = orderManagementLineList.stream().filter(n -> n.getAllocatedQty() != null).mapToDouble(OrderManagementLineV2::getAllocatedQty).sum();
            IKeyValuePair caseTolerance = getnoOfCaseTolerance(companyCodeId, plantId, languageId, warehouseId);
            log.info("caseTolerance: " + caseTolerance);

//            List<DocumentNumber> documentNumberList = new ArrayList<>();
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
                            if (i > totalCases) {
                                i = 1;
                                PU_NO = getNextRangeNumber(10L, companyCodeId, plantId, languageId, warehouseId);
                            }
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

            // PickupHeader Save Process
            List<PickupHeaderV2> savedPickupHeaders = savePickupHeadersTx(pickupHeaderV2List);

            log.info("All Tables Update Status Process Started ------------->");
            doPostPickupUpdatesTx(companyCodeId, plantId, languageId, warehouseId, savedPickupHeaders);
            log.info("All Tables Update Status Process Completed ------------->");
        } catch (Exception e) {
            log.error("Exception in createPickupHeaderV4: ", e);
            throw new BadRequestException(e.getLocalizedMessage());
        }
    }

    /**
     * Persist a batch of pickup headers in a short transactional block.
     */
    public List<PickupHeaderV2> savePickupHeadersTx(List<PickupHeaderV2> pickupHeaderV2List) {
        log.info("PickupHeader Saved Process Completed Size is -----------------------> " + pickupHeaderV2List.size());

        int retry = 3;

        while (retry > 0) {
            try {
                return savePickupHeaders(pickupHeaderV2List);
            } catch (Exception e) {
//                if (e.getMessage().contains("deadlock")) {
                    retry--;
                    try {
                        Thread.sleep(200);
                    } catch (Exception ignored)
                    {}
//                }
            }
        }
        throw new RuntimeException("Failed after retries");
//        return pickupHeaderV2Repository.saveAll(pickupHeaderV2List);
    }

    @Transactional
    public List<PickupHeaderV2> savePickupHeaders(List<PickupHeaderV2> pickupHeaderV2List) {
        log.info("PickupHeader Saved Process Completed Size is -----------------------> " + pickupHeaderV2List.size());
        return pickupHeaderV2Repository.saveAll(pickupHeaderV2List);
    }

    /**
     * Perform post-save updates in a deterministic order for each saved PickupHeader.
     * Keep this transaction short. Retryable for transient lock acquisition issues.
     */
    @Retryable(value = { CannotAcquireLockException.class, ObjectOptimisticLockingFailureException.class, Exception.class },
            maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void doPostPickupUpdatesTx(String companyCodeId, String plantId, String languageId, String warehouseId, List<PickupHeaderV2> savedPickupHeaders) {
        for (PickupHeaderV2 ph : savedPickupHeaders) {
            String refDoc = ph.getRefDocNumber();
            String pickupNumber = ph.getPickupNumber();
            String statusDescription = ph.getStatusDescription();

            // 1) outboundLine update
            int outboundLineCount = outboundLineV2Repository.updateOutboundLineStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, ph.getPreOutboundNo(), 48L, statusDescription, ph.getLineNumber(), ph.getItemCode());
            log.info("OutboundLine Status Update Count Size {} ", outboundLineCount);

            // 2) outboundHeader update
            int outboundHeaderCount = outboundHeaderV2Repository.updateOutboundHeaderStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, ph.getPreOutboundNo(), 48L, statusDescription);
            log.info("OutboundHeader Status Update Count {} ", outboundHeaderCount);

            // 3) preOutboundHeader update - include pickup number
            int preoutboundHeader = preOutboundHeaderV2Repository.updatePreOutboundHeaderStatus(companyCodeId, plantId, languageId, warehouseId, refDoc, pickupNumber, 48L, statusDescription);
            log.info("PreOutboundHeader Status Update Count {} ", preoutboundHeader);

            // 4) outboundOrder text update
            int obOrderStatusUpdateCount =  outboundOrderV2Repository.updateOBHeaderText(ph.getOutboundOrderTypeId(), refDoc, "PickupHeader Created");
            log.info("OB Order PickupHeader Status Updated Count {} ", obOrderStatusUpdateCount);

            log.info("Completed post updates for RefDocNo {}", refDoc);
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

}
