package com.tekclover.wms.api.idmaster.model.subitemgroupid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateSubItemGroupId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroupId;
	private String subItemGroup;
	private String languageId;
	private String description;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
