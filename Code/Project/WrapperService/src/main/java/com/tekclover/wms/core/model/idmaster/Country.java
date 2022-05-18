package com.tekclover.wms.core.model.idmaster;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class Country {

	private String countryId;
	private String countryName;
	private String languageId;
}
