package com.tekclover.wms.api.enterprise.model.itemtype;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemTypeCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
}
