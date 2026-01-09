package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class StorageBin {

    private String storageBin;
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private Long floorId;
    private Long levelId;
    private String storageSectionId;
    private String rowId;
    private String aisleNumber;
    private String spanId;
    private String shelfId;
    private Long binSectionId;
    private Long storageTypeId;
    private Long binClassId;
    private String occupiedVolume;
    private String occupiedWeight;
    private String occupiedQuantity;
    private String remainingVolume;
    private String remainingWeight;
    private String remainingQuantity;
    private String totalVolume;
    private String totalQuantity;
    private String totalWeight;
    private String description;
    private String binBarcode;
    private Integer putawayBlock;
    private Integer pickingBlock;
    private String blockReason;
    private Long statusId;
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

    //v2 fields
    private boolean capacityCheck;
    private Double allocatedVolume;
    private String capacityUnit;
    private Double length;
    private Double width;
    private Double height;
    private String capacityUom;
    private String quantity;
    private Double weight;

}
