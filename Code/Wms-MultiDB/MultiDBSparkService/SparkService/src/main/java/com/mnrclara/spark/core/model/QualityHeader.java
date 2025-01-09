package com.mnrclara.spark.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.sql.Timestamp;


@Data
public class QualityHeader {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String preOutboundNo;
    private String refDocNumber;
    private String partnerCode;
    private String pickupNumber;
    private String qualityInspectionNo;
    private String actualHeNo;
    private Long outboundOrderTypeId;
    private Long statusId;
    private String qcToQty;
    private String qcUom;
    private String manufacturerPartNo;
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
    private String qualityCreatedBy;
    private Timestamp qualityCreatedOn;
    private String qualityConfirmedBy;
    private Timestamp qualityConfirmedOn;
    private String qualityUpdatedBy;
    private Timestamp qualityUpdatedOn;
    private String qualityReversedBy;
    private Timestamp qualityReversedOn;
}
