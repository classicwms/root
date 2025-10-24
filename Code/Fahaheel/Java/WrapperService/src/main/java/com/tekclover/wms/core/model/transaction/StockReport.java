package com.tekclover.wms.core.model.transaction;

import lombok.Data;

@Data
public class StockReport {

    private String languageId;          // LANG_ID
    private String companyCodeId;       // C_ID
    private String plantId;             // PLANT_ID
    private String warehouseId;         // WH_ID
    private String itemCode;            // ITM_CODE
    private String manufacturerSKU;     // MFR_SKU
    private String itemText;            // ITEM_TEXT
//    private Double onHandQty;           // INV_QTY
//    private Double damageQty;           // Damage Qty
//    private Double holdQty;             // Hold Qty
//    private Double availableQty;        // SUM (5+6+7)
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;

    private Double invQty;           // INV_QTY
    private Double allocQty;         // Alloc Qty
    private Double totalQty;        // SUM (5+6)

//    private String barcodeId;
    private String manufacturerName;
}
