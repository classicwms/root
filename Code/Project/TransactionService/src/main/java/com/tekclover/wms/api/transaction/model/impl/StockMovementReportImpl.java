package com.tekclover.wms.api.transaction.model.impl;

import java.util.Date;

public interface StockMovementReportImpl {
    String getWarehouseId();
    String getManufacturerSKU();
    String getItemCode();
    String getItemText();
    String getDocumentType();
    String getDocumentNumber();
    Double getMovementQty();
    String getCustomerCode();
    Date getConfirmedOn();
    Double getBalanceOHQty();
    Double getOpeningStock();
}
