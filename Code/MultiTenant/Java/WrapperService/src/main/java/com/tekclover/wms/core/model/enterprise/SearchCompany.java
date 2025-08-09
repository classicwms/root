package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import lombok.Data;

@Data
public class SearchCompany {
	/*
	 * C_ID
	 * VERT_ID
	 * COUNTRY
	 * CNT_NM
	 * CTD_BY
	 * CTD_ON
	 */
	private String companyId;
	private String languageId;
	private Long verticalId;
	private String country;
	private String contactName;
	private String createdBy;
    private Date startCreatedOn; //getStartCreatedOn()
	private Date endCreatedOn; //getEndCreatedOn()

}
