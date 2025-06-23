package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class StagingHeaderV2 extends StagingHeader {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;

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
    private String customerId;
    private String customerName;

}