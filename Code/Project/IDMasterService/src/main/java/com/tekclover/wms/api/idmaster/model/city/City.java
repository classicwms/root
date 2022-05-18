package com.tekclover.wms.api.idmaster.model.city;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblcityid")
public class City { 
	
	@Id
	@Column(name = "CITY_ID")
	private String cityId;
	
	@Column(name = "CITY_NM")
	private String cityName;
	
	@Column(name = "STATE_ID")
	private String stateId;
	
	@Column(name = "COUNTRY_ID")
	private String countryId;
	
	@Column(name = "ZIP_CD")
	private Long zipCode;

	@Column(name = "LANG_ID")
	private String languageId;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
