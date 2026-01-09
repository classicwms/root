package com.tekclover.wms.api.inbound.transaction.model.inbound.preinbound;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`
 */
@Table(
		name = "tblpreinboundheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_preinboundheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO"})
				}
		)
@IdClass(PreInboundHeaderCompositeKey.class)
public class PreInboundHeaderEntity { 
	
	@Id
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Id
	@Column(name = "C_ID")
	private String companyCode;
	
	@Id
	@Column(name = "PLANT_ID") 
	private String plantId;
	
	@Id
	@Column(name = "WH_ID") 
	private String warehouseId;
	
	@Id
	@Column(name = "PRE_IB_NO") 
	private String preInboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO")
	private String refDocNumber;
	
	@Column(name = "IB_ORD_TYP_ID")
	private Long inboundOrderTypeId;
	
	@Column(name = "REF_DOC_TYP")
	private String referenceDocumentType;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "CONT_NO")
	private String containerNo;
	
	@Column(name = "NO_CONTAINERS")
	private Long noOfContainers;
	
	@Column(name = "CONT_TYP") 
	private String containerType;
	
	@Column(name = "REF_DOC_DATE") 
	private Date refDocDate;
	
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
	
	@Column(name = "CTD_BY")
	private String createdBy;
	
	@Column(name = "CTD_ON")
	private Date createdOn = new Date();
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn = new Date();
}
