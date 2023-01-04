package com.ustorage.api.trans.model.quote;

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
@Table(name = "tblquote")
@Where(clause = "IS_DELETED='0'")
public class Quote {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_quotation")
	@GenericGenerator(name = "seq_quotation", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "Q"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%011d")})
	@Column(name = "QUOTE_ID")
	private String quoteId;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "ENQUIRY_REFERENCE_NUMBER")
	private String enquiryReferenceNumber;
			
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;
	
	@Column(name = "REQUIREMENT")
	private String requirement;

	@Column(name = "SBU")
	private String sbu;
			
	@Column(name = "EMAIL")
	private String email;
	
	@Column(name = "CUSTOMER_GROUP")
	private String customerGroup;
	
	@Column(name = "STORE_SIZE")
	private String storeSize;
	
	@Column(name = "RENT")
	private String rent;
	
	@Column(name = "NOTES")
	private String notes;
	
	@Column(name = "DOCUMENT_STATUS")
	private String documentStatus;
	
	@Column(name = "REQUIREMENT_TYPE")
	private String requirementType;

	@Column(name = "CUSTOMER_CODE")
	private String customerCode;

	@Column(name = "STATUS")
	private String status;

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
