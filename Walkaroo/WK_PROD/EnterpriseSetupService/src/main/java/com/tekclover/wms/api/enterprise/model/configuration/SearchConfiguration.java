package com.tekclover.wms.api.enterprise.model.configuration;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchConfiguration {

	private Long configurationId;
	public String companyCodeId;
	private String plantId;
	private String languageId;
	private String warehouseId;
	private List<String> profile;
    public List<String> createdBy;
    private Date startCreatedOn;
	private Date endCreatedOn;
}
