package com.tekclover.wms.api.inbound.orders.model.outbound.pickup;

import java.util.Date;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Data
public class AddPickupLine {

	private String languageId;
	private Long companyCodeId;
	private String plantId;
	private String warehouseId;
	private String preOutboundNo;
	private String refDocNumber;
	private String partnerCode;
	private Long lineNumber;
	private String pickupNumber;
	private Double pickConfirmQty;
	private Double allocatedQty;
	private String itemCode;
	private String actualHeNo;
	private String pickedStorageBin;
	private String pickedPackCode;
	private Long variantCode;
	private String variantSubCode;
	private String batchSerialNumber;
	private String pickQty;
	private String pickUom;
	private Long stockTypeId;
	private Long specialStockIndicatorId;
	private String description;
	private String manufacturerPartNo;
	private String assignedPickerId;
	private String pickPalletCode;
	private String pickCaseCode;
	private Long outboundOrderTypeId;
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
	private String pickupCreatedBy;
	private Date pickupCreatedOn;
	private String pickupConfirmedBy;
	private Date pickupConfirmedOn;
	private String pickupUpdatedBy;
	private Date pickupupdatedOn;
	private String pickupReversedBy;
	private Date pickupReversedOn;

	private Double inventoryQuantity;
	private Double pickedCbm;
	private String cbmUnit;
	private String manufacturerCode;
	private String manufacturerName;
	private String storageSectionId;
	private String origin;
	private String brand;
	private String barcodeId;
	private String levelId;

	private Double bagSize;
	private Double NoBags;
	
	/**
	 * 
	 * @return
	 */
	public String uniqueAttributes () {
		String uk = languageId + companyCodeId + plantId + warehouseId + preOutboundNo + refDocNumber + partnerCode + lineNumber + 
				pickupNumber + itemCode + pickedStorageBin + pickedPackCode;
		log.info("---------PickupLine---------UNIQUE-KEY----------> : " + uk);
		return uk;
	}
}
