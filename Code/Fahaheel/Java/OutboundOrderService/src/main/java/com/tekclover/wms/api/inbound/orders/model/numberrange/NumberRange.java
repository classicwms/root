package com.tekclover.wms.api.inbound.orders.model.numberrange;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
// `LANG_ID`, `CLASS_ID`, `NUM_RAN_CODE`, `NUM_RAN_OBJ`
@Table(
		name = "tblnumberrangeid", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_numberrange", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "NUM_RAN_CODE"})
				}
		)
@IdClass(NumberRangeCompositeKey.class)
public class NumberRange {
	@Id
	@Column(name = "LANG_ID",columnDefinition = "nvarchar(5)")
	private String languageId;
	
	@Id
	@Column(name = "C_ID",columnDefinition = "nvarchar(50)")
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID",columnDefinition = "nvarchar(50)")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID",columnDefinition = "nvarchar(50)")
	private String warehouseId;
	
	@Id
	@Column(name = "NUM_RAN_CODE")
	private Long numberRangeCode;
	

	@Column(name = "FISCAL_YEAR")
	private Long fiscalYear;

	@Column(name = "NUM_RAN_OBJ",columnDefinition = "nvarchar(200)")
	private String numberRangeObject;
	
	@Column(name = "NUM_RAN_FROM")
	private Long numberRangeFrom;

	@Column(name="COMP_ID_DESC",columnDefinition = "nvarchar(500)")
	private String companyIdAndDescription;

	@Column(name="PLANT_ID_DESC",columnDefinition = "nvarchar(500)")
	private String plantIdAndDescription;

	@Column(name="WH_ID_DESC",columnDefinition = "nvarchar(500)")
	private String warehouseIdAndDescription;
	
	@Column(name = "NUM_RAN_TO")
	private Long numberRangeTo;
	
	@Column(name = "NUM_RAN_CURRENT",columnDefinition = "nvarchar(50)")
	private String numberRangeCurrent;
	
	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;
	
	@Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
