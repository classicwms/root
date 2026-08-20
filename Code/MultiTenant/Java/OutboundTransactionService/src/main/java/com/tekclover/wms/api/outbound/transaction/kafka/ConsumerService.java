//package com.tekclover.wms.api.outbound.transaction.kafka;
//
//
//import com.tekclover.wms.api.outbound.transaction.kafka.event.*;
//import com.tekclover.wms.api.outbound.transaction.model.inventory.v2.InventoryV2;
//import com.tekclover.wms.api.outbound.transaction.model.outbound.pickup.v2.PickupLineV2;
//import com.tekclover.wms.api.outbound.transaction.repository.*;
//import com.tekclover.wms.api.outbound.transaction.service.AsyncService;
//import com.tekclover.wms.api.outbound.transaction.service.PeriodicLineService;
//import com.tekclover.wms.api.outbound.transaction.service.PickupLineService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.List;
//
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ConsumerService {
//
//    @Autowired
//    PickupLineV2Repository pickupLineV2Repository;
//
//    @Autowired
//    InventoryMovementRepository inventoryMovementRepository;
//
//    @Autowired
//    PickupLineService pickupLineService;
//
//    @Autowired
//    InventoryV2Repository inventoryV2Repository;
//
//    @Autowired
//    private PeriodicHeaderV2Repository periodicHeaderV2Repository;
//
//    @Autowired
//    OutboundLineV2Repository outboundLineV2Repository;
//
//    @Autowired
//    StorageBinV2Repository storageBinV2Repository;
//
//    @Autowired
//    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
//
//    @Autowired
//    OutboundHeaderV2Repository outboundHeaderV2Repository;
//
//    @Autowired
//    PickupHeaderV2Repository pickupHeaderV2Repository;
//
//    @Autowired
//    OrderManagementLineV2Repository orderManagementLineV2Repository;
//
//    @Autowired
//    PeriodicLineService periodicLineService;
//
//    @Autowired
//    AsyncService asyncService;
//
//    // PickupLine Creation
//    @KafkaListener(topics = "pickupline-create-topic-v1", groupId = "pickupline-group-v1", containerFactory = "pickupLineCreateListenerFactory")
//    public void consume(PickupLineCreatedEvent event) throws Exception {
//        pickupLineService.createPickupLineNonCBMV4(event.getAddPickupLines(), event.getLoginUserID());
//    }
//
//
//    // PickupLine Creation
//    @KafkaListener(topics = "pickupline-save-topic-v1", groupId = "pickupline-save-group-v1", containerFactory = "pickupLineSaveListenerFactory")
//    public void consume(PickupLineSaveEvent event) throws Exception {
//
//        List<PickupLineV2> pickupLineV2List = event.getPickupLineV2();
//        log.info("Saving {} records", pickupLineV2List.size());
//
//        List<PickupLineV2> pickupLineList;
//        try {
//            pickupLineList = pickupLineV2Repository.saveAll(pickupLineV2List);
//            log.info("PickupLine Saved List ---------> {}", pickupLineList.size());
//            asyncService.getInventoryForMatchingBarcodeIdV4(pickupLineList, event.getLoginUserID());
//        } catch (Exception e) {
//            log.error("Error while saving PickupLine, Skipping Inventory Allocation.", e);
//        }
//    }
//
//
//    @KafkaListener(topics = "outboundline-status-update-topic-v1", groupId = "outboundline-status-update-group-v1", containerFactory = "outboundLineStatusListenerFactory")
//    public void consume(UpdateOutboundLineStatusEvent event) {
//        log.info("Update OutboundLine Status Event {}", event);
//        outboundLineV2Repository.updateOutboundLineStatusV4(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),event.getRefDocNumber(), event.getPreOutboundNo(), event.getItemCode(), event.getManufacturerName(),event.getPartnerCode(),
//                event.getActualHeNo(), event.getAssignedPickerId(), event.getLineNumber(), event.getStatusId(),event.getStatusDescription(),new Date(),event.getBagSize(),event.getNoBags());
//    }
//
//
//    @KafkaListener(topics = "outboundline-update-topic-v1", groupId = "outboundline-update-group-v1", containerFactory = "outboundLineListenerFactory")
//    public void consume(UpdateOutboundLineEvent event) {
//        log.info("Update outboundline Event {}", event);
//        outboundLineV2Repository.updateOutboundLineV6(event.getCompanyCodeId(), event.getPlantId(), event.getWarehouseId(), event.getRefDocNumber(),event.getPreOutboundNo(),event.getItemCode(), event.getLineNumber(), event.getReferenceField6());
//    }
//
//    @KafkaListener(topics = "storagebinstatus-update-topic-v1", groupId = "storagebinstatus-update-group-v1", containerFactory = "storageBinStatusListenerFactory")
//    public void consume(UpdateStorageBinStatusEvent event) {
//        log.info("storageBinStatus Event {}", event);
//        storageBinV2Repository.updateStorageBinStatus(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(),event.getWarehouseId(), event.getStorageBin(),event.getStatusId(),event.getUpdatedBy());
//    }
//
//    @KafkaListener(topics = "pickupLine-expdate-topic-v1", groupId = "pickupLine-expdate-group-v1", containerFactory = "expDateListenerFactory")
//    public void consume(UpdateExpDateEvent event) {
//        log.info("Update PickupLineExpDate Event {}", event);
//        pickupLineV2Repository.updateExpDate(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(),
//                event.getWarehouseId(), event.getReferenceDocumentNo(), event.getItemCode(), event.getBarcodeId(),
//                event.getExpiryDate() );
//    }
//
//    //     Inventory Save
//    @KafkaListener(topics = "inventory-saves-topic-v1", groupId = "inventory-saves-group-v1", containerFactory = "inventorySaveListenerFactory")
//    public void consume(InventorySaveEvent event) {
//        InventoryV2 inventorySave = event.getInventoryV2();
//        log.info("Saving {} records", event);
//        inventoryV2Repository.save(inventorySave);
//    }
//
//    // PreObHeader Update
//    @KafkaListener(topics = "preobheader-status-update-topic-v1", groupId = "preobheader-status-update-group-v1", containerFactory = "updatePreObHeaderStatusListenerFactory")
//    public void consume(UpdatePreOutboundHeaderStatus event) {
//        log.info("Update PreOutbound Line Event {}", event);
//        preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV2(event.getCompanyId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
//                event.getRefDocNo(), event.getPreOutboundNo(), event.getStatusId(), event.getStatusDescription(), event.getLoginUserID() ,new Date());
//    }
//
//    // OutboundHeader Update
//    @KafkaListener(topics = "obheader-status-update-topic-v1", groupId = "obheader-status-update-group-v1", containerFactory = "updateObHeaderStatusListenerFactory")
//    public void consume(UpdateOutboundHeaderStatus event) {
//        log.info("Update Outbound Line Event {}", event);
//        outboundHeaderV2Repository.updateOutboundHeaderStatusV2(event.getCompanyId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
//                event.getRefDocNo(), event.getPreOutboundNo(), event.getStatusId(), event.getStatusDescription(), event.getLoginUserID() ,new Date());
//    }
//}
