package com.tekclover.wms.api.enterprise.model.storagebintype;

import java.util.Date;

import lombok.Data;

@Data
public class SearchStorageBinType {
	/*
	 * WH_ID
	 * ST_CL_ID
	 * ST_TYP_ID
	 * ST_BIN_TYP_ID
	 * ST_BIN_TYP_ID_TEXT
	 * CTD_BY
	 * CTD_ON
	 */
    private String warehouseId;
    private Long storageTypeId;
    private Long storageBinTypeId;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
