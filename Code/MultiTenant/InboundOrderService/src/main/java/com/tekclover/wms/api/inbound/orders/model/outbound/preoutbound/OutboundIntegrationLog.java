package com.tekclover.wms.api.inbound.orders.model.outbound.preoutbound;

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
 * `LANG_ID`, `C_ID`, `WH_ID`, `WH_ID`, `INT_LOG_NO`, `REF_DOC_NO`
 */
@Table(
		name = "tbloutboundintegrationlog", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_outboundintegrationlog", 
						columnNames = {"LANG_ID", "C_ID", "WH_ID", "WH_ID", "INT_LOG_NO", "REF_DOC_NO"})
				}
		)
@IdClass(OutboundIntegrationLogCompositeKey.class)
public class OutboundIntegrationLog { 
	
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
	@Column(name = "INT_LOG_NO")
	private String integrationLogNumber;
	
	@Id
	@Column(name = "REF_DOC_NO") 
	private String refDocNumber;
	
	@Column(name = "SUCCESS_FILE_NM") 
	private String successFileName;
	
	@Column(name = "ERROR_FILE_NM") 
	private String errorFileName;
	
	@Column(name = "ORD_REC_DATE") 
	private Date orderReceiptDate = new Date();
	
	@Column(name = "INT_STATUS") 
	private String integrationStatus;
	
	@Column(name = "REMARK") 
	private String remarks;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
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
	private Date createdOn = new Date();
}
