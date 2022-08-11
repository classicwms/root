package com.tekclover.wms.core.batch.scheduler.entity;

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
@Table(name = "tblinventory")
//@IdClass(InventoryCompositeKey.class)
public class Inventory { 

//	@Id
//	@Column(name = "LANG_ID") 
//	private String languageId;
//	
//	@Id
//	@Column(name = "C_ID") 
//	private String companyCodeId;
//	
//	@Id
//	@Column(name = "PLANT_ID")
//	private String plantId;
//	
//	@Id
	@Column(name = "wh_id") 
	private String warehouseId;
	
//	@Column(name = "PAL_CODE") 
//	private String palletCode;
//
//	@Column(name = "CASE_CODE") 
//	private String caseCode;
	
//	@Id
	@Column(name = "pack_barcode")
	private String packBarcodes;
	
	@Id
	@Column(name = "itm_code") 
	private String itemCode;
	
//	@Id
	@Column(name = "st_bin") 
	private String storageBin;
	
//	@Id
	@Column(name = "stck_typ_id") 
	private Long stockTypeId;
	
	@Column(name = "text") 
	private String description;
	
	@Column(name = "inv_qty") 
	private Double inventoryQuantity;
	
	@Column(name = "alloc_qty") 
	private Double allocatedQuantity;
	
	@Column(name = "inv_uom") 
	private String inventoryUom;

//	@Column(name = "VAR_ID") 
//	private Long variantCode;
//
//	@Column(name = "VAR_SUB_ID") 
//	private String variantSubCode;
//
//	@Column(name = "STR_NO") 
//	private String batchSerialNumber;
	
//	@Id
//	@Column(name = "SP_ST_IND_ID") 
//	private Long specialStockIndicatorId;
//	
//	@Column(name = "REF_ORD_NO") 
//	private String referenceOrderNo;
//	
//	@Column(name = "STR_MTD") 
//	private String storageMethod;
//	
//	@Column(name = "BIN_CL_ID") 
//	private Long binClassId;
	
	
	
//	@Column(name = "MFR_DATE") 
//	private Date manufacturerDate;
//	
//	@Column(name = "EXP_DATE")
//	private Date expiryDate;
//	
//	@Column(name = "IS_DELETED") 
//	private Long deletionIndicator;
//	
//	@Column(name = "REF_FIELD_1") 
//	private String referenceField1;
//	
//	@Column(name = "REF_FIELD_2") 
//	private String referenceField2;
//	
//	@Column(name = "REF_FIELD_3")
//	private String referenceField3;
//	
//	@Column(name = "REF_FIELD_4")
//	private String referenceField4;
//	
//	@Column(name = "REF_FIELD_5") 
//	private String referenceField5;
//	
//	@Column(name = "REF_FIELD_6")
//	private String referenceField6;
//	
//	@Column(name = "REF_FIELD_7") 
//	private String referenceField7;
//	
//	@Column(name = "REF_FIELD_8") 
//	private String referenceField8;
//	
//	@Column(name = "REF_FIELD_9") 
//	private String referenceField9;
//	
	@Column(name = "ref_field_10")
	private String referenceField10;
//	
//	@Column(name = "IU_CTD_BY") 
//	private String createdBy;
//	
//	@Column(name = "IU_CTD_ON") 
//	private Date createdOn = new Date();
}
