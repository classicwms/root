package com.tekclover.wms.api.idmaster.model.warehouseid;

import java.io.Serializable;

import lombok.Data;

@Data
public class WarehouseCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
}
