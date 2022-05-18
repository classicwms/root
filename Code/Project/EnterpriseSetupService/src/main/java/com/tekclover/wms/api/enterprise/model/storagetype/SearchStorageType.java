package com.tekclover.wms.api.enterprise.model.storagetype;

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
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
