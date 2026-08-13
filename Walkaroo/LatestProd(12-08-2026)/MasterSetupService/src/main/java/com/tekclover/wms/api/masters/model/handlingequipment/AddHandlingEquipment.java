package com.tekclover.wms.api.masters.model.handlingequipment;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddHandlingEquipment {

	@NotBlank(message = "Language is mandatory")
	private String languageId;
	
	@NotBlank(message = "Company Code Id is mandatory")
	private String companyCodeId;
	
	@NotBlank(message = "Plant Id is mandatory")
	private String plantId;
	
	@NotBlank(message = "Warehouse Id is mandatory")
	private String warehouseId;
	
	@NotNull(message = "Handling Equipment Id is mandatory")
	private String handlingEquipmentId;
	
	private String category;
	
	private String handlingUnit;
	
	private Date acquistionDate;
	
	private Double acquistionValue;
	
	private Long currencyId;
	
	private String modelNo;
	
	private String manufacturerPartNo;
	
	private String countryOfOrigin;
	
	private String heBarcode;
	
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
