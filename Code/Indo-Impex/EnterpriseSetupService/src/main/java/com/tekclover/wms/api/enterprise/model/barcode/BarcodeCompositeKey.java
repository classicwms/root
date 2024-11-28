package com.tekclover.wms.api.enterprise.model.barcode;

import java.io.Serializable;

import lombok.Data;

@Data
public class BarcodeCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `MTD`, `BAR_TYP_ID`, `BAR_SUB_ID`, `LEVEL_ID`, `LEVEL_REF`, `PROCESS_ID`
	 */
	 
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private String method;
	private Long barcodeTypeId;
	private Long barcodeSubTypeId;
	private Long levelId;
	private String levelReference;
	private Long processId;
}
