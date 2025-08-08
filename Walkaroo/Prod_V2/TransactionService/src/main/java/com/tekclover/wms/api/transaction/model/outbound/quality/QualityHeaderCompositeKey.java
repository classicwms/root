package com.tekclover.wms.api.transaction.model.outbound.quality;

import java.io.Serializable;

import lombok.Data;

@Data
public class QualityHeaderCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `PU_NO`, `QC_NO`, `PICK_HE_NO`
	 */
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String pickupNumber;
	private String qualityInspectionNo;
	private String actualHeNo;
	private String barcodeId;
}
