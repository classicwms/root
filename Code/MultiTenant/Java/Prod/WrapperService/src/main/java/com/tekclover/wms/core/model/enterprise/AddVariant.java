package com.tekclover.wms.core.model.enterprise;

import lombok.Data;

import java.util.Set;

@Data
public class AddVariant {

	private String variantId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String variantSubId;
	private Long levelId;
	private String levelReference;
	private String languageId;
	private String description;
	private Long deletionIndicator;
}
