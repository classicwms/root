package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class FastSlowMovingDashboardRequest {

	@NotNull
	private String warehouseId;
	private Date fromDate;
	private Date toDate;
}