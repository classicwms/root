package com.tekclover.wms.core.model.masters;

import java.util.Date;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddBomLine {

	private String languageId;
	
	private String companyCode;
	
	private String plantId;
	
	private String warehouseId;
	
	private Long bomNumber;
	
	private String childItemCode;
	
	private Double childItemQuantity;

	private Long statusId;

	private Long sequenceNo;

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

    private Date createdOn = new Date();

    private String updatedBy;

	private Date updatedOn = new Date();
	private String uom;
    
}
