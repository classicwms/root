package com.tekclover.wms.api.transaction.model.inbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchInboundLine {
	/*
	* WH_ID
	* REF_DOC_NO
	* IB_CNF_ON
	*/
	private List<String> warehouseId;
//	private String warehouseId;
	private String referenceField1;	
	private Date startConfirmedOn;
	private Date endConfirmedOn;

	private List<Long> statusId;
	private List<String> itemCode;
	private List<String> manufacturerPartNo;
	private List<String> refDocNumber;
}
