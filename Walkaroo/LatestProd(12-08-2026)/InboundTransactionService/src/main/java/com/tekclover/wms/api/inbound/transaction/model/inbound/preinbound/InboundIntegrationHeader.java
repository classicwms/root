package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

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
    private String parentProductionOrderNo;

    // For ALM Orders
    private String branchCode;
    private String companyCode;
    private String containerNo;
    private String isCompleted;
    private Date updatedOn;
    private String isCancelled;
    private String languageId;

    private String middlewareId;
    private String middlewareTable;

    private Date transferOrderDate;
    private String sourceBranchCode;
    private String sourceCompanyCode;
    private String loginUserId;
    
    // V3
    private String preInboundNo;
    
    //-------InboundUpload Vs SAP API -------Differentiator
  	private Boolean isSapOrder;

    private List<InboundIntegrationLine> inboundIntegrationLine;

}