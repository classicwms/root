package com.tekclover.wms.api.outbound.transaction.model.report;


import java.util.Date;

public interface HistoryReport {
         String getCompanyCodeId();
         String getPlantId();
        String getLanguageId();
         String getWarehouseId();
         String getItemCode();
        String getItemDescription();
        String getCompanyDescription();
        String getPlantDescription();
        String getWarehouseDescription();
         String getManufacturerName();
        Double getVariance();
        Double getClosingStock();
        Double getOpeningStock();
        Double getInboundQty();
         Double getOutboundQty();
        Double getStockAdjustmentQty();
        Double getSystemInventory();

        String getBatchNo();
        Date getDate();
        Date getMfg();
        String getUom();
        String getCustomerName();
    String getInventoryOwner();
}
