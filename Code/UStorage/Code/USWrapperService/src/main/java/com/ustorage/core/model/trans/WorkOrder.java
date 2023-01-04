package com.ustorage.core.model.trans;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class WorkOrder {

	private String workOrderId;
	private String codeId;
	private String customerId;
	private String remarks;
	private String created;
	private Set<WoProcessedBy> woProcessedBy;
	private String processedTime;
	private String leadTime;
	private String jobCard;
	private String jobCardType;
	private String status;

	private String startTime;
	private String endTime;
	private Date startDate;
	private Date endDate;

	private Date workOrderDate;
	private String workOrderNumber;
	private String workOrderSbu;
	private String plannedHours;

	private String fromAddress;
	private String toAddress;
	private Set<ItemService> itemServices;

	private Long deletionIndicator = 0L;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private String referenceField4;
	private String referenceField5;
	private String referenceField6;
	private String referenceField7;
	private String referenceField8;
	private String referenceField9;
	private String referenceField10;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
