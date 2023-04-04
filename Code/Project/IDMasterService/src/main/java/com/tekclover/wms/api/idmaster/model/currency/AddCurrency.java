package com.tekclover.wms.api.idmaster.model.currency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddCurrency {

	@NotNull(message = "Currency Id is mandatory")
	private Long currencyId;
	
	private String currencyDescription;
	
	@NotBlank(message = "Language is mandatory")
	private String languageId;
    
	private String createdBy;
}
