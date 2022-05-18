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
public class StorageSection { 
	
	private String storageSectionId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String languageId;
	private Long storageTypeNo;
	private Long noRows;
	private Long noAisles;
	private Long noSpan;
	private Long noShelves;
	private Long noStorageBins;
	private Long itemType;
	private Long itemGroup;
	private Long subItemGroup;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
