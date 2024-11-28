package com.tekclover.wms.api.enterprise.model.batchserial;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_MTD`,`LEVEL_ID`
 */
@Table(
		name = "tblbatchserial",
		uniqueConstraints = {
				@UniqueConstraint (
						name = "unique_key_batchserial",
						columnNames = {"ID","LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ST_MTD", "LEVEL_ID","MAINT"})
		}
)
@IdClass(BatchSerialCompositeKey.class)
public class BatchSerial { 

	@Id
	@Column(name="ID")
	private Long id;

	@Id
	@Column(name = "ST_MTD")
	private String storageMethod;

	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyId;

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;

	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;

	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Id
	@Column(name = "LEVEL_ID")
	private Long levelId;

	@Id
	@Column(name = "MAINT")
	private String maintenance;

	@Column(name="LEVEL_REF")
	private String levelReferences;

	@Column(name="LEVEL_ID_DESC")
	private String levelIdAndDescription;

	@Column(name="COMP_ID_DESC")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC")
	private String warehouseIdAndDescription;

	@Column(name = "C_TEXT")
	private String description;

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