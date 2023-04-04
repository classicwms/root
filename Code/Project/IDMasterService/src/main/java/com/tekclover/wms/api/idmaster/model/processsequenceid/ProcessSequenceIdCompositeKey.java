package com.tekclover.wms.api.idmaster.model.processsequenceid;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProcessSequenceIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `PROCESS_ID`, `SUB_PROCESS_SEQ_ID`, `LANG_ID`, `PROCESS`, `SUBPROCESS`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long processId;
	private Long subLevelId;
	private String languageId;
	private String processDescription;
	private String subProcessDescription;
}
