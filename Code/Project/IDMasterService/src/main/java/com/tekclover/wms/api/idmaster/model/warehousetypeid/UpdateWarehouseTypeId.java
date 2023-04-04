package com.tekclover.wms.api.idmaster.model.warehousetypeid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateWarehouseTypeId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long warehouseTypeId;
	private String languageId;
	private String warehouseTypeText;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
