package com.tekclover.wms.api.outbound.transaction.model.impl;

public interface StockReportImpl {

    String getLanguageId();

    String getCompanyCodeId();

    String getPlantId();

    String getWarehouseId();

    String getManufacturerSKU();

    String getItemCode();

    String getItemText();

    Double getOnHandQty();

    Double getDamageQty();

    Double getHoldQty();

    Double getAvailableQty();
    Double getInvQty();

    Double getAllocQty();

    Double getTotalQty();

    String getCompanyDescription();

    String getPlantDescription();

    String getWarehouseDescription();

    String getBarcodeId();

    String getManufacturerName();
}
