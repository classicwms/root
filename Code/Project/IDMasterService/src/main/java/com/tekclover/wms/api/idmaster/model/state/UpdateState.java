package com.tekclover.wms.api.idmaster.model.state;

import lombok.Data;

@Data
public class UpdateState {

	private String stateName;
	private String countryId;
	private String languageId;
	private String updatedBy;
}
