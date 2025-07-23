package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class SearchOutboundHeader {
	/*
	 * WH_ID
	 * REF_DOC_NO
	 * PARTNER_CODE
	 * OB_ORD_TYP_ID
	 * STATUS_ID
	 * REQ_DEL_DATE
	 * REF_FIELD_1
	 * DLV_CNF_ON
	 * ORD_REC_DATE
	 */
	 
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType; //referenceField1;

	private String startRequiredDeliveryDate;
	private String endRequiredDeliveryDate;
	
	private String startDeliveryConfirmedOn;
	private String endDeliveryConfirmedOn;
	
	private String startOrderDate;
	private String endOrderDate;

}
