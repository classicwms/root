package com.tekclover.wms.api.idmaster.model.itemtypeid;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`, `ITM_TYP`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private String itemType;
}
