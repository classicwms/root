package com.tekclover.wms.api.transaction.model.outbound.pickup;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `PU_NO`, `ITM_CODE`, `PICK_HE_NO`, `PICK_ST_BIN`, `PICK_PACK_BARCODE`
 */
@Table(
		name = "tblpickupline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_pickupline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", "OB_LINE_NO", 
								"PU_NO", "ITM_CODE", "PICK_HE_NO", "PICK_ST_BIN", "PICK_PACK_BARCODE"})
				}
		)
@IdClass(PickupLineCompositeKey.class)
public class PickupLine { 
	
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
	
	@Id
	@Column(name = "OB_LINE_NO")
	private Long lineNumber;
	
	@Id
	@Column(name = "PU_NO")
	private String pickupNumber;
	
	@Id
	@Column(name = "ITM_CODE")
	private String itemCode;
	
	@Id
	@Column(name = "PICK_HE_NO")
	private String actualHeNo;
	
	@Id
	@Column(name = "PICK_ST_BIN") 
	private String pickedStorageBin;
	
	@Id
	@Column(name = "PICK_PACK_BARCODE")
	private String pickedPackCode;
	
	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;
	
	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
	@Column(name = "PICK_CNF_QTY") 
	private Double pickConfirmQty;
	
	@Column(name = "ALLOC_QTY") 
	private Double allocatedQty;
	
	@Column(name = "PICK_UOM")
	private String pickUom;
	
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "ITEM_TEXT")
	private String description;
	
	@Column(name = "MFR_PART") 
	private String manufacturerPartNo;
	
	@Column(name = "ASS_PICKER_ID")
	private String assignedPickerId;
	
	@Column(name = "PICK_PAL_CODE")
	private String pickPalletCode;
	
	@Column(name = "PICK_CASE_CODE") 
	private String pickCaseCode;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
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
	
	@Column(name = "PICK_CTD_BY") 
	private String pickupCreatedBy;	
	
	@Column(name = "PICK_CTD_ON") 
	private Date pickupCreatedOn = new Date();
	
	@Column(name = "PICK_CNF_BY")
	private String pickupConfirmedBy;
	
	@Column(name = "PICK_CNF_ON")
	private Date pickupConfirmedOn = new Date();
	
	@Column(name = "PICK_UTD_BY") 
	private String pickupUpdatedBy;
	
	@Column(name = "PICK_UTD_ON") 
	private Date pickupUpdatedOn = new Date();
	
	@Column(name = "PICK_REV_BY")
	private String pickupReversedBy;
	
	@Column(name = "PICK_REV_ON")
	private Date pickupReversedOn = new Date();
}
