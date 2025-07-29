package com.tekclover.wms.api.inbound.orders.model.outbound.pickup;

import java.io.Serializable;

import lombok.Data;

@Data
public class PickupLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `PU_NO`, `ITM_CODE`, `PICK_HE_NO`, `PICK_ST_BIN`, `PICK_PACK_BARCODE`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String pickupNumber;
	private String itemCode;
	private String actualHeNo;
	private String pickedStorageBin;
	private String pickedPackCode;
}
