package com.tekclover.wms.api.idmaster.model.workcenterid;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkCenterIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `WRK_CTR_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String workCenterId;
	private String languageId;
}
