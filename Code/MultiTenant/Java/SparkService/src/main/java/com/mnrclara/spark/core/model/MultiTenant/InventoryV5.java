package com.mnrclara.spark.core.model.MultiTenant;


import lombok.Data;

@Data
public class InventoryV5 {

    private String barcodeId;
    private Double inventoryQuantity;
    private String itemCode;
    private Double referenceField4;
    private Double allocatedQuantity;
}
