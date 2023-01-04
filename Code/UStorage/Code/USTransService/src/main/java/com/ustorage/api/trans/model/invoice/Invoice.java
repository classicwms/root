package com.ustorage.api.trans.model.invoice;

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
@Table(name = "tblinvoice")
@Where(clause = "IS_DELETED='0'")
public class Invoice {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_invoice")
	@GenericGenerator(name = "seq_invoice", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "IN"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "INVOICE_NUMBER")
	private String invoiceNumber;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "INVOICE_DATE")
	private Date invoiceDate;
			
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	private String customerType;

	@Column(name = "SBU")
	private String sbu;
	
	@Column(name = "DOCUMENT_NUMBER")
	private String documentNumber;
				
	@Column(name = "DOCUMENT_START_DATE")
	private Date documentStartDate;
	
	@Column(name = "DOCUMENT_END_DATE")
	private Date documentEndDate;
	
	@Column(name = "INVOICE_CURRENCY")
	private String invoiceCurrency;
	
	@Column(name = "INVOICE_AMOUNT")
	private String invoiceAmount;
	
	@Column(name = "INVOICE_DISCOUNT")
	private String invoiceDiscount;
	
	@Column(name = "TOTAL_AFTER_DISCOUNT")
	private String totalAfterDiscount;
	
	@Column(name = "INVOICE_DOCUMENT_STATUS")
	private String invoiceDocumentStatus;

	@Column(name = "REMARKS")
	private String remarks;

	@Column(name = "UNIT")
	private String unit;

	@Column(name = "Quantity")
	private String quantity;

	@Column(name = "MONTHLY_RENT")
	private String monthlyRent;

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
