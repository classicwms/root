package com.tekclover.wms.api.idmaster.model.rowid;

import lombok.Data;
import java.io.Serializable;


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
	private String storageSectionId;
	private String rowId;
	private String languageId;
}
