package com.tekclover.wms.api.idmaster.model.state;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddState {

	@NotBlank(message = "State Id is mandatory")
    private String stateId;
	
	private String stateName;
	
	@NotBlank(message = "Country Id is mandatory")
	private String countryId;
	
	@NotBlank(message = "Language is mandatory")
	private String languageId;
    
	private String createdBy;
}
