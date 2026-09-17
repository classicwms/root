package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound;

import java.util.Date;

import lombok.Data;

@Data
	public class UpdatePreInboundLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String itemCode;
	private Long variantCode;
	private String variantSubCode;
	private Long statusId;
	private String itemDescription;
	private String containerNo;
	private String invoiceNo;
	private String businessPartnerCode;
	private String partnerItemNo;
	private String brandName;
	private String manufacturerPartNo;
	private String hsnCode;
	private Date expectedArrivalDate;
	private Double orderQty;
	private String orderUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String numberOfPallets;
	private String numberOfCases;
	private Double itemPerPalletQty;
	private Double itemCaseQty;
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
	private String referenceField11;
	private String referenceField12;
	private String referenceField13;
	private String referenceField14;
	private String referenceField15;
	private String referenceField16;
	private String referenceField17;
	private String referenceField18;
	private String referenceField19;
	private String referenceField20;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
