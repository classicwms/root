package com.tekclover.wms.api.inbound.orders.model.mnc;

import lombok.Data;

@Data
public class UpdateInhouseTransferLine {

	private String languageId;

	private String companyCodeId;

	private String plantId;

	private String warehouseId;

	private String transferNumber;

	private String sourceItemCode;

	private Long sourceStockTypeId;

	private String sourceStorageBin;

	private String targetItemCode;

	private Long stockTypeId;

	private String targetStorageBin;

	private Double transferOrderQty;

	private Double transferConfirmedQty;

	private String transferUom;

	private String palletCode;

	private String caseCode;

	private String packBarcodes;

	private Long specialStockIndicatorId;

	private String barcodeId;

	private Long statusId;

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

	private String remarks;

	private String updatedBy;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;

}
