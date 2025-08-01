package com.tekclover.wms.core.model.transaction;

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
	private List<Long> movementTypeId;
	private List<Long> subMovementTypeId;

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
}
