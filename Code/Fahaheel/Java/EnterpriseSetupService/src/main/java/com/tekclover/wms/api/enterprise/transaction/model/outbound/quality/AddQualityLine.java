package com.tekclover.wms.api.enterprise.transaction.model.outbound.quality;

import java.util.Date;

import lombok.Data;

@Data
public class AddQualityLine {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String actualHeNo;
	private String pickPackBarCode;
	private String qualityInspectionNo;
	private String itemCode;
	private Long outboundOrderTypeId;
	private Long statusId;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
	private String packingMaterialNo;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private Double qualityQty;	
	private Double pickConfirmQty;
	private String qualityConfirmUom;
	private String rejectQty;
	private String rejectUom;
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
	private String qualityCreatedBy;
	private Date qualityCreatedOn;
	private String qualityConfirmedBy;
	private Date qualityConfirmedOn;
	private String qualityUpdatedBy;
	private Date qualityUpdatedOn;
	private String qualityReversedBy;
	private Date qualityReversedOn;

	private String manufactureName;

	/**
	 * 
	 * @return
	 */
	public String uniqueAttributes () {
		return languageId + companyCodeId + plantId + warehouseId + preOutboundNo + refDocNumber + 
				partnerCode + lineNumber + qualityInspectionNo + itemCode;
	}
}