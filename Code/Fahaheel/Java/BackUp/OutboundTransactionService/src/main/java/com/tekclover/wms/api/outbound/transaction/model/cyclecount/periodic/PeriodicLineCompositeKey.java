package com.tekclover.wms.api.outbound.transaction.model.cyclecount.periodic;

import java.io.Serializable;

import lombok.Data;

@Data
public class PeriodicLineCompositeKey implements Serializable {

	private static final long serialVersionUID = -7617672247680004647L;
	
	/*
	 * `C_ID`, `PLANT_ID`, `WH_ID`, `CC_NO`, `ST_BIN`, `ITM_CODE`, `PACK_BARCODE` 
	 */
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String cycleCountNo;
	private String storageBin;
	private String itemCode;
	private String packBarcodes;
	private String manufacturerName;
	private Long lineNo;
}
