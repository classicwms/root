package com.tekclover.wms.api.masters.model.itemtype;
import lombok.Data;

@Data
public class AddItemType {
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private String description;
	private String storageMethod;
	private Long variantManagementIndicator;
	private Long deletionIndicator;

}
