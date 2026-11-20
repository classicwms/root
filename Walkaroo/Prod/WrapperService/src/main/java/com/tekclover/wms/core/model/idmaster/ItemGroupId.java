package com.tekclover.wms.core.model.idmaster;
import java.util.Date;
import lombok.Data;

@Data public class ItemGroupId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private String languageId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String itemTypeIdAndDescription;
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
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
