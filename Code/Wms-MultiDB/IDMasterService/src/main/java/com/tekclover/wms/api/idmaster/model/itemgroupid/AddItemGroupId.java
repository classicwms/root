package com.tekclover.wms.api.idmaster.model.itemgroupid;

import java.util.Date;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Data
public class AddItemGroupId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	@NotNull(message = "ItemType Id is mandatory")
	private Long itemTypeId;
	@NotNull(message = "ItemGroup Id is mandatory")
	private Long itemGroupId;
	private String languageId;
	private String itemGroup;
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
