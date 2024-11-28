package com.tekclover.wms.api.enterprise.model.storagesection;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageSectionCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `ST_SEC_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private String storageSectionId;
}
