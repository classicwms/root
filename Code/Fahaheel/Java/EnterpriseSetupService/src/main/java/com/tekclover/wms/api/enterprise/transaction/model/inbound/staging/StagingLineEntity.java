package com.tekclover.wms.api.enterprise.transaction.model.inbound.staging;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `STG_NO`, `PAL_CODE`, `CASE_CODE`, `IB_LINE_NO`, `ITM_CODE`
 */
@Table(
		name = "tblstagingline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_stagingline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO", "STG_NO", "PAL_CODE", "CASE_CODE", "IB_LINE_NO", "ITM_CODE"})
				}
		)
@IdClass(StagingLineCompositeKey.class)
public class StagingLineEntity { 
	
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
	
	@Id
	@Column(name = "STG_NO") 
	private String stagingNo;
	
	@Id
	@Column(name = "PAL_CODE") 
	private String palletCode;
	
	@Id
	@Column(name = "CASE_CODE") 
	private String caseCode;
	
	@Id
	@Column(name = "IB_LINE_NO")
	private Long lineNo;
	
	@Id
	@Column(name = "ITM_CODE") 
	private String itemCode;
	
	@Column(name = "IB_ORD_TYP_ID") 
	private Long inboundOrderTypeId;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID") 
	private String variantSubCode;
	
	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "ST_MTD") 
	private String storageMethod;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "PARTNER_CODE")	
	private String businessPartnerCode;
	
	@Column(name = "CONT_NO") 
	private String containerNo;
	
	@Column(name = "INV_NO") 
	private String invoiceNo;
	
	@Column(name = "ORD_QTY")	
	private Double orderQty;
	
	@Column(name = "ORD_UOM") 
	private String orderUom;
	
	@Column(name = "ITM_PAL_QTY")
	private Double itemQtyPerPallet;
	
	@Column(name = "ITM_CASE_QTY")
	private Double itemQtyPerCase;
	
	@Column(name = "ASS_USER_ID") 
	private String assignedUserId;
	
	@Column(name = "ITEM_TEXT") 
	private String itemDescription;
	
	@Column(name = "MFR_PART") 
	private String manufacturerPartNo;
	
	@Column(name = "HSN_CODE") 
	private String hsnCode;
	
	@Column(name = "VAR_TYP") 
	private String variantType;
	
	@Column(name = "SPEC_ACTUAL") 
	private String specificationActual;
	
	@Column(name = "ITM_BARCODE")
	private String itemBarcode;
	
	@Column(name = "REF_ORD_NO")
	private String referenceOrderNo;
	
	@Column(name = "REF_ORD_QTY")
	private Double referenceOrderQty;
	
	@Column(name = "CROSS_DOCK_ALLOC_QTY") 
	private Double crossDockAllocationQty;
	
	@Column(name = "REMARK") 
	private String remarks;
	
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
	
	@Column(name = "ST_CTD_BY")
	private String createdBy;
	
	@Column(name = "ST_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "ST_UTD_BY") 
	private String updatedBy;
	
	@Column(name = "ST_UTD_ON") 
	private Date updatedOn = new Date();
	
	@Column(name = "ST_CNF_BY")
	private String confirmedBy;
	
	@Column(name = "ST_CNF_ON") 
	private Date confirmedOn = new Date();
}