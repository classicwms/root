package com.tekclover.wms.api.inbound.orders.model.inbound.staging;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchStagingHeader {
	/*
	 * IB_ORD_TYP_ID
	 * STG_NO
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * STATUS_ID
	 * WH_ID
	 * ST_CTD_BY
	 * ST_CTD_ON
	 */
	private List<Long> inboundOrderTypeId;	
	private List<String> stagingNo;
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private List<Long> statusId;
	private List<String> warehouseId;	
	private List<String> createdBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;	
}
