package com.tekclover.wms.api.idmaster.model.city;

import java.util.Date;

import javax.persistence.*;

import com.tekclover.wms.api.idmaster.model.binsectionid.BinSectionIdCompositeKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `CITY_ID`,`STATE_ID`, `COUNTRY_ID`, `ZIP_CD`,`LANG_ID`
 */
@Table(
		name = "tblcityid",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_key_tblcityid",
						columnNames = {"CITY_ID", "STATE_ID", "COUNTRY_ID", "LANG_ID"})
		}
)
@IdClass(CityIdCompositeKey.class)
public class City { 
	
	@Id
	@Column(name = "CITY_ID",columnDefinition = "nvarchar(10)")
	private String cityId;

	@Id
	@Column(name = "STATE_ID",columnDefinition = "nvarchar(10)")
	private String stateId;

	@Id
	@Column(name = "COUNTRY_ID",columnDefinition = "nvarchar(10)")
	private String countryId;

	@Id
	@Column(name = "LANG_ID",columnDefinition = "nvarchar(5)")
	private String languageId;

	@Column(name = "ZIP_CD")
	private Long zipCode;

	@Column(name = "CITY_NM",columnDefinition = "nvarchar(50)")
	private String cityName;

	@Column(name="STATE_ID_DESC",columnDefinition = "nvarchar(500)")
	private String stateIdAndDescription;

	@Column(name="COUNTRY_ID_DESC",columnDefinition = "nvarchar(500)")
	private String countryIdAndDescription;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "REF_FIELD_1",columnDefinition = "nvarchar(200)")
	private String referenceField1;

	@Column(name = "REF_FIELD_2",columnDefinition = "nvarchar(200)")
	private String referenceField2;

	@Column(name = "REF_FIELD_3",columnDefinition = "nvarchar(200)")
	private String referenceField3;

	@Column(name = "REF_FIELD_4",columnDefinition = "nvarchar(200)")
	private String referenceField4;

	@Column(name = "REF_FIELD_5",columnDefinition = "nvarchar(200)")
	private String referenceField5;

	@Column(name = "REF_FIELD_6",columnDefinition = "nvarchar(200)")
	private String referenceField6;

	@Column(name = "REF_FIELD_7",columnDefinition = "nvarchar(200)")
	private String referenceField7;

	@Column(name = "REF_FIELD_8",columnDefinition = "nvarchar(200)")
	private String referenceField8;

	@Column(name = "REF_FIELD_9",columnDefinition = "nvarchar(200)")
	private String referenceField9;

	@Column(name = "REF_FIELD_10",columnDefinition = "nvarchar(200)")
	private String referenceField10;

	@Column(name = "CTD_BY",columnDefinition = "nvarchar(50)")
	private String createdBy;

	@Column(name = "CTD_ON")
    private Date createdOn = new Date();

	@Column(name = "UTD_BY",columnDefinition = "nvarchar(50)")
    private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}