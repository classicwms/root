package com.tekclover.wms.core.model.transaction;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

	public class InboundLine { 

	private String languageId;
	private String companyCode;
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
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String confirmedBy;
	private Date confirmedOn = new Date();

	private String middlewareId;
	private String middlewareTable;

}
