package com.tekclover.wms.api.inbound.orders.model.outbound.ordermangement;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`, `PROP_ST_BIN`, `PROP_PACK_BARCODE`
 */
@Table(
		name = "tblordermangementline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_ordermangementline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", 
								"OB_LINE_NO", "ITM_CODE", "PROP_ST_BIN", "PROP_PACK_BARCODE", "PARTNER_ITEM_BARCODE"})
				}
		)
@IdClass(OrderManagementLineCompositeKey.class)
public class OrderManagementLine { 
	
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
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;
	
	@Id
	@Column(name = "PROP_ST_BIN", columnDefinition = "nvarchar(100)") 
	private String proposedStorageBin;
	
	@Id
	@Column(name = "PROP_PACK_BARCODE", columnDefinition = "nvarchar(50)")
	private String proposedPackBarCode; //proposedPackCode
	
    @Id
    @Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(100)")
    private String barcodeId;
	
	@Column(name = "PU_NO", columnDefinition = "nvarchar(50)")
	private String pickupNumber;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;
	
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
	
	@Column(name = "HSN_CODE") 
	private String hsnCode;
	
	@Column(name = "ITM_BARCODE") 
	private String itemBarcode;
	
	@Column(name = "ORD_QTY") 
	private Double orderQty;
	
	@Column(name = "ORD_UOM")
	private String orderUom;
	
	@Column(name = "INV_QTY") 
	private Double inventoryQty;
	
	@Column(name = "ALLOC_QTY") 
	private Double allocatedQty;
	
	@Column(name = "RE_ALLOC_QTY")
	private Double reAllocatedQty;
	
	@Column(name = "STR_TYP_ID") 
	private Long strategyTypeId;
	
	@Column(name = "ST_NO")
	private String strategyNo;
	
	@Column(name = "REQ_DEL_DATE")
	private Date requiredDeliveryDate;
	
	@Column(name = "PROP_STR_NO")
	private String proposedBatchSerialNumber;
	
	@Column(name = "PROP_PAL_CODE") 
	private String proposedPalletCode;
	
	@Column(name = "PROP_CASE_CODE")
	private String proposedCaseCode;
	
	@Column(name = "PROP_HE_NO") 
	private String proposedHeNo;
	
	@Column(name = "PROP_PICKER_ID")
	private String proposedPicker;
	
	@Column(name = "ASS_PICKER_ID")
	private String assignedPickerId;
	
	@Column(name = "REASS_PICKER_ID")
	private String reassignedPickerId;
	
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
	
	@Column(name = "RE_ALLOC_BY")
	private String reAllocatedBy;
	
	@Column(name = "RE_ALLOC_ON")	
	private Date reAllocatedOn = new Date();
	
	@Column(name = "PICK_UP_CTD_BY")
	private String pickupCreatedBy;
	
	@Column(name = "PICK_UP_CTD_ON") 
	private Date pickupCreatedOn = new Date();
	
	@Column(name = "PICK_UP_UTD_BY")	
	private String pickupUpdatedBy;
	
	@Column(name = "PICK_UP_UTD_ON")
	private Date pickupUpdatedOn = new Date();
	
	@Column(name = "PICKER_ASSIGN_BY")
	private String pickerAssignedBy;
	
	@Column(name = "PICKER_ASSIGN_ON") 
	private Date pickerAssignedOn = new Date();
	
	@Column(name = "PICKER_REASSIGN_BY") 
	private String pickerReassignedBy;
	
	@Column(name = "PICKER_REASSIGN_ON") 
	private Date pickerReassignedOn = new Date();
}