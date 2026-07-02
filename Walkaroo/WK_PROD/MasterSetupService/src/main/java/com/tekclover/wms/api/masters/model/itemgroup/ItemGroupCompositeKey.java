package com.tekclover.wms.api.masters.model.itemgroup;

import lombok.Data;

import java.io.Serializable;

@Data
public class ItemGroupCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`, `ITM_GRP_ID`, `SUB_ITM_GRP_ID`
	 */
	 
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroupId;
}
