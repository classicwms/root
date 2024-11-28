package com.tekclover.wms.api.enterprise.model.doors;

import java.util.Date;

import lombok.Data;

@Data
public class SearchDoors {
	/*
	 * C_ID
	 * VERT_ID
	 * COUNTRY
	 * CNT_NM
	 * CTD_BY
	 * CTD_ON
	 */
    public String companyId;
    public String verticalId;
    public String country;
    public String contactName;
    public String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
