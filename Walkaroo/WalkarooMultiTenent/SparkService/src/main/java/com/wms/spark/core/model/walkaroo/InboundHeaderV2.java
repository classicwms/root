package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class InboundHeaderV2 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String refDocNumber;
    private String preInboundNo;
    private Long statusId;
    private Long inboundOrderTypeId;
    private String containerNo;
    private String vechicleNo;
    private String headerText;
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

    //v2 fields
    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String purchaseOrderNumber;
    private String middlewareId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String referenceDocumentType;
    private Long countOfOrderLines;
    private Long receivedLines;
}
