package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class UpdatePackingLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String packingNo;
	private String itemCode;
	private Long variantCode;
	private String variantSubCode;
	private String packCode;
	private String batchSerialNumber;
	private Long outboundOrderTypeId;
	private Long statusId;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String packingMaterialNo;
	private Double packQtyPerItem;
	private Double numberOfPacks;
	private Boolean shrinkWrapReqd;
	private Long deletionIndicator;
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
	private String packConfirmedBy;
	private Date packConfirmedOn = new Date();
	private String packUpdatedBy;
	private Date packUpdatedOn = new Date();
	private String packingReversedBy;
	private Date packingReversedOn = new Date();

}
