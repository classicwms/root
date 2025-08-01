package com.tekclover.wms.api.outbound.transaction.model.inbound;

import lombok.Data;

import java.io.Serializable;

@Data
public class InboundLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `REF_DOC_NO`, `PRE_IB_NO`, `IB_LINE_NO`, `ITM_CODE`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preInboundNo;
	private Long lineNo;
	private String itemCode;
}
