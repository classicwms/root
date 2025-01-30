package com.tekclover.wms.core.model.spark;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InhouseTransferHeader {

    private String languageId;
    private String companyCodeId;
    private String plantId;
    private String warehouseId;
    private String transferNumber;
    private Long transferTypeId;
    private String transferMethod;
    private Long statusId;
    private String remarks;
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

    private String manufacturerName;
}
