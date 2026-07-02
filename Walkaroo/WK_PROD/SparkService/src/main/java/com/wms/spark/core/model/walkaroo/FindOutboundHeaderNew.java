package com.wms.spark.core.model.walkaroo;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class FindOutboundHeaderNew {

	private List<String> languageId;
	private List<String> companyCodeId;
    private List<String> companyDescription;
    private List<String> plantDescription;
    private List<String> warehouseDescription;
    private List<String> statusDescription;

    private List<String> plantId;
	private List<String> targetBranchCode;
	private List<String> preOutboundNo;
	private List<String> warehouseId;
	private List<String> refDocNumber;
	private List<String> partnerCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType;

	private Date startRequiredDeliveryDate;
	private Date endRequiredDeliveryDate;
	
	private Date startDeliveryConfirmedOn;
	private Date endDeliveryConfirmedOn;
	
	private Date startOrderDate;
	private Date endOrderDate;


	private List<String> shipToParty;
	private List<String> salesOrderNumber;
	private List<String> customerId;
	private List<String> customerName;
	private List<String> shipToCode;
	private List<String> deliveryOrderNo;
}
