package com.tekclover.wms.api.transaction.model.outbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchOutboundHeader {
	/*
	 * WH_ID REF_DOC_NO PARTNER_CODE OB_ORD_TYP_ID STATUS_ID REQ_DEL_DATE
	 * REF_FIELD_1 DLV_CNF_ON ORD_REC_DATE
	 */
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType; //referenceField1;

	private Date startRequiredDeliveryDate;
	private Date endRequiredDeliveryDate;
	
	private Date startDeliveryConfirmedOn;
	private Date endDeliveryConfirmedOn;
	
	private Date startOrderDate;
	private Date endOrderDate;
	
	// Added for Reports
//	private String refField1;

}
