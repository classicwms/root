package com.tekclover.wms.api.inbound.transaction.model.inbound.putaway;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `GR_NO`, `PRE_IB_NO`, `REF_DOC_NO`, `PA_NO`, `IB_LINE_NO`, `ITM_CODE`, `PROP_ST_BIN`, `CNF_ST_BIN`
 */
@Table(
		name = "tblputawayline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_putawayline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "GR_NO", "PRE_IB_NO", "REF_DOC_NO", "PA_NO", "IB_LINE_NO", "ITM_CODE", "PROP_ST_BIN", "CNF_ST_BIN"})
				}
		)
@IdClass(PutAwayLineCompositeKey.class)
public class PutAwayLine { 
	
	@Id
	@Column(name = "LANG_ID")
	private String languageId;
	
	@Id
	@Column(name = "C_ID") 
	private String companyCode;
	
	@Id
	@Column(name = "PLANT_ID")
	private String plantId;
	
	@Id
	@Column(name = "WH_ID") 
	private String warehouseId;
	
	@Id
	@Column(name = "GR_NO") 
	private String goodsReceiptNo;
	
	@Id
	@Column(name = "PRE_IB_NO") 
	private String preInboundNo;
	
	@Id
	@Column(name = "REF_DOC_NO")
	private String refDocNumber;
	
	@Id
	@Column(name = "PA_NO") 
	private String putAwayNumber;
	
	@Id
	@Column(name = "IB_LINE_NO")
	private Long lineNo;
	
	@Id
	@Column(name = "ITM_CODE")
	private String itemCode;
	
	@Id
	@Column(name = "PROP_ST_BIN") 
	private String proposedStorageBin;
	
	@Id
	@Column(name = "CNF_ST_BIN")
	private String confirmedStorageBin;
	
	@Column(name = "PACK_BARCODE")
	private String packBarcodes;
	
	@Column(name = "PA_QTY") 
	private Double putAwayQuantity;
	
	@Column(name = "PA_UOM") 
	private String putAwayUom;
	
	@Column(name = "PA_CNF_QTY") 
	private Double putawayConfirmedQty;
	
	@Column(name = "VAR_ID")
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID")
	private String variantSubCode;
	
	@Column(name = "ST_MTD") 
	private String storageMethod;
	
	@Column(name = "STR_NO")
	private String batchSerialNumber;
	
	@Column(name = "IB_ORD_TYP_ID") 
	private Long inboundOrderTypeId;
	
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "REF_ORD_NO") 
	private String referenceOrderNo;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "TEXT") 
	private String description;
	
	@Column(name = "SPEC_ACTUAL") 
	private String specificationActual;
	
	@Column(name = "VEN_CODE") 
	private String vendorCode;
	
	@Column(name = "MFR_PART") 
	private String manufacturerPartNo;
	
	@Column(name = "HSN_CODE") 
	private String hsnCode;
	
	@Column(name = "ITM_BARCODE") 
	private String itemBarcode;
	
	@Column(name = "MFR_DATE") 
	private Date manufacturerDate;
	
	@Column(name = "EXP_DATE") 
	private Date expiryDate;
	
	@Column(name = "STR_QTY")
	private Double storageQty;
	
	@Column(name = "ST_TEMP") 
	private String storageTemperature;
	
	@Column(name = "ST_UOM")
	private String storageUom;
	
	@Column(name = "QTY_TYPE") 
	private String quantityType;
	
	@Column(name = "PROP_HE_NO") 
	private String proposedHandlingEquipment;
	
	@Column(name = "ASS_USER_ID") 
	private String assignedUserId;
	
	@Column(name = "WRK_CTR_ID") 
	private Long workCenterId;
	
	@Column(name = "PAWAY_HE_NO") 
	private String putAwayHandlingEquipment;
	
	@Column(name = "PAWAY_EMP_ID")
	private String putAwayEmployeeId;
	
	@Column(name = "CREATE_REMARK") 
	private String createRemarks;
	
	@Column(name = "CNF_REMARK")	
	private String cnfRemarks;
	
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
	
	@Column(name = "PA_CNF_BY") 
	private String confirmedBy;
	
	@Column(name = "PA_CNF_ON")
	private Date confirmedOn = new Date();
	
	@Column(name = "PA_UTD_BY") 
	private String updatedBy;
	
	@Column(name = "PA_UTD_ON") 
	private Date updatedOn = new Date();

	@Column(name= "USER_ID")
	private String userId;
}
