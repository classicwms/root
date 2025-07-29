package com.tekclover.wms.core.batch.dto;

import lombok.Data;

@Data
public class IMPartner {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String businessPartnerType;
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

	private String partnerItemBarcode;
	private String manufacturerCode;
	private String manufacturerName;
	private String partnerName;
	private String vendorItemBarcode;
	private Double stock;
	private String stockUom;

	/**
	 * @param businessPartnerCode
	 * @param languageId
	 * @param companyCodeId
	 * @param plantId
	 * @param warehouseId
	 * @param itemCode
	 * @param businessPartnerType
	 * @param partnerItemBarcode
	 * @param manufacturerCode
	 * @param manufacturerName
	 * @param brandName
	 * @param partnerName
	 * @param partnerItemNo
	 * @param vendorItemBarcode
	 * @param mfrBarcode
	 * @param stock
	 * @param stockUom
	 * @param statusId
	 * @param referenceField1
	 * @param referenceField2
	 * @param referenceField3
	 * @param deletionIndicator
	 * @param createdBy
	 */
	public IMPartner(String businessPartnerCode, String languageId, String companyCodeId, String plantId, String warehouseId,
					 String itemCode, String businessPartnerType, String partnerItemBarcode, String manufacturerCode,
					 String manufacturerName, String brandName, String partnerName, String partnerItemNo, String vendorItemBarcode,
					 String mfrBarcode, Double stock, String stockUom, Long statusId, String referenceField1, String referenceField2,
					 String referenceField3, Long deletionIndicator, String createdBy) {

		this.businessPartnerCode = businessPartnerCode;
		this.languageId = languageId;
		this.companyCodeId = companyCodeId;
		this.plantId = plantId;
		this.warehouseId = warehouseId;
		this.itemCode = itemCode;
		this.businessPartnerType = businessPartnerType;
		this.partnerItemBarcode = partnerItemBarcode;
		this.manufacturerCode = manufacturerCode;
		this.manufacturerName = manufacturerName;
		this.brandName = brandName;
		this.partnerName = partnerName;
		this.partnerItemNo = partnerItemNo;
		this.vendorItemBarcode = vendorItemBarcode;
		this.mfrBarcode = mfrBarcode;
		this.stock = stock;
		this.stockUom = stockUom;
		this.statusId = statusId;
		this.referenceField1 = referenceField1;
		this.referenceField2 = referenceField2;
		this.referenceField3 = referenceField3;
		this.deletionIndicator = deletionIndicator;
		this.createdBy = createdBy;
	}
}
