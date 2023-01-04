package com.ustorage.api.trans.model.handlingcharge;

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
@Table(name = "tblhandlingcharge")
public class HandlingCharge {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_handling_charge")
	@GenericGenerator(name = "seq_handling_charge", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "HC"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.NUMBER_FORMAT_PARAMETER, value = "%010d")})
	@Column(name = "ITEM_CODE")
	private String itemCode;

	@Column(name = "CODE_ID")
	private String codeId;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "ITEM_TYPE")
	private String itemType;

	@Column(name = "SBU")
	private String sbu;
		
	@Column(name = "ITEM_GROUP")
	private String itemGroup;
		
	@Column(name = "SALE_UNIT_OF_MEASURE")
	private String saleUnitOfMeasure;
		
	@Column(name = "UNIT_PRICE")
	private String unitPrice;

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
