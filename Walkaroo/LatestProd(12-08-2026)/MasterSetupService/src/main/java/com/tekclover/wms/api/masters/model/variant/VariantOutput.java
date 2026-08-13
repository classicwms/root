package com.tekclover.wms.api.masters.model.variant;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VariantOutput {
	
	private String variantId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String variantSubId;
	private Long levelId;
	private List<String> levelReferenceVariant;
	private String levelIdAndDescription;
	private String languageId;
	private String description;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
