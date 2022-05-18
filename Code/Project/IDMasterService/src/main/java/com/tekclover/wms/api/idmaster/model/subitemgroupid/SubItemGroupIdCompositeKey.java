package com.tekclover.wms.api.idmaster.model.subitemgroupid;

import java.io.Serializable;

import lombok.Data;

@Data
public class SubItemGroupIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYP_ID`, `ITM_GRP_ID`, `SUB_ITM_GRP_ID`, `SUB_ITM_GRP`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private Long subItemGroupId;
	private String subItemGroup;
	private String languageId;
}
