package com.tekclover.wms.api.transaction.model.impl;

import java.util.Date;

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
