package com.tekclover.wms.api.enterprise.model.itemtype;

import java.util.Date;

import lombok.Data;

@Data
public class SearchItemType {
	/*
	 * WH_ID
	 * ITM_TYP_ID
	 * ITM_TYP_TEXT
	 * CTD_BY
	 * CTD_ON
	 */    
	private String warehouseId;
	private Long itemTypeId;
	private String description;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
