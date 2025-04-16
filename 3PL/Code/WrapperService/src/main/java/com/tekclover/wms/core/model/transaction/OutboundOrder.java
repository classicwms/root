package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class OutboundOrder {

    private String orderId;

    private String warehouseID;            // WH_ID
    private String refDocumentNo;            // REF_DOC_NO
    private String refDocumentType;        // REF_DOC_TYPE
    private String partnerCode;            // PARTNER_CODE
    private String partnerName;            // PARTNER_NM
    private Date requiredDeliveryDate;        // REQ_DEL_DATE

    // Additional Fields
    private Date orderReceivedOn;
    private Date orderProcessedOn;
    private Long processedStatusId;
    private Long outboundOrderTypeID;
    private String customerCode;
    private String TransferRequestType;
    private Set<OutboundOrderLine> lines;
}