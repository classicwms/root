package com.tekclover.wms.api.enterprise.transaction.model.impl;

public interface StockReportImpl {
    String getWarehouseId();
    String getManufacturerSKU();
    String getItemCode();
    String getItemText();
    Double getOnHandQty();
    Double getDamageQty();
    Double getHoldQty();
    Double getAvailableQty();
}