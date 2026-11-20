package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferHeaderEvent;
import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferOrderEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void saveInhouseTransferHeaderEvent(InhouseTransferHeaderEvent event) {
        kafkaTemplate.send("inhouse-transfer-save-topic-v2", event);
    }

    public void transferProcessInInventory(InhouseTransferInventoryEvent event) {
        kafkaTemplate.send("inventory-transfer-topic-v2", event);
    }

    public void saveInhouseOrderInHht(TransferOrderEvent event) {
        kafkaTemplate.send("transfer-order-hht-topic-v1", event);
    }

    public void transferOrderInInventory(TransferInventoryEvent event) {
        kafkaTemplate.send("transfer-inventory-hht-topic-v1", event);
    }
}
