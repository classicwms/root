package com.tekclover.wms.api.inbound.orders.model.outbound.pickup;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPickupLine {
	/*
	 * WH_ID 
	 * PRE_OB_NO 
	 * REF_DOC_NO 
	 * PARTNER_CODE 
	 * OB_LINE_NO 
	 * PU_NO 
	 * ITM_CODE
	 * PICK_HE_NO
	 * PICK_ST_BIN 
	 * PICK_PACK_BARCODE
	 */

	private List<String> warehouseId;
	private List<String> preOutboundNo;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> lineNumber;
	private List<String> pickupNumber;
	private List<String> itemCode;
	private List<String> actualHeNo;
	private List<String> pickedStorageBin;
	private List<String> pickedPackCode;

	private List<Long> statusId;
	private Date fromPickConfirmedOn;
	private Date toPickConfirmedOn;
}
