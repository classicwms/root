package com.tekclover.wms.api.inbound.transaction.model.inbound.gr;

import java.util.List;

import lombok.Data;

@Data
public class SearchGrLine {
	/*
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * PACK_BARCODE
	 * IB_LINE_NO
	 * ITM_CODE
	 */
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private List<String> packBarcodes;
	private List<Long> lineNo;
	private List<String> itemCode;
	private List<String> caseCode;
	private List<Long> statusId;
}
