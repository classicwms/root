package com.tekclover.wms.api.enterprise.transaction.model.inbound.preinbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InboundIntegrationHeader {

	private String id;						// PRIMARY KEY
	private String refDocumentNo; 			// REF_DOC_NO
	private String refDocumentType; 		// REF_DOC_TYPE
	private String warehouseID; 			// WH_ID
	private Long inboundOrderTypeId; 		// IB_ORD_TYP_ID
	private Date orderReceivedOn;
	private Date orderProcessedOn;
	private Long processedStatusId;
	
	private List<InboundIntegrationLine> inboundIntegrationLine;
}