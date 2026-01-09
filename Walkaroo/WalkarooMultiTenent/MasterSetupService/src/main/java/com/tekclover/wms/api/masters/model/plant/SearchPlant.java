package com.tekclover.wms.api.masters.model.plant;

import lombok.Data;

import java.util.Date;

@Data
public class SearchPlant {
	/*
	 * C_ID
	 * PLANT_ID
	 * PLANT_TEXT
	 * CNT_NM
	 * CITY
	 * STATE
	 * COUNTRY
	 * CTD_BY
	 * CTD_ON
	 */
    private String companyId;
    private String plantId;
	private String languageId;
    private String contactName;
	private String city;
	private String state;
	private String country;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;


}
