package com.tekclover.wms.api.outbound.transaction.model.report;

public interface StockReportRes {
    String getCompanyCodeId();

    String getPlantId();

    String getLanguageId();

    String getWarehouseId();

    String getItemCode();            // ITM_CODE

    String getManufacturerName();    // MFR_SKU

    String getItemText();            // ITEM_TEXT

    Double getInvQty();            // INV_QTY

    Double getAllocQty();            // Alloc Qty

    Double getTotalQty();                // Total Qty

    String getCompanyDescription();

    String getPlantDescription();

    String getWarehouseDescription();
}
