package com.tekclover.wms.api.transaction.scheduler;

import com.tekclover.wms.api.transaction.model.inbound.inventory.v2.InventoryV2;
import com.tekclover.wms.api.transaction.model.tng.Fetch;
import com.tekclover.wms.api.transaction.model.tng.FetchStock;
import com.tekclover.wms.api.transaction.model.tng.PartnerInfoProjection;
import com.tekclover.wms.api.transaction.model.warehouse.inbound.WarehouseApiResponse;
import com.tekclover.wms.api.transaction.repository.ImBasicData1Repository;
import com.tekclover.wms.api.transaction.repository.ImBasicData1V2Repository;
import com.tekclover.wms.api.transaction.repository.InventoryV2Repository;
import com.tekclover.wms.api.transaction.service.IDMasterService;
import com.tekclover.wms.api.transaction.service.ScheduleAsyncService;
import com.tekclover.wms.api.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class BatchJobScheduler {

    @Autowired
    ScheduleAsyncService scheduleAsyncService;

    @Autowired
    ImBasicData1V2Repository imBasicData1V2Repository;

    @Autowired
    IDMasterService idMasterService;

    @Autowired
    InventoryV2Repository inventoryV2Repository;

    //-------------------------------------------------------------------------------------------

    @Scheduled(fixedDelay = 20000)
    public void scheduleJob() throws InterruptedException, InvocationTargetException, IllegalAccessException, ParseException {

        CompletableFuture<WarehouseApiResponse> inboundOrder = scheduleAsyncService.processInboundOrder();
        CompletableFuture<WarehouseApiResponse> perpetualStockCountOrder = scheduleAsyncService.processPerpetualStockCountOrder();
        CompletableFuture<WarehouseApiResponse> periodicStockCountOrder = scheduleAsyncService.processPeriodicStockCountOrder();
        CompletableFuture<WarehouseApiResponse> stockAdjustmentOrder = scheduleAsyncService.processStockAdjustmentOrder();
    }


    //=======================================================Inventory-Process=================================================

    @Scheduled(fixedDelay = 900000)
    public void processInventory() {

        List<String> businessPartTrueList = imBasicData1V2Repository.findByRefTrue(0L);

        List<String> sqlInventory = imBasicData1V2Repository.findByItemCode(0L, businessPartTrueList);

        if (sqlInventory != null && !sqlInventory.isEmpty()){
            Fetch fetch = new Fetch();
            fetch.setStorerKey("TAAGER");
            fetch.setSkus(sqlInventory);

            List<FetchStock> response = idMasterService.fetchStock(fetch);

            for (FetchStock fetchStock : response) {
                InventoryV2 inventory = new InventoryV2();
                inventory.setBarcodeId("88888");
                inventory.setBinClassId(1L);
                inventory.setDeletionIndicator(0L);
                inventory.setStorageBin("BIN0001");
                PartnerInfoProjection partnerData = imBasicData1V2Repository.getPartnerId(fetchStock.getSku());

                if (partnerData != null ) {
                    inventory.setThreePLPartnerId(partnerData.getPartNo() != null ? partnerData.getPartNo() : null );
                    inventory.setManufacturerName(partnerData.getPartName() != null ? partnerData.getPartName() : null);
                    inventory.setManufacturerCode(partnerData.getPartName() != null ? partnerData.getPartName() : null);
                    inventory.setCompanyDescription(partnerData.getCompanyText() != null ? partnerData.getCompanyText() : null);
                    inventory.setPlantDescription(partnerData.getPlantText() != null ? partnerData.getPlantText() : null);
                    inventory.setWarehouseDescription(partnerData.getWarehouseText() != null ? partnerData.getWarehouseText() : null);
                    String partnerText = imBasicData1V2Repository.findPartnerText(partnerData.getPartNo());
                    inventory.setThreePLPartnerText(partnerText);
                }
                inventory.setItemCode(fetchStock.getSku());
                inventory.setDescription(fetchStock.getDescription());
                inventory.setReferenceField8(fetchStock.getDescription());
                inventory.setCompanyCodeId("1000");
                inventory.setPlantId("1100");
                inventory.setWarehouseId("200");
                inventory.setLanguageId("EN");
                inventory.setInventoryQuantity(fetchStock.getAvailableQty());
                inventory.setReferenceField4(fetchStock.getAvailableQty());
                inventory.setCreatedBy("TNG");
                inventory.setCreatedOn(new Date());

                inventoryV2Repository.save(inventory);
            }
        }
    }

}