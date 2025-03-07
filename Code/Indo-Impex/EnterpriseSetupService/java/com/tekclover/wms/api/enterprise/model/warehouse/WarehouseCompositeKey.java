package com.tekclover.wms.api.enterprise.model.warehouse;

import java.io.Serializable;

import lombok.Data;

@Data
public class WarehouseCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `IMP_MTD`, `WH_TYP_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
//	private String modeOfImplementation;
	private Long warehouseTypeId;
}
