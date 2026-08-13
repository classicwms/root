package com.tekclover.wms.core.model.enterprise;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class BatchSerialOutput {
	
	private String storageMethod;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long levelId;
	private String maintenance;
	private String levelIdAndDescription;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private String languageId;
	private List<String> levelReference;
	private String description;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;

}
