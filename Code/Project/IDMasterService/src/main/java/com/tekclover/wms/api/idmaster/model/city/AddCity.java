package com.tekclover.wms.api.idmaster.model.city;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddCity {
	
	@NotBlank(message = "City Id is mandatory")
	private String cityId;
	
	private String cityName;
	
	@NotBlank(message = "State Id is mandatory")
	private String stateId;
	
	@NotBlank(message = "Country Id is mandatory")
	private String countryId;
	
	@NotNull(message = "Zip code is mandatory")
	private Long zipCode;

	@NotBlank(message = "Language is mandatory")
	private String languageId;
    
	private String createdBy;
}
