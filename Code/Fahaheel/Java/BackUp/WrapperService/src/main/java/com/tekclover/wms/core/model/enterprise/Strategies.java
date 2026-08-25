package com.tekclover.wms.core.model.enterprise;

import java.util.Date;
import lombok.Data;

@Data
public class Strategies { 
	
	private Long strategyTypeId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long sequenceIndicator;
	private String languageId;
	private Long priority1;
	private Long priority2;
	private Long priority3;
	private Long priority4;
	private Long priority5;
	private Long priority6;
	private Long priority7;
	private Long priority8;
	private Long priority9;
	private Long priority10;
	private String priorityDescription1;
	private String priorityDescription2;
	private String priorityDescription3;
	private String priorityDescription4;
	private String priorityDescription5;
	private String priorityDescription6;
	private String priorityDescription7;
	private String priorityDescription8;
	private String priorityDescription9;
	private String priorityDescription10;
	private String plantIdAndDescription;
	private String companyIdAndDescription;
	private String warehouseIdAndDescription;
	private String description;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
