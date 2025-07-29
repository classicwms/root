package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchStagingHeader {
	/*
	 * WH_ID
	 * PRE_IB_NO
	 * REF_DOC_NO
	 * STG_NO
	 * IB_ORD_TYP_ID
	 * STATUS_ID
	 * ST_CTD_BY
	 * ST_CTD_ON
	 */

	private List<String> warehouseId;	
	private List<String> preInboundNo;
	private List<String> refDocNumber;
	private List<String> stagingNo;
	private List<Long> inboundOrderTypeId;	
	private List<Long> statusId;
	private List<String> createdBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;	
}
