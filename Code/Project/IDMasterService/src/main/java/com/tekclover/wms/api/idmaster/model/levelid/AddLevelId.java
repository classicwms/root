package com.tekclover.wms.api.idmaster.model.levelid;

import java.util.Date;

import lombok.Data;

@Data
public class AddLevelId {

	private String warehouseId;
	private Long levelId;
	private String languageId;
	private String level;
	private String levelReference;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
