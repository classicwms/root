package com.tekclover.wms.api.idmaster.model.uomid;

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
 * `C_ID`, `UOM_ID`, `LANG_ID`
 */
@Table(
		name = "tbluomid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_uomid", 
						columnNames = {"C_ID", "UOM_ID", "LANG_ID"})
				}
		)
@IdClass(UomIdCompositeKey.class)
public class UomId { 
	
	@Id
	@Column(name = "C_ID") 
	private String companyCodeId;
	
	@Id
	@Column(name = "UOM_ID")
	private String uomId;
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Column(name = "UOM_TEXT")
	private String description;
	
	@Column(name = "UOM_TYP")
	private String uomType;
	
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
