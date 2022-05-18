package com.tekclover.wms.api.enterprise.model.batchserial;

import java.util.Date;

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
    private String warehouseId;
    private String storageMethod;
	private String maintenance;
	private Long levelId;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
