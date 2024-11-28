package com.tekclover.wms.api.enterprise.model.variant;

import java.util.Date;
import java.util.Set;

import javax.persistence.*;

import com.tekclover.wms.api.enterprise.model.batchserial.BatchSerialCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyId;

	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;

	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Id
	@Column(name = "VAR_SUB_ID")
	private String variantSubId;

	@Id
	@Column(name = "LEVEL_ID")
	private Long levelId;

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
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

	@Column(name = "CTD_BY", columnDefinition = "nvarchar(100)")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "UTD_BY", columnDefinition = "nvarchar(100)")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}