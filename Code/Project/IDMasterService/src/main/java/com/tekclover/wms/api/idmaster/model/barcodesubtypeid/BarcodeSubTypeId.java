package com.tekclover.wms.api.idmaster.model.barcodesubtypeid;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `BAR_TYP_ID`, `BAR_SUB_ID`, `LANG_ID`
 */
@Table(
		name = "tblbarcodesubtypeid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_barcodesubtypeid", 
						columnNames = {"C_ID", "PLANT_ID", "WH_ID", "BAR_TYP_ID", "BAR_SUB_ID", "LANG_ID"})
				}
		)
@IdClass(BarcodeSubTypeIdCompositeKey.class)
public class BarcodeSubTypeId { 
	
	@Id
	@Column(name = "C_ID")
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
	@Column(name = "BAR_TYP_ID")
	private Long barcodeTypeId;
	
	@Id
	@Column(name = "BAR_SUB_ID")
	private Long barcodeSubTypeId;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "BAR_SUB_TYP")
	private String barcodeSubType;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "CTD_BY")
	private String createdBy;
	
	@Column(name = "CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();
}
