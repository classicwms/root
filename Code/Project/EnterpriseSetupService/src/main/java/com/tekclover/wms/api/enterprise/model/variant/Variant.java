package com.tekclover.wms.api.enterprise.model.variant;

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
@Table(name = "tblvariant")
public class Variant { 
	
	@Id
	@Column(name = "VAR_ID")
	private String variantId;

	@Column(name = "C_ID")
	private String companyId;

	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Column(name = "WH_ID")
	private String warehouseID;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubId;
	
	@Column(name = "LEVEL_ID")
	private Long levelId;
	
	@Column(name = "LEVEL_REF")
	private String levelReferece;
	
	@Column(name = "LANG_ID")
	private String languageId;

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
