package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InboundIntegrationHeader {

    private String id;                        // PRIMARY KEY
    private String refDocumentNo;            // REF_DOC_NO
    private String refDocumentType;        // REF_DOC_TYPE
    private String warehouseID;            // WH_ID
    private Long inboundOrderTypeId;        // IB_ORD_TYP_ID
    private Date orderReceivedOn;
    private Date orderProcessedOn;
    private Long processedStatusId;

    // For ALM Orders
    private String branchCode;
    private String companyCode;
    private String containerNo;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;

    private String middlewareId;
    private String middlewareTable;

    private Date transferOrderDate;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String customerCode;
    private String TransferRequestType;
    private String AMSSupplierInvoiceNo;
    private List<InboundIntegrationLine> inboundIntegrationLine;
}