package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class AddBarcodeSubTypeId {

//	private String companyCodeId;
//	private String plantId;
	private String warehouseId;
	private Long barcodeTypeId;
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
