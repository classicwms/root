package com.ustorage.api.master.model.sbu;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddSbu {

//	@NotNull(message = "Code is mandatory")
	private String codeId;
	
//	@NotBlank(message = "Description is mandatory")
	private String description;

	private String accountNo;
	private String bank;
	private String ibanNo;
	private String swiftCode;
	private String address;


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
