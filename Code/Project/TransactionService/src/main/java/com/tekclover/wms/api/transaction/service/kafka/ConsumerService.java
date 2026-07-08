package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.*;
import com.tekclover.wms.api.transaction.repository.*;
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
    @Autowired
    QualityLineRepository qualityLineRepository;
    @Autowired
    QualityHeaderRepository qualityHeaderRepository;
    @Autowired
    OutboundLineInterimRepository outboundLineInterimRepository;
    @Autowired
    PickupHeaderRepository pickupHeaderRepository;
    @Autowired
    PickupLineRepository pickupLineRepository;


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

    // OutboundLine Reversal Process
    @KafkaListener(topics = "outbound-line-reversal-topic-v1", groupId = "outbound-line-reversal-group-v1", containerFactory = "outboundLineReversalListenerFactory")
    public void outboundLineConsume(OutboundLineReverseEvent event) throws Exception {
        log.info("OutboundLine Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        int outboundLine = outboundLineRepository.updateOutboundLineInReversal(event.getWarehouseId(), event.getRefDocNumber(), event.getItemCode(), 0D, event.getStatusId(), event.getLoginUserID(), new Date());
        log.info("OutboundLine Reversal Process Completed In this REF_DOC_NO {} And ItemCode {}, Affected Row's {} ", event.getRefDocNumber(), event.getItemCode(), outboundLine);
    }

    // QualityLine Reversal Process
    @KafkaListener(topics = "quality-line-reversal-topic-v1", groupId = "quality-line-reversal-group-v1", containerFactory = "qualityLineReversalListenerFactory")
    public void qualityLineConsume(OutboundLineReverseEvent event) throws Exception {
        log.info("QualityLine Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        int qualityLine = qualityLineRepository.deleteQualityLineInReversal(1L, event.getLoginUserID(), new Date(), event.getWarehouseId(), event.getRefDocNumber(), event.getItemCode());
        log.info("QualityLine Reversal Process Completed In this REF_DOC_NO {} And ItemCode {}, Affected Row's {} ", event.getRefDocNumber(), event.getItemCode(), qualityLine);
    }

    // QualityHeader Reversal Process
    @KafkaListener(topics = "quality-header-reversal-topic-v1", groupId = "quality-header-reversal-group-v1", containerFactory = "qualityHeaderReversalListenerFactory")
    public void qualityHeaderConsume(QualityHeaderReverseEvent event) throws Exception {
        log.info("QualityHeader Reversal Process Started In this REF_DOC_NO {} ", event.getRefDocNumber());
        int qualityHeader = qualityHeaderRepository.deleteQualityHeader(1L, event.getLoginUserID(), new Date(), event.getWarehouseId(), event.getRefDocNumber(), event.getPickupNumber());
        log.info("QualityHeader Reversal Process Completed In this REF_DOC_NO {} Affected Row's {} ", event.getRefDocNumber(), qualityHeader);
    }

    // OutboundLine Interim Reversal Process
    @KafkaListener(topics = "outboundline-interim-reversal-topic-v1", groupId = "outboundline-interim-reversal-group-v1", containerFactory = "outboundLineInterimReversalListenerFactory")
    public void outboundLineInterimConsume(OutboundLineReverseEvent event) throws Exception {
        log.info("OutboundLineInterim Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        int outboundLine = outboundLineInterimRepository.deleteOutboundLineInterimReversal(1L, event.getLoginUserID(), new Date(), event.getWarehouseId(), event.getRefDocNumber(), event.getItemCode());
        log.info("OutboundLineInterim Reversal Process Completed In this REF_DOC_NO {} And ItemCode {}, Affected Row's {} ", event.getRefDocNumber(), event.getItemCode(), outboundLine);
    }

    // PickupHeader Reversal Process
    @KafkaListener(topics = "pickup-header-reversal-topic-v1", groupId = "pickup-header-reversal-group-v1", containerFactory = "pickupHeaderReversalListenerFactory")
    public void pickupHeaderConsume(OutboundLineReverseEvent event) throws Exception {
        log.info("PickupHeader Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        int pickupHeader = pickupHeaderRepository.deletePickupHeader(1L, event.getLoginUserID(), new Date(), event.getWarehouseId(), event.getItemCode(), event.getRefDocNumber());
        log.info("PickupHeader Reversal Process Completed In this REF_DOC_NO {} And ItemCode {}, Affected Row's {} ", event.getRefDocNumber(), event.getItemCode(), pickupHeader);

        log.info("PickupLine Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        int pickupLine = pickupLineRepository.deletePickupLine(1L, event.getLoginUserID(), new Date(), event.getWarehouseId(), event.getItemCode(), event.getRefDocNumber());
        log.info("PickupLine Reversal Process Completed In this REF_DOC_NO {} And ItemCode {}, Affected Row's {} ", event.getRefDocNumber(), event.getItemCode(), pickupLine);


    }

    // PickupHeader Reversal Process
    @KafkaListener(topics = "order-management-line-reversal-topic-v1", groupId = "order-management-line-reversal-group-v1", containerFactory = "orderManagementLineReversalListenerFactory")
    public void orderManagementLineConsume(OutboundLineReverseEvent event) throws Exception {
        log.info("OrderManagementLine Reversal Process Started In this REF_DOC_NO {} And ItemCode {}", event.getRefDocNumber(), event.getItemCode());
        outboundLineService.updateOrderManagementLineForReversal(event.getRefDocNumber(), event.getItemCode(), event.getWarehouseId(), event.getLoginUserID());
        log.info("OrderManagementLine Reversal Process Completed In this REF_DOC_NO {} And ItemCode {} ", event.getRefDocNumber(), event.getItemCode());
    }
}
