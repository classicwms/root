package com.tekclover.wms.api.transaction.model.outbound.packing;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `PACK_NO`, `ITM_CODE`
 */
@Table(
		name = "tblpackingline",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_key_packingline",
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", "OB_LINE_NO", "PACK_NO", "ITM_CODE"}
				)
		}
)
@IdClass(PackingLineCompositeKey.class)
public class PackingLine {

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(50)")
	private String languageId;

	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(50)")
	private String companyCodeId; // Changed from Long to String

	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(50)")
	private String plantId;

	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(50)")
	private String warehouseId;

	@Id
	@Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)")
	private String preOutboundNo;

	@Id
	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(50)")
	private String refDocNumber;

	@Id
	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(50)")
	private String partnerCode;

	@Id
	@Column(name = "OB_LINE_NO")
	private Long lineNumber;

	@Id
	@Column(name = "PACK_NO")
	private Long packingNo;

	@Id
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(50)")
	private String itemCode;

	@Column(name = "VAR_ID")
	private Long variantCode;

	@Column(name = "VAR_SUB_ID", columnDefinition = "nvarchar(50)")
	private String variantSubCode;

	@Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(50)")
	private String packCode;

	@Column(name = "STR_NO", columnDefinition = "nvarchar(50)")
	private String batchSerialNumber;

	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;

	@Column(name = "STATUS_ID")
	private Long statusId;

	@Column(name = "C_DESC", columnDefinition = "nvarchar(500)")
	private String companyDescription;

	@Column(name = "PLANT_DESC",columnDefinition = "nvarchar(500)")
	private String plantDescription;

	@Column(name = "WAREHOUSE_DESC",columnDefinition = "nvarchar(500)")
	private String warehouseDescription;

	@Column(name = "STATUS_DESC",columnDefinition = "nvarchar(500)")
	private String statusDescription;

	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;

	@Column(name = "SP_ST_IND_ID")
	private Long specialStockIndicatorId;

	@Column(name = "PROFORMA_INVOICE_NO")
	private Long proformaInvoiceNo = 0L;

	@Column(name = "ITEM_TEXT", columnDefinition = "nvarchar(50)")
	private String description;

	@Column(name = "PACK_MAT_NO", columnDefinition = "nvarchar(50)")
	private String packingMaterialNo;

	@Column(name = "PACK_QTY_ITM")
	private Double packQtyPerItem;

	@Column(name = "NO_PACKS")
	private Double numberOfPacks;

	@Column(name = "SHRINK_WRAP")
	private Boolean shrinkWrapReqd;

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

	@Column(name = "PACK_CNF_BY") // Fixed space issue in column name
	private String packConfirmedBy;

	@Column(name = "PACK_CNF_ON")
	private Date packConfirmedOn; // Removed initialization

	@Column(name = "PACK_UTD_BY")
	private String packUpdatedBy;

	@Column(name = "PACK_UTD_ON")
	private Date packUpdatedOn; // Removed initialization

	@Column(name = "PACK_REV_BY")
	private String packingReversedBy;

	@Column(name = "PACK_REV_ON")
	private Date packingReversedOn; // Removed initialization

	@Column(name = "PICK_CNF_QTY")
	private Double pickConfirmQty;

	@Column(name = "ALLOC_QTY")
	private Double allocatedQty;

	@Column(name = "PICK_UOM")
	private String pickUom;

	// 3PL
	@Column(name = "TPL_CBM")
	private Double threePLCbm;

	@Column(name = "TPL_UOM", columnDefinition = "nvarchar(50)")
	private String threePLUom;

	@Column(name = "TPL_BILL_STATUS", columnDefinition = "nvarchar(50)")
	private String threePLBillStatus;

	@Column(name = "TPL_LENGTH")
	private Double threePLLength;

	@Column(name = "TPL_WIDTH")
	private Double threePLHeight;

	@Column(name = "TPL_HEIGHT")
	private Double threePLWidth;

	@Column(name = "RATE")
	private Double rate;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "TOTAL_TPL_CBM")
	private Double totalThreePLCbm;

	@Column(name = "TOTAL_RATE")
	private Double totalRate;

	@Column(name = "PACKING_INVOICE")
	private Long packingInvoice =0L;
}
