package com.tekclover.wms.core.model.idmaster;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StorageBinTypeId { 

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private Long storageBinTypeId;
	private String languageId;
	private String description;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
