package com.tekclover.wms.api.transaction.model.inbound.containerreceipt;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchContainerReceipt {
	/*
	 * WH_ID
	 * CONT_REC_NO
	 * CONT_REC_DATE
	 * CONT_NO
	 * VEN_CODE
	 * CNT_UL_BY
	 * STATUS_ID/STATUS_TEXT
	 */
	private List<String> warehouseId;	
	private List<String> containerReceiptNo;
	
	private Date startContainerReceivedDate;
	private Date endContainerReceivedDate;
	
	private List<String> containerNo;
	
	// VEN_CODE
	private List<String> partnerCode;
	
	// CNT_UL_BY
	private List<String> unloadedBy;
	private List<Long> statusId;
	
	private Date fromCreatedOn;
	private Date toCreatedOn;
}
