package com.tekclover.wms.api.enterprise.transaction.model.inbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchInboundLine {
	/*
	* WH_ID
	* REF_DOC_NO
	* IB_CNF_ON
	*/
	private String warehouseId;	
	private String referenceField1;	
	private Date startConfirmedOn;
	private Date endConfirmedOn;

	private List<Long> statusId;
}