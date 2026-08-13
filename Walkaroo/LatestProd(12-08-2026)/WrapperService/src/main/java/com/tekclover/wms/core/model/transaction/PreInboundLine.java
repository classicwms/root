package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import lombok.Data;

@Data
public class PreInboundLine { 
	
	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private Long lineNo;
	private String itemCode;
	private Long inboundOrderTypeId;
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
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
