package com.tekclover.wms.core.model.enterprise;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchBatchSerial {
	/*
	 * WH_ID
	 * STR_MTD
	 * MAINT
	 * LEVEL_ID
	 * CTD_BY
	 * CTD_ON
	 */
	private List<String> warehouseId;
	private List<String> storageMethod;
	private List<String> maintenance;
	private List<String> languageId;
	private List<Long> levelId;
	private List<String> createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
	private List<String> companyId;
	private List<String> plantId;
	private List<String> id;

}
