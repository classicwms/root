//package com.tekclover.wms.api.transaction.model.outbound.packing;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.IdClass;
//import javax.persistence.Table;
//import javax.persistence.UniqueConstraint;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
///*
// * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `QC_NO`, `PACK_NO`
// */
//@Table(
//		name = "tblpackingheader", 
//		uniqueConstraints = { 
//				@UniqueConstraint (
//						name = "unique_key_packingheader", 
//						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", "QC_NO", "PACK_NO"})
//				}
//		)
//@IdClass(PackingHeaderCompositeKey.class)
//public class PackingHeader { 
//	
//	@Id
//	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
//	private String languageId;
//	
//	@Id
//	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
//	private String companyCodeId;
//	
//	@Id
//	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
//	private String plantId;
//	
//	@Id
//	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)") 
//	private String warehouseId;
//	
//	@Id
//	@Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)")
//	private String preOutboundNo;
//	
//	@Id
//	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
//	private String refDocNumber;
//	
//	@Id
//	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
//	private String partnerCode;
//	
//	@Id
//	@Column(name = "QC_NO", columnDefinition = "nvarchar(50)")
//	private String qualityInspectionNo;
//	
//	@Id
//	@Column(name = "PACK_NO")
//	private String packingNo;
//	
//	@Column(name = "OB_ORD_TYP_ID") 
//	private Long outboundOrderTypeId;
//	
//	@Column(name = "QC_QTY") 
//	private String qualityConfirmQty;
//	
//	@Column(name = "QC_UOM")
//	private String qualityConfirmUom;
//	
//	@Column(name = "STATUS_ID") 
//	private Long statusId;
//	
//	@Column(name = "REF_FIELD_1") 
//	private String referenceField1;
//	
//	@Column(name = "REF_FIELD_2") 
//	private String referenceField2;
//	
//	@Column(name = "REF_FIELD_3") 
//	private String referenceField3;
//	
//	@Column(name = "REF_FIELD_4") 
//	private String referenceField4;
//	
//	@Column(name = "REF_FIELD_5")
//	private String referenceField5;
//	
//	@Column(name = "REF_FIELD_6")
//	private String referenceField6;
//	
//	@Column(name = "REF_FIELD_7") 
//	private String referenceField7;
//	
//	@Column(name = "REF_FIELD_8") 
//	private String referenceField8;
//	
//	@Column(name = "REF_FIELD_9") 
//	private String referenceField9;
//	
//	@Column(name = "REF_FIELD_10") 
//	private String referenceField10;
//	
//	@Column(name = "IS_DELETED") 
//	private Long deletionIndicator;
//	
//	@Column(name = "REMARK") 
//	private String remarks;
//	
//	@Column(name = "PACK _CNF_BY")
//	private String packConfirmedBy;
//	
//	@Column(name = "PACK_CNF_ON") 
//	private Date packConfirmedOn = new Date();
//	
//	@Column(name = "PACK_UTD_BY") 
//	private String packUpdatedBy;
//	
//	@Column(name = "PACK_UTD_ON")
//	private Date packUpdatedOn = new Date();
//	
//	@Column(name = "PACK_REV_BY") 
//	private String packingReversedBy;
//	
//	@Column(name = "PACK_REV_ON") 
//	private Date packingReversedOn = new Date();
//}