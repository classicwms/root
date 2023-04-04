package com.tekclover.wms.api.idmaster.model.country;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AddCountry {

	@NotBlank(message = "Country Id is mandatory")
    private String countryId;
	
	private String countryName;
	
	@NotBlank(message = "Language is mandatory")
	private String languageId;
    
	private String createdBy;
}
