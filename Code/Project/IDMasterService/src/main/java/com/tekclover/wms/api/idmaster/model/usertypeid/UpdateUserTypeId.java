package com.tekclover.wms.api.idmaster.model.usertypeid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateUserTypeId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long userTypeId;
	private String languageId;
	private String userTypeDescription;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
