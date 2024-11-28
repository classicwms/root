package com.tekclover.wms.api.transaction.model.packing;//package com.tekclover.wms.api.transaction.model.outbound.packing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
				@UniqueConstraint (
						name = "unique_key_packingline",
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", "OB_LINE_NO", "PACK_NO", "ITM_CODE"})
				}
		)
@IdClass(PackingLineCompositeKey.class)
public class PackingLine {

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(10)")
	private String languageId;

	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(50)")
	private Long companyCodeId;

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

	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;

	@Column(name = "SP_ST_IND_ID")
	private Long specialStockIndicatorId;

	@Column(name = "ITEM_TEXT", columnDefinition = "nvarchar(200)")
	private String description;

	@Column(name = "PACK_MAT_NO", columnDefinition = "nvarchar(100)")
	private String packingMaterialNo;

	@Column(name = "PACK_QTY_ITM")
	private Double packQtyPerItem;

	@Column(name = "NO_PACKS")
	private Double numberOfPacks;

	@Column(name = "SHRINK_WRAP")
	private Boolean shrinkWrapReqd;

	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "REF_FIELD_1", columnDefinition = "nvarchar(200)")
	private String referenceField1;

	@Column(name = "REF_FIELD_2", columnDefinition = "nvarchar(200)")
	private String referenceField2;

	@Column(name = "REF_FIELD_3", columnDefinition = "nvarchar(200)")
	private String referenceField3;

	@Column(name = "REF_FIELD_4", columnDefinition = "nvarchar(200)")
	private String referenceField4;

	@Column(name = "REF_FIELD_5", columnDefinition = "nvarchar(200)")
	private String referenceField5;

	@Column(name = "REF_FIELD_6", columnDefinition = "nvarchar(200)")
	private String referenceField6;

	@Column(name = "REF_FIELD_7", columnDefinition = "nvarchar(200)")
	private String referenceField7;

	@Column(name = "REF_FIELD_8", columnDefinition = "nvarchar(200)")
	private String referenceField8;

	@Column(name = "REF_FIELD_9", columnDefinition = "nvarchar(200)")
	private String referenceField9;

	@Column(name = "REF_FIELD_10", columnDefinition = "nvarchar(200)")
	private String referenceField10;

	@Column(name = "PACK_CNF_BY", columnDefinition = "nvarchar(50)")
	private String packConfirmedBy;

	@Column(name = "PACK_CNF_ON")
	private Date packConfirmedOn = new Date();

	@Column(name = "PACK_UTD_BY", columnDefinition = "nvarchar(50)")
	private String packUpdatedBy;

	@Column(name = "PACK_UTD_ON")
	private Date packUpdatedOn = new Date();

	@Column(name = "PACK_REV_BY", columnDefinition = "nvarchar(50)")
	private String packingReversedBy;

	@Column(name = "PACK_REV_ON")
	private Date packingReversedOn = new Date();
}
