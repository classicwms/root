package com.tekclover.wms.api.inbound.transaction.model.outbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchOutboundReversal {
	/*
	 * OB_REVERSAL_NO
	 * REVERSAL_TYPE
	 * REF_DOC_NO
	 * PARTNER_CODE
	 * ITM_CODE
	 * PACK_BARCODE
	 * STATUS_ID
	 * OB_REV_BY
	 * OB_REV_ON
	 */
	 
	private List<String> outboundReversalNo;
	private List<String> reversalType;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<String> itemCode;
	private List<String> packBarcode;
	private List<Long> statusId;
	private List<String> reversedBy;
	
	private Date startReversedOn;
	private Date endReversedOn;
	}
