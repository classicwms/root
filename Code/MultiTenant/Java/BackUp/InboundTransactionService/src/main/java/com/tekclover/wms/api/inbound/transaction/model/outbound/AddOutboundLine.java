package com.tekclover.wms.api.inbound.transaction.model.outbound;

import lombok.Data;

import java.util.Date;

@Data
public class AddOutboundLine {

	private String languageId;
	private Long companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private String deliveryOrderNo;
	private String batchSerialNumber;
	private Long variantCode;
	private String variantSubCode;
	private Long outboundOrderTypeId;
	private Long statusId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private Double orderQty;
	private String orderUom;
	private Double deliveryQty;
	private String deliveryUom;
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
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String deliveryConfirmedBy;
	private Date deliveryConfirmedOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
	private String reversedBy;
	private Date reversedOn = new Date();
}
