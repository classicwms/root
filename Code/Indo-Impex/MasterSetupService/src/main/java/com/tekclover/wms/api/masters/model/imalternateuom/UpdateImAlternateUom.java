package com.tekclover.wms.api.masters.model.imalternateuom;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateImAlternateUom {

	private Long id;
    private String languageId;
	
	private String companyCodeId;
	
	private String plantId;
	
	private String warehouseId;
	
	private String itemCode;
	
	private String uomId;
	private String alternateUom;
	
	private Long slNo;
	
	private Long fromUnit;
	
	private Long toUnit;
	
	private Double qpc20Ft;
	
	private Double qpc40Ft;
	
	private String alternateUomBarcode ;
	
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
	private Double uomIdQty;
	private Double alternateUomQty;
}