package com.tekclover.wms.api.idmaster.model.processid;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProcessIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `PROCESS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String processId;
	private String languageId;
}
