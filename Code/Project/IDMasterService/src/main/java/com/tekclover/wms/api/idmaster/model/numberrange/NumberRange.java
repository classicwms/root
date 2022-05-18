package com.tekclover.wms.api.idmaster.model.numberrange;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "NUM_RAN_CODE", "FISCAL_YEAR", "NUM_RAN_OBJ"})
				}
		)
@IdClass(NumberRangeCompositeKey.class)
public class NumberRange {
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
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
	@Column(name = "NUM_RAN_CODE")
	private Long numberRangeCode;
	
	@Id
	@Column(name = "FISCAL_YEAR")
	private Long fiscalYear;
	
	@Id
	@Column(name = "NUM_RAN_OBJ")
	private String numberRangeObject;
	
	@Column(name = "NUM_RAN_FROM")
	private Long numberRangeFrom;
	
	@Column(name = "NUM_RAN_TO")
	private Long numberRangeTo;
	
	@Column(name = "NUM_RAN_CURRENT")
	private String numberRangeCurrent;
	
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
