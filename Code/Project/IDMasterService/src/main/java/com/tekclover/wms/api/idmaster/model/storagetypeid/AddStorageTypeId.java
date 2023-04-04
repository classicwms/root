package com.tekclover.wms.api.idmaster.model.storagetypeid;

import java.util.Date;

import lombok.Data;

@Data
public class AddStorageTypeId {

	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private String languageId;
	private String description;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
