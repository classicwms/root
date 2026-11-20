package com.tekclover.wms.api.masters.model.storageclass;

import lombok.Data;

import java.util.Date;

@Data
public class SearchStorageClass {
	/*
	 * WH_ID
	 * ST_CL_ID
	 * ST_CL_TEXT
	 * CTD_BY
	 * CTD_ON
	 */
    private String warehouseId;
    private Long storageClassId;
	private String languageId;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
	private String companyId;
	private String plantId;
}
