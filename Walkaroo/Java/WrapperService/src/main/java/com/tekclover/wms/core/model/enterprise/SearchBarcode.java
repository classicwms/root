package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchBarcode {
	/*
	 * WH_ID
	 * MTD
	 * BAR_TYP_ID
	 * PROCESS_ID
	 * BAR_TYP_SUB_ID
	 * LEVEL_ID
	 * CTD_BY 
	 * CTD_ON

	 */
	private String warehouseId;
	private String method;
	private Long barcodeTypeId;
	private Long processId;
	private Long barcodeSubTypeId;
	private String companyId;
	private String plantId;
	private String languageId;
	private Long levelId;
	private String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
}
