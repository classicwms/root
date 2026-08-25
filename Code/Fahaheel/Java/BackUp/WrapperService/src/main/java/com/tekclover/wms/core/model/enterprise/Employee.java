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
public class Employee { 
	
	private String employeeId;
	private String companyId;
	private String plantId;
	private String warehouseID;
	private String companyCode;
	private String warehouseNo;
	private Long processId;
	private String languageId;
	private String stroageSection;
	private String handlingEquipment;
	private String status;
	private Long deletionIndicator = 0L;
	private String createdBy;
	private Date createdOn = new Date();
	private String updatedBy;
	private Date updatedOn = new Date();
}
