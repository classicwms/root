package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FastSlowMovingDashboardRequestModel {

	@NotNull
	private String warehouseId;
	private Date fromDate;
	private Date toDate;
}
