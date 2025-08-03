package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;

import java.io.Serializable;

import lombok.Data;

@Data
public class BarcodeSubTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `BAR_TYP_ID`, `BAR_SUB_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long barcodeTypeId;
	private Long barcodeSubTypeId;
	private String languageId;
}
