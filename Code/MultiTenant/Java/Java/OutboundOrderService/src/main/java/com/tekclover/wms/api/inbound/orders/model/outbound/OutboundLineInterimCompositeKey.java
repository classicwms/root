package com.tekclover.wms.api.inbound.orders.model.outbound;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class OutboundLineInterimCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `OBLINE_INTERIM_ID`, `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private Date createdOn;
	private String barcodeId;
}