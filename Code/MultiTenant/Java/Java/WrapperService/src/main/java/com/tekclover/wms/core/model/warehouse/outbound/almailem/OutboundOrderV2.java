package com.tekclover.wms.core.model.warehouse.outbound.almailem;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class OutboundOrderV2 {
    private String branchCode;
    private String companyCode;
    private String languageId;
    private String returnOrderReference;

    private String pickListNumber;
    private String pickListStatus;
    private String companyName;
    private String branchName;
    private String warehouseName;
    private String salesOrderNumber;
    private Date salesInvoiceDate;
    private String salesInvoiceNumber;
    private String sourceCompanyCode;
    private String targetCompanyCode;
    private String targetBranchCode;
    private String tokenNumber;

    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date updatedOn;
    private Long middlewareId;
    private String middlewareTable;


    private String orderId;

    private String warehouseID; 			// WH_ID
    private String refDocumentNo; 			// REF_DOCument_NO
    private String refDocumentType; 		// REF_DOC_TYPE
    private String partnerCode; 			// PARTNER_CODE
    private String partnerName; 			// PARTNER_NM
    private Date requiredDeliveryDate;		// REQ_DEL_DATE

    // Additional Fields
    private Date orderReceivedOn; 			// order_received_on
    private Date orderProcessedOn;
    private Long processedStatusId;			// processed_status_id
    private Long outboundOrderTypeID;
    private Set<OutboundOrderLineV2> line;
    private String transferOrderNumber;
    private Date transferOrderDate;
    private String sourceBranchCode;

}
