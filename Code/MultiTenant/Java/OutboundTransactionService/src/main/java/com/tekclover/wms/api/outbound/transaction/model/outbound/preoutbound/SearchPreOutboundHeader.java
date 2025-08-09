package com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPreOutboundHeader {
	/*
	 * WH_ID
	 * REF_DOC_NO
	 * PRE_OB_NO
	 * PARTNER_CODE
	 * OB_ORD_TYP_ID
	 * STATUS_ID
	 * REQ_DEL_DATE
	 * ORD_REC_DATE
	 * REF_FIELD_1
	 * PRE_OB_CTD_BY
	 * PRE_OB_CTD_ON
	 */
	 
	private List<String> warehouseId;
	private List<String> preOutboundNo;
	private List<Long> outboundOrderTypeId;
	private List<String> soType; 			// SO type - Ref.Field1
	private List<String> soNumber; 			// refDocNumber - SO Number
	private List<String> partnerCode;
	private List<Long> statusId;
	private List<String> createdBy;
	
	private Date startRequiredDeliveryDate;
	private Date endRequiredDeliveryDate;
	private Date startOrderDate;
	private Date endOrderDate;
	private Date startCreatedOn;
	private Date endCreatedOn;
	
}
