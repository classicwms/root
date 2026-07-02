package com.tekclover.wms.api.masters.model.imbasicdata2;

import lombok.Data;

@Data
public class UpdateImBasicData2 {

    private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String itemBarcode;
	
	private Double length;
	
	private Double width;
	
	private Double height;
	
	private String dimensionUom;
	
	private Double volume;
	
	private String volumeUom;
	
	private Double qpc20Ft;
	
	private Double qpc40Ft;
	
	private Double unitPrice;
	
	private Long currencyId;
	
	private Double grossWeight;
	
	private Double netWeight;
	
	private String weightUom;
	
	private String preferredStorageBin;
	
//	private Object photo;
	
	private String itemClass;
	
	private Boolean crossDockingIndicator;
	
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

	private String updatedBy;
}
