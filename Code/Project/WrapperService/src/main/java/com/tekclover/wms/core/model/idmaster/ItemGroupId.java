package com.tekclover.wms.core.model.idmaster;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

	public class ItemGroupId { 

	private String companyCodeId;
	private String plantId;
	private String warehouseId;
	private Long itemTypeId;
	private Long itemGroupId;
	private String itemGroup;
	private Long deletionIndicator;
	private String createdBy;
	private Date createdOn;
	private String updatedBy;
	private Date updatedOn;
}
