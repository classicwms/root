package com.tekclover.wms.api.inbound.transaction.model.inbound.staging;

import java.util.List;

import lombok.Data;

@Data
public class SearchStagingLine {
	/*
	 * WH_ID
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * STG_NO
	 * PAL_CODE
	 * CASE_CODE
	 * IB_LINE_NO
	 * ITM_CODE
	 */
	 
	private List<String> warehouseId;
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private List<String> stagingNo;
	private List<String> palletCode;
	private List<String> caseCode;
	private List<Long> lineNo;
	private List<String> itemCode;
	private List<Long> statusId;
}
