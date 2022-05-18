package com.tekclover.wms.api.idmaster.model.city;

import lombok.Data;

@Data
public class UpdateCity {

	private String cityName;
	private String stateId;
	private String countryId;
	private Long zipCode;
	private String languageId;
	private String updatedBy;
}
