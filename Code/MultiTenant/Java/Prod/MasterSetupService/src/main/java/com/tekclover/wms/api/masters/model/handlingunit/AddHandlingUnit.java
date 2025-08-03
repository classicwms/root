package com.tekclover.wms.api.masters.model.handlingunit;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddHandlingUnit {

	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	@NotBlank(message = "Company Code Id is mandatory")
	private String companyCodeId;
	
	@NotBlank(message = "Plant Id is mandatory")
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotBlank(message = "Handling Unit is mandatory")
	private String handlingUnit;
	
	private Double length;
	
	private String lengthUom;
	
	private Double width;	
	
	private String widthUom;
	
	private Double height;	
	
	private String heightUom;
	
	private Double volume;	
	
	private String volumeUom;
	
	private Double weight;	
	
	private String weightUom;
	
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
