package com.tekclover.wms.api.idmaster.model.statusid;

import java.io.Serializable;

import lombok.Data;

@Data
public class StatusIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `STATUS_ID`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long statusId;
}
