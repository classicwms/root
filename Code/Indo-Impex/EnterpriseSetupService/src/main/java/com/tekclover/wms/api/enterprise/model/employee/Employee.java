package com.tekclover.wms.api.enterprise.model.employee;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblemployee")
public class Employee { 
	
	@Id
	@Column(name = "EMP_ID")
	private String employeeId;

	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyId;

	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;
	
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseID;
	
	@Column(name = "C_CODE")
	private String companyCode;
	
	@Column(name = "WH_NO")
	private String warehouseNo;
	
	@Column(name = "PROCESS_ID")
	private Long processId;
	
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;
	
	@Column(name = "ST_SEC")
	private String stroageSection;
	
	@Column(name = "HAND_EQP")
	private String handlingEquipment;
	
	@Column(name = "STATUS")
	private String status;

   	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

	@Column(name = "CTD_BY", columnDefinition = "nvarchar(100)")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "UTD_BY", columnDefinition = "nvarchar(100)")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();

}