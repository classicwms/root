package com.tekclover.wms.api.idmaster.model.storageclassid;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageClassIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `ST_CL_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private String languageId;
}
