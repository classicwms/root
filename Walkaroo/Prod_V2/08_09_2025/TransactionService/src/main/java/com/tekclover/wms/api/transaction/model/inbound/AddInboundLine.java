package com.tekclover.wms.api.transaction.model.inbound;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AddInboundLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String refDocNumber;
	private String preInboundNo;
	private Long lineNo;
	private String itemCode;
	private Double orderQty;
	private String orderUom;
	private Double acceptedQty;
	private Double damageQty;
	private Double putawayConfirmedQty;
	private Double varianceQty;
	private Long variantCode;
	private String variantSubCode;
	private Long inboundOrderTypeId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String referenceOrderNo;
	private Long statusId;
	private String businessPartnerCode;
	private Date expectedArrivalDate;
	private String containerNo;
	private String invoiceNo;
	private String description ;
	private String manufacturerPartNo;
	private String hsnCode;
	private String itemBarcode;
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

	private List<AddInboundLine> addInboundLine;
}
