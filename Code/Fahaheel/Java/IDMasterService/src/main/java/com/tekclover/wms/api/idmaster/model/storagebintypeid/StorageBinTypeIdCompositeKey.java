package com.tekclover.wms.api.idmaster.model.storagebintypeid;

import java.io.Serializable;

import lombok.Data;

@Data
public class StorageBinTypeIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `ST_CL_ID`, `ST_TYP_ID`, `ST_BIN_TYP_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long storageClassId;
	private Long storageTypeId;
	private Long storageBinTypeId;
	private String languageId;
}
