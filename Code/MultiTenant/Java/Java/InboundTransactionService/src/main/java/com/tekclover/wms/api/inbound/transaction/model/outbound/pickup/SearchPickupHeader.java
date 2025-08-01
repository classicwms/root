package com.tekclover.wms.api.inbound.transaction.model.outbound.pickup;

import lombok.Data;

import java.util.List;

@Data
public class SearchPickupHeader {
	/*
	 * WH_ID 
	 * REF_DOC_NO 
	 * PARTNER_CODE 
	 * PU_NO ITM_CODE 
	 * PROP_ST_BIN 
	 * PROP_PACK_BARCODE
	 * OB_ORD_TYP_ID 
	 * STATUS_ID 
	 * REF_FIELD_1 
	 * ASS_PICKER_ID
	 */

	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<String> pickupNumber;
	private List<String> itemCode;
	private List<String> proposedStorageBin;
	private List<String> proposedPackCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType; // referenceField1;
	private List<String> assignedPickerId;
}
