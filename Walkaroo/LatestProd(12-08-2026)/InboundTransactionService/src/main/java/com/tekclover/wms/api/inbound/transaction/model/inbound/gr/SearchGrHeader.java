package com.tekclover.wms.api.inbound.transaction.model.inbound.gr;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchGrHeader {
	/*
	 * IB_ORD_TYP_ID
	 * GR_NO
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * GR_CTD_ON
	 * WH_ID
	 * CASE_CODE
	 * STATUS_ID
	 * GR_CTD_BY
	 */
	private List<Long> inboundOrderTypeId;
	private List<String> goodsReceiptNo;
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private Date startCreatedOn;
	private Date endCreatedOn;
	private List<Long> statusId;
	private List<String> warehouseId;
	private List<String> caseCode;
	private List<String> createdBy;
	private Long deletionIndicator;
}
