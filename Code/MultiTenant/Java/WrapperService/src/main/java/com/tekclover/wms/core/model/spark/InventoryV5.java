package com.tekclover.wms.core.model.spark;


import lombok.Data;

@Data
public class InventoryV5 {

    private String barcodeId;
    private Double inventoryQuantity;
    private String itemCode;
    private Double referenceField4;
    private Double allocatedQuantity;
}
