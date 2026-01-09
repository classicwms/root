package com.tekclover.wms.api.masters.model.itemgroup;

import lombok.Data;

@Data
public class AddItemGroup {

    private String companyId;
	private String plantId;
    private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroupId;
	private String languageId;
	private Long storageClassId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String itemTypeIdAndDescription;
	private String subItemGroupIdAndDescription;
	private String storageSectionId;
	private String description;
	
	private String createdBy;
    
}
