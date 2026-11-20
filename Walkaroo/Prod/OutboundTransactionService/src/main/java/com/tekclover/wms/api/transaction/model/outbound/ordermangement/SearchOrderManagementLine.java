package com.tekclover.wms.api.transaction.model.outbound.ordermangement;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchOrderManagementLine {
	/*
	 * WH_ID PRE_OB_NO REF_DOC_NO PARTNER_CODE ITM_CODE OB_ORD_TYP_ID STATUS_ID
	 * ITEM_TEXT REQ_DEL_DATE ORD_REC_DATE REF_FIELD_1
	 */
	private List<String> warehouseId;
	private List<String> preOutboundNo;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<String> itemCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> description;
	private List<String> soType; //referenceField1;

	private Date startRequiredDeliveryDate;
	private Date endRequiredDeliveryDate;
	private Date startOrderDate;
	private Date endOrderDate;

}
