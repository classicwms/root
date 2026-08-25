package com.tekclover.wms.api.idmaster.model.warehousetypeid;

import java.io.Serializable;

import lombok.Data;

@Data
public class WarehouseTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `WH_TYP_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long warehouseTypeId;
	private String languageId;
}
