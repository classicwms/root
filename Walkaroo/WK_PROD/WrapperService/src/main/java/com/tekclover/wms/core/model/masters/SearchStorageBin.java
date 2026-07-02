package com.tekclover.wms.core.model.masters;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SearchStorageBin {
	/*
	 * Field_Name
	 * WH_ID
	 * ST_BIN
	 * FL_ID
	 * ST_SEC_ID
	 * ROW_ID
	 * AISLE_ID
	 * SPAN_ID
	 * SHELF_ID
	 * CTD_BY
	 * CTD_ON
	 * UTD_BY
	 * UTD_ON
	 */
	
	private List<String> warehouseId;
	private List<String> storageBin;
	private List<Long> floorId;
	private List<String> storageSectionId;
	private List<String> rowId;
	private List<String> aisleNumber;
	private List<String>languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
	private List<String> spanId;
	private List<String> shelfId;
	private List<String> createdBy;
	private List<String> updatedBy;
	private List<Long> statusId;
	
	private Date startCreatedOn;
	private Date endCreatedOn;	

	private Date startUpdatedOn;
	private Date endUpdatedOn;	
}
