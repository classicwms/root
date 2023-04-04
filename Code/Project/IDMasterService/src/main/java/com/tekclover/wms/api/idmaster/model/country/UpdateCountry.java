package com.tekclover.wms.api.idmaster.model.country;

import lombok.Data;

@Data
public class UpdateCountry {

	private String countryName;
	private String languageId;
	private String updatedBy;
}
