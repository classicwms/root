package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

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

	private List<Long> statusId;
	private List<String> orderType;
	private Date fromDeliveryDate;	// DLV_CNF_ON
	private Date toDeliveryDate;	// DLV_CNF_ON

	}
