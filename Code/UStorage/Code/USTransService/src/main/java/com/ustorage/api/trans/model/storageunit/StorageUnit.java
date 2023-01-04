package com.ustorage.api.trans.model.storageunit;

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
@Table(name = "tblstorageunit")
public class StorageUnit {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_storage_unit")
	@GenericGenerator(name = "seq_storage_unit", strategy = "com.ustorage.api.trans.sequence.DefaultSequence", parameters = {
			@org.hibernate.annotations.Parameter(name = BaseSequence.INCREMENT_PARAM, value = "1"),
			@org.hibernate.annotations.Parameter(name = BaseSequence.VALUE_PREFIX_PARAMETER, value = "SU"),
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
	
	@Column(name = "DOOR_TYPE")
	private String doorType;
	
	@Column(name = "STORAGE_TYPE")
	private String storageType;
	
	@Column(name = "PHASE")
	private String phase;
	
	@Column(name = "ZONE")
	private String zone;
	
	@Column(name = "ROOM")
	private String room;
	
	@Column(name = "RACK")
	private String rack;
	
	@Column(name = "BIN")
	private String bin;

	@Column(name = "PRICE_METER_SQAURE")
	private String priceMeterSquare;
	
	@Column(name = "WEEKLY")
	private String weekly;
	
	@Column(name = "MONTHLY")
	private String monthly;
	
	@Column(name = "QUARTERLY")
	private String quarterly;
	
	@Column(name = "HALF_YEARLY")
	private String halfYearly;
	
	@Column(name = "YEARLY")
	private String yearly;
	
	@Column(name = "LENGTH")
	private String length;
	
	@Column(name = "WIDTH")
	private String width;
	
	@Column(name = "ORIGINAL_DIMENTION")
	private String originalDimention;
	
	@Column(name = "ROUNDED_DIMENTION")
	private String roundedDimention;
	
	@Column(name = "AVAILABILITY")
	private String availability;
	
	@Column(name = "OCCUPIED_FROM")
	private Date occupiedFrom;
	
	@Column(name = "AVAILABLE_AFTER")
	private Date availableAfter;
	
	@Column(name = "LINKED_AGREEMENT")
	private String linkedAgreement;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "STORE_SIZE_METER_SQAURE")
	private String storeSizeMeterSquare;

	@Column(name = "AISLE")
	private String aisle;


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
