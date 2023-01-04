package com.ustorage.api.master.model.servicerendered;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddServiceRendered {

//	@NotNull(message = "Code is mandatory")
	private String codeId;
	
//	@NotBlank(message = "Description is mandatory")
	private String description;
		
	private Long deletionIndicator;

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
