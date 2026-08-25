package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchStorageSection {
	/*
	 * C_ID
	 * PLANT_ID
	 * WH_ID
	 * FL_ID
	 * ST_SEC_ID
	 * ST_SEC
	 * CTD_BY
	 * CTD_ON
	 */
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String languageId;
	private Long floorId;
	private Long storageSectionId;
	public String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
}
