package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateBarcodeSubTypeId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long barcodeTypeId;
	private Long barcodeSubTypeId;
	private String languageId;
	private String barcodeSubType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
