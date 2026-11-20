package com.tekclover.wms.api.idmaster.model.binclassid;

import java.io.Serializable;

import lombok.Data;

@Data
public class BinClassIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `BIN_CL_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long binClassId;
	private String languageId;
}
