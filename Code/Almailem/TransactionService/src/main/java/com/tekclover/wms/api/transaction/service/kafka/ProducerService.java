package com.tekclover.wms.api.transaction.service.kafka;

import com.tekclover.wms.api.transaction.model.kafka.*;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    //
    public void pickupLineProcess(PickupLineEvent event) {
        kafkaTemplate.send("pickupline-topic-v1", event);
    }

    // PutAwayLine Save
    public void savePickupLine(PickupLineCreateEvent event) {
        kafkaTemplate.send("pickupline-save-topic-v1", event);
    }

    // Update PickupHeader
    public void updatePickupHeader(UpdatePickupHeaderEvent event) {
        kafkaTemplate.send("pickupheader-update-topic-v1", event);
    }

    // QualityLine Save
    public void qualityLineSave(QualityLineSaveEvent event) {
        kafkaTemplate.send("qualityline-save-topic-v1", event);
    }

    // QualityHeader Update
    public void qualityHeaderUpdate(QualityHeaderUpdateEvent event) {
        kafkaTemplate.send("qualityheader-update-topic-v1", event);
    }

//    // OutboundLineInterim Save
//    public void outboundLineInterimSave(OutboundLineInterimSaveEvent event) {
//        kafkaTemplate.send("outboundlineinterim-save-topic-v1", event);
//    }

    //    // DLV_QTY Update
//    public void dlvQTYUpdate(QualityLineSaveEvent event) {
//        kafkaTemplate.send("dlv_qty-update-topic-v1", event);
//    }
    // Delivery Confirm
    public void deliveryConfirm(DeliveryConfirmEvent event) {
        kafkaTemplate.send("delivery-confirm-topic-v1", event);
    }
}
