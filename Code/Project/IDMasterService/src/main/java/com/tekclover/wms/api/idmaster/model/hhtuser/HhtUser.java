package com.tekclover.wms.api.idmaster.model.hhtuser;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `WH_ID`, `USR_ID`
 */
@Table(
		name = "tblhhtuser", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_hhtuser", 
						columnNames = {"WH_ID", "USR_ID"})
				}
		)
@IdClass(HhtUserCompositeKey.class)
public class HhtUser { 
	
	
	@Column(name = "LANG_ID") 
	private String languageId;
	
	@Column(name = "C_ID") 
	private String companyCodeId;
	
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID") 
	private String warehouseId;
	
	@Id
	@Column(name = "USR_ID")
	private String userId;
	
	@Column(name = "PASSWORD") 
	private String password;
	
	@Column(name = "USER_NM") 
	private String userName;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "CASE_RECEIPT")
	private Boolean caseReceipts;
	
	@Column(name = "ITEM_RECEIPT") 
	private Boolean itemReceipts;
	
	@Column(name = "PUTAWAY") 
	private Boolean putaway;
	
	@Column(name = "TRANSFER")
	private Boolean transfer;
	
	@Column(name = "PICKING")
	private Boolean picking;
	
	@Column(name = "QUALITY") 
	private Boolean quality;
	
	@Column(name = "INVENTORY") 
	private Boolean inventory;
	
	@Column(name = "CUSTOMER_RET")
	private Boolean customerReturn;
	
	@Column(name = "SUPPLIER_RET") 
	private Boolean supplierReturn;
	
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
