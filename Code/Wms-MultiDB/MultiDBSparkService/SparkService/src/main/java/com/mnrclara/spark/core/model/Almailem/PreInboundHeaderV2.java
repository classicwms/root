package com.mnrclara.spark.core.model.Almailem;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class PreInboundHeaderV2 {

    private String languageId;
    private String companyCode;
    private String plantId;
    private String warehouseId;
    private String preInboundNo;
    private String refDocNumber;
    private Long inboundOrderTypeId;
    private String referenceDocumentType;
    private Long statusId;
    private String containerNo;
    private Long noOfContainers;
    private String containerType;
    private Timestamp refDocDate;
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
    private Timestamp createdOn ;
    private String updatedBy;
    private Timestamp updatedOn;


    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String middlewareId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String statusDescription;
    private String customerCode;
    private String TransferRequestType;
}