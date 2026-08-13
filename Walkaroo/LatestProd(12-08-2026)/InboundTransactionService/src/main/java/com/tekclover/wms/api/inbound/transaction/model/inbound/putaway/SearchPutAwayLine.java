package com.tekclover.wms.api.inbound.transaction.model.inbound.putaway;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPutAwayLine {
	/*
	 * WH_ID
	 * GR_NO
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * PA_NO
	 * IB_LINE_NO
	 * ITM_CODE
	 * PROP_ST_BIN
	 * CNF_ST_BIN
	 */
	 
	private List<String> warehouseId;
	private List<String> goodsReceiptNo;
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private List<String> putAwayNumber;
	private List<Long> lineNo;
	private List<String> itemCode;
	private List<String> proposedStorageBin;
	private List<String> confirmedStorageBin;

	private List<String> packBarCodes;
	private Date fromConfirmedDate;
	private Date toConfirmedDate;

	private Date fromCreatedDate;
	private Date toCreatedDate;
}
