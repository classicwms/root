package com.tekclover.wms.core.model.transaction;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString(callSuper = true)
public class PreOutboundHeaderV2 extends PreOutboundHeader {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;

    private String middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;
    private String salesOrderNumber;
    private String tokenNumber;
    private String targetBranchCode;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private Integer imsSaleTypeCode;
 private String customerId;
    private String customerName;
    private String consignment;
    private String transportName;

}