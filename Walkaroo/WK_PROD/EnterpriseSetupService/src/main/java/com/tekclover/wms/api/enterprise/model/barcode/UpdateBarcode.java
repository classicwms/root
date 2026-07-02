package com.tekclover.wms.api.enterprise.model.barcode;

import lombok.Data;

@Data
public class UpdateBarcode {

	private Long barcodeTypeId;
    private String companyId;
	private String plantId;
    private String warehouseID;
	private String method;
	private Long barcodeSubTypeId;
	private Long levelId;
	private String levelReference;
	private Long processId;
	private String languageId;
	private String numberRangeFrom;
	private String numberRangeTo;
	private Double barcodeLength;
	private Double barcodeWidth;
	private String dimensionUom;
	private String labelInformation;
	private String updatedBy;
}
