package com.tekclover.wms.api.outbound.transaction.model.impl;

import java.util.Date;

public interface StockMovementReportImpl {
    String getWarehouseId();
    String getWarehouseDescription();
    String getCompanyCodeId();
    String getCompanyDescription();
    String getPlantId();
    String getPlantDescription();
    String getStatusDescription();
    String getLanguageId();
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
    String getStorageBin();
}
