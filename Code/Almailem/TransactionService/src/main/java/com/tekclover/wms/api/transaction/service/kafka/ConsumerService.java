package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.PickupLineCreateEvent;
import com.tekclover.wms.api.transaction.model.kafka.PickupLineEvent;
import com.tekclover.wms.api.transaction.model.kafka.UpdatePickupHeaderEvent;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.repository.PickupHeaderV2Repository;
import com.tekclover.wms.api.transaction.repository.PickupLineV2Repository;
import com.tekclover.wms.api.transaction.service.PickupLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    @Autowired
    PickupLineV2Repository pickupLineV2Repository;

    @Autowired
    PickupLineService pickupLineService;

    @Autowired
    PickupHeaderV2Repository pickupHeaderV2Repository;

    // PickupLine Creation Process
    @KafkaListener(topics = "pickupline-topic-v1", groupId = "pickupline-group-v1", containerFactory = "pickupLineListenerFactory")
    public void consume(PickupLineEvent event) throws Exception {
        pickupLineService.createPickupLineNonCBMV2(event.getPickupLines(), event.getLoginUserID());
    }

    // PickupLine Save
    @KafkaListener(topics = "pickupline-save-topic-v1", groupId = "pickupline-save-group-v1", containerFactory = "pickupLineSaveListenerFactory")
    public void consume(PickupLineCreateEvent event) {
        List<PickupLineV2> pickupLineV2List = event.getPickupLineV2List();
        log.info("Saving {} records", pickupLineV2List.size());
        pickupLineV2Repository.saveAll(pickupLineV2List);
    }

    // Inventory Creation
    @KafkaListener(topics = "pickupheader-update-topic-v1", groupId = "pickupHeader-update-group-v1", containerFactory = "updatePickupHeaderListenerFactory")
    public void consume(UpdatePickupHeaderEvent event) {
        log.info("Update PickupHeader Event {}", event);
        pickupHeaderV2Repository.updatePickupheader(event.getRefDocNumber(), event.getPickupNumber(), event.getStatusId(), event.getStatusDescription(), event.getLoginUserID(), new Date());
    }

}
