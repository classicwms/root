package com.tekclover.wms.core.model.enterprise;

import java.util.Date;
import lombok.Data;

import javax.persistence.Column;

@Data
public class StorageSection {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String floorIdAndDescription;
	private Long storageTypeId;
	private Long noRows;
	private Long noAisles;
	private Long noSpan;
	private Long noShelves;
	private Long noStorageBins;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroup;
	private String description;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
