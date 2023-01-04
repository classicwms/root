package com.ustorage.api.trans.model.agreement;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.ustorage.api.trans.model.storenumber.StoreNumber;
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
@Table(name = "tblagreement")
@Where(clause = "IS_DELETED=0")
public class Agreement {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_agreement")
	@GenericGenerator(name = "seq_agreement", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "AG"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "AGREEMENT_NUMBER")
	private String agreementNumber;

	@Column(name = "CODE_ID")
	private String codeId;
	
	@Column(name = "QUOTE_NUMBER")
	private String quoteNumber;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;

	@Column(name = "SBU")
	private String sbu;
	
	@Column(name = "CIVIL_ID_NUMBER")
	private String civilIDNumber;
	
	@Column(name = "NATIONALITY")
	private String nationality;
	
	@Column(name = "ADDRESS")
	private String address;
	
	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;
	
	@Column(name = "SECONDARY_NUMBER")
	private String secondaryNumber;
		
	@Column(name = "FAX_NUMBER")
	private String faxNumber;
	
	@Column(name = "ITEMS_TO_BE_STORED")
	private String itemsToBeStored;
	
	@Column(name = "START_DATE")
	private Date startDate;
	
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name = "LOCATION")
	private String location;

	@Column(name = "SIZE")
	private String size;
	
	@Column(name = "INSURANCE")
	private String insurance;
	
	@Column(name = "RENT")
	private String rent;
	
	@Column(name = "RENT_PERIOD")
	private String rentPeriod;
	
	@Column(name = "TOTAL_RENT")
	private String totalRent;
	
	@Column(name = "PAYMENT_TERMS")
	private String paymentTerms;
	
	@Column(name = "AGREEMENT_DISCOUNT")
	private String agreementDiscount;
	
	@Column(name = "TOTAL_AFTER_DISCOUNT")
	private String totalAfterDiscount;
	
	@Column(name = "NOTES")
	private String notes;

	@OneToMany(cascade = CascadeType.ALL,mappedBy = "agreementNumber",fetch = FetchType.EAGER)
	private Set<StoreNumber> storeNumbers;
	//@Column(name = "STORE_NUMBER")
	//private String StoreNumbers;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "AGREEMENT_TYPE")
	private String agreementType;

	@Column(name = "DEPOSIT")
	private String deposit;

	@Column(name = "OTHERS")
	private String others;

	@Column(name = "ITEMS_TO_BE_STORED1")
	private String itemsToBeStored1;

	@Column(name = "ITEMS_TO_BE_STORED2")
	private String itemsToBeStored2;

	@Column(name = "ITEMS_TO_BE_STORED3")
	private String itemsToBeStored3;

	@Column(name = "ITEMS_TO_BE_STORED4")
	private String itemsToBeStored4;

	@Column(name = "ITEMS_TO_BE_STORED5")
	private String itemsToBeStored5;

	@Column(name = "ITEMS_TO_BE_STORED6")
	private String itemsToBeStored6;

	@Column(name = "ITEMS_TO_BE_STORED7")
	private String itemsToBeStored7;

	@Column(name = "ITEMS_TO_BE_STORED8")
	private String itemsToBeStored8;

	@Column(name = "ITEMS_TO_BE_STORED9")
	private String itemsToBeStored9;

	@Column(name = "ITEMS_TO_BE_STORED10")
	private String itemsToBeStored10;

	@Column(name = "ITEMS_TO_BE_STORED11")
	private String itemsToBeStored11;

	@Column(name = "ITEMS_TO_BE_STORED12")
	private String itemsToBeStored12;

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
