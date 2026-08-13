package com.tekclover.wms.api.inbound.transaction.model.preoutbound;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class AddPreOutboundLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preOutboundNo;
	private String partnerCode;
	private Long lineNumber;
	private String itemCode;
	private Long outboundOrderTypeId;
	private Long variantCode;
	private String variantSubCode;
	private Long statusId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
	private String hsnCode;
	private String itemBarcode;
	private Double orderQty;
	private String orderUom;
	private Date requiredDeliveryDate;
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
	private String updatedBy;
	private Date updatedOn = new Date();

	private List<AddPreOutboundLine> addPreOutboundLine;
}
