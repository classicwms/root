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

    // OutboundLine Reversal
    public void outboundLineReversal(OutboundLineReverseEvent event) {
        kafkaTemplate.send("outbound-line-reversal-topic-v1", event);
    }

    // OutboundLine Reversal
    public void qualityLineReversal(OutboundLineReverseEvent event) {
        kafkaTemplate.send("quality-line-reversal-topic-v1", event);
    }

    // QualityHeader Reversal
    public void qualityHeaderReversal(QualityHeaderReverseEvent event) {
        kafkaTemplate.send("quality-header-reversal-topic-v1", event);
    }

    // QualityLineInterim Reversal
    public void outboundLineInterimReversal(OutboundLineReverseEvent event) {
        kafkaTemplate.send("outboundline-interim-reversal-topic-v1", event);
    }

    // QualityLineInterim Reversal
    public void pickupHeaderReversal(OutboundLineReverseEvent event) {
        kafkaTemplate.send("pickup-header-reversal-topic-v1", event);
    }

    public void orderManagementLineReversal(OutboundLineReverseEvent outboundLineReverseEvent) {
        kafkaTemplate.send("order-management-line-reversal-topic-v1", outboundLineReverseEvent);
    }
}
