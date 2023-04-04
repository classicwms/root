package com.tekclover.wms.api.idmaster.model.rowid;

import java.io.Serializable;

import lombok.Data;

@Data
public class RowIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `FL_ID`, `ST_SEC_ID`, `ROW_ID`, `LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long floorId;
	private Long storageSectionId;
	private String rowId;
	private String languageId;
}
