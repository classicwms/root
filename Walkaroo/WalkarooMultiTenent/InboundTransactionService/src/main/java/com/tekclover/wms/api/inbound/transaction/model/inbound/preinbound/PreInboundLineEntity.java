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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `IB_LINE_NO`, `ITM_CODE`
 */
@Table(
		name = "tblpreinboundline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_preinboundline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO", "IB_LINE_NO", "ITM_CODE"})
				}
		)
@IdClass(PreInboundLineCompositeKey.class)
public class PreInboundLineEntity { 
	
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
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "ITEM_TEXT") 
	private String itemDescription;
	
	@Column(name = "CONT_NO")
	private String containerNo;
	
	@Column(name = "INV_NO")
	private String invoiceNo;
	
	@Column(name = "PARTNER_CODE") 
	private String businessPartnerCode;
	
	@Column(name = "PARTNER_ITM_CODE")
	private String partnerItemNo;
	
	@Column(name = "BRND_NM") 
	private String brandName;
	
	@Column(name = "MFR_PART")
	private String manufacturerPartNo;
	
	@Column(name = "HSN_CODE")
	private String hsnCode;
	
	@Column(name = "EA_DATE")
	private Date expectedArrivalDate;
	
	@Column(name = "ORD_QTY")
	private Double orderQty;
	
	@Column(name = "ORD_UOM")
	private String orderUom;
	
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID")
	private Long specialStockIndicatorId;
	
	@Column(name = "PAL_QTY")
	private String numberOfPallets;
	
	@Column(name = "CASE_NO") 
	private String numberOfCases;
	
	@Column(name = "ITM_PAL_QTY") 
	private Double itemPerPalletQty;
	
	@Column(name = "ITM_CASE_QTY")
	private Double itemCaseQty; // PACK_QTY in AX_API
	
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
	
	@Column(name = "REF_FIELD_11") 
	private String referenceField11;
	
	@Column(name = "REF_FIELD_12")
	private String referenceField12;
	
	@Column(name = "REF_FIELD_13")
	private String referenceField13;
	
	@Column(name = "REF_FIELD_14")
	private String referenceField14;
	
	@Column(name = "REF_FIELD_15") 
	private String referenceField15;
	
	@Column(name = "REF_FIELD_16") 
	private String referenceField16;
	
	@Column(name = "REF_FIELD_17") 
	private String referenceField17;
	
	@Column(name = "REF_FIELD_18") 
	private String referenceField18;
	
	@Column(name = "REF_FIELD_19") 
	private String referenceField19;
	
	@Column(name = "REF_FIELD_20") 
	private String referenceField20;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "CTD_BY")
	private String createdBy;
	
	@Column(name = "CTD_ON")
	private Date createdOn;
	
	@Column(name = "UTD_BY")
	private String updatedBy;
	
	@Column(name = "UTD_ON")
	private Date updatedOn;
}
