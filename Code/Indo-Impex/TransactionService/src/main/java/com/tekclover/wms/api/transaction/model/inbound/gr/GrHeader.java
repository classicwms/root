package com.tekclover.wms.api.transaction.model.inbound.gr;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `STG_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`
 */
@Table(
		name = "tblgrheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_grheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO", "STG_NO", 
								"GR_NO", "PAL_CODE", "CASE_CODE"})
				}
		)
@IdClass(GrHeaderCompositeKey.class)
public class GrHeader { 
	
	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;
	
	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)") 
	private String companyCode;
	
	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
	private String warehouseId;
	
	@Id
	@Column(name = "PRE_IB_NO") 
	private String preInboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String refDocNumber;
	
	@Id
	@Column(name = "STG_NO")
	private String stagingNo;
	
	@Id
	@Column(name = "GR_NO") 
	private String goodsReceiptNo;
	
	@Id
	@Column(name = "PAL_CODE") 
	private String palletCode;
	
	@Id
	@Column(name = "CASE_CODE")
	private String caseCode;
	
	@Column(name = "IB_ORD_TYP_ID")
	private Long inboundOrderTypeId;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "GR_MTD")
	private String grMethod;
	
	@Column(name = "CONT_REC_NO") 
	private String containerReceiptNo;
	
	@Column(name = "DOCK_ALL_NO") 
	private String dockAllocationNo;
	
	@Column(name = "CONT_NO") 
	private String containerNo;
	
	@Column(name = "VEH_NO") 
	private String vechicleNo;
	
	@Column(name = "EA_DATE") 
	private Date expectedArrivalDate;
	
	@Column(name = "GR_DATE") 
	private Date goodsReceiptDate;
	
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
	
	@Column(name = "GR_CTD_BY")
	private String createdBy;
	
	@Column(name = "GR_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "GR_UTD_BY") 
	private String updatedBy;
	
	@Column(name = "GR_UTD_ON")
	private Date updatedOn = new Date();
	
	@Column(name = "GR_CNF_BY")
	private String confirmedBy;
	
	@Column(name = "GR_CNF_ON") 
	private Date confirmedOn = new Date();
}