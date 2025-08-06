package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchStrategies {
	/*
	 * WH_ID
	 * STR_TYP_ID
	 * SEQ_IND
	 * ST_NO
	 * PRIORITY
	 * CTD_BY
	 * CTD_ON
	 */
	private String warehouseId;
	private Long strategyTypeId;
	private String languageId;
	private Long sequenceIndicator;
	private String createdBy;
	private Date startCreatedOn;
	private Date endCreatedOn;
	private String companyId;
	private String plantId;
	private Long priority1;
	private Long priority2;
	private Long priority3;
	private Long priority4;
	private Long priority5;
	private Long priority6;
	private Long priority7;
	private Long priority8;
	private Long priority9;
	private Long priority10;

}
