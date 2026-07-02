package com.tekclover.wms.api.transaction.service;

import com.tekclover.wms.api.transaction.model.warehouse.stockAdjustment.StockAdjustment;
import com.tekclover.wms.api.transaction.repository.StockAdjustmentMiddlewareRepository;
import com.tekclover.wms.api.transaction.repository.WarehouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class StockAdjustmentMiddlewareService extends BaseService {
    @Autowired
    StockAdjustmentMiddlewareRepository stockAdjustmentMiddlewareRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    InventoryService inventoryService;

    /**
     * @param stockAdjustment
     * @return
     */
    public StockAdjustment createStockAdjustment(StockAdjustment stockAdjustment) {

        if (stockAdjustment != null) {
            StockAdjustment createStockAdjustment = stockAdjustmentMiddlewareRepository.save(stockAdjustment);
            log.info("StockAdjustment Created: " + createStockAdjustment);
            return createStockAdjustment;
        }
        return null;
    }

    /**
     * @param stockAdjustmentId
     */
    public void updateProcessedOrderV2(Long stockAdjustmentId, Long processStatusId) {
        StockAdjustment dbStockAdjustment = stockAdjustmentMiddlewareRepository.findByStockAdjustmentId(stockAdjustmentId);
        log.info("StockAdjustmentId : " + stockAdjustmentId);
        log.info("dbStockAdjustmentId : " + dbStockAdjustment);
        if (dbStockAdjustment != null) {
            dbStockAdjustment.setProcessedStatusId(processStatusId);
            dbStockAdjustment.setOrderProcessedOn(new Date());
            stockAdjustmentMiddlewareRepository.save(dbStockAdjustment);
        }
    }
}