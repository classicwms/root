package com.tekclover.wms.api.enterprise.transaction.model.report;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class OrderStatusReportRequest {

	@NotNull
	private String warehouseId;
	private Date fromDeliveryDate;
	private Date toDeliveryDate;
	private List<String> customerCode;
	private List<String> orderNumber;
	private List<String> orderType;
	private List<Long> statusId;
}