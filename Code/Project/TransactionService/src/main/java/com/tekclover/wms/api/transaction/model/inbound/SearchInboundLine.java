package com.tekclover.wms.api.transaction.model.inbound;

import java.util.Date;

import lombok.Data;

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
}
