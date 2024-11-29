package com.tekclover.wms.api.enterprise.transaction.model.mnc;

import lombok.Data;

import java.io.Serializable;

@Data
public class InhouseTransferLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `TR_NO`,`SRCE_ITM_CODE`
	 */
	private String languageId;	
	private String companyCodeId;
	private String plantId;
	private String warehouseId;	
	private String transferNumber;
	private String sourceItemCode;	
}