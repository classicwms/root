package com.tekclover.wms.api.enterprise.transaction.model.inbound.putaway;

import lombok.Data;

import java.io.Serializable;

@Data
public class PutAwayLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `GR_NO`, `PRE_IB_NO`, `REF_DOC_NO`, `PA_NO`, `IB_LINE_NO`, `ITM_CODE`, `PROP_ST_BIN`, `CNF_ST_BIN`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String goodsReceiptNo;
	private String preInboundNo;
	private String refDocNumber;
	private String putAwayNumber;
	private Long lineNo;
	private String itemCode;
	private String proposedStorageBin;
	private String confirmedStorageBin;
}