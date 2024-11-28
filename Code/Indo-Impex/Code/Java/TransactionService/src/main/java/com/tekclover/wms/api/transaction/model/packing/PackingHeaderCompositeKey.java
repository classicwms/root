package com.tekclover.wms.api.transaction.model.packing;//package com.tekclover.wms.api.transaction.model.outbound.packing;

import lombok.Data;

import java.io.Serializable;

@Data
public class PackingHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;

	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `QC_NO`, `PACK_NO`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String qualityInspectionNo;
	private String packingNo;
}
