package com.tekclover.wms.core.model.masters;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchPackingMaterial {
	/*
	 * WH_ID
	 * PACK_MAT_NO
	 * PACK_MAT_TEXT
	 * PARTNER_CODE
	 * STATUS_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	 
	private List<String> warehouseId;	
	private List<String> packingMaterialNo;
	private List<String> description;
	private List<String> businessPartnerCode;
	private List<Long> statusId;
	
	private List<String> createdBy;
	private List<String> updatedBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	
	private Date startUpdatedOn;
	private Date endUpdatedOn;
}
