package com.tekclover.wms.api.enterprise.model.storagesection;

import lombok.Data;
@Data
public class AddStorageSection {

	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
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
}
