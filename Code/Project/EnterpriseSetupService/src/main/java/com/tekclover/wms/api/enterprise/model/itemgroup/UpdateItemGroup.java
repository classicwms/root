package com.tekclover.wms.api.enterprise.model.itemgroup;

import lombok.Data;

@Data
public class UpdateItemGroup {

    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroupId;
	private String languageId;
	private Long storageClassId;
	private String storageSectionId;
	private String updatedBy;
}
