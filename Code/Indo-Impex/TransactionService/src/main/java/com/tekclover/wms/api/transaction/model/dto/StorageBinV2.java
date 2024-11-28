package com.tekclover.wms.api.transaction.model.dto;

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
 * `LANG_ID`, `C_ID`, `PLANT_ID`, `WH_ID`, `ST_BIN`
 */
@Table(
		name = "tblstoragebin",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "unique_key_storagebin",
						columnNames = {"LANG_ID", "C_ID", "PLANT_ID", "WH_ID", "ST_BIN"})
		}
)
@IdClass(StorageBinCompositeKey.class)
public class StorageBinV2 {

	@Id
	@Column(name = "ST_BIN")
	private String storageBin;

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

	@Column(name = "FL_ID")
	private Long floorId;

	@Column(name = "ST_SEC_ID")
	private String storageSectionId;

	@Column(name = "ROW_ID")
	private String rowId;

	@Column(name = "AISLE_ID")
	private String aisleNumber;

	@Column(name = "SPAN_ID")
	private String spanId;

	@Column(name = "SHELF_ID")
	private String shelfId;

	@Column(name = "BIN_SECTION_ID")
	private Long binSectionId;

	@Column(name = "ST_TYP_ID")
	private Long storageTypeId;

	@Column(name = "BIN_CL_ID")
	private Long binClassId;

	@Column(name = "ST_BIN_TEXT")
	private String description;

	@Column(name = "BIN_BAR")
	private String binBarcode;

	@Column(name = "PUTAWAY_BLOCK")
	private Integer putawayBlock;

	@Column(name = "PICK_BLOCK")
	private Integer pickingBlock;

	@Column(name = "BLK_REASON")
	private String blockReason;

	@Column(name = "STATUS_ID")
	private Long statusId;

	@Column(name="OCC_VOL")
	private String occupiedVolume;

	@Column(name = "OCC_WT")
	private String occupiedWeight;

	@Column(name="OCC_QTY")
	private String occupiedQuantity;

	@Column(name = "REMAIN_VOL")
	private String remainingVolume;

	@Column(name = "REMAIN_WT")
	private String remainingWeight;

	@Column(name="REMAIN_QTY")
	private String remainingQuantity;

	@Column(name="TOT_VOL")
	private String totalVolume;

	@Column(name="TOT_QTY")
	private String totalQuantity;

	@Column(name="TOT_WT")
	private String totalWeight;

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

	@Column(name = "CTD_BY")
	private String createdBy;

	@Column(name = "CTD_ON")
	private Date createdOn;

	@Column(name = "UTD_BY")
	private String updatedBy;

	@Column(name = "UTD_ON")
	private Date updatedOn;

	@Column(name = "CAP_CHECK")
	private boolean capacityCheck;

	@Column(name="ALLOC_VOL")
	private Double allocatedVolume;

	@Column(name = "CAP_UNIT")
	private String capacityUnit;

	@Column(name = "LENGTH")
	private Double length;

	@Column(name = "WIDTH")
	private Double width;

	@Column(name = "HEIGHT")
	private Double height;

	@Column(name = "CAP_UOM")
	private String capacityUom;

	@Column(name = "QUANTITY")
	private String quantity;

	@Column(name = "WEIGHT")
	private Double weight;

	@Column(name = "C_TEXT", columnDefinition = "nvarchar(255)")
	private String companyDescription;

	@Column(name = "PLANT_TEXT", columnDefinition = "nvarchar(255)")
	private String plantDescription;

	@Column(name = "WH_TEXT", columnDefinition = "nvarchar(255)")
	private String warehouseDescription;
}