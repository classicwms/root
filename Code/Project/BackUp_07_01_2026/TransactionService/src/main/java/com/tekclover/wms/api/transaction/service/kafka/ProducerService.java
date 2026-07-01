package com.tekclover.wms.api.transaction.service.kafka;

import com.tekclover.wms.api.transaction.model.kafka.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // OutboundHeader Status Update
    public void outboundHeaderStatusUpdate(OutboundHeaderUpdateEvent event) {
        kafkaTemplate.send("outbound-header-update-topic-v1", event);
    }

    // Inventory Update
    public void inventoryInDeliveryConfirm(DeliveryConfirmEvent event) {
        kafkaTemplate.send("delivery-confirm-topic-v1", event);
    }

    // OutboundLine Status Update
    public void outboundLineStatusUpdate(OutboundLineStatusUpdate event) {
        kafkaTemplate.send("outbound-line-update-topic-v1", event);
    }

    // PreoutboundLine Status Update
    public void preOutboundLineStatusUpdate(PreOutboundLineStatusUpdateEvent event) {
        kafkaTemplate.send("preoutbound-line-update-topic-v1", event);
    }

    // PreOutboundHeader Status Update
    public void preOutboundHeaderStatusUpdate(PreOutboundHeaderStatusEvent event) {
        kafkaTemplate.send("preoutbound-header-update-topic-v2", event);
    }
}
