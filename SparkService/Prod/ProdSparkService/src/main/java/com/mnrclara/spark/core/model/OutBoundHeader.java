package com.mnrclara.spark.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
public class OutBoundHeader {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String deliveryOrderNo;
    private String referenceDocumentType;
    private Long outboundOrderTypeId;
    private Long statusId;
    private Timestamp refDocDate;
    private Timestamp requiredDeliveryDate;
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
    private String createdBy;
    private Timestamp createdOn;
    private String deliveryConfirmedBy;
    private Timestamp deliveryConfirmedOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String reversedBy;
    private Timestamp reversedOn;
}
