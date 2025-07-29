package com.tekclover.wms.api.idmaster.model.approvalid;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApprovalIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `APP_ID`,`APP_LVL`,APP_PROCESS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String approvalId;
	private String approvalLevel;
	private String approvalProcessId;
	private String languageId;
}
