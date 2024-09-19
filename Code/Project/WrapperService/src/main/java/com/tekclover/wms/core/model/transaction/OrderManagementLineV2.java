package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class OrderManagementLineV2 {

	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private String proposedStorageBin;
	private String proposedPackBarCode; //proposedPackCode
	private Long outboundOrderTypeId;
	private Long statusId;
	private String description;
	private Double orderQty;
	private Double inventoryQty;
	private Double allocatedQty;
	private Date requiredDeliveryDate;
	private String referenceField7;
}