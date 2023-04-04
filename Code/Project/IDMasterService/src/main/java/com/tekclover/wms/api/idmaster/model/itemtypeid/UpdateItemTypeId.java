package com.tekclover.wms.api.idmaster.model.itemtypeid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateItemTypeId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private String itemType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
