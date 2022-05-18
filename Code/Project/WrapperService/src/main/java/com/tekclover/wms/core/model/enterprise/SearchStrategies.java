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
    private Long sequenceIndicator;
    private String strategyNo;
	private Long priority;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
