package com.tekclover.wms.api.inbound.orders.model.trans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
		name = "tblinventorytrans", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_inventory", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "PACK_BARCODE", "ITM_CODE", "ST_BIN", "SP_ST_IND_ID"})
				}
		)
@IdClass(InventoryTransCompositeKey.class)
public class InventoryTrans { 
	
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
	
	@Column(name = "PAL_CODE") 
	private String palletCode;

	@Column(name = "CASE_CODE") 
	private String caseCode;
	
	@Id
	@Column(name = "PACK_BARCODE")
	private String packBarcodes;
	
	@Id
	@Column(name = "ITM_CODE") 
	private String itemCode;

	@Column(name = "VAR_ID") 
	private Long variantCode;

	@Column(name = "VAR_SUB_ID") 
	private String variantSubCode;

	@Column(name = "STR_NO") 
	private String batchSerialNumber;
	
	@Id
	@Column(name = "ST_BIN") 
	private String storageBin;
	
//	@Id
	@Column(name = "STCK_TYP_ID") 
	private Long stockTypeId;
	
	@Id
	@Column(name = "SP_ST_IND_ID") 
	private Long specialStockIndicatorId;
	
	@Column(name = "REF_ORD_NO") 
	private String referenceOrderNo;
	
	@Column(name = "STR_MTD") 
	private String storageMethod;
	
	@Column(name = "BIN_CL_ID") 
	private Long binClassId;
	
	@Column(name = "TEXT") 
	private String description;
	
	@Column(name = "INV_QTY") 
	private Double inventoryQuantity;
	
	@Column(name = "ALLOC_QTY") 
	private Double allocatedQuantity;
	
	@Column(name = "INV_UOM") 
	private String inventoryUom;
	
	@Column(name = "MFR_DATE") 
	private Date manufacturerDate;
	
	@Column(name = "EXP_DATE")
	private Date expiryDate;
	
	@Column(name = "IS_DELETED") 
	private Long deletionIndicator;
	
	@Column(name = "REF_FIELD_1") 
	private String referenceField1;
	
	@Column(name = "REF_FIELD_2") 
	private String referenceField2;
	
	@Column(name = "REF_FIELD_3")
	private String referenceField3;
	
	@Column(name = "REF_FIELD_4")
	private Double referenceField4;
	
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
	
	@Column(name = "RE_RUN")
	private Long reRun;
	
	@Column(name = "IU_CTD_BY") 
	private String createdBy;
	
	@Column(name = "IU_CTD_ON") 
	private Date createdOn;
	
	@Column(name = "UTD_BY") 
	private String updatedBy;
	
	@Column(name = "UTD_ON") 
	private Date updatedOn;
}
