package com.ustorage.api.master.model.statusid;

import java.util.Date;

import lombok.Data;

@Data
public class AddStatusId {

	
	private String plantId;
	private String warehouseId;
	private Long statusId;
	private String status;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
