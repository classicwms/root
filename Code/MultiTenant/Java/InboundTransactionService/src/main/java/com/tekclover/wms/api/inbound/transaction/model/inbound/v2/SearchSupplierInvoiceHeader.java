package com.tekclover.wms.api.inbound.transaction.model.inbound.v2;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchSupplierInvoiceHeader {

	private List<String> languageId;
	private List<String> companyCodeId;
	private List<String> plantId;

	private List<String> warehouseId;
	private List<Long> inboundOrderTypeId;
	private List<Long> oldStatusId;
	private List<Long> newStatusId;

	private List<Long> supplierInvoiceCancelHeaderId;
	private List<String> oldRefDocNumber;
	private List<String> newRefDocNumber;
	private List<String> oldPreInboundNo;
	private List<String> newPreInboundNo;
	private List<String> oldContainerNo;
	private List<String> newContainerNo;
	private List<String> purchaseOrderNumber;

	private Date startCreatedOn;
	private Date endCreatedOn;
	private Date startConfirmedOn;
	private Date endConfirmedOn;
}