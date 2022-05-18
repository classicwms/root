package com.tekclover.wms.api.idmaster.model.itemgroupid;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemGroupIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `ITM_TYPE_ID`, `ITM_GRP_ID`, `IMT_GRP`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private String itemGroup;
}
