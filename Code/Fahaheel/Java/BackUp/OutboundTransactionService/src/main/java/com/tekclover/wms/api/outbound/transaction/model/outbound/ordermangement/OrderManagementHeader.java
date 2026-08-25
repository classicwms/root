package com.tekclover.wms.api.outbound.transaction.model.outbound.ordermangement;

import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`
 */
@Table(
		name = "tblordermangementheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_ordermangementheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE"})
				}
		)
@IdClass(OrderManagementHeaderCompositeKey.class)
public class OrderManagementHeader { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)") 
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
	private String warehouseId;
	
	@Id
	@Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)")
	private String preOutboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)") 
	private String refDocNumber;
	
	@Id
	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
	private String partnerCode;
	
	@Column(name = "REF_DOC_TYP") 
	private String referenceDocumentType;
	
	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "ORD_REC_DATE")
	private Date orderReceiptDate = new Date();
	
	@Column(name = "REQ_DEL_DATE")
	private Date requiredDeliveryDate;
	
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
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "REMARK")
	private String remarks;
	
	@Column(name = "PICK_UP_CTD_BY") 
	private String pickupCreatedBy;
	
	@Column(name = "PICK_UP_CTD_ON")
	private Date pickupCreatedOn = new Date();
	
	@Column(name = "PICK_UP_UTD_BY")
	private String pickupUpdatedBy;
	
	@Column(name = "PICK_UP_UTD_ON") 
	private Date pickupupdatedOn = new Date();
	
	@Column(name = "RE_ALLOC_BY") 
	private String reAllocatedBy;
	
	@Column(name = "RE_ALLOC_ON") 
	private Date reAllocatedOn = new Date();
	
	@Column(name = "PICKER_ASSIGN_BY")	
	private String pickerAssignedBy;
	
	@Column(name = "PICKER_ASSIGN_ON")
	private Date pickerAssignedOn = new Date();
}