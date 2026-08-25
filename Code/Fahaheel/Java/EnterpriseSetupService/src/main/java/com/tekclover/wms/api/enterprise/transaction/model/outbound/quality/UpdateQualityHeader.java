package com.tekclover.wms.api.enterprise.transaction.model.outbound.quality;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateQualityHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String pickupNumber;
	private String qualityInspectionNo;
	private String actualHeNo;
	private Long outboundOrderTypeId;
	private Long statusId;
	private String qcToQty;
	private String qcUom;
	private String manufacturerPartNo;
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
	private String qualityCreatedBy;
	private Date qualityCreatedOn = new Date();
	private String qualityConfirmedBy;
	private Date qualityConfirmedOn = new Date();
	private String qualityUpdatedBy;
	private Date qualityUpdatedOn = new Date();
	private String qualityReversedBy;
	private Date qualityReversedOn = new Date();
}