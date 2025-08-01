package com.tekclover.wms.api.inbound.orders.model.inbound.preinbound;

import java.io.Serializable;

import lombok.Data;

@Data
public class InboundIntegrationLogCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `INT_LOG_NO`, `REF_DOC_NO`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String integrationLogNumber;
	private String refDocNumber;
}
