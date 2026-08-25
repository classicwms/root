package com.tekclover.wms.core.model.transaction;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AddGrLine {

	private String languageId;
	private String companyCode;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
	private List<PackBarcode> packBarcodes;
	private Long lineNo;
	private String itemCode;
	private Long inboundOrderTypeId;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String storageMethod;
	private Long statusId;
	private String businessPartnerCode;
	private String containerNo;
	private String invoiceNo;
	private String itemDescription;
	private String manufacturerPartNo;
	private String hsnCode;
	private String variantType;
	private String specificationActual;
	private String itemBarcode;
	private Double orderQty;
	private String orderUom;
	private Double goodReceiptQty;
	private String grUom;
	private Double acceptedQty;
	private Double damageQty;
	private String quantityType;
	private String assignedUserId;
	private String putAwayHandlingEquipment;
	private Double confirmedQty;
	private Double remainingQty;
	private String referenceOrderNo;
	private Double referenceOrderQty;
	private Double crossDockAllocationQty;
	private Date manufacturerDate;
	private Date expiryDate;
	private Double storageQty;
	private String remarks;
//	private String cbm;
	private String cbmUnit;
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
	private String confirmedBy;
	private Date confirmedOn = new Date();
}
