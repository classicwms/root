package com.tekclover.wms.api.inbound.transaction.model.outbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOutboundLine {
	
	/*
	 * WH_ID
	 * PRE_OB_NO
	 * REF_DOC_NO
	 * PARTNER_CODE
	 * OB_LINE_NO
	 * ITM_CODE
	 */
	 
	private List<String> warehouseId;
	private List<String> preOutboundNo;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> lineNumber;
	private List<String> itemCode;
	
	// For Reports
	private Date fromDeliveryDate;	// DLV_CNF_ON
	private Date toDeliveryDate;	// DLV_CNF_ON
	private List<String> orderType; // REF_FIELD_1
	private List<Long> statusId;	// STATUS_ID
}
