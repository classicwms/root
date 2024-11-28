package com.tekclover.wms.api.enterprise.model.itemtype;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

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
	private String languageId;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
	private String companyId;
	private String plantId;

}
