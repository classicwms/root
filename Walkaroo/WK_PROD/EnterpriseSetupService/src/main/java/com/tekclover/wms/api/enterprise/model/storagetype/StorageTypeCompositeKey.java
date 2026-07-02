package com.tekclover.wms.api.enterprise.model.storagetype;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageTypeCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_CL_ID`, `ST_TYP_ID`
	 */
	private String languageId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
}
