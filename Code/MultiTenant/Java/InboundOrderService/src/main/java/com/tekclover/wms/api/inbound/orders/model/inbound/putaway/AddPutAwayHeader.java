package com.tekclover.wms.api.inbound.orders.model.inbound.putaway;

import java.util.Date;

import lombok.Data;

@Data
public class AddPutAwayHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preInboundNo;
	private String refDocNumber;
	private String goodsReceiptNo;
	private String palletCode;
	private String caseCode;
	private String packBarcodes;
	private String putAwayNumber;
	private String proposedStorageBin;
	private Long inboundOrderTypeId;
	private Double putAwayQuantity;
	private String putAwayUom;
	private Long strategyTypeId;
	private String strategyNo;
	private String proposedHandlingEquipment;
	private String assignedUserId;
	private Long statusId;
	private String quantityType;
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
