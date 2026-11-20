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
	private String warehouseId;
	private String variantId;
	private String languageId;
	private Long levelId;
	private String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
	private String companyId;
	private String plantId;
	private String variantSubId;
}
