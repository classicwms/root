package com.ustorage.api.trans.model.workorder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.ustorage.api.trans.model.itemservice.ItemService;
import lombok.Data;

import java.util.Date;
import java.sql.Time;
import java.util.List;
import java.util.Set;

@Data
public class AddWorkOrder {

	@NotNull(message = "Customer ID is mandatory")
	private String customerId;

	private String codeId;
	private String remarks;

	private String created;

	private List<String> woProcessedBy;

	private String fromAddress;
	private String toAddress;
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

	private List<ItemService> itemServices;

		
	private Long deletionIndicator;
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

}
