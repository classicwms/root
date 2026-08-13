package com.tekclover.wms.api.masters.model.storagebintype;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageBinTypeCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_TYP_ID`, `ST_BIN_TYP_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageTypeId;
	private Long storageBinTypeId;
}
