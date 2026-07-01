package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.*;
import com.tekclover.wms.api.transaction.repository.OutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.OutboundLineRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundHeaderRepository;
import com.tekclover.wms.api.transaction.repository.PreOutboundLineRepository;
import com.tekclover.wms.api.transaction.service.OutboundLineService;
import com.tekclover.wms.api.transaction.service.PickupLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    @Autowired
    PickupLineService pickupLineService;
    @Autowired
    OutboundLineService outboundLineService;
    @Autowired
    OutboundHeaderRepository outboundHeaderRepository;
    @Autowired
    OutboundLineRepository outboundLineRepository;
    @Autowired
    PreOutboundLineRepository preOutboundLineRepository;
    @Autowired
    PreOutboundHeaderRepository preOutboundHeaderRepository;

    // OutboundHeader Update Process
    @KafkaListener(topics = "outbound-header-update-topic-v1", groupId = "outbound-header-update-group-v1", containerFactory = "outboundHeaderListenerFactory")
    public void consume(OutboundHeaderUpdateEvent event) throws Exception {
        log.info("OutboundHeader 59 Update Process Started In this REF_DOC_NO {}", event.getRefDocNumber());
        int outboundHeader = outboundHeaderRepository.updateOutboundHeaderStatusInDeliveryConfirm(event.getWarehouseId(), event.getRefDocNumber(), event.getStatusId(), new Date());
        log.info("OutboundHeader 59 Update Process Completed in this REF_DOC_NO {}, Affected Row's {}", event.getRefDocNumber(), outboundHeader);
    }

    // DeliveryConfirm Process
    @KafkaListener(topics = "delivery-confirm-topic-v1", groupId = "delivery-confirm-group-v1", containerFactory = "deliveryConfirmListenerFactory")
    public void consume(DeliveryConfirmEvent event) throws Exception {
        log.info("DeliveryConfirm Inventory Update Process Started In this REF_DOC_NO {}", event.getRefDocNumber());
        outboundLineService.inventoryUpdateInDeliveryConfirm(event.getWarehouseId(), event.getPreOutboundNo(), event.getRefDocNumber(), event.getPartnerCode());
        log.info("DeliveryConfirm Inventory Update Process Completed In this REF_DOC_NO {}", event.getRefDocNumber());
    }

    // OutboundLine Update Process
    @KafkaListener(topics = "outbound-line-update-topic-v1", groupId = "outbound-line-update-group-v1", containerFactory = "outboundLineUpdateListenerFactory")
    public void consume(OutboundLineStatusUpdate event) throws Exception {
        log.info("OutboundLine Update Process Started In this REF_DOC_NO {}", event.getRefDocNumber());
        int outboundLine = outboundLineRepository.updateOutboundLineStatus (event.getWarehouseId(), event.getRefDocNumber(), event.getStatusID(), new Date());
        log.info("OutboundLine Update Process Completed In this REF_DOC_NO {}, Affected Row's {} ", event.getRefDocNumber(), outboundLine);
    }

    // PreOutboundLine Update Process
    @KafkaListener(topics = "preoutbound-line-update-topic-v1", groupId = "preoutbound-line-update-group-v1", containerFactory = "preOutboundLineUpdateListenerFactory")
    public void preOutboundLineConsume(PreOutboundLineStatusUpdateEvent event) throws Exception {
        log.info("PreOutboundLine Update Process Started In this REF_DOC_NO {}", event.getRefDocNumber());
        int outboundLine = preOutboundLineRepository.updateLineStatus (event.getWarehouseId(), event.getRefDocNumber(), event.getStatusID());
        log.info("PreOutboundLine Update Process Completed In this REF_DOC_NO {}, Affected Row's {} ", event.getRefDocNumber(), outboundLine);
    }

    // PreOutboundHeader Process
    @KafkaListener(topics = "preoutbound-header-update-topic-v2", groupId = "preoutbound-header-update-group-v2", containerFactory = "preOutboundHeaderUpdateListenerFactory")
    public void preOutboundHeaderConsume(PreOutboundHeaderStatusEvent event) throws Exception {
        log.info("PreOutboundHeader Update Process Started In this REF_DOC_NO {}", event.getRefDocNumber());
        String status = outboundHeaderRepository.findStatusDescription(59L, event.getWarehouseId());
        log.info("Status Id: {} Status Text: {} ", 59L, status);
        int preOutboundHeader = preOutboundHeaderRepository.updateHeaderStatus(event.getWarehouseId(), event.getRefDocNumber(), event.getStatusID(), status);
        log.info("PreOutboundHeader Update Process Completed In this REF_DOC_NO {}, Affected Row's {} ", event.getRefDocNumber(), preOutboundHeader);
    }

}
