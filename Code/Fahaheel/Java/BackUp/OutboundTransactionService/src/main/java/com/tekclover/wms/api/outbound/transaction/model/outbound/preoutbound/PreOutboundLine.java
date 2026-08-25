package com.tekclover.wms.api.outbound.transaction.model.outbound.preoutbound;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `REF_DOC_NO`, `PRE_OB_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`
 */
@Table(
		name = "tblpreoutboundline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_preoutboundline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "REF_DOC_NO", "PRE_OB_NO", "PARTNER_CODE", 
								"OB_LINE_NO", "ITM_CODE"})
				}
		)
@IdClass(PreOutboundLineCompositeKey.class)
public class PreOutboundLine { 
	
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
	@Column(name = "REF_DOC_NO", columnDefinition = "nvarchar(100)") 
	private String refDocNumber;
	
	@Id
	@Column(name = "PRE_OB_NO", columnDefinition = "nvarchar(50)") 
	private String preOutboundNo;
	
	@Id
	@Column(name = "PARTNER_CODE", columnDefinition = "nvarchar(100)")
	private String partnerCode;
	
	@Id
	@Column(name = "OB_LINE_NO")
	private Long lineNumber;
	
	@Id
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(255)")
	private String itemCode;

//	@Id
	@Column(name = "PARTNER_ITEM_BARCODE", columnDefinition = "nvarchar(100)")
	private String barcodeId;
	
	@Column(name = "OB_ORD_TYP_ID") 
	private Long outboundOrderTypeId;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "TEXT") 
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
	
	@Column(name = "REQ_DEL_DATE")
	private Date requiredDeliveryDate;
	
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
	
	@Column(name = "PRE_OB_CTD_BY") 
	private String createdBy;
	
	@Column(name = "PRE_OB_CTD_ON")
	private Date createdOn = new Date();
	
	@Column(name = "PRE_OB_UTD_BY") 
	private String updatedBy;
	
	@Column(name = "PRE_OB_UTD_ON") 
	private Date updatedOn = new Date();
}