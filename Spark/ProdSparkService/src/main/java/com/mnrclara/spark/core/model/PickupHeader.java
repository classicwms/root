package com.mnrclara.spark.core.model;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class PickupHeader {
    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String pickupNumber;
    private Long lineNumber;
    private String itemCode;
    private String proposedStorageBin;
    private String proposedPackBarCode;
    private Long outboundOrderTypeId;
    private Double pickToQty;
    private String pickUom;
    private Long stockTypeId;
    private Long specialStockIndicatorId;
    private String manufacturerPartNo;
    private Long statusId;
    private String assignedPickerId;
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
    private String remarks;
    private String pickupCreatedBy;
    private Timestamp pickupCreatedOn;
    private String pickConfimedBy;
    private Timestamp pickConfimedOn;
    private String pickUpdatedBy;
    private Timestamp pickUpdatedOn;
    private String pickupReversedBy;
    private Timestamp pickupReversedOn;
}
