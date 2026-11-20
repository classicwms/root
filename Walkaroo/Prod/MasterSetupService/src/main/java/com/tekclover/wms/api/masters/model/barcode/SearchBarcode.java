package com.tekclover.wms.api.masters.model.barcode;

import lombok.Data;

import java.util.Date;

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
    private String warehouseID;
    private String method;
	private Long barcodeTypeId;
	private Long processId;
	private Long barcodeSubTypeId;
	private Long levelId;
	private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
