package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PickerDenialHeader {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String partnerName;
	private String orderType;
	private Long skuOrdered;
	private Long skuShipped;
	private Long skuDenied;
	private Long orderedQty;
	private Long shippedQty;
	private Double percentageShipped;
	private Date denialDate;
	private Date deliveryDate;
	private String sDenialDate;
	private String sDeliveryDate;
	private String pickupCreatedBy;
	private String pickupConfirmedBy;

	List<PickerDenialReportImpl> lines;
}