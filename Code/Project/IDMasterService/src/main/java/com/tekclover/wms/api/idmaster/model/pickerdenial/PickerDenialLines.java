package com.tekclover.wms.api.idmaster.model.pickerdenial;

import lombok.Data;

import java.util.Date;

@Data
public class PickerDenialLines {

	private String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private Long orderedQty;
	private Long shippedQty;
	private Date denialDate;
	private Date deliveryDate;
	private String sDenialDate;
	private String sDeliveryDate;
	private String pickupCreatedBy;
	private String pickupConfirmedBy;
}