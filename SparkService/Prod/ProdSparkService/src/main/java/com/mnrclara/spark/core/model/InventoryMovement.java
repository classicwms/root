package com.mnrclara.spark.core.model;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InventoryMovement {

    private String warehouseId;
    private String itemCode;
    private String description;
    private String packBarcodes;
    private String storageBin;
    private Long movementType;
    private Long submovementType;
    private String refDocNumber;
    private Double movementQty;
    private String inventoryUom;
    private Timestamp createdOn;
}
