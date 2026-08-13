package com.tekclover.wms.api.idmaster.model.approvalprocessid;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApprovalProcessIdCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`,`PLANT_ID`, `WH_ID`, `APP_PROCESS_ID`,`LANG_ID`
	 */
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String approvalProcessId;
	private String languageId;
}
