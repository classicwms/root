package com.tekclover.wms.api.idmaster.model.warehouseid;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * C_ID, WH_ID, LANG_ID, PLANT_ID
 */
@Table(
		name = "tblwarehouseid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_warehouse", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID"})
				}
		)
@IdClass(WarehouseCompositeKey.class)
public class Warehouse { 
	
	@Id
	@Column(name = "C_ID")
	private String companyCode;
	
	@Id
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Id
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_TEXT")
	private String warehouseDesc;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
