package com.tekclover.wms.api.enterprise.transaction.model.outbound.preoutbound;

import lombok.Data;

import java.io.Serializable;

@Data
public class OutboundIntegrationLogCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `WH_ID`, `WH_ID`, `INT_LOG_NO`, `REF_DOC_NO`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String integrationLogNumber;
	private String refDocNumber;
}