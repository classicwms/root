package com.tekclover.wms.api.masters.model.impartner;

import lombok.Data;

@Data
public class UpdateImPartner {

	private String languageId;
	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private String itemCode;
	private String businessPartnerType;
	private String businessPartnerCode;
	private String partnerItemBarcode;
	private String manufacturerCode;
	private String manufacturerName;
	private String partnerName;
	private String partnerItemNo;
	private String vendorItemBarcode;
	private String mfrBarcode;
	private String brandName;
	private Double stock;
	private String stockUom;
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
}
