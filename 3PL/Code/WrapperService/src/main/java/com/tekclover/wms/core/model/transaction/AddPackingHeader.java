package com.tekclover.wms.core.model.transaction;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;
import java.util.List;

@Data
public class AddPackingHeader {
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
	private Double pickConfirmQty;
	private Double allocatedQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String remarks;
	private String packConfirmedBy;
	private Date packConfirmedOn = new Date();
	private String packUpdatedBy;
	private Date packUpdatedOn = new Date();
	private String packingReversedBy;
	private Date packingReversedOn = new Date();
	private Double threePLCbm;
	private String threePLUom;
	private String threePLBillStatus;
	private Double threePLLength;
	private Double threePLHeight;
	private Double threePLWidth;
	private Double rate;
	private String currency;
}
