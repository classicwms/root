package com.tekclover.wms.api.outbound.transaction.model.outbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchPickListHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> warehouseId;
	private List<String> oldPreOutboundNo;
	private List<String> newPreOutboundNo;
	private List<String> oldRefDocNumber;
	private List<String> newRefDocNumber;
	private List<String> oldInvoiceNumber;
	private List<String> newInvoiceNumber;
	private List<String> oldSalesOrderNumber;
	private List<String> newSalesOrderNumber;
	private List<String> oldSalesInvoiceNumber;
	private List<String> newSalesInvoiceNumber;
	private List<String> oldSupplierInvoiceNo;
	private List<String> newSupplierInvoiceNo;
	private List<String> oldPickListNumber;
	private List<String> newPickListNumber;
	private List<String> newTokenNumber;
	private List<String> oldTokenNumber;
	private List<String> partnerCode;
	private List<Long> outboundOrderTypeId;
	private List<Long> statusId;
	private List<String> soType; //referenceField1;

	private Date startRequiredDeliveryDate;
	private Date endRequiredDeliveryDate;

	private Date startDeliveryConfirmedOn;
	private Date endDeliveryConfirmedOn;

	private Date startOrderDate;
	private Date endOrderDate;

}