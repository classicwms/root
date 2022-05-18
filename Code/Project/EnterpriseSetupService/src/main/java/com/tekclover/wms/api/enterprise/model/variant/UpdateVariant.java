package com.tekclover.wms.api.enterprise.model.variant;

import lombok.Data;

@Data
public class UpdateVariant {

	private String variantId;
    private String companyId;
	private String plantId;
    private String warehouseId;
	private String variantSubId;
	private Long levelId;
	private String languageId;
	private String levelReferece;
	private String updatedBy;
}
