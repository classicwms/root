package com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class OutboundIntegrationHeader {

	private String id;						// PRIMARY KEY
	private String warehouseID; 			// WH_ID
	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String partnerCode; 			// PARTNER_CODE
	private String partnerName; 			// PARTNER_NM
	private Date requiredDeliveryDate;		// REQ_DEL_DATE

	// Additional Fields
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;
	private Long outboundOrderTypeID;

	private List<OutboundIntegrationLine> outboundIntegrationLine;
}