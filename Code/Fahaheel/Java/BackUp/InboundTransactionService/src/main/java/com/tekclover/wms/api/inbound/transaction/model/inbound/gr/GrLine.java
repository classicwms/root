package com.tekclover.wms.api.inbound.transaction.model.inbound.gr;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`, `PACK_BARCODE`, `IB_LINE_NO`, `ITM_CODE`
 */
@Table(
		name = "tblgrline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_grline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO", "GR_NO", "PAL_CODE", "CASE_CODE", "PACK_BARCODE", "IB_LINE_NO", "ITM_CODE", "GR_CTD_ON"})
				}
		)
@IdClass(GrLineCompositeKey.class)
public class GrLine { 
	
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
	@Column(name = "GR_NO") 
	private String goodsReceiptNo;
	
	@Id
	@Column(name = "PAL_CODE") 
	private String palletCode;
	
	@Id
	@Column(name = "CASE_CODE") 
	private String caseCode;
	
	@Id
	@Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
	private String packBarcodes;
	
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
	
	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
	private String businessPartnerCode;
	
	@Column(name = "CONT_NO") 
	private String containerNo;
	
	@Column(name = "INV_NO") 
	private String invoiceNo;
	
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
	
	@Column(name = "ORD_QTY") 
	private Double orderQty;
	
	@Column(name = "ORD_UOM")
	private String orderUom;
	
	@Column(name = "GR_QTY") 
	private Double goodReceiptQty;
	
	@Column(name = "GR_UOM") 
	private String grUom;
	
	@Column(name = "ACCEPT_QTY") 
	private Double acceptedQty;
	
	@Column(name = "DAMAGE_QTY") 
	private Double damageQty;
	
	@Column(name = "QTY_TYPE") 
	private String quantityType;
	
	@Column(name = "ASS_USER_ID") 
	private String assignedUserId;
	
	@Column(name = "PAWAY_HE_NO") 
	private String putAwayHandlingEquipment;
	
	@Column(name = "PA_CNF_QTY") 
	private Double confirmedQty;
	
	@Column(name = "REM_QTY")
	private Double remainingQty;
	
	@Column(name = "REF_ORD_NO")
	private String referenceOrderNo;
	
	@Column(name = "REF_ORD_QTY") 
	private Double referenceOrderQty;
	
	@Column(name = "CROSS_DOCK_ALLOC_QTY")
	private Double crossDockAllocationQty;
	
	@Column(name = "MFR_DATE")
	private Date manufacturerDate;
	
	@Column(name = "EXP_DATE") 
	private Date expiryDate;
	
	@Column(name = "STR_QTY")
	private Double storageQty;
	
	@Column(name = "REMARK")
	private String remark;
	
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
	
	@Column(name = "GR_CTD_BY") 
	private String createdBy;
	@Id
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