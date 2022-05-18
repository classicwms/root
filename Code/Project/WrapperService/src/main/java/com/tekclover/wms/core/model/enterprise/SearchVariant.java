package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchVariant {
	/*
	 * WH_ID
	 * VAR_ID
	 * VAR_TYP
	 * LEVEL_ID
	 * CTD_BY
	 * CTD_ON
	 */
    private String warehouseID;
    private String variantId;
    private Long levelId;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
