package com.ustorage.api.trans.model.paymentvoucher;

import java.util.Date;

import javax.persistence.*;

import com.ustorage.api.trans.sequence.BaseSequence;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblpaymentvoucher")
public class PaymentVoucher {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_payment_voucher")
	@GenericGenerator(name = "seq_payment_voucher", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "PV"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "VOUCHER_ID")
	private String voucherId;

	@Column(name = "CODE_ID")
	private String codeId;

	@Column(name = "SERVICE_TYPE")
	private String serviceType;
			
	@Column(name = "STORE_NUMBER")
	private String storeNumber;
	
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
				
	@Column(name = "CONTRACT_NUMBER")
	private String contractNumber;
	
	@Column(name = "PERIOD")
	private String period;

	@Column(name = "SBU")
	private String sbu;
	
	@Column(name = "START_DATE")
	private Date startDate;

	@Column(name = "VOUCHER_DATE")
	private Date voucherDate;

	@Column(name = "PAID_DATE")
	private Date paidDate;
	
	@Column(name = "END_DATE")
	private Date endDate;
	
	@Column(name = "VOUCHER_AMOUNT")
	private String voucherAmount;
	
	@Column(name = "MODE_OF_PAYMENT")
	private String modeOfPayment;
	
	@Column(name = "PAYMENT_REFERENCE")
	private String paymentReference;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "VOUCHER_STATUS")
	private String voucherStatus;
	
	@Column(name = "DOCUMENT_TYPE")
	private String documentType;

	@Column(name = "BANK")
	private String bank;

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
