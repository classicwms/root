package com.tekclover.wms.api.idmaster.model.varientid;

import java.util.Date;

import lombok.Data;

@Data
public class AddVariantId {

	private String warehouseId;
	private String variantCode;
	private String variantType;
	private String variantSubCode;
	private String variantText;
	private String variantSubType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
