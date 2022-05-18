package com.tekclover.wms.api.masters.model.bom;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchBomHeader {
	/*
	 * WH_ID
	 * PAR_ITM_CODE
	 * BOM_NO
	 * STATUS_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	 
	private List<String> warehouseId;	
	private List<String> parentItemCode;
	private List<Long> bomNumber;
	private List<Long> statusId;
	
	private List<String> createdBy;
	private List<String> updatedBy;
	
	private Date startCreatedOn;
	private Date endCreatedOn;
	
	private Date startUpdatedOn;
	private Date endUpdatedOn; 
	

}
