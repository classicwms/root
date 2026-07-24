package com.tekclover.wms.api.inbound.transaction.kafka;

import com.tekclover.wms.api.inbound.transaction.kafka.event.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object event) {
        kafkaTemplate.send(topic, event);
    }

    // PutAwayLine Save
    public void savePutAwayLine(PutAwayLineCreatedEvent event) {
        kafkaTemplate.send("putawayline-save-topic-v2", event);
    }

    // Update PutAwayHeader Status Update
    public void updatePutAwayHeaderStatus(PutAwayHeaderUpdateEvent event) {
        kafkaTemplate.send("putawayheader-update-topic-v1", event);
    }

    // Inventory Creation
    public void createInventory(InventoryEvent event) {
        kafkaTemplate.send("inventory-created-topic-v1", event);
    }

    // StorageBin update
    public void updateStorageBin(StorageBinUpdateEvent event) {
        kafkaTemplate.send("storageBin-update-topic-v1", event);
    }

    // UpdateInboundLine
    public void updateInboundLine(InboundLineUpdateEvent event) {
        kafkaTemplate.send("inboundline-update-topic-v1", event);
    }

    // InboundConfirmValidation
    public void updateInboundConfirmValidation(InboundConfirmValidationEvent event) {
        kafkaTemplate.send("inboundconfirmvalidation-topic-v1", event);
    }

    public void updateIBReceivedLinesCount(IBHeaderReceivedLinesEvent event) {
        kafkaTemplate.send("update-ibreceivedlines-count-topic-v1", event);
    }
}
