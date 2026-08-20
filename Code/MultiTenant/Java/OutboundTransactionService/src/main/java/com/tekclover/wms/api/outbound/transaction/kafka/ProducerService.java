//package com.tekclover.wms.api.outbound.transaction.kafka;
//
//import com.tekclover.wms.api.outbound.transaction.kafka.event.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ProducerService {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public void publish(String topic, Object event) {
//        kafkaTemplate.send(topic, event);
//    }
//
//    // PickupLine SaveAll
//    public void savePickupLine(PickupLineSaveEvent event) {
//        kafkaTemplate.send("pickupline-save-topic-v1", event);
//    }
//
//    // Inventory Save
//    public void saveInventory(InventorySaveEvent event) {
//        kafkaTemplate.send("inventory-saves-topic-v1", event);
//    }
//
//    public void updateOutboundLineStatus(UpdateOutboundLineStatusEvent event) {
//        kafkaTemplate.send("outboundline-status-update-topic-v1", event);
//    }
//
//    public void updateOutboundLine(UpdateOutboundLineEvent event) {
//        kafkaTemplate.send("outboundline-update-topic-v1", event);
//    }
//
//    public void updateStorageBinStatus(UpdateStorageBinStatusEvent event) {
//        kafkaTemplate.send("storagebinstatus-update-topic-v1", event);
//    }
//
//    public void updateObHeaderPreObHeader(updateObHeaderPreObHeaderEvent event) {
//        kafkaTemplate.send("obheaderpreobheader-status-update-topic-v1", event);
//    }
//
//    public void UpdatePickupLineExpDate(UpdateExpDateEvent event) {
//        kafkaTemplate.send("pickupLine-expdate-topic-v1", event);
//    }
//
//    // Update OutboundLine
//    public void updatePreOutboundHeader(UpdatePreOutboundHeaderStatus event) {
//        kafkaTemplate.send("preobheader-status-update-topic-v1", event);
//    }
//
//    // Update OutboundLine
//    public void updateOutboundHeader(UpdateOutboundHeaderStatus event) {
//        kafkaTemplate.send("obheader-status-update-topic-v1", event);
//    }
//
//}
