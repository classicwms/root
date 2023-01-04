package com.ustorage.core.model.reports;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WorkOrderStatusModel {

	private List<String> workOrderId;
	private List<String> workOrderSbu;
	private Date startDate;
	private Date endDate;
}
