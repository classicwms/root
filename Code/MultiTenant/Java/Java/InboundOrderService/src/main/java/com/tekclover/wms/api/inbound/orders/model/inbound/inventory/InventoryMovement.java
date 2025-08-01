package com.tekclover.wms.api.inbound.orders.model.inbound.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `MVT_TYP_ID`, `SUB_MVT_TYP_ID`, `PAL_CODE`, `CASE_CODE`, `PACK_BARCODE`, `ITM_CODE`, `VAR_ID`, `VAR_SUB_ID`, `STR_NO`, `MVT_DOC_NO`
 */
@Table(
		name = "tblinventorymovement",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_key_inventorymovement",
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "MVT_TYP_ID", "SUB_MVT_TYP_ID",
								"PACK_BARCODE", "ITM_CODE", "STR_NO", "MVT_DOC_NO"})
		}
)
@IdClass(InventoryMovementCompositeKey.class)
public class InventoryMovement {

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
	@Column(name = "MVT_TYP_ID")
	private Long movementType;

	@Id
	@Column(name = "SUB_MVT_TYP_ID")
	private Long submovementType;

	@Id
	@Column(name = "PAL_CODE")
	private String palletCode;

	@Id
	@Column(name = "CASE_CODE")
	private String caseCode;

	@Id
	@Column(name = "PACK_BARCODE")
	private String packBarcodes;

	@Id
	@Column(name = "ITM_CODE")
	private String itemCode;

	@Id
	@Column(name = "VAR_ID")
	private Long variantCode;

	@Id
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;

	@Id
	@Column(name = "STR_NO")
	private String batchSerialNumber;

	@Id
	@Column(name = "MVT_DOC_NO")
	private String movementDocumentNo;

	@Column(name = "MFR_PART")
	private String manufacturerName;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "ST_BIN")
	private String storageBin;

	@Column(name = "STR_MTD")
	private String storageMethod;

	@Column(name = "TEXT")
	private String description;

	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;

	@Column(name = "SP_ST_IND_ID")
	private Long specialStockIndicator;

	@Column(name = "MVT_QTY_VAL")
	private String movementQtyValue;

	@Column(name = "MVT_QTY")
	private Double movementQty;

	@Column(name = "BAL_OH_QTY")
	private Double balanceOHQty;

	@Column(name = "MVT_UOM")
	private String inventoryUom;

	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)")
	private String refDocNumber;

	@Column(name = "REF_NO")
	private String referenceNumber;

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

	@Column(name = "IM_CTD_BY")
	private String createdBy;

	@Column(name = "IM_CTD_ON")
	private Date createdOn = new Date();

	@Column(name = "C_TEXT")
	private String companyDescription;

	@Column(name = "PLANT_TEXT")
	private String plantDescription;

	@Column(name = "WH_TEXT")
	private String warehouseDescription;

	@Column(name = "BARCODE_ID")
	private String barcodeId;
}