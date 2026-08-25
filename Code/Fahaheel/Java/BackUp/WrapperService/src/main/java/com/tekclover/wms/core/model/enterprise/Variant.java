package com.tekclover.wms.core.model.enterprise;

import lombok.Data;
import java.util.Date;
import java.util.Set;

@Data

public class Variant { 

	private String variantId;
	private Long id;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String variantSubId;
	private Long levelId;
	private String levelReference;
	private String levelIdAndDescription;
	private String languageId;
	private String description;
	private String companyIdAndDescription;
	private String plantIdAndDescription;
	private String warehouseIdAndDescription;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
