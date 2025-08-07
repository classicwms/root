package com.mnrclara.spark.core.model.wmscorev2;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class ImBasicData1 {

    private String uomId;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String itemCode;
    private String manufacturerPartNo;
    private String description;
    private String model;
    private String specifications1;
    private String specifications2;
    private String eanUpcNo;
    private String hsnCode;
    private Long itemType;
    private Long itemGroup;
    private Long subItemGroup;
    private String storageSectionId;
    private Double totalStock;
    private Double minimumStock;
    private Double maximumStock;
    private Double reorderLevel;
    private Boolean capacityCheck;
    private Double replenishmentQty;
    private Double safetyStock;
    private String capacityUnit;
    private String capacityUom;
    private String quantity;
    private Double weight;
    private Long statusId;
    private Boolean shelfLifeIndicator;
    private String referenceField1;
    private String referenceField2;
    private String referenceField3;
    private String referenceField4;
    private String referenceField5;
    private String referenceField6;
    private String referenceField7;
    private String referenceField8;
    private String referenceField9;
    private String referenceField10;
    private Long deletionIndicator;
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
}
