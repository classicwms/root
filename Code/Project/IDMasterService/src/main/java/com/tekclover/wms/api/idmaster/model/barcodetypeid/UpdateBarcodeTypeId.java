package com.tekclover.wms.api.idmaster.model.barcodetypeid;

import java.util.Date;

import lombok.Data;

@Data
public class UpdateBarcodeTypeId {

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long barcodeTypeId;
	private String languageId;
	private String barcodeType;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
