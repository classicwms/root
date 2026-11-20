package com.tekclover.wms.api.inbound.transaction.model.inbound.gr;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class GrLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`, `PACK_BARCODE`, `IB_LINE_NO`, `ITM_CODE`
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
	private String packBarcodes;
	private Long lineNo;
	private String itemCode;
	private Date createdOn;
}
