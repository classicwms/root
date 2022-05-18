package com.tekclover.wms.api.enterprise.model.storageclass;

import java.util.Date;

import lombok.Data;

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
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
