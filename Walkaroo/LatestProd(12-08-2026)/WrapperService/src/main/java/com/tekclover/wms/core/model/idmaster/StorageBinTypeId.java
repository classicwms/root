package com.tekclover.wms.core.model.idmaster;

import java.util.Date;
import lombok.Data;

@Data
public class StorageBinTypeId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private Long storageBinTypeId;
	private String languageId;
	private String description;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String storageClassIdAndDescription;
	private String storageTypeIdAndDescription;
	private Long deletionIndicator = 0L;
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
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
