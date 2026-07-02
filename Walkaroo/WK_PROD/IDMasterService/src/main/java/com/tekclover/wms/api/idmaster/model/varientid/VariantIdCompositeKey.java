package com.tekclover.wms.api.idmaster.model.varientid;

import java.io.Serializable;

import lombok.Data;

@Data
public class VariantIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `VAR_ID`, `VAR_TYP`, `VAR_SUB_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String variantCode;
	private String variantType;
	private String variantSubCode;
	private String languageId;
}
