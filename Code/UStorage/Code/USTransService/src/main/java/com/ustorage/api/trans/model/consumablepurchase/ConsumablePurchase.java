package com.ustorage.api.trans.model.consumablepurchase;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblconsumablepurchase",
		uniqueConstraints = {
				@UniqueConstraint (
						name = "unique_key_consumablepurchase",
						columnNames = { "ITEM_CODE" , "RECEIPT_NO"})
		})
@IdClass(ConsumablePurchaseCompositeKey.class)
public class ConsumablePurchase {

	@Id
	@Column(name = "ITEM_CODE")
	private String itemCode;

	@Id
	@Column(name = "RECEIPT_NO")
	private String receiptNo;
	
	@Column(name = "RECEIPT_DATE")
	private String receiptDate;
		
	@Column(name = "VENDOR")
	private String vendor;
	
	@Column(name = "QUANTITY")
	private String quantity;
	
	@Column(name = "WAREHOUSE")
	private String warehouse;
	
	@Column(name = "VALUE")
	private String value;

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
