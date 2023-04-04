package com.tekclover.wms.api.idmaster.model.uomid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateUomId {

	private String companyCodeId;
	private String uomId;
	private String languageId;
	private String description;
	private String uomType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
