package com.tekclover.wms.core.model.spark;

import lombok.Data;

@Data
public class StockReport {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;            // WH_ID
    private String itemCode;            // ITM_CODE
    private String manufacturerSKU;    // MFR_SKU
    private String itemText;            // ITEM_TEXT
    private Double onHandQty;            // INV_QTY
    private Double damageQty;            // Damage Qty
    private Double holdQty;                // Hold Qty
    private Double availableQty;        // SUM (5+6+7)
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String barcodeId;
    private String manufacturerName;

}
