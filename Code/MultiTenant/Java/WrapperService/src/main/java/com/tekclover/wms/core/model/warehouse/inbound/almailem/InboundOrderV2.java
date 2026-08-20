package com.tekclover.wms.core.model.warehouse.inbound.almailem;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class InboundOrderV2 {
    private String branchCode;
    private String companyCode;
    private String returnOrderReference;

    private String transferOrderNumber;

    private String sourceCompanyCode;
    private String sourceBranchCode;
    private Date transferOrderDate;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;
    private String orderId;

    private String refDocumentNo; 			// REF_DOC_NO
    private String refDocumentType; 		// REF_DOC_TYPE
    private String warehouseID;
    private Date orderReceivedOn;
    private Date orderProcessedOn;
    private Long processedStatusId;

    private String purchaseOrderNumber;
    private Long inboundOrderTypeId;
    private String warehouseId;
    private String languageId;

    //MiddleWare Fields
    private Long middlewareId;
    private String middlewareTable;
    // Order_Processing
    private Long preInboundHeader;
    private Long inboundHeader;
    private Long stagingHeader;
    private Long grHeader;
    private Long putawayHeader;
    private String orderText;
    private Set<InboundOrderLinesV2> line;
}
