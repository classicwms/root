package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public class UpdatePackingHeader {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private String qualityInspectionNo;
	private String packingNo;
	private Long outboundOrderTypeId;
	private String qualityConfirmQty;
	private String qualityConfirmUom;
	private Long statusId;
	private String companyDescription;
	private String plantDescription;
	private String warehouseDescription;
	private String statusDescription;
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
	private String packConfirmedBy;
	private Date packConfirmedOn = new Date();
	private String packUpdatedBy;
	private Date packUpdatedOn = new Date();
	private String packingReversedBy;
	private Date packingReversedOn = new Date();
}
