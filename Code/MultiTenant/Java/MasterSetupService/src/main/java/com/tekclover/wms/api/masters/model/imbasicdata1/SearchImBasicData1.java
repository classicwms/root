package com.tekclover.wms.api.masters.model.imbasicdata1;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchImBasicData1 {
	/*
	 * WH_ID
	 * ITM_CODE
	 * TEXT
	 * ITM_TYP_ID
	 * ITM_GRP_ID	 
	 * SUB_ITM_GRP_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	 
	private List<String> warehouseId;	
	private List<String> itemCode;
	private List<String> description;
	private List<Long> itemType;
	private List<Long> itemGroup;
	private List<Long> subItemGroup;
	private List<String> companyCodeId;
	private List<String> manufacturerPartNo;
	private List<String> plantId;
	private List<String> languageId;
	private List<String> createdBy;
	private List<String> updatedBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	
	private Date startUpdatedOn;
	private Date endUpdatedOn;

}
