package com.tekclover.wms.core.model.warehouse.outbound;

import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class OutboundIntegrationHeaderV2 extends OutboundIntegrationHeader {

    private String companyDescription;
    private String plantDescription;
    private String warehouseDescription;
    private String statusDescription;
    private String warehouseID; 	

    // For ALM Orders
    private String branchCode;
    private String companyCode;
    private String targetBranchCode;
    private String targetCompanyCode;
    private String languageId;

    private Long middlewareId;
    private String middlewareTable;
    private String referenceDocumentType;

    private String salesOrderNumber;
    private String salesInvoiceNumber;
    private String pickListNumber;
    private String deliveryType;
    private String customerId;
    private String customerName;
    private String address;
    private String phoneNumber;
    private String alternateNo;
    private String status;
    private String tokenNumber;

    private String fromBranchCode;
    private String isCompleted;
    private String isCancelled;
    private Date mUpdatedOn;
    private String loginUserId;
    
    /*
     * Outbound Order from SAP
     */
    private Boolean isSAPOrder = false;

    private List<OutboundIntegrationLineV2> outboundIntegrationLines;
}