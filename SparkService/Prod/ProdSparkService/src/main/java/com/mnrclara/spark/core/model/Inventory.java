package com.mnrclara.spark.core.model;


import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class Inventory {
        private String languageId;
        private String companyCodeId;
        private String plantId;
        private String warehouseId;
        private String palletCode;
        private String caseCode;
        private String packBarcodes;
        private String itemCode;
        private Long variantCode;
        private String variantSubCode;
        private String batchSerialNumber;
        private String storageBin;
        private Long stockTypeId;
        private Long specialStockIndicatorId;
        private String referenceOrderNo;
        private String storageMethod;
        private Long binClassId;
        private String description;
        private Double inventoryQuantity;
        private Double allocatedQuantity;
        private String inventoryUom;
        private Date manufacturerDate;
        private Date expiryDate;
        private Long deletionIndicator;
        private String referenceField1;
        private String referenceField2;
        private String referenceField3;
        private Double referenceField4;
        private String referenceField5;
        private String referenceField6;
        private String referenceField7;
        private String referenceField8;
        private String referenceField9;
        private String referenceField10;
        private String createdBy;
        private Timestamp createdOn;
        private String updatedBy;
        private Date updatedOn;




}
