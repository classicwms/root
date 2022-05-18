package com.tekclover.wms.api.enterprise.model.plant;

import java.util.Date;

import lombok.Data;

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
    private String contactName;
	private String city;
	private String state;
	private String country;
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
