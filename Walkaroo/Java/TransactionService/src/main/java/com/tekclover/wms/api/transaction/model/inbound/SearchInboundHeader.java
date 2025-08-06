package com.tekclover.wms.api.transaction.model.inbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchInboundHeader {
	/*
	* WH_ID
	* REF_DOC_NO
	* STATUS_ID
	* IB_ORD_TYP_ID
	* CONT_NO
	* IB_CTD_ON
	* IB_CNF_ON
	*/
	private List<String> warehouseId;	
	private List<String> refDocNumber;
	private List<Long> inboundOrderTypeId;
	private List<String> containerNo;		
	private List<Long> statusId;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	private Date startConfirmedOn;
	private Date endConfirmedOn;
}
