package com.tekclover.wms.api.outbound.transaction.model.outbound.quality;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchQualityHeader {

	/*
	 * REF_DOC_NO PARTNER_CODE QC_NO PICK_HE_NO OB_ORD_TYP_ID STATUS_ID REF_FIELD_1
	 * QC_CTD_ON
	 */

	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<String> qualityInspectionNo;
	private List<String> actualHeNo;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType; //referenceField1;
	private Date startQualityCreatedOn;
	private Date endQualityCreatedOn;

	private List<String> warehouseId;
	
}
