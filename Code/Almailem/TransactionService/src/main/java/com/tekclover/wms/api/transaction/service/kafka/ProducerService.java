package com.tekclover.wms.api.transaction.service.kafka;

import com.tekclover.wms.api.transaction.model.kafka.PickupLineCreateEvent;
import com.tekclover.wms.api.transaction.model.kafka.PickupLineEvent;
import com.tekclover.wms.api.transaction.model.kafka.UpdatePickupHeaderEvent;
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

}
