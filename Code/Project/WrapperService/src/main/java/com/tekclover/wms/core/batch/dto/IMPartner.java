package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class IMPartner { 
	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String businessParnterType;
	private String businessPartnerCode;
	private String partnerItemNo;
	private String mfrBarcode;
	private String brandName;
	private Long statusId;
	private String referenceField1;
	private String referenceField2;
	private String referenceField3;
	private Long deletionIndicator;
	private String createdBy;
	
	/**
	* @param languageId
	* @param companyCodeId
	* @param plantId
	* @param warehouseId
	* @param itemCode
	* @param businessParnterType
	* @param businessPartnerCode
	* @param partnerItemNo
	* @param mfrBarcode
	* @param brandName
	* @param statusId
	* @param referenceField1
	* @param referenceField2
	* @param referenceField3
	* @param deletionIndicator
	* @param createdBy
	*/
	public IMPartner (String languageId, String companyCodeId, String plantId, String warehouseId, String itemCode, String businessParnterType,
					String businessPartnerCode, String partnerItemNo, String mfrBarcode, String brandName, Long statusId, 
					String referenceField1, String referenceField2, String referenceField3, Long deletionIndicator, String createdBy) {
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.businessParnterType = businessParnterType;
		this.businessPartnerCode = businessPartnerCode;
		this.partnerItemNo = partnerItemNo;
		this.mfrBarcode = mfrBarcode;
		this.brandName = brandName;
		this.statusId = statusId;
		this.referenceField1 = referenceField1;
		this.referenceField2 = referenceField2;
		this.referenceField3 = referenceField3;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
