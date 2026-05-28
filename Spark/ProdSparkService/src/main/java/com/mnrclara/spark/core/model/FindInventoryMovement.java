package com.mnrclara.spark.core.model;

import lombok.Data;

import java.util.List;

@Data
public class FindInventoryMovement {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
}
