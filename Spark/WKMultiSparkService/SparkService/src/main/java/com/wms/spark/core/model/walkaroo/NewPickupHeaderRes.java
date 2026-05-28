package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class NewPickupHeaderRes {

    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String customerId;
    private String customerName;
    private String pickListNumber;
    private String shipToCode;
    private String shipToParty;
    //    private String statusDescription;
    private Timestamp pickupCreatedOn;

    private String languageId;
    private String pickupNumber;
    private String referenceField2;
}