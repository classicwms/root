package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class InboundOrder {
	
	private String orderId;
	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String warehouseID; 			// WH_ID
	private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;
    private Set<InboundOrderLines> lines;
	
}