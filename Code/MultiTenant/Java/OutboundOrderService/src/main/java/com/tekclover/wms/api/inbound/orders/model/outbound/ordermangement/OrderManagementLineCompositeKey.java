package com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderManagementLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`, `PROP_ST_BIN`, `PROP_PACK_BARCODE`
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
	private String proposedStorageBin;
	private String proposedPackBarCode;
	private String barcodeId;
}
