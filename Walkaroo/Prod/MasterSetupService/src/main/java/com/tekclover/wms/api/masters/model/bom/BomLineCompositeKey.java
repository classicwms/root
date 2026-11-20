package com.tekclover.wms.api.masters.model.bom;

import java.io.Serializable;

import lombok.Data;

@Data
public class BomLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `BOM_NO`, `CHL_ITEM_CODE`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private Long bomNumber;
	private String childItemCode;
}
