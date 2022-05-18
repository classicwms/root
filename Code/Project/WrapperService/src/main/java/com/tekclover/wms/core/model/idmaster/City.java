package com.tekclover.wms.core.model.idmaster;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class City {

	private String cityId;
	private String cityName;
	private String stateId;
	private String countryId;
	private Long zipCode;
	private String languageId;
	
	@JsonIgnore
	private String updatedBy;
}
