package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchWarehouse {
	/*
	 * C_ID
	 * PLANT_ID
	 * WH_ID
	 * WH_TEXT
	 * CNT_NM
	 * CTD_BY
	 * CTD_ON
	 */
	private String companyId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String contactName;
	private String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
	private String modeOfImplementation;
	private Long warehouseTypeId;
}
