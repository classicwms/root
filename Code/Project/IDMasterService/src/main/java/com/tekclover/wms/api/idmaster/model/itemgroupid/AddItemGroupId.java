package com.tekclover.wms.api.idmaster.model.itemgroupid;

import java.util.Date;

import lombok.Data;

@Data
public class AddItemGroupId {

	private Long itemTypeId;
	private Long itemGroupId;
	private String itemGroup;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
