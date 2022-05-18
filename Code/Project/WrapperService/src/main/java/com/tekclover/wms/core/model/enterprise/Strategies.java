package com.tekclover.wms.core.model.enterprise;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Strategies { 
	
	private Long strategyTypeId;
	private String companyId;
	private String plantId;
	private String warehouseId;
	private Long sequenceIndicator;
	private String strategyNo;
	private Long priority;
	private String languageId;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();

}
