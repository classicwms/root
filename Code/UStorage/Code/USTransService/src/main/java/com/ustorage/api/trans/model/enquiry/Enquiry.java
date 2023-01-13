package com.ustorage.api.trans.model.enquiry;

import java.util.Date;

import javax.persistence.*;

import com.ustorage.api.trans.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblenquiry")
@Where(clause = "IS_DELETED='0'")
public class Enquiry {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_enquiry")
	@GenericGenerator(name = "seq_enquiry", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "IQ"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "ENQUIRY_ID")
	private String enquiryId;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "ENQUIRY_NAME")
	private String enquiryName;
			
	@Column(name = "ENQUIRY_MOBILE_NUMBER")
	private String enquiryMobileNumber;
	
	@Column(name = "REQUIREMENT_DETAIL")
	private String requirementDetail;
				
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "CUSTOMER_GROUP")
	private String customerGroup;
	
	@Column(name = "ENQUIRY_STORE_SIZE")
	private String enquiryStoreSize;
	
	@Column(name = "ENQUIRY_STATUS")
	private String enquiryStatus;
	
	@Column(name = "ENQUIRY_REMARKS")
	private String enquiryRemarks;

	@Column(name = "SBU")
	private String sbu;
	
	@Column(name = "REQUIREMENT_TYPE")
	private String requirementType;

	@Column(name = "RENT_TYPE")
	private String rentType;

	@Column(name = "ADDRESS_FROM", columnDefinition = "nvarchar(1000)")
	private String addressFrom;
	@Column(name = "ADDRESS_TO", columnDefinition = "nvarchar(1000)")
	private String addressTo;

	@Column(name = "NUMBER_OF_TRIPS")
	private String numberOfTrips;
	@Column(name = "PACKING_COST")
	private Float packingCost;
	@Column(name = "JOBCARD_TYPE")
	private String jobcardType;


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
