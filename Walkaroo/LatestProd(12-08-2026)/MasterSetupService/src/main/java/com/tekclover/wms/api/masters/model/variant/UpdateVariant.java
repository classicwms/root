package com.tekclover.wms.api.masters.model.variant;

import lombok.Data;

@Data
public class UpdateVariant {

	private String variantId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String variantSubId;
	private Long levelId;
	private Long id;
	private String description;
	private String languageId;
	private String levelReference;
	private String updatedBy;
}
