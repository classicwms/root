package com.tekclover.wms.api.idmaster.model.rowid;

import java.util.Date;

import lombok.Data;

@Data
public class AddRowId {

	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String rowId;
	private String languageId;
	private String rowNumber;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
