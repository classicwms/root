package com.tekclover.wms.api.idmaster.model.processsequenceid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateProcessSequenceId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long processId;
	private Long subLevelId;
	private String languageId;
	private String processDescription;
	private String subProcessDescription;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
