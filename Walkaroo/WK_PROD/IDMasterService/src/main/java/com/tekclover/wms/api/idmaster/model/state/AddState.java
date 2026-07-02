package com.tekclover.wms.api.idmaster.model.state;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddState {

	@NotBlank(message = "State Id is mandatory")
    private String stateId;
	
	private String stateName;
	
	@NotBlank(message = "Country Id is mandatory")
	private String countryId;
	
	@NotNull(message = "Language is mandatory")
	private String languageId;
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
