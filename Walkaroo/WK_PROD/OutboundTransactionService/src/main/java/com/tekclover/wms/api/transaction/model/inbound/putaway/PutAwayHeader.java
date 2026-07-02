package com.tekclover.wms.api.transaction.model.inbound.putaway;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@AllArgsConstructor
@NoArgsConstructor
/*
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `PRE_IB_NO`, `REF_DOC_NO`, `GR_NO`, `PAL_CODE`, `CASE_CODE`, `PACK_BARCODE`, `PA_NO`, `PROP_ST_BIN`
 */
@Table(
		name = "tblputawayheader", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_putawayheader", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PRE_IB_NO", "REF_DOC_NO", "GR_NO", "PAL_CODE", 
								"CASE_CODE", "PACK_BARCODE", "PA_NO", "PROP_ST_BIN"})
				}
		)
@IdClass(PutAwayHeaderCompositeKey.class)
public class PutAwayHeader { 
	
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
	@Column(name = "PRE_IB_NO")
	private String preInboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO") 
	private String refDocNumber;
	
	@Id
	@Column(name = "GR_NO") 
	private String goodsReceiptNo;
	
	@Column(name = "IB_ORD_TYP_ID") 
	private Long inboundOrderTypeId;
	
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
	@Column(name = "PA_NO") 
	private String putAwayNumber;
	
	@Id
	@Column(name = "PROP_ST_BIN") 
	private String proposedStorageBin;
	
	@Column(name = "PA_QTY") 
	private Double putAwayQuantity;
	
	@Column(name = "PA_UOM")
	private String putAwayUom;
	
	@Column(name = "STR_TYP_ID") 
	private Long strategyTypeId;
	
	@Column(name = "ST_NO")
	private String strategyNo;
	
	@Column(name = "PROP_HE_NO") 
	private String proposedHandlingEquipment;
	
	@Column(name = "ASS_USER_ID") 
	private String assignedUserId;
	
	@Column(name = "STATUS_ID")
	private Long statusId;
	
	@Column(name = "QTY_TYPE") 
	private String quantityType;
	
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
	
	@Column(name = "PA_CTD_BY") 
	private String createdBy;
	
	@Column(name = "PA_CTD_ON") 
	private Date createdOn = new Date();
	
	@Column(name = "PA_UTD_BY") 
	private String updatedBy;
	
	@Column(name = "PA_UTD_ON") 
	private Date updatedOn = new Date();
	
	@Column(name = "PA_CNF_BY") 
	private String confirmedBy;
	
	@Column(name = "PA_CNF_ON") 
	private Date confirmedOn = new Date();
}
