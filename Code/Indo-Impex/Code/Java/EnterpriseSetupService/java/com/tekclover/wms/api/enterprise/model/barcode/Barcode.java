package com.tekclover.wms.api.enterprise.model.barcode;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `MTD`, `BAR_TYP_ID`, `BAR_SUB_ID`, `LEVEL_ID`, `LEVEL_REF`, `PROCESS_ID`
 */
@Table(
		name = "tblbarcode", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_barcode", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "MTD", "BAR_TYP_ID", "BAR_SUB_ID", 
								"LEVEL_ID", "LEVEL_REF", "PROCESS_ID"})
				}
		)
@IdClass(BarcodeCompositeKey.class)
public class Barcode { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;
	
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
	@Column(name = "MTD") 
	private String method;
	
	@Id
	@Column(name = "BAR_TYP_ID")
	private Long barcodeTypeId;
	
	@Id
	@Column(name = "BAR_SUB_ID")
	private Long barcodeSubTypeId;

	@Id
	@Column(name = "LEVEL_ID") 
	private Long levelId;
	
	@Id
	@Column(name = "LEVEL_REF") 
	private String levelReference;
	
	@Id
	@Column(name = "PROCESS_ID") 
	private Long processId;
	
	@Column(name = "NUM_RANGE_FROM")
	private String numberRangeFrom;
	
	@Column(name = "NUM_RANGE_TO") 
	private String numberRangeTo;
	
	@Column(name = "BAR_LEN")
	private Double barcodeLength;
	
	@Column(name = "BAR_WID")
	private Double barcodeWidth;
	
	@Column(name = "DIM_UOM") 
	private String dimensionUom;
	
	@Column(name = "LABL_INFO") 
	private String labelInformation;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;
	
	@Column(name = "CTD_BY", columnDefinition = "nvarchar(100)") 
	private String createdBy;
	
	@Column(name = "CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY", columnDefinition = "nvarchar(100)")
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn = new Date();

}