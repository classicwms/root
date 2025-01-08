package com.tekclover.wms.api.transaction.model.cyclecount.periodic;

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
 * `C_ID`, `PLANT_ID`, `WH_ID`, `CC_NO`, `ST_BIN`, `ITM_CODE`, `PACK_BARCODE` 
 */
@Table(
		name = "tblperiodicline", 
		uniqueConstraints = { 
				@UniqueConstraint (
						name = "unique_key_periodicline", 
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "CC_NO", "ST_BIN", 
								"ITM_CODE", "PACK_BARCODE", "MFR_NAME", "SC_LINE_NO"})
				}
		)
@IdClass(PeriodicLineCompositeKey.class)
public class PeriodicLine {

	@Id
	@Column(name = "LANG_ID", columnDefinition = "nvarchar(25)")
	private String languageId;

	@Id
	@Column(name = "C_ID", columnDefinition = "nvarchar(25)")
	private String companyCode;

	@Id
	@Column(name = "PLANT_ID", columnDefinition = "nvarchar(25)")
	private String plantId;

	@Id
	@Column(name = "WH_ID", columnDefinition = "nvarchar(25)")
	private String warehouseId;

	@Id
	@Column(name = "CC_NO", columnDefinition = "nvarchar(50)")
	private String cycleCountNo;

	@Id
	@Column(name = "ST_BIN", columnDefinition = "nvarchar(50)")
	private String storageBin;

	@Id
	@Column(name = "ITM_CODE", columnDefinition = "nvarchar(100)")
	private String itemCode;

	@Id
	@Column(name = "PACK_BARCODE", columnDefinition = "nvarchar(25)")
	private String packBarcodes;

	@Id
	@Column(name = "MFR_NAME", columnDefinition = "nvarchar(25)")
	private String manufacturerName;

	@Id
	@Column(name = "SC_LINE_NO")
	private Long lineNo;
	
	@Column(name = "VAR_ID") 
	private Long variantCode;
	
	@Column(name = "VAR_SUB_ID") 
	private String variantSubCode;
	
	@Column(name = "STR_NO")
	private String batchSerialNumber;
	
	@Column(name = "STCK_TYP_ID")
	private Long stockTypeId;
	
	@Column(name = "SP_ST_IND_ID") 
	private String specialStockIndicator;
	
	@Column(name = "INV_QTY") 
	private Double inventoryQuantity;
	
	@Column(name = "INV_UOM") 
	private String inventoryUom;
	
	@Column(name = "CTD_QTY") 
	private Double countedQty;
	
	@Column(name = "VAR_QTY") 
	private Double varianceQty;
	
	@Column(name = "COUNTER_ID") 
	private String cycleCounterId;
	
	@Column(name = "COUNTER_NM")
	private String cycleCounterName;
	
	@Column(name = "STATUS_ID") 
	private Long statusId;
	
	@Column(name = "ACTION") 
	private String cycleCountAction;
	
	@Column(name = "REF_NO")
	private String referenceNo;
	
	@Column(name = "APP_PROCESS_ID") 
	private Long approvalProcessId;
	
	@Column(name = "APP_LVL") 
	private String approvalLevel;
	
	@Column(name = "APP_CODE") 
	private String approverCode;
	
	@Column(name = "APP_STATUS")
	private String approvalStatus;
	
	@Column(name = "REMARK")
	private String remarks;
	
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
	
	@Column(name = "CC_CTD_BY") 
	private String createdBy;
	
	@Column(name = "CC_CTD_ON")
	private Date createdOn = new Date();
	
	@Column(name = "CC_CNF_BY") 
	private String confirmedBy;
	
	@Column(name = "CC_CNF_ON") 
	private Date confirmedOn = new Date();
	
	@Column(name = "CC_CNT_BY")
	private String countedBy;
	
	@Column(name = "CC_CNT_ON") 
	private Date countedOn = new Date();

	/*----------------------Impex--------------------------------------------------*/

	@Column(name = "SIZE", columnDefinition = "nvarchar(50)")
	private String size;

	@Column(name = "BRAND", columnDefinition = "nvarchar(100)")
	private String brand;

	@Column(name = "ALT_UOM", columnDefinition = "nvarchar(50)")
	private String alternateUom;

	@Column(name = "NO_BAGS")
	private Double noBags;

	@Column(name = "BAG_SIZE")
	private Double bagSize;

	@Column(name = "MRP")
	private Double mrp;

	@Column(name = "ITM_TYP", columnDefinition = "nvarchar(100)")
	private String itemType;

	@Column(name = "ITM_GRP", columnDefinition = "nvarchar(100)")
	private String itemGroup;

}