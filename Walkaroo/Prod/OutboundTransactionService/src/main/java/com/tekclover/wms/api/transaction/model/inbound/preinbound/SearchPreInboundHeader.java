package com.tekclover.wms.api.transaction.model.inbound.preinbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPreInboundHeader {
	/*
	 * WH_ID
	 * PRE_IB_NO
	 * IB_ORD_TYP_ID
	 * REF_DOC_NO
	 * REF_DOC_DATE
	 * PRE_IB_CTD_ON
	 * STATUS_ID
	 */
	private List<String> warehouseId;	
	private List<String> preInboundNo;
	private List<Long> inboundOrderTypeId;
	private List<String> refDocNumber;	
	private List<Long> statusId;
	
	private Date startRefDocDate;
	private Date endRefDocDate;
	
	private Date startCreatedOn;
	private Date endCreatedOn;	
}
