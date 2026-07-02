package com.tekclover.wms.api.enterprise.model.variant;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;

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
