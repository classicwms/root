package com.mnrclara.spark.core.model.impex;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class ImBasicData1V4 {

    private String uomId;
//    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private String manufacturerPartNo;
    private String description;
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String brand;
    private String size;
    private Long itemType;
    private Long itemGroup;
    private String createdBy;
    private Timestamp createdOn;
    private String itemTypeDescription;
    private String itemGroupDescription;
}