package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;

import java.util.Date;
import java.util.List;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddBarcodeSubTypeId {

	private String companyCodeId;
	private String plantId;
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	@NotNull(message = "BarCode Type Id is mandatory")
	private Long barcodeTypeId;
	@NotNull(message = "BarCode Sub Type Id is mandatory")
	private Long barcodeSubTypeId;
	private String languageId;
	private String barcodeSubType;
	private Long deletionIndicator;
	private List<AddBarcodeSubTypeId> addBarcodeSubTypeId;

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
}
