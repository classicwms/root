package com.tekclover.wms.api.transaction.model.outbound;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_OB_NO`, `REF_DOC_NO`, `PARTNER_CODE`, `OB_LINE_NO`, `ITM_CODE`
 */
@Table(
		name = "tbloutboundline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_outboundline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_OB_NO", "REF_DOC_NO", 
								"PARTNER_CODE", "OB_LINE_NO", "ITM_CODE"})
				}
		)
@IdClass(OutboundLineCompositeKey.class)
public class OutboundLine { 
	
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
	
	@Column(name = "DLV_ORD_NO")	
	private String deliveryOrderNo;
	
	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
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
	
	@Column(name = "ORD_QTY")
	private Double orderQty;
	
	@Column(name = "ORD_UOM") 
	private String orderUom;
	
	@Column(name = "DLV_QTY") 
	private Double deliveryQty;
	
	@Column(name = "DLV_UOM")
	private String deliveryUom;
	
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
	
	@Column(name = "DLV_CTD_BY")
	private String createdBy;
	
	@Column(name = "DLV_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "DLV_CNF_BY")
	private String deliveryConfirmedBy;
	
	@Column(name = "DLV_CNF_ON")
	private Date deliveryConfirmedOn;
	
	@Column(name = "DLV_UTD_BY")
	private String updatedBy;
	
	@Column(name = "DLV_UTD_ON")
	private Date updatedOn;
	
	@Column(name = "DLV_REV_BY")
	private String reversedBy;
	
	@Column(name = "DLV_REV_ON") 
	private Date reversedOn;

	@Transient
	private String itemText;

	@Transient
	private String mfrPartNumber;

	@Version
	private Long versionId = 0L;
}
