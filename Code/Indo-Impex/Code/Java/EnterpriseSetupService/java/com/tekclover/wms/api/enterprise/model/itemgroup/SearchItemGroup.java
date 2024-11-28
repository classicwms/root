package com.tekclover.wms.api.enterprise.model.itemgroup;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
public class SearchItemGroup {
	/*
	 * WH_ID
	 * ITM_TYP_ID
	 * ITM_GRP_ID
	 * ITM_GRP_TEXT
	 * SUB_ITM_GRP_ID
	 * SUB_ITM_GRP_TEXT
	 * CTD_BY
	 * CTD_ON
	 */
	private String warehouseId;
    private Long itemTypeId;
	private String languageId;
    private Long itemGroupId;	
	private Long subItemGroupId;	
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
	private String companyId;
	private String plantId;
}
