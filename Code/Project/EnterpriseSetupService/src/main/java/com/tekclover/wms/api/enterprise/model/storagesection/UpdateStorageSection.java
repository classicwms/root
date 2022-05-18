package com.tekclover.wms.api.enterprise.model.storagesection;

import lombok.Data;

@Data
public class UpdateStorageSection {

    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String languageId;
	private Long storageTypeNo;
	private Long noRows;
	private Long noAisles;
	private Long noSpan;
	private Long noShelves;
	private Long noStorageBins;
	private Long itemType ;
	private Long itemGroup;
	private Long subItemGroup;
	private String updatedBy;
}
