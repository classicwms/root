package com.tekclover.wms.api.transaction.service.kafka;


import com.tekclover.wms.api.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferHeaderEvent;
import com.tekclover.wms.api.transaction.model.kafka.InhouseTransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferInventoryEvent;
import com.tekclover.wms.api.transaction.model.kafka.TransferOrderEvent;
import com.tekclover.wms.api.transaction.model.mnc.AddInhouseTransferLine;
import com.tekclover.wms.api.transaction.service.BaseService;
import com.tekclover.wms.api.transaction.service.InhouseTransferHeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService extends BaseService {


    @Autowired
    InhouseTransferHeaderService inhouseTransferHeaderService;

    @KafkaListener(topics = "inhouse-transfer-save-topic-v2", groupId = "inhouse-transfer-save-group-v2", containerFactory = "inhouseTransferListenerFactory")
    public void consume(InhouseTransferHeaderEvent event) {
        try {
            String profile = getDataBase(event.getInhouseTransferHeader().getPlantId(), event.getInhouseTransferHeader().getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("Current DB " + profile);
            inhouseTransferHeaderService.createInHouseTransferHeaderV2(event.getInhouseTransferHeader(), event.getLoginUserID());
        } catch (Exception e) {
            log.info("InhouseTransfer process exception :{} ", e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @KafkaListener(topics = "inventory-transfer-topic-v2", groupId = "inventory-transfer-group-v2", containerFactory = "transferInventoryListenerFactory")
    public void consume(InhouseTransferInventoryEvent event) {

        try {
            String profile = getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("Current DB " + profile);
            inhouseTransferHeaderService.updateInventoryInKafka(event.getCompanyCodeId(), event.getLanguageId(), event.getPlantId(), event.getWarehouseId(), event.getLoginUserID(), event.getInhouseTransferLine());
        } catch (Exception e) {
            log.info("Inhouse Transfer Inventory Process Exception {} ", e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @KafkaListener(topics = "transfer-inventory-hht-topic-v1", groupId = "transfer-inventory-hht-group-v1", containerFactory = "transferInventoryHhtListenerFactory")
    public void consume(TransferInventoryEvent event) {

        try {
            String profile = getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("Current DB " + profile);
            inhouseTransferHeaderService.updateInventoryInHht(event.getCompanyCodeId(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(), event.getLoginUserID(), event.getInhouseTransferLine());
        } catch (Exception e) {
            log.info("Inhouse Transfer Inventory Process Exception {} ", e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @KafkaListener(topics = "transfer-order-hht-topic-v1", groupId = "transfer-order-hht-group-v1", containerFactory = "inhouseTransferInHhtListenerFactory")
    public void consume(TransferOrderEvent event) {
        try {
            String profile = getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(profile);
            log.info("Current DB " + profile);
            inhouseTransferHeaderService.saveTransferOrder(event);
        } catch (Exception e) {
            log.info("InhouseTransfer process exception :{} ", e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

}
