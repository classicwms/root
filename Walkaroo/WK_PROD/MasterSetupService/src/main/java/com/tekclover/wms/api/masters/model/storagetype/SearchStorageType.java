package com.tekclover.wms.api.masters.model.storagetype;

import lombok.Data;

import java.util.Date;

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
	private String companyId;
	private String plantId;

}
