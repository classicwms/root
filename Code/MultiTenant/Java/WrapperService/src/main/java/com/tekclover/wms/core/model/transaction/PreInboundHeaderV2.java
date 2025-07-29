package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

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
    private Date createdOn;
    private String updatedBy;
    private Date updatedOn;

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String middlewareId;
    private String middlewareTable;

    private Date transferOrderDate;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String parentProductionOrderNo;
    /*-------------------------------------------Reeferon-----------------------------------*/
    private Double qtyInCase;
    private Double qtyInPiece;
    private Double qtyInCreate;
    private List<PreInboundLineV2> preInboundLineV2;
    private String customerId;
    private String customerName;

}
