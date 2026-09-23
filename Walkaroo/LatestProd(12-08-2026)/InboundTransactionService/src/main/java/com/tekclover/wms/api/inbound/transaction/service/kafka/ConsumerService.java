package com.tekclover.wms.api.inbound.transaction.service.kafka;


import com.tekclover.wms.api.inbound.transaction.config.dynamicConfig.DataBaseContextHolder;
import com.tekclover.wms.api.inbound.transaction.model.kafka.event.*;
import com.tekclover.wms.api.inbound.transaction.repository.DbConfigRepository;
import com.tekclover.wms.api.inbound.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.PutAwayLineV2Repository;
import com.tekclover.wms.api.inbound.transaction.repository.StorageBinV2Repository;
import com.tekclover.wms.api.inbound.transaction.service.BaseService;
import com.tekclover.wms.api.inbound.transaction.service.PutAwayLineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

    @Autowired
    PutAwayLineV2Repository putAwayLineV2Repository;
    @Autowired
    PutAwayLineService putAwayLineService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    @Autowired
    StorageBinV2Repository storageBinV2Repository;

    @Autowired
    DbConfigRepository dbConfigRepository;

    @Autowired
    BaseService baseService;

    // PutAwayLine Confirmation
    @KafkaListener(topics = "putawayline-topic-v6", groupId = "putawayline-group-v5", containerFactory = "putAwayListenerFactory")
    public void consume(PutAwayLineProcessEvent event) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPutAwayLines().get(0).getPlantId(), event.getPutAwayLines().get(0).getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            putAwayLineService.putAwayLineConfirmKafka(event.getPutAwayLines(), event.getLoginUserID());
        } catch (Exception e) {
           log.info("PutAwayLine Exception In Kafka" + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // PutAwayLine Save
    @KafkaListener(topics = "putawayline-save-topic-v2", groupId = "putawayline-save-group-v2", containerFactory = "putAwaySaveListenerFactory")
    public void consume(PutAwayLineCreatedEvent event) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPutAwayLineV2List().get(0).getPlantId(), event.getPutAwayLineV2List().get(0).getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
//            List<PutAwayLineV2> putAwayLineV2List = event.getPutAwayLineV2List();
            log.info("Saving {} records", event.getPutAwayLineV2List().size());
            putAwayLineV2Repository.saveAll(event.getPutAwayLineV2List());
        } catch (Exception e) {
            log.info("PutAwayLine Save Exception " + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }


    // Inventory Creation
    @KafkaListener(topics = "inventory-created-topic-v1", groupId = "inventory-save-group-v1", containerFactory = "inventoryCreatedListenerFactory")
    public void consume(InventoryEvent event) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getInventoryV2().getPlantId(), event.getInventoryV2().getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("Inventory ---> {}", event.getInventoryV2());
            inventoryV2Repository.save(event.getInventoryV2());
        } catch (Exception e) {
            log.info("Inventory Save In Kafka Exception " + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // StorageBin Update
    @KafkaListener(topics = "storageBin-update-topic-v1", groupId = "storageBin-update-group-v1", containerFactory = "storageBinUpdateListenerFactory")
    public void updateStorageBin(StorageBinUpdateEvent event) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("Update StorageBin input ----> {}", event);
            storageBinV2Repository.updateStorageBin(event.getCompanyCode(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(), event.getStorageBin(), 1L);
        } catch (Exception e) {
            log.info("StorageBin Update Exception In Kafka " + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    // Update InboundLine
 /*   @KafkaListener(topics = "inboundline-update-topic-v1", groupId = "inboundline-update-group-v1", containerFactory = "updateInboundLineListenerFactory")
    public void updateInboundLine(InboundLineUpdateEvent event) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPutAwayLine().getPlantId(), event.getPutAwayLine().getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("UpdateInboundLine input ----> {}", event);
            putAwayLineService.updateInboudLine(event.getPutAwayLine());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            DataBaseContextHolder.clear();
        }
    }

  */

    // Update  InboundConfirmValidation
    @KafkaListener(topics = "inboundconfirmvalidation-topic-v1", groupId = "inboundconfirmvalidation-group-v1", containerFactory = "updateInboundConfirmValidationListenerFactory")
    public void updateInboundConfirmValidation(InboundConfirmValidationEvent event) throws Exception {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("updateInboundConfirmValidation input ----> {}", event);
            putAwayLineService.inboundConfirmValidationInKafka(event.getCompanyCode(), event.getPlantId(), event.getLanguageId(),
                    event.getWarehouseId(), event.getRefDocNumber(),
                    event.getPreInboundNo(), event.getLoginUserID());
        } catch (Exception e) {
            log.info("InboundConfirmation Exception in Kafka " + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }

    @KafkaListener(topics = "update-ibreceivedlines-count-topic-v1", groupId = "update-ibreceivedlines-count-group-v1", containerFactory = "updateIBReceivedLinesCountListenerFactory")
    public void updateIBReceivedLinesCount(IBHeaderReceivedLinesEvent event) {
        try {
            DataBaseContextHolder.setCurrentDb("WK");
            String currentDB = baseService.getDataBase(event.getPlantId(), event.getWarehouseId());
            DataBaseContextHolder.clear();
            DataBaseContextHolder.setCurrentDb(currentDB);
            log.info("Current DB " + currentDB);
            log.info("updateIBReceivedLinesCount input ----> {}", event);

            int inboundCount = putAwayLineV2Repository.updateReceivedLinesCount(
                    event.getReceivedLines(), event.getCompanyCode(), event.getPlantId(), event.getLanguageId(), event.getWarehouseId(),
                    event.getRefDocNumber(),
                    event.getPreInboundNo());
            log.info("InboundLines Updated Count : {} ", inboundCount);
        } catch (Exception e) {
            log.info("Inbound Header Received Lines Updated Exception in kafka " + e.getMessage());
        } finally {
            DataBaseContextHolder.clear();
        }
    }
}
