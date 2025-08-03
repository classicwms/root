package com.tekclover.wms.api.inbound.orders.service;

import com.tekclover.wms.api.inbound.orders.controller.BadRequestException;
import com.tekclover.wms.api.inbound.orders.model.dto.IInventory;
import com.tekclover.wms.api.inbound.orders.model.dto.Strategies;
import com.tekclover.wms.api.inbound.orders.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.inbound.orders.model.inbound.strategies.OutboundStrategies;
import com.tekclover.wms.api.inbound.orders.repository.OutboundStrategiesRepo;
import com.tekclover.wms.api.inbound.orders.repository.StrategiesRepository;
import com.tekclover.wms.api.inbound.orders.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StrategiesService {

    @Autowired
    private StrategiesRepository strategiesRepository;


    @Autowired
    private OutboundStrategiesRepo outboundStrategiesRepo;

    /**
     * @param companyCodeId
     * @param languageId
     * @param plantId
     * @param warehouseId
     * @param strategyTypeId
     * @param sequenceIndicator
     * @return
     */
    public Strategies getStrategies(String companyCodeId, String languageId, String plantId, String warehouseId, Long strategyTypeId, Long sequenceIndicator) {
        Optional<Strategies> strategies =
                strategiesRepository.findByLanguageIdAndCompanyIdAndPlantIdAndWarehouseIdAndStrategyTypeIdAndSequenceIndicatorAndDeletionIndicator(
                        languageId,
                        companyCodeId,
                        plantId,
                        warehouseId,
                        strategyTypeId,
                        sequenceIndicator,
                        0L);
        if (strategies.isEmpty()) {
            throw new BadRequestException("The given company : " + companyCodeId +
                    " plantId" + plantId + "warehouseId" + warehouseId + " doesn't exist.");
        }
        return strategies.get();
    }

    /**
     * @param iInventoryList
     * @param loginUserID
     */
    public void createOutboundStrategies(String companyId, String plantId, String languageId, String warehouseId, List<IInventory> iInventoryList,
                                         String loginUserID, String refDocNumber, Double allocatedQty, Double inventoryQty, String method) {
        log.info("Create OutBoundStrategies Created " + iInventoryList);
        Long sequenceNo = 1L;
        for (IInventory inventory : iInventoryList) {
            OutboundStrategies outboundStrategies = new OutboundStrategies();
            BeanUtils.copyProperties(inventory, outboundStrategies, CommonUtils.getNullPropertyNames(inventory));

            // Setting PartnerName from businessPartnerName and ItemName
            String partnerName = strategiesRepository.getPartnerName(companyId, languageId, plantId, warehouseId, inventory.getPartnerCode());
            if (partnerName != null) {
                outboundStrategies.setPartnerName(partnerName);
            }

            // Get Date_PutAway confirmed_on
            Date pa_cnf_on = strategiesRepository.getPutawayConfimDate(inventory.getBarcodeId(), inventory.getItemCode(), companyId, plantId, warehouseId);
            outboundStrategies.setCreatedOn(pa_cnf_on);
            outboundStrategies.setItemDescription(inventory.getDescription());
            outboundStrategies.setSequenceNo(sequenceNo);
            outboundStrategies.setInventoryId(inventory.getInventoryId());
            outboundStrategies.setCompanyCodeId(companyId);
            outboundStrategies.setPlantId(plantId);
            outboundStrategies.setWarehouseId(warehouseId);
            outboundStrategies.setItemCode(inventory.getItemCode());
            outboundStrategies.setLanguageId("EN");
//            outboundStrategies.setManufacturerName(inventory.getManufacturerName());
            outboundStrategies.setStorageBin(inventory.getStorageBin());
            outboundStrategies.setCreatedBy(loginUserID);
            outboundStrategies.setOutboundStrategies(method);
//            outboundStrategies.setInventoryQuantity(inventoryQty);
//            outboundStrategies.setAllocatedQuantity(allocatedQty);
            // INV_QTY
            if(inventory.getInventoryQty() != null && inventory.getInventoryQty() !=0){
                outboundStrategies.setInventoryQuantity(inventory.getInventoryQty());
            } else {
                outboundStrategies.setInventoryQuantity(null);
            }
            // ALL_QRT
            if(inventory.getAllocatedQty() != null && inventory.getAllocatedQty() !=0) {
                outboundStrategies.setAllocatedQuantity(inventory.getAllocatedQty());
            } else {
                outboundStrategies.setAllocatedQuantity(null);
            }
            outboundStrategies.setReferenceOrderNo(refDocNumber);
            outboundStrategies.setDeletionIndicator(0L);
            sequenceNo++;
            outboundStrategiesRepo.save(outboundStrategies);
            log.info("Outbound Strategies Saved Successfully ----------------" + outboundStrategies);
        }
    }

    /**
     * @param inventoryV2
     * @param loginUserID
     */
    public void updateStrategies(InventoryV2 inventoryV2, String loginUserID, String inventoryId, String refDocNumber,
                                 Double alloctedQty, Double inventoryQty) {
        log.info("Inventory to Strategies update " + inventoryV2);
        log.info("Inventory Id -----------------------------------------------> " + inventoryId);

        OutboundStrategies str = outboundStrategiesRepo.findTopByCompanyCodeIdAndLanguageIdAndPlantIdAndInventoryIdAndItemCodeAndBarcodeIdAndDeletionIndicatorOrderByOutboundStrategiesIdDesc(
                inventoryV2.getCompanyCodeId(), inventoryV2.getLanguageId(), inventoryV2.getPlantId(), Long.valueOf(inventoryId), inventoryV2.getItemCode(), inventoryV2.getBarcodeId(), 0L);
        if (str != null) {
            log.info("Outbound Strategies Get Values -----------------------------------> " + str);
            BeanUtils.copyProperties(inventoryV2, str, CommonUtils.getNullPropertyNames(inventoryV2));
            // Get Date_PutAway confirmed_on
            Date pa_cnf_on = strategiesRepository.getPutawayConfimDate(inventoryV2.getBarcodeId(), inventoryV2.getItemCode(),
                    inventoryV2.getCompanyCodeId(), inventoryV2.getPlantId(), inventoryV2.getWarehouseId());
            str.setCreatedOn(pa_cnf_on);
            // ALL_QTY
            if(alloctedQty != null && alloctedQty != 0) {
                str.setAllocatedQuantity(alloctedQty);
            }
            // INV_QTY
            if(inventoryQty != null && inventoryQty !=0) {
                str.setInventoryQuantity(inventoryQty);
            }
            str.setReferenceOrderNo(refDocNumber);
            str.setUpdatedBy(loginUserID);
//            str.setUpdatedOn(inventoryV2.getUpdatedOn());
            outboundStrategiesRepo.save(str);
            log.info("OutboundStrategies Quantity Updated" + str);
        } else {
            log.info("OutboundStrategies Record not found");
        }
    }

}