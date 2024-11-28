package com.tekclover.wms.api.enterprise.model.itemtype;

import lombok.Data;

@Data
public class UpdateItemType {

	private Long itemTypeId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String languageId;
	private String description;
	private String storageMethod;
	private Long variantManagementIndicator;
	private String updatedBy;
}
