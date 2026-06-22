package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.model.kafka.*;
import com.tekclover.wms.api.transaction.model.outbound.pickup.v2.PickupLineV2;
import com.tekclover.wms.api.transaction.repository.*;
import com.tekclover.wms.api.transaction.service.OutboundLineService;
import com.tekclover.wms.api.transaction.service.PickupLineService;
import com.tekclover.wms.api.transaction.service.QualityLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        int qualityHeader= qualityHeaderV2Repository.updateQualityHeader(event.getCompanyCodeId(), event.getPlantId(),
                event.getLanguageId(), event.getWarehouseId(), event.getQualityInspectionNo(),
                event.getStatusId(), event.getStatusDescription(), event.getQualityCreatedBy());
        log.info("QualityHeader Updated Affected Row's: {} ", qualityHeader);
    }

    // OutboundLine Interim Save
    @KafkaListener(topics = "outboundlineinterim-save-topic-v1", groupId = "outboundlineinterim-save-group-v1", containerFactory = "outboundlineInterimListenerFactory")
    public void consume(OutboundLineInterimSaveEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
        log.info("OutboundLine Interim Save Event {} ", event.getOutboundLineInterimList());
        outboundLineInterimRepository.saveAll(event.getOutboundLineInterimList());
    }
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
        outboundLineService.deliveryConfirmationInKafka(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
                event.getPreOutboundNo(), event.getRefDocNumber(), event.getPartnerCode(), event.getLoginUserID());
    }
    @KafkaListener(topics = "qualityline-process-topic-v1", groupId = "qualityline-confirm-group-v1", containerFactory = "qualityLineProcessEventConsumerFactory")
    public void consume(QualityLineCreateEvent event) throws ParseException, InvocationTargetException, IllegalAccessException {
        qualityLineService.createQualityLineInKafka(event.getQualityLineV2s(), event.getLoginUserID());
    }

}
