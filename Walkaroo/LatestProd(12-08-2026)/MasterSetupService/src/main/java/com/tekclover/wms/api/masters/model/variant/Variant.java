package com.tekclover.wms.api.masters.model.variant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `VAR_ID`,`LEVEL_ID`'VAR_SUB_ID'
 */
@Table(
		name = "tblvariant",
		uniqueConstraints = {
				@UniqueConstraint (
						name = "unique_key_variant",
						columnNames = {"ID","LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "VAR_ID", "LEVEL_ID","VAR_SUB_ID"})
		}
)
@IdClass(VariantCompositeKey.class)
public class Variant { 

	@Id
	@Column(name = "ID")
	private Long id;

	@Id
	@Column(name = "VAR_ID")
	private String variantId;

	@Id
	@Column(name = "C_ID")
	private String companyId;

	@Id
	@Column(name = "PLANT_ID")
	private String plantId;

	@Id
	@Column(name = "WH_ID")
	private String warehouseId;

	@Id
	@Column(name = "VAR_SUB_ID")
	private String variantSubId;

	@Id
	@Column(name = "LEVEL_ID")
	private Long levelId;

	@Id
	@Column(name = "LANG_ID")
	private String languageId;

	@Column(name="LEVEL_REF")
	private String levelReference;

	@Column(name="LEVEL_ID_DESC")
	private String levelIdAndDescription;

	@Column(name = "C_TEXT")
	private String description;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

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
