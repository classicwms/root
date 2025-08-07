package com.mnrclara.spark.core.model;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class StagingHeader {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private String stagingNo;
    private Long inboundOrderTypeId;
    private Long statusId;
    private String containerReceiptNo;
    private String dockAllocationNo;
    private String containerNo;
    private String vechicleNo;
    private String grMtd;
    private Long deletionIndicator;
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
    private String createdBy;
    private Timestamp createdOn;
    private String updatedBy;
    private Timestamp updatedOn;
    private String confirmedBy;
    private Timestamp confirmedOn;
}
