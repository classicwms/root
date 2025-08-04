package com.tekclover.wms.api.transaction.model.outbound.preoutbound;

import java.util.List;

import lombok.Data;

@Data
public class SearchPreOutboundLine {
	/*
	 * WH_ID
	 * REF_DOC_NO
	 * PRE_OB_NO
	 * PARTNER_CODE
	 * OB_LINE_NO
	 * ITM_CODE
	 */
	 
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> preOutboundNo;
	private List<String> partnerCode;
	private List<Long> lineNumber;
	private List<String> itemCode;
}
