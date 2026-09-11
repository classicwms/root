package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.*;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupHeaderV2;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.service.OrderManagementLineService;
import com.tekclover.wms.api.transaction.service.OutboundLineService;
import com.tekclover.wms.api.transaction.service.PickupLineService;
import com.tekclover.wms.api.transaction.service.QualityLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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
    @Autowired
    QualityLineService qualityLineService;
    @Autowired
    QualityHeaderV2Repository qualityHeaderV2Repository;
    @Autowired
    OutboundLineInterimRepository outboundLineInterimRepository;
    @Autowired
    QualityLineV2Repository qualityLineV2Repository;
    @Autowired
    OutboundLineService outboundLineService;
    @Autowired
    PreOutboundHeaderV2Repository preOutboundHeaderV2Repository;
    @Autowired
    OutboundHeaderV2Repository outboundHeaderV2Repository;
    @Autowired
    OrderManagementLineService orderManagementLineService;
    @Autowired
    OrderManagementLineV2Repository orderManagementLineV2Repository;

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

    // QualityLine Creation
    @KafkaListener(topics = "qualityline-save-topic-v1", groupId = "qualityline-save-group-v1", containerFactory = "qualitylineListenerFactory")
    public void saveConsume(QualityLineSaveEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
        log.info("Quality Line Create Event {} ", event);
        qualityLineV2Repository.saveAll(event.getQualityLineV2List());
//        qualityLineService.createQualityLineV2(event.getQualityLineV2s(), event.getLoginUserID());
    }

    // QualityHeader Update
    @KafkaListener(topics = "qualityheader-update-topic-v1", groupId = "qualityheader-update-group-v1", containerFactory = "qualityHeaderUpdateListenerFactory")
    public void consume(QualityHeaderUpdateEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
        log.info("Quality Header Update Event {} ", event);
        int qualityHeader= qualityHeaderV2Repository.updateQualityHeader(event.getStatusDescription(), event.getQualityInspectionNo() );
        log.info("QualityHeader Updated Affected Row's: {} ", qualityHeader);
    }

    // OutboundLine Interim Save
//    @KafkaListener(topics = "outboundlineinterim-save-topic-v1", groupId = "outboundlineinterim-save-group-v1", containerFactory = "outboundlineInterimListenerFactory")
//    public void consume(OutboundLineInterimSaveEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
//        log.info("OutboundLine Interim Save Event {} ", event.getOutboundLineInterimList());
//        outboundLineInterimRepository.saveAll(event.getOutboundLineInterimList());
//    }
//    // DLV_QTY Update
//    @KafkaListener(topics = "dlv_qty-update-topic-v1", groupId = "dlv_qty-update-group-v1", containerFactory = "dlvQtyInterimListenerFactory")
//    public void consume(QualityLineSaveEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
//        log.info("DLV_QTY Update Save Event {} ", event.getQualityLineV2List());
//        qualityLineService.updateDeliveryQty(event.getQualityLineV2List());
//    }
    // Delivery Confirm
    @KafkaListener(topics = "delivery-confirm-topic-v1", groupId = "delivery-confirm-group-v1", containerFactory = "deliveryConfirmInterimListenerFactory")
    public void consume(DeliveryConfirmEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
//        qualityLineService.postDeliveryConfirm(event.getQualityLineV2List(), event.getLoginUserID());
        log.info("Delivery Confirm Process Started from Kafka: {} ", event);
        outboundLineService.deliveryConfirmationV2Kafka(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
                event.getPreOutboundNo(), event.getRefDocNumber(), event.getPartnerCode(), event.getLoginUserID(), event.getLineNumbers());
    }


    // New PreObHeader Update
    @KafkaListener(topics = "preobheader-status-update-topic-v1", groupId = "preobheader-status-update-group-v1", containerFactory = "updatePreObHeaderStatusListenerFactory")
    public void consume(UpdatePreOutboundHeaderStatus event) {
        log.info("Update PreOutbound Line Event {}", event);
        preOutboundHeaderV2Repository.updatePreOutboundHeaderStatusV2New(event.getCompanyId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
                event.getRefDocNo(), event.getPreOutboundNo(), event.getStatusId(), event.getStatusDescription(), event.getLoginUserID() ,new Date());
    }

    // New OutboundHeader Update
    @KafkaListener(topics = "obheader-status-update-topic-v1", groupId = "obheader-status-update-group-v1", containerFactory = "updateObHeaderStatusListenerFactory")
    public void consume(UpdateOutboundHeaderStatus event) {
        log.info("Update Outbound Line Event {}", event);
        outboundHeaderV2Repository.updateOutboundHeaderStatusV2New(event.getCompanyId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
                event.getRefDocNo(), event.getPreOutboundNo(), event.getStatusId(), event.getStatusDescription(), event.getLoginUserID() ,new Date());
    }

    // QualityLine Creation Process
    @KafkaListener(topics = "qualityline-create-topic-v1", groupId = "qualityline-create-group-v1", containerFactory = "qualityLineProcessListenerFactory")
    public void consume(QualityLineCreateEvent event) throws Exception {
        qualityLineService.createQualityLineV2(event.getQualityLineV2s(), event.getLoginUserID());
    }

    // AssignPicker
    @KafkaListener(topics = "assign-picker-topic-v1", groupId = "assign-picker-group-v1", containerFactory = "assignPickerListenerFactory")
    public void consume(AssignPickerEvent event) throws Exception {
        log.info("Assign Picker Event {}", event);
        orderManagementLineService.doAssignPickerV2(event.getAssignPickers(), event.getAssignedPickerId(), event.getLoginUserID());
        log.info("Assign Picker Event Completed {}", event);
    }

    // Save PickupHeader
    @KafkaListener(topics = "save-pickupheader-topic-v1", groupId = "pickupheader-save-group-v1", containerFactory = "pickupHeaderSaveListenerFactory")
    public void consume(PickupHeaderEvent event) throws Exception {
        log.info("Save PickupHeader Event {}", event);
        List<PickupHeaderV2> pickupHeaderList = pickupHeaderV2Repository.saveAll(event.getPickupHeaderV2List());
        log.info("Save PickupHeader Event Completed {}", event);

        pickupHeaderList.stream().forEach(ph -> {
            int orderLine = orderManagementLineV2Repository.updateOrderManagementLineV2(ph.getCompanyCodeId(), ph.getPlantId(), ph.getLanguageId(),
                    ph.getWarehouseId(), ph.getPreOutboundNo(), ph.getRefDocNumber(), ph.getPartnerCode(), ph.getLineNumber(), ph.getItemCode(), 48L,
                    event.getStatusDescription(), event.getAssignPickerId(), ph.getPickupNumber(), event.getLoginUserID(), ph.getProposedStorageBin(), new Date());
            log.info("OrderManagementLine Updated Affected Row's: {} ", orderLine);
        });
    }
}
