package com.tekclover.wms.core.model.idmaster;

import java.util.Date;
import lombok.Data;

@Data
public class RowId {
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
	private String rowId;
	private String languageId;
	private String rowNumber;
	private String aisleId;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdDescription;
	private String floorIdAndDescription;
	private String storageSectionIdAndDescription;
	private String aisleIdAndDescription;
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
