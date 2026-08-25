package com.tekclover.wms.core.model.transaction;

import java.util.List;

import lombok.Data;

@Data
public class SearchQualityLine {

	/*
	 * WH_ID PRE_OB_NO REF_DOC_NO PARTNER_CODE OB_LINE_NO QC_NO ITM_CODE
	 */
	private List<String> warehouseId;
	private List<String> preOutboundNo;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> lineNumber;
	private List<String> qualityinspectionNo;
	private List<String> itemCode;
}
