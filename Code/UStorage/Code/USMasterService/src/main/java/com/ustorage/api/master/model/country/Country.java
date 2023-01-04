package com.ustorage.api.master.model.country;

import com.ustorage.api.master.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblcountry")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_country")
	@GenericGenerator(name = "seq_country", strategy = "com.ustorage.api.master.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "CY"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%011d")})
	@Column(name = "CODE")
	private String code;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "COUNTRY_NAME")
	private String countryName;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator = 0L;

	@Column(name = "REF_FIELD_1")
	private String referenceField1;

	@Column(name = "REF_FIELD_2")
	private String referenceField2;

	@Column(name = "REF_FIELD_3")
	private String referenceField3;

	@Column(name = "REF_FIELD_4")
	private String referenceField4;

	@Column(name = "REF_FIELD_5")
	private String referenceField5;

	@Column(name = "REF_FIELD_6")
	private String referenceField6;

	@Column(name = "REF_FIELD_7")
	private String referenceField7;

	@Column(name = "REF_FIELD_8")
	private String referenceField8;

	@Column(name = "REF_FIELD_9")
	private String referenceField9;

	@Column(name = "REF_FIELD_10")
	private String referenceField10;

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn;

	@Column(name = "UTD_BY")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;
}
