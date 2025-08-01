package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ItemGroup { 
	
	private Long itemGroupId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String languageId;
	private Long itemTypeId;
	private Long subItemGroupId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String itemTypeIdAndDescription;
	private String subItemGroupIdAndDescription;
	private Long storageClassId;
	private String storageSectionId;
	private String description;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
