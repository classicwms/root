package com.tekclover.wms.api.idmaster.model.subitemgroupid;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data
public class AddSubItemGroupId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	@NotNull(message = "Item Type Id is mandatory")
	private Long itemTypeId;
	@NotNull(message = "Item Group Id is mandatory")
	private Long itemGroupId;
	@NotNull(message = "sub Item Group Id is mandatory")
	private Long subItemGroupId;
	private String subItemGroup;
	private String languageId;
	private String description;
	private Long deletionIndicator;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;

}
