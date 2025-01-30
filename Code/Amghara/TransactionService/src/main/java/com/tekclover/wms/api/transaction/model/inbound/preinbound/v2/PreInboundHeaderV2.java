package com.tekclover.wms.api.transaction.model.inbound.preinbound.v2;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString(callSuper = true)
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
    private Date refDocDate;
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
    private Date createdOn = new Date();
    private String updatedBy;
    private Date updatedOn = new Date();


    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String purchaseOrderNumber;
    private String middlewareId;
    private String middlewareTable;
    private String manufacturerFullName;
    private String statusDescription;

    private Date transferOrderDate;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String customerCode;
    private String TransferRequestType;

    private List<PreInboundLineEntityV2> preInboundLineV2;
}