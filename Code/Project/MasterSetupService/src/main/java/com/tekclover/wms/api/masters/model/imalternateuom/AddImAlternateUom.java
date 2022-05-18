package com.tekclover.wms.api.masters.model.imalternateuom;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddImAlternateUom {

	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	@NotBlank(message = "Company Code Id is mandatory")
	private String companyCodeId;
	
	@NotBlank(message = "Plant Id is mandatory")
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Item Code is mandatory")
	private String itemCode;

	@NotBlank(message = "UOM Id is mandatory")
	private String uomId;
	
	@NotBlank(message = "Alternate UOM is mandatory")
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
	
	private String createdBy;
    
}
