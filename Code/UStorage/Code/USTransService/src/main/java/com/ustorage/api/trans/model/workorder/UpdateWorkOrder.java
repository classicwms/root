package com.ustorage.api.trans.model.workorder;

import com.ustorage.api.trans.model.itemservice.ItemService;
import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class UpdateWorkOrder {

	private String customerId;
	private String codeId;
	
	private String remarks;

	private String created;

	private List<String> woProcessedBy;

	private String processedTime;

	private String leadTime;

	private String fromAddress;
	private String toAddress;

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

	private Double movingQuantity;
	private Double wrappingQuantity;
	private Double dismantlingAssemblingQuantity;
	private Double sBoxSizeWithPackingQuantity;
	private Double sBoxSizeWithoutPackingQuantity;
	private Double lBoxSizeWithPackingQuantity;
	private Double lBoxSizeWithoutPackingQuantity;
	private Double corrugatedRollQuantity;
	private Double bubbleWrapQuantity;
	private Double nylonRollQuantity;
	private Double stretchFilmQuantity;
	private Double tapeQuantity;
	private Double otherQuantity;

	private Double movingUnitPrice;
	private Double wrappingUnitPrice;
	private Double dismantlingAssemblingUnitPrice;
	private Double sBoxSizeWithPackingUnitPrice;
	private Double sBoxSizeWithoutPackingUnitPrice;
	private Double lBoxSizeWithPackingUnitPrice;
	private Double lBoxSizeWithoutPackingUnitPrice;
	private Double corrugatedRollUnitPrice;
	private Double bubbleWrapUnitPrice;
	private Double nylonRollUnitPrice;
	private Double stretchFilmUnitPrice;
	private Double tapeUnitPrice;
	private Double otherUnitPrice;
	private Double movingTotal;
	private Double wrappingTotal;
	private Double dismantlingAssemblingTotal;
	private Double sBoxSizeWithPackingTotal;
	private Double sBoxSizeWithoutPackingTotal;
	private Double lBoxSizeWithPackingTotal;
	private Double lBoxSizeWithoutPackingTotal;
	private Double corrugatedRollTotal;
	private Double bubbleWrapTotal;
	private Double nylonRollTotal;
	private Double stretchFilmTotal;
	private Double tapeTotal;
	private Double otherTotal;
		
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
