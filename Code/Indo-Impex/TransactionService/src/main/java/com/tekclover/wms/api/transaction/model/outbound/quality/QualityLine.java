package com.tekclover.wms.api.transaction.model.outbound.quality;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `QC_NO`, `ITM_CODE`
 */
@Table(
		name = "tblqualityline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_qualityline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", 
								"OB_LINE_NO", "QC_NO", "ITM_CODE", "PARTNER_ITEM_BARCODE"})
				}
		)
@IdClass(QualityLineCompositeKey.class)
public class QualityLine { 
	
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
	
	@Id
	@Column(name = "OB_LINE_NO") 
	private Long lineNumber;
	
	@Id
	@Column(name = "QC_NO", columnDefinition = "nvarchar(50)")
	private String qualityInspectionNo;
	
	@Id
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;

	@Id
	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(100)")
	private String barcodeId;
	
	@Column(name = "PICK_HE_NO")
	private String actualHeNo;
	
	@Column(name = "PICK_PACK_BARCODE", columnDefinition = "nvarchar(50)")
	private String pickPackBarCode;
	
	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "ITEM_TEXT")
	private String description;
	
	@Column(name = "MFR_PART")
	private String manufacturerPartNo;
	
	@Column(name = "PACK_MT_NO")
	private String packingMaterialNo;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;
	
	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
	@Column(name = "QC_QTY")
	private Double qualityQty;	
	
	@Column(name = "PICK_CNF_QTY")
	private Double pickConfirmQty;
	
	@Column(name = "QC_UOM") 
	private String qualityConfirmUom;
	
	@Column(name = "REJ_QTY") 
	private String rejectQty;
	
	@Column(name = "REJ_UOM") 
	private String rejectUom;
	
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
	
	@Column(name = "QC_CTD_BY") 
	private String qualityCreatedBy;
	
	@Column(name = "QC_CTD_ON")
	private Date qualityCreatedOn = new Date();
	
	@Column(name = "QC_CNF_BY")
	private String qualityConfirmedBy;
	
	@Column(name = "QC_CNF_ON") 
	private Date qualityConfirmedOn = new Date();
	
	@Column(name = "QC_UTD_BY")
	private String qualityUpdatedBy;
	
	@Column(name = "QC_UTD_ON")
	private Date qualityUpdatedOn = new Date();
	
	@Column(name = "QC_REV_BY")
	private String qualityReversedBy;
	
	@Column(name = "QC_REV_ON")
	private Date qualityReversedOn = new Date();
}