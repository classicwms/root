package com.tekclover.wms.api.inbound.transaction.model.preoutbound.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`
 */
@Table(
		name = "tbloutboundheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_outboundheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE"})
				}
		)
@IdClass(OutboundHeaderCompositeKey.class)
public class OutboundHeader { 
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Id
	@Column(name = "C_ID") 
	private String companyCodeId;
	
	@Id
	@Column(name = "PLANT_ID") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID")
	private String warehouseId;
	
	@Id
	@Column(name = "PRE_OB_NO")
	private String preOutboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO")
	private String refDocNumber;
	
	@Id
	@Column(name = "PARTNER_CODE")
	private String partnerCode;
	
	@Column(name = "DLV_ORD_NO")
	private String deliveryOrderNo;
	
	@Column(name = "REF_DOC_TYP")
	private String referenceDocumentType;
	
	@Column(name = "OB_ORD_TYP_ID") 
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "REF_DOC_DATE")
	private Date refDocDate = new Date();
	
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
	
	@Column(name = "DLV_CTD_BY") 
	private String createdBy;
	
	@Column(name = "DLV_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "DLV_CNF_BY") 
	private String deliveryConfirmedBy;
	
	@Column(name = "DLV_CNF_ON")
	private Date deliveryConfirmedOn;
	
	@Column(name = "DLV_UTD_BY")
	private String updatedBy;
	
	@Column(name = "DLV_UTD_ON")
	private Date updatedOn;
	
	@Column(name = "DLV_REV_BY") 
	private String reversedBy;
	
	@Column(name = "DLV_REV_ON") 
	private Date reversedOn;
}
