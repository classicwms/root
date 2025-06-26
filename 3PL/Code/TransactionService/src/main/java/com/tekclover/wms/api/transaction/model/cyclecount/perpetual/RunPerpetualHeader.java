package com.tekclover.wms.api.transaction.model.cyclecount.perpetual;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class RunPerpetualHeader {

	/*
	 * Pass From and To dates entered in Header screen into INVENOTRYMOVEMENT tables in IM_CTD_BY field 
	 * along with selected MVT_TYP_ID/SUB_MVT_TYP_ID values and fetch the below values
	 */
	private Date dateFrom;
	private Date dateTo;
	private List<String> warehouseId;
	private List<Long> movementTypeId;
	private List<Long> subMovementTypeId;
	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;
}
