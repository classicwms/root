package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchStorageType {
	/*
	 * WH_ID
	 * ST_CL_ID
	 * ST_TYP
	 * ST_TYP_TEXT
	 * CTD_BY
	 * CTD_ON
	 */
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private String languageId;
	private String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
}
