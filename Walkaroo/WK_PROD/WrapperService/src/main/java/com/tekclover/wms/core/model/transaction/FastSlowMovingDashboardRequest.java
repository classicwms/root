package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FastSlowMovingDashboardRequest {

	@NotNull
	private String warehouseId;
	private String fromDate;
	private String toDate;
}
