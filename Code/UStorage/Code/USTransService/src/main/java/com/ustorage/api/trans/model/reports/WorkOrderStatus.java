package com.ustorage.api.trans.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkOrderStatus {

	private List<String> workOrderId;
	private List<String> workOrderSbu;
	private Date startDate;
	private Date endDate;
}
