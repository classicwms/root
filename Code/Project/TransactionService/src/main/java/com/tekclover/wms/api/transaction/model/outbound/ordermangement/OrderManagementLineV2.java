package com.tekclover.wms.api.transaction.model.outbound.ordermangement;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`, `PROP_ST_BIN`, `PROP_PACK_BARCODE`
 */
@Table(
		name = "tblordermangementline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_ordermangementline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", "PARTNER_CODE", 
								"OB_LINE_NO", "ITM_CODE", "PROP_ST_BIN", "PROP_PACK_BARCODE"})
				}
		)
@IdClass(OrderManagementLineCompositeKey.class)
public class OrderManagementLineV2 {

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
	@Column(name = "ITM_CODE")
	private String itemCode;
	
	@Id
	@Column(name = "PROP_ST_BIN")
	private String proposedStorageBin;
	
	@Id
	@Column(name = "PROP_PACK_BARCODE")
	private String proposedPackBarCode; //proposedPackCode
	
	@Column(name = "OB_ORD_TYP_ID")
	private Long outboundOrderTypeId;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "ITEM_TEXT")
	private String description;
	
	@Column(name = "ORD_QTY")
	private Double orderQty;
	
	@Column(name = "INV_QTY")
	private Double inventoryQty;
	
	@Column(name = "ALLOC_QTY")
	private Double allocatedQty;
	
	@Column(name = "REQ_DEL_DATE")
	private Date requiredDeliveryDate;
	
	@Column(name = "REF_FIELD_7")
	private String referenceField7;
	@Column(name = "IS_DELETED")
	private Long deletionIndicator;

	@Column(name = "PICK_UP_CTD_ON")
	private Date pickupCreatedOn;
	
}