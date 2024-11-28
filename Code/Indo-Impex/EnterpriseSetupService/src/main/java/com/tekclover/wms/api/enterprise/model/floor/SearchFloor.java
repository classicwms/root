package com.tekclover.wms.api.enterprise.model.floor;

import java.util.Date;

import lombok.Data;

@Data
public class SearchFloor {
	/*
	 * C_ID
	 * PLANT_ID
	 * WH_ID
	 * FL_ID
	 * FL_TEXT
	 * CTD_BY
	 * CTD_ON
	 */
    public String companyId;
    private String plantId;
    private String warehouseId;
	private String languageId;
	private Long floorId;	
    private String createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
