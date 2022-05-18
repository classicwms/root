package com.tekclover.wms.api.idmaster.model.plantid;

import java.util.Date;

import lombok.Data;

@Data
public class AddPlantId {

	private String languageId;
	private String description;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
